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
	
	String getClassName() {
		return "GeoFunctionConditional";
	}
	
	String getTypeString() {
		return "Function";
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
	
	/*
	public boolean isDefined() {
		return isDefined;
	}*/
			
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
		if (isDefined()) {
			StringBuffer sb = new StringBuffer(80);
			sb.append(app.getCommand("If"));
			sb.append("[");
			sb.append(condFun.toValueString());
			sb.append(", ");
			sb.append(ifFun.toValueString());
			if (elseFun != null) {
				sb.append(", ");
				sb.append(elseFun.toValueString());
			}
			sb.append("]");
			return sb.toString();
		} 
		else
			return app.getPlain("undefined");
	}	
	
	final public String toLaTeXString(boolean symbolic) {
		// TODO: create latex string for conditional function
		if (isDefined()) {
			StringBuffer sb = new StringBuffer(80);
			sb.append(app.getCommand("If"));
			sb.append("[");
			sb.append(condFun.toValueString());
			sb.append(", ");
			sb.append(ifFun.toValueString());
			if (elseFun != null) {
				sb.append(", ");
				sb.append(elseFun.toValueString());
			}
			sb.append("]");
			return sb.toString();
		} 
		else
			return app.getPlain("undefined");
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
		return isDefined() &&
			Math.abs(evaluate(P.inhomX) - P.inhomY) <= eps;
	}

	public boolean isGeoFunctionable() {
		return true;
	}
	
	public boolean isBooleanFunction() {
		return false;
	}		

}
