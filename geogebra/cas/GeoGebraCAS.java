/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.cas;

import geogebra.JarManager;
import geogebra.cas.view.CASView;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;
import geogebra.main.MyResourceBundle;

import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.mathpiper.interpreters.EvaluationResponse;
import org.mathpiper.interpreters.Interpreter;
import org.mathpiper.interpreters.Interpreters;

/**
 * This class provides an interface for GeoGebra to use the computer algebra
 * systems Jasymca and MathPiper.
 * 
 * @author Markus Hohenwarter
 */
public class GeoGebraCAS {
	
	final public String RB_GGB_TO_MathPiper = "/geogebra/cas/ggb2mathpiper";

	private Kernel kernel;
	private Application app;
	private CASparser casParser;
	private Interpreter ggbMathPiper;
	
	private ResourceBundle ggb2MathPiper;
	private StringBuffer sbInsertSpecial, sbRemoveSpecial, sbPolyCoeffs;

	public GeoGebraCAS(Kernel kernel) {
		this.kernel = kernel;
		app = kernel.getApplication();
		casParser = new CASparser(kernel);
		
		sbInsertSpecial = new StringBuffer(80);
		sbRemoveSpecial = new StringBuffer(80);		
	}
	
	/**
	 * Processes the CAS input string and returns an evaluation result.
	 * @boolean doEvaluate: whether inputExp should be evaluated (i.e. simplified).
	 * @return null if something went wrong.
	 */
	public synchronized String processCASInput(String inputExp, boolean resolveVariables) throws Throwable {
		// replace #1, #2 references by row input
		inputExp = resolveCASrowReferences(inputExp);
		
		// PARSE input
		ValidExpression ve = parseGeoGebraCASInput(inputExp);
		
		// check for assignment, e.g. a := 5
		String assignmentLabel = ve.getLabel();
		
		// convert parsed input to MathPiper string
		String MathPiperString = toMathPiperString(ve, resolveVariables);
		
		// EVALUATE input in MathPiper 
		String MathPiperResult = evaluateMathPiper(MathPiperString);
				
		// convert MathPiper result back into GeoGebra syntax
		ve = parseMathPiper(MathPiperResult);
		String ggbResult = ve.toString();		
		
		// if we evaluated an assignment, we may need to update a global variable 
		if (assignmentLabel != null) {
			// check for global variable with this name
			if (kernel.lookupLabel(assignmentLabel) != null) {
				// process assignment in GeoGebra
				ve.setLabel(assignmentLabel);
				kernel.getAlgebraProcessor().processValidExpression(ve);
			}			
		}
		
		return ggbResult;
	}
	
	
	/**
	 * Parses the given GeoGebra CAS input and returns an ValidExpression object.
	 */
	public synchronized ValidExpression parseGeoGebraCASInput(String exp) throws Throwable {
		// parse input
		ValidExpression ve = casParser.parseGeoGebraCASInput(exp);
		
		// TODO: remove
		Application.debug("  checkCASinput: " + ve);
		
		return ve;		
	}
	
	/**
	 * Replaces references to other rows (e.g. #3) in input string by
	 * the values from those rows.
	 */
	private synchronized String resolveCASrowReferences(String inputExp) {
		if (!app.hasCasView()) 
			return inputExp;
				
		CASView casView = (CASView) app.getCasView();	
		sbCASreferences.setLength(0);
		int length = inputExp.length();
		int lastPos = length -1;
		for (int i = 0; i < length; i++) {
			char ch = inputExp.charAt(i);
			if (ch == '#' && i < lastPos) {
				// get number after #
				i++;
				int pos = inputExp.charAt(i) - '0' - 1;
				if (pos >= 0 && pos < casView.getRowCount()) {
					// success
					sbCASreferences.append(casView.getRowValue(pos));
				} else
					// failed
					sbCASreferences.append(ch);
			} else {
				sbCASreferences.append(ch);
			}
		}

		return sbCASreferences.toString();
	}

	private StringBuffer sbCASreferences = new StringBuffer();
	
	/**
	 * Evaluates the given ExpressionValue and returns the result in MathPiper syntax.
	 * 
	 * @param resolveVariables: states whether variables from the GeoGebra kernel 
	 *    should be used. Note that this changes the given ExpressionValue. 
	 */
	public synchronized String toMathPiperString(ValidExpression ve, boolean resolveVariables) {
		
		// resolve global variables
		if (resolveVariables) {				
			casParser.resolveVariablesForCAS(ve);
			
			// TODO: remove
			Application.debug("  resolveVariablesForCAS: " + ve);
		}	
		
		// convert to MathPiper String
		String MathPiperStr = casParser.toMathPiperString(ve, resolveVariables);
	
		
		
		
		String veLabel = ve.getLabel();
		if (veLabel != null) {
			StringBuffer sb = new StringBuffer();
			
			if (ve instanceof Function) {
				// function, e.g. f(x) := 2*x
				Function fun = (Function) ve;
				sb.append(veLabel);
				sb.append("(" );
				sb.append(fun.getFunctionVariable());
				sb.append(") := ");
				sb.append(MathPiperStr);
				MathPiperStr = sb.toString();
			} else {	
				// assignment, e.g. a := 5
				MathPiperStr = veLabel + " := " + MathPiperStr;
			}
		}
		
		// TODO: remove
		Application.debug(" toMathPiperString: " + MathPiperStr);
		return MathPiperStr;
	}		
	
	
	/**
	 * Tries to parse a given MathPiper string and returns a ValidExpression object.
	 */
	public synchronized ValidExpression parseMathPiper(String MathPiperString) throws Throwable {
		return casParser.parseMathPiper(MathPiperString);
	}
	
	/**
	 * Returns the MathPiper command for the given key (from ggb2MathPiper.properties)
	 * and the given command arguments. 
	 * For example, getMathPiperCommand("Expand.0", {"3*(a+b)"}) returns "Expand( 3*(a+b) )"
	 */
	final synchronized public String getMathPiperCommand(String name, ArrayList args, boolean symbolic) {
		StringBuffer sbMathPiperCommand = new StringBuffer(80);
				
		// build command key as name + "." + args.size()
		sbMathPiperCommand.setLength(0);
		sbMathPiperCommand.append(name);
		sbMathPiperCommand.append('.');
		sbMathPiperCommand.append(args.size());
		
		// get translation ggb -> MathPiper
		String translation = getMathPiperCommand(sbMathPiperCommand.toString());
		sbMathPiperCommand.setLength(0);		
		
		// no translation found: 
		// use key as command name
		if (translation == null) {			
			sbMathPiperCommand.append(name);
			sbMathPiperCommand.append('(');
			for (int i=0; i < args.size(); i++) {
				ExpressionValue ev = (ExpressionValue) args.get(i);				
				if (symbolic)
					sbMathPiperCommand.append(ev.toString());
				else
					sbMathPiperCommand.append(ev.toValueString());
				sbMathPiperCommand.append(',');
			}
			sbMathPiperCommand.setCharAt(sbMathPiperCommand.length()-1, ')');
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
							sbMathPiperCommand.append(ev.toString());
						else
							sbMathPiperCommand.append(ev.toValueString());
					} else {
						// failed
						sbMathPiperCommand.append(ch);
					}
				} else {
					sbMathPiperCommand.append(ch);
				}
			}
		}

		return sbMathPiperCommand.toString();
	}
	
	
	/**
	 * Returns the MathPiper command for the given key (from ggb2MathPiper.properties)
	 */ 
	private synchronized String getMathPiperCommand(String key) {
		if (ggb2MathPiper == null) {
			ggb2MathPiper = MyResourceBundle.loadSingleBundleFile(RB_GGB_TO_MathPiper);
		}
		
		String ret;
		try {
			ret =  ggb2MathPiper.getString(key);
		} catch (MissingResourceException e) {
			ret = null;
		}
		
		// TODO: remove
		Application.debug("getMathPiperCommand for " + key + " gives: " + ret);
		
		return ret;
	}
	
	/**
	 * Evaluates a MathPiper expression and returns the result as a string in MathPiper syntax, 
	 * e.g. evaluateMathPiper("D(x) (x^2)") returns "2*x".
	 * 
	 * @return result string (null possible)
	 */
	final synchronized public String evaluateMathPiper(String exp) {
		String result = evaluateMathPiper(exp, true);		
		// TODO: remove
		//Application.debug("evaluateMathPiper: " + exp + ", result: " + result);				
		return result;
	}
	
	/**
	 * Evaluates a MathPiper expression without any preprocessing and returns the
	 * result as a string, e.g. exp = "D(x) (x^2)" returns "2*x".
	 * 
	 * example: getPolynomialCoeffs("3*a*x^2 + b"); returns ["b", "0", "3*a"]
	 */
	final public synchronized String evaluateMathPiperRaw(String exp) {
		return evaluateMathPiper(exp, false);
	}
				
	/**
	 * Evaluates a MathPiper expression wrapped in a command and returns the result as a string, 
	 * e.g. wrapperCommand = "Factor", exp = "3*(a+b)" evaluates "Factor(3*(a+b)" and 
	 * returns "3*a+3*b".
	 * 
	 * @return result string (null possible)
	 */
	final synchronized public String evaluateMathPiper(String wrapperCommand, String exp) {
		StringBuffer sb = new StringBuffer(exp.length()+wrapperCommand.length()+2);
		sb.append(wrapperCommand);
		sb.append('(');
		sb.append(exp);				
		sb.append(')');
		return evaluateMathPiper(sb.toString());
	}	
	
	/**
	 * Returns the error message of the last MathPiper evaluation.
	 * @return null if last evaluation was successful.
	 */
	final synchronized public String getMathPiperError() {
		if (response != null)
			return response.getExceptionMessage();
		else 
			return null;
	}
	
	EvaluationResponse response ;	
	
	private synchronized String evaluateMathPiper(String exp, boolean replaceSpecialChars) {
		// TODO: remove
		Application.debug("exp for MathPiper: " + exp);
		
		try {
			String result;
			
			// MathPiper has problems with special characters
			if (replaceSpecialChars)
				exp = replaceSpecialChars(exp);

			// evaluate the MathPiper expression
			Interpreter mathpiper = getMathPiper();

			response = mathpiper.evaluate(exp);
			
			if (response.isExceptionThrown())
			{
				Application.debug("Exception from MathPiper: "+response.getExceptionMessage());
				return null;
			}
			result = response.getResult();
					
			// undo special character handling
			if (replaceSpecialChars)
				result = insertSpecialChars(result);

			// TODO: remove
			Application.debug(" result: " + result);
			
			return result;
		} catch (Throwable th) {
			//MathPiper.Evaluate("restart;");
			th.printStackTrace();
			return null;
		} 
	}
	
	private synchronized Interpreter getMathPiper() {				
		if (ggbMathPiper == null) {
			// where to find MathPiper scripts
			//eg docBase = "jar:http://www.geogebra.org/webstart/alpha/geogebra_cas.jar!/";
			
			String scriptBase = "jar:" + app.getCodeBase().toString() + JarManager.CAS_JAR_NAME + "!/";			
			
			// TODO: remove
			Application.debug("loading MathPiper scripts from: "+scriptBase);			
			
			ggbMathPiper = Interpreters.getSynchronousInterpreter(scriptBase);
		}
		
		return ggbMathPiper;
	}	
	

	final public String simplifyMathPiper(String exp) {
		return evaluateMathPiper("Simplify", exp );
	}
	
	final public String factorMathPiper(String exp) {
		return evaluateMathPiper("Factor", exp );
	}

	final public String expandMathPiper(String exp) {
		return evaluateMathPiper("ExpandBrackets", exp );
	}
	
	/**
	 * Expands the given MathPiper expression and tries to get its polynomial
	 * coefficients. The coefficients are returned in ascending order. If exp is
	 * not a polynomial, null is returned.
	 * 
	 * example: getPolynomialCoeffs("3*a*x^2 + b"); returns ["b", "0", "3*a"]
	 */
	final public String[] getPolynomialCoeffs(String MathPiperExp, String variable) {
		//return ggbJasymca.getPolynomialCoeffs(MathPiperExp, variable);
		
		if (sbPolyCoeffs == null)
			sbPolyCoeffs = new StringBuffer();
		else
			sbPolyCoeffs.setLength(0);
		
		// Expand expression and get polynomial coefficients using MathPiper:
		// Prog( Local(exp), 
		//   	 exp := ExpandBrackets( 3*a*x^2 + b ), 
		//		 Coef(exp, x, 0 .. Degree(exp, x)) 
		// )		
		sbPolyCoeffs.append("Prog( Local(exp), exp := ExpandBrackets(");
		sbPolyCoeffs.append(MathPiperExp);
		sbPolyCoeffs.append("), Coef(exp, x, 0 .. Degree(exp, x)))");
			
		try {
			// expand expression and get coefficients of
			// "3*a*x^2 + b" in form "{ b, 0, 3*a }" 
			String result = evaluateMathPiper(sbPolyCoeffs.toString());
			
			// remove { } to get "b, 0, 3*a"
			result = result.substring(1, result.length()-1);
			
			// split to get coefficients array ["b", "0", "3*a"]
			String [] coeffs = result.split(",");				    
            return coeffs;						
		} 
		catch(Exception e) {
			Application.debug("GeoGebraCAS.getPolynomialCoeffs(): " + e.getMessage());
			//e.printStackTrace();
		}
		
		return null;
	}


	/**
	 * Converts all special characters (like greek letters) in the given String
	 * to "unicode" + charactercode + DELIMITER Strings. This is neede because
	 * MathPiper cannot handle all unicode characters.
	 */
	private synchronized String replaceSpecialChars(String str) {
		int len = str.length();
		sbRemoveSpecial.setLength(0);

		// convert every single character and append it to sb
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			int code = (int) c;

			// standard characters
			if ((code >= 32 && code <= 122)) {
				switch (c) {
				// keep _ for indices like n_{3}
//				case '_': // replace _
//					sbRemoveSpecial.append(ExpressionNode.UNICODE_PREFIX);
//					sbRemoveSpecial.append(code);
//					sbRemoveSpecial.append(ExpressionNode.UNICODE_DELIMITER);
//					break;

				default:
					// do not convert
					sbRemoveSpecial.append(c);
				}
			}
			// special characters
			else {
				switch (c) {
				case '{': // keep {
				case '}': // keep }
					sbRemoveSpecial.append(c);
					break;
				
				case '\u00b0': // replace degree sign by " * unicode_string_of_degree_sign"
					sbRemoveSpecial.append("*");

				default:
					sbRemoveSpecial.append(ExpressionNode.UNICODE_PREFIX);
					sbRemoveSpecial.append(code);
					sbRemoveSpecial.append(ExpressionNode.UNICODE_DELIMITER);
				}

			}
		}
		return sbRemoveSpecial.toString();
	}

	/**
	 * Reverse operation of removeSpecialChars().
	 * @see ExpressionNode.operationToString() for XCOORD, YCOORD
	 */
	private String insertSpecialChars(String str) {
		int len = str.length();
		sbInsertSpecial.setLength(0);

		// convert every single character and append it to sb
		char prefixStart = ExpressionNode.UNICODE_PREFIX.charAt(0);
		int prefixLen = ExpressionNode.UNICODE_PREFIX.length();
		boolean prefixFound;
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			prefixFound = false;

			// first character of prefix found
			if (c == prefixStart) {
				prefixFound = true;
				// check prefix
				int j = i;
				for (int k = 0; k < prefixLen; k++, j++) {
					if (ExpressionNode.UNICODE_PREFIX.charAt(k) != str
							.charAt(j)) {
						prefixFound = false;
						break;
					}
				}

				if (prefixFound) {
					// try to get the unicode
					int code = 0;
					char digit;
					while (j < len && Character.isDigit(digit = str.charAt(j))) {
						code = 10 * code + (digit - 48);
						j++;
					}

					if (code > 0 && code < 65536) { // valid unicode
						sbInsertSpecial.append((char) code);
						i = j;
					} else { // invalid
						sbInsertSpecial.append(ExpressionNode.UNICODE_PREFIX);
						i += prefixLen;
					}
				} else {
					sbInsertSpecial.append(c);
				}
			} else {
				sbInsertSpecial.append(c);
			}
		}
		return sbInsertSpecial.toString();
	}
	
	/*
	 * public static void main(String [] args) {
	 * 
	 * GeoGebraCAS cas = new GeoGebraCAS();
	 * 
	 * Application.debug("GGBCAS"); // Read/eval/print loop int i=1;
	 * while(true){ Application.debug( "(In"+i+") "); // Prompt try{ String line =
	 * readLine(System.in); //String result = MathPiper.Evaluate(line);
	 * 
	 * String result = cas.evaluateJASYMCA(line);
	 * 
	 * Application.debug( "(Out"+i+") "+result ); i++; }catch(Exception e){
	 * Application.debug("\n"+e); } } }
	 */

}