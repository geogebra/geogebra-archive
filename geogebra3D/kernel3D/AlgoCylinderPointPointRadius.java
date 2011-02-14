package geogebra3D.kernel3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;

/**
 * @author ggb3D
 *
 */
public class AlgoCylinderPointPointRadius extends AlgoCylinderPointRadius {
	
	/**
	 * @param c construction
	 * @param label 
	 * @param origin 
	 * @param secondPoint 
	 * @param r 
	 */
	public AlgoCylinderPointPointRadius(Construction c, String label, GeoPointND origin, GeoPointND secondPoint, NumberValue r) {
		super(c,label,origin,(GeoElement) secondPoint,r);
	}
	
	protected Coords getDirection(){
		return ((GeoPointND) getSecondInput()).getCoordsInD(3).sub(getOrigin().getCoordsInD(3));
	}
	
    final public String toString() {
    	return app.getPlain("CylinderWithAxisThroughABRadiusC",getOrigin().getLabel(),getSecondInput().getLabel(),getRadius().getLabel());

    }
	

}
