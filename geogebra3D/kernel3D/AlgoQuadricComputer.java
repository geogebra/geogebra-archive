package geogebra3D.kernel3D;

import geogebra.Matrix.Coords;

/**
 * Computer for quadric algos, saying if it's a cone, cylinder, etc.
 * 
 * @author mathieu
 *
 */
public abstract class AlgoQuadricComputer {
	

	/**
	 * 
	 * @return the class name of the algo calling
	 */
	abstract public String getClassName() ;
	

	/**
	 * sets the quadric
	 * @param quadric
	 * @param origin
	 * @param direction
	 * @param number
	 */
	abstract public void setQuadric(GeoQuadric3D quadric, Coords origin, Coords direction, double number);


	/**
	 * return Double.NaN if no usable number
	 * @param v
	 * @return usable number for the quadric
	 */
	abstract public double getNumber(double v);

}
