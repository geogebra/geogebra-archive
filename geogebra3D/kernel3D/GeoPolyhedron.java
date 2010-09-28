package geogebra3D.kernel3D;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionElement;
import geogebra.kernel.ConstructionElementCycle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoSegmentInterface;
import geogebra.main.Application;

import java.awt.Color;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

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
	//protected ArrayList<GeoPoint3D> points;
	
	
	/** edges index */
	protected TreeMap<ConstructionElementCycle,Long> segmentsIndex;
	
	/** max faces edges */
	protected long segmentsIndexMax = 0;

	/** edges */
	protected TreeMap<Long, GeoSegment3D> segments;
	
	
	
	
	/** faces index */
	protected TreeMap<ConstructionElementCycle,Long> polygonsIndex;
	
	/** max faces index */
	protected long polygonsIndexMax = 0;
	
	/** faces */
	protected TreeMap<Long,GeoPolygon3D> polygons;
	
	
	/** segments to remove for update */
	protected TreeSet<ConstructionElementCycle> oldSegments;
	
	/** polygons to remove for update */
	protected TreeSet<ConstructionElementCycle> oldPolygons;
	
	
	
	
	/** face currently constructed */
	private ConstructionElementCycle currentFace;
	
	
	

	

	
	
	/** constructor 
	 * @param c construction
	 */
	public GeoPolyhedron(Construction c) {
		super(c);

		
		polygonsIndex = new TreeMap<ConstructionElementCycle,Long>();
		polygons = new TreeMap<Long, GeoPolygon3D>();
		
		segmentsIndex = new TreeMap<ConstructionElementCycle,Long>();
		segments = new TreeMap<Long, GeoSegment3D>();
		
		oldPolygons = new TreeSet<ConstructionElementCycle>();		
		oldSegments = new TreeSet<ConstructionElementCycle>();		

	}
	
	
	/**
	 * restart faces descriptions
	 */
	public void restartFaces(){
		
		oldPolygons.clear();
		for (ConstructionElementCycle key : polygonsIndex.keySet()){
			//Application.debug("key : "+key);
			oldPolygons.add(key);
		}
		
		oldSegments.clear();
		for (ConstructionElementCycle key : segmentsIndex.keySet())
			oldSegments.add(key);
		
	}
	
	
	/**
	 * start a new face
	 */
	public void startNewFace(){
		currentFace = new ConstructionElementCycle();
	}
	
	
	/** add the point to the current face
	 * and to the point list if it's a new one
	 * @param point
	 */
	public void addPointToCurrentFace(GeoPoint3D point){

		currentFace.add(point);
	}
	
	
	/**
	 * ends the current face and store it in the faces list
	 */
	public void endCurrentFace(){
		currentFace.setDirection();
		
		//if the old polygons contains the current face, then this won't be removed nor recreated
		if (oldPolygons.contains(currentFace)){
			oldPolygons.remove(currentFace);
			//update old segments
			Iterator<ConstructionElement> it = currentFace.iterator();
			GeoPoint3D endPoint = (GeoPoint3D) it.next();
			GeoPoint3D firstPoint = endPoint;
			for (; it.hasNext();){
				GeoPoint3D startPoint = endPoint;
				endPoint = (GeoPoint3D) it.next();
				oldSegments.remove(
						ConstructionElementCycle.SegmentDescription(startPoint, endPoint));
			}
			//last segment
			oldSegments.remove(
					ConstructionElementCycle.SegmentDescription(endPoint, firstPoint));	
		}else{
			//faces.add(currentFace);
			polygonsIndex.put(currentFace, new Long(polygonsIndexMax));
			polygonsIndexMax++;
		}
		
	}
	
	
	
	
	/**
	 * update the faces regarding vertices and faces description
	 */
	public void updateFaces(){
		
		
		//remove old faces and edges
		for (ConstructionElementCycle key : oldPolygons){
			
			GeoPolygon3D polygon = polygons.get(key);
			if (polygon!=null){
				Application.debug("polygon : "+polygon.getLabel());
				polygon.remove();
				polygons.remove(key);
			}
		}
		for (ConstructionElementCycle key : oldSegments){
			GeoSegment3D segment = segments.get(key);
			if (segment!=null){
				Application.debug("segment : "+segment.getLabel());
				segment.remove();
				segments.remove(key);
			}
		}
		
		
		// create missing faces
		for (ConstructionElementCycle currentFace : polygonsIndex.keySet()){
			
			//if a polygons already corresponds to the face description, then pass it
			if (polygons.containsKey(polygonsIndex.get(currentFace)))
				continue;
			
			//vertices of the face
			GeoPoint3D[] p = new GeoPoint3D[currentFace.size()];
			
			//edges linked to the face
			GeoSegmentInterface[] s = new GeoSegmentInterface[currentFace.size()];
			
			Iterator<ConstructionElement> it2 = currentFace.iterator();
			GeoPoint3D endPoint = (GeoPoint3D) it2.next();
			int j=0;
			p[j]= endPoint; //first point for the polygon
			GeoPoint3D firstPoint = endPoint;
			for (; it2.hasNext();){
				// creates edges
				GeoPoint3D startPoint = endPoint;
				endPoint = (GeoPoint3D) it2.next();
				s[j] = createSegment(startPoint, endPoint);
				
				//points for the polygon
				j++;
				p[j]=endPoint;

			}
			//last segment
			s[j] = createSegment(endPoint, firstPoint);
			
			GeoPolygon3D polygon = createPolygon(p);
			polygons.put(polygonsIndex.get(currentFace), polygon);
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

		 return polygon;
	 }
	
	
	
	 /**
	  * return a segment joining startPoint and endPoint
	  * if this segment already exists in segments, return the already stored one
	  * @param startPoint the start point
	  * @param endPoint the end point
	  * @return the segment
	  */
	
	 public GeoSegment3D createSegment(GeoPoint3D startPoint, GeoPoint3D endPoint){
		 
		 ConstructionElementCycle key = 
			 ConstructionElementCycle.SegmentDescription(startPoint,endPoint);
		 /*
			 new ConstructionElementCycle();
		 key.add(startPoint);key.add(endPoint);
		 */

		 if (segmentsIndex.containsKey(key))
			 return segments.get(segmentsIndex.get(key));
				
			
		 GeoSegment3D segment;

		 AlgoJoinPoints3D algoSegment = new AlgoJoinPoints3D(cons, 
				 startPoint, endPoint, this, GeoElement3D.GEO_CLASS_SEGMENT3D);            
		 cons.removeFromConstructionList(algoSegment);               

		 segment = (GeoSegment3D) algoSegment.getCS(); 
		 // refresh color to ensure segments have same color as polygon:
		 segment.setObjColor(getObjectColor()); 
		 
		 //TODO translation for edge
		 //segment.setLabel("edge"+startPoint.getLabel()+endPoint.getLabel());

		 Long index = new Long(segmentsIndexMax);
		 segmentsIndex.put(key, index);
		 segments.put(index, segment);
		 segmentsIndexMax++;
			
		 return segment;
		 
		 
	 }
	 
	 
	 
	 
	 
	 
	    /**
	     * Inits the labels of this polyhedron, its faces and edges.
	     * labels[0] for polyhedron itself, labels[1..n] for faces and edges,
	     * @param labels
	     */
	    void initLabels(String [] labels) {       	 
	    	
	    	int index=1;
	    	
	    	if (labels == null || labels.length == 0) 
	    		return;

	        // first label for polyhedron itself
	    	setLabel(labels[0]); 
	    	
	    	
	    	if (labels.length - index < polygons.size()){
	    		defaultPolygonsLabels();
	    		defaultSegmentLabels();
	    		return;
	    	}
	    	
	    	
	    	// labels for polygons
	    	for (GeoPolygon3D polygon : polygons.values()){
	    		polygon.setLabel(labels[index]);
	    		index++;
	    	}

	    	
	    	if (labels.length - index < segments.size()){
	    		defaultSegmentLabels();
	    		return;
	    	}

	    	
	    	// labels for segments
	    	for (GeoSegment3D segment : segments.values()){
	    		segment.setLabel(labels[index]);
	    		index++;
	    	}			 
	 
	    }

	    private void defaultPolygonsLabels() {
	    	for (ConstructionElementCycle key : polygonsIndex.keySet()){
	    		StringBuffer sb = new StringBuffer();
	    		sb.append("face"); //TODO translation
	    		
	    		//stores points names and find the first
	    		String[] points = new String[key.size()];
	    		int indexFirstPointName=0;	    		
	    		int i=0;
	    		for(Iterator<ConstructionElement> it = key.iterator();it.hasNext();){
	    			points[i]=((GeoElement) it.next()).getLabel();
	    			if (points[i].compareToIgnoreCase(points[indexFirstPointName])<0)
	    				indexFirstPointName = i;
	    			i++;
	    		}
	    		
	    		//sets the direction to the next first name
	    		int indexSecondPointPlus = indexFirstPointName+1;
	    		if (indexSecondPointPlus==points.length)
	    			indexSecondPointPlus=0;
	    		int indexSecondPointMinus = indexFirstPointName-1;
	    		if (indexSecondPointMinus==-1)
	    			indexSecondPointMinus=points.length-1;
	    		
	    		if (points[indexSecondPointPlus]
	    		           .compareToIgnoreCase(points[indexSecondPointMinus])<0){
	    			for (int j=indexFirstPointName;j<points.length;j++)
		    			sb.append(points[j]);
		    		for (int j=0;j<indexFirstPointName;j++)
		    			sb.append(points[j]);
	    		}else{
	    			for (int j=indexFirstPointName;j>=0;j--)
		    			sb.append(points[j]);
		    		for (int j=points.length-1;j>indexFirstPointName;j--)
		    			sb.append(points[j]);
	    		}
	    		
	    		
	    		polygons.get(polygonsIndex.get(key)).setLabel(sb.toString());
	    	}	
	    }


	    private void defaultSegmentLabels() {
	    	for (ConstructionElementCycle key : segmentsIndex.keySet()){
	    		StringBuffer sb = new StringBuffer();
	    		sb.append("edge"); //TODO translation
	    		String[] points = new String[2];
	    		int i=0;
	    		for(Iterator<ConstructionElement> it = key.iterator();it.hasNext();){
	    			points[i]=((GeoElement) it.next()).getLabel();
	    			i++;
	    		}
	    		//sets the points names in order
	    		if (points[0].compareToIgnoreCase(points[1])<0){
	    			sb.append(points[0]);
	    			sb.append(points[1]);
	    		}else{
	    			sb.append(points[1]);
	    			sb.append(points[0]);
	    		}
	    		segments.get(segmentsIndex.get(key)).setLabel(sb.toString());
	    	}	
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


	 //TODO remove this and replace with tessellation
	 public void setInteriorPoint(GgbVector point){
		 for (GeoPolygon3D polygon : polygons.values()){
			 polygon.setInteriorPoint(point);
		 }
	 }

	 
	 
	 public void setEuclidianVisible(boolean visible) {
		 
		 super.setEuclidianVisible(visible);

		 for (GeoPolygon3D polygon : polygons.values()){
			 polygon.setEuclidianVisible(visible,false);
		 }

		 for (GeoSegment3D segment : segments.values()){
			 segment.setEuclidianVisible(visible);
		 }
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
	 
	 



	 
		


		public void update() {

			for (GeoPolygon3D polygon : polygons.values()){
				polygon.update();
			}

			for (GeoSegment3D segment : segments.values()){
				segment.update();
			}


		}
		   
	 
		
		/**
		 * update the polygons and the segments from their parent algorithms
		 */
		public void updatePolygonsAndSegmentsFromParentAlgorithms() {

			for (GeoPolygon3D polygon : polygons.values()){
				//polygon.updateCoordSysAndPoints2D();
				polygon.getParentAlgorithm().update();
			}

			for (GeoSegment3D segment : segments.values()){
				segment.getParentAlgorithm().update();
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


	
	public String getClassName() {
		return "GeoPolyhedron";
	}
	
	
	
	
	
	/** to be able to fill it with an alpha value */
	public boolean isFillable() {
		return true;
	}


	
	///////////////////////////////////////////
	// GeoElement3DInterface

	public GgbVector getLabelPosition(){
		return new GgbVector(4); //TODO
	}

	

}
