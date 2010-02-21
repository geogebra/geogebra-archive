/**
 * Wichmann-Hill algorithm for random variates from the 
 * standard uniform distribution, U(0,1).
 * <p>
 *  Wichmann, B. A. and I. D. Hill (1982).
 *  Algorithm AS 183: An efficient and portable
 *  pseudo-random number generator,
 *  Applied Statistics, 31, 188.
 *  
 * Created on Apr 16, 2007
 */
package org.mathpiper.builtin.library.statdistlib.rng;

import org.mathpiper.builtin.library.statdistlib.StdUniformRng;

public class WichmannHill implements StdUniformRng {
  
  int i1_seed;
  int[] i_seed;
  static final int c0 = 30269;
  static final int c1 = 30307;
  static final int c2 = 30323;
  
  public WichmannHill() {
    i1_seed = 123;
    i_seed = new int[2];
    fixupSeeds();
  }
  
  public void fixupSeeds() {
    // exclude 0 as seed
    if (i1_seed==0) i1_seed++;
    for (int j=0; j < i_seed.length; j++) {
      if (i_seed[j]==0) i_seed[j]++;
    }
    if (i1_seed >= c0 ||
         i_seed[0] >= c1 ||
         i_seed[1] >= c2) {
      random();
    }
  }
  
  public double random() {
    i1_seed = i1_seed * 171 % c0;
    i_seed[0] = i_seed[0] * 172 % c1;
    i_seed[1] = i_seed[1] * 170 % c2;
    double value =
      (double)i1_seed / c0 +
      (double)i_seed[0] / c1 +
      (double)i_seed[1] / c2;
    return value - (int) value; // ensure in range [0,1)
  }
  
}
