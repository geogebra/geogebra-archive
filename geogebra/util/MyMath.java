/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.util;

import geogebra.kernel.Kernel;

import java.math.BigDecimal;

/**
 * @author Markus Hohenwarter
 */
public final class MyMath {
	
	public static final double LOG10 = Math.log(10);
	public static final double LOG2 = Math.log(2);
	public static final double ONE_THIRD = 1d / 3d;


	/**
	 * Cubic root
	 */
	final public static double cbrt(double a) {
		if (a > 0.0)    
	    	return  Math.pow(a, ONE_THIRD);
	    else      
	    	return -Math.pow(-a, ONE_THIRD);  
	}
	
	
	final public static double sgn(Kernel kernel, double a) {
		if (kernel.isZero(a)) 
			return 0.0;
	    else if (a > 0.0)    
	    	return  1.0;
	    else      
	    	return -1.0;    
	}
	
	final public static double cosh(double a) {  
		return  (Math.exp(a) + Math.exp(-a)) * 0.5; 
	}
	
	final public static double sinh(double a) {  
		return (Math.exp(a) - Math.exp(-a)) * 0.5; 
	}
	
	final public static double tanh(double a) {  
		double e = Math.exp(2.0 * a);
		return (e - 1.0) / (e + 1.0);  
	}
	
	final public static double csch(double a) {  
		return  1 / sinh(a);
	}
	
	final public static double sech(double a) {  
		return 1 / cosh(a);
	}
	
	final public static double coth(double a) {  
		double e = Math.exp(2.0 * a);
		return (e + 1.0) / (e - 1.0);  
	}
	
	final public static double acosh(double a) {  
		return Math.log(a + Math.sqrt(a*a - 1.0));
	}

	final public static double asinh(double a) {  
		return Math.log(a + Math.sqrt(a*a + 1.0));
	}

	final public static double atanh(double a) {		  
		return Math.log((1.0 + a)/(1.0 - a)) * 0.5;
	}		
	
	final public static double csc(double a) {		  
		return 1 / Math.sin(a);
	}		
	
	final public static double sec(double a) {		  
		return 1 / Math.cos(a);
	}		
	
	final public static double cot(double a) {		  
		return Math.cos(a) / Math.sin(a);
	}		
	
	final public static double gamma(double x, Kernel kernel) {		

		// Michael Borcherds 2008-05-04
		 if (x <= 0 && kernel.isEqual( x, Math.round( x ) )) return Double.NaN; // negative integers

		 //		 Michael Borcherds 2007-10-15 BEGIN added case for x<0 otherwise no results in 3rd quadrant
		if (x>=0) 
			return Math.exp(gammln(x));
		else
			return -Math.PI/(x*gamma(-x, kernel)*Math.sin(Math.PI*x));
// Michael Borcherds 2007-10-15 END
	}
	
	/**
	 * Factorial function of x. If x is an integer value x! is returned,
	 * otherwise gamma(x + 1) will be returned.	 
	 * For x < 0 Double.NaN is returned.
	 */	
	final public static double factorial(double x) {			

		if (x < 0) return Double.NaN; // bugfix Michael Borcherds 2008-05-04

		  // big x or floating point x is computed using gamma function
		if (x < 0 || x > 32 || x - Math.floor(x) > 1E-10) 
			// exp of log(gamma(x+1)) 
			return Math.exp(gammln(x+1.0));				
		
		int n = (int) x;		
		int j;				
		while (factorialTop<n) {
			j=factorialTop++;
			factorialTable[factorialTop]=factorialTable[j]*factorialTop;
		}
		return factorialTable[n];
	}
	
	private static int factorialTop=4;
	private static double [] factorialTable = new double[33]; 
	static { factorialTable[0] = 1.0; 
				factorialTable[1] = 1.0; 
				factorialTable[2] = 2.0; 
				factorialTable[3] = 6.0; 
				factorialTable[4] = 24.0; }						

	// logarithm of gamma function of xx
	public static double gammln(double xx) {
		double x,y,tmp,ser;
		int j;

		y=x=xx;
		tmp=x+5.5;
		tmp -= (x+0.5)* Math.log(tmp);
		ser=1.000000000190015;
		for (j=0;j<=5;j++) ser += cof[j]/++y;
		return -tmp+Math.log(2.5066282746310005*ser/x);
	}
	// coefficients for gammln
	private static double [] cof = {76.18009172947146,-86.50532032941677,
					24.01409824083091,-1.231739572450155,
					0.1208650973866179e-2,-0.5395239384953e-5};
	
	/**
	 * Round a double to the given number of digits
	 */
	final public static double truncate(double x, int digits) {
		BigDecimal bd = new BigDecimal(x);
		bd = bd.setScale(digits, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
	

// http://www.java2s.com/Code/Java/Development-Class/Returnstheleastcommonmultiplebetweentwointegervalues.htm
	/* 
	 * Licensed to the Apache Software Foundation (ASF) under one or more
	 *  contributor license agreements.  See the NOTICE file distributed with
	 *  this work for additional information regarding copyright ownership.
	 *  The ASF licenses this file to You under the Apache License, Version 2.0
	 *  (the "License"); you may not use this file except in compliance with
	 *  the License.  You may obtain a copy of the License at
	 *
	 *      http://www.apache.org/licenses/LICENSE-2.0
	 *
	 *  Unless required by applicable law or agreed to in writing, software
	 *  distributed under the License is distributed on an "AS IS" BASIS,
	 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 *  See the License for the specific language governing permissions and
	 *  limitations under the License.
	 *
	 *
	 */


	  /**
	   * Returns the least common multiple between two integer values.
	   * 
	   * @param a the first integer value.
	   * @param b the second integer value.
	   * @return the least common multiple between a and b.
	   * @throws ArithmeticException if the lcm is too large to store as an int
	   * @since 1.1
	   */
	  public static int lcm(int a, int b) {
	      return Math.abs(mulAndCheck(a / gcd(a, b), b));
	  }
	  /**
	   * Multiply two integers, checking for overflow.
	   * 
	   * @param x a factor
	   * @param y a factor
	   * @return the product <code>x*y</code>
	   * @throws ArithmeticException if the result can not be represented as an
	   *         int
	   * @since 1.1
	   */
	  public static int mulAndCheck(int x, int y) {
	      long m = ((long)x) * ((long)y);
	      if (m < Integer.MIN_VALUE || m > Integer.MAX_VALUE) {
	          throw new ArithmeticException("overflow: mul");
	      }
	      return (int)m;
	  }

	  /**
	   * <p>
	   * Gets the greatest common divisor of the absolute value of two numbers,
	   * using the "binary gcd" method which avoids division and modulo
	   * operations. See Knuth 4.5.2 algorithm B. This algorithm is due to Josef
	   * Stein (1961).
	   * </p>
	   * 
	   * @param u a non-zero number
	   * @param v a non-zero number
	   * @return the greatest common divisor, never zero
	   * @since 1.1
	   */
	  public static int gcd(int u, int v) {
	      if (u * v == 0) {
	          return (Math.abs(u) + Math.abs(v));
	      }
	      // keep u and v negative, as negative integers range down to
	      // -2^31, while positive numbers can only be as large as 2^31-1
	      // (i.e. we can't necessarily negate a negative number without
	      // overflow)
	      /* assert u!=0 && v!=0; */
	      if (u > 0) {
	          u = -u;
	      } // make u negative
	      if (v > 0) {
	          v = -v;
	      } // make v negative
	      // B1. [Find power of 2]
	      int k = 0;
	      while ((u & 1) == 0 && (v & 1) == 0 && k < 31) { // while u and v are
	                                                          // both even...
	          u /= 2;
	          v /= 2;
	          k++; // cast out twos.
	      }
	      if (k == 31) {
	          throw new ArithmeticException("overflow: gcd is 2^31");
	      }
	      // B2. Initialize: u and v have been divided by 2^k and at least
	      // one is odd.
	      int t = ((u & 1) == 1) ? v : -(u / 2)/* B3 */;
	      // t negative: u was odd, v may be even (t replaces v)
	      // t positive: u was even, v is odd (t replaces u)
	      do {
	          /* assert u<0 && v<0; */
	          // B4/B3: cast out twos from t.
	          while ((t & 1) == 0) { // while t is even..
	              t /= 2; // cast out twos
	          }
	          // B5 [reset max(u,v)]
	          if (t > 0) {
	              u = -t;
	          } else {
	              v = t;
	          }
	          // B6/B3. at this point both u and v should be odd.
	          t = (v - u) / 2;
	          // |u| larger: t positive (replace u)
	          // |v| larger: t negative (replace v)
	      } while (t != 0);
	      return -u * (1 << k); // gcd is u*2^k
	  }


	   

}
