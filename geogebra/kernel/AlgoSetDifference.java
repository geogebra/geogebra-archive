/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/*
 * used for A\B Set Difference notation
 */
public class AlgoSetDifference extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
	private GeoList inputList2; //input
    private GeoList outputList; //output	
    private int size, size2;

    AlgoSetDifference(Construction cons, String label, GeoList inputList, GeoList inputList2) {
        super(cons);
        
        
        this.inputList = inputList;
        this.inputList2 = inputList2;

               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }


    protected String getClassName() {
        return "AlgoSetDifference";
    }

    protected void setInputOutput(){
        input = new GeoElement[2];
        
	    input[0] = inputList;
	    input[1] = inputList2;

        output = new GeoElement[1];
        output[0] = outputList;
        setDependencies(); // done by AlgoElement
    }

    GeoList getResult() {
        return outputList;
    }

    protected final void compute() {
    	
    	size = inputList.size();
    	size2 = inputList2.size();
   	
    	if (!inputList.isDefined() || !inputList2.isDefined()) {
    		outputList.setUndefined();
    		return;
    	} 
       
    	outputList.setDefined(true);
    	outputList.clear();
    	
  	
        for (int i = 0 ; i < size ; i++) {
        	boolean inList2 = false;
    		GeoElement geo = inputList.get(i);
        	for (int j = 0 ; j < size2 ; j++) {
        		
        		if (inputList2.get(j).isEqual(geo)) {      			
        			inList2 = true;
        			break;
        		}
        		
        	}
    		if (!inList2)
    			outputList.add(geo.copy());

        		
        }
        	
    }

   
  
}
