package geogebra.Matrix;

/**
 * 4x4 matrix for 3D transformations, planes descriptions, lines, etc.
 * 
 * @author ggb3D
 *
 */
public class CoordMatrix4x4 extends CoordMatrix {

	
	
	
	
	
	
	///////////////////////////////////////////////////
	// CONSTRUCTORS
	
	/**
	 * basic constructor.
	 */
	public CoordMatrix4x4(){
		super(4,4);
	}
	
	
	/**
	 * create a 4x4 identity matrix.
	 * @return 4x4 identity matrix
	 */
	static final public CoordMatrix4x4 Identity(){
		CoordMatrix4x4 ret = new CoordMatrix4x4();
		ret.set(Identity(4));
		return ret;
	}
	
	/** complete a 4 x n matrix to a 4 x 4 matrix, orthogonal method 
	 * @param a_matrix source 4 x n matrix
	 */
	public CoordMatrix4x4(CoordMatrix a_matrix){
				
		this();
		Coords l_O;
		
		switch(a_matrix.getColumns()){
		case 4:
			set(a_matrix);
			break;
		case 2:
			Coords V = a_matrix.getColumn(1);
			l_O = a_matrix.getColumn(2);
			
			Coords Vn1 = new Coords(4);
			Coords Vn2 = new Coords(4);
			
			if (V.get(1)!=0){
				Vn1.set(1,-V.get(2));
				Vn1.set(2,V.get(1));
				Vn1.normalize();
			}else{
				Vn1.set(1, 1.0);
			}
			
			Vn2 = V.crossProduct(Vn1);
			Vn2.normalize();
			
			set(new Coords[] {V,Vn1,Vn2,l_O});
			break;
		case 3:
			Coords V1 = a_matrix.getColumn(1);
			Coords V2 = a_matrix.getColumn(2);
			l_O = a_matrix.getColumn(3);
			
			Coords Vn = new Coords(4);			
			Vn = V1.crossProduct(V2);
			Vn.normalize();
			
			set(new Coords[] {V1,V2,Vn,l_O});		
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
	public CoordMatrix4x4 mul(CoordMatrix4x4 m){
		
		CoordMatrix4x4 result = new CoordMatrix4x4(); 
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
	public Coords getOrigin(){ return getColumn(4); }	
	
	/** return "x-axis" vector
	 * @return "x-axis" vector
	 */
	public Coords getVx(){ return getColumn(1); }	
	
	/** return "y-axis" vector
	 * @return "y-axis" vector
	 */
	public Coords getVy(){ return getColumn(2); }	
	
	/** return "z-axis" vector
	 * @return "z-axis" vector
	 */
	public Coords getVz(){ return getColumn(3); }	
	
	/** set origin of the matrix
	 * @param v origin
	 */	
	public void setOrigin(Coords v){ set(v,4); }	
	
	/** return "x-axis" vector
	 * @param v "x-axis" vector
	 */
	public void setVx(Coords v){ set(v,1); }	
	
	/** return "y-axis" vector
	 * @param v "y-axis" vector
	 */
	public void setVy(Coords v){ set(v,2); }	
	
	/** return "z-axis" vector
	 * @param v "z-axis" vector
	 */
	public void setVz(Coords v){ set(v,3); }	
	
	
	
	
	
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
	public Coords getPoint(double a_x, double a_y, double a_z){
		Coords v=new Coords(new double[] {a_x,a_y,a_z,1});	
		return this.mul(v);
	}
	
	/** return a matrix that describe a quad with corners (a_x1,a_y1) and  (a_x2,a_y2) 
	 * @param a_x1 x coord of the first corner
	 * @param a_y1 y coord of the first corner
	 * @param a_x2 x coord of the third corner
	 * @param a_y2 y coord of the third corner
	 * @return matrix describing the quad */
	public CoordMatrix4x4 quad(double a_x1, double a_y1, double a_x2, double a_y2){
				
		CoordMatrix4x4 ret = new CoordMatrix4x4();
		
		Coords o = getPoint(a_x1,a_y1,0);
		Coords px = getPoint(a_x2,a_y1,0);
		Coords py = getPoint(a_x1,a_y2,0);

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
	public CoordMatrix segmentX(double a_x1, double a_x2){
				
		CoordMatrix4x4 ret = new CoordMatrix4x4();
		
		ret.setOrigin((Coords) getOrigin().add(getVx().mul(a_x1)));
		ret.setVx((Coords) getVx().mul(a_x2-a_x1));		
		ret.setVy(getVy());
		ret.setVz(getVz());	
		
		return ret;			
			
		
		
	}
	
	
	/** return this translated along y axis
	 * @param a_y value of the y-translation
	 * @return matrix translated */
    public CoordMatrix4x4 translateY(double a_y){
				
		CoordMatrix4x4 ret = new CoordMatrix4x4();
		
		ret.setOrigin((Coords) getOrigin().add(getVy().mul(a_y)));
		ret.setVx(getVx());		
		ret.setVy(getVy());
		ret.setVz(getVz());		
		
		return ret;			
		
	}
    
    

	
    
    
	/** return this mirrored by x=y plane
	 * @return mirrored matrix*/
    public CoordMatrix4x4 mirrorXY(){
				
		CoordMatrix4x4 ret = new CoordMatrix4x4();
						
		ret.setOrigin(getOrigin());
		ret.setVx(getVy());		
		ret.setVy(getVx());
		ret.setVz((Coords) getVz().mul(-1));		
		
		return ret;			
			
		
	}
	
	
	

}
