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
    private double gx, gy, hx, hy, wx, wy, lenH, lenG, length, ip;
    private GeoLine g, h;
    private GeoVector wv; // direction of bisector line bisector
    private boolean infiniteB;

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

    String getClassName() {
        return "AlgoAngularBisectorPoints";
    }

    // for AlgoElement
    void setInputOutput() {
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

    final void compute() {
        infiniteB = B.isInfinite();

        // compute lines g = B v A, h = B v C                
        GeoVec3D.cross(B, A, g);
        GeoVec3D.cross(B, C, h);

        // (gx, gy) is direction of g = B v A        
        gx = g.y;
        gy = -g.x;
        lenG = GeoVec2D.length(gx, gy);
        gx /= lenG;
        gy /= lenG;

        // (hx, hy) is direction of h = B v C
        hx = h.y;
        hy = -h.x;
        lenH = GeoVec2D.length(hx, hy);
        hx /= lenH;
        hy /= lenH;

        // set direction vector of bisector: (wx, wy)        
        if (infiniteB) {
            // if B is at infinity g and h are parallel
            // and the bisector line has same direction as g (and h)
            wx = gx;
            wy = gy;

            // calc z value of line in the middle of g, h 
            bisector.z = (g.z / lenG + h.z / lenH) / 2.0;

            // check orientation: take smallest change!!!
            if (wv.x * wx + wv.y * wy >= 0) {
                wv.x = wx;
                wv.y = wy;
            } else { // angle > 180�, change orientation
                wv.x = -wx;
                wv.y = -wy;
                bisector.z = -bisector.z;
            }
            // set direction vector
            bisector.x = -wv.y;
            bisector.y = wv.x;
        }
        // standard case: B is not at infinity            
        else {
            // calc direction vector (wx, wy) of angular bisector
            // check if angle between vectors is > 90�
            ip = gx * hx + gy * hy;
            if (ip >= 0.0) { // angle < 90�
                // standard case
                wx = gx + hx;
                wy = gy + hy;
            } else { // ip <= 0.0, angle > 90�            
                // BC - BA is a normalvector of the bisector                        
                wx = gy - hy;
                wy = hx - gx;
            }

            // make (wx, wy) a unit vector
            length = GeoVec2D.length(wx, wy);
            wx /= length;
            wy /= length;

            // check orientation: take smallest change!!!
            if (wv.x * wx + wv.y * wy >= 0) {
                wv.x = wx;
                wv.y = wy;
            } else { // angle > 180�, change orientation
                wv.x = -wx;
                wv.y = -wy;
            }

            // set bisector
            bisector.x = -wv.y;
            bisector.y = wv.x;
            bisector.z = - (B.inhomX * bisector.x + B.inhomY * bisector.y);
        }
        //System.out.println("bisector = (" + bisector.x + ", " + bisector.y + ", " + bisector.z + ")\n");
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();

        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("AngularBisectorOf"));
            sb.append(' ');
        }
        sb.append(A.getLabel());
        sb.append(", ");
        sb.append(B.getLabel());
        sb.append(", ");
        sb.append(C.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("AngularBisectorOf"));
        }

        return sb.toString();
    }
}
