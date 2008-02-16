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
 * Sort a list. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 2008-02-16
 */

public class AlgoMedian extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
    private GeoNumeric median; //output	
    private int size;

    AlgoMedian(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        median = new GeoNumeric(cons);

        setInputOutput();
        compute();
        median.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoMedian";
    }

    void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputList;

        output = new GeoElement[1];
        output[0] = median;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getMedian() {
        return median;
    }

    final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size == 0) {
    		median.setUndefined();
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
			median.setUndefined();
    		return;			
		 }
       }
       
       // do the sorting
       Arrays.sort(sortList);
       
       if (Math.floor((double)size/2)==(double)size/2.0)
       {
    	 median.setValue((sortList[size/2]+sortList[size/2-1])/2);  
       }
       else
       {
    	 median.setValue(sortList[(size-1)/2]);   
       }
      
    }
}
