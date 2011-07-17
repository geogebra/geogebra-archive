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

	private final static String RB_GGB_TO_MPReduce = "/geogebra/cas/ggb2mpreduce";
	private Interpreter2 mpreduce;
	
	// We escape any upper-letter words so Reduce doesn't switch them to lower-letter,
	// however the following function-names should not be escaped
	// (note: all functions here must be in lowercase!)
	final private Set<String> predefinedFunctions = ExpressionNodeConstants.RESERVED_FUNCTION_NAMES;

	public CASmpreduce(CASparser casParser) {
		super(casParser, RB_GGB_TO_MPReduce);
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
		String exp = translateToCAS(casInput, ExpressionNode.STRING_TYPE_MPREDUCE);
		
		exp="<<off rounded, complex$ "+exp+">>";
		
		String result = evaluateMPReduce(exp);
		
		// convert result back into GeoGebra syntax
		String ggbString = toGeoGebraString(result);
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
			String ret = evaluateRaw(exp);
			ret = casParser.insertSpecialChars(ret); // undo special character handling
			return ret;
		} catch (Throwable e) {
			e.printStackTrace();
			return "?";
		}
	}
	
	
	public String translateFunctionDeclaration(String label, String parameters, String body)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" procedure ");
		sb.append(label);
		sb.append("(");
		sb.append(parameters);
		sb.append("); begin return ");
		sb.append(body);
		sb.append(" end; ");
		
		return sb.toString();
	}

	@Override
	public String evaluateRaw(String exp) throws Throwable {
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

		String result = mpreduce.evaluate(exp);
		
		sb.setLength(0);
		for (String s : result.split("\n")) {
			s = s.trim();
			if (s.length() == 0)
				continue;
			else if (s.startsWith("***")) { // MPReduce comment
				Application.debug("MPReduce comment: " + s);
				continue;
			}
			else {
				// look for any trailing $
				int len = s.length();
				while (len > 0 && s.charAt(len - 1) == '$')
					--len;
				
				// remove the !
				for (int i = 0; i < len; ++i) {
					char character = s.charAt(i);
					if (character=='!') {
						if (i+1 < len) {
							char nextChar=s.charAt(i+1);
							if (Character.isLetter(nextChar) && (((int) nextChar)<97 || ((int) nextChar)>122)){
								i++;
								character=nextChar;
							}
						}
					}
					sb.append(character);
				}	
			}
		}

		result = sb.toString();
		result = result.replaceAll("\\*\\*", "^");
		
		// TODO: remove
		System.out.println("eval with MPReduce: " + exp);
		System.out.println("   result: " + result);
		return result;
	}

	@Override
	public String getEvaluateGeoGebraCASerror() {
		// TODO Auto-generated method stub
		return null;
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
			// mpreduce.evaluate("operator log!-temp");
			// mpreduce.evaluate("sub(log!-temp = log, ( int(1/x,x) where {log(~xx) => abs(log!-temp(xx))}))");

			// access functions for elements of a vector 
			mpreduce.evaluate("procedure x(a); if not numberp(a) and part(a, 0) = list then first(a) else x*a;");
			mpreduce.evaluate("procedure y(a); if not numberp(a) and part(a, 0) = list then second(a) else x*a;");
			mpreduce.evaluate("procedure z(a); if not numberp(a) and part(a, 0) = list then third(a) else x*a;");
		
			
			mpreduce.evaluate(" Degree := pi/180;");
			
			// erf in Reduce is currently broken: http://sourceforge.net/projects/reduce-algebra/forums/forum/899364/topic/4546339
			// this is a numeric approximation according to Abramowitz & Stegun 7.1.26.
			mpreduce.evaluate("procedure dot(vec1,vec2); " +
					"	begin scalar tmplength; " +
					"  if not numberp(vec1) and part(vec1,0)=mat and column_dim(vec1)=1 then " +
					"    vec1:=tp(vec1);" +
					"  if not numberp(vec2) and part(vec2,0)=mat and column_dim(vec2)=1 then " +
					"    vec2:=tp(vec2); " +
					"  return  " +
					"  if not numberp(vec1) and part(vec1,0)=list then << " +
					"    if not numberp(vec2) and part(vec2,0)=list then  " +
					"      <<tmplength:=length(vec1);  " +
					"      for i:=1:tmplength  " +
					"	sum part(vec1,i)*part(vec2,i) >> " +
					"    else if not numberp(vec2) and part(vec2,0)=mat and row_dim(vec2)=1 then" +
					"      <<tmplength:=length(vec1);  " +
					"      for i:=1:tmplength  " +
					"	sum part(vec1,i)*vec2(1,i)>> " +
					"      else " +
					"	? " +
					"  >> " +
					"  else <<if not numberp(vec1) and part(vec1,0)=mat and row_dim(vec1)=1 then << " +
					"    if not numberp(vec2) and part(vec2,0)=list then  " +
					"      <<tmplength:=length(vec2); " +
					"      for i:=1:tmplength  " +
					"	sum vec1(1,i)*part(vec2,i)>> " +
					"    else if not numberp(vec2) and part(vec2,0)=mat and row_dim(vec2)=1 then" +
					"      <<tmplength:=column_dim(vec1);  " +
					"      for i:=1:tmplength  " +
					"	sum vec1(1,i)*vec2(1,i) " +
					"      >> " +
					"      else " +
					"		? " +
					"    >> " +
					"  else " +
					"    ? " +
					"  >> " +
					"end;");
			mpreduce.evaluate(
				"procedure erf(x); " + 
				"begin " +
				"     on rounded;" +
				"     a1!° :=  0.254829592; "+
				"     a2!° := -0.284496736; "+
				"     a3!° :=  1.421413741; "+
				"     a4!° := -1.453152027; "+
				"     a5!° :=  1.061405429; "+
				"     p!°  :=  0.3275911; "+
				"     sign!° := 1; "+
				"     if x < 0 then sign!° := -1; "+
				"     x!° := Abs(x); "+
				"     t!° := 1.0/(1.0 + p!°*x!°); "+
				"     y!° := 1.0 - (((((a5!°*t!° + a4!°)*t!°) + a3!°)*t!° + a2!°)*t!° + a1!°)*t!°*Exp(-x!°*x!°); "+
				"     return sign!°*y!° "+
				"end;");

			mpreduce.evaluate("procedure harmonic(n,m); for i:=1:n sum 1/(i**m);");
			mpreduce.evaluate("procedure uigamma(n,m); gamma(n)-igamma(n,m);");
			mpreduce.evaluate("procedure arg(z); atan2(repart(z),impart(z));");
			mpreduce.evaluate("procedure listtocolumnvector(list); "
					+ "begin scalar lengthoflist; "
					+ "lengthoflist:=length(list); "
					+ "matrix m!°(lengthoflist,1); "
					+ "for i:=1:lengthoflist do " 
					+ "m!°(i,1):=part(list,i); "
					+ "return m!° " 
					+ "end;");

			mpreduce.evaluate("procedure listtorowvector(list); "
					+ "begin scalar lengthoflist; "
					+ "lengthoflist:=length(list); "
					+ "matrix m!°(1,lengthoflist); "
					+ "for i:=1:lengthoflist do "
					+ "m!°(1,i):=part(list,i); "
					+ "return m!° " 
					+ "end;");
			
			mpreduce.evaluate("load_package rsolve;");
			mpreduce.evaluate("load_package numeric;");
			mpreduce.evaluate("load_package specfn;");
			mpreduce.evaluate("load_package odesolve;");
			mpreduce.evaluate("load_package defint;");
			mpreduce.evaluate("load_package linalg;");
			mpreduce.evaluate("load_package boolean;");
			
			// the first command sent to mpreduce produces an error
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
		else {
			StringBuilder sb = new StringBuilder();
			sb.append("MPReduce ");
			sb.append(m.group(1));
			return sb.toString();
		}
	}
}
