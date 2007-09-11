/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * Interface to unify object types that offer a derivative
 * like GeoFunction and GeoCurveCartesian.
 * @author Markus 
 */
public interface GeoDeriveable {
	
	/**
	 * Sets this object to the n-th derivative of f.
	 */
	public void setDerivative(GeoDeriveable f, int n);
		
	public String toSymbolicString();
	public String getVarString();
	public GeoElement toGeoElement();
}
