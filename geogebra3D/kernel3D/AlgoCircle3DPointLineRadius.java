package geogebra3D.kernel3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;

/**
 * 
 * @author mathieu
 *
 */
public class AlgoCircle3DPointLineRadius extends AlgoCircle3DPointAxisRadius{

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param point
	 * @param forAxis
	 * @param radius
	 */
	public AlgoCircle3DPointLineRadius(Construction cons, String label,
			GeoPointND point, NumberValue radius, GeoLineND forAxis) {
		
		super(cons, label, point, radius, (GeoElement) forAxis);

	}

	protected Coords getDirection() {
		GeoLineND axis = (GeoLineND) getForAxis();
		Coords o = axis.getPointInD(3, 0);
    	Coords d = axis.getPointInD(3, 1).sub(o);
		return d;
	}
	

    final public String toString() {
    	return app.getPlain("CircleWithCenterAandRadiusBAxisParallelToC",((GeoElement) getPoint()).getLabel(),((GeoElement) getRadius()).getLabel(),getForAxis().getLabel());
    }

}
