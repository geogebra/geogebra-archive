package geogebra3D.Matrix;

public class Ggb3DMatrix4x4 extends Ggb3DMatrix {

	
	
	
	
	
	
	///////////////////////////////////////////////////
	// CONSTRUCTORS
	
	public Ggb3DMatrix4x4(){
		super(4,4);
	}
	
	
	static final public Ggb3DMatrix4x4 Identity(){
		Ggb3DMatrix4x4 ret = new Ggb3DMatrix4x4();
		ret.set(Identity(4));
		return ret;
	}
	
	/** complete a 4 x n matrix to a 4 x 4 matrix, orthogonal method */
	public Ggb3DMatrix4x4(Ggb3DMatrix a_matrix){
				
		this();
		Ggb3DVector l_O;
		
		switch(a_matrix.getColumns()){
		case 4:
			set(a_matrix);
			break;
		case 2:
			Ggb3DVector V = a_matrix.getColumn(1);
			l_O = a_matrix.getColumn(2);
			
			Ggb3DVector Vn1 = new Ggb3DVector(4);
			Ggb3DVector Vn2 = new Ggb3DVector(4);
			
			if (V.get(1)!=0){
				Vn1.set(1,-V.get(2));
				Vn1.set(2,V.get(1));
				Vn1.normalize();
			}else{
				Vn1.set(1, 1.0);
			}
			
			Vn2 = V.crossProduct(Vn1);
			Vn2.normalize();
			
			set(new Ggb3DVector[] {V,Vn1,Vn2,l_O});
			break;
		case 3:
			Ggb3DVector V1 = a_matrix.getColumn(1);
			Ggb3DVector V2 = a_matrix.getColumn(2);
			l_O = a_matrix.getColumn(3);
			
			Ggb3DVector Vn = new Ggb3DVector(4);			
			Vn = V1.crossProduct(V2);
			Vn.normalize();
			
			set(new Ggb3DVector[] {V1,V2,Vn,l_O});		
			break;
		default:
			break;
		}
		
		
	}
	
	
	
	
	///////////////////////////////////////////////////
	// SETTERS AND GETTERS
	
	public Ggb3DVector getOrigin(){ return getColumn(4); }	
	public Ggb3DVector getVx(){ return getColumn(1); }	
	public Ggb3DVector getVy(){ return getColumn(2); }	
	public Ggb3DVector getVz(){ return getColumn(3); }	
	
	public void setOrigin(Ggb3DVector v){ set(v,4); }	
	public void setVx(Ggb3DVector v){ set(v,1); }	
	public void setVy(Ggb3DVector v){ set(v,2); }	
	public void setVz(Ggb3DVector v){ set(v,3); }	
	
	
	
	
	
	///////////////////////////////////////////////////
	// LENGTHS

	/** return length of unit for each axis*/
	public double getUnit(int a_axis){
		return getColumn(a_axis).norm();
	}
	
	
	///////////////////////////////////////////////////
	// GEOMETRIES
	
	/** returns the point at position a_x, a_y, a_z */
	public Ggb3DVector getPoint(double a_x, double a_y, double a_z){
		Ggb3DVector v=new Ggb3DVector(new double[] {a_x,a_y,a_z,1});	
		return this.mul(v);
	}
	
	/** return a matrix that describe a quad with corners (a_x1,a_y1) and  (a_x2,a_y2) */
	public Ggb3DMatrix4x4 quad(double a_x1, double a_y1, double a_x2, double a_y2){
				
		Ggb3DMatrix4x4 ret = new Ggb3DMatrix4x4();
		
		Ggb3DVector o = getPoint(a_x1,a_y1,0);
		Ggb3DVector px = getPoint(a_x2,a_y1,0);
		Ggb3DVector py = getPoint(a_x1,a_y2,0);

		ret.setOrigin(o);
		ret.setVx(px.sub(o));		
		ret.setVy(py.sub(o));
		ret.setVz(getVz());	
		
		return ret;			
			
		
		
	}
	
	
	
	/** return a matrix that describe a segment along x-axis from x=x1 to x=x2 */
	public Ggb3DMatrix segmentX(double a_x1, double a_x2){
				
		Ggb3DMatrix4x4 ret = new Ggb3DMatrix4x4();
		
		ret.setOrigin((Ggb3DVector) getOrigin().add(getVx().mul(a_x1)));
		ret.setVx((Ggb3DVector) getVx().mul(a_x2-a_x1));		
		ret.setVy(getVy());
		ret.setVz(getVz());	
		
		return ret;			
			
		
		
	}
	
	
	/** return this translated along y axis*/
    public Ggb3DMatrix4x4 translateY(double a_y){
				
		Ggb3DMatrix4x4 ret = new Ggb3DMatrix4x4();
		
		ret.setOrigin((Ggb3DVector) getOrigin().add(getVy().mul(a_y)));
		ret.setVx(getVx());		
		ret.setVy(getVy());
		ret.setVz(getVz());		
		
		return ret;			
		
	}
    
    

	
    
    
	/** return this mirrored by x=y plane*/
    public Ggb3DMatrix4x4 mirrorXY(){
				
		Ggb3DMatrix4x4 ret = new Ggb3DMatrix4x4();
						
		ret.setOrigin(getOrigin());
		ret.setVx(getVy());		
		ret.setVy(getVx());
		ret.setVz((Ggb3DVector) getVz().mul(-1));		
		
		return ret;			
			
		
	}
	
	
	

}
