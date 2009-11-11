package geogebra3D.kernel3D;

import geogebra.kernel.Construction;

public class GeoRay3D extends GeoLine3D {

	public GeoRay3D(Construction c, GeoPoint3D O) {
		super(c);
		setStartPoint(O);
        
		// TODO Auto-generated constructor stub
	}
	
	public GeoRay3D(Construction construction) {
		super(construction);
	}

	public int getGeoClassType(){
		return GEO_CLASS_RAY3D;
		
	}
	
	protected String getTypeString(){
		return "Ray3D";
	}
	
	
	
	
	
	//Path3D interface
	public double getMinParameter() {
		return 0;
	}
	
	
	public boolean isValidCoord(double x){
		return (x>=0);
	}
	

}
