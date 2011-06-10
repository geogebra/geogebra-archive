/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import java.util.ArrayList;

/**
 *  Algorithm that takes a list of GeoElement objects to build a Geolist with them.
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class AlgoDependentList extends AlgoElement {

	private static final long serialVersionUID = 1L;	
	private ArrayList<GeoElement> listItems; //input GeoElements
    private GeoList geoList;     // output    
    
        
    /**
     * Creates a new algorithm that takes a list of GeoElements to build a Geolist with 
     * them.
     * @param cons construction
     * @param label label for new list
     * @param listItems list of GeoElement objects
     */
    public AlgoDependentList(Construction cons, String label, ArrayList<GeoElement> listItems) {
    	this (cons, listItems, false);
    	geoList.setLabel(label);
    }
    
    /**
     * Creates an unlabeled algorithm that takes a list of GeoElements to build a Geolist with 
     * them.
     * @param cons
     * @param listItems list of GeoElement objects
     * @param isCellRange
     */
    AlgoDependentList(Construction cons, ArrayList<GeoElement> listItems, boolean isCellRange) {
    	super(cons);
    	this.listItems = listItems;
    	       
    	// create output object
        geoList = new GeoList(cons);                                    	
    	setInputOutput(); 
        
        // compute value of dependent number
        compute();              
    }   
    
	public String getClassName() {
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
    		
    		if (!input[i].isLabelSet()) {
    			input[i].labelWanted = false;
    		}
    	}          
        
        setOutputLength(1);        
        setOutput(0,geoList);        
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
    
    /**
     * Returns the list
     * @return the list as geo
     */
    public GeoList getGeoList() { 
    	return geoList; 
    }       
    
    protected final void compute() {	    	
    	geoList.clear();
    	for (int i=0; i < input.length; i++) {    
    		// add input and its siblings to the list
    		// if the siblings are of the same type
    		
    		AlgoElement algo = input[i].getParentAlgorithm();
    		if (algo != null && algo.getOutput().length>1 && algo.hasSingleOutputType()) {
    			// all siblings have same type: add them all
    			for (int k=0; k < algo.getOutputLength(); k++) {
    				GeoElement geo = algo.getOutput(k);
    				if ((geo == input[i] || geo.isDefined()) && !geoList.listContains(geo))
    			         geoList.add(geo);
    			}    			
    		} else {
    			// independent or mixed sibling types:
    			// add only this element    						
    			geoList.add(input[i]);
    		}
    	}
    }   
    
	private StringBuilder sb; 
    final public String toString() {

        if (sb == null) sb = new StringBuilder();
        else sb.setLength(0);
    	sb.append("{");
    	
    	if(input.length > 0) { // Florian Sonner 2008-07-12
	    	for (int i=0; i < input.length - 1; i++) {
	    		sb.append(input[i].getLabel());
	    		sb.append(", ");
	    	}    	
	    	sb.append(input[input.length-1].getLabel());
    	}
    	sb.append("}");    		    	    	
        return sb.toString();
    }        
    
    final public String toRealString() {

        if (sb == null) sb = new StringBuilder();
        else sb.setLength(0);
    	sb.append("{");
    	
    	if(input.length > 0) { // Florian Sonner 2008-07-12
	    	for (int i=0; i < input.length - 1; i++) {
	    		sb.append(input[i].getRealLabel());
	    		sb.append(", ");
	    	}    	
	    	sb.append(input[input.length-1].getRealLabel());
    	}
    	sb.append("}");    		    	    	
        return sb.toString();
    }
    
}
