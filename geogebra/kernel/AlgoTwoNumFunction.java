/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

/**
 * Parent algorithm for commands that are functions of R^2 -> R
 * @author  Markus Hohenwarter
 * @version 
 */
public abstract class AlgoTwoNumFunction extends AlgoElement {

	private static final long serialVersionUID = 1L;
	protected NumberValue a, b;  // input
    protected GeoNumeric num;     // output           
        
    AlgoTwoNumFunction(Construction cons, String label, NumberValue a, NumberValue b) {       
	  super(cons); 
      this.a = a;
      this.b = b;
      num = new GeoNumeric(cons); 
      setInputOutput(); // for AlgoElement
      
      // compute angle
      compute();     
          
      num.setLabel(label);
    }   
  
    abstract String getClassName();
    
    // for AlgoElement
    void setInputOutput() {
        input =  new GeoElement[2];
        input[0] = a.toGeoElement();
        input[1] = b.toGeoElement();
        
        output = new GeoElement[1];        
        output[0] = num;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoNumeric getResult() { return num; }        

    abstract void compute();     
}
