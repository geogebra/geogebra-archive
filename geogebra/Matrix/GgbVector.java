/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.Matrix;

import geogebra.kernel.Kernel;
import geogebra.main.Application;




/**
 * 
 * A Ggb3DVector is composed of {x1,x2,...,xn} coordinates in double precision.
 * This class provides methods for basic linear algebra calculus.
 *
 * @author  ggb3D
 * 
 */
public class GgbVector
	extends GgbMatrix{

	private double norm, sqNorm;
	private boolean calcNorm = true;
	private boolean calcSqNorm = true;
	
	///////////////////////////////////////////////////:
	//Constructors 
	
	/** creates a vector of the dimension specified by rows.  
	 * @param rows number of rows
	 */
	public GgbVector(int rows){
		
		super(rows,1);
		
	}
	
	/** creates a vector with values vals   
	 * @param vals values {x1, x2, ...} */
	public GgbVector(double[] vals){
		
		super(vals.length,1);
		
		for (int i=0;i<vals.length;i++)
			val[i]=vals[i];
		
	}
	
	/** creates a 3D vector with the specified values
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public GgbVector(double x, double y, double z){
		super(4,1);
		val[0]=x;
		val[1]=y;
		val[2]=z;
	}
	
	/** creates a 3D vector/point with the specified values
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public GgbVector(double x, double y, double z, double w){
		super(4,1);
		val[0]=x;
		val[1]=y;
		val[2]=z;
		val[3]=w;
	}
	
	///////////////////////////////////////////////////:
	//setters and getters
	/** sets v(i) to val0 
	 * @param i number of the row
	 * @param val0 value 
	 */
	public void set(int i, double val0){
		val[i-1]=val0;
		calcNorm=calcSqNorm=true;
	}

	/** sets v to vals0 
	 * @param vals0 values {x1, x2, ...}
	 */
	public void set(double[] vals0){
		//Application.debug("-------------vals0.length = "+vals0.length);
		for (int i=0;i<vals0.length;i++)
			val[i]=vals0[i];
		
		calcNorm=calcSqNorm=true;
	}
	
	/** returns v(i)  
	 * @param i number of the row
	 * @return value*/
	public double get(int i){
		return 	val[i-1];
		
	}
	
	/** returns v "x-coord"  
	 * @return x-coord*/	
	public double getX(){
		return val[0];
	}
	
	/** returns v "y-coord"  
	 * @return y-coord*/	
	public double getY(){
		return val[1];
	}
	
	/** returns v "z-coord"  
	 * @return z-coord*/	
	public double getZ(){
		return val[2];
	}	
	
	/** returns v "w-coord"  
	 * @return w-coord*/	
	public double getW(){
		return val[3];
	}
	
	/** returns v last coord  
	 * @return last coord*/	
	public double getLast(){
		return val[getRows()-1];
	}
	
	/** sets the "x-coord" 
	 * @param val
	 */
	public void setX(double val){
		this.val[0]=val;
		calcNorm=calcSqNorm=true;
	}
	
	/** sets the "y-coord" 
	 * @param val
	 */
	public void setY(double val){
		this.val[1]=val;
		calcNorm=calcSqNorm=true;
	}

	/** sets the "z-coord" 
	 * @param val
	 */
	public void setZ(double val){
		this.val[2]=val;
		calcNorm=calcSqNorm=true;
	}

	/** sets the "w-coord" 
	 * @param val
	 */
	public void setW(double val){
		this.val[3]=val;
		calcNorm=calcSqNorm=true;
	}

	/** returns number of rows of the vector 
	 * @return number of rows
	 */
	public int getLength(){
		
		return this.getRows();
		
	}
	
	/** returns a copy of the vector 
	 * @return a copy of the vector
	 */
	public GgbVector copyVector(){ 
		
		GgbVector result = new GgbVector(rows);
		for(int i=0;i<rows;i++)
			result.val[i]=val[i];
		
		return result;
				
	}
	
	/** returns the start-end subvector 
	 * @param start number of starting row
	 * @param end number of end row
	 * @return vector with rows between start and end 
	 */
	public GgbVector subVector(int start, int end){ 
		int r = end-start+1;
		GgbVector result = new GgbVector(r); 

		for(int i=0;i<r;i++)
			result.val[i]=val[start+i-1];
		
		return result;
				
	}
	
	/** returns the subvector composed of this without the row number row 
	 * @param row number of the row to remove
	 * @return vector composed of this without the row number row  
	 */
	public GgbVector subVector(int row){ 
		int r = rows;
		GgbVector result = new GgbVector(r-1); 

		int shift = 0;
		for(int i=0;i<r;i++){
			if (i==row)
				shift = 1;
			else
				result.val[i]=val[i+shift];
		}
		
		return result;
				
	}
	
	///////////////////////////////////////////////////:
	//basic operations 
	
	/** returns dot product  this * v.
	 * <p>
	 * If this={x1,x2,...} and v={x'1,x'2,...}, the dot product is x1*x'1+x2*x'2+...
	 * @param v vector multiplied with
	 * @return value of the dot product*/
	public double dotproduct(GgbVector v){
		int len = getLength();
		double res = 0;
		for(int i=0;i<len; i++)
			res+=val[i]*v.val[i];
		return res;
	}
	
	/** returns cross product this * v.
	 * Attempt that the two vectors are of dimension 3.
	 * <p>
	 * If this={x,y,z} and v={x',y',z'}, then cross product={yz'-y'z,zx'-z'x,xy'-yx'}
	 * @param v vector multiplied with
	 * @return vector resulting of the cross product
	 */
	public GgbVector crossProduct(GgbVector v){
		
		GgbVector ret = new GgbVector(3);
		
		ret.val[0] = val[1]*v.val[2]-val[2]*v.val[1];
		ret.val[1] = val[2]*v.val[0]-val[0]*v.val[2];
		ret.val[2] = val[0]*v.val[1]-val[1]*v.val[0];
		
		return ret;
	}

	
	/** returns the scalar norm.
	 * <p>
	 * If this={x1,x2,...}, then norm=sqrt(x1*x1+x2*x2+...).
	 * Same result as Math.sqrt(this.dotproduct(this))
	 * @return the scalar norm*/
	public double norm(){
		if(calcNorm){
			norm=Math.sqrt(this.dotproduct(this));
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
			sqNorm=this.dotproduct(this);
			calcSqNorm=false;
		}
		return sqNorm;
	}
	
	/** returns this normalized 
	 * @return this/this.norm() 
	 */
	public GgbVector normalized(){
		
		GgbVector ret = new GgbVector(getLength());
		double normInv = 1/this.norm();
		int len = getLength();
		for (int i=0; i<len; i++)
			ret.val[i]=val[i]*normInv;
		
		return ret;
	}
	
	
	/** normalize this */
	public GgbVector normalize(){
		
		double normInv = 1/this.norm();
		int len = getLength();
		for (int i=0; i<len; i++)
			val[i]*=normInv;

		norm=sqNorm=1.0;
		
		return this;
	}
	
	
	
	
	/** returns the distance between this and v 
	 * @param v second vector
	 * @return (this-v).norm()
	 */
	public double distance(GgbVector v){
		
		return this.sub(v).norm();
	}
	
	/** returns the distance between this and a 3D-line represented by the matrix {V O} 
	 * @param O origin of the line
	 * @param V direction of the line
	 * @return distance between this and the line*/
	public double distLine(GgbVector O, GgbVector V){
		
		GgbVector OM = this.sub(O);
		GgbVector N = V.normalized();
		GgbVector OH = (GgbVector) N.mul(OM.dotproduct(N)); //TODO optimize
		GgbVector HM = OM.sub(OH);
		
		return HM.norm();
	}
	
	/** returns this projected on the plane represented by the matrix (third vector used for direction).
	 * If direction is parallel to the plane, return infinite point (direction vector). 
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4. 
	 * @param m matrix {v1 v2 v3 o} where (o,v1,v2) is a coord sys fo the plane, and v3 the direction used for projection
	 * @return two vectors {globalCoords,inPlaneCoords}: the point projected, and the original point in plane coords
	 */
	public GgbVector[] projectPlane(GgbMatrix m){
		GgbVector inPlaneCoords, globalCoords;
		
		
		if  (Kernel.isEqual((m.getVx().crossProduct(m.getVy())).dotproduct(m.getVz()),0,Kernel.STANDARD_PRECISION)){
			//direction of projection is parallel to the plane : point is infinite
			//Application.printStacktrace("infinity");
			inPlaneCoords = new GgbVector(new double[] {0,0,-1,0});
			globalCoords = m.getVz().copyVector();
		}else{
			//m*inPlaneCoords=this
			inPlaneCoords = m.solve(this);

			//globalCoords=this-inPlaneCoords_z*plane_vz
			globalCoords = (GgbVector) this.add(m.getColumn(3).mul(-inPlaneCoords.get(3)));
		}
		
		return new GgbVector[] {globalCoords,inPlaneCoords};
		
	}
	
	/** returns this projected on the plane represented by the matrix, with vector v used for direction. 
	 * <p>
	 *  Attempt this to be of dimension 4, the matrix to be of dimension 4*4, and the vector to be of dimension 4. 
	 * @param m matrix {v1 v2 ?? o} where (o,v1,v2) is a coord sys fo the plane, and v3
	 * @param v the direction used for projection
	 * @return two vectors {globalCoords,inPlaneCoords}: the point projected, and the original point in plane coords
	 */	
	public GgbVector[] projectPlaneThruV(GgbMatrix m, GgbVector v){
		
		GgbMatrix m1 = new GgbMatrix(4,4);
		m1.set(new GgbVector[] {m.getColumn(1), m.getColumn(2), v, m.getColumn(4)});
		
		return projectPlane(m1);
		
	}	
	
	/** returns this projected on the plane represented by the matrix, with vector v used for direction.  
	 *  <p>
	 *  If v is parallel to plane, then plane third vector is used instead
	 * @param m matrix {v1 v2 v3 o} where (o,v1,v2) is a coord sys fo the plane, and v3
	 * @param v the direction used for projection (v3 is used instead if v is parallel to the plane)
	 * @return two vectors {globalCoords,inPlaneCoords}: the point projected, and the original point in plane coords
	 */	
	public GgbVector[] projectPlaneThruVIfPossible(GgbMatrix m, GgbVector v){
		
		// check if v is parallel to plane
		GgbVector v3 = m.getColumn(3);
		if (Kernel.isEqual(v3.dotproduct(v), 0.0, Kernel.STANDARD_PRECISION))
			return projectPlane(m);
		
		// if not, use v for direction
		GgbMatrix m1 = new GgbMatrix(4,4);
		m1.set(new GgbVector[] {m.getColumn(1), m.getColumn(2), v, m.getColumn(4)});
		
		return projectPlane(m1);
		
	}		
	
	
	
	
	
	/** calculates projection of this on the 3D-line represented by the matrix {V O}.
	 * @param O origin of the line
	 * @param V direction of the line
	 * @return {point projected, {parameter on the line, normalized parameter} } */
	public GgbVector[] projectLine(GgbVector O, GgbVector V){
		
		GgbVector OM = this.sub(O);
		GgbVector N = V.normalized();
		double parameter = OM.dotproduct(N);
		GgbMatrix OH = N.mul(parameter);
		GgbVector H = O.add(OH).getColumn(1); //TODO optimize
		
		return new GgbVector[] {H,new GgbVector(new double[] {parameter/V.norm(), parameter})};
		
	}
	
	
	
	/** calculates projection of this as far as possible to the 3D-line represented by the matrix {V O}
	 *  regarding V2 direction.
	 * @param O origin of the line
	 * @param V direction of the line
	 * @param V2 direction of projection
	 * @return point projected*/
	public GgbVector projectNearLine(GgbVector O, GgbVector V, GgbVector V2){
		
		GgbVector V3 = V.crossProduct(V2);
		
		if (Kernel.isEqual(V3.norm(), 0.0, Kernel.STANDARD_PRECISION)){
			return this.copyVector();
		}else{
			GgbMatrix m = new GgbMatrix(4,4);
			m.set(new GgbVector[] {V, V3, V2, O});
			return this.projectPlane(m)[0];
		}
		
	}	
	
	
	
	/**
	 * project this on the line (O,V) in the direction V2.<p>
	 * returns the point of (O,V) that is the nearest to line (this,V2).<p>
	 * if V and V2 are parallel, return O.
	 * @param O origin of the line where this is projected
	 * @param V direction of the line where this is projected
	 * @param V2 direction of projection
	 * @return {point projected, {coord of the proj. point on the line, distance between this and the proj. point}}
	 */
	public GgbVector[] projectOnLineWithDirection(GgbVector O, GgbVector V, GgbVector V2){
		
		GgbVector V3 = V.crossProduct(V2);
		
		if (Kernel.isEqual(V3.norm(), 0.0, Kernel.STANDARD_PRECISION)){
			return new GgbVector[] {O, new GgbVector(new double[] {0,this.sub(O).norm()})};
		}else{
			GgbMatrix m = new GgbMatrix(4,4);
			m.set(new GgbVector[] {V2, V3, V, this});
			GgbVector[] result = O.projectPlane(m);
			return new GgbVector[] {result[0], 
					new GgbVector(new double[] {-result[1].get(3),this.sub(result[0]).norm()})};
		}
		
	}	
	
	/** returns this-v 
	 * @param v vector subtracted
	 * @return this-v 
	 */
	public GgbVector sub(GgbVector v){
		int i;
		GgbVector result=new GgbVector(rows);
		for (i=0;i<rows;i++)
			result.val[i]=val[i]-v.val[i];

		return result;
	}
	
	/** returns n-1 length vector, all coordinates divided by the n-th.
	 * <p>
	 * If this={x1,x2,xn}, it returns {x1/xn,x2/xn,...,x(n-1)}
	 * @return {x1/xn,x2/xn,...,x(n-1)/xn}
	 */
	public GgbVector getInhomCoords(){
		int r = rows;
		GgbVector result=new GgbVector(r-1);

		double wdiv = 1/val[r-1];
		for (int i=0;i<r-1;i++)
			result.val[i]=val[i]*wdiv;

		return result;
	}

	/** returns n length vector, all coordinates divided by the n-th.
	 * <p>
	 * If this={x1,x2,xn}, it returns {x1/xn,x2/xn,...,1}
	 * @return {x1/xn,x2/xn,...,1}*/
	public GgbVector getCoordsLast1(){
		int len = getLength();
		GgbVector result=new GgbVector(len);
		double lastCoord = val[len-1];
		if (lastCoord!=0.0){
			double lastCoordInv=1/lastCoord;
			for (int i=0;i<len;i++)
				result.val[i]=val[i]*lastCoordInv;
		} else
			result.set(this);
		return result;
	}
	
	/**
	 * Return true if this==v for the precision given (ie each coordinates are not different more than precision).
	 * @param v vector compared with
	 * @param precision 
	 * @return true if the vectors are equal
	 */
	public boolean equalsForKernel(GgbVector v, double precision){
		int len = getLength();
		for(int i=0;i<len;i++)
			if (!Kernel.isEqual(val[i], v.val[i], precision))
				return false;
		
		return true;
	}
	
	/**
	 * Return true if all coordinates are not different from val more than precision.
	 * @param val value compared with
	 * @param precision 
	 * @return true if all coordinates are not different from val more than precision.
	 */
	public boolean equalsForKernel(double val, double precision){
		int len = getLength();
		for(int i=0;i<len;i++)
			if (!Kernel.isEqual(this.val[i], val, precision))
				return false;
		
		return true;
		
	}
	
	public GgbVector[] completeOrthonormal() {
		GgbVector vn1 = new GgbVector(4);
		GgbVector vn2 = new GgbVector(4);

		if (val[0] != 0) {
			vn1.val[0] = -val[1];
			vn1.val[1] = val[0];
			vn1.normalize();
		} else {
			vn1.val[0] = 1.0;
		}

		vn2 = this.crossProduct(vn1);
		vn2.normalize();

		return new GgbVector[] { vn1, vn2 };
	}

	
	/////////////////////////////////////////////////////
	// BASIC OPERATIONS
	/////////////////////////////////////////////////////
	
	public GgbVector add(GgbVector v){
		
		return (GgbVector) super.add(v);
		
	}
	
	public GgbVector mul(double val0){
		
		return (GgbVector) super.mul(val0);
	}
	
	
	///////////////////////////////////////////////////:
	/** for testing the package */
	public static synchronized void main(String[] args) {	
		
		GgbVector v1 = new GgbVector(2);
		v1.val[0]=3.0;
		v1.val[1]=4.0;
		
		Application.debug("v1.v1 = "+v1.dotproduct(v1));
	}
}