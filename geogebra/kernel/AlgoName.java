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
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 * Returns the name of a GeoElement as a GeoText.
 * @author  Markus
 * @version 
 */
public class AlgoName extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoElement geo;  // input
    private GeoText text;     // output              
        
    public AlgoName(Construction cons, String label, GeoElement geo) {
    	super(cons);
        this.geo = geo;  
        
       text = new GeoText(cons);
       setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        text.setLabel(label);
    }   
    
	String getClassName() {
		return "AlgoName";
	}
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = geo;
        
        output = new GeoElement[1];        
        output[0] = text;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoText getGeoText() { return text; }
    
    // calc the current value of the arithmetic tree
    final void compute() {    	
    	text.setTextString(geo.getLabel());	    	
    }         
}
