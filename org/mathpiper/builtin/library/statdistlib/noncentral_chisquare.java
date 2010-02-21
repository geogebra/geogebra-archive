package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class noncentral_chisquare
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
     *    double density(x, df, lambda);
     *
     *  DESCRIPTION
     *
     *    The density of the noncentral chisquare distribution with
     *    "df" degrees of freedom and noncentrality parameter "lambda".
     *
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  density(double x, double df, double lambda)
    {
    	double dens, i, lambda2, psum, sum, weight;
    	final int maxiter = 100;
    	final double eps = 1.e-14;
    
    /*!* #ifdef IEEE_754 /*4!*/
    	if (Double.isNaN(x) || Double.isNaN(df) || Double.isNaN(lambda))
    		return x + df + lambda;
    /*!* #endif /*4!*/
    
    	if (lambda < 0 || df <= 0) {
    		throw new java.lang.ArithmeticException("Math Error: DOMAIN");
    	}
    
    /*!* #ifdef IEEE_754 /*4!*/
    	if (Double.isInfinite(df) || Double.isInfinite(lambda)) {
    		throw new java.lang.ArithmeticException("Math Error: DOMAIN");
		//    		return Double.NaN;
    	}
    /*!* #endif /*4!*/
    
    	if(x <= 0) return 0;
    
    	dens = chisquare.density(x, df);
    	if(lambda == 0)
    		return dens;
    
    	lambda2 = 0.5 * lambda;
/*!* 	weight = exp(-lambda2); *!*/
    	weight = java.lang.Math.exp(-lambda2);
    	sum = weight * dens;
    	psum = weight;
    	for(i=1 ; i<maxiter ; i++) {
    		dens = (x/df) * dens;
    		df = df + 2;
    		weight = weight * lambda2 / i;
    		sum = sum + dens * weight;
    		psum = psum + weight;
    		if (1 - psum < eps) break;
    	}
    	return sum;
    }
    /*
     *  Algorithm AS 275 appl.statist. (1992), vol.41, no.2
     *
     *  computes the noncentral chi-square distribution function with
     *  positive real degrees of freedom f and nonnegative noncentrality
     *  parameter theta
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    /*----------- DEBUGGING -------------
     *
     *	make CFLAGS='-DDEBUG_pnch -g -I/usr/local/include -I../include'
    
     * -- Feb.1, 1998 (R 0.62 alpha); M.Maechler:  still have
    	- INFINITE loop \
    	- bad precision / in some cases
     */
    /*!* #ifdef DEBUG_pnch /*4!*/
    /*!* # include "PrtUtil.h" /*4!*/
    /*!* #endif /*4!*/
    
    public static double  cumulative(double x, double f, double theta)
    {
        double ans, lam, u, v, x2, f2, t, term, bound, twon;
        int n;
	boolean flag;
    
        final double errmax = 1e-12;
        final double zero = 0;
        final double half = 0.5;
        final int itrmax = 100;
    
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(f) || Double.isNaN(theta))
	    return x + f + theta;
        if (Double.isInfinite(f) || Double.isInfinite(theta)) 
	    {
		throw new java.lang.ArithmeticException("Math Error: DOMAIN");
		//		return Double.NaN;
	    }
    /*!* #endif /*4!*/
    
        if (f < zero && theta < zero) {
	    throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	    //	    return Double.NaN;
        }
        if (x <= zero)
	    return 0;
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isInfinite(x))
	    return 1;
    /*!* #endif /*4!*/
    
    
        lam = theta * half;
     
    /*!* #ifdef DEBUG_pnch /*4!*/
	//  REprintf("pnchisq(x=%12g, f=%12g, theta=%12g):\n",x,f,theta);
    /*!* #endif /*4!*/
    
        /* evaluate the first term */
    
/*!*     v = u = exp(-lam); *!*/
        u = java.lang.Math.exp(-lam);
	v = u;
        x2 = x * half;
        f2 = f / half; 
        /* The following overflows very soon, eg. x=f=150 */
/*!*        t = pow(x2, f2) * exp(-x2) / exp(lgamma((f2 + 1))); */ /*!*/
        /* t = java.lang.Math.pow(x2, f2) * java.lang.Math.exp(-x2) / java.lang.Math.exp(lgamma((f2 + 1))); */
/*!*     t = exp(f2*log(x2) -x2 - lgammafn(f2 + 1)); *!*/
        t = java.lang.Math.exp(f2*java.lang.Math.log(x2) -x2 - misc.lgammafn(f2 + 1));
    
        /* there is no need to test ifault si */
        /* already been checked */ /*^^^^^^^^ ?????? */
    
        term = v * t;
        ans = term;
    /*!* #ifdef DEBUG_pnch /*4!*/
/*!*     REprintf("\t v=exp(-th/2)=%12g, x/2=%12g, f/2=%12g ==> t=%12g\n",v,x2,f2,t); *!*/
	//        REprintf("\t v=java.lang.Math.exp(-th/2)=%12g, x/2=%12g, f/2=%12g ==> t=%12g\n",v,x2,f2,t);
    /*!* #endif /*4!*/
    
        /* check if (f+2n) is greater than x */
    
        flag = false;
        n = 1; twon = n*2;
    L_End: for(;;) {
    /*!* #ifdef DEBUG_pnch /*4!*/
	    //    	REprintf(" _OL_: n=%d",n);
    /*!* #endif /*4!*/
    	if (!(f + twon - x > zero)) {
    	    /* evaluate the next term of the */
    	    /* expansion and then the partial sum */
    	    u *= lam / n;
    	    v += u;
    	    t *= x / (f + twon);
    	    term = v * t;
    	    ans += term;
    	    n++; twon = n*2;
	}
	else
	    {
		/* find the error bound and check for convergence */
		flag = true;
		
		for(;;) {
		    /*!* #ifdef DEBUG_pnch /*4!*/
		    //	    REprintf(" il: n=%d",n);
		    /*!* #endif /*4!*/
		    
		    bound = t * x / (f + twon - x);
		    /*!* #ifdef DEBUG_pnch /*4!*/
		    //    	    REprintf("\tL10: n=%d; term=%12g; bound=%12g\n",n,term,bound);
		    /*!* #endif /*4!*/
		    if (bound <= errmax || n > itrmax)
			break L_End;
		/* evaluate the next term of the */
		/* expansion and then the partial sum */
		u *= lam / n;
		v += u;
		t *= x / (f + twon);
		term = v * t;
		ans += term;
		n++; twon = n*2;
		}    
		
	    } 
    }// L_End:
        if (bound > errmax)
	    throw new java.lang.ArithmeticException("Math Error: PRECISION");
	/*!* #ifdef DEBUG_pnch /*4!*/
	//        REprintf("\tL_End: n=%d; term=%12g; bound=%12g\n",n,term,bound);
	/*!* #endif /*4!*/
	        return ans;
    }
    /*
     *  R : A Computer Langage for Statistical Data Analysis
     *  Copyright (C) 1995, 1996  Robert Gentleman and Ross Ihaka
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
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  quantile(double p, double n, double lambda)
    {
    	double ux, lx, nx;
    	double acu = 1.0e-12;
    
    /*!* #ifdef IEEE_754 /*4!*/
    	if (Double.isNaN(p) || Double.isNaN(n) || Double.isNaN(lambda))
    		return p + n + lambda;
    	if (Double.isInfinite(n)) {
    		throw new java.lang.ArithmeticException("Math Error: DOMAIN");
		//    		return Double.NaN;
    	}
    /*!* #endif /*4!*/
/*!* 	n = floor(n + 0.5); *!*/
    	n = java.lang.Math.floor(n + 0.5);
    	if (p < 0 || p >= 1 || n < 1 || lambda < 0) {
    		throw new java.lang.ArithmeticException("Math Error: DOMAIN");
		//    		return Double.NaN;
    	}
    	if (p == 0)
    		return 0;
    	for (ux = 1.0; cumulative(ux, n, lambda) < p; ux *= 2);
    	for (lx = ux; cumulative(lx, n, lambda) > p; lx *= 0.5);
    	do {
    		nx = 0.5 * (lx + ux);
    		if (cumulative(nx, n, lambda) > p)
    			ux = nx;
    		else
    			lx = nx;
    	}
    	while ((ux - lx) / nx > acu);
    	return 0.5 * (ux + lx);
    }
  }
