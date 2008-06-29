package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.linalg.GgbVector;

public class GeoPlane3D extends GeoCoordSys2D {
	

	
	/** creates a plane with origin o, vectors v1, v2*/
	public GeoPlane3D(Construction c, GgbVector o, GgbVector v1, GgbVector v2){
		super(c,o,v1,v2);
	}
	
	
	
	
	
	
	
	
	protected String getClassName() {
		return "GeoPlane3D";
	}        
	
    protected String getTypeString() {
		return "Plane3D";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_PLANE3D; 
    }
	
	
}
