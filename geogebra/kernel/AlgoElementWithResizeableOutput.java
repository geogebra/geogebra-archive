package geogebra.kernel;

/**
 * AlgoElement implementing this have output that can be resizeable
 * 
 * @author mathieu
 *
 */
public interface AlgoElementWithResizeableOutput {
	
	
	/**
	 * add new label to output list, regarding the type of geo
	 * @param label
	 * @param type 
	 * @return geo, possibly already computed by the algo
	 */
	public GeoElement addLabelToOutput(String label, int type);

}
