/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;


/**
 * upper sum of function f in interval [a, b] with
 * n intervals
 */
public class AlgoSumLower extends AlgoSumUpperLower {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlgoSumLower(Construction cons, String label, GeoFunction f, 
								   NumberValue a, NumberValue b, NumberValue n) {
		super(cons, label, f, a, b, n, false);		
	}
	
	String getClassName() {
		return "AlgoSumLower";
	}
	
}
