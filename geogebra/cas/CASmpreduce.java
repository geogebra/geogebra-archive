package geogebra.cas;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
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
	 * @param casInput: in GeoGebraCAS syntax
	 * @return evaluation result
	 * @throws Throwable
	 */
	public synchronized String evaluateGeoGebraCAS(ValidExpression casInput) throws Throwable {
		// convert parsed input to MathPiper string
		String exp = toMPReduceString(casInput);
		
		//MPReduce supports UNICODE and case sensitivity by adding a ! in front of a letter
		StringBuilder strBuilder=new StringBuilder();
		int strLength=exp.length();
		for (int i=0; i< strLength; i++){
			char character=exp.charAt(i);
			
			//lowercase letters don't need a !
			if (Character.isLetter(character) && (((int) character)<97 || ((int) character)>122)){
				//the character is not a lowercase ascii character
				strBuilder.append("!");
				strBuilder.append(character);
			} else {
				strBuilder.append(character);
			}
		}
		exp=strBuilder.toString();
		
		String result = evaluateMPReduce(exp);
		
		//removing the !
		strBuilder=new StringBuilder();
		strLength=result.length();
		for (int i=0; i< strLength; i++){
			char character=result.charAt(i);
			if (character=='!')
				if (i+1<strLength){
					char nextChar=result.charAt(i+1);
					if (Character.isLetter(nextChar) && (((int) nextChar)<97 || ((int) nextChar)>122)){
						i++;
						character=nextChar;
					}
				}
			strBuilder.append(character);
		}
		
		result=strBuilder.toString();
		
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
			ret = ret.trim();
			ret=ret.replaceAll("\\*\\*", "^");
	        while (ret.endsWith("$")) {
	        	ret = ret.substring(0, ret.length() - 1);
	        }
			// undo special character handling
			ret = casParser.insertSpecialChars(ret);
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
		// TODO Auto-generated method stub
	}
	
	private synchronized void initMyMPReduceFunctions() {
		try {
			mpreduce.evaluate("off nat;");
			
			// ARBVARS introduces arbitrary new variables when solving singular systems of equations
			mpreduce.evaluate("off arbvars;");
			
			// make sure x*(x+1) isn't returned factored
			mpreduce.evaluate("off pri;");
			
			mpreduce.evaluate("load\\_package(\"rsolve\");");
			mpreduce.evaluate("load\\_package(\"numeric\");");
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
