package geogebra.kernel;


/**
 * @author ggb3D
 * 
 * simple interface for Kernel3D : GeoGebra can use it to "pre-load" Kernel3D methods without loading Kernel3D package
 *
 */
public interface KernelInterface {
	
	
	/** Point3D label with cartesian coordinates (x,y,z)   
	 * @param label label of the point
	 * @param x x-coord
	 * @param y y-coord
	 * @param z z-coord
	 * @return a 3D point (using GeoPoint3D class in Kernel3D)*/
	public GeoElement Point3D(String label, double x, double y, double z);

}
