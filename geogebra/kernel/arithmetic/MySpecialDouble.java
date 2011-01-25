/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import java.math.BigDecimal;

import geogebra.kernel.Kernel;

/**
 * MyDouble that returns a certain string in toString(). 
 * This is used for example for the degree sign in geogebra.parser.Parser.jj:
 * new MySpecialDouble(kernel, Math.PI / 180.0d,  "\u00b0" );
 * 
 * @author Markus Hohenwarter
 */
public class MySpecialDouble extends MyDouble {
	
	private String strToString;
	private Kernel kernel;
	
	public MySpecialDouble(Kernel kernel, double val, String strToString) {
		super(kernel, val);
		this.kernel = kernel;
		
		// convert scientific notation to plain decimal
		// e.g. 8.571428571428571E-1 to 0.8571428571428571
		if (strToString.indexOf('E') > -1) {
			strToString = new BigDecimal(strToString).toPlainString();
		}
		
		// remove trailing 0s after decimal point
		if (strToString.indexOf('.') > 0) {
			int pos = strToString.length();
			while (strToString.charAt(--pos) == '0');
			if (pos < strToString.length() -1) {
				if (strToString.charAt(pos) == '.') pos--; // remove decimal point too 
				strToString = strToString.substring(0, pos+1);
			}
		}
		
		this.strToString = strToString;
	}
	
	public String toString() {
		switch (kernel.getCASPrintForm()) {
			//case ExpressionNode.STRING_TYPE_JASYMCA:
		case ExpressionNode.STRING_TYPE_MATH_PIPER:
			char ch = strToString.charAt(0);
			switch (ch) {
				// pi
				case '\u03c0':	return "Pi";
				// degree
				case '\u00b0':	return "Pi/180";
			} 	
			
			break;
			
		case ExpressionNode.STRING_TYPE_MAXIMA:
			ch = strToString.charAt(0);
			switch (ch) {
				// pi
				case '\u03c0':	return "%pi";
				// degree
				case '\u00b0':	return "180/%pi";
			} 	
			
			break;
				
			//default:
			//	return strToString;		
		}
		return strToString;	
	}

}
