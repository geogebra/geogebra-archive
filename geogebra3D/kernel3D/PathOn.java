/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Path;
import geogebra3D.Matrix.Ggb3DMatrix;

/**
 * @author Markus Hohenwarter + ggb3D
 */
public interface PathOn {
	

	
	/**
	 * Sets coords of P and its path parameter when
	 * the coords of P have changed.
	 * Afterwards P lies on this path.
	 * 
	 * Note: P.setCoords() is not called!
	 */
	public void pointChanged(GeoPoint3D P);
	
	/**
	 * Sets coords of P and its path parameter
	 * when this path has changed.
	 * Afterwards P lies on this path.
	 * 
	 * Note: P.setCoords() is not called!
	 */
	public void pathChanged(GeoPoint3D P);
	
	/**
	 * Returns true if the given point lies on this path.
	 */	
	public boolean isOnPath(GeoPoint3D P, double eps);
	
	/**
	 * Returns this path as an object of type GeoElement.
	 */
	public GeoElement toGeoElement();
	

	/**
	 * Returns the GeoElement Path linked with
	 */
	public Path getPath2D();

	
	/**
	 * Returns a PathMover object for this path.
	 */
	//public PathMover createPathMover();	
	
	
	
	/** returns matrix for moving the point in the screen view */
	public Ggb3DMatrix getMovingMatrix(Ggb3DMatrix toScreenMatrix);
	
}
