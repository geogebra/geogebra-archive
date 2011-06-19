/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import java.util.Iterator;
import java.util.TreeSet;


/**
 * Create a dot plot. 
 * 
 * Input: list of unsorted raw numeric data 
 * Output: sorted list of points forming a dot plot of the raw data
 * 
 * A dot plot is a set of points for which:
 *  x coordinates = values from a list of numeric data
 *  y coordinates = number of times the x data value has occurred 
 *  
 *  example:
 *      raw data = { 5,11,12,12,12,5 }
 *      dot plot = { (5,1), (5,2), (11,1), (12,1), (12,2), (12,3) }
 *  
 * Adapted from AlgoSort and AlgoPointList
 * @author G.Sturr
 * @version 2010-8-10
 */

public class AlgoDotPlot extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
    private GeoList outputList; //output	
    private int size;

    AlgoDotPlot(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    public String getClassName() {
        return "AlgoDotPlot";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputList;

        setOutputLength(1);
        setOutput(0,outputList);
        setDependencies(); // done by AlgoElement
    }

    GeoList getResult() {
        return outputList;
    }

    protected final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size == 0) {
    		outputList.setUndefined();
    		return;
    	} 

    	//========================================
    	// sort the raw data
    	
    	GeoElement geo0 = inputList.get(0);     	    	 	
    	TreeSet<GeoNumeric> sortedSet;
    		
    	if (geo0.isNumberValue()) {
    		sortedSet = new TreeSet<GeoNumeric>(GeoNumeric.getComparator());
    		
    	} else {
    		outputList.setUndefined();
    		return;    		
    	}
    	
    
        for (int i=0 ; i<size ; i++)
        {
	      	GeoElement geo = inputList.get(i); 
	   		if (geo instanceof GeoNumeric) {
	   			sortedSet.add((GeoNumeric)geo);
	   		}
	   		else
	   		{
	   		  outputList.setUndefined();
	       	  return;			
	   		}
        }
        
        
        //========================================
        // iterate through the sorted data and 
        // create dot plot points
    	 
        outputList.setDefined(true);
        outputList.clear();
        
        boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
	
        Iterator<GeoNumeric> iterator = sortedSet.iterator();    
        double k = 1.0;
        double prevValue;
        double currentValue = (double)((GeoNumeric) iterator.next()).getDouble(); 
        
        // first point
        outputList.add(new GeoPoint(cons, null, currentValue, k, 1.0));
        
        // remaining points
        while (iterator.hasNext()) {
        	prevValue = currentValue;
        	currentValue = (double)((GeoNumeric) iterator.next()).getDouble(); 
        	// check is same as previous element
        	if(currentValue == prevValue ) 
				++k;
			else
				k = 1;
        	outputList.add(new GeoPoint(cons, null, currentValue, k, 1.0));
        }      
		
        cons.setSuppressLabelCreation(suppressLabelCreation);
        
    }
  
}
