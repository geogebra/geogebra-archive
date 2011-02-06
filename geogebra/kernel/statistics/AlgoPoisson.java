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
import geogebra.main.Application;

import org.apache.commons.math.distribution.PoissonDistribution;



/**
 * 
 * @author G. Sturr
 */

public class AlgoPoisson extends AlgoDistribution {
	
	private static final long serialVersionUID = 1L;

	
	public AlgoPoisson(Construction cons, String label, NumberValue a,NumberValue b, GeoBoolean isCumulative) {
		super(cons, label, a, b, null, isCumulative); 
	}

	public String getClassName() {
		return "AlgoPoisson";
	}

	@SuppressWarnings("deprecation")
	protected final void compute() {


		if (input[0].isDefined() && input[1].isDefined() && input[2].isDefined()) {
			int param = (int)Math.round(a.getDouble());
			double val = b.getDouble();	
			try {
				PoissonDistribution dist = getPoissonDistribution(param);
				if(isCumulative.getBoolean())
					num.setValue(dist.cumulativeProbability(val));  // P(X <= val)
				else
					num.setValue(dist.probability(val));   // P(X = val)
			}
			catch (Exception e) {
				Application.debug(e.getMessage());
				num.setUndefined();        			
			}
		} else
			num.setUndefined();
	}       


}



