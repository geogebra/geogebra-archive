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
import org.apache.commons.math.distribution.TDistribution;

/**
 * 
 * @author Michael Borcherds
 * @version 20090706
 */

public class AlgoTDistribution extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private NumberValue a,b; //input
    private GeoNumeric num; //output	
    private DistributionFactory factory = app.getDistributionFactory();
    private TDistribution t = null;
    
    public AlgoTDistribution(Construction cons, String label, NumberValue a,NumberValue b) {
        super(cons);
        this.a = a;
        this.b = b;
               
        num = new GeoNumeric(cons);

        setInputOutput();
        compute();
        num.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoTDistribution";
    }

    protected void setInputOutput(){
        input = new GeoElement[2];
        input[0] = a.toGeoElement();
        input[1] = b.toGeoElement();

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
    		    double val = b.getDouble();
        		try {
        			DistributionFactory factory = app.getDistributionFactory();
        			TDistribution t = getDistribution(param);
        			num.setValue(t.cumulativeProbability(val));     // P(T <= val)
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
        
    TDistribution getDistribution(double param) {
    	if (t == null) 
    		t = factory.createTDistribution(param);
    	
    	if (t.getDegreesOfFreedom() != param)
    		t.setDegreesOfFreedom(param);
    	
    	return t;
    }
    
}



