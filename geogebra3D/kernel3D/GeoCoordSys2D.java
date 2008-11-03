package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;

public abstract class GeoCoordSys2D extends GeoCoordSys implements Path2D {
	
	
	GgbVector Vn = new GgbVector(4); //orthogonal vector
	
	//grid
	double x0, y0; //origin of the grid in plane coordinates
	GgbVector gridOrigin = null;
	GgbVector gridOriginProjected = new GgbVector(4);

	
	public GeoCoordSys2D(Construction c){
		super(c);	
	}
	
	public GeoCoordSys2D(Construction c, GgbVector O, GgbVector V1, GgbVector V2){
		super(c);
		M=new GgbMatrix(4,3);
		setCoord(O,V1,V2);		
	}

	public GeoCoordSys2D(Construction c, GeoPoint3D O, GeoPoint3D I, GeoPoint3D J){
		super(c);
		M=new GgbMatrix(4,3);
		setCoord(O,I,J);		
	}
	
	
	/** set the matrix to [V1 V2 O] */
	public void setCoord(GgbVector O, GgbVector V1, GgbVector V2){
		M.set(new GgbVector[] {V1, V2,O});
		
		Vn = V1.crossProduct(V2);
		Vn.normalize();
		
		matrixCompleted.set(new GgbVector[] {M.getColumn(1),M.getColumn(2),Vn,M.getColumn(3)});
		//matrixCompleted.SystemPrint();
		//Application.debug("matrixCompleted");
		
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
	
	
	
	
	
	
	
	
	/** returns completed matrix for drawing : (V1 V2 Vn O) with Vn normed orthogonal vector to the plane */
	public GgbMatrix getMatrixCompleted(){
		return matrixCompleted.copy();
	}
	
	
	
	/** returns the point at position l1, l2 on the coord sys */
	public GgbVector getPoint(double l1, double l2){
		GgbVector v=new GgbVector(new double[] {l1,l2,1});
		//Application.debug("v ="); v.SystemPrint();
		GgbVector r=M.mul(v);	
		//Application.debug("M ="); M.SystemPrint();
		//Application.debug("r ="); r.SystemPrint();
		return r;
	}	
	
	
	/** returns area of the 1x1 parallélogramme */
	public double getUnitArea(){
		return (M.getColumn(1).crossProduct(M.getColumn(2))).norm();
	}
	
	
	////////////////////////////////////
	// grid
	public void setGridOrigin(GgbVector v){
		
		gridOrigin = v.copyVector();
		updateGridOriginProjected();
		
	}
	
	public void updateGridOriginProjected(){
		GgbVector c = gridOrigin.projectPlane(matrixCompleted)[1];
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
		GgbVector[] project = v.projectPlane(matrixCompleted);
		P.setCoords(project[0],false);
		
		// set path parameter		
		Path2DParameters pp = P.getPath2DParameters();
		pp.setT(project[1].get(1),project[1].get(2));
	}
	
	
	
	public void pathChanged(GeoPoint3D P){
		Path2DParameters pp = P.getPath2DParameters();
		P.setCoords(getPoint(pp.getT1(),pp.getT2()),false);
	}
	

	public boolean isOnPath(GeoPoint3D P, double eps){
		return false; //TODO
	}



	

	public boolean isClosedPath(){
		return false;
	}
	

	public GgbMatrix getMovingMatrix(){
		return matrixCompleted;
	}
	

	
}
