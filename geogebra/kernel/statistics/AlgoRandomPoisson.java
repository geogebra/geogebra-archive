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
		if (input[0].isDefined() && input[1].isDefined()) {
			num.setValue(randomNormal(a.getDouble(), 0.0));
		} else
			num.setUndefined();
	}

	private static double randomNormal(double mean, double sd) {
		double fac, rsq, v1, v2;
		do {
			v1 = 2.0 * Math.random() - 1;
			v2 = 2.0 * Math.random() - 1; // two random numbers from -1 to +1
			rsq = v1 * v1 + v2 * v2;
		} while (rsq >= 1.0 || rsq == 0.0); // keep going until they are in the
											// unit circle
		fac = Math.sqrt(-2.0 * Math.log(rsq) / rsq);
		// Application.debug("randomNormal="+(v1*fac));
		return v1 * fac * sd + mean;

	}

}
