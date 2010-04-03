package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */

public class gamma 
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
     *    double density(double x, double shape, double scale);
     *
     *  DESCRIPTION
     *
     *    Computes the density of the gamma distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  density(double x, double shape, double scale)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(shape) || Double.isNaN(scale))
    	return x + shape + scale;
    /*!* #endif /*4!*/
        if (shape <= 0 || scale <= 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x < 0)
    	return 0;
        if (x == 0) {
    	if (shape < 1) {
    	    throw new java.lang.ArithmeticException("Math Error: RANGE");
	    //    	    return Double.POSITIVE_INFINITY;
    	}
    	if (shape > 1) {
    	    return 0;
    	}
    	return 1 / scale;
        }
        x = x / scale;
/*!*     return exp((shape - 1) * log(x) - lgammafn(shape) - x) / scale; *!*/
        return java.lang.Math.exp((shape - 1) * java.lang.Math.log(x) - misc.lgammafn(shape) - x) / scale;
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
     *    double cumulative(double x, double a, double scale);
     *
     *  DESCRIPTION
     *
     *    This function computes the distribution function for the
     *    gamma distribution with shape parameter a and scale parameter
     *    scale.  This is also known as the incomplete gamma function.
     *    See Abramowitz and Stegun (6.5.1) for example.
     *
     *  NOTES
     *
     *    This function is an adaptation of Algorithm 239 from the
     *    Applied Statistics Series.  The algorithm is faster than
     *    those by W. Fullerton in the FNLIB library and also the
     *    TOMS 542 alorithm of W. Gautschi.  It provides comparable
     *    accuracy to those algorithms and is considerably simpler.
     *
     *  REFERENCES
     *
     *    Algorithm 239, Incomplete Gamma Function
     *    Applied Statistics 37, 1988.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    static private double
        third = 1.0 / 3.0,
        zero = 0.0,
        one = 1.0,
        two = 2.0,
        oflo = 1.0e+37,
        three = 3.0,
        nine = 9.0,
        xbig = 1.0e+8,
        plimit = 1000.0e0,
        elimit = -88.0e0;
    
    public static double  cumulative(double x, double p, double scale)
    {
        double pn1, pn2, pn3, pn4, pn5, pn6, arg, c, rn, a, b, an;
        double sum;
    
        /* check that we have valid values for x and p */
    
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(p) || Double.isNaN(scale))
    	return x + p + scale;
    /*!* #endif /*4!*/
        if(p <= zero || scale <= zero) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        x = x / scale;
        if (x <= zero)
    	return 0.0;
    
        /* use a normal approximation if p > plimit */
    
        if (p > plimit) {
/*!* 	pn1 = sqrt(p) * three * (pow(x/p, third) + one / (p * nine) - one); *!*/
    	pn1 = java.lang.Math.sqrt(p) * three * (java.lang.Math.pow(x/p, third) + one / (p * nine) - one);
    	return normal.cumulative(pn1, 0.0, 1.0);
        }
    
        /* if x is extremely large compared to p then return 1 */
    
        if (x > xbig)
    	return one;
    
        if (x <= one || x < p) {
    
    	/* use pearson's series expansion. */
    
/*!* 	arg = p * log(x) - x - lgammafn(p + one); *!*/
    	arg = p * java.lang.Math.log(x) - x - misc.lgammafn(p + one);
    	c = one;
    	sum = one;
    	a = p;
    	do {
    	    a = a + one;
    	    c = c * x / a;
    	    sum = sum + c;
    	} while (c > Constants.DBL_EPSILON);
/*!* 	arg = arg + log(sum); *!*/
    	arg = arg + java.lang.Math.log(sum);
    	sum = zero;
    	if (arg >= elimit)
/*!* 	    sum = exp(arg); *!*/
    	    sum = java.lang.Math.exp(arg);
        } else {
    
    	/* use a continued fraction expansion */
    
/*!* 	arg = p * log(x) - x - lgammafn(p); *!*/
    	arg = p * java.lang.Math.log(x) - x - misc.lgammafn(p);
    	a = one - p;
    	b = a + x + one;
    	c = zero;
    	pn1 = one;
    	pn2 = x;
    	pn3 = x + one;
    	pn4 = x * b;
    	sum = pn3 / pn4;
    	for (;;) {
    	    a = a + one;
    	    b = b + two;
    	    c = c + one;
    	    an = a * c;
    	    pn5 = b * pn3 - an * pn1;
    	    pn6 = b * pn4 - an * pn2;
/*!* 	    if (fabs(pn6) > zero) { *!*/
    	    if (java.lang.Math.abs(pn6) > zero) {
    		rn = pn5 / pn6;
/*!* 		if (fabs(sum - rn) <= fmin2(Constants.DBL_EPSILON, Constants.DBL_EPSILON * rn)) *!*/
    		if (java.lang.Math.abs(sum - rn) <= Math.min(Constants.DBL_EPSILON, Constants.DBL_EPSILON * rn))
    		    break;
    		sum = rn;
    	    }
    	    pn1 = pn3;
    	    pn2 = pn4;
    	    pn3 = pn5;
    	    pn4 = pn6;
/*!* 	    if (fabs(pn5) >= oflo) { *!*/
    	    if (java.lang.Math.abs(pn5) >= oflo) {
    
                    /* re-scale the terms in continued fraction */
    		/* if they are large */
    
    		pn1 = pn1 / oflo;
    		pn2 = pn2 / oflo;
    		pn3 = pn3 / oflo;
    		pn4 = pn4 / oflo;
    	    }
    	}
/*!* 	arg = arg + log(sum); *!*/
    	arg = arg + java.lang.Math.log(sum);
    	sum = one;
    	if (arg >= elimit)
/*!* 	    sum = one - exp(arg); *!*/
    	    sum = one - java.lang.Math.exp(arg);
        }
        return sum;
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
     *    double quantile(double p, double shape, double scale);
     *
     *  DESCRIPTION
     *
     *    Compute the quantile function of the gamma distribution.
     *
     *  NOTES
     *
     *    This function is based on the Applied Statistics
     *    Algorithm AS 91 and AS 239.
     *
     *  REFERENCES
     *
     *    Best, D. J. and D. E. Roberts (1975).
     *    Percentage Points of the Chi-Squared Disribution.
     *    Applied Statistics 24, page 385.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    static private double  C7 = 4.67; 
    static private double  C8 = 6.66; 
    static private double  C9 = 6.73; 
    static private double  C10 = 13.32; 
    
    static private double  C11 = 60; 
    static private double  C12 = 70; 
    static private double  C13 = 84; 
    static private double  C14 = 105; 
    static private double  C15 = 120; 
    static private double  C16 = 127; 
    static private double  C17 = 140; 
    static private double  C18 = 1175; 
    static private double  C19 = 210; 
    
    static private double  C20 = 252; 
    static private double  C21 = 2264; 
    static private double  C22 = 294; 
    static private double  C23 = 346; 
    static private double  C24 = 420; 
    static private double  C25 = 462; 
    static private double  C26 = 606; 
    static private double  C27 = 672; 
    static private double  C28 = 707; 
    static private double  C29 = 735; 
    
    static private double  C30 = 889; 
    static private double  C31 = 932; 
    static private double  C32 = 966; 
    static private double  C33 = 1141; 
    static private double  C34 = 1182; 
    static private double  C35 = 1278; 
    static private double  C36 = 1740; 
    static private double  C37 = 2520; 
    static private double  C38 = 5040; 
    
    static private double  EPS0 = 5e-7/* originally: IDENTICAL to EPS2; not clear why */; 
    static private double  EPS1 = 1e-2; 
    static private double  EPS2 = 5e-7; 
    static private double  MAXIT = 20; 
    
    static private double  pMIN = 0.000002; 
    static private double  pMAX = 0.999998; 
    
    public static double  quantile(double p, double alpha, double scale)
    {
        double a, b, c, ch, g, p1, v;
        double p2, q, s1, s2, s3, s4, s5, s6, t=0.0, x;
        int i;
    
        /* test arguments and initialise */
    
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(p) || Double.isNaN(alpha) || Double.isNaN(scale))
    	return p + alpha + scale;
    /*!* #endif /*4!*/
    
        if (p < 0 || p > 1 || alpha <= 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (/* 0 <= */ p < pMIN) return 0;
        if (/* 1 >= */ p > pMAX) return Double.POSITIVE_INFINITY;
    
        v = 2*alpha;
    
        c = alpha-1;
/*!*     g = lgammafn(alpha);!!!COMMENT!!! *!*/
        g = misc.lgammafn(alpha);/* log Gamma(v/2) */
    
/*!*     if(v < (-1.24)*log(p)) { *!*/
        if(v < (-1.24)*java.lang.Math.log(p)) {
          /* starting approximation for small chi-squared */
    
/*!* 	ch = pow(p*alpha*exp(g+alpha*Constants.M_LN_2), 1/alpha); *!*/
    	ch = java.lang.Math.pow(p*alpha*java.lang.Math.exp(g+alpha*Constants.M_LN_2), 1/alpha);
    	if(ch < EPS0) {
    	    throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	    //    	    return Double.NaN;
    	}
    
        } else if(v > 0.32) {
    
    	/* starting approximation using Wilson and Hilferty estimate */
    
    	x = normal.quantile(p, 0, 1);
    	p1 = 0.222222/v;
/*!* 	ch = v*pow(x*sqrt(p1)+1-p1, 3); *!*/
    	ch = v*java.lang.Math.pow(x*java.lang.Math.sqrt(p1)+1-p1, 3);
    
    	/* starting approximation for p tending to 1 */
    
    	if( ch > 2.2*v + 6 )
/*!* 	    ch = -2*(log(1-p) - c*log(0.5*ch) + g); *!*/
    	    ch = -2*(java.lang.Math.log(1-p) - c*java.lang.Math.log(0.5*ch) + g);
    
        } else { /* starting approximation for v <= 0.32 */
    
    	ch = 0.4;
/*!* 	a = log(1-p) + g + c*Constants.M_LN_2; *!*/
    	a = java.lang.Math.log(1-p) + g + c*Constants.M_LN_2;
    	do {
    	    q = ch;
    	    p1 = 1+ch*(C7+ch);
    	    p2 = ch*(C9+ch*(C8+ch));
    	    t = -0.5 +(C7+2*ch)/p1 - (C9+ch*(C10+3*ch))/p2;
/*!* 	    ch -= (1- exp(a+0.5*ch)*p2/p1)/t; *!*/
    	    ch -= (1- java.lang.Math.exp(a+0.5*ch)*p2/p1)/t;
/*!* 	} while(fabs(q/ch - 1) > EPS1); *!*/
    	} while(java.lang.Math.abs(q/ch - 1) > EPS1);
        }
    
        /* algorithm AS 239 and calculation of seven term taylor series */
    
        for( i=1 ; i <= MAXIT ; i++ ) {
    	q = ch;
    	p1 = 0.5*ch;
    	p2 = p - cumulative(p1, alpha, 1);
    /*!* #ifdef IEEE_754 /*4!*/
    	if(Double.isInfinite(p2))
    /*!* #else /*4!*/
	    //    	if((!!!!fixme!!!!) != 0)
    /*!* #endif /*4!*/
	    //    		return Double.NaN;
    
/*!* 	t = p2*exp(alpha*Constants.M_LN_2+g+p1-c*log(ch)); *!*/
    	t = p2*java.lang.Math.exp(alpha*Constants.M_LN_2+g+p1-c*java.lang.Math.log(ch));
    	b = t/ch;
    	a = 0.5*t-b*c;
    	s1 = (C19+a*(C17+a*(C14+a*(C13+a*(C12+C11*a)))))/C24;
    	s2 = (C24+a*(C29+a*(C32+a*(C33+C35*a))))/C37;
    	s3 = (C19+a*(C25+a*(C28+C31*a)))/C37;
    	s4 = (C20+a*(C27+C34*a)+c*(C22+a*(C30+C36*a)))/C38;
    	s5 = (C13+C21*a+c*(C18+C26*a))/C37;
    	s6 = (C15+c*(C23+C16*c))/C38;
    	ch = ch+t*(1+0.5*t*s1-b*c*(s1-b*(s2-b*(s3-b*(s4-b*(s5-b*s6))))));
/*!* 	if(fabs(q/ch-1) > EPS2) *!*/
    	if(java.lang.Math.abs(q/ch-1) > EPS2)
    	    return 0.5*scale*ch;
        }
        throw new java.lang.ArithmeticException("Math Error: PRECISION");
	//        return 0.5*scale*ch;
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
     *    double random(double a, double scale);
     *
     *  DESCRIPTION
     *
     *    Random variates from the gamma distribution.
     *
     *  REFERENCES
     *
     *    [1] Shape parameter a >= 1.  Algorithm GD in:
     *
     *	  Ahrens, J.H. and Dieter, U. (1982).
     *	  Generating gamma variates by a modified
     *	  rejection technique.
     *	  Comm. ACM, 25, 47-54.
     *
     *
     *    [2] Shape parameter 0 < a < 1. Algorithm GS in:
     *
     *        Ahrens, J.H. and Dieter, U. (1974).
     *	  Computer methods for sampling from gamma, beta,
     *	  poisson and binomial distributions.
     *	  Computing, 12, 223-246.
     *
     *    Input: a = parameter (mean) of the standard gamma distribution.
     *    Output: a variate from the gamma(a)-distribution
     *
     *    Coefficients q(k) - for q0 = sum(q(k)*a**(-k))
     *    Coefficients a(k) - for q = q0+(t*t/2)*sum(a(k)*v**k)
     *    Coefficients e(k) - for exp(q)-1 = sum(e(k)*q**k)
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    static private double a1 = 0.3333333;
    static private double a2 = -0.250003;
    static private double a3 = 0.2000062;
    static private double a4 = -0.1662921;
    static private double a5 = 0.1423657;
    static private double a6 = -0.1367177;
    static private double a7 = 0.1233795;
    static private double e1 = 1.0;
    static private double e2 = 0.4999897;
    static private double e3 = 0.166829;
    static private double e4 = 0.0407753;
    static private double e5 = 0.010293;
    static private double q1 = 0.04166669;
    static private double q2 = 0.02083148;
    static private double q3 = 0.00801191;
    static private double q4 = 0.00144121;
    static private double q5 = -7.388e-5;
    static private double q6 = 2.4511e-4;
    static private double q7 = 2.424e-4;
    static private double sqrt32 = 5.656854;
    
    static private double aa = 0.;
    static private double aaa = 0.;
    
    static private double b, c, d, e, p, q, r, s, t, u, v, w, x;
    static private double q0, s2, si;
      
    
    public static double  random(double a, double scale, uniform PRNG)
    {
    	double ret_val;
    
    	if (a < 1.0) {
    		/* alternate method for parameters a below 1 */
    		/* 0.36787944117144232159 = exp(-1) */
    		aa = 0.0;
    		b = 1.0 + 0.36787944117144232159 * a;
    		while(true) {
    			p = b * PRNG.random();
    			if (p >= 1.0) {
/*!* 				ret_val = -log((b - p) / a); *!*/
    				ret_val = -java.lang.Math.log((b - p) / a);
/*!* 				if (exponential.random!!!COMMENT!!!() >= (1.0 - a) * log(ret_val)) *!*/
    				if (exponential.random(PRNG) >= (1.0 - a) * java.lang.Math.log(ret_val))
    					break;
    			} else {
/*!* 				ret_val = exp(log(p) / a); *!*/
    				ret_val = java.lang.Math.exp(java.lang.Math.log(p) / a);
    				if (exponential.random(PRNG) >= ret_val)
    					break;
    			}
    		}
    		return scale * ret_val;
    	}
    	/* Step 1: Recalculations of s2, s, d if a has changed */
    	if (a != aa) {
    		aa = a;
    		s2 = a - 0.5;
/*!* 		s = sqrt(s2); *!*/
    		s = java.lang.Math.sqrt(s2);
    		d = sqrt32 - s * 12.0;
    	}
    	/* Step 2: t = standard normal deviate, */
    	/* x = (s,1/2)-normal deviate. */
    	/* immediate acceptance (i) */
    
    	t = normal.random(PRNG);
    	x = s + 0.5 * t;
    	ret_val = x * x;
    	if (t >= 0.0)
    		return scale * ret_val;
    
    	/* Step 3: u = 0,1 - uniform sample. squeeze acceptance (s) */
    	u = PRNG.random();
    	if (d * u <= t * t * t) {
    		return scale * ret_val;
    	}
    	/* Step 4: recalculations of q0, b, si, c if necessary */
    
    	if (a != aaa) {
    		aaa = a;
    		r = 1.0 / a;
    		q0 = ((((((q7 * r + q6) * r + q5) * r + q4)
    			* r + q3) * r + q2) * r + q1) * r;
    
    		/* Approximation depending on size of parameter a */
    		/* The constants in the expressions for b, si and */
    		/* c were established by numerical experiments */
    
    		if (a <= 3.686) {
    			b = 0.463 + s + 0.178 * s2;
    			si = 1.235;
    			c = 0.195 / s - 0.079 + 0.16 * s;
    		} else if (a <= 13.022) {
    			b = 1.654 + 0.0076 * s2;
    			si = 1.68 / s + 0.275;
    			c = 0.062 / s + 0.024;
    		} else {
    			b = 1.77;
    			si = 0.75;
    			c = 0.1515 / s;
    		}
    	}
    	/* Step 5: no quotient test if x not positive */
    
    	if (x > 0.0) {
    		/* Step 6: calculation of v and quotient q */
    		v = t / (s + s);
/*!* 		if (fabs(v) <= 0.25) *!*/
    		if (java.lang.Math.abs(v) <= 0.25)
    			q = q0 + 0.5 * t * t * ((((((a7 * v + a6)
    					    * v + a5) * v + a4) * v + a3)
    						 * v + a2) * v + a1) * v;
    		else
    			q = q0 - s * t + 0.25 * t * t + (s2 + s2)
/*!* 			    * log(1.0 + v); *!*/
    			    * java.lang.Math.log(1.0 + v);
    
    
    		/* Step 7: quotient acceptance (q) */
    
/*!* 		if (log(1.0 - u) <= q) *!*/
    		if (java.lang.Math.log(1.0 - u) <= q)
    			return scale * ret_val;
    	}
    	/* Step 8: e = standard exponential deviate */
    	/* u= 0,1 -uniform deviate */
    	/* t=(b,si)-double exponential (laplace) sample */
    
    	while(true) {
    		e = exponential.random(PRNG);
    		u = PRNG.random();
    		u = u + u - 1.0;
    		if (u < 0.0)
    			t = b - si * e;
    		else
    			t = b + si * e;
    		/* Step  9:  rejection if t < tau(1) = -0.71874483771719 */
    		if (t >= -0.71874483771719) {
    			/* Step 10:  calculation of v and quotient q */
    			v = t / (s + s);
/*!* 			if (fabs(v) <= 0.25) *!*/
    			if (java.lang.Math.abs(v) <= 0.25)
    				q = q0 + 0.5 * t * t * ((((((a7 * v + a6)
    					    * v + a5) * v + a4) * v + a3)
    						 * v + a2) * v + a1) * v;
    			else
    				q = q0 - s * t + 0.25 * t * t + (s2 + s2)
/*!* 				    * log(1.0 + v); *!*/
    				    * java.lang.Math.log(1.0 + v);
    			/* Step 11:  hat acceptance (h) */
    			/* (if q not positive go to step 8) */
    			if (q > 0.0) {
    				if (q <= 0.5)
    					w = ((((e5 * q + e4) * q + e3)
    					      * q + e2) * q + e1) * q;
    				else
/*!* 					w = exp(q) - 1.0; *!*/
    					w = java.lang.Math.exp(q) - 1.0;
    				/* if t is rejected */
    				/* sample again at step 8 */
/*!* 				if (c * fabs(u) <= w * exp(e - 0.5 * t * t)) *!*/
    				if (c * java.lang.Math.abs(u) <= w * java.lang.Math.exp(e - 0.5 * t * t))
    					break;
    			}
    		}
    	}
    	x = s + 0.5 * t;
    	return scale * x * x;
    }
  }
