package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class chisquare
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
     *    double density(double x, double df)
     *
     *  DESCRIPTION
     *
     *    The density of the chi-squared disribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  density(double x, double df)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        /* NaNs propagated correctly */
    /*!* #endif /*4!*/
        return gamma.density(x, df / 2.0, 2.0);
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
     *    double cumulative(double x, double df);
     *
     *  DESCRIPTION
     *
     *    The disribution function of the chi-squared distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  cumulative(double x, double df)
    {
        return gamma.cumulative(x, df / 2.0, 2.0);
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
     *    double quantile(double p, double df);
     *
     *  DESCRIPTION
     *
     *    The quantile function of the chi-squared distribution.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  quantile(double p, double df)
    {
        return gamma.quantile(p, 0.5 * df, 2.0);
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
     *    double random(double df);
     *
     *  DESCRIPTION
     *
     *    Random variates from the chi-squared distribution.
     *
     *  NOTES
     *
     *    Calls rgamma to do the real work.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  random(double df, uniform PRNG)
    {
        if (
    /*!* #ifdef IEEE_754 /*4!*/
    	Double.isInfinite(df) ||
    /*!* #endif /*4!*/
    	df <= 0.0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        return gamma.random(df / 2.0, 2.0, PRNG);
    }
  }
