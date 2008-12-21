package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;

public class GeoPlane3D extends GeoCoordSys2D {
	
	double xmin, xmax, ymin, ymax; //for drawing
	
	//grid
	double dx = 1.0; //distance between two marks on the grid //TODO use object properties
	double dy = 1.0; 
	

	
	/** creates a plane with origin o, vectors v1, v2*/
	public GeoPlane3D(Construction c, 
			GgbVector o, GgbVector v1, GgbVector v2,
			double xmin, double xmax, double ymin, double ymax){
		
		
		super(c,o,v1,v2);
		this.xmin = xmin; this.xmax = xmax;
		this.ymin = ymin; this.ymax = ymax;

		//grid
		setGridOrigin(new GgbVector(new double[] {0,0,0,1}));
        

		
	}
	
	

	
	
	/** returns a matrix for drawing */
	public GgbMatrix getDrawingMatrix(){
		GgbMatrix m = getMatrix4x4().copy();
		
		GgbVector o = getPoint(xmin,ymin);
		GgbVector px = getPoint(xmax,ymin);
		GgbVector py = getPoint(xmin,ymax);
		m.set(px.sub(o), 1);m.set(py.sub(o), 2);m.set(o, 4);
		return m;
	}	
	
	
	
	
	///////////////////////////////////
	// grid
	
	/** sets corners of the grid */
	public void setGridCorners(double x1, double  y1, double  x2, double  y2){
		if (x1<x2){
			this.xmin = x1;
			this.xmax = x2;
		}else{
			this.xmin = x2;
			this.xmax = x1;
		}
		if (y1<y2){
			this.ymin = y1;
			this.ymax = y2;
		}else{
			this.ymin = y2;
			this.ymax = y1;
		}
	
	}
	
	/** returns a matrix for drawing a segment, equation x=l (y=ymin..ymax) */
	public GgbMatrix getDrawingXMatrix(double l){
		GgbMatrix m = getMatrix4x4().copy();
		
		GgbVector p1 = getPoint(l,ymin);
		GgbVector p2 = getPoint(l,ymax);

		m.set(m.getColumn(3), 2);m.set(m.getColumn(1), 3);
		m.set(p2.sub(p1), 1);
		m.set(p1, 4);
		return m;
		
	}

	/** returns a matrix for drawing a segment, equation y=l (x=xmin..xmax) */
	public GgbMatrix getDrawingYMatrix(double l){
		GgbMatrix m = getMatrix4x4().copy();
		
		GgbVector p1 = getPoint(xmin,l);
		GgbVector p2 = getPoint(xmax,l);
		
		
		m.set(p2.sub(p1), 1);
		m.set(p1, 4);
		return m;
		
	}
	
	
	/** returns first x on the grid */
	public double getGridXmin(){
		double n = Math.floor((xmin-x0)/dx)  + 1.0;
		return x0+n*dx;
	}
	
	/** returns last x on the grid */
	public double getGridXmax(){
		double n = Math.floor((xmax-x0)/dx);
		return x0+n*dx;
	}
	
	/** returns x delta for the grid */
	public double getGridXd(){
		return dx; 
	}


	
	/** returns first y on the grid */
	public double getGridYmin(){
		double n = Math.floor((ymin-y0)/dy)  + 1.0;
		return y0+n*dy;
	}
	
	/** returns last y on the grid */
	public double getGridYmax(){
		double n = Math.floor((ymax-y0)/dy);
		return y0+n*dy;
	}
	
	/** returns y delta for the grid */
	public double getGridYd(){
		return dy; 
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected String getClassName() {
		return "GeoPlane3D";
	}        
	
    protected String getTypeString() {
		return "Plane3D";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_PLANE3D; 
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
		
		s+=" : "; 
		
		
		
		//TODO undefined...
		//TODO remove x/y/z if not needed
		//TODO check this
		GgbVector Vn = getMatrix4x4().getColumn(3);
		s+=kernel.format(Vn.get(1))+"x + "+kernel.format(Vn.get(2))+"y + "+kernel.format(Vn.get(3))+"z = "
			+kernel.format(Vn.dotproduct(getMatrix().getColumn(3)));
		
		return s;
	}

	
	
	
}
