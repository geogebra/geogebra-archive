/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;

import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.Path;
import geogebra3D.Matrix.Ggb3DMatrix4x4;

/**
 * Class extending Path :
 * <ul>
 * <li> adding {@link #getPath2D()} method for 3D Path linked to a 2D Path </li>
 * <li> adding {@link #getMovingMatrix(Ggb3DMatrix4x4)} method that provides a plane for moving a point with the mouse </li>
 * </ul>
 * 
 * @author Markus Hohenwarter + ggb3D
 */
public interface Path3D extends Path {
	

	
	
	
	
	

	/**
	 * Returns the 2D GeoElement Path linked with
	 * @return the 2D GeoElement Path linked with
	 */
	//public Path getPath2D();

	
	
	
	
	/** returns matrix describing a plane in the real world, 
	 * for moving the point, according to the screen view 
	 * (for using mouse moving)
	 * @param toScreenMatrix screen view
	 * @return matrix describing a plane in the real world
	 * */
	//public Ggb3DMatrix4x4 getMovingMatrix(Ggb3DMatrix4x4 toScreenMatrix);
	
}
