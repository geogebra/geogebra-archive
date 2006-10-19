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
public class AlgoRotatePoint extends AlgoElement {

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

        compute();
      
    }

    String getClassName() {
        return "AlgoRotatePoint";
    }

    // for AlgoElement
    void setInputOutput() {    	
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
    final void compute() {
        Bgeo.set(Ageo);
        B.rotate(angle, Q);
    }
       
    final public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(Ageo.getLabel());
        sb.append(' ');
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("rotatedByAngle"));
            sb.append(' ');
            sb.append(angleGeo.getLabel());
            sb.append(' ');
            sb.append(app.getPlain("around"));
            sb.append(' ');
            sb.append(Q.getLabel());
        } else {
            sb.append(app.getPlain("around"));
            sb.append(' ');
            sb.append(Q.getLabel());
            sb.append(' ');
            sb.append(angleGeo.getLabel());
            sb.append(' ');
            sb.append(app.getPlain("rotatedByAngle"));
        }
        return sb.toString();
    }
}
