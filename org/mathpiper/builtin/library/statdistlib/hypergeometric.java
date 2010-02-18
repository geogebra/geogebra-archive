package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class hypergeometric
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
     *    double density(double x, double NR, double NB, double n);
     *
     *  DESCRIPTION
     *
     *    The density of the hypergeometric distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  density(double x, double NR, double NB, double n)
    {
        double N;
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(NR) || Double.isNaN(NB) || Double.isNaN(n))
    	return x + NR + NB + n;
    /*!* #endif /*4!*/
/*!*     x = floor(x + 0.5); *!*/
        x = java.lang.Math.floor(x + 0.5);
/*!*     NR = floor(NR + 0.5); *!*/
        NR = java.lang.Math.floor(NR + 0.5);
/*!*     NB = floor(NB + 0.5); *!*/
        NB = java.lang.Math.floor(NB + 0.5);
        N = NR + NB;
/*!*     n = floor(n + 0.5); *!*/
        n = java.lang.Math.floor(n + 0.5);
        if (NR < 0 || NB < 0 || n < 0 || n > N) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
/*!*     if (x < fmax2(0, n - NB) || x > fmin2(n, NR)) *!*/
        if (x < Math.max(0, n - NB) || x > Math.min(n, NR))
    	return 0;
/*!*     return exp(lfastchoose(NR, x) + lfastchoose(NB, n - x) *!*/
        return java.lang.Math.exp(misc.lfastchoose(NR, x) + misc.lfastchoose(NB, n - x)
/*!* 	       - lfastchoose(N, n)); *!*/
    	       - misc.lfastchoose(N, n));
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
     *    double cumulative(double x, double NR, double NB, double n);
     *
     *  DESCRIPTION
     *
     *    The distribution function of the hypergeometric distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  cumulative(double x, double NR, double NB, double n)
    {
        double N, xstart, xend, xr, xb, sum, term;
    
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isNaN(x) || Double.isNaN(NR) || Double.isNaN(NB) || Double.isNaN(n))
    	return x + NR + NB + n;
        if(Double.isInfinite(x) || Double.isInfinite(NR) || Double.isInfinite(NB) || Double.isInfinite(n)) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    /*!* #endif /*4!*/
    
/*!*     x = floor(x); *!*/
        x = java.lang.Math.floor(x);
/*!*     NR = floor(NR + 0.5); *!*/
        NR = java.lang.Math.floor(NR + 0.5);
/*!*     NB = floor(NB + 0.5); *!*/
        NB = java.lang.Math.floor(NB + 0.5);
        N = NR + NB;
/*!*     n = floor(n + 0.5); *!*/
        n = java.lang.Math.floor(n + 0.5);
        if (NR < 0 || NB < 0 || n < 0 || n > N) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
/*!*     xstart = fmax2(0, n - NB); *!*/
        xstart = Math.max(0, n - NB);
/*!*     xend = fmin2(n, NR); *!*/
        xend = Math.min(n, NR);
        if(x < xstart) return 0.0;
        if(x >= xend) return 1.0;
        xr = xstart;
        xb = n - xr;
/*!*     term = exp(lfastchoose(NR, xr) + lfastchoose(NB, xb) *!*/
        term = java.lang.Math.exp(misc.lfastchoose(NR, xr) + misc.lfastchoose(NB, xb)
/*!* 	       - lfastchoose(N, n)); *!*/
    	       - misc.lfastchoose(N, n));
        NR = NR - xr;
        NB = NB - xb;
        sum = 0.0;
        while(xr <= x) {
    	sum += term;
    	xr++;
    	NB++;
    	term *= (NR / xr) * (xb / NB);
    	xb--;
    	NR--;
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
     *    double density(double x, double NR, double NB, double n);
     *
     *  DESCRIPTION
     *
     *    The quantile function of the hypergeometric distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  quantile(double x, double NR, double NB, double n)
    {
        double N, xstart, xend, xr, xb, sum, term;
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(NR) || Double.isNaN(NB) || Double.isNaN(n))
    	return x + NR + NB + n;
        if(Double.isInfinite(x) || Double.isInfinite(NR) || Double.isInfinite(NB) || Double.isInfinite(n)) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    /*!* #endif /*4!*/
/*!*     NR = floor(NR + 0.5); *!*/
        NR = java.lang.Math.floor(NR + 0.5);
/*!*     NB = floor(NB + 0.5); *!*/
        NB = java.lang.Math.floor(NB + 0.5);
        N = NR + NB;
/*!*     n = floor(n + 0.5); *!*/
        n = java.lang.Math.floor(n + 0.5);
        if (x < 0 || x > 1 || NR < 0 || NR < 0 || n < 0 || n > N) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
/*!*     xstart = fmax2(0, n - NB); *!*/
        xstart = Math.max(0, n - NB);
/*!*     xend = fmin2(n, NR); *!*/
        xend = Math.min(n, NR);
        if(x <= 0) return xstart;
        if(x >= 1) return xend;
        xr = xstart;
        xb = n - xr;
/*!*     term = exp(lfastchoose(NR, xr) + lfastchoose(NB, xb) *!*/
        term = java.lang.Math.exp(misc.lfastchoose(NR, xr) + misc.lfastchoose(NB, xb)
/*!* 	       - lfastchoose(N, n)); *!*/
    	       - misc.lfastchoose(N, n));
        NR = NR - xr;
        NB = NB - xb;
        sum = term;
        while(sum < x && xr < xend) {
    	xr++;
    	NB++;
    	term *= (NR / xr) * (xb / NB);
    	sum += term;
    	xb--;
    	NR--;
        }
        return xr;
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
     *    double random(double NR, double NB, double n);
     *
     *  DESCRIPTION
     *
     *    Random variates from the hypergeometric distribution.
     *    Returns the number of white balls drawn when kk balls
     *    are drawn at random from an urn containing nn1 white
     *    and nn2 black balls.
     *
     *  REFERENCE
     *
     *    V. Kachitvichyanukul and B. Schmeiser (1985).
     *    ``Computer generation of hypergeometric random variates,''
     *    Journal of Statistical Computation and Simulation 22, 127-145.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    /* afc(i) :=  ln( i! )	[logarithm of the factorial i.
     *	   If (i > 7), use Stirling's approximation, otherwise use table lookup.
    */
    
    static private double al[] =
    {
        0.0,
        0.0,/*ln(0!)=ln(1)*/
        0.0,/*ln(1!)=ln(1)*/
        0.69314718055994530941723212145817,/*ln(2) */
        1.79175946922805500081247735838070,/*ln(6) */
        3.17805383034794561964694160129705,/*ln(24)*/
        4.78749174278204599424770093452324,
        6.57925121201010099506017829290394,
        8.52516136106541430016553103634712
        /*, 10.60460290274525022841722740072165*/
    };
    
    static private double afc(int i)
    {
        double di, value;
        if (i < 0) {
          System.out.println("rhyper.c: afc(i)+ i=%d < 0 -- SHOULD NOT HAPPEN!\n"+i);
          return -1;/* unreached (Wall) */
        } else if (i <= 7) {
    	value = al[i + 1];
        } else {
    	di = i;
/*!* 	value = (di + 0.5) * log(di) - di + 0.08333333333333 / di *!*/
    	value = (di + 0.5) * java.lang.Math.log(di) - di + 0.08333333333333 / di
    	    - 0.00277777777777 / di / di / di + 0.9189385332;
        }
        return value;
    }
    

      static private int ks = -1;
      static private int n1s = -1;
      static private int n2s = -1;
      static private double con = 57.56462733;
      static private double deltal = 0.0078;
      static private double deltau = 0.0034;
      static private double scale = 1e25;
      
      static private double a;
      static private double d, e, f, g;
      static private int i, k, m;
      static private double p;
      static private double r, s, t;
      static private double u, v, w;
      static private double lamdl, y, lamdr;
      static private int minjx, maxjx, n1, n2;
      static private double p1, p2, p3, y1, de, dg;
      static private boolean setup1, setup2;
      static private double gl, kl, ub, nk, dr, nm, gu, kr, ds, dt;
      static private int ix;
      static private double tn;
      static private double xl;
      static private double ym, yn, yk, xm;
      static private double xr;
      static private double xn;
      static private boolean reject;
      static private double xk;
      /* extern double afc(int); */
      static private double alv;
    

    public static double  random(double nn1in, double nn2in, double kkin,
				 uniform PRNG)
    {
        int nn1, nn2, kk;
    
        /* check parameter validity */
    
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isInfinite(nn1in) || Double.isInfinite(nn2in) || Double.isInfinite(kkin)) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    /*!* #endif /*4!*/
    
/*!*     nn1 = floor(nn1in+0.5); *!*/
        nn1 = (int) java.lang.Math.floor(nn1in+0.5);
/*!*     nn2 = floor(nn2in+0.5); *!*/
        nn2 = (int) java.lang.Math.floor(nn2in+0.5);
/*!*     kk = floor(kkin+0.5); *!*/
        kk = (int) java.lang.Math.floor(kkin+0.5);
    
        if (nn1 < 0 || nn2 < 0 || kk < 0 || kk > nn1 + nn2) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//  	return Double.NaN;
        }
        /* if new parameter values, initialize */
    
        reject = true;
        setup1 = false;
        setup2 = false;
        if (nn1 != n1s || nn2 != n2s) {
    	setup1 = true;
    	setup2 = true;
        } else if (kk != ks) {
    	setup2 = true;
        }
        if (setup1) {
    	n1s = nn1;
    	n2s = nn2;
    	tn = nn1 + nn2;
    	if (nn1 <= nn2) {
    	    n1 = nn1;
    	    n2 = nn2;
    	} else {
    	    n1 = nn2;
    	    n2 = nn1;
    	}
        }
        if (setup2) {
    	ks = kk;
    	if (kk + kk >= tn) {
    	    k = (int) (tn) - kk;
    	} else {
    	    k = kk;
    	}
        }
        if (setup1 || setup2) {
    	m = (int) ((k + 1.0) * (n1 + 1.0) / (tn + 2.0));
/*!* 	minjx = imax2(0, k - n2); *!*/
    	minjx = Math.max(0, k - n2);
/*!* 	maxjx = Math.min(n1, k); *!*/
    	maxjx = Math.min(n1, k);
        }
        /* generate random variate */
    
        if (minjx == maxjx) {
    	/* degenerate distribution */
    	ix = maxjx;
    	/* return ix;
    	   No, need to unmangle <TSL>*/
    	/* return appropriate variate */
    
    	if (kk + kk >= tn) {
	    if (nn1 > nn2) {
		ix = kk - nn2 + ix;
	    } else {
		ix = nn1 - ix;
	    }
    	} else {
	    if (nn1 > nn2)
		ix = kk - ix;
    	}
    	return ix;
	
        } else if (m - minjx < 10) {
	    /* inverse transformation */
	    if (setup1 || setup2) {
		if (k < n2) {
		    /*!* 		w = exp(con + afc(n2) + afc(n1 + n2 - k) *!*/
		    w = java.lang.Math.exp(con + afc(n2) + afc(n1 + n2 - k)
					   - afc(n2 - k) - afc(n1 + n2));
		} else {
		    /*!* 		w = exp(con + afc(n1) + afc(k) *!*/
		    w = java.lang.Math.exp(con + afc(n1) + afc(k)
					   - afc(k - n2) - afc(n1 + n2));
		}
	    }
	L10: while(true) {
	    p = w;
	    ix = minjx;
	    u = PRNG.random() * scale;
	L20: while(true) {
	    if (u > p) {
		u = u - p;
		p = p * (n1 - ix) * (k - ix);
		ix = ix + 1;
		p = p / ix / (n2 - k + ix);
		if (ix > maxjx)
		    continue L10;
		continue L20;
	    }
	    break L10;
	}}
        } else {
	    /* h2pe */
    
	    if (setup1 || setup2) {
		/*!* 	    s = sqrt((tn - k) * k * n1 * n2 / (tn - 1) / tn / tn); *!*/
		s = java.lang.Math.sqrt((tn - k) * k * n1 * n2 / (tn - 1) / tn / tn);
		
		/* remark: d is defined in reference without int. */
    	    /* the truncation centers the cell boundaries at 0.5 */
    
    	    d = (int) (1.5 * s) + .5;
    	    xl = m - d + .5;
    	    xr = m + d + .5;
    	    a = afc(m) + afc(n1 - m) + afc(k - m)
    		+ afc(n2 - k + m);
/*!* 	    kl = exp(a - afc((int) (xl)) - afc((int) (n1 - xl)) *!*/
    	    kl = java.lang.Math.exp(a - afc((int) (xl)) - afc((int) (n1 - xl))
    		     - afc((int) (k - xl))
    		     - afc((int) (n2 - k + xl)));
/*!* 	    kr = exp(a - afc((int) (xr - 1)) *!*/
    	    kr = java.lang.Math.exp(a - afc((int) (xr - 1))
    		     - afc((int) (n1 - xr + 1))
    		     - afc((int) (k - xr + 1))
    		     - afc((int) (n2 - k + xr - 1)));
/*!* 	    lamdl = -log(xl * (n2 - k + xl) / (n1 - xl + 1) *!*/
    	    lamdl = -java.lang.Math.log(xl * (n2 - k + xl) / (n1 - xl + 1)
    			 / (k - xl + 1));
/*!* 	    lamdr = -log((n1 - xr + 1) * (k - xr + 1) *!*/
    	    lamdr = -java.lang.Math.log((n1 - xr + 1) * (k - xr + 1)
    			 / xr / (n2 - k + xr));
    	    p1 = d + d;
    	    p2 = p1 + kl / lamdl;
    	    p3 = p2 + kr / lamdr;
    	}
	L30: while(true) {
    	u = PRNG.random() * p3;
    	v = PRNG.random();
    	if (u < p1) {
    	    /* rectangular region */
    	    ix = (int) (xl + u);
    	} else if (u <= p2) {
    	    /* left tail */
/*!* 	    ix = xl + log(v) / lamdl; *!*/
    	    ix = (int) (xl + java.lang.Math.log(v) / lamdl);
    	    if (ix < minjx)
    		continue L30;
    	    v = v * (u - p1) * lamdl;
    	} else {
    	    /* right tail */
/*!* 	    ix = xr - log(v) / lamdr; *!*/
    	    ix = (int) (xr - java.lang.Math.log(v) / lamdr);
    	    if (ix > maxjx)
    		continue L30;
    	    v = v * (u - p2) * lamdr;
    	}
	
    	/* acceptance/rejection test */
    
    	if (m < 100 || ix <= 50) {
    	    /* explicit evaluation */
    	    f = 1.0;
    	    if (m < ix) {
    		for (i = m + 1; i <= ix; i++)
    		    f = f * (n1 - i + 1) * (k - i + 1)
    			/ (n2 - k + i) / i;
    	    } else if (m > ix) {
    		for (i = ix + 1; i <= m; i++)
    		    f = f * i * (n2 - k + i) / (n1 - i)
    			/ (k - i);
    	    }
    	    if (v <= f) {
    		reject = false;
    	    }
    	} else {
    	    /* squeeze using upper and lower bounds */
    	    y = ix;
    	    y1 = y + 1.0;
    	    ym = y - m;
    	    yn = n1 - y + 1.0;
    	    yk = k - y + 1.0;
    	    nk = n2 - k + y1;
    	    r = -ym / y1;
    	    s = ym / yn;
    	    t = ym / yk;
    	    e = -ym / nk;
    	    g = yn * yk / (y1 * nk) - 1.0;
    	    dg = 1.0;
    	    if (g < 0.0)
    		dg = 1.0 + g;
    	    gu = g * (1.0 + g * (-0.5 + g / 3.0));
    	    gl = gu - .25 * (g * g * g * g) / dg;
    	    xm = m + 0.5;
    	    xn = n1 - m + 0.5;
    	    xk = k - m + 0.5;
    	    nm = n2 - k + xm;
    	    ub = y * gu - m * gl + deltau
    		+ xm * r * (1. + r * (-0.5 + r / 3.0))
    		+ xn * s * (1. + s * (-0.5 + s / 3.0))
    		+ xk * t * (1. + t * (-0.5 + t / 3.0))
    		+ nm * e * (1. + e * (-0.5 + e / 3.0));
    	    /* test against upper bound */
/*!* 	    alv = log(v); *!*/
    	    alv = java.lang.Math.log(v);
    	    if (alv > ub) {
    		reject = true;
    	    } else {
    				/* test against lower bound */
    		dr = xm * (r * r * r * r);
    		if (r < 0.0)
    		    dr = dr / (1.0 + r);
    		ds = xn * (s * s * s * s);
    		if (s < 0.0)
    		    ds = ds / (1.0 + s);
    		dt = xk * (t * t * t * t);
    		if (t < 0.0)
    		    dt = dt / (1.0 + t);
    		de = nm * (e * e * e * e);
    		if (e < 0.0)
    		    de = de / (1.0 + e);
    		if (alv < ub - 0.25 * (dr + ds + dt + de)
    		    + (y + m) * (gl - gu) - deltal) {
    		    reject = false;
    		} else {
    		    /*
    		     * stirling's formula to machine
    		     * accuracy
    		     */
    		    if (alv <= (a - afc(ix) - afc(n1 - ix)
    				- afc(k - ix) - afc(n2 - k + ix))) {
    			reject = false;
    		    } else {
    			reject = true;
    		    }
    		}
    	    }
    	}
    	if (reject)
    	    continue L30;
	break L30;
        }
	}
        /* return appropriate variate */
    
        if (kk + kk >= tn) {
    	if (nn1 > nn2) {
    	    ix = kk - nn2 + ix;
    	} else {
    	    ix = nn1 - ix;
    	}
        } else {
    	if (nn1 > nn2)
    	    ix = kk - ix;
        }
        return ix;
    }
  }
