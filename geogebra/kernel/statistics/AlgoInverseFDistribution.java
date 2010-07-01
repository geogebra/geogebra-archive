/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel.statistics;

import geogebra.kernel.Construction;
import geogebra.kernel.arithmetic.NumberValue;

import org.apache.commons.math.distribution.FDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseFDistribution extends AlgoDistribution {

	private static final long serialVersionUID = 1L;
    
    public AlgoInverseFDistribution(Construction cons, String label, NumberValue a,NumberValue b, NumberValue c) {
        super(cons, label, a, b, c, null);
    }

    public String getClassName() {
        return "AlgoInverseFDistribution";
    }

    @SuppressWarnings("deprecation")
	protected final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
		    double param = a.getDouble();
		    double param2 = b.getDouble();
    		    double val = c.getDouble();
        		try {
        			FDistribution dist = getFDistribution(param, param2);
        			num.setValue(dist.inverseCumulativeProbability(val));     // P(T <= val)
        		}
        		catch (Exception e) {
        			e.printStackTrace();
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
        
    
}



