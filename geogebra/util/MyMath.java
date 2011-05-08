/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.util;

import geogebra.kernel.Kernel;

import java.math.BigDecimal;

import org.apache.commons.math.MathException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.special.Beta;
import org.apache.commons.math.special.Erf;
import org.apache.commons.math.special.Gamma;

/**
 * @author Markus Hohenwarter
 */
public final class MyMath {
	
	public static final double LOG10 = Math.log(10);
	public static final double LOG2 = Math.log(2);
	public static final double ONE_THIRD = 1d / 3d;


	/**
	 * Cubic root
	 */
	final public static double cbrt(double a) {
		if (a > 0.0)    
	    	return  Math.pow(a, ONE_THIRD);
	    else      
	    	return -Math.pow(-a, ONE_THIRD);  
	}
	
	
	final public static double sgn(Kernel kernel, double a) {
		
		// bugfix for graph f(x) = sgn(sqrt(1 - x)) 
    	if (Double.isNaN(a)) return Double.NaN;
    	
		if (Kernel.isZero(a)) 
			return 0.0;
	    else if (a > 0.0)    
	    	return  1.0;
	    else      
	    	return -1.0;    
	}
	
	final public static double cosh(double a) {  
		return  (Math.exp(a) + Math.exp(-a)) * 0.5; 
	}
	
	final public static double sinh(double a) {  
		return (Math.exp(a) - Math.exp(-a)) * 0.5; 
	}
	
	final public static double tanh(double a) {  
		double e = Math.exp(2.0 * a);
		return (e - 1.0) / (e + 1.0);  
	}
	
	final public static double csch(double a) {  
		return  1 / sinh(a);
	}
	
	final public static double sech(double a) {  
		return 1 / cosh(a);
	}
	
	final public static double coth(double a) {  
		double e = Math.exp(2.0 * a);
		return (e + 1.0) / (e - 1.0);  
	}
	
	final public static double acosh(double a) {  
		return Math.log(a + Math.sqrt(a*a - 1.0));
	}

	final public static double asinh(double a) {  
		return Math.log(a + Math.sqrt(a*a + 1.0));
	}

	final public static double atanh(double a) {		  
		return Math.log((1.0 + a)/(1.0 - a)) * 0.5;
	}		
	
	final public static double csc(double a) {		  
		return 1 / Math.sin(a);
	}		
	
	final public static double sec(double a) {		  
		return 1 / Math.cos(a);
	}		
	
	final public static double cot(double a) {		  
		return Math.cos(a) / Math.sin(a);
	}		
	
	final public static double gamma(double x, Kernel kernel) {		

		// Michael Borcherds 2008-05-04
		 if (x <= 0 && kernel.isEqual( x, Math.round( x ) )) return Double.NaN; // negative integers

		 //		 Michael Borcherds 2007-10-15 BEGIN added case for x<0 otherwise no results in 3rd quadrant
		if (x>=0) 
			return Math.exp(Gamma.logGamma(x));
		else
			return -Math.PI/(x*gamma(-x, kernel)*Math.sin(Math.PI*x));
// Michael Borcherds 2007-10-15 END
	}
	
	final public static double gammaIncomplete(double a, double x, Kernel kernel) {		

		try {
			// see http://mathworld.wolfram.com/RegularizedGammaFunction.html
			// http://en.wikipedia.org/wiki/Incomplete_gamma_function#Regularized_Gamma_functions_and_Poisson_random_variables
			return Gamma.regularizedGammaQ(a, x) * gamma(a, kernel);
		} catch (MathException e) {
			return Double.NaN;
		}
	
	}
	
	final public static double gammaIncompleteRegularized(double a, double x) {		

		try {
			return Gamma.regularizedGammaQ(a, x);
		} catch (MathException e) {
			return Double.NaN;
		}
	
	}
	
	final public static double beta(double a, double b) {		

		return Math.exp(Beta.logBeta(a, b));
	
	}
	
	final public static double betaIncomplete(double a, double b, double x) {	

		try {
			return Beta.regularizedBeta(x, a, b) * beta(a,b);
		} catch (MathException e) {
			return Double.NaN;
		}
	
	}
	
	final public static double betaIncompleteRegularized(double a, double b, double x) {		

		try {
			return Beta.regularizedBeta(x, a, b);
		} catch (MathException e) {
			return Double.NaN;
		}
	}
	
	/**
	 * Factorial function of x. If x is an integer value x! is returned,
	 * otherwise gamma(x + 1) will be returned.	 
	 * For x < 0 Double.NaN is returned.
	 */	
	final public static double factorial(double x) {			

		if (x < 0) return Double.NaN; // bugfix Michael Borcherds 2008-05-04

		  // big x or floating point x is computed using gamma function
		if (x < 0 || x > 32 || x - Math.floor(x) > 1E-10) 
			// exp of log(gamma(x+1)) 
			return Math.exp(Gamma.logGamma(x+1.0));				
		
		int n = (int) x;		
		int j;				
		while (factorialTop<n) {
			j=factorialTop++;
			factorialTable[factorialTop]=factorialTable[j]*factorialTop;
		}
		return factorialTable[n];
	}
	
	private static int factorialTop=4;
	private static double [] factorialTable = new double[33]; 
	static { factorialTable[0] = 1.0; 
				factorialTable[1] = 1.0; 
				factorialTable[2] = 2.0; 
				factorialTable[3] = 6.0; 
				factorialTable[4] = 24.0; }						
/* replaced with Gamma.logGamma from Apache Commons Math
	// logarithm of gamma function of xx
	public static double gammln(double xx) {
		double x,y,tmp,ser;
		int j;

		y=x=xx;
		tmp=x+5.5;
		tmp -= (x+0.5)* Math.log(tmp);
		ser=1.000000000190015;
		for (j=0;j<=5;j++) ser += cof[j]/++y;
		return -tmp+Math.log(2.5066282746310005*ser/x);
	}
	// coefficients for gammln
	private static double [] cof = {76.18009172947146,-86.50532032941677,
					24.01409824083091,-1.231739572450155,
					0.1208650973866179e-2,-0.5395239384953e-5}; */
	
	/**
	 * Round a double to the given number of digits
	 */
	final public static double truncate(double x, int digits) {
		BigDecimal bd = new BigDecimal(x);
		bd = bd.setScale(digits, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
	
    final public static double erf(double mean, double standardDeviation, double x) {
        try {
            return Erf.erf((x - mean) /
                    (standardDeviation * Math.sqrt(2.0)));
        } catch (Exception ex) {
            if (x < (mean - 20 * standardDeviation)) { // JDK 1.5 blows at 38
                return 0.0d;
            } else if (x > (mean + 20 * standardDeviation)) {
                return 1.0d;
            } else {
                return Double.NaN;
            }
        }
    }



}
