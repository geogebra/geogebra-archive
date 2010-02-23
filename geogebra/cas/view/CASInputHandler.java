package geogebra.cas.view;

import geogebra.cas.CASparser;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ValidExpression;

import java.util.regex.Pattern;

public class CASInputHandler {
	
	public static char ROW_REFERENCE_STATIC = '#';
	public static char ROW_REFERENCE_DYNAMIC = '$';
	
	private CASView casView;
	private Kernel kernel;
	private CASTable consoleTable;
	private CASparser casParser;

	public CASInputHandler(CASView view) {
		this.casView = view;
		kernel = view.getApp().getKernel();
		casParser = casView.getCAS().getCASparser();
		consoleTable = view.getConsoleTable();
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
		
		// change the input cell if the structure has changed during preprocessing
		String newText = prefix + evalText + postfix;
		if (!casView.getCAS().isStructurallyEqual(cellValue.getInput(), newText)) {			
			cellValue.setInput(newText);
		}
	
		// Substitute dialog
		if (ggbcmd.equals("SubstituteDialog")) {
			// show substitute dialog
			CASSubDialog d = new CASSubDialog(casView, prefix, evalText, postfix, selRow);
			d.setVisible(true);
			return;
		}
		
		// standard case: evaluate and update row
		if (!ggbcmd.equals("Eval")){
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
		
		// remember eval information for future automatic evaluations of processRow()
		cellValue.setEvalInformation(prefix, evalText, postfix);
		
		// process row using the prepared evalText
		String result = processRow(selRow, prefix, evalText, postfix);
			
		// update table
		consoleTable.updateRow(selRow);
		if (result != null)	
			// start editing next row (may create a new row)
			consoleTable.startEditingRow(selRow + 1);
		else
			consoleTable.startEditingRow(selRow);
	}
	
	/**
	 * Evaluates evalText and returns the result. Furthermore, the
	 * output of the selRow is set to prefix + result + postfix.
	 * 
	 * @param selRow row number
	 * @param prefix not evaluated text before evalText
	 * @param evalText evaluated text
	 * @param postfix not evaluated text after evalText
	 * @return result of evaluation
	 */
	public String processRow(int selRow, String prefix, String evalText, String postfix) {
		CASTableCellValue cellValue = consoleTable.getCASTableCellValue(selRow);
		
		// resolve dynamic references for prefix and postfix
		// dynamic references for evalText is handled in evaluateGeoGebraCAS()
		prefix = resolveCASrowReferences(prefix, selRow, ROW_REFERENCE_DYNAMIC);
		postfix = resolveCASrowReferences(postfix, selRow, ROW_REFERENCE_DYNAMIC);
			
		// process evalText
		String result = null;
		try {
			// evaluate using GeoGebraCAS syntax
			result = evaluateGeoGebraCAS(evalText, selRow);
		} catch (Throwable th) {
			System.err.println("GeoGebraCAS.evaluateRow " + selRow + ": " + th.getMessage());
		}
		
		// update cell
		setCellOutput(cellValue, prefix, result, postfix);
		
		return result;
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
		return processCASviewInput(evalText, casView.getUseGeoGebraVariableValues());
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
	 * @boolean useGeoGebraVariables: whether GeoGebra objects should be substituted before evaluation
	 * @return result as String in GeoGebra syntax
	 */
	 synchronized String processCASviewInput(String inputExp, boolean useGeoGebraVariables) throws Throwable {		
		// PARSE input to check if it's a valid expression
		ValidExpression inVE = casParser.parseGeoGebraCASInput(inputExp);
					
		// EVALUATE input expression with MathPiper
		String CASResult = null;
		Throwable throwable = null;
		try {
			// evaluate input in MathPiper and convert result back to GeoGebra expression
			CASResult = casView.getCAS().getCurrentCAS().evaluateGeoGebraCAS(inVE, useGeoGebraVariables);
		} catch (Throwable th1) {
			throwable = th1;
			System.err.println("CAS evaluation failed: " + inputExp + "\n error: " + th1.toString());
		}
		
		// check some things
		boolean assignment = inVE.getLabel() != null;
		boolean delete = inputExp.startsWith("Delete") || inputExp.startsWith(casView.getApp().getCommand("Delete"));
		boolean CASSuccessful = CASResult != null;
		boolean CASResultContainsCommands = CASResult != null && CASResult.indexOf('[') > -1;
		
		// EVALUATE input expression in GeoGebra if we have
		// - an assignments (e.g. a := 5, f(x) := x^2)
		// - or Delete, e.g. Delete[a]
		// - or MathPiper was not successful
		// - or MathPiper result contains commands
		boolean evalInGeoGebra = useGeoGebraVariables && 
			(assignment || delete || !CASSuccessful || CASResultContainsCommands); 
		String ggbResult = null;
		if (evalInGeoGebra) {
			// EVALUATE inputExp in GeoGebra
			try {
				// process inputExp in GeoGebra
				ggbResult = evalInGeoGebra(inputExp);
			} catch (Throwable th2) {
				if (throwable == null) throwable = th2;
				System.err.println("GeoGebra evaluation failed: " + inputExp + "\n error: " + th2.toString());
			}
			
			// inputExp failed with GeoGebra
			// try to evaluate result of MathPiper
			if (ggbResult == null && CASSuccessful) {
				// EVALUATE result of MathPiper
				try {
					// process mathPiperResult in GeoGebra
					ggbResult = evalInGeoGebra(CASResult);
				} catch (Throwable th2) {
					if (throwable == null) throwable = th2;
					System.err.println("GeoGebra evaluation failed: " + CASResult + "\n error: " + th2.toString());
				}
			}
		}
		
		// return result string:
		// use MathPiper if that worked, otherwise GeoGebra
		if (CASSuccessful) {
			if (assignment && "true".equals(CASResult)) {
				// MathPiper returned true: use ggbResult if we have one, otherwise return mathPiperResult
				if (ggbResult != null) {
					return ggbResult;
				} else {
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
