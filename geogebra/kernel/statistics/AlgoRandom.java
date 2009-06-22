/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

 */

package geogebra.kernel.statistics;

import geogebra.kernel.AlgoTwoNumFunction;
import geogebra.kernel.Construction;
import geogebra.kernel.arithmetic.NumberValue;

/**
 * Computes RandomNormal[a, b]
 * 
 * @author Michael Borcherds
 * @version
 */
public class AlgoRandom extends AlgoTwoNumFunction {

	public AlgoRandom(Construction cons, String label, NumberValue a,
			NumberValue b) {
		super(cons, label, a, b);

		// output is random number
		cons.addRandomNumber(num);
}

	protected String getClassName() {
		return "AlgoRandom";
	}

	protected final void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {
			num.setValue(random(a.getDouble(), b.getDouble()));
		} else
			num.setUndefined();
		
	}

	private static double random(double a, double b) {
		double min = Math.min(a, b);
		double max = Math.max(a, b);
		return Math.floor(Math.random()*(max - min +1)) + min;

	}

}
