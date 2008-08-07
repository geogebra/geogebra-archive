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

public class AlgoHistogram extends AlgoSumUpperLower {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlgoHistogram(Construction cons, String label,
								   NumberValue a, NumberValue b, GeoList list1, GeoList list2) {
		super(cons, label, a, b, list1, list2);		
	}
	
	protected String getClassName() {
		return "AlgoHistogram";
	}
	
}
