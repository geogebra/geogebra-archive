/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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
 * added TYPE_SEGMENT Michael Borcherds 2008-03-14	
 * @version 
 */
public class AlgoCirclePointRadius extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint M; // input
    private NumberValue r; // input
    private GeoElement rgeo;
    private GeoConic circle; // output    
    
    private int type;
    final static int TYPE_RADIUS  = 0;
    final static int TYPE_SEGMENT = 1;

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
            GeoSegment segment, boolean dummy) {
        	
            this(cons, M, segment, dummy);
            circle.setLabel(label);
        }
        
    public AlgoCirclePointRadius(
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
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = M;
        input[1] = rgeo;
        output = new GeoElement[1];
        output[0] = circle;
        setDependencies(); // done by AlgoElement
    }

    public GeoConic getCircle() {
        return circle;
    }
    GeoPoint getM() {
        return M;
    }

    // compute circle with midpoint M and radius r
    protected final void compute() {
        switch (type) {
        case TYPE_RADIUS:
        	circle.setCircle(M, r.getDouble());
        	break;
        case TYPE_SEGMENT:
            circle.setCircle(M, (GeoSegment)rgeo);
        	break;
        }
    }

    final public String toString() {
        StringBuilder sb = new StringBuilder();

        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("CircleWithCenterAandRadiusB",M.getLabel(),rgeo.getLabel()));
        
        return sb.toString();
    }
}
