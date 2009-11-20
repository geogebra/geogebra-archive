/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoRotateVector.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoRotateVector extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoVector A; // input
    private GeoNumeric angle; // input
    private GeoVector B; // output        

    AlgoRotateVector(
        Construction cons,
        String label,
        GeoVector A,
        GeoNumeric angle) {
        super(cons);
        this.A = A;
        this.angle = angle;

        // create new Vector
        B = new GeoVector(cons);
        setInputOutput();

        compute();
        B.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoRotateVector";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = A;
        input[1] = angle;

        output = new GeoElement[1];
        output[0] = B;
        setDependencies(); // done by AlgoElement
    }

    GeoVector getVector() {
        return A;
    }
    GeoNumeric getAngle() {
        return angle;
    }
    GeoVector getRotatedVector() {
        return B;
    }

    // calc rotated Vector
    protected final void compute() {
        B.setCoords(A);
        B.rotate(angle);
    }

    final public String toString() {
        StringBuilder sb = new StringBuilder();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("ARotatedByAngleB",A.getLabel(),angle.getLabel()));
        
        return sb.toString();
    }
}
