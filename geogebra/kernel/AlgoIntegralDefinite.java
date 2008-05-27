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
import geogebra.kernel.integration.GaussQuadIntegration;
import geogebra.kernel.roots.RealRootFunction;

/**
 * Integral of a function (GeoFunction)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegralDefinite extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private NumberValue a, b; //input
    private GeoElement ageo, bgeo;
    private GeoNumeric n; // output g = integral(f(x), x, a, b)   

    // for numerical adaptive GaussQuad integration
    private static final int MAX_STEP = 40; // max bisection steps
    private static final int FIRST_ORDER = 5;
    private static final int SECOND_ORDER = 7;
    private static final double ACCURACY = Kernel.MIN_PRECISION;   
    private static GaussQuadIntegration firstGauss, secondGauss;

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
        setInputOutput(); // for AlgoElement        
        compute();
    }

    protected String getClassName() {
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

        // get function of f
        Function fun = f.getFunction();

        /* 
         * Try to get symbolic integral
         *
         * We only do this for functions that do NOT include divisions by their variable.
         * Otherwise there might be problems like:
         * Integral[ 1/x, -2, -1 ] would be undefined (log(-1) - log(-2))
         * Integral[ 1/x^2, -1, 1 ] would be defined (-2)
         */
        if (!f.includesDivisionByVar()) {
	        Function intFun = fun.getIntegral();
	        if (intFun != null) {
	        	double val = intFun.evaluate(upperLimit) - intFun.evaluate(lowerLimit);
	            n.setValue(val);	        
	            if (n.isDefined()) return;
	        }
        }                

        // numerical integration
       // max_error = ACCURACY; // current maximum error
        //maxstep = 0;              
        double integral = adaptiveGaussQuad(fun, lowerLimit, upperLimit);
        n.setValue(integral);
              
        /*
        System.out.println("***\nsteps: " + maxstep);                   
        System.out.println("max_error: " + max_error);
        */
    }
    //  private int maxstep;
    
    public static double adaptiveGaussQuad(RealRootFunction fun, double a, double b) {
    	return adaptiveGaussQuad(fun, a, b, 0, ACCURACY);
    }    		

    // adaptive GaussQuad integration:
    // take bisection step if difference between two GaussQuad orders is too big
    private static double adaptiveGaussQuad(
    	RealRootFunction fun,
        double a,
        double b,
        int step,
        double maxError) 
    {
    	// init GaussQuad classes for numerical integration
        if (firstGauss == null) {
            firstGauss = new GaussQuadIntegration(FIRST_ORDER);
            secondGauss = new GaussQuadIntegration(SECOND_ORDER);
        }
    	
        double firstSum = firstGauss.integrate(fun, a, b);
        double secondSum = secondGauss.integrate(fun, a, b);
        double error = Math.abs(firstSum - secondSum);
               
        if (error <= maxError || error <= 1E-10) { // success              
            return secondSum;
        } else {
            if (step < MAX_STEP) { // do bisection
                double mid = (a + b) / 2;
                int s = step + 1;
                double err = maxError / 2;
                double left = adaptiveGaussQuad(fun, a, mid, s, err);
                if (Double.isNaN(left))
                    return Double.NaN;
                else
                    return left + adaptiveGaussQuad(fun, mid, b, s, err);
            } else
				//  if we get here then the accuracy could not be reached
                //System.out.println("MAX_STEP reached: no integral found: " + a + ", " + b + ", err: " + error);
                return Double.NaN;
        }
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("IntegralOfAfromBtoC",f.getLabel(),ageo.getLabel(),bgeo.getLabel()));
        
        return sb.toString();
    }

}
