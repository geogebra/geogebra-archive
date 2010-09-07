/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * Histogram algorithm. See AlgoFunctionAreaSums for implementation.
 * 
 * @author George Sturr
 *
 */
public class AlgoHistogram extends AlgoFunctionAreaSums {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates histogram
	 * @param cons construction
	 * @param label label for the histogram
	 * @param list1 list of boundaries
	 * @param list2 list of heights or raw data
	 */
	public AlgoHistogram(Construction cons, String label,
								   GeoList list1, GeoList list2) {
		super(cons, label, list1, list2);		
	}
	
	public String getClassName() {
		return "AlgoHistogram";
	}
	
	public AlgoHistogram copy() {
		return new AlgoHistogram(this.cons,null,(GeoList)this.getList1().copy(),(GeoList)this.getList2().copy());
	}
	
}
