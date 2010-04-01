package geogebra3D.kernel3D;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

/**
 * Algo creating a 2D coord sys linked to 3D points.
 * <p>
 * The dependencies can be set or not (for helper algo).
 * 
 * 
 * @author ggb3D
 *
 */
public class AlgoCoordSys2D extends AlgoElement3D {

	/** the 2D coord sys created */
	protected GeoCoordSys2DAbstract cs;
	
	
	/** 3D points */
	private GeoPoint3D[] points;
	
	
	/** says if 2D points are created */
	boolean createPoints2D;
	
	/** 2D points */
	private GeoPoint[] points2D;
	
	/** says if the vector Vx has to be parallel to xOy plane */
	private boolean vxParallelToXoy;
	
	/**
	 * create a 2D coord sys joining points, with label.
	 * @param c construction
	 * @param label label of the polygon
	 * @param points the vertices of the polygon
	 * @param createPoints2D says if 2D points have to be created
	 */
	public AlgoCoordSys2D(Construction c, String label, GeoPoint3D[] points, boolean createPoints2D) {
		this(c,points,createPoints2D);
		cs.setLabel(label);
	}
	
	/**
	 * create a 2D coord sys joining points.
	 * @param c construction
	 * @param points the vertices of the polygon
	 * @param createPoints2D says if 2D points have to be created
	 */
	public AlgoCoordSys2D(Construction c, GeoPoint3D[] points, 
			boolean createPoints2D) {		
		this(c,points,createPoints2D,false,true);
	}
	
	/**
	 * create a 2D coord sys joining points.
	 * @param c construction
	 * @param points the vertices of the polygon
	 * @param createPoints2D says if 2D points have to be created
	 * @param vxParallelToXoy says if the vector Vx has to be parallel to xOy plane
	 * @param setDependencies says if the dependencies have to be set
	 */
	public AlgoCoordSys2D(Construction c, GeoPoint3D[] points, 
			boolean createPoints2D, boolean vxParallelToXoy, boolean setDependencies) {
		super(c);
		
		createCoordSys(c);
		this.points = points;
		this.createPoints2D = createPoints2D;
		this.vxParallelToXoy = vxParallelToXoy;

		
		GeoElement[] out;
	
		if (createPoints2D){
			points2D = new GeoPoint[points.length];
			out = new GeoElement[points.length+1];	
			for(int i=0;i<points.length;i++){
				points2D[i]=new GeoPoint(c);
				//points2D[i].setLabel("essaiPoint");
				out[i+1]=points2D[i];
			}
		}else{
			out = new GeoElement[1];		
		}
		
		out[0]=cs;
		
		
		//set input and output		
		setInputOutput(points, out, setDependencies);
		
		
		
		
	}
	
	
	/**
	 * create the coord sys
	 * @param c construction
	 */
	protected void createCoordSys(Construction c){
		cs = new GeoCoordSys2DAbstract(c);
		
	}
	
	protected void compute() {
				
		//recompute the coord sys
		cs.resetCoordSys();
		for(int i=0;(!cs.isMadeCoordSys())&&(i<points.length);i++)
			cs.addPointToCoordSys(points[i].getCoords(),true,vxParallelToXoy);
		
	
		
		for(int i=0;i<points.length;i++){
			//project the point on the coord sys
			GgbVector[] project=points[i].getCoords().projectPlane(cs.getMatrix4x4());
			
			//check if the vertex lies on the coord sys
			if(!Kernel.isEqual(project[1].get(3), 0, Kernel.STANDARD_PRECISION))
				cs.setUndefined();

			
			//set the 2D points
			if (createPoints2D)
				points2D[i].setCoords(project[1].get(1), project[1].get(2), 1);
		}
		
	}

	
	/**
	 * return the cs
	 * @return the cs
	 */
	public GeoCoordSys2DAbstract getCoordSys() {		
		return cs;
	}

	
	
	/** return the 2D points
	 * @return the 2D points
	 */
	public GeoPoint[] getPoints2D(){
		return points2D;
	}
	
	
	
	
	protected String getClassName() {
		return "AlgoCoordSys2D";
	}


}
