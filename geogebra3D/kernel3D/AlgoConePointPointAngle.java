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
public class AlgoConePointPointAngle extends AlgoConePointAngle {
	
	/**
	 * @param c construction
	 * @param label 
	 * @param origin 
	 * @param secondPoint 
	 * @param angle 
	 */
	public AlgoConePointPointAngle(Construction c, String label, GeoPointND origin, GeoPointND secondPoint, NumberValue angle) {
		super(c,label,origin,(GeoElement) secondPoint,angle);
	}
	
	protected Coords getDirection(){
		return ((GeoPointND) getSecondInput()).getCoordsInD(3).sub(getOrigin().getCoordsInD(3));
	}
	
    final public String toString() {
    	return app.getPlain("ConeWithCenterAAxisThroughBAngleC",getOrigin().getLabel(),getSecondInput().getLabel(),getAngle().getLabel());

    }
	

}
