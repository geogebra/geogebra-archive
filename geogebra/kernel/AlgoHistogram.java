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
 * Histogram algorithm. See AlgoFunctionAreaSums for implementation.
 * 
 * @author M. Borcherds
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
	

	/**
	 * Creates histogram with density scaling factor 
	 * @param cons construction
	 * @param label label for the histogram
	 * @param list1 list of boundaries
	 * @param list2 list of heights or raw data
	 * @param useDensity flag  
	 * @param density density scaling factor 
	 */
	public AlgoHistogram(Construction cons, String label,
			GeoBoolean isCumulative,					   
			GeoList list1, 
			GeoList list2, 
			GeoBoolean useDensity, 
			GeoNumeric density) {
		super(cons, label, isCumulative, list1, list2, useDensity, density);		
	}
	
	
	public String getClassName() {
		return "AlgoHistogram";
	}
	
	public AlgoHistogram copy() {
		return new AlgoHistogram(this.cons,null,(GeoList)this.getList1().copy(),(GeoList)this.getList2().copy());
	}
	
}
