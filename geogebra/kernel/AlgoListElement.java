/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;


/**
 * n-th element of a GeoList object.
 * 
 * Note: the type of the returned GeoElement object is determined
 * by the type of the first list element. If the list is initially empty,
 * a GeoNumeric object is created for element.
 * 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoListElement extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
	private NumberValue num; // input
	private GeoElement numGeo;
    private GeoElement element; //output	

    AlgoListElement(Construction cons, String label, GeoList geoList, NumberValue num) {
        super(cons);
        this.geoList = geoList;
        this.num = num;
        numGeo = num.toGeoElement();
               
        // init return element as copy of first list element
        if (geoList.size() > 0) {
        	// create copy of first GeoElement in list
        	element = geoList.get(0).copyInternal(cons);
        } else {
        	element = new GeoNumeric(cons);
        }      

        setInputOutput();
        compute();
        element.setLabel(label);
    }

    String getClassName() {
        return "AlgoListElement";
    }

    void setInputOutput(){
        input = new GeoElement[2];
        input[0] = geoList;
        input[1] = numGeo;

        output = new GeoElement[1];
        output[0] = element;
        setDependencies(); // done by AlgoElement
    }

    GeoElement getElement() {
        return element;
    }

    final void compute() {
    	if (!numGeo.isDefined() || !geoList.isDefined()) {
        	element.setUndefined();
    		return;
    	}
    	
    	// index of wanted element
    	int n = (int) Math.round(num.getDouble()) - 1;    	
    	if (n >= 0 && n < geoList.size()) {
    		GeoElement nth = geoList.get(n);
    		// check type:
    		if (nth.getGeoClassType() == element.getGeoClassType()) {
    			element.set(nth);
    		} else {
    			element.setUndefined();
    		}
    	} else {
    		element.setUndefined();
    	}
    }
    
}
