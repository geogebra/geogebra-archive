package org.mathpiper.builtin.library.statdistlib;

/* data translated from C using perl script translate.pl */
/* script version 0.00                               */


import java.lang.*;
import java.lang.Math;
import java.lang.Double;

public class polygamma 
  { 
/***UNUSED***    /*
/***UNUSED***     *  DistLib : A C Library of Special Functions
/***UNUSED***     *  Copyright (C) 1998 Ross Ihaka
/***UNUSED***     *
/***UNUSED***     *  This program is free software; you can redistribute it and/or modify
/***UNUSED***     *  it under the terms of the GNU General Public License as published by
/***UNUSED***     *  the Free Software Foundation; either version 2 of the License, or
/***UNUSED***     *  (at your option) any later version.
/***UNUSED***     *
/***UNUSED***     *  This program is distributed in the hope that it will be useful,
/***UNUSED***     *  but WITHOUT ANY WARRANTY; without even the implied warranty of
/***UNUSED***     *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
/***UNUSED***     *  GNU General Public License for more details.
/***UNUSED***     *
/***UNUSED***     *  You should have received a copy of the GNU General Public License
/***UNUSED***     *  along with this program; if not, write to the Free Software
/***UNUSED***     *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
/***UNUSED***     *
/***UNUSED***     *  SYNOPSIS
/***UNUSED***     *
/***UNUSED***     *    #include "DistLib.h"
/***UNUSED***     *    void dpsifn(double x, int n, int kode, int m,
/***UNUSED***     *		  double *ans, int *nz, int *ierr)
/***UNUSED***     *    double digamma(double x);
/***UNUSED***     *    double trigamma(double x)
/***UNUSED***     *    double tetragamma(double x)
/***UNUSED***     *    double pentagamma(double x)
/***UNUSED***     *
/***UNUSED***     *  DESCRIPTION
/***UNUSED***     *
/***UNUSED***     *    Compute the derivatives of the psi function
/***UNUSED***     *    and polygamma functions.
/***UNUSED***     *
/***UNUSED***     *    The following definitions are used in dpsifn:
/***UNUSED***     *
/***UNUSED***     *    Definition 1
/***UNUSED***     *
/***UNUSED***     *	 psi(x) = d/dx (ln(gamma(x)),  the first derivative of
/***UNUSED***     *				       the log gamma function.
/***UNUSED***     *
/***UNUSED***     *    Definition 2
/***UNUSED***     *		     k	 k
/***UNUSED***     *	 psi(k,x) = d /dx (psi(x)),    the k-th derivative
/***UNUSED***     *				       of psi(x).
/***UNUSED***     *
/***UNUSED***     *
/***UNUSED***     *    "dpsifn" computes a sequence of scaled derivatives of
/***UNUSED***     *    the psi function; i.e. for fixed x and m it computes
/***UNUSED***     *    the m-member sequence
/***UNUSED***     *
/***UNUSED***     *		  ((-1)**(k+1)/gamma(k+1))*psi(k,x)
/***UNUSED***     *		     for k = n,...,n+m-1
/***UNUSED***     *
/***UNUSED***     *    where psi(k,x) is as defined above.   For kode=1, dpsifn
/***UNUSED***     *    returns the scaled derivatives as described.  kode=2 is
/***UNUSED***     *    operative only when k=0 and in that case dpsifn returns
/***UNUSED***     *    -psi(x) + ln(x).	That is, the logarithmic behavior for
/***UNUSED***     *    large x is removed when kode=2 and k=0.  When sums or
/***UNUSED***     *    differences of psi functions are computed the logarithmic
/***UNUSED***     *    terms can be combined analytically and computed separately
/***UNUSED***     *    to help retain significant digits.
/***UNUSED***     *
/***UNUSED***     *    Note that dpsifn(x, 0, 1, 1, ans) results in ans = -psi(x).
/***UNUSED***     *
/***UNUSED***     *  INPUT
/***UNUSED***     *
/***UNUSED***     *	x     - argument, x > 0.
/***UNUSED***     *
/***UNUSED***     *	n     - first member of the sequence, 0 <= n <= 100
/***UNUSED***     *		n == 0 gives ans(1) = -psi(x)	    for kode=1
/***UNUSED***     *					 -psi(x)+ln(x) for kode=2
/***UNUSED***     *
/***UNUSED***     *	kode  - selection parameter
/***UNUSED***     *		kode == 1 returns scaled derivatives of the
/***UNUSED***     *		psi function.
/***UNUSED***     *		kode == 2 returns scaled derivatives of the
/***UNUSED***     *		psi function except when n=0. In this case,
/***UNUSED***     *		ans(1) = -psi(x) + ln(x) is returned.
/***UNUSED***     *
/***UNUSED***     *	m     - number of members of the sequence, m >= 1
/***UNUSED***     *
/***UNUSED***     *  OUTPUT
/***UNUSED***     *
/***UNUSED***     *	ans   - a vector of length at least m whose first m
/***UNUSED***     *		components contain the sequence of derivatives
/***UNUSED***     *		scaled according to kode.
/***UNUSED***     *
/***UNUSED***     *	nz    - underflow flag
/***UNUSED***     *		nz == 0, a normal return
/***UNUSED***     *		nz != 0, underflow, last nz components of ans are
/***UNUSED***     *			 set to zero, ans(m-k+1)=0.0, k=1,...,nz
/***UNUSED***     *
/***UNUSED***     *	ierr  - error flag
/***UNUSED***     *		ierr=0, a normal return, computation completed
/***UNUSED***     *		ierr=1, input error,	 no computation
/***UNUSED***     *		ierr=2, overflow,	 x too small or n+m-1 too
/***UNUSED***     *			large or both
/***UNUSED***     *		ierr=3, error,		 n too large. dimensioned
/***UNUSED***     *			array trmr(nmax) is not large enough for n
/***UNUSED***     *
/***UNUSED***     *    The nominal computational accuracy is the maximum of unit
/***UNUSED***     *    roundoff (d1mach(4)) and 1e-18 since critical constants
/***UNUSED***     *    are given to only 18 digits.
/***UNUSED***     *
/***UNUSED***     *    The basic method of evaluation is the asymptotic expansion
/***UNUSED***     *    for large x >= xmin followed by backward recursion on a two
/***UNUSED***     *    term recursion relation
/***UNUSED***     *
/***UNUSED***     *	     w(x+1) + x**(-n-1) = w(x).
/***UNUSED***     *
/***UNUSED***     *    this is supplemented by a series
/***UNUSED***     *
/***UNUSED***     *	     sum( (x+k)**(-n-1) , k=0,1,2,... )
/***UNUSED***     *
/***UNUSED***     *    which converges rapidly for large n. both xmin and the
/***UNUSED***     *    number of terms of the series are calculated from the unit
/***UNUSED***     *    roundoff of the machine environment.
/***UNUSED***     *
/***UNUSED***     *  AUTHOR
/***UNUSED***     *
/***UNUSED***     *    Amos, D. E.  (Fortran)
/***UNUSED***     *    Ross Ihaka   (C Translation)
/***UNUSED***     *
/***UNUSED***     *  REFERENCES
/***UNUSED***     *
/***UNUSED***     *    Handbook of Mathematical Functions,
/***UNUSED***     *    National Bureau of Standards Applied Mathematics Series 55,
/***UNUSED***     *    Edited by M. Abramowitz and I. A. Stegun, equations 6.3.5,
/***UNUSED***     *    6.3.18, 6.4.6, 6.4.9 and 6.4.10, pp.258-260, 1964.
/***UNUSED***     *
/***UNUSED***     *    D. E. Amos, (1983). "A Portable Fortran Subroutine for
/***UNUSED***     *    Derivatives of the Psi Function", Algorithm 610,
/***UNUSED***     *    TOMS 9(4), pp. 494-502.
/***UNUSED***     *
/***UNUSED***     *    Routines called: d1mach, i1mach.
/***UNUSED***     */
/***UNUSED***    
/***UNUSED***    /*!* #include "DistLib.h" /*4!*/
/***UNUSED***    
/***UNUSED***    	/* Bernoulli Numbers */
/***UNUSED***    
/***UNUSED***    static private double b[] = {
/***UNUSED***	00, /** DUMMY ENTRY SO INDEXING FROM 1 WORKS **/
/***UNUSED***         1.00000000000000000e+00,
/***UNUSED***        -5.00000000000000000e-01,
/***UNUSED***         1.66666666666666667e-01,
/***UNUSED***        -3.33333333333333333e-02,
/***UNUSED***         2.38095238095238095e-02,
/***UNUSED***        -3.33333333333333333e-02,
/***UNUSED***         7.57575757575757576e-02,
/***UNUSED***        -2.53113553113553114e-01,
/***UNUSED***         1.16666666666666667e+00,
/***UNUSED***        -7.09215686274509804e+00,
/***UNUSED***         5.49711779448621554e+01,
/***UNUSED***        -5.29124242424242424e+02,
/***UNUSED***         6.19212318840579710e+03,
/***UNUSED***        -8.65802531135531136e+04,
/***UNUSED***         1.42551716666666667e+06,
/***UNUSED***        -2.72982310678160920e+07,
/***UNUSED***         6.01580873900642368e+08,
/***UNUSED***        -1.51163157670921569e+10,
/***UNUSED***         4.29614643061166667e+11,
/***UNUSED***        -1.37116552050883328e+13,
/***UNUSED***         4.88332318973593167e+14,
/***UNUSED***        -1.92965793419400681e+16
/***UNUSED***    };
/***UNUSED***    
/***UNUSED***      //    static private double *b = (double *)&bvalues -1;
/***UNUSED***    static private int nmax = 100;
/***UNUSED***    
/***UNUSED***    public static int ierr = 0;
/***UNUSED***
/***UNUSED***    static double[] dpsifn(double x, int n, int kode, int m, int nz) 
/***UNUSED***    {
/***UNUSED***	double ans[] = new double[n+1];
/***UNUSED***	double retval[] = new double[n];
/***UNUSED***        int i, j, k, mm, mx, nn, np, nx, fn;
/***UNUSED***        double arg, den, elim, eps, fln, fx, rln, rxsq;
/***UNUSED***    	double r1m4, r1m5, s, slope, t, ta, tk, tol, tols, tss, tst;
/***UNUSED***    	double tt, t1, t2, wdtol, xdmln, xdmy, xinc, xln, xm, xmin;
/***UNUSED***    	double xq, yint;
/***UNUSED***        double trm[] = new double[23], trmr[] = new double[101];
/***UNUSED***    
/***UNUSED***        ierr = 0;
/***UNUSED***        if (x <= 0.0 || n < 0 || kode < 1 || kode > 2 || m < 1) {
/***UNUSED***    	ierr = 1;
/***UNUSED***    	return ans;
/***UNUSED***        }
/***UNUSED***    
/***UNUSED***        /* fortran adjustment */
/***UNUSED***        //ans--;
/***UNUSED***    
/***UNUSED***        nz = 0;
/***UNUSED***        mm = m;
/***UNUSED*** /*!*     nx = Math.min(-i1mach(15), i1mach(16)); *!*/
/***UNUSED***        nx = Math.min(-misc.i1mach(15), misc.i1mach(16));
/***UNUSED*** /*!*     r1m5 = d1mach(5); *!*/
/***UNUSED***        r1m5 = misc.d1mach(5);
/***UNUSED*** /*!*     r1m4 = d1mach(4) * 0.5; *!*/
/***UNUSED***        r1m4 = misc.d1mach(4) * 0.5;
/***UNUSED*** /*!*     wdtol = fmax2(r1m4, 0.5e-18); *!*/
/***UNUSED***        wdtol = Math.max(r1m4, 0.5e-18);
/***UNUSED***    
/***UNUSED***        /* elim = approximate exponential over and underflow limit */
/***UNUSED***    
/***UNUSED***        elim = 2.302 * (nx * r1m5 - 3.0);
/***UNUSED*** /*!*     xln = log(x); *!*/
/***UNUSED***        xln = java.lang.Math.log(x);
/***UNUSED***        for(;;) {
/***UNUSED***    	nn = n + mm - 1;
/***UNUSED***    	fn = nn;
/***UNUSED***    	t = (fn + 1) * xln;
/***UNUSED***    
/***UNUSED***    	/* overflow and underflow test for small and large x */
/***UNUSED***    
/***UNUSED*** /*!* 	if (fabs(t) > elim) { *!*/
/***UNUSED***    	if (java.lang.Math.abs(t) > elim) {
/***UNUSED***    	    if (t <= 0.0) {
/***UNUSED***    		nz = 0;
/***UNUSED***    		ierr = 2;
/***UNUSED***    		{
/***UNUSED***		    for(int count=0; count<n; count++)
/***UNUSED***			retval[count] = ans[count+1];
/***UNUSED***		    return retval ;
/***UNUSED***		}
/***UNUSED***    	    }
/***UNUSED***    	}
/***UNUSED***    	else {
/***UNUSED***    	    if (x < wdtol) {
/***UNUSED*** /*!* 		ans[1] = pow(x, -n-1.0); *!*/
/***UNUSED***    		ans[1] = java.lang.Math.pow(x, -n-1.0);
/***UNUSED***    		if (mm != 1) {
/***UNUSED***    		    k = 1;
/***UNUSED***    		    for(i=2 ; i<=mm ; i++) {
/***UNUSED***    			ans[k+1] = ans[k] / x;
/***UNUSED***    			k = k+1;
/***UNUSED***    		    }
/***UNUSED***    		}
/***UNUSED***    		if (n == 0 && kode == 2)
/***UNUSED***    		    ans[1] = ans[1] + xln;
/***UNUSED***    		{
/***UNUSED***		    for(int count=0; count<n; count++)
/***UNUSED***			retval[count] = ans[count+1];
/***UNUSED***		    return retval ;
/***UNUSED***		}
/***UNUSED***    	    }
/***UNUSED***    
/***UNUSED***    	    /* compute xmin and the number of terms of the series,  fln+1 */
/***UNUSED***    
/***UNUSED*** /*!* 	    rln = r1m5 * i1mach(14); *!*/
/***UNUSED***    	    rln = r1m5 * misc.i1mach(14);
/***UNUSED*** /*!* 	    rln = fmin2(rln, 18.06); *!*/
/***UNUSED***    	    rln = Math.min(rln, 18.06);
/***UNUSED*** /*!* 	    fln = fmax2(rln, 3.0) - 3.0; *!*/
/***UNUSED***    	    fln = Math.max(rln, 3.0) - 3.0;
/***UNUSED***    	    yint = 3.50 + 0.40 * fln;
/***UNUSED***    	    slope = 0.21 + fln * (0.0006038 * fln + 0.008677);
/***UNUSED***    	    xm = yint + slope * fn;
/***UNUSED***    	    mx = (int)xm + 1;
/***UNUSED***    	    xmin = mx;
/***UNUSED***    	    if (n != 0) {
/***UNUSED*** /*!* 		xm = -2.302 * rln - fmin2(0.0, xln); *!*/
/***UNUSED***    		xm = -2.302 * rln - Math.min(0.0, xln);
/***UNUSED***    		arg = xm / n;
/***UNUSED*** /*!* 		arg = fmin2(0.0, arg); *!*/
/***UNUSED***    		arg = Math.min(0.0, arg);
/***UNUSED*** /*!* 		eps = exp(arg); *!*/
/***UNUSED***    		eps = java.lang.Math.exp(arg);
/***UNUSED***    		xm = 1.0 - eps;
/***UNUSED*** /*!* 		if (fabs(arg) < 1.0e-3) *!*/
/***UNUSED***    		if (java.lang.Math.abs(arg) < 1.0e-3)
/***UNUSED***    		    xm = -arg;
/***UNUSED***    		fln = x * xm / eps;
/***UNUSED***    		xm = xmin - x;
/***UNUSED***    		if (xm > 7.0 && fln < 15.0)
/***UNUSED***    		    break;
/***UNUSED***    	    }
/***UNUSED***    	    xdmy = x;
/***UNUSED***    	    xdmln = xln;
/***UNUSED***    	    xinc = 0.0;
/***UNUSED***    	    if (x < xmin) {
/***UNUSED***    		nx = (int)x;
/***UNUSED***    		xinc = xmin - nx;
/***UNUSED***    		xdmy = x + xinc;
/***UNUSED*** /*!* 		xdmln = log(xdmy); *!*/
/***UNUSED***    		xdmln = java.lang.Math.log(xdmy);
/***UNUSED***    	    }
/***UNUSED***    
/***UNUSED***    	    /* generate w(n+mm-1, x) by the asymptotic expansion */
/***UNUSED***    
/***UNUSED***    	    t = fn * xdmln;
/***UNUSED***    	    t1 = xdmln + xdmln;
/***UNUSED***    	    t2 = t + xdmln;
/***UNUSED*** /*!* 	    tk = fmax2(fabs(t), fmax2(fabs(t1), fabs(t2))); *!*/
/***UNUSED***    	    tk = Math.max(java.lang.Math.abs(t), Math.max(java.lang.Math.abs(t1), java.lang.Math.abs(t2)));
/***UNUSED***    	    if (tk <= elim)
/***UNUSED***    		break L10;
/***UNUSED***    	}
/***UNUSED***
/***UNUSED***	nz = nz + 1;
/***UNUSED***    	ans[mm] = 0.0;
/***UNUSED***    	mm = mm - 1;
/***UNUSED***    	if (mm == 0)
/***UNUSED***    	   {
/***UNUSED***	       for(int count=0; count<n; count++)
/***UNUSED***		   retval[count] = ans[count+1];
/***UNUSED***	       return retval ;
/***UNUSED***	   }
/***UNUSED***        }
/***UNUSED***        nn = (int)fln + 1;
/***UNUSED***        np = n + 1;
/***UNUSED***        t1 = (n + 1) * xln;
/***UNUSED*** /*!*     t = exp(-t1); *!*/
/***UNUSED***        t = java.lang.Math.exp(-t1);
/***UNUSED***        s = t;
/***UNUSED***        den = x;
/***UNUSED***        for(i=1 ; i<=nn ; i++) {
/***UNUSED***    	den = den + 1.0;
/***UNUSED*** /*!* 	trm[i] = pow(den, (double)-np); *!*/
/***UNUSED***    	trm[i] = java.lang.Math.pow(den, (double)-np);
/***UNUSED***    	s = s + trm[i];
/***UNUSED***        }
/***UNUSED***        ans[1] = s;
/***UNUSED***        if (n == 0 && kode == 2)
/***UNUSED***    	ans[1] = s + xln;
/***UNUSED***    
/***UNUSED***        if (mm!=1) {
/***UNUSED***    
/***UNUSED***    	/* generate higher derivatives,	 j > n */
/***UNUSED***    
/***UNUSED***    	tol = wdtol / 5.0;
/***UNUSED***    	for(j=2 ; j<=mm ; j++) {
/***UNUSED***    	    t = t / x;
/***UNUSED***    	    s = t;
/***UNUSED***    	    tols = t * tol;
/***UNUSED***    	    den = x;
/***UNUSED***    	    for(i=1 ; i<=nn ; i++) {
/***UNUSED***    		den = den + 1.0;
/***UNUSED***    		trm[i] = trm[i] / den;
/***UNUSED***    		s = s + trm[i];
/***UNUSED***    		if (trm[i] < tols)
/***UNUSED***    		    break;
/***UNUSED***    	    }
/***UNUSED***    	    ans[j] = s;
/***UNUSED***    	}
/***UNUSED***        }
/***UNUSED***        {
/***UNUSED***	    for(int count=0; count<n; count++)
/***UNUSED***		retval[count] = ans[count+1];
/***UNUSED***	    return retval ;
/***UNUSED***	}
/***UNUSED***
/***UNUSED*** /*!*   L10:	tss = exp(-t); *!*/
/***UNUSED***      L10:	tss = java.lang.Math.exp(-t);
/***UNUSED***        tt = 0.5 / xdmy;
/***UNUSED***        t1 = tt;
/***UNUSED***        tst = wdtol * tt;
/***UNUSED***        if (nn != 0)
/***UNUSED***    	t1 = tt + 1.0 / fn;
/***UNUSED***        rxsq = 1.0 / (xdmy * xdmy);
/***UNUSED***        ta = 0.5 * rxsq;
/***UNUSED***        t = (fn + 1) * ta;
/***UNUSED***        s = t * b[3];
/***UNUSED*** /*!*     if (fabs(s) >= tst) { *!*/
/***UNUSED***        if (java.lang.Math.abs(s) >= tst) {
/***UNUSED***    	tk = 2.0;
/***UNUSED***    	for(k=4 ; k<=22 ; k++) {
/***UNUSED***    	    t = t * ((tk + fn + 1)/(tk + 1.0))*((tk + fn)/(tk + 2.0)) * rxsq;
/***UNUSED***    	    trm[k] = t * b[k];
/***UNUSED*** /*!* 	    if (fabs(trm[k]) < tst) *!*/
/***UNUSED***    	    if (java.lang.Math.abs(trm[k]) < tst)
/***UNUSED***    		break;
/***UNUSED***    	    s = s + trm[k];
/***UNUSED***    	    tk = tk + 2.0;
/***UNUSED***    	}
/***UNUSED***        }
/***UNUSED***        s = (s + t1) * tss;
/***UNUSED***        if (xinc != 0.0) {
/***UNUSED***    
/***UNUSED***    	/* backward recur from xdmy to x */
/***UNUSED***    
/***UNUSED***    	nx = (int)xinc;
/***UNUSED***    	np = nn + 1;
/***UNUSED***    	if (nx > nmax) {
/***UNUSED***    	    nz = 0;
/***UNUSED***    	    ierr = 3;
/***UNUSED***	    {
/***UNUSED***		for(int count=0; count<n; count++)
/***UNUSED***		    retval[count] = ans[count+1];
/***UNUSED***		return retval ;
/***UNUSED***	    }    
/***UNUSED***    	}
/***UNUSED***    	else {
/***UNUSED***    	    if (nn==0)
/***UNUSED***    		break L20;
/***UNUSED***    	    xm = xinc - 1.0;
/***UNUSED***    	    fx = x + xm;
/***UNUSED***    
/***UNUSED***    	    /* this loop should not be changed. fx is accurate when x is small */
/***UNUSED***    
/***UNUSED***    	    for(i=1 ; i<=nx ; i++) {
/***UNUSED*** /*!* 		trmr[i] = pow(fx, (double)-np); *!*/
/***UNUSED***    		trmr[i] = java.lang.Math.pow(fx, (double)-np);
/***UNUSED***    		s = s + trmr[i];
/***UNUSED***    		xm = xm - 1.0;
/***UNUSED***    		fx = x + xm;
/***UNUSED***    	    }
/***UNUSED***    	}
/***UNUSED***        }
/***UNUSED***        ans[mm] = s;
/***UNUSED***    L30: {
/***UNUSED***        if (fn == 0)
/***UNUSED***    	break L30;
/***UNUSED***    
/***UNUSED***        /* generate lower derivatives,  j < n+mm-1 */
/***UNUSED***    
/***UNUSED***        if (mm == 1)
/***UNUSED***    	{
/***UNUSED***	    for(int count=0; count<n; count++)
/***UNUSED***		retval[count] = ans[count+1];
/***UNUSED***	    return retval ;
/***UNUSED***	}
/***UNUSED***
/***UNUSED***    L20: {
/***UNUSED***        for(j=2 ; j<=mm ; j++) {
/***UNUSED***	    fn = fn - 1;
/***UNUSED***	    tss = tss * xdmy;
/***UNUSED***	    t1 = tt;
/***UNUSED***	    if (fn!=0)
/***UNUSED***		t1 = tt + 1.0 / fn;
/***UNUSED***	    t = (fn + 1) * ta;
/***UNUSED***	    s = t * b[3];
/***UNUSED***	    /*!* 	if (fabs(s) >= tst) { *!*/
/***UNUSED***	    if (java.lang.Math.abs(s) >= tst) {
/***UNUSED***		tk = 4 + fn;
/***UNUSED***		for(k=4 ; k<=22 ; k++) {
/***UNUSED***		    trm[k] = trm[k] * (fn + 1) / tk;
/***UNUSED***		    /*!* 		if (fabs(trm[k]) < tst) *!*/
/***UNUSED***		    if (java.lang.Math.abs(trm[k]) < tst)
/***UNUSED***			break;
/***UNUSED***		    s = s + trm[k];
/***UNUSED***		    tk = tk + 2.0;
/***UNUSED***		}
/***UNUSED***	    }
/***UNUSED***	    s = (s + t1) * tss;
/***UNUSED***	    
/***UNUSED***	    if (xinc != 0.0) {
/***UNUSED***		if (fn == 0)
/***UNUSED***		    break L20;
/***UNUSED***		xm = xinc - 1.0;
/***UNUSED***		fx = x + xm;
/***UNUSED***		for(i=1 ; i<=nx ; i++) {
/***UNUSED***		    trmr[i] = trmr[i] * fx;
/***UNUSED***		    s = s + trmr[i];
/***UNUSED***		    xm = xm - 1.0;
/***UNUSED***		    fx = x + xm;
/***UNUSED***		}
/***UNUSED***	    }
/***UNUSED***	    mx = mm - j + 1;
/***UNUSED***	    ans[mx] = s;
/***UNUSED***	    if (fn == 0)
/***UNUSED***		break L30;
/***UNUSED***        }
/***UNUSED***        {
/***UNUSED***	    for(int count=0; count<n; count++)
/***UNUSED***		retval[count] = ans[count+1];
/***UNUSED***	    return retval ;
/***UNUSED***	}
/***UNUSED***	
/***UNUSED***	} // L20: 
/***UNUSED***	for(i=1 ; i<=nx ; i++)
/***UNUSED***	s = s + 1.0 / (x + nx - i);
/***UNUSED***	
/***UNUSED***	} //L30:
/***UNUSED***	if (kode!=2)
/***UNUSED***	    ans[1] = s - xdmln;
/***UNUSED***	else if (xdmy != x) {
/***UNUSED***	    xq = xdmy / x;
/***UNUSED***	    /*!*       ans[1] = s - log(xq); *!*/
/***UNUSED***	    ans[1] = s - java.lang.Math.log(xq);
/***UNUSED***	}
/***UNUSED***        {
/***UNUSED***	    for(int count=0; count<n; count++)
/***UNUSED***		retval[count] = ans[count+1];
/***UNUSED***	    return retval ;
/***UNUSED***	}
/***UNUSED***    }
/***UNUSED***      
/***UNUSED***    double digamma(double x)
/***UNUSED***    {
/***UNUSED***        double ans[];
/***UNUSED***        int nz = 0;
/***UNUSED***    /*!* #ifdef IEEE_754 /*4!*/
/***UNUSED***        if(Double.isNaN(x)) return x;
/***UNUSED***    /*!* #endif /*4!*/
/***UNUSED***        ans = dpsifn(x, 0, 1, 1, nz);
/***UNUSED***	if(ierr != 0) {
/***UNUSED***	  throw new java.lang.ArithmeticException("Math Error: DOMAIN");
/***UNUSED***	  //    	(!!!!fixme!!!!) = EDOM;
/***UNUSED***	  //    	return -Double.MAX_VALUE;
/***UNUSED***	}
/***UNUSED***        return -ans[0];
/***UNUSED***    }
/***UNUSED***    
/***UNUSED***    double trigamma(double x)
/***UNUSED***    {
/***UNUSED***        double ans[];
/***UNUSED***        int nz = 0;
/***UNUSED***    /*!* #ifdef IEEE_754 /*4!*/
/***UNUSED***        if(Double.isNaN(x)) return x;
/***UNUSED***    /*!* #endif /*4!*/
/***UNUSED***        ans = dpsifn(x, 1, 1, 1, nz);
/***UNUSED***	if(ierr != 0) {
/***UNUSED***	  throw new java.lang.ArithmeticException("Math Error: DOMAIN");
/***UNUSED***	//    	(!!!!fixme!!!!) = EDOM;
/***UNUSED***	//    	return -Double.MAX_VALUE;
/***UNUSED***	}
/***UNUSED***        return ans[0];
/***UNUSED***    }
/***UNUSED***    
/***UNUSED***    double tetragamma(double x)
/***UNUSED***    {
/***UNUSED***        double ans[];
/***UNUSED***        int nz = 0;
/***UNUSED***    /*!* #ifdef IEEE_754 /*4!*/
/***UNUSED***        if(Double.isNaN(x)) return x;
/***UNUSED***    /*!* #endif /*4!*/
/***UNUSED***        ans = dpsifn(x, 2, 1, 1, nz);
/***UNUSED***	if(ierr != 0) {
/***UNUSED***	  throw new java.lang.ArithmeticException("Math Error: DOMAIN");
/***UNUSED***	//    	(!!!!fixme!!!!) = EDOM;
/***UNUSED***	//    	return -Double.MAX_VALUE;
/***UNUSED***	}
/***UNUSED***        return -2.0 * ans[0];
/***UNUSED***    }
/***UNUSED***    
/***UNUSED***    double pentagamma(double x)
/***UNUSED***    {
/***UNUSED***        double ans[];
/***UNUSED***        int nz = 0;
/***UNUSED***    /*!* #ifdef IEEE_754 /*4!*/
/***UNUSED***        if(Double.isNaN(x)) return x;
/***UNUSED***    /*!* #endif /*4!*/
/***UNUSED***        ans = dpsifn(x, 3, 1, 1, nz);
/***UNUSED***	if(ierr != 0) {
/***UNUSED***	  throw new java.lang.ArithmeticException("Math Error: DOMAIN");
/***UNUSED***	//    	(!!!!fixme!!!!) = EDOM;
/***UNUSED***	//    	return -Double.MAX_VALUE;
/***UNUSED***        }
/***UNUSED***        return 6.0 * ans[0];
/***UNUSED***    }
/***UNUSED***  
/***UNUSED***/
  }

