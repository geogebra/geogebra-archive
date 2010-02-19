package geogebra.cas.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CASInputHandler {
	
	public static char ROW_REFERENCE_STATIC = '#';
	public static char ROW_REFERENCE_DYNAMIC = '$';
	
	private CASView casView;
	private CASTable consoleTable;

	public CASInputHandler(CASView view) {
		this.casView = view;
		this.consoleTable = view.getConsoleTable();
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
		boolean hasSelectedText = selectedText == null || selectedText.trim().length() == 0;
		if (hasSelectedText) {
			// no selected text: evaluate input using current cell
			prefix = "";
			evalText = selRowInput;
			postfix = "";		
		}
		else {
			// selected text: break it up into prefix, evalText, and postfix
			prefix = selRowInput.substring(0, selStart).trim();
			evalText = selectedText;
			postfix = selRowInput.substring(selEnd).trim();
		}
		
		// resolve static row references and change input field accordingly
		prefix = resolveCASrowReferences(prefix, selRow, ROW_REFERENCE_STATIC);
		evalText = resolveCASrowReferences(evalText, selRow, ROW_REFERENCE_STATIC);
		postfix = resolveCASrowReferences(postfix, selRow, ROW_REFERENCE_STATIC);
		
		// FIX common INPUT ERRORS in evalText
		if (ggbcmd.equals("Eval") || ggbcmd.equals("Hold"))
			evalText = fixInputErrors(evalText);
		
		String newText = prefix + evalText + postfix;
		if (!newText.equals(cellValue.getInput())) {
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
			System.err.println("GeoGebraCAS.evaluateRow: " + casView.getCAS().getMathPiperError());
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
		return casView.getCAS().processCASInput(evalText, casView.getUseGeoGebraVariableValues());
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

}
