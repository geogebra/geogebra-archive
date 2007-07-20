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
 * Iteration[ f(x), x0, n ] 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoIterationList extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; //input
	private NumberValue startValue, n;
	private GeoElement startValueGeo, nGeo;
    private GeoList list; //output	

    AlgoIterationList(Construction cons, String label, 
    		GeoFunction f, NumberValue startValue, NumberValue n) {
        super(cons);
        this.f = f;
        this.startValue = startValue;
        startValueGeo = startValue.toGeoElement();
        this.n = n;
        nGeo = n.toGeoElement();
               
        list = new GeoList(cons);

        setInputOutput();
        compute();
        list.setLabel(label);
    }

    String getClassName() {
        return "AlgoIterationList";
    }

    void setInputOutput(){
        input = new GeoElement[3];
        input[0] = f;
        input[1] = startValueGeo;
        input[2] = nGeo;

        output = new GeoElement[1];
        output[0] = list;
        setDependencies(); // done by AlgoElement
    }

    GeoList getResult() {
        return list;
    }

    final void compute() {
    	list.setDefined(true);
    	for (int i=0; i < input.length; i++) {
    		if (!input[i].isDefined()) {
    			list.setUndefined();
    			return;
    		}
    	}     
    	
    	// number of iterations
       	int iterations = (int) Math.round(n.getDouble());   
    	if (iterations < 0) {
    		list.clear();
    		return;
    	}
    	    	    	     	
    	// perform iteration f(f(f(...(startValue))))
    	// and fill list with all intermediate results
    	int oldListSize = list.size();
    	double val = startValue.getDouble();
    	setListElement(0, val);    	
    	for (int i=0; i < iterations; i++) {    		    		
    		val = f.evaluate(val);
    		setListElement(i+1, val);
    	}    	    	
    	
    	// remove all list elements that are no longer used
    	// the new list has size = iterations + 1
		for (int i=oldListSize-1; i > iterations; i--) {
			list.remove(i);
		}
    }   
    
    private void setListElement(int index, double value) {
    	GeoNumeric listElement;
    	if (index < list.size()) {
    		// use existing list element
    		listElement = (GeoNumeric) list.get(index);    	
    	} else {
    		// create a new list element
    		listElement = new GeoNumeric(cons);
    		listElement.setParentAlgorithm(this);
    		listElement.setConstructionDefaults();
			listElement.setUseVisualDefaults(false);	
    		list.add(listElement);
    	}
    	listElement.setValue(value);
    }
    
}
