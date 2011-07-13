package geogebra.cas;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


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
	final public String evaluateGeoGebraCAS(String exp) throws Throwable {
		ValidExpression inVE = casParser.parseGeoGebraCASInput(exp);
		return evaluateGeoGebraCAS(inVE);
	}
	
	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra notation.
	 * @param casInput in GeoGebraCAS syntax
	 * @return evaluation result
	 * @throws Throwable
	 */
	public abstract String evaluateGeoGebraCAS(ValidExpression casInput) throws Throwable;
	
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
	
	/**
	 * Returns whether var is a defined variable.
	 * @param var the Variable
	 * @return true if the variable is defined, false otherwise.
	 */
	public abstract boolean isVariableBound(String var);
	
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
}
