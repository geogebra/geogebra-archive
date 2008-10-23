package geogebra3D.kernel3D;


import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.linalg.GgbVector;

public class GeoTriangle3D extends GeoCoordSys2D {

	public GeoTriangle3D(Construction c, GeoPoint3D O, GeoPoint3D I, GeoPoint3D J) {
		super(c, O, I, J);
		
		
	}

	public GeoTriangle3D(Construction c, GgbVector A, GgbVector B, GgbVector C) {
		super(c, A, B.sub(A), C.sub(A));
		
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

	public GeoElement copy() {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	public boolean isDefined() {
		// TODO Raccord de méthode auto-généré
		return true;
	}

	public boolean isEqual(GeoElement Geo) {
		// TODO Raccord de méthode auto-généré
		return false;
	}

	public void set(GeoElement geo) {
		// TODO Raccord de méthode auto-généré
		
	}

	public void setUndefined() {
		// TODO Raccord de méthode auto-généré
		
	}

	protected boolean showInAlgebraView() {
		// TODO Raccord de méthode auto-généré
		return true;
	}

	protected boolean showInEuclidianView() {
		// TODO Raccord de méthode auto-généré
		return true;
	}

	public String toValueString() {
		// TODO Raccord de méthode auto-généré
		return "todo";
	}	

}
