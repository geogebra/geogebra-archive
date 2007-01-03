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
import geogebra.kernel.arithmetic.Parametric2D;

/**
 * Cartesian parametric curve, e.g. (cos(t), sin(t)) for t from 0 to 2pi.
 * 
 * 
 * @author Markus Hohenwarter
 */
public class GeoCurveCartesian extends GeoElement
implements Path, Translateable, Traceable, Parametric2D {

	private static final long serialVersionUID = 1L;
	
	private Function funX, funY;	
	private double startParam, endParam;
	private boolean isDefined = true;
	private boolean isClosedPath;
	private boolean trace = false;
	
	public GeoCurveCartesian(Construction c, 
			Function fx, Function fy) 
	{
		super(c);
		setFunctionX(fx);
		setFunctionY(fy);				
	}
	
	String getClassName() {
		return "GeoCurveCartesian";
	}
	
	String getTypeString() {
		return "CurveCartesian";
	}
	
    public int getGeoClassType() {
    	return GEO_CLASS_CURVE_CARTESIAN;
    }

	/** copy constructor */
	public GeoCurveCartesian(GeoCurveCartesian f) {
		super(f.cons);
		set(f);
	}

	public GeoElement copy() {
		return new GeoCurveCartesian(this);
	}
	
	/** 
	 * Sets the function of the x coordinate of this curve.
	 */
	final public void setFunctionX(Function funX) {
		this.funX = funX;			
	}
	
	/** 
	 * Sets the function of the x coordinate of this curve.
	 */
	final public void setFunctionY(Function funY) {
		this.funY = funY;			
	}
	
	/** 
	 * Sets the start and end parameter value of this curve.
	 * Note: setFunctionX() and setFunctionY() has to be called 
	 * before this method.
	 */
	public void setInterval(double startParam, double endParam) {
		this.startParam = startParam;
		this.endParam = endParam;
		
		// update isClosedPath, i.e. startPoint == endPoint
		isClosedPath =  
			Kernel.isEqual(funX.evaluate(startParam), funX.evaluate(endParam), Kernel.MIN_PRECISION) &&
			Kernel.isEqual(funY.evaluate(startParam), funY.evaluate(endParam), Kernel.MIN_PRECISION);
	}

	public void set(GeoElement geo) {
		GeoCurveCartesian geoCurve = (GeoCurveCartesian) geo;				
		
		funX = new Function(geoCurve.funX, kernel);
		funY = new Function(geoCurve.funY, kernel);
		startParam = geoCurve.startParam;
		endParam = geoCurve.endParam;
		isDefined = geoCurve.isDefined;
		
		// macro OUTPUT
		if (geo.cons != cons && isAlgoMacroOutput) {			
			// this object is an output object of AlgoMacro
			// we need to check the references to all geos in its function's expression
			AlgoMacro algoMacro = (AlgoMacro) getParentAlgorithm();
			algoMacro.initFunction(funX);
			algoMacro.initFunction(funY);
		}
	}		
	
	/**
	 * Set this curve to the n-th derivative of curve c
	 */
	public void setDerivative(GeoCurveCartesian c, int n) {
		if (c.isDefined()) {
			
			funX = c.funX.getDerivative(n);
			funY = c.funY.getDerivative(n);			
		} else {
			isDefined = false;
		}	
	}		
		
	/**
	 * Calculates the Cartesian coordinates of this curve
	 * for the given parameter paramVal. The result is written
	 * to out.
	 */
	public void evaluate(double paramVal, double [] out) {		
		out[0] = funX.evaluate(paramVal);
		out[1] = funY.evaluate(paramVal);
	}
	
	public ExpressionValue evaluate() {
		return this;
	}
	
	/**
	 * translate function by vector v
	 */
	final public void translate(GeoVector v) {
		funX.translate(v.x, 0);
		funY.translate(0, v.y);
	}
	
	final public boolean isTranslateable() {
		return true;
	}
	
	final public void translate(double vx, double vy) {
		funX.translate(vx, 0);
		funY.translate(0, vy);
	}	

	final public boolean isDefined() {
		return isDefined;
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
			sbToString.append('(');
			sbToString.append(funX.getFunctionVariable().toString());
			sbToString.append(") = ");					
		}		
		sbToString.append(toValueString());
		return sbToString.toString();
	}
	private StringBuffer sbToString = new StringBuffer(80);
	private StringBuffer sbTemp = new StringBuffer(80);
	
	public String toValueString() {		
		if (isDefined) {
			sbTemp.setLength(0);
			sbTemp.append('(');
			sbTemp.append(funX.toValueString());
			sbTemp.append(", ");
			sbTemp.append(funY.toValueString());
			sbTemp.append(')');
			return sbTemp.toString();
		} else
			return app.getPlain("undefined");
	}	
	
	public String toSymbolicString() {	
		if (isDefined) {
			sbTemp.setLength(0);
			sbTemp.append('(');
			sbTemp.append(funX.toString());
			sbTemp.append(", ");
			sbTemp.append(funY.toString());
			sbTemp.append(')');
			return sbTemp.toString();
		} else
			return app.getPlain("undefined");
	}
	
	public String toLaTeXString(boolean symbolic) {
		if (isDefined) {
			sbTemp.setLength(0);
			sbTemp.append("\\left(\\begin{array}{c}");
			sbTemp.append(funX.toLaTeXString(symbolic));
			sbTemp.append("\\\\");
			sbTemp.append(funY.toLaTeXString(symbolic));
			sbTemp.append("\\end{array}\\right)");
			return sbTemp.toString();
		} else
			return app.getPlain("undefined");		
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
		// TODO: implement pointChanged() for path interface
		// note: find minimum of (funX(t) - P.inhomX)^2 + (funY(t) - P.inhomY)^2
		// using ExtremumFinder.findMinimum
	}
	
	public boolean isOnPath(GeoPoint P, double eps) {
		return false;
		
		// TODO: implement isOnPath() for path interface
		//return isDefined &&
		//	Math.abs(fun.evaluate(P.inhomX) - P.inhomY) <= eps;
	}

	public void pathChanged(GeoPoint P) {
		// calc point for given parameter
		P.x = funX.evaluate(P.pathParameter.t);
		P.y = funY.evaluate(P.pathParameter.t);
		P.z = 1.0;		
	}
	
	public boolean isPath() {
		return true;
	}

	
	/**
	 * Returns the start parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * @return
	 */
	public double getMinParameter() {
		return startParam;
	}
	
	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * @return
	 */
	public double getMaxParameter() {
		return endParam;
	}
	
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}
	
	public boolean isClosedPath() {	
		return isClosedPath;
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
	
	public boolean isGeoCurveCartesian() {
		return true;
	}

	public GeoVec2D evaluate(double t) {		
		return new GeoVec2D(kernel, funX.evaluate(t), funY.evaluate(t));
	}
	
	final public boolean isTraceable() {
		return true;
	}

	final public boolean getTrace() {		
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;	
	}   
}
