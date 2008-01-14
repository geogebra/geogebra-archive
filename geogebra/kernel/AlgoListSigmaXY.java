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

public class AlgoListSigmaXY extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
    private GeoNumeric num; //output	

    AlgoListSigmaXY(Construction cons, String label, GeoList geoList) {
        super(cons);
        this.geoList = geoList;
               
        num = new GeoNumeric(cons);

        setInputOutput();
        compute();
        num.setLabel(label);
    }

    String getClassName() {
        return "AlgoListSigmaXY";
    }

    void setInputOutput(){
        input = new GeoElement[1];
        input[0] = geoList;

        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getSigmaXY() {
        return num;
    }

    final void compute() {
    	int size = geoList.size();
    	if (!geoList.isDefined() ||  size == 0) {
    		num.setUndefined();
    		return;
    	}

    	
    	double sigmaxy=0;
    	
        for (int i=0 ; i<size ; i++)
        {
   		 GeoElement geo = geoList.get(i); 
 		 if (geo.isGeoPoint()) {
 			double x=((GeoPoint)geo).getX();
 			double y=((GeoPoint)geo).getY();
 			double z=((GeoPoint)geo).getZ();
 			x=x/z;
 			y=y/z;
  			sigmaxy+=x*y;
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
