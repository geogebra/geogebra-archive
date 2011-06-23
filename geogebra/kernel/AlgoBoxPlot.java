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
 * Boxplot algorithm. See AlgoFunctionAreaSums for implementation.
 * 
 * @author George Sturr
 *
 */
public class AlgoBoxPlot extends AlgoFunctionAreaSums {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates boxplot given all the quartiles, y-offset and y-scale
	 * @param cons construction
	 * @param label label
	 * @param a y-offset
	 * @param b y-scale
	 * @param min
	 * @param Q1
	 * @param median
	 * @param Q3
	 * @param max
	 */
	public AlgoBoxPlot(Construction cons, String label,
			NumberValue a, NumberValue b, NumberValue min, NumberValue Q1,
			NumberValue median, NumberValue Q3, NumberValue max) {
		super(cons, label, min, Q1, median, Q3, max, a, b);		
	}
	
	/**
	 * Creates boxplot from list of raw data
	 * @param cons construction
	 * @param label label
	 * @param a y-offset
	 * @param b y-scale
	 * @param rawData
	 */
	public AlgoBoxPlot(Construction cons, String label,
			NumberValue a, NumberValue b, GeoList rawData) {
		super(cons, label, rawData, a, b);		
	}
	
	public AlgoBoxPlot(NumberValue a, NumberValue b, GeoList list1) {
		super(list1,a,b);
	}

	public AlgoBoxPlot(NumberValue a, NumberValue b,
			NumberValue min, NumberValue q1, NumberValue q2,
			NumberValue q3, NumberValue max) {
		super(a,b,min,q1,q2,q3,max);
	}

	public String getClassName() {
		return "AlgoBoxPlot";
	}
	
	public AlgoBoxPlot copy(){
		if(this.getType() == AlgoFunctionAreaSums.TYPE_BOXPLOT_RAWDATA)
		return new AlgoBoxPlot((NumberValue)this.getA().deepCopy(kernel),
				(NumberValue)this.getB().deepCopy(kernel),(GeoList)this.getList1().copy());
		else
			return new AlgoBoxPlot((NumberValue)this.getA().deepCopy(kernel),
					(NumberValue)this.getB().deepCopy(kernel),(NumberValue)this.getMinGeo().copy().evaluate(),
					(NumberValue)this.getQ1geo().copy().evaluate(),(NumberValue)this.getMedianGeo().copy().evaluate(),
					(NumberValue)this.getQ3geo().copy().evaluate(),(NumberValue)this.getMaxGeo().copy().evaluate());
	}
	
}
