/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAngleConic.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;



/**
 * Converts a number to an angle.
 */
public class AlgoAngleNumeric extends AlgoElement {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoNumeric num;  // input
    private GeoAngle angle;     // output                  
    
    AlgoAngleNumeric(Construction cons, String label, GeoNumeric num) { 
        super(cons);       
        this.num = num;                                                              
        angle = new GeoAngle(cons);                
        setInputOutput(); // for AlgoElement                
        compute();              
        angle.setLabel(label);            
    }   
    
    String getClassName() {
        return "AlgoAngleNumeric";
    }
        
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = num;        
        
        output = new GeoElement[1];
        output[0] = angle;
        setDependencies(); // done by AlgoElement
    }    
    
    GeoAngle getAngle() { return angle; }    
    GeoNumeric getNumber() { return num; }        
    
    // compute conic's angle
    final void compute() {                
        // copy number to angle
        angle.setValue(num.value);       
    }
    
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(app.getCommand("Angle"));        
        sb.append('[');
        sb.append(num.getLabel());  
        sb.append(']');        
        
        return sb.toString();
    }
}
