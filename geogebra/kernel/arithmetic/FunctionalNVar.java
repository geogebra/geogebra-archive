/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import java.util.List;

/**
 * Interface for GeoFunction and GeoFunctionNVar
 * 
 * @author Markus
 *
 */
public interface FunctionalNVar {
	/**
	 * @param vals
	 * @return value at vals
	 */
	public double evaluate(double[] vals);
	/**
	 * @return function
	 */
	public FunctionNVar getFunction();
	/**
	 * @return list of inequalities
	 */
	public List<Inequality> getIneqs();
	
	/**
	 * Returns true iff the function is boolean
	 * @return true iff the function is boolean
	 */
	public boolean isBooleanFunction();
	//public GeoFunctionNVar getGeoDerivative(int order, int nvar);
}
