/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

public class AlgoImageCorner extends AlgoElement 
implements EuclidianViewAlgo {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoImage img;  // input
    private GeoPoint corner;     // output    
    private NumberValue number;
    
    AlgoImageCorner(Construction cons, String label, GeoImage img, NumberValue number) {        
        super(cons);
        this.img = img;   
        this.number = number;
        
        corner = new GeoPoint(cons);                
        setInputOutput(); // for AlgoElement                
        compute();              
        corner.setLabel(label);   
        
        kernel.registerEuclidianViewAlgo(this);
    }   
    
    protected String getClassName() {
        return "AlgoImageCorner";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = img;        
        input[1] = number.toGeoElement();
        
        output = new GeoElement[1];
        output[0] = corner;        
        setDependencies(); // done by AlgoElement
    }       
         
    GeoPoint getCorner() { return corner; }        
    
    final void compute() {         	
		img.calculateCornerPoint(corner, (int) number.getDouble());	    	
    }
    
	public void euclidianViewUpdate() {
		compute();
	}
    
    final public String toString() {
        return getCommandDescription();
    }
	
}
