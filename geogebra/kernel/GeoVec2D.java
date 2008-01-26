/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoVec2D.java
 *
 * Created on 31. August 2001, 11:34
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.VectorValue;

import java.util.HashSet;

/** 
 * 
 * @author  Markus
 * @version 
 */
final public class GeoVec2D // extends GeoElement
implements VectorValue {        

    public double x = Double.NaN;
    public double y = Double.NaN;    
    
    private int mode; // POLAR or CARTESIAN   
    
    private Kernel kernel;
    
    /** Creates new GeoVec2D */
    public GeoVec2D(Kernel kernel) {
    	this.kernel = kernel;
    }
    
    /** Creates new GeoVec2D with coordinates (x,y)*/
    public GeoVec2D(Kernel kernel, double x, double y) {
    	this(kernel);
        this.x = x;
        this.y = y;
    }
    
    /** Creates new GeoVec2D with coordinates (a[0],a[1])*/
    public GeoVec2D(Kernel kernel, double [] a) {
    	this(kernel);
        x = a[0];
        y = a[1];
    }
    
    /** Copy constructor */
    public GeoVec2D(GeoVec2D v) {
    	this(v.kernel);
        x = v.x;
        y = v.y;
    }
    
	public ExpressionValue deepCopy(Kernel kernel) {
		return new GeoVec2D(this);
	}   
	
    public void resolveVariables() {     
    }
            
    /** Creates new GeoVec2D as vector between Points P and Q */
    public GeoVec2D(Kernel kernel, GeoPoint p, GeoPoint q) {   
    	this(kernel);    
        x = q.x - p.x;
        y = q.y - p.y;
    }
   
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setCoords(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public void setCoords(double [] a) {
        x = a[0];
        y = a[1];
    }
    
    public void setCoords(GeoVec2D v) {
        x = v.x;
        y = v.y;
    }
    
    public void setPolarCoords(double r, double phi) {              
        x = r * Math.cos( phi );
        y = r * Math.sin( phi );  
    } 
   
    final public double getX() { return x; }
    final public double getY() { return y; }  
    final public double getR() {  return length(x, y); }
    final public double getPhi() { return Math.atan2(y, x); }    
    
    final public double [] getCoords() {
        double [] res = { x, y };
        return res;
    }
    
    /** Calculates the eucilidian length of this 2D vector.
     * The result is sqrt(x^2  + y^2).
     */
    final public double length() {
        return length(x, y);
    } 
    
    /** Calculates the eucilidian length of this 2D vector.
     * The result is sqrt(a[0]^2  + a[1]^2).
     */
    final public static double length(double [] a) {
        return length(a[0], a[1]);
    } 
    
     /** Calculates the euclidian length sqrt(a^2  + b^2).     
     */
    final public static double length(double a, double b) {                
        return Math.sqrt(a*a + b*b);
        
        /*
         * The Algorithm ist taken
         * from Numerical Recipes, Appendix C, p.949 (Cabs) and
         * avoids overflows.         
         *
        double res;        
        double x = Math.abs(a);
        double y = Math.abs(b);
        
        if ( Kernel.isZero(x) ) 
            res = y;
        else if ( Kernel.isZero(y) )
            res = x;
        else if ( x > y ) {
            double temp = y / x;
            res  = x * Math.sqrt(1.0 + temp * temp);
        } else {
            double temp = x / y;
            res  = y * Math.sqrt(1.0 + temp * temp);
        }
        return res;
        */           
    } 
    
    /** Changes this vector to a vector with the same direction 
     * and orientation, but length 1.
     */
    final public void makeUnitVector() {
        double len = this.length();
        x = x / len;
        y = y / len;
    }
    
    /** Returns a new vector with the same direction 
     * and orientation, but length 1.
     */
    final public GeoVec2D getUnitVector() {
        double len = this.length();
        return new GeoVec2D(kernel,  x / len, y / len );
    }
    
    /** Returns the coordinates of a vector with the same direction 
     * and orientation, but length 1.
     */
    final public double[] getUnitCoords() {
        double len = this.length();
        double [] res = { x / len, y / len };
        return res;
    }

     /** Calculates the inner product of this vector and vector v.
     */
    final public double inner(GeoVec2D v) {
        return x * v.x + y * v.y;
    }
    
    /** Yields true if the coordinates of this vector are equal to
     * those of vector v. 
     */
    final public boolean equals(GeoVec2D v) {                   
        return kernel.isEqual(x, v.x) && kernel.isEqual(y, v.y);                   
    }
    
    /** Yields true if this vector and v are linear dependent 
     * This is done by calculating the determinant
     * of this vector an v: this = v <=> det(this, v) = nullvector.
     */
    final public boolean linDep(GeoVec2D v) {
        // v = l* w  <=>  det(v, w) = o
        return kernel.isZero(det(this, v));                   
    }
    
    /** calculates the determinant of u and v.
     * det(u,v) = u1*v2 - u2*v1
     */
    final public static double det(GeoVec2D u, GeoVec2D v) {
        return u.x * v.y - u.y * v.x;
        /*
        // symmetric operation
        // det(u,v) = -det(v,u)
        if (u.objectID < v.objectID) {
            return u.x * v.y - u.y * v.x;
        } else {
            return -(v.x * u.y - v.y * u.x);
        }*/
    }
    
    /**
     * translate this vector by vector v
     */
    final public void translate(GeoVec2D v) {
        x += v.x;
        y += v.y;
    }
    
    /**
     * rotate this vector by angle phi
     */
    final public void rotate(double phi) {
        double cos = Math.cos(phi);
        double sin = Math.sin(phi);
        
        double x0 = x * cos - y * sin;
        y = x * sin + y * cos;
        x = x0;        
    }  
    
    /**
     * mirror this point at point Q
     */
    final public void mirror(GeoPoint Q) {           
        x = 2.0 * Q.inhomX - x;
        y = 2.0 * Q.inhomY - y;
    }
    
    /**
     * mirror transform with angle phi
     *  [ cos(phi)       sin(phi)   ]
     *  [ sin(phi)      -cos(phi)   ]  
     */
    final public void mirror(double phi) {
        double cos = Math.cos(phi);
        double sin = Math.sin(phi);
                
        double x0 = x * cos + y * sin;
        y = x * sin - y * cos;
        x = x0;        
    }
    
     /** returns this + a */
    final public GeoVec2D add(GeoVec2D a) {
        GeoVec2D res = new GeoVec2D(kernel, 0,0);
        add(this, a, res);
        return res;
    }                
    
    /** c = a + b */
    final public static void add(GeoVec2D a, GeoVec2D b, GeoVec2D c) {                                       
        c.x = a.x + b.x;
        c.y = a.y + b.y;
    }
    
     /** returns this - a */
    final public GeoVec2D sub(GeoVec2D a) {
        GeoVec2D res = new GeoVec2D(kernel, 0,0);
        sub(this, a, res);
        return res;
    }
    
    /** c = a - b */
    final public static void sub(GeoVec2D a, GeoVec2D b, GeoVec2D c) {
        c.x = a.x - b.x;
        c.y = a.y - b.y;
    }       
        
    final public void mult(double b) {
        x = b*x;
        y = b*y;
    }
    
    /** c = a * b */
    final public static void mult(GeoVec2D a, double b, GeoVec2D c) {
        c.x = a.x * b;
        c.y = a.y * b;        
    }    
   
    /** c = a / b Michael Borcherds 2007-12-09 */
    final public static void complexDivide(GeoVec2D a, GeoVec2D b, GeoVec2D c) {                                       
    	// NB temporary variables *crucial*: a and c can be the same variable
    	double x1=a.x,y1=a.y,x2=b.x,y2=b.y;
    	// complex division
      c.x = (x1 * x2 + y1 * y2)/(x2 * x2 + y2 * b.y);
      c.y = (y1 * x2 - x1 * y2)/(x2 * x2 + y2 * b.y);
    	// actually do multiply!?
//      c.x = (x1 * x2 - y1 * y2);
//      c.y = (y2 * x1 + x2 * y1);

    }
    
    /** c = a / b Michael Borcherds 2007-12-09 */
    final public static void complexMultiply(GeoVec2D a, GeoVec2D b, GeoVec2D c) {                                       
    	// NB temporary variables *crucial*: a and c can be the same variable
    	double x1=a.x,y1=a.y,x2=b.x,y2=b.y;
    	//  do multiply
      c.x = (x1 * x2 - y1 * y2);
      c.y = (y2 * x1 + x2 * y1);
    }

    final public static void inner(GeoVec2D a, GeoVec2D b, double c) {
        c = a.x * b.x + a.y * b.y;        
    }       
    
    final public static void inner(GeoVec2D a, GeoVec2D b, MyDouble c) {
        c.set(a.x * b.x + a.y * b.y);        
    }           
    
    /** c = a / b */
    final public static void div(GeoVec2D a, double b, GeoVec2D c) {
        c.x = a.x / b;
        c.y = a.y / b;
    }        
    
    final public boolean isDefined() {		
		return !(Double.isNaN(x) || Double.isNaN( y));
	}
    
    final public String toString() {          
		sbToString.setLength(0);
		sbToString.append('(');
		sbToString.append(kernel.format(x));
		sbToString.append(", ");
		sbToString.append(kernel.format(y));
		sbToString.append(')');         
        return sbToString.toString();
    }         
	private StringBuffer sbToString = new StringBuffer(50);
    
    /**
     * interface VectorValue implementation
     */           
    final public GeoVec2D getVector() {
        return this;
    }        
        
    final public boolean isConstant() {
        return true;
    }
    
    final public boolean isLeaf() {
        return true;
    }             
        
    final public int getMode() {
        return  mode;
    }        
    
    final public ExpressionValue evaluate() { return this; }
    
    final public HashSet getVariables() { return null; }
    
    final public void setMode(int mode) {
        this.mode = mode;
    }

	final public String toValueString() {
		return toString();
	}  
	
	public String toLaTeXString(boolean symbolic) {
		return toString();
	}    
    
    
    // abstract methods of GeoElement
    /*
    final public GeoElement copy() {
        return new GeoVec2D(this);
    }
    
    final public void set(GeoElement geo) {
        GeoVec2D v = (GeoVec2D) geo;
        this.x = v.x;
        this.y = v.y;
    }
    
    final public boolean isDefined() {
        return true;
    }
     */
     
	 final public boolean isNumberValue() {
		 return false;
	 }

	 final public boolean isVectorValue() {
		 return true;
	 }
	 
    final public boolean isBooleanValue() {
        return false;
    }

	 final public boolean isPolynomialInstance() {
		 return false;
	 }   
	 
	 final public boolean isTextValue() {
		 return false;
	 }
	 
	 final public boolean isExpressionNode() {
		 return false;
	 }
	 
	 final public boolean isVariable() {
		 return false;
	 }   
	 
    public boolean isListValue() {
        return false;
    }

	 
	 final public boolean isGeoElement() {
	 	return false;
	 }
	 
	 final public boolean contains(ExpressionValue ev) {
		 return ev == this;
	 }
}
