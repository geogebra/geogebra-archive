/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel.statistics;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.NumberValue;

import java.util.Arrays;


/**
 * Mode of a list. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 2008-02-16
 */

public class AlgoMode extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
    private GeoList outputList; //output	
    private int size;

    public AlgoMode(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoMode";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputList;

        output = new GeoElement[1];
        output[0] = outputList;
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return outputList;
    }

    protected final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size == 0) {
    		outputList.setUndefined();
    		return;
    	} 
       
    	
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
       
       // check what the longest run of equal numbers is
       int maxRun=1;
       int run=1;
       double val=sortList[0];
       
       for (int i=1 ; i<size ; i++)
       {
    	   if (sortList[i]==val)
    	   {
    		   run++;
    	   }
    	   else
    	   {
   		   if (run>=maxRun) maxRun=run;
    		   run=1;
    		   val=sortList[i];
    	   }
       }
       if (run>=maxRun) maxRun=run;
       
       outputList.setDefined(true);
       outputList.clear();

       if (maxRun==1) return; // no mode, return empty list
       
       // check which numbers occur maxRun times and put them in a list     
       run=1;
       val=sortList[0];
       int modeNo=0;
       
       for (int i=1 ; i<size ; i++)
       {
    	   if (sortList[i]==val)
    	   {
    		   run++;
    		   if (run==maxRun) setListElement(modeNo++,val);
    	   }
    	   else
    	   {
    		   run=1;
    		   val=sortList[i];
    	   }
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

}
