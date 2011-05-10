package geogebra3D.kernel3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoPointND;

/**
 * 
 * @author mathieu
 *
 */
public class AlgoCircle3DPointPlaneRadius extends AlgoCircle3DPointAxisRadius{

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param point
	 * @param forAxis
	 * @param radius
	 */
	public AlgoCircle3DPointPlaneRadius(Construction cons, String label,
			GeoPointND point, NumberValue radius, GeoCoordSys2D forAxis) {
		
		super(cons, label, point, radius, (GeoElement) forAxis);

	}

	protected Coords getDirection() {
		GeoCoordSys2D plane = (GeoCoordSys2D) getForAxis();
		return plane.getCoordSys().getNormal();
	}
	

    final public String toString() {
    	return app.getPlain("CircleOfCenterARadiusBParallelToC",((GeoElement) getPoint()).getLabel(),((GeoElement) getRadius()).getLabel(),getForAxis().getLabel());
    }

}
