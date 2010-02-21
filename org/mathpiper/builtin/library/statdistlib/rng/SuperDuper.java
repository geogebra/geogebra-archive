/**
 * Standard random deviates via 
 * Reeds et al (1984) implementation; 
 * modified using __unsigned__  seeds instead of signed ones.
 *
 * Created on Apr 17, 2007
 */
package org.mathpiper.builtin.library.statdistlib.rng;

import org.mathpiper.builtin.library.statdistlib.StdUniformRng;


public class SuperDuper implements StdUniformRng {

  private int i1_seed;
  private int[] i_seed;

  static private double i2_32m1 = 2.328306437080797e-10; /* = 1/(2^32 - 1) */
  static private int do32bits(int N) { return (N); }

  public SuperDuper() {
    i1_seed = 123;
    i_seed = new int[1];
    fixupSeeds();
  }
  public void fixupSeeds() {
    if (i1_seed==0) i1_seed++;
    for(int j=0; j < i_seed.length; j++) {
      if (i_seed[j]==0) i_seed[j]++;
    }
    i_seed[0] |= 1; // seed must be odd
  }

  public double random() {
    i1_seed ^= ((i1_seed >> 15) & 0377777); /* Tausworthe */
    i1_seed ^= do32bits(i1_seed << 17);
    i_seed[0] *= 69069;     /* Congruential */
    i_seed[0] = do32bits(69069 * i_seed[0]);
    return (i1_seed^i_seed[0]) * i2_32m1;/* in [0,1) */
  }

}
