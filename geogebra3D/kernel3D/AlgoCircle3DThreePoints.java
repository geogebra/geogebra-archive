package geogebra3D.kernel3D;

import geogebra.Matrix.GgbCoordSys;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.AlgoCircleThreePoints;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;

/**
 * @author ggb3D
 * 
 * Algo that creates a 3D circle joining three 3D points
 *
 */
public class AlgoCircle3DThreePoints extends AlgoCircleThreePoints {

	/** coord sys defined by the three points where the 3D circle lies */
	private GgbCoordSys coordSys;
	
	/** 2D projection of the 3D points in the coord sys */
	private GeoPoint[] points2D;
	
	/** 3D points  */
	private GeoPoint3D[] points3D;
	
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
			GeoPointND A, GeoPointND B, GeoPointND C) {
		super(cons, label, A, B, C);
		
	}

	
	
    protected void setPoints(GeoPointND A, GeoPointND B, GeoPointND C){
    	
    	
    	points3D = new GeoPoint3D[3];
    	
    	points3D[0] = (GeoPoint3D) A;
    	points3D[1] = (GeoPoint3D) B;
       	points3D[2] = (GeoPoint3D) C;
            	

    	coordSys = new GgbCoordSys(2);
    	
    	
    	points2D = new GeoPoint[3];
    	for (int i=0;i<3;i++)
    		points2D[i] = new GeoPoint(getConstruction());
    	
    	super.setPoints(points2D[0],points2D[1],points2D[2]);
    	
    	
    	

    	
    	//this.getKernel().setSilentMode(true);
    	/*
    	algo = new AlgoCoordSys2D(this.getConstruction(),
    			new GeoPoint3D[] {(GeoPoint3D) A, (GeoPoint3D) B, (GeoPoint3D) C},true,false,false);    	
    	coordSys = algo.getCoordSys();
    	points2D = algo.getPoints2D();
    	this.getKernel().setSilentMode(false);
    	*/
    }

    
    protected void createCircle(){
    	
        circle = new GeoConic3D(cons,coordSys);
    }
    
    
    protected void setInput() {
        input = points3D;

    }
    
    protected void setOutput() {

    	output = new GeoElement[1];
    	output[0] = circle;	

    }
    
    
    /*
    public GeoPoint getA() {
        return points2D[0];
    }
    public GeoPoint getB() {
        return points2D[1];
    }
    public GeoPoint getC() {
        return points2D[2];
    }
    */
  
    public void compute(){
    	

    	coordSys.resetCoordSys();
    	for(int i=0;i<3;i++)
    		coordSys.addPoint(points3D[i].getCoords());
    	
  
    	if (!coordSys.makeOrthoMatrix(true)){
    		circle.setUndefined();
    		return;
    	}
    	
    	 for(int i=0;i<3;i++){
			 //project the point on the coord sys
			 GgbVector[] project=points3D[i].getCoords().projectPlane(coordSys.getMatrixOrthonormal());

			 //check if the vertex lies on the coord sys
			 if(!Kernel.isEqual(project[1].get(3), 0, Kernel.STANDARD_PRECISION)){
				 coordSys.setUndefined();
				 break;
			 }

			 //Application.debug("i="+i+",project="+project);
			 
			 //set the 2D points
			 points2D[i].setCoords(project[1].get(1), project[1].get(2), 1);
			 
		 }
    	
    	super.compute();
    }
    
    
    /*
    public void remove() {      

    	algo.remove();
    	coordSys.doRemove();
    	for(int i=0;i<points2D.length;i++)
    		points2D[i].remove();
    	
    }
    */
}
