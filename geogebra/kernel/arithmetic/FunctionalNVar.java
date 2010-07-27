/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;



public interface FunctionalNVar {
	public double evaluate(double[] vals);
	public FunctionNVar getFunction();
	//public GeoFunctionNVar getGeoDerivative(int order, int nvar);
}
