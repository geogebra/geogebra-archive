package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DVector;

/**
 * Algo creating a 2D coord sys linked to 3D points.
 * 
 * @author ggb3D
 *
 */
public class AlgoCoordSys2D extends AlgoElement3D {

	/** the 2D coord sys created */
	private GeoCoordSys2D cs;
	
	
	/** 3D points */
	private GeoPoint3D[] points;
	
	
	/** says if 2D points are created */
	boolean createPoints2D;
	
	/** 3D points */
	private GeoPoint[] points2D;
	
	
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
	 * @param point 
	 * @param createPoints2Ds 
	 * @param points the vertices of the polygon
	 * @param createPoints2D says if 2D points have to be created
	 */
	public AlgoCoordSys2D(Construction c, GeoPoint3D[] points, boolean createPoints2D) {
		super(c);
		
		cs = new GeoCoordSys2D(c);
		this.points = points;
		this.createPoints2D = createPoints2D;

		
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
		setInputOutput(points, out);
		
		
	}
	
	protected void compute() {
		
		//recompute the coord sys
		cs.resetCoordSys();
		for(int i=0;(!cs.isMadeCoordSys())&&(i<points.length);i++)
			cs.addPointToCoordSys(points[i].getCoords(),true);
		
		//if there's no coord sys, the coord sys is undefined
		//TODO add case where the coord sys is made of colinear points
		
		
		
		//recompute the vertices
		//polygon.updateVertices();
		for(int i=0;i<points.length;i++){
			//project the point on the coord sys
			Ggb3DVector[] project=points[i].getCoords().projectPlane(cs.getMatrix4x4());
			
			//TODO check if the vertex lies on the coord sys
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
	public GeoCoordSys2D getCoordSys() {		
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
