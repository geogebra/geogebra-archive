package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class f 
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
     *    double density(double x, double n1, double n2);
     *
     *  DESCRIPTION
     *
     *    The density function of the F distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  density(double x, double n1, double n2)
    {
    	double a;
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(n1) || Double.isNaN(n2))
    	return x + n1 + n2;
    /*!* #endif /*4!*/
    	if (n1 <= 0 || n2 <= 0) {
    	    throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	    //    	    return Double.NaN;
    	}
    	if (x <= 0.0)
    		return 0.0;
    	a = (n1 / n2) * x;
/*!* 	return pow(a, 0.5 * n1) * pow(1.0 + a, -0.5 * (n1 + n2)) *!*/
    	return java.lang.Math.pow(a, 0.5 * n1) * java.lang.Math.pow(1.0 + a, -0.5 * (n1 + n2))
/*!* 			/ (x * beta(0.5 * n1, 0.5 * n2)); *!*/
    			/ (x * misc.beta(0.5 * n1, 0.5 * n2));
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
     *    double cumulative(double x, double n1, double n2);
     *
     *  DESCRIPTION
     *
     *    The distribution function of the F distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  cumulative(double x, double n1, double n2)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(n1) || Double.isNaN(n2))
    	return x + n2 + n1;
    /*!* #endif /*4!*/
        if (n1 <= 0.0 || n2 <= 0.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x <= 0.0)
    	return 0.0;
        x = 1.0 - beta.cumulative(n2 / (n2 + n1 * x), n2 / 2.0, n1 / 2.0);
        return !Double.isNaN(x) ? x : Double.NaN;
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
     *    double quantile(double x, double n1, double n2);
     *
     *  DESCRIPTION
     *
     *    The quantile function of the F distribution.
    */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  quantile(double x, double n1, double n2)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(n1) || Double.isNaN(n2))
    	return x + n1 + n2;
    /*!* #endif /*4!*/
        if (n1 <= 0.0 || n2 <= 0.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x <= 0.0)
    	return 0.0;
        x = (1.0 / beta.quantile(1.0 - x, n2 / 2.0, n1 / 2.0) - 1.0) * (n2 / n1);
        return !Double.isNaN(x) ? x : Double.NaN;
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
     *    double random(double dfn, double dfd);
     *
     *  DESCRIPTION
     *
     *    Pseudo-random variates from an F distribution.
     *
     *  NOTES
     *
     *    This function calls rchisq to do the real work
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  random(double n1, double n2, uniform PRNG)
    {
        double v1, v2;
        if (
    /*!* #ifdef IEEE_754 /*4!*/
    	Double.isNaN(n1) || Double.isNaN(n2) ||
    /*!* #endif /*4!*/
    	n1 <= 0.0 || n2 <= 0.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        v1 = !Double.isInfinite(n1) ? (chisquare.random(n1,PRNG) / n1) : normal.random(PRNG);
        v2 = !Double.isInfinite(n2) ? (chisquare.random(n2,PRNG) / n2) : normal.random(PRNG);
        return v1 / v2;
    }
  }
