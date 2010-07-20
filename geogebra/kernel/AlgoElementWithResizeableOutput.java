package geogebra.kernel;

/**
 * AlgoElement implementing this have output that can be resizeable
 * 
 * @author mathieu
 *
 */
public interface AlgoElementWithResizeableOutput {
	
	/**
	 * add the geo to output list.
	 * If needed, only the label is taken for an already computed geo.
	 * @param geo
	 * @return geo, possibly already computed by the algo
	 */
	public GeoElement addToOutput(GeoElement geo, boolean computedGeo);

}
