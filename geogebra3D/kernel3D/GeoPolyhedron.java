package geogebra3D.kernel3D;

import geogebra.kernel.AlgoJoinPointsSegment;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.GeoSegmentInterface;
import geogebra.main.Application;

/**
 * @author ggb3D
 * 
 * Class describing a GeoPolyhedron
 *
 */
public class GeoPolyhedron extends GeoElement3D {

	/** vertices */
	protected GeoPoint3D[] points;
	
	/** edges */
	protected GeoSegment3D[] segments;
	
	/** faces */
	protected GeoPolygon3D[] polygons;
	
	/** faces description */
	protected int[][] faces;
	

	
	/** Default constructor
	 * @param c construction
	 */
	public GeoPolyhedron(Construction c) {
		super(c);
		
	}
	
	
	/** constructor that sets the vertices and the faces description
	 * @param c construction
	 * @param points vertices
	 * @param faces faces description
	 * 	 */
	public GeoPolyhedron(Construction c, GeoPoint3D[] points, int[][] faces) {
		super(c);
		setPoints(points, faces);
	}
	
	
	
	
	/**
	 * set the vertices and the faces description
	 * @param points vertices
	 * @param faces faces description
	 */
	public void setPoints(GeoPoint3D[] points, int[][] faces){
		this.points = points;
		this.faces = faces;
		updateFaces();
	}
	
	
	
	/**
	 * update the faces regarding vertices and faces description
	 */
	public void updateFaces(){
		
		
		
		//TODO remove old faces
		
		polygons = new GeoPolygon3D[faces.length];
		
		// create missing faces
        for (int i=0; i < faces.length; i++) {
        	GeoPoint3D[] p = new GeoPoint3D[faces[i].length];
        	for (int j=0; j < faces[i].length; j++) {
        		p[j]=points[faces[i][j]];
       	}
        	polygons[i] = createPolygon(p);
        }  
	}
	
	
	
	
	 /** create a polygon joining the given points
	 * @param points vertices of the polygon
	 * @return the polygon
	 */
	public GeoPolygon3D createPolygon(GeoPoint3D[] points){
		 GeoPolygon3D polygon;

		 AlgoPolygon3D algo = new AlgoPolygon3D(cons,null,points,false);            
		 cons.removeFromConstructionList(algo);               

		 polygon = (GeoPolygon3D) algo.getPoly();
		 // refresh color to ensure segments have same color as polygon:
		 polygon.setObjColor(getObjectColor()); 

		 return polygon;
	 }
	
	
	
	
	

	@Override
	public GeoElement copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGeoClassType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected String getTypeString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDefined() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void set(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUndefined() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toValueString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}

}
