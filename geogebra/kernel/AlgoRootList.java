/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;



/**
 * RootList[{1,2,3}] makes { (1,0) (2,0), (3,0) }. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 04-04-2010
 */

public class AlgoRootList extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
    private GeoList outputList; //output	
    private int size;

    AlgoRootList(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    public String getClassName() {
        return "AlgoRootList";
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
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size == 0) {
    		outputList.setUndefined();
    		return;
    	} 
     
        outputList.setDefined(true);
        outputList.clear();
        
        boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
        for (int i = 0 ; i < size ; i++ ){
        	GeoElement geo = inputList.get(i);
        	if (geo.isGeoNumeric()) {
        		GeoNumeric num = (GeoNumeric)geo;
        		outputList.add(new GeoPoint(cons, null, num.getDouble(), 0.0, 1.0));
        		
        	}
        	
        }
		cons.setSuppressLabelCreation(suppressLabelCreation);
        

    }
  
}
