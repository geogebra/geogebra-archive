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
 * AlgoJoinPoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoJoinPoints extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint P, Q;  // input
    private GeoLine  g;     // output       
        
    /** Creates new AlgoJoinPoints */
    AlgoJoinPoints(Construction cons, String label, GeoPoint P, GeoPoint Q) {
        super(cons);
        this.P = P;
        this.Q = Q;                
        g = new GeoLine(cons); 
        g.setStartPoint(P);
        g.setEndPoint(Q);
        setInputOutput(); // for AlgoElement
        
        // compute line through P, Q
        compute();      
        g.setLabel(label);
    }   
    
    String getClassName() {
        return "AlgoJoinPoints";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = Q;
        
        output = new GeoElement[1];        
        output[0] = g;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoLine getLine() { return g; }
    GeoPoint getP() { return P; }
    GeoPoint getQ() { return Q; }
    
    // calc the line g through P and Q    
    final void compute() {
        // g = P v Q  <=>  g_n : n = P x Q
        // g = cross(P, Q)
        GeoVec3D.lineThroughPoints(P, Q, g);
    }   
    
    final public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append(app.getPlain("LineThrough"));
        sb.append(' ');
        sb.append(P.getLabel());
        sb.append(", ");
        sb.append(Q.getLabel());
        
        return sb.toString();
    }
}
