package geogebra.kernel.kernelND;

import geogebra.Matrix.Coords;

/**
 * geos that have a direction (lines, vectors, planes, ...)
 * @author mathieu
 *
 */
public interface GeoDirectionND {
	
	/**
	 * 
	 * @return the direction in 3D
	 */
	public Coords getDirectionInD3();

}
