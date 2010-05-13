package geogebra3D.kernel3D;

import geogebra.Matrix.GgbVector;

/** interface for all GeoElements that can be evaluated as (u,v) -> (x,y,z) surfaces
 * @author matthieu
 *
 */
public interface GeoFunction2VarInterface {

	
	/** return point for parameters (u,v)
	 * @param u
	 * @param v
	 * @return point for parameters (u,v)
	 */
	public GgbVector evaluatePoint(double u, double v);
	
	
	/** return normal vector at parameters (u,v)
	 * (return null if none)
	 * @param u
	 * @param v
	 * @return normal vector at parameters (u,v)
	 */
	public GgbVector evaluateNormal(double u, double v);
	
	

	/**
	 * Returns the start parameter value 
	 * @param index of the parameter (0 -> u / 1 -> v)
	 * @return the start parameter value 
	 */
	public double getMinParameter(int index);
	
	/**
	 * Returns the largest possible parameter value 
	 * @param index of the parameter (0 -> u / 1 -> v)
	 * @return the largest possible parameter value 
	 */
	public double getMaxParameter(int index);
	
	
}
