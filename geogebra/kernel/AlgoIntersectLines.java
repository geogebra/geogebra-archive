/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * AlgoIntersectLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoIntersectLines extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoLine g, h; // input
    private GeoPoint S; // output       

    /** Creates new AlgoJoinPoints */
    AlgoIntersectLines(Construction cons, String label, GeoLine g, GeoLine h) {
        super(cons);
        this.g = g;
        this.h = h;
        S = new GeoPoint(cons);
        setInputOutput(); // for AlgoElement

        // compute line through P, Q
        compute();
        S.setLabel(label);
    }

    String getClassName() {
        return "AlgoIntersectLines";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = g;
        input[1] = h;

        output = new GeoElement[1];
        output[0] = S;
        setDependencies(); // done by AlgoElement
    }

    GeoPoint getPoint() {
        return S;
    }
    GeoLine geth() {
        return g;
    }
    GeoLine getg() {
        return h;
    }

    // calc intersection S of lines g, h
    final void compute() {   	
        GeoVec3D.cross(g, h, S); 
              
        // test the intersection point
        // this is needed for the intersection of segments
        if (S.isDefined()) {
        	if (!(g.isIntersectionPointIncident(S, Kernel.MIN_PRECISION) &&
			      h.isIntersectionPointIncident(S, Kernel.MIN_PRECISION)) )
				S.setUndefined();
        }
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("IntersectionPointOf"));
            sb.append(" ");
        }
        sb.append(g.getLabel());
        sb.append(", ");
        sb.append(h.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("IntersectionPointOf"));
        }
        return sb.toString();
    }
}
