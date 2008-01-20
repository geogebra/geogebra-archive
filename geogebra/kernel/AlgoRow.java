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
 * Turn a spreadsheet row into a list. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 09-01-2008
 */

public class AlgoRow extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private NumberValue startValue, n; // input
	private GeoElement startValueGeo, nGeo;
	private GeoElement cellGeo,cellGeoA1;
    private GeoList outputList; //output	

    AlgoRow(Construction cons, String label, NumberValue startValue, NumberValue n) {
        super(cons);
        this.startValue = startValue;
        startValueGeo = startValue.toGeoElement();
        this.n = n;
        nGeo = n.toGeoElement();
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    String getClassName() {
        return "AlgoRow";
    }

    void setInputOutput(){
        input = new GeoElement[2];
        input[0] = startValueGeo;
        input[1] = nGeo;

        output = new GeoElement[1];
        output[0] = outputList;
        setDependencies(); // done by AlgoElement
    }

    GeoList getResult() {
        return outputList;
    }

    final void compute() {
		if (startValue.getDouble()!=Math.floor(startValue.getDouble()) || n.getDouble()!=Math.floor(n.getDouble()))
		{
    		outputList.setUndefined();
    		return;
		}
		
		int row = (int)startValue.getDouble();
		int columns = (int)n.getDouble();
		
		cellGeoA1 = kernel.lookupLabel(GeoElement.getSpreadsheetCellName(0, 0)); // A1
		outputList.clear();
		
		for (int i=0 ; i<columns ; i++)
		{   	
    	  try {
			cellGeo = kernel.lookupLabel(GeoElement.getSpreadsheetCellName(i, row));
    	    outputList.add(cellGeo);
    	  }
    	  catch (Exception e)
    	  {
    		  outputList.remove(cellGeo);
    		  outputList.add(cellGeoA1); // copies cell A1 TODO should be value 0
    	  }
		}
    }
 }
