/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

 */

package geogebra.kernel.statistics;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.util.MyMath;

/**
 * Computes RandomNormal[a, b]
 * 
 * @author Michael Borcherds
 * @version
 */
public class AlgoRandomPoisson extends AlgoElement {
	
	protected NumberValue a;  // input
    protected GeoNumeric num;     // output           

	public AlgoRandomPoisson(Construction cons, String label, NumberValue a) {
		super(cons);
	    this.a = a;
	    num = new GeoNumeric(cons); 
	    setInputOutput(); // for AlgoElement
	    
	    // compute angle
	    compute();     
	        
	    num.setLabel(label);
	    

		// create dummy random number in (0,1)
		// and call setRandomInputNumber() in order to
		// make sure that this algorithm is updated when
		// arrow keys are pressed
		GeoNumeric randNum = new GeoNumeric(cons);
		randNum.setUsedForRandom(true);
		GeoNumeric[] randNums = { randNum };
		setRandomInputNumbers(randNums);
	}


  
  // for AlgoElement
  protected void setInputOutput() {
      input =  new GeoElement[1];
      input[0] = a.toGeoElement();
      
      output = new GeoElement[1];        
      output[0] = num;        
      setDependencies(); // done by AlgoElement
  }    
  
  public GeoNumeric getResult() { return num; }  
  
	protected String getClassName() {
		return "AlgoRandomPoisson";
	}

	protected final void compute() {
		if (input[0].isDefined()) {
			double lambda = a.getDouble();
			if (lambda > 0)
				num.setValue(randomPoissonTRS(lambda));
			else
				num.setUndefined();
		} else
			num.setUndefined();
	}
	
	/*
	 * poisson random number (Knuth)
	 */
	private int randomPoisson(int lambda) {
		double L = Math.exp(-lambda);
		double p = 1;
		int k = 0;
		do {
			k++;
			p *= Math.random();
		} while (p >= L);
		
		return k - 1;
		
	}

	/*
	 * 
	 * H�rmann, Wolfgang:
	 * The transformed rejection method for generating Poisson random variables 
	 * Algorithm PTRS
	 * http://statmath.wu-wien.ac.at/papers/92-04-13.wh.ps.gz
	 * http://epub.wu-wien.ac.at/dyn/virlib/wp/eng/mediate/epub-wu-01_6f2.pdf?ID=epub-wu-01_6f2
	 */
	private int randomPoissonTRS(double mu) {
		
		
		if (mu < 10) return randomPoisson((int)mu);
			
		double b = 0.931 +  + 2.53 * Math.sqrt(mu);
		double a = -0.059 + 0.02438 * b;
		double v_r = 0.9277 - 3.6224 / (b - 2);
		
		double us = 0;
		double v = 1;
	
		while (true) {
		
			int k = -1;
			while ( k < 0 || (us < 0.013 && v > us)) {
				double u = Math.random() - 0.5;
				v = Math.random();
				us = 0.5 - Math.abs(u);
				k = (int)Math.floor((2 * a / us + b) * u + mu + 0.43);
				if (us >= 0.07 && v < v_r) return k;
			}
			
			double alpha = 1.1239 + 1.1328 / (b - 3.4);
			double lnmu = Math.log(mu);
			
			v = v * alpha / (a / (us * us) + b);
			
			if (Math.log(v * alpha / (a / us / us + b)) <= -mu +k * lnmu - logOfKFactorial(k)) return k;
		}
	
	}
	
	private static double halflog2pi = 0.5 * Math.log(2 * Math.PI);
	
	private static double logtable[] = new double[10];
	
	private double logOfKFactorial(int k) {
		if (k<10) {
			if (logtable[k] == 0) logtable[k] = Math.log(MyMath.gammln(k+1d));
			return logtable[k];
		}
	
		// Stirling approximation
		return halflog2pi + (k+0.5) * Math.log(k+1) - (k+1) + (1/12.0 - (1/360.0 - 1/1260.0/(k+1)/(k+1))/(k+1)/(k+1))/(k+1);
	}

}
