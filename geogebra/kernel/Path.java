/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * @author Markus Hohenwarter
 */
public interface Path {
	
	/*
	static final int PATH_LINE = 0;
	static final int PATH_SEGMENT = 1;
	static final int PATH_ELLIPSE = 10;
	static final int PATH_HYPERBOLA = 11;
	static final int PATH_PARABOLA = 12;
	static final int PATH_POINT = 13;
	static final int PATH_EMPTY = 14;
	static final int PATH_FUNCTION = 20;
	*/
	
	/**
	 * Sets coords of P and its path parameter when
	 * the coords of P have changed.
	 * Afterwards P lies on this path.
	 * 
	 * Note: P.setCoords() is not called!
	 */
	public void pointChanged(GeoPointInterface PI);
	
	/**
	 * Sets coords of P and its path parameter
	 * when this path has changed.
	 * Afterwards P lies on this path.
	 * 
	 * Note: P.setCoords() is not called!
	 */
	public void pathChanged(GeoPointInterface PI);
	
	/**
	 * Returns true iff the given point lies on this path.
	 */	
	public boolean isOnPath(GeoPointInterface PI, double eps);
	
	/**
	 * Returns this path as an object of type GeoElement.
	 */
	public GeoElement toGeoElement();
	
	/**
	 * Returns the smallest possible parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 */
	public double getMinParameter();
	
	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 */
	public double getMaxParameter();
	
	/**
	 * Returns whether this path is closed (i.e. its
	 * first and last point are equal).	
	 */
	public boolean isClosedPath();
	
	/**
	 * Returns a PathMover object for this path.
	 */
	public PathMover createPathMover();	
	
}
