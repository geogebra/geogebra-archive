/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra3D.Matrix;

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
public class Ggb3DVector
	extends Ggb3DMatrix{
	
	///////////////////////////////////////////////////:
	//Constructors 
	
	/** creates a vector of the dimension specified by rows.  
	 * @param rows number of rows
	 */
	public Ggb3DVector(int rows){
		
		super(rows,1);
		
	}
	
	/** creates a vector with values vals   
	 * @param vals values {x1, x2, ...} */
	public Ggb3DVector(double[] vals){
		
		super(vals.length,1);
		
		for (int i=0;i<vals.length;i++)
			set(i+1,vals[i]);
		
	}
	
	
	///////////////////////////////////////////////////:
	//setters and getters
	/** sets v(i) to val0 
	 * @param i number of the row
	 * @param val0 value 
	 */
	public void set(int i, double val0){
		set(i,1,val0);
	}

	/** sets v to vals0 
	 * @param vals0 values {x1, x2, ...}
	 */
	public void set(double[] vals0){
		//Application.debug("-------------vals0.length = "+vals0.length);
		for (int i=0;i<vals0.length;i++)
			set(i+1,vals0[i]);
		
	}
	
	/** returns v(i)  
	 * @param i number of the row
	 * @return value*/
	public double get(int i){
		return get(i,1);
		
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
	public Ggb3DVector copyVector(){ 
		
		Ggb3DVector result = new Ggb3DVector(getRows()); 

		for(int i=1;i<=result.getRows();i++){
			result.set(i,get(i));
		}
		
		return result;
				
	}
	
	/** returns the start-end subvector 
	 * @param start number of starting row
	 * @param end number of end row
	 * @return vector with rows between start and end 
	 */
	public Ggb3DVector subVector(int start, int end){ 
		
		Ggb3DVector result = new Ggb3DVector(end-start+1); 

		for(int i=1;i<=result.getRows();i++){
			result.set(i,get(start+i-1));
		}
		
		return result;
				
	}
	
	/** returns the subvector composed of this without the row number row 
	 * @param row number of the row to remove
	 * @return vector composed of this without the row number row  
	 */
	public Ggb3DVector subVector(int row){ 
		
		Ggb3DVector result = new Ggb3DVector(this.getRows()-1); 

		int shift = 0;
		for(int i=1;i<=getRows();i++){
			if (i==row)
				shift = 1;
			else
				result.set(i,get(i+shift));
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
	public double dotproduct(Ggb3DVector v){
		
		Ggb3DMatrix v1 = this.transposeCopy();
		Ggb3DMatrix m = v1.mul(v);

		return m.get(1,1);
		
	}
	
	/** returns cross product this * v.
	 * Attempt that the two vectors are of dimension 3.
	 * <p>
	 * If this={x,y,z} and v={x',y',z'}, then cross product={yz'-y'z,zx'-z'x,xy'-yx'}
	 * @param v vector multiplied with
	 * @return vector resulting of the cross product
	 */
	public Ggb3DVector crossProduct(Ggb3DVector v){
		
		Ggb3DVector ret = new Ggb3DVector(3);
		
		ret.set(1, this.get(2)*v.get(3)-this.get(3)*v.get(2));
		ret.set(2, this.get(3)*v.get(1)-this.get(1)*v.get(3));
		ret.set(3, this.get(1)*v.get(2)-this.get(2)*v.get(1));
		
		return ret;
	}
	
	
	
	/** returns the scalar norm.
	 * <p>
	 * If this={x1,x2,...}, then norm=sqrt(x1*x1+x2*x2+...).
	 * Same result as Math.sqrt(this.dotproduct(this))
	 * @return the scalar norm*/
	public double norm(){
		
		return Math.sqrt(this.dotproduct(this));
	}
	
	/** returns the square of the scalar norm.
	 * <p>
	 * If this={x1,x2,...}, then norm=x1*x1+x2*x2+...
	 * Same result as this.dotproduct(this)
	 * @return the scalar norm*/
	public double squareNorm(){
		
		return this.dotproduct(this);
	}
	
	/** returns this normalized 
	 * @return this/this.norm() 
	 */
	public Ggb3DVector normalized(){
		
		Ggb3DVector ret = new Ggb3DVector(getLength());
		double norm = this.norm();
		for (int i=1; i<=getLength(); i++){
			ret.set(i,get(i)/norm);
		}
		
		return ret;
	}
	
	
	/** normalize this */
	public void normalize(){
		
		double norm = this.norm();
		for (int i=1; i<=getLength(); i++){
			this.set(i,get(i)/norm);
		}
		
	
	}
	
	
	
	
	/** returns the distance between this and v 
	 * @param v second vector
	 * @return (this-v).norm()
	 */
	public double distance(Ggb3DVector v){
		
		return this.sub(v).norm();
	}
	
	/** returns the distance between this and a 3D-line represented by the matrix {V O} 
	 * @param O origin of the line
	 * @param V direction of the line
	 * @return distance between this and the line*/
	public double distLine(Ggb3DVector O, Ggb3DVector V){
		
		Ggb3DVector OM = this.sub(O);
		Ggb3DVector N = V.normalized();
		Ggb3DVector OH = (Ggb3DVector) N.mul(OM.dotproduct(N)); //TODO optimize
		Ggb3DVector HM = OM.sub(OH);
		
		return HM.norm();
	}
	
	/** returns this projected on the plane represented by the matrix (third vector used for direction). 
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4. 
	 * @param m matrix {v1 v2 v3 o} where (o,v1,v2) is a coord sys fo the plane, and v3 the direction used for projection
	 * @return two vectors {globalCoords,inPlaneCoords}: the point projected, and the original point in plane coords
	 */
	public Ggb3DVector[] projectPlane(Ggb3DMatrix m){
		Ggb3DVector inPlaneCoords, globalCoords;
		
		//m*inPlaneCoords=this
		inPlaneCoords = m.solve(this);
		
		//globalCoords=this-inPlaneCoords_z*plane_vz
		globalCoords = (Ggb3DVector) this.add(m.getColumn(3).mul(-inPlaneCoords.get(3)));
		
		return new Ggb3DVector[] {globalCoords,inPlaneCoords};
		
	}
	
	/** returns this projected on the plane represented by the matrix, with vector v used for direction. 
	 * <p>
	 *  Attempt this to be of dimension 4, the matrix to be of dimension 4*4, and the vector to be of dimension 4. 
	 * @param m matrix {v1 v2 ?? o} where (o,v1,v2) is a coord sys fo the plane, and v3
	 * @param v the direction used for projection
	 * @return two vectors {globalCoords,inPlaneCoords}: the point projected, and the original point in plane coords
	 */	
	public Ggb3DVector[] projectPlaneThruV(Ggb3DMatrix m, Ggb3DVector v){
		
		Ggb3DMatrix m1 = new Ggb3DMatrix(4,4);
		m1.set(new Ggb3DVector[] {m.getColumn(1), m.getColumn(2), v, m.getColumn(4)});
		
		return projectPlane(m1);
		
	}	
	
	/** returns this projected on the plane represented by the matrix, with vector v used for direction.  
	 *  <p>
	 *  If v is parallel to plane, then plane third vector is used instead
	 * @param m matrix {v1 v2 v3 o} where (o,v1,v2) is a coord sys fo the plane, and v3
	 * @param v the direction used for projection (v3 is used instead if v is parallel to the plane)
	 * @return two vectors {globalCoords,inPlaneCoords}: the point projected, and the original point in plane coords
	 */	
	public Ggb3DVector[] projectPlaneThruVIfPossible(Ggb3DMatrix m, Ggb3DVector v){
		
		// check if v is parallel to plane
		Ggb3DVector v3 = m.getColumn(3);
		if (Kernel.isEqual(v3.dotproduct(v), 0.0, Kernel.STANDARD_PRECISION))
			return projectPlane(m);
		
		// if not, use v for direction
		Ggb3DMatrix m1 = new Ggb3DMatrix(4,4);
		m1.set(new Ggb3DVector[] {m.getColumn(1), m.getColumn(2), v, m.getColumn(4)});
		
		return projectPlane(m1);
		
	}		
	
	
	
	
	
	/** calculates projection of this on the 3D-line represented by the matrix {V O}.
	 * @param O origin of the line
	 * @param V direction of the line
	 * @return {point projected, {parameter on the line, normalized parameter} } */
	public Ggb3DVector[] projectLine(Ggb3DVector O, Ggb3DVector V){
		
		Ggb3DVector OM = this.sub(O);
		Ggb3DVector N = V.normalized();
		double parameter = OM.dotproduct(N);
		Ggb3DMatrix OH = N.mul(parameter);
		Ggb3DVector H = O.add(OH).getColumn(1); //TODO optimize
		
		return new Ggb3DVector[] {H,new Ggb3DVector(new double[] {parameter/V.norm(), parameter})};
		
	}
	
	
	
	/** calculates projection of this as far as possible to the 3D-line represented by the matrix {V O}
	 *  regarding V2 direction.
	 * @param O origin of the line
	 * @param V direction of the line
	 * @param V2 direction of projection
	 * @return point projected*/
	public Ggb3DVector projectNearLine(Ggb3DVector O, Ggb3DVector V, Ggb3DVector V2){
		
		Ggb3DVector V3 = V.crossProduct(V2);
		
		if (Kernel.isEqual(V3.norm(), 0.0, Kernel.STANDARD_PRECISION)){
			return this.copyVector();
		}else{
			Ggb3DMatrix m = new Ggb3DMatrix(4,4);
			m.set(new Ggb3DVector[] {V, V3, V2, O});
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
	public Ggb3DVector[] projectOnLineWithDirection(Ggb3DVector O, Ggb3DVector V, Ggb3DVector V2){
		
		Ggb3DVector V3 = V.crossProduct(V2);
		
		if (Kernel.isEqual(V3.norm(), 0.0, Kernel.STANDARD_PRECISION)){
			return new Ggb3DVector[] {O, new Ggb3DVector(new double[] {0,this.sub(O).norm()})};
		}else{
			Ggb3DMatrix m = new Ggb3DMatrix(4,4);
			m.set(new Ggb3DVector[] {V2, V3, V, this});
			Ggb3DVector[] result = O.projectPlane(m);
			return new Ggb3DVector[] {result[0], 
					new Ggb3DVector(new double[] {-result[1].get(3),this.sub(result[0]).norm()})};
		}
		
	}	
	
	/** returns this-v 
	 * @param v vector subtracted
	 * @return this-v 
	 */
	public Ggb3DVector sub(Ggb3DVector v){
		int i;
		Ggb3DVector result=new Ggb3DVector(rows);
		for (i=1;i<=rows;i++){
			result.set(i,this.get(i)-v.get(i));
		}
		return result;
	}
	

	
	/** returns n-1 length vector, all coordinates divided by the n-th.
	 * <p>
	 * If this={x1,x2,xn}, it returns {x1/xn,x2/xn,...,x(n-1)}
	 * @return {x1/xn,x2/xn,...,x(n-1)/xn}
	 */
	public Ggb3DVector getInhomCoords(){
		Ggb3DVector result=new Ggb3DVector(getLength()-1);
		int i;
		for (i=1;i<getLength();i++){
			result.set(i,get(i)/get(getLength()));
		}
		return result;
	}

	/** returns n length vector, all coordinates divided by the n-th.
	 * <p>
	 * If this={x1,x2,xn}, it returns {x1/xn,x2/xn,...,1}
	 * @return {x1/xn,x2/xn,...,1}*/
	public Ggb3DVector getCoordsLast1(){
		Ggb3DVector result=new Ggb3DVector(getLength());
		int i;
		double lastCoord = get(getLength());
		if (lastCoord!=0.0)
			for (i=1;i<=getLength();i++){
				result.set(i,get(i)/lastCoord);
			}
		else
			result.set(this);
		return result;
	}
	
	
	
	
	
	
	///////////////////////////////////////////////////:
	/** for testing the package */
	public static synchronized void main(String[] args) {	
		
		Ggb3DVector v1 = new Ggb3DVector(2);
		v1.set(1,3.0);
		v1.set(2,4.0);
		
		Application.debug("v1.v1 = "+v1.dotproduct(v1));
		
		
	}
	
	

}