/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

/**
 * Computes GCD[a, b]
 * @author  Michael Borcherds
 * @version 
 */
public class AlgoGCD extends AlgoTwoNumFunction {         
        
    AlgoGCD(Construction cons, String label, NumberValue a, NumberValue b) {       
	  super(cons, label, a, b); 
    }   
  
    protected String getClassName() {
        return "AlgoGCD";
    }
      
    final void compute() {
    	if (input[0].isDefined() && input[1].isDefined()) {
    		if (a.getDouble()==Math.floor(a.getDouble()) && b.getDouble()==Math.floor(b.getDouble()))
    		{       // TODO what shall we do with numbers larger than 2^57?
    				// Gcd[2^58+1,2] and Gcd[2^58,2] currently give the same answer

    			String aa=Double.toString(a.getDouble());
    			String bb=Double.toString(b.getDouble());
    			String yacasCommand="Gcd(Round("+aa+"),Round("+bb+"))";
        		String result=kernel.evaluateYACAS(yacasCommand);
        		try {
            		double gcd = Double.valueOf(result).doubleValue();
            		num.setValue(gcd);
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    		}
    		else
    		{ // not integers
    			num.setUndefined();
    		}
    	} else
    		num.setUndefined();
    }       
    
}
