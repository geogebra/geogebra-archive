/**
 * Interface for standard uniform random number generator in this package.
 * 
 * Created on Apr 16, 2007
 */
package org.mathpiper.builtin.library.statdistlib;

public interface StdUniformRng {
  public void fixupSeeds();
  public double random();
}
