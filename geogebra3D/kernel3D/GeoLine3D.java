package geogebra3D.kernel3D;

import geogebra.Matrix.GgbCoordSys;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.Kernel;
import geogebra.kernel.PathMover;

public class GeoLine3D extends GeoCoordSys1D {

	GeoPoint3D startPoint;
	
	/** creates a line joining O and I */
	public GeoLine3D(Construction c, GeoPoint3D O, GeoPoint3D I) {
		super(c, O, I);
	}

    public GeoLine3D(Construction c) {
		super(c);
	}

	protected GeoLine3D(Construction c, GgbVector o, GgbVector v) {
		super(c,o,v);
	}

	final void setStartPoint(GeoPoint3D P) {        	
    	startPoint = P;	    	
    }
    
	
	public GeoElement copy() {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	public int getGeoClassType() {
		
		return GEO_CLASS_LINE3D;
	}

	protected String getTypeString() {
		// TODO Raccord de méthode auto-généré
		return "Line3D";
	}



	public boolean isEqual(GeoElement Geo) {
		// TODO Raccord de méthode auto-généré
		return false;
	}

	public void set(GeoElement geo) {
		// TODO Raccord de méthode auto-généré

	}


	public boolean showInAlgebraView() {
		// TODO Raccord de méthode auto-généré
		return true;
	}

	protected boolean showInEuclidianView() {
		return true;
	}

	public String toValueString() {
		return toString();
	}
	
	
	
	final public String toString() {
		
		StringBuilder sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(": ");  //TODO use kernel property
		
		
		//TODO undefined...
		String parameter = "\u03bb";
		GgbVector O = coordsys.getOrigin();//TODO inhom coords
		GgbVector V = coordsys.getVx();

		sbToString.append("X = (");
		sbToString.append(kernel.format(O.get(1)));
		sbToString.append(", ");
		sbToString.append(kernel.format(O.get(2)));
		sbToString.append(", ");
		sbToString.append(kernel.format(O.get(3)));
		sbToString.append(") + ");
		sbToString.append(parameter);
		sbToString.append(" (");
		sbToString.append(kernel.format(V.get(1)));
		sbToString.append(", ");
		sbToString.append(kernel.format(V.get(2)));
		sbToString.append(", ");
		sbToString.append(kernel.format(V.get(3)));
		sbToString.append(")");
		
		return sbToString.toString();  
	}
	
	
	

	protected String getClassName() {
		// TODO Raccord de méthode auto-généré
		return "GeoLine3D";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Path3D interface
	
	
	public boolean isOnPath(GeoPointInterface p, double eps) {
		// TODO Auto-generated method stub
		return false;
	}



	public PathMover createPathMover() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getMaxParameter() {
		return Double.POSITIVE_INFINITY;
	}

	public double getMinParameter() {
		return Double.NEGATIVE_INFINITY;
	}

	public boolean isClosedPath() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	
	
	public boolean isValidCoord(double x){
		return true;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
