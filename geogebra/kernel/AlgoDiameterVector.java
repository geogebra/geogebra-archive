/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * AlgoDiameterLineVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDiameterVector extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c; // input
    private GeoVector v; // input
    private GeoLine diameter; // output

    /** Creates new AlgoDiameterVector */
    AlgoDiameterVector(
        Construction cons,
        String label,
        GeoConic c,
        GeoVector v) {
        super(cons);
        this.v = v;
        this.c = c;
        diameter = new GeoLine(cons);

        setInputOutput(); // for AlgoElement

        compute();
        diameter.setLabel(label);
    }

    String getClassName() {
        return "AlgoDiameterVector";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = v;
        input[1] = c;

        output = new GeoElement[1];
        output[0] = diameter;
        setDependencies(); // done by AlgoElement
    }

    GeoVector getVector() {
        return v;
    }
    GeoConic getConic() {
        return c;
    }
    GeoLine getDiameter() {
        return diameter;
    }

    // calc diameter line of v relativ to c
    final void compute() {
        c.diameterLine(v, diameter);
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("DiameterLineOf"));
            sb.append(' ');
        }
        sb.append(c.getLabel());
        sb.append(' ');
        sb.append(app.getPlain("conjugateTo"));
        sb.append(' ');
        sb.append(v.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("DiameterLineOf"));
        }
        return sb.toString();
    }
}
