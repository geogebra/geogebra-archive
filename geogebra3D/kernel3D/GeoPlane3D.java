package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;

public class GeoPlane3D extends GeoCoordSys2D {
	
	double xmin, xmax, ymin, ymax; //for drawing

	
	/** creates a plane with origin o, vectors v1, v2*/
	public GeoPlane3D(Construction c, 
			GgbVector o, GgbVector v1, GgbVector v2,
			double xmin, double xmax, double ymin, double ymax){
		
		super(c,o,v1,v2);
		this.xmin = xmin; this.xmax = xmax;
		this.ymin = ymin; this.ymax = ymax;
	}
	
	
	/** returns a matrix for drawing */
	public GgbMatrix getDrawingMatrix(){
		GgbMatrix m = new GgbMatrix(4,4);
		GgbVector o = getPoint(xmin,ymin);
		GgbVector px = getPoint(xmax,ymin);
		GgbVector py = getPoint(xmin,ymax);
		m.set(new GgbVector[] {px.sub(o),py.sub(o),Vn,o});
		return m;
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
