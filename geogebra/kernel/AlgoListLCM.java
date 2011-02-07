/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import java.math.BigInteger;

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

    public String getClassName() {
        return "AlgoListLCM";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = geoList;

        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getLCM() {
        return num;
    }

    protected final void compute() {
    	int size = geoList.size();
    	if (!geoList.isDefined() ||  size == 0) {
    		num.setUndefined();
    		return;
    	}
    	
    	if (!geoList.getGeoElementForPropertiesDialog().isGeoNumeric()) {
    		num.setUndefined();
    		return;   		
    	}
    	
    	BigInteger gcd = BigInteger.valueOf((long)((GeoNumeric)(geoList.get(0))).getDouble());
    	
    	for (int i = 1 ; i < geoList.size() ; i++) {
        	BigInteger n = BigInteger.valueOf((long)((GeoNumeric)(geoList.get(i))).getDouble());
    		gcd = gcd.gcd(n);
    	}
    	
    	BigInteger result = BigInteger.valueOf(1);
    	
    	for (int i = 0 ; i < geoList.size() ; i++) {
        	BigInteger n = BigInteger.valueOf((long)((GeoNumeric)(geoList.get(i))).getDouble());
    		n = n.divide(gcd);
    		result = result.multiply(n);
    	}
    	
    	double resultD = Math.abs(result.multiply(gcd).doubleValue());
    	
    	// can't store integers greater than this in a double accurately
    	if (resultD > 1e15) {
    		num.setUndefined();
    		return;
    	}
    	
    	num.setValue(resultD);
    	
    }
    
}
