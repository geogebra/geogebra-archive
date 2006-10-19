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
 * AlgoDistancePoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDistancePoints extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint P, Q; // input
    private GeoNumeric dist; // output       

    AlgoDistancePoints(
        Construction cons,
        String label,
        GeoPoint P,
        GeoPoint Q) {
        super(cons);
        this.P = P;
        this.Q = Q;
        dist = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        dist.setLabel(label);
    }

    String getClassName() {
        return "AlgoDistancePoints";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = Q;

        output = new GeoElement[1];
        output[0] = dist;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getDistance() {
        return dist;
    }
    GeoPoint getP() {
        return P;
    }
    GeoPoint getQ() {
        return Q;
    }

    // calc length of vector v   
    final void compute() {
        dist.setValue(P.distance(Q));
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("DistanceOf"));
            sb.append(' ');
        }
        sb.append(P.getLabel());
        sb.append(' ');
        sb.append(app.getPlain("and"));
        sb.append(' ');
        sb.append(Q.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("DistanceOf"));
        }
        return sb.toString();
    }
}
