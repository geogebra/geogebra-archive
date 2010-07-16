/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.roots.RealRootAdapter;
import geogebra.kernel.roots.RealRootDerivAdapter;
import geogebra.kernel.roots.RealRootDerivFunction;
import geogebra.kernel.roots.RealRootUtil;

import org.apache.commons.math.analysis.solvers.UnivariateRealSolver;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactory;

/**
 * Finds one real root of a function with newtons method.
 * The first derivative of this function must exist. 
 */
public class AlgoRootNewton extends AlgoIntersectAbstract {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input, g for intersection of functions       
    private NumberValue start; // start value for root of f 
    private GeoPoint rootPoint; // output 

    private GeoElement startGeo;
    private UnivariateRealSolver rootFinderBrent, rootFinderNewton; 

    public AlgoRootNewton(
        Construction cons,
        String label,
        GeoFunction f,
        NumberValue start) {
        super(cons);
        this.f = f;
        this.start = start;
        startGeo = start.toGeoElement();

        // output
        rootPoint = new GeoPoint(cons);
        setInputOutput(); // for AlgoElement    
        compute();

        rootPoint.setLabel(label);
    }

    AlgoRootNewton(Construction cons) {
        super(cons);
    }

    public String getClassName() {
        return "AlgoRootNewton";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = f;
        input[1] = startGeo;
                
        output = new GeoPoint[1];
        output[0] = rootPoint;
        setDependencies();       
    }

    public GeoPoint getRootPoint() {
        return rootPoint;
    }

    protected void compute() {
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
    	double [] borders = getDomain(fun, start);
    	
    	// for Newton's method we need the derivative of our function fun
        RealRootDerivFunction derivFun = fun.getRealRootDerivFunction();        
        
        // derivative found: let's use Newton's method
        if (derivFun != null) {
        	if (rootFinderNewton == null) {
        		UnivariateRealSolverFactory fact = UnivariateRealSolverFactory.newInstance();
        		rootFinderNewton = fact.newNewtonSolver();
        	}
        	
            try {        	                  	
            	root = rootFinderNewton.solve(new RealRootDerivAdapter(derivFun), borders[0], borders[1], start);                      
            } 
            catch (Exception e) {  
            	e.printStackTrace();
            	root = Double.NaN;
            }
        }
        
        // if Newton didn't know the answer, let's ask Brent
        if (Double.isNaN(root)) {
        	if (rootFinderBrent == null) {
        		UnivariateRealSolverFactory fact = UnivariateRealSolverFactory.newInstance();
        		rootFinderBrent = fact.newBrentSolver();
        	}
        	
        	try {		
        		root = rootFinderBrent.solve(new RealRootAdapter(fun), borders[0], borders[1]);
        	} catch (Exception e) {
        		root = Double.NaN;
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
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("RootOfAWithInitialValueB",f.getLabel(),startGeo.getLabel());

    }
}
