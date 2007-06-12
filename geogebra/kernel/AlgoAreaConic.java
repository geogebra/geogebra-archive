/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package geogebra.kernel;




/**
 * Computes the area of a conic section
 * @author  Markus Hohenwarter
 * @version 
 */
public class AlgoAreaConic extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoConic  c;  // input
    private GeoNumeric area;     // output           
        
    AlgoAreaConic(Construction cons, String label, GeoConic c) {       
	  super(cons); 
      this.c = c;
      area = new GeoNumeric(cons); 
      setInputOutput(); // for AlgoElement
      
      // compute angle
      compute();     
          
      area.setLabel(label);
    }   
  
    String getClassName() {
        return "AlgoAreaConic";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input =  new GeoElement[1];
        input[0] = c;
        
        output = new GeoElement[1];        
        output[0] = area;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoNumeric getArea() { return area; }        
    
    // calc area of conic c 
    final void compute() {  
    	if (c.isGeoConicPart() || !c.isDefined())
    		area.setUndefined();
    	
    	int type = c.getType();		
		switch (type) {
			case GeoConic.CONIC_CIRCLE:
				// r is length of one of the half axes
				double r = c.halfAxes[0];
				area.setValue(r * r * Math.PI);
				break;
				
			case GeoConic.CONIC_ELLIPSE:
				// lengths of the half axes
				double a = c.halfAxes[0];
				double b = c.halfAxes[1];
				area.setValue(a * b * Math.PI);
				break;
					
			default:			
				area.setUndefined();			
		}    	
    }   
    
    
}
