package geogebra.kernel.linalg;

public class GgbMatrix4x4 extends GgbMatrix {

	
	
	
	
	
	
	///////////////////////////////////////////////////
	// CONSTRUCTORS
	
	public GgbMatrix4x4(){
		super(4,4);
	}
	
	
	static final public GgbMatrix4x4 Identity(){
		GgbMatrix4x4 ret = new GgbMatrix4x4();
		ret.set(Identity(4));
		return ret;
	}
	
	/** complete a 4 x n matrix to a 4 x 4 matrix, orthogonal method */
	public GgbMatrix4x4(GgbMatrix a_matrix){
				
		this();
		GgbVector l_O;
		
		switch(a_matrix.getColumns()){
		case 4:
			set(a_matrix);
			break;
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
			
			set(new GgbVector[] {V,Vn1,Vn2,l_O});
			break;
		case 3:
			GgbVector V1 = a_matrix.getColumn(1);
			GgbVector V2 = a_matrix.getColumn(2);
			l_O = a_matrix.getColumn(3);
			
			GgbVector Vn = new GgbVector(4);			
			Vn = V1.crossProduct(V2);
			Vn.normalize();
			
			set(new GgbVector[] {V1,V2,Vn,l_O});		
			break;
		default:
			break;
		}
		
		
	}
	
	
	
	
	///////////////////////////////////////////////////
	// LENGTHS

	/** return length of unit for each axis*/
	public double getUnit(int a_axis){
		return getColumn(a_axis).norm();
	}
	
	
	///////////////////////////////////////////////////
	// GEOMETRIES
	
	/** returns the point at position a_x, a_y, a_z */
	public GgbVector getPoint(double a_x, double a_y, double a_z){
		GgbVector v=new GgbVector(new double[] {a_x,a_y,a_z,1});	
		return this.mul(v);
	}
	
	/** return a matrix that describe a quad with corners (a_x1,a_y1) and  (a_x2,a_y2) */
	public GgbMatrix4x4 quad(double a_x1, double a_y1, double a_x2, double a_y2){
				
		GgbMatrix4x4 l_return = new GgbMatrix4x4();
		
		GgbVector o = getPoint(a_x1,a_y1,0);
		GgbVector px = getPoint(a_x2,a_y1,0);
		GgbVector py = getPoint(a_x1,a_y2,0);
		l_return.set(px.sub(o), 1);
		l_return.set(py.sub(o), 2);
		l_return.set(getColumn(3), 3);
		l_return.set(o, 4);
		
		return l_return;			
			
		
		
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
