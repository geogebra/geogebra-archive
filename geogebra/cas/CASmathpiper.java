package geogebra.cas;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;
import geogebra.main.MyResourceBundle;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.mathpiper.interpreters.EvaluationResponse;
import org.mathpiper.interpreters.Interpreter;

public class CASmathpiper extends CASgeneric {
	
	final public String RB_GGB_TO_MathPiper = "/geogebra/cas/ggb2mathpiper";

	private Interpreter ggbMathPiper;
	private EvaluationResponse response;
	private ResourceBundle ggb2MathPiper;
	
	public CASmathpiper(CASparser casParser) {
		super(casParser);
		getMathPiper();
	}
	
	private synchronized Interpreter getMathPiper() {				
		if (ggbMathPiper == null) {
			ggbMathPiper = org.mathpiper.interpreters.Interpreters.newSynchronousInterpreter();
			initMyMathPiperFunctions();
			evaluateMathPiper("Factor(42)"); // this solves ticket #320
			Application.setCASVersionString("MathPiper "+org.mathpiper.Version.version); 
		}
		
		return ggbMathPiper;
	}	
	
	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 */
	public void reset() {
		ggbMathPiper = null;
		getMathPiper();
	}
	
	/**
	 * Returns whether var is a defined variable in MathPiper.
	 */
	public boolean isVariableBound(String var) {
		StringBuilder exp = new StringBuilder("IsBound(");
		exp.append(var);
		exp.append(')');
		return "True".equals(evaluateMathPiper(exp.toString()));
	}
	
	/**
	 * Unbinds (deletes) var in MathPiper.
	 * @param var
	 * @param isFunction
	 */
	public void unbindVariable(String var) {
		StringBuilder sb = new StringBuilder();
		
		// clear function variable, e.g. Retract("f", *)
		sb.append("[Retract(\"");
		sb.append(var);
		sb.append("\", *);");	

		// clear variable, e.g. Unbind(f)
		sb.append("Unbind(");
		sb.append(var);
		sb.append(");]");
		
		evaluateMathPiper(sb.toString());
	}
	
	/**
	 * Evaluates a valid expression in GeoGebraCAS syntax and returns the resulting String in GeoGebra notation.
	 * @param casInput: in GeoGebraCAS syntax
	 * @param useGeoGebraVariables: whether GeoGebra objects should be substituted before evaluation
	 * @return evaluation result
	 * @throws Throwable
	 */
	public synchronized String evaluateGeoGebraCAS(ValidExpression casInput, boolean useGeoGebraVariables) throws Throwable {
		// convert parsed input to MathPiper string
		String MathPiperString = toMathPiperString(casInput, useGeoGebraVariables);
			
		// EVALUATE input in MathPiper 
		String result = evaluateMathPiper(MathPiperString);

		// convert MathPiper result back into GeoGebra syntax
		String ggbString = toGeoGebraString(result);
		
		// TODO: remove
		System.out.println("eval with MathPiper: " + MathPiperString);
		System.out.println("   result: " + result);
		System.out.println("   ggbString: " + ggbString);
		
		return ggbString;
	}
	
	/**
	 * Tries to parse a given MathPiper string and returns a String in GeoGebra syntax.
	 */
	public synchronized String toGeoGebraString(String MathPiperString) throws Throwable {
		ValidExpression ve = casParser.parseMathPiper(MathPiperString);
		return casParser.toGeoGebraString(ve);
	}
	
    /**
	 * Evaluates a MathPiper expression and returns the result as a string in MathPiper syntax, 
	 * e.g. evaluateMathPiper("D(x) (x^2)") returns "2*x".
	 * 
	 * @return result string (null possible)
	 */
	final synchronized public String evaluateMathPiper(String exp) {
		try {
			String result;

			// MathPiper has problems with indices like a_3, b_{12}
			exp = casParser.replaceIndices(exp);
//			
//			final boolean debug = true;
//			if (debug) Application.debug("Expression for mathPiper: "+exp);
			
			// evaluate the MathPiper expression
			final Interpreter mathpiper = getMathPiper();
			
			
			 EvaluationResponse response;


			 // timeout needed for eg Limit((Sin(1/x)*x^2-x*Cos(1/x))/x^2,-Infinity)
		       final Timer timer = new Timer();

		       timer.schedule(new TimerTask() {
		           public void run() {
		        	   mathpiper.haltEvaluation();
		               timer.cancel();
		           }

		       }, GeoGebraCAS.CAS_TIMEOUT * 1000); //Time out after three seconds.
		       
		       response = mathpiper.evaluate(exp);
		       timer.cancel();
		    	          
			
			if (response.isExceptionThrown())
			{
				System.err.println("evaluateMathPiper: "+exp+"\n  Exception: "+response.getExceptionMessage());
				return "?";
			}
			result = response.getResult();
			
			//if (debug) System.out.println("Result: "+result);
					
			// undo special character handling
			result = casParser.insertSpecialChars(result);
			
			// convert MathPiper's scientific notation from e.g. 3.24e-4 to 3.2E-4
			result = CASparser.convertScientificFloatNotation(result, 'e');

			return result;
		} catch (Throwable th) {
			//MathPiper.Evaluate("restart;");
			th.printStackTrace();
			return null;
		} 
	}
	
	final synchronized public String getEvaluateGeoGebraCASerror() {
		if (response != null)
			return response.getExceptionMessage();
		else 
			return null;
	}
	
	/**
	 * Returns the MathPiper command for the given key (from ggb2MathPiper.properties)
	 */ 
	public synchronized String getTranslatedCASCommand(String key) {
		if (ggb2MathPiper == null) {
			ggb2MathPiper = MyResourceBundle.loadSingleBundleFile(RB_GGB_TO_MathPiper);
		}
		
		String ret;
		try {
			ret =  ggb2MathPiper.getString(key);
		} catch (MissingResourceException e) {
			ret = null;
		}

		return ret;
	}
	
	/**
	 * Evaluates the given ExpressionValue and returns the result in MathPiper syntax.
	 * 
	 * @param resolveVariables: states whether variables from the GeoGebra kernel 
	 *    should be used. Note that this changes the given ExpressionValue. 
	 */
	public synchronized String toMathPiperString(ValidExpression ve, boolean resolveVariables) {
		
		// resolve global variables
//		if (resolveVariables) {			
//			casParser.resolveVariablesForCAS(ve);
//		}	
		
		// convert to MathPiper String
		String MathPiperStr = doToMathPiperString(ve, resolveVariables);

		// handle assignments
		String veLabel = ve.getLabel();
		boolean assignment = veLabel != null;
		if (assignment) {
			StringBuilder sb = new StringBuilder();
			
			if (ve instanceof FunctionNVar) {
				// function, e.g. f(x) := 2*x
				FunctionNVar fun = (FunctionNVar) ve;
				sb.append(veLabel);
				sb.append("(" );
				sb.append(fun.getVarString());
				sb.append(") := ");
				sb.append(MathPiperStr);
				MathPiperStr = sb.toString();
			} else {	
				// assignment, e.g. a := 5
				MathPiperStr = veLabel + " := " + MathPiperStr;
			}
		}
		
		//System.out.println("GeoGebraCAS.toMathPiperString: " + MathPiperStr);
		return MathPiperStr;
	}
	
	
	/**
	 * Returns the given expression as a string in MathPiper syntax.
	 */
	private String doToMathPiperString(ExpressionValue ev, boolean substituteVariables) {
		String MathPiperString;
		if (!ev.isExpressionNode()) {
			ev = new ExpressionNode(casParser.getKernel(), ev);			
		}
		
		MathPiperString = ((ExpressionNode) ev).getCASstring(ExpressionNode.STRING_TYPE_MATH_PIPER, !substituteVariables);			
		return MathPiperString;
	}

	/**
	 * Initialize special commands needed in our ggbMathPiper instance,e.g.
	 * getPolynomialCoeffs(exp,x).
	 */
	private synchronized boolean initMyMathPiperFunctions() {		
// Expand expression and get polynomial coefficients using MathPiper:
//		getPolynomialCoeffs(expr,x) :=
//			       If( CanBeUni(expr),
//			           [
//							Coef(MakeUni(expr,x),x, 0 .. Degree(expr,x));			           ],
//			           {};
//			      );
		String strGetPolynomialCoeffs = "getPolynomialCoeffs(expr,x) := If( CanBeUni(expr),[ Coef(MakeUni(expr,x),x, 0 .. Degree(expr,x));],{});";
		EvaluationResponse resp = ggbMathPiper.evaluate(strGetPolynomialCoeffs);
		if (resp.isExceptionThrown()) {
			return false;
		}
		
		// define constant for Degree
		response = ggbMathPiper.evaluate("Degree := 180/Pi;");
		
		// set default numeric precision to 16 significant figures
		ggbMathPiper.evaluate("BuiltinPrecisionSet(16);");
		
		// user defined function
		ggbMathPiper.evaluate("log10(x) := Ln(x) / Ln(10);");
		ggbMathPiper.evaluate("log2(x) := Ln(x) / Ln(2);");
		ggbMathPiper.evaluate("logB(b, x) := Ln(x) / Ln(b);");
		ggbMathPiper.evaluate("cbrt(x) := x^(1/3);");
		
		// Rules for equation manipulation
		// allow certain commands for equations
		ggbMathPiper.evaluate("NotEqu(exp) := Not( IsEquation(exp));");
		
		//ggbMathPiper.evaluate("KeepInput( (x_NotEqu == y_NotEqu) + z_NotEqu ) <-- Subst(a, x) Subst(b, y) Subst(c, z) (Hold(a + c) ==  Hold(b + c)) ;");
		
		
		// standard commands for equations
		ggbMathPiper.evaluate("Simplify(x_NotEqu == y_NotEqu)  <-- Simplify(x) == Simplify(y);");
		ggbMathPiper.evaluate("Factor(x_NotEqu == y_NotEqu)  <-- Factor(x) == Factor(y);");
		ggbMathPiper.evaluate("Expand(x_NotEqu == y_NotEqu)  <-- Expand(x) == Expand(y);");
		ggbMathPiper.evaluate("ExpandBrackets(x_NotEqu == y_NotEqu)  <-- ExpandBrackets(x) == ExpandBrackets(y);");
		ggbMathPiper.evaluate("Sqrt(x_NotEqu == y_NotEqu)  <-- Sqrt(x) == Sqrt(y);");
		ggbMathPiper.evaluate("Exp(x_NotEqu == y_NotEqu)  <-- Exp(x) == Exp(y);");
		ggbMathPiper.evaluate("Ln(x_NotEqu == y_NotEqu)  <-- Ln(x) == Ln(y);");
		
		// arithmetic for equations and scalars
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) + z_NotEqu <-- x + z == y + z;");
		ggbMathPiper.evaluate("z_NotEqu + (x_NotEqu == y_NotEqu) <-- z + x == z + y;");
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) - z_NotEqu <-- x - z == y - z;");
		ggbMathPiper.evaluate("z_NotEqu - (x_NotEqu == y_NotEqu) <-- z - x == z - y;");
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) * z_NotEqu <-- x * z == y * z;");
		ggbMathPiper.evaluate("z_NotEqu * (x_NotEqu == y_NotEqu) <-- z * x == z * y;");
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) / z_NotEqu <-- x / z == y / z;");
		ggbMathPiper.evaluate("z_NotEqu / (x_NotEqu == y_NotEqu) <-- z / x == z / y;");
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) ^ z_NotEqu <-- x ^ z == y ^ z;");
		ggbMathPiper.evaluate("z_NotEqu ^ (x_NotEqu == y_NotEqu) <-- z ^ x == z ^ y;");
		
		// arithmetic for two equations
		ggbMathPiper.evaluate("(a_NotEqu == b_NotEqu) + (c_NotEqu == d_NotEqu) <-- a + c == b + d;");
		ggbMathPiper.evaluate("(a_NotEqu == b_NotEqu) - (c_NotEqu == d_NotEqu) <-- a - c == b - d;");
		ggbMathPiper.evaluate("(a_NotEqu == b_NotEqu) * (c_NotEqu == d_NotEqu) <-- a * c == b * d;");
		ggbMathPiper.evaluate("(a_NotEqu == b_NotEqu) / (c_NotEqu == d_NotEqu) <-- a / c == b / d;");
		
		
		//ggbMathPiper.evaluate("KeepInput(x_IsAtom)  <-- Simplify(x) == Simplify(y);");
		
		
		return true;
	}
}
