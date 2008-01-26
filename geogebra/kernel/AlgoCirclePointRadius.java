/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoCirclePointRadius.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoCirclePointRadius extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint M; // input
    private NumberValue r; // input
    private GeoElement rgeo;
    private GeoConic circle; // output         

    AlgoCirclePointRadius(
        Construction cons,
        String label,
        GeoPoint M,
        NumberValue r) {
    	
        this(cons, M, r);
        circle.setLabel(label);
    }
    
    AlgoCirclePointRadius(
        Construction cons,
        GeoPoint M,
        NumberValue r) {
    	
        super(cons);
        this.M = M;
        this.r = r;
        rgeo = r.toGeoElement();
        circle = new GeoConic(cons);
        
        setInputOutput(); // for AlgoElement

        compute();            
    }

    String getClassName() {
        return "AlgoCirclePointRadius";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = M;
        input[1] = rgeo;

        output = new GeoElement[1];
        output[0] = circle;
        setDependencies(); // done by AlgoElement
    }

    GeoConic getCircle() {
        return circle;
    }
    GeoPoint getM() {
        return M;
    }

    // compute circle with midpoint M and radius r
    final void compute() {
        circle.setCircle(M, r.getDouble());
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();

        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("Circle"));
            sb.append(' ');
        }
        sb.append(app.getPlain("with"));
        sb.append(' ');
        sb.append(app.getPlain("Center"));
        sb.append(' ');
        sb.append(M.getLabel());
        sb.append(' ');
        sb.append(app.getPlain("and"));
        sb.append(' ');
        sb.append(app.getPlain("Radius"));
        sb.append(' ');
        sb.append(rgeo.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(app.getPlain("Circle"));
        }

        return sb.toString();
    }
}
