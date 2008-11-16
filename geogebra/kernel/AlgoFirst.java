/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * Take first n objects from a list
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoFirst extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
	private GeoNumeric n; //input
    private GeoList outputList; //output	
    private int size;

    AlgoFirst(Construction cons, String label, GeoList inputList, GeoNumeric n) {
        super(cons);
        this.inputList = inputList;
        this.n=n;

               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoFirst";
    }

    protected void setInputOutput(){
        input = new GeoElement[2];
        input[0] = inputList;
        input[1] = n;

        output = new GeoElement[1];
        output[0] = outputList;
        setDependencies(); // done by AlgoElement
    }

    GeoList getResult() {
        return outputList;
    }

    protected final void compute() {
    	
    	size = inputList.size();
    	int outsize=(int)n.getDouble();
    	
    	if (!inputList.isDefined() ||  size == 0 || outsize < 0 || outsize > size) {
    		outputList.setUndefined();
    		return;
    	} 
       
    	outputList.setDefined(true);
    	outputList.clear();
    	
    	if (outsize == 0) return; // return empty list
    	
    	for (int i=0 ; i<outsize ; i++)
    		outputList.add(inputList.get(i).copyInternal(cons));
   }
  
}
