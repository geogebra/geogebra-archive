package geogebra.cas.view;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;
import geogebra.util.Util;

import java.util.HashSet;
import java.util.Iterator;



public class CASTableCellValue {
	private ValidExpression inputVE, evalVE, outputVE;
	private String prefix, postfix, error, latex, prevInput;
	private boolean allowLaTeX = true;
	private boolean suppressOutput = false;
	
	// input and output variables of this cell expression
	private HashSet <String> invars;
	private String assignmentVar;
	private boolean includesRowReferences;
		
	private String evalCmd;
	private CASView view;
	private Kernel kernel;
	private int row;

	public CASTableCellValue(CASView view) {
		this.view = view;
		this.kernel = view.getApp().getKernel();
		
		inputVE = null;
		outputVE = null;
		prefix = "";
		evalVE = null;
		postfix = "";
		evalCmd = "";
	}
	
	void setRow(int row) {
		this.row = row;
	}
	
	int getRow() {
		return row;
	}

	/** 
	 * Returns the input of this row using command names in the application's language.
	 */
	public String getLocalizedInput() {
		if (inputVE == null) 
			return "";
		else
			return inputVE.toString();
	}
	
	/** 
	 * Returns the input of this row using internal command names.
	 */
	public String getInternalInput() {
		if (inputVE == null) 
			return "";
		else
			return toInternalString(inputVE);
	}

	/** 
	 * Returns the output of this row using command names in the application's language.
	 */
	public String getLocalizedOutput() {
		if (error != null)
			return view.getApp().getError(error);
			
		if (outputVE == null) 
			return "";
		else
			return outputVE.toString();
	}
	
	/** 
	 * Returns the output of this row using internal command names.
	 */
	public String getInternalOutput() {
		if (error != null)
			return error;
		
		if (outputVE == null) 
			return "";
		else
			return toInternalString(outputVE);
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	/** 
	 * Returns the evaluation text (between prefix and postfix) of this row using internal command names.
	 * This method is important to process this row using GeoGebraCAS.
	 */
	public String getEvalText() {
		if (evalVE == null) 
			return "";
		else
			return toInternalString(evalVE);
	}
	
	public ValidExpression getEvalVE() {
		return evalVE;
	}
	
	public String getPostfix() {
		return postfix;
	}
	
	/** 
	 * Returns the given expression using internal command names.
	 */
	private String toInternalString(ValidExpression ve) {
		int oldPrintForm = kernel.getCASPrintForm();
		boolean oldValue = kernel.isTranslateCommandName();
		kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA_XML);
        kernel.setTranslateCommandName(false); 
       
        String result = ve.toString();
		
        kernel.setCASPrintForm(oldPrintForm);
		kernel.setTranslateCommandName(oldValue); 
		
		return result;
	}
	
	public void setAllowLaTeX(boolean flag) {
		allowLaTeX = flag;
	}
	
	public String getLaTeXOutput() {
		if (!allowLaTeX || isError())
			return null;
		else if (latex == null) {
			try {
				latex = view.getCAS().convertGeoGebraToLaTeXString(getLocalizedOutput());
			} catch (Throwable th) {
				System.err.println("no latex for: " + getLocalizedOutput());
				latex = "";
			}
		}
		
		return latex;
	}
	
	public boolean isEmpty() {
		return isInputEmpty() && isOutputEmpty();
	}
	
	public boolean isInputEmpty() {
		return inputVE == null;
	}
	
	public boolean isOutputEmpty() {
		return outputVE == null;
	}
	
	public boolean showOutput() {
		return !isOutputEmpty()  && !suppressOutput();
	}
	
	private boolean suppressOutput() {
		return suppressOutput && !isError();
	}

	/**
	 * Sets the input of this row.
	 * @param inValue
	 */
	public void setInput(String inValue) {
		if (prevInput != null && prevInput.equals(inValue)) return;
		prevInput = inValue;
		
		try {
			// parse input into valid expression
			inputVE = view.getCAS().getCASparser().parseGeoGebraCASInput(inValue);
			suppressOutput = inValue.endsWith(";");
		} catch (Throwable e) {
			inputVE = null;
			suppressOutput = false;
		}

		prefix = "";
		evalVE = inputVE;
		postfix = "";
		
		// update input and output variables
		updateInOutVars(inputVE);
	}
	
	/**
	 * Sets the input in parts where prefix + eval + postfix
	 * are assumed to be structurally equal to the current input.
	 * Note that setInput() needs to be called separately.
	 * 
	 * @param prefix: beginning part that should not be evaluated
	 * @param eval: selected part of the input that needs to be evaluated
	 * @param postfix: end part that should not be evaluated
	 */
	public void setInput(String prefix, String eval, String postfix) {
		this.prefix = prefix;
		this.postfix = postfix;
		
		evalCmd = "";
		try {
			// parse eval text into valid expression
			evalVE = view.getCAS().getCASparser().parseGeoGebraCASInput(eval);
			if (evalVE.isTopLevelCommand()) {
				// extract command from eval
				int bracketPos = getEvalText().indexOf('[');
				evalCmd = bracketPos > 0 ? eval.substring(0, bracketPos) : "";
			}
		} catch (Throwable e) {
			evalVE = null;
		}
	}
	
	/**
	 * Checks if prefix + eval + postfix
	 * is structurally equal to the current input.
	 * 
	 * @param prefix: beginning part that should not be evaluated
	 * @param eval: selected part of the input that needs to be evaluated
	 * @param postfix: end part that should not be evaluated
	 */
	public boolean isStructurallyEqualToLocalizedInput(String newInput) {
		// check if the structure of inputVE and prefix + evalText + postfix is equal
		// this is important to catch wrong selections, e.g.
		// 2 + 2/3 is not equal to the selection (2+2)/3
		if (!view.getCAS().isStructurallyEqual(getLocalizedInput(), newInput)) {			
			setError("CAS.SelectionStructureError");
			return false;
		}
		return true;
	}
	
	/**
	 * Parses the input string and updates the sets of
	 * input and output variables. For example, the input "b := a + 5"
	 * has the 
	 */
	private void updateInOutVars(ValidExpression ve) {		
		try {
			// clear var sets
			clearInVars();
			
			// check for function
			boolean isFunction = ve instanceof Function;
				
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
					String var = geo.getLabel();
					
					// local function variables are NOT input variables
					// e.g. f(k) := k^2 + 3 does NOT depend on k
					if (!(isFunction && ((Function) ve).isFunctionVariable(var))) {
						addInVar(var);
					}
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
	 * Returns whether this cell has no inputVariables and no row references.
	 */
	final public boolean isIndependent() {
		return !includesRowReferences && (invars == null || invars.isEmpty()); 
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
	
	final public void setEvalCommand(String cmd) {
		evalCmd = cmd;
	}

	public void setOutput(String output) {
		error = null;
		latex = null;

		try {
			// parse input into valid expression
			outputVE = view.getCAS().getCASparser().parseGeoGebraCASInput(output);
		} catch (Throwable e) {
			outputVE = null;
			e.printStackTrace();
		}
	}
	
	public void setError(String error) {
		this.error = error;
		latex = null;
	}
	
	public boolean isError() {
		return error != null;
	}

	// generate the XML file for this CASTableCellValue
	public String getXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("\t<cellPair>\n");

		// inputCell
		if (!isInputEmpty()) {
			sb.append("\t\t");
			sb.append("<inputCell>\n");
			sb.append("\t\t\t");
			sb.append("<expression");
			sb.append(" value=\"");
			sb.append(Util.encodeXML(getInternalInput()));
			sb.append("\" ");
			
			if (evalVE != inputVE) {
				sb.append(" prefix=\"");
				sb.append(Util.encodeXML(prefix));
				sb.append("\" ");
				sb.append(" eval=\"");
				sb.append(Util.encodeXML(getEvalText()));
				sb.append("\" ");
				sb.append(" postfix=\"");
				sb.append(Util.encodeXML(postfix));
				sb.append("\" ");
			}
			
			sb.append("/>\n");
			sb.append("\t\t");
			sb.append("</inputCell>\n");
		}

		// outputCell
		if (!isOutputEmpty()) {
			sb.append("\t\t");
			sb.append("<outputCell>\n");
			sb.append("\t\t\t");
			sb.append("<expression");
			
			sb.append(" value=\"");
			sb.append(Util.encodeXML(getInternalOutput()));
			sb.append("\"");
			if (isError()) {
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
