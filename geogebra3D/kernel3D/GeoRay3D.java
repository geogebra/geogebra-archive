package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoRayND;

public class GeoRay3D extends GeoLine3D implements GeoRayND{

	public GeoRay3D(Construction c, GeoPointND O, GeoPointND Q) {
		super(c, O, Q);
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
	
	
	

	protected GeoCoordSys1D create(Construction cons){
		return new GeoRay3D(cons);
	}
	
	//Path3D interface
	public double getMinParameter() {
		return 0;
	}
	
	
	public boolean isValidCoord(double x){
		return (x>=0);
	}
	

}
