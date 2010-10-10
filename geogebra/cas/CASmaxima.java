package geogebra.cas;


import geogebra.cas.jacomax.JacomaxAutoConfigurator;
import geogebra.cas.jacomax.JacomaxSimpleConfigurator;
import geogebra.cas.jacomax.MaximaConfiguration;
import geogebra.cas.jacomax.MaximaInteractiveProcess;
import geogebra.cas.jacomax.MaximaProcessLauncher;
import geogebra.cas.jacomax.MaximaTimeoutException;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;
import geogebra.main.MyResourceBundle;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class CASmaxima extends CASgeneric {
	
	final public String RB_GGB_TO_Maxima = "/geogebra/cas/ggb2maxima";
	
	private MaximaInteractiveProcess ggbMaxima;
	private ResourceBundle ggb2Maxima;
	
	public CASmaxima(CASparser casParser) {
		super(casParser);
	}
	
	/**
	 * Returns whether var is a defined variable in Maxima.
	 */
	public boolean isVariableBound(String var) {
		// check if var is assigned a value or defined as function in Maxima
		StringBuilder sb = new StringBuilder();
		
		// TODO: improve
		// not sequal(string(u), "u") or not equal(errcatch(fundef(u)), []);
		sb.append("(not sequal(string(");
		sb.append(var);
		sb.append("), \"");
		sb.append(var);
		sb.append("\") or not equal(errcatch(fundef(");
		sb.append(var);
		sb.append(")), []));");
		
		return "true".equals(evaluateMaxima(sb.toString()));
	}
	
	/**
	 * Unbinds (deletes) var in Maxima.
	 * @param var
	 * @param isFunction
	 */
	public void unbindVariable(String var) {		
		StringBuilder sb = new StringBuilder();
		
		// kill variable var
		sb.append("kill(");
		sb.append(var);
		sb.append(");");

		evaluateMaxima(sb.toString());
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
		
		// Maxima simplification is turned off by default using simp:false;
		// We turn it on here using ev(command, simp) when KeepInput is not used
		if (!casInput.isKeepInputUsed()) {
			StringBuilder sb = new StringBuilder();
			sb.append("ev(");
			sb.append(MaximaString);
			sb.append(",simp)");
			MaximaString = sb.toString();
		}
		
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
			
			if (ve instanceof FunctionNVar) {
				// function, e.g. f(x) := 2*x
				FunctionNVar fun = (FunctionNVar) ve;
				sb.append(veLabel);
				sb.append("(" );
				sb.append(fun.getVarString());
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
			
			// Maxima uses [] for lists
			while (exp.indexOf('{') > -1 ) exp = exp.replace('{', '[');
			while (exp.indexOf('}') > -1 ) exp = exp.replace('}', ']');
			
			final boolean debug = true;
			if (debug) Application.debug("Expression for Maxima: "+exp, 1);
			
			// evaluate the MathPiper expression
			//RawMaximaSession maxima = getMaxima();
			
			//result = maxima.executeExpectingSingleOutput(exp);
			//String results[] = maxima.executeExpectingMultipleLabels(exp);			
			//result = results[results.length - 1];
			
			//String results[] = executeRaw(exp).split("\n");
			
			String res = executeRaw(exp);
			
			while (res.indexOf('\n') > -1 ) res = res.replace('\n', ' ');
			
			String results[] = res.split("\\(%[oi]\\d+\\)\\s*");
			
			result = results[results.length - 1];
			
			// if last line is empty, look for next non-empty previous line
			if (result.trim().length() == 0 && results.length > 1) {
				int i = results.length - 2;
				while (results[i].trim().length() == 0 && i > 0) i--;
				result = results[i];
			}
			
			// remove (%o1) at start
			//result = result.replaceFirst("\\(%o\\d+\\)\\s*", "").trim();
			
			if (result.indexOf("%c") > -1) {
				result = result.replaceAll("%c","const");
				Application.debug("WARNING: replacing %c by const",1);
			}
			
			if (result.indexOf("%e") > -1) {
				result = result.replaceAll("%e","e");
				Application.debug("WARNING: replacing %e by e",1);
			}
			
			if (result.indexOf(" =") > -1) { // y = not :=
				result = result.replaceAll(" =","==");
				Application.debug("WARNING: replacing = by ==",1);
			}
			
			if (debug) {
				for (int i = 0 ; i < results.length ; i++)
					System.err.println("Result "+i+": "+results[i]);
				System.out.println("result: "+result);
			}
				
			// undo special character handling
			result = casParser.insertSpecialChars(result);
			
			// replace eg [x=0,x=1] with {x=0,x=1}
			while (result.indexOf('[') > -1) result = result.replace('[','{');
			while (result.indexOf(']') > -1) result = result.replace(']','}');

			return result;
		} catch (MaximaTimeoutException e) {
			Application.debug("Timeout from Maxima, resetting");
			ggbMaxima = null;
			return "?";
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
	
	
	private synchronized MaximaInteractiveProcess getMaxima() {
		if (ggbMaxima == null) {
			MaximaConfiguration configuration = casParser.getKernel().getApplication().maximaConfiguration;
			
			if (configuration == null) configuration = JacomaxAutoConfigurator.guessMaximaConfiguration();
			
			MaximaProcessLauncher launcher = new MaximaProcessLauncher(configuration);
			ggbMaxima = launcher.launchInteractiveProcess();
			try {
				initMyMaximaFunctions();
				System.out.println(ggbMaxima.executeCall("1+2;"));
			} catch (MaximaTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//process.terminate();		
		}
		

				
				
				

			
		
	
		return ggbMaxima;
	}
	
	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 */
	public void reset() {
		ggbMaxima = null;
		getMaxima();
	}
	
	private void initMyMaximaFunctions() throws MaximaTimeoutException, geogebra.cas.jacomax.MaximaTimeoutException {
	
		// turn auto-simplification off, so a+a gives a+a
		// with this setting ev( a+a, simp ) is needed to get 2*a
	    ggbMaxima.executeCall("simp:false;");
	    
	    // variable ordering: then factor(a^2-b^2) gives (a-b)*(b+a) instead of �(b-a)(b+a) 
	    // see http://www.geogebra.org/trac/ticket/281
	    ggbMaxima.executeCall("ordergreat(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,z);");
	    
		// set line length of "terminal"
		// we don't want lines broken
	    ggbMaxima.executeCall("linel:10000;");
	    
		// make sure results are returned
	    ggbMaxima.executeCall("display2d:false;");
	    
	    // make sure integral(1/x) = log(abs(x))
	    ggbMaxima.executeCall("logabs:true;");
	    
	    // make sure algsys (solve) doesn't return complex roots
	    ggbMaxima.executeCall("realonly:true;");
	    
	    // eg x^-1 displayed as 1/x
	    ggbMaxima.executeCall("exptdispflag:true;");
	    
	    // suppresses the printout of the message informing the user of the conversion of floating point numbers to rational numbers
	    ggbMaxima.executeCall("ratprint:false;");
	    
	    // When true, r some rational number, and x some expression, %e^(r*log(x)) will be simplified into x^r . It should be noted that the radcan command also does this transformation, and more complicated transformations of this ilk as well. The logcontract command "contracts" expressions containing log. 
	    ggbMaxima.executeCall("%e_to_numlog:true;");
	    
	    
	    // define custom functions
	    ggbMaxima.executeCall("log10(x) := log(x) / log(10);");
	    ggbMaxima.executeCall("log2(x) := log(x) / log(2);");
	    ggbMaxima.executeCall("cbrt(x) := x^(1/3);");
	    
	    // needed to define lcm()
	    ggbMaxima.executeCall("load(functs)$");
	    
	    // needed for degree()
	    ggbMaxima.executeCall("load(powers)$");
	       
	    // needed for ???
	    ggbMaxima.executeCall("load(format)$");
	       
	    // turn {x=3} into {3} etc
	    ggbMaxima.executeCall("stripequals(ex):=block(" +
	    		 "if atom(ex) then return(ex)" +
	    		 "else if op(ex)=\"=\" then return(stripequals(rhs(ex)))" +
	    		 "else apply(op(ex),map(stripequals,args(ex)))" +
	    		")$");
	    
	    /* This function takes an expression ex and returns a list of coefficients of v */
	    ggbMaxima.executeCall("coefflist(ex,v):= block([deg,kloop,cl]," +
	    		"cl:[]," +
	      "ex:ev(expand(ex),simp)," +
	      "deg:degree(ex,v)," +
	      "ev(for kloop:0 thru deg do\n" +
	      "cl:append(cl,[coeff(ex,v,kloop)]),simp)," +
	      "cl" +
	      ")$");
	   
	    /*
	     * eg integrate(x^n,x) asks if n+1 is zero
	     * this disables the interactivity
	     * but we get:
	     * if equal(n+1,0) then log(abs(x)) else x^(n+1)/(n+1)
	     * TODO: change to ggb syntax
	     */
	    ggbMaxima.executeCall("load(\"noninteractive\");");

	    
	    // define Degree
	    ggbMaxima.executeCall("Degree:180/%pi;");

	}

	private String executeRaw(String maximaInput) throws MaximaTimeoutException, geogebra.cas.jacomax.MaximaTimeoutException {
        char lastChar = maximaInput.charAt(maximaInput.length() - 1);
        if (lastChar != ';' && lastChar != '$' && !maximaInput.startsWith(":lisp")) {
        	maximaInput += ";";
        }    
       
        return getMaxima().executeCall(maximaInput);

	}
}
