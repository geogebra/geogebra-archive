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
 * AlgoTranslatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoTranslate extends AlgoTransformation {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Translateable out;   
    private GeoElement inGeo, outGeo;
    private GeoVector v;  // input      
    
    AlgoTranslate(Construction cons, String label, Translateable in, GeoVector v) {
    	this(cons, in, v);
    	outGeo.setLabel(label);
    }
            
    AlgoTranslate(Construction cons, Translateable in, GeoVector v) {
        super(cons);        
        this.v = v;
        
        inGeo = in.toGeoElement();
                
        // create out
        outGeo = inGeo.copy();
        out = (Translateable) outGeo;
        setInputOutput();
                
        compute();               
    }           
    
    String getClassName() {
        return "AlgoTranslate";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = inGeo;        
        input[1] = v;        
        
        output = new GeoElement[1];        
        output[0] = outGeo;        
        setDependencies(); // done by AlgoElement
    }           
        
    GeoElement getResult() { return outGeo; }
        
    // calc translated point
    final void compute() {      
        outGeo.set(inGeo);
        out.translate(v);
    }       
    
    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if(!app.isReverseLanguage()){//FKH 20040906
        sb.append(app.getPlain("TranslationOf"));
        sb.append(" ");
        sb.append(inGeo.getLabel());
        sb.append(' ');
        }
        sb.append(app.getPlain("by"));
        sb.append(' ');
        sb.append(v.getLabel());
        if(app.isReverseLanguage()){//FKH 20040906
        sb.append(' ');
        sb.append(inGeo.getLabel());
        sb.append(' ');
        sb.append(app.getPlain("TranslationOf"));
        }
        return sb.toString();
    }
}
