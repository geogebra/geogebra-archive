/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoCirclePointRadius.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

/**
 *
 * @author  Markus
 * added TYPE_TWO_POINTS Michael Borcherds 2008-03-13	
 * @version 
 */
public class AlgoCirclePointRadius extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint M, B, C; // input
    private NumberValue r; // input
    private GeoElement rgeo;
    private GeoConic circle; // output    
    
    private int type;
    final static int TYPE_RADIUS=0;
    final static int TYPE_TWO_POINTS=1;
    final static int TYPE_SEGMENT=2;

    AlgoCirclePointRadius(
            Construction cons,
            String label,
            GeoPoint M,
            NumberValue r) {
        	
            this(cons, M, r);
            circle.setLabel(label);
        }
        
    AlgoCirclePointRadius(
            Construction cons,
            String label,
            GeoPoint M,
            GeoPoint B,
            GeoPoint C) {
        	
            this(cons, M, B, C);
            circle.setLabel(label);
        }
        
    AlgoCirclePointRadius(
            Construction cons,
            String label,
            GeoPoint M,
            GeoSegment segment, boolean dummy) {
        	
            this(cons, M, segment, dummy);
            circle.setLabel(label);
        }
        
    AlgoCirclePointRadius(
            Construction cons,
            GeoPoint M,
            NumberValue r) {
        	
            super(cons);
            
            type=TYPE_RADIUS;
            
            this.M = M;
            this.r = r;
            rgeo = r.toGeoElement();
            circle = new GeoConic(cons);
            
            setInputOutput(); // for AlgoElement

            compute();            
        }
    
    AlgoCirclePointRadius(
            Construction cons,
            GeoPoint M,
            GeoPoint B,
            GeoPoint C) {
        	
            super(cons);
            
            type=TYPE_TWO_POINTS;
  
            
            this.M = M;
            this.B = B;
            this.C = C;
            circle = new GeoConic(cons);
            
            setInputOutput(); // for AlgoElement

            compute();            
        }
    AlgoCirclePointRadius(
            Construction cons,
            GeoPoint M,
            GeoSegment rgeo, boolean dummy) {
        	
            super(cons);
            
            type=TYPE_SEGMENT;
  
            
            this.M = M;
            this.rgeo=rgeo;
            
            circle = new GeoConic(cons);
            
            setInputOutput(); // for AlgoElement

            compute();            
        }

    protected String getClassName() {
        return "AlgoCirclePointRadius";
    }

    // for AlgoElement
    void setInputOutput() {
        switch (type) {
        case TYPE_RADIUS:
            input = new GeoElement[2];
            input[0] = M;
            input[1] = rgeo;
        	break;
        case TYPE_TWO_POINTS:
            input = new GeoElement[3];
            input[0] = M;
            input[1] = B;
            input[2] = C;
        	break;
        case TYPE_SEGMENT:
            input = new GeoElement[2];
            input[0] = M;
            input[1] = rgeo;
        	break;
        }

        output = new GeoElement[1];
        output[0] = circle;
        setDependencies(); // done by AlgoElement
    }

    GeoConic getCircle() {
        return circle;
    }
    GeoPoint getM() {
        return M;
    }

    // compute circle with midpoint M and radius r
    final void compute() {
        switch (type) {
        case TYPE_RADIUS:
        	circle.setCircle(M, r.getDouble());
        	break;
        case TYPE_TWO_POINTS:
            circle.setCircle(M, B, C);
        	break;
        case TYPE_SEGMENT:
            circle.setCircle(M, (GeoSegment)rgeo);
        	break;
        }
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();

        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("Circle"));
            sb.append(' ');
        }
        sb.append(app.getPlain("with"));
        sb.append(' ');
        sb.append(app.getPlain("Center"));
        sb.append(' ');
        sb.append(M.getLabel());
        sb.append(' ');
        sb.append(app.getPlain("and"));
        sb.append(' ');
        sb.append(app.getPlain("Radius"));
        sb.append(' ');
        switch (type) {
        case TYPE_RADIUS:
        case TYPE_SEGMENT:
            sb.append(rgeo.getLabel());
        	break;
        case TYPE_TWO_POINTS:
            sb.append(B.getLabel());
            sb.append(C.getLabel());
        	break;
        }
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(app.getPlain("Circle"));
        }

        return sb.toString();
    }
}
