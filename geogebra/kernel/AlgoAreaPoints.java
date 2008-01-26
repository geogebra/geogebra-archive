/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package geogebra.kernel;

public class AlgoAreaPoints extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint [] P;  // input
    private GeoNumeric area;     // output           
        
    AlgoAreaPoints(Construction cons, String label, GeoPoint [] P) {       
        this(cons, P);
        area.setLabel(label);
    }   
    
    AlgoAreaPoints(Construction cons, GeoPoint [] P) {      
        super(cons); 
        this.P = P;
        area = new GeoNumeric(cons); 
        setInputOutput(); // for AlgoElement
        
        // compute angle
        compute();      
    }   
    
    String getClassName() {
        return "AlgoAreaPoints";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = P;
        
        output = new GeoElement[1];        
        output[0] = area;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoNumeric getArea() { return area; }
    GeoPoint [] getPoints() { return P; }    
    
    // calc area of polygon P[0], ..., P[n]  
    // angle in range [0, pi]
    final void compute() {      
        area.setValue(GeoPolygon.calcArea(P)); 
    }       
    
}
