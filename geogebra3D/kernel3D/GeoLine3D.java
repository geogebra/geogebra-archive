package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.linalg.GgbVector;

public class GeoLine3D extends GeoCoordSys1D {

	GeoPoint3D startPoint;
	
	/** creates a line joining O and I */
	public GeoLine3D(Construction c, GeoPoint3D O, GeoPoint3D I) {
		super(c, O, I);
	}

    public GeoLine3D(Construction c) {
		super(c);
	}

	final void setStartPoint(GeoPoint3D P) {        	
    	startPoint = P;	    	
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
		return "Line3D";
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
	
	
	
	final public String toString() {
		
		String s = getLabel();
		
		s+=" : "; //TODO use kernel property
		
		
		//TODO undefined...
		String parameter = "\u03bb";
		GgbVector O = getMatrix().getColumn(2);//TODO inhom coords
		GgbVector V = getMatrix().getColumn(1);
		s+="X = ("+kernel.format(O.get(1))+", "+kernel.format(O.get(2))+", "+kernel.format(O.get(3))+") + "
			+parameter+" ("+kernel.format(V.get(1))+", "+kernel.format(V.get(2))+", "+kernel.format(V.get(3))+")";
		
		return s;
	}
	
	
	

	protected String getClassName() {
		// TODO Raccord de méthode auto-généré
		return "GeoLine3D";
	}

}
