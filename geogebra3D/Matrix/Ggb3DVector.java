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
 * @version 3.2
 */
public class Ggb3DVector
	extends Ggb3DMatrix{
	
	///////////////////////////////////////////////////:
	//Constructors 
	
	/** creates a vector of dimension rows   */
	public Ggb3DVector(int rows){
		
		super(rows,1);
		
	}
	
	/** creates a vector with values vals   */
	public Ggb3DVector(double[] vals){
		
		super(vals.length,1);
		
		for (int i=0;i<vals.length;i++)
			set(i+1,vals[i]);
		
	}
	
	
	///////////////////////////////////////////////////:
	//setters and getters
	/** sets v(i) to val0 */
	public void set(int i, double val0){
		set(i,1,val0);
	}

	/** sets v to vals0 */
	public void set(double[] vals0){
		//Application.debug("-------------vals0.length = "+vals0.length);
		for (int i=0;i<vals0.length;i++)
			set(i+1,vals0[i]);
		
	}
	
	/** returns v(i)  */
	public double get(int i){
		return get(i,1);
		
	}
	
	/** returns number of coordinates of the vector */
	public int getLength(){
		
		return this.getRows();
		
	}
	
	/** returns a copy of the vector */
	public Ggb3DVector copyVector(){ //TODO cast
		
		Ggb3DVector result = new Ggb3DVector(getRows()); 

		for(int i=1;i<=result.getRows();i++){
			result.set(i,get(i));
		}
		
		return result;
				
	}
	
	/** returns the start-end subvector */
	public Ggb3DVector subVector(int start, int end){ 
		
		Ggb3DVector result = new Ggb3DVector(end-start+1); 

		for(int i=1;i<=result.getRows();i++){
			result.set(i,get(start+i-1));
		}
		
		return result;
				
	}
	
	
	
	///////////////////////////////////////////////////:
	//basic operations 
	
	/** returns scalar product  this . v  */
	public double dotproduct(Ggb3DVector v){
		
		Ggb3DMatrix v1 = this.transposeCopy();
		Ggb3DMatrix m = v1.mul(v);

		return m.get(1,1);
		
	}
	
	/** returns cross product this ^ v */
	public Ggb3DVector crossProduct(Ggb3DVector v){
		
		Ggb3DVector ret = new Ggb3DVector(3);
		
		ret.set(1, this.get(2)*v.get(3)-this.get(3)*v.get(2));
		ret.set(2, this.get(3)*v.get(1)-this.get(1)*v.get(3));
		ret.set(3, this.get(1)*v.get(2)-this.get(2)*v.get(1));
		
		return ret;
	}
	
	
	
	/** returns the scalar norm */
	public double norm(){
		
		return Math.sqrt(this.dotproduct(this));
	}
	
	/** returns this normalized */
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
	
	
	
	
	/** returns the distance between this and v */
	public double distance(Ggb3DVector v){
		
		return this.sub(v).norm();
	}
	
	/** returns the distance between this and a 3D-line represented by the matrix [V O] */
	public double distLine(Ggb3DVector O, Ggb3DVector V){
		
		Ggb3DVector OM = this.sub(O);
		//OM.SystemPrint();
		Ggb3DVector N = V.normalized();
		Ggb3DVector OH = N.mul(OM.dotproduct(N)).getColumn(1); //TODO optimize
		//OH.SystemPrint();
		Ggb3DVector HM = OM.sub(OH);
		
		return HM.norm();
	}
	
	/** returns this projected on the plane (third vector used for direction) 
	 *  result two GgbVectors, the point and (x,y,l,1) : (x,y) plane coordinates, l direction coordinate 
	 */
	public Ggb3DVector[] projectPlane(Ggb3DMatrix m){
		Ggb3DVector ret1, ret2;
		
		ret1 = m.solve(this);
		ret1.set(3,-ret1.get(3));
		
		ret2 = (Ggb3DVector) this.add(m.getColumn(3).mul(ret1.get(3)));
		
		return new Ggb3DVector[] {ret2,ret1};
		
	}
	
	/** returns this projected on the plane with vector v used for direction 
	 *  result two GgbVectors, the point and (x,y,l,1) : (x,y) plane coordinates, l direction coordinate 
	 */	
	public Ggb3DVector[] projectPlaneThruV(Ggb3DMatrix m, Ggb3DVector v){
		//GgbVector ret;
		
		Ggb3DMatrix m1 = new Ggb3DMatrix(4,4);
		m1.set(new Ggb3DVector[] {m.getColumn(1), m.getColumn(2), v, m.getColumn(4)});
		
		return projectPlane(m1);
		
	}	
	
	/** returns this projected on the plane with vector v used for direction 
	 *  if v is parallel to plane, then plane third vector is used instead
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
	
	
	
	
	
	/** calculates projection of this on the 3D-line represented by the matrix [V O]
	 *  and returns {projection, {parameter, normalized parameter} } */
	public Ggb3DVector[] projectLine(Ggb3DVector O, Ggb3DVector V){
		
		Ggb3DVector OM = this.sub(O);
		Ggb3DVector N = V.normalized();
		double parameter = OM.dotproduct(N);
		Ggb3DMatrix OH = N.mul(parameter);
		Ggb3DVector H = O.add(OH).getColumn(1); //TODO optimize
		
		return new Ggb3DVector[] {H,new Ggb3DVector(new double[] {parameter/V.norm(), parameter})};
		
	}
	
	
	
	/** calculates projection of this as far as possible to the 3D-line represented by the matrix [V O]
	 *  regarding V2 direction
	 *  and returns projection */
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
	
	/** returns this-v */
	public Ggb3DVector sub(Ggb3DVector v){
		int i;
		Ggb3DVector result=new Ggb3DVector(rows);
		for (i=1;i<=rows;i++){
			result.set(i,this.get(i)-v.get(i));
		}
		return result;
	}
	
	/*
	public GgbVector subInhom(GgbVector v){
		GgbVector result=this.sub(v);
		result.set(getLength(),get(getLength()));
		return result;
	}
	*/
	
	/** returns n-1 length vector, all coordinates divided by the n-th */
	public Ggb3DVector getInhomCoords(){
		Ggb3DVector result=new Ggb3DVector(getLength()-1);
		int i;
		for (i=1;i<getLength();i++){
			result.set(i,get(i)/get(getLength()));
		}
		return result;
	}

	/** returns n length vector, all coordinates divided by the n-th */
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