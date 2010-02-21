package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class weibull 
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
     *    The density function of the Weibull distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  density(double x, double shape, double scale)
    {
        double tmp1, tmp2;
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(shape) || Double.isNaN(scale))
    	return x + shape + scale;
    /*!* #endif /*4!*/
        if (shape <= 0 || scale <= 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x <= 0) return 0;
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isInfinite(x)) return 0;
    /*!* #endif /*4!*/
/*!*     tmp1 = pow(x / scale, shape - 1); *!*/
        tmp1 = java.lang.Math.pow(x / scale, shape - 1);
        tmp2 = tmp1 * (x / scale);
/*!*     return shape * tmp1 * exp(-tmp2) / scale; *!*/
        return shape * tmp1 * java.lang.Math.exp(-tmp2) / scale;
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
     *    double cumulative(double x, double shape, double scale);
     *
     *  DESCRIPTION
     *
     *    The distribution function of the Weibull distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  cumulative(double x, double shape, double scale)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(shape) || Double.isNaN(scale))
    	return x + shape + scale;
    /*!* #endif /*4!*/
        if(shape <= 0 || scale <= 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x <= 0) return 0;
/*!*     return 1.0 - exp(-pow(x / scale, shape)); *!*/
        return 1.0 - java.lang.Math.exp(-java.lang.Math.pow(x / scale, shape));
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
     *    double quantile(double x, double shape, double scale);
     *
     *  DESCRIPTION
     *
     *    The quantile function of the Weibull distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  quantile(double x, double shape, double scale)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(shape) || Double.isNaN(scale))
    	return x + shape + scale;
    /*!* #endif /*4!*/
        if (shape <= 0 || scale <= 0 || x < 0 || x > 1) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        if (x == 0) return 0;
    /*!* #ifdef IEEE_754 /*4!*/
        if (x == 1) return Double.POSITIVE_INFINITY;
    /*!* #endif /*4!*/
/*!*     return scale * pow(-log(1.0 - x), 1.0 / shape); *!*/
        return scale * java.lang.Math.pow(-java.lang.Math.log(1.0 - x), 1.0 / shape);
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
     *    double density(double x, double shape, double scale);
     *
     *  DESCRIPTION
     *
     *    Random variates from the Weibull distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  random(double shape, double scale, uniform PRNG)
    {
        if (
    /*!* #ifdef IEEE_754 /*4!*/
    	Double.isInfinite(shape) || Double.isInfinite(scale) ||
    /*!* #endif /*4!*/
    	shape <= 0.0 || scale <= 0.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
/*!*     return scale * pow(-log(sunif()), 1.0 / shape); *!*/
        return scale * java.lang.Math.pow(-java.lang.Math.log(PRNG.random()), 1.0 / shape);
    }
  }
