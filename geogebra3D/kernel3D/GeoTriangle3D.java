package geogebra3D.kernel3D;


import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DVector;

/*
 * This class will disappear int GeoPolygon3D 
 */

public class GeoTriangle3D extends GeoCoordSys2D {

	public GeoTriangle3D(Construction c, GeoPoint3D O, GeoPoint3D I, GeoPoint3D J) {
		super(c, O, I, J);
		
	}
	
	
	public GeoTriangle3D(Construction c, GeoPoint3D[] points) {
		super(c);
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
	
	
	

	public GeoTriangle3D(Construction c, Ggb3DVector A, Ggb3DVector B, Ggb3DVector C) {
		super(c, A, B.sub(A), C.sub(A));
		
	}

	
	
	public double getArea(){
		return getUnitArea()/2;
	}
	
	
	
	
	
	
	
	////////////////////////////////////////
	// Path2D interface
	
	public boolean isLimitedPath(){
		return true;
	}

	public void limitPathParameters(PathParameters pps){
		//TODO adapt to a polygon, using orthogonal directions
		double t1 = pps.getT(0);
		double t2 = pps.getT(1);
		
		if (t1<0)
			t1=0;
		if (t2<0)
			t2=0;
		if (t1+t2>1){
			double t=t1+t2;
			t1=t1/t;
			t2=t2/t;
		}
		
		pps.setTs(new double[] {t1,t2});
	}		
	
	
	
	protected String getClassName() {
		return "GeoPolygon3D";
	}        
	
    protected String getTypeString() {
		return "Polygon3D";
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
	
	
	
	final public String toString() {
		
		String s = getLabel();
		
		s+=" = "; //TODO use kernel property
		
		
		//TODO undefined...
		s+=kernel.format(getArea());
		
		return s;
	}	
	
	

}
