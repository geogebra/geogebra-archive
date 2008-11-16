/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
/**
 * Try to expand the given GeoFunction's expression
 * (e.g. function expression or dependent number's expression). 
 * 
 * @author Michael Borcherds, Markus Hohenwarter
 */
public class AlgoExpand extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoFunction g; // output         
   
    public AlgoExpand(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        // create output
        if (f.isIndependent()) {
        	// independent object: expand will be done in compute()
        	g = (GeoFunction) f.copyInternal(cons);
        } else {
        	// dependen object: expand is only done once: now
        	g = expand(f);
        }
        
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }   
    
    protected String getClassName() {
        return "AlgoExpand";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoElement getResult() {
        return g;
    }

    protected final void compute() {     	  

    	if (!f.isDefined()) {
    		g.setUndefined();
    		return;
    	}
    	

    	g.set(expand(f));
    }
    
    /**
     * Expands the expression of geo and returns a resulting new geo.
     */
    private GeoFunction expand(GeoFunction geo) {
    	GeoFunction result = null;
    	
    	// JASYMCA version  
    	 try {     		
    		// get geo definition String
    		 
    	    String geoDef = geo.getFormulaString(ExpressionNode.STRING_TYPE_JASYMCA, false);
/*
    		 
         	int oldCASPrintForm = kernel.getCASPrintForm();
         	kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_JASYMCA);         	
 			
         	// get current definition of geo
         	String geoDef = geo.isIndependent() ? 
 						geo.toValueString() :
 						geo.getFunction().toString();
 			kernel.setCASPrintForm(oldCASPrintForm);   */
 			 		
 		//	Application.debug("expand: " + geoDef);
 			
 			// expand definition
 			String geoDefExpanded = kernel.evaluateJASYMCA("Expand("+geoDef+")");
 			 	         		
 			//Application.debug("expanded: " + geoDefExpanded);
 			
 	        /*
 	        // Yacas version
 			function = kernel.evaluateYACASRaw("ExpandBrackets("+function+")");
 			*/ 
 			
 			// create resulting geo from expanded definition
 			boolean oldSuppressLabels = cons.isSuppressLabelsActive();
 			cons.setSuppressLabelCreation(true);
 			result = (GeoFunction) kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(geoDefExpanded, false)[0]; 			 			 			  					
 			cons.setSuppressLabelCreation(oldSuppressLabels);
    	 } 
    	 catch (Error e) {
          	Application.debug("expand error: " + e.getMessage());          	
          }
         catch (Exception e) {
         	Application.debug("expand error: " + e.getMessage());         	
         }
 		
         // return resulting geo
         if (result == null) {
        	 return (GeoFunction) geo.copyInternal(geo.cons);
         }
         else 
        	 return result;
    }
    
    final public String toString() {    	    	
    	return getCommandDescription() + " : " + g.getFunction().toString();    	  	
    }

}
