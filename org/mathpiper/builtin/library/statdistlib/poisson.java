package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class poisson 
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
     *    double density(double x, double lambda)
     *
     *  DESCRIPTION
     *
     *    The density function of the Poisson distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  density(double x, double lambda)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isNaN(x) || Double.isNaN(lambda))
    	return x + lambda;
    /*!* #endif /*4!*/
/*!*     x = floor(x + 0.5); *!*/
        x = java.lang.Math.floor(x + 0.5);
        if(lambda <= 0.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x < 0)
    	return 0;
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isInfinite(x))
    	return 0;
    /*!* #endif /*4!*/
/*!*     return exp(x * log(lambda) - lambda - lgammafn(x + 1)); *!*/
        return java.lang.Math.exp(x * java.lang.Math.log(lambda) - lambda - misc.lgammafn(x + 1));
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
     *    double cumulative(double x, double lambda)
     *
     *  DESCRIPTION
     *
     *    The distribution function of the Poisson distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  cumulative(double x, double lambda)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(lambda))
    	return x + lambda;
    /*!* #endif /*4!*/
/*!*     x = floor(x + 0.5); *!*/
        x = java.lang.Math.floor(x + 0.5);
        if(lambda <= 0.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x < 0)
    	return 0;
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isInfinite(x))
    	return 1;
    /*!* #endif /*4!*/
        return  1 - gamma.cumulative(lambda, x + 1, 1.0);
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
     *    double quantile(double x, double lambda)
     *
     *  DESCRIPTION
     *
     *    The quantile function of the Poisson distribution.
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
    
    public static double  quantile(double x, double lambda)
    {
        double mu, sigma, gamma, z, y;
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(lambda))
    	return x + lambda;
        if(Double.isInfinite(lambda)) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    /*!* #endif /*4!*/
        if(x < 0 || x > 1 || lambda <= 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x == 0) return 0;
    /*!* #ifdef IEEE_754 /*4!*/
        if (x == 1) return Double.POSITIVE_INFINITY;
    /*!* #endif /*4!*/
        mu = lambda;
/*!*     sigma = sqrt(lambda); *!*/
        sigma = java.lang.Math.sqrt(lambda);
        gamma = sigma;
        z = normal.quantile(x, 0.0, 1.0);
/*!*     y = floor(mu + sigma * (z + gamma * (z * z - 1) / 6) + 0.5); *!*/
        y = java.lang.Math.floor(mu + sigma * (z + gamma * (z * z - 1) / 6) + 0.5);
        z = cumulative(y, lambda);
    
        if(z >= x) {
    
    	/* search to the left */
    
    	for(;;) {
    	    if((z = poisson.cumulative(y - 1, lambda)) < x)
    		return y;
    	    y = y - 1;
    	}
        }
        else {
    
    	/* search to the right */
    
    	for(;;) {
    	    if((z = poisson.cumulative(y + 1, lambda)) >= x)
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
     *    double density(double x, double lambda)
     *
     *  DESCRIPTION
     *
     *    Random variates from the Poisson distribution.
     *
     *  REFERENCE
     *
     *    Ahrens, J.H. and Dieter, U. (1982).
     *    Computer generation of Poisson deviates
     *    from modified normal distributions.
     *    ACM Trans. Math. Software 8, 163-179.
     */
    
    /* Factorial Table */
    static double fact[] =
    {
    	1.0,
    	1.0,
    	2.0,
    	6.0,
    	24.0,
    	120.0,
    	720.0,
    	5040.0,
    	40320.0,
    	362880.0
    };
    
    static private double  a0 = -0.5; 
    static private double  a1 = 0.3333333; 
    static private double  a2 = -0.2500068; 
    static private double  a3 = 0.2000118; 
    static private double  a4 = -0.1661269; 
    static private double  a5 = 0.1421878; 
    static private double  a6 = -0.1384794; 
    static private double  a7 = 0.1250060; 
    
      //    static private double  while(true) = for(;;); 
    
    /*!* #include "DistLib.h" /*4!*/
    

      static private double /* a0, a1, a2, a3, a4, a5, a6, a7, */ b1, b2;
      static private double c, c0, c1, c2, c3, d, del, difmuk, e;
      static private double fk, fx, fy, g, omega;
      static private double p, p0, px, py, q, s, t, u, v, x, xx;
      static private double pp[] = new double[36];
      static private int j, k, kflag, l, m;
      static private int ipois;
      static private double muprev = 0.0;
      static private double muold = 0.0;


    public static double  random(double mu, uniform PRNG )
    {
	throw new  java.lang.ArithmeticException("FUNCTION NOT IMPLEMENTED");
    }
}
/******        
/******        if (mu != muprev) {
/******    	if (mu >= 10.0) {
/******    	    /* case a. (recalculation of s,d,l */
/******    	    /* if mu has changed)  */
/******    	    /* the poisson probabilities pk */
/******    	    /* exceed the discrete normal */
/******    	    /* probabilities fk whenever k >= m(mu). */
/******    	    /* l=ifix(mu-1.1484) is an upper bound */
/******    	    /* to m(mu) for all mu >= 10. */
/******    	    muprev = mu;
/****** /*!* 	    s = sqrt(mu); *!*/
/******    	    s = java.lang.Math.sqrt(mu);
/******    	    d = 6.0 * mu * mu;
/******    	    l = mu - 1.1484;
/******    	} else {
/******    	    /* Case B. (start new table and */
/******    	    /* calculate p0 if necessary) */
/******    	    muprev = 0.0;
/******    	    if (mu != muold) {
/******    		muold = mu;
/****** /*!* 		m = imax2(1, (int) mu); *!*/
/******    		m = Math.max(1, (int) mu);
/******    		l = 0;
/****** /*!* 		p = exp(-mu); *!*/
/******    		p = java.lang.Math.exp(-mu);
/******    		q = p;
/******    		p0 = p;
/******    	    }
/******    	    while(true) {
/******    				/* Step U. uniform sample */
/******    				/* for inversion method */
/******    		u = uniform.random();
/******    		ipois = 0;
/******    		if (u <= p0)
/******    		    return (double)ipois;
/******    				/* Step T. table comparison until */
/******    				/* the end pp(l) of the pp-table of */
/******    				/* cumulative poisson probabilities */
/******    				/* (0.458=pp(9) for mu=10) */
/******    		if (l != 0) {
/******    		    j = 1;
/******    		    if (u > 0.458)
/****** /*!* 			j = Math.min(l, m); *!*/
/******    			j = Math.min(l, m);
/******    		    for (k = j; k <= l; k++)
/******    			if (u <= pp[k])
/******    			    return (double)k;
/******    		    if (l == 35)
/******    			continue;
/******    		}
/******    				/* Step C. creation of new poisson */
/******    				/* probabilities p and their cumulatives */
/******    				/* q=pp[k] */
/******    		l = l + 1;
/******    		for (k = l; k <= 35; k++) {
/******    		    p = p * mu / k;
/******    		    q = q + p;
/******    		    pp[k] = q;
/******    		    if (u <= q) {
/******    			l = k;
/******    			return (double)k;
/******    		    }
/******    		}
/******    		l = 35;
/******    	    }
/******    	}
/******        }
/******        /* Step N. normal sample */
/******        /* normal.random() for standard normal deviate */
/******        g = mu + s * normal.random();
/******        if (g >= 0.0) {
/******    	ipois = g;
/******    	/* Step I. immediate acceptance */
/******    	/* if ipois is large enough */
/******    	if (ipois >= l)
/******    	    return (double)ipois;
/******    	/* Step S. squeeze acceptance */
/******    	/* uniform.random() for (0,1)-sample u */
/******    	fk = ipois;
/******    	difmuk = mu - fk;
/******    	u = uniform.random();
/******    	if (d * u >= difmuk * difmuk * difmuk)
/******    	    return (double)ipois;
/******        }
/******        /* Step P. preparations for steps Q and H. */
/******        /* (recalculations of parameters if necessary) */
/******        /* 0.3989423=(2*pi)**(-0.5) */
/******        /* 0.416667e-1=1./24. */
/******        /* 0.1428571=1./7. */
/******        /* The quantities b1, b2, c3, c2, c1, c0 are for the Hermite */
/******        /* approximations to the discrete normal probabilities fk. */
/******        /* c=.1069/mu guarantees majorization by the 'hat'-function. */
/******        if (mu != muold) {
/******    	muold = mu;
/******    	omega = 0.3989423 / s;
/******    	b1 = 0.4166667e-1 / mu;
/******    	b2 = 0.3 * b1 * b1;
/******    	c3 = 0.1428571 * b1 * b2;
/******    	c2 = b2 - 15. * c3;
/******    	c1 = b1 - 6. * b2 + 45. * c3;
/******    	c0 = 1. - b1 + 3. * b2 - 15. * c3;
/******    	c = 0.1069 / mu;
/******        }
/******        if (g >= 0.0) {
/******    	/* 'Subroutine' F is called (kflag=0 for correct return) */
/******    	kflag = 0;
/******    	goto L20;
/******        }
/******	else while(true) {
/******    	/* Step E. Exponential Sample */
/******    	/* exponential.random() for standard exponential deviate */
/******    	/* e and sample t from the laplace 'hat' */
/******    	/* (if t <= -0.6744 then pk < fk for all mu >= 10.) */
/******    	e = exponential.random();
/******    	u = uniform.random();
/******    	u = u + u - 1.0;
/****** /*!* 	t = 1.8 + fsign(e, u); *!*/
/******    	t = 1.8 + misc.fsign(e, u);
/******    	if (t > -0.6744) {
/******    	    ipois = mu + s * t;
/******    	    fk = ipois;
/******    	    difmuk = mu - fk;
/******	    f(
/******    	    /* 'subroutine' f is called */
/******    	    /* (kflag=1 for correct return) */
/******    	    kflag = 1;
/******	    //**********	    subroutine_f(kflag)  ************** //
/******
/******   	}
/******        }
/******        return (double)ipois;
/******    }
/******  }
/******
/******double[] subroutine_f ( double px; double mu; double py; double del; double fk; double v; double a7; double a6; double a5; double a4; double a3; double a2; double a1; double a0; double x; double xx; double fx; double omega; double c3; double c2; double c1; double c0; double u; double e; int kflag )
/******    {
/******
/******    	    /* Step f. 'subroutine' f. */
/******    	    /* calculation of px,py,fx,fy. */
/******    	    /* case ignpoi < 10 uses */
/******    	    /* factorials from table fact */
/******    	  L20:if (ipois < 10) {
/******    	      px = -mu;
/****** /*!* 	      py = pow(mu, (double) ipois) / fact[ipois]; *!*/
/******    	      py = java.lang.Math.pow(mu, (double) ipois) / fact[ipois];
/******    	  } else {
/******    				/* Case ipois >= 10 uses polynomial */
/******    				/* approximation a0-a7 for accuracy */
/******    				/* when advisable */
/******    				/* 0.8333333e-1=1./12.0 */
/******    				/* 0.3989423=(2*pi)**(-0.5) */
/******    	      del = 0.8333333e-1 / fk;
/******    	      del = del - 4.8 * del * del * del;
/******    	      v = difmuk / fk;
/****** /*!* 	      if (fabs(v) <= 0.25) *!*/
/******    	      if (java.lang.Math.abs(v) <= 0.25)
/******    		  px = fk * v * v * (((((((a7 * v + a6) * v + a5) * v + a4) * v + a3) * v + a2) * v + a1) * v + a0) - del;
/******    	      else
/****** /*!* 		  px = fk * log(1.0 + v) - difmuk - del; *!*/
/******    		  px = fk * java.lang.Math.log(1.0 + v) - difmuk - del;
/****** /*!* 	      py = 0.3989423 / sqrt(fk); *!*/
/******    	      py = 0.3989423 / java.lang.Math.sqrt(fk);
/******    	  }
/******    	    x = (0.5 - difmuk) / s;
/******    	    xx = x * x;
/******    	    fx = -0.5 * xx;
/******    	    fy = omega * (((c3 * xx + c2) * xx + c1) * xx + c0);
/******    	    if (kflag > 0) {
/******    				/* Step H. hat acceptance */
/******    				/* (e is while(true)ed on rejection) */
/****** /*!* 		if (c * fabs(u) <= py * exp(px + e) - fy * exp(fx + e)) *!*/
/******    		if (c * java.lang.Math.abs(u) <= py * java.lang.Math.exp(px + e) - fy * java.lang.Math.exp(fx + e))
/******    		    break;
/******    	    } else
/******    				/* step q. quotient acceptance (rare case) */
/****** /*!* 		if (fy - u * fy <= py * exp(px - fx)) *!*/
/******    		if (fy - u * fy <= py * java.lang.Math.exp(px - fx))
/******    		    break;
/******}
*******/
 
