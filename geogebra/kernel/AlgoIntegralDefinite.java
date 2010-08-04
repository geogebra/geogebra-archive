/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import org.apache.commons.math.analysis.integration.LegendreGaussIntegrator;

import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.roots.RealRootAdapter;
import geogebra.kernel.roots.RealRootFunction;

/**
 * Integral of a function (GeoFunction)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegralDefinite extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private NumberValue a, b; //input
    private GeoElement ageo, bgeo;
    private GeoNumeric n; // output g = integral(f(x), x, a, b)   

    // for symbolic integration
    private GeoFunction symbIntegral;
    
    // for numerical adaptive GaussQuad integration  
    private static final int ORDER = 5;
    private static final int MAX_ITER = 100;
    private static LegendreGaussIntegrator gauss;   

    public AlgoIntegralDefinite(
        Construction cons,
        String label,
        GeoFunction f,
        NumberValue a,
        NumberValue b) {
        this(cons, f, a, b);
        n.setLabel(label);
    }

    AlgoIntegralDefinite(
        Construction cons,
        GeoFunction f,
        NumberValue a,
        NumberValue b) {
        super(cons);
        this.f = f;
        n = new GeoNumeric(cons); // output
        this.a = a;
        this.b = b;
        ageo = a.toGeoElement();
        bgeo = b.toGeoElement();
                
        // create helper algorithm for symbolic integral
        // don't use symbolic integral for conditional functions
        if (!f.isGeoFunctionConditional()) {
	        AlgoCasIntegral algoInt = new AlgoCasIntegral(cons, f, null);
	        symbIntegral = (GeoFunction) algoInt.getResult();
	        cons.removeFromConstructionList(algoInt);     
        }
        
        setInputOutput(); // for AlgoElement        
        compute();
        n.setDrawable(true);
    }

    public String getClassName() {
        return "AlgoIntegralDefinite";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = f;
        input[1] = ageo;
        input[2] = bgeo;

        output = new GeoElement[1];
        output[0] = n;
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getIntegral() {
        return n;
    }

    double getIntegralValue() {
        return n.value;
    }

    public GeoFunction getFunction() {
        return f;
    }

    public NumberValue getA() {
        return a;
    }

    public NumberValue getB() {
        return b;
    }

    protected final void compute() {
        if (!f.isDefined() || !ageo.isDefined() || !bgeo.isDefined()) {
            n.setUndefined();
            return;
        }

        // check for equal bounds
        double lowerLimit = a.getDouble();
        double upperLimit = b.getDouble();
        if (kernel.isEqual(lowerLimit, upperLimit)) {
            n.setValue(0);
            return;
        }
        
        // check if f(a) and f(b) are defined
        double fa = f.evaluate(lowerLimit);
        double fb = f.evaluate(upperLimit);
        if (Double.isNaN(fa) || Double.isInfinite(fa)
        	|| Double.isNaN(fb) || Double.isInfinite(fb)) {
        	n.setUndefined();
        	return;
        }

        /* 
         * Try to use symbolic integral
         *
         * We only do this for functions that do NOT include divisions by their variable.
         * Otherwise there might be problems like:
         * Integral[ 1/x, -2, -1 ] would be undefined (log(-1) - log(-2))
         * Integral[ 1/x^2, -1, 1 ] would be defined (-2)
         */
        if (symbIntegral != null && symbIntegral.isDefined() && !f.includesDivisionByVar()) {        
        	double val = symbIntegral.evaluate(upperLimit) - symbIntegral.evaluate(lowerLimit);
	            n.setValue(val);	 
	            if (n.isDefined()) return;	        
        }                

        // numerical integration
       // max_error = ACCURACY; // current maximum error
        //maxstep = 0;           
        
        double integral = adaptiveGaussQuad(f, lowerLimit, upperLimit);
        n.setValue(integral);
              
        /*
        Application.debug("***\nsteps: " + maxstep);                   
        Application.debug("max_error: " + max_error);
        */
    }
    //  private int maxstep;
    
    /**
     * Computes integral of function fun in interval a, b using an adaptive Gauss 
     * quadrature approach.
     */
    public static double adaptiveGaussQuad(RealRootFunction fun, double a, double b) {
    	// init GaussQuad classes for numerical integration
        if (gauss == null) {
            gauss = new LegendreGaussIntegrator(ORDER, MAX_ITER);
        }
    	
        // integrate using adaptive gauss quadrature
		try {
			return gauss.integrate(new RealRootAdapter(fun), a, b);
		} catch (Exception e) {
			return Double.NaN;
		}
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("IntegralOfAfromBtoC",f.getLabel(),ageo.getLabel(),bgeo.getLabel());
    }

}
