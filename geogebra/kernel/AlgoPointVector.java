/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


public class AlgoPointVector extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint P;  // input
    private GeoVector v; // input
    private GeoPoint Q;     // output       
        
    AlgoPointVector(Construction cons, String label, GeoPoint P, GeoVector v) {
        super(cons);
        this.P = P;
        this.v = v;         
        Q = new GeoPoint(cons); 
        
        setInputOutput(); // for AlgoElement
        
        // compute line through P, Q
        compute();      
        Q.setLabel(label);
    }   
    
    protected String getClassName() {
        return "AlgoPointVector";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = v;
        
        output = new GeoElement[1];        
        output[0] = Q;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoPoint getQ() { return Q; }
    
    protected final void compute() {
        Q.setCoords(P.inhomX + v.x, P.inhomY + v.y, 1.0);
    }   
    
    final public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
        sb.append(app.getPlain("PointAplusB",input[0].getLabel(),input[1].getLabel()));

        /*
        sb.append(app.getPlain("Point"));
        sb.append(' ');
        sb.append(input[0].getLabel());
        sb.append(" + ");
        sb.append(input[1].getLabel());*/
        
        return sb.toString();
    }
}
