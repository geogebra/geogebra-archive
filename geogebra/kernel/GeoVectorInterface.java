package geogebra.kernel;

/**
 * Simple common interface for GeoVector and GeoVector3D
 * 
 * @author ggb3D
 *
 */
public interface GeoVectorInterface {

	void setLabel(String label);

	void setStartPoint(GeoPointInterface p) throws CircularDefinitionException;

	GeoPointInterface getStartPoint();

	void setUndefined();
	
	public void setCoords(double[] c);

}
