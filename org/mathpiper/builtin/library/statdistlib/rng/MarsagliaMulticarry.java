/*
 * Created on Apr 17, 2007
 */
package org.mathpiper.builtin.library.statdistlib.rng;

import org.mathpiper.builtin.library.statdistlib.StdUniformRng;


public class MarsagliaMulticarry implements StdUniformRng {
  
  int i1_seed;
  int[] i_seed;
  
  static private double i2_32m1 = 2.328306437080797e-10; /* = 1/(2^32 - 1) */
  static private int do32bits(int N) { return (N); }
  
  public MarsagliaMulticarry() {
    i1_seed = 123;
    i_seed = new int[1];
    fixupSeeds();
  }
  
  public void fixupSeeds() {
    if (i1_seed==0) i1_seed++;
    for(int j=0; j < i_seed.length; j++) {
      if (i_seed[j]==0) i_seed[j]++;
    }
  }
  
  public double random() {
    i1_seed= 36969*(i1_seed & 0177777) + (i1_seed>>16);
    i_seed[0]= 18000*(i_seed[0] & 0177777) + (i_seed[0]>>16);
    return (do32bits(i1_seed << 16) ^ (i_seed[0] & 0177777)) * i2_32m1; /* in [0,1) */
  }
  
}
