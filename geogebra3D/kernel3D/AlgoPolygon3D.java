package geogebra3D.kernel3D;

import geogebra.kernel.AlgoPolygon;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPoint;

public class AlgoPolygon3D extends AlgoPolygon {

	
    
    
	public AlgoPolygon3D(Construction cons, String[] label, GeoCoordSys2D cs, GeoPoint[] points) {
		super(cons, label, points,null);
		((GeoPolygon3D) poly).setCoordSys(cs);
		
		GeoPolygon3D poly3D = (GeoPolygon3D) poly;
		
		
	}
	
    /**
     * create the polygon
     * @param cons the construction
     * @param points the 2D points
     */
    protected void createPolygon(Construction cons, GeoPoint [] points){
    	poly = new GeoPolygon3D(cons, points);
    }
	
	



}
