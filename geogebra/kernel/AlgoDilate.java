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
        compute();
        
             
    }

    String getClassName() {
        return "AlgoDilate";
    }

    // for AlgoElement
    void setInputOutput() {    	
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
    final void compute() {
        Bgeo.set(Ageo);
        B.dilate(r, S);
    }
       
    final public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(Ageo.getLabel());
        sb.append(' ');
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("dilatedByFactor"));
            sb.append(' ');
            sb.append(rgeo.getLabel());
            sb.append(' ');
            sb.append(app.getPlain("from"));
            sb.append(' ');
            sb.append(S.getLabel());
        } else {
            sb.append(app.getPlain("from"));
            sb.append(' ');
            sb.append(S.getLabel());
            sb.append(' ');
            sb.append(rgeo.getLabel());
            sb.append(' ');
            sb.append(app.getPlain("dilatedByFactor"));
        }
        return sb.toString();
    }
}
