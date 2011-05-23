package geogebra.Matrix;


/**
 * 4x4 matrix for 3D transformations, planes descriptions, lines, etc.
 * 
 * @author ggb3D
 *
 */
public class CoordMatrix4x4 extends CoordMatrix {

	
	
	final static public int VX = 0;
	final static public int VY = 1;
	final static public int VZ = 2;
	
	
	
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
			
			Coords[] n = CoordMatrix4x4.getOrthoVectors(V);
			
			set(new Coords[] {V,n[0],n[1],l_O});
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
	
	
	/** complete a given origin and direction to a 4 x 4 matrix, orthogonal method 
	 * @param origin 
	 * @param direction 
	 * @param type says which place takes the direction given (VX, VY or VZ)
	 */
	public CoordMatrix4x4(Coords origin, Coords direction, int type){
		
		this();		
		Coords[] n = CoordMatrix4x4.getOrthoVectors(direction);
		
		switch(type){
		case VX:
			set(new Coords[] {direction,n[0],n[1],origin});	
			break;
		case VY:
			set(new Coords[] {n[1],direction,n[0],origin});	
			break;
		case VZ:
			set(new Coords[] {n[0],n[1],direction,origin});	
			break;
		}
		
		
	}
	
	
	private static final Coords[] getOrthoVectors(Coords V){
		Coords[] ret = new Coords[2];
		
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
		
		ret[0]=Vn1;
		ret[1]=Vn2;
		
		return ret;
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
