/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

/**
 * Derivative of a function (GeoFunction)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDerivative extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoElement fgeo, ggeo;
	private GeoDeriveable f; // input
    private NumberValue order;
    private GeoDeriveable g; // output g = f'    

    private GeoElement orderGeo;

    public AlgoDerivative(Construction cons, String label, GeoDeriveable f) {
        this(cons, label, f, null);
    }

    public AlgoDerivative(
        Construction cons,
        String label,
        GeoDeriveable f,
        NumberValue order) 
    {
        this(cons, f, order);
        ggeo.setLabel(label);
    }
    
    AlgoDerivative(Construction cons,  GeoDeriveable f) {
    	this(cons, f, null);    	
    }
    
    AlgoDerivative(Construction cons,  GeoDeriveable f, NumberValue order) {
            super(cons);
            this.f = f;
            this.order = order;
            if (order != null)
                orderGeo = order.toGeoElement();
            
            this.fgeo = f.toGeoElement();        
            g = (GeoDeriveable) fgeo.copyInternal();  // output
            ggeo = g.toGeoElement();
            
            setInputOutput(); // for AlgoElement    
            compute();            
     }

    String getClassName() {
        return "AlgoDerivative";
    }

    // for AlgoElement
    void setInputOutput() {
        int length = (order == null) ? 1 : 2;
        input = new GeoElement[length];
        input[0] = fgeo;
        if (orderGeo != null)
            input[1] = orderGeo;

        output = new GeoElement[1];
        output[0] = ggeo;
        setDependencies(); // done by AlgoElement
    }

    public GeoElement getDerivative() {
        return ggeo;
    }

    final void compute() {
        if (!fgeo.isDefined()) {
            ggeo.setUndefined();
            return;
        }

        if (order == null) {            
            g.setDerivative(f, 1);
        } else {
            double ord;
            if (orderGeo.isDefined() && (ord = order.getDouble()) >= 0) {                
                g.setDerivative(f, (int) Math.round(ord));
            } else {
                ggeo.setUndefined();
            }
        }
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();   
        if (!app.isReverseLanguage()) { //FKH 20040906
            if (order != null) {
                sb.append(orderGeo.getLabel());
                sb.append(". ");
            }
            sb.append(app.getPlain("Derivative"));
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
        }
        sb.append(fgeo.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
            if (order != null) {
                sb.append(orderGeo.getLabel());
                sb.append(". ");
            }
            sb.append(app.getPlain("Derivative"));
        }
        if (!fgeo.isIndependent()) { // show the symbolic representation too
            sb.append(": ");
            sb.append(ggeo.getLabel());
            sb.append('(');
            sb.append(g.getVarString());
            sb.append(")= ");
    		sb.append(g.toSymbolicString());            
        }
        return sb.toString();
    }

}
