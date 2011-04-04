package geogebra.kernel.kernelND;

import geogebra.Matrix.CoordMatrix;
import geogebra.Matrix.Coords;

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
	public Coords getPointInD(int dimension, double lambda);

	public boolean getTrace();
	
	/**
	 * 
	 * @param m
	 * @return the (a,b,c) equation vector that describe the line
	 * in the plane described by the matrix m
	 * (ie ax+by+c=0 is an equation of the line in the plane)
	 */
	public Coords getCartesianEquationVector(CoordMatrix m);
	
	/**
	 * 
	 * @param dimension 
	 * @return coords of the starting point
	 */
	public Coords getStartInhomCoords();



	/**
	 * @return inhom coords of the end point
	 */
	public Coords getEndInhomCoords();
	
	/** see PathOrPoint 
	 * @return min parameter */
	public double getMinParameter();
	
	/** see PathOrPoint 
	 * @return max parameter */
	public double getMaxParameter();

}
