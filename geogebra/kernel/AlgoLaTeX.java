/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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
import geogebra.main.Application;


/**
 * Returns the name of a GeoElement as a GeoText.
 * @author  Markus
 * @version 
 */
public class AlgoLaTeX extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoElement geo;  // input
	private GeoBoolean substituteVars; 
    private GeoText text;     // output              
        
    public AlgoLaTeX(Construction cons, String label, GeoElement geo, GeoBoolean substituteVars ) {
    	super(cons);
        this.geo = geo;  
        this.substituteVars = substituteVars;

       text = new GeoText(cons);
       setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        text.setLabel(label);
    }   
    
    public AlgoLaTeX(Construction cons, String label, GeoElement geo) {
    	super(cons);
        this.geo = geo;  
        this.substituteVars = null;

       text = new GeoText(cons);

		text.setIsTextCommand(true); // stop editing as text
		setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        text.setLabel(label);
        
        // make sure for new LaTeX texts we get nice "x"s
        text.setSerifFont(true);
    }   
    
	public String getClassName() {
		return "AlgoLaTeX";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
		
		if (substituteVars == null) {
			input = new GeoElement[1];
			input[0] = geo;				
		} else {
			input = new GeoElement[2];
			input[0] = geo;			
			input[1] = substituteVars;	
		}
        
        output = new GeoElement[1];        
        output[0] = text;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoText getGeoText() { return text; }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {    
    	
		if (!geo.isDefined() || (substituteVars != null && !substituteVars.isDefined())) {
    		text.setTextString("");
		} else {
    		boolean bool = substituteVars == null ? true : substituteVars.getBoolean();

    		text.setTemporaryPrintAccuracy();
    		
    		Application.debug(geo.getFormulaString(ExpressionNode.STRING_TYPE_LATEX, bool ));
    		
    		text.setTextString(geo.getFormulaString(ExpressionNode.STRING_TYPE_LATEX, bool ));    		

    		text.restorePrintAccuracy();
		}
		
    	
    	text.setLaTeX(true, false);
    	
    	/*
    	int tempCASPrintForm = kernel.getCASPrintForm();
    	kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_LATEX);
    	text.setTextString(geo.getCommandDescription());	    	
    	kernel.setCASPrintForm(tempCASPrintForm);*/
    }         
}
