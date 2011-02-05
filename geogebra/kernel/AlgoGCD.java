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
    		
    		// 2^52
    		if (a.getDouble() > 4503599627370496d || b.getDouble() > 4503599627370496d) {
    			num.setUndefined();
    			return;
    		}
    		
    		// use algorithm in ints first
    		boolean success = false;
    		int aInt = (int)Math.abs(a.getDouble());
    		int bInt = (int)Math.abs(b.getDouble());
    		if (aInt < Integer.MAX_VALUE &&  bInt < Integer.MAX_VALUE) { // ensure answer will fit in an int
    			try {
    				num.setValue(MyMath.gcd(aInt, bInt));
    				success = true;
    			} catch (ArithmeticException e) {
    				e.printStackTrace();
    			}
    			if (success) return;
    		}
    		
    		// for larger numbers, use BigInteger
    		if (a.getDouble() == Math.floor(a.getDouble()) && b.getDouble() == Math.floor(b.getDouble()))
    		{  
    			BigInteger i1 = BigInteger.valueOf((long)a.getDouble());
    			BigInteger i2 = BigInteger.valueOf((long)b.getDouble());
    			
    			i1 = i1.gcd(i2);
    			
    			num.setValue(i1.doubleValue());
    		} else {
    			num.setUndefined();
    		}
    			
    	} else
    		num.setUndefined();
    }       
    
}
