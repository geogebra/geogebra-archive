/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDistancePointLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDistancePointObject extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint P; // input
    private GeoElement g; // input
    private GeoNumeric dist; // output       

    AlgoDistancePointObject(
        Construction cons,
        String label,
        GeoPoint P,
        GeoElement g) {
        super(cons);
        this.P = P;
        this.g = g;
        dist = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        dist.setLabel(label);
    }

    public String getClassName() {
        return "AlgoDistancePointObject";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = g;

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
    GeoElement getg() {
        return g;
    }

    // calc length of vector v   
    protected final void compute() {
        dist.setValue(g.distance(P));
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("DistanceOfAandB",P.getLabel(),g.getLabel());
    }
}
