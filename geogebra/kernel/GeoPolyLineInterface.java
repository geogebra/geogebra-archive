package geogebra.kernel;


public interface GeoPolyLineInterface {
	/**
	 * Returns true iff all vertices are labeled
	 * @return true iff all vertices are labeled
	 */
	public boolean isAllVertexLabelsSet() ;

	/**
	 * Returns true iff number of vertices is not volatile
	 * @return true iff number of vertices is not volatile
	 */
	public boolean isVertexCountFixed() ;
	
	public GeoPoint[] getPoints(); 

}
