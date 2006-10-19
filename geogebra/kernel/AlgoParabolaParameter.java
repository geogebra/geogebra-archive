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
 * AlgoParabolaParameter.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoParabolaParameter extends AlgoElement {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c;  // input
    private GeoNumeric num;     // output                  
    
    AlgoParabolaParameter(Construction cons, String label, GeoConic c) {        
        super(cons);
        this.c = c;                                                              
        num = new GeoNumeric(cons);                
        setInputOutput(); // for AlgoElement                
        compute();              
        num.setLabel(label);            
    }   
    
    String getClassName() {
        return "AlgoParabolaParameter";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;        
        
        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }    
    
    GeoNumeric getParameter() { return num; }    
    GeoConic getConic() { return c; }        
    
    // set parameter of parabola
    final void compute() {        
        if (c.type == GeoConic.CONIC_PARABOLA)
            num.setValue(c.p);
        else 
            num.setUndefined();
    }
    
    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if(!app.isReverseLanguage()){//FKH 20040906
             sb.append(app.getPlain("ParameterOf"));
             sb.append(' ');
        }
        sb.append(c.getLabel());
        if(app.isReverseLanguage()){//FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("ParameterOf"));
         }
        return sb.toString();
    }
}
