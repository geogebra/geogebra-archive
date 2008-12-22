/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * MyDouble.java
 *
 * Created on 07. Oktober 2001, 12:23
 */

package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.util.MyMath;

import java.util.HashSet;

/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class MyDouble  extends ValidExpression 
implements NumberValue {

    private double val;
    private boolean isAngle = false;    
    
    private Kernel kernel;
    
    public MyDouble(Kernel kernel) {
    	this(kernel, 0.0);
    }
    
    /** Creates new MyDouble */
    public MyDouble(Kernel kernel, double x) {
    	this.kernel = kernel;
        val = x;        
    }
    
    public MyDouble(MyDouble d) {
    	kernel = d.kernel;
        val = d.val;        
        isAngle = d.isAngle;
    }    
    
	public ExpressionValue deepCopy(Kernel kernel) {
		return new MyDouble(this);
	}   
    
    final public void set(double x) { val = x; }
    
    public void resolveVariables() {    	
    }

    
	public String toString() {
		if (Double.isInfinite(val) || Double.isNaN(val))
			return kernel.getApplication().getPlain("undefined");
		else if (isAngle)
			return kernel.formatAngle(val).toString();
		else
			return kernel.format(val); 
	}
    
	final public String toValueString() {
		return toString();
	}
	
	final public String toLaTeXString(boolean symbolic) {
		return toString();
	}
    
    public void setAngle() { isAngle = true; }            
    public boolean isAngle() { return isAngle; }
    

    final public MyDouble random() {
    	val = Math.random();
    	isAngle = false; 
    	return this;
    }
    
    /** c = a + b */
    final public static void add(MyDouble a, MyDouble b, MyDouble c) {
        c.val = a.val + b.val;    
        c.isAngle = a.isAngle && b.isAngle;
    }
    
    /** c = a - b */
    final public static void sub(MyDouble a, MyDouble b, MyDouble c) {
        c.val = a.val - b.val;
        c.isAngle = a.isAngle && b.isAngle;
    }
    
    /** c = a * b */
    final public static void mult(MyDouble a, MyDouble b, MyDouble c) {
        c.val = a.val * b.val;
        c.isAngle = a.isAngle || b.isAngle;
    }
    
    /** c = a / b */
    final public static void div(MyDouble a, MyDouble b, MyDouble c) {
    	if (b.kernel.isZero(b.val)) {
    		c.val = Double.NaN;
    	} else {
			c.val = a.val / b.val;
    	}
        c.isAngle = a.isAngle && !b.isAngle;
    }
    
    /** c = pow(a,b) */
    final public static void pow(MyDouble a, MyDouble b, MyDouble c) {
//    	if (b.val == 0d) {
//    		if (a.val < 0d)
//    			c.val = -1;
//    		else
//    			c.val = 1;    		
//    	} else {
//    		// check for integer value in exponent
//    		//double bint = Math.round(b.val);
//    		//if (b.kernel.isEqual(b.val, bint))
//    		//	c.val = Math.pow(a.val, bint);
//    		//else
//    		c.val = Math.pow(a.val, b.val); 	
//    	}
//    	
    	c.val = Math.pow(a.val, b.val);    		
        c.isAngle = a.isAngle && !b.isAngle;
    }

    final public MyDouble cos() {  val = Math.cos(val); isAngle = false; return this; }
    final public MyDouble sin() {  val = Math.sin(val); isAngle = false; return this; }
  
   final public MyDouble tan() {  		
   		if (kernel.isZero(Math.cos(val))) {
   			val = Double.NaN;
   		} else {
   			val = Math.tan(val);
   		}  		 
  		isAngle = false;  
  		return this; 
  	}
  	
    final public MyDouble acos() {  val = Math.acos(val); isAngle = kernel.arcusFunctionCreatesAngle; return this;  }
    final public MyDouble asin() {  val = Math.asin(val); isAngle = kernel.arcusFunctionCreatesAngle; return this;  }
    final public MyDouble atan() {  val = Math.atan(val); isAngle = kernel.arcusFunctionCreatesAngle; return this;  }
    
    final public MyDouble log() {  val = Math.log(val);  isAngle = false; return this; }
    final public MyDouble log10() {  val = Math.log(val)/MyMath.LOG10;  isAngle = false; return this; }
    final public MyDouble log2() {  val = Math.log(val)/MyMath.LOG2;  isAngle = false; return this; }
    
    final public MyDouble exp() {  val = Math.exp(val);  isAngle = false; return this; }    
    final public MyDouble sqrt() {  val = Math.sqrt(val); isAngle = false;  return this; }    
    final public MyDouble cbrt() {  val = MyMath.cbrt(val); isAngle = false;  return this; }
    final public MyDouble abs() {  val = Math.abs(val);  return this; }    
	final public MyDouble floor() {  val = Math.floor(val);  return this; }
	final public MyDouble ceil() {  val = Math.ceil(val);  return this; }
	final public MyDouble round() { 
		
		// Java quirk/bug Round(NaN) = 0
		if (!(Double.isInfinite(val) || Double.isNaN(val)))		
			val = Math.round(val); 
		
		return this;
	}
	
    final public MyDouble sgn() {  
        val = MyMath.sgn(kernel, val);         
        isAngle = false;
        return this; 
    }    
    
	final public MyDouble cosh() {  
		val = MyMath.cosh(val);
		isAngle = false; 
		return this; 
	}
	
	final public MyDouble sinh() {  
		val = MyMath.sinh(val);
		isAngle = false; 
		return this; 
	}
	
	final public MyDouble tanh() {  
		val = MyMath.tanh(val);
		isAngle = false;  
		return this; 
	}
	
	final public MyDouble acosh() {  
		val = MyMath.acosh(val);
		isAngle = false; 
		return this; 
	}

	final public MyDouble asinh() {  
		val = MyMath.asinh(val);
		isAngle = false; 
		return this; 
	}

	final public MyDouble atanh() {  
		val = MyMath.atanh(val);
		isAngle = false;  
		return this; 
	}
	
	final public MyDouble factorial() {
		val = MyMath.factorial(val);
		isAngle = false;
		return this;
	}
	
	final public MyDouble gamma() {
		val = MyMath.gamma(val, kernel);
		isAngle = false;
		return this;
	}	
  
	final public MyDouble apply(Functional f) {
		val = f.evaluate(val);
		isAngle = false; // want function to return numbers eg f(x) = sin(x), f(45°)
		return this;
	}
    
    /*
     * interface NumberValue
     */    
    final public MyDouble getNumber() {
    	return new MyDouble(this);
    	
    	/* Michael Borcherds 2008-05-20
    	 * removed unstable optimisation
    	 * fails for eg -2 sin(x) - 5 cos(x)
    	if (isInTree()) {
			// used in expression node tree: be careful
    		 return new MyDouble(this);
		} else {
			// not used anywhere: reuse this object
			return this;
		}	      */
    }
    
    
    public boolean isConstant() {
        return true;
    }
    
    final public HashSet getVariables() {
        return null;
    }      
    
    final public boolean isLeaf() {
        return true;
    }
    
    final public ExpressionValue evaluate() {
        return this;
    }
    
    final public double getDouble() {
        return val;
    }
    
	final public GeoElement toGeoElement() {
		GeoNumeric num = new GeoNumeric(kernel.getConstruction());
		num.setValue(val);
		return num;
	}
    
	public boolean isNumberValue() {
		return true;
	}

	public boolean isVectorValue() {
		return false;
	}
	
	public boolean isBooleanValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}
	
	public boolean isTextValue() {
		return false;
	}   
	
	final public boolean isExpressionNode() {
		return false;
	}
	
 
	public boolean isListValue() {
	    return false;
	}	
     

	
	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}    	
}
