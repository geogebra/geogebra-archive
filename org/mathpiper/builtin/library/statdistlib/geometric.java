package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class geometric
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
     *    double density(double x, double p);
     *
     *  DESCRIPTION
     *
     *    The density of the geometric distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  density(double x, double p)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(p)) return x + p;
    /*!* #endif /*4!*/
        if (p <= 0 || p >= 1) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
/*!*     x = floor(x + 0.5); *!*/
        x = java.lang.Math.floor(x + 0.5);
        if (x < 0)
    	return 0;
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isInfinite(x)) return 1;
    /*!* #endif /*4!*/
/*!*     return p * pow(1 - p, x); *!*/
        return p * java.lang.Math.pow(1 - p, x);
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
     *    double quantile(double x, double p);
     *
     *  DESCRIPTION
     *
     *    The distribution function of the geometric distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  cumulative(double x, double p)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(p))
    	return x + p;
    /*!* #endif /*4!*/
/*!*     x = floor(x); *!*/
        x = java.lang.Math.floor(x);
        if(p <= 0 || p >= 1) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x < 0.0) return 0;
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isInfinite(x)) return 1;
    /*!* #endif /*4!*/
/*!*     return 1 - pow(1 - p, x + 1); *!*/
        return 1 - java.lang.Math.pow(1 - p, x + 1);
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
     *    double quantile(double x, double p);
     *
     *  DESCRIPTION
     *
     *    The quantile function of the geometric distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  quantile(double x, double p)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(p))
    	return x + p;
        if (x < 0 || x > 1 || p <= 0 || p > 1) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x == 1) return Double.POSITIVE_INFINITY;
    /*!* #else /*4!*/
        if (x < 0 || x >= 1 || p <= 0 || p > 1) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    /*!* #endif /*4!*/
        if (x == 0) return 0;
/*!*     return ceil(log(1 - x) / log(1.0 - p) - 1); *!*/
        return java.lang.Math.ceil(java.lang.Math.log(1 - x) / java.lang.Math.log(1.0 - p) - 1);
    }
    /*
     *  DistLib : A C Library of Special Functions
     *  Copyright (C) 1998 Ross Ihaka and the R Core Team.
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
     *    double random(double p);
     *
     *  DESCRIPTION
     *
     *    Random variates from the geometric distribution.
     *
     *  NOTES
     *
     *    We generate lambda as exponential with scale parameter
     *    p / (1 - p).  Return a Poisson deviate with mean lambda.
     *
     *  REFERENCE
     *
     *    Devroye, L. (1980).
     *    Non-Uniform Random Variate Generation.
     *    New York: Springer-Verlag.
     *    Page 480.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  random(double p, uniform PRNG)
    {
        if (
    /*!* #ifdef IEEE_754 /*4!*/
    	Double.isNaN(p) ||
    /*!* #endif /*4!*/
    	p <= 0 || p >= 1) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        return poisson.random(exponential.random( PRNG ) * ((1 - p) / p), PRNG);
    }
  }
