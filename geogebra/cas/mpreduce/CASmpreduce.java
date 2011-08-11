package geogebra.cas.mpreduce;

import geogebra.cas.CASgeneric;
import geogebra.cas.CASparser;
import geogebra.cas.CasParserTools;
import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mathpiper.mpreduce.Interpreter2;

public class CASmpreduce extends CASgeneric {
	
	private final static String RB_GGB_TO_MPReduce = "/geogebra/cas/mpreduce/ggb2mpreduce";
	private Interpreter2 mpreduce;
	private final CasParserTools parserTools;
	
	// We escape any upper-letter words so Reduce doesn't switch them to lower-letter,
	// however the following function-names should not be escaped
	// (note: all functions here must be in lowercase!)
	final private Set<String> predefinedFunctions = ExpressionNodeConstants.RESERVED_FUNCTION_NAMES;

	public CASmpreduce(CASparser casParser, CasParserTools parserTools) {
		super(casParser, RB_GGB_TO_MPReduce);
		this.parserTools = parserTools;
		getInterpreter();
	}

	private synchronized Interpreter2 getInterpreter() {
		if (mpreduce == null) {
			mpreduce = new Interpreter2();

			// the first command sent to mpreduce produces an error
			try {
				initMyMPReduceFunctions();
				loadMyMPReduceFunctions();
			} catch (Throwable e)
			{}
			
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
		StringBuilder sb = new StringBuilder();
		sb.append("<<numeric!!:=0$ precision 30$ print\\_precision 16$ off complex, rounded, numval, factor, div, expandlogs$ on pri, combinelogs$ ");
		sb.append(translateToCAS(casInput, ExpressionNode.STRING_TYPE_MPREDUCE));
		sb.append(">>");

		String result = evaluateMPReduce(sb.toString());

		// convert result back into GeoGebra syntax
		if (!(casInput instanceof FunctionNVar)) {
			String ggbString = toGeoGebraString(result);
			return ggbString;
		}
		else
		{
			int oldPrintForm = casParser.getKernel().getCASPrintForm();
			casParser.getKernel().setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA);
			String ret = casInput.toString();
			casParser.getKernel().setCASPrintForm(oldPrintForm);
			return ret;
		}
	}

	/**
	 * Tries to parse a given MPReduce string and returns a String in GeoGebra syntax.
	 * @param mpreduceString String in MPReduce syntax
	 * @return String in Geogebra syntax.
	 * @throws Throwable Throws if the underlying CAS produces an error
	 */
	public synchronized String toGeoGebraString(String mpreduceString) throws Throwable {
		// since casParserparse<CAS>() is basically the same for all CAS anyway, we use the MathPiper one
		ValidExpression ve = casParser.parseMPReduce(mpreduceString);
		return casParser.toGeoGebraString(ve);
	}

	/**
	 * Evaluates an expression and returns the result as a string in MPReduce
	 * syntax, e.g. evaluateMathPiper("D(x) (x^2)") returns "2*x".
	 * 
     * @param exp expression (with command names already translated to MPReduce syntax).
	 * @return result string (null possible)
	 */
	public final String evaluateMPReduce(String exp) {
		try {
			exp = casParser.replaceIndices(exp);
			String ret = evaluateRaw(exp);
			ret = casParser.insertSpecialChars(ret); // undo special character handling
			
			// convert MPReduce's scientific notation from e.g. 3.24e-4 to 3.2E-4
			ret = parserTools.convertScientificFloatNotation(ret);
			
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
		sb.append(" end ");

		return sb.toString();
	}

	@Override
	public String evaluateRaw(String exp) throws Throwable {
		// we need to escape any upper case letters and non-ascii codepoints with '!'
		StringTokenizer tokenizer = new StringTokenizer(exp, "(),;[] ", true);
		StringBuilder sb = new StringBuilder();
		while (tokenizer.hasMoreElements()) {
			String t = tokenizer.nextToken();
			if (predefinedFunctions.contains(t.toLowerCase()))
				sb.append(t);
			else {
				for (int i = 0; i < t.length(); ++i) {
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

		long timeout = GeoGebraCAS.getTimeout()*1000;
		System.out.println("eval with MPReduce: " + exp);
		String result = mpreduce.evaluate(exp, timeout);

		sb.setLength(0);
		for (String s : result.split("\n")) {
			s = s.trim();
			if (s.length() == 0)
				continue;
			else if (s.startsWith("***")) { // MPReduce comment
				Application.debug("MPReduce comment: " + s);
				continue;
			}
			else if (s.startsWith("Unknown")){ 
				Application.debug("Assumed "+s);
				continue;
			} else {
				// look for any trailing $
				int len = s.length();
				while (len > 0 && s.charAt(len - 1) == '$')
					--len;

				// remove the !
				for (int i = 0; i < len; ++i) {
					char character = s.charAt(i);
					if (character == '!') {
						if (i + 1 < len) {
							character = s.charAt(++i);
						}
					}
					sb.append(character);
				}
			}
		}

		result = sb.toString();

		// TODO: remove
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
		try {
			mpreduce.evaluate("resetreduce;");
			mpreduce.initialize();
			initMyMPReduceFunctions();
		} catch (Throwable e) {
			e.printStackTrace();

			// if we fail, we just re-initialize the interpreter
			Application.debug("failed to reset MPReduce, creating new MPReduce instance");
			mpreduce = null;
			getInterpreter();
		}
	}

	@Override
	public void unbindVariable(String var) {
		try {
			mpreduce.evaluate("clear(" + ExpressionNodeConstants.GGBCAS_VARIABLE_PREFIX + var + ");");
		} catch (Throwable e) {
			System.err.println("Failed to clear variable from MPReduce: " + var);
		}
	}

	private synchronized void loadMyMPReduceFunctions() throws Throwable {
		mpreduce.evaluate("load_package rsolve;");
		mpreduce.evaluate("load_package numeric;");
		mpreduce.evaluate("load_package specfn;");
		mpreduce.evaluate("load_package odesolve;");
		mpreduce.evaluate("load_package defint;");
		mpreduce.evaluate("load_package linalg;");
		mpreduce.evaluate("load_package reset;");
		mpreduce.evaluate("load_package randpoly;");
		mpreduce.evaluate("load_package taylor;");
		mpreduce.evaluate("load_package assist;");
		mpreduce.evaluate("load_package groebner;");		
		mpreduce.evaluate("load_package trigsimp;");		
		
	}

	private synchronized void initMyMPReduceFunctions() throws Throwable {
		mpreduce.evaluate("off nat;");
			
		// ARBVARS introduces arbitrary new variables when solving singular systems of equations
		mpreduce.evaluate("off arbvars;");


		mpreduce.evaluate("off numval;");
		mpreduce.evaluate("linelength 50000;");
		mpreduce.evaluate("scientific_notation {16,5};");
		mpreduce.evaluate("on fullroots;");
		mpreduce.evaluate("printprecision!!:=5");
		
		mpreduce.evaluate("korder ggbcasvarx, ggbcasvary, ggbcasvarz, ggbcasvara, " +
				"ggbcasvarb, ggbcasvarc, ggbcasvard, ggbcasvare, ggbcasvarf, " +
				"ggbcasvarg, ggbcasvarh, ggbcasvari, ggbcasvarj, ggbcasvark, " +
				"ggbcasvarl, ggbcasvarm, ggbcasvarn, ggbcasvaro, ggbcasvarp, " +
				"ggbcasvarq, ggbcasvarr, ggbcasvars, ggbcasvart, ggbcasvaru, " +
				"ggbcasvarv, ggbcasvarw;");
		
		mpreduce.evaluate("let {" +
				"int(~w/~x,~x) => w*log(abs(x)) when freeof(w,x)," +
				"int(~w/(~x+~a),~x) => w*log(abs(x+a)) when freeof(w,x) and freeof(a,x)," +
				"int((~b*~x+~w)/(~x+~a),~x) => int((b*xw)/(x+a),x)+w*log(abs(x+a)) when freeof(w,x) and freeof(a,x) and freeof(b,x)," +
				"int((~a*~x+~w)/~x,~x) => int(a,x)+w*log(abs(x)) when freeof(w,x) and freeof(a,x)," +
				"int((~x+~w)/~x,~x) => x+w*log(abs(x)) when freeof(w,x)," +
				"int(tan(~x),~x) => log(abs(sec(x)))," +
				"int(~w*tan(~x),~x) => w*log(abs(sec(x))) when freeof(w,x)," +
				"int(~w+tan(~x),~x) => int(w,x)+log(abs(sec(x)))," +
				"int(~a+~w*tan(~x),~x) => int(a,x)+w*log(abs(sec(x))) when freeof(w,x)," +
				"int(cot(~x),~x) => log(abs(sin(x)))," +
				"int(~w*cot(~x),~x) => w*log(abs(sin(x))) when freeof(w,x)," +
				"int(~a+cot(~x),~x) => int(a,x)+log(abs(sin(x)))," +
				"int(~a+~w*cot(~x),~x) => int(a,x)+w*log(abs(sin(x))) when freeof(w,x)," +
				"int(sec(~x),~x) => -log(abs(tan(x / 2) - 1)) + log(abs(tan(x / 2) + 1))," +
				"int(~w*sec(~x),~x) => -log(abs(tan(x / 2) - 1))*w + log(abs(tan(x / 2) + 1) )*w when freeof(w,x)," +
				"int(~w+sec(~x),~x) => -log(abs(tan(x / 2) - 1)) + log(abs(tan(x / 2) + 1) )+int(w,x)," +
				"int(~a+w*sec(~x),~x) => -log(abs(tan(x / 2) - 1))*w + log(abs(tan(x / 2) + 1) )*w+int(a,x) when freeof(w,x)," +
				"int(csc(~x),~x) => log(abs(tan(x / 2)))," +
				"int(~w*csc(~x),~x) => w*log(abs(tan(x / 2))) when freeof(w,x)," +
				"int(~w+csc(~x),~x) => int(w,x)+log(abs(tan(x / 2)))," +
				"int(~a+~w*csc(~x),~x) => int(a,x)+w*log(abs(tan(x / 2))) when freeof(w,x)" +
				"};"
				);
		
		mpreduce.evaluate("let {cos(~w)^2 => 1-sin(w)^2}");
		
		mpreduce.evaluate("let {impart(arbint(~w)) => 0, arbint(~w)*i =>  0};");
		mpreduce.evaluate("let {atan(sin(~x)/cos(~x))=>x};");
		
		mpreduce.evaluate("solverules:={" +
				"tan(~x) => sin(x)/cos(x)" +
				"};");
		
		mpreduce.evaluate("procedure myatan2(y,x);" +
				" begin scalar xinput, yinput;" +
				" xinput:=x; yinput:=y;" +
				" on rounded, roundall, numval;" +
				" x:=x+0; y:=y+0;" +
				" return " +
				" if numberp(y) and numberp(x) then" +
				"   if x>0 then <<if numeric!!=0 then off rounded, roundall, numval; atan(yinput/xinput)>>" +
				"   else if x<0 and y>=0 then <<if numeric!!=0 then off rounded, roundall, numval; atan(yinput/xinput)+pi>>" +
				"   else if x<0 and y<0 then <<if numeric!!=0 then off rounded, roundall, numval; atan(yinput/xinput)-pi>>" +
				"   else if x=0 and y>0 then <<if numeric!!=0 then off rounded, roundall, numval; pi/2>>" +
				"   else if x=0 and y<0 then <<if numeric!!=0 then off rounded, roundall, numval; -pi/2>>" +
				"   else if x=0 and y=0 then <<if numeric!!=0 then off rounded, roundall, numval; 0>>" +
				"   else '?" +
				" else" +
				"   '? end;");
		
		// access functions for elements of a vector
		mpreduce.evaluate("procedure ggbcasvarx(a); if arglength(a)>-1 and part(a,0)='list then first(a) else ggbcasvarx*a;");
		mpreduce.evaluate("procedure ggbcasvary(a); if arglength(a)>-1 and part(a,0)='list then second(a) else ggbcasvary*a;");
		mpreduce.evaluate("procedure ggbcasvarz(a); if arglength(a)>-1 and part(a,0)='list then third(a) else ggbcasvarz*a;");

		mpreduce.evaluate(" Degree := pi/180;");

		mpreduce.evaluate("procedure myround(x);" 
				+ "floor(x+0.5);");
		
		
		mpreduce.evaluate("procedure harmonic(n,m); for i:=1:n sum 1/(i**m);");
		mpreduce.evaluate("procedure uigamma(n,m); gamma(n)-igamma(n,m);");
		mpreduce.evaluate("procedure beta!Regularized(a,b,x); ibeta(a,b,x);");
		mpreduce.evaluate("procedure myarg(x);" +
				" if arglength(x)>-1 and part(x,0)='list then myatan2(part(x,2), part(x,1)) " +
				" else if arglength(x)>-1 and part(x,0)='mat then <<" +
				"   clear x!!;" +
				"   x!!:=x;" +
				"   if row_dim(x!!)=1 then myatan2(x!!(1,2),x!!(1,1))" +
				"   else if column_dim(x!!)=1 then myatan2(x!!(2,1),x!!(2,1))" +
				"   else arg(x!!) >>" +
				" else myatan2(impart(x),repart(x));");
		mpreduce.evaluate("procedure polartocomplex(r,phi); r*(cos(phi)+i*sin(phi));");
		mpreduce.evaluate("procedure polartopoint(r,phi); list(r*cos(phi),r*sin(phi));");
		mpreduce.evaluate("procedure complexexponential(r,phi); r*(cos(phi)+i*sin(phi));");
		mpreduce.evaluate("procedure conjugate(x); conj(x);");
		mpreduce.evaluate("procedure myrandom(); <<on rounded; random(100000001)/(random(100000000)+1)>>;");
		mpreduce.evaluate("procedure gamma!Regularized(a,x); igamma(a,x);");
		mpreduce.evaluate("procedure gamma2(a,x); gamma(a)*igamma(a,x);");
		mpreduce.evaluate("procedure beta3(a,b,x); beta(a,b)*ibeta(a,b,x);");
		mpreduce.evaluate("symbolic procedure isbound!! x; if get(x, 'avalue) then 1 else 0;");	
		mpreduce.evaluate("procedure myabs(x);" +
				" if arglength(x!!)>-1 and part(x,0)='list then sqrt(for each elem!! in x sum elem!!^2)" +
				" else if arglength(x)>-1 and part(x,0)='mat then <<" +
				"   clear x!!;" +
				"   x!!:=x;" +
				"   if row_dim(x!!)=1 then sqrt(for i:=1:column_dim(x!!) sum x!!(1,i)^2)" +
				"   else if column_dim(x!!)=1 then sqrt(for i:=1:row_dim(x!!) sum x!!(i,1)^2)" +
				"   else abs(x!!) >>" +
				" else if freeof(x,i) then abs(x)" +
				" else sqrt(repart(x)^2+impart(x)^2);");

		mpreduce.evaluate("procedure mylog10 x;" +
				"begin scalar x!!;" +
				" x!!:=x;" +
				" on rounded, roundall, numval;" +
				" if numberp(x!!) then <<" +
				"   x!!:=log10(x!!);" +
				"   if floor(x!!)=x!! then <<" +
				"     if numeric!!=0 then" +
				"       off rounded, roundall, numval;" +
				"     return x!!" +
				"   >>" +
				" >>;" +
				" if numeric!!=0 then" +
				"   off rounded, roundall, numval;" +
				" return log10(x) " +
				"end;");
		
		mpreduce.evaluate("procedure flattenlist(a);" +
				"if 1=for each elem!! in a product length(elem!!) then for each elem!! in a join elem!! else a;");
		
		mpreduce.evaluate("procedure mysolve(eqn, var);"
				+ " begin scalar solutions!!, bool!!;"
				+ "  eqn:=mkdepthone({eqn});"
				+ "  let solverules;"
				+ "  if arglength(eqn)>-1 and part(eqn,0)='list then"
				+ "    eqn:=for each x in eqn collect"
				+ "      if freeof(x,=) then x else lhs(x)-rhs(x)"
				+ "  else if freeof(eqn,=) then 1 else eqn:=lhs(eqn)-rhs(eqn);"
				+ "  solutions!!:=solve(eqn,var);"
				+ "	 if depth(solutions!!)<2 then"
				+ "		solutions!!:=for each x in solutions!! collect {x};"
				+ "	 solutions!!:=for each sol in solutions!! join <<"
				+ "    bool!!:=1;"
				+ "    for each solution!! in sol do"
				+ "      if freeof(solution!!,'root_of) then <<"
				+ "		   on rounded, roundall, numval, complex;"
				+ "		   if freeof(solution!!,'i) or aeval(impart(rhs(solution!!)))=0 then 1 else bool!!:=0;"
				+ "		   off complex;"
				+ "		   if numeric!!=0 then off rounded, roundall, numval"
				+ "      >>" 
				+ "      else" 
				+ "	       bool!!:=2*bool!!;"
				+ "    if bool!!=1 then" 
				+ "  	 {sol}"
				+ "	   else if bool!!>1 then " 
				+ "  	 {{var='?}}" 
				+ "    else "
				+ "		 {} >>;"
				+ "  clearrules solverules;"
				+ "  return mkset(solutions!!);" 
				+ " end;");
		
		mpreduce.evaluate("procedure mycsolve(eqn, var);" +
				" begin scalar solutions!!, bool!!;" +
				"  eqn:=mkdepthone({eqn});" +
				"  let solverules;" +
				"  if arglength(eqn)>-1 and part(eqn,0)='list then" +
				"    eqn:=for each x in eqn collect" +
				"      if freeof(x,=) then x else lhs(x)-rhs(x)" +
				"  else if freeof(eqn,=) then 1 else eqn:=lhs(eqn)-rhs(eqn);" +
				"    solutions!!:=solve(eqn,var);" +
				"    if depth(solutions!!)<2 then" +
				"      solutions!!:=for each x in solutions!! collect {x};" +
				"    solutions!!:= for each sol in solutions!! join <<" +
				"      bool!!:=1;" +
				"      for each solution!! in sol do" +
				"        if freeof(solution!!,'root_of) then 1 else" +
				"      		bool!!:=0;" +
				"      if bool!!=1 then" +
				"        {sol}" +
				"      else if bool!!=0 then" +
				"        {{var='?}}" +
				"      >>;" +
				"  clearrules solverules;" +
				"  return mkset(solutions!!);" +
				" end;");
		
		mpreduce.evaluate("procedure mysolve1(eqn);"
				+ " begin scalar solutions!!, bool!!;"
				+ "  eqn:=mkdepthone({eqn});"
				+ "  let solverules;"
				+ "  if arglength(eqn)>-1 and part(eqn,0)='list then"
				+ "    eqn:=for each x in eqn collect"
				+ "      if freeof(x,=) then x else lhs(x)-rhs(x)"
				+ "  else if freeof(eqn,=) then 1 else eqn:=lhs(eqn)-rhs(eqn);"
				+ "  solutions!!:=solve(eqn);"
				+ "	 if depth(solutions!!)<2 then"
				+ "		solutions!!:=for each x in solutions!! collect {x};"
				+ "	 solutions!!:=for each sol in solutions!! join <<"
				+ "    bool!!:=1;"
				+ "    for each solution!! in sol do"
				+ "      if freeof(solution!!,'root_of) then <<"
				+ "		   on rounded, roundall, numval, complex;"
				+ "		   if freeof(solution!!,'i) or aeval(impart(rhs(solution!!)))=0 then 1 else bool!!:=0;"
				+ "		   off complex;"
				+ "		   if numeric!!=0 then off rounded, roundall, numval"
				+ "      >>" 
				+ "      else" 
				+ "	       bool!!:=2*bool!!;"
				+ "    if bool!!=1 then" 
				+ "  	 {sol}"
				+ "	   else if bool!!>1 then " 
				+ "  	 {{'?}}" 
				+ "    else "
				+ "		 {} >>;" 
				+ "  clearrules solverules;"
				+ "  return mkset(solutions!!);" 
				+ " end;");
		
		mpreduce.evaluate("procedure mycsolve1(eqn);" +
				" begin scalar solutions!!, bool!!;" +
				"  let solverules;" +
				"  eqn:=mkdepthone({eqn});" +
				"  if arglength(eqn)>-1 and part(eqn,0)='list then" +
				"    eqn:=for each x in eqn collect" +
				"      if freeof(x,=) then x else lhs(x)-rhs(x)" +
				"  else if freeof(eqn,=) then 1 else eqn:=lhs(eqn)-rhs(eqn);" +
				"    solutions!!:=solve(eqn);" +
				"    if depth(solutions!!)<2 then" +
				"      solutions!!:=for each x in solutions!! collect {x};" +
				"    solutions!!:= for each sol in solutions!! join <<" +
				"      bool!!:=1;" +
				"      for each solution!! in sol do" +
				"        if freeof(solution!!,'root_of) then 1 else" +
				"      		bool!!:=0;" +
				"      if bool!!=1 then" +
				"        {sol}" +
				"      else if bool!!=0 then" +
				"        {{var='?}}" +
				"      >>;" +
				"  clearrules solverules;" +
				"  return mkset(solutions!!);" +
				" end;");
		
		mpreduce.evaluate("procedure dot(vec1,vec2); "
				+ "	begin scalar tmplength; "
				+ "  if arglength(vec1)>-1 and part(vec1,0)='mat and column_dim(vec1)=1 then "
				+ "    vec1:=tp(vec1);"
				+ "  if arglength(vec2)>-1 and part(vec2,0)='mat and column_dim(vec2)=1 then "
				+ "    vec2:=tp(vec2); "
				+ "  return  "
				+ "  if arglength(vec1)>-1 and part(vec1,0)='list then << "
				+ "    if arglength(vec2)>-1 and part(vec2,0)='list then  "
				+ "      <<tmplength:=length(vec1);  "
				+ "      for i:=1:tmplength  "
				+ "			sum part(vec1,i)*part(vec2,i) >> "
				+ "    else if arglength(vec2)>-1 and part(vec2,0)='mat and row_dim(vec2)=1 then"
				+ "      <<tmplength:=length(vec1);  "
				+ "      for i:=1:tmplength  "
				+ "	sum part(vec1,i)*vec2(1,i)>> "
				+ "      else "
				+ "	'? "
				+ "  >> "
				+ "  else <<if arglength(vec1)>-1 and part(vec1,0)='mat and row_dim(vec1)=1 then << "
				+ "    if arglength(vec2)>-1 and part(vec2,0)='list then  "
				+ "      <<tmplength:=length(vec2); "
				+ "      for i:=1:tmplength  "
				+ "			sum vec1(1,i)*part(vec2,i)>> "
				+ "    else if arglength(vec2)>-1 and part(vec2,0)='mat and row_dim(vec2)=1 then"
				+ "      <<tmplength:=column_dim(vec1);  "
				+ "      for i:=1:tmplength  " 
				+ "			sum vec1(1,i)*vec2(1,i) "
				+ "      >> " 
				+ "      else " 
				+ "		'? " 
				+ "    >> " 
				+ "  else "
				+ "    '? " 
				+ "  >> " 
				+ "end;");
		
		mpreduce.evaluate("procedure cross(atmp,btmp); " +
				"begin;" +
				"  a:=atmp; b:= btmp;" +
				"  if arglength(a)=-1 or (length(a) neq 3 and length(a) neq 2 and length(a) neq {1,3} and length(a) neq {3,1} and length(a) neq {1,2} and length(a) neq {2,1}) then return '?;" +
				"  if arglength(b)=-1 or (length(b) neq 3 and length(b) neq 2 and length(b) neq {1,3} and length(b) neq {3,1} and length(b) neq {1,2} and length(b) neq {2,1}) then return '?;" +
				"  if length(a)={1,3} or length(b)={1,2} then a:=tp(a);" +
				"  if length(b)={1,3} or length(b)={1,2} then b:=tp(b);" +
				"  return" +
				"  if arglength(a)>-1 and part(a,0)='mat then <<" +
				"    if arglength(b)>-1 and part(b,0)='mat then <<" +
				"      if length(a)={3,1} and length(b)={3,1} then" +
				"        mat((a(2,1)*b(3,1)-a(3,1)*b(2,1))," +
				"        (a(3,1)*b(1,1)-a(1,1)*b(3,1))," +
				"        (a(1,1)*b(2,1)-a(2,1)*b(1,1)))" +
				"      else if length(a)={2,1} and length(b)={2,1} then" +
				"        mat((0)," +
				"        (0)," +
				"        (a(1,1)*b(2,1)-a(2,1)*b(1,1)))" +
				"      else '?" +
				"    >> else if arglength(b)>-1 and part(b,0)='list then <<" +
				"      if length(a)={3,1} and length(b)=3 then" +
				"        list(a(2,1)*part(b,3)-a(3,1)*part(b,2)," +
				"        a(3,1)*part(b,1)-a(1,1)*part(b,3)," +
				"        a(1,1)*part(b,2)-a(2,1)*part(b,1))" +
				"      else if length(a)={2,1} and length(b)=2 then" +
				"        list(0," +
				"        0," +
				"        a(1,1)*part(b,2)-a(2,1)*part(b,1))" +
				"      else '?" +
				"    >> else << '? >>" +
				"  >> else if arglength(a)>-1 and part(a,0)='list then <<" +
				"    if arglength(b)>-1 and part(b,0)='mat then <<" +
				"      if length(a)=3 and length(b)={3,1} then" +
				"        list(part(a,2)*b(3,1)-part(a,3)*b(2,1)," +
				"        part(a,3)*b(1,1)-part(a,1)*b(3,1)," +
				"        part(a,1)*b(2,1)-part(a,2)*b(1,1))" +
				"      else if length(a)=2 and length(b)={2,1} then" +
				"        list(0," +
				"        0," +
				"        part(a,1)*b(2,1)-part(a,2)*b(1,1))" +
				"      else '?" +
				"    >> else if arglength(b)>-1 and part(b,0)='list then <<" +
				"      if length(a)=3 and length(b)=3 then" +
				"        list(part(a,2)*part(b,3)-part(a,3)*part(b,2)," +
				"        part(a,3)*part(b,1)-part(a,1)*part(b,3)," +
				"        part(a,1)*part(b,2)-part(a,2)*part(b,1))" +
				"      else if length(a)=2 and length(b)=2 then" +
				"        list(0," +
				"        0," +
				"        part(a,1)*part(b,2)-part(a,2)*part(b,1))" +
				"      else '?" +
				"    >> else << '? >>" +
				"  >> else << '? >> " +
				"end;");

		mpreduce.evaluate("procedure mattoscalar(m);"
				+ " if length(m)={1,1} then trace(m) else m;");

		mpreduce.evaluate("procedure multiplication(a,b);"
				+ "  if arglength(a)>-1 and part(a,0)='mat then"
				+ "    if arglength(b)>-1 and part(b,0)='mat then"
				+ "      mattoscalar(a*b)"
				+ "    else if arglength(b)>-1 and part(b,0)='list then"
				+ "      mattoscalar(a*<<listtocolumnvector(b)>>)"
				+ "    else"
				+ "      a*b"
				+ "  else if arglength(a)>-1 and part(a,0)='list then"
				+ "    if arglength(b)>-1 and part(b,0)='mat then"
				+ "      mattoscalar(<<listtorowvector(a)>>*b)"
				+ "    else if arglength(b)>-1 and part(b,0)='list then"
				+ "      mattoscalar(<<listtorowvector(a)>>*<<listtocolumnvector(b)>>)"
				+ "    else" 
				+ "      map(~w!!*b,a)" 
				+ "  else"
				+ "    if arglength(b)>-1 and part(b,0)='list then" 
				+ "      map(a*~w!!,b)"
				+ "    else"
				+ "		 if a=infinity then"
				+ "		   if (numberp(b) and b>0) or b=infinity then infinity"
				+ "		   else if (numberp(b) and b<0) or b=-infinity then -infinity"
				+ "		   else '?"
				+ "		 else if a=-infinity then"
				+ "		   if (numberp(b) and b>0) or b=infinity then -infinity"
				+ "		   else if (numberp(b) and b<0) or b=-infinity then infinity"
				+ "		   else '?"
				+ "		 else if b=infinity then"
				+ "		   if (numberp(a) and a>0) or a=infinity then infinity"
				+ "		   else if (numberp(a) and a<0) or a=-infinity then -infinity"
				+ "		   else '?"
				+ "		 else if b=-infinity then"
				+ "		   if (numberp(a) and a>0) or a=infinity then -infinity"
				+ "		   else if (numberp(a) and a<0) or a=infinity then infinity"
				+ "		   else '?"
				+ "		 else"
				+ "        a*b;");
		
		mpreduce.evaluate("operator multiplication;");

		mpreduce.evaluate("procedure addition(a,b);"
				+ "  if arglength(a)>-1 and part(a,0)='list and arglength(b)>-1 and part(b,0)='list then"
				+ "    for i:=1:length(a) collect part(a,i)+part(b,i)"
				+ "  else if arglength(a)>-1 and part(a,0)='list then" 
				+ "    map(~w!!+b,a)"
				+ "  else if arglength(b)>-1 and part(b,0)='list then" 
				+ "    map(a+~w!!,b)"
				+ "  else" 
				+ "    a+b;");
		
		mpreduce.evaluate("operator addition;");

		mpreduce.evaluate("procedure subtraction(a,b);"
				+ "  if arglength(a)>-1 and part(a,0)='list and arglength(b)>-1 and part(b,0)='list then"
				+ "    for i:=1:length(a) collect part(a,i)-part(b,i)"
				+ "  else if arglength(a)<-1 and part(a,0)='list then" 
				+ "    map(~w!!-b,a)"
				+ "  else if arglength(b)>-1 and part(b,0)='list then" 
				+ "    map(a-~w!!,b)"
				+ "  else" 
				+ "    a-b;");
		
		mpreduce.evaluate("operator subtraction;");
		
		// erf in Reduce is currently broken:
		// http://sourceforge.net/projects/reduce-algebra/forums/forum/899364/topic/4546339
		// this is a numeric approximation according to Abramowitz & Stegun
		// 7.1.26.
		mpreduce.evaluate("procedure erf(x); "
				+ "begin scalar a1!!, a2!!, a3!!, a4!!, a5!!, p!!, x!!, t!!, y!!, sign!!, result!!;"
				+ "     on rounded;"
				+ "		if numberp(x) then 1 else return !*hold(erf(x));"
				+ "     a1!! :=  0.254829592; "
				+ "     a2!! := -0.284496736; "
				+ "     a3!! :=  1.421413741; "
				+ "     a4!! := -1.453152027; "
				+ "     a5!! :=  1.061405429; "
				+ "     p!!  :=  0.3275911; "
				+ "     sign!! := 1; "
				+ "     if x < 0 then sign!! := -1; "
				+ "     x!! := Abs(x); "
				+ "     t!! := 1.0/(1.0 + p!!*x!!); "
				+ "     y!! := 1.0 - (((((a5!!*t!! + a4!!)*t!!) + a3!!)*t!! + a2!!)*t!! + a1!!)*t!!*Exp(-x!!*x!!); "
				+ "     result!! := sign!!*y!!;"
				+ "     if numeric!!=1 then off rounded;"
				+ "     return result!! " 
				+ "end;");


		
		mpreduce.evaluate("procedure mkdepthone(liste);" +
				"	for each x in liste join " +
				"	if arglength(x)>-1 and part(x,0)='list then" +
				"	mkdepthone(x) else {x};");
		
		mpreduce.evaluate("procedure listtocolumnvector(list); "
				+ "begin scalar lengthoflist; "
				+ "lengthoflist:=length(list); "
				+ "matrix m!!(lengthoflist,1); " 
				+ "for i:=1:lengthoflist do "
				+ "m!!(i,1):=part(list,i); " 
				+ "return m!! " 
				+ "end;");

		mpreduce.evaluate("procedure listtorowvector(list); "
				+ "begin scalar lengthoflist; "
				+ "	lengthoflist:=length(list); "
				+ "	matrix m!!(1,lengthoflist); "
				+ "	for i:=1:lengthoflist do " 
				+ "		m!!(1,i):=part(list,i); "
				+ "	return m!! " 
				+ "end;");

		mpreduce.evaluate("procedure mod!!(a,b);" +
				" a-b*div(a,b)");
		
		mpreduce.evaluate("procedure div(a,b);" +
				" begin scalar a!!, b!!, result!!;" +
				"  a!!:=a; b!!:=b;" +
				"  on rounded, roundall, numval;" +
				"  return " +
				"  if numberp(a!!) and numberp(b!!) then <<" +
				"    if numeric!!=0 then" +
				"      off rounded, roundall, numval;" +
				"    if b!!>0 then " +
				"	   floor(a/b)" +
				"    else" +
				"      ceiling(a/b)" +
				"  >> else << " +
				"    if numeric!!=0 then" +
				"      off rounded, roundall, numval;" +
				"    part(divpol(a*b*a,b*a*b),1)>>" +
				" end;");
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
	
	/**
	 * Sets the number of signficiant figures (digits) that should be used as print precision for the
	 * output of Numeric[] commands.
	 * 
	 * @param significantNumbers
	 */
	public void setSignificantFiguresForNumeric(int significantNumbers) {
		try{
			mpreduce.evaluate("printprecision!!:=" + significantNumbers);
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}
}
