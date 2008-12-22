/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

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
public class AlgoRotatePoint extends AlgoTransformation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint Q;
    private PointRotateable B;    
    private NumberValue angle; 
    private GeoElement Ageo, Bgeo, angleGeo;
    
    AlgoRotatePoint(Construction cons, String label,
            PointRotateable A, NumberValue angle, GeoPoint Q) {
    	this(cons, A, angle, Q);
    	Bgeo.setLabel(label);
    }
    
    AlgoRotatePoint(Construction cons, 
        PointRotateable A, NumberValue angle, GeoPoint Q) {
        super(cons);               
        this.angle = angle;
        this.Q = Q;

        Ageo = A.toGeoElement();
        angleGeo = angle.toGeoElement();
        
        // create output object
        Bgeo = Ageo.copy();       
        B = (PointRotateable) Bgeo;
        setInputOutput();

        kernel.registerEuclidianViewAlgo(this);
        
        compute();
      
    }

    protected String getClassName() {
        return "AlgoRotatePoint";
    }

    final public boolean wantsEuclidianViewUpdate() {
        return Ageo.isGeoImage();
    }
    
    // for AlgoElement
    protected void setInputOutput() {    	
        input = new GeoElement[3];
        input[0] = Ageo;
        input[1] = angleGeo;
        input[2] = Q;

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
        B.rotate(angle, Q);
    }
       
    final public String toString() {
        StringBuffer sb = new StringBuffer();

        // Michael Borcherds 2008-03-25
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("ARotatedByAngleB",Ageo.getLabel(),angleGeo.getLabel()));

        return sb.toString();
    }
}
