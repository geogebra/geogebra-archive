/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.statistics;

import geogebra.kernel.AlgoDrawInformation;
import geogebra.kernel.AlgoFunctionAreaSums;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.arithmetic.NumberValue;

/**
 * @author G. Sturr
 * @version 2011-06-21
 */

public class AlgoPoissonBarChart extends AlgoFunctionAreaSums {

	private static final long serialVersionUID = 1L;

	public AlgoPoissonBarChart(Construction cons, String label, 
			NumberValue mean) {
        super(cons,label, mean, null, null, null, AlgoFunctionAreaSums.TYPE_BARCHART_POISSON);
    }
	
	
	public AlgoPoissonBarChart(Construction cons, String label, 
			NumberValue mean, GeoBoolean isCumulative) {
        super(cons,label, mean, null, null, isCumulative, AlgoFunctionAreaSums.TYPE_BARCHART_POISSON);
    }
	

    public String getClassName() {
        return "AlgoPoissonBarChart";
    }

	public AlgoDrawInformation copy() {
		// TODO Auto-generated method stub
		return null;
	}
}

