/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
    
	protected String getClassName() {
		return "AlgoDependentList";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
    	// create input array from listItems array-list
    	// and fill the geoList with these objects
    	int size = listItems.size();
        input = new GeoElement[size];
    	for (int i=0; i < size; i++) {
    		input[i] = (GeoElement) listItems.get(i);    		
    	}          
        
        output = new GeoElement[1];        
        output[0] = geoList;        
        setDependencies(); // done by AlgoElement
    }    
    
    /**
     * Call super.remove() to remove the list.
     * Then remove all unlabeled input objects (= list elements)
     */
    public void remove() {
    	super.remove();
    	
		//  removing unlabeled input
		for (int i=0; i < input.length; i++) {
			if (!input[i].isLabelSet()) {				
				input[i].remove();
			}
		}    		
    		    	 	    
    }
    
    public GeoList getGeoList() { 
    	return geoList; 
    }       
    
    protected final void compute() {	    	
    	geoList.clear();
    	for (int i=0; i < input.length; i++) {    
    		// add input and its siblings to the list
    		// if the siblings are of the same type
    		
    		AlgoElement algo = input[i].getParentAlgorithm();
    		if (algo != null && algo.hasSingleOutputType()) {
    			// all siblings have same type: add them all
    			for (int k=0; k < algo.output.length; k++) {
    				geoList.add(algo.output[k]);
    			}    			
    		} else {
    			// independent or mixed sibling types:
    			// add only this element
    			geoList.add(input[i]);
    		}
    	}
    }   
    
    final public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("{");    	    
    	for (int i=0; i < input.length - 1; i++) {
    		sb.append(input[i].getLabel());
    		sb.append(", ");
    	}    	
    	sb.append(input[input.length-1].getLabel());
    	sb.append("}");    		    	    	
        return sb.toString();
    }        
    
}
