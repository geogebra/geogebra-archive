/*  DistLib - A Mathematical Function Library
 *  Copyright (C) 1998  Ross Ihaka
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
 * Miscellaneous functions and values.
 */

public class misc {

  /**
   * Value of the beta function
   * evaluated with arguments a and b.
   *
   * This routine is a translation into C of a Fortran subroutine
   * by W. Fullerton of Los Alamos Scientific Laboratory.
   * Some modifications have been made so that the routines
   * conform to the IEEE 754 standard.
   */

  public static double  beta(double a, double b) {
    double xmax = 0;
    double alnsml = 0;
    double val=0.0, xmin=0.0;
    double temp[];

    if (xmax == 0) {
      temp = gammalims(xmin, xmax);
      xmin = temp[0]; xmax=temp[1];
      alnsml = java.lang.Math.log(d1mach(1));
    }

    if (Double.isNaN(a) || Double.isNaN(b)) return a + b;

    if (a < 0 || b < 0) {
      throw new java.lang.ArithmeticException("Math Error: DOMAIN");
    }
    else if (a == 0 || b == 0) {
      return Double.POSITIVE_INFINITY;
    }
    else if (Double.isInfinite(a) || Double.isInfinite(b)) {
      return 0;
    }

    if (a + b < xmax)
      return gammafn(a) * gammafn(b) / gammafn(a+b);

    val = lbeta(a, b);
    // check for underflow of beta
    if (val < alnsml) {
      throw new java.lang.ArithmeticException("Math Error: UNDERFLOW");
    }
    return java.lang.Math.exp(val);
  }
    
  /**
   * Determine the number of terms for the
   * double precision orthogonal Chebyshev series "dos" needed to insure
   * the error is no larger than "eta".  Ordinarily eta will be
   * chosen to be one-tenth machine precision.
   *
   *    These routines are translations into C of Fortran routines
   *    by W. Fullerton of Los Alamos Scientific Laboratory.
   *
   *    Based on the Fortran routine dcsevl by W. Fullerton.
   *    Adapted from R. Broucke, Algorithm 446, CACM., 16, 254 (1973).
   */
  static int chebyshev_init(double dos[], int nos, double eta) {
    if (nos < 1) return 0;

    double err = 0.0;
    int i = 0;
    for (int ii=1; ii<=nos; ii++) {
      i = nos - ii;
      err += java.lang.Math.abs(dos[i]);
      if (err > eta) {
        return i;
      }
    }
    return i;
  }
    
  /**
   * evaluate the n-term Chebyshev series
   * @param x
   * @param a
   * @param n
   * @return
   */    
  public static double  chebyshev_eval(double x, double a[], int n) {
    if (n < 1 || n > 1000) {
      throw new java.lang.ArithmeticException("Math Error: DOMAIN");
    }

    if (x < -1.1 || x > 1.1) {
      throw new java.lang.ArithmeticException("Math Error: DOMAIN");
    }

    double twox = x * 2;
    double b2 = 0;
    double b1 = 0;
    double b0 = 0;
    for (int i = 1; i <= n; i++) {
      b2 = b1;
      b1 = b0;
      b0 = twox * b1 - b2 + a[(int) n - i];
    }
    return (b0 - b2) * 0.5;
  }
    
    /*
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    double choose(double n, double k);
     *    double fastchoose(double n, double k);
     *    double lchoose(double n, double k);
     *    double lfastchoose(double n, double k);
     *
     *  DESCRIPTION
     *
     *    Binomial coefficients.
     */
    /*!* #include "DistLib.h" /*4!*/
    
  public static double lfastchoose(double n, double k) {
    return lgammafn(n + 1.0) - lgammafn(k + 1.0) - lgammafn(n - k + 1.0);
  }

  public static double fastchoose(double n, double k) {
    return java.lang.Math.exp(lfastchoose(n, k));
  }

  public static double lchoose(double n, double k) {
    n = java.lang.Math.floor(n + 0.5);
    k = java.lang.Math.floor(k + 0.5);
    if (Double.isNaN(n) || Double.isNaN(k)) return n + k;
    if (k < 0 || n < k) {
      throw new java.lang.ArithmeticException("Math Error: DOMAIN");
    }
    return lfastchoose(n, k);
  }

  /**
   * binomial coefficient
   * @param n
   * @param k
   * @return
   */
  public static double choose(double n, double k) {
    n = java.lang.Math.floor(n + 0.5);
    k = java.lang.Math.floor(k + 0.5);
    if (Double.isNaN(n) || Double.isNaN(k)) return n + k;
    if (k < 0 || n < k) {
      throw new java.lang.ArithmeticException("Math Error: DOMAIN");
    }
    return java.lang.Math.floor(java.lang.Math.exp(lfastchoose(n, k)) + 0.5);
  }

  /**
   * machine dependant constants
   * @param i
   * @return
   */
  
  public static double  d1mach(int i) {
    switch (i) {
    
    case 1: return Double.MIN_VALUE;
    case 2: return Double.MAX_VALUE;
    case 3: return java.lang.Math.pow((double)i1mach(10), -(double)i1mach(14));    
    case 4: return java.lang.Math.pow((double)i1mach(10), 1-(double)i1mach(14));
    case 5: return Math.log(2.0)/Math.log(10.0);

    default: return 0.0;
    }
  }
    
  /*
   * Returns the cube of its argument.
   */
   public static double fcube(double x) {
      return x * x * x;
  }
    
    
  public static double  fmax2(double x, double y) {
  	if (Double.isNaN(x) || Double.isNaN(y))
  	  return x + y;
  	return (x < y) ? y : x;
  }
  
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  fmin2(double x, double y)
    {
    /*!* #ifdef IEEE_754 /*4!*/
    	if (Double.isNaN(x) || Double.isNaN(y))
    		return x + y;
    /*!* #endif /*4!*/
    	return (x < y) ? x : y;
    }
    /*
     *
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    double fmod(double x, double y);
     *
     *  DESCRIPTION
     *
     *    Floating-point remainder of x / y;
     *
     *  NOTES
     *
     *    It may be better to use the system version of this function,
     *    but this version is portable.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  fmod(double x, double y)
    {
        double quot;
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(y))
    	return x + y;
    /*!* #endif /*4!*/
        quot = x / y;
/*!*     return x - (quot < 0.0 ? ceil(quot) : floor(quot)) * y; *!*/
        return x - (quot < 0.0 ? java.lang.Math.ceil(quot) : java.lang.Math.floor(quot)) * y;
    }
    
  /**
   * Returns the value of x rounded to "digits" significant
   * decimal digits.
   *
   * This routine is a translation into C of a Fortran subroutine
   * by W. Fullerton of Los Alamos Scientific Laboratory.
   * Some modifications have been made so that the routines
   * conform to the IEEE 754 standard.
   * 
   * Improvements by Martin Maechler, May 1997
   * Note that the code could be further improved by using 
   * java.lang.Math.pow(x, i)  instead of  pow(x, (double)i) 
   */

  static final double MAXPLACES = Constants.DBL_DIG; 

  public static double fprec(double x, double digits) {

    if (Double.isNaN(x) || Double.isNaN(digits)) return x + digits;
    if (Double.isInfinite(x)) return x;
    if (Double.isInfinite(digits)) {
      if (digits > 0) return x;
      else return 0;
    }

    if (x == 0) return x;
    
    digits = java.lang.Math.floor(digits+0.5);
    if (digits > MAXPLACES) return x;
    else if (digits < 1) digits = 1;

    double sgn = 1.0;
    if (x < 0.0) {
      sgn = -sgn;
      x = -x;
    }
    double l10 = Math.log(x) / Math.log(10.0);
    // Max.expon. of 10 (=308.2547)
    int e10 = (int)(digits-1-java.lang.Math.floor(l10));
    final double max10e = Constants.DBL_MAX_EXP * Constants.M_LOG10_2;
    if (Math.abs(l10) < max10e - 2) {
      double pow10 = Math.pow(10.0, (double)e10);
      return (sgn*Math.floor(x*pow10+0.5)/pow10);
    } else { /* -- LARGE or small -- */
      /*!* 	do_round = max10e - l10	 >= pow(10.0, -digits); *!*/
      boolean do_round = max10e - l10 >= Math.pow(10.0, -digits);
      int e2 = (e10>0)? 16 : -16;
      double p10 = Math.pow(10.0, (double)e2);
      x *= p10;
      double P10 = Math.pow(10.0, (double)e10-e2);
      x *= P10;
      /*-- p10 * P10 = 10 ^ e10 */
      if (do_round) x += 0.5;
      x = Math.floor(x) / p10;
      return (sgn*x/P10);
    }
  }
  
    /*
     *
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    double fround(double x, double digits);
     *
     *  DESCRIPTION
     *
     *    Rounds "x" to "digits" decimal digits.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    /*!* #ifndef HAVE_RINT /*4!*/
    /*!* #define USE_BUILTIN_RINT /*4!*/
    /*!* #endif /*4!*/
    
    /*!* #ifdef USE_BUILTIN_RINT /*4!*/
      // final  double  R_rint = static private_rint; 
    
    	/* The largest integer which can be represented */
    	/* exactly in floating point form. */
    
    static final  double  BIGGEST = 4503599627370496.0E0; 
	/* 2^52 for IEEE */
    
    static private double Rint(double x)
    {
    	final  double biggest = BIGGEST;
    	double tmp;
    
    	if (x != x) return x;			/* NaN */
    
/*!* 	if (fabs(x) >= biggest)			!!!COMMENT!!! *!*/
    	if (java.lang.Math.abs(x) >= biggest)			/* Already integer */
    		return x;
    
    	if(x >= 0) {
    		tmp = x + biggest;
    		return tmp - biggest;
    	}
    	else {
    		tmp = x - biggest;
    		return tmp + biggest;
    	}
    }
    
    /*!* #else /*4!*/
      //final  double  R_rint = rint; 
    /*!* #endif /*4!*/
    
    public static double  fround(double x, double digits)
    {
    	double pow10, sgn, intx;
    	final  double maxdigits = Constants.DBL_DIG - 1;
    
    /*!* #ifdef IEEE_754 /*4!*/
    	if (Double.isNaN(x) || Double.isNaN(digits))
    		return x + digits;
    	if(Double.isInfinite(x)) return x;
    /*!* #endif /*4!*/
    
/*!* 	digits = floor(digits + 0.5); *!*/
    	digits = java.lang.Math.floor(digits + 0.5);
    	if (digits > maxdigits)
    		digits = maxdigits;
/*!* 	pow10 = pow(10.0, digits); *!*/
    	pow10 = java.lang.Math.pow(10.0, digits);
    	sgn = 1.0;
    	if(x < 0.0) {
    		sgn = -sgn;
    		x = -x;
    	}
    	if (digits > 0.0) {
/*!* 		intx = floor(x); *!*/
    		intx = java.lang.Math.floor(x);
    		x = x - intx;
    	} else {
    		intx = 0.0;
    	}
    	return sgn * (intx + java.lang.Math.rint(x * pow10) / pow10);
    }
    /*
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    double fsign(double x, double y);
     *
     *  DESCRIPTION
     *
     *    This function performs transfer of sign.  The result is:
     *
     *                        |x| * signum(y)
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  fsign(double x, double y)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x) || Double.isNaN(y))
    	return x + y;
    /*!* #endif /*4!*/
/*!*     return ((y >= 0) ? fabs(x) : -fabs(x)); *!*/
        return ((y >= 0) ? java.lang.Math.abs(x) : -java.lang.Math.abs(x));
    }
    /*
     *
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    double fsquare(double x);
     *
     *  DESCRIPTION
     *
     *    This function returns the square of its argument.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  fsquare(double x)
    {
        return x * x;
    }
    
  /**
   * Truncation toward zero.
   */
  public static double ftrunc(double x) {
    if (x >= 0) return java.lang.Math.floor(x);
    else return java.lang.Math.ceil(x);
  }
    
    /*
     *
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    double gammafn(double x);
     *
     *  DESCRIPTION
     *
     *    This function computes the value of the gamma function.
     *
     *  NOTES
     *
     *    This function is a translation into C of a Fortran subroutine
     *    by W. Fullerton of Los Alamos Scientific Laboratory.
     *
     *    The accuracy of this routine compares (very) favourably
     *    with those of the Sun Microsystems portable mathematical
     *    library.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
        static final  double gamcs[] = {
    	+.8571195590989331421920062399942e-2,
    	+.4415381324841006757191315771652e-2,
    	+.5685043681599363378632664588789e-1,
    	-.4219835396418560501012500186624e-2,
    	+.1326808181212460220584006796352e-2,
    	-.1893024529798880432523947023886e-3,
    	+.3606925327441245256578082217225e-4,
    	-.6056761904460864218485548290365e-5,
    	+.1055829546302283344731823509093e-5,
    	-.1811967365542384048291855891166e-6,
    	+.3117724964715322277790254593169e-7,
    	-.5354219639019687140874081024347e-8,
    	+.9193275519859588946887786825940e-9,
    	-.1577941280288339761767423273953e-9,
    	+.2707980622934954543266540433089e-10,
    	-.4646818653825730144081661058933e-11,
    	+.7973350192007419656460767175359e-12,
    	-.1368078209830916025799499172309e-12,
    	+.2347319486563800657233471771688e-13,
    	-.4027432614949066932766570534699e-14,
    	+.6910051747372100912138336975257e-15,
    	-.1185584500221992907052387126192e-15,
    	+.2034148542496373955201026051932e-16,
    	-.3490054341717405849274012949108e-17,
    	+.5987993856485305567135051066026e-18,
    	-.1027378057872228074490069778431e-18,
    	+.1762702816060529824942759660748e-19,
    	-.3024320653735306260958772112042e-20,
    	+.5188914660218397839717833550506e-21,
    	-.8902770842456576692449251601066e-22,
    	+.1527474068493342602274596891306e-22,
    	-.2620731256187362900257328332799e-23,
    	+.4496464047830538670331046570666e-24,
    	-.7714712731336877911703901525333e-25,
    	+.1323635453126044036486572714666e-25,
    	-.2270999412942928816702313813333e-26,
    	+.3896418998003991449320816639999e-27,
    	-.6685198115125953327792127999999e-28,
    	+.1146998663140024384347613866666e-28,
    	-.1967938586345134677295103999999e-29,
    	+.3376448816585338090334890666666e-30,
    	-.5793070335782135784625493333333e-31
        };
    
    public static double  gammafn(double x)
    {
        int ngam = 0;
        double xmin = 0.;
        double xmax = 0.;
        double xsml = 0.;
        double dxrel = 0.;
	double temp[];
    
        int i, n;
        double y;
        double sinpiy, value;
    
        if (ngam == 0) {
    	ngam = chebyshev_init(gamcs, 42, 0.1 * d1mach(3));
    	temp = gammalims(xmin, xmax);
	xmin=temp[0]; xmax=temp[1];
/*!* 	xsml = exp(fmax2(log(d1mach(1)), -log(d1mach(2)))+0.01); *!*/
    	xsml = java.lang.Math.exp(fmax2(java.lang.Math.log(d1mach(1)), -java.lang.Math.log(d1mach(2)))+0.01);
/*!* 	dxrel = sqrt(d1mach(4)); *!*/
    	dxrel = java.lang.Math.sqrt(d1mach(4));
        }
    
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isNaN(x)) return x;
    /*!* #endif /*4!*/
    
/*!*     y = fabs(x); *!*/
        y = java.lang.Math.abs(x);
    
        if (y <= 10) {
    
    	/* Compute gamma(x) for -10 <= x <= 10. */
    	/* Reduce the interval and find gamma(1 + y) for */
    	/* 0 <= y < 1 first of all. */
    
    	n = (int) x;
    	if(x < 0) --n;
    	y = x - n;/* n = floor(x)  ==>	y in [ 0, 1 ) */
    	--n;
    	value = chebyshev_eval(y * 2 - 1, gamcs, ngam) + .9375;
    	if (n == 0)
    	    return value;/* x = 1.dddd = 1+y */
    
    	if (n < 0) {
    	    /* compute gamma(x) for -10 <= x < 1 */
    
    	    /* If the argument is exactly zero or a negative integer */
    	    /* then return NaN. */
    	    if (x == 0 || (x < 0 && x == n + 2)) {
    		throw new java.lang.ArithmeticException("Math Error: RANGE");
		//    		return Double.NaN;
    	    }
    
    	    /* The answer is less than half precision */
    	    /* because x too near a negative integer. */
/*!* 	    if (x < -0.5 && fabs(x - (int)(x - 0.5) / x) < dxrel) { *!*/
    	    if (x < -0.5 && java.lang.Math.abs(x - (int)(x - 0.5) / x) < dxrel) {
    		throw new java.lang.ArithmeticException("Math Error: PRECISION");
    	    }
    
    	    /* The argument is so close to 0 that the result would overflow. */
    	    if (y < xsml) {
    		throw new java.lang.ArithmeticException("Math Error: RANGE");
		//    		if(x > 0) return Double.POSITIVE_INFINITY;
		//    		else return Double.NEGATIVE_INFINITY;
    	    }
    
    	    n = -n;
    
    	    for (i = 0; i < n; i++) {
    		value /= (x + i);
    	    }
    	    return value;
    	}
    	else {
    	    /* gamma(x) for 2 <= x <= 10 */
    
    	    for (i = 1; i <= n; i++) {
    		value *= (y + i);
    	    }
    	    return value;
    	}
        }
        else {
    	/* gamma(x) for	 y = |x| > 10. */
    
    	if (x > xmax) {			/* Overflow */
    	    throw new java.lang.ArithmeticException("Math Error: RANGE");
	    //    	    return Double.POSITIVE_INFINITY;
    	}
    
    	if (x < xmin) {			/* Underflow */
    	    throw new java.lang.ArithmeticException("Math Error: UNDERFLOW");
	    //    	    return (Double.MIN_VALUE * Double.MIN_VALUE);
    	}
    
/*!* 	value = exp((y - 0.5) * log(y) - y + Constants.M_LN_SQRT_2PI + lgammacor(y)); *!*/
    	value = java.lang.Math.exp((y - 0.5) * java.lang.Math.log(y) - y + Constants.M_LN_SQRT_2PI + lgammacor(y));
    
    	if (x > 0)
    	    return value;
    
/*!* 	if (fabs((x - (int)(x - 0.5))/x) < dxrel){ *!*/
    	if (java.lang.Math.abs((x - (int)(x - 0.5))/x) < dxrel){
    
    	    /* The answer is less than half precision because */
    	    /* the argument is too near a negative integer. */
    
    	    throw new java.lang.ArithmeticException("Math Error: PRECISION");
    	}
    
/*!* 	sinpiy = sin(Constants.M_PI * y); *!*/
    	sinpiy = java.lang.Math.sin(Constants.M_PI * y);
    	if (sinpiy == 0) {		/* Negative integer arg - overflow */
    	    throw new java.lang.ArithmeticException("Math Error: RANGE");
	    //    	    return Double.POSITIVE_INFINITY;
    	}
    
    	return -Constants.M_PI / (y * sinpiy * value);
        }
    }
    /* From http://www.netlib.org/specfun/gamma	Fortran translated by f2c,...
     *	------------------------------#####	Martin Maechler, ETH Zurich
     *
     *=========== was part of	ribesl (Bessel I(.))
     *===========			~~~~~~
     */
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  gamma_cody(double x)
    {
    /* ----------------------------------------------------------------------
    
       This routine calculates the GAMMA function for a float argument X.
       Computation is based on an algorithm outlined in reference [1].
       The program uses rational functions that approximate the GAMMA
       function to at least 20 significant decimal digits.	Coefficients
       for the approximation over the interval (1,2) are unpublished.
       Those for the approximation for X >= 12 are from reference [2].
       The accuracy achieved depends on the arithmetic system, the
       compiler, the intrinsic functions, and proper selection of the
       machine-dependent constants.
    
       *******************************************************************
    
       Error returns
    
       The program returns the value XINF for singularities or
       when overflow would occur.	 The computation is believed
       to be free of underflow and overflow.
    
       Intrinsic functions required are:
    
       INT, DBLE, EXP, LOG, REAL, SIN
    
    
       References:
       [1]  "An Overview of Software Development for Special Functions",
    	W. J. Cody, Lecture Notes in Mathematics, 506,
    	Numerical Analysis Dundee, 1975, G. A. Watson (ed.),
    	Springer Verlag, Berlin, 1976.
    
       [2]  Computer Approximations, Hart, Et. Al., Wiley and sons, New York, 1968.
    
       Latest modification: October 12, 1989
    
       Authors: W. J. Cody and L. Stoltz
       Applied Mathematics Division
       Argonne National Laboratory
       Argonne, IL 60439
       ----------------------------------------------------------------------*/
    
    /* ----------------------------------------------------------------------
       Mathematical constants
       ----------------------------------------------------------------------*/
        final  double sqrtpi = .9189385332046727417803297; /* == ??? */
    
    /* *******************************************************************
    
       Explanation of machine-dependent constants
    
       beta	- radix for the floating-point representation
       maxexp - the smallest positive power of beta that overflows
       XBIG	- the largest argument for which GAMMA(X) is representable
    	in the machine, i.e., the solution to the equation
    	GAMMA(XBIG) = beta**maxexp
       XINF	- the largest machine representable floating-point number;
    	approximately beta**maxexp
       EPS	- the smallest positive floating-point number such that  1.0+EPS > 1.0
       XMININ - the smallest positive floating-point number such that
    	1/XMININ is machine representable
    
       Approximate values for some important machines are:
    
       beta	      maxexp	     XBIG
    
       CRAY-1		(S.P.)	      2		8191	    966.961
       Cyber 180/855
       under NOS	(S.P.)	      2		1070	    177.803
       IEEE (IBM/XT,
       SUN, etc.)	(S.P.)	      2		 128	    35.040
       IEEE (IBM/XT,
       SUN, etc.)	(D.P.)	      2		1024	    171.624
       IBM 3033	(D.P.)	     16		  63	    57.574
       VAX D-Format	(D.P.)	      2		 127	    34.844
       VAX G-Format	(D.P.)	      2		1023	    171.489
    
       XINF	 EPS	    XMININ
    
       CRAY-1		(S.P.)	 5.45E+2465   7.11E-15	  1.84E-2466
       Cyber 180/855
       under NOS	(S.P.)	 1.26E+322    3.55E-15	  3.14E-294
       IEEE (IBM/XT,
       SUN, etc.)	(S.P.)	 3.40E+38     1.19E-7	  1.18E-38
       IEEE (IBM/XT,
       SUN, etc.)	(D.P.)	 1.79D+308    2.22D-16	  2.23D-308
       IBM 3033	(D.P.)	 7.23D+75     2.22D-16	  1.39D-76
       VAX D-Format	(D.P.)	 1.70D+38     1.39D-17	  5.88D-39
       VAX G-Format	(D.P.)	 8.98D+307    1.11D-16	  1.12D-308
    
       *******************************************************************
    
       ----------------------------------------------------------------------
       Machine dependent parameters
       ----------------------------------------------------------------------
       */
    
    
        final  double xbig = 171.624;
        /* ML_POSINF ==   static private double xinf = 1.79e308;*/
        /* Constants.DBL_EPSILON = static private double eps = 2.22e-16;*/
        /* Double.MIN_VALUE ==   static private double xminin = 2.23e-308;*/
    
        /*----------------------------------------------------------------------
          Numerator and denominator coefficients for rational minimax
          approximation over (1,2).
          ----------------------------------------------------------------------*/
	//        final  double p[8] = {
        final  double p[] = {
    	-1.71618513886549492533811,
    	24.7656508055759199108314,-379.804256470945635097577,
    	629.331155312818442661052,866.966202790413211295064,
    	-31451.2729688483675254357,-36144.4134186911729807069,
    	66456.1438202405440627855 };
	//        final  double q[8] = { 
        final  double q[] = {
   	-30.8402300119738975254353,
    	315.350626979604161529144,-1015.15636749021914166146,
    	-3107.77167157231109440444,22538.1184209801510330112,
    	4755.84627752788110767815,-134659.959864969306392456,
    	-115132.259675553483497211 };
        /*----------------------------------------------------------------------
          Coefficients for minimax approximation over (12, INF).
          ----------------------------------------------------------------------*/
	//        final  double c[7] = {
        final  double c[] = {
    	-.001910444077728,8.4171387781295e-4,
    	-5.952379913043012e-4,7.93650793500350248e-4,
    	-.002777777777777681622553,.08333333333333333331554247,
    	.0057083835261 };
    
        /* Local variables */
        long i, n;
        boolean parity;/*logical*/
        double fact, xden, xnum, y, z, y1, res, sum, ysq;
    
        parity = false;
        fact = 1.;
        n = 0;
        y = x;
    L_end: {
	  if (y <= 0.) {
	    /* -------------------------------------------------------------
	       Argument is negative
	       ------------------------------------------------------------- */
	    y = -x;
	    y1 = ftrunc(y);
	    res = y - y1;
	    if (res != 0.) {
	      if (y1 != ftrunc(y1 * .5) * 2.)
    		parity = true;
	      /*!* 	    fact = -Constants.M_PI / sin(Constants.M_PI * res); *!*/
	      fact = -Constants.M_PI / java.lang.Math.sin(Constants.M_PI * res);
	      y += 1.;
	    } else {
	      res = Double.POSITIVE_INFINITY;
	      break L_end;
	    }
	  }
	  /* -----------------------------------------------------------------
	     Argument is positive
	     -----------------------------------------------------------------*/
	  if (y < Constants.DBL_EPSILON) {
	    /* --------------------------------------------------------------
	       Argument < EPS
	       -------------------------------------------------------------- */
	    if (y >= Double.MIN_VALUE) {
	      res = 1. / y;
	    } else {
	      res = Double.POSITIVE_INFINITY;
	      break L_end;
	    }
	  } else if (y < 12.) {
	    y1 = y;
	    if (y < 1.) {
	      /* ---------------------------------------------------------
		 EPS < argument < 1
		 --------------------------------------------------------- */
	      z = y;
	      y += 1.;
	    } else {
	      /* -----------------------------------------------------------
		 1 <= argument < 12, reduce argument if necessary
		 ----------------------------------------------------------- */
	      n = (long) y - 1;
	      y -= (double) n;
	      z = y - 1.;
	    }
	    /* ---------------------------------------------------------
	       Evaluate approximation for 1.0 < argument < 2.0
	       ---------------------------------------------------------*/
	    xnum = 0.;
	    xden = 1.;
	    for (i = 0; i < 8; ++i) {
	      xnum = (xnum + p[(int) i]) * z;
	      xden = xden * z + q[(int) i];
	    }
	    res = xnum / xden + 1.;
	    if (y1 < y) {
	      /* --------------------------------------------------------
		 Adjust result for case  0.0 < argument < 1.0
		 -------------------------------------------------------- */
	      res /= y1;
	    } else if (y1 > y) {
	      /* ----------------------------------------------------------
		 Adjust result for case  2.0 < argument < 12.0
		 ---------------------------------------------------------- */
	      for (i = 0; i < n; ++i) {
    		res *= y;
    		y += 1.;
	      }
	    }
	  } else {
	    /* -------------------------------------------------------------
	       Evaluate for argument >= 12.0,
	       ------------------------------------------------------------- */
	    if (y <= xbig) {
	      ysq = y * y;
	      sum = c[6];
	      for (i = 0; i < 6; ++i) {
    		sum = sum / ysq + c[(int) i];
	      }
	      sum = sum / y - y + sqrtpi;
	      /*!* 	    sum += (y - .5) * log(y); *!*/
	      sum += (y - .5) * java.lang.Math.log(y);
	      /*!* 	    res = exp(sum); *!*/
	      res = java.lang.Math.exp(sum);
	    } else {
	      res = Double.POSITIVE_INFINITY;
	      break L_end;
	    }
	  }
	  /* ----------------------------------------------------------------------
	     Final adjustments and return
	     ----------------------------------------------------------------------*/
	  if (parity)
	    res = -res;
	  if (fact != 1.)
	    res = fact / res;
    
	} // L_end:
        return res;
    }
    
    /*
     *
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    void gammalims(double *xmin, double *xmax);
     *
     *  DESCRIPTION
     *
     *    This function alculates the minimum and maximum legal bounds
     *    for x in gammafn(x).  These are not the only bounds, but they
     *    are the only non-trivial ones to calculate.
     *
     *  NOTES
     *
     *    This routine is a translation into C of a Fortran subroutine
     *    by W. Fullerton of Los Alamos Scientific Laboratory.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    /* FIXME: We need an ifdef'ed version of this which gives  */
    /* the exact values when we are using IEEE 754 arithmetic. */
    
    static double[] gammalims(double xmin, double xmax)
    {
        double alnbig, alnsml, xln, xold;
        int i;
    
/*!*     alnsml = log(d1mach(1)); *!*/
        alnsml = java.lang.Math.log(d1mach(1));
        xmin = -alnsml;
    find_xmax: {
        for (i=1; i<=10; ++i) {
	    xold = xmin;
/*!* 	xln = log(*xmin); *!*/
	    xln = java.lang.Math.log(xmin);
	    xmin -= xmin * ((xmin + .5) * xln - xmin - .2258 + alnsml) /
    		(xmin * xln + .5);
/*!* 	if (fabs(xmin - xold) < .005) { *!*/
	    if (java.lang.Math.abs(xmin - xold) < .005) {
		xmin = -(xmin) + .01;
		break find_xmax;
	    }
        }
    
        /* unable to find xmin */
    
        throw new java.lang.ArithmeticException("Math Error: NOCONV");
        // xmin = xmax = Double.NaN;
    
	} //    find_xmax:
    
/*!*     alnbig = log(d1mach(2)); *!*/
        alnbig = java.lang.Math.log(d1mach(2));
        xmax = alnbig;
    done: {
        for (i=1; i<=10; ++i) {
	    xold = xmax;
/*!* 	xln = log(*xmax); *!*/
	    xln = java.lang.Math.log(xmax);
	    xmax -= xmax * ((xmax - .5) * xln - xmax + .9189 - alnbig) /
    		(xmax * xln - .5);
/*!* 	if (fabs(xmax - xold) < .005) { *!*/
	    if (java.lang.Math.abs(xmax - xold) < .005) {
		xmax += -.01;
		break done;
	    }
        }
    
        /* unable to find xmax */
    
        throw new java.lang.ArithmeticException("Math Error: NOCONV");
        // xmin = xmax = Double.NaN;
    
	} //    done:
        xmin = fmax2(xmin, -(xmax) + 1);

	double retval[] = new double[2];
	retval[0] = xmin;
	retval[1] = xmax;
	return(retval);
    }
     
    /*!* #include "DistLib.h" /*4!*/
    
    public static int i1mach(int i)
    {
        switch(i) {
    
        case  1: return 5;
        case  2: return 6;
        case  3: return 0;
        case  4: return 0;
    
        case  5: /*return CHAR_BIT * sizeof(int);*/ throw new java.lang.RuntimeException("Unimplemented Feature.");
        case  6: /*return sizeof(int)/sizeof(char);*/ throw new java.lang.RuntimeException("Unimplemented Feature.");
    
        case  7: return 2;
        case  8: /*return CHAR_BIT * sizeof(int) - 1;*/ throw new java.lang.RuntimeException("Unimplemented Feature.");
        case  9: return java.lang.Integer.MAX_VALUE; /*INT_MAX;*/
    
        case 10: return Constants.FLT_RADIX;
    
        case 11: return Constants.FLT_MANT_DIG;
        case 12: return Constants.FLT_MIN_EXP;
        case 13: return Constants.FLT_MAX_EXP;
    
        case 14: return Constants.DBL_MANT_DIG;
        case 15: return Constants.DBL_MAX_EXP;
        case 16: return Constants.DBL_MIN_EXP;
    
        default: return 0;
        }
    }
    
    int i1mach_(int i)
    {
    	return i1mach(i);
    }
    /*
     *
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    int imax2(int x, int y);
     *
     *  DESCRIPTION
     *
     *    Compute maximum of two integers.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    int imax2(int x, int y)
    {
        return (x < y) ? y : x;
    }
    /*
     *
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    int Math.min(int x, int y);
     *
     *  DESCRIPTION
     *
     *    Compute minimum of two integers.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    int imin2(int x, int y)
    {
        return (x < y) ? x : y;
    }
    /*
     *
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    double lbeta(double a, double b);
     *
     *  DESCRIPTION
     *
     *    This function returns the value of the log beta function.
     *
     *  NOTES
     *
     *    This routine is a translation into C of a Fortran subroutine
     *    by W. Fullerton of Los Alamos Scientific Laboratory.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  lbeta(double a, double b)
    {
        double corr, p, q;
    
        p = q = a;
        if(b < p) p = b;/* := min(a,b) */
        if(b > q) q = b;/* := max(a,b) */
    
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isNaN(a) || Double.isNaN(b))
    	return a + b;
    /*!* #endif /*4!*/
    
        /* both arguments must be >= 0 */
    
        if (p < 0) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
        else if (p == 0) {
    	return Double.POSITIVE_INFINITY;
        }
    /*!* #ifdef IEEE_754 /*4!*/
        else if (Double.isInfinite(q)) {
    	return Double.NEGATIVE_INFINITY;
        }
    /*!* #endif /*4!*/
    
        if (p >= 10) {
    	/* p and q are big. */
    	corr = lgammacor(p) + lgammacor(q) - lgammacor(p + q);
/*!* 	return log(q) * -0.5 + Constants.M_LN_SQRT_2PI + corr *!*/
    	return java.lang.Math.log(q) * -0.5 + Constants.M_LN_SQRT_2PI + corr
/*!* 		+ (p - 0.5) * log(p / (p + q)) + q * logrelerr(-p / (p + q)); *!*/
    		+ (p - 0.5) * java.lang.Math.log(p / (p + q)) + q * logrelerr(-p / (p + q));
        }
        else if (q >= 10) {
    	/* p is small, but q is big. */
    	corr = lgammacor(q) - lgammacor(p + q);
/*!* 	return lgammafn(p) + corr + p - p * log(p + q) *!*/
    	return lgammafn(p) + corr + p - p * java.lang.Math.log(p + q)
    		+ (q - 0.5) * logrelerr(-p / (p + q));
        }
        else
    	/* p and q are small: p <= q > 10. */
/*!* 	return log(gammafn(p) * (gammafn(q) / gammafn(p + q))); *!*/
    	return java.lang.Math.log(gammafn(p) * (gammafn(q) / gammafn(p + q)));
    }
    /*
     *
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    extern int signgam;
     *    double lgammafn(double x);
     *
     *  DESCRIPTION
     *
     *    This function computes log|gamma(x)|.  At the same time
     *    the variable "signgam" is set to the sign of the gamma
     *    function.
     *
     *  NOTES
     *
     *    This routine is a translation into C of a Fortran subroutine
     *    by W. Fullerton of Los Alamos Scientific Laboratory.
     *
     *    The accuracy of this routine compares (very) favourably
     *    with those of the Sun Microsystems portable mathematical
     *    library.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    static int signgam;
    
    public static double  lgammafn(double x)
    {
        double xmax = 0.;
        double dxrel = 0.;
        double ans, y, sinpiy;
    
        if (xmax == 0) {
/*!* 	xmax = d1mach(2)/log(d1mach(2)); *!*/
    	xmax = d1mach(2)/java.lang.Math.log(d1mach(2));
    	dxrel = java.lang.Math.sqrt (d1mach(4));
        }
    
        signgam = 1;
    
    /*!* #ifdef IEEE_754 /*4!*/
        if(Double.isNaN(x)) return x;
    /*!* #endif /*4!*/
    
        if (x <= 0 && x == (int)x) { /* Negative integer argument */
    	throw new java.lang.ArithmeticException("Math Error: RANGE");
	//    	return Double.POSITIVE_INFINITY;/* +Inf, since lgamma(x) = log|gamma(x)| */
        }
    
/*!*     y = fabs(x); *!*/
        y = java.lang.Math.abs(x);
    
        if (y <= 10) {
/*!* 	return log(fabs(gammafn(x))); *!*/
    	return java.lang.Math.log(java.lang.Math.abs(gammafn(x)));
        }
        else { /* y = |x| > 10  */
    
    	if (y > xmax) {
    	    throw new java.lang.ArithmeticException("Math Error: RANGE");
	    //    	    return Double.POSITIVE_INFINITY;
    	}
    
    	if (x > 0)
/*!* 	  return Constants.M_LN_SQRT_2PI + (x - 0.5) * log(x) - x + lgammacor(y); *!*/
    	  return Constants.M_LN_SQRT_2PI + (x - 0.5) * java.lang.Math.log(x) - x + lgammacor(y);
    
    	/* else: x < -10 */
/*!* 	sinpiy = fabs(sin(Constants.M_PI * y)); *!*/
    	sinpiy = java.lang.Math.abs(java.lang.Math.sin(Constants.M_PI * y));
    
    	if (sinpiy == 0) { /* Negative integer argument ===
    			      Now UNNECESSARY: caught above */
    	    System.out.println(" ** should NEVER happen! *** [lgamma.c: Neg.int+ y=%g]\n"+y);
    	    throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	    //    	    return Double.NaN;
    	}
    
/*!* 	ans = Constants.M_LN_SQRT_PId2 + (x - 0.5) * log(y) - x *!*/
    	ans = Constants.M_LN_SQRT_PId2 + (x - 0.5) * java.lang.Math.log(y) - x
/*!* 	      - log(sinpiy) - lgammacor(y); *!*/
    	      - java.lang.Math.log(sinpiy) - lgammacor(y);
    
/*!* 	if(fabs((x - (int)(x - 0.5)) * ans / x) < dxrel) { *!*/
    	if(java.lang.Math.abs((x - (int)(x - 0.5)) * ans / x) < dxrel) {
    
    	    /* The answer is less than half precision because */
    	    /* the argument is too near a negative integer. */
    
    	    throw new java.lang.ArithmeticException("Math Error: PRECISION");
    	}
    
    	if (x > 0)
    	  return ans;
    	else if (((int)(-x))%2 == 0)
    	  signgam = -1;
    	return ans;
        }
    }
    /*
     *
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    double lgammacor(double x);
     *
     *  DESCRIPTION
     *
     *    Compute the log gamma correction factor for x >= 10 so that
     *
     *    log(gamma(x)) = log(sqrt(2*pi))+(x-.5)*log(x)-x+lgammacor(x)
     *
     *  NOTES
     *
     *    This routine is a translation into C of a Fortran subroutine
     *    written by W. Fullerton of Los Alamos Scientific Laboratory.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  lgammacor(double x)
    {
        final  double algmcs[] /*[15]*/ = { 
    	+.1666389480451863247205729650822e+0,
    	-.1384948176067563840732986059135e-4,
    	+.9810825646924729426157171547487e-8,
    	-.1809129475572494194263306266719e-10,
    	+.6221098041892605227126015543416e-13,
    	-.3399615005417721944303330599666e-15,
    	+.2683181998482698748957538846666e-17,
    	-.2868042435334643284144622399999e-19,
    	+.3962837061046434803679306666666e-21,
    	-.6831888753985766870111999999999e-23,
    	+.1429227355942498147573333333333e-24,
    	-.3547598158101070547199999999999e-26,
    	+.1025680058010470912000000000000e-27,
    	-.3401102254316748799999999999999e-29,
    	+.1276642195630062933333333333333e-30
        };
        int nalgm = 0;
        double xbig = 0;
        double xmax = 0;
        double tmp;
    
        if (nalgm == 0) {
    	nalgm = chebyshev_init(algmcs, 15, d1mach(3));
/*!* 	xbig = 1 / sqrt(d1mach(3)); *!*/
    	xbig = 1 / java.lang.Math.sqrt(d1mach(3));
/*!* 	xmax = exp(fmin2(log(d1mach(2) / 12), -log(12 * d1mach(1)))); *!*/
    	xmax = java.lang.Math.exp(fmin2(java.lang.Math.log(d1mach(2) / 12), -java.lang.Math.log(12 * d1mach(1))));
        }
    
        if (x < 10) {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	    //            return Double.NaN;
        }
        else if (x >= xmax) {
            throw new java.lang.ArithmeticException("Math Error: UNDERFLOW");
	    //            return (Double.MIN_VALUE * Double.MIN_VALUE);
        }
        else if (x < xbig) {
            tmp = 10 / x;
            return chebyshev_eval(tmp * tmp * 2 - 1, algmcs, nalgm) / x;
        }
        else return 1 / (x * 12);
    }
    /*
     *
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    double dlnrel(double x);
     *
     *  DESCRIPTION
     *
     *    Compute the relative error logarithm.
     *
     *                      log(1 + x)
     *
     *  NOTES
     *
     *    This code is a translation of a Fortran subroutine of the
     *    same name written by W. Fullerton of Los Alamos Scientific
     *    Laboratory.
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  logrelerr(double x)
    {
        /* series for alnr on the interval -3.75000e-01 to  3.75000e-01 */
        /*                               with weighted error   6.35e-32 */
        /*                                log weighted error  31.20     */
        /*                      significant figures required  30.93     */
        /*                           decimal places required  32.01     */
        final  double alnrcs[] /*[43]*/ = {
    	+.10378693562743769800686267719098e+1,
    	-.13364301504908918098766041553133e+0,
    	+.19408249135520563357926199374750e-1,
    	-.30107551127535777690376537776592e-2,
    	+.48694614797154850090456366509137e-3,
    	-.81054881893175356066809943008622e-4,
    	+.13778847799559524782938251496059e-4,
    	-.23802210894358970251369992914935e-5,
    	+.41640416213865183476391859901989e-6,
    	-.73595828378075994984266837031998e-7,
    	+.13117611876241674949152294345011e-7,
    	-.23546709317742425136696092330175e-8,
    	+.42522773276034997775638052962567e-9,
    	-.77190894134840796826108107493300e-10,
    	+.14075746481359069909215356472191e-10,
    	-.25769072058024680627537078627584e-11,
    	+.47342406666294421849154395005938e-12,
    	-.87249012674742641745301263292675e-13,
    	+.16124614902740551465739833119115e-13,
    	-.29875652015665773006710792416815e-14,
    	+.55480701209082887983041321697279e-15,
    	-.10324619158271569595141333961932e-15,
    	+.19250239203049851177878503244868e-16,
    	-.35955073465265150011189707844266e-17,
    	+.67264542537876857892194574226773e-18,
    	-.12602624168735219252082425637546e-18,
    	+.23644884408606210044916158955519e-19,
    	-.44419377050807936898878389179733e-20,
    	+.83546594464034259016241293994666e-21,
    	-.15731559416479562574899253521066e-21,
    	+.29653128740247422686154369706666e-22,
    	-.55949583481815947292156013226666e-23,
    	+.10566354268835681048187284138666e-23,
    	-.19972483680670204548314999466666e-24,
    	+.37782977818839361421049855999999e-25,
    	-.71531586889081740345038165333333e-26,
    	+.13552488463674213646502024533333e-26,
    	-.25694673048487567430079829333333e-27,
    	+.48747756066216949076459519999999e-28,
    	-.92542112530849715321132373333333e-29,
    	+.17578597841760239233269760000000e-29,
    	-.33410026677731010351377066666666e-30,
    	+.63533936180236187354180266666666e-31,
        };
        int nlnrel = 0;
        double xmin = 0.;
    
        if (nlnrel == 0) {
            nlnrel = chebyshev_init(alnrcs, 43, 0.1 * d1mach(3));
/*!*         xmin = -1.0 + sqrt(d1mach(4)); *!*/
            xmin = -1.0 + java.lang.Math.sqrt(d1mach(4));
        }
    
        if (x <= -1) {
    	throw new java.lang.ArithmeticException("Math Error: DOMAIN");
	//    	return Double.NaN;
        }
    
        if (x < xmin) {
    	/* answer less than half precision because x too near -1 */
    	throw new java.lang.ArithmeticException("Math Error: PRECISION");
        }
    
/*!*     if (fabs(x) <= .375) *!*/
        if (java.lang.Math.abs(x) <= .375)
    	return x * (1 - x * chebyshev_eval(x / .375, alnrcs, nlnrel));
        else
/*!* 	return log(x + 1); *!*/
    	return java.lang.Math.log(x + 1);
    }
    
    /*!* #include "DistLib.h" /*4!*/
    
    /*!* #ifdef IEEE_754 /*4!*/
    /* These are used in IEEE exception handling */
    static double m_zero = 0;
    static double m_one = 1;
    static double m_tiny = Double.MIN_VALUE;
    /*!* #endif /*4!*/
    
    /*!* #ifndef IEEE_754 /*4!*/
    
      /*
    void ml_error(int n)
    {
        switch(n) {
    
        case "Math Error: NONE":
    	(!!!!fixme!!!!) = 0;
    	break;
    
        case "Math Error: DOMAIN":
        case "Math Error: NOCONV":
    	(!!!!fixme!!!!) = EDOM;
    	break;
    
        case "Math Error: RANGE":
    	(!!!!fixme!!!!) = ERANGE;
    	break;
    
        default:
    	break;
        }
    }
    
      */
    /*!* #endif /*4!*/
    /*
     *
     *  SYNOPSIS
     *
     *    #include "DistLib.h"
     *    double sign(double x);
     *
     *  DESCRIPTION
     *
     *    This function computes the  'signum(.)' function:
     *
     *    	sign(x) =  1  if x > 0
     *    	sign(x) =  0  if x == 0
     *    	sign(x) = -1  if x < 0
     */
    
    /*!* #include "DistLib.h" /*4!*/
    
    public static double  sign(double x)
    {
    /*!* #ifdef IEEE_754 /*4!*/
        if (Double.isNaN(x))
    	return x;
    /*!* #endif /*4!*/
        return ((x > 0) ? 1 : ((x == 0)? 0 : -1));
    }
  }
