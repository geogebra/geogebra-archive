/*
 * Created on Apr 17, 2007
 */
package org.mathpiper.builtin.library.statdistlib.rng;

import java.util.Random;

import org.mathpiper.builtin.library.statdistlib.StdUniformRng;


public class Rand implements StdUniformRng {

  Random random;
  
  public Rand() {
    random = new Random();
  }
  
  public void fixupSeeds() {
    ; // do nothing since seeds are managed
  }

  public double random() {
    return random.nextDouble();
  }

}
