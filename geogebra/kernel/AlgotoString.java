/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;


/**
 * Returns the name of a GeoElement as a GeoText.
 * @author  Markus
 * @version 
 */
public class AlgotoString extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoElement geo;  // input
    private GeoText text;     // output              
        
    public AlgotoString(Construction cons, String label, GeoElement geo) {
    	super(cons);
        this.geo = geo;  
        
       text = new GeoText(cons);
       setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        text.setLabel(label);
    }   
    
	protected String getClassName() {
		return "AlgotoString";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = geo;
        
        output = new GeoElement[1];        
        output[0] = text;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoText getGeoText() { return text; }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {    
    	int tempCASPrintForm = kernel.getCASPrintForm();
    	kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA);
    	String geoDef="";
    	if (geo.isGeoFunction()) {
     	// get current definition of geo
    		GeoFunction geoFun = (GeoFunction)geo;
     		geoDef = geoFun.isIndependent() ? 
     				geoFun.toValueString() :
     				geoFun.getFunction().toString();
    	}
    	else
    		geoDef = geo.getCommandDescription();
    	
    	text.setTextString(geoDef);	    	
    	kernel.setCASPrintForm(tempCASPrintForm);
    }         
}
