package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.linalg.GgbMatrix;

public class GeoCoordSys extends GeoElement3D{


	GgbMatrix M;

	
	public GeoCoordSys(Construction c) {
		super(c);
		
	}
	

	
	public GgbMatrix getMatrix(){
		return M;
	}
	

	
	
	public GeoElement copy() {
		// TODO Auto-generated method stub
		return null;
	}
	public int getGeoClassType() {
		// TODO Auto-generated method stub
		return 0;
	}
	protected String getTypeString() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isDefined() {
		// TODO Auto-generated method stub
		return true;
	}
	public void set(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}
	public void setUndefined() {
		// TODO Auto-generated method stub
		
	}
	protected boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return true;
	}
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}
	public String toValueString() {
		// TODO Auto-generated method stub
		return "geocoordsys";
	}
	protected String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}



	public boolean isEqual(GeoElement Geo) {
		// TODO Raccord de méthode auto-généré
		return false;
	};
}
