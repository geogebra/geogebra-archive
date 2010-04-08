package geogebra.cas.view;

import geogebra.cas.CASparser;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.ValidExpression;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class CASInputHandler {
	
	public static char ROW_REFERENCE_STATIC = '#';
	public static char ROW_REFERENCE_DYNAMIC = '$';
	
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

		// get possibly selected text
		String selectedText = cellEditor == null ? null : cellEditor.getInputSelectedText();
		int selStart = cellEditor.getInputSelectionStart();
		int selEnd = cellEditor.getInputSelectionEnd();
		String selRowInput = cellEditor.getInput();
		if (selRowInput == null || selRowInput.length() == 0) {
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
		if (!hasSelectedText && (ggbcmd.equals("Evaluate") || ggbcmd.equals("CheckInput"))) {
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
		
		// Substitute dialog
		if (ggbcmd.equals("SubstituteDialog")) {
			// show substitute dialog
			casView.showSubstituteDialog(prefix, evalText, postfix, selRow);
			return;
		}
		
		// standard case: evaluate and update row
		if (!ggbcmd.equals("Evaluate")) {
			// prepare evalText as ggbcmd[ evalText, parameters ... ]
			StringBuilder sb = new StringBuilder();
			sb.append(ggbcmd);
			sb.append("[");
			sb.append(evalText);
			if (params != null) {
				for (int i=0; i < params.length; i++) {
					sb.append(',');
					sb.append(params[i]);
				}
			}
			sb.append("]");
			evalText = sb.toString();	
		}
		
		// remember evalText and selection for future calls of processRow()
		cellValue.setProcessingInformation(prefix, evalText, postfix);
		
		// process given row
		boolean success = processRow(selRow);
		
		// process dependent rows below
		if (success) {
			// check if the processed row is an assignment, e.g. b := 25
			String var = cellValue.getAssignmentVariable();
			// process all dependent rows below
			success = processDependentRows(var, selRow);
		}
		
		// start editing row below successful evaluation
		boolean isLastRow = consoleTable.getRowCount() == selRow+1;
		boolean goDown = success && 
			// we are in last row or next row is empty
			(isLastRow || consoleTable.isRowEmpty(selRow+1));
		consoleTable.startEditingRow(goDown ? selRow+1 : selRow);
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
		if (result != null)	{
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
			if (rowRefEval != eval) {
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
		// check for delimiter first
		if (inputExp.length() == 0 || inputExp.indexOf(delimiter) < 0) {
			return inputExp;
		}
		
		StringBuilder sbCASreferences = new StringBuilder();
		
		int length = inputExp.length();
		for (int i = 0; i < length; i++) {
			char ch = inputExp.charAt(i);
			if (ch == delimiter) {
				int start = i+1;
				int end = start;
				char endCharacter = end < length ? inputExp.charAt(end) : ' ';
				
				// get digits after #
				while (end < length && Character.isDigit(endCharacter = inputExp.charAt(end))) {
					end++;
				}
				i = end;
				
				int rowRef;
				if (start == end || end == ' ') {
					// # references previous row
					rowRef = selectedRow - 1;
				}
				else {
					// #n references n-th row
					rowRef = Integer.parseInt(inputExp.substring(start, end)) - 1;
				}
				
				if (rowRef == selectedRow) {
					// reference to selected row: insert blank
					sbCASreferences.append(" ");
				}
				else if (rowRef >= 0 && rowRef < casView.getRowCount()) {
					// #number or #number# 
					// insert referenced row
					String rowStr = (endCharacter == delimiter) ?
							casView.getRowInputValue(rowRef) :
							casView.getRowOutputValue(rowRef);
					if (isNumberOrVariable(rowStr))
						sbCASreferences.append(rowStr);
					else {
						sbCASreferences.append('(');
						sbCASreferences.append(rowStr);
						sbCASreferences.append(')');
					}
				}
				
				// keep end character after reference
				if (!Character.isDigit(endCharacter) && endCharacter != delimiter)
					sbCASreferences.append(endCharacter);
				
			} else {
				sbCASreferences.append(ch);
			}
		}
		
		// resulting string
		String result = sbCASreferences.toString();
		if (result.indexOf(delimiter) < 0) {
			return result;
		} else {
			// resolve references that are still here
			return resolveCASrowReferences(result, selectedRow, delimiter);
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
		
		// replace f(x) = x^2 by f(x) := x^2
		else if (functionDefinition.matcher(input).matches()) 
		{
			input = input.replaceFirst("=", ":=");
		}
		
		return input;
	}
	
	// f(x) = x^2
	// should be changed to f(x) := x^2
	private static Pattern functionDefinition = Pattern.compile("(\\p{L})*\\([\\p{L}&&[^\\)]]*\\)(\\s)*[=].*");
	
	
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
				CASResult = casView.getCAS().evaluateGeoGebraCAS(evalVE, casView.getUseGeoGebraVariableValues());
			} 
			else {
				// build Simplify[inVE]
				Command simplifyCommand = new Command(kernel, "Simplify", false);
				ExpressionNode inEN = evalVE.isExpressionNode() ? (ExpressionNode) evalVE :
										new ExpressionNode(kernel, evalVE);
				simplifyCommand.addArgument(inEN);
				simplifyCommand.setLabel(evalVE.getLabel());
				// evaluate Simplify[inVE] in CAS and convert result back to GeoGebra expression
				CASResult = casView.getCAS().getCurrentCAS().evaluateGeoGebraCAS(simplifyCommand, casView.getUseGeoGebraVariableValues());
			}
		} catch (Throwable th1) {
			throwable = th1;
			System.err.println("CAS evaluation failed: " + eval + "\n error: " + th1.toString());
		}
		boolean CASSuccessful = CASResult != null;
		
		// GeoGebra Evaluation needed?
		boolean evalInGeoGebra = false;
		boolean isDeleteCommand = false;
		if (casView.getUseGeoGebraVariableValues()) {
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
		}

		String ggbResult = null;
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
			if (ggbResult == null && !isDeleteCommand && CASSuccessful && !"true".equals(CASResult)) {
				String ggbEval = CASResult;
				if (assignment) {
					StringBuilder sb = new StringBuilder();
					sb.append(evalVE.getLabelForAssignment());
					sb.append(":=");
					sb.append(CASResult);
					ggbEval = sb.toString();
				}
				
				// EVALUATE result of MathPiper
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
			
			// assignment:
			// return CAS result for commands and simple vars, e.g. f(x) := Derivative[ a x^2 ], b := 5 + 3
			// return input expression for functions
			if (assignment) {
				StringBuilder assignmentResult = new StringBuilder();
				assignmentResult.append(evalVE.getLabelForAssignment());
				assignmentResult.append(" := ");
				
				// keep structure of simple functions in output, e.g. f(x) := (1/2)*(x+2/x) 
				boolean keepStructure = evalVE instanceof Function && !((Function)evalVE).getExpression().isTopLevelCommand();
				if (keepStructure) {
					assignmentResult.append(evalVE.toString());
				} else {
					// return value of assigned variable
					try {
						// evaluate assignment variable like a or f(x)
						assignmentResult.append(casView.getCAS().getCurrentCAS().evaluateGeoGebraCAS(evalVE.getLabelForAssignment(), false));
					} catch (Throwable th1) {
						assignmentResult.append(CASResult);
					}
				}
				
				return assignmentResult.toString();
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
			GeoElement [] ggbEval = kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(casInput, false);

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

}
