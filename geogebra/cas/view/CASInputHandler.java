package geogebra.cas.view;

import geogebra.cas.CASparser;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.kernel.arithmetic.Variable;

import java.awt.Color;
import java.awt.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class CASInputHandler {

	public static char ROW_REFERENCE_STATIC = '#';
	public static char ROW_REFERENCE_DYNAMIC = '$';
	private static int REPLACE_INSTEAD_OF_UPDATING = 0; // needed in updateCASrowreferences

	private CASView casView;
	private Kernel kernel;
	private CASTable consoleTable;
	private CASparser casParser;

	private ArrayList<String> changedVars;
	private boolean assignToFreeGeoOnly = false;

	public CASInputHandler(CASView view) {
		this.casView = view;
		kernel = view.getApp().getKernel();
		casParser = casView.getCAS().getCASparser();
		consoleTable = view.getConsoleTable();

		changedVars = new ArrayList<String>();
	}

	/** 
	 * Process input of current row.
	 * @param ggbcmd: command like "Factor" or "Integral"
	 * @param params: optional command parameters like "x"
	 */	
	public void processCurrentRow(String ggbcmd, String[] params){
		// get editor 
		CASTableCellEditor cellEditor = consoleTable.getEditor();
		
		if (ggbcmd.equalsIgnoreCase("Solve")){
			if (casView.getRowHeader().getSelectedIndices().length>1){
				processMultipleRows(ggbcmd, params);
				return;
			} else {
				String cellText=cellEditor.getInput();
				int depth=0;
				boolean mSolveNecessary=false;
				for (int j=0;j<cellText.length();j++){
					switch (cellText.charAt(j)){
					case '{':
						depth++;
						break;
					case '}':
						depth--;
						break;
					case ',':
						if (depth==1)
							mSolveNecessary=true;
						break;
					}
				}
				if (mSolveNecessary) {
					processMultipleRows(ggbcmd, params);
					return;
				}
			}
		}
		
		
		// get possibly selected text
		String selectedText = cellEditor == null ? null : cellEditor.getInputSelectedText();
		int selStart = cellEditor.getInputSelectionStart();
		int selEnd = cellEditor.getInputSelectionEnd();
		String selRowInput = cellEditor.getInput();
		if (selRowInput == null || selRowInput.length() == 0) {
			consoleTable.startEditingRow(consoleTable.getSelectedRow());
			return;
		}

		// save the edited value into the table model
		consoleTable.stopEditing();
		
		// get current row and input text		
		int selRow = consoleTable.getSelectedRow();	
		if (selRow < 0) selRow = consoleTable.getRowCount() - 1;
		CASTableCellValue cellValue = consoleTable.getCASTableCellValue(selRow);


		// DIRECT MathPiper use: line starts with "MathPiper:"
		if (selRowInput.startsWith("MathPiper:")) {
			String evalText = selRowInput.substring(10);
			// evaluate using MathPiper syntax
			String result = casView.getCAS().evaluateMathPiper(evalText);
			cellValue.setAllowLaTeX(false);
			setCellOutput(cellValue, "", result, "");
			return;
		}

		// STANDARD CASE: GeoGebraCAS input
		// break text into prefix, evalText, postfix
		String prefix, evalText, postfix;			
		boolean hasSelectedText = selectedText != null && selectedText.trim().length() > 0;
		if (hasSelectedText) {
			// selected text: break it up into prefix, evalText, and postfix
			prefix = selRowInput.substring(0, selStart).trim();
			if (selStart > 0 || selEnd < selRowInput.length()) {
				// part of input is selected
				evalText = "(" + selectedText + ")";
			} else {
				// full input is selected
				evalText = selectedText;
			}
			postfix = selRowInput.substring(selEnd).trim();
		}
		else {
			// no selected text: evaluate input using current cell
			prefix = "";
			evalText = selRowInput;
			postfix = "";	
		}

		// resolve static row references and change input field accordingly
		boolean staticReferenceFound = false;
		String newPrefix = resolveCASrowReferences(prefix, selRow, ROW_REFERENCE_STATIC);
		if (!newPrefix.equals(prefix)) {
			staticReferenceFound = true;
			prefix = newPrefix;
		}
		String newEvalText = resolveCASrowReferences(evalText, selRow, ROW_REFERENCE_STATIC);
		if (!newEvalText.equals(evalText)) {
			staticReferenceFound = true;
			evalText = newEvalText;
		}
		String newPostfix = resolveCASrowReferences(postfix, selRow, ROW_REFERENCE_STATIC);
		if (!newPostfix.equals(postfix)) {
			staticReferenceFound = true;
			postfix = newPostfix;
		}
		if (staticReferenceFound) {
			// change input if necessary
			cellValue.setInput(newPrefix + newEvalText + newPostfix);
		}

		// FIX common INPUT ERRORS in evalText
		if (!hasSelectedText && (ggbcmd.equals("Evaluate") || ggbcmd.equals("KeepInput"))) {
			String fixedInput = fixInputErrors(selRowInput);
			if (!fixedInput.equals(selRowInput)) {
				cellValue.setInput(fixedInput);
				evalText = fixedInput;
			}
		}

		// remember input selection information for future calls of processRow()
		// check if structure of selection is ok
		boolean structureOK = cellValue.isStructurallyEqualToLocalizedInput(prefix + evalText + postfix);
		if (!structureOK) {
			// show current selection again
			consoleTable.startEditingRow(selRow);
			cellEditor = consoleTable.getEditor();
			cellEditor.setInputSelectionStart(selStart);
			cellEditor.setInputSelectionEnd(selEnd);
			return;
		}

		boolean isAssignment = cellValue.getAssignmentVariable() != null;
		boolean isEvaluate = ggbcmd.equals("Evaluate");
		boolean isKeepInput = ggbcmd.equals("KeepInput");

		// assignments are processed immediately, the ggbcmd creates a new row below
		if (isAssignment) {
			// tell row that KeepInput was used
			if (isKeepInput)
				cellValue.setEvalCommand("KeepInput");

			// evaluate assignment row
			boolean needInsertRow = !isEvaluate && !isKeepInput;
			boolean success = processRowsBelowThenEdit(selRow, !needInsertRow);

			// insert a new row below with the assignment label and process it using the current command
			if (success && needInsertRow) {
				String assignmentLabel = cellValue.getEvalVE().getLabelForAssignment();
				CASTableCellValue newRowValue = new CASTableCellValue(casView);
				newRowValue.setInput(assignmentLabel);
				consoleTable.insertRow(newRowValue, true);
				processCurrentRow(ggbcmd, params);
			}

			return;
		}

		// Substitute dialog
		if (ggbcmd.equals("Substitute")) {
			// show substitute dialog
			casView.showSubstituteDialog(prefix, evalText, postfix, selRow);
			return;
		}

		// standard case: build eval command
		String paramString = null;
		if (!isEvaluate) {
			// prepare evalText as ggbcmd[ evalText, parameters ... ]
			StringBuilder sb = new StringBuilder();
			sb.append(ggbcmd);
			sb.append("[");
			sb.append(evalText);
			if (params != null) {
				StringBuilder paramSB = new StringBuilder();
				for (int i=0; i < params.length; i++) {
					paramSB.append(", ");
					paramSB.append(resolveButtonParameter(params[i], cellValue));
				}
				paramString = paramSB.substring(2);
				sb.append(paramSB);
			}
			sb.append("]");
			evalText = sb.toString();	
		}

		// remember evalText and selection for future calls of processRow()
		cellValue.setProcessingInformation(prefix, evalText, postfix);
		cellValue.setEvalComment(paramString);

		// process given row and below, then start editing
		processRowsBelowThenEdit(selRow, true);
	}
	/**
	 * Determines the selected rows and tries to solve (solve is
	 * the only implemented function up to now) the equations
	 * or lists of equations found in the selected rows.
	 * The result is written into the active cell.
	 * 
	 * @param ggbcmd is the given command (just Solve is supported)
	 * @param params the list of parameters
	 */
	private void processMultipleRows(String ggbcmd, String[] params){

		// get current row and input text
		int selRow = consoleTable.getSelectedRow();	
		if (selRow < 0) selRow = consoleTable.getRowCount() - 1;
		boolean oneRowOnly=false;
		int currentRow=selRow;

		int [] selectedIndices = casView.getRowHeader().getSelectedIndices();
		int nrEquations;
		int lastRowSelected;
		if (selectedIndices.length<=1){
			selectedIndices=new int[1];
			selectedIndices[0]=selRow;
			oneRowOnly=true;
			nrEquations=1;
			lastRowSelected=selectedIndices[nrEquations-1];
		} else {
			nrEquations=selectedIndices.length;
			currentRow=1+(lastRowSelected=selectedIndices[nrEquations-1]);;
		}

		CASTableCellValue cellValue = consoleTable.getCASTableCellValue(currentRow);
		if (cellValue!=null){
			if (!cellValue.isEmpty() && !oneRowOnly){
				cellValue= new CASTableCellValue(casView);
				consoleTable.insertRowAfter(lastRowSelected, cellValue, true);
			} 
		} else {
			cellValue= new CASTableCellValue(casView);
			consoleTable.insertRowAfter(lastRowSelected, cellValue, true);
		}

		//gets the number of equations
		for (int i=0;i<selectedIndices.length;i++){
			String cellText;
			CASTableCellValue selCellValue=consoleTable.getCASTableCellValue(selectedIndices[i]);
			if (selectedIndices[i]==selRow){
				cellText=consoleTable.getEditor().getInput();
			} else {
				cellText=selCellValue.getInputVE().toString();
			}
			cellText=resolveCASrowReferences(cellText, selectedIndices[i], ROW_REFERENCE_STATIC);
			int depth=0;
			for (int j=0;j<cellText.length();j++){
				switch (cellText.charAt(j)){
				case '{':
					depth++;
					break;
				case '}':
					depth--;
					break;
				case ',':
					if (depth==1)
						nrEquations++;
					break;
				}
			}
		}

		//generates an array of references (e.g. $1,a,...) and
		//an array of equations
		int counter=0;
		String[] references=new String[nrEquations];
		String[] equations=new String[nrEquations];
		for (int i=0;i<selectedIndices.length;i++){
			CASTableCellValue selCellValue=consoleTable.getCASTableCellValue(selectedIndices[i]);
			String cellText;
			String assignedVariable=selCellValue.getAssignmentVariable();
			boolean inTheSelectedRow= currentRow==selectedIndices[i];
			if (assignedVariable!=null){
				references[counter]=assignedVariable;
				equations[counter++]=resolveCASrowReferences(selCellValue.getInputVE().toString(), selectedIndices[i], ROW_REFERENCE_STATIC);
			}
			else{
				if (selectedIndices[i]==selRow){
					cellText=consoleTable.getEditor().getInput();
				} else {
					cellText=selCellValue.getInputVE().toString();
				}
				cellText=resolveCASrowReferences(cellText, selectedIndices[i], ROW_REFERENCE_STATIC);
				if (!inTheSelectedRow) references[counter]="$"+(selectedIndices[i]+1);
				if (!cellText.startsWith("{")){
					if (inTheSelectedRow) references[counter]=cellText;
					equations[counter++]=cellText;
				}
				else {
					int depth=0;
					int leftIndex=1;
					for (int j=0;j<cellText.length();j++){
						switch (cellText.charAt(j)){
						case '{':
							depth++;
							break;
						case '}':
							depth--;
							if (depth==0){
								if (inTheSelectedRow) references[counter]=cellText.substring(leftIndex, j).replaceAll(" ", "");
								equations[counter++]=cellText.substring(leftIndex, j).replaceAll(" ", "");
							}
							break;
						case ',':
							if (depth==1){
								if (inTheSelectedRow) references[counter]=cellText.substring(leftIndex, j).replaceAll(" ", "");
								equations[counter++]=cellText.substring(leftIndex, j).replaceAll(" ", "");
								leftIndex=j+1;
							}
							break;
						}
					}
				}
			}
		}

		//The equations have to be dereferenced further and a CASTableCellValue 
		//is generated to obtain the parameters (the variables) for the solve function.
		StringBuilder equationsVariablesResolved=new StringBuilder("{");
		for (int i=0; i<equations.length; i++){
			equations[i]=resolveCASrowReferences(equations[i], currentRow, ROW_REFERENCE_DYNAMIC);
			equations[i]=resolveCASrowReferences(equations[i], currentRow, ROW_REFERENCE_STATIC);
			CASTableCellValue v=new CASTableCellValue(casView);
			if (equations[i].startsWith("(")){
				equations[i]=equations[i].substring(1,equations[i].lastIndexOf(")"));
			}
			v.setInput(equations[i]);
			if (v.getAssignmentVariable()!=null){
				references[i]=v.getAssignmentVariable();
			}
			equations[i]=v.getInputVE().toString();
			Boolean isVariable=kernel.isCASVariableBound(equations[i]);
			if (isVariable) {
				Variable var=new Variable(kernel, equations[i]);
				equationsVariablesResolved.append(", "+var.resolveAsExpressionValue().toValueString());
			} else {
				equationsVariablesResolved.append(", "+equations[i]);
			}
		}

		equationsVariablesResolved.append("}");
		CASTableCellValue cellToObtainParameters=new CASTableCellValue(casView);

		String prefix, evalText, postfix;			

		prefix = "";
		postfix = "";

		cellToObtainParameters.setInput(evalText=equationsVariablesResolved.toString().replaceFirst(", ", ""));

		StringBuilder cellText=new StringBuilder("{");
		for (int i=0;i<nrEquations;i++){
			cellText.append(", ");
			cellText.append(references[i]);
		}
		cellText.append("}");
		String cellTextS=cellText.toString();
		cellTextS=cellTextS.replaceFirst(", ", "");		

		if (params.length==1){
			if (params[0].indexOf("%0")!=-1){
				String[] b=new String[nrEquations];
				for (int i=0;i<nrEquations;i++){
					b[i]=("%"+i);
				}
				params= b;
			}
		}

		// save the edited value into the table model
		consoleTable.stopEditing();

		// FIX common INPUT ERRORS in evalText
		if ((ggbcmd.equals("Evaluate") || ggbcmd.equals("KeepInput"))) {
			String fixedInput = fixInputErrors(cellTextS);
			if (!fixedInput.equals(cellTextS)) {
				evalText = fixedInput;
			}
		}

		// standard case: build eval command
		String paramString = null;
		//CASTableCellValue cellValueTmp=cellValue;
		cellValue.setInput(cellTextS);

		// prepare evalText as ggbcmd[ evalText, parameters ... ]
		StringBuilder sb = new StringBuilder();
		sb.append(ggbcmd);
		sb.append("[");
		sb.append(cellTextS);
		sb.append(", {");
		if (params != null) {
			StringBuilder paramSB = new StringBuilder();
			for (int i=0; i < params.length; i++) {
				paramSB.append(", ");
				paramSB.append(resolveButtonParameter(params[i], cellToObtainParameters));
			}
			paramString = paramSB.substring(2);
			sb.append(paramSB.substring(2));
			sb.append("}");
		}
		sb.append("]");
		evalText = sb.toString();	

		// remember evalText and selection for future calls of processRow()
		cellValue.setProcessingInformation(prefix, evalText, postfix);
		cellValue.setEvalComment(paramString);

		// process given row and below, then start editing
		processRowsBelowThenEdit(currentRow, true);
	}

	/**
	 * Replaces %0, %1, %2 etc. by input variables of cellValue. Note
	 * that x, y, z are used if possible.
	 * @param param
	 * @param cellValue
	 * @return
	 */
	private String resolveButtonParameter(String param, CASTableCellValue cellValue) {
		if (param.charAt(0) == '%') {
			int n = Integer.parseInt(param.substring(1));
			
			//to make sure that for an input like x+y+z the
			//parameters are not resolved to %0=x %1=x %2=x
			
			// try x, y, z first
			String[] vars = {"x", "y", "z"};
			for (int i=0; i < vars.length; i++) {
				if (cellValue.isFunctionVariable(vars[i]) || cellValue.isInputVariable(vars[i])) {
					if (0==n)
						return vars[i];
					else
						n--;
				}
			}

			// try function variable like m in f(m) := 2m + b
			String resolvedParam = cellValue.getFunctionVariable();
			if (resolvedParam != null)
				return resolvedParam;

			// try input variables like a in c := a + b
			resolvedParam = cellValue.getInVar(n);
			if (resolvedParam != null)
				return resolvedParam;
			else
				return "x";
		}

		// standard case
		return param;
	}

	private boolean processRowsBelowThenEdit(int selRow, boolean startEditing) {
		boolean success = processRow(selRow);

		// process dependent rows below
		if (success) {
			CASTableCellValue cellValue = consoleTable.getCASTableCellValue(selRow);
			// check if the processed row is an assignment, e.g. b := 25
			String var = cellValue.getAssignmentVariable();
			// process all dependent rows below
			success = processDependentRows(var, selRow+1);
		}

		if (startEditing || !success) {
			// start editing row below successful evaluation
			boolean isLastRow = consoleTable.getRowCount() == selRow+1;
			boolean goDown = success && 
			// we are in last row or next row is empty
			(isLastRow || consoleTable.isRowEmpty(selRow+1));
			consoleTable.startEditingRow(goDown ? selRow+1 : selRow);
		}

		return success;
	}

	/**
	 * Processes all dependent rows starting with the given row that depend on var
	 * or have dynamic cell references.
	 * @param var: changed variable
	 * @return whether processing was successful
	 */
	public boolean processDependentRows(String var, int startRow) {
		boolean success = true;

		// PROCESS DEPENDENT ROWS BELOW
		// we need to collect all assignment variables that are changed
		// due to dependencies on var, e.g.
		// row1   b := 5
		// row2   c := b + 8
		// row3   c + 7
		// when row1 is change to b:=6, we need to process row2 because it
		// depends on b. This also changes c, so we need to add c to our
		// list of changed variables. This will ensure that also row3 is
		// updated.
		changedVars.clear();
		if (var != null) changedVars.add(var); // assignment variable like "b" in "b := 5"

		// only allow assignments to free geos when they already exist
		// in order to avoid redefinitions
		assignToFreeGeoOnly = true;

		// process all rows below that have one of vars as inputVariable
		for (int i = startRow; i < consoleTable.getRowCount(); i++) {
			CASTableCellValue cellValue = consoleTable.getCASTableCellValue(i);

			// check for row references like $2
			boolean needsProcessing = cellValue.includesRowReferences();

			if (!needsProcessing) {
				// check if row i depends on at least one of the changedVars
				for (int k=0; k < changedVars.size(); k++) {
					if (cellValue.isInputVariable(changedVars.get(k))) {
						//System.out.println("row " + i + " depends on " + changedVars.get(k));
						needsProcessing = true;
						break;
					}
				}
			}

			if (needsProcessing) {
				// process row i
				success = processRow(i) && success;

				// add assignment variable of row i to changedVars
				var = cellValue.getAssignmentVariable();
				if (var != null) changedVars.add(var);
			}
		}

		// revert back to allow redefinitions
		assignToFreeGeoOnly = false;

		// successfully processed all rows
		return success;
	}

	/**
	 * Returns whether it's allowed to change var in GeoGebra at the moment.
	 * This is important to avoid unnecessary redefinitions.
	 * @param var
	 * @return
	 */
	private boolean isGeoGebraAssignmentAllowed(String var) {		
		// don't allow assignment to dependent geo
		if (assignToFreeGeoOnly) {
			// check for dependent geo with label var
			GeoElement geo = kernel.lookupLabel(var);
			if (geo != null && !geo.isIndependent()) {
				// TODO: remove
				System.out.println("NO assignment to dependent: " + var);
				return false;
			}
		}

		return true;
	}

	/**
	 * Processes the input of the given row and updates the row's output.
	 * @param row number
	 * @return whether processing was successful
	 */
	final public boolean processRow(int row) {
		CASTableCellValue cellValue = consoleTable.getCASTableCellValue(row);

		// resolve dynamic references for prefix and postfix
		// dynamic references for evalText is handled in evaluateGeoGebraCAS()
		String prefix = resolveCASrowReferences(cellValue.getPrefix(), row, ROW_REFERENCE_DYNAMIC);
		String postfix = resolveCASrowReferences(cellValue.getPostfix(), row, ROW_REFERENCE_DYNAMIC);

		// process evalText
		String result = null;
		try {
			// evaluate using GeoGebraCAS syntax
			//currentUpdateVar = cellValue.getAssignmentVariable();
			result = evaluateGeoGebraCAS(cellValue.getEvalVE(), row);
		} catch (Throwable th) {
			//th.printStackTrace();
			System.err.println("GeoGebraCAS.processRow " + row + ": " + th.getMessage());
		}

		// update cell
		setCellOutput(cellValue, prefix, result, postfix);

		// update table
		consoleTable.updateRow(row);

		return result != null;
	} //apply(String,String[]

	/**
	 * Sets output of cellValue according to the given strings.
	 * @param cellValue
	 * @param result if null, an error is set as output
	 * @param prefix
	 * @param postfix
	 */
	private void setCellOutput(CASTableCellValue cellValue, String prefix, String result, String postfix) {
		// Set the value into the table		
		if (result != null && prefix != null && postfix != null)	{
			if (prefix.length() == 0 && postfix.length() == 0) {
				// no prefix, no postfix: just evaluation
				cellValue.setOutput(result);
			} else {
				// make sure that evaluation is put into parentheses
				cellValue.setOutput(prefix + " (" + result + ") " + postfix);
			}
		} else {
			cellValue.setError("CAS.GeneralErrorMessage");
			System.err.println("GeoGebraCAS.evaluateRow: " + casView.getCAS().getGeoGebraCASError());
		}
	}

	/**
	 * Evaluates eval as GeoGebraCAS input. Dynamic references are
	 * resolved according to the given row number.
	 */
	private String evaluateGeoGebraCAS(ValidExpression evalVE, int row) throws Throwable {
		boolean oldValue = kernel.isTranslateCommandName();
		kernel.setTranslateCommandName(false);

		try {
			// resolve dynamic row references
			String eval = evalVE.toAssignmentString();
			String rowRefEval = resolveCASrowReferences(eval, row, ROW_REFERENCE_DYNAMIC);
			if (!rowRefEval.equals(eval)) {
				eval = rowRefEval;
				evalVE = casParser.parseGeoGebraCASInput(rowRefEval);
			}

			// process this input
			return processCASviewInput(evalVE, rowRefEval);
		}
		finally {
			kernel.setTranslateCommandName(oldValue); 
		}
	}

	/**
	 * Replaces references to other rows (e.g. #3, %3) in input string by
	 * the values from those rows.
	 */
	public String resolveCASrowReferences(String inputExp, int selectedRow, char delimiter) {
		if (inputExp == null || inputExp.length() == 0) {
			return inputExp;
		}

		StringBuilder sb = new StringBuilder();
		int startPos = 0;
		int[] delimInfo = getNextDelimiter(inputExp, selectedRow, startPos, delimiter);
		while (delimInfo != null)
		{
			int rowRef = delimInfo[1];
			char endCharacter = (char)delimInfo[2];

			sb.append(inputExp.substring(startPos, delimInfo[0]-1));
			if (rowRef == selectedRow) {
				// reference to selected row: insert blank
				sb.append(" ");
			}
			else if (rowRef >= 0 && rowRef < casView.getRowCount()) {
				// #number or #number# 
				// insert referenced row
				String rowStr = endCharacter == delimiter ?
						casView.getRowInputValue(rowRef) :
							casView.getRowOutputValue(rowRef);
						if (isNumberOrVariable(rowStr))
							sb.append(rowStr);
						else {
							sb.append('(');
							sb.append(rowStr);
							sb.append(')');
						}
			}
			int len = (int) Math.ceil(Math.log10(delimInfo[1] + 1.1)); // in the String, row-refs aren't 0-based!

			// keep end character after reference
			if (!Character.isDigit(endCharacter) && endCharacter != delimiter)
			{
				sb.append(endCharacter);
				++len;
			}
			startPos = delimInfo[0] + len;
			delimInfo = getNextDelimiter(inputExp, selectedRow, startPos, delimiter);
		}
		if (startPos < inputExp.length())
			sb.append(inputExp.substring(startPos));
		String result = sb.toString();

		if (result.indexOf(delimiter) < 0) {
			return result;
		} else {
			// resolve references that are still here
			return resolveCASrowReferences(result, selectedRow, delimiter);
		}
	}

	/**
	 * Goes through the input of a given row and updates all the Row-References.
	 * @param changedRow the row that was changed. For Insertions, that is the row after 
	 *                   which a new row was inserted, for deletions it's the deleted row
	 * @param currentRow the row whose input should be updated
	 * @param delimiter  the delimiter-char of the reference (i.e. $ or #)
	 * @param isInsertion true for insertions, false for deletions
	 */
	public void updateReferencesAfterRowInsertOrDelete(int changedRow, int currentRow, char delimiter, boolean isInsertion) {	

		CASTableCellValue v = (CASTableCellValue) consoleTable.getValueAt(currentRow, CASTable.COL_CAS_CELLS);
		String inputExp = v.getInternalInput();
		String evalText = v.getEvalText();
		String evalComm = v.getEvalComment();
		String [] toUpdate={inputExp,evalText,evalComm};
		
		for (int i=0;i<toUpdate.length;i++){
		if (toUpdate[i] == null || toUpdate[i].length() == 0) {
			continue;
		}

		StringBuilder sb = new StringBuilder();
		int startPos = 0;
		int[] delimInfo = getNextDelimiter(toUpdate[i], currentRow, startPos, delimiter);
		while (delimInfo != null)
		{
			int rowRef = delimInfo[1];
			char endCharacter = (char)delimInfo[2];
			sb.append(toUpdate[i].substring(startPos, delimInfo[0]-1));

			// note: the references in input strings start at 1, but rowRef is 0-based!
			sb.append(delimiter);
			if (isInsertion && rowRef > changedRow)
				sb.append(rowRef + 2);
			else if (!isInsertion && rowRef == changedRow) // references deleted row
				sb.append('?');
			else if (!isInsertion && rowRef > changedRow)
				sb.append(rowRef);
			else
				sb.append(rowRef+1);

			int len = (int) Math.ceil(Math.log10(delimInfo[1] + 1.1));

			// keep end character after reference
			if (!Character.isDigit(endCharacter))
			{
				sb.append(endCharacter);
				++len;
			}
			startPos = delimInfo[0] + len;
			delimInfo = getNextDelimiter(toUpdate[i], currentRow, startPos, delimiter);
		}
		if (startPos < toUpdate[i].length())
			sb.append(toUpdate[i].substring(startPos));
		String result = sb.toString();
		switch  (i){
		case 0:		v.setInput(result);
		break;
		case 1: v.setProcessingInformation("", result, "");
		break;
		case 2: v.setEvalComment(result);
		}
		}
	}


	/**
	 * Returns whether str is a number or variable
	 */
	private static boolean isNumberOrVariable(String str) {
		for (int i=0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (!Character.isLetterOrDigit(ch) && ch != '.')
				return false;
		}
		return true;
	}



	/**
	 * Fixes common input errors and returns the corrected input String.
	 * @param input
	 * @return
	 */
	private String fixInputErrors(String input) {
		input = input.trim();

		// replace a :=  with  Delete[a]
		if (input.endsWith(":=")) {
			input = casView.getApp().getCommand("Delete") + "[" + input.substring(0, input.length()-2).trim() + "];";
		}

		// remove trailing = 
		else if (input.endsWith("=")) {
			input = input.substring(0, input.length()-1);
		}

		return input;
	}

	/**
	 * Processes the CASview input and returns an evaluation result. Note that this method
	 * can have side-effects on the GeoGebra kernel by creating new objects or deleting an existing object.
	 * 
	 * @return result as String in GeoGebra syntax
	 */
	private synchronized String processCASviewInput(ValidExpression evalVE, String eval) throws Throwable {		 	
		// check for assignment
		String assignmentVar = evalVE.getLabel();
		boolean assignment = assignmentVar != null;

		// EVALUATE input expression with current CAS
		String CASResult = null;
		Throwable throwable = null;
		try {
			if (assignment || evalVE.isTopLevelCommand()) {
				// evaluate inVE in CAS and convert result back to GeoGebra expression
				CASResult = casView.getCAS().evaluateGeoGebraCAS(evalVE);
			} 
			else {
				// build Simplify[inVE]
				Command simplifyCommand = new Command(kernel, "Simplify", false);
				ExpressionNode inEN = evalVE.isExpressionNode() ? (ExpressionNode) evalVE :
					new ExpressionNode(kernel, evalVE);
				simplifyCommand.addArgument(inEN);
				simplifyCommand.setLabel(evalVE.getLabel());
				// evaluate Simplify[inVE] in CAS and convert result back to GeoGebra expression
				CASResult = casView.getCAS().getCurrentCAS().evaluateGeoGebraCAS(simplifyCommand);
			}
		} catch (Throwable th1) {
			throwable = th1;
			System.err.println("CAS evaluation failed: " + eval + "\n error: " + th1.toString());
		}
		boolean CASSuccessful = CASResult != null;

		// GeoGebra Evaluation needed?
		boolean evalInGeoGebra = false;
		boolean isDeleteCommand = false;
		
		// check if assignment is allowed
		if (assignment) {
			// assignment (e.g. a := 5, f(x) := x^2)
			evalInGeoGebra = isGeoGebraAssignmentAllowed(assignmentVar);
		}		
		else {
			// evaluate input expression in GeoGebra if we have
			// - or Delete, e.g. Delete[a]
			// - or CAS was not successful
			// - or CAS result contains commands
			isDeleteCommand = isDeleteCommand(eval);
			evalInGeoGebra = !CASSuccessful || isDeleteCommand || containsCommand(CASResult);
		}

		String ggbResult = null;
		String assignmentResult = null;
		if (evalInGeoGebra) {
			// we have just set this variable in the CAS, so ignore the update fired back by the
			// GeoGebra kernel when we call evalInGeoGebra
			casView.addToIgnoreUpdates(assignmentVar);

			// EVALUATE inputExp in GeoGebra
			try {
				// process inputExp in GeoGebra
				if (!assignToFreeGeoOnly)
					ggbResult = evalInGeoGebra(eval);
			} catch (Throwable th2) {
				if (throwable == null) throwable = th2;
				System.err.println("GeoGebra evaluation failed: " + eval + "\n error: " + th2.toString());
			}

			// inputExp failed with GeoGebra
			// try to evaluate result of MathPiper
			if (ggbResult == null && !isDeleteCommand && CASSuccessful) {	
				// EVALUATE result of MathPiper
				String ggbEval = CASResult;
				if (assignment) {
					assignmentResult = getAssignmentResult(evalVE);
					ggbEval = assignmentResult;
				} 

				try {	
					// process mathPiperResult in GeoGebra
					ggbResult = evalInGeoGebra(ggbEval);
				} catch (Throwable th2) {
					if (throwable == null) throwable = th2;
					System.err.println("GeoGebra evaluation failed: " + ggbEval + "\n error: " + th2.toString());
				}
			}

			// handle future updates of assignmentVar again
			casView.removeFromIgnoreUpdates(assignmentVar);
		}

		// return result string:
		// use MathPiper if that worked, otherwise GeoGebra
		if (CASSuccessful) {
			// assignment: return value of assigned variable, e.g. f(x) := 2 a x
			if (assignment) {
				if (assignmentResult == null)
					assignmentResult = getAssignmentResult(evalVE);
				return assignmentResult;
			} 

			// no assignment: return CAS result
			else {
				return CASResult;
			}	
		} 

		else if (ggbResult != null) {
			// GeoGebra evaluation worked
			return ggbResult;
		}

		else {
			// nothing worked
			throw throwable;
		}
	}

	/**
	 * Returns evalVE when isKeepInputUsed() is set and otherwise the value of evalVE.getLabel() in the underlying CAS.
	 * @param evalVE
	 * @return
	 */
	private String getAssignmentResult(ValidExpression evalVE) {
		StringBuilder assignmentResult = new StringBuilder();
		assignmentResult.append(evalVE.getLabelForAssignment());
		assignmentResult.append(evalVE.getAssignmentOperator());

		if (evalVE.isKeepInputUsed()) {
			// keep input
			assignmentResult.append(evalVE.toString());
		} else {
			// return value of assigned variable
			try {
				// evaluate assignment variable like a or f(x)
				assignmentResult.append(casView.getCAS().getCurrentCAS().evaluateGeoGebraCAS(evalVE.getLabelForAssignment()));
			} catch (Throwable th1) {
				return evalVE.getLabelForAssignment();
			}
		}

		return assignmentResult.toString();
	}

	private boolean isDeleteCommand(String inputExp) {
		return inputExp.startsWith("Delete");	
	}

	private boolean containsCommand(String CASResult) {
		return  CASResult != null && CASResult.indexOf('[') > -1;
	}

	/**
	 * Evaluates expression with GeoGebra and returns the resulting string.
	 */
	private synchronized String evalInGeoGebra(String casInput) throws Throwable {
		GeoElement [] ggbEval = kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(casInput, false, false, true);

		if (ggbEval.length == 1) {
			return ggbEval[0].toValueString();
		} else {
			StringBuilder sb = new StringBuilder('{');
			for (int i=0; i<ggbEval.length; i++) {
				sb.append(ggbEval[i].toValueString());
				if (i < ggbEval.length - 1)
					sb.append(", ");
			}
			sb.append('}');
			return sb.toString();
		}
	}

	/**
	 * Looks at the Input Expression of a given row, and looks for the 
	 * next delimiter ($ or #) in it.
	 * Returns the position of the next delimiter and the rownumber it 
	 * references (if any, otherwise 0), and the character at the end of the reference.
	 * Returns null if no delimiter could be found.
	 */
	int[] getNextDelimiter(String inputExpression, int row, int startPos, char delimiter)
	{
		int length = inputExpression.length();
		if (startPos >= length)
			return null;

		int[] retval = new int[3];
		for (int i = startPos; i < length; i++) {
			char ch = inputExpression.charAt(i);
			if (ch == delimiter) {
				int start = i+1;
				int end = start;
				char endCharacter = end < length ? inputExpression.charAt(end) : ' ';

				// get digits after #
				while (end < length && Character.isDigit(endCharacter = inputExpression.charAt(end))) {
					end++;
				}
				i = end;

				int rowRef;
				if (start == end || end == ' ') {
					// # references previous row
					rowRef = row - 1;
				}
				else {
					// #n references n-th row
					rowRef = Integer.parseInt(inputExpression.substring(start, end)) - 1;
				}

				retval[0] = start;
				retval[1] = rowRef;
				retval[2] = (int)endCharacter;
				return retval;
			}
		}
		return null;
	}
}
