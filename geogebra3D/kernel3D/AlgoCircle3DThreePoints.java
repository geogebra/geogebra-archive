package geogebra3D.kernel3D;

import geogebra.kernel.AlgoCircleThreePoints;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPointInterface;

/**
 * @author ggb3D
 * 
 * Algo that creates a 3D circle joining three 3D points
 *
 */
public class AlgoCircle3DThreePoints extends AlgoCircleThreePoints {

	/** coord sys defined by the three points where the 3D circle lies */
	private GeoCoordSys2D coordSys;
	
	/** 2D projection of the 3D points in the coord sys */
	private GeoPoint[] points2D;
	
	/** helper algo for 2D coord sys */
	private AlgoCoordSys2D algo;
	
	/**
	 * Basic constructor
	 * @param cons construction
	 * @param label name of the circle
	 * @param A first point
	 * @param B second point
	 * @param C third point
	 */
	public AlgoCircle3DThreePoints(Construction cons, String label,
			GeoPointInterface A, GeoPointInterface B, GeoPointInterface C) {
		super(cons, label, A, B, C);
		
	}

	
	
    protected void setPoints(GeoPointInterface A, GeoPointInterface B, GeoPointInterface C){
    	
    	super.setPoints(A, B, C);

    	this.getKernel().setSilentMode(true);
    	algo = new AlgoCoordSys2D(this.getConstruction(),
    			new GeoPoint3D[] {(GeoPoint3D) A, (GeoPoint3D) B, (GeoPoint3D) C},true,false);    	
    	coordSys = algo.getCoordSys();
    	points2D = algo.getPoints2D();
    	this.getKernel().setSilentMode(false);
    }

    
    protected void createCircle(){
    	
        circle = new GeoConic3D(cons,coordSys);
    }
    
    
    
    
    public GeoPoint getA() {
        return points2D[0];
    }
    public GeoPoint getB() {
        return points2D[1];
    }
    public GeoPoint getC() {
        return points2D[2];
    }
    
  
    public void compute(){
    	
    	algo.compute();
    	super.compute();
    }
    
}
