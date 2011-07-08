package geogebra.cas;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides an interface for GeoGebra to use the computer algebra
 * systems Maxima and MathPiper.
 * 
 * @author Markus Hohenwarter
 */
public class GeoGebraCAS {
	/**
	 * Timeout for CAS in seconds.
	 */
	private static int timeout = 5;

	private Application app;
	private CASparser casParser;
	private CASgeneric cas;
	public int currentCAS = -1;

	public GeoGebraCAS(Kernel kernel) {
		app = kernel.getApplication();
		casParser = new CASparser(kernel);
		setCurrentCAS(Kernel.DEFAULT_CAS);
	}
	
	public CASparser getCASparser() {
		return casParser;
	}
	
	public CASgeneric getCurrentCAS() {
		return cas;
	}
	
	public int getCurrentCASstringType() {
		switch (currentCAS) {
			case Application.CAS_MAXIMA:
				return ExpressionNode.STRING_TYPE_MAXIMA;
				
			case Application.CAS_MPREDUCE:
				return ExpressionNode.STRING_TYPE_MPREDUCE;
				
			default:
			case Application.CAS_MATHPIPER:
				return ExpressionNode.STRING_TYPE_MATH_PIPER;	
		}
	}
	
	/**
	 * Sets the currently used CAS for evaluateGeoGebraCAS().
	 * @param CAS: use CAS_MATHPIPER or CAS_MAXIMA
	 */
	public void setCurrentCAS(int CAS) {
		try {
			switch (CAS) {
				case Application.CAS_MAXIMA:
					cas = getMaxima();
					((CASmaxima) cas).initialize();
					currentCAS = CAS;
					break;
					
				case Application.CAS_MPREDUCE:
					cas = getMPReduce();
					currentCAS = CAS;
					break;	
				
				default:
					cas = getMathPiper();
					currentCAS = CAS;
					break;
			}
		}catch (MaximaVersionUnsupportedExecption e){
			app.showError("CAS.MaximaVersionUnsupported");
			setCurrentCAS(Application.CAS_MATHPIPER);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 */
	public void reset() {
		cas.reset();
	}
	
	private CASmathpiper getMathPiper() {
		if (currentCAS == Application.CAS_MATHPIPER)
			return (CASmathpiper) cas;
		else
			return new CASmathpiper(casParser, new CasParserToolsImpl('e'));
	}
	
	private CASmaxima getMaxima() {
		if (currentCAS == Application.CAS_MAXIMA)
			return (CASmaxima) cas;
		else
			return new CASmaxima(casParser, new CasParserToolsImpl('b'));
	}
	
	private CASmpreduce getMPReduce() {
		if (currentCAS == Application.CAS_MPREDUCE)
			return (CASmpreduce) cas;
		else
			return new CASmpreduce(casParser);
	}
	
	/**
	 * Returns whether var is a defined variable.
	 */
	public boolean isVariableBound(String var) {
		return cas.isVariableBound(var);
	}
	
	/**
	 * Unbinds (deletes) variable.
	 * @param var
	 * @param isFunction
	 */
	public void unbindVariable(String var) {
		cas.unbindVariable(var);
	}
	
	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra notation.
	 * @param casInput: in GeoGebraCAS syntax
	 * @return evaluation result
	 * @throws Throwable
	 */
	public String evaluateGeoGebraCAS(ValidExpression casInput) throws Throwable {

		Kernel kernel = app.getKernel();
		boolean oldDigits = kernel.internationalizeDigits;
		kernel.internationalizeDigits = false;
		
		String ret = cas.evaluateGeoGebraCAS(casInput);
		
		kernel.internationalizeDigits = oldDigits;
		
		return ret;
	}
	
	/** 
	 * Evaluates an expression in GeoGebraCAS syntax with the currently active CAS
	 * (MathPiper or Maxima).
     * @return result string (null possible)
	 * @throws Throwable 
     */
	final public String evaluateGeoGebraCAS(String exp) throws Throwable {
		return cas.evaluateGeoGebraCAS(exp);
	}
	
	/** 
	 * Evaluates an expression in the syntax of the currently active CAS
	 * (MathPiper or Maxima).
     * @return result string (null possible)
	 * @throws Throwable 
     */
	final public String evaluateRaw(String exp) throws Throwable {
		return cas.evaluateRaw(exp);
	}
	
	/**
	 * Returns the error message of the last call of evaluateGeoGebraCAS().
	 * @return null if last evaluation was successful.
	 */
	final synchronized public String getGeoGebraCASError() {
		return cas.getEvaluateGeoGebraCASerror();
	}
	
	/** 
	 * Evaluates an expression in MathPiper syntax.
     * @return result string (null possible)
     */
	final public String evaluateMathPiper(String exp) {		
		return getMathPiper().evaluateMathPiper(exp);
	}
	
	/** 
	 * Evaluates an expression in Maxima syntax.
     * @return result string (null possible)
     */
	final public String evaluateMaxima(String exp) {
		return getMaxima().evaluateMaxima(exp);
	}
	
	final public String evaluateMPReduce(String exp)
	{
		return getMPReduce().evaluateMPReduce(exp);
	}
	

	// these variables are cached to gain some speed in getPolynomialCoeffs
	private Map<String, String[]> getPolynomialCoeffsCache = new HashMap<String, String[]>(64);
	private StringBuilder getPolynomialCoeffsSB = new StringBuilder();
	private StringBuilder sbPolyCoeffs = new StringBuilder();
	
	/**
	 * Expands the given MathPiper expression and tries to get its polynomial
	 * coefficients. The coefficients are returned in ascending order. If exp is
	 * not a polynomial, null is returned.
	 * 
	 * example: getPolynomialCoeffs("3*a*x^2 + b"); returns ["b", "0", "3*a"]
	 */
	final public String[] getPolynomialCoeffs(String polyExpr, String variable) {
		getPolynomialCoeffsSB.setLength(0);
		getPolynomialCoeffsSB.append(polyExpr);
		getPolynomialCoeffsSB.append(',');
		getPolynomialCoeffsSB.append(variable);
		
		String[] result = getPolynomialCoeffsCache.get(getPolynomialCoeffsSB.toString());
		if (result != null)
			return result;
		
		sbPolyCoeffs.setLength(0);
		sbPolyCoeffs.append("coeff(");
		sbPolyCoeffs.append(getPolynomialCoeffsSB.toString());
		sbPolyCoeffs.append(')');
			
		try {
			// expand expression and get coefficients of
			// "3*a*x^2 + b" in form "{ b, 0, 3*a }" 
			String tmp = evaluateMPReduce(sbPolyCoeffs.toString());

			if ("{}".equals(tmp) || "".equals(tmp) || tmp == null)  // no result
				return null;

			tmp = tmp.substring(1, tmp.length()-1); // strip '{' and '}'
			result = tmp.split(",");
			
			getPolynomialCoeffsCache.put(getPolynomialCoeffsSB.toString(), result);
            return result;						
		} 
		catch(Throwable e) {
			Application.debug("GeoGebraCAS.getPolynomialCoeffs(): " + e.getMessage());
			//e.printStackTrace();
		}
		
		return null;
	}


	

	
	/**
	 * Tries to convert an expression in GeoGebra syntax into a LaTeX string.
	 * 
	 * @return null if something went wrong or the resulting String doesn't contain
	 * any LaTeX commands (i.e. no \).
	 */
	public synchronized String convertGeoGebraToLaTeXString(ValidExpression ggbExp) {
		if (ggbExp == null)
			return null;
		
		String latex = ggbExp.toAssignmentLaTeXString();
		
		// render in latex if necessary
		// eg if contains ^2 or \frac
		if (Application.isLaTeXneeded(latex)) return latex;
		else return ggbExp.toString();
					
	}
	
	
	
	final private String toString(ExpressionValue ev, boolean symbolic) {
		if (symbolic)
			return ev.toString();
		else
			return ev.toValueString();
		
	}
	
	/**
	 * Returns the CAS command for the currently set CAS using the given key and command arguments. 
	 * For example, getCASCommand("Expand.1", {"3*(a+b)"}) returns "ExpandBrackets( 3*(a+b) )" when
	 * MathPiper is the currently used CAS.
	 */
	final synchronized public String getCASCommand(String name, ArrayList args, boolean symbolic) {
		StringBuilder sbCASCommand = new StringBuilder(80);
				
		// build command key as name + ".N"
		sbCASCommand.setLength(0);
		sbCASCommand.append(name);
		sbCASCommand.append(".N");
		
		String translation = cas.getTranslatedCASCommand(sbCASCommand.toString());

		// check for eg Sum.N=sum(%)
		if (translation != null) {
			sbCASCommand.setLength(0);
			for (int i = 0; i < translation.length(); i++) {
				char ch = translation.charAt(i);
				if (ch == '%') {
					if (args.size() == 1) { // might be a list as the argument
						ExpressionValue ev = (ExpressionValue) args.get(0);
						String str = toString(ev, symbolic);
						if (ev.isListValue()) {
							// is a list, remove { and }
							sbCASCommand.append(str.substring(1, str.length() - 1));
						} else {
							// not a list, just append
							sbCASCommand.append(str);
						}
					}
					else {
						for (int j=0; j < args.size(); j++) {
						ExpressionValue ev = (ExpressionValue) args.get(j);				
						sbCASCommand.append(toString(ev, symbolic));
						sbCASCommand.append(',');
						}
						// remove last comma
						sbCASCommand.setLength(sbCASCommand.length() - 1);
					}
				} else {
					sbCASCommand.append(ch);
				}
					
			}
			
			return sbCASCommand.toString();
		}
		
		// build command key as name + "." + args.size()
		// remove 'N'
		sbCASCommand.setLength(sbCASCommand.length() - 1);
		// add eg '3'
		sbCASCommand.append(args.size());
		
		// get translation ggb -> MathPiper/Maxima
		translation = cas.getTranslatedCASCommand(sbCASCommand.toString());
		sbCASCommand.setLength(0);		
		
		// no translation found: 
		// use key as command name
		if (translation == null) {			
			sbCASCommand.append(name);
			sbCASCommand.append('(');
			for (int i=0; i < args.size(); i++) {
				ExpressionValue ev = (ExpressionValue) args.get(i);				
				sbCASCommand.append(toString(ev,symbolic));
				sbCASCommand.append(',');
			}
			sbCASCommand.setCharAt(sbCASCommand.length()-1, ')');
		}
		
		// translation found: 
		// replace %0, %1, etc. in translation by command arguments
		else {
			for (int i = 0; i < translation.length(); i++) {
				char ch = translation.charAt(i);
				if (ch == '%') {
					// get number after %
					i++;
					int pos = translation.charAt(i) - '0';
					if (pos >= 0 && pos < args.size()) {
						// success: insert argument(pos)
						ExpressionValue ev = (ExpressionValue) args.get(pos);				
						if (symbolic)
							sbCASCommand.append(ev.toString());
						else
							sbCASCommand.append(ev.toValueString());
					} else {
						// failed
						sbCASCommand.append(ch);
						sbCASCommand.append(translation.charAt(i));
					}
				} else {
					sbCASCommand.append(ch);
				}
			}
		}

		return sbCASCommand.toString();
	}
	
	/**
	 * Returns true if the two input expressions are structurally equal. 
	 * For example "2 + 2/3" is structurally equal to "2 + (2/3)"
	 * but unequal to "(2 + 2)/3"
	 */
	public boolean isStructurallyEqual(String input1, String input2) {
		if (input1.equals(input2)) return true;
	
		try {
			// parse both input expressions
			ValidExpression ve1 = casParser.parseGeoGebraCASInput(input1);
			String input1normalized = casParser.toString(ve1, ExpressionNode.STRING_TYPE_GEOGEBRA_XML);
			
			ValidExpression ve2 = casParser.parseGeoGebraCASInput(input2);
			String input2normalized = casParser.toString(ve2, ExpressionNode.STRING_TYPE_GEOGEBRA_XML);
			
			// compare if the parsed expressions are equal
			return input1normalized.equals(input2normalized);
		} catch (Throwable th) {
		}
		
		return false;
	}
	
	/**
	 * @return CAS timeout in seconds
	 */
	public static int getTimeout() {
		return GeoGebraCAS.timeout;
	}
	
	/**
	 * @param timeout Timeout in seconds
	 */
	public static void setTimeout(int timeout) {
		GeoGebraCAS.timeout = timeout;
	}
	
}