/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoIncircle.java, dsun48 [6/26/2011]
 *
 */

package geogebra.kernel;

import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.euclidian.EuclidianConstants;
import geogebra.main.Application;

public class AlgoIncircle extends AlgoElement {

    private static final long serialVersionUID = 1L;
    private GeoPointND A, B, C; // input    
    protected GeoConicND circle; // output     

    // angle bisector calculations
    private GeoLine bisectorC, bisectorB, sideBC, heightBC;
    private GeoPoint heightFoot, incenter;    
    private GeoPoint A1, B1, C1;

    protected AlgoIncircle(
                           Construction cons,
                           String label,
                           GeoPointND A,
                           GeoPointND B,
                           GeoPointND C) {
        this(cons, A, B, C);
        circle.setLabel(label);
    }
    
    public AlgoIncircle(
                        Construction cons,           
                        GeoPointND A,
                        GeoPointND B,
                        GeoPointND C) {
        
        super(cons);
            
        this.A = A;
        this.B = B;
        this.C = C; 
 
        circle = new GeoConic(cons); // output

        bisectorC = new GeoLine(cons);
        bisectorB = new GeoLine(cons);
        heightFoot = new GeoPoint(cons);            
        heightBC = new GeoLine(cons);
        sideBC = new GeoLine(cons);
        incenter = new GeoPoint(cons);            
        A1 = new GeoPoint(cons);            
        B1 = new GeoPoint(cons);            
        C1 = new GeoPoint(cons);            

        setInputOutput();

        compute();            
    }
    
    public String getClassName() {
        return "AlgoIncircle";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = (GeoElement) A;
        input[1] = (GeoElement) B;
        input[2] = (GeoElement) C;
        output = new GeoElement[1];
        output[0] = (GeoElement) circle;
        setDependencies(); // done by AlgoElement
    }
    
    public GeoConicND getCircle() {
        return (GeoConicND) circle;
    }
    public GeoPoint getA() {
        return (GeoPoint) A;
    }
    public GeoPoint getB() {
        return (GeoPoint) B;
    }
    public GeoPoint getC() {
        return (GeoPoint) C;
    }

    // compute incircle of triangle A, B, C
    protected void compute() {
        // bisector of angle ABC
        double dAB = getA().distance(getB());
        double dAC = getA().distance(getC());
        double dBC = getB().distance(getC());
        A1.setCoords(1.0/dAB * (getA().x-getB().x)+getB().x, 
                     1.0/dAB * (getA().y-getB().y)+getB().y, 
                     1.0/dAB * (getA().z-getB().z)+getB().z);  
        C1.setCoords(1.0/dBC * (getC().x-getB().x)+getB().x,
                     1.0/dBC * (getC().y-getB().y)+getB().y,
                     1.0/dBC * (getC().z-getB().z)+getB().z);  
        B1.setCoords((A1.x+C1.x)/2.0, (A1.y+C1.y)/2.0, (A1.z+C1.z)/2.0);
        GeoVec3D.lineThroughPoints(getB(), B1, bisectorB);
        // bisector of angle BCA
        A1.setCoords(1.0/dAC * (getA().x-getC().x)+getC().x, 
                     1.0/dAC * (getA().y-getC().y)+getC().y, 
                     1.0/dAC * (getA().z-getC().z)+getC().z);  
        B1.setCoords(1.0/dBC * (getB().x-getC().x)+getC().x,
                     1.0/dBC * (getB().y-getC().y)+getC().y,
                     1.0/dBC * (getB().z-getC().z)+getC().z);  
        C1.setCoords((A1.x+B1.x)/2.0, (A1.y+B1.y)/2.0, (A1.z+B1.z)/2.0);
        GeoVec3D.lineThroughPoints(getC(), C1, bisectorC);
        // intersect angle bisectors to get incenter
        GeoVec3D.lineThroughPoints(getB(), getC(), sideBC);
        GeoVec3D.cross(bisectorB, bisectorC, incenter);
        GeoVec3D.cross(incenter, sideBC.x, sideBC.y, 0.0, heightBC);
        GeoVec3D.cross(sideBC, heightBC, heightFoot);
        double dist = incenter.distance(heightFoot);
        circle.setCircle(incenter, dist);
    }

    public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("IncircleOfTriangleABC", A.getLabel(), B.getLabel(), C.getLabel());
    }
}

// Local Variables:
// indent-tabs-mode: nil
// c-basic-offset: 4
// tab-width: 4
// End:
// vim: set expandtab shiftwidth=4 softtabstop=4 tabstop=4

