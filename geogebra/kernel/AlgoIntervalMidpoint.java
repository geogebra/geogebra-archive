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

public class AlgoIntervalMidpoint extends AlgoIntervalAbstract {


	public AlgoIntervalMidpoint(Construction cons, String label, GeoInterval s) {
		super(cons, label, s);
	}


	public String getClassName() {
        return "AlgoIntervalMidpoint";
    }


    protected final void compute() {
    	
    	result.setValue(interval.getMidPoint());
    }
    
}
