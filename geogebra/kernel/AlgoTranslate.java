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
    private GeoVec3D v;  // input      
    
    /**
     * Creates labeled translation algo
     * @param cons
     * @param label
     * @param in
     * @param v
     */
    AlgoTranslate(Construction cons, String label, GeoElement in, GeoVec3D v) {
    	this(cons, in, v);
    	outGeo.setLabel(label);
    }
            
    /**
     * Creates unlabeled translation algo
     * @param cons
     * @param in
     * @param v
     */
    AlgoTranslate(Construction cons, GeoElement in, GeoVec3D v) {
        super(cons);        
        this.v = v;
        
        inGeo = in;
                
        // create out
        if(in.isGeoList()){
        	outGeo = new GeoList(cons);
        }else {
        outGeo = inGeo.copy();
        out = (Translateable) outGeo;
        }
        setInputOutput();
        
        cons.registerEuclidianViewAlgo(this);
                
        compute();               
    }           
    
    public String getClassName() {
        return "AlgoTranslate";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = inGeo;        
        input[1] = v;        
        
        setOutputLength(1);        
        setOutput(0,outGeo);        
        setDependencies(); // done by AlgoElement
    }           
        
    GeoElement getResult() { return outGeo; }
        
    // calc translated point
    protected final void compute() {
    	if(inGeo.isGeoList()){
    		return;
    	}
        outGeo.set(inGeo);
        out.translate(new GgbVector(new double[] {v.x,v.y,v.z}));
    }       

    
    final public String toString() {

        // Michael Borcherds 2008-03-24 simplified code!
        return app.getPlain("TranslationOfAbyB",inGeo.getLabel(),v.getLabel());
    }
}
