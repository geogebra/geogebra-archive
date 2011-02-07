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
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.arithmetic.NumberValue;

import org.apache.commons.math.distribution.ZipfDistribution;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoZipf extends AlgoDistribution {

	private static final long serialVersionUID = 1L;
    
    public AlgoZipf(Construction cons, String label, NumberValue a,NumberValue b, NumberValue c, GeoBoolean isCumulative) {
        super(cons, label, a, b, c, isCumulative);
    }

    public String getClassName() {
        return "AlgoZipf";
    }

	protected final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
		    int param = (int)a.getDouble();
		    double param2 = b.getDouble();
    		    double val = c.getDouble();
        		try {
        			ZipfDistribution dist = getZipfDistribution(param, param2);
        			num.setValue(dist.cumulativeProbability(val));     // P(T <= val)
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
        
    
}



