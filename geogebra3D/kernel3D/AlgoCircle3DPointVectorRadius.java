package geogebra3D.kernel3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoVectorND;

/**
 * 
 * @author mathieu
 *
 */
public class AlgoCircle3DPointVectorRadius extends AlgoCircle3DPointAxisRadius{

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param point
	 * @param forAxis
	 * @param radius
	 */
	public AlgoCircle3DPointVectorRadius(Construction cons, String label,
			GeoPointND point, NumberValue radius, GeoVectorND forAxis) {
		
		super(cons, label, point, radius, (GeoElement) forAxis);

	}

	protected Coords getDirection() {
		GeoVectorND vector = (GeoVectorND) getForAxis();
		return vector.getCoordsInD(3);
	}
	

    final public String toString() {
    	return app.getPlain("CircleWithCenterAandRadiusBAxisParallelToC",((GeoElement) getPoint()).getLabel(),((GeoElement) getRadius()).getLabel(),getForAxis().getLabel());
    }

}
