package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class noncentral_t 
  { 
    /*
     *  Algorithm AS 243  Appl. Statist. (1989), Vol.38, No. 1.
     *
     *  Cumulative probability at t of the non-central t-distribution
     *  with df degrees of freedom (may be fractional) and non-centrality
     *  parameter delta.
     *
     *  NOTE
     *
     *    Requires the following auxiliary routines:
     *
     *        lgammafn(x)       - log gamma function
     *        beta.cumulative(x, a, b)  - incomplete beta function
     *        normal.cumulative(x)        - normal distribution function
     *
     *  CONSTANTS
     *
     *    M_SQRT_2dPI  = 1/ {gamma(1.5) * sqrt(2)} = sqrt(2 / pi)
     *    M_LN_SQRT_PI = ln(sqrt(pi)) = ln(pi)/2
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  cumulative(double t, double df, double delta)
    {
        double a, albeta, b, del, en, errbd, geven, godd;
	double lambda, p, q, rxb, s, tnc, tt, x, xeven, xodd;
        boolean negdel;
    
        /* note - itrmax and errmax may be changed to suit one's needs. */
    
        final double itrmax = 100.1;
        final double errmax = 1.e-12;
    
        final double zero = 0.0;
        final double half = 0.5;
        final double one = 1.0;
        final double two = 2.0;
    
        tnc = zero;
        if (df <= zero) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        tt = t;
        del = delta;
        negdel = false;
        if (t < zero) {
    	negdel = true;
    	tt = -tt;
    	del = -del;
        }
        /* initialize twin series */
        /* (guenther, j. statist. computn. simuln.  vol.6, 199, 1978). */
    
        en = one;
        x = t * t / (t * t + df);
        if (x > zero) {
    	lambda = del * del;
/*!* 	p = half * exp(-half * lambda); *!*/
    	p = half * java.lang.Math.exp(-half * lambda);
    	q = Constants.M_SQRT_2dPI * p * del;
    	s = half - p;
    	a = half;
    	b = half * df;
/*!* 	rxb = pow(one - x, b); *!*/
    	rxb = java.lang.Math.pow(one - x, b);
/*!* 	albeta = Constants.M_LN_SQRT_PI + lgammafn(b) - lgammafn(a + b); *!*/
    	albeta = Constants.M_LN_SQRT_PI + misc.lgammafn(b) - misc.lgammafn(a + b);
    	xodd = beta.cumulative(x, a, b);
/*!* 	godd = two * rxb * exp(a * log(x) - albeta); *!*/
    	godd = two * rxb * java.lang.Math.exp(a * java.lang.Math.log(x) - albeta);
    	xeven = one - rxb;
    	geven = b * x * rxb;
    	tnc = p * xodd + q * xeven;
    
    	/* while(true) until convergence */
    
    	do {
    	    a = a + one;
    	    xodd = xodd - godd;
    	    xeven = xeven - geven;
    	    godd = godd * x * (a + b - one) / a;
    	    geven = geven * x * (a + b - half) / (a + half);
    	    p = p * lambda / (two * en);
    	    q = q * lambda / (two * en + one);
    	    s = s - p;
    	    en = en + one;
    	    tnc = tnc + p * xodd + q * xeven;
    	    errbd = two * s * (xodd - godd);
    	}
    	while (errbd > errmax && en <= itrmax);
        }
        if (en <= itrmax)
    	throw new java.lang.ArithmeticException("Math Error: PRECISION");
        tnc = tnc + normal.cumulative(- del, zero, one);
        if (negdel)
    	tnc = one - tnc;
        return tnc;
    }
  }
