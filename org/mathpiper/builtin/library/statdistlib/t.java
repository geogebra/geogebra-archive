package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class t 
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
     *    double density(double x, double n);
     *
     *  DESCRIPTION
     *
     *    The density of the "Student" t distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  density(double x, double n)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(n))
    	return x + n;
    /*!* #endif /*4!*/
        if (n <= 0.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isInfinite(x))
    	return 0;
        if(Double.isInfinite(n))
    	return normal.density(x, 0.0, 1.0);
    /*!* #endif /*4!*/
/*!*     return pow(1.0 + x * x / n, -0.5 * (n + 1.0)) *!*/
        return java.lang.Math.pow(1.0 + x * x / n, -0.5 * (n + 1.0))
/*!* 	/ (sqrt(n) * beta(0.5, 0.5 * n)); *!*/
    	/ (java.lang.Math.sqrt(n) * misc.beta(0.5, 0.5 * n));
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
    
    public static double  cumulative(double x, double n)
    {
        double val;
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(n))
    	return x + n;
    /*!* #endif /*4!*/
        if (n <= 0.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isInfinite(x))
    	return (x < 0) ? 0 : 1;
        if(Double.isInfinite(n))
    	return normal.cumulative(x, 0.0, 1.0);
    /*!* #endif /*4!*/
        val = 0.5 * beta.cumulative(n / (n + x * x), n / 2.0, 0.5);
        return (x > 0.0) ? 1 - val : val;
    }
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
     *    double quantile(double p, double ndf);
     *
     *  DESCRIPTION
     *
     *    The "Student" t distribution quantile function.
     *
     *  NOTES
     *
     *    This is a C translation of the Fortran routine given in:
     *    Algorithm 396: Student's t-quantiles by G.W. Hill
     *    CACM 13(10), 619-620, October 1970
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    static private double eps = 1.e-12;
    
    public static double  quantile(double p, double ndf)
    {
    	double a, b, c, d, prob, P, q, x, y;
    	boolean neg;
    
    /*!* #ifdef IEEE_754 /*4!*/
    	if (Double.isNaN(p) || Double.isNaN(ndf))
    		return p + ndf;
    	if(ndf < 1 || p > 1 || p < 0) {
    	    throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	    //    	    return Double.NaN;
    	}
    	if (p == 0) return Double.NEGATIVE_INFINITY;
    	if (p == 1) return Double.POSITIVE_INFINITY;
    /*!* #else /*4!*/
    	if (ndf < 1 || p > 1 || p < 0) {
    	    throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	    //    	    return Double.NaN;
    	}
    /*!* #endif /*4!*/
    	if (ndf > 1e20) return normal.quantile(p, 0.0, 1.0);
    
    	if(p > 0.5) {
    		neg = false; P = 2 * (1 - p);
    	} else {
    		neg = true; P = 2 * p;
    	}
    
/*!* 	if (fabs(ndf - 2) < eps) { *!*/
    	if (java.lang.Math.abs(ndf - 2) < eps) {
                    /* df ~= 2 */
/*!* 		q = sqrt(2 / (P * (2 - P)) - 2); *!*/
    		q = java.lang.Math.sqrt(2 / (P * (2 - P)) - 2);
    	}
    	else if (ndf < 1 + eps) {
                    /* df ~= 1 */
    		prob = P * Constants.M_PI_half;
/*!* 		q = cos(prob) / sin(prob); *!*/
    		q = java.lang.Math.cos(prob) / java.lang.Math.sin(prob);
    	}
    	else {
                    /*-- usual case;  including, e.g.,  df = 1.1 */
    		a = 1 / (ndf - 0.5);
    		b = 48 / (a * a);
    		c = ((20700 * a / b - 98) * a - 16) * a + 96.36;
/*!* 		d = ((94.5 / (b + c) - 3) / b + 1) * sqrt(a * Constants.M_PI_half) * ndf; *!*/
    		d = ((94.5 / (b + c) - 3) / b + 1) * java.lang.Math.sqrt(a * Constants.M_PI_half) * ndf;
/*!* 		y = pow(d * P, 2 / ndf); *!*/
    		y = java.lang.Math.pow(d * P, 2 / ndf);
    
    		if (y > 0.05 + a) {
    			/* Asymptotic inverse expansion about normal */
    			x = normal.quantile(0.5 * P, 0.0, 1.0);
    			y = x * x;
    			if (ndf < 5)
    				c = c + 0.3 * (ndf - 4.5) * (x + 0.6);
    			c = (((0.05 * d * x - 5) * x - 7) * x - 2) * x + b + c;
    			y = (((((0.4 * y + 6.3) * y + 36) * y + 94.5) / c - y - 3) / b + 1) * x;
    			y = a * y * y;
    			if (y > 0.002)
/*!* 				y = exp(y) - 1; *!*/
    				y = java.lang.Math.exp(y) - 1;
    			else {
                                    /* Taylor of  e^y -1 : */
    				y = 0.5 * y * y + y;
    			}
    		} else {
    			y = ((1 / (((ndf + 6) / (ndf * y) - 0.089 * d - 0.822)
    				   * (ndf + 2) * 3) + 0.5 / (ndf + 4))
    			     * y - 1) * (ndf + 1) / (ndf + 2) + 1 / y;
    		}
/*!* 		q = sqrt(ndf * y); *!*/
    		q = java.lang.Math.sqrt(ndf * y);
    	}
    	if(neg) q = -q;
    	return q;
    }
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
     *    #include "mathlib.h"
     *    double random(double df);
     *
     *  DESCRIPTION
     *
     *    Pseudo-random variates from an F distribution.
     *
     *  NOTES
     *
     *    This function calls rchisq and rnorm to do the real work.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  random(double df, uniform PRNG)
    {
        if (
    /*!* #ifdef IEEE_754 /*4!*/
    	Double.isNaN(df) ||
    /*!* #endif /*4!*/
    	df <= 0.0) {
	    throw new java.lang.ArithmeticException("Math Error: DOMAIN");
        }
        if(Double.isInfinite(df))
	    return normal.random(PRNG);
        else
/*!* 	return normal.random!!!COMMENT!!!() / sqrt(rchisq(df) / df); *!*/
	    return normal.random(PRNG) / java.lang.Math.sqrt(chisquare.random(df, PRNG) / df);
    }
  }
