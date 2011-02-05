/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import java.math.BigInteger;

import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import geogebra.util.MyMath;

/**
 * Computes LCM[a, b]
 * @author  Michael Borcherds
 * @version 
 */
public class AlgoLCM extends AlgoTwoNumFunction {        
	
	private StringBuilder sb;
        
    AlgoLCM(Construction cons, String label, NumberValue a, NumberValue b) {       
	  super(cons, label, a, b); 
    }   
  
    public String getClassName() {
        return "AlgoLCM";
    }
      
    protected final void compute() {
    	if (input[0].isDefined() && input[1].isDefined()) {
    		
    		// use algorithm in ints first
    		boolean success = false;
    		int aInt = (int)Math.abs(a.getDouble());
    		int bInt = (int)Math.abs(b.getDouble());
    		if (aInt < 32768 &&  bInt < 32768) { // ensure answer will fit in an int
    			try {
    				num.setValue(MyMath.lcm(aInt, bInt));
    	    		success = true;
    			} catch (ArithmeticException e) {
    				e.printStackTrace();
    			}
    			if (success) return;
    		}
    		
    		if (a.getDouble() > Integer.MAX_VALUE || b.getDouble() > Integer.MAX_VALUE) {
    			num.setUndefined();
    			return;
    		}

    		
    		// for larger numbers, use BigInteger
    		if (a.getDouble()==Math.floor(a.getDouble()) && b.getDouble()==Math.floor(b.getDouble()))
    		{   
    			BigInteger i1 = BigInteger.valueOf((long)a.getDouble());
    			BigInteger i2 = BigInteger.valueOf((long)b.getDouble());
    			
    			BigInteger gcd = i1.gcd(i2);
    			
    			i1 = i1.divide(gcd);
    			i2 = i2.multiply(i1); // this is LCM
    			
    			num.setValue(i2.doubleValue());
    		}
    		else
    		{ // not integers
    			num.setUndefined();
    		}
    	} else
    		num.setUndefined();
    }       
    
}
