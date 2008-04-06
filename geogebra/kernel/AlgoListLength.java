/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


/**
 * Length of a GeoList object.
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoListLength extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
    private GeoNumeric length; //output	

    AlgoListLength(Construction cons, String label, GeoList geoList) {
        super(cons);
        this.geoList = geoList;
               
        length = new GeoNumeric(cons);

        setInputOutput();
        compute();
        length.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoListLength";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = geoList;

        output = new GeoElement[1];
        output[0] = length;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getLength() {
        return length;
    }

    protected final void compute() {
    	if (geoList.isDefined())
    		length.setValue(geoList.size());
    	else 
    		length.setUndefined();
    }
    
}
