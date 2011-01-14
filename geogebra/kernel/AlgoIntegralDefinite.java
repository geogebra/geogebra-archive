/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.roots.RealRootAdapter;
import geogebra.kernel.roots.RealRootFunction;

import org.apache.commons.math.analysis.integration.LegendreGaussIntegrator;
import org.apache.commons.math.analysis.integration.RombergIntegrator;
import org.apache.commons.math.analysis.integration.TrapezoidIntegrator;

/**
 * Integral of a function (GeoFunction)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegralDefinite extends AlgoElement  implements AlgoDrawInformation{

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private NumberValue a, b; //input
    private GeoElement ageo, bgeo;
    private GeoNumeric n; // output g = integral(f(x), x, a, b)   

    // for symbolic integration
    private GeoFunction symbIntegral;
    
    private static final int MAX_ITER = 12; // max 2^MAX_ITER function evaluations
    private static RombergIntegrator romberg;
    

    /**
     * Creates new labeled integral
     * @param cons
     * @param label
     * @param f
     * @param a
     * @param b
     */
    public AlgoIntegralDefinite(
        Construction cons,
        String label,
        GeoFunction f,
        NumberValue a,
        NumberValue b) {
        this(cons, f, a, b);
        n.setLabel(label);
    }

    /**
     * Creates new unlabeled integral
     * @param cons
     * @param f
     * @param a
     * @param b
     */
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

    public AlgoIntegralDefinite copy(){
    	return new AlgoIntegralDefinite(cons, (GeoFunction)f.copy(), 
    			new MyDouble(kernel,a.getDouble()), new MyDouble(kernel,b.getDouble()));

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

        setOutputLength(1);
        setOutput(0,n);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the integral as GeoNumeric object
     * @return the integral
     */
    public GeoNumeric getIntegral() {
        return n;
    }

    /**
     * Returns the integral value without recomputing
     * @return integral value 
     */
    double getIntegralValue() {
        return n.value;
    }

    /**
     * Returns the function
     * @return function to be integrated
     */
    public GeoFunction getFunction() {
        return f;
    }

    /**
     * Returns the lower bound
     * @return lower bound
     */
    public NumberValue getA() {
        return a;
    }

    /**
     * Returns the upper bound
     * @return upper bound
     */
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
        
        double integral = numericIntegration(f, lowerLimit, upperLimit);
        n.setValue(integral);
              
        /*
        Application.debug("***\nsteps: " + maxstep);                   
        Application.debug("max_error: " + max_error);
        */
    }
    //  private int maxstep;
    
    /**
     * Computes numeric integral of function fun in interval a, b.
     * @return Integral value
     * @param fun function to be integrated
     * @param a lower bound
     * @param b upper bound
     */
    public static double numericIntegration(RealRootFunction fun, double a, double b) {
//    	// init GaussQuad classes for numerical integration
//        if (gauss == null) {
//            gauss = new LegendreGaussIntegrator(ORDER, MAX_ITER);
//        }
//    	 // integrate using adaptive gauss quadrature
//		try {
//			return gauss.integrate(new RealRootAdapter(fun), a, b);
//		} catch (Exception e) {
//			return Double.NaN;
//		}
		
	 	// Romberg integration using trapezoid rule
    	// the max number of function evaluations is here limited
    	// to 2^MAX_ITER
        if (romberg == null) {
        	romberg = new RombergIntegrator();
        	romberg.setMaximalIterationCount(MAX_ITER);
        }
    	 // integrate using adaptive gauss quadrature
		try {
			return romberg.integrate(new RealRootAdapter(fun), a, b);
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
