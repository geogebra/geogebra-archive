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

/**
 * Normal
 * @author Michael Borcherds
 * @version 20-01-2008
 */

public class AlgoNormal extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private NumberValue a,b,c; //input
    private GeoNumeric num; //output	

    public AlgoNormal(Construction cons, String label, NumberValue a,NumberValue b, NumberValue c) {
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

    protected final void compute() {
    	
    	
    	if (input[0].isDefined() && input[1].isDefined()) {
    		    double mean=a.getDouble();
    		    double sd=b.getDouble();
        		try {
            		num.setValue(getCDF((c.getDouble()-mean)/sd));
        			
        		}
        		catch (Exception e) {
        			num.setUndefined();        			
        		}
    	} else
    		num.setUndefined();
    }       
    
    // http://www4.ncsu.edu/unity/users/p/pfackler/www/ECG790C/accuratecumnorm.pdf
    double getCDF(double x)
    {
    	
	double Cumnorm, build, Exponential;

    	double XAbs = Math.abs(x);
    	if (XAbs > 37) {
    	Cumnorm = 0;
    	} else {
    	Exponential = Math.exp(-XAbs * XAbs / 2.0);
    	if (XAbs < 7.07106781186547) {
    	build = 3.52624965998911E-02 * XAbs + 0.700383064443688;
    	build = build * XAbs + 6.37396220353165;
    	build = build * XAbs + 33.912866078383;
    	build = build * XAbs + 112.079291497871;
    	build = build * XAbs + 221.213596169931;
    	build = build * XAbs + 220.206867912376;
    	Cumnorm = Exponential * build;
    	build = 8.83883476483184E-02 * XAbs + 1.75566716318264;
    	build = build * XAbs + 16.064177579207;
    	build = build * XAbs + 86.7807322029461;
    	build = build * XAbs + 296.564248779674;
    	build = build * XAbs + 637.333633378831;
    	build = build * XAbs + 793.826512519948;
    	build = build * XAbs + 440.413735824752;
    	Cumnorm = Cumnorm / build;
    	} else {
    	build = XAbs + 0.65;
    	build = XAbs + 4 / build;
    	build = XAbs + 3 / build;
    	build = XAbs + 2 / build;
    	build = XAbs + 1 / build;
    	Cumnorm = Exponential / build / 2.506628274631;
    	}
    }
    	if (x > 0) Cumnorm = 1 - Cumnorm;     
    	
    	return Cumnorm;
    }
    
    double normalZ(double X) {
    	return Math.exp(- Math.sqrt(X) / 2.0)/Math.sqrt(2 * Math.PI);
    }

}



