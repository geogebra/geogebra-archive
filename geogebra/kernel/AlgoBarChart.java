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
 * Bar chart algorithm. See AlgoFunctionAreaSums for implementation.
 * @author George Sturr
 *
 */
public class AlgoBarChart extends AlgoFunctionAreaSums {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param a
	 * @param b
	 * @param list1
	 */
	public AlgoBarChart(Construction cons, String label,
			   NumberValue a, NumberValue b, GeoList list1) {
		super(cons, label, a, b, list1);		
	}

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param a
	 */
	public AlgoBarChart(Construction cons, String label,
			GeoList list1, GeoNumeric a) {
		super(cons, label, list1, a);		
	}

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 */
	public AlgoBarChart(Construction cons, String label,
			GeoList list1, GeoList list2) {
		super(cons, label, list1, list2, true); // true = dummy		
	}

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param a
	 */
	public AlgoBarChart(Construction cons, String label,
			GeoList list1, GeoList list2, NumberValue a) {
		super(cons, label, list1, list2, a);
	}

	public String getClassName() {
		return "AlgoBarChart";
	}
	
	public AlgoBarChart copy() {
		switch(this.getType()) {
		case AlgoFunctionAreaSums.TYPE_BARCHART:
			return new AlgoBarChart(this.cons,null,(NumberValue)getA().deepCopy(kernel),
					(NumberValue)getB().deepCopy(kernel),(GeoList)getList1().copy());
		case AlgoFunctionAreaSums.TYPE_BARCHART_FREQUENCY_TABLE:
			return new AlgoBarChart(this.cons,null,(GeoList)getList1().copy(),(GeoList)getList2().copy());
		case AlgoFunctionAreaSums.TYPE_BARCHART_FREQUENCY_TABLE_WIDTH:
			return new AlgoBarChart(this.cons,null,(GeoList)getList1().copy(),(GeoList)getList2().copy(),(NumberValue)getA().deepCopy(kernel));
		default: //TYPE_BARCHART_RAWDATA
			return new AlgoBarChart(this.cons,null,(GeoList)getList1().copy(),(GeoNumeric)getN().copy());	
		}
	}
	
}
