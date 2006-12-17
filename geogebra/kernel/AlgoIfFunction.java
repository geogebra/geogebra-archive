/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.kernel;


/**
 * Special case of if for functions.
 * Example:  If[ x < 2, x^2, x + 2 ]
 * 
 * @author  Markus
 * @version 
 */
public class AlgoIfFunction extends AlgoElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoFunction boolFun;     // input
	private GeoFunction ifFun, elseFun;  // input
	private GeoFunction result; // output
    	   
    public AlgoIfFunction(Construction cons, String label, 
    		GeoFunction boolFun, 
    		GeoFunction ifFun, GeoFunction elseFun) {
    	
    	super(cons);
    	this.boolFun = boolFun;
        this.ifFun = ifFun;
        this.elseFun = elseFun;               
        
        // create output GeoElement of same type as ifGeo
        result = new GeoFunctionConditional(cons, boolFun, ifFun, elseFun);
        
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        result.setLabel(label);
    }   
    
	String getClassName() {
		return "AlgoIfFunction";
	}
    
    // for AlgoElement
    void setInputOutput() {
    	if (elseFun != null)
    		input = new GeoElement[3];
    	else
    		input = new GeoElement[2];    	
        input[0] = boolFun;
        input[1] = ifFun;
        if (elseFun != null)
        	input[2] = elseFun;        	
        
        output = new GeoElement[1];        
        output[0] = result;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoFunction getGeoFunction() { return result; }
       
    final void compute() {	
    	// nothing to do here: this algorithm is only
    	// needed to make sure the resulting function is
    	// updated when ifFun or elseFun is updated
    	    	    	
    	if (ifFun.isDefined())
    		// set fun to dummy value to show that the function has changed
    		// note: result is a GeoFunctionConditional object 
    		result.setFunction(ifFun.getFunction());
    	else
    		result.setUndefined();
    }   
    
    final public String toString() {        
        return getCommandDescription();
    }
}
