package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class binomial
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
     *    double density(double x, double n, double p)
     *
     *  DESCRIPTION
     *
     *    The density of the binomial distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  density(double x, double n, double p)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        /* NaNs propagated correctly */
        if (Double.isNaN(x) || Double.isNaN(n) || Double.isNaN(p)) return x + n + p;
    /*!* #endif /*4!*/
/*!*     n = floor(n + 0.5); *!*/
        n = java.lang.Math.floor(n + 0.5);
        if(n <= 0 || p < 0 || p > 1) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
/*!*     x = floor(x + 0.5); *!*/
        x = java.lang.Math.floor(x + 0.5);
        if (x < 0 || x > n)
    	return 0;
        if (p == 0)
    	return (x == 0) ? 1 : 0;
        if (p == 1)
    	return (x == n) ? 1 : 0;
/*!*     return exp(lfastchoose(n, x) + log(p) * x + (n - x) * log(1 - p)); *!*/
        return java.lang.Math.exp(misc.lfastchoose(n, x) + java.lang.Math.log(p) * x + (n - x) * java.lang.Math.log(1 - p));
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
     *    double cumulative(double x, double n, double p)
     *  
     *  DESCRIPTION
     *
     *    The distribution function of the binomial distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  cumulative(double x, double n, double p)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(n) || Double.isNaN(p))
    	return x + n + p;
        if (Double.isInfinite(n) || Double.isInfinite(p)) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    /*!* #endif /*4!*/
/*!*     n = floor(n + 0.5); *!*/
        n = java.lang.Math.floor(n + 0.5);
        if(n <= 0 || p < 0 || p > 1) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
/*!*     x = floor(x); *!*/
        x = java.lang.Math.floor(x);
        if (x < 0.0) return 0;
        if (n <= x) return 1;
        return beta.cumulative(1.0 - p, n - x, x + 1);
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
     *    The quantile function of the binomial distribution.
     *
     *  NOTES
     *
     *    The function uses the Cornish-Fisher Expansion to include
     *    a skewness correction to a normal approximation.  This gives
     *    an initial value which never seems to be off by more than
     *    1 or 2.  A search is then conducted of values close to
     *    this initial start point.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  quantile(double x, double n, double p)
    {
        double q, mu, sigma, gamma, z, y;
    
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(n) || Double.isNaN(p))
    	return x + n + p;
        if(Double.isInfinite(x) || Double.isInfinite(n) || Double.isInfinite(p)) {
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
        if (x == 0) return 0.0;
        if (x == 1) return n;
        q = 1 - p;
        mu = n * p;
/*!*     sigma = sqrt(n * p * q); *!*/
        sigma = java.lang.Math.sqrt(n * p * q);
        gamma = (q-p)/sigma;
        z = normal.quantile(x, 0.0, 1.0);
/*!*     y = floor(mu + sigma * (z + gamma * (z*z - 1) / 6) + 0.5); *!*/
        y = java.lang.Math.floor(mu + sigma * (z + gamma * (z*z - 1) / 6) + 0.5);
    
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
     *    double random(double nin, double pp)
     *    
     *  DESCRIPTION
     *
     *    Random variates from the binomial distribution.
     *
     *  REFERENCE
     *
     *    Kachitvichyanukul, V. and Schmeiser, B. W. (1988).
     *    Binomial random variate generation.
     *    Communications of the ACM 31, p216.
     *    (Algorithm BTPEC).
     */
    
    /*!* #include "DistLib.h" /*4!*/
    /*!* #include <stdlib.h> /*4!*/
    

    public static double  random(double nin, double pp, uniform PRNG)
    {
      double al=0.0, alv=0.0, amaxp=0.0, c=0.0, f=0.0, f1=0.0;
      double f2=0.0, ffm=0.0, fm=0.0, g=0.0;
      double p1=0.0, p2=0.0, p3=0.0, p4=0.0, qn=0.0, r=0.0;
      double u=0.0, v=0.0, w=0.0, w2=0.0;
      double x=0.0, x1=0.0, x2=0.0, xl=0.0, xll=0.0, xlr=0.0;
      double xm=0.0, xnp=0.0, xnpq=0.0, xr=0.0, ynorm=0.0, z=0.0, z2=0.0;
      int i=0, ix=0, ix1=0, k=0, m=0, mp=0, n=0;
      double p=0.0, q=0.0;
      double psave = -1.0;
      int nsave = -1;

    
/*!*     n = floor(nin + 0.5); *!*/
        n = (int) java.lang.Math.floor(nin + 0.5);
        /* n=0, p=0, p=1 are not errors <TSL>*/
        if (
    /*!* #ifdef IEEE_754 /*4!*/
    	Double.isInfinite(n) || Double.isInfinite(pp) ||
    /*!* #endif /*4!*/
    	n < 0.0 || pp < 0.0 || pp > 1.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (n==0.0 || pp==0) return 0;
        if (pp==1.0) return n;
    
        /* setup, perform only when parameters change */
    
    L30: {
    L20: {
    L10: {
        if (pp != psave) {
    	psave = pp;
/*!* 	p = fmin2(psave, 1.0 - psave); *!*/
    	p = Math.min(psave, 1.0 - psave);
    	q = 1.0 - p;
        } else if (n == nsave) {
    	if (xnp < 30.0)
    	    break L20;
    	break L10;
        }
        xnp = n * p;
        nsave = n;
        if (xnp < 30.0) {
    	/* inverse cdf logic for mean less than 30 */
/*!* 	qn = pow(q, (double) n); *!*/
    	qn = java.lang.Math.pow(q, (double) n);
    	r = p / q;
    	g = r * (n + 1);
    	break L20;
        } else {
    	ffm = xnp + p;
    	m = (int) ffm;
    	fm = m;
    	xnpq = xnp * q;
/*!* 	p1 = (int)(2.195 * sqrt(xnpq) - 4.6 * q) + 0.5; *!*/
    	p1 = (int)(2.195 * java.lang.Math.sqrt(xnpq) - 4.6 * q) + 0.5;
    	xm = fm + 0.5;
    	xl = xm - p1;
    	xr = xm + p1;
    	c = 0.134 + 20.5 / (15.3 + fm);
    	al = (ffm - xl) / (ffm - xl * p);
    	xll = al * (1.0 + 0.5 * al);
    	al = (xr - ffm) / (xr * q);
    	xlr = al * (1.0 + 0.5 * al);
    	p2 = p1 * (1.0 + c + c);
    	p3 = p2 + c / xll;
    	p4 = p3 + c / xlr;
        }
    } 
    // L10:
    while(true) {
	  u = PRNG.random() * p4;
          v = PRNG.random();
          /* triangular region */
          if (u <= p1) {
    	  ix = (int) (xm - p1 * v + u);
    	  break L30;
          }
          /* parallelogram region */
          if (u <= p2) {
    	  x = xl + (u - p1) / c;
/*!* 	  v = v * c + 1.0 - fabs(xm - x) / p1; *!*/
    	  v = v * c + 1.0 - java.lang.Math.abs(xm - x) / p1;
    	  if (v > 1.0 || v <= 0.)
    	      continue;
    	  ix = (int) x;
          } else {
    	  if (u > p3) {	/* right tail */
/*!* 	      ix = xr - log(v) / xlr; *!*/
    	      ix = (int)( xr - java.lang.Math.log(v) / xlr);
    	      if (ix > n)
    		  continue;
    	      v = v * (u - p3) * xlr;
    	  } else {/* left tail */
/*!* 	      ix = xl + log(v) / xll; *!*/
    	      ix = (int) (xl + java.lang.Math.log(v) / xll);
    	      if (ix < 0)
    		  continue;
    	      v = v * (u - p2) * xll;
    	  }
          }
          /* determine appropriate way to perform accept/reject test */
/*!*       k = abs(ix - m); *!*/
          k = java.lang.Math.abs(ix - m);
          if (k <= 20 || k >= xnpq / 2 - 1) {
    	  /* explicit evaluation */
    	  f = 1.0;
    	  r = p / q;
    	  g = (n + 1) * r;
    	  if (m < ix) {
    	      mp = m + 1;
    	      for (i = mp; i <= ix; i++)
    		  f = f * (g / i - r);
    	  } else if (m != ix) {
    	      ix1 = ix + 1;
    	      for (i = ix1; i <= m; i++)
    		  f = f / (g / i - r);
    	  }
    	  if (v <= f)
    	      break L30;
          } else {
    	  /* squeezing using upper and lower bounds */
    	  /* on log(f(x)) */
    	  amaxp = (k / xnpq) * ((k * (k / 3.0 + 0.625) + 0.1666666666666) / xnpq + 0.5);
    	  ynorm = -k * k / (2.0 * xnpq);
/*!* 	  alv = log(v); *!*/
    	  alv = java.lang.Math.log(v);
    	  if (alv < ynorm - amaxp)
    	      break L30;
    	  if (alv <= ynorm + amaxp) {
    				/* stirling's formula to machine accuracy */
    				/* for the final acceptance/rejection test */
    	      x1 = ix + 1;
    	      f1 = fm + 1.0;
    	      z = n + 1 - fm;
    	      w = n - ix + 1.0;
    	      z2 = z * z;
    	      x2 = x1 * x1;
    	      f2 = f1 * f1;
    	      w2 = w * w;
/*!* 	      if (alv <= xm * log(f1 / x1) + (n - m + 0.5) * log(z / w) + (ix - m) * log(w * p / x1 * q) + (13860.0 - (462.0 - (132.0 - (99.0 - 140.0 / f2) / f2) / f2) / f2) / f1 / 166320.0 + (13860.0 - (462.0 - (132.0 - (99.0 - 140.0 / z2) / z2) / z2) / z2) / z / 166320.0 + (13860.0 - (462.0 - (132.0 - (99.0 - 140.0 / x2) / x2) / x2) / x2) / x1 / 166320.0 + (13860.0 - (462.0 - (132.0 - (99.0 - 140.0 / w2) / w2) / w2) / w2) / w / 166320.) *!*/
    	      if (alv <= xm * java.lang.Math.log(f1 / x1) + (n - m + 0.5) * java.lang.Math.log(z / w) + (ix - m) * java.lang.Math.log(w * p / x1 * q) + (13860.0 - (462.0 - (132.0 - (99.0 - 140.0 / f2) / f2) / f2) / f2) / f1 / 166320.0 + (13860.0 - (462.0 - (132.0 - (99.0 - 140.0 / z2) / z2) / z2) / z2) / z / 166320.0 + (13860.0 - (462.0 - (132.0 - (99.0 - 140.0 / x2) / x2) / x2) / x2) / x1 / 166320.0 + (13860.0 - (462.0 - (132.0 - (99.0 - 140.0 / w2) / w2) / w2) / w2) / w / 166320.)
    		  break L30;
    	  }
          }
      }
    } 
    // L20:
    while(true) {
	ix = 0;
	f = qn;
	u = PRNG.random();
	while(true) {
	    if (u < f)
		break L30;
	    if (ix > 110)
		break;
	    u = u - f;
	    ix = ix + 1;
	    f = f * (g / ix - r);
	}
    }
	}
	// L30:
	if (psave > 0.5)
	    ix = n - ix;
	return (double)ix;
    }
}
