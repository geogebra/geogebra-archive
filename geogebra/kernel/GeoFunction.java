/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.Functional;

/**
 * Explicit function in one variable ("x"). This is actually a wrapper class for Function
 * in geogebra.kernel.arithmetic. In arithmetic trees (ExpressionNode) it evaluates
 * to a Function.
 * 
 * @author Markus Hohenwarter
 */
public class GeoFunction extends GeoElement
implements Path, Translateable, Traceable, Functional, GeoFunctionable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean trace;

	private Function fun;	

	private boolean isDefined = true;

	public GeoFunction(Construction c) {
		super(c);
	}

	public GeoFunction(Construction c, String label, Function f) {
		super(c);
		fun = f;
		setLabel(label);
	}
	
	String getClassName() {
		return "GeoFunction";
	}
	
	String getTypeString() {
		return "Function";
	}

	/** copy constructor */
	public GeoFunction(GeoFunction f) {
		super(f.cons);
		set(f);
	}

	public GeoElement copy() {
		return new GeoFunction(this);
	}

	public void set(GeoElement geo) {
		fun = new Function(((GeoFunction)geo).fun);
	}

	public void setFunction(Function f) {
		fun = f;
	}
			
	final public Function getFunction() {
		return fun;
	}	
	
	/**
	 * Set this function to the n-th derivative of f
	 * @param f
	 * @param order
	 */
	public void setDerivative(GeoFunction f, int n) {
		if (f.isDefined()) {
			fun = f.fun.getDerivative(n);
		} else {
			isDefined = false;
		}	
	}
	
	/**
	 * Set this function to the integral of f
	 * @param f
	 * @param order
	 */
	public void setIntegral(GeoFunction f) {
		if (f.isDefined()) {
			fun = f.fun.getIntegral();	
		} else {
			isDefined = false;
		}	
	}
		
	/**
	 * Returns this function's value at position x.
	 * @param x
	 * @return f(x)
	 */
	public double evaluate(double x) {		
		return fun.evaluate(x);
	}
	
	/**
	 * Returns this boolean function's value at position x.
	 * @param x
	 * @return f(x)
	 */
	final public boolean evaluateBoolean(double x) {		
		return fun.evaluateBoolean(x);
	}
	
	final public Function getDerivative(int order){
		if (fun == null) return null;
		else return fun.getDerivative(order);
	}
	
	public ExpressionValue evaluate() {
		return this;
	}
	
	/**
	 * translate function by vector v
	 */
	final public void translate(GeoVector v) {
		fun.translate(v.x, v.y);
	}
	
	final public boolean isTranslateable() {
		return true;
	}
	
	final public void translate(double vx, double vy) {
		fun.translate(vx, vy);
	}

	public void setMode(int mode) {
		// dummy
	}

	public int getMode() {
		// dummy
		return -1;
	}
	
	/**
	 * Returns true if this function is a polynomial.
	 * 
	 * @param forRootFinding: set to true if you want to allow
	 * functions that can be factored into polynomial factors
	 * for root finding (e.g. sqrt(x) could be replaced by x)
	 */
	public boolean isPolynomialFunction(boolean forRootFinding) {		
		// don't do root finding simplification here
		// i.e. don't replace a factor "sqrt(x)" by "x"
		if (!isDefined()) 
			return false;
		else
			return fun.isConstantFunction() || 
						fun.getPolynomialFactors(forRootFinding) != null;
	}

	public boolean isDefined() {
		return isDefined && fun != null;
	}

	public void setDefined(boolean defined) {
		isDefined = defined;
	}

	public void setUndefined() {
		isDefined = false;
	}

	boolean showInAlgebraView() {
		return true;
	}

	boolean showInEuclidianView() {
		return isDefined();
	}
	
	
	public String toString() {
		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			sbToString.append("(x) = ");
		}		
		sbToString.append(toValueString());
		return sbToString.toString();
	}
	private StringBuffer sbToString = new StringBuffer(80);
	
	public String toValueString() {		
		if (fun != null)
			return fun.toValueString();
		else
			return app.getPlain("undefined");
	}	
	
	public String toLaTeXString(boolean symbolic) {
		if (isDefined())
			return fun.toLaTeXString(symbolic);
		else
			return app.getPlain("undefined");
	}
	
	/*
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(label);
		sb.append("(x) = ");
		if (fun != null)
			sb.append(fun.toValueString());
		else
			sb.append(app.getPlain("undefined"));		
		return sb.toString();
	}
	
	// function names should not be expanded 
	public final String toValueString() {
		if (label == null) { 
			// this is a special case that will only occur
			// for functions without label that are directly
			// used as command arguments
			return fun.toString();
		}
		return label;
	}*/
	
	/**
	   * save object in xml format
	   */ 
	  public final String getXML() {
		 StringBuffer sb = new StringBuffer();
		 
		 // an indpendent function needs to add
		 // its expression itself
		 // e.g. f(x) = x� - 3x
		 if (isIndependent()) {
			sb.append("<expression");
				sb.append(" label =\"");
				sb.append(label);
				sb.append("\" exp=\"");
				sb.append(toString());
				// expression   
			sb.append("\"/>\n");
		 }
	  		  
		  sb.append("<element"); 
			  sb.append(" type=\"function\"");
			  sb.append(" label=\"");
			  sb.append(label);
		  sb.append("\">\n");
		  sb.append(getXMLtags());
		  sb.append("</element>\n");
		  
		  return sb.toString();
	  }
	
	/**
	* returns all class-specific xml tags for getXML
	*/
   String getXMLtags() {
	   StringBuffer sb = new StringBuffer();
	   sb.append(super.getXMLtags());
	 
	   //	line thickness and type  
	   sb.append(getLineStyleXML());	  

	   return sb.toString();   
   }

	/* 
	 * Path interface
	 */	 
	public void pointChanged(GeoPoint P) {				
		if (P.z == 1.0) {
			P.x = P.x;			
		} else {
			P.x = P.x / P.z;			
		}
				
		if (fun.interval) {
			//	don't let P move out of interval
			if (P.x < fun.a) 
				P.x = fun.a;
			else if (P.x > fun.b) 
				P.x = fun.b;
		}
		
		P.y = fun.evaluate(P.x);
		P.z = 1.0;
		
		// set path parameter for compatibility with
		// PathMoverGeneric
		P.pathParameter.t = P.x;
	}
	
	public boolean isOnPath(GeoPoint P, double eps) {
		return isDefined &&
			Math.abs(fun.evaluate(P.inhomX) - P.inhomY) <= eps;
	}

	public void pathChanged(GeoPoint P) {
		P.x = P.pathParameter.t;
		pointChanged(P);
	}
	
	public boolean isPath() {
		return true;
	}

	
	/**
	 * Returns the smallest possible parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * @return
	 */
	public double getMinParameter() {
		return kernel.getXmin();
	}
	
	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * @return
	 */
	public double getMaxParameter() {
		return kernel.getXmax();
	}
	
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}
	
	public boolean isClosedPath() {
		return false;
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

	public boolean isTextValue() {
		return false;
	}
	
	public boolean isTraceable() {
		return true;
	}

	public boolean getTrace() {		
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;	
	}   

	
	
	public GeoFunction getGeoFunction() {
		return this;
	}
	
	public boolean isGeoFunction() {
		if (fun != null)
			return !fun.isBooleanFunction();
		else
			return true;
	}
	
	public boolean isGeoFunctionable() {
		return isGeoFunction();
	}
	
	public boolean isBooleanFunction() {
		if (fun != null)
			return fun.isBooleanFunction();
		else
			return false;
	}
}
