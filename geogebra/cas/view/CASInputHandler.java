package geogebra.cas.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CASInputHandler {
	
	public static char ROW_REFERENCE_STATIC = '#';
	public static char ROW_REFERENCE_DYNAMIC = '%';
	
	private CASView casView;
	private CASTable consoleTable;
	
	// equation patterns
	// 5 * (3x + 4 = 7) - 4
	private static Pattern equationPatternParentheses = Pattern.compile("(.*)\\((.+)=(.+)\\)(.*)");
	// 3x + 4 = 7
	private static Pattern equationPatternSimple = Pattern.compile("(.+)=(.+)");
	
	public CASInputHandler(CASView view) {
		this.casView = view;
		this.consoleTable = view.getConsoleTable();
	}
	
	/** Called from buttons and menus with for example:
	 *  "Integral", [par1, par2, ...]
	 *  Copied from apply(int mod)
	 */	
	public void processInput(String ggbcmd,String[] params){
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
		String newText = prefix + evalText + postfix;
		if (!newText.equals(cellValue.getInput())) {
			cellValue.setInput(newText);
		}
		
		// resolve dynamic references for prefix and postfix
		// dynamic references for evalText is handled in evaluateGeoGebraCAS()
		prefix = resolveCASrowReferences(prefix, selRow, ROW_REFERENCE_DYNAMIC);
		postfix = resolveCASrowReferences(postfix, selRow, ROW_REFERENCE_DYNAMIC);

		// DIRECT MathPiper use: line starts with "MathPiper:"
		boolean directMathPiperCall = selRowInput.startsWith("MathPiper:");
		if (directMathPiperCall) {
			evalText = selRowInput.substring(10);
		}
		
		// Create a CASSubDialog with the cell value
		else if (ggbcmd.equals("Substitute")) {
			CASSubDialog d = new CASSubDialog(casView, prefix, evalText, postfix, selRow);
			d.setVisible(true);
			return;
		}
		
		// handle equations specially for simplify, expand, and factor
		// simplify of equations should simplify lhs and rhs individually
		else if (ggbcmd.equals("Simplify") ||
				ggbcmd.equals("Expand") || 
				ggbcmd.equals("Factor")) 
		{
			 // equation in parentheses: 5 * (3x + 4 = 7) - 4		
			 Matcher m = equationPatternParentheses.matcher(evalText);
			 boolean isEquation = m.matches();							 
			 if (isEquation) {
				 // Simplify[5 * (3x + 4 = 7) - 4] gets 
				 // Simplify[5 * (3x + 4) - 4] = Simplify[5 * (7) - 4]
				 String pre = m.group(1);
				 String lhs = m.group(2);
				 String rhs = m.group(3);
				 String post = m.group(4);
				 StringBuilder sb = new StringBuilder();
				 sb.append(ggbcmd);
				 sb.append("[");
				 sb.append(pre);
				 sb.append("(");
				 sb.append(lhs);
				 sb.append(")");
				 sb.append(post);
				 sb.append("]");
				 sb.append("=");
				 sb.append(ggbcmd);
				 sb.append("[");
				 sb.append(pre);
				 sb.append("(");
				 sb.append(rhs);
				 sb.append(")");
				 sb.append(post);
				 sb.append("]");				 
				 evalText = sb.toString();
			 }
			 else {
				 // simple equation: 3x + 4 = 7
				 m = equationPatternSimple.matcher(evalText);
				 isEquation = m.matches();				 
				 if (isEquation) {
					 // Simplify[3x + 4 = 7] gets 
					 // Simplify[3x + 4] = Simplify[7]				
					 String lhs = m.group(1);
					 String rhs = m.group(2);
					 evalText=ggbcmd+"[" + lhs + "] = " + ggbcmd + "[" + rhs + "]";
				 }
				 else { 		
					 // standard case: no equation
					 evalText=ggbcmd+"["+ evalText + "]";
				 }
			 }						
		}
		
		// standard case
		else if (!ggbcmd.equals("Eval")){
			// use action command as command for mathpiper
			evalText=ggbcmd+"["+evalText+"]";
		}
									
		// process evalText
		String evaluation = null;
		try {
			if (directMathPiperCall) {
				// evaluate using MathPiper syntax
				evaluation = casView.getCAS().evaluateMathPiper(evalText);
			}
			else {
				// evaluate using GeoGebraCAS syntax
				evaluation = evaluateGeoGebraCAS(evalText, selRow);
			}
			
		} catch (Throwable th) {
			th.printStackTrace();
		}
		
		// Set the value into the table		
		if (evaluation != null)	{
			if (prefix.length() == 0 && postfix.length() == 0) {
				// no prefix, no postfix: just evaluation
				cellValue.setOutput(evaluation);
			} else {
				// make sure that evaluation is put into parentheses
				cellValue.setOutput(prefix + " (" + evaluation + ") " + postfix);
			}
			
			cellValue.setAllowLaTeX(!directMathPiperCall);
		} else {
			// 	error = app.getError("CAS.GeneralErrorMessage");
			cellValue.setOutput(casView.getCAS().getMathPiperError(), true);			
		}
		
		consoleTable.updateRow(selRow);
		
		if (evaluation != null)	
			// start editing next row (may create a new row)
			consoleTable.startEditingRow(selRow + 1);
		else
			consoleTable.startEditingRow(selRow);
		
	}//apply(String,String[]
	
	/**
	 * Evaluates evalText as GeoGebraCAS input. Dynamic references are
	 * resolved according to the given row number.
	 */
	public String evaluateGeoGebraCAS(String evalText, int row) throws Throwable {
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

}
