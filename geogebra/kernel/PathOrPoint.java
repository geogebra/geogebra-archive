package geogebra.kernel;

import geogebra.kernel.kernelND.GeoPointND;

/*
 * PathOrPoint needed as well as Path so that points can be elements of compound paths
 * eg {(2,3), (4,5), Segment[(6,7),(8,9)] }
 * see GeoList.pointChanged()
 */

public interface PathOrPoint {
	
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
	public void pointChanged(GeoPointND PI);
	
	/**
	 * Sets coords of P and its path parameter
	 * when this path has changed.
	 * Afterwards P lies on this path.
	 * 
	 * Note: P.setCoords() is not called!
	 */
	public void pathChanged(GeoPointND PI);
	
	/**
	 * Returns true iff the given point lies on this path.
	 */	
	public boolean isOnPath(GeoPointND PI, double eps);
	
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
