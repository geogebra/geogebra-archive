/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoCircleThreePoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoCircleThreePoints extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint A, B, C; // input    
    private GeoConic circle; // output     

    // line bisectors
    private GeoLine s0, s1;
    private GeoPoint center;    
    private double[] det = new double[3];
    transient private double ax,
        ay,
        bx,
        by,
        cx,
        cy,
        ABx,
        ABy,
        ACx,
        ACy,
        BCx,
        BCy,
        maxDet;
    transient private int casenr;

    AlgoCircleThreePoints(
        Construction cons,
        String label,
        GeoPoint A,
        GeoPoint B,
        GeoPoint C) {
        this(cons, A, B, C);
        circle.setLabel(label);
    }
    
    public AlgoCircleThreePoints(
            Construction cons,           
            GeoPoint A,
            GeoPoint B,
            GeoPoint C) {
    	
            super(cons);
            this.A = A;
            this.B = B;
            this.C = C;
            circle = new GeoConic(cons);
            circle.addPointOnConic(A);
            circle.addPointOnConic(B);
            circle.addPointOnConic(C);
            
            // temp: line bisectors
            s0 = new GeoLine(cons);
            s1 = new GeoLine(cons);

            center = new GeoPoint(cons);            

            setInputOutput(); // for AlgoElement

            compute();            
        }

    protected String getClassName() {
        return "AlgoCircleThreePoints";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = A;
        input[1] = B;
        input[2] = C;

        output = new GeoElement[1];
        output[0] = circle;
        setDependencies(); // done by AlgoElement
    }

    public GeoConic getCircle() {
        return circle;
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

    // compute circle through A, B, C
    protected final void compute() {
        // A, B or C undefined
        if (!A.isFinite() || !B.isFinite() || !C.isFinite()) {
            circle.setUndefined();
            return;
        }

        // get inhomogenous coords of points
        ax = A.inhomX;
        ay = A.inhomY;
        bx = B.inhomX;
        by = B.inhomY;
        cx = C.inhomX;
        cy = C.inhomY;

        // A = B = C
        if (kernel.isEqual(ax, bx)
            && kernel.isEqual(ax, cx)
            && kernel.isEqual(ay, by)
            && kernel.isEqual(ay, cy)) {
            circle.setCircle(A, 0.0); // single point
            return;
        }

        // calc vectors AB, AC, BC
        ABx = bx - ax;
        ABy = by - ay;
        ACx = cx - ax;
        ACy = cy - ay;
        BCx = cx - bx;
        BCy = cy - by;

        double lengthAB = GeoVec2D.length(ABx, ABy);
        double lengthAC = GeoVec2D.length(ACx, ACy);
        double lengthBC = GeoVec2D.length(BCx, BCy);

        // find the two bisectors with max intersection angle
        // i.e. maximum abs of determinant of directions            
        // max( abs(det(AB, AC)), abs(det(AC, BC)), abs(det(AB, BC)) )
        det[0] = Math.abs(ABx * ACy - ABy * ACx) / (lengthAB * lengthAC);
        // AB, AC
        det[1] = Math.abs(ACx * BCy - ACy * BCx) / (lengthAC * lengthBC);
        // AC, BC
        det[2] = Math.abs(ABx * BCy - ABy * BCx) / (lengthAB * lengthBC);
        // AB, BC

        // take ip[0] as init minimum and find minimum case
        maxDet = det[0];
        casenr = 0;
        if (det[1] > maxDet) {
            casenr = 1;
            maxDet = det[1];
        }
        if (det[2] > maxDet) {
            casenr = 2;
            maxDet = det[2];
        }

        // A, B, C are collinear: set M to infinite point
        // in perpendicular direction of AB
        if (kernel.isZero(maxDet)) {
            center.setCoords(-ABy, ABx, 0.0d);
            circle.setCircle(center, A);
        }
        // standard case
        else {
            // intersect two line bisectors according to casenr
            switch (casenr) {
                case 0 : // bisectors of AB, AC                        
                    s0.x = ABx;
                    s0.y = ABy;
                    s0.z = - ((ax + bx) * s0.x + (ay + by) * s0.y) / 2.0;

                    s1.x = ACx;
                    s1.y = ACy;
                    s1.z = - ((ax + cx) * s1.x + (ay + cy) * s1.y) / 2.0;
                    break;

                case 1 : // bisectors of AC, BC                    
                    s1.x = ACx;
                    s1.y = ACy;
                    s1.z = - ((ax + cx) * s1.x + (ay + cy) * s1.y) / 2.0;

                    s0.x = BCx;
                    s0.y = BCy;
                    s0.z = - ((bx + cx) * s0.x + (by + cy) * s0.y) / 2.0;
                    break;

                case 2 : // bisectors of AB, BC                    
                    s0.x = ABx;
                    s0.y = ABy;
                    s0.z = - ((ax + bx) * s0.x + (ay + by) * s0.y) / 2.0;

                    s1.x = BCx;
                    s1.y = BCy;
                    s1.z = - ((bx + cx) * s1.x + (by + cy) * s1.y) / 2.0;
                    break;
            }

            // intersect line bisectors to get midpoint
            GeoVec3D.cross(s0, s1, center);
            circle.setCircle(center, center.distance(A));
        }
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();

        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("CircleThroughABC",A.getLabel(),B.getLabel(),C.getLabel()));

        return sb.toString();
    }
}
