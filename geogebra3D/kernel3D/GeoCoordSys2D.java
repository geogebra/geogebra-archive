package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DVector;

public class GeoCoordSys2D extends GeoCoordSys implements PathIn {
	
	
	
	//grid
	double x0, y0; //origin of the grid in plane coordinates
	Ggb3DVector gridOrigin = null;
	Ggb3DVector gridOriginProjected = new Ggb3DVector(4);

	
	public GeoCoordSys2D(Construction c){
		super(c,2);	
	}
	
	public GeoCoordSys2D(Construction c, Ggb3DVector O, Ggb3DVector V1, Ggb3DVector V2){
		this(c);
		setCoord(O,V1,V2);		
	}

	public GeoCoordSys2D(Construction c, GeoPoint3D O, GeoPoint3D I, GeoPoint3D J){
		this(c);
		setCoord(O,I,J);		
	}
	
	
	/** set the matrix to [V1 V2 O] */
	public void setCoord(Ggb3DVector a_O, Ggb3DVector a_V1, Ggb3DVector a_V2){
		setOrigin(a_O);
		setVx(a_V1);
		setVy(a_V2);
		setMadeCoordSys();
		
		
		updateDrawingMatrix();
		
		if (gridOrigin!=null)
			updateGridOriginProjected();
		
	}
	
	/** set coords to origin O and vectors (I-O) and (J-O) */
	public void setCoord(GeoPoint3D O, GeoPoint3D I, GeoPoint3D J){
		//Application.debug("setCoord -- Points");
		Ggb3DVector vO = O.getCoords();
		Ggb3DVector vI = I.getCoords();
		Ggb3DVector vJ = J.getCoords();
		setCoord(vO,vI.sub(vO),vJ.sub(vO));
		
	}
	
	
	
	
	
	
	
	

	
	
	
	/** returns the point at position l1, l2 on the coord sys */
	public Ggb3DVector getPoint(double l1, double l2){
		Ggb3DVector v=new Ggb3DVector(new double[] {l1,l2,1});
		//Application.debug("v ="); v.SystemPrint();
		Ggb3DVector r=getMatrix().mul(v);	
		//Application.debug("M ="); M.SystemPrint();
		//Application.debug("r ="); r.SystemPrint();
		return r;
	}	
	
	
	/** returns area of the 1x1 parallélogramme */
	public double getUnitArea(){
		return (getMatrix().getColumn(1).crossProduct(getMatrix().getColumn(2))).norm();
	}
	
	
	////////////////////////////////////
	// grid
	public void setGridOrigin(Ggb3DVector v){
		
		gridOrigin = v.copyVector();
		updateGridOriginProjected();
		
	}
	
	public void updateGridOriginProjected(){
		Ggb3DVector c = gridOrigin.projectPlane(getMatrix4x4())[1];
		//c.SystemPrint();
		x0 = c.get(1); y0 = c.get(2);
		gridOriginProjected.set(getPoint(x0,y0));
		//Application.debug("c = "+c.get(1)+","+c.get(2)+" -- gridOriginProjected ="); gridOriginProjected.SystemPrint();
	}
	
	
	
	
	/////////////////////////////////////////
	// Path2D interface
	
	public boolean isPath2D(){
		return true;
	}
	
	public void pointChanged(GeoPoint3D P){
		
		//project P on plane
		Ggb3DVector v = P.getCoords();
		Ggb3DVector[] project = v.projectPlane(getMatrix4x4());
		
		if (!isLimitedPath()){
			P.setCoords(project[0],false);

			// set path parameter		
			PathParameters pps = P.getPathParameters(2);
			pps.setTs(new double[] {project[1].get(1),project[1].get(2)});
		}else{
			// set path parameter		
			PathParameters pps = P.getPathParameters(2);
			pps.setTs(new double[] {project[1].get(1),project[1].get(2)});		
			limitPathParameters(pps);
			
			P.setCoords(getPoint(pps.getT(0),pps.getT(1)),false);
		}
	}
	
	
	
	public void pathChanged(GeoPoint3D P){
		PathParameters pps = P.getPathParameters(2);
		P.setCoords(getPoint(pps.getT(0),pps.getT(1)),false);
	}
	
	
	
	public boolean isLimitedPath(){
		return false;
	}

	public void limitPathParameters(PathParameters pps){}	
	

	public boolean isOnPath(GeoPoint3D P, double eps){
		return false; //TODO
	}



	

	public boolean isClosedPath(){
		return false;
	}
	

	public Ggb3DMatrix getMovingMatrix(Ggb3DMatrix toScreenMatrix){
		return getMatrix4x4();
	}

	@Override
	public GeoElement copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGeoClassType() {
		// TODO Auto-generated method stub
		return 0;
	}

	protected String getTypeString() {
		return "CoordSys2D";
	}

	
	



	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void set(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}


	protected boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return false;
	}

	public String toValueString() {
		return null;
	}


	protected String getClassName() {
		return "GeoCoordSys2D";
	}
	

	
}
