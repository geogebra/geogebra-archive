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
 * Algorithm to invoke a specific macro. 
 * 
 * @author  Markus
 * @version 
 */
public class AlgoMacro extends AlgoElement {

	private static final long serialVersionUID = 1L;	
	
	private Macro macro;   
        
    /**
     * Creates a new algorithm that applies a macro to the
     * given input objects.        
     */
    public AlgoMacro(Construction cons, String [] labels, Macro macro, GeoElement [] input) {
    	super(cons);
    	    	    	    	     
        this.macro = macro;
    	this.input = input;
    	this.output = macro.createOutputCopies();                             
    	
    	setInputOutput();                 
        compute();
        
        GeoElement.setLabels(labels, output);
    }   
    
	String getClassName() {
		return "AlgoMacro";
	}
	
	String getCommandName() {
		return macro.getName();
	}
    
    void setInputOutput() {    	             
        setDependencies(); // done by AlgoElement
    }              
        
    final void compute() {	
    	// apply macro to input and set output
    	macro.applyMacro(input, output);
    }   
    
    final public String toString() {    	
        return getCommandDescription();
    }        
    
}
