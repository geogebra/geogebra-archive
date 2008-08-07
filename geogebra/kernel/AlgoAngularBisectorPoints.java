/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAngularBisector.java
 *
 * Created on 26. Oktober 2001
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAngularBisectorPoints extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint A, B, C; // input    
    private GeoLine bisector; // output   

    // temp    
    private GeoLine g, h;
    private GeoVector wv; // direction of line bisector  

    /** Creates new AlgoLineBisector */
    AlgoAngularBisectorPoints(
        Construction cons,
        String label,
        GeoPoint A,
        GeoPoint B,
        GeoPoint C) {
        super(cons);
        this.A = A;
        this.B = B;
        this.C = C;
        bisector = new GeoLine(cons);
        bisector.setStartPoint(B);
        setInputOutput(); // for AlgoElement

        g = new GeoLine(cons);
        h = new GeoLine(cons);
        wv = new GeoVector(cons);
        wv.setCoords(0, 0, 0);

        // compute bisector of angle(A, B, C)
        compute();
        bisector.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoAngularBisectorPoints";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = A;
        input[1] = B;
        input[2] = C;

        output = new GeoElement[1];
        output[0] = bisector;
        setDependencies(); // done by AlgoElement
    }

    GeoLine getLine() {
        return bisector;
    }
    GeoPoint getA() {
        return A;
    }
    GeoPoint getB() {
        return B;
    }
    GeoPoint getC() {
        return C;
    }

    protected final void compute() {
        boolean infiniteB = B.isInfinite();

        // compute lines g = B v A, h = B v C                       
        GeoVec3D.cross(B, A, g);
        GeoVec3D.cross(B, C, h);        

        // (gx, gy) is direction of g = B v A        
        double gx = g.y;
        double gy = -g.x;
        double lenG = GeoVec2D.length(gx, gy);
        gx /= lenG;
        gy /= lenG;

        // (hx, hy) is direction of h = B v C
        double hx = h.y;
        double hy = -h.x;
        double lenH = GeoVec2D.length(hx, hy);
        hx /= lenH;
        hy /= lenH;

        // set direction vector of bisector: (wx, wy)       
        double wx, wy;
        if (infiniteB) {
            // if B is at infinity then g and h are parallel
            // and the bisector line has same direction as g (and h)
            wx = gx;
            wy = gy;

            // calc z value of line in the middle of g, h 
            bisector.z = (g.z / lenG + h.z / lenH) / 2.0;
            
            // CONTINUITY handling
            if (kernel.isContinuous()) {
            	// init old direction vector
            	if (bisector.isDefined()) {
            		wv.x =  bisector.y;
            		wv.y = -bisector.x;
            	}        	
	            
                // check orientation: take smallest change!!!
	            if (wv.x * wx + wv.y * wy >= 0) {
	                wv.x = wx;
	                wv.y = wy;
	            } else { // angle > 180�, change orientation
	                wv.x = -wx;
	                wv.y = -wy;
	                bisector.z = -bisector.z;
	            }
            } else {
            	 wv.x = wx;
	             wv.y = wy;
            }
            
            // set direction vector
            bisector.x = -wv.y;
            bisector.y = wv.x;
        }
        // standard case: B is not at infinity            
        else {
            // calc direction vector (wx, wy) of angular bisector
            // check if angle between vectors is > 90 degrees
            double ip = gx * hx + gy * hy;
            if (ip >= 0.0) { // angle < 90 degrees
                // standard case
                wx = gx + hx;
                wy = gy + hy;              
            } 
            else { // ip <= 0.0, angle > 90 degrees            
                // BC - BA is a normalvector of the bisector                        
                wx = hy - gy;
                wy = gx - hx;
                
                // if angle > 180 degree change orientation of direction
                // det(g,h) < 0
                if (gx * hy < gy * hx) {
                	wx = -wx;
                	wy = -wy;
                }                            
            }

            // make (wx, wy) a unit vector
            double length = GeoVec2D.length(wx, wy);
            wx /= length;
            wy /= length;
            
            // CONTINUITY handling
            if (kernel.isContinuous()) {	
            	// init old direction vector
            	if (bisector.isDefined()) {
            		wv.x =  bisector.y;
            		wv.y = -bisector.x;
            	}
            	
            	// check orientation: take smallest change compared to old direction vector wv !!!
                if (wv.x * wx + wv.y * wy >= 0) {
                    wv.x = wx;
                    wv.y = wy;
                } else { // angle > 180 degrees, change orientation
                    wv.x = -wx;
                    wv.y = -wy;
                }                
            } else {
            	 wv.x = wx;
                 wv.y = wy;
            }            

            // set bisector
            bisector.x = -wv.y;
            bisector.y =  wv.x;
            bisector.z = - (B.inhomX * bisector.x + B.inhomY * bisector.y);
        }
        //Application.debug("bisector = (" + bisector.x + ", " + bisector.y + ", " + bisector.z + ")\n");
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();

        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("AngleBisectorOfABC",A.getLabel(),B.getLabel(),C.getLabel()));

        return sb.toString();
    }
}
