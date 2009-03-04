package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra3D.Matrix.Ggb3DVector;

/**
 * Algo creating a 3D polygon.
 * 
 * @author ggb3D
 *
 */
public class AlgoPolygon3D extends AlgoElement3D {

	/** the 3D polygon created */
	private GeoPolygon3D polygon;
	
	/** number of vertices */
	private int numPoints;
	
	/** 2D points used for computation */
	//private GeoPoint3D[] points;
	
	
	/**
	 * create a 3D polygon joining points, with label.
	 * @param c construction
	 * @param label label of the polygon
	 * @param points the vertices of the polygon
	 */
	public AlgoPolygon3D(Construction c, String label, GeoPoint3D[] points) {
		this(c,points);
		polygon.setLabel(label);
	}
	
	/**
	 * create a 3D polygon joining points.
	 * @param c construction
	 * @param points the vertices of the polygon
	 */
	public AlgoPolygon3D(Construction c, GeoPoint3D[] points) {
		super(c);
		
		polygon = new GeoPolygon3D(c,points);
		numPoints = points.length;
		
		//creating 2D points linked to 3D points, and set the output
		/*
		GeoElement[] out = new GeoElement[numPoints+1];
		points2D = new GeoPoint[numPoints];
		Algo3Dto2D algo;
		for(int i=0;i<numPoints;i++){
			//out[i]=points[i];
			algo = new Algo3Dto2D(c,points[i],polygon);
			out[i+1]= algo.getGeo();
			points2D[i]= (GeoPoint) algo.getGeo();
		}
		*/
		
		GeoElement[] out = new GeoElement[1];
		out[0]=polygon;
		
		//set input and output
		setInputOutput(points, out);
		
		//compute
		compute();

		
	}
	
	protected void compute() {
		//recompute the coord sys
		polygon.setCoordSys();
		
		//recompute the vertices
		polygon.updateVertices();
		

	}

	
	/**
	 * return the polygon
	 * @return the polygon
	 */
	public GeoPolygon3D getPoly() {		
		return polygon;
	}

	
	
	
	
	
	
	protected String getClassName() {
		return "AlgoPolygon";
	}


}
