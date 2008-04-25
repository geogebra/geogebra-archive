/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

// adapted from AlgoImageCorner by Michael Borcherds 2007-11-26

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

public class AlgoTextCorner extends AlgoElement 
implements EuclidianViewAlgo {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoText txt;  // input
    private GeoPoint corner;     // output    
    private NumberValue number;
    
    AlgoTextCorner(Construction cons, String label, GeoText txt, NumberValue number) {        
        super(cons);
        this.txt = txt;   
        this.number = number;
        
        // make sure bounding box of text is kept up to date
        // so we can use it in compute()
        txt.setNeedsUpdatedBoundingBox(true);
                
        corner = new GeoPoint(cons);                
        setInputOutput(); // for AlgoElement                
        compute();              
        corner.setLabel(label);                  
    }   
    
    protected String getClassName() {
        return "AlgoTextCorner";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = txt;        
        input[1] = number.toGeoElement();
        
        output = new GeoElement[1];
        output[0] = corner;        
        setDependencies(); // done by AlgoElement
    }       
         
    GeoPoint getCorner() { return corner; }        
    
    protected final void compute() {         	
		txt.calculateCornerPoint(corner, (int) number.getDouble());	    	
    }    	
    
    final public boolean wantsEuclidianViewUpdate() {
    	return true;
    }
    
    final public String toString() {
        return getCommandDescription();
    }
	
}
