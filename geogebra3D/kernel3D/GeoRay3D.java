package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.main.Application;

public class GeoRay3D extends GeoLine3D {

	public GeoRay3D(Construction c, GeoPoint3D O) {
		super(c);
		setStartPoint(O);
        Application.debug("AlgoRayPointVector3D : constructor");

		// TODO Auto-generated constructor stub
	}
	
	public int getGeoClassType(){
		return GEO_CLASS_RAY3D;
		
	}
	
	protected String getTypeString(){
		return "Ray3D";
	}
	
	public boolean isDefined(){
		return true;
	}
	
	
	
	//Path3D interface
	public double getMinParameter() {
		return 0;
	}
	
	
	

}
