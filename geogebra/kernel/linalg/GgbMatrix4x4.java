package geogebra.kernel.linalg;

public class GgbMatrix4x4 {

	
	/** complete a 4 x n matrix to a 4 x 4 matrix, orthogonal method */
	static public GgbMatrix toMatrix4x4(GgbMatrix a_matrix){
				
		GgbMatrix l_return;
		GgbVector l_O;
		
		switch(a_matrix.getColumns()){
		case 4:
			return a_matrix;
		case 2:
			GgbVector V = a_matrix.getColumn(1);
			l_O = a_matrix.getColumn(2);
			
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
			
			l_return = new GgbMatrix(4,4);
			l_return.set(new GgbVector[] {V,Vn1,Vn2,l_O});
			return l_return;
			
		case 3:
			GgbVector V1 = a_matrix.getColumn(1);
			GgbVector V2 = a_matrix.getColumn(2);
			l_O = a_matrix.getColumn(3);
			
			GgbVector Vn = new GgbVector(4);			
			Vn = V1.crossProduct(V2);
			Vn.normalize();
			
			l_return = new GgbMatrix(4,4);
			l_return.set(new GgbVector[] {V1,V2,Vn,l_O});
			return l_return;			
			
		default:
			return null;
		}
		
		
	}
	
	
	
	/** return a matrix that describe a segment along x-axis from x=x1 to x=x2 */
	static public GgbMatrix subSegmentX(GgbMatrix a_matrix, double x1, double x2){
				
		GgbMatrix l_return = a_matrix.copy();
		GgbVector l_origin = l_return.getColumn(4);
		GgbVector l_vx = l_return.getColumn(1);
		
		l_return.set((GgbVector) l_origin.add(l_vx.mul(x1)), 4);
		l_return.set((GgbVector) l_vx.mul(x2-x1), 1);
		
		return l_return;			
			
		
		
	}

}
