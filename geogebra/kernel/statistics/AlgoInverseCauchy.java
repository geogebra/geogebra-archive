/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel.statistics;

import geogebra.kernel.Construction;
import geogebra.kernel.arithmetic.NumberValue;

import org.apache.commons.math.distribution.CauchyDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoInverseCauchy extends AlgoDistribution {

	private static final long serialVersionUID = 1L;
    
    public AlgoInverseCauchy(Construction cons, String label, NumberValue a,NumberValue b, NumberValue c) {
        super(cons, label, a, b, c, null);
    }

    protected String getClassName() {
        return "AlgoInverseCauchy";
    }

    @SuppressWarnings("deprecation")
	protected final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
		    double param = a.getDouble();
		    double param2 = b.getDouble();
    		    double val = c.getDouble();
        		try {
        			CauchyDistribution dist = getCauchyDistribution(param, param2);
        			num.setValue(dist.inverseCumulativeProbability(val));     
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
        
    
}



