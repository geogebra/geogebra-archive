/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.util.GgbMat;

/**
 * ReducedRowEchelonForm a matrix. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 16-02-2008
 */

public class AlgoReducedRowEchelonForm extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
    private GeoList outputList; //output	

    AlgoReducedRowEchelonForm(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    public String getClassName() {
        return "AlgoReducedRowEchelonForm";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputList;

        output = new GeoElement[1];
        output[0] = outputList;
        setDependencies(); // done by AlgoElement
    }

    GeoList getResult() {
        return outputList;
    }

    protected final void compute() {
    	   		
   		GgbMat matrix = new GgbMat(inputList);
   		
   		if (matrix.isUndefined()) {
  			outputList.setUndefined();
	   		return;   		
	   	}
   		
   		matrix.reducedRowEchelonFormImmediate();
   		// ReducedRowEchelonForm[{{1,2},{3,4}}]
   		
   		outputList = matrix.getGeoList(outputList, cons);
       
    }
        
     
}
