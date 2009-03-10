package geogebra3D.kernel3D;

import geogebra.kernel.AlgoPolygon;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoPoint;

/**
 * AlgoElement creating a GeoPolygon3D
 * 
 * @author ggb3D
 *
 */
public class AlgoPolygon3D extends AlgoPolygon {
	
	
   
	

	
	
	/**
	 * Constructor with an 2D coord sys and points
	 * @param cons the construction
	 * @param label names of the polygon and segments
	 * @param cs 2D coord sys
	 * @param points vertices of the polygon
	 */    
	public AlgoPolygon3D(Construction cons, String[] label, GeoCoordSys2D cs, GeoPoint[] points) {
		super(cons, label, points, null,cs);

	}
	
	
	
	public AlgoPolygon3D(Construction cons, String[] label, GeoPoint3D[] points) {
		super(cons, label, points, null,null);

	}
	
	
	
	
    /**
     * create the polygon
     */
    protected void createPolygon(){
    	poly = new GeoPolygon3D(cons, points, (GeoCoordSys2D) cs2D);
    }
	




}
