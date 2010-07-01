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
            g = (GeoDeriveable) fgeo.copyInternal(cons);  // output
            ggeo = g.toGeoElement();
            
            setInputOutput(); // for AlgoElement    
            compute();            
     }

    public String getClassName() {
        return "AlgoDerivative";
    }

    // for AlgoElement
    protected void setInputOutput() {
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

    protected final void compute() {
        if (!fgeo.isDefined()) {
            ggeo.setUndefined();
            return;
        }

        if (order == null) {            
            g.setDerivative(f, 1);
              
        } else {
            double ord;
            if (orderGeo.isDefined() && (ord = order.getDouble()) > -Kernel.MIN_PRECISION) {                
                g.setDerivative(f, (int) Math.round(ord));
            } else {
                ggeo.setUndefined();
            }
        }      
    }

    StringBuilder sb = new StringBuilder();   
    final public String toString() {
        if (sb == null) sb = new StringBuilder();
        else sb.setLength(0);
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        if (order != null)
        	sb.append(app.getPlain("AthDerivativeOfB",orderGeo.getLabel(),fgeo.getLabel()));
        else
        	sb.append(app.getPlain("DerivativeOfA",fgeo.getLabel()));
        
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
