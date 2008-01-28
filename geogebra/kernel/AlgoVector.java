/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
    private GeoPoint startPoint;
        
    /** Creates new AlgoVector */  
    AlgoVector(Construction cons, String label, GeoPoint P, GeoPoint Q) {
        super(cons);
        this.P = P;
        this.Q = Q;                
        // create new vector
        v = new GeoVector(cons);                    
                 
        setInputOutput();
        
        // compute vector PQ        
        compute();        
        v.setLabel(label);
    }           
    
    protected String getClassName() {
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
            
            try {     
            	if (P.isLabelSet())
            		v.setStartPoint(P);
                else  {
                	if (startPoint == null)
                		startPoint = new GeoPoint(P);
                	startPoint.set(P);
                	v.setStartPoint(startPoint);
                }        		
            } catch (CircularDefinitionException e) {}
            
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
