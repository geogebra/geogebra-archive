/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoEllipseFociLength.java
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
public class AlgoEllipseFociLength extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint A, B; // input    
    private NumberValue a; // input     
    private GeoElement ageo;
    private GeoConic ellipse; // output             

    AlgoEllipseFociLength(
        Construction cons,
        String label,
        GeoPoint A,
        GeoPoint B,
        NumberValue a) {
        super(cons);
        this.A = A;
        this.B = B;
        this.a = a;
        ageo = a.toGeoElement();
        ellipse = new GeoConic(cons);
        setInputOutput(); // for AlgoElement

        compute();
        ellipse.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoEllipseFociLength";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = A;
        input[1] = B;
        input[2] = ageo;

        output = new GeoElement[1];
        output[0] = ellipse;
        setDependencies(); // done by AlgoElement
    }

    GeoConic getEllipse() {
        return ellipse;
    }
    GeoPoint getFocus1() {
        return A;
    }
    GeoPoint getFocus2() {
        return B;
    }

    // compute ellipse with foci A, B and length of half axis a
    protected final void compute() {
        ellipse.setEllipseHyperbola(A, B, a.getDouble());
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();

        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("EllipseWithFociABandFirstAxisLengthC",A.getLabel(),B.getLabel(),ageo.getLabel()));
        
        /*
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("Ellipse"));
            sb.append(' ');
            sb.append(app.getPlain("with"));
            sb.append(' ');
        }
        sb.append(app.getPlain("Foci"));
        sb.append(' ');
        sb.append(A.getLabel());
        sb.append(", ");
        sb.append(B.getLabel());
        sb.append(' ');
        sb.append(app.getPlain("and"));
        sb.append(' ');
        sb.append(app.getPlain("firstAxisLength"));
        sb.append(' ');
        sb.append(ageo.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("Ellipse"));
        }*/
        
        return sb.toString();
    }
}
