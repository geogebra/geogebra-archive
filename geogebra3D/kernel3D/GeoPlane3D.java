package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
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
	
	
	/** set the matrix to [V1 V2 O] */
	public void setCoord(GgbVector O, GgbVector V1, GgbVector V2){
	
		super.setCoord(O, V1, V2);

		
		
	}
	
	
	/** returns a matrix for drawing */
	public GgbMatrix getDrawingMatrix(){
		GgbMatrix m = new GgbMatrix(4,4);
		GgbVector o = getPoint(xmin,ymin);
		GgbVector px = getPoint(xmax,ymin);
		GgbVector py = getPoint(xmin,ymax);
		m.set(new GgbVector[] {px.sub(o),py.sub(o),Vn,o});
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
		GgbMatrix m = new GgbMatrix(4,4);
		GgbVector p1 = getPoint(l,ymin);
		GgbVector p2 = getPoint(l,ymax);
		m.set(new GgbVector[] {p2.sub(p1),Vn,M.getColumn(1),p1});
		//m.SystemPrint();
		return m;
		
	}

	/** returns a matrix for drawing a segment, equation y=l (x=xmin..xmax) */
	public GgbMatrix getDrawingYMatrix(double l){
		GgbMatrix m = new GgbMatrix(4,4);
		GgbVector p1 = getPoint(xmin,l);
		GgbVector p2 = getPoint(xmax,l);
		m.set(new GgbVector[] {p2.sub(p1),M.getColumn(2),Vn,p1});
		//m.SystemPrint();
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
	
	
}
