/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * AlgoRadius.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoRadius extends AlgoElement {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c;  // input
    private GeoNumeric num;     // output                  
    
    AlgoRadius(Construction cons, String label,GeoConic c) {        
        super(cons);
        this.c = c;                                                              
        num = new GeoNumeric(cons);                
        setInputOutput(); // for AlgoElement                
        compute();              
        num.setLabel(label);            
    }   
    
    String getClassName() {
        return "AlgoRadius";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;        
        
        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }       
    
    GeoNumeric getRadius() { return num; }    
    GeoConic getConic() { return c; }        
    
    // set parameter of parabola
    final void compute() {        
        if (c.type == GeoConic.CONIC_CIRCLE)
            num.setValue(c.halfAxes[0]);
        else 
            num.setUndefined();
    }
    
    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if(!app.isReverseLanguage()){//FKH 20040906
                sb.append(app.getPlain("RadiusOf"));
                sb.append(' ');
         }
         sb.append(c.getLabel());
         if(app.isReverseLanguage()){//FKH 20040906
             sb.append(' ');
             sb.append(app.getPlain("RadiusOf"));
        }
        return sb.toString();
    }
}
