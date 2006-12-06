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
	private GeoFunction f; // input
    private NumberValue order;
    private GeoFunction g; // output g = f'    

    private GeoElement orderGeo;

    /** Creates new AlgoDependentFunction */
    public AlgoDerivative(Construction cons, String label, GeoFunction f) {
        this(cons, label, f, null);
    }

    public AlgoDerivative(
        Construction cons,
        String label,
        GeoFunction f,
        NumberValue order) {
        super(cons);
        this.f = f;
        this.order = order;
        if (order != null)
            orderGeo = order.toGeoElement();

        g = (GeoFunction) f.copyInternal();  // output
        setInputOutput(); // for AlgoElement    
        compute();
        g.setLabel(label);
    }

    String getClassName() {
        return "AlgoDerivative";
    }

    // for AlgoElement
    void setInputOutput() {
        int length = (order == null) ? 1 : 2;
        input = new GeoElement[length];
        input[0] = f;
        if (orderGeo != null)
            input[1] = orderGeo;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getDerivative() {
        return g;
    }

    final void compute() {
        if (!f.isDefined()) {
            g.setUndefined();
            return;
        }

        if (order == null) {
            g.setDefined(true);
            g.setDerivative(f, 1);
        } else {
            double ord;
            if (orderGeo.isDefined() && (ord = order.getDouble()) >= 0) {
                g.setDefined(true);
                g.setDerivative(f, (int) Math.round(ord));
            } else {
                g.setUndefined();
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
        sb.append(f.getLabel());
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
        if (!f.isIndependent()) { // show the symbolic representation too
            sb.append(": ");
            sb.append(g.getLabel());
            sb.append("(x) = ");
    		sb.append(g.toSymbolicString());            
        }
        return sb.toString();
    }

}
