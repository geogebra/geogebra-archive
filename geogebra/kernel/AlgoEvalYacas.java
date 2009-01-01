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

public class AlgoEvalYacas extends AlgoElement {

	private static final long serialVersionUID = 1L;
    private GeoText text;     // input              
	//private GeoBoolean bool;  // output
	private GeoText textOut;  // output
        
    public AlgoEvalYacas(Construction cons, String label, GeoText text) {
    	super(cons);
        this.text = text;  
        
        //bool = new GeoBoolean(cons);
        textOut = new GeoText(cons);
       setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        //bool.setLabel(label);
        textOut.setLabel(label);
    }   
    
	protected String getClassName() {
		return "AlgoEvalYacas";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = text;
        
        output = new GeoElement[1];        
        //output[0] = bool;        
        output[0] = textOut;        
        setDependencies(); // done by AlgoElement
    }    
    
    //public GeoBoolean getResult() { return bool; }
    public GeoText getResult() { return textOut; }
    
    protected final void compute() {    
    	
    	textOut.setTextString(kernel.evaluateMathPiperRaw(text.getTextString()));
    }         
}
