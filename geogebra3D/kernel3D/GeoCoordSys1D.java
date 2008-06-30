package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Path;
import geogebra.kernel.PathMover;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;

public class GeoCoordSys1D extends GeoCoordSys implements Path3D {
	
	GgbVector Vn1 = new GgbVector(3);
	GgbVector Vn2 = new GgbVector(3); //orthogonal vectors

	public GeoCoordSys1D(Construction c, GgbVector O, GgbVector V){
		super(c);
		M=new GgbMatrix(4,2);
		setCoord(O,V);
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
	
	public GgbMatrix getMatrixCompleted(){
		GgbMatrix m = new GgbMatrix(4,4);
		//m.set(new GgbVector[] {M.getColumn(1).normalized(),Vn1,Vn2,M.getColumn(2)});
		m.set(new GgbVector[] {M.getColumn(1),Vn1,Vn2,M.getColumn(2)});
		return m;
	}
	
	/** returns matrix corresponding to segment joining l1 to l2 */
	public GgbMatrix getSegmentMatrix(double l1, double l2){
		GgbMatrix m = new GgbMatrix(4,4);
		GgbVector p1 = getPoint(l1);
		GgbVector p2 = getPoint(l2);
		m.set(new GgbVector[] {p2.sub(p1),Vn1,Vn2,p1});
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




	
	
	
	
	
	
	
	
	
	
	
	///////////////////////////
	// Path 3D
	public void pointChanged(GeoPoint3D P) {
		//project P on line
		GgbVector P1 = P.getInhomCoords().projectLine(M.getColumn(2), M.getColumn(1));
		
	}
	
}
