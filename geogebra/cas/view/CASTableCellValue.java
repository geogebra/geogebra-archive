package geogebra.cas.view;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;
import geogebra.util.Util;

import java.util.HashSet;
import java.util.Iterator;



public class CASTableCellValue {
	private String input, prefix, eval, postfix, output, latex;
	private boolean error = false;
	private boolean allowLaTeX = true;
	
	// input and output variables of this cell expression
	private HashSet <String> invars;
	private String assignmentVar;
	private boolean includesRowReferences;
		
	private String evalCmd;
	private CASView view;
	private int row;

	public CASTableCellValue(CASView view) {
		this.view = view;
		
		input = "";
		output = "";
		prefix = "";
		eval = "";
		postfix = "";
		evalCmd = "";
	}
	
	void setRow(int row) {
		this.row = row;
	}
	
	int getRow() {
		return row;
	}

	public String getInput() {
		return input;
	}

	public String getOutput() {
		return output;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getEvalText() {
		return eval;
	}
	
	public String getPostfix() {
		return postfix;
	}
	
	public void setAllowLaTeX(boolean flag) {
		allowLaTeX = flag;
	}
	
	public String getLaTeXOutput() {
		if (error || !allowLaTeX)
			return null;
		else if (latex == null) {
			try {
				latex = view.getCAS().convertGeoGebraToLaTeXString(output);
			} catch (Throwable th) {
				System.err.println("no latex for: " + output);
				latex = "";
			}
		}
		
		return latex;
	}

	public boolean isOutputVisible() {
		return output == null || output.length() == 0;
	}
	
	public boolean isEmpty() {
		return isInputEmpty() && isOutputEmpty();
	}
	
	public boolean isInputEmpty() {
		return (input == null || input.length() == 0);
	}
	
	public boolean isOutputEmpty() {
		return (output == null || output.length() == 0 );
	}
	
	public boolean showOutput() {
		return !isOutputEmpty()  && !suppressOutput();
	}
	
	private boolean suppressOutput() {
		return !isOutputError() && (input != null && input.endsWith(";"));
	}

	/**
	 * Sets the input of this
	 * @param inValue
	 */
	public void setInput(String inValue) {
		if (input.equals(inValue)) return;
		
		input = inValue;
		prefix = "";
		eval = input;
		postfix = "";
		
		// update input and output variables
		updateInOutVars(input);
	}
	
	/**
	 * Sets the input in parts where prefix + eval + postfix
	 * are ensured to be structurally equal to the current input.
	 * If the structure is different, the current input is changed.
	 * For example "2 + 3/4" is structurally equal to "2 + (3/4)",
	 * but structurally different from "(2 + 3)/4".
	 * 
	 * @param prefix: beginning part that should not be evaluated
	 * @param eval: selected part of the input that needs to be evaluated
	 * @param postfix: end part that sould not be evaluated
	 */
	public void setInput(String prefix, String eval, String postfix) {
		this.prefix = prefix;
		this.eval = eval;
		this.postfix = postfix;
		
		// change the input if the structure of prefix + evalText + postfix is different
		String newText = prefix + eval + postfix;
		if (!view.getCAS().isStructurallyEqual(getInput(), newText)) {			
			setInput(newText);
		}
		
		// extract command from eval
		int bracketPos = eval.indexOf('[');
		evalCmd = bracketPos > 0 ? eval.substring(0, bracketPos) : "";
	}
	
	/**
	 * Parses the input string and updates the sets of
	 * input and output variables. For example, the input "b := a + 5"
	 * has the 
	 */
	private void updateInOutVars(String input) {		
		try {
			// clear var sets
			clearInVars();

			// parse input expression
			ValidExpression ve = view.getCAS().getCASparser().parseGeoGebraCASInput(input);
			
			// outvar of assignment b := a + 5 is "b"
			setAssignmentVar(ve.getLabel());

			// get input vars: 
			// resolve Variable objects in ValidExpression as GeoDummy objects
			Kernel kernel = view.getApp().getKernel();
			kernel.setResolveVariablesForCASactive(true);
			ve.resolveVariables();
			kernel.setResolveVariablesForCASactive(false);
			HashSet geoVars = ve.getVariables();
			if (geoVars != null) {
				Iterator it = geoVars.iterator();
				while (it.hasNext()) {
					GeoElement geo = (GeoElement) it.next();
					addInVar(geo.getLabel());
				}
			}
		} 
		catch (Throwable th) {
		}
	}
	
	/**
	 * Set assignment var of this cell. For example "b := a^2 + 3"
	 * has assignment var "b".
	 * @param var
	 */
	private void setAssignmentVar(String var) {
		assignmentVar = var;
		view.setAssignment(var, this);
	}
	
	private void addInVar(String var) {
		if (invars == null)
			invars = new HashSet<String>();
		invars.add(var);
		
		includesRowReferences = includesRowReferences || 
			var.indexOf(CASInputHandler.ROW_REFERENCE_DYNAMIC) > -1;
	}
	
	private void clearInVars() {
		if (invars != null) invars.clear();
		includesRowReferences = false;
	}
	
	/**
	 * Returns whether var is an input variable of this cell. For example,
	 * "b" is an input variable of "c := a + b"
	 */
	final public boolean isInputVariable(String var) {
		return invars != null && invars.contains(var);
	}
	
	/**
	 * Returns whether this cell includes row references like $2.
	 */
	final public boolean includesRowReferences() {
		return includesRowReferences;
	}
	
	/**
	 * Returns the assignment variable of this cell. For example,
	 * "c" is the assignment variable of "c := a + b"
	 * @return may be null
	 */
	final public String getAssignmentVariable() {
		return assignmentVar;
	}
	
	final public String getEvalCommand() {
		return evalCmd;
	}

	public void setOutput(String inValue) {
		setOutput(inValue, false);
	}
	
	public void setOutput(String output, boolean isError) {
		this.output = output;
		error = isError;
		latex = null;
	}
	
	public boolean isOutputError() {
		return error;
	}

	// generate the XML file for this CASTableCellValue
	public String getXML() {
		String input = getInput();
		String output = getOutput();
		
		boolean inputEmpty = input == null || input.length() == 0;
		boolean outputEmpty = output == null || output.length() == 0;
		
		StringBuilder sb = new StringBuilder();
		sb.append("\t<cellPair>\n");

		// inputCell
		if (!inputEmpty) {
			sb.append("\t\t");
			sb.append("<inputCell>\n");
			sb.append("\t\t\t");
			sb.append("<expression");
			sb.append(" value=\"");
			sb.append(Util.encodeXML(input));
			sb.append("\"/>\n");
			sb.append("\t\t");
			sb.append("</inputCell>\n");
		}

		// outputCell
		if (!outputEmpty) {
			sb.append("\t\t");
			sb.append("<outputCell>\n");
			sb.append("\t\t\t");
			sb.append("<expression");
			sb.append(" value=\"");
			sb.append(Util.encodeXML(output));
			sb.append("\"");
			if (error) {
				sb.append(" error=\"true\"");
			}
			sb.append("/>\n");
			sb.append("\t\t");
			sb.append("</outputCell>\n");
		}
		
		sb.append("\t</cellPair>\n");

		return sb.toString();
	}

	// public void setLineBorderVisible(boolean inV){
	// isLineBorderVisible = inV;
	// }
}
