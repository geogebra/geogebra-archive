/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;


/**
 * Minimum value of a list.
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoListMin extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
    private GeoNumeric min; //output	

    AlgoListMin(Construction cons, String label, GeoList geoList) {
        super(cons);
        this.geoList = geoList;
               
        min = new GeoNumeric(cons);

        setInputOutput();
        compute();
        min.setLabel(label);
    }

    String getClassName() {
        return "AlgoListMin";
    }

    void setInputOutput(){
        input = new GeoElement[1];
        input[0] = geoList;

        output = new GeoElement[1];
        output[0] = min;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getMin() {
        return min;
    }

    final void compute() {
    	int size = geoList.size();
    	if (!geoList.isDefined() ||  size == 0) {
    		min.setUndefined();
    		return;
    	}
    	
    	double minVal = Double.POSITIVE_INFINITY;
    	for (int i=0; i < size; i++) {
    		GeoElement geo = geoList.get(i);
    		if (geo.isNumberValue()) {
    			NumberValue num = (NumberValue) geo;
    			minVal = Math.min(minVal, num.getDouble());
    		} else {
    			min.setUndefined();
        		return;
    		}    		    		
    	}   
    	
    	min.setValue(minVal);
    }
    
}
