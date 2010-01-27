package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class signrank 
  { 


	public static final double  SIGNRANK_NMAX = 50; 

    /*
     *  DistLib : A C Library of Special Functions
     *  Copyright (C) 1998 R Core Team
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
     *    double density(double x, double n)
     *
     *  DESCRIPTION
     *
     *    The density of the Wilcoxon Signed Rank distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    static private double w[][];
    
    static private double csignrank(int k, int n) {
      int c, u, i;
    
      u = n * (n + 1) / 2;
      c = (int) (u / 2);
    
      if ((k < 0) || (k > u))
          return(0);
      if (k > c)
          k = u - k;
      if (w[n] == null) {
          w[n] = new double[c + 1];
          for (i = 0; i <= c; i++)
    	  w[n][i] = -1;
      }
      if (w[n][k] < 0) {
          if (n == 0)
    	  w[n][k] = (k == 0)?1.0:0.0;
          else
    	  w[n][k] = csignrank(k - n, n - 1) + csignrank(k, n - 1);
      }
      return(w[n][k]);
    }
    
    public static double  density(double x, double n) {
    /*!* #ifdef IEEE_754 /*4!*/
        /* NaNs propagated correctly */
        if (Double.isNaN(x) || Double.isNaN(n)) return x + n;
    /*!* #endif /*4!*/
/*!*     n = floor(n + 0.5); *!*/
        n = java.lang.Math.floor(n + 0.5);
        if (n <= 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        } else if (n >= SIGNRANK_NMAX) {
    	System.out.println("n should be less than %d\n"+ SIGNRANK_NMAX);
    	return Double.NaN;
        }
/*!*     x = floor(x + 0.5); *!*/
        x = java.lang.Math.floor(x + 0.5);
        if ((x < 0) || (x > (n * (n + 1) / 2)))
    	return 0;
/*!*     return(exp(log(csignrank(x, n)) - n * log(2))); *!*/
        return(java.lang.Math.exp(
		  java.lang.Math.log(
		     csignrank((int) x, (int) n)) - n * java.lang.Math.log(2)));
    }
    /*
     *  DistLib : A C Library of Special Functions
     *  Copyright (C) 1998 R Core Team
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
     *    double cumulative(double x, double n)
     *
     *  DESCRIPTION
     *
     *    The distribution function of the Wilcoxon Signed Rank distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  cumulative(double x, double n) {
        int i;
        double p = 0.0;
    
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(n))
        return x + n;
        if (Double.isInfinite(n)) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    /*!* #endif /*4!*/
/*!*     n = floor(n + 0.5); *!*/
        n = java.lang.Math.floor(n + 0.5);
        if (n <= 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        } else if (n >= SIGNRANK_NMAX) {
    	System.out.println("n should be less than %d\n"+ SIGNRANK_NMAX);
    	return Double.NaN;
        }
/*!*     x = floor(x + 0.5); *!*/
        x = java.lang.Math.floor(x + 0.5);
        if (x < 0.0)
    	return 0;
        if (x >= n * (n + 1) / 2)
    	return 1;
        for (i = 0; i <= x; i++)
    	p += density(i, n);
        return(p);
    }
    /*
     *  DistLib : A C Library of Special Functions
     *  Copyright (C) 1998 R Core Team
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
     *    double quantile(double x, double n);
     *
     *  DESCRIPTION
     *
     *    The quantile function of the Wilcoxon Signed Rank distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  quantile(double x, double n)
    {
        double p, q;
    
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(n))
    	return x + n;
        if(Double.isInfinite(x) || Double.isInfinite(n)) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    /*!* #endif /*4!*/
    
/*!*     n = floor(n + 0.5); *!*/
        n = java.lang.Math.floor(n + 0.5);
        if (x < 0 || x > 1 || n <= 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        } else if (n >= SIGNRANK_NMAX) {
    	System.out.println("n should be less than %d\n"+ SIGNRANK_NMAX);
    	return Double.NaN;
        }
    
        if (x == 0) return(0.0);
        if (x == 1) return(n * (n + 1) / 2);
        p = 0.0;
        q = 0.0;
        for (;;) {
    	/* Don't call cumulative() for efficiency */
    	p += density(q, n);
    	if (p >= x)
    	    return(q);
    	q++;
        }
    }
    /*
     *  DistLib : A C Library of Special Functions
     *  Copyright (C) 1998 R Core Team
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
     *    double random(double n)
     *    
     *  DESCRIPTION
     *
     *    Random variates from the Wilcoxon Signed Rank distribution.
     *
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  random(double n)
    {
        int i, k;
        double r;
      
    /*!* #ifdef IEEE_754 /*4!*/
        /* NaNs propagated correctly */
        if (Double.isNaN(n)) return(n);
    /*!* #endif /*4!*/
/*!*     n = floor(n + 0.5); *!*/
        n = java.lang.Math.floor(n + 0.5);
        if (n < 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (n == 0)
    	return(0);
        r = 0.0;
        k = (int) n;
        for (i = 0; i < k; ) {
/*!* 	r += (++i) * floor(sunif() + 0.5); *!*/
    	r += (++i) * java.lang.Math.floor(uniform.random() + 0.5);
        }
        return(r);
    }
  }
