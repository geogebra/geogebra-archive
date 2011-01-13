/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.cas;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoDummyVariable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.kernel.parser.Parser;
import geogebra.main.Application;

/**
 * Handles parsing and evaluating of input in the CAS view.
 * 
 * @author Markus Hohenwarter
 */
public class CASparser {
	
	private Kernel kernel;
	private Parser ggbParser;
	private StringBuilder sbInsertSpecial, sbReplaceIndices;
	
	public CASparser(Kernel kernel) {	
		this.kernel = kernel;
		ggbParser = kernel.getParser();
		
		sbInsertSpecial = new StringBuilder(80);
		sbReplaceIndices = new StringBuilder(80);
	}
	
	Kernel getKernel() {
		return kernel;
	}
	
	/**
	 * Parses the given expression and returns it as a ValidExpression.
	 * @throws Throwable when something goes wrong
	 */
	public ValidExpression parseGeoGebraCASInput(String exp) throws Throwable {
		return ggbParser.parseGeoGebraCAS(exp);
	}
	
	/**
	 * Resolves all variables in ValidExpression. Unknown variables are
	 * kept as symbolic variables.
	 */
	public synchronized void resolveVariablesForCAS(ExpressionValue ev) {
		
		// add local variables to kernel
		boolean isFunction = ev instanceof Function;
		String localVar = null;
		if (isFunction) {
			Construction cmdCons = kernel.getConstruction();  
			localVar = ((Function) ev).getFunctionVariables().toString();
			GeoElement localVarGeo = new GeoDummyVariable(cmdCons, localVar);
			cmdCons.addLocalVariable(localVar, localVarGeo);
		}
		
		// resolve variables of valid expression
		kernel.setResolveUnkownVarsAsDummyGeos(true);
		ev.resolveVariables();
		kernel.setResolveUnkownVarsAsDummyGeos(false);
		
		// remove local variables from kernel
		if (isFunction) {
			Construction cmdCons = kernel.getConstruction();    		
			cmdCons.removeLocalVariable(localVar);
		}				
	}
	
	
	
	/**
	 * Tries to convert the given MathPiper string to GeoGebra syntax.
	 */
	public String toGeoGebraString(ExpressionValue ev) throws Throwable {
		return toString(ev, ExpressionNode.STRING_TYPE_GEOGEBRA);
	}
	
	/**
	 * Tries to convert the given MathPiper string to the given syntax.
	 * @param STRING_TYPE: one of ExpressionNode.STRING_TYPE_GEOGEBRA, STRING_TYPE_GEOGEBRA_XML
	 */
	public String toString(ExpressionValue ev, int STRING_TYPE) throws Throwable {
		String GeoGebraString;
		
		if (!ev.isExpressionNode()) {
			ev = new ExpressionNode(kernel, ev);			
		}
		
		ExpressionNode en = (ExpressionNode) ev;
		GeoGebraString = en.getCASstring(STRING_TYPE, true);		
		return GeoGebraString;
	}
	

	
	/**
	 * Tries to convert the given MathPiper string to GeoGebra syntax.
	 */
	public ValidExpression parseMathPiper(String MathPiperString) throws Throwable {
		return ggbParser.parseMathPiper(MathPiperString);		
	}
	
	/**
	 * Tries to convert the given MathPiper string to GeoGebra syntax.
	 */
	public ValidExpression parseMaxima(String maximaString) throws Throwable {
		return ggbParser.parseMaxima(maximaString);		
	}


	/**
	 * Converts all index characters ('_', '{', '}') in the given String
	 * to "unicode" + charactercode + DELIMITER Strings. This is needed because
	 * MathPiper does not handle indices correctly.
	 */
	public synchronized String replaceIndices(String str) {
		int len = str.length();
		sbReplaceIndices.setLength(0);
		
		boolean foundIndex = false;

		// convert every single character and append it to sb
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			int code = (int) c;
			
			boolean replaceCharacter = false;			
			switch (c) {
				case '_': // start index
					foundIndex = true;
					replaceCharacter = true;
					
					if (i > 0 && str.charAt(i-1) == '\\')
						replaceCharacter = false;
					break;
										
				case '{': 	
					if (foundIndex) {
						replaceCharacter = true;						
					}					
					break;					
					
				case '}':
					if (foundIndex) {
						replaceCharacter = true;
						foundIndex = false; // end of index
					}					
					break;
					
				default:
					replaceCharacter = false;
			}
			
			if (replaceCharacter) {
				sbReplaceIndices.append(ExpressionNode.UNICODE_PREFIX);
				sbReplaceIndices.append(code);
				sbReplaceIndices.append(ExpressionNode.UNICODE_DELIMITER);
			} else {
				sbReplaceIndices.append(c);
			}
		}
					
		return sbReplaceIndices.toString();
	}

	/**
	 * Reverse operation of removeSpecialChars().
	 * @see ExpressionNode.operationToString() for XCOORD, YCOORD
	 */
	public String insertSpecialChars(String str) {
		int prefixLen = ExpressionNode.UNICODE_PREFIX.length();
		
		if (str.length() < prefixLen) return str;
		
		int len = str.length();
		sbInsertSpecial.setLength(0);

		// convert every single character and append it to sb
		char prefixStart = ExpressionNode.UNICODE_PREFIX.charAt(0);
		boolean prefixFound;
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			prefixFound = false;

			// first character of prefix found
			if (c == prefixStart && i + prefixLen < str.length()) {
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
	 * Converts scientific number notations in input into GeoGebra notation.
	 * For example, 3.4e-5 is changed into 3.4E-5 for expChar 'e' (MathPiper) and
	 * 3.4b-5 is changed into 3.4E-5 for expChar 'b' (Maxima).
	 * @param input expression
	 * @param expChar exponent character like 'e' or 'b'
	 * @return converted expression with exponent character 'E'
	 */
	public static String convertScientificFloatNotation(String input, char expChar) {
		// convert MathPiper's scientific notation from e.g. 3.24e-4 to 3.2E-4
		if (input.indexOf(expChar) > -1) {
			boolean prevDigit = false;
			StringBuilder sb = new StringBuilder(input.length());
			for (int i=0; i < input.length(); i++) {
				char cur = input.charAt(i);
				if (cur == expChar && prevDigit) {
					sb.append('E');
				} else {
					sb.append(cur);
				}
				prevDigit = Character.isDigit(cur);
			}
			return sb.toString();
		}
		return input;
	}

}
