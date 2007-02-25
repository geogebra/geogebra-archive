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
 * AlgoCircleTwoPoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoCircleTwoPoints extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint M, P; // input    
    private GeoConic circle; // output         

    public AlgoCircleTwoPoints(
        Construction cons,
        GeoPoint M,
        GeoPoint P) {
        super(cons);
        this.M = M;
        this.P = P;
        circle = new GeoConic(cons);
        circle.addPointOnConic(P);
        setInputOutput(); // for AlgoElement

        compute();
    }
    
    AlgoCircleTwoPoints(
            Construction cons,
            String label,
            GeoPoint M,
            GeoPoint P) {
         this(cons, M, P);
         circle.setLabel(label);
    }

    String getClassName() {
        return "AlgoCircleTwoPoints";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = M;
        input[1] = P;

        output = new GeoElement[1];
        output[0] = circle;
        setDependencies(); // done by AlgoElement
    }

    public GeoConic getCircle() {
        return circle;
    }
    GeoPoint getM() {
        return M;
    }
    GeoPoint getP() {
        return P;
    }

    // compute circle with midpoint M and radius r
    final void compute() {
        circle.setCircle(M, P);
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
        sb.append(app.getPlain("through"));
        sb.append(' ');
        sb.append(P.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(app.getPlain("Circle"));
        }

        return sb.toString();
    }
}
