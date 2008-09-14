package geogebra3D.kernel3D;


import geogebra.Application;
import geogebra.kernel.Construction;
import geogebra.kernel.linalg.GgbVector;

public class GeoTriangle3D extends GeoCoordSys2D {

	public GeoTriangle3D(Construction c, GeoPoint3D O, GeoPoint3D I, GeoPoint3D J) {
		super(c, O, I, J);
		//Application.debug("new -- points");
		
	}

	public GeoTriangle3D(Construction c, GgbVector A, GgbVector B, GgbVector C) {
		super(c, A, B.sub(A), C.sub(A));//,0,0,1,1);
		
	}

	
	
	
	
	
	protected String getClassName() {
		return "GeoTriangle3D";
	}        
	
    protected String getTypeString() {
		return "Triangle3D";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_TRIANGLE3D; 
    }	

}
