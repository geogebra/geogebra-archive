package geogebra.cas;

import java.util.ResourceBundle;


import geogebra.kernel.arithmetic.ValidExpression;

public abstract class CASgeneric {
	
	protected CASparser casParser;
	
	public CASgeneric(CASparser casParser) {
		this.casParser = casParser;
	}
	
	/** 
	 * Evaluates an expression in GeoGebraCAS syntax and returns the resulting String in GeoGebra syntax.
	 * 
     * @return result string (null possible)
	 * @throws Throwable 
     */
	final public String evaluateGeoGebraCAS(String exp) throws Throwable {
		ValidExpression inVE = casParser.parseGeoGebraCASInput(exp);
		return evaluateGeoGebraCAS(inVE);
	}
	
	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra notation.
	 * @param casInput: in GeoGebraCAS syntax
	 * @return evaluation result
	 * @throws Throwable
	 */
	public abstract String evaluateGeoGebraCAS(ValidExpression casInput) throws Throwable;
	
	/**
	 * Returns the error message of the last call of evaluateGeoGebraCAS().
	 * @return null if last evaluation was successful.
	 */
	public abstract String getEvaluateGeoGebraCASerror();
	
	/**
	 * Returns whether var is a defined variable.
	 */
	public abstract boolean isVariableBound(String var);
	
	/**
	 * Unbinds (deletes) variable.
	 * @param var
	 * @param isFunction
	 */
	public abstract void unbindVariable(String var);
	
	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 */
	public abstract void reset();
	
	/**
	 * Returns the CAS command for the currently set CAS using the given key and command arguments. 
	 * For example, getCASCommand("Expand.0", {"3*(a+b)"}) returns "ExpandBrackets( 3*(a+b) )" when
	 * MathPiper is the currently used CAS.
	 */
	public abstract String getTranslatedCASCommand(String command);
	
}
