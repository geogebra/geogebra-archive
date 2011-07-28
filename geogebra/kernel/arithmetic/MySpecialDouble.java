/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.util.Unicode;

import java.math.BigDecimal;

/**
 * MyDouble that returns a certain string in toString(). 
 * This is used for example for the degree sign in geogebra.parser.Parser.jj:
 * new MySpecialDouble(kernel, Math.PI / 180.0d,  "\u00b0" );
 * 
 * @author Markus Hohenwarter
 */
public class MySpecialDouble extends MyDouble {
	
	private String strToString;
	private int precision; // number of significant digits
	private boolean isLetterConstant; // for Pi, Euler, or Degree constant
	
	private static MySpecialDouble eulerConstant;
	
	public MySpecialDouble(Kernel kernel, double val, String strToString) {
		super(kernel, val);
		
		// check if this is a letter constant, e.g. Pi or Euler number
		char firstChar = strToString.charAt(0);
		isLetterConstant = Character.isLetter(firstChar) || firstChar == Unicode.degreeChar;
		
		if (!isLetterConstant) {
			// determine precision of strToString
			if (strToString.indexOf('E') > -1) {
				// convert scientific notation to plain decimal
				// e.g. 8.571428571428571E-1 to 0.8571428571428571
				BigDecimal bd = new BigDecimal(strToString);
				precision = bd.precision();
				strToString = bd.toPlainString();
			} else {
				precision = strToString.length(); 
				if (strToString.indexOf('.') > -1) {
					precision--; // don't count decimal point
					if (strToString.startsWith("0.")) {
						precision--; // don't count leading zero
						int pos = 2; // don't count zeros after decimal point
						while (pos < strToString.length() && strToString.charAt(pos++) == '0') {
							precision--;
						}
					}
				}
			}
		}
		
		this.strToString = strToString;
	}
	
	public static MySpecialDouble getEulerConstant(Kernel kernel) {
		if (eulerConstant == null) {
			eulerConstant = new MySpecialDouble(kernel, Math.E, Unicode.EULER_STRING);
		}
		return eulerConstant;
	}
	
	public boolean isEulerConstant() {
		return getDouble() == Math.E;
	}
	
	public String toString() {
		switch (kernel.getCASPrintForm()) {
			//case ExpressionNode.STRING_TYPE_JASYMCA:
			case ExpressionNode.STRING_TYPE_MATH_PIPER:
				char ch = strToString.charAt(0);
				switch (ch) {
					case Unicode.piChar:	return "Pi";
					case Unicode.degreeChar:	return "Pi/180";
				} 	
			break;
			
			case ExpressionNode.STRING_TYPE_MAXIMA:
				ch = strToString.charAt(0);
				switch (ch) {
					case Unicode.piChar:	return "%pi";
					case Unicode.degreeChar:	return "%pi/180";
					case Unicode.eulerChar: return "%e";
				} 	
			break;
			
			case ExpressionNode.STRING_TYPE_MPREDUCE:
				ch = strToString.charAt(0);
				switch (ch) {
					case Unicode.piChar:	return "pi";
					case Unicode.degreeChar:	return "pi/180";
					case Unicode.eulerChar: return "e";
				} 	
			break;
			
			case ExpressionNode.STRING_TYPE_LATEX:
				ch = strToString.charAt(0);
				switch (ch) {
					case Unicode.piChar:	return "\\pi";
					case Unicode.degreeChar:	return "^{\\circ}";
					case Unicode.eulerChar: return Unicode.EULER_STRING; // TODO: find better Latex rendering for "e"
				}
			break;
				
			//default:
			//	return strToString;		
		}
		
		if (isLetterConstant || precision > 16)		
			return strToString;
		else 
			return super.toString();	
	}

}
