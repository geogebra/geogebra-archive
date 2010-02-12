/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoRotatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDilate extends AlgoTransformation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint S;
    private Dilateable B;    
    private NumberValue r; 
    private GeoElement Ageo, Bgeo, rgeo;
    
    AlgoDilate(Construction cons, String label,
    		Dilateable A, NumberValue r, GeoPoint S) {
    	this(cons, A, r, S);
    	Bgeo.setLabel(label);    
    }
    
    AlgoDilate(Construction cons, 
    		Dilateable A, NumberValue r, GeoPoint S) {
        super(cons);        
        this.r = r;
        this.S = S;

        Ageo = A.toGeoElement();
        rgeo = r.toGeoElement();               
        
        // create output object
        Bgeo = Ageo.copy();
        B = (Dilateable) Bgeo;
        
        setInputOutput();
        
        cons.registerEuclidianViewAlgo(this);
        
        compute();
        
             
    }

    protected String getClassName() {
        return "AlgoDilate";
    }

    // for AlgoElement
    protected void setInputOutput() {    	
        input = new GeoElement[3];
        input[0] = Ageo;
        input[1] = rgeo;
        input[2] = S;

        output = new GeoElement[1];
        output[0] = Bgeo;
        setDependencies(); // done by AlgoElement
    }

    GeoElement getResult() {
        return Bgeo;
    }

    // calc rotated point
    protected final void compute() {
        Bgeo.set(Ageo);
        B.dilate(r, S);
    }
       
    final public boolean wantsEuclidianViewUpdate() {
        return Ageo.isGeoImage();
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("ADilatedByFactorBfromC",Ageo.getLabel(),rgeo.getLabel(),S.getLabel());

    }
}
