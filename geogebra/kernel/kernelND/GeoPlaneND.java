package geogebra.kernel.kernelND;

/**
 * simple plane interface for all geos that can be considered as a plane (3D plane, polygons, ...)
 * @author mathieu
 *
 */
public interface GeoPlaneND {
	
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

}
