package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSegmentInterface;
import geogebra.kernel.Path;
import geogebra.kernel.PathParameter;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.Drawable3D;


/**
 * Class extending {@link GeoPolygon} in 3D world.
 * 
 * @author ggb3D
 *
 */
public class GeoPolygon3D 
extends GeoPolygon implements GeoElement3DInterface, Path, Region3D {

	
	/** 2D coord sys where the polygon exists */
	private GeoCoordSys2D coordSys; 
	
	/** link with drawable3D */
	private Drawable3D drawable3D = null;
	
	
	/** image of the 3D points in the coord sys*/
	private GeoPoint[] points2D;
	
	/** says if this is a part of a closed surface (e.g. a polyhedron) */
	private boolean isPartOfClosedSurface = false;
	
	
	private boolean createSegments = true;
	
	
	private boolean isPickable = true;
	
	/**
	 * default constructor
	 * @param c construction
	 * @param points 2D points
	 * @param cs2D 2D coord sys where the polygon is drawn
	 * @param createSegments says if the polygon has to creates its edges
	 */
	public GeoPolygon3D(Construction c, GeoPointInterface[] points, GeoCoordSys2D cs2D, boolean createSegments) {
		super(c, points, cs2D, createSegments);

		this.createSegments = createSegments;
		
		
	}
	
	/** common constructor for 3D.
	 * @param c the construction
	 * @param points vertices 
	 */
	public GeoPolygon3D(Construction c, GeoPointInterface[] points) {
		this(c,points,null,true);
	}


	/////////////////////////////////////////
	// GeoPolygon3D
	public int getGeoClassType() {
		return GeoElement3D.GEO_CLASS_POLYGON3D;
	}
	
	
	/**
	 * it's a 3D GeoElement.
	 * @return true
	 */
	public boolean isGeoElement3D(){
		return true;
	}

	
	/////////////////////////////////////////
	// Overwrite GeoPolygon
	
	
	
	 /**
	  * remove an old segment
	  * @param oldSegment the old segment 
	  */
	
	 public void removeSegment(GeoSegmentInterface oldSegment){
		 ((GeoSegment3D) oldSegment).getParentAlgorithm().remove();
	 }
	 
	 
	 
	 /**
	  * return a segment joining startPoint and endPoint
	  * @param startPoint the start point
	  * @param endPoint the end point
	  * @return the segment
	  */
	 public GeoSegmentInterface createSegment(GeoPointInterface startPoint, GeoPointInterface endPoint){
		 GeoSegmentInterface segment;

		 AlgoJoinPoints3D algoSegment = new AlgoJoinPoints3D(cons, 
				 (GeoPoint3D) startPoint, (GeoPoint3D) endPoint, this, GeoElement3D.GEO_CLASS_SEGMENT3D);            
		 cons.removeFromConstructionList(algoSegment);               

		 segment = (GeoSegmentInterface) algoSegment.getCS(); 
		 // refresh color to ensure segments have same color as polygon:
		 segment.setObjColor(getObjectColor()); 

		 return segment;
		 
		 
	 }
	 
	 
	 
	 /**
	  * Returns the i-th 2D point of this polygon.
	  * @param i number of point
	  * @return the i-th point
	  */
	 public GeoPoint getPoint(int i) {
		 return (GeoPoint) points2D[i];
	 }
	 
	 /**
	  * Returns the i-th 3D point of this polygon.
	  * @param i number of point
	  * @return the i-th point
	  */	 
	 public Ggb3DVector getPoint3D(int i){
		 return coordSys.getPoint(getPointX(i), getPointY(i));
	 }
	 
	 
	 /** return the normal of the polygon's plane
	 * @return the normal of the polygon's plane
	 */
	public Ggb3DVector getNormal(){
		
		
		if (interiorPoint==null)
			return coordSys.getMatrix4x4().getVz();
		
		Ggb3DVector vn = coordSys.getMatrix4x4().getVz();
		
		//Application.debug("polygon("+getLabel()+") : "+vn.dotproduct(interiorPoint.sub(getPoint3D(0))));

		if (vn.dotproduct(interiorPoint.sub(getPoint3D(0)))>0)
			return (Ggb3DVector) vn.mul(-1); //vn is oriented to interior
		else
			return vn; 
	 }
	
	
	/** interior point for oriented surfaces */
	//TODO remove this and replace with tesselation
	private Ggb3DVector interiorPoint = null;
	
	public void setInteriorPoint(Ggb3DVector point){
		interiorPoint = point;
	}
	 

	 /**
	  * Returns the 2D points of this polygon.
	  * Note that this array may change dynamically.
	  */
	 public GeoPoint [] getPoints() {
		 return (GeoPoint []) points2D;
	 }


	 

	 public void setEuclidianVisible(boolean visible) {

		 setEuclidianVisible(visible,createSegments);

	 }  
	 

	 protected String getClassName() {
		 return "GeoPolygon3D";
	 }

	
	/////////////////////////////////////////
	// link with the 2D coord sys
	
	
	/** set the 2D coordinate system
	 * @param cs the 2D coordinate system
	 */
	 public void setCoordSys(GeoElement cs){
		 
		 if (points==null)
			 return;
		 
		 setDefined();
		 
		 coordSys = (GeoCoordSys2D) cs;

		 /*
		 Application.debug("coordSys ="+coordSys);
		 
		 String s = "points = ";
		 for (int i=0;i<points.length;i++)
			 s+=points[i].getLabel()+", ";
		 Application.debug(s);
		 */
		 
		 // turn off the kernel notifications for algos below
		 this.getKernel().setSilentMode(true);
		 
		 // if there's no coord sys, create it with AlgoCoordSys2D
		 if (coordSys==null){
			 AlgoCoordSys2D algo = new AlgoCoordSys2D(this.getConstruction(),null,(GeoPoint3D[]) points,false);
			 coordSys = algo.getCoordSys();
		 }
		 
		 //Application.debug("coordSys ="+coordSys);
		 
		 //sets the 2D points in coord sys
		 points2D = new GeoPoint[points.length];
		 for(int i=0; i<points.length; i++){
			 points2D[i]=(GeoPoint) ((Kernel3D) this.getKernel()).From3Dto2D(null, (GeoPoint3D) points[i], this.coordSys);
			 //cons.removeFromConstructionList(points2D[i]);    
		 }
		 
		 // turn on the kernel notifications 
		 this.getKernel().setSilentMode(false);
		 
			 
	}
	 
	 
	 /**
	 * update the coord system and 2D points
	 */
	public void updateCoordSysAndPoints2D(){
		 
		 getCoordSys().getParentAlgorithm().update();
		 
		 for(int i=0; i<points2D.length; i++)
			 points2D[i].getParentAlgorithm().update();
	 }
	
	
	
	/** return the 2D coordinate system
	 * @return the 2D coordinate system
	 */
	public GeoCoordSys2D getCoordSys(){
		return coordSys;
	}
	
	
	/** return true if there's a polygon AND a 2D coord sys */
	public boolean isDefined() {
		if (coordSys==null)
			return false;
		else
			return super.isDefined() && coordSys.isDefined();
			//return coordSys.isDefined();
   }	
	
	
	
	/////////////////////////////////////////
	// link with Drawable3D
	
	/**
	 * set the 3D drawable linked to
	 * @param d the 3D drawable 
	 */
	public void setDrawable3D(Drawable3D d){
		drawable3D = d;
	}
	
	/** return the 3D drawable linked to
	 * @return the 3D drawable linked to
	 */
	public Drawable3D getDrawable3D(){
		return drawable3D;
	}
	
	
	
	
	
	public Ggb3DMatrix4x4 getDrawingMatrix() {
		
		//Application.debug("coordSys="+coordSys);
		
		if (coordSys!=null)
			return coordSys.getDrawingMatrix();
		else
			return null;
	}
	
	 public Ggb3DMatrix4x4 getLabelMatrix(){
		 if (coordSys!=null)
			 return coordSys.getLabelMatrix();
		 else
			 return null;
	 }

	
	public void setDrawingMatrix(Ggb3DMatrix4x4 matrix) {
		coordSys.setDrawingMatrix(matrix);

	}

	
	
	 /** says if the object is pickable
	  * @return true if the object is pickable
	  */
	 public boolean isPickable(){
		 return isPickable;
	 }
	 
	
    /** set the alpha value to alpha for openGL
     * @param alpha alpha value
     */
	public void setAlphaValue(float alpha) {

		alphaValue = alpha;

	}
	
	
	
	
	/** set if this is a part of a closed surface
	 * @param v
	 */
	public void setIsPartOfClosedSurface(boolean v){
		isPartOfClosedSurface = v;
	}
	
	
	
	/** if this is a part of a closed surface
	 * @return if this is a part of a closed surface
	 */
	public boolean isPartOfClosedSurface(){
		return isPartOfClosedSurface;
	}
	
	
	
	
	
	
	
	public GeoElement getGeoElement2D() {
		return null;
	}

	public boolean hasGeoElement2D() {
		return false;
	}


	public void setGeoElement2D(GeoElement geo) {

	}

	
	
	
	///////////////////////////////////
	// Path interface
	
	
	
	
	
	//TODO merge with GeoPolygon
	public void pathChanged(GeoPointInterface PI) {		
		
		GeoPoint3D P = (GeoPoint3D) PI;
		
		PathParameter pp = P.getPathParameter();
		
		//remember old parameter
		double oldT = pp.getT();
		
		//find the segment where the point lies
		int index = (int) pp.getT();
		GeoSegmentInterface seg = segments[index];
		
		//sets the path parameter for the segment, calc the new position of the point
		pp.setT(pp.getT() - index);		
		seg.pathChanged(P);
		
		//recall the old parameter
		pp.setT(oldT);
	}
	
	
	//TODO merge with GeoPolygon
	public void pointChanged(GeoPointInterface PI) {
		
		GeoPoint3D P = (GeoPoint3D) PI;
		
		//Ggb3DVector coordOld = P.getInhomCoords();
		
		double minDist = Double.POSITIVE_INFINITY;
		Ggb3DVector res = null;
		double param=0;
		
		// find closest point on each segment
		PathParameter pp = P.getPathParameter();
		for (int i=0; i < segments.length; i++) {
			//P.setCoords(coordOld,false); //prevent circular path.pointChanged
			segments[i].pointChanged(P);			

			//double dist = P.getInhomCoords().sub(coordOld).squareNorm();			
			double dist = 0;
			if (P.getWillingCoords()!=null && P.getWillingDirection()!=null)
				dist=P.getInhomCoords().distLine(P.getWillingCoords(), P.getWillingDirection());

			//Application.debug("distance au segment "+i+" : "+dist);
			if (dist < minDist) {
				minDist = dist;
				// remember closest point
				res = P.getInhomCoords();
				param = i + pp.getT();
			}
		}				
			
		P.setCoords(res,false);
		pp.setT(param);	
	}	
	
	
	
	

	
	
	
	
	///////////////////////////////////
	// REGION3D INTERFACE
	
	public void setRegionChanged(GeoPointInterface PI, double x, double y){
		
		//Application.debug("x = "+x+", y = "+y);
		((GeoPoint3D) PI).setCoords2D(x, y);
		((GeoPoint3D) PI).updateCoordsFrom2D(false);
	}
	
	public boolean isInRegion(GeoPointInterface PI){
		
		return isInRegion(PI.getX2D(), PI.getY2D());
		
	}
	
	public Ggb3DVector getPoint(double x2d, double y2d){
		return coordSys.getPoint(x2d,y2d);
	}

	
	/** return the normal projection of the (coords) point on the region 
	 * @param coords coords of the point
	 * @return normal projection
	 */
	public Ggb3DVector[] getNormalProjection(Ggb3DVector coords){
		//return coords.projectPlane(coordSys.getMatrix4x4());
		return coordSys.getNormalProjection(coords);
	}


	/** return the willingDirection projection of the (coords) point on the region 
	 * @param coords coords of the point
	 * @param willingDirection direction of the projection
	 * @return projection
	 */
	public Ggb3DVector[] getProjection(Ggb3DVector coords, Ggb3DVector willingDirection){
		//return coords.projectPlaneThruV(coordSys.getMatrix4x4(),willingDirection);
		return coordSys.getProjection(coords, willingDirection);
	}

	
	
	/** sets the pickability of the object
	 * @param v pickability
	 */
	public void setIsPickable(boolean v){
		isPickable = v;
	}

	

}
