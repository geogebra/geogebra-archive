/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.roots.RealRoot;
import geogebra.kernel.roots.RealRootDerivFunction;
import geogebra.kernel.roots.RealRootUtil;

/**
 * Finds one real root of a function with newtons method.
 * The first derivative of this function must exist. 
 */
public class AlgoRootNewton extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input, g for intersection of functions       
    private NumberValue start; // start value for root of f 
    private GeoPoint rootPoint; // output 

    private GeoElement startGeo;
    private RealRoot rootFinder;

    public AlgoRootNewton(
        Construction cons,
        String label,
        GeoFunction f,
        NumberValue start) {
        super(cons);
        this.f = f;
        this.start = start;
        startGeo = start.toGeoElement();

        rootFinder = new RealRoot();

        // output
        rootPoint = new GeoPoint(cons);
        setInputOutput(); // for AlgoElement    
        compute();

        rootPoint.setLabel(label);
    }

    AlgoRootNewton(Construction cons) {
        super(cons);
        rootFinder = new RealRoot();
    }

    protected String getClassName() {
        return "AlgoRootNewton";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = f;
        input[1] = startGeo;
                
        output = new GeoPoint[1];
        output[0] = rootPoint;
        setDependencies();
        
        kernel.registerEuclidianViewAlgo(this);
    }

    public GeoPoint getRootPoint() {
        return rootPoint;
    }

    void compute() {
        if (!(f.isDefined() && startGeo.isDefined())) {
            rootPoint.setUndefined();
        } else {
        	double startValue = start.getDouble();
        	Function fun = f.getFunction(startValue);        	
        	
            // try to get derivative            
            rootPoint.setCoords(calcRoot(fun, startValue), 0.0, 1.0);                       
        }
    }

    final double calcRoot(Function fun, double start) {
    	double root = Double.NaN;
    	
    	// for Newton's method we need the derivative of our function fun
        RealRootDerivFunction derivFun = fun.getRealRootDerivFunction();        
        
        // no derivative found: let's use REGULA FALSI
        if (derivFun == null) {
        	double [] borders = getDomain(fun, start);
        	root = rootFinder.brent(fun, borders[0], borders[1]);           	        	        
        }
        else {
        	// NEWTON's METHOD
            try {        	                  	
            	root = rootFinder.newtonRaphson(derivFun, start);                      
            } 
            catch (IllegalArgumentException e) {   
            	// BISECTION with NEWTON's METHOD
                try {                               	        	
                	// Newton's method failed because we left our function's domain
                	// Let's search for a valid domain and try again using a restricted method
                	double [] borders = getDomain(fun, start);
                	root = rootFinder.bisectNewtonRaphson(derivFun, borders[0], borders[1]);                                
                } catch (Exception ex) {
                }                      	        	        	
            }
        }
              
        // check what we got
        if (Math.abs(fun.evaluate(root)) < Kernel.MIN_PRECISION )
        	return root; 	
        else
        	return Double.NaN;
    }
    
    /**
     * Tries to find a valid domain for the given function around it's starting value.
     */
    private double [] getDomain(Function fun, double start) {
    	double screenWidth = kernel.getXmax() - kernel.getXmin();
    	return RealRootUtil.getDefinedInterval(fun, start - screenWidth, start + screenWidth);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("Root"));
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
            sb.append(f.getLabel());
            sb.append(' ');
        }
        sb.append(app.getPlain("withStartValue"));
        sb.append(' ');
        sb.append(startGeo.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(f.getLabel());
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
            sb.append(app.getPlain("Root"));
        }
        return sb.toString();
    }
}
