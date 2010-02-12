/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoOrthoLinePointLine.java
 *
 * line through P orthogonal to l
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoOrthoLinePointLine extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint P; // input
    private GeoLine l; // input
    private GeoLine g; // output       

    /** Creates new AlgoJoinPoints */
    AlgoOrthoLinePointLine(
        Construction cons,
        String label,
        GeoPoint P,
        GeoLine l) {
        super(cons);
        this.P = P;
        this.l = l;
        g = new GeoLine(cons);
        g.setStartPoint(P);
        setInputOutput(); // for AlgoElement

        // compute line 
        compute();
        g.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoOrthoLinePointLine";
    }

    // for AlgoElement
    public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = l;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    GeoLine getLine() {
        return g;
    }
    GeoPoint getP() {
        return P;
    }
    GeoLine getl() {
        return l;
    }

    // calc the line g through P and normal to l   
    protected final void compute() {
        GeoVec3D.cross(P, l.x, l.y, 0.0, g);
    }

    public final String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("LineThroughAPerpendicularToB",P.getLabel(),l.getLabel());

    }
}
