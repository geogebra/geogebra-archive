/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;
import java.util.*;


/**
 * Sort a list. Adapted from AlgoMax and AlgoIterationList
 * @author Michael Borcherds
 * @version 04-01-2008
 */

public class AlgoSort extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
    private GeoList outputList; //output	
    private int size;

    AlgoSort(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    String getClassName() {
        return "AlgoSort";
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
    	if (!inputList.isDefined() ||  size == 0) {
    		outputList.setUndefined();
    		return;
    	} 
       
    	if (inputList.get(0).isNumberValue()) sortNumbers(); else sortStrings();
    }
    
    final void sortStrings() {
    	
      String[] sortList = new String[size];
        
      // copy inputList into an array
      for (int i=0 ; i<size ; i++)
      {
    	GeoElement geo = inputList.get(i); 
 		if (geo.isTextValue()) {
     		GeoText listElement = (GeoText) inputList.getCached(i); 
 			sortList[i]=listElement.getTextString();
 		}
 		else
 		{
 		  outputList.setUndefined();
     	  return;			
 		}
      }
        
        // do the sorting
        Arrays.sort(sortList);
        
        // copy the sorted array back into a list
        outputList.setDefined(true);
        outputList.clear();
 	    for (int i=0 ; i<size ; i++)
 	    {
     	   setListElement(i, sortList[i]);
        }      
    }
    
    final void sortNumbers() {
    	
       double[] sortList = new double[size];

       // copy inputList into an array
       for (int i=0 ; i<size ; i++)
       {
   		 GeoElement geo = inputList.get(i); 
		 if (geo.isNumberValue()) {
			NumberValue num = (NumberValue) geo;
			sortList[i]=num.getDouble();
		 }
		 else
		 {
			outputList.setUndefined();
    		return;			
		 }
       }
       
       // do the sorting
       Arrays.sort(sortList);
       
       // copy the sorted array back into a list
       outputList.setDefined(true);
       outputList.clear();
	   for (int i=0 ; i<size ; i++)
       {
    	   setListElement(i, sortList[i]);
       }      
    }
    
    // copied from AlgoInterationList.java
    // TODO should it be centralised?
    private void setListElement(int index, double value) {
    	GeoNumeric listElement;
    	if (index < outputList.getCacheSize()) {
    		// use existing list element
    		listElement = (GeoNumeric) outputList.getCached(index);    	
    	} else {
    		// create a new list element
    		listElement = new GeoNumeric(cons);
    		listElement.setParentAlgorithm(this);
    		listElement.setConstructionDefaults();
    		listElement.setUseVisualDefaults(false);	    		
    	}
    	
    	outputList.add(listElement);
    	listElement.setValue(value);
    }    

    private void setListElement(int index, String value) {
    	GeoText listElement;
    	if (index < outputList.getCacheSize()) {
    		// use existing list element
    		listElement = (GeoText) outputList.getCached(index);    	
    	} else {
    		// create a new list element
    		listElement = new GeoText(cons);
    		listElement.setParentAlgorithm(this);
    		listElement.setConstructionDefaults();
    		listElement.setUseVisualDefaults(false);	    		
    	}
    	
    	outputList.add(listElement);
    	listElement.setTextString(value);
    }    
    
}
