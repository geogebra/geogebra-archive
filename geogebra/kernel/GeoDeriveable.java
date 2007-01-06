/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
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
