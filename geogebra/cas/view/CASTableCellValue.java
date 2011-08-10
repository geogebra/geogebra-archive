package geogebra.cas.view;

import geogebra.cas.CASparser;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;
import geogebra.util.Util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;



public class CASTableCellValue {

	private ValidExpression inputVE, evalVE, outputVE;
	private String input, prefix, postfix, error, latex;
	private String localizedInput;
	private Locale currentLocale;
	private boolean suppressOutput = false;
	
	// input and output variables of this cell expression
	private TreeSet <String> invars;
	// internal command names used in the input expression
	private HashSet <String> cmdNames;
	private String assignmentVar;
	private boolean includesRowReferences;
	private boolean includesNumericCommand;
		
	private String evalCmd, evalComment;
	private CASView view;
	private Kernel kernel;
	private int row = -1;

	public CASTableCellValue(CASView view) {
		this.view = view;
		this.kernel = view.getApp().getKernel();
		
		input = "";
		localizedInput = "";
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
	 * Returns the input of this row. Command names are localized when 
	 * kernel.isPrintLocalizedCommandNames() is true, otherwise internal command 
	 * names are used.
	 */
	public String getInput() {
		if (kernel.isPrintLocalizedCommandNames()) {
			// input with localized command names
			if (currentLocale != view.getApp().getLocale()) {
				updateLocalizedInput();
			}
			return localizedInput;
		} 
		else {
			// input with internal command names
			return input;
		}
	}

	/** 
	 * Returns the output of this row.
	 */
	public String getOutput() {
		if (error != null) {
			if (kernel.isPrintLocalizedCommandNames())
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
	
//	public void setAllowLaTeX(boolean flag) {
//		allowLaTeX = flag;
//	}
	
	public String getLaTeXOutput() {
		if (isError())
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
		suppressOutput = inValue.endsWith(";");

		// parse input into valid expression
		inputVE = parseGeoGebraCASInputAndResolveDummyVars(inValue);
				
		input = inValue != null ? inValue : ""; // remember exact user input
		prefix = "";
		evalVE = inputVE;
		postfix = "";
		setEvalCommand("");
		evalComment = "";
		
		// update input and output variables
		updateInOutVars(inputVE);
		
		// input should have internal command names
		input = internalizeInput(input);
		
		// for efficiency: input with localized command names
		updateLocalizedInput();					
	}
	
	private void updateLocalizedInput() {
		// for efficiency: localized input with local command names
		currentLocale = view.getApp().getLocale();
		localizedInput = localizeInput(input);
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
	 * Checks if newInput is structurally equal to the current input String.
	 */
	public boolean isStructurallyEqualToLocalizedInput(String newInput) {
		if (localizedInput != null && localizedInput.equals(newInput)) 
			return true;
		
		// check if the structure of inputVE and prefix + evalText + postfix is equal
		// this is important to catch wrong selections, e.g.
		// 2 + 2/3 is not equal to the selection (2+2)/3
		if (!view.getCAS().isStructurallyEqual(inputVE, newInput)) {			
			setError("CAS.SelectionStructureError");
			return false;
		}
		return true;
	}
	
	/**
	 * Parses the given expression and resolves variables as GeoDummy objects.
	 * The result is returned as a ValidExpression.
	 */
	private ValidExpression parseGeoGebraCASInputAndResolveDummyVars(String inValue) {
		try {			
			return view.getCAS().getCASparser().parseGeoGebraCASInputAndResolveDummyVars(inValue);
		}catch (Throwable e) {
			return null;
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
		
		// get all command names
		cmdNames = new HashSet<String>();
		ve.addCommandNames(cmdNames);
		if (cmdNames.isEmpty()) {
			cmdNames = null;
		} else {
			includesNumericCommand = cmdNames.contains("Numeric");
			getInVars().addAll(cmdNames);
		}
		
		// get all used GeoElement variables
		try {
			// check for function
			boolean isFunction = ve instanceof FunctionNVar;
				
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
	 * Sets input to use internal command names and translatedInput 
	 * to use localized command names. As a side effect, all command
	 * names are added as input variables as they could be function names.
	 */
	private String internalizeInput(String input) {	
		// local commands -> internal commands
		return translate(input, false); 
	}
	
	/** 
	 * Returns the input using command names in the current language.
	 */
	private String localizeInput(String input) {
		// replace all internal command names in input by local command names
		if (kernel.isPrintLocalizedCommandNames()) {
			// internal commands -> local commands
			return translate(input, true); 
		} else 
			// keep internal commands
			return input;
	}
	
	/**
	 * Translates given expression by replacing all command names
	 * @param exp
	 * @param toLocalCmd true: internalCmd -> localCmd, false: localCmd -> internalCmd
	 * @return translated expression
	 */
	private String translate(String exp, boolean toLocalCmd) {
		if (cmdNames == null) return exp;
		
		String translatedExp = exp;
		Iterator<String> it = cmdNames.iterator();
		while (it.hasNext()) {
			String internalCmd = it.next();
			String localCmd = view.getApp().getCommand(internalCmd);
			
			if (toLocalCmd) {
				// internal command names -> local command names
				translatedExp = replaceAllCommands(translatedExp, internalCmd, localCmd);
			} else {
				// local command names -> internal command names
				translatedExp = replaceAllCommands(translatedExp, localCmd, internalCmd);
			}
		}
		
		return translatedExp;
	}
	
	/**
	 * Replaces oldCmd command names by newCmd command names in expression.
	 */
	private static String replaceAllCommands(String expression, String oldCmd, String newCmd) {
		// build regex to find local command names
		StringBuilder regexPrefix = new StringBuilder();	
		regexPrefix.append("(?i)"); // ignore case
		regexPrefix.append("\\b"); // match words for command only, not parts of a word
		
		// replace commands with [
		StringBuilder regexSb = new StringBuilder(regexPrefix);
		regexSb.append(oldCmd);
		regexSb.append("[\\[]");
		StringBuilder newCmdSb = new StringBuilder(newCmd);
		newCmdSb.append("[");
		expression = expression.replaceAll(regexSb.toString(), newCmdSb.toString());
		
		// replace commands with (
		regexSb.setLength(0);
		regexSb.append(regexPrefix);
		regexSb.append(oldCmd);
		regexSb.append("[\\(]");
		newCmdSb.setLength(0);
		newCmdSb.append(newCmd);
		newCmdSb.append("(");
		return expression.replaceAll(regexSb.toString(), newCmdSb.toString());
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
		invars.add(var);
		includesRowReferences = includesRowReferences || 
			var.indexOf(CASInputHandler.ROW_REFERENCE_DYNAMIC) > -1;
	}
	
	private TreeSet<String> getInVars() {
		if (invars == null)
			invars = new TreeSet<String>();
		return invars;
	}
	
	private void clearInVars() {
		if (invars != null) invars.clear();
		includesRowReferences = false;
		includesNumericCommand = false;
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
		return inputVE instanceof Function && 
			((Function) inputVE).isFunctionVariable(var);
	}
	
	/**
	 * Returns the function variable string if input is a function or null otherwise. 
	 * For example, "m" is a function variable of "f(m) := 2m + b"
	 */
	final public String getFunctionVariable() {
		return (inputVE instanceof Function) ? 
				((Function) inputVE).getFunctionVariable().getLabel() 
				: null;
	}
	
	/**
	 * Returns whether this cell includes row references like $2.
	 */
	final public boolean includesRowReferences() {
		return includesRowReferences;
	}
	
	/**
	 * Returns whether this cell includes any Numeric[] commands.
	 */
	final public boolean includesNumericCommand() {
		return includesNumericCommand;
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

		includesNumericCommand = includesNumericCommand || 
			evalCmd != null && evalCmd.equals("Numeric");
		setKeepInputUsed(evalCmd != null && evalCmd.equals("KeepInput"));
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

		CASparser parser = view.getCAS().getCASparser();
		Kernel kernel = parser.getKernel();
		boolean oldValue = kernel.isKeepCasNumbers();
		
		// make sure numbers and their precision are kept from Numeric[] commands
		kernel.setKeepCasNumbers(includesNumericCommand);
		
		// parse output into valid expression
		outputVE = parseGeoGebraCASInputAndResolveDummyVars(output);	
		
		kernel.setKeepCasNumbers(oldValue);
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
