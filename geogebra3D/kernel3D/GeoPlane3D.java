package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;

public class GeoPlane3D extends GeoCoordSys2D {
	
	double xmin, xmax, ymin, ymax; //for drawing
	
	//grid
	boolean m_grid = true;
	double dx = 1.0; //distance between two marks on the grid //TODO use object properties
	double dy = 1.0; 
	

	
	/** creates a plane with origin o, vectors v1, v2*/
	public GeoPlane3D(Construction c, 
			Ggb3DVector o, Ggb3DVector v1, Ggb3DVector v2,
			double xmin, double xmax, double ymin, double ymax){
		
		
		super(c,o,v1,v2);
		this.xmin = xmin; this.xmax = xmax;
		this.ymin = ymin; this.ymax = ymax;

		//grid
		setGridOrigin(new Ggb3DVector(new double[] {0,0,0,1}));
        

		
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
	
	
	/** returns min/max on x/y */
	public double getXmin(){
		return xmin;
	}
	public double getYmin(){
		return ymin;
	}
	public double getXmax(){
		return xmax;
	}
	public double getYmax(){
		return ymax;
	}
	
	


	
	
	/** returns if there is a grid to plot or not */
	public boolean hasGrid(){
		return m_grid;
	}
	
	public void setGrid(boolean a_grid){
		m_grid = a_grid;
	}
	
	
	/** returns x delta for the grid */
	public double getGridXd(){
		return dx; 
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
		Ggb3DVector Vn = getMatrix4x4().getColumn(3);
		s+=kernel.format(Vn.get(1))+"x + "+kernel.format(Vn.get(2))+"y + "+kernel.format(Vn.get(3))+"z = "
			+kernel.format(Vn.dotproduct(getMatrix().getColumn(3)));
		
		return s;
	}

	
	
	
}
