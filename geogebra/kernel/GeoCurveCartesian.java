/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.optimization.ExtremumFinder;
import geogebra.kernel.roots.RealRootFunction;

/**
 * Cartesian parametric curve, e.g. (cos(t), sin(t)) for t from 0 to 2pi.
 * 
 * @author Markus Hohenwarter
 */
public class GeoCurveCartesian extends GeoElement
implements Path, Translateable, Traceable, GeoDeriveable, ParametricCurve {

	private static final long serialVersionUID = 1L;
	
	// samples to find interval with closest parameter position to given point
	private static final int CLOSEST_PARAMETER_SAMPLES = 100;
	
	private Function funX, funY;	
	private double startParam, endParam;
	private boolean isDefined = true;
	private boolean isClosedPath;
	private boolean trace = false;	
//	Victor Franco Espino 25-04-2007
	/*
	 * Parameter in dialog box for adjust color of curvature
	 */
	double CURVATURE_COLOR = 15;//optimal value 
    //Victor Franco Espino 25-04-2007


	private ParametricCurveDistanceFunction distFun;
	
	public GeoCurveCartesian(Construction c, 
			Function fx, Function fy) 
	{
		super(c);
		setFunctionX(fx);
		setFunctionY(fy);					
	}
	
	protected String getClassName() {
		return "GeoCurveCartesian";
	}
	
	protected String getTypeString() {
		return "CurveCartesian";
	}
	
	public String translatedTypeString() {
		// TODO: add translation for "Curve" in plain.properties and remove this method
		return app.getCommand("CurveCartesian");
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
     * Replaces geo and all its dependent geos in this function's
     * expression by copies of their values.
     */
    public void replaceChildrenByValues(GeoElement geo) {     	
    	if (funX != null) {
    		funX.replaceChildrenByValues(geo);
    	}
    	if (funY != null) {
    		funY.replaceChildrenByValues(geo);
    	}
    }
	
	/** 
	 * Sets the start and end parameter value of this curve.
	 * Note: setFunctionX() and setFunctionY() has to be called 
	 * before this method.
	 */
	public void setInterval(double startParam, double endParam) {
		this.startParam = startParam;
		this.endParam = endParam;
		
		isDefined = startParam <= endParam;			
		
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
		if (geo.cons != cons && isAlgoMacroOutput()) {	
			if (!geo.isIndependent()) {
				// this object is an output object of AlgoMacro
				// we need to check the references to all geos in its function's expression
				AlgoMacro algoMacro = (AlgoMacro) getParentAlgorithm();
				algoMacro.initFunction(funX);
				algoMacro.initFunction(funY);
			}
		}
	}		
	
	/**
	 * Set this curve to the n-th derivative of curve c
	 */
	public void setDerivative(GeoDeriveable cd, int n) {
		GeoCurveCartesian c = (GeoCurveCartesian) cd;
		
		if (c.isDefined()) {			
			funX = c.funX.getDerivative(n);
			funY = c.funY.getDerivative(n);	
			isDefined = !(funX == null || funY == null);
			if (isDefined)
				setInterval(c.startParam, c.endParam);			
		} else {
			isDefined = false;
		}	
	}		
		
	
	
	final public RealRootFunction getRealRootFunctionX() {
		return funX;
	}
	
	final public RealRootFunction getRealRootFunctionY() {
		return funY;
	}
	
	public ExpressionValue evaluate() {
		return this;
	}
	
	/**
	 * translate function by vector v
	 */
	final public void translate(GeoVector v) {
		funX.translateY(v.x);
		funY.translateY(v.y);
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

	protected boolean showInAlgebraView() {
		return true;
	}

	protected boolean showInEuclidianView() {
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
   protected String getXMLtags() {
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
		// get closest parameter position on curve
		double t = getClosestParameter(P, P.pathParameter.t);
		P.pathParameter.t = t;
		pathChanged(P);	
	}
	
	public boolean isOnPath(GeoPoint P, double eps) {						
		if (P.getPath() == this)
			return true;
			
		// get closest parameter position on curve
		double t = getClosestParameter(P, P.pathParameter.t);
		boolean onPath =
			Math.abs(funX.evaluate(t) - P.inhomX) <= eps &&
			Math.abs(funY.evaluate(t) - P.inhomY) <= eps;				
		return onPath;
	}

	public void pathChanged(GeoPoint P) {
		if (P.pathParameter.t < startParam)
			P.pathParameter.t = startParam;
		else if (P.pathParameter.t > endParam)
			P.pathParameter.t = endParam;
		
		// calc point for given parameter
		P.x = funX.evaluate(P.pathParameter.t);
		P.y = funY.evaluate(P.pathParameter.t);
		P.z = 1.0;		
	}
	
	public boolean isPath() {
		return true;
	}
	
	/**
	 * Returns the parameter value t where this curve has minimal distance
	 * to point P.
	 * @param startValue: an intervall around startValue is specially investigated				
	 */
	public double getClosestParameter(GeoPoint P, double startValue) {		
		if (distFun == null)
			distFun = new ParametricCurveDistanceFunction(this);				
		distFun.setDistantPoint(P.x/P.z, P.y/P.z);	
		
		// check if P is on this curve and has the right path parameter already
    	if (P.getPath() == this) { 
    		// point A is on curve c, take its parameter
    		double pathParam = P.pathParameter.t;
    		if (distFun.evaluate(pathParam) < Kernel.MIN_PRECISION * Kernel.MIN_PRECISION)
    			return pathParam;   
    			
			// if we don't have a startValue yet, let's take the path parameter as a guess
    		if (Double.isNaN(startValue))
    			startValue = pathParam;
    	} 									
		
		// first sample distFun to find a start intervall for ExtremumFinder		
		double step = (endParam - startParam) / CLOSEST_PARAMETER_SAMPLES;
		double minVal = distFun.evaluate(startParam);
		double minParam = startParam;
		double t = startParam;		
		for (int i=0; i < CLOSEST_PARAMETER_SAMPLES; i++) {
			t = t + step;
			double ft = distFun.evaluate(t);
			if (ft < minVal) {
				// found new minimum
				minVal = ft;
				minParam = t;
			}
		}
		
		// use interval around our minParam found by sampling
		// to find minimum
		double left = Math.max(startParam, minParam - step);
		double right = Math.min(endParam, minParam + step);	
		ExtremumFinder extFinder = kernel.getExtremumFinder();
		double sampleResult = extFinder.findMinimum(left, right,
									distFun, Kernel.MIN_PRECISION);	
		
		// if we have a valid startParam we try the intervall around it too
		// however, we don't check the same intervall again
		if (!Double.isNaN(startValue) &&
			(startValue < left || right < startValue)) {
			left = Math.max(startParam, startValue - step);
			right = Math.min(endParam, startValue + step);				
			double startValResult = extFinder.findMinimum(left, right,
										distFun, Kernel.MIN_PRECISION);
			if (distFun.evaluate(startValResult) <
					distFun.evaluate(sampleResult)	) {				
				return startValResult;
			}				
		}

		return sampleResult;
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
	
	final public boolean isTraceable() {
		return true;
	}

	final public boolean getTrace() {		
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;	
	}   	
	
	/**
	 * Calculates the Cartesian coordinates of this curve
	 * for the given parameter paramVal. The result is written
	 * to out.
	 */
	public void evaluateCurve(double paramVal, double [] out) {		
		out[0] = funX.evaluate(paramVal);
		out[1] = funY.evaluate(paramVal);
	}
	
	public GeoVec2D evaluateCurve(double t) {		
		return new GeoVec2D(kernel, funX.evaluate(t), funY.evaluate(t));
	}  
	
	/**
	 * Calculates curvature for curve: 
	 * k(t) = (a'(t)b''(t)-a''(t)b'(t))/T^3, T = sqrt(a'(t)^2+b'(t)^2)
	 * @author Victor Franco, Markus Hohenwarter
	 */
	public double evaluateCurvature(double t){		
		Function f1X, f1Y, f2X, f2Y;		
		f1X = funX.getDerivative(1);
		f1Y = funY.getDerivative(1);
		f2X = funX.getDerivative(2);
		f2Y = funY.getDerivative(2);
		
		if (f1X == null || f1Y == null || f2X == null || f2Y == null)
			return Double.NaN;
		
	  	double f1eval[] = new double[2];
    	double f2eval[] = new double[2];    	   
    	f1eval[0] = f1X.evaluate(t);
    	f1eval[1] = f1Y.evaluate(t);
    	f2eval[0] = f2X.evaluate(t);
    	f2eval[1] = f2Y.evaluate(t);
        double t1 = Math.sqrt(f1eval[0]*f1eval[0] + f1eval[1]*f1eval[1]);
        double t3 = t1 * t1 * t1;
        return (f1eval[0]*f2eval[1] - f2eval[0]*f1eval[1]) / t3;
	}
	

	
	final public boolean isGeoCurveable() {
		return true;
	}
	
	public boolean isGeoDeriveable() {
		return true;
	}

	public String getVarString() {	
		return funX.getFunctionVariable().toString();
	}
}
