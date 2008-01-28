/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoExcentricity.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoExcentricity extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c; // input
    private GeoNumeric num; // output                  

    AlgoExcentricity(Construction cons, String label, GeoConic c) {
        super(cons);
        this.c = c;
        num = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement                
        compute();
        num.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoExcentricity";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;

        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getExcentricity() {
        return num;
    }
    GeoConic getConic() {
        return c;
    }

    // set excentricity
    final void compute() {
        switch (c.type) {
            case GeoConic.CONIC_CIRCLE :
                num.setValue(0.0);
                break;

            case GeoConic.CONIC_HYPERBOLA :
            case GeoConic.CONIC_ELLIPSE :
                num.setValue(c.excent);
                break;

            default :
                num.setUndefined();
        }
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("ExcentricityOf"));
            sb.append(' ');
        }
        sb.append(c.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("ExcentricityOf"));
        }
        return sb.toString();
    }
}
