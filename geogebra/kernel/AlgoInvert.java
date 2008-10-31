/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.linalg.GgbMatrix;
import geogebra.main.Application;

/**
 * Reverse a list. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 16-02-2008
 */

public class AlgoInvert extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
    private GeoList outputList; //output	
    private int cols, rows;

    AlgoInvert(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoInvert";
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
    	
    	cols = inputList.size();
    	if (!inputList.isDefined() || cols == 0) {
    		outputList.setUndefined();
    		return;
    	} 
    	
    	GeoElement geo = inputList.get(0);
    	
    	if (!geo.isGeoList()) {
    		outputList.setUndefined();
    		return;   		
    	}
    	

   		rows = ((GeoList)geo).size();
   		
   		if (rows == 0) {
    		outputList.setUndefined();
    		return;   		
    	}
   		
   		GgbMatrix matrix = new GgbMatrix(rows, cols);
   		GeoList rowList;
   		
   		for (int c = 0 ; c < cols ; c++) {
   			geo = inputList.get(c);
   			if (!geo.isGeoList()) {
   				outputList.setUndefined();
   				return;   		
   			}
   			rowList = (GeoList)geo;
   			if (rowList.size() != rows) {
   				outputList.setUndefined();
   				return;   		
   			}
   	   		for (int r = 0 ; r < rows ; r++) {
   	   			geo = rowList.get(r);
   	   			if (!geo.isGeoNumeric()) {
   	   				outputList.setUndefined();
   	   				return;   		
   	   			}
   	   			
   	   			matrix.set(r + 1, c + 1, ((GeoNumeric)geo).getValue());
   	   		}
  			
   		}
   		
   		//matrix.SystemPrint();
   		
   		matrix = matrix.inverse();
   		
   		//matrix.SystemPrint();
   		
   		if (matrix == null) {
  			outputList.setUndefined();
	   		return;   		
	   	}
   		// Invert[{{1,2},{3,4}}]
   		
        outputList.setDefined(true);
        outputList.clear();
        
   		for (int c = 0 ; c < cols ; c++) {
   			rowList = new GeoList(cons);
   	   		for (int r = 0 ; r < rows ; r++) {  	   			
   	   			rowList.add(new GeoNumeric(cons, matrix.get(r + 1, c + 1)));  	   			
   	   		}
   	   		outputList.add(rowList);
   		}

   		
       
    }
        
     
}
