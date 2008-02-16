/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * Reverse a list. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 16-02-2008
 */

public class AlgoReverse extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
    private GeoList outputList; //output	
    private int size;

    AlgoReverse(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoReverse";
    }

    void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputList;

        output = new GeoElement[1];
        output[0] = outputList;
        setDependencies(); // done by AlgoElement
    }

    GeoList getResult() {
        return outputList;
    }

    final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined()) {
    		outputList.setUndefined();
    		return;
    	} 
       
        outputList.setDefined(true);
        outputList.clear();
        
        if (size==0) return; // return empty list
        
 	    for (int i=0 ; i<size ; i++)
 	    {
     	   outputList.add(inputList.get(size-1-i));
        }      
     }
    
}
