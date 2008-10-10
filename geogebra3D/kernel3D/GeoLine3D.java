package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;

public class GeoLine3D extends GeoCoordSys1D {

	
	/** creates a line joining O and I */
	public GeoLine3D(Construction c, GeoPoint3D O, GeoPoint3D I) {
		super(c, O, I);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	public GeoElement copy() {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	public int getGeoClassType() {
		// TODO Raccord de méthode auto-généré
		return GEO_CLASS_LINE3D;
	}

	protected String getTypeString() {
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

	protected String getClassName() {
		// TODO Raccord de méthode auto-généré
		return "GeoLine3D";
	}

}
