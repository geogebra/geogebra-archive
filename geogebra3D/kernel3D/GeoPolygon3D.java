package geogebra3D.kernel3D;


import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;

public class GeoPolygon3D extends GeoCoordSys2D {

	public GeoPolygon3D(Construction c, GeoPoint3D O, GeoPoint3D I, GeoPoint3D J) {
		super(c, O, I, J);
		
	}
	
	
	public GeoPolygon3D(Construction c, GeoPoint3D[] points) {
		super(c);
		M=new GgbMatrix(4,3);
		setCoord(points);	

	}	
	
	
	public void setCoord(GeoPoint3D[] points) {
		if (points.length<3)
			this.setUndefined();
		else{
			this.setCoord(points[0],points[1],points[2]);
			//TODO process for more than 3 points			
		}
		
	}
	
	
	

	public GeoPolygon3D(Construction c, GgbVector A, GgbVector B, GgbVector C) {
		super(c, A, B.sub(A), C.sub(A));
		
	}

	
	
	public double getArea(){
		return getUnitArea()/2;
	}
	
	
	
	
	protected String getClassName() {
		return "GeoPolygon3D";
	}        
	
    protected String getTypeString() {
		return "Polygon3D";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_POLYGON3D; 
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
	
	
	
	final public String toString() {
		
		String s = getLabel();
		
		s+=" = "; //TODO use kernel property
		
		
		//TODO undefined...
		s+=kernel.format(getArea());
		
		return s;
	}	
	
	

}
