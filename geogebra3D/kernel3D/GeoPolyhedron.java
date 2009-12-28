package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoSegmentInterface;
import geogebra.main.Application;
import geogebra3D.euclidian3D.Drawable3D;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

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
	protected ArrayList<GeoPoint3D> points;
	
	/** edges */
	protected Hashtable<String, GeoSegment3D> segments;
	
	/** faces */
	protected Hashtable<String,GeoPolygon3D> polygons;
	
	/** faces description */
	protected ArrayList<ArrayList<Integer>> faces;
	
	/** face currently constructed */
	private ArrayList<Integer> currentFace;
	
	
	

	

	
	
	/** constructor 
	 * @param c construction
	 */
	public GeoPolyhedron(Construction c) {
		super(c);
		points = new ArrayList<GeoPoint3D>();
		faces = new ArrayList<ArrayList<Integer>>();
		
		polygons = new Hashtable<String, GeoPolygon3D>();
		segments = new Hashtable<String, GeoSegment3D>();

	}
	
	
	
	/**
	 * start a new face
	 */
	public void startNewFace(){
		currentFace = new ArrayList<Integer>();
	}
	
	
	/** add the point to the current face
	 * and to the point list if it's a new one
	 * @param point
	 */
	public void addPointToCurrentFace(GeoPoint3D point){
		int index;
		if (points.contains(point))
			index = points.indexOf(point);
		else{
			index = points.size();
			points.add(point);
		}
		currentFace.add(new Integer(index));
	}
	
	
	/**
	 * ends the current face and store it in the faces list
	 */
	public void endCurrentFace(){
		faces.add(currentFace);
	}
	
	
	/**
	 * return the hashtable key for a segment between startPoint and endPoint
	 * @param startPoint
	 * @param endPoint
	 * @return the hashtable key for a segment between startPoint and endPoint
	 */
	private String getSegmentKey(int startPoint, int endPoint){
		
		if(startPoint>endPoint){
			int temp = startPoint;
			startPoint = endPoint;
			endPoint = temp;
		}
		
		String key = startPoint+"-"+endPoint;
		
		return key;
	}
	
	
	/**
	 * return the hashtable key for a face 
	 * @param face
	 * @return
	 */
	private String getFaceKey(ArrayList<Integer> face){
		
		StringBuffer key = new StringBuffer();
		
		//find the min point index
		Integer min = Integer.MAX_VALUE;
		int minIndex = 0;
		for(int i=0;i<face.size();i++){
			if (min>face.get(i)){
				min=face.get(i);
				minIndex = i;
			}
		}
		
		//find the increasing direction
		int direction = 1;
		if ( face.get((minIndex+1) % face.size()) < face.get((minIndex+face.size()-1) % face.size())){
			direction = -1;
			minIndex += face.size();
		}
			
		
		for (int i=0; i<face.size();i++){
			key.append(face.get( (minIndex+i*direction) % face.size()));
			key.append("-");
		}
			
		//Application.debug(key.toString());
		
		
		return key.toString();
		
	}
	
	
	/**
	 * update the faces regarding vertices and faces description
	 */
	public void updateFaces(){
		
		if (faces == null) return;
		
		//TODO remove old faces and edges
		
		
		
		// create missing faces
		for (int i=0; i < faces.size(); i++) {
			currentFace = faces.get(i);
			
			//if a polygons already corresponds to the face description, then pass it
			String faceKey = getFaceKey(currentFace);
			if (polygons.containsKey(faceKey))
				continue;
			
			//vertices of the face
			GeoPoint3D[] p = new GeoPoint3D[currentFace.size()];
			//edges linked to the face
			GeoSegmentInterface[] s = new GeoSegmentInterface[currentFace.size()];
			for (int j=0; j < currentFace.size(); j++) {
				p[j]=points.get(currentFace.get(j));

				// creates edges
				int startPoint = currentFace.get(j);
				int endPoint = currentFace.get((j+1) % currentFace.size());
				String key = getSegmentKey(startPoint, endPoint);

				if (!segments.containsKey(key))
					segments.put(key, createSegment(startPoint, endPoint));
				
				s[j] = segments.get(key);
			}
			GeoPolygon3D polygon = createPolygon(p);
			polygons.put(faceKey, polygon);
			polygon.setSegments(s);
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
				 points.get(startPoint), points.get(endPoint), this, GeoElement3D.GEO_CLASS_SEGMENT3D);            
		 cons.removeFromConstructionList(algoSegment);               

		 segment = (GeoSegment3D) algoSegment.getCS(); 
		 // refresh color to ensure segments have same color as polygon:
		 segment.setObjColor(getObjectColor()); 
		 
		 //TODO translation for edge
		 segment.setLabel("edge"+points.get(startPoint).getLabel()+points.get(endPoint).getLabel());

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
		 GeoPolygon3D[] polygonsArray = new GeoPolygon3D[polygons.size()];
		 int index=0;
		 for (GeoPolygon3D polygon : polygons.values()){
			 polygonsArray[index]=polygon;
			 index++;
		 }

		 return polygonsArray;
	 }
	 
	 
	   public void setObjColor(Color color) {
		   
	   		super.setObjColor(color);
	   		
	   		
	   		//if (polygons != null) {
	   			/*
	   			for (int i=0; i < polygons.length; i++) {
	   				polygons[i].setObjColor(color);
	   				polygons[i].update();
	   			}
	   			*/
	   			for (GeoPolygon3D polygon : polygons.values()){
	   				polygon.setObjColor(color);
	   				polygon.update();
		   		}
	   			
	   	   		for (GeoSegment3D segment : segments.values()){
		   			segment.setObjColor(color);
		   			segment.update();
		   		}
	   		//}
	   		
	   		
	
	   }
	   
	 
		public void setAlphaValue(float alpha) {
		   
	   		super.setAlphaValue(alpha);
	   		
   			for (GeoPolygon3D polygon : polygons.values()){
   				polygon.setAlphaValue(alpha);
   				polygon.update();
	   		}
   			
   			/*
	   		if (polygons != null) {
	   			for (int i=0; i < polygons.length; i++) {
	   				polygons[i].setAlphaValue(alpha);
	   				polygons[i].update();
	   			}
	   		}
	   		*/
	   		
	   		
	
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
