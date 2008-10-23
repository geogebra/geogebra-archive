/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel.linalg;

import geogebra.main.Application;




/**
 *
 * @author  ggb3D
 * @version 
 */
public class GgbVector
	extends GgbMatrix{
	
	///////////////////////////////////////////////////:
	//Constructors 
	
	/** creates a vector of dimension rows   */
	public GgbVector(int rows){
		
		super(rows,1);
		
	}
	
	/** creates a vector with values vals   */
	public GgbVector(double[] vals){
		
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
	
	/** returns length of the vector */
	public int getLength(){
		
		return this.getRows();
		
	}
	
	/** returns a copy of the vector */
	public GgbVector copyVector(){ //TODO cast
		
		GgbVector result = new GgbVector(getRows()); 

		for(int i=1;i<=result.getRows();i++){
			result.set(i,get(i));
		}
		
		return result;
				
	}
	
	/** returns the start-end subvector */
	public GgbVector subVector(int start, int end){ 
		
		GgbVector result = new GgbVector(end-start+1); 

		for(int i=1;i<=result.getRows();i++){
			result.set(i,get(start+i-1));
		}
		
		return result;
				
	}
	
	
	
	///////////////////////////////////////////////////:
	//basic operations 
	
	/** returns scalar product  this . v  */
	public double dotproduct(GgbVector v){
		
		GgbMatrix v1 = this.transposeCopy();
		GgbMatrix m = v1.mul(v);

		return m.get(1,1);
		
	}
	
	/** returns cross product this ^ v */
	public GgbVector crossProduct(GgbVector v){
		
		GgbVector ret = new GgbVector(3);
		
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
	public GgbVector normalized(){
		
		GgbVector ret = new GgbVector(getLength());
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
	public double distance(GgbVector v){
		
		return this.sub(v).norm();
	}
	
	/** returns the distance between this and a 3D-line represented by the matrix [V O] */
	public double distLine(GgbVector O, GgbVector V){
		
		GgbVector OM = this.sub(O);
		//OM.SystemPrint();
		GgbVector N = V.normalized();
		GgbVector OH = N.mul(OM.dotproduct(N)).getColumn(1); //TODO optimize
		//OH.SystemPrint();
		GgbVector HM = OM.sub(OH);
		
		return HM.norm();
	}
	
	/** returns this projected on the plane (third vector used for direction) 
	 *  result two GgbVectors, the point and (x,y,l,1) : (x,y) plane coordinates, l direction coordinate 
	 */
	public GgbVector[] projectPlane(GgbMatrix m){
		GgbVector ret1, ret2;
		
		ret1 = m.solve(this);
		ret1.set(3,-ret1.get(3));
		
		ret2 = this.add(m.getColumn(3).mul(ret1.get(3))).v();
		
		return new GgbVector[] {ret2,ret1};
		
	}
	
	/** returns this projected on the plane with vector v used for direction 
	 *  result GgbVector (x,y,l,1) : (x,y) plane coordinates, l direction coordinate 
	 */	
	public GgbVector[] projectPlaneThruV(GgbMatrix m, GgbVector v){
		//GgbVector ret;
		
		GgbMatrix m1 = new GgbMatrix(4,4);
		m1.set(new GgbVector[] {m.getColumn(1), m.getColumn(2), v, m.getColumn(4)});
		
		return projectPlane(m1);
		
	}	
	
	
	
	
	
	
	
	/** returns the projection of this on the 3D-line represented by the matrix [V O] */
	public GgbVector projectLine(GgbVector O, GgbVector V){
		
		GgbVector OM = this.sub(O);
		GgbVector N = V.normalized();
		GgbMatrix OH = N.mul(OM.dotproduct(N));
		GgbVector H = O.add(OH).getColumn(1); //TODO optimize
		
		return H;
		
	}
	
	
	
	/** returns this-v */
	public GgbVector sub(GgbVector v){
		int i;
		GgbVector result=new GgbVector(rows);
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
	public GgbVector getInhomCoords(){
		GgbVector result=new GgbVector(getLength()-1);
		int i;
		for (i=1;i<getLength();i++){
			result.set(i,get(i)/get(getLength()));
		}
		return result;
	}

	/** returns n length vector, all coordinates divided by the n-th */
	public GgbVector getCoordsLast1(){
		GgbVector result=new GgbVector(getLength());
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
	//testing the package
	public static synchronized void main(String[] args) {	
		
		GgbVector v1 = new GgbVector(2);
		v1.set(1,3.0);
		v1.set(2,4.0);
		
		Application.debug("v1.v1 = "+v1.dotproduct(v1));
		
		
	}
	
	

}