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
    /**
     * Creates new labeled enlarge geo
     * @param cons
     * @param label
     * @param A
     * @param r
     * @param S
     */
    AlgoDilate(Construction cons, String label,
    		Dilateable A, NumberValue r, GeoPoint S) {
    	this(cons, A, r, S);
    	Bgeo.setLabel(label);    
    }
    
  
    /**
     * Creates new unlabeled enlarge geo
     * @param cons
     * @param A
     * @param r
     * @param S
     */
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

    public String getClassName() {
        return "AlgoDilate";
    }

    // for AlgoElement
    protected void setInputOutput() {    	
        input = new GeoElement[S==null ? 2:3];
        input[0] = Ageo;
        input[1] = rgeo;
        if(S != null)input[2] = S;

        setOutputLength(1);
        setOutput(0,Bgeo);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the resulting GeoElement
     * @return the resulting GeoElement
     */
    GeoElement getResult() {
        return Bgeo;
    }

    // calc rotated point
    protected final void compute() {
        Bgeo.set(Ageo);
        if(S==null){
        	//Application.debug(cons.getOrigin());
        	B.dilate(r, cons.getOrigin());
        }
        else
        	B.dilate(r, S);
    }
       
   

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	String sLabel = S == null ? cons.getOrigin().toValueString() : S.getLabel();
    	return app.getPlain("ADilatedByFactorBfromC",Ageo.getLabel(),rgeo.getLabel(),sLabel);

    }
}
