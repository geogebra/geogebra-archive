/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.cas;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.main.Application;
import geogebra.main.MyResourceBundle;
import jasymca.GeoGebraJasymca;

import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import yacas.YacasInterpreter;

/**
 * This class provides an interface for GeoGebra to use the computer algebra
 * systems Jasymca and Yacas.
 * 
 * @author Markus Hohenwarter
 */
public class GeoGebraCAS {
	
	final public String RB_GGB_TO_YACAS = "/geogebra/cas/ggb2yacas";

	private Kernel kernel;
	private CASparser casParser;
	private YacasInterpreter yacas;
	private GeoGebraJasymca ggbJasymca;
	
	private ResourceBundle ggb2Yacas;
	private StringBuffer sbInsertSpecial, sbRemoveSpecial;

	public GeoGebraCAS(Kernel kernel) {
		this.kernel = kernel;
		casParser = new CASparser(kernel);
		
		sbInsertSpecial = new StringBuffer(80);
		sbRemoveSpecial = new StringBuffer(80);		
	}
	
	/**
	 * Parses the given GeoGebra CAS input and returns an ExpressionValue object.
	 */
	public ExpressionValue parseInput(String exp) throws Throwable {		
		ExpressionValue ev = casParser.parseForCAS(exp);
		
		// TODO: remove
		Application.debug("  checkCASinput: " + ev);
		
		return ev;		
	}
	
	/**
	 * Evaluates the given ExpressionValue and returns the result in Yacas syntax.
	 * 
	 * @param resolveVariables: states whether variables from the GeoGebra kernel 
	 *    should be used. Note that this changes the given ExpressionValue. 
	 */
	public String toYacasString(ExpressionValue ev, boolean resolveVariables) {
		if (resolveVariables) {
			casParser.resolveVariablesForCAS(ev);
			
			// TODO: remove
			Application.debug("  resolveVariablesForCAS: " + ev);
		}	
		
		// convert to Yacas String
		String yacasStr = casParser.toYacasString(ev, resolveVariables);
	
		// TODO: remove
		Application.debug(" toYacasString: " + yacasStr);
		return yacasStr;
	}				
		
	/**
	 * Evaluates a YACAS expression and returns the result as a string, e.g. exp =
	 * "D(x) (x^2)" returns "2*x".
	 * 
	 * @return result string (null possible)
	 */
	final public String evaluateYACAS(String exp) {
		String result = evaluateYACAS(exp, true);
		
		// TODO: remove
		Application.debug("evaluateYacas: " + exp + ", result: " + result);
		
		
		return result;
	}
	
	/**
	 * Evaluates a YACAS expression wrapped in a command and returns the result as a string, 
	 * e.g. wrapperCommand = "Factor", exp = "3*(a+b)" evaluates "Factor(3*(a+b)" and 
	 * returns "3*a+3*b".
	 * 
	 * @return result string (null possible)
	 */
	final public String evaluateYACAS(String wrapperCommand, String exp) {
		StringBuffer sb = new StringBuffer(exp.length()+wrapperCommand.length()+2);
		sb.append(wrapperCommand);
		sb.append('(');
		sb.append(exp);				
		sb.append(')');
		return evaluateYACAS(sb.toString());
	}
	
	/**
	 * Evaluates a YACAS expression without any preprocessing and returns the
	 * result as a string, e.g. exp = "D(x) (x^2)" returns "2*x".
	 * 
	 * @return result string (null possible)
	 */
	final public String evaluateYACASRaw(String exp) {
		return evaluateYACAS(exp, false);
	}
	
	/**
	 * Returns the error message of the last Yacas evaluation.
	 * @return null if last evaluation was successful.
	 */
	final public String getYACASError() {
		return getYacas().getErrorMessage();
	}
			
	private String evaluateYACAS(String exp, boolean replaceSpecialChars) {
		// Application.debug("exp for YACAS: " + exp);
		try {
			String result;
			
			// YACAS has problems with special characters
			if (replaceSpecialChars)
				exp = replaceSpecialChars(exp);

			// evaluate the Yacas expression
			result = getYacas().Evaluate(exp);

			// undo special character handling
			if (replaceSpecialChars)
				result = insertSpecialChars(result);

			// Application.debug(" result: " + result);
			
			return result;
		} catch (Throwable th) {
			//yacas.Evaluate("restart;");
			th.printStackTrace();
			return null;
		} 
	}

	private synchronized GeoGebraJasymca getJasymca() {
		if (ggbJasymca == null)
			ggbJasymca = new GeoGebraJasymca();
		return ggbJasymca;
	}
	
	private synchronized YacasInterpreter getYacas() {
		if (yacas == null) 
			yacas = new YacasInterpreter();				
		return yacas;
	}
	
	/**
	 * Evaluates a JASYMCA expression and returns the result as a string, e.g.
	 * exp = "diff(x^2,x)" returns "2*x".
	 * 
	 * @return result string, null possible
	 */
	final public String evaluateJASYMCA(String exp) {		
		String result = getJasymca().evaluate(exp);

		// to handle x(A) and x(B) they are converted
		// to unicode strings in ExpressionNode,
		// we need to convert them back here
		result = insertSpecialChars(result);

		// Application.debug("exp for JASYMCA: " + exp);
		// Application.debug(" result: " + result);

		return result;
	}
	

	/**
	 * Expands the given JASYMCA expression and tries to get its polynomial
	 * coefficients. The coefficients are returned in ascending order. If exp is
	 * not a polynomial null is returned.
	 * 
	 * example: getPolynomialCoeffs("3*a*x^2 + b"); returns ["0", "b", "3*a"]
	 */
	final public String[] getPolynomialCoeffs(String jasymcaExp, String variable) {		
		return getJasymca().getPolynomialCoeffs(jasymcaExp, variable);
	}



	/**
	 * Converts all special characters (like greek letters) in the given String
	 * to "unicode" + charactercode + DELIMITER Strings. This is neede because
	 * YACAS cannot handle all unicode characters.
	 */
	private String replaceSpecialChars(String str) {
		int len = str.length();
		sbRemoveSpecial.setLength(0);

		// convert every single character and append it to sb
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			int code = (int) c;

			// standard characters
			if ((code >= 32 && code <= 122)) {
				switch (code) {
				case 95: // replace _
					// case 39: // replace '
					sbRemoveSpecial.append(ExpressionNode.UNICODE_PREFIX);
					sbRemoveSpecial.append(code);
					sbRemoveSpecial.append(ExpressionNode.UNICODE_DELIMITER);
					break;

				default:
					// do not convert
					sbRemoveSpecial.append(c);
				}
			}
			// special characters
			else {
				switch (code) {
				case 176: // replace degree sign by " * unicode_string_of_degree_sign"
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

	
	
	/**
	 * Returns the Yacas command for the given key (from ggb2yacas.properties)
	 * and the given command arguments. 
	 * For example, getYacasCommand("Expand.0", {"3*(a+b)"}) returns "Expand( 3*(a+b) )"
	 */
	final public String getYacasCommand(String name, ArrayList args, boolean symbolic) {
		StringBuffer sbYacasCommand = new StringBuffer(80);
				
		// build command key as name + "." + args.size()
		sbYacasCommand.setLength(0);
		sbYacasCommand.append(name);
		sbYacasCommand.append('.');
		sbYacasCommand.append(args.size());
		
		// get translation ggb -> yacas
		String translation = getYacasCommand(sbYacasCommand.toString());
		sbYacasCommand.setLength(0);		
		
		// no translation found: 
		// use key as command name
		if (translation == null) {			
			sbYacasCommand.append(name);
			sbYacasCommand.append('(');
			for (int i=0; i < args.size(); i++) {
				ExpressionValue ev = (ExpressionValue) args.get(i);				
				if (symbolic)
					sbYacasCommand.append(ev.toString());
				else
					sbYacasCommand.append(ev.toValueString());
				sbYacasCommand.append(',');
			}
			sbYacasCommand.setCharAt(sbYacasCommand.length()-1, ')');
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
							sbYacasCommand.append(ev.toString());
						else
							sbYacasCommand.append(ev.toValueString());
					} else {
						// failed
						sbYacasCommand.append(ch);
					}
				} else {
					sbYacasCommand.append(ch);
				}
			}
		}

		return sbYacasCommand.toString();
	}
	
	
	/**
	 * Returns the Yacas command for the given key (from ggb2yacas.properties)
	 */ 
	private String getYacasCommand(String key) {
		if (ggb2Yacas == null) {
			ggb2Yacas = MyResourceBundle.loadSingleBundleFile(RB_GGB_TO_YACAS);
		}
		
		String ret;
		try {
			ret =  ggb2Yacas.getString(key);
		} catch (MissingResourceException e) {
			ret = null;
		}
		
		// TODO: remove
		Application.debug("getYacasCommand for " + key + " gives: " + ret);
		
		return ret;
	}
		
	/*
	 * public static void main(String [] args) {
	 * 
	 * GeoGebraCAS cas = new GeoGebraCAS();
	 * 
	 * Application.debug("GGBCAS"); // Read/eval/print loop int i=1;
	 * while(true){ Application.debug( "(In"+i+") "); // Prompt try{ String line =
	 * readLine(System.in); //String result = yacas.Evaluate(line);
	 * 
	 * String result = cas.evaluateJASYMCA(line);
	 * 
	 * Application.debug( "(Out"+i+") "+result ); i++; }catch(Exception e){
	 * Application.debug("\n"+e); } } }
	 */

	// Read everything until ';' or EOF
//	static String readLine(InputStream in) {
//		StringBuffer s = new StringBuffer();
//		try {
//			int c;
//			while ((c = in.read()) != -1 && c != ';')
//				s.append((char) c);
//		} catch (Exception e) {
//		}
//		return s.toString();
//	}
	

	/*
	 * Evaluates an JSCL expression and returns the result as a string. e.g. exp =
	 * "d(x^2, x)" returns "2*x"
	 * 
	 * @param expression
	 *            string
	 * @return result string (null possible)
	 * 
	 * final public String evaluateJSCL(String exp) { //Application.debug("exp
	 * for JSCL: " + exp);
	 * 
	 * try { String result; // JSCL has problems with special characters: // get
	 * rid of them String myExp = removeSpecialChars(exp); Generic
	 * in=Expression.valueOf(myExp); // Strings for expand, simplify and
	 * factorize // we want the shortest string to be returned //String [] str =
	 * new String[2];
	 * 
	 * Generic out = in.expand();
	 * 
	 * //Application.debug(" expand: " + out);
	 * 
	 * if (out.isPolynomial(xVar)) { // build polynomial UnivariatePolynomial p =
	 * UnivariatePolynomial.valueOf(out, xVar); result = toReverseString(p); }
	 * else { out = out.simplify(); result = out.toString(); }
	 * 
	 * //Application.debug(" result: " + result); //Application.debug(" result
	 * (special chars): " + insertSpecialChars(result));
	 * 
	 * result = out.toString(); return insertSpecialChars(result); } catch
	 * (Error err) { err.printStackTrace(); return null; } catch (Exception e) {
	 * e.printStackTrace(); return null; } }
	 */

	/*
	 * private static String toReverseString(UnivariatePolynomial p) {
	 * sbReverse.setLength(0); if(p.signum()==0) sbReverse.append("0"); int n=0;
	 * int d=p.degree(); for(int i=d;i>=0;i--) { Generic a=p.get(i);
	 * if(a.signum()==0) continue; if(a instanceof Expression)
	 * a=a.signum()>0?GenericVariable.valueOf(a).expressionValue():GenericVariable.valueOf(a.negate()).expressionValue().negate();
	 * if(a.signum()>0 && n>0) sbReverse.append("+"); if(i==0)
	 * sbReverse.append(a); else { if(a.compareTo(JSCLInteger.valueOf(1))==0);
	 * else if(a.compareTo(JSCLInteger.valueOf(-1))==0) sbReverse.append("-");
	 * else sbReverse.append(a).append("*");
	 * 
	 * switch (i) { case 1: sbReverse.append("x"); break; default:
	 * sbReverse.append("x^"); sbReverse.append(i); } } n++; } return
	 * sbReverse.toString(); }
	 */

	/*
	 * old code for JSCL
	 * 
	 * final public String [] getPolynomialCoeffs(String exp) { try { // JSCL
	 * does not recognize x^2 / 4 as a polynomial // but it does recognize x^2 *
	 * 1/4, so we replace every "/" by "*1/" String noDivExp =
	 * removeSpecialChars(exp.replaceAll("/", "*1/"));
	 * 
	 * //Application.debug("getPolynomialCoeffs for " + exp);
	 * //Application.debug("noDivExp " + noDivExp);
	 * 
	 * Generic jsclExp = Expression.valueOf(noDivExp).expand();
	 * 
	 * //Application.debug(" expand: " + jsclExp); //Application.debug("
	 * isPolynomial(x): " + jsclExp.isPolynomial(xVar)); // check if we have a
	 * polynomial if (!jsclExp.isPolynomial(xVar)) { // try to simplify jsclExp =
	 * jsclExp.simplify(); //Application.debug(" simplify: " + jsclExp); if
	 * (!jsclExp.isPolynomial(xVar)) return null; } // build polynomial
	 * UnivariatePolynomial p = UnivariatePolynomial.valueOf(jsclExp, xVar);
	 * 
	 * int deg = p.degree(); String [] coeffs = new String[deg+1]; for (int i=0;
	 * i <= deg; i++) { Generic coeff = p.get(i); // the coefficient must not
	 * include the variable if (coeff.isConstant(xVar)) coeffs[i] =
	 * insertSpecialChars(coeff.toString()); else return null;
	 * 
	 * //Application.debug(" coeff " + i + ": " + coeffs[i]);
	 * //Application.debug(" is constant: " + p.get(i).isConstant(xVar)); }
	 * 
	 * return coeffs; } catch (Error err) { err.printStackTrace(); return null; }
	 * catch (Exception e) { e.printStackTrace(); return null; } }
	 */
}