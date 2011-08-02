package geogebra.cas;

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
	
	private final static String RB_GGB_TO_MPReduce = "/geogebra/cas/ggb2mpreduce";
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
		sb.append("<<numeric!!:=0$ precision 16$ print\\_precision 16$ off complex, rounded, numval, factor, div$ on pri$ ");
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
		result = result.replaceAll("\\*\\*", "^");

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
		
	}

	private synchronized void initMyMPReduceFunctions() throws Throwable {
		mpreduce.evaluate("off nat;");
			
		// ARBVARS introduces arbitrary new variables when solving singular systems of equations
		mpreduce.evaluate("off arbvars;");


		mpreduce.evaluate("off numval;");
		mpreduce.evaluate("linelength 50000;");
		mpreduce.evaluate("scientific_notation {16,5};");
		
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
				"int(~a+~w*csc(~x),~x) => int(a,x)+w*log(abs(tan(x / 2))) when freeof(w,x)};"
				);
		
		mpreduce.evaluate("let {impart(arbint(~w)) => 0, arbint(~w)*i =>  0};");
		
		// bugfix for reduce, will be removed when the bug is fixed in reduce ( :rd: - problem)
//		mpreduce.evaluate("symbolic procedure xprint(u,flg);"
//				+ "   begin scalar v,w;"
//				+ "      v := tc u;"
//				+ "      u := tpow u;"
//				+ "      if (w := kernlp v) and w neq 1"
//				+ "        then <<v := quotf(v,w);"
//				+ "               if minusf w"
//				+ "                 then <<oprin 'minus; w := !:minus w; flg := nil>>>>;"
//				+ "      if flg then oprin 'plus;"
//				+ "      if w and w neq 1"
//				+ "        then <<if domainp w then maprin w else prin2!* w; oprin 'times>>;"
//				+ "      xprinp u;"
//				+ "      if v neq 1 then <<oprin 'times; xprinf(v,red v,nil)>>"
//				+ "   end;");
		// make sure integral(1/x) gives ln(abs(x)) [TODO: NOT WORKING]
		// mpreduce.evaluate("operator log!-temp");
		// mpreduce.evaluate("sub(log!-temp = log, ( int(1/x,x) where {log(~xx) => abs(log!-temp(xx))}))");

		// access functions for elements of a vector
		
		
		mpreduce.evaluate("procedure ggbcasvarx(a); if arglength(a)>-1 and part(a,0)='list then first(a) else ggbcasvarx*a;");
		mpreduce.evaluate("procedure ggbcasvary(a); if arglength(a)>-1 and part(a,0)='list then second(a) else ggbcasvary*a;");
		mpreduce.evaluate("procedure ggbcasvarz(a); if arglength(a)>-1 and part(a,0)='list then third(a) else ggbcasvarz*a;");

		mpreduce.evaluate(" Degree := pi/180;");

		mpreduce.evaluate("procedure myround(x);" 
				+ "floor(x+0.5);");
		
		mpreduce.evaluate("symbolic procedure isbound!! x; if get(x, 'avalue) then 1 else 0;");
		
		mpreduce.evaluate("procedure mysolve(eqn, var);"
				+ " begin scalar solutions!!, bool!!;"
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
				+ "  return mkset(solutions!!);" 
				+ " end;");
		
		mpreduce.evaluate("procedure mycsolve(eqn, var);" +
				"  begin scalar solutions!!, bool!!;" +
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
				"      return mkset(solutions!!);" +
				"  end;");
		
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
				+ "      a*b;");
		
		mpreduce.evaluate("operator multiplication;");

		mpreduce.evaluate("procedure addition(a,b);"
				+ "  if arglength(a)>-1 and a='list and arglength(b)>-1 and part(b,0)='list then"
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
				+ "begin "
				+ "     on rounded;"
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
				+ "     return sign!!*y!! " + "end;");

		mpreduce.evaluate("procedure harmonic(n,m); for i:=1:n sum 1/(i**m);");
		mpreduce.evaluate("procedure uigamma(n,m); gamma(n)-igamma(n,m);");
		mpreduce.evaluate("procedure beta!Regularized(a,b,x); ibeta(a,b,x);");
		mpreduce.evaluate("procedure arg(z); atan2(repart(z),impart(z));");
		mpreduce.evaluate("procedure complexpolar(r,phi); r*(cos(phi)+i*sin(phi));");
		mpreduce.evaluate("procedure complexexponential(r,phi); r*(cos(phi)+i*sin(phi));");
		
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
				" if numberp(a) and numberp(b) then" +
				"	 a-b*div(a,b)" +
				" else" +
				"	 part(divpol(a*b*a,b*a*b),2)/(a*b);");
		
		mpreduce.evaluate("procedure div(a,b);" +
				" if numberp(a) and numberp(b) then" +
				"	if b>0 then " +
				"	  floor(a/b)" +
				"	else" +
				"	  ceiling(a/b)" +
				" else " +
				"    part(divpol(a*b*a,b*a*b),1);");

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
