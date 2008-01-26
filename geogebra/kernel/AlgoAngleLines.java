/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAngleLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAngleLines extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoLine g, h; // input
    private GeoAngle angle; // output           

    AlgoAngleLines(Construction cons, String label, GeoLine g, GeoLine h) {
        super(cons);
        this.g = g;
        this.h = h;
        angle = new GeoAngle(cons);
        setInputOutput(); // for AlgoElement

        // compute angle
        compute();
        angle.setLabel(label);
    }

    String getClassName() {
        return "AlgoAngleLines";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = g;
        input[1] = h;

        output = new GeoElement[1];
        output[0] = angle;
        setDependencies(); // done by AlgoElement
    }

    GeoAngle getAngle() {
        return angle;
    }
    public GeoLine getg() {
        return g;
    }
    public GeoLine geth() {
        return h;
    }

    // calc angle between lines g and h
    // use normalvectors (gx, gy), (hx, hy)
    final void compute() {
     	// |v| * |w| * sin(alpha) = det(v, w)
    	// cos(alpha) = v . w / (|v| * |w|)
    	// tan(alpha) = sin(alpha) / cos(alpha)
    	// => tan(alpha) = det(v, w) / v . w    	    	
    	double det = g.x * h.y - g.y * h.x;
    	double prod = g.x * h.x + g.y * h.y;    	    
    	double value = Math.atan2(det, prod);                  	    	
        
        angle.setValue(value);
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();

        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(g.getLabel());
            sb.append(", ");
            sb.append(h.getLabel());
            sb.append(' ');
            sb.append(app.getPlain("AngleBetween"));
        } else {
            sb.append(app.getPlain("AngleBetween"));
            sb.append(' ');
            sb.append(g.getLabel());
            sb.append(", ");
            sb.append(h.getLabel());
        }

        return sb.toString();
    }
}
