package geogebra3D.kernel3D;

import geogebra.kernel.AlgoPolygon;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPoint;

public class AlgoPolygon3D extends AlgoPolygon {

	
    
    
	public AlgoPolygon3D(Construction cons, String[] label, GeoCoordSys2D cs, GeoPoint[] points) {
		super(cons, label, points, null,cs);

	}
	
    /**
     * create the polygon
     */
    protected void createPolygon(){
    	poly = new GeoPolygon3D(cons, points, (GeoCoordSys2D) cs2D);
    }
	
	



}
