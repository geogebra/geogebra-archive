package geogebra.kernel;

import geogebra.kernel.kernelND.GeoPointND;

/**
 * Simple common interface for GeoVector and GeoVector3D
 * 
 * @author ggb3D
 *
 */
public interface GeoVectorInterface {

	void setLabel(String label);

	void setStartPoint(GeoPointND p) throws CircularDefinitionException;

	GeoPointND getStartPoint();

	void setUndefined();
	
	public void setCoords(double[] c);

}
