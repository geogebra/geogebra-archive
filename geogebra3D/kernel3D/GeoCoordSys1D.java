package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.PathParameter;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra.main.Application;

public abstract class GeoCoordSys1D extends GeoCoordSys implements Path1D {
	
	GgbVector Vn1 = new GgbVector(4);
	GgbVector Vn2 = new GgbVector(4); //orthogonal vectors

	public GeoCoordSys1D(Construction c, GgbVector O, GgbVector V){
		super(c);
		M=new GgbMatrix(4,2);
		setCoord(O,V);
	}
	
	
	public GeoCoordSys1D(Construction c, GeoPoint3D O, GeoPoint3D I){
		super(c);
		M=new GgbMatrix(4,2);
		setCoord(O,I);
	}	
	
	
	/** set the matrix to [V O] */
	public void setCoord(GgbVector O, GgbVector V){
		M.set(new GgbVector[] {V,O});
		
		if (V.get(1)!=0){
			Vn1.set(1,-V.get(2));
			Vn1.set(2,V.get(1));
			Vn1.normalize();
		}else{
			Vn1.set(1, 1.0);
		}
		
		Vn2 = V.crossProduct(Vn1);
		Vn2.normalize();
		
	}
	
	
	/** set coords to origin O and vector (I-O) */
	public void setCoord(GeoPoint3D O, GeoPoint3D I){
		
		GgbVector vO = O.getCoords();
		GgbVector vI = I.getCoords();
		setCoord(vO,vI.sub(vO));
		
	}
	
	
	
	
	/** returns completed matrix for drawing */
	public GgbMatrix getMatrixCompleted(){
		GgbMatrix m = new GgbMatrix(4,4);		
		m.set(new GgbVector[] {M.getColumn(1),Vn1,Vn2,M.getColumn(2)});
		return m;
	}
	
	/** returns matrix corresponding to segment joining l1 to l2, using getLineThickness() */
	public GgbMatrix getSegmentMatrix(double l1, double l2){
		GgbMatrix m = new GgbMatrix(4,4);
		GgbVector p1 = getPoint(l1);
		GgbVector p2 = getPoint(l2);
		m.set(new GgbVector[] {p2.sub(p1),Vn1.mul(getLineThickness()).v(),Vn2.mul(getLineThickness()).v(),p1});
		return m;
	}	
	
	
	
	//public GeoPoint3D getPoint(double lambda){
	/** returns the point at position lambda on the coord sys */
	public GgbVector getPoint(double lambda){
		GgbVector v=new GgbVector(new double[] {lambda,1});
		GgbVector r=M.mul(v);		
		//r.SystemPrint();
		return r;
		//return new GeoPoint3D(getConstruction(), "M", r);
	}


	/** returns cs unit */
	public double getUnit(){
		return M.getColumn(1).norm();
	}


	
	
	
	// Path1D interface
	public void pointChanged(GeoPoint3D P){
		//project P on line
		GgbVector v = P.getInhomCoords();
		GgbVector p = new GgbVector(4);
		GgbVector[] project = v.projectLine(M.getColumn(2).subVector(1, 3), M.getColumn(1).subVector(1, 3));
		p.set(project[0]);
		p.set(4, 1);
		P.setCoords(p,false); //avoid new pointChanged computation
		
		// set path parameter
		//Application.debug("parameter = "+project[1].get(1));
		PathParameter1D pp = P.getPathParameter1D();
		pp.setT(project[1].get(1));
		
	}
	
	public void pathChanged(GeoPoint3D P){
		PathParameter1D pp = P.getPathParameter1D();
		P.setCoords(getPoint(pp.getT()),false);
	}
	
	public boolean isOnPath(GeoPoint3D P, double eps){
		return false;
	}

	
	
	public double getMinParameter(){
		return Double.NEGATIVE_INFINITY;
	}
	public double getMaxParameter(){
		return Double.POSITIVE_INFINITY;
	}
	public boolean isClosedPath(){
		return false;
	}
	
	
	
	public GgbMatrix getMovingMatrix(GgbMatrix toScreenMatrix){
		
		GgbMatrix ret = toScreenMatrix.mul(getMatrixCompleted());
		
		GgbVector V = ret.getColumn(1); //gets direction vector of the path
		GgbVector Vn1 = new GgbVector(4); 
		GgbVector Vn2 = new GgbVector(4);
		if (V.get(1)!=0){
			Vn1.set(1,-V.get(2));
			Vn1.set(2,V.get(1));
			Vn1.normalize();
		}else{
			Vn1.set(1, 1.0);
		}
		Vn2 = V.crossProduct(Vn1);
		Vn2.normalize();	
		
		ret.set(Vn1, 2);ret.set(Vn2, 3);
		
		return ret;
	}
	
	
	

	
}
