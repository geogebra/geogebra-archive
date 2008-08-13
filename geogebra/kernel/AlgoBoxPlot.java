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

public class AlgoBoxPlot extends AlgoFunctionAreaSums {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlgoBoxPlot(Construction cons, String label,
								   NumberValue a, GeoList list1) {
		super(cons, label,  list1, a);		
	}
	
	protected String getClassName() {
		return "AlgoBoxPlot";
	}
	
}
