package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;

public class GeoCoordSys2D extends GeoCoordSys {
	
	
	GgbVector Vn = new GgbVector(3); //orthogonal vector

	public GeoCoordSys2D(Construction c, GgbVector O, GgbVector V1, GgbVector V2){
		super(c);
		M=new GgbMatrix(4,3);
		setCoord(O,V1,V2);
	}
	
	
	/** set the matrix to [V1 V2 O] */
	public void setCoord(GgbVector O, GgbVector V1, GgbVector V2){
		M.set(new GgbVector[] {V1, V2,O});
		
		Vn = V1.crossProduct(V2);
		Vn.normalize();
		
	}
	
	public GgbMatrix getMatrixCompleted(){
		GgbMatrix m = new GgbMatrix(4,4);
		m.set(new GgbVector[] {M.getColumn(1),M.getColumn(2),Vn,M.getColumn(3)});
		return m;
	}
	
	
	
	/** returns the point at position l1, l2 on the coord sys */
	public GgbVector getPoint(double l1, double l2){
		GgbVector v=new GgbVector(new double[] {l1,l2,1});
		GgbVector r=M.mul(v);		
		return r;
	}	
	

	
}
