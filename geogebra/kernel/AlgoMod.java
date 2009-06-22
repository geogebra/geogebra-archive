/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;




/**
 * Computes Mod[a, b]
 * @author  Markus Hohenwarter
 * @version 
 */
public class AlgoMod extends AlgoTwoNumFunction {

      
    AlgoMod(Construction cons, String label, NumberValue a, NumberValue b) {       
	  super(cons, label, a, b);     
    }   
  
    protected String getClassName() {
        return "AlgoMod";
    }       
    
    protected final void compute() {
    	if (input[0].isDefined() && input[1].isDefined()) {
    		
    		double mod = Math.round(a.getDouble());
    		double bInt = Math.abs(Math.round(b.getDouble()));
    		
    		if (Math.abs(mod) > MyDouble.LARGEST_INTEGER || bInt > MyDouble.LARGEST_INTEGER) {
    			num.setUndefined();
    			return;
    		}
    		
    		mod = mod % bInt;

    		if (mod < 0) mod += bInt; // bugfix Michael Borcherds 2008-08-07
    		
    		num.setValue(mod);
    		
    	} else
    		num.setUndefined();
    }       
    
}
