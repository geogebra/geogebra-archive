package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class beta 
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
     *    double density(double x, double a, double b);
     *
     *  DESCRIPTION
     *
     *    The density of the beta distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double density(double x, double a, double b)
    {
        double y;
    /*!* #ifdef IEEE_754 /*4!*/
        /* NaNs propagated correctly */
        if (Double.isNaN(x) || Double.isNaN(a) || Double.isNaN(b)) return x + a + b;
    /*!* #endif /*4!*/
        if (a <= 0.0 || b <= 0.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x < 0)
    	return 0;
        if (x > 1)
    	return 0;
        y = misc.beta(a, b);
/*!*     a = pow(x, a - 1);  *!*/
        a = java.lang.Math.pow(x, a - 1);
/*!*     b = pow(1.0 - x, b - 1.0);  *!*/
        b = java.lang.Math.pow(1.0 - x, b - 1.0);
    /*!* #ifndef IEEE_754 /*4!*/
	//        if(errno) return Double.NaN;
    /*!* #endif /*4!*/
        return a * b / y;
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
     *    double cumulative(double x, double pin, double qin);
     *
     *  DESCRIPTION
     *
     *    Returns distribution function of the beta distribution.
     *    (The incomplete beta ratio).
     *
     *  NOTES
     *
     *    This routine is a translation into C of a Fortran subroutine
     *    by W. Fullerton of Los Alamos Scientific Laboratory.
     *
     *  REFERENCE
     *
     *    Bosten and Battiste (1974).
     *    Remark on Algorithm 179,
     *    CACM 17, p153, (1974).
     */
    
    /*!* #include "DistLib.h" /*4!*/


    
    static double  pbeta_raw(double x, double pin, double qin)
    {
        double ans, c, finsum, p, ps, p1, q, term, xb, xi, y;
        int n, i, ib;
	double eps = 0;
	double alneps = 0;
	double sml = 0;
	double alnsml = 0;
    
        if (eps == 0) {
            eps = misc.d1mach(3);
/*!*         alneps = log(eps);  *!*/
            alneps = java.lang.Math.log(eps);
            sml = misc.d1mach(1);
/*!*         alnsml = log(sml);  *!*/
            alnsml = java.lang.Math.log(sml);
        }
    
        y = x;
        p = pin;
        q = qin;
    
        /* swap tails if x is greater than the mean */
    
        if (p / (p + q) < x) {
            y = 1 - y;
            p = qin;
            q = pin;
        }
    
        if ((p + q) * y / (p + 1) < eps) {
    
    	/* tail approximation */
    
            ans = 0;
/*!*         xb = p * log(Math.max(y, sml)) - log(p) - misc.lbeta(p, q);  *!*/
            xb = p * java.lang.Math.log(Math.max(y, sml)) - java.lang.Math.log(p) - misc.lbeta(p, q);
            if (xb > alnsml && y != 0)
/*!*             ans = exp(xb);  *!*/
                ans = java.lang.Math.exp(xb);
            if (y != x || p != pin)
                ans = 1 - ans;
        }
        else {
    
            /* evaluate the infinite sum first.  term will equal */
            /* y^p / beta(ps, p) * (1 - ps)-sub-i * y^i / fac(i) */
    
/*!*         ps = q - floor(q);  *!*/
            ps = q - java.lang.Math.floor(q);
            if (ps == 0)
                ps = 1;
/*!*         xb = p * log(y) - misc.lbeta(ps, p) - log(p);  *!*/
            xb = p * java.lang.Math.log(y) - misc.lbeta(ps, p) - java.lang.Math.log(p);
            ans = 0;
            if (xb >= alnsml) {
/*!*             ans = exp(xb);  *!*/
                ans = java.lang.Math.exp(xb);
                term = ans * p;
                if (ps != 1) {
                    n = (int) Math.max(alneps/java.lang.Math.log(y), 4.0);
    		for(i=1 ; i<= n ; i++) {
                        xi = i;
                        term = term * (xi - ps) * y / xi;
                        ans = ans + term / (p + xi);
                    }
                }
            }
    
            /* now evaluate the finite sum, maybe. */
    
            if (q > 1) {
/*!*             xb = p * log(y) + q * log(1 - y) - misc.lbeta(p, q) - log(q);  *!*/
                xb = p * java.lang.Math.log(y) + q * java.lang.Math.log(1 - y) - misc.lbeta(p, q) - java.lang.Math.log(q);
                ib = (int) Math.max(xb / alnsml, 0.0);
/*!*             term = exp(xb - ib * alnsml);  *!*/
                term = java.lang.Math.exp(xb - ib * alnsml);
                c = 1 / (1 - y);
                p1 = q * c / (p + q - 1);
    
                finsum = 0;
                n = (int) q;
                if (q == n)
                    n = n - 1;
    	    for(i=1 ; i<=n ; i++) {
                    if (p1 <= 1 && term / eps <= finsum)
                        break;
                    xi = i;
                    term = (q - xi + 1) * c * term / (p + q - xi);
                    if (term > 1) {
                        ib = ib - 1;
                        term = term * sml;
    		}
                    if (ib == 0)
                        finsum = finsum + term;
                }
                ans = ans + finsum;
            }
            if (y != x || p != pin)
                ans = 1 - ans;
            ans = Math.max(Math.min(ans, 1.0), 0.0);
        }
        return ans;
    }
    
    public static double cumulative(double x, double pin, double qin)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(pin) || Double.isNaN(qin))
    	return x + pin + qin;
    /*!* #endif /*4!*/
    
        if (pin <= 0 || qin <= 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x <= 0)
    	return 0;
        if (x >= 1)
    	return 1;
        return pbeta_raw(x, pin, qin);
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
     *
    
     * Reference:
     * Cran, G. W., K. J. Martin and G. E. Thomas (1977).
     *	Remark AS R19 and Algorithm AS 109,
     *	Applied Statistics, 26(1), 111-114.
     * Remark AS R83 (v.39, 309-310) and the correction (v.40(1) p.236)
     *	have been incorporated in this version.
     */
    
    
    /*!* #include "DistLib.h" /*4!*/
    
    static double  zero = 0.0; 
    
    /* set the exponent of accu to -2r-2 for r digits of accuracy */
    /*!* #ifdef OLD 
      static double  acu = 1.0e-32; 
      static double  lower = 0.0001; 
      static double  upper = 0.9999; 
    *4!*/
    /*!* #else/*---- NEW ---- -- still fails for p = 1e11, q=.5*/ /*4!*/
    
    static double  fpu = 3e-308; 
    /* acu_min:  Minimal value for accuracy 'acu' which will depend on (a,p);
    	     acu_min >= fpu ! */
    static double  acu_min = 1e-300; 
    static double  lower = fpu; 
    static double  upper = 1-2.22e-16; 
    
    /*!* #endif /*4!*/
    
    static double  const1 = 2.30753; 
    static double  const2 = 0.27061; 
    static double  const3 = 0.99229; 
    static double  const4 = 0.04481; 
    
    static volatile double xtrunc;
    
    public static double quantile(double alpha, double p, double q)
    {
    	int swap_tail, i_pb, i_inn;
    	double a, adj, logbeta, g, h, pp, prev, qq, r, s, t, tx, w, y, yprev;
    	double acu;
    	double xinbta;
    
    	/* define accuracy and initialize */
    
    	xinbta = alpha;
    
    	/* test for admissibility of parameters */
    
    /*!* #ifdef IEEE_754 /*4!*/
    	if (Double.isNaN(p) || Double.isNaN(q) || Double.isNaN(alpha))
    		return p + q + alpha;
    /*!* #endif /*4!*/
    	if(p < zero || q < zero || alpha < zero || alpha > 1) {
    		throw new java.lang.ArithmeticException("Math Error: DOMAIN");
		//    		return Double.NaN;
    	}
    	if (alpha == zero || alpha == 1)
    		return alpha;
    
    	logbeta = misc.lbeta(p, q);
    
    	/* change tail if necessary;  afterwards   0 < a <= 1/2	 */
    	if (alpha <= 0.5) {
    		a = alpha;	pp = p; qq = q; swap_tail = 0;
    	} else { /* change tail, swap  p <-> q :*/
    		a = 1 - alpha; pp = q; qq = p; swap_tail = 1;
    	}
    
    	/* calculate the initial approximation */
    
/*!* 	r = sqrt(-log(a * a));  *!*/
    	r = java.lang.Math.sqrt(-java.lang.Math.log(a * a));
    	y = r - (const1 + const2 * r) / (1 + (const3 + const4 * r) * r);
    	if (pp > 1 && qq > 1) {
    		r = (y * y - 3) / 6;
    		s = 1 / (pp + pp - 1);
    		t = 1 / (qq + qq - 1);
    		h = 2 / (s + t);
/*!* 		w = y * sqrt(h + r) / h - (t - s) * (r + 5 / 6 - 2 / (3 * h));  *!*/
    		w = y * java.lang.Math.sqrt(h + r) / h - (t - s) * (r + 5 / 6 - 2 / (3 * h));
/*!* 		xinbta = pp / (pp + qq * exp(w + w));  *!*/
    		xinbta = pp / (pp + qq * java.lang.Math.exp(w + w));
    	} else {
    		r = qq + qq;
    		t = 1 / (9 * qq);
/*!* 		t = r * pow(1 - t + y * sqrt(t), 3);  *!*/
    		t = r * java.lang.Math.pow(1 - t + y * java.lang.Math.sqrt(t), 3);
    		if (t <= zero)
/*!* 			xinbta = 1 - exp((log((1 - a) * qq) + logbeta) / qq);  *!*/
    			xinbta = 1 - java.lang.Math.exp((java.lang.Math.log((1 - a) * qq) + logbeta) / qq);
    		else {
    			t = (4 * pp + r - 2) / t;
    			if (t <= 1)
/*!* 				xinbta = exp((log(a * pp) + logbeta) / pp);  *!*/
    				xinbta = java.lang.Math.exp((java.lang.Math.log(a * pp) + logbeta) / pp);
    			else
    				xinbta = 1 - 2 / (t + 1);
    		}
    	}
    
    	/* solve for x by a modified newton-raphson method, */
    	/* using the function pbeta_raw */
    
    	r = 1 - pp;
    	t = 1 - qq;
    	yprev = zero;
    	adj = 1;
    	if (xinbta < lower)
    	  xinbta = lower;
    	else if (xinbta > upper)
    	  xinbta = upper;
    
    	/* Desired accuracy should depend on  (a,p)
    	 * This is from Remark .. on AS 109, adapted.
    	 * However, it's not clear if this is "optimal" for IEEE double prec.
    
    	 * acu = Math.max(acu_min, pow(10., -25. - 5./(pp * pp) - 1./(a * a)));
    
    	 * NEW: 'acu' accuracy NOT for squared adjustment, but simple;
    	 * ---- i.e.,  "new acu" = sqrt(old acu)
    
    	 */
    	acu = Math.max(acu_min, java.lang.Math.pow(10., -13 - 2.5/(pp * pp) - 0.5/(a * a)));
    	tx = prev = zero;	/* keep -Wall happy */
    
L_converged:	{
    	for (i_pb=0; i_pb < 1000; i_pb++) {
    		y = pbeta_raw(xinbta, pp, qq);
    		/* y = pbeta_raw2(xinbta, pp, qq, logbeta); */
    /*!* #ifdef IEEE_754 /*4!*/
    		if(Double.isInfinite(y))
    /*!* #else /*4!*/
		    //    		if (errno)
    /*!* #endif /*4!*/
		    //    		{ throw new java.lang.ArithmeticException("Math Error: DOMAIN"); return Double.NaN; }
    		y = (y - a) *
/*!* 			exp(logbeta + r * log(xinbta) + t * log(1 - xinbta));  *!*/
    			java.lang.Math.exp(logbeta + r * java.lang.Math.log(xinbta) + t * java.lang.Math.log(1 - xinbta));
    		if (y * yprev <= zero)
    			prev = Math.max(java.lang.Math.abs(adj),fpu);
    		g = 1;
    		for (i_inn=0; i_inn < 1000;i_inn++) {
    		  adj = g * y;
    		  if (java.lang.Math.abs(adj) < prev) {
    		    tx = xinbta - adj; /* trial new x */
    		    if (tx >= zero && tx <= 1) {
    		      if (prev <= acu)	  break L_converged;
    		      if (java.lang.Math.abs(y) <= acu) break L_converged;
    		      if (tx != zero && tx != 1)
    			break;
    		    }
    		  }
    		  g /= 3;
    		}
    		xtrunc = tx;	/* this prevents trouble with excess FPU */
    				/* precision on some machines. */
    		if (xtrunc == xinbta)
    			break L_converged;
    		xinbta = tx;
    		yprev = y;
    	}
    	/*-- NOT converged: Iteration count --*/
    	throw new java.lang.ArithmeticException("Math Error: PRECISION");
    }   

    	if (swap_tail==1)
    		xinbta = 1 - xinbta;
    	return xinbta;
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
    
    /* Reference:
     * R. C. H. Cheng (1978).
     * Generating beta variates with nonintegral shape parameters.
     * Communications of the ACM 21, 317-322.
     * (Algorithms BB and BC)
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
/*!* double random(double aa, double bb) *!*/
    public static double random(double aa, double bb, uniform PRNG)
    {
    	int qsame;
      
	double expmax = 0.0;
	double a=0.0, b=0.0, delta=0.0, r=0.0, s=0.0, t=0.0, u1=0.0;
	double u2=0.0, v=0.0, w=0.0, y=0.0, z=0.0;
	double alpha=0.0, beta=0.0, gamma=0.0, k1=0.0, k2=0.0;
	double olda = -1.0;
	double oldb = -1.0;

    
    
    	if (expmax == 0.0)
/*!* 		expmax = log(Double.MAX_VALUE);  *!*/
    		expmax = java.lang.Math.log(Double.MAX_VALUE);
    
    	/*!* qsame = (olda == aa) && (oldb == bb); *!*/
    	qsame = ( (olda == aa) && (oldb == bb) )?1:0;
    
    	if (!(qsame==1)) {
    		if (aa > 0.0 && bb > 0.0) {
    			olda = aa;
    			oldb = bb;
    		} else {
    			throw new java.lang.ArithmeticException("Math Error: DOMAIN");
			//    			return Double.NaN;
    		}
    	}

    deliver: {

    	if (Math.min(aa, bb) <= 1.0) {	/* Algorithm BC */
    		if (!(qsame==1)) {
    			a = Math.max(aa, bb);
    			b = Math.min(aa, bb);
    			alpha = a + b;
    			beta = 1.0 / b;
    			delta = 1.0 + a - b;
    			k1 = delta * (0.0138889 + 0.0416667 * b) /
    			    (a * beta - 0.777778);
    			k2 = 0.25 + (0.5 + 0.25 / delta) * b;
    		}
    		for(;;) {
 			u1 = PRNG.random();
 			u2 = PRNG.random();

    			if (u1 < 0.5) {
    				y = u1 * u2;
    				z = u1 * y;
    				if (0.25 * u2 + z - y >= k1)
    					continue;
    			} else {
    				z = u1 * u1 * u2;
    				if (z <= 0.25)
    					break;
    				if (z >= k2)
    					continue;
    			}
/*!* 			v = beta * log(u1 / (1.0 - u1));  *!*/
    			v = beta * java.lang.Math.log(u1 / (1.0 - u1));
    			if (v <= expmax)
/*!* 				w = a * exp(v);  *!*/
    				w = a * java.lang.Math.exp(v);
    			else
    				w = Double.MAX_VALUE;
/*!* 			if (alpha * (log(alpha / (b + w)) + v) - 1.3862944  *!*/
    			if (alpha * (java.lang.Math.log(alpha / (b + w)) + v) - 1.3862944
/*!* 			    >= log(z))  *!*/
    			    >= java.lang.Math.log(z))
    				break deliver;
    		}
/*!* 		v = beta * log(u1 / (1.0 - u1));  *!*/
    		v = beta * java.lang.Math.log(u1 / (1.0 - u1));
    		if (v <= expmax)
/*!* 			w = a * exp(v);  *!*/
    			w = a * java.lang.Math.exp(v);
    		else
    			w = Double.MAX_VALUE;
    	} else {		/* Algorithm BB */
    		if (!(qsame==1)) {
    			a = Math.min(aa, bb);
    			b = Math.max(aa, bb);
    			alpha = a + b;
/*!* 			beta = sqrt((alpha - 2.0) / (2.0 * a * b - alpha));  *!*/
    			beta = java.lang.Math.sqrt((alpha - 2.0) / (2.0 * a * b - alpha));
    			gamma = a + 1.0 / beta;
    		}
    		do {
/*!* 			u1 = PRNG.random();  *!*/
    			u1 = PRNG.random();
/*!* 			u2 = PRNG.random();  *!*/
    			u2 = PRNG.random();
/*!* 			v = beta * log(u1 / (1.0 - u1));  *!*/
    			v = beta * java.lang.Math.log(u1 / (1.0 - u1));
    			if (v <= expmax)
/*!* 				w = a * exp(v);  *!*/
    				w = a * java.lang.Math.exp(v);
    			else
    				w = Double.MAX_VALUE;
    			z = u1 * u1 * u2;
    			r = gamma * v - 1.3862944;
    			s = a + r - w;
    			if (s + 2.609438 >= 5.0 * z)
    				break;
/*!* 			t = log(z);  *!*/
    			t = java.lang.Math.log(z);
    			if (s > t)
    				break;
    		}
/*!* 		while (r + alpha * log(alpha / (b + w)) < t);  *!*/
    		while (r + alpha * java.lang.Math.log(alpha / (b + w)) < t);
    	}
    
	} // deliver:
    	return (aa != a) ? b / (b + w) : w / (b + w);
    }
  }
