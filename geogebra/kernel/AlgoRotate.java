/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
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
public class AlgoRotate extends AlgoTransformation {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Rotateable B;    
    private NumberValue angle; 
    private GeoElement Ageo, Bgeo, angleGeo;
    
    AlgoRotate(Construction cons, String label,
            Rotateable A, NumberValue angle) {
    	this(cons, A, angle);
    	Bgeo.setLabel(label);
    }
    
    AlgoRotate(Construction cons, Rotateable A, NumberValue angle) {
        super(cons);        
        this.angle = angle;

        Ageo = A.toGeoElement();
        angleGeo = angle.toGeoElement();
        
        // create output object
        Bgeo = Ageo.copy();
        B = (Rotateable) Bgeo;
        setInputOutput();

        compute();        
    }

    String getClassName() {
        return "AlgoRotate";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = Ageo;
        input[1] = angle.toGeoElement();

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
        B.rotate(angle);
    }
    
    final public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(Ageo.getLabel());
        sb.append(' ');
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("rotatedByAngle"));
            sb.append(' ');
        }
        sb.append(angleGeo.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("rotatedByAngle"));
        }
        return sb.toString();
    }    
}
