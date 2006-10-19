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
 * AlgoAngleConic.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAngleConic extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c; // input
    private GeoAngle angle; // output                  

    AlgoAngleConic(Construction cons, String label, GeoConic c) {
        super(cons);
        this.c = c;
        angle = new GeoAngle(cons);
        setInputOutput(); // for AlgoElement                
        compute();
        angle.setLabel(label);
    }

    String getClassName() {
        return "AlgoAngleConic";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;

        output = new GeoElement[1];
        output[0] = angle;
        setDependencies(); // done by AlgoElement
    }

    GeoAngle getAngle() {
        return angle;
    }
    GeoConic getConic() {
        return c;
    }

    // compute conic's angle
    final void compute() {
        // take a look at first eigenvector
        angle.setValue(Math.atan2(c.eigenvec[0].y, c.eigenvec[0].x));
    }

    public final String toString() {
        StringBuffer sb = new StringBuffer();
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(c.getLabel());
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
            sb.append(app.getPlain("Angle"));
        } else {
            sb.append(app.getPlain("Angle"));
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
            sb.append(c.getLabel());
        }
        return sb.toString();
    }
}
