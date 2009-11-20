/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoTranslatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;



/**
 * Vector w = v starting at A
 * @author  Markus
 * @version 
 */
public class AlgoTranslateVector extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint A;   // input
    private GeoVector v;  // input
    private GeoVector w;     // output        
            
    AlgoTranslateVector(Construction cons, String label,  GeoVector v, GeoPoint A) {
        super(cons);
        this.A = A;        
        this.v = v;
        
        // create new Point
        w = new GeoVector(cons);  
        
        try {     
            w.setStartPoint(A);
        } catch (CircularDefinitionException e) {}
        
        setInputOutput();
                
        compute();        
        w.setLabel(label);
    }           
    
    protected String getClassName() {
        return "AlgoTranslateVector";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = v;        
        input[1] = A;        
        
        output = new GeoElement[1];        
        output[0] = w;        
        setDependencies(); // done by AlgoElement
    }           
        
    GeoPoint getPoint() { return A; }
    GeoVector getVector() { return v; }
    GeoVector getTranslatedVector() { return w; }
        
    // simply copy v
    protected final void compute() {
        w.setCoords(v);        
    }       
    
    final public String toString() {
        StringBuilder sb = new StringBuilder();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("TranslationOfAtoB",v.getLabel(),A.getLabel()));

        return sb.toString();
    }
}
