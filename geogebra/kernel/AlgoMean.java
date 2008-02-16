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
 * Mean of a list, adapted from AlgoListMin
 * @author Michael Borcherds
 * @version 2008-02-16
 */

public class AlgoMean extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
    private GeoNumeric mean; //output	

    AlgoMean(Construction cons, String label, GeoList geoList) {
        super(cons);
        this.geoList = geoList;
               
        mean = new GeoNumeric(cons);

        setInputOutput();
        compute();
        mean.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoMean";
    }

    void setInputOutput(){
        input = new GeoElement[1];
        input[0] = geoList;

        output = new GeoElement[1];
        output[0] = mean;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getMean() {
        return mean;
    }

    final void compute() {
    	int size = geoList.size();
    	if (!geoList.isDefined() ||  size == 0) {
    		mean.setUndefined();
    		return;
    	}
    	
    	double sumVal = 0;
    	for (int i=0; i < size; i++) {
    		GeoElement geo = geoList.get(i);
    		if (geo.isNumberValue()) {
    			NumberValue num = (NumberValue) geo;
    			sumVal += num.getDouble();
    		} else {
    			mean.setUndefined();
        		return;
    		}    		    		
    	}   
    	
    	mean.setValue(sumVal/(double)size);
    }
    
}
