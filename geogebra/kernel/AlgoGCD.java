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
    		
    		// for larger numbers, use CAS
    		if (a.getDouble() == Math.floor(a.getDouble()) && b.getDouble() == Math.floor(b.getDouble()))
    		{       // TODO what shall we do with numbers larger than 2^57?
    				// Gcd[2^58+1,2] and Gcd[2^58,2] currently give the same answer

    			if (sb == null)
    				sb = new StringBuilder();
    			
    			// build MathPiper command
    			sb.setLength(0);
    			sb.append("Gcd(Round(");
    			sb.append(a.getDouble());
    			sb.append("),Round(");
    			sb.append(b.getDouble());
    			sb.append("))");
    			
        		String result=kernel.evaluateMathPiper(sb.toString());
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
