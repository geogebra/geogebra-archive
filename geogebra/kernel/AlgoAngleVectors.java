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
 * AlgoAngleVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAngleVectors extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoVector v, w; // input
    private GeoAngle angle; // output           

    AlgoAngleVectors(
        Construction cons,
        String label,
        GeoVector v,
        GeoVector w) {
        super(cons);
        this.v = v;
        this.w = w;
        angle = new GeoAngle(cons);
        setInputOutput(); // for AlgoElement

        // compute angle
        compute();
        angle.setLabel(label);
    }

    String getClassName() {
        return "AlgoAngleVectors";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = v;
        input[1] = w;

        output = new GeoElement[1];
        output[0] = angle;
        setDependencies(); // done by AlgoElement
    }

    GeoAngle getAngle() {
        return angle;
    }
    public GeoVector getv() {
        return v;
    }
    public GeoVector getw() {
        return w;
    }

    // calc angle between vectors v and w
    // angle in range [0, 2pi) 
    // use normalvector to 
    final void compute() {
        double value =
            Kernel.trimmedAcos(
                (v.x * w.x + v.y * w.y)
                    / (GeoVec2D.length(v.x, v.y) * GeoVec2D.length(w.x, w.y)));

        // check if angle between pi an 2pi
        if (v.x * w.y < v.y * w.x) {
            value = Kernel.PI_2 - value;
        }
        angle.setValue(value);
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("AngleBetween"));
            sb.append(' ');
        }
        sb.append(v.getLabel());
        sb.append(", ");
        sb.append(w.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("AngleBetween"));
        }

        return sb.toString();
    }
}
