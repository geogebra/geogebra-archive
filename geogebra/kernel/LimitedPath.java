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

import geogebra.kernel.arithmetic.NumberValue;

public interface LimitedPath extends Path {
	
	/**
	 * Returns whether intersection points with this
	 * path are allowed
	 * that lie on the extension of this path.
	 */
	public boolean allowOutlyingIntersections();
	
	/**
	 * Sets whether intersection points with this
	 * path are allowed
	 * that lie on the extension of this path.
	 */
	public void setAllowOutlyingIntersections(boolean flag);
	
	/**
	 * Returns true iff the intersection point P lies on 
	 * this limited path.
	 * @param P
	 * @param eps: epsilon precision for testing
	 */
	public boolean isIntersectionPointIncident(GeoPoint P, double eps);
	
	/**
	 * Returns whether a geometric transform of this
	 * path should yield an object of the same kind
	 * (i.e. segment becomes segment).
	 */
	public boolean keepsTypeOnGeometricTransform();
			
	/**
	 * Sets whether a geometric transform of this
	 * path should yield an object of the same kind
	 * (i.e. segment becomes segment).
	 */
	public void setKeepTypeOnGeometricTransform(boolean flag);
	
	/**
	 * Creates a new object using the geometrical transform of the given type.
	 * @param type: one of Kernel.TRANSFORM_...
	 */
	public GeoElement [] createTransformedObject(int type, String label, GeoPoint Q, GeoLine l, GeoVector v, NumberValue n);
}
