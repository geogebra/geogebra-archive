/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * GeoVec3D.java
 *
 * Created on 31. August 2001, 11:22
 */

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Traceable;
import geogebra3D.kernel3D.Linalg.GgbMatrix;
import geogebra3D.kernel3D.Linalg.GgbVector;

/**
 *
 * @author  Markus + Mathieu
 * @version 
 */
public abstract class GeoVec extends GeoElement3D //TODO extends GeoElement
implements Traceable {
       
    public GgbVector v;
	public boolean trace;	 
    
	
    public GeoVec(Construction c) {super(c);}  

    public GeoVec(Construction c, int n) {
    	this(c);
    	v = new GgbVector(n);
    }  

    /** Creates new GeoVec with coordinates coords[] and label */
    public GeoVec(Construction c, double[] coords) {    	
    	super(c); 
    	
    	v = new GgbVector(coords);
    	v.set(Double.NaN);
    }                 
    
    /** Copy constructor */
    public GeoVec(Construction c, GeoVec vec) {   
    	super(c); 	
        set(vec);
    }
    
    /*
    public GeoElement copy() {
        return new GeoVec(this.cons, this);        
    }
    */
    
    public boolean isDefined() {
    	return v.isDefined();        
    }
    
    public void setUndefined() {  
    	if (v!=null)
    		v.set(Double.NaN);       
    }       
    
    protected boolean showInEuclidianView() {     
        return isDefined();
    }
    
    protected boolean showInAlgebraView() {
       // return true;
	   //return isDefined();
    	return true;
    }        
    
    public void set(GeoElement geo) {    
        GeoVec vec = (GeoVec) geo;        
        setCoords(vec.v);        
    }         
    
	public void setCoords(GgbVector v0){
		v.set(v0);
	}
	
	public void setCoords(double[] vals){
		v.set(vals);
	}
	
	//	TODO cast on add, mul methods in GgbVector
	public void setCoords(GgbMatrix v0){		
		v.set(v0);		
	}
	public void setCoords(GeoVec vec){
		v.set(vec.v);
	}


	/*
    final public double getX() { return x; }
    final public double getY() { return y; }
    final public double getZ() { return z; } 
    final public double getW() { return w; } 
    */
    final public GgbVector getCoords() {
    	return v;
    }             

    /** 
     * Writes x and y and z to the array res.
     */
    /*
    public void getInhomCoords(double [] res) {       
        res[0] = x;
        res[1] = y;                                
        res[2] = z;                                
    }
    */
    
    
    /** Yields true if the coordinates of this vector are equal to
     * those of vector v. 
     */
    final public boolean equals(GeoVec vec) {
    
        kernel.setMinPrecision();
        
        boolean ret = true;
        
        for(int i=1;(i<=v.getLength())&&(ret);i++)
        	ret = ret && kernel.isEqual(v.get(i), vec.v.get(i));             
        kernel.resetPrecision();
        return ret;
    }
    
	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}
    
    /** Yields true if this vector and v are linear dependent 
     * This is done by calculating the cross product
     * of this vector an v: this lin.dep. v <=> this.cross(v) = nullvector.
     */
	// TODO : update
	/* 
    final public boolean linDep(GeoVec4D v) {
        // v lin.dep this  <=>  cross(v,w) = o            
        return kernel.isEqual(y * v.z, z * v.y)
			&& kernel.isEqual(z * v.x, x * v.z) 
			&& kernel.isEqual(x * v.y, y * v.x);       
    }*/
    
    final public boolean isZero() {
    	boolean ret = true;
        for(int i=1;(i<=v.getLength())&&(ret);i++)
        	ret = ret && kernel.isZero(v.get(i));             
        return ret;
    }
    
     /** Calculates the cross product of this vector and vector v.
     * The result ist returned as a new GeoVec3D.
     */
//    final public GeoVec3D cross(GeoVec3D v) {         
//       GeoVec3D res = new GeoVec3D(v.cons);
//       cross(this, v, res);
//       return res;
//    }
    
    /** Calculates the cross product of vectors u and v.
     * The result is stored in w.
     */
    // TODO : update
    /*
    final public static void cross(GeoVec4D u, GeoVec4D v, GeoVec4D w) {                
        w.setCoords( u.y * v.z - u.z * v.y, 
        					 u.z * v.x - u.x * v.z,  
        					 u.x * v.y - u.y * v.x );              
    } 
    */ 
    
    /** Calculates the line through the points A and B.
     * The result is stored in g.
     */
    // TODO : update
    /*
    final public static void lineThroughPoints(GeoPoint A, GeoPoint B, GeoLine g) {
    	// note: this could be done simply using cross(A, B, g)
    	// but we want to avoid large coefficients in the line
    	// and we want AB to be the direction vector of the line
    	
    	if (!(A.isDefined() && B.isDefined())) {
    		g.setUndefined();
    		return;
    	}
    	
    	if (A.isInfinite()) {// A is direction
    		if (B.isInfinite()) { 
				// g is undefined
			    g.setUndefined();
			} else { 
				// through point B
				g.setCoords(A.y , 
		    			    -A.x,
						    A.x * B.inhomY - A.y * B.inhomX);
			}
    	}
    	else { // through point A
			if (B.isInfinite()) { 
				// B is direction
			    g.setCoords(-B.y, 
	    			        B.x,
					        A.inhomX * B.y - A.inhomY * B.x);
			} else { 
				// through point B
				g.setCoords(A.inhomY - B.inhomY, 
		    			   B.inhomX - A.inhomX,
						   A.inhomX * B.inhomY - A.inhomY * B.inhomX);
			}
    	}            
    }  
    
    */
    
    /** Calculates the line through the point A with direction v.
     * The result is stored in g.
     */
    // TODO : update
    /*
    final public static void lineThroughPointVector(GeoPoint A, GeoVec4D v, GeoLine g) {
    	// note: this could be done simply using cross(A, v, g)
    	// but we want to avoid large coefficients in the line
    	// and we want v to be the direction vector of the line
    	
    	if (A.isInfinite()) {// A is direction
			g.setUndefined();
    	}
    	else { // through point A
			// v is direction
		    g.setCoords(-v.y, 
    			        v.x,
				        A.inhomX * v.y - A.inhomY * v.x);
    	}        
    }  
    */
    
    /** Calculates the cross product of vectors u and v.
     * The result is stored in w.
     */
    // TODO : update
    /*
    final public static void cross(GeoVec4D u, 
                                   double vx, double vy, double vz, GeoVec4D w) {
		w.setCoords( u.y * vz - u.z * vy, 
							 u.z * vx - u.x * vz,  
							 u.x * vy - u.y * vx );                                 
    }
    */
    
     /** Calculates the inner product of this vector and vector v.
     */
    final public double inner(GeoVec vec) {
        return v.dotproduct(vec.v);
    }
    
    /** Changes orientation of this vector. v is changed to -v.    
     */
    final public void changeSign() {
        setCoords(v.mul(-1.0));        
    }
    
    /** returns -v
     */
//    final public GeoVec3D getNegVec() {
//        return new GeoVec3D(cons, -x, -y, -z);        
//    }
    
    /** returns this + a */
//    final public GeoVec3D add(GeoVec3D a) {
//        GeoVec3D res = new GeoVec3D(cons);
//        add(this, a, res);
//        return res;
//    }    
        
    /** c = a + b */
    final public static void add(GeoVec a, GeoVec b, GeoVec c) {                
        c.setCoords(a.v.add(b.v));    
    }
    
     /** returns this - a */
//    final public GeoVec3D sub(GeoVec3D a) {
//        GeoVec3D res = new GeoVec3D(cons);
//        sub(this, a, res);
//        return res;
//    }
    
    /** c = a - b */
    final public static void sub(GeoVec a, GeoVec b, GeoVec c) {
    	c.setCoords(a.v.add(b.v.mul(-1.0)));           
    }       
    
    public String toString() {
		sbToString.setLength(0);
        for(int i=1;i<=v.getLength();i++){
        	sbToString.append('(');
        	sbToString.append(v.get(i));
        	sbToString.append(", ");
        }
		sbToString.append(')');
        return sbToString.toString();
    }
	private StringBuffer sbToString = new StringBuffer(50);
	
    /**
     * returns all class-specific xml tags for saveXML
     */
    protected String getXMLtags() {
        StringBuffer sb = new StringBuffer();
        //TODO sb.append(super.getXMLtags());
        
        
        sb.append("\t<coords");
        for(int i=1;i<=v.getLength();i++){
        	sb.append(" x"+i+"=\"" + v.get(i) + "\"");
        }
        sb.append("/>\n");
        
        return sb.toString();
    }
    
	public boolean isNumberValue() {
		return false;
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}   
    
}
