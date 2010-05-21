package geogebra3D.kernel3D;

import geogebra.Matrix.GgbCoordSys;
import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.Functional2Var;

public class GeoPlane3D extends GeoCoordSys2DAbstract
implements Functional2Var {
	
	double xmin, xmax, ymin, ymax; //for drawing
	
	//grid and plate
	boolean gridVisible = false;
	boolean plateVisible = true;
	double dx = 1.0; //distance between two marks on the grid //TODO use object properties
	double dy = 1.0; 
	

	//string
	protected static final char[] VAR_STRING = {'x','y','z'};
	

	/**
	 * creates an empty plane
	 * @param c construction
	 */
	public GeoPlane3D(Construction c){
		super(c);

		this.xmin = -2.5; this.xmax = 2.5;
		this.ymin = -2.5; this.ymax = 2.5;	
		
		//grid
		setGridOrigin(new GgbVector(new double[] {0,0,0,1}));
		setGridVisible(false);
        
		
		//alpha
		//setAlphaValue(ConstructionDefaults3D.DEFAULT_PLANE_ALPHA);
		
	}
	
	/** creates a plane with origin o, vectors v1, v2*/
	public GeoPlane3D(Construction c, 
			GgbVector o, GgbVector v1, GgbVector v2,
			double xmin, double xmax, double ymin, double ymax){
		
		
		super(c,o,v1,v2);
		this.xmin = xmin; this.xmax = xmax;
		this.ymin = ymin; this.ymax = ymax;

		//grid
		setGridOrigin(new GgbVector(new double[] {0,0,0,1}));
        
		
		//alpha
		//setAlphaValue(ConstructionDefaults3D.DEFAULT_PLANE_ALPHA);

		
	}
	
	
	

	///////////////////////////////////
	// REGION INTERFACE
	
	
	public boolean isRegion(){
		return true;
	}
	

	
	
	
	
	///////////////////////////////////
	// GRID AND PLATE
	
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
	
	/** set grid distances (between two ticks)
	 * @param dx
	 * @param dy
	 */
	public void setGridDistances(double dx, double dy){
		this.dx = dx;
		this.dy = dy;
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
	public boolean isGridVisible(){
		return gridVisible && isEuclidianVisible();
	}
	
	public void setGridVisible(boolean grid){
		gridVisible = grid;
	}
	
	/** returns if there is a plate visible */
	public boolean isPlateVisible(){
		return plateVisible && isEuclidianVisible();
	}
	
	public void setPlateVisible(boolean flag){
		plateVisible = flag;
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


	public boolean showInAlgebraView() {		
		return true;
	}


	protected boolean showInEuclidianView() {
		// TODO Raccord de méthode auto-généré
		return true;
	}


	public String toValueString() {
		// TODO Raccord de méthode auto-généré
		return toString();
	}
	
	

	
	
	final public String toString() {
		
		StringBuilder sbToValueString = getSbToString();
		sbToValueString.setLength(0);
		sbToValueString.append(label);
		sbToValueString.append(": ");   
		
		
		
		//TODO undefined...
		//TODO remove x/y/z if not needed
		//TODO check this
		
		int dimension = 3;
		boolean first = true; //says if it's the first coeff

		GgbVector Vn = getMatrix4x4().getColumn(3);
		
		for (int i=0; i<dimension; i++){
			
			double val = Vn.get(i+1);
			
			if (!kernel.isZero(val)) {
				if (val<0)
					sbToValueString.append("- ");
				else
					if (!first)
						sbToValueString.append("+ ");
				
				val = Math.abs(val);
				if (!kernel.isZero(val-1)){
					sbToValueString.append(kernel.format(val));
					sbToValueString.append(" ");
				}
				sbToValueString.append(VAR_STRING[i]);
				sbToValueString.append(" ");
				first=false;
			} 	
		}
		
		sbToValueString.append("= ");
		sbToValueString.append(kernel.format(Vn.dotproduct(getMatrix().getColumn(3)))); 
		
		/*
		sbToValueString.append(kernel.format(Vn.get(1)));   
		sbToValueString.append("x + "); 
		sbToValueString.append(kernel.format(Vn.get(2))); 
		sbToValueString.append("y + "); 
		sbToValueString.append(kernel.format(Vn.get(3))); 
		sbToValueString.append("z = "); 
		sbToValueString.append(kernel.format(Vn.dotproduct(getMatrix().getColumn(3)))); 
		*/
		
		return sbToValueString.toString();  
	}

	
	
	/** to be able to fill it with an alpha value */
	public boolean isFillable() {
		return true;
	}
	
	
	
	
	/////////////////////////////////////////////
	// 2 VAR FUNCTION INTERFACE
	////////////////////////////////////////////

	public GgbVector evaluateNormal(double u, double v) {
		
		return getMatrix4x4().getVz();
	}

	public GgbVector evaluatePoint(double u, double v) {
		GgbMatrix4x4 m = getMatrix4x4();
		return (GgbVector) m.getOrigin().add(m.getVx().mul(u)).add(m.getVy().mul(v));
	}
	

	public double getMinParameter(int index) {

		return 0; //TODO



	}


	public double getMaxParameter(int index) {

		return 0; //TODO

	}

}
