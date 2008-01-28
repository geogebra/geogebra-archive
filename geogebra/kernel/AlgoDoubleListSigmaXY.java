/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;


/**
 * SigmaXY of a list.
 * adapted from AlgoListMax
 * @author Michael Borcherds
 * @version 13-01-2008
 */

public class AlgoDoubleListSigmaXY extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoListX; //input
	private GeoList geoListY; //input
    private GeoNumeric num; //output	

    AlgoDoubleListSigmaXY(Construction cons, String label, GeoList geoListX, GeoList geoListY) {
        super(cons);
        this.geoListX = geoListX;
        this.geoListY = geoListY;
               
        num = new GeoNumeric(cons);

        setInputOutput();
        compute();
        num.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoDoubleListSigmaXY";
    }

    void setInputOutput(){
        input = new GeoElement[2];
        input[0] = geoListX;
        input[1] = geoListY;

        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getSigmaXY() {
        return num;
    }

    final void compute() {
    	int sizeX = geoListX.size();
    	int sizeY = geoListY.size();
    	if (!geoListX.isDefined() ||  sizeX == 0 ||
    		!geoListY.isDefined() ||  sizeY == 0 || sizeX!=sizeY) {
    		num.setUndefined();
    		return;
    	}
    	double sigmaxy=0;
    	
        for (int i=0 ; i<sizeX ; i++)
        {
   		 GeoElement geoX = geoListX.get(i); 
		 GeoElement geoY = geoListY.get(i); 
 		 if (geoX.isNumberValue() && geoY.isNumberValue()) {
  			NumberValue numX = (NumberValue) geoX;
 			NumberValue numY = (NumberValue) geoY;
 			sigmaxy+=numX.getDouble()*numY.getDouble();
 		 }
 		 else
 		 {
 			num.setUndefined();	
     		return;			
 		 }
        }
		num.setValue(sigmaxy);
    }
   
}
