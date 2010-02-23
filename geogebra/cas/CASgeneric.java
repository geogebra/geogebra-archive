package geogebra.cas;

import java.util.ResourceBundle;

import org.qtitools.mathassess.tools.maximaconnector.RawMaximaSession;

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
	final public String evaluateGeoGebraCAS(String exp, boolean useGeoGebraVariables) throws Throwable {
		ValidExpression inVE = casParser.parseGeoGebraCASInput(exp);
		return evaluateGeoGebraCAS(inVE, useGeoGebraVariables);
	}
	
	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra notation.
	 * @param casInput: in GeoGebraCAS syntax
	 * @param useGeoGebraVariables: whether GeoGebra objects should be substituted before evaluation
	 * @return evaluation result
	 * @throws Throwable
	 */
	public abstract String evaluateGeoGebraCAS(ValidExpression casInput, boolean useGeoGebraVariables) throws Throwable;
	
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
	 * Returns the CAS command for the currently set CAS using the given key and command arguments. 
	 * For example, getCASCommand("Expand.0", {"3*(a+b)"}) returns "ExpandBrackets( 3*(a+b) )" when
	 * MathPiper is the currently used CAS.
	 */
	public abstract String getTranslatedCASCommand(String command);

}
