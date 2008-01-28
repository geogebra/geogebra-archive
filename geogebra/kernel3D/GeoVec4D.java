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

package geogebra.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.kernel.Traceable;

/**
 *
 * @author  Markus + Mathieu
 * @version 
 */
public abstract class GeoVec4D extends GeoElement3D 
implements Traceable {
       
    public double x, y, z, w = Double.NaN;
	public boolean trace;	 
    
    public GeoVec4D(Construction c) {super(c);}  
    
    /** Creates new GeoVec4D with coordinates (x,y,z,w) and label */
    public GeoVec4D(Construction c, double x, double y, double z, double w) {    	
    	super(c);    	
       setCoords(x, y, z, w);
    }                 
    
    /** Copy constructor */
    public GeoVec4D(Construction c, GeoVec4D v) {   
    	super(c); 	
        set(v);
    }
    
//    public GeoElement copy() {
//        return new GeoVec4D(this.cons, this);        
//    }
    
    public boolean isDefined() {
    	return (!(Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z) || Double.isNaN(w)));        
    }
    
    public void setUndefined() {     
    	setCoords(Double.NaN, Double.NaN, Double.NaN, Double.NaN);        
    }       
    
    protected boolean showInEuclidianView() {     
        return isDefined();
    }
    
    protected boolean showInAlgebraView() {
       // return true;
	   //return isDefined();
    	return true;
    }        
    
    public void set(GeoElement3D geo) {    
        GeoVec4D v = (GeoVec4D) geo;        
        setCoords(v.x, v.y, v.z, v.w);        
    }         
    
	public abstract void setCoords(double x, double y, double z, double w);
	public abstract void setCoords(GeoVec4D v) ;
   
    final public double getX() { return x; }
    final public double getY() { return y; }
    final public double getZ() { return z; } 
    final public double getW() { return w; } 
    final public void getCoords(double[] ret) {
        ret[0] = x;
        ret[1] = y;
        ret[2] = z;        
        ret[3] = w;        
    }             

    /** 
     * Writes x and y and z to the array res.
     */
    public void getInhomCoords(double [] res) {       
        res[0] = x;
        res[1] = y;                                
        res[2] = z;                                
    }
    
    // TODO: polar to spherical 
    // POLAR or CARTESIAN mode    
    final public boolean isPolar() { return toStringMode == Kernel.COORD_POLAR; }
    public int getMode() { return toStringMode;  }
    public void setMode(int mode ) {
        toStringMode = mode;
    }        
    
    public void setPolar() { toStringMode = Kernel.COORD_POLAR; }
    public void setCartesian() { toStringMode = Kernel.COORD_CARTESIAN; }
    
    /** Yields true if the coordinates of this vector are equal to
     * those of vector v. 
     */
    final public boolean equals(GeoVec4D v) {
    
        kernel.setMinPrecision();
        boolean ret =  kernel.isEqual(x, v.x) && 
								kernel.isEqual(y, v.y) && 
								kernel.isEqual(z, v.z) && 
								kernel.isEqual(w, v.w);                      
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
        return kernel.isZero(x) && kernel.isZero(y) && kernel.isZero(z) && kernel.isZero(w);
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
    final public double inner(GeoVec4D v) {
        return x * v.x + y * v.y + z * v.z + w * v.w;
    }
    
    /** Changes orientation of this vector. v is changed to -v.    
     */
    final public void changeSign() {
        setCoords(-x, -y, -z, -w);        
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
    final public static void add(GeoVec4D a, GeoVec4D b, GeoVec4D c) {                
        c.setCoords(a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);    
    }
    
     /** returns this - a */
//    final public GeoVec3D sub(GeoVec3D a) {
//        GeoVec3D res = new GeoVec3D(cons);
//        sub(this, a, res);
//        return res;
//    }
    
    /** c = a - b */
    final public static void sub(GeoVec4D a, GeoVec4D b, GeoVec4D c) {
		c.setCoords(a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);         
    }       
    
    public String toString() {
		sbToString.setLength(0);
		sbToString.append('(');
		sbToString.append(x);
		sbToString.append(", ");
		sbToString.append(y);
		sbToString.append(", ");
		sbToString.append(z);
		sbToString.append(", ");
		sbToString.append(w);
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
                sb.append(" x=\"" + x + "\"");
                sb.append(" y=\"" + y + "\"");
                sb.append(" z=\"" + z + "\"");
                sb.append(" w=\"" + w + "\"");
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
