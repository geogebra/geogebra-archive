package geogebra.kernel.kernelND;

import geogebra.Matrix.GgbMatrix;
import geogebra.Matrix.GgbVector;

/**
 * @author mathieu
 *
 * Interface for lines (lines, segments, ray, ...) in any dimension
 */
public interface GeoLineND {
	
	
	/** returns the point at position lambda on the coord sys in the dimension given
	 * @param dimension 
	 * @param lambda 
	 * @return the point at position lambda on the coord sys  
	 * */
	public GgbVector getPointInD(int dimension, double lambda);

	public boolean getTrace();
	
	/**
	 * 
	 * @param m
	 * @return the (a,b,c) equation vector that describe the line
	 * in the plane described by the matrix m
	 * (ie ax+by+c=0 is an equation of the line in the plane)
	 */
	public GgbVector getCartesianEquationVector(GgbMatrix m);
	
	/**
	 * 
	 * @return coords of the starting point
	 */
	public GgbVector getStartInhomCoords();



	/**
	 * @return inhom coords of the end point
	 */
	public GgbVector getEndInhomCoords();

}
