package geogebra.kernel;

/**
 * Algorithms for transformations 
 */
public abstract class AlgoTransformation extends AlgoElement {

	/**
	 * Create new transformation algo
	 * @param c
	 */
	public AlgoTransformation(Construction c) {
        super(c);  	
	}		
	
	/**
     * Returns the resulting GeoElement
     * @return the resulting GeoElement
     */
	abstract GeoElement getResult();
	 
}
