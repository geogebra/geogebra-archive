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

import geogebra.kernel.arithmetic.Function;


/**
 * Explicit function in one variable ("x") in the 
 * form of an If-Then-Else statement
 * 
 * example:  If[ x < 2, x^2, x + 2 ]
 * where "x < 2" is a boolean function
 * 
 * @author Markus Hohenwarter
 */
public class GeoFunctionConditional extends GeoFunction {
	
	private boolean isDefined = true;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoFunction condFun, ifFun, elseFun;	

	/**
	 * Creates a new GeoFunctionConditional object.
	 * 
	 * @param c
	 * @param condFun: a GeoFunction that evaluates
	 * 	to a boolean value (i.e. isBooleanFunction() returns true)
	 * @param ifFun
	 * @param elseFun: may be null
	 */
	public GeoFunctionConditional(Construction c, 
			GeoFunction condFun, GeoFunction ifFun, GeoFunction elseFun) {
		super(c);
		this.condFun 	= condFun;
		this.ifFun 		= ifFun;
		this.elseFun 	= elseFun;	
	}	
	
	public GeoFunctionConditional(GeoFunctionConditional geo) {
		super(geo.cons);		
		set(geo);					
	}	
	
	public GeoElement copy() {
		return new GeoFunctionConditional(this);
	}		
	
	public void set(GeoElement geo) {				
		GeoFunctionConditional geoFunCond = (GeoFunctionConditional) geo;
		isDefined = geoFunCond.isDefined;
			
		if (condFun == null) 
			condFun = new GeoFunction(geoFunCond.condFun);
		else 
			condFun.set(geoFunCond.condFun);
		
		if (ifFun == null)
			ifFun = new GeoFunction(geoFunCond.ifFun);
		else
			ifFun.set(geoFunCond.ifFun);
		
		if (geoFunCond.elseFun == null) {
			elseFun = null;
		} else {
			if (elseFun == null)
				elseFun = new GeoFunction(geoFunCond.elseFun);
			else
				elseFun.set(geoFunCond.elseFun);
		}
	}		
	
	String getClassName() {
		return "GeoFunctionConditional";
	}
	
	String getTypeString() {
		return "Function";
	}		
	
    public int getGeoClassType() {
    	return GEO_CLASS_FUNCTIONCONDITIONAL;
    }
    
    public boolean isDefined() {
		return isDefined;
	}      
      
    /**
	 * Set this function to the n-th derivative of f
	 * @param f
	 * @param order
	 */
	public void setDerivative(GeoDeriveable f, int n) {		
		GeoFunctionConditional fcond = (GeoFunctionConditional) f;
		ifFun.setDerivative(fcond.ifFun, n);
		if (elseFun != null)
			elseFun.setDerivative(fcond.elseFun, n);					
	}
	
	/**
	 * Set this function to the integral of f
	 * @param f
	 * @param order
	 */
	public void setIntegral(GeoFunction f) {
		GeoFunctionConditional fcond = (GeoFunctionConditional) f;		
		ifFun.setIntegral(fcond.ifFun);
		if (elseFun != null)
			elseFun.setIntegral(fcond.elseFun);		
	}
				
	/**
	 * Returns this function's value at position x.
	 *
	 * @param x 
	 * @return f(x) = condition(x) ? ifFun(x) : elseFun(x)
	 */
	final public double evaluate(double x) {		
		if (condFun.evaluateBoolean(x))
			return ifFun.evaluate(x);
		else {
			if (elseFun == null)
				return Double.NaN;
			else
				return elseFun.evaluate(x);
		}		
	}		
	
	/**
	 * Returns the corresponding Function for the given x-value.
	 * This is important for conditional functions where we have
	 * two differen Function objects.
	 */
	public Function getFunction(double x) {
		if (elseFun == null) { 
			return ifFun.getFunction(x);
		} else {
			if (condFun.evaluateBoolean(x))
				return ifFun.getFunction(x);
			else 
				return elseFun.getFunction(x);
		}
	}		
		
	public GeoFunction getGeoDerivative(int order){	
		if (derivGeoFun == null) {
			derivGeoFun = new GeoFunctionConditional(this);
		}
		
		derivGeoFun.setDerivative(this, order);
		return derivGeoFun;				
	}
	private GeoFunctionConditional derivGeoFun;
			
	public boolean isPolynomialFunction(boolean forRootFinding) {		
		return false;   			
	}
	
	public final String toString() {
		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			sbToString.append("(x) = ");
		}		
		sbToString.append(toValueString());
		return sbToString.toString();
	}
	private StringBuffer sbToString = new StringBuffer(80);
	
	final public String toValueString() {					
		return toString(false);
	}	
	
	final public String toSymbolicString() {					
		return toString(true);
	}	
	
	private String toString(boolean symbolic) {					
		if (isDefined()) {
			StringBuffer sb = new StringBuffer(80);
			sb.append(app.getCommand("If"));
			sb.append("[");
			
			if (symbolic) 
				sb.append(condFun.toSymbolicString());
			else
				sb.append(condFun.toValueString());
			
			sb.append(", ");
			
			if (symbolic)
				sb.append(ifFun.toSymbolicString());
			else
				sb.append(ifFun.toValueString());
			
			if (elseFun != null) {
				sb.append(", ");
				if (symbolic)
					sb.append(elseFun.toSymbolicString());
				else
					sb.append(elseFun.toValueString());
			}
			sb.append("]");
			return sb.toString();
		} 
		else
			return app.getPlain("undefined");
	}	
	
	final public String toLaTeXString(boolean symbolic) {	
		return toString(symbolic);
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
		
		P.y = evaluate(P.x);
		P.z = 1.0;
		
		// set path parameter for compatibility with
		// PathMoverGeneric
		P.pathParameter.t = P.x;
	}
	
	public boolean isOnPath(GeoPoint P, double eps) {
		if (P.getPath() == this)
			return true;
		
		return isDefined() &&
			Math.abs(evaluate(P.inhomX) - P.inhomY) <= eps;
	}

	public boolean isGeoFunction() {
		return true;
	}		
	
	public boolean isGeoFunctionConditional() {		
		return true;
	}
	
	public boolean isBooleanFunction() {
		return false;
	}



}
