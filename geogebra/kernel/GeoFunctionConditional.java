/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
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
			
		if (condFun == null) { 
			condFun = (GeoFunction) geoFunCond.condFun.copyInternal(cons);			
		}	
		
		if (isAlgoMacroOutput()) {
			condFun.setAlgoMacroOutput(true);
			condFun.setParentAlgorithm(getParentAlgorithm());
			condFun.setConstruction(cons);
		}		
		condFun.set(geoFunCond.condFun);
		
		if (ifFun == null) {
			ifFun = (GeoFunction) geoFunCond.ifFun.copyInternal(cons);				
		}
		if (isAlgoMacroOutput()) {
			ifFun.setAlgoMacroOutput(true);
			ifFun.setParentAlgorithm(getParentAlgorithm());
			ifFun.setConstruction(cons);
		}
		ifFun.set(geoFunCond.ifFun);
		
		if (geoFunCond.elseFun == null) {
			elseFun = null;
		} else {
			if (elseFun == null) {
				elseFun = (GeoFunction) geoFunCond.elseFun.copyInternal(cons);					
			}			
			if (isAlgoMacroOutput()) {
				elseFun.setAlgoMacroOutput(true);
				elseFun.setParentAlgorithm(getParentAlgorithm());	
				elseFun.setConstruction(cons);
			}
			elseFun.set(geoFunCond.elseFun);
		}					
	}	
	
	
	protected String getClassName() {
		return "GeoFunctionConditional";
	}
	
	protected String getTypeString() {
		return "Function";
	}		
	
    public int getGeoClassType() {
    	return GEO_CLASS_FUNCTIONCONDITIONAL;
    }
    
    public boolean isDefined() {
		return isDefined;
	}    
      
    final public GeoFunction getIfFunction() {
    	return ifFun;
    }
    
    final public GeoFunction getElseFunction() {
    	return elseFun;
    }
    
	 /**
     * Replaces geo and all its dependent geos in this function's
     * expressions by copies of their values.
     */
    public void replaceChildrenByValues(GeoElement geo) {     	
    	if (condFun != null) {
    		condFun.replaceChildrenByValues(geo);
    	}
    	if (ifFun != null) {
    		ifFun.replaceChildrenByValues(geo);
    	}
    	if (elseFun != null) {
    		elseFun.replaceChildrenByValues(geo);
    	}
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
        if (interval) {
            // check if x is in interval [a, b]
            if (x < intervalMin || x > intervalMax) 
            	return Double.NaN;           
        }
		
		if (condFun.evaluateBoolean(x))
			return ifFun.evaluate(x);
		else {
			if (elseFun == null)
				return Double.NaN;
			else
				return elseFun.evaluate(x);
		}		
	}	
	
	public void translate(double vx, double vy) {	
		// translate condition by vx, thus
		// changing every x into (x - vx)
		condFun.translate(vx, 0);
		
		// translate if and else parts too
		ifFun.translate(vx, vy);	
		if (elseFun != null)
			elseFun.translate(vx, vy);			
	}
	
	/**
	 * Returns the corresponding Function for the given x-value.
	 * This is important for conditional functions where we have
	 * two different Function objects.
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
			
	public boolean isPolynomialFunction(boolean forRootFinding, boolean symbolic) {		
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
	private StringBuilder sbToString = new StringBuilder(80);
	
	final public String toValueString() {					
		return toString(false);
	}	
	
	final public String toSymbolicString() {					
		return toString(true);
	}	
	
	private String toString(boolean symbolic) {			
		
		switch (kernel.getCASPrintForm()) {
		case ExpressionNode.STRING_TYPE_MATH_PIPER:
			
			if (isDefined()) {
				StringBuilder sb = new StringBuilder(80);
				sb.append("if(");
				
				if (symbolic) 
					sb.append(condFun.toSymbolicString());
				else
					sb.append(condFun.toValueString());
				
				sb.append(")(");
				
				if (symbolic)
					sb.append(ifFun.toSymbolicString());
				else
					sb.append(ifFun.toValueString());
				
				if (elseFun != null) {
					sb.append(") else (");
					if (symbolic)
						sb.append(elseFun.toSymbolicString());
					else
						sb.append(elseFun.toValueString());
				}
				sb.append(')');
				return sb.toString();
			} 
			else
				return app.getPlain("Undefined");
			
			default:
		
				
		if (isDefined()) {
			StringBuilder sb = new StringBuilder(80);
			sb.append(app.getCommand("If"));
			sb.append('[');
			
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
			sb.append(']');
			return sb.toString();
		} 
		else
			return app.getPlain("undefined");
		}
	}	
	
	final public String toLaTeXString(boolean symbolic) {	
		return toString(symbolic);
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

	final public boolean isEqual(GeoElement geo) {

		if (geo.getGeoClassType() != GeoElement.GEO_CLASS_FUNCTIONCONDITIONAL)
			return false;
		
		GeoFunctionConditional geoFun = (GeoFunctionConditional)geo;
		
		// TODO better CAS checking for condFun
		
		return 		condFun.toValueString().equals(geoFun.condFun.toValueString())
					&& ifFun.isEqual(geoFun.ifFun)
					&& ( elseFun != null && elseFun.isEqual(geoFun.elseFun));		

		
	}
	
	final public boolean evaluateCondition(double x) {
		System.err.println("GeoFunctionConditional");
		return condFun.evaluateBoolean(x);
	}
	
	public void getVerticalAsymptotes(GeoFunction f, StringBuilder verticalSB, boolean reverse) {
		ifFun.getVerticalAsymptotes((GeoFunction)this, verticalSB, false);
		if (elseFun != null) elseFun.getVerticalAsymptotes((GeoFunction)this, verticalSB, true);
	}

	public void getDiagonalAsymptotes(GeoFunction f, StringBuilder verticalSB, boolean reverse) {
		ifFun.getVerticalAsymptotes((GeoFunction)this, verticalSB, false);
		if (elseFun != null) elseFun.getVerticalAsymptotes((GeoFunction)this, verticalSB, true);
	}

	public void getHorizontalPositiveAsymptote(GeoFunction f, StringBuilder verticalSB) {
		if (evaluateCondition(Double.POSITIVE_INFINITY))
		ifFun.getHorizontalPositiveAsymptote((GeoFunction)this, verticalSB);
		else if (elseFun != null) elseFun.getHorizontalPositiveAsymptote((GeoFunction)this, verticalSB);

	}

	public void getHorizontalNegativeAsymptote(GeoFunction f, StringBuilder verticalSB) {
		if (evaluateCondition(Double.NEGATIVE_INFINITY))
		ifFun.getHorizontalNegativeAsymptote((GeoFunction)this, verticalSB);
		else if (elseFun != null) elseFun.getHorizontalNegativeAsymptote((GeoFunction)this, verticalSB);

	}


}
