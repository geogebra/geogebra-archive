package geogebra3D.kernel3D;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.Kernel;

/**
 * @author ggb3D
 *
 * 2D coordinate system.
 *
 */
public class GeoCoordSys2D extends GeoCoordSysAbstract implements Region3D {
	
	
	
	//grid
	double x0, y0; //origin of the grid in plane coordinates
	GgbVector gridOrigin = null;
	GgbVector gridOriginProjected = new GgbVector(4);

	
	public GeoCoordSys2D(Construction c){
		super(c,2);	
	}
	
	public GeoCoordSys2D(Construction c, GgbVector O, GgbVector V1, GgbVector V2){
		this(c);
		setCoord(O,V1,V2);		
	}

	public GeoCoordSys2D(Construction c, GeoPoint3D O, GeoPoint3D I, GeoPoint3D J){
		this(c);
		setCoord(O,I,J);		
	}
	
	
	/** set the matrix to [V1 V2 O] */
	public void setCoord(GgbVector a_O, GgbVector a_V1, GgbVector a_V2){
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
		GgbVector vO = O.getCoords();
		GgbVector vI = I.getCoords();
		GgbVector vJ = J.getCoords();
		setCoord(vO,vI.sub(vO),vJ.sub(vO));
		
	}
	
	
	
	
	
	
	
	

	
	
	
	/** returns the point at position l1, l2 on the coord sys */
	public GgbVector getPoint(double l1, double l2){
		GgbVector v=new GgbVector(new double[] {l1,l2,1});
		//Application.debug("v ="+v.toString());
		GgbVector r=getMatrix().mul(v);	
		//Application.debug("getMatrix() ="+getMatrix().toString());
		//Application.debug("r ="+ r.toString());
		return r;
	}	
	
	
	/** returns area of the 1x1 parallélogramme */
	public double getUnitArea(){
		return (getMatrix().getColumn(1).crossProduct(getMatrix().getColumn(2))).norm();
	}
	
	
	////////////////////////////////////
	// grid
	public void setGridOrigin(GgbVector v){
		
		gridOrigin = v.copyVector();
		updateGridOriginProjected();
		
	}
	
	public void updateGridOriginProjected(){
		GgbVector c = gridOrigin.projectPlane(getMatrix4x4())[1];
		//c.SystemPrint();
		x0 = c.get(1); y0 = c.get(2);
		gridOriginProjected.set(getPoint(x0,y0));
		//Application.debug("c = "+c.get(1)+","+c.get(2)+" -- gridOriginProjected ="); gridOriginProjected.SystemPrint();
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


	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return false;
	}

	public String toValueString() {
		return "todo-GeoCoordSys2D.toValueString()";
	}
	

	protected String getClassName() {
		return "GeoCoordSys2D";
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}

	
	
	
	/////////////////////////////////////
	//
	// REGION3D INTERFACE
	//
	/////////////////////////////////////
	
	
	public GgbVector[] getNormalProjection(GgbVector coords) {
		return coords.projectPlane(this.getMatrix4x4());
	}

	public GgbVector[] getProjection(GgbVector coords,
			GgbVector willingDirection) {
		return coords.projectPlaneThruV(this.getMatrix4x4(),willingDirection);
	}

	public boolean isInRegion(GeoPointInterface P) {
		GgbVector planeCoords = getNormalProjection(((GeoPoint3D) P).getCoords())[1];
		return Kernel.isEqual(planeCoords.get(3),0,Kernel.STANDARD_PRECISION);
	}

	public void pointChangedForRegion(GeoPointInterface P) {
		
		P.updateCoords2D();
		P.updateCoordsFrom2D(false);
		
		
	}

	public void regionChanged(GeoPointInterface P) {
		pointChangedForRegion(P);
		
	}
	
	
	
	///////////////////////////////////
	// GEOELEMENT3DINTERFACE INTERFACE	
	public GgbVector getNormal(){ 
		return getMatrix4x4().getVz();
	};


	
}
