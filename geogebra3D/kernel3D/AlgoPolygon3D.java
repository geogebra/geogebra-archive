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
	
	
   
	/** says if the polygon has to creates its edges */
	boolean createSegments = true;

	
	
	/**
	 * Constructor with an 2D coord sys and points
	 * @param cons the construction
	 * @param label names of the polygon and segments
	 * @param cs 2D coord sys
	 * @param points vertices of the polygon
	 */    
	public AlgoPolygon3D(Construction cons, String[] label, GeoCoordSys2D cs, GeoPoint[] points) {
		super(cons, label, points, null,cs,true);

	}
	
	
	
	/**
	 * Constructor with points
	 * @param cons the construction
	 * @param label names of the polygon and segments
	 * @param points vertices of the polygon
	 */   
	public AlgoPolygon3D(Construction cons, String[] label, GeoPoint3D[] points) {
		this(cons, label, points, true);

	}
	
	
	/**
	 * @param cons
	 * @param label
	 * @param points
	 * @param createSegments
	 */
	public AlgoPolygon3D(Construction cons, String[] label, GeoPoint3D[] points, boolean createSegments) {
		super(cons, label, points, null,null,createSegments);
		
	}
	
	
    /**
     * create the polygon
     * @param createSegments says if the polygon has to creates its edges (3D only)
     */
    protected void createPolygon(boolean createSegments){
    	poly = new GeoPolygon3D(cons, points, (GeoCoordSys2D) cs2D, createSegments);
    }
	




}
