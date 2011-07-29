package geogebra.cas;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.MyResourceBundle;

public abstract class CASgeneric {
	
	protected CASparser casParser;
	private ResourceBundle casTranslations;
	private String translationResourcePath;
	
	public CASgeneric(CASparser casParser, String translationResourcePath) {
		this.casParser = casParser;
		this.translationResourcePath = translationResourcePath;
	}
	
	/** 
	 * Evaluates an expression in GeoGebraCAS syntax and returns the resulting String in GeoGebra syntax.
	 * @param exp The expression in GeogebraCAS syntax.
     * @return result string (null possible)
	 * @throws Throwable 
     */
	final String evaluateGeoGebraCAS(String exp) throws Throwable {
		ValidExpression inVE = casParser.parseGeoGebraCASInput(exp);
		return evaluateGeoGebraCAS(inVE);
	}
	
	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra notation.
	 * @param casInput in GeoGebraCAS syntax
	 * @return evaluation result
	 * @throws Throwable
	 */
	abstract String evaluateGeoGebraCAS(ValidExpression casInput) throws Throwable;
	
	/** 
	 * Evaluates an expression in the syntax of the currently active CAS
	 * (MathPiper or Maxima).
	 * @param exp The expression to be evaluated.
     * @return result string (null possible)
	 * @throws Throwable 
     */
	public abstract String evaluateRaw(String exp) throws Throwable;
	
	/**
	 * Returns the error message of the last call of evaluateGeoGebraCAS().
	 * @return null if last evaluation was successful.
	 */
	public abstract String getEvaluateGeoGebraCASerror();
	
//	/**
//	 * Returns whether var is a defined variable.
//	 * @param var the Variable
//	 * @return true if the variable is defined, false otherwise.
//	 */
//	public abstract boolean isVariableBound(String var);
	
	/**
	 * Unbinds (deletes) variable.
	 * @param var the name of the variable.
	 */
	public abstract void unbindVariable(String var);
	
	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 */
	public abstract void reset();
	
	/**
	 * Returns the CAS command for the currently set CAS using the given key. 
	 * For example, getCASCommand"Expand.0" returns "ExpandBrackets( %0 )" when
	 * MathPiper is the currently used CAS.
	 * @param command The command to be translated (should end in ".n", where n is the number of arguments to this command).
	 * @return The command in CAS format, where parameter n is written as %n.
	 *
	 */
	public String getTranslatedCASCommand(String command)
	{
		if (casTranslations == null) {
			casTranslations = MyResourceBundle.loadSingleBundleFile(translationResourcePath);
		}

		String ret;
		try {
			ret = casTranslations.getString(command);
		} catch (MissingResourceException e) {
			ret = null;
		}

		return ret;
	}
	
	
	/**
	 * Translates a given expression in the format expected by the cas.
	 * @param ve the Expression to be translated
	 * @param casStringType one of ExpressionNode.STRING_TYPE_{MAXIMA, MPREDUCE, MATH_PIPER}
	 * @return the translated String.
	 */
	protected String translateToCAS(ValidExpression ve, int casStringType)
	{
		Kernel kernel = casParser.getKernel();
		int oldPrintForm = kernel.getCASPrintForm();
		kernel.setCASPrintForm(casStringType);
		
		try {
			ValidExpression tmp = ve;
			if (!ve.isExpressionNode())
				tmp = new ExpressionNode(casParser.getKernel(), ve);			
			
			String body = ((ExpressionNode) tmp).getCASstring(casStringType, true);			
			
			// handle assignments
			String label = ve.getLabel();
			if (label != null) { // is an assignment or a function declaration
				// make sure to escape labels to avoid problems with reserved CAS labels
				label = GeoElement.printLabel(casStringType, label);
				if (ve instanceof FunctionNVar) {
					FunctionNVar fun = (FunctionNVar) ve;
					return translateFunctionDeclaration(label, fun.getVarString(), body);
				}
				else	
					return translateAssignment(label, body);
			} else
				return body;
		}
		finally {
			kernel.setCASPrintForm(oldPrintForm);
		}
	}

	
	/**
	 * Translates a variable/constant assignment  like "x := 3" into the format expected by the CAS.
	 * Function-Assignments have to be translated using @see translateFunctionDeclaration().
	 * @param label the label of the assignment, e.g. x
	 * @param body the value that will be assigned to the label, e.g. "3"
	 * @return String in CAS format.
	 */
	public String translateAssignment(String label, String body)
	{
		// default implementation works for MPReduce and MathPiper
		return label + " := " + body;
	}


	/**
	 * Translates a function definition/function assignment like "f(x, y) = 3*x^2 + y" into the format expected by the CAS.
	 * Function-Assignments have to be translated using @see translateAssignment().
	 * @param label the name of the function, e.g. f
	 * @param parameters the parameters of the function, separated by commas, e.g. "x, y"
	 * @param body the body of the function.
	 * @return String in CAS format.
	 */
	public abstract String translateFunctionDeclaration(String label, String parameters, String body);
	
}
