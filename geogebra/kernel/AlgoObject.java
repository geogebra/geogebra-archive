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
 * Returns the GeoElement from an object's label.
 * @author  Michael, Markus
 * @version 
 */
public class AlgoObject extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoElement geo;  // output
    private GeoText text;     // input            
    
    private String currentLabel;
    private GeoElement [] updateInput;
        
    public AlgoObject(Construction cons, String label, GeoText text) {
    	super(cons);
        this.text = text;  
             
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        geo.setLabel(label);
    }   
    
	protected String getClassName() {
		return "AlgoObject";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
	   // copy referenced object to get output object
       currentLabel = text.getTextString();
       GeoElement refObject = kernel.lookupLabel(currentLabel);  
       
       if (refObject != null ) {
    	   geo = refObject.copyInternal(cons);
    	   geo.setVisualStyle(refObject);
       } else
    	   geo = new GeoNumeric(cons,Double.NaN);
		
		
		// input for saving is only the name of the object
		input = new GeoElement[1];
		input[0] = text;
        
		// input for updating is both the name of the object and the object itself
		updateInput = new GeoElement[2];
		updateInput[0] = text;				
		updateInput[1] = refObject != null ? refObject : geo; // if null, pass new GeoNumeric(cons,Double.NaN);
        
        output = new GeoElement[1];        
        output[0] = geo;        

        // handle dependencies
        setEfficientDependencies(input, updateInput); 
    }    
    
    public GeoElement getResult() { return geo; }
    
    protected final void compute() {     	    
    	// did name of object change?
    	if (currentLabel != text.getTextString()) {
    		// get new object 
    		currentLabel = text.getTextString();
    		updateInput[1] = kernel.lookupLabel(currentLabel);
 
    		// add this new object to update set of this algorithm
    		updateInput[1].addToUpdateSetOnly(this);  
    	}
    	
    	
    	// check if updateInput has same 
    	if (updateInput[1] != null && 
    		updateInput[1].getGeoClassType() == geo.getGeoClassType())
    	{
    		geo.set(updateInput[1]);
    	} else {
    		geo.setUndefined();   
    	}    	    
    }         
}
