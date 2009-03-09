package geogebra3D.kernel3D;

import geogebra.kernel.AlgoJoinPointsSegment;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoSegmentInterface;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.euclidian3D.Drawable3D;
import geogebra.kernel.GeoPolygon;


/**
 * Class extending {@link GeoPolygon} in 3D world.
 * 
 * @author ggb3D
 *
 */
public class GeoPolygon3D 
extends GeoPolygon implements GeoElement3DInterface {

	
	/** 2D coord sys where the polygon exists */
	private GeoCoordSys2D coordSys; 
	
	/** link with drawable3D */
	private Drawable3D drawable3D = null;
	
	/**
	 * default constructor
	 * @param c construction
	 * @param points 2D points
	 * @param cs2D 2D coord sys where the polygon is drawn
	 */
	public GeoPolygon3D(Construction c, GeoPoint[] points, GeoCoordSys2D cs2D) {
		super(c, points, cs2D);
		setUseVisualDefaults(false);
		setAlphaValue(ConstructionDefaults3D.DEFAULT_POLYGON3D_ALPHA);
		
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
	
	 public GeoSegmentInterface createSegment(GeoPoint startPoint, GeoPoint endPoint){
		 GeoSegmentInterface segment;

		 AlgoJoinPoints3D algoSegment = new AlgoJoinPoints3D(cons, startPoint, endPoint, this,GeoElement3D.GEO_CLASS_SEGMENT3D);            
		 cons.removeFromConstructionList(algoSegment);               

		 segment = (GeoSegmentInterface) algoSegment.getCS(); 
		 // refresh color to ensure segments have same color as polygon:
		 segment.setObjColor(getObjectColor()); 

		 return segment;
		 
		 
	 }
	 
	
	
	
	/////////////////////////////////////////
	// link with the 2D coord sys
	
	
	/** set the 2D coordinate system
	 * @param cs the 2D coordinate system
	 */
	 public void setCoordSys(GeoElement cs){
		 this.coordSys = (GeoCoordSys2D) cs;
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
			//TODO return super.isDefined() && coordSys.isDefined();
			return coordSys.isDefined();
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
		if (coordSys!=null)
			return coordSys.getDrawingMatrix();
		else
			return null;
	}

	
	public void setDrawingMatrix(Ggb3DMatrix4x4 matrix) {
		coordSys.setDrawingMatrix(matrix);

	}

	
	
	
	
    /** set the alpha value to alpha for openGL
     * @param alpha alpha value
     */
	public void setAlphaValue(float alpha) {

		alphaValue = alpha;

	}
	
	
	
	public GeoElement getGeoElement2D() {
		return null;
	}

	public boolean hasGeoElement2D() {
		return false;
	}


	public void setGeoElement2D(GeoElement geo) {

	}

}
