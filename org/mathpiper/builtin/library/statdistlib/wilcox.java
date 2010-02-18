/*
 *  DistLib : A C Library of Special Functions
 *  Copyright (C) 1998 R Core Team
 * 
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 * data translated from C using perl script translate.pl
 * script version 0.00
 */

package org.mathpiper.builtin.library.statdistlib;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

/**
 * Wrapper of functions for Wilcoxon distribution.
 * <p>
 * This actually the Mann-Whitney Ux statistic.
 */

public class wilcox {
  //private static Log log = LogFactory.getLog(wilcox.class);

  public static final int  WILCOX_MMAX = 50; 
  public static final int  WILCOX_NMAX = 50; 

  /**
   * check values for too large and log complaint
   */
  private static boolean checkSizesLarge(final double m, final double n) {
    if (m >= WILCOX_MMAX) {
      //log.info("m should be less than %d\n"+ WILCOX_MMAX);
      return false;
    }
    if (n >= WILCOX_NMAX) {
      //log.info("n should be less than %d\n"+ WILCOX_NMAX);
      return false;
    }
    return true;    
  }
  
  /**
   * round sizes to integer
   */
  private static void roundSizes(double m, double n) {
    m = Math.floor(m + 0.5);
    n = Math.floor(n + 0.5);
  }

  // table of exact cumulative probabilities
  static private double w[][][] = new double[WILCOX_MMAX][WILCOX_NMAX][];

  /**
   *    The density of the Wilcoxon distribution.
   */
  static private double cwilcox(int k, int m, int n) {
    int u = m * n;
    int c = (int)(u / 2);

    if ((k < 0) || (k > u)) return(0);
    if (k > c) k = u - k;
    int i = m;
    int j = n;
    if (m >= n) {
      i = n;
      j = m;
    }
    if (w[i][j] == null) {
      w[i][j] = new double[c + 1];
      for (int l = 0; l <= c; l++)
        w[i][j][l] = -1;
    }
    if (w[i][j][k] < 0) {
      if ((i == 0) || (j == 0))
        w[i][j][k] = (k == 0)?1.0:0.0;
      else
        w[i][j][k] = cwilcox(k - n, m - 1, n) + cwilcox(k, m, n - 1);
    }
    return(w[i][j][k]);
  }

  /**
   * density function
   * @param x
   * @param m
   * @param n
   * @return density
   */
  public static double density(double x, double m, double n) {
    /*!* #ifdef IEEE_754 /*4!*/
    /* NaNs propagated correctly */
    if (Double.isNaN(x) || Double.isNaN(m) || Double.isNaN(n)) return x + m + n;
    /*!* #endif /*4!*/
    roundSizes(m,n);
    if (m <= 0 || n <= 0) {
      throw new java.lang.ArithmeticException("Math Error: DOMAIN");
      //        return Double.NaN;
    }
    if (!checkSizesLarge(m,n)) return Double.NaN;
    
    /*!*   x = floor(x + 0.5); *!*/
    x = java.lang.Math.floor(x + 0.5);
    if ((x < 0) || (x > m * n))
      return 0;
    /*!*   return(cwilcox(x, m, n) / choose(m + n, n)); *!*/
    return(cwilcox((int) x, (int) m, (int) n) / misc.choose(m + n, n));
  }

  /**
   * Cumulative distribution function of the Wilcoxon distribution.
   */
  public static double cumulative(double x, double m, double n) {

    /*!* #ifdef IEEE_754 /*4!*/
    if (Double.isNaN(x) || Double.isNaN(m) || Double.isNaN(n))
      return x + m + n;
    if (Double.isInfinite(m) || Double.isInfinite(n)) {
      throw new java.lang.ArithmeticException("Math Error: DOMAIN");
      //        return Double.NaN;
    }
    /*!* #endif /*4!*/
    roundSizes(m,n);
    if (m <= 0 || n <= 0) {
      throw new java.lang.ArithmeticException("Math Error: DOMAIN");
      //        return Double.NaN;
    }
    if (!checkSizesLarge(m,n)) return Double.NaN;
    /*!*   x = floor(x + 0.5); *!*/
    x = java.lang.Math.floor(x + 0.5);
    if (x < 0.0) return 0;
    if (x >= m * n) return 1;
    double p = 0.0;
    for (int i = 0; i <= x; i++)
      p += density(i, m, n);
    return(p);
  }

  /**
   * The quantile function of the Wilcoxon distribution.
   */
  public static double  quantile(double x, double m, double n) {

    /*!* #ifdef IEEE_754 /*4!*/
    if (Double.isNaN(x) || Double.isNaN(m) || Double.isNaN(n))
      return x + m + n;
    if(Double.isInfinite(x) || Double.isInfinite(m) || Double.isInfinite(n)) {
      throw new java.lang.ArithmeticException("Math Error: DOMAIN");
      //    	return Double.NaN;
    }
    /*!* #endif /*4!*/

    roundSizes(m,n);
    if (x < 0 || x > 1 || m <= 0 || n <= 0) {
      throw new java.lang.ArithmeticException("Math Error: DOMAIN");
      //    	return Double.NaN;
    };
    if (!checkSizesLarge(m,n)) return Double.NaN;

    if (x == 0) return(0.0);
    if (x == 1) return(m * n);
    double p = 0.0;
    double q = 0.0;
    for (;;) {
      /* Don't call cumulative() for efficiency */
      p += density(q, m, n);
      if (p >= x)
        return(q);
      q++;
    }
  }

  /**
   *    Random variates from the Wilcoxon distribution.
   */
  public static double random(double m, double n) {

    /*!* #ifdef IEEE_754 /*4!*/
    /* NaNs propagated correctly */
    if (Double.isNaN(m) || Double.isNaN(n)) return(m + n);
    /*!* #endif /*4!*/
    roundSizes(m,n);
    if ((m < 0) || (n < 0)) {
      throw new java.lang.ArithmeticException("Math Error: DOMAIN");
      //        return Double.NaN;
    }
    if ((m == 0) || (n == 0))
      return(0);
    double r = 0.0;
    int k = (int) (m + n);
    int[] x = new int[k];
    for (int i = 0; i < k; i++)
      x[i] = i;
    for (int i = 0; i < n; i++) {
      /*!*     j = floor(k * sunif()); *!*/
      int j = (int) java.lang.Math.floor(k * uniform.random());
      r += x[j];
      x[j] = x[--k];
    }
    return(r - n * (n - 1) / 2);
  }
}
