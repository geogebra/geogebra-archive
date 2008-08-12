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

public class AlgoEval extends AlgoElement {

	private static final long serialVersionUID = 1L;
    private GeoText text;     // input              
	//private GeoBoolean bool;  // output
	private GeoList list;  // output
        
    public AlgoEval(Construction cons, String label, GeoText text) {
    	super(cons);
        this.text = text;  
        
        //bool = new GeoBoolean(cons);
        list = new GeoList(cons);
       setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        //bool.setLabel(label);
        list.setLabel(label);
    }   
    
	protected String getClassName() {
		return "AlgoEval";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = text;
        
        output = new GeoElement[1];        
        //output[0] = bool;        
        output[0] = list;        
        setDependencies(); // done by AlgoElement
    }    
    
    //public GeoBoolean getResult() { return bool; }
    public GeoList getResult() { return list; }
    
    protected final void compute() {    
		GeoElement [] result = kernel.getAlgebraProcessor().
		processAlgebraCommand(text.getTextString(), false);
		
		list.clear();
		if (result.length > 0)
			for (int i=0 ; i < result.length ; i++)
				list.add(result[i]);
    }         
}
