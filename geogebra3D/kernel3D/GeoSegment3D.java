package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.linalg.GgbVector;

public class GeoSegment3D extends GeoCoordSys1D {
	
	/** creates a segment linking p1 to p2*/
	public GeoSegment3D(Construction c, GeoPoint3D p1, GeoPoint3D p2){
		super(c,p1,p2);
	}
	
	/** creates a segment linking v1 to v2*/
	public GeoSegment3D(Construction c, GgbVector v1, GgbVector v2){
		super(c,v1,v2.sub(v1));
	}
	
	
	
	/** returns segment's length */
	public double getLength(){
		return M.getColumn(1).norm();
	}
	
	
	
	
	
	protected String getClassName() {
		return "GeoSegment3D";
	}        
	
    protected String getTypeString() {
		return "Segment3D";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_SEGMENT3D; //TODO GEO_CLASS_POINT3D
    }
	
	
}
