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
 * @author  Mathieu
 * 
 * @version 
 * 
 * Generalization of algo for circle/sphere
 */
public abstract class AlgoSphereNDPointRadius extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint M; // input
    private NumberValue r; // input
    private GeoElement rgeo;
    private GeoQuadricND sphereND; // output    
    
    private int type;
    final static int TYPE_RADIUS  = 0;
    final static int TYPE_SEGMENT = 1;

    AlgoSphereNDPointRadius(
            Construction cons,
            String label,
            GeoPoint M,
            NumberValue r) {
        	
            this(cons, M, r);
            sphereND.setLabel(label);
        }
        
    AlgoSphereNDPointRadius(
            Construction cons,
            String label,
            GeoPoint M,
            GeoSegment segment, boolean dummy) {
        	
            this(cons, M, segment, dummy);
            sphereND.setLabel(label);
        }
        
    public AlgoSphereNDPointRadius(
            Construction cons,
            GeoPoint M,
            NumberValue r) {
        	
            super(cons);
            
            type=TYPE_RADIUS;
            
            this.M = M;
            this.r = r;
            rgeo = r.toGeoElement();
            sphereND = createSphereND(cons);
            
            setInputOutput(); // for AlgoElement

            compute();            
        }
    
    AlgoSphereNDPointRadius(
            Construction cons,
            GeoPoint M,
            GeoSegment rgeo, boolean dummy) {
        	
            super(cons);
            
            type=TYPE_SEGMENT;  
            
            this.M = M;
            this.rgeo=rgeo;
            
            sphereND = createSphereND(cons);
            
            setInputOutput(); // for AlgoElement

            compute();            
        }
    
    
    /** return a conic (2D) or a quadric (3D)
     * @param cons
     * @return a conic (2D) or a quadric (3D)
     */
    abstract protected GeoQuadricND createSphereND(Construction cons);
    


    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = M;
        input[1] = rgeo;
        output = new GeoElement[1];
        output[0] = sphereND;
        setDependencies(); // done by AlgoElement
    }

    public GeoQuadricND getSphereND() {
        return sphereND;
    }
    
    
    protected GeoPoint getM() {
        return M;
    }
    
    protected GeoElement getRGeo() {
        return rgeo;
    }
    
    // compute circle with midpoint M and radius r
    protected final void compute() {
        switch (type) {
        case TYPE_RADIUS:
        	sphereND.setNSphere(M, r.getDouble());
        	break;
        case TYPE_SEGMENT:
        	sphereND.setNSphere(M, (GeoSegment)rgeo);
        	break;
        }
    }
    

}
