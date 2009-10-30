/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

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
		this.strToString = strToString;
	}
	
	public String toString() {
//		switch (kernel.getCASPrintForm()) {
//			//case ExpressionNode.STRING_TYPE_JASYMCA:
//			case ExpressionNode.STRING_TYPE_MATH_PIPER:
//				char ch = strToString.charAt(0);
//				switch (ch) {
//					// pi
//					case '\u03c0':	return "Pi";
//					// degree
//					case '\u00b0':	return "180/Pi";
//				} 				
//				
//			default:
//				return strToString;		
//		}

		return strToString;
	}

}
