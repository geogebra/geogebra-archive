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
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.Path;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;

/**
 * @author Markus Hohenwarter + ggb3D
 */
public interface Path3D extends Path {
	

	

	/**
	 * Returns the 2D GeoElement Path linked with
	 */
	public Path getPath2D();

	
	/**
	 * Returns a PathMover object for this path.
	 */
	//public PathMover createPathMover();	
	
	
	
	/** returns matrix describing a plane in the real world, 
	 * for moving the point, according to the screen view 
	 * (for using mouse moving)
	 * */
	public Ggb3DMatrix4x4 getMovingMatrix(Ggb3DMatrix4x4 toScreenMatrix);
	
}
