package org.mathpiper.builtin.library.statdistlib;

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
     */


/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class normal 
  { 
    
    /*  Mathematical Constants */
    static private double  SIXTEN = 1.6;   /* Magic Cutoff */


    /*
     * 	M_1_SQRT_2PI = 1 / sqrt(2 * pi)
     */
    
    /** The Normal Density Function */
    public static double  density(double x, double mu, double sigma)
    {
        if (Double.isNaN(x) || Double.isNaN(mu) || Double.isNaN(sigma))
    	return x + mu + sigma;
        if (sigma <= 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
        }
    
        x = (x - mu) / sigma;
        return Constants.M_1_SQRT_2PI * 
               java.lang.Math.exp(-0.5 * x * x) / sigma;
    }

    /**  DESCRIPTION 
     *    The main computation evaluates near-minimax approximations derived
     *    from those in "Rational Chebyshev approximations for the error
     *    function" by W. J. Cody, Math. Comp., 1969, 631-637.  This
     *    transportable program uses rational functions that theoretically
     *    approximate the normal distribution function to at least 18
     *    significant decimal digits.  The accuracy achieved depends on the
     *    arithmetic system, the compiler, the intrinsic functions, and
     *    proper selection of the machine-dependent constants.
     *
     *  REFERENCE
     *
     *    Cody, W. D. (1993).
     *    ALGORITHM 715: SPECFUN - A Portable FORTRAN Package of
     *    Special Function Routines and Test Drivers".
     *    ACM Transactions on Mathematical Software. 19, 22-32.
     */

    public static double cumulative(double x, double mu, double sigma)
    {
        final double c[] = {
    	0.39894151208813466764,
    	8.8831497943883759412,
    	93.506656132177855979,
    	597.27027639480026226,
    	2494.5375852903726711,
    	6848.1904505362823326,
    	11602.651437647350124,
    	9842.7148383839780218,
    	1.0765576773720192317e-8
        };
    
        final double d[] = {
    	22.266688044328115691,
    	235.38790178262499861,
    	1519.377599407554805,
    	6485.558298266760755,
    	18615.571640885098091,
    	34900.952721145977266,
    	38912.003286093271411,
    	19685.429676859990727
        };
    
        final double p[] = {
    	0.21589853405795699,
    	0.1274011611602473639,
    	0.022235277870649807,
    	0.001421619193227893466,
    	2.9112874951168792e-5,
    	0.02307344176494017303
        };
    
        final double q[] = {
    	1.28426009614491121,
    	0.468238212480865118,
    	0.0659881378689285515,
    	0.00378239633202758244,
    	7.29751555083966205e-5
        };
    
        final double a[] = {
    	2.2352520354606839287,
    	161.02823106855587881,
    	1067.6894854603709582,
    	18154.981253343561249,
    	0.065682337918207449113
        };
    
        final double b[] = {
    	47.20258190468824187,
    	976.09855173777669322,
    	10260.932208618978205,
    	45507.789335026729956
        };
    
        double xden, temp, xnum, result, ccum;
        double del, min, eps, xsq;
        double y;
        int i;
    
        /* Note: The structure of these checks has been */
        /* carefully thought through.  For example, if x == mu */
        /* and sigma == 0, we still get the correct answer. */
    
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isNaN(x) || Double.isNaN(mu) || Double.isNaN(sigma))
    	return x + mu + sigma;
    /*!* #endif /*4!*/
        if (sigma < 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        x = (x - mu) / sigma;
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isInfinite(x)) {
    	if(x < 0) return 0;
    	else return 1;
        }
    /*!* #endif /*4!*/
    
        eps = Constants.DBL_EPSILON * 0.5;
        min = Double.MIN_VALUE;
/*!*     y = fabs(x); *!*/
        y = java.lang.Math.abs(x);
        if (y <= 0.66291) {
    	xsq = 0.0;
    	if (y > eps) {
    	    xsq = x * x;
    	}
    	xnum = a[4] * xsq;
    	xden = xsq;
    	for (i = 1; i <= 3; ++i) {
    	    xnum = (xnum + a[i - 1]) * xsq;
    	    xden = (xden + b[i - 1]) * xsq;
    	}
    	result = x * (xnum + a[3]) / (xden + b[3]);
    	temp = result;
    	result = 0.5 + temp;
    	ccum = 0.5 - temp;
        }
        else if (y <= Constants.M_SQRT_32) {
    
    	/* Evaluate pnorm for 0.66291 <= |z| <= sqrt(32) */
    
    	xnum = c[8] * y;
    	xden = y;
    	for (i = 1; i <= 7; ++i) {
    	    xnum = (xnum + c[i - 1]) * y;
    	    xden = (xden + d[i - 1]) * y;
    	}
    	result = (xnum + c[7]) / (xden + d[7]);
/*!* 	xsq = floor(y * SIXTEN) / SIXTEN; *!*/
    	xsq = java.lang.Math.floor(y * SIXTEN) / SIXTEN;
    	del = (y - xsq) * (y + xsq);
/*!* 	result = exp(-xsq * xsq * 0.5) * exp(-del * 0.5) * result; *!*/
    	result = java.lang.Math.exp(-xsq * xsq * 0.5) * java.lang.Math.exp(-del * 0.5) * result;
    	ccum = 1.0 - result;
    	if (x > 0.0) {
    	    temp = result;
    	    result = ccum;
    	    ccum = temp;
    	}
        }
        else if(y < 50) {
    
    	/* Evaluate pnorm for sqrt(32) < |z| < 50 */
    
    	result = 0.0;
    	xsq = 1.0 / (x * x);
    	xnum = p[5] * xsq;
    	xden = xsq;
    	for (i = 1; i <= 4; ++i) {
    	    xnum = (xnum + p[i - 1]) * xsq;
    	    xden = (xden + q[i - 1]) * xsq;
    	}
    	result = xsq * (xnum + p[4]) / (xden + q[4]);
    	result = (Constants.M_1_SQRT_2PI - result) / y;
/*!* 	xsq = floor(x * SIXTEN) / SIXTEN; *!*/
    	xsq = java.lang.Math.floor(x * SIXTEN) / SIXTEN;
    	del = (x - xsq) * (x + xsq);
/*!* 	result = exp(-xsq * xsq * 0.5) * exp(-del * 0.5) * result; *!*/
    	result = java.lang.Math.exp(-xsq * xsq * 0.5) * java.lang.Math.exp(-del * 0.5) * result;
    	ccum = 1.0 - result;
    	if (x > 0.0) {
    	    temp = result;
    	    result = ccum;
    	    ccum = temp;
    	}
        }
        else {
    	if(x > 0) {
    	    result = 1.0;
    	    ccum = 0.0;
    	}
    	else {
    	    result = 0.0;
    	    ccum = 1.0;
    	}
        }
        if (result < min) {
    	result = 0.0;
        }
        if (ccum < min) {
    	ccum = 0.0;
        }
        return result;
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
     *    double cumulative(double p, double mu, double sigma);
     *
     *  DESCRIPTION
     *
     *    Compute the quantile function for the normal distribution.
     *
     *    For small to moderate probabilities, algorithm referenced
     *    below is used to obtain an initial approximation which is
     *    polished with a final Newton step.
     *
     *    For very large arguments, an algorithm of Wichura is used.
     *
     *  REFERENCE
     *
     *    Beasley, J. D. and S. G. Springer (1977).
     *    Algorithm AS 111: The percentage points of the normal distribution,
     *    Applied Statistics, 26, 118-121.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    
    public static double  quantile(double p, double mu, double sigma)
    {
        double q, r, val;
    
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(p) || Double.isNaN(mu) || Double.isNaN(sigma))
    	return p + mu + sigma;
    /*!* #endif /*4!*/
        if (p < 0.0 || p > 1.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    
        q = p - 0.5;
    
/*!*     if (fabs(q) <= 0.42) { *!*/
        if (java.lang.Math.abs(q) <= 0.42) {
    
    	/* 0.08 < p < 0.92 */
    
    	r = q * q;
    	val = q * (((-25.44106049637 * r + 41.39119773534) * r
    		    - 18.61500062529) * r + 2.50662823884)
    	    / ((((3.13082909833 * r - 21.06224101826) * r
    		 + 23.08336743743) * r + -8.47351093090) * r + 1.0);
        }
        else {
    
    	/* p < 0.08 or p > 0.92, set r = min(p, 1 - p) */
    
    	r = p;
    	if (q > 0.0)
    	    r = 1.0 - p;
    
    	if(r > Constants.DBL_EPSILON) {
/*!* 	    r = sqrt(-log(r)); *!*/
    	    r = java.lang.Math.sqrt(-java.lang.Math.log(r));
    	    val = (((2.32121276858 * r + 4.85014127135) * r
    		    - 2.29796479134) * r - 2.78718931138)
    		/ ((1.63706781897 * r + 3.54388924762) * r + 1.0);
    	    if (q < 0.0)
    		val = -val;
    	}
    	else if(r > 1e-300) {		/* Assuming IEEE here? */
/*!* 	    val = -2 * log(p); *!*/
    	    val = -2 * java.lang.Math.log(p);
/*!* 	    r = log(6.283185307179586476925286766552 * val); *!*/
    	    r = java.lang.Math.log(6.283185307179586476925286766552 * val);
    	    r = r/val + (2 - r)/(val * val)
    		+ (-14 + 6 * r - r * r)/(2 * val * val * val);
/*!* 	    val = sqrt(val * (1 - r)); *!*/
    	    val = java.lang.Math.sqrt(val * (1 - r));
    	    if(q < 0.0)
    		val = -val;
    	    return val;
    	}
    	else {
    	    throw new java.lang.ArithmeticException("Math Error: RANGE");
	    //    	    if(q < 0.0) {
	    //    		return Double.NEGATIVE_INFINITY;
	    //    	    }
	    //    	    else {
	    //    		return Double.POSITIVE_INFINITY;
	    //    	    }
    	}
        }
        val = val - (cumulative(val, 0.0, 1.0) - p) / normal.density(val, 0.0, 1.0);
        return mu + sigma * val;
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
     *    double random(double mu, double sigma, uniform PRNG );
     *
     *  DESCRIPTION
     *
     *    Random variates from the normal distribution.
     *
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  random(double mu, double sigma, uniform PRNG)
    {
        if(
    /*!* #ifdef IEEE_754 /*4!*/
            Double.isInfinite(mu) || Double.isInfinite(sigma) ||
    /*!* #endif /*4!*/
    	sigma < 0.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN; 
        } else
        if (sigma == 0.0)
    	return mu;
        else
    	return mu + sigma * random(PRNG);
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
     *    double random(void);
     *
     *  DESCRIPTION
     *
     *    Random variates from the STANDARD normal distribution  N(0,1).
     *
     * Is called from  random(..), but also rt(), rf(), rgamma(), ...
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    /*!* #define KINDERMAN_RAMAGE /*4!*/
    
    /*!* #ifdef AHRENS_DIETER /*4!*/
    
    /*
     *  REFERENCE
     *
     *    Ahrens, J.H. and Dieter, U.
     *    Extensions of Forsythe's method for random sampling from
     *    the normal distribution.
     *    Math. Comput. 27, 927-937.
     *
     *    The definitions of the constants a[k], d[k], t[k] and
     *    h[k] are according to the abovementioned article
     */
    public static double  random_AhrensDieter( uniform PRNG )
    {      
      final double a[] =
    {
    	0.0000000, 0.03917609, 0.07841241, 0.1177699,
    	0.1573107, 0.19709910, 0.23720210, 0.2776904,
    	0.3186394, 0.36012990, 0.40225010, 0.4450965,
    	0.4887764, 0.53340970, 0.57913220, 0.6260990,
    	0.6744898, 0.72451440, 0.77642180, 0.8305109,
    	0.8871466, 0.94678180, 1.00999000, 1.0775160,
    	1.1503490, 1.22985900, 1.31801100, 1.4177970,
    	1.5341210, 1.67594000, 1.86273200, 2.1538750
    };
    
    final double d[] =
    {
    	0.0000000, 0.0000000, 0.0000000, 0.0000000,
    	0.0000000, 0.2636843, 0.2425085, 0.2255674,
    	0.2116342, 0.1999243, 0.1899108, 0.1812252,
    	0.1736014, 0.1668419, 0.1607967, 0.1553497,
    	0.1504094, 0.1459026, 0.1417700, 0.1379632,
    	0.1344418, 0.1311722, 0.1281260, 0.1252791,
    	0.1226109, 0.1201036, 0.1177417, 0.1155119,
    	0.1134023, 0.1114027, 0.1095039
    };
    
    final double t[] =
    {
    	7.673828e-4, 0.002306870, 0.003860618, 0.005438454,
    	0.007050699, 0.008708396, 0.010423570, 0.012209530,
    	0.014081250, 0.016055790, 0.018152900, 0.020395730,
    	0.022811770, 0.025434070, 0.028302960, 0.031468220,
    	0.034992330, 0.038954830, 0.043458780, 0.048640350,
    	0.054683340, 0.061842220, 0.070479830, 0.081131950,
    	0.094624440, 0.112300100, 0.136498000, 0.171688600,
    	0.227624100, 0.330498000, 0.584703100
    };
    
    final double h[] =
    {
    	0.03920617, 0.03932705, 0.03950999, 0.03975703,
    	0.04007093, 0.04045533, 0.04091481, 0.04145507,
    	0.04208311, 0.04280748, 0.04363863, 0.04458932,
    	0.04567523, 0.04691571, 0.04833487, 0.04996298,
    	0.05183859, 0.05401138, 0.05654656, 0.05953130,
    	0.06308489, 0.06737503, 0.07264544, 0.07926471,
    	0.08781922, 0.09930398, 0.11555990, 0.14043440,
    	0.18361420, 0.27900160, 0.70104740
    };
    
        double s, u, w, y, ustar, aa, tt;
        int i;
    
        u = PRNG.random();
        s = 0.0;
        if (u > 0.5)
	    s = 1.0;
        u = u + u - s;
        u *= 32.0;
        i = (int) u;
        if (i == 32)
    	i = 31;
	deliver: {
	    if (i != 0) {
		ustar = u - i;
		aa = a[i - 1];
		while (ustar <= t[i - 1]) {
		    u = PRNG.random();
		    w = u * (a[i] - aa);
		    tt = (w * 0.5 + aa) * w;
		    while(true) {
			if (ustar > tt)
			    break deliver;
			u = PRNG.random();
			if (ustar < u)
			    break;
			tt = u;
			ustar = PRNG.random();
		    }
		    ustar = PRNG.random();
		}
		w = (ustar - t[i - 1]) * h[i - 1];
	    }
	    else {
		i = 6;
		aa = a[31];
		while(true) {
		    u = u + u;
		    if (u >= 1.0)
			break;
		    aa = aa + d[i - 1];
		    i = i + 1;
		}
		u = u - 1.0;
		jump: while(true) {
		    w = u * d[i - 1];
		    tt = (w * 0.5 + aa) * w;
		    while(true) {
			ustar = PRNG.random();
			if (ustar > tt)
			    break jump;
			u = PRNG.random();
			if (ustar < u)
			    break;
			tt = u;
		    }
		    u = PRNG.random();
		} // jump:;
	    }
	    
	} // deliver:
        y = aa + w;
        return (s == 1.0) ? -y : y;
    
    }
    
    /*!* #endif /*4!*/
    
    /*!* #ifdef KINDERMAN_RAMAGE /*4!*/
    
    /*
     *  REFERENCE
     *
     *    Kinderman A. J. and Ramage J. G. (1976).
     *    Computer generation of normal random variables.
     *    JASA 71, 893-896.
     */
    
    static final double  C1 = 0.398942280401433; 
    static final double  C2 = 0.180025191068563; 
/*!* /*!* #define g(x)		(C1*exp(-x*x/2.0)-C2*(a-fabs(x))) /*4!* *!*/
      static final double a =  2.216035867166471;

      static final double g(double x)
    {
	return (C1*java.lang.Math.exp(-x*x/2.0)-C2*(a-java.lang.Math.abs(x))) ;
    }
    
    public static double  random( uniform PRNG )
    {
        double t, u1, u2, u3;
    
        u1 = PRNG.random();
        if(u1 < 0.884070402298758) {
    	u2 = PRNG.random();
    	return a*(1.13113163544180*u1+u2-1);
        }
    
        if(u1 >= 0.973310954173898) {
        tail: while(true) {
    	u2 = PRNG.random();
    	u3 = PRNG.random();
/*!* 	t = (a*a-2*log(u3)); *!*/
    	t = (a*a-2*java.lang.Math.log(u3));
    	if( u2*u2<(a*a)/t )
/*!* 	    return (u1 < 0.986655477086949) ? sqrt(t) : -sqrt(t) ; *!*/
    	    return (u1 < 0.986655477086949) ? java.lang.Math.sqrt(t) : -java.lang.Math.sqrt(t) ;
    	// continue tail;
        }
	}
    
        if(u1 >= 0.958720824790463) {
        region3: while(true) {
    	u2 = PRNG.random();
    	u3 = PRNG.random();
/*!* 	t = a - 0.630834801921960* fmin2(u2,u3); *!*/
    	t = a - 0.630834801921960* Math.min(u2,u3);
/*!* 	if(fmax2(u2,u3) <= 0.755591531667601) *!*/
    	if(Math.max(u2,u3) <= 0.755591531667601)
    	    return (u2<u3) ? t : -t ;
/*!* 	if(0.034240503750111*fabs(u2-u3) <= g(t)) *!*/
    	if(0.034240503750111*java.lang.Math.abs(u2-u3) <= g(t))
    	    return (u2<u3) ? t : -t ;
    	// continue region3;
        }
	}
    
        if(u1 >= 0.911312780288703) {
        region2: {
    	u2 = PRNG.random();
    	u3 = PRNG.random();
/*!* 	t = 0.479727404222441+1.105473661022070*fmin2(u2,u3); *!*/
    	t = 0.479727404222441+1.105473661022070*Math.min(u2,u3);
/*!* 	if( fmax2(u2,u3)<=0.872834976671790 ) *!*/
    	if( Math.max(u2,u3)<=0.872834976671790 )
    	    return (u2<u3) ? t : -t ;
/*!* 	if( 0.049264496373128*fabs(u2-u3)<=g(t) ) *!*/
    	if( 0.049264496373128*java.lang.Math.abs(u2-u3)<=g(t) )
    	    return (u2<u3) ? t : -t ;
    	// continue region2;
        }}
    
    region1: while(true) {
        u2 = PRNG.random();
        u3 = PRNG.random();
/*!*     t = 0.479727404222441-0.595507138015940*fmin2(u2,u3); *!*/
        t = 0.479727404222441-0.595507138015940*Math.min(u2,u3);
/*!*     if(fmax2(u2,u3) <= 0.805577924423817) *!*/
        if(Math.max(u2,u3) <= 0.805577924423817)
    	return (u2<u3) ? t : -t ;
	// continue region1;
    }}
    
    /*!* #endif /*4!*/

//     public boolean test()
//     {
// 	final int HOWMANY = 10000;
// 	double testArr[] = new double[10000];

// 	for(int i=0; i<HOWMANY; i++)
// 	    testArr[i] = random();
	
// 	/* dumb bubblesort */
// 	boolean ordered=false;
// 	double temp;
// 	while(!ordered)
// 	    {
// 		for(int i=1; i<HOWMANY-1; i++)
// 		    if( testArr[i] > testArr[i+1] )
// 			{
// 			    temp = testArr[i];
// 			    testArr[i] = testArr[i+1];
// 			    testArr[i+1] = temp;
// 			    ordered=false;
// 			}
// 	    }

// 	return true;

//     }

  }
