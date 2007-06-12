/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

/**
 * Computes Max[a, b]
 * @author  Markus Hohenwarter
 * @version 
 */
public class AlgoMax extends AlgoTwoNumFunction {         
        
    AlgoMax(Construction cons, String label, NumberValue a, NumberValue b) {       
	  super(cons, label, a, b); 
    }   
  
    String getClassName() {
        return "AlgoMax";
    }
      
    final void compute() {
    	if (input[0].isDefined() && input[1].isDefined()) {
    		double max = Math.max(a.getDouble(), b.getDouble());
    		num.setValue(max);
    	} else
    		num.setUndefined();
    }       
    
}
