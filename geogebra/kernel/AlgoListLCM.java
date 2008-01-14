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
 * LCM of a list.
 * adapted from AlgoListMax
 * @author Michael Borcherds
 * @version 03-01-2008
 */

public class AlgoListLCM extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
    private GeoNumeric num; //output	

    AlgoListLCM(Construction cons, String label, GeoList geoList) {
        super(cons);
        this.geoList = geoList;
               
        num = new GeoNumeric(cons);

        setInputOutput();
        compute();
        num.setLabel(label);
    }

    String getClassName() {
        return "AlgoListLCM";
    }

    void setInputOutput(){
        input = new GeoElement[1];
        input[0] = geoList;

        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getLCM() {
        return num;
    }

    final void compute() {
    	int size = geoList.size();
    	if (!geoList.isDefined() ||  size == 0) {
    		num.setUndefined();
    		return;
    	}
    	
    	String yacasList=geoList.toValueString();
    	String yacasCommand="Lcm("+yacasList+")";    	
		String result=kernel.evaluateYACASRaw(yacasCommand);
		try {
			double lcm = Double.valueOf(result).doubleValue();
			num.setValue(lcm);
			
		}
		catch (Exception e) {
			num.setUndefined();	
		}
    }
    
}
