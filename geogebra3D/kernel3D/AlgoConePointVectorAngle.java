package geogebra3D.kernel3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoVectorND;

/**
 * @author ggb3D
 *
 */
public class AlgoConePointVectorAngle extends AlgoConePointAngle {
	
	/**
	 * @param c construction
	 * @param label 
	 * @param origin 
	 * @param secondPoint 
	 * @param angle 
	 */
	public AlgoConePointVectorAngle(Construction c, String label, GeoPointND origin, GeoVectorND direction, NumberValue angle) {
		super(c,label,origin,(GeoElement) direction,angle);
	}
	
	protected Coords getDirection(){
		return ((GeoVectorND) getSecondInput()).getCoordsInD(3);
	}
	
    final public String toString() {
    	return app.getPlain("ConeWithCenterAAxisParallelToBAngleC",getOrigin().getLabel(),getSecondInput().getLabel(),getAngle().getLabel());

    }
	

}
