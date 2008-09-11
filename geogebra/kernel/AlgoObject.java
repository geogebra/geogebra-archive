/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


/**
 * Returns the GeoElement from a GeoText.
 * @author  Michael
 * @version 
 */
public class AlgoObject extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoElement geo;  // output
    private GeoText text;     // input              
        
    public AlgoObject(Construction cons, String label, GeoText text) {
    	super(cons);
        this.text = text;  
        
        // don't know what type it should be,
        // so find out:
       geo = kernel.lookupLabel(text.getTextString()).copyInternal(cons);
       setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        text.setLabel(label);
    }   
    
	protected String getClassName() {
		return "AlgoObject";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = text;
        //input[1] = kernel.lookupLabel(text.getTextString());
        
        output = new GeoElement[1];        
        output[0] = geo;        
        //kernel.lookupLabel("A3").addAlgorithm(this);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoElement getResult() { return geo; }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {    	
    	geo = kernel.lookupLabel(text.getTextString());
    }         
}
