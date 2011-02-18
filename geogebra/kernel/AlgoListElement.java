/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;


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
	private NumberValue num, num2 = null; // input
	private GeoElement numGeo, num2Geo = null;
    private GeoElement element; //output	

    /**
     * Creates new labeled element algo
     * @param cons
     * @param label
     * @param geoList
     * @param num
     */
    public AlgoListElement(Construction cons, String label, GeoList geoList, NumberValue num) {
        super(cons);
        this.geoList = geoList;
        this.num = num;
        numGeo = num.toGeoElement();
        
        int initIndex = Math.max(0, (int) Math.round( num.getDouble() ) - 1);
               
        // init return element as copy of initIndex list element
        if (geoList.size() > initIndex) {
        	// create copy of initIndex GeoElement in list
        	element = geoList.get(initIndex).copyInternal(cons);
        } 
        
        // if not enough elements in list:
        // init return element as copy of first list element
        else if (geoList.size() > 0) {
        	// create copy of first GeoElement in list
        	element = geoList.get(0).copyInternal(cons);        
        } 

        // desperate case: empty list
        else {        	        	        	
        	element = new GeoNumeric(cons);
        }      

        setInputOutput();
        compute();
        element.setLabel(label);
    }

    /**
     * Creates new unlabeed element algo
     * @param cons
     * @param label
     * @param geoList
     * @param num2
     * @param num
     */
    AlgoListElement(Construction cons, String label, GeoList geoList, NumberValue num2, NumberValue num) {
        super(cons);
        this.geoList = geoList;
        this.num = num;
        this.num2 = num2;
        numGeo = num.toGeoElement();
        num2Geo = num2.toGeoElement();
        
        int initIndex = Math.max(0, (int) Math.round( num.getDouble() ) - 1);
        int initIndex2 = Math.max(0, (int) Math.round( num2.getDouble() ) - 1);
        
        element = null;
               
        try {
        // init return element as copy of initIndex list element
        if (geoList.size() > initIndex) {
        	// create copy of initIndex GeoElement in list
        	element = geoList.get(initIndex, initIndex2).copyInternal(cons);
        } 
        
        // if not enough elements in list:
        // init return element as copy of first list element
        else if (geoList.size() > 0) {
        	// create copy of first GeoElement in list
        	element = geoList.get(0, 0).copyInternal(cons);        
        } 
        } catch (Exception e) { Application.debug("error initialising list"); };

        // desperate case: empty list, or malformed 2D array
        if (element == null)        	        	        	
        	element = new GeoNumeric(cons);
              

        setInputOutput();
        compute();
        element.setLabel(label);
    }

    public String getClassName() {
        return "AlgoListElement";
    }

    protected void setInputOutput(){
    	
    	if (num2 == null) {
	        input = new GeoElement[2];
	        input[0] = geoList;
	        input[1] = numGeo;
    	}
    	else
    	{
	        input = new GeoElement[3];
	        input[0] = geoList;
	        input[2] = numGeo;
	        input[1] = num2Geo;	
    	}

        setOutputLength(1);
        setOutput(0,element);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns chosen element
     * @return chosen element
     */
    public GeoElement getElement() {
        return element;
    }

    protected final void compute() {
    	if (!numGeo.isDefined() || !geoList.isDefined()) {
        	element.setUndefined();
    		return;
    	}
    	
    	if (num2 == null) {
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
	    	
    	} else {

        	if (!num2Geo.isDefined()) {
            	element.setUndefined();
        		return;
        	}

        	int n = (int) Math.round(num.getDouble()) - 1;    	
	    	int m = (int) Math.round(num2.getDouble()) - 1;    	
	    	if (n >= 0 && n < geoList.size()) {
	    		GeoElement nth = geoList.get(n);
	    		
	    		if (!nth.isGeoList()){
	    			element.setUndefined();
	    			return;
	    		}
	    		
	    		GeoList list = ((GeoList)nth);
	    		
	    		if (m < list.size())
	    			nth = list.get(m);
	    		else {
	    			element.setUndefined();
	    			return;
	    		}
	    		
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
    
}
