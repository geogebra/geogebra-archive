package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;

/**
 * @author ggb3D
 *
 */
public class AlgoPlane extends AlgoCoordSys2D {
	
	/**
	 * create a plane joining points, with label.
	 * @param c construction
	 * @param label label of the polygon
	 * @param A first point
	 * @param B second point
	 * @param C third point
	 */
	public AlgoPlane(Construction c, String label, GeoPoint3D A, GeoPoint3D B, GeoPoint3D C) {
		this(c,A,B,C);
		((GeoElement) cs).setLabel(label);
	}
	/**
	 * create a plane joining points.
	 * @param c construction
	 * @param A first point
	 * @param B second point
	 * @param C third point
	 */
	public AlgoPlane(Construction c, GeoPoint3D A, GeoPoint3D B, GeoPoint3D C) {		
		super(c,new GeoPoint3D[] {A, B, C},true,true);
	}
	
	
	protected void createCoordSys(Construction c){
		cs = new GeoPlane3D(c);
	}
	
	
	
	public String getClassName() {
		return "AlgoPlane";
	}

}
