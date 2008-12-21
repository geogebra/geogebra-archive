package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;

public abstract class GeoCoordSys2D extends GeoCoordSys implements PathIn {
	
	
	
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
		//Application.debug("v ="); v.SystemPrint();
		GgbVector r=getMatrix().mul(v);	
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
	
	
	
	
	/////////////////////////////////////////
	// Path2D interface
	
	public boolean isPath2D(){
		return true;
	}
	
	public void pointChanged(GeoPoint3D P){
		
		//project P on plane
		GgbVector v = P.getCoords();
		GgbVector[] project = v.projectPlane(getMatrix4x4());
		
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
	

	public GgbMatrix getMovingMatrix(GgbMatrix toScreenMatrix){
		return getMatrix4x4();
	}
	

	
}
