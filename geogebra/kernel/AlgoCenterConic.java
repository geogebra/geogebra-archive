/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoMidpointConic.java
 *
 * Created on 11. November 2001, 21:37
 */

package geogebra.kernel;


/**
 * Center of a conic section. 
 */
public class AlgoCenterConic extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c; // input
    private GeoPoint midpoint; // output                 

    AlgoCenterConic(Construction cons, String label, GeoConic c) {
        super(cons);
        this.c = c;
        midpoint = new GeoPoint(cons);
        setInputOutput(); // for AlgoElement

        compute();
        midpoint.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoCenterConic";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;

        output = new GeoElement[1];
        output[0] = midpoint;
        setDependencies(); // done by AlgoElement
    }

    GeoConic getConic() {
        return c;
    }
    GeoPoint getPoint() {
        return midpoint;
    }

    final void compute() {
        if (!c.isDefined()) {
            midpoint.setUndefined();
            return;
        }

        switch (c.type) {
            case GeoConic.CONIC_CIRCLE :
            case GeoConic.CONIC_ELLIPSE :
            case GeoConic.CONIC_HYPERBOLA :
            case GeoConic.CONIC_SINGLE_POINT :
            case GeoConic.CONIC_INTERSECTING_LINES :
                midpoint.setCoords(c.b.x, c.b.y, 1.0d);
                break;

            default :
                // midpoint undefined
                midpoint.setUndefined();
        }
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(c.getLabel());
            sb.append(' ');
            sb.append(app.getPlain("CenterOf"));
        } else {
            sb.append(app.getPlain("CenterOf"));
            sb.append(' ');
            sb.append(c.getLabel());
        }

        return sb.toString();
    }
}
