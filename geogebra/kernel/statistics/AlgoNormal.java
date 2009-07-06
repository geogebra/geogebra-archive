/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel.statistics;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.NumberValue;

import org.apache.commons.math.distribution.DistributionFactory;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.TDistribution;

/**
 * 
 * @author Michael Borcherds
 * @version 20090706
 */

public class AlgoNormal extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private NumberValue a, b, c; //input
    private GeoNumeric num; //output	
    private DistributionFactory factory = app.getDistributionFactory();
    private NormalDistribution normal = null;
    
    public AlgoNormal(Construction cons, String label, NumberValue a, NumberValue b, NumberValue c) {
        super(cons);
        this.a = a;
        this.b = b;
        this.c = c;
               
        num = new GeoNumeric(cons);

        setInputOutput();
        compute();
        num.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoNormal";
    }

    protected void setInputOutput(){
        input = new GeoElement[3];
        input[0] = a.toGeoElement();
        input[1] = b.toGeoElement();
        input[2] = c.toGeoElement();

        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getResult() {
        return num;
    }

    @SuppressWarnings("deprecation")
	protected final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined()) {
		    double param = a.getDouble();
		    double param2 = b.getDouble();
    		    double val = c.getDouble();
        		try {
        			NormalDistribution normal = getDistribution(param, param2);
        			num.setValue(normal.cumulativeProbability(val));     // P(T <= val)
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
        
    @SuppressWarnings("deprecation")
	NormalDistribution getDistribution(double param, double param2) {
    	if (normal == null) 
    		normal = factory.createNormalDistribution();
    	
    		normal.setMean(param);
    		normal.setStandardDeviation(param2);
    	
    	return normal;
    }
    
}



