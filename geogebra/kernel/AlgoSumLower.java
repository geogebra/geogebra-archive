/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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
public class AlgoSumLower extends AlgoFunctionAreaSums {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlgoSumLower(Construction cons, String label, GeoFunction f, 
								   NumberValue a, NumberValue b, NumberValue n) {
		super(cons, label, f, a, b, n, AlgoFunctionAreaSums.TYPE_LOWERSUM);		
		cons.registerEuclidianViewAlgo(this);
	}
	
	public String getClassName() {
		return "AlgoSumLower";
	}
	
}
