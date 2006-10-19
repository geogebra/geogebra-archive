package geogebra.kernel;


/**
 * Interface for GeoElements that have a start point (GeoText, GeoVector)
 */
public interface Locateable {
	public void setStartPoint(GeoPoint p) throws CircularDefinitionException;
	public void removeStartPoint(GeoPoint p);
	
	// GeoImage has three startPoints (i.e. corners)
	public void setStartPoint(GeoPoint p, int number) throws CircularDefinitionException;
	
	public GeoPoint getStartPoint();
	public boolean hasAbsoluteLocation();
	
	/**
	 * Use this method to tell the locateable that its
	 * startpoint will be set soon. (This is needed
	 * during XML parsing, as startpoints are processed
	 * at the end of a construction, @see geogebra.io.MyXMLHandler)
	 */
	public void setWaitForStartPoint();
	
	public GeoElement toGeoElement();
}
