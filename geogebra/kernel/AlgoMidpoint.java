/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoMidPoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoMidpoint extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint P, Q; // input
    private GeoPoint M; // output        

    /** Creates new AlgoVector */
    AlgoMidpoint(Construction cons, String label, GeoPoint P, GeoPoint Q) {
    	this(cons, P, Q);
    	M.setLabel(label);
    }
	
    AlgoMidpoint(Construction cons, GeoPoint P, GeoPoint Q) {
        super(cons);
        this.P = P;
        this.Q = Q;
        // create new Point
        M = new GeoPoint(cons);
        setInputOutput();

        // compute M = (P + Q)/2
        compute();        
    }

    String getClassName() {
        return "AlgoMidpoint";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = Q;

        output = new GeoElement[1];
        output[0] = M;
        setDependencies(); // done by AlgoElement
    }

    GeoPoint getPoint() {
        return M;
    }

    // calc midpoint
    final void compute() {
        boolean pInf = P.isInfinite();
        boolean qInf = Q.isInfinite();

        if (!pInf && !qInf) {
            // M = (P + Q) / 2          
            M.setCoords(
                (P.inhomX + Q.inhomX) / 2.0d,
                (P.inhomY + Q.inhomY) / 2.0d,
                1.0);
        } else if (pInf && qInf)
            M.setUndefined();
        else if (pInf)
            M.setCoords(P);
        else // qInf
            M.setCoords(Q);
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("MidpointOf"));
            sb.append(" ");
        }
        sb.append(P.getLabel());
        sb.append(", ");
        sb.append(Q.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(" ");
            sb.append(app.getPlain("MidpointOf"));
        }
        return sb.toString();
    }
}
