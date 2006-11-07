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

import java.util.ArrayList;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDependentList extends AlgoElement {

	private static final long serialVersionUID = 1L;	
	private ArrayList listItems; //input GeoElements
    private GeoList geoList;     // output    
        
    /**
     * Creates a new algorithm that takes a list of GeoElements to build a Geolist with 
     * them.
     * @param cons
     * @param label
     * @param listItems: list of GeoElement objects
     */
    public AlgoDependentList(Construction cons, String label, ArrayList listItems) {
    	super(cons);
    	this.listItems = listItems;
    	       
    	// create output object
        geoList = new GeoList(cons);                                    	
    	setInputOutput(); 
        
        // compute value of dependent number
        compute();      
        geoList.setLabel(label);
    }   
    
	String getClassName() {
		return "AlgoDependentList";
	}
    
    // for AlgoElement
    void setInputOutput() {
    	// create input array from listItems array-list
    	// and fill the geoList with these objects
    	int size = listItems.size();
        input = new GeoElement[size];
    	for (int i=0; i < size; i++) {
    		input[i] = (GeoElement) listItems.get(i);
    		geoList.add(input[i]);
    	}          
        
        output = new GeoElement[1];        
        output[0] = geoList;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoList getGeoList() { 
    	return geoList; 
    }       
    
    // calc the current value of the arithmetic tree
    final void compute() {	
    	// nothing has to be done here as all the dependent GeoElements
    	// have already been updated before
    }   
    
    final public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("{");    	    
    	for (int i=0; i < input.length - 1; i++) {
    		sb.append(input[i].getLabelOrDefinitionDescription());
    		sb.append(", ");
    	}    	
    	sb.append(input[input.length-1].getLabelOrDefinitionDescription());
    	sb.append("}");    		    	    	
        return sb.toString();
    }        
    
}
