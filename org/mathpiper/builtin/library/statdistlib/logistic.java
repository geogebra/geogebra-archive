package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class logistic
  { 
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
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double density(double x, double location, double scale)
    {
    	double e, f;
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(location) || Double.isNaN(scale))
    	return x + location + scale;
    /*!* #endif /*4!*/
    	if (scale <= 0.0) {
    		throw new java.lang.ArithmeticException("Math Error: DOMAIN");
		//    		return Double.NaN;
    	}
/*!* 	e = exp(-(x - location) / scale); *!*/
    	e = java.lang.Math.exp(-(x - location) / scale);
    	f = 1.0 + e;
    	return e / (scale * f * f);
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
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double cumulative(double x, double location, double scale)
    {
    /*!* #ifdef IEEE_754 /*4!*/
    	if (Double.isNaN(x) || Double.isNaN(location) || Double.isNaN(scale))
    		return x + location + scale;
    /*!* #endif /*4!*/
    	if (scale <= 0.0) {
    		throw new java.lang.ArithmeticException("Math Error: DOMAIN");
		//    		return Double.NaN;
    	}
    	if(Double.isInfinite(x)) {
    		if (x > 0) return 1;
    		else return 0;
    	}
/*!* 	return 1.0 / (1.0 + exp(-(x - location) / scale)); *!*/
    	return 1.0 / (1.0 + java.lang.Math.exp(-(x - location) / scale));
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
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double quantile(double x, double location, double scale)
    {
    /*!* #ifdef IEEE_754 /*4!*/
    	if (Double.isNaN(x) || Double.isNaN(location) || Double.isNaN(scale))
    		return x + location + scale;
    /*!* #endif /*4!*/
    	if (scale <= 0.0 || x < 0 || x > 1) {
    		throw new java.lang.ArithmeticException("Math Error: DOMAIN");
		//    		return Double.NaN;
    	}
    	if(x <= 0) return Double.NEGATIVE_INFINITY;
    	if(x == 1) return Double.POSITIVE_INFINITY;
/*!* 	return location + scale * log(x / (1.0 - x)); *!*/
    	return location + scale * java.lang.Math.log(x / (1.0 - x));
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
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double random(double location, double scale, uniform PRNG)
    {
    	double u;
    /* #ifndef IEEE_754 */
    	if (Double.isInfinite(location) || Double.isInfinite(scale)) {
    		throw new java.lang.ArithmeticException("Math Error: DOMAIN");
		//    		return Double.NaN;
    	}
    /* #endif */
    	u = PRNG.random();
/*!* 	return location + scale * log(u / (1.0 - u)); *!*/
    	return location + scale * java.lang.Math.log(u / (1.0 - u));
    }
  }
