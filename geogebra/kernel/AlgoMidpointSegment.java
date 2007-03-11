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
public class AlgoMidpointSegment extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoSegment segment; // input
    private GeoPoint M; // output        
    private GeoPoint P, Q; // endpoints of segment
    
    /** Creates new AlgoVector */
    AlgoMidpointSegment(Construction cons, String label, GeoSegment segment) {
    	this(cons, segment);
    	M.setLabel(label);
    }
	
    AlgoMidpointSegment(Construction cons, GeoSegment segment) {
        super(cons);
        this.segment = segment;
        
        // create new Point
        M = new GeoPoint(cons);
        setInputOutput();
        
        P = segment.getStartPoint();
    	Q = segment.getEndPoint();

        // compute M = (P + Q)/2
        compute();        
    }

    String getClassName() {
        return "AlgoMidpointSegment";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = segment;        

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
        sb.append(segment.getLabel());        
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(" ");
            sb.append(app.getPlain("MidpointOf"));
        }
        return sb.toString();
    }
}
