/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.cas;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.kernel.arithmetic.Variable;
import geogebra.kernel.parser.Parser;

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
		// resolve variables of valid expression
		kernel.setResolveVariablesForCASactive(true);
		ev.resolveVariables();
		kernel.setResolveVariablesForCASactive(false);				
	}
	
	
	
	/**
	 * Tries to convert the given MathPiper string to GeoGebra syntax.
	 */
	public String toGeoGebraString(ExpressionValue ev) throws Throwable {
		String GeoGebraString;
		
		if (!ev.isExpressionNode()) {
			ev = new ExpressionNode(kernel, ev);			
		}
		
		ExpressionNode en = (ExpressionNode) ev;
		GeoGebraString = en.getCASstring(ExpressionNode.STRING_TYPE_GEOGEBRA, true);		
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


}
