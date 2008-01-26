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

import geogebra.kernel.arithmetic.NumberValue;




/**
 * Computes Div[a, b]
 * @author  Markus Hohenwarter
 * @version 
 */
public class AlgoDiv extends AlgoTwoNumFunction {  
        
    AlgoDiv(Construction cons, String label, NumberValue a, NumberValue b) {       
	  super(cons, label, a, b);     
    }   
  
    String getClassName() {
        return "AlgoDiv";
    }        
    
    // calc area of conic c 
    final void compute() {
    	if (input[0].isDefined() && input[1].isDefined()) {
    		double fraction = a.getDouble() / b.getDouble();
    		double integer = Math.round(fraction);	
    		if (kernel.isEqual(fraction, integer)) {
    			num.setValue(integer);
    		} else {
    			double div = Math.floor(fraction);
        		num.setValue(div);
    		}    		
    	} else
    		num.setUndefined();
    }       
    
}
