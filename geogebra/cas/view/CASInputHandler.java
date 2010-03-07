package geogebra.cas.view;

import geogebra.cas.CASparser;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
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
				
		// save the edited value into the table model
		consoleTable.stopEditing();
		
		// get current row and input text		
		int selRow = consoleTable.getSelectedRow();	
		if (selRow < 0) selRow = consoleTable.getRowCount() - 1;
		CASTableCellValue cellValue = consoleTable.getCASTableCellValue(selRow);
		String selRowInput = cellValue.getInput();	
		if (selRowInput == null || selRowInput.length() == 0) {
			consoleTable.startEditingRow(selRow);
			return;
		}
		
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
		prefix = resolveCASrowReferences(prefix, selRow, ROW_REFERENCE_STATIC);
		evalText = resolveCASrowReferences(evalText, selRow, ROW_REFERENCE_STATIC);
		postfix = resolveCASrowReferences(postfix, selRow, ROW_REFERENCE_STATIC);
		
		// FIX common INPUT ERRORS in evalText
		if (ggbcmd.equals("Eval") || ggbcmd.equals("Hold"))
			evalText = fixInputErrors(evalText);
		
		// remember input selection information for future calls of processRow()
		cellValue.setInput(prefix, evalText, postfix);
	
		// Substitute dialog
		if (ggbcmd.equals("SubstituteDialog")) {
			// show substitute dialog
			CASSubDialog d = new CASSubDialog(casView, prefix, evalText, postfix, selRow);
			d.setVisible(true);
			return;
		}
		
		// standard case: evaluate and update row
		if (ggbcmd.equals("Eval")){
			// replace "Eval" by "Simplify"
			ggbcmd = "Simplify";
		}
		
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
		
		// update input selection information for future calls of processRow()
		cellValue.setInput(prefix, evalText, postfix);
		
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
		boolean success = false;
		
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
	final boolean processRow(int row) {
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
			result = evaluateGeoGebraCAS(cellValue.getEvalText(), row);
		} catch (Throwable th) {
			th.printStackTrace();
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
			// 	error = app.getError("CAS.GeneralErrorMessage");
			cellValue.setOutput(casView.getApp().getError("CAS.GeneralErrorMessage"), true);
			System.err.println("GeoGebraCAS.evaluateRow: " + casView.getCAS().getGeoGebraCASError());
		}
	}
	
	/**
	 * Evaluates evalText as GeoGebraCAS input. Dynamic references are
	 * resolved according to the given row number.
	 */
	private String evaluateGeoGebraCAS(String evalText, int row) throws Throwable {
		// resolve dynamic row references
		evalText = resolveCASrowReferences(evalText, row, ROW_REFERENCE_DYNAMIC);
		
		// process this input
		return processCASviewInput(evalText);
	}
	
	/**
	 * Replaces references to other rows (e.g. #3, %3) in input string by
	 * the values from those rows.
	 */
	public String resolveCASrowReferences(String inputExp, int selectedRow, char delimiter) {		
		StringBuilder sbCASreferences = new StringBuilder();
		
		int length = inputExp.length();
		for (int i = 0; i < length; i++) {
			char ch = inputExp.charAt(i);
			if (ch == delimiter) {
				int start = i+1;
				int end = start;
				char endCharacter = inputExp.charAt(end);
				
				// get digits after #
				while (end < length && Character.isDigit(endCharacter = inputExp.charAt(end))) {
					end++;
				}
				i = end;
				
				int rowRef;
				if (start == end) {
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

		return sbCASreferences.toString();
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
	 * Processes the CASview input string and returns an evaluation result. Note that this method
	 * can have side-effects on the GeoGebra kernel by creating new objects or deleting an existing object.
	 * 
	 * @return result as String in GeoGebra syntax
	 */
	 private synchronized String processCASviewInput(String inputExp) throws Throwable {		 
		// PARSE input to check if it's a valid expression
		ValidExpression inVE = casParser.parseGeoGebraCASInput(inputExp);
		String assignmentVar = inVE.getLabel();
		boolean assignment = assignmentVar != null;
		
		// EVALUATE input expression with current CAS
		String CASResult = null;
		Throwable throwable = null;
		try {
			// evaluate input in MathPiper and convert result back to GeoGebra expression
			CASResult = casView.getCAS().getCurrentCAS().evaluateGeoGebraCAS(inVE, casView.getUseGeoGebraVariableValues());
		} catch (Throwable th1) {
			throwable = th1;
			System.err.println("CAS evaluation failed: " + inputExp + "\n error: " + th1.toString());
		}
		boolean CASSuccessful = CASResult != null;
		
		// GeoGebra Evaluation needed?
		boolean evalInGeoGebra = false;
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
				evalInGeoGebra = !CASSuccessful || isDeleteCommand(inputExp) || containsCommand(CASResult);
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
					ggbResult = evalInGeoGebra(inputExp);
			} catch (Throwable th2) {
				if (throwable == null) throwable = th2;
				System.err.println("GeoGebra evaluation failed: " + inputExp + "\n error: " + th2.toString());
			}
			
			// inputExp failed with GeoGebra
			// try to evaluate result of MathPiper
			if (ggbResult == null && CASSuccessful) {
				String ggbEval = CASResult;
				if (assignment) {
					StringBuilder sb = new StringBuilder();
					sb.append(getLabelForAssignment(inVE));
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
			if (assignment && "true".equals(CASResult)) {
				// return value of assigned variable
				try {
					// evaluate assignment variable like f(x) or a 
					return casView.getCAS().getCurrentCAS().evaluateGeoGebraCAS(getLabelForAssignment(inVE), false);
				} catch (Throwable th1) {
					return CASResult;
				}
			} 
			else {
				// MathPiper evaluation worked
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
		 return inputExp.startsWith("Delete") || inputExp.startsWith(casView.getApp().getCommand("Delete"));	
	 }
	 
	 private boolean containsCommand(String CASResult) {
		 return  CASResult != null && CASResult.indexOf('[') > -1;
	 }

	 private String getLabelForAssignment(ValidExpression ve) {
			if (ve instanceof Function) {
				StringBuilder sb = new StringBuilder();
				// function, e.g. f(x) := 2*x
				Function fun = (Function) ve;
				sb.append(ve.getLabel());
				sb.append("(");
				sb.append(fun.getFunctionVariable());
				sb.append(")");	
				return sb.toString();
			} 
			else {
				return ve.getLabel();
			}
	 }
	 
		/**
		 * Evaluates expression with GeoGebra and returns the resulting string.
		 */
		private synchronized String evalInGeoGebra(String casInput) throws Throwable {
			// TODO: remove
			System.out.println("evalInGeoGebra: " + casInput);
			
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
