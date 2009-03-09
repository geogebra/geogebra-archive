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
	
	/** for when the 2D coord sys is especially created for the polygon */
	private AlgoCoordSys2D algoCS;
	
	/** when this is removed, the AlgoCoordSys2D has to be removed */
	private boolean algoCSisRemoved;
    
	
	/**
	 * Constructor with an AlgoCoordSys2D
	 * @param cons the construction
	 * @param label names of the polygon and segments
	 * @param algoCS the AlgoElement providing the 2D coord sys
	 */
	public AlgoPolygon3D(Construction cons, String[] label, AlgoCoordSys2D algoCS) {
		
		this(cons, label, algoCS.getCoordSys(),algoCS.getPoints2D());
		this.algoCS = algoCS;
		algoCSisRemoved = false;
	
	}
	
	
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
	
    /**
     * create the polygon
     */
    protected void createPolygon(){
    	poly = new GeoPolygon3D(cons, points, (GeoCoordSys2D) cs2D);
    }
	
    
    public void remove(){   	
    	//if there's an AlgoCoordSys2D, it begins removing it
    	if (!algoCSisRemoved){
    		algoCSisRemoved=true;
    		algoCS.remove(); //it will call this.remove()
    	}else
    		super.remove();
    	
    	
    }
 	



}
