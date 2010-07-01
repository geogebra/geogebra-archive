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

public class AlgoBoxPlot extends AlgoFunctionAreaSums {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlgoBoxPlot(Construction cons, String label,
			NumberValue a, NumberValue b, NumberValue min, NumberValue Q1,
			NumberValue median, NumberValue Q3, NumberValue max) {
		super(cons, label, min, Q1, median, Q3, max, a, b);		
	}
	
	public AlgoBoxPlot(Construction cons, String label,
			NumberValue a, NumberValue b, GeoList rawData) {
		super(cons, label, rawData, a, b);		
	}
	
	public String getClassName() {
		return "AlgoBoxPlot";
	}
	
}
