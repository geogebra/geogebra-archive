package geogebra3D.kernel3D;

import geogebra.kernel.Region;
import geogebra3D.Matrix.Ggb3DVector;

/**
 * @author ggb3D
 *
 * extends region interface for getPoint() ability
 */
public interface Region3D extends Region {

	
	/** return the 3D point from (x2d,y2d) 2D coords
	 * @param x2d x-coord
	 * @param y2d y-coord
	 * @return the 3D point
	 */
	public Ggb3DVector getPoint(double x2d, double y2d);

	
	/** return the normal projection of the (coords) point on the region 
	 * @param coords coords of the point
	 * @return normal projection
	 */
	public Ggb3DVector[] getNormalProjection(Ggb3DVector coords);


	/** return the willingDirection projection of the (coords) point on the region 
	 * @param coords coords of the point
	 * @param willingDirection direction of the projection
	 * @return projection
	 */
	public Ggb3DVector[] getProjection(Ggb3DVector coords, Ggb3DVector willingDirection);
	
	

}
