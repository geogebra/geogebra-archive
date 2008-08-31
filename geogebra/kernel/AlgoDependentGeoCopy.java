/* 
GeoGebra - Dynamic Mathematics for Everyone
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


/**
 * Creates a dependent copy of the given GeoElement.
 */
public class AlgoDependentGeoCopy extends AlgoElement {

	private static final long serialVersionUID = 1L;
    private GeoElement origGeo, copyGeo;     // input, ouput              
        
    public AlgoDependentGeoCopy(Construction cons, String label, GeoElement origGeo) {
    	super(cons);
        this.origGeo = origGeo;
        
        copyGeo = origGeo.copy();
        setInputOutput(); // for AlgoElement
        
        compute();      
        copyGeo.setLabel(label);
    }   
    
	protected String getClassName() {
		return "Expression";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = origGeo;
        
        output = new GeoElement[1];        
        output[0] = copyGeo;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoElement getGeo() { return copyGeo; }
    
    // copy geo
    protected final void compute() {	
    	try {
    		copyGeo.set(origGeo);
    	} catch (Exception e) {
    		copyGeo.setUndefined();
    	}
    }   
    
    final public String toString() {
        // was defined as e.g.  c = a & b
        StringBuffer sb = new StringBuffer();
        sb.append(copyGeo.getLabel());
        sb.append(" = ");
        sb.append(origGeo.getLabel());
        return sb.toString();
    }
}
