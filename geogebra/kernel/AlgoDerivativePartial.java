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
 * Partial derivative of a multivariate function (GeoFunctionNVar)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDerivativePartial extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunctionNVar f, g; // input, output g = f'
    private GeoNumeric var;
	private NumberValue order;
    private GeoElement orderGeo;

    public AlgoDerivativePartial(
        Construction cons,
        String label,
        GeoFunctionNVar f, GeoNumeric var, NumberValue order) 
    {
        this(cons, f, var, order);
        g.setLabel(label);
    }
    
    AlgoDerivativePartial(Construction cons,  GeoFunctionNVar f, GeoNumeric var, NumberValue order) {
            super(cons);
            this.f = f;
            this.var = var;
            this.order = order;
            if (order != null)
                orderGeo = order.toGeoElement();
       
            g = new GeoFunctionNVar(cons);  // output
            
            setInputOutput(); // for AlgoElement    
            compute();            
     }

    public String getClassName() {
        return "AlgoDerivativePartial";
    }

    // for AlgoElement
    protected void setInputOutput() {
        int length = (order == null) ? 2 : 3;
        input = new GeoElement[length];
        input[0] = f;
        input[1] = var;
        if (orderGeo != null)
            input[2] = orderGeo;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoFunctionNVar getDerivative() {
        return g;
    }

    protected final void compute() {
        if (!f.isDefined()) {
            g.setUndefined();
            return;
        }

        if (order == null) {            
            g.setDerivative(f, var.getLabel(), 1);
              
        } else {
            double ord;
            if (orderGeo.isDefined() && (ord = order.getDouble()) > -Kernel.MIN_PRECISION) {                
                g.setDerivative(f, var.getLabel(), (int) Math.round(ord));
            } else {
                g.setUndefined();
            }
        }      
    }

}
