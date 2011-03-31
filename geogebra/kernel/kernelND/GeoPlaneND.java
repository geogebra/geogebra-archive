package geogebra.kernel.kernelND;

import geogebra.Matrix.Coords;
import geogebra.kernel.Region;

/**
 * simple plane interface for all geos that can be considered as a plane (3D plane, polygons, ...)
 * @author mathieu
 *
 */
public interface GeoPlaneND extends Region{
	
	/**
	 * sets the fading for the "ends" of the plane 
	 * @param fading
	 */
	public void setFading(float fading);
	
	/**
	 * 
	 * @return the fading for the "ends" of the plane 
	 */
	public float getFading();
	
	/**
	 * create a 2D view of this plane
	 */
	public void createView2D();
	
	
	public Coords[] getNormalProjection(Coords coords);

}
