/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;


/**
 * upper sum of function f in interval [a, b] with
 * n intervals
 */
public class AlgoSumUpper extends AlgoSumUpperLower {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlgoSumUpper(Construction cons, String label, GeoFunction f, 
								   NumberValue a, NumberValue b, NumberValue n) {
		super(cons, label, f, a, b, n, true);		
	}
	
	protected String getClassName() {
		return "AlgoSumUpper";
	}
	
}
