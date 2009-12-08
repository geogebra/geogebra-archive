/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;


/**
 * Sum[{A,B,C}]
 * @author Michael Borcherds
 * @version 2008-10-16
 */

public class AlgoSumPoints extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
    public GeoNumeric Truncate; //input	
    public GeoElement result; //output	
    

    
    public AlgoSumPoints(Construction cons, String label, GeoList geoList) {
        this(cons, label, geoList, null);
    }

    public AlgoSumPoints(Construction cons, String label, GeoList geoList, GeoNumeric Truncate) {
        super(cons);
        this.geoList = geoList;

        this.Truncate=Truncate;
        
        if (geoList.get(0).isGeoVector())
        	result = new GeoVector(cons);
        else // Numeric or Point
        	result = new GeoPoint(cons);

        setInputOutput();
        compute();
        result.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoSumPoints";
    }

    protected void setInputOutput(){
    	if (Truncate == null) {
	        input = new GeoElement[1];
	        input[0] = geoList;
    	}
    	else {
    		 input = new GeoElement[2];
             input[0] = geoList;
             input[1] = Truncate;
    	}

        output = new GeoElement[1];
        output[0] = result;
        setDependencies(); // done by AlgoElement
    }

    public GeoElement getResult() {
        return result;
    }
    

    protected final void compute() {
    	
    	// TODO: remove
    	//Application.debug("compute: " + geoList);
    	
    	int truncate;
    	int size = geoList.size();

    	if (Truncate!=null)
    	{
    		truncate=(int)Truncate.getDouble();
    		if (truncate<1 || truncate>size)
    		{
        		result.setUndefined();
        		return;
    		}
    		size=truncate; // truncate the list
    	}
    	
    	if (!geoList.isDefined() ||  size == 0) {
    		result.setUndefined();
    		return;
    	}
    	
    	
    	double x = 0, y = 0;
    	
    	for (int i = 0 ; i < size ; i++) {
    		GeoElement p = geoList.get(i);
    		if (p.isGeoPoint()) {
	        	x += ((GeoPoint)p).getInhomX();
	        	y += ((GeoPoint)p).getInhomY();
    		} else if (p.isGeoVector()) {
	        	x += ((GeoVector)p).getX();
	        	y += ((GeoVector)p).getY();   		
    		} else if (p.isNumberValue()) {
	        	x += ((NumberValue)p).getDouble();
    		} else {
				result.setUndefined();
				return;
    		}
    	}
   	
   	
    	((GeoVec3D)result).setCoords(x, y, 1.0);
   	

    }
    
}
