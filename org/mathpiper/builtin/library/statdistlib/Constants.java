/*  DistLib : A C Library of Special Functions
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
 * data translated from C using perl script translate.pl
 * script version 0.00
 */

package org.mathpiper.builtin.library.statdistlib;

/**
 * Class defining constants.
 */

public class Constants { 
        
    /* 30 Decimal-place constants computed with bc -l (scale=32; proper round) */
    
    public static final double  M_SQRT_2 = 1.4142135623730950488016887242097; 
    /* 1/sqrt(2) */
    public static final double  M_1_SQRT_2 = 0.707106781186547524400844362105; 
    /* sqrt(32) */
    public static final double  M_SQRT_32 = 5.656854249492380195206754896838; 
    
    public static final double  M_LN_2 = 0.693147180559945309417232121458176568; 
    public static final double  M_LOG10_2 = 0.301029995663981195213738894724493027; 
    
    public static final double  M_PI   = 3.141592653589793238462643383279502884197169399375; 
    public static final double  M_PI_half = 1.570796326794896619231321691640; 

    /* 1/pi */
    public static final double  M_1_PI =  0.31830988618379067153776752674502872406891929148;

   /* pi/2 */
    public static final double  M_PI_2 =  1.57079632679489661923132169163975144209858469969;

    /* sqrt(pi),  1/sqrt(2pi),  sqrt(2/pi) : */
    public static final double  M_SQRT_PI = 1.772453850905516027298167483341; 
    public static final double  M_1_SQRT_2PI = 0.398942280401432677939946059934; 
    public static final double  M_SQRT_2dPI = 0.79788456080286535587989211986876; 

    /* log(sqrt(pi)) = log(pi)/2 : */
    public static final double  M_LN_SQRT_PI = 0.5723649429247000870717136756765293558; 
    /* log(sqrt(2*pi)) = log(2*pi)/2 : */
    public static final double  M_LN_SQRT_2PI = 0.91893853320467274178032973640562; 
    /* log(sqrt(pi/2)) = log(pi/2)/2 : */
    public static final double  M_LN_SQRT_PId2 = 0.225791352644727432363097614947441; 
        
    public static final double  ME_NONE = 0; 
    public static final double  ME_DOMAIN = 1; 
    public static final double  ME_RANGE = 2; 
    public static final double  ME_NOCONV = 3; 
    public static final double  ME_PRECISION = 4; 
    public static final double  ME_UNDERFLOW = 5; 

    /* constants taken from float.h for gcc 2.90.29 for Linux 2.0 i386  */
    /* -- should match Java since both are supposed to be IEEE 754 compliant */

    /* Radix of exponent representation */
    public static final int    FLT_RADIX   = 2;

    /* Difference between 1.0 and the minimum float/double greater than 1.0 */
    public static final double FLT_EPSILON = 1.19209290e-07F;
    public static final double DBL_EPSILON = 2.2204460492503131e-16; 
    
    /* Number of decimal digits of precision in a float/double */
    public static final int FLT_DIG = 6;
    public static final int DBL_DIG = 15;          

    /* Number of base-FLT_RADIX digits in the significand of a double */
    public static final int FLT_MANT_DIG = 24;
    public static final int DBL_MANT_DIG = 53;

    /* Minimum int x such that FLT_RADIX**(x-1) is a normalised double */
    public static final int FLT_MIN_EXP = -125;
    public static final int DBL_MIN_EXP = -1021;

    /* Maximum int x such that FLT_RADIX**(x-1) is a representable double */
    public static final int FLT_MAX_EXP = 128;
    public static final int DBL_MAX_EXP = 1024;    
    
  }
