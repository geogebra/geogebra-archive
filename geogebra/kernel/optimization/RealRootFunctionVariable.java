/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.
*/

package geogebra.kernel.optimization;

import geogebra.kernel.roots.RealRootFunction;
import geogebra.kernel.GeoNumeric;


/**
 * RealRootFunctionVariable
 * 
 * Presents the relationship <dependent variable> <-- <independent variable>
 * as a "function", so that ExtrFinder can use it as a function and find
 * the value of the independent variable when the dependent variable is 
 * maximum or minimum.
 * 
 * Used by the command searchMaximum[ <dependent variable>, <independent variable> ]
 * in kernel.AlgoSearchMaximum.  (Also a minimum version...)
 * 
 * @author  	Hans-Petter Ulven
 * @version 	10.02.2011
 */
public class RealRootFunctionVariable implements RealRootFunction {
	
	private GeoNumeric geodep	=	null;				//dependent variable
	private GeoNumeric geoindep	=	null;				//independent variable
	
	/**
	 * Constructor
	 * @param		geodep
	 * @param		geoindep
	 */
	public RealRootFunctionVariable(GeoNumeric geodep,GeoNumeric geoindep) {
		this.geodep=geodep;
		this.geoindep=geoindep;
	}//Constructor
	

	public double evaluate(double x) {
		if( (geodep!=null) && (geoindep!=null) ){
			geoindep.setValue(x);
			geoindep.updateCascade();
			return geodep.getValue();
		}else{
			return Double.NaN;
		}//if variables are ok
	}//evaluate(double)
	

	
}//class RealRootFunctionVariable


