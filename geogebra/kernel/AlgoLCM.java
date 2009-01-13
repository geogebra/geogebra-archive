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
 * Computes LCM[a, b]
 * @author  Michael Borcherds
 * @version 
 */
public class AlgoLCM extends AlgoTwoNumFunction {        
	
	private StringBuffer sb = new StringBuffer();
        
    AlgoLCM(Construction cons, String label, NumberValue a, NumberValue b) {       
	  super(cons, label, a, b); 
    }   
  
    protected String getClassName() {
        return "AlgoLCM";
    }
      
    protected final void compute() {
    	if (input[0].isDefined() && input[1].isDefined()) {
    		if (a.getDouble()==Math.floor(a.getDouble()) && b.getDouble()==Math.floor(b.getDouble()))
    		{       // TODO what shall we do with numbers larger than 2^57?
    				// Lcm[2^58+1,2] and Lcm[2^58,2] currently give the same answer
	
    			// build MathPiper command
    			sb.setLength(0);
    			sb.append("Lcm(Round(");
    			sb.append(a.getDouble());
    			sb.append("),Round(");
    			sb.append(b.getDouble());
    			sb.append("))");
    			
        		String result=kernel.evaluateMathPiper(sb.toString());
        		try {
            		double lcm = Double.valueOf(result).doubleValue();
            		num.setValue(lcm);
        			
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
