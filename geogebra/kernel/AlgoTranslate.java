/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoTranslatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;

import geogebra.Matrix.GgbVector;





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
        
        cons.registerEuclidianViewAlgo(this);
                
        compute();               
    }           
    
    protected String getClassName() {
        return "AlgoTranslate";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = inGeo;        
        input[1] = v;        
        
        output = new GeoElement[1];        
        output[0] = outGeo;        
        setDependencies(); // done by AlgoElement
    }           
        
    GeoElement getResult() { return outGeo; }
        
    // calc translated point
    protected final void compute() {      
        outGeo.set(inGeo);
        out.translate(new GgbVector(new double[] {v.x,v.y,v.z}));
    }       
    
    final public boolean wantsEuclidianViewUpdate() {
        return inGeo.isGeoImage();
    }

    
    final public String toString() {

        // Michael Borcherds 2008-03-24 simplified code!
        return app.getPlain("TranslationOfAbyB",inGeo.getLabel(),v.getLabel());
    }
}
