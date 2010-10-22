package geogebra.kernel.kernelND;

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

}
