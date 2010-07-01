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
 * Sort a list. Adapted from AlgoMax and AlgoIterationList
 * @author Michael Borcherds
 * @version 04-01-2008
 */

public class AlgoPointList extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
    private GeoList outputList; //output	
    private int size;

    AlgoPointList(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    public String getClassName() {
        return "AlgoPointList";
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
     
        // copy the sorted treeset back into a list
        outputList.setDefined(true);
        outputList.clear();
        
        boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
        for (int i = 0 ; i < size ; i++ ){
        	GeoElement geo = inputList.get(i);
        	if (geo.isGeoList()) {
        		GeoList list = (GeoList)geo;
        		if (list.size() == 2) {
        			GeoElement geoX = list.get(0);
        			GeoElement geoY = list.get(1);
        			if (geoX.isGeoNumeric() && geoY.isGeoNumeric()) {
        				outputList.add(new GeoPoint(cons, null, ((GeoNumeric)geoX).getDouble(), ((GeoNumeric)geoY).getDouble(), 1.0));
        			}
        		}
        		
        	}
        	
        }
		cons.setSuppressLabelCreation(suppressLabelCreation);
        

    }
  
}
