package geogebra.cas;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.qtitools.mathassess.tools.maximaconnector.MaximaTimeoutException;
import org.qtitools.mathassess.tools.maximaconnector.RawMaximaSession;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;
import geogebra.main.MyResourceBundle;

public class CASmaxima extends CASgeneric {
	
	final public String RB_GGB_TO_Maxima = "/geogebra/cas/ggb2maxima";
	
	private RawMaximaSession ggbMaxima;
	private ResourceBundle ggb2Maxima;
	
	public CASmaxima(CASparser casParser) {
		super(casParser);
	}
	
	/**
	 * Returns whether var is a defined variable in MathPiper.
	 */
	public boolean isVariableBound(String var) {
		// TODO: implement for Maxima
		
//		StringBuilder exp = new StringBuilder("IsBound(");
//		exp.append(var);
//		exp.append(')');
//		return "True".equals(evaluateMathPiper(exp.toString()));
		return false;
	}
	
	/**
	 * Unbinds (deletes) var in MathPiper.
	 * @param var
	 * @param isFunction
	 */
	public void unbindVariable(String var) {
		// TODO: implement for Maxima
		
//		StringBuilder sb = new StringBuilder();
//		
//		// clear function variable, e.g. Retract("f", *)
//		sb.append("[Retract(\"");
//		sb.append(var);
//		sb.append("\", *);");	
//
//		// clear variable, e.g. Unbind(f)
//		sb.append("Unbind(");
//		sb.append(var);
//		sb.append(");]");
//		
//		evaluateMathPiper(sb.toString());
	}

	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra notation.
	 * @param casInput: in GeoGebraCAS syntax
	 * @param useGeoGebraVariables: whether GeoGebra objects should be substituted before evaluation
	 * @return evaluation result
	 * @throws Throwable
	 */
	public String evaluateGeoGebraCAS(ValidExpression casInput, boolean useGeoGebraVariables) throws Throwable {
		// convert parsed input to Maxima string
		String MaximaString = toMaximaString(casInput, useGeoGebraVariables);
		
		if (!MaximaString.endsWith(";")) MaximaString = MaximaString + ';';
			
		// EVALUATE input in Maxima 
		String result = evaluateMaxima(MaximaString);

		// convert Maxima result back into GeoGebra syntax
		String ggbString = toGeoGebraString(result);
		
		// TODO: remove
		System.out.println("eval with Maxima: " + MaximaString);
		System.out.println("   result: " + result);
		System.out.println("   ggbString: " + ggbString);
		
		return ggbString;
	}
	
	final synchronized public String getEvaluateGeoGebraCASerror() {
		// TODO: implement for Maxima
		return null;
		
//		if (response != null)
//			return response.getExceptionMessage();
//		else 
//			return null;
	}
	
	/**
	 * Tries to parse a given Maxima string and returns a String in GeoGebra syntax.
	 */
	public synchronized String toGeoGebraString(String maximaString) throws Throwable {
		ValidExpression ve = casParser.parseMaxima(maximaString);
		return casParser.toGeoGebraString(ve);
	}
	
	/**
	 * Evaluates the given ExpressionValue and returns the result in MathPiper syntax.
	 * 
	 * @param resolveVariables: states whether variables from the GeoGebra kernel 
	 *    should be used. Note that this changes the given ExpressionValue. 
	 */
	public synchronized String toMaximaString(ValidExpression ve, boolean resolveVariables) {
		
		// resolve global variables
		if (resolveVariables) {			
			casParser.resolveVariablesForCAS(ve);
		}	
		
		// convert to Maxima String
		String MaximaStr = doToMaximaString(ve, resolveVariables);

		// handle assignments
		String veLabel = ve.getLabel();
		if (veLabel != null) {
			StringBuilder sb = new StringBuilder();
			
			if (ve instanceof Function) {
				// function, e.g. f(x) := 2*x
				Function fun = (Function) ve;
				sb.append(veLabel);
				sb.append("(" );
				sb.append(fun.getFunctionVariable());
				sb.append(") := ");
				
				// evaluate right hand side:
				// import for e.g. g(x) := Eval(D(x) x^2)
				//sb.append("Eval(");
				sb.append(MaximaStr);
				//sb.append(")");
				MaximaStr = sb.toString();
			} else {	
				// assignment, e.g. a := 5
				MaximaStr = veLabel + " := " + MaximaStr;
			}
		}
		
		// TODO: remove
		System.out.println("CASmaxima.toMaxima: " + MaximaStr);
		return MaximaStr;
	}	
	
	/**
	 * Returns the given expression as a string in Maxima syntax.
	 */
	private String doToMaximaString(ExpressionValue ev, boolean substituteVariables) {
		String MathPiperString;
		
		if (!ev.isExpressionNode()) {
			ev = new ExpressionNode(casParser.getKernel(), ev);			
		}
		
		MathPiperString = ((ExpressionNode) ev).getCASstring(ExpressionNode.STRING_TYPE_MAXIMA, !substituteVariables);		
				
		return MathPiperString;
	}

	
    /**
	 * Evaluates a Maxima expression and returns the result as a string in Maxima syntax, 
	 * e.g. evaluateMaxima("integrate (sin(x)^3, x);") returns "cos(x)^3/3-cos(x)".
	 * 
	 * @return result string (null possible)
	 */
	final synchronized public String evaluateMaxima(String exp) {
		try {
			String result;
						
			// MathPiper has problems with indices like a_3, b_{12}
			exp = casParser.replaceIndices(exp);
			
			final boolean debug = true;
			if (debug) Application.debug("Expression for Maxima: "+exp);
			
			// evaluate the MathPiper expression
			RawMaximaSession maxima = getMaxima();
			
			//result = maxima.executeExpectingSingleOutput(exp);
			//String results[] = maxima.executeExpectingMultipleLabels(exp);			
			//result = results[results.length - 1];
			
			String results[] = maxima.executeRaw(exp).split("\n");
			
			result = results[results.length - 1];
			
			// if last line is empty, look for next non-empty previous line
			if (result.trim().length() == 0 && results.length > 1) {
				int i = results.length - 2;
				while (results[i].trim().length() == 0 && i > 0) i--;
				result = results[i];
			}
			
			// remove (%o1) at start
			result = result.replaceFirst("\\(%o\\d+\\)\\s*", "").trim();
			
			
			if (debug) {
				for (int i = 0 ; i < results.length ; i++)
					System.err.println("Result "+i+": "+results[i]);
				System.out.println("result: "+result);
			}
				
			// undo special character handling
			result = casParser.insertSpecialChars(result);

			return result;
		} catch (Throwable th) {
			//MathPiper.Evaluate("restart;");
			th.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * Returns the Maxima command for the given key (from ggb2Maxima.properties)
	 */ 
	public synchronized String getTranslatedCASCommand(String key) {
		if (ggb2Maxima == null) {
			ggb2Maxima = MyResourceBundle.loadSingleBundleFile(RB_GGB_TO_Maxima);
		}
		
		String ret;
		try {
			ret =  ggb2Maxima.getString(key);
		} catch (MissingResourceException e) {
			ret = null;
		}

		return ret;
	}
	
	
	private synchronized RawMaximaSession getMaxima() {
		if (ggbMaxima == null) {
		    //PropertiesMaximaConfiguration configuration = new PropertiesMaximaConfiguration();
		    ggbMaxima = new RawMaximaSession();
		}
		
		if (!ggbMaxima.isOpen()) {
		    try {
				ggbMaxima.open();
				
				initMyMaximaFunctions();
				
			} catch (MaximaTimeoutException e) {
				Application.debug("Timeout from Maxima");
				return null;
			}
			
		}
	
		return ggbMaxima;
	}
	
	private void initMyMaximaFunctions() throws MaximaTimeoutException {
	
		// make sure results are returned
	    ggbMaxima.executeRaw("display2d:false;");
	    
	    // make sure integral(1/x) = log(abs(x))
	    ggbMaxima.executeRaw("logabs:true;");
	    
	    // define custom functions
	    ggbMaxima.executeRaw("log10(x) := log(x) / log(10);");
	    ggbMaxima.executeRaw("log2(x) := log(x) / log(2);");
	    ggbMaxima.executeRaw("cbrt(x) := x^(1/3);");
	    
	    // define Degree
	    //ggbMaxima.executeRaw("Degree := 180 / %pi;");
	    
	    // TODO: define getPolynomialCoeffs(expr,x) for Maxima
	    // Expand expression and get polynomial coefficients using MathPiper:
	    //		getPolynomialCoeffs(expr,x) :=
	    //			       If( CanBeUni(expr),
	    //			           [
	    //							Coef(MakeUni(expr,x),x, 0 .. Degree(expr,x));			           ],
	    //			           {};
	    //			      );
	    //	String strGetPolynomialCoeffs = "getPolynomialCoeffs(expr,x) := If( CanBeUni(expr),[ Coef(MakeUni(expr,x),x, 0 .. Degree(expr,x));],{});";

	}

}
