package geogebra.Matrix;

/**
 * 4x4 matrix for 3D transformations, planes descriptions, lines, etc.
 * 
 * @author ggb3D
 *
 */
public class GgbMatrix4x4 extends GgbMatrix {

	
	
	
	
	
	
	///////////////////////////////////////////////////
	// CONSTRUCTORS
	
	/**
	 * basic constructor.
	 */
	public GgbMatrix4x4(){
		super(4,4);
	}
	
	
	/**
	 * create a 4x4 identity matrix.
	 * @return 4x4 identity matrix
	 */
	static final public GgbMatrix4x4 Identity(){
		GgbMatrix4x4 ret = new GgbMatrix4x4();
		ret.set(Identity(4));
		return ret;
	}
	
	/** complete a 4 x n matrix to a 4 x 4 matrix, orthogonal method 
	 * @param a_matrix source 4 x n matrix
	 */
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
	// OVERWRITE Ggb3DMatrix
	
	//matrix multiplication
	/** returns this * m 
	 * @param m matrix
	 * @return resulting matrix*/
	public GgbMatrix4x4 mul(GgbMatrix4x4 m){
		
		GgbMatrix4x4 result = new GgbMatrix4x4(); 
		this.mul(m,result);
		
		return result;
		
	}
	
	///////////////////////////////////////////////////
	// OPERATIONS
	
	public void mulAllButOrigin(double v){
		for (int i=0; i<12; i++)
			val[i]*=v;

			
	}
	
	
	///////////////////////////////////////////////////
	// SETTERS AND GETTERS
	
	/** return origin of the matrix
	 * @return origin
	 */	
	public GgbVector getOrigin(){ return getColumn(4); }	
	
	/** return "x-axis" vector
	 * @return "x-axis" vector
	 */
	public GgbVector getVx(){ return getColumn(1); }	
	
	/** return "y-axis" vector
	 * @return "y-axis" vector
	 */
	public GgbVector getVy(){ return getColumn(2); }	
	
	/** return "z-axis" vector
	 * @return "z-axis" vector
	 */
	public GgbVector getVz(){ return getColumn(3); }	
	
	/** set origin of the matrix
	 * @param v origin
	 */	
	public void setOrigin(GgbVector v){ set(v,4); }	
	
	/** return "x-axis" vector
	 * @param v "x-axis" vector
	 */
	public void setVx(GgbVector v){ set(v,1); }	
	
	/** return "y-axis" vector
	 * @param v "y-axis" vector
	 */
	public void setVy(GgbVector v){ set(v,2); }	
	
	/** return "z-axis" vector
	 * @param v "z-axis" vector
	 */
	public void setVz(GgbVector v){ set(v,3); }	
	
	
	
	
	
	///////////////////////////////////////////////////
	// LENGTHS

	/** return length of unit for each axis
	 * @param a_axis number of the axis
	 * @return length of unit */
	public double getUnit(int a_axis){
		return getColumn(a_axis).norm();
	}
	
	
	///////////////////////////////////////////////////
	// GEOMETRIES
	
	/** returns the point at position a_x, a_y, a_z 
	 * @param a_x x coord
	 * @param a_y y coord
	 * @param a_z z coord
	 * @return the point */
	public GgbVector getPoint(double a_x, double a_y, double a_z){
		GgbVector v=new GgbVector(new double[] {a_x,a_y,a_z,1});	
		return this.mul(v);
	}
	
	/** return a matrix that describe a quad with corners (a_x1,a_y1) and  (a_x2,a_y2) 
	 * @param a_x1 x coord of the first corner
	 * @param a_y1 y coord of the first corner
	 * @param a_x2 x coord of the third corner
	 * @param a_y2 y coord of the third corner
	 * @return matrix describing the quad */
	public GgbMatrix4x4 quad(double a_x1, double a_y1, double a_x2, double a_y2){
				
		GgbMatrix4x4 ret = new GgbMatrix4x4();
		
		GgbVector o = getPoint(a_x1,a_y1,0);
		GgbVector px = getPoint(a_x2,a_y1,0);
		GgbVector py = getPoint(a_x1,a_y2,0);

		ret.setOrigin(o);
		ret.setVx(px.sub(o));		
		ret.setVy(py.sub(o));
		ret.setVz(getVz());	
		
		return ret;			
			
		
		
	}
	
	
	
	/** return a matrix that describe a segment along x-axis from x=x1 to x=x2 
	 * @param a_x1 x-start of the segment
	 * @param a_x2 x-end of the segment
	 * @return matrix describing the segment*/
	public GgbMatrix segmentX(double a_x1, double a_x2){
				
		GgbMatrix4x4 ret = new GgbMatrix4x4();
		
		ret.setOrigin((GgbVector) getOrigin().add(getVx().mul(a_x1)));
		ret.setVx((GgbVector) getVx().mul(a_x2-a_x1));		
		ret.setVy(getVy());
		ret.setVz(getVz());	
		
		return ret;			
			
		
		
	}
	
	
	/** return this translated along y axis
	 * @param a_y value of the y-translation
	 * @return matrix translated */
    public GgbMatrix4x4 translateY(double a_y){
				
		GgbMatrix4x4 ret = new GgbMatrix4x4();
		
		ret.setOrigin((GgbVector) getOrigin().add(getVy().mul(a_y)));
		ret.setVx(getVx());		
		ret.setVy(getVy());
		ret.setVz(getVz());		
		
		return ret;			
		
	}
    
    

	
    
    
	/** return this mirrored by x=y plane
	 * @return mirrored matrix*/
    public GgbMatrix4x4 mirrorXY(){
				
		GgbMatrix4x4 ret = new GgbMatrix4x4();
						
		ret.setOrigin(getOrigin());
		ret.setVx(getVy());		
		ret.setVy(getVx());
		ret.setVz((GgbVector) getVz().mul(-1));		
		
		return ret;			
			
		
	}
	
	
	

}
