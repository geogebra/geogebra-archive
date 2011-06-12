package geogebra3D.kernel3D;

import geogebra.Matrix.CoordMatrix;
import geogebra.Matrix.CoordMatrix4x4;
import geogebra.Matrix.CoordSys;
import geogebra.Matrix.Coords;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.RegionParameters;
import geogebra.kernel.arithmetic.Functional2Var;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra3D.euclidianForPlane.EuclidianViewForPlane;

public class GeoPlane3D extends GeoElement3D
implements Functional2Var, GeoCoordSys2D, GeoCoords4D, GeoPlaneND{
	
	
	
	/** default labels */
	private static final char[] Labels = {'p', 'q', 'r'};	
	
	private static boolean KEEP_LEADING_SIGN = true;
	
	
	double xmin, xmax, ymin, ymax; //for drawing
	
	//grid and plate
	boolean gridVisible = false;
	boolean plateVisible = true;
	double dx = 1.0; //distance between two marks on the grid //TODO use object properties
	double dy = 1.0; 
	
	/** coord sys */
	protected CoordSys coordsys;

	//string
	protected static final String[] VAR_STRING = {"x","y","z"};
	

	/**
	 * creates an empty plane
	 * @param c construction
	 */
	public GeoPlane3D(Construction c){
		super(c);
		
		coordsys = new CoordSys(2);

		this.xmin = -2.5; this.xmax = 2.5;
		this.ymin = -2.5; this.ymax = 2.5;	
		
		//grid
		setGridVisible(false);
        		
	}
	
	
	public GeoPlane3D(Construction cons, String label, double a, double b, double c, double d){
		this(cons);
		
		setEquation(a, b, c, d);
		setLabel(label);
		
		
		
	}
	
	private void setEquation(double a, double b, double c, double d, boolean makeCoordSys){

		setEquation(new double[] {a,b,c,d},makeCoordSys);
		
	}
	
	public void setEquation(double a, double b, double c, double d){
		
		setEquation(a,b,c,d,true);
	}
	
	public void setCoords(double x, double y, double z, double w){
     	setEquation(x, y, z, w,false);
    }
	
	private void setEquation(double[] v, boolean makeCoordSys){

		if (makeCoordSys || !getCoordSys().isDefined()){
			getCoordSys().makeCoordSys(v);
			getCoordSys().makeOrthoMatrix(true,true);
		}
	}
	
	
	

	///////////////////////////////////
	// REGION INTERFACE
	
	
	public boolean isRegion(){
		return true;
	}
	
	
	
	public Coords[] getNormalProjection(Coords coords) {
		return coords.projectPlane(getCoordSys().getMatrixOrthonormal());
	}

	public Coords[] getProjection(Coords oldCoords, Coords willingCoords, Coords willingDirection) {
		return willingCoords.projectPlaneThruVIfPossible(getCoordSys().getMatrixOrthonormal(),oldCoords,willingDirection);
	}

	public boolean isInRegion(GeoPointND P) {
		Coords planeCoords = getNormalProjection(P.getInhomCoordsInD(3))[1];
		//Application.debug(P.getLabel()+":\n"+planeCoords);
		return Kernel.isEqual(planeCoords.get(3),0,Kernel.STANDARD_PRECISION);
	}
	
	public boolean isInRegion(double x0, double y0){
		return true;
	}

	public void pointChangedForRegion(GeoPointND P) {
		
		P.updateCoords2D();
		P.updateCoordsFrom2D(false,null);
		
		
	}

	public void regionChanged(GeoPointND P) {
		//pointChangedForRegion(P);
		RegionParameters rp = P.getRegionParameters();
		P.setCoords(getPoint(rp.getT1(), rp.getT2()),false);
		
	}
	
	public Coords getPoint(double x2d, double y2d){
		return getCoordSys().getPoint(x2d,y2d);
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
		
		
		//Application.printStacktrace(xmin+","+xmax+"\n"+ymin+","+ymax);
		

	
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
	
	
	
	
	
	
	
	
	
    ///////////////////////////////////
	// GEOELEMENT3D
	
	
	
	/**
	 * return the (v1, v2, o) parametric matrix of this plane,
	 * ie each point of the plane is (v1, v2, o)*(a,b,1) for some a, b value
	 * @return the (v1, v2, o) parametric matrix of this plane
	 */
	public CoordMatrix getParametricMatrix(){
		CoordMatrix4x4 m4 = getCoordSys().getMatrixOrthonormal();
		CoordMatrix ret = new CoordMatrix(4,3);
		ret.setVx(m4.getVx());
		ret.setVy(m4.getVy());
		ret.setOrigin(m4.getOrigin());
		return ret;
	}
	
	public Coords getMainDirection(){
		
		return getCoordSys().getNormal();
	}
	

	public Coords getLabelPosition(){
		return getCoordSys().getPoint(0.5, 0.5);
	}
	
	
	
	
	
	public String getClassName() {
		return "GeoPlane3D";
	}        
	
    protected String getTypeString() {
		return "Plane3D";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_PLANE3D; 
    }


    
    
    
    
	public GeoElement copy() {
		GeoPlane3D p = new GeoPlane3D(cons);
		
		//TODO move this elsewhere
		CoordSys cs = p.getCoordSys();
		cs.addPoint(getCoordSys().getOrigin());
		cs.addVector(getCoordSys().getVx());
	    cs.addVector(getCoordSys().getVy());
	    cs.makeOrthoMatrix(false,false);
	    cs.makeEquationVector();
	    
	    
		return p;
	}




	public boolean isEqual(GeoElement Geo) {
		// TODO Raccord de méthode auto-généré
		return false;
	}


	public void set(GeoElement geo) {
		
		if (geo instanceof GeoPlane3D){
			GeoPlane3D plane = (GeoPlane3D) geo;
			setEquation(plane.getCoordSys().getEquationVector().get(),true);
			
		}
		
	}
	
	
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);
		if (geo.isGeoPlane()) {
			setFading(((GeoPlaneND) geo).getFading());
		}	
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
		return buildValueString().toString();
	}
	

	final public String toString() {

    	StringBuilder sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(": ");  //TODO use kernel property
		sbToString.append(buildValueString());
		return sbToString.toString();   
	}
	
	
	private StringBuilder buildValueString() {	
		
		
		return kernel.buildImplicitEquation(
				getCoordSys().getEquationVector().get(), VAR_STRING, KEEP_LEADING_SIGN, false, '='
				);   
		
	}

	
	
	/** to be able to fill it with an alpha value */
	public boolean isFillable() {
		return true;
	}
	
	
	
	
	/////////////////////////////////////////////
	// 2 VAR FUNCTION INTERFACE
	////////////////////////////////////////////

	public Coords evaluateNormal(double u, double v) {
		
		return coordsys.getNormal();
	}

	public Coords evaluatePoint(double u, double v) {
		
		return coordsys.getPointForDrawing(u, v);
		//return coordsys.getPoint(u, v);

	}
	

	public double getMinParameter(int index) {

		return 0; //TODO



	}


	public double getMaxParameter(int index) {

		return 0; //TODO

	}

	
	
	
	public CoordSys getCoordSys() {
		return coordsys;
	}
	

	public boolean isDefined() {
		return coordsys.isDefined();
	}
	
	public boolean isMoveable(){
		return false;
	}
	
	
	
	
	public String getDefaultLabel() {	
		return getDefaultLabel(Labels, false);
	}
	
	
	
	protected void getXMLtags(StringBuilder sb) {
        super.getXMLtags(sb);
        
        Coords equation = getCoordSys().getEquationVector();
        
        //equation
        sb.append("\t<coords");
        sb.append(" x=\""); sb.append(equation.getX()); sb.append("\"");
        sb.append(" y=\""); sb.append(equation.getY()); sb.append("\"");
        sb.append(" z=\""); sb.append(equation.getZ()); sb.append("\"");        
        sb.append(" w=\""); sb.append(equation.getW()); sb.append("\"");        
        sb.append("/>\n");
        
        
		// fading
		sb.append("\t<fading val=\"");
		sb.append(getFading());
		sb.append("\"/>\n");

    }
	
	
	public boolean isGeoPlane() {
		return true;
	}

	//////////////////////////////////
	// FADING

	
	private float fading = 0.10f;
	
	public void setFading(float fading){
		this.fading = fading;
	}
	
	public float getFading(){
		return fading;
	}
	
	
	
	
	//////////////////////////////////
	// 2D VIEW
	
	private EuclidianViewForPlane euclidianViewForPlane;
	
	public void createView2D(){
		euclidianViewForPlane = (EuclidianViewForPlane) app.createEuclidianViewForPlane(this);
	
	}
	
	public void update(){
		super.update();
		if (euclidianViewForPlane!=null){
			euclidianViewForPlane.updateMatrix();
			updateViewForPlane();
		}
	}

	private void updateViewForPlane(){
		euclidianViewForPlane.updateAllDrawables(true);
	}
	

	public void updateViewForPlaneDirection(Coords directionView3D, CoordMatrix toScreenMatrix){
		
		if (euclidianViewForPlane!=null){
			euclidianViewForPlane.setTransform(directionView3D, toScreenMatrix);
			updateViewForPlane();
		}
			
	}


	public Coords getDirectionInD3(){
		return getCoordSys().getNormal();
	}
	
	
}
