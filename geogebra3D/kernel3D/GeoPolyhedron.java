package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoSegmentInterface;

import java.awt.Color;
import java.util.Hashtable;

/**
 * @author ggb3D
 * 
 * Class describing a GeoPolyhedron
 *
 */
public class GeoPolyhedron extends GeoElement3D {

	
	public static final int TYPE_NONE = 0;
	public static final int TYPE_PYRAMID = 1;
	public static final int TYPE_PSEUDO_PRISM = 2;
	public static final int TYPE_PRISM = 3;
	
	
	/** vertices */
	protected GeoPoint3D[] points;
	
	/** edges */
	protected Hashtable<Integer, GeoSegment3D> segments;
	
	/** faces */
	protected GeoPolygon3D[] polygons;
	
	/** faces description */
	protected int[][] faces;
	
	
	

	

	
	
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
		
		if (faces == null) return;
		
		//TODO remove old faces and edges
		
		polygons = new GeoPolygon3D[faces.length];
		segments = new Hashtable<Integer, GeoSegment3D>();
		
		
		// create missing faces
		for (int i=0; i < faces.length; i++) {
			//vertices of the face
			GeoPoint3D[] p = new GeoPoint3D[faces[i].length];
			//edges linked to the face
			GeoSegmentInterface[] s = new GeoSegmentInterface[faces[i].length];
			for (int j=0; j < faces[i].length; j++) {
				p[j]=points[faces[i][j]];

				// creates edges
				int startPoint = faces[i][j];
				int endPoint = faces[i][(j+1) % faces[i].length];
				if(startPoint>endPoint){
					int temp = startPoint;
					startPoint = endPoint;
					endPoint = temp;
				}
				
				int index = startPoint+endPoint*points.length;

				if (!segments.containsKey(index))
					segments.put(index, createSegment(startPoint, endPoint));
				
				s[j] = segments.get(index);
			}
			polygons[i] = createPolygon(p);
			polygons[i].setSegments(s);
        }  
	}
	
	
	
	
	 /** create a polygon joining the given points
	 * @param points vertices of the polygon
	 * @return the polygon
	 */
	public GeoPolygon3D createPolygon(GeoPoint3D[] points){
		 GeoPolygon3D polygon;

		 AlgoPolygon3D algo = new AlgoPolygon3D(cons,null,points,false,this);            
		 cons.removeFromConstructionList(algo);               

		 polygon = (GeoPolygon3D) algo.getPoly();
		 // refresh color to ensure segments have same color as polygon:
		 polygon.setObjColor(getObjectColor()); 
		 
		 //TODO translation for face
		 String s="face";
		 for(int i=0;i<points.length;i++)
			 s+=points[i].getLabel();
		 polygon.setLabel(s);
		 

		 return polygon;
	 }
	
	
	
	 /**
	  * return a segment joining startPoint and endPoint
	  * @param startPoint the start point
	  * @param endPoint the end point
	  * @return the segment
	  */
	
	 public GeoSegment3D createSegment(int startPoint, int endPoint){
		 GeoSegment3D segment;

		 AlgoJoinPoints3D algoSegment = new AlgoJoinPoints3D(cons, 
				 points[startPoint], points[endPoint], this, GeoElement3D.GEO_CLASS_SEGMENT3D);            
		 cons.removeFromConstructionList(algoSegment);               

		 segment = (GeoSegment3D) algoSegment.getCS(); 
		 // refresh color to ensure segments have same color as polygon:
		 segment.setObjColor(getObjectColor()); 
		 
		 //TODO translation for edge
		 segment.setLabel("edge"+points[startPoint].getLabel()+points[endPoint].getLabel());

		 return segment;
		 
		 
	 }
	
	
	 
	 public GeoSegment3D[] getSegments(){
		 
		 GeoSegment3D[] ret = new GeoSegment3D[segments.size()];
		 int i=0;
		 for (GeoSegment3D segment : segments.values()){
			 ret[i]=segment;
			 i++;
		 }
		 return ret;
	 }
	 
	 public GeoPolygon3D[] getFaces(){
		 return polygons;
	 }
	 
	 
	   public void setObjColor(Color color) {
		   
	   		super.setObjColor(color);
	   		
	   		
	   		if (polygons != null) {
	   			for (int i=0; i < polygons.length; i++) {
	   				polygons[i].setObjColor(color);
	   				polygons[i].update();
	   			}
	   			
	   	   		for (GeoSegment3D segment : segments.values()){
		   			segment.setObjColor(color);
		   			segment.update();
		   		}
	   		}
	   		
	   		
	
	   }
	   
	 
		public void setAlphaValue(float alpha) {
		   
	   		super.setAlphaValue(alpha);
	   		
	   		
	   		if (polygons != null) {
	   			for (int i=0; i < polygons.length; i++) {
	   				polygons[i].setAlphaValue(alpha);
	   				polygons[i].update();
	   			}
	   		}
	   		
	   		
	
	   }
	 
	 



	 
	 
	 
	 
	

	@Override
	public GeoElement copy() {
		// TODO Auto-generated method stub
		return null;
	}


	public int getGeoClassType() {
		return GEO_CLASS_POLYHEDRON;
	}


	protected String getTypeString() {
		return "Polyhedron";
	}

	@Override
	public boolean isDefined() {
		// TODO Auto-generated method stub
		return true;
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
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String toValueString() {
		// TODO Auto-generated method stub
		return "todo-GeoPolyhedron";
	}


	
	protected String getClassName() {
		return "GeoPolyhedron";
	}
	
	
	
	
	
	

	
	

}
