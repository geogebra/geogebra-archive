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
 * Normal
 * @author Michael Borcherds
 * @version 20-01-2008
 */

public class AlgoNormal extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private NumberValue a,b,c; //input
    private GeoNumeric num; //output	

    public AlgoNormal(Construction cons, String label, NumberValue a,NumberValue b, NumberValue c) {
        super(cons);
        this.a = a;
        this.b = b;
        this.c = c;
               
        num = new GeoNumeric(cons);

        setInputOutput();
        compute();
        num.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoNormal";
    }

    protected void setInputOutput(){
        input = new GeoElement[3];
        input[0] = a.toGeoElement();
        input[1] = b.toGeoElement();
        input[2] = c.toGeoElement();

        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getResult() {
        return num;
    }

    protected final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined()) {
    		    double mean=a.getDouble();
    		    double sd=b.getDouble();
        		try {
            		num.setValue(getCDF((c.getDouble()-mean)/sd));
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
    
    double getCDF(double x)
    {
      double b1 =  0.319381530;
      double b2 = -0.356563782;
      double b3 =  1.781477937;
      double b4 = -1.821255978;
      double b5 =  1.330274429;
      double p  =  0.2316419;
      double c  =  0.39894228;

      if(x >= 0.0) {
          double t = 1.0 / ( 1.0 + p * x );
          return (1.0 - c * Math.exp( -x * x / 2.0 ) * t *
          ( t *( t * ( t * ( t * b5 + b4 ) + b3 ) + b2 ) + b1 ));
      }
      else {
          double t = 1.0 / ( 1.0 - p * x );
          return ( c * Math.exp( -x * x / 2.0 ) * t *
          ( t *( t * ( t * ( t * b5 + b4 ) + b3 ) + b2 ) + b1 ));
        }
    }

}



