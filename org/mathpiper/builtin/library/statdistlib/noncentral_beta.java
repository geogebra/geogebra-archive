package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class noncentral_beta 
  { 
    /*
     *  DistLib : A C Library of Special Functions
     *  Copyright (C) 1998 Ross Ihaka
     *
     *  This program is free software; you can redistribute it and/or modify
     *  it under the terms of the GNU General Public License as published by
     *  the Free Software Foundation; either version 2 of the License, or
     *  (at your option) any later version.
     *
     *  This program is distributed in the hope that it will be useful,
     *  but WITHOUT ANY WARRANTY; without even the implied warranty of
     *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     *  GNU General Public License for more details.
     *
     *  You should have received a copy of the GNU General Public License
     *  along with this program; if not, write to the Free Software
     *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
     *
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    double density(double x, double a, double b, double lambda);
     *
     *  DESCRIPTION
     *
     *    Computes the density of the noncentral beta distribution with
     *    noncentrality parameter lambda.  The noncentral beta distribution
     *    has density:
     *
     *                     Inf
     *        f(x|a,b,d) = SUM p(i) * B(a+i,b) * x^(a+i-1) * (1-x)^(b-1)
     *                     i=0
     *
     *    where:
     *
     *              p(k) = exp(-lambda) lambda^k / k!
     *
     *            B(a,b) = Gamma(a+b) / (Gamma(a) * Gamma(b))
     *
     *
     *    This can be computed efficiently by using the recursions:
     *
     *            p(k+1) = (lambda/(k+1)) * p(k-1)
     *
     *        B(a+k+1,b) = ((a+b+k)/(a+k)) * B(a+k,b)
     *
     *    The summation of the series continues until
     *
     *              psum = p(0) + ... + p(k)
     *
     *    is close to 1.  Here we continue until 1 - psum < epsilon,
     *    with epsilon set close to the relative machine precision.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  density(double x, double a, double b, double lambda)
    {
    	double k, lambda2, psum, sum, term, weight;
    	final double eps = 1.e-14;
    	final int maxiter = 200;
    
    /*!* #ifdef IEEE_754 /*4!*/
    	if (Double.isNaN(x) || Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(lambda))
    		return x + a + b + lambda;
    /*!* #endif /*4!*/
    
    	if (lambda < 0 || a <= 0 || b <= 0) {
    		throw new java.lang.ArithmeticException("Math Error: DOMAIN");
    	}
    
    /*!* #ifdef IEEE_754 /*4!*/
    	if (Double.isInfinite(a) || Double.isInfinite(b) || Double.isInfinite(lambda)) {
    		throw new java.lang.ArithmeticException("Math Error: DOMAIN");
		//    		return Double.NaN;
    	}
    /*!* #endif /*4!*/
    
    	if(x <= 0) return 0;
    
    	term = beta.density(x, a, b);
    	if(lambda == 0)
    		return term;
    
    	lambda2 = 0.5 * lambda;
/*!* 	weight = exp(- lambda2); *!*/
    	weight = java.lang.Math.exp(- lambda2);
    	sum = weight * term;
    	psum = weight;
    	for(k=1 ; k<=maxiter ; k++) {
    		weight = weight * lambda2 / k;
    		term = term * x * (a + b) / a;
    		sum = sum + weight * term;
    		psum = psum + weight;
    		a = a + 1;
    		if(1 - psum < eps) break;
    	}
    	return sum;
    }
    /*
     *  Algorithm AS 226 Appl. Statist. (1987) Vol. 36, No. 2
     *  Incorporates modification AS R84 from AS Vol. 39, pp311-2, 1990
     *
     *  Returns the cumulative probability of x for the non-central
     *  beta distribution with parameters a, b and non-centrality lambda.
     *
     *  Auxiliary routines required:
     *    lgamma - log-gamma function
     *    pbeta  - incomplete-beta function
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  cumulative(double x, double a, double b, double lambda)
    {
        double a0, ans, ax, lbeta, c, errbd, gx, q, sumq, temp, x0;
        int j;
    
        final double zero = 0;
        final double one = 1;
        final double half = 0.5;
    
            /* change errmax and itrmax if desired */
    
        final double ualpha = 5.0;
        final double errmax = 1.0e-6;
        final int itrmax = 100;
    
    
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(lambda))
    	return x + a + b + lambda;
    /*!* #endif /*4!*/
    
        if (lambda < zero || a <= zero || b <= zero) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    
        if (x <= zero) return 0;
        if(x >= one) return 1;
    
        c = lambda * half;
    
            /* initialize the series */
    
/*!*     x0 = floor(fmax2(c - ualpha * sqrt(c), zero)); *!*/
        x0 = java.lang.Math.floor(Math.max(c - ualpha * java.lang.Math.sqrt(c), zero));
        a0 = a + x0;
/*!*     lbeta = lgammafn(a0) + lgammafn(b) - lgammafn(a0 + b); *!*/
        lbeta = misc.lgammafn(a0) + misc.lgammafn(b) - misc.lgammafn(a0 + b);
        temp = beta.cumulative(x, a0, b);
/*!*     gx = exp(a0 * log(x) + b * log(one - x) - lbeta - log(a0)); *!*/
        gx = java.lang.Math.exp(a0 * java.lang.Math.log(x) + b * java.lang.Math.log(one - x) - lbeta - java.lang.Math.log(a0));
        if (a0 > a)
/*!* 	q = exp(-c + x0 * log(c) - lgammafn(x0 + one)); *!*/
    	q = java.lang.Math.exp(-c + x0 * java.lang.Math.log(c) - misc.lgammafn(x0 + one));
        else
/*!* 	q = exp(-c); *!*/
    	q = java.lang.Math.exp(-c);
    
        ax = q * temp;
        sumq = one - q;
        ans = ax;
    
            /* recur over subsequent terms */
            /* until convergence is achieved */
        j = 0;
        do {
    	j++;
    	temp += - gx;
    	gx *= x * (a + b + j - one) / (a + j);
    	q *= c / j;
    	sumq += - q;
    	ax = temp * q;
    	ans += ax;
    	errbd = (temp - gx) * sumq;
        }
        while (errbd > errmax && j < itrmax);
    
        if (errbd > errmax) {
    	throw new java.lang.ArithmeticException("Math Error: PRECISION");
        }
        return ans;
    }
  }
