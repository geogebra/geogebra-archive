/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


/**
 * Try to expand the given GeoFunction's expression
 * (e.g. function expression or dependent number's expression). 
 * 
 * @author Markus Hohenwarter
 */
public class AlgoExpand extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoFunction g; // output         
   
    public AlgoExpand(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
        
        g = (GeoFunction) f.copyInternal(cons);        
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }   
    
    public String getClassName() {
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
    	
    	g.setExpanded(f);
    }
       
    
    final public String toString() {    	    	
    	return getCommandDescription() + " : " + g.getFunction().toString();    	  	
    }

}
