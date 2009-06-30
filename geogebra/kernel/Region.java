package geogebra.kernel;



/**
 * @author Mathieu Blossier
 * 
 * 
 * 
 */


public interface Region {

	
	
	/**
	 * Sets coords of P  when
	 * the coords of P have changed.
	 * Afterwards P lies in this region.
	 * 
	 * 
	 */
	public void pointChangedForRegion(GeoPointInterface P);
	
	
	/**
	 * Sets coords of P 
	 * when this region has changed.
	 * Afterwards P lies in this region.
	 * 
	 *
	 */
	public void regionChanged(GeoPointInterface P);
	
	
	
	/**
	 * Returns true if the given point lies inside this Region.
	 */	
	public boolean isInRegion(GeoPointInterface P);
	
	
	
	/**
	 * Returns this region as an object of type GeoElement.
	 */
	public GeoElement toGeoElement();


	
	
}
