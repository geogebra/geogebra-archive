/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.statistics;

import geogebra.kernel.Construction;
import geogebra.kernel.arithmetic.NumberValue;

import org.apache.commons.math.distribution.PoissonDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInversePoisson extends AlgoDistribution {

	private static final long serialVersionUID = 1L;
    
    public AlgoInversePoisson(Construction cons, String label, NumberValue a,NumberValue b) {
        super(cons, label, a, b, null, null);
    }

    public String getClassName() {
        return "AlgoInversePoisson";
    }

    @SuppressWarnings("deprecation")
	protected final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined()) {
		    int param = (int)Math.round(a.getDouble());
		    double param2 = b.getDouble();
    		    double val = b.getDouble();
        		try {
        			PoissonDistribution dist = getPoissonDistribution(param);
        			
        			double result = dist.inverseCumulativeProbability(val);
        			
        			// eg InversePascal[1,1,1] returns  2147483647 
        			if (result >= Integer.MAX_VALUE )
        				num.setUndefined();
        			else
        				num.setValue(result);    
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
        
    
}



