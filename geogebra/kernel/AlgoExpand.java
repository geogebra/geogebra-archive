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
/**
 * Try to expand the given function 
 * 
 * @author Michael Borcherds
 */
public class AlgoExpand extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoFunction g; // output         
   
    public AlgoExpand(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        g = new GeoFunction(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    protected String getClassName() {
        return "AlgoExpand";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getResult() {
        return g;
    }

    final void compute() {       
        if (!f.isDefined()) {
        	g.setUndefined();
        	return;
        }    
                
        /*
        // Yacas version
		String function = f.getFunction().
		getExpression().getCASstring(ExpressionNode.STRING_TYPE_YACAS, false);
		//System.out.println("Expand input:"+function);
		function = kernel.evaluateYACASRaw("ExpandBrackets("+function+")");
		*/
		
		// JASYMCA version
		String function = f.getFunction().
		getExpression().getCASstring(ExpressionNode.STRING_TYPE_JASYMCA, false);    	
		System.out.println("Expand input:"+function);
		function = kernel.evaluateJASYMCA("Expand("+function+")");
		
		
		System.out.println("Expand output:"+function);
		
		g = kernel.getAlgebraProcessor().evaluateToFunction(function);
		
		g.setDefined(true);	
		
    }
    
    final public String toString() {
    	return getCommandDescription();
    }

}
