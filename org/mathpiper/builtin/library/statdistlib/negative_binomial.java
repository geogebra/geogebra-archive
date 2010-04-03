package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class negative_binomial 
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
     *    double density(double x, double n, double p);
     *
     *  DESCRIPTION
     *
     *    The density function of the negative binomial distribution.
     *
     *  NOTES
     *
     *    x = the number of failures before the n-th success
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  density(double x, double n, double p)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(n) || Double.isNaN(p))
    	return x + n + p;
    /*!* #endif /*4!*/
/*!*     x = floor(x + 0.5); *!*/
        x = java.lang.Math.floor(x + 0.5);
/*!*     n = floor(n + 0.5); *!*/
        n = java.lang.Math.floor(n + 0.5);
        if (n < 1 || p <= 0 || p >= 1) {
	    throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	    //    	return Double.NaN;
        }
        if (x < 0)
    	return 0;
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isInfinite(x))
    	return 0;
    /*!* #endif /*4!*/
/*!*     return exp(lfastchoose(x + n - 1, x) *!*/
        return java.lang.Math.exp(misc.lfastchoose(x + n - 1, x)
/*!* 	       + n * log(p) + x * log(1 - p)); *!*/
    	       + n * java.lang.Math.log(p) + x * java.lang.Math.log(1 - p));
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
     *    double cumulative(double x, double n, double p);
     *
     *  DESCRIPTION
     *
     *    The distribution function of the negative binomial distribution.
     *
     *  NOTES
     *
     *    x = the number of failures before the n-th success
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  cumulative(double x, double n, double p)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(n) || Double.isNaN(p))
    	return x + n + p;
        if(Double.isInfinite(n) || Double.isInfinite(p)) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    /*!* #endif /*4!*/
/*!*     x = floor(x + 0.5); *!*/
        x = java.lang.Math.floor(x + 0.5);
/*!*     n = floor(n + 0.5); *!*/
        n = java.lang.Math.floor(n + 0.5);
        if (n < 1 || p <= 0 || p >= 1) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x < 0) return 0;
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isInfinite(x))
    	return 1;
    /*!* #endif /*4!*/
        return beta.cumulative(p, n, x + 1);
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
     *    double quantile(double x, double n, double p);
     *
     *  DESCRIPTION
     *
     *    The distribution function of the negative binomial distribution.
     *
     *  NOTES
     *
     *    x = the number of failures before the n-th success
     *
     *  METHOD
     *
     *    Uses the Cornish-Fisher Expansion to include a skewness
     *    correction to a normal approximation.  This gives an
     *    initial value which never seems to be off by more than
     *    1 or 2.  A search is then conducted of values close to
     *    this initial start point.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  quantile(double x, double n, double p)
    {
        double P, Q, mu, sigma, gamma, z, y;
    
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(n) || Double.isNaN(p))
    	return x + n + p;
        if (Double.isInfinite(x)) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    /*!* #endif /*4!*/
/*!*     n = floor(n + 0.5); *!*/
        n = java.lang.Math.floor(n + 0.5);
        if (x < 0 || x > 1 || p <= 0 || p >= 1 || n <= 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x == 0) return 0;
    /*!* #ifdef IEEE_754 /*4!*/
        if (x == 1) return Double.POSITIVE_INFINITY;
    /*!* #endif /*4!*/
        Q = 1.0 / p;
        P = (1.0 - p) * Q;
        mu = n * P;
/*!*     sigma = sqrt(n * P * Q); *!*/
        sigma = java.lang.Math.sqrt(n * P * Q);
        gamma = (Q + P)/sigma;
        z = normal.quantile(x, 0.0, 1.0);
/*!*     y = floor(mu + sigma * (z + gamma * (z*z - 1.0) / 6.0) + 0.5); *!*/
        y = java.lang.Math.floor(mu + sigma * (z + gamma * (z*z - 1.0) / 6.0) + 0.5);
    
        z = cumulative(y, n, p);
        if(z >= x) {
    
    	/* search to the left */
    
    	for(;;) {
    	    if((z = cumulative(y - 1, n, p)) < x)
    		return y;
    	    y = y - 1;
    	}
        }
        else {
    
    	/* search to the right */
    
    	for(;;) {
    	    if((z = cumulative(y + 1, n, p)) >= x)
    		return y + 1;
    	    y = y + 1;
    	}
        }
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
     *    double density(double x, double n, double p);
     *
     *  DESCRIPTION
     *
     *    Random variates from the negative binomial distribution.
     *
     *  NOTES
     *
     *    x = the number of failures before the n-th success
     * 
     *  REFERENCE
     * 
     *    Devroye, L. (1980).
     *    Non-Uniform Random Variate Generation.
     *    New York:Springer-Verlag. Page 480.
     * 
     *  METHOD
     * 
     *    Generate lambda as gamma with shape parameter n and scale
     *    parameter p/(1-p).  Return a Poisson deviate with mean lambda.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  random(double n, double p, uniform PRNG)
    {
/*!*     n = floor(n + 0.5); *!*/
        n = java.lang.Math.floor(n + 0.5);
        if(
    /*!* #ifdef IEEE_754 /*4!*/
    	Double.isInfinite(n) || Double.isInfinite(p) ||
    /*!* #endif /*4!*/
    	n <= 0 || p <= 0 || p >= 1) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        return poisson.random(gamma.random(n, (1 - p) / p, PRNG), PRNG);
    }
  }
