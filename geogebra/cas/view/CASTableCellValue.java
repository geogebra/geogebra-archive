package geogebra.cas.view;

import geogebra.cas.MaximaVersionUnsupportedExecption;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;
import geogebra.util.Util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;



public class CASTableCellValue {

	private String input, prefix, postfix, error, latex;
	private ValidExpression inputVE, evalVE, outputVE;
	private boolean allowLaTeX = true;
	private boolean suppressOutput = false;
	
	// input and output variables of this cell expression
	private TreeSet <String> invars;
	private String assignmentVar;
	private boolean includesRowReferences;
	
	// command names used
	private HashSet <String> cmdNames;
		
	private String evalCmd, evalComment;
	private CASView view;
	private Kernel kernel;
	private int row = -1;

	public CASTableCellValue(CASView view) {
		this.view = view;
		this.kernel = view.getApp().getKernel();
		
		input = "";
		inputVE = null;
		outputVE = null;
		prefix = "";
		evalVE = null;
		postfix = "";
		evalCmd = "";
		evalComment = "";
	}
	
	void setRow(int row) {
		this.row = row;		
	}
	
	int getRow() {
		return row;
	}

	/** 
	 * Returns the input of this row.
	 */
	public String getTranslatedInput() {
		String translatedInput = input;
		
		// replace all internal command names in input by local command names
		if (cmdNames != null) {
			Iterator<String> it = cmdNames.iterator();
			while (it.hasNext()) {
				String cmd = it.next();
				String localCmd = view.getApp().getCommand(cmd);
			
				// replace internal command name by local command name
				translatedInput = translatedInput.replaceAll(cmd, localCmd);
			}
		}
		
		return translatedInput;
	}
	
	/** 
	 * Returns the input of this row.
	 */
	public String getInternalInput() {
		return input;
	}

	/** 
	 * Returns the output of this row.
	 */
	public String getOutput() {
		if (error != null) {
			if (kernel.isTranslateCommandName())
				return view.getApp().getError(error);
			else 
				return error;
		}
			
		if (outputVE == null) 
			return "";
		else
			return outputVE.toAssignmentString();
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
			return evalVE.toString();
	}
	
	/** 
	 * Returns the evaluation expression (between prefix and postfix) of this row.
	 * This method is important to process this row using GeoGebraCAS.
	 */
	public ValidExpression getEvalVE() {
		return evalVE;
	}
	
	public ValidExpression getInputVE(){
		return inputVE;
	}
	
	public String getPostfix() {
		return postfix;
	}
	
	public void setAllowLaTeX(boolean flag) {
		allowLaTeX = flag;
	}
	
	public String getLaTeXOutput() {
		if (!allowLaTeX || isError())
			return null;
		else if (latex == null) {
			try {
				latex = view.getCAS().convertGeoGebraToLaTeXString(outputVE);
			} catch (Throwable th) {
				Application.debug("no latex for: " + getOutput());
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
		return outputVE == null && error == null;
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
	 * @return success
	 */
	public void setInput(String inValue) {
		if (input != null && input.equals(inValue)) return;
		
		input = inValue;
		suppressOutput = inValue.endsWith(";");

		// parse input into valid expression
		inputVE = parseGeoGebraCASInputAndResolveDummyVars(inValue);
		
		prefix = "";
		evalVE = inputVE;
		postfix = "";
		setEvalCommand("");
		evalComment = "";
		
		// update input and output variables
		updateInOutVars(inputVE);
		
		// make sure input uses only internal command names
		translateInput(inputVE);
	}
	
	private ValidExpression parseGeoGebraCASInputAndResolveDummyVars(String inValue) {
		try {
			// parse input into valid expression
			ValidExpression ve = view.getCAS().getCASparser().parseGeoGebraCASInput(inValue);
			
			// resolve Variable objects in ValidExpression as GeoDummy objects
			view.getCAS().getCASparser().resolveVariablesForCAS(ve);
			
			return ve;
		}catch (MaximaVersionUnsupportedExecption e) {
			throw e; // propagate exception
		}catch (Throwable e) {
			return null;
		}
		
		
	}
	
	/**
	 * Sets how this row should be evaluated. Note that the input
	 * is NOT changed by this method, so you need to call setInput()
	 * first.
	 * 
	 * @param prefix: beginning part that should NOT be evaluated
	 * @param eval: part of the input that needs to be evaluated
	 * @param postfix: end part that should NOT be evaluated
	 */
	public void setProcessingInformation(String prefix, String eval, String postfix) {
		setEvalCommand("");
		evalComment = "";
		
		// stop if input is assignment
		if (inputVE != null && inputVE.getLabel() != null) {
			if (eval.startsWith("KeepInput")) {
				setEvalCommand("KeepInput");
			}
			return;
		}
		
		// parse eval text into valid expression
		evalVE = parseGeoGebraCASInputAndResolveDummyVars(eval);
		if (evalVE != null) {
			if (evalVE.isTopLevelCommand()) {
				// extract command from eval
				setEvalCommand(evalVE.getTopLevelCommand().getName());
			}
			this.prefix = prefix;
			this.postfix = postfix;
		}
		else {
			evalVE = inputVE;
			this.prefix = "";
			this.postfix = "";
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
		if (!view.getCAS().isStructurallyEqual(input, newInput)) {			
			setError("CAS.SelectionStructureError");
			return false;
		}
		return true;
	}
	
	/**
	 * Sets input to use internal command names and translatedInput 
	 * to use localized command names. As a side effect, all command
	 * names are added as input variables as they could be function names.
	 */
	private void translateInput(ValidExpression ve) {	
		if (ve == null) {
			cmdNames = null;
			return;
		}
		
		// get all command names
		cmdNames = new HashSet<String>();
		ve.addCommandNames(cmdNames);
		
		// replace all local command names in input by internal command names
		Iterator<String> it = cmdNames.iterator();
		while (it.hasNext()) {
			String cmd = it.next();
			String localCmd = view.getApp().getCommand(cmd);
		
			// replace local command name by internal command in input
			input = input.replaceAll(localCmd, cmd);
		
			// add command name as invar as it could be a function name
			addInVar(cmd);
		}
	}
	
	/**
	 * Updates the sets of
	 * input and output variables. For example, the input "b := a + 5"
	 * has the 
	 */
	private void updateInOutVars(ValidExpression ve) {		
		// clear var sets
		clearInVars();
		
		if (ve == null) return;
		
		try {
			// check for function
			boolean isFunction = ve instanceof Function;
				
			// outvar of assignment b := a + 5 is "b"
			setAssignmentVar(ve.getLabel());

			// get input vars:
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
			invars = new TreeSet<String>();
		invars.add(var);
		
		includesRowReferences = includesRowReferences || 
			var.indexOf(CASInputHandler.ROW_REFERENCE_DYNAMIC) > -1;
	}
	
	private void clearInVars() {
		if (invars != null) invars.clear();
		includesRowReferences = false;
	}
	
	/**
	 * Returns the n-th input variable (in alphabetical order).
	 * @param i
	 * @return
	 */
	public String getInVar(int n) {
		if (invars == null) return null;
		
		Iterator<String> it = invars.iterator();
		int pos=0; 
		while (it.hasNext()) {
			String var = it.next();
			if (pos == n) return var;
			pos++;
		}
		
		return null;
	}
	
//	/**
//	 * Replaces all row references from by to in input.
//	 * @param from
//	 * @param to
//	 */
//	public void replaceRowReferences(String from, String to) {
//		if (includesRowReferences)
//			setInput(input.replaceAll(from, to));
//	}
	
	/**
	 * Returns whether var is an input variable of this cell. For example,
	 * "b" is an input variable of "c := a + b"
	 */
	final public boolean isInputVariable(String var) {
		return invars != null && invars.contains(var);
	}
	
	/**
	 * Returns whether var is a function variable of this cell. For example,
	 * "y" is a function variable of "f(y) := 2y + b"
	 */
	final public boolean isFunctionVariable(String var) {
		return inputVE instanceof Function && ((Function) inputVE).isFunctionVariable(var);
	}
	
	/**
	 * Returns whether var is a function variable of this cell. For example,
	 * "y" is a function variable of "f(y) := 2y + b"
	 */
	final public String getFunctionVariable() {
		return (inputVE instanceof Function) ? ((Function) inputVE).getFunctionVariable().getLabel() : null;
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
		setKeepInputUsed(evalCmd != null && (evalCmd.equals("KeepInput") || evalCmd.equals("ProperFraction") || evalCmd.equals("Substitute") || evalCmd.equals("ToPolar")));
	}
	
	public void setKeepInputUsed(boolean keepInputUsed) {
		if (inputVE != null)
			inputVE.setKeepInputUsed(keepInputUsed);
		if (evalVE != null)
			evalVE.setKeepInputUsed(keepInputUsed);
	}
	
	final public void setEvalComment(String comment) {
		if (comment != null)
			evalComment = comment;
	}
	
	final public String getEvalComment() {
		return evalComment;
	}

	public void setOutput(String output) {
		error = null;
		latex = null;

		// parse output into valid expression
		outputVE = parseGeoGebraCASInputAndResolveDummyVars(output);
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
			sb.append(Util.encodeXML(input));
			sb.append("\" ");
			
			if (evalVE != inputVE) {
				if (!"".equals(prefix)) {
					sb.append(" prefix=\"");
					sb.append(Util.encodeXML(prefix));
					sb.append("\" ");
				}
				
				sb.append(" eval=\"");
				sb.append(Util.encodeXML(getEvalText()));
				sb.append("\" ");
				
				if (!"".equals(postfix)) {
					sb.append(" postfix=\"");
					sb.append(Util.encodeXML(postfix));
					sb.append("\" ");
				}
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
			sb.append(Util.encodeXML(getOutput()));
			sb.append("\"");
			if (isError()) {
				sb.append(" error=\"true\"");
			}
			
			if (!"".equals(evalCmd)) {
				sb.append(" evalCommand=\"");
				sb.append(Util.encodeXML(evalCmd));
				sb.append("\" ");
			}
			
			if (!"".equals(evalComment)) {
				sb.append(" evalComment=\"");
				sb.append(Util.encodeXML(evalComment));
				sb.append("\" ");
			}
			
			sb.append("/>\n");
			sb.append("\t\t");
			sb.append("</outputCell>\n");
		}
		
		sb.append("\t</cellPair>\n");

		return sb.toString();
	}

	public Kernel getKernel() {
		return kernel;
		
	}

	// public void setLineBorderVisible(boolean inV){
	// isLineBorderVisible = inV;
	// }
}
