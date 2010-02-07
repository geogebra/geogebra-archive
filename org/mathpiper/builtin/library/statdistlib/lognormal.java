package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class lognormal
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
     *    double density(double x, double logmean, double logsd);
     *
     *  DESCRIPTION
     *
     *    The density of the lognormal distribution.
     *
     * 	M_1_SQRT_2PI = 1 / sqrt(2 * pi)
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  density(double x, double logmean, double logsd)
    {
        double y;
    
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(logmean) || Double.isNaN(logsd))
    	return x + logmean + logsd;
    /*!* #endif /*4!*/
        if(logsd <= 0) {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	    //            return Double.NaN;
        }
        if(x == 0) return 0;
/*!*     y = (log(x) - logmean) / logsd; *!*/
        y = (java.lang.Math.log(x) - logmean) / logsd;
/*!*     return Constants.M_1_SQRT_2PI * exp(-0.5 * y * y) / (x * logsd); *!*/
        return Constants.M_1_SQRT_2PI * java.lang.Math.exp(-0.5 * y * y) / (x * logsd);
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
     *    double cumulative(double x, double logmean, double logsd);
     *
     *  DESCRIPTION
     *
     *    The lognormal distribution function.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  cumulative(double x, double logmean, double logsd)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(logmean) || Double.isNaN(logsd))
    	return x + logmean + logsd;
    /*!* #endif /*4!*/
        if (logsd <= 0) {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	    //            return Double.NaN;
        }
        if (x > 0)
/*!* 	return normal.cumulative!!!COMMENT!!!(log(x), logmean, logsd); *!*/
    	return normal.cumulative(java.lang.Math.log(x), logmean, logsd);
        return 0;
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
     *    double quantile(double x, double logmean, double logsd);
     *
     *  DESCRIPTION
     *
     *    This the lognormal quantile function.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  quantile(double x, double logmean, double logsd)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(logmean) || Double.isNaN(logsd))
    	return x + logmean + logsd;
    /*!* #endif /*4!*/
        if(x < 0 || x > 1 || logsd <= 0) {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	    //            return Double.NaN;
        }
        if (x == 1) return Double.POSITIVE_INFINITY;
/*!*     if (x > 0) return exp(qnorm(x, logmean, logsd)); *!*/
        if (x > 0) return java.lang.Math.exp(normal.quantile(x, logmean, logsd));
        return 0;
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
     *    double random(double logmean, double logsd);
     *
     *  DESCRIPTION
     *
     *    Random variates from the lognormal distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  random(double logmean, double logsd, uniform PRNG)
    {
        if(
    /*!* #ifdef IEEE_754 /*4!*/
    	Double.isInfinite(logmean) || Double.isInfinite(logsd) ||
    /*!* #endif /*4!*/
    	logsd <= 0.0) {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	    //            return Double.NaN;
        }
/*!*     return exp(rnorm(logmean, logsd)); *!*/
        return java.lang.Math.exp(normal.random(logmean, logsd, PRNG));
    }
  }
