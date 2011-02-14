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
public class AlgoCylinderPointVectorRadius extends AlgoCylinderPointRadius {
	
	/**
	 * @param c construction
	 * @param label 
	 * @param origin 
	 * @param direction 
	 * @param r 
	 */
	public AlgoCylinderPointVectorRadius(Construction c, String label, GeoPointND origin, GeoVectorND direction, NumberValue r) {
		super(c,label,origin,(GeoElement) direction, r);
	}
	
	protected Coords getDirection(){
		return ((GeoVectorND) getSecondInput()).getCoordsInD(3);
	}
	
    final public String toString() {
    	return app.getPlain("CylinderWithAxisThroughAParallelToBRadiusC",getOrigin().getLabel(),getSecondInput().getLabel(),getRadius().getLabel());

    }




	


}
