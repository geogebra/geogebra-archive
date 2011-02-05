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
import geogebra.util.MyMath;

import java.math.BigInteger;

/**
 * Computes GCD[a, b]
 * @author  Michael Borcherds
 * @version 
 */
public class AlgoGCD extends AlgoTwoNumFunction {  
	
	private StringBuilder sb;
        
    AlgoGCD(Construction cons, String label, NumberValue a, NumberValue b) {       
	  super(cons, label, a, b); 
    }   
  
    public String getClassName() {
        return "AlgoGCD";
    }
      
    protected final void compute() {
    	if (input[0].isDefined() && input[1].isDefined()) {
    		
    		if (a.getDouble() > Long.MAX_VALUE || b.getDouble() > Long.MAX_VALUE || 
    				a.getDouble() < -Long.MAX_VALUE || b.getDouble() < -Long.MAX_VALUE) {
    			num.setUndefined();
    			return;
    		}
    		
    		if (a.getDouble() == Math.floor(a.getDouble()) && b.getDouble() == Math.floor(b.getDouble()))
    		{  
    			BigInteger i1 = BigInteger.valueOf((long)a.getDouble());
    			BigInteger i2 = BigInteger.valueOf((long)b.getDouble());
    			
    			i1 = i1.gcd(i2);
    			
    			double result = Math.abs(i1.doubleValue());
    			
    	    	// can't store integers greater than this in a double accurately
    	    	if (result > 1e15) {
    	    		num.setUndefined();
    	    		return;
    	    	}
   			
    			num.setValue(result);
    		} else {
    			num.setUndefined();
    		}
    			
    	} else
    		num.setUndefined();
    }       
    
}
