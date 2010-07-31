package geogebra.Matrix;

import geogebra.kernel.Kernel;

public class GgbVector3D{
	public double x, y, z, w;
	private double norm, sqNorm;
	private boolean calcNorm = true;
	private boolean calcSqNorm = true;
	
	public GgbVector3D(double x, double y, double z, double w) {
		this.x=x; this.y=y; this.z=z; this.w=w;
	}
	
	///////////////////////////////////////////////////:
	//setters and getters
	/** sets v(i) to val0 
	 * @param i number of the row
	 * @param val0 value 
	 */
	public void set(int i, double val0){
		if(i==0) x=val0;
		if(i==1) y=val0;
		if(i==2) z=val0;
		if(i==3) w=val0;
		calcNorm=calcSqNorm=true;
	}

	/** sets v to vals0 
	 * @param vals0 values {x1, x2, ...}
	 */
	public void set(double[] vals0){
		x=vals0[0]; 
		y=vals0[1]; 
		z=vals0[2]; 
		w=vals0[3];
		calcNorm=calcSqNorm=true;
	}
	
	/** returns v(i)  
	 * @param i number of the row
	 * @return value*/
	public double get(int i){
		if(i==0) return x;
		if(i==1) return y;
		if(i==2) return z;
		else return w;
	}
	
	/** returns v "x-coord"  
	 * @return x-coord*/	
	public double getX(){ return x; }
	
	/** returns v "y-coord"  
	 * @return y-coord*/	
	public double getY(){ return y; }
	
	/** returns v "z-coord"  
	 * @return z-coord*/	
	public double getZ(){ return z; }
	
	/** returns v "w-coord"  
	 * @return w-coord*/	
	public double getW(){ return w; }
	
	/** sets the "x-coord" 
	 * @param val
	 */
	public void setX(double val){ x=val; calcNorm=calcSqNorm=true; }
	
	/** sets the "y-coord" 
	 * @param val
	 */
	public void setY(double val){ y=val; calcNorm=calcSqNorm=true; }

	/** sets the "z-coord" 
	 * @param val
	 */
	public void setZ(double val){ z=val; calcNorm=calcSqNorm=true; }

	/** sets the "w-coord" 
	 * @param val
	 */
	public void setW(double val){ w=val; calcNorm=calcSqNorm=true; }
	
	///////////////////////////////////////////////////:
	//basic operations 
	
	/** returns dot product  this * v.
	 * <p>
	 * If this={x1,x2,...} and v={x'1,x'2,...}, the dot product is x1*x'1+x2*x'2+...
	 * @param v vector multiplied with
	 * @return value of the dot product*/
	public double dotproduct(GgbVector3D a){
		return x*a.x+y*a.y+z*a.z;
	}
	
	/** returns cross product this * v.
	 * Attempt that the two vectors are of dimension 3.
	 * <p>
	 * If this={x,y,z} and v={x',y',z'}, then cross product={yz'-y'z,zx'-z'x,xy'-yx'}
	 * @param v vector multiplied with
	 * @return vector resulting of the cross product
	 */
	public GgbVector3D crossProduct(GgbVector3D a){
		return new GgbVector3D(y*a.z-z*a.y,z*a.x-x*a.z,
							   x*a.y-y*a.x,0);
	}
	
	
	
	/** returns the scalar norm.
	 * <p>
	 * If this={x1,x2,...}, then norm=sqrt(x1*x1+x2*x2+...).
	 * Same result as Math.sqrt(this.dotproduct(this))
	 * @return the scalar norm*/
	public double norm(){
		if(calcNorm){
			norm=Math.sqrt(x*x+y*y+z*z);
			calcNorm=false;
		}
		return norm;
	}
	
	/** returns the square of the scalar norm.
	 * <p>
	 * If this={x1,x2,...}, then norm=x1*x1+x2*x2+...
	 * Same result as this.dotproduct(this)
	 * @return the scalar norm*/
	public double squareNorm(){
		if(calcSqNorm){
			sqNorm=x*x+y*y+z*z;
			calcSqNorm=false;
		}
		return sqNorm;
	}
	
	/** returns this normalized 
	 * @return this/this.norm() 
	 */
	public GgbVector3D normalized(){
		
		double inv;
		if(calcNorm)
			inv=1/Math.sqrt(x*x+y*y+z*z);
		else
			inv=1/norm;
		return new GgbVector3D(x*inv, y*inv, z*inv, w*inv);
	}
	
	
	/** normalize this */
	public GgbVector3D normalize(){
		double inv;
		if(calcNorm)
			inv=1/Math.sqrt(x*x+y*y+z*z);
		else
			inv=1/norm;
		x*=inv;
		y*=inv;
		z*=inv;
		norm=sqNorm=1.0;
		return this;
	}
	
	/** returns this-v 
	 * @param v vector subtracted
	 * @return this-v 
	 */
	public GgbVector3D sub(GgbVector3D v){
		return new GgbVector3D(x-v.x,y-v.y,z-v.z,0);
	}
	
	/** returns this-v 
	 * @param v vector subtracted
	 * @return this-v 
	 */
	public GgbVector3D add(GgbVector3D v){
		return new GgbVector3D(x+v.x,y+v.y,z+v.z,0);
	}

	/**
	 * @return
	 */
	public boolean isDefined() {
		return !(x!=x || y!=y || z!=z);
	}
	
	/** returns a copy of the vector 
	 * @return a copy of the vector
	 */
	public GgbVector3D copyVector(){ 
		return new GgbVector3D(x,y,z,w);
				
	}

	public boolean isFinite() {
		return !((x == Double.POSITIVE_INFINITY) || (x == Double.NEGATIVE_INFINITY) ||
				 (y == Double.POSITIVE_INFINITY) || (y == Double.NEGATIVE_INFINITY) ||
				 (z == Double.POSITIVE_INFINITY) || (z == Double.NEGATIVE_INFINITY));
	}
}
