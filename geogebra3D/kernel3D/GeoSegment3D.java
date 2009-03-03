package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.Path;
import geogebra3D.Matrix.Ggb3DVector;

/**
 * 
 * Class for 3D segments.
 * <p>
 * See {@link GeoCoordSys1D} for 1D coord sys abilities (matrix description, path for points).
 * 
 * 
 * @author ggb3d
 *
 */
public class GeoSegment3D extends GeoCoordSys1D {
	
	
	
	/** creates a segment linking p1 to p2
	 * @param c construction
	 * @param p1 start point
	 * @param p2 end point
	 */
	public GeoSegment3D(Construction c, GeoPoint3D p1, GeoPoint3D p2){
		super(c,p1,p2);
	}
	
	/** creates a segment linking v1 to v2
	 * @param c construction
	 * @param v1 start point
	 * @param v2 end point
	 */
	public GeoSegment3D(Construction c, Ggb3DVector v1, Ggb3DVector v2){
		super(c,v1,v2.sub(v1));
	}
	
	
	
	/** returns segment's length 
	 * @return length
	 */
	public double getLength(){
		return getUnit();
	}
	
	
	
	
	/**
	 * return "GeoSegment3D"
	 * @return "GeoSegment3D"
	 */
	protected String getClassName() {
		return "GeoSegment3D";
	}        
	
	/**
	 * return "Segment3D"
	 * @return "Segment3D"
	 */
   protected String getTypeString() {
		return "Segment3D";
	}
    
	/**
	 * return {@link GeoElement3D#GEO_CLASS_SEGMENT3D}
	 * @return {@link GeoElement3D#GEO_CLASS_SEGMENT3D}
	 */
   public int getGeoClassType() {
    	return GEO_CLASS_SEGMENT3D; 
    }

   /**
    * TODO return copy of this
    * @return copy of this
    */
	public GeoElement copy() {
		return null;
	}

	/**
	 * TODO return if this is defined
	 * @return if this is defined
	 */
	public boolean isDefined() {
		return true;
	}

	/**
	 * TODO return if this is equal to Geo
	 * @param GeoElement
	 * @return if this is equal to Geo
	 */
	public boolean isEqual(GeoElement Geo) {
		return false;
	}

	/**
	 * TODO set this to Geo
	 * @param GeoElement
	 */
	public void set(GeoElement geo) {
		
	}

	/**
	 * TODO set this to undefined
	 * 
	 */	
	public void setUndefined() {
		
	}

	
	/**
	 * TODO say if this is to be shown in algebra view
	 * @return if this is to be shown in algebra view
	 * 
	 */	
	protected boolean showInAlgebraView() {
		
		return true;
	}

	/**
	 * TODO say if this is to be shown in (3D) euclidian view
	 * @return if this is to be shown in (3D) euclidian view
	 * 
	 */	
	protected boolean showInEuclidianView() {
		
		return true;
	}

	/**
	 * TODO 
	 * @return "todo"
	 * 
	 */	
	public String toValueString() {
		
		return "todo";
	}
	
	
	/**
	 * return the length of the segment as a string 
	 * @return the length of the segment as a string 
	 * 
	 */	
	final public String toString() {
		
		String s = getLabel();
		
		s+=" = "; //TODO use kernel property
		
		
		//TODO undefined...
		s+=kernel.format(getLength());
		
		return s;
	}
	
	
	
	
	
	
	
	
	//Path3D interface
	
	/**
	 * return the 2D segment path linked to
	 * @return the 2D segment path linked to
	 */
	public Path getPath2D(){
		return (Path) getGeoElement2D();
	}
	

	/**
	 * {@inheritDoc}
	 */
	public GeoElement getGeoElement2D(){ 
		
		if (!hasGeoElement2D()){
			AlgoTo2D algo = new AlgoTo2D(cons, this);
			setGeoElement2D(algo.getOut());
		}
		return super.getGeoElement2D();
	}
	
	
	
}
