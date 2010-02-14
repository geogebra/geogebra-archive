/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
/**
 * Find asymptotes
 * 
 * @author Michael Borcherds
 */
public class AlgoAsymptoteFunction extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoList g; // output        
    
    private StringBuilder sb = new StringBuilder();
   
    public AlgoAsymptoteFunction(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        g = new GeoList(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    protected String getClassName() {
        return "AlgoAsymptoteFunction";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return g;
    }

    protected final void compute() {       
        if (!f.isDefined()) {
        	g.setUndefined();
        	return;
        }    
		
	    sb.setLength(0);
	    sb.append("{");
		f.getHorizontalPositiveAsymptote(f, sb);
		f.getHorizontalNegativeAsymptote(f, sb);
	    
		f.getDiagonalPositiveAsymptote(f, sb);
		f.getDiagonalNegativeAsymptote(f, sb);
		
    	f.getVerticalAsymptotes(f, sb, false);

	    sb.append("}");
		
	    //Application.debug(sb.toString());
		g.set(kernel.getAlgebraProcessor().evaluateToList(sb.toString()));	
		
		g.setLineType(EuclidianView.LINE_TYPE_DASHED_SHORT);
		
		g.setDefined(true);	
		
		kernel.evaluateMathPiper("Limit(x,Infinity)(if(x-1<0)1 else -1)");
		
    }
    
    
    final public String toString() {
    	return getCommandDescription();
    }
 

}
