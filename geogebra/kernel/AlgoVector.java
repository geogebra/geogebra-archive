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
 * AlgoVector.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoVector extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint P, Q;   // input
    private GeoVector  v;     // output        
        
    /** Creates new AlgoVector */  
    AlgoVector(Construction cons, String label, GeoPoint P, GeoPoint Q) {
        super(cons);
        this.P = P;
        this.Q = Q;                
        // create new vector
        v = new GeoVector(cons);   
        
        try {     
            v.setStartPoint(P);  
        } catch (CircularDefinitionException e) {}
                 
        setInputOutput();
        
        // compute vector PQ        
        compute();        
        v.setLabel(label);
    }           
    
    String getClassName() {
        return "AlgoVector";
    }
    
    // for AlgoElement
    public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = Q;
        
        output = new GeoElement[1];        
        output[0] = v;        
        setDependencies(); // done by AlgoElement
    }           
    
    GeoVector getVector() { return v; }
    public GeoPoint getP() { return P; }
    public GeoPoint getQ() { return Q; }
    
    // calc the line g through P and Q    
    final void compute() {
        if (Q.isFinite() && P.isFinite()) {        
            v.x = Q.inhomX - P.inhomX;
            v.y = Q.inhomY - P.inhomY;             
            v.z = 0.0;
        } else {
            v.setUndefined();
        }
    }       
    
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append(app.getCommand("Vector") );
        sb.append("[");
        sb.append(P.getLabel());
        sb.append(", ");
        sb.append(Q.getLabel());
        sb.append("]");        
        return sb.toString();
    }
}
