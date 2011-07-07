package geogebra.cas;

import java.util.Arrays;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import geogebra.cas.jacomax.MaximaTimeoutException;
import geogebra.kernel.arithmetic.*;
import geogebra.main.Application;
import geogebra.main.MyResourceBundle;

import org.mathpiper.mpreduce.*;

public class CASmpreduce extends CASgeneric {

	final public String RB_GGB_TO_MPReduce = "/geogebra/cas/ggb2mpreduce";
	private ResourceBundle ggb2MPReduce;
	private Interpreter2 mpreduce;
	
	// We escape any upper-letter words so Reduce doesn't switch them to lower-letter,
	// however the following function-names should not be escaped
	// (note: all functions here must be in lowercase!)
	final private Set<String> predefinedFunctions = ExpressionNodeConstants.RESERVED_FUNCTION_NAMES;

	public CASmpreduce(CASparser casParser) {
		super(casParser);
		getInterpreter();
	}
	
	private synchronized Interpreter2 getInterpreter() {				
		if (mpreduce == null) {
			mpreduce = new Interpreter2();
			initMyMPReduceFunctions();
			Application.setCASVersionString(getVersionString());
		}
		return mpreduce;
	}
	
	/**
	 * Evaluates a valid expression in GeoGebraCAS syntax and returns the resulting String in GeoGebra notation.
	 * @param casInput in GeoGebraCAS syntax
	 * @return evaluation result
	 * @throws Throwable
	 */
	public synchronized String evaluateGeoGebraCAS(ValidExpression casInput) throws Throwable {
		// convert parsed input to MathPiper string
		String exp = toMPReduceString(casInput);
		
		// we need to escape any upper case letters and non-ascii codepoints with '!'
		StringTokenizer tokenizer = new StringTokenizer(exp, "(),;[] ", true);
		StringBuilder sb = new StringBuilder();
		while (tokenizer.hasMoreElements())
		{
			String t = tokenizer.nextToken();
			if (predefinedFunctions.contains(t.toLowerCase()))
				sb.append(t);
			else
			{
				for (int i = 0; i < t.length(); ++i)
				{
					char c = t.charAt(i);
					if (Character.isLetter(c) && (((int) c) < 97 || ((int) c) > 122)) {
						sb.append('!');
						sb.append(c);
					} else
						sb.append(c);
				}
			}
		}
		exp = sb.toString();

		String result = evaluateMPReduce(exp);
		
		//removing the !
		sb = new StringBuilder();
		for (int i=0; i< result.length(); i++){
			char character=result.charAt(i);
			if (character=='!')
				if (i+1 < result.length()){
					char nextChar=result.charAt(i+1);
					if (Character.isLetter(nextChar) && (((int) nextChar)<97 || ((int) nextChar)>122)){
						i++;
						character=nextChar;
					}
				}
			sb.append(character);
		}
		result = sb.toString();
		
		// convert result back into GeoGebra syntax
		String ggbString = toGeoGebraString(result);
		
		// TODO: remove
//		System.out.println("eval with MPReduce: " + exp);
//		System.out.println("   result: " + result);
//		System.out.println("   ggbString: " + ggbString);
		
		return ggbString;
	}
	
	/**
	 * Tries to parse a given MPReduce string and returns a String in GeoGebra syntax.
	 */
	public synchronized String toGeoGebraString(String mpreduceString) throws Throwable {
		// since casParserparse<CAS>() is basically the same for all CAS anyway, we use the MathPiper one
		ValidExpression ve = casParser.parseMathPiper(mpreduceString);
		return casParser.toGeoGebraString(ve);
	}
	
    /**
	 * Evaluates a MathPiper expression and returns the result as a string in MathPiper syntax, 
	 * e.g. evaluateMathPiper("D(x) (x^2)") returns "2*x".
	 * 
	 * @return result string (null possible)
	 */
	public final String evaluateMPReduce(String exp) {
        try {
        	exp=casParser.replaceIndices(exp);
			String ret = mpreduce.evaluate(exp);
			
			StringBuilder sb = new StringBuilder();
			for (String s : ret.split("\n")) {
				s = s.trim();
				if (s.length() == 0)
					continue;
				else if (s.startsWith("***")) { // MPReduce comment
					Application.debug("MPReduce comment: " + s);
					continue;
				}
				else {
					// remove trailing $
					int i = s.length() - 1;
					while (i > 0 && s.charAt(i) == '$')
						--i;
					sb.append(s.substring(0, i+1));
				}
			}
			ret = sb.toString();
			ret = ret.replaceAll("\\*\\*", "^");
			ret = casParser.insertSpecialChars(ret); // undo special character handling
			return ret;
		} catch (Throwable e) {
			e.printStackTrace();
			return "?";
		}
	}

	/**
	 * Evaluates the given ExpressionValue and returns the result in MPReduce syntax.
	 */
	public synchronized String toMPReduceString(ValidExpression ve) {

		String str = doToMPReduceString(ve);
		
		// handle assignments
		String veLabel = ve.getLabel();
		if (veLabel != null) {
			StringBuilder sb = new StringBuilder();
			
			if (ve instanceof FunctionNVar) {
				// function, e.g. f(x) := 2*x
				FunctionNVar fun = (FunctionNVar) ve;
				sb.append(veLabel);
				sb.append("(" );
				sb.append(fun.getVarString());
				sb.append(") := ");
				sb.append(str);
				str = sb.toString();
			} else {	
				// assignment, e.g. a : 5
				str = veLabel + " := " + str;
			}
		}
		return str;
	}	
	
	private String doToMPReduceString(ExpressionValue ev) {
		if (!ev.isExpressionNode()) {
			ev = new ExpressionNode(casParser.getKernel(), ev);			
		}
		return ((ExpressionNode) ev).getCASstring(ExpressionNode.STRING_TYPE_MPREDUCE, true);			
	}

	@Override
	public String evaluateRaw(String exp) throws Throwable {
		return evaluateGeoGebraCAS(exp);
	}

	@Override
	public String getEvaluateGeoGebraCASerror() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the MPReduce command for the given key (from ggb2mpreduce.properties)
	 */ 
	public synchronized String getTranslatedCASCommand(String key) {
		if (ggb2MPReduce == null) {
			ggb2MPReduce = MyResourceBundle.loadSingleBundleFile(RB_GGB_TO_MPReduce);
		}
		
		String ret;
		try {
			ret =  ggb2MPReduce.getString(key);
		} catch (MissingResourceException e) {
			ret = null;
		}

		return ret;
	}


	@Override
	public boolean isVariableBound(String var) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// just recreate the interpreter.
		// there might be a better way to do this using the RESET package for reduce
		mpreduce = null;
		getInterpreter();
	}

	@Override
	public void unbindVariable(String var) {
		try {
			mpreduce.evaluate("clear(" + var + ");");
		} catch (Throwable e) {
			System.err.println("Failed to clear variable from MPReduce: " + var);
		}
	}
	
	private synchronized void initMyMPReduceFunctions() {
		try {
			mpreduce.evaluate("off nat;");
			
			// ARBVARS introduces arbitrary new variables when solving singular systems of equations
			mpreduce.evaluate("off arbvars;");
			
			// make sure x*(x+1) isn't returned factored
			mpreduce.evaluate("off pri;");
			
			mpreduce.evaluate("off rounded;");
			
			// make sure integral(1/x) gives ln(abs(x)) [TODO: NOT WORKING]
			//mpreduce.evaluate("operator log!-temp");
			//mpreduce.evaluate("sub(log!-temp = log, ( int(1/x,x) where {log(~xx) => abs(log!-temp(xx))}))");
			
			mpreduce.evaluate("load_package(\"rsolve\");");
			mpreduce.evaluate("load_package(\"numeric\");");
			mpreduce.evaluate("load_package defint;");
			
			//the first command sent to mpreduce produces an error
			evaluateGeoGebraCAS("1+2");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private String getVersionString() {
	
		Pattern p = Pattern.compile("version (\\S+)");
		Matcher m = p.matcher(mpreduce.getStartMessage());
		if (!m.find())
			return "MPReduce";
		else
		{
			StringBuilder sb = new StringBuilder();
			sb.append("MPReduce ");
			sb.append(m.group(1));
			return sb.toString();
		}
	}
}
