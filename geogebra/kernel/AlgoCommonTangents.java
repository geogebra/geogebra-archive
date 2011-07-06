/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoCommonTangents.java, dsun48 [6/26/2011]
 *
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;

/**
 * Common tangents of two circles
 */
public class AlgoCommonTangents extends AlgoElement {

    private static final long serialVersionUID = 1L;

    private GeoConic c1; // input
    private GeoConic c2; // input
    private GeoLine[] tangents; // output  

    private GeoPoint P; // temp

    private GeoLine polar;
    private AlgoIntersectLineConic algoIntersect;
    private GeoPoint[] tangentPoints;
    private boolean equalLines = false;

    /*  
        AlgoCommonTangents(Construction cons, String label, GeoPoint P, GeoConic c) {
        this(cons, P, c);
        GeoElement.setLabels(label, tangents);            
        }*/

    AlgoCommonTangents(
                       Construction cons,
                       String[] labels,
                       GeoConic c1,
                       GeoConic c2) {
        this(cons, c1, c2);
        GeoElement.setLabels(labels, tangents);
    }

    AlgoCommonTangents(Construction cons, GeoConic c1, GeoConic c2) {
        super(cons);
        this.c1 = c1;
        this.c2 = c2;

        P = new GeoPoint(cons);
        c1 = new GeoConic(cons);

        tangents = new GeoLine[4];
        tangents[0] = new GeoLine(cons);
        tangents[1] = new GeoLine(cons);
        tangents[2] = new GeoLine(cons);
        tangents[3] = new GeoLine(cons);
        polar = new GeoLine(cons);

        setInputOutput(); // for AlgoElement

        compute();

        // check if both lines are equal after creation:
        // if they are equal we started with a point on the conic section
        // in this case we only want to see one tangent line,
        // so we make the second one undefined
        equalLines = tangents[0].isEqual(tangents[1]);
        if (equalLines) {        
            tangents[1].setUndefined();
            tangentPoints[1].setUndefined();
        }
        if( tangents[2].isEqual(tangents[3]) ) {
        	tangents[3].setUndefined();
        }
    }

    public String getClassName() {
        return "AlgoCommonTangents";
    }

    public int getRelatedModeID() {
        return EuclidianConstants.MODE_TANGENTS;
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = c1;
        input[1] = c2;
        output = tangents;
        setDependencies(); // done by AlgoElement
    }

    GeoLine[] getTangents() {
        return tangents;
    }

    // GeoPoint getPoint() {
    //     return P;
    // }
    // GeoConic getConic() { 
    //     return c2; // ???
    // }       

    // /**
    //  * return intersection point of tangent line and conic c.
    //  * return null if line is not defined as tangent of conic c.
    //  */
    // GeoPoint getTangentPoint(GeoConic conic, GeoLine line) {
    //     if (conic != c2) // ???
    //         return null;
    // 
    //     if (line == tangents[0])
    //         return tangentPoints[0];
    //     else if (line == tangents[1])
    //         return tangentPoints[1];
    //     else
    //         return null;
    // }
    
    // /**
    //  * Inits the helping interesection algorithm to take
    //  * the current position of the lines into account.
    //  * This is important so the the tangent lines are not
    //  * switched after loading a file
    //  */
    // public void initForNearToRelationship() {
    //     // if first tangent point is not on first tangent,
    //     // we switch the intersection points
    //     if (!tangents[0].isOnFullLine(tangentPoints[0], Kernel.MIN_PRECISION)) {
    //         algoIntersect.initForNearToRelationship();
    //         
    //         // remember first point
    //         double px = tangentPoints[0].x;
    //         double py = tangentPoints[0].y;
    //         double pz = tangentPoints[0].z;
    //         
    //         // first = second
    //         algoIntersect.setIntersectionPoint(0, tangentPoints[1]);
    //         
    //         // second = first
    //         tangentPoints[1].setCoords(px, py, pz);
    //         algoIntersect.setIntersectionPoint(1, tangentPoints[1]);
    //     }                    
    // }

    // calc tangents
    protected final void compute() {

        if( !c1.isCircle() || !c2.isCircle() ) {
            for(int i=0; i<4; i++) {
                tangents[i].setUndefined();
            }
            return;
        }

        double r1 = c1.getCircleRadius();
        double r2 = c2.getCircleRadius();
        
        // outer tangents
        if( Math.abs(r1-r2) > Kernel.MIN_PRECISION) {
            P.setCoords((c1.b.x*r2-c2.b.x*r1)/(r2-r1),
                        (c1.b.y*r2-c2.b.y*r1)/(r2-r1), 1.0d);
        } else {
            P.setCoords((c1.b.x*r2-c2.b.x*r1),
                        (c1.b.y*r2-c2.b.y*r1), 0.0d); // infinity
        }
        algoIntersect = new AlgoIntersectLineConic(cons, polar, c1);
        // this is only an internal Algorithm that shouldn't be in the construction list
        // cons.removeFromConstructionList(algoIntersect);
        tangentPoints = algoIntersect.getIntersectionPoints();
        tangents[0].setStartPoint(P);
        tangents[1].setStartPoint(P);
        // degenerates should not have any tangents
        if (c1.isDegenerate()) {
            tangents[0].setUndefined();
            tangents[1].setUndefined();
            return;
        }
        // update polar line
        c1.polarLine(P, polar);
        // if P lies on the conic, the polar is a tangent        
        if (c1.isIntersectionPointIncident(P, Kernel.MIN_PRECISION)) {
            tangents[0].setCoords(polar);
            tangentPoints[0].setCoords(P);
            // check if we had equal lines at the beginning
            // if so we still don't want to see the second line
            if (equalLines) {
                tangents[1].setUndefined();
                tangentPoints[1].setUndefined();
            } else {
                tangents[1].setCoords(polar);
                tangentPoints[1].setCoords(P);
            }
        }
        // if P is not on the conic, the tangents pass through
        // the intersection points of polar and conic
        else {
            // intersect polar line with conic -> tangentPoints
            algoIntersect.update();
            // calc tangents through tangentPoints
            GeoVec3D.lineThroughPoints(P, tangentPoints[0], tangents[0]);
            GeoVec3D.lineThroughPoints(P, tangentPoints[1], tangents[1]);
            // we no longer have equal lines (if we ever had them)
            equalLines = false;
        }
        // end of outer tangents

        // inner tangents
        P.setCoords((c1.b.x*r2+c2.b.x*r1)/(r2+r1),
                    (c1.b.y*r2+c2.b.y*r1)/(r2+r1), 1.0d);
        algoIntersect = new AlgoIntersectLineConic(cons, polar, c1);
        //  this is only an internal Algorithm that shouldn't be in the construction list
        // cons.removeFromConstructionList(algoIntersect);
        tangentPoints = algoIntersect.getIntersectionPoints();
        tangents[0+2].setStartPoint(P);
        tangents[1+2].setStartPoint(P);
        // degenerates should not have any tangents
        if (c1.isDegenerate()) {
            tangents[0+2].setUndefined();
            tangents[1+2].setUndefined();
            return;
        }
        // update polar line
        c1.polarLine(P, polar);
        // if P lies on the conic, the polar is a tangent        
        if (c1.isIntersectionPointIncident(P, Kernel.MIN_PRECISION)) {
            tangents[0+2].setCoords(polar);
            tangentPoints[0].setCoords(P);
            // check if we had equal lines at the beginning
            // if so we still don't want to see the second line
            if (equalLines) {
                tangents[1+2].setUndefined();
                tangentPoints[1].setUndefined();
            } else {
                tangents[1+2].setCoords(polar);
                tangentPoints[1].setCoords(P);
            }
        }
        // if P is not on the conic, the tangents pass through
        // the intersection points of polar and conic
        else {
            // intersect polar line with conic -> tangentPoints
            algoIntersect.update();
            // calc tangents through tangentPoints
            GeoVec3D.lineThroughPoints(P, tangentPoints[0], tangents[0+2]);
            GeoVec3D.lineThroughPoints(P, tangentPoints[1], tangents[1+2]);
            // we no longer have equal lines (if we ever had them)
            equalLines = false;
        }
        // end of inner tangents
        
    } // end of compute

    public final String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("CommonTangentOfCirclesAandB", c1.getLabel(), c2.getLabel());
    }
}

// Local Variables:
// indent-tabs-mode: nil
// c-basic-offset: 4
// tab-width: 4
// End:
// vim: set expandtab shiftwidth=4 softtabstop=4 tabstop=4
