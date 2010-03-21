/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra3D.kernel3D;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;


/**
 * Compute a plane through a point and:
 * <ul>
 * <li> parallel to another plane (or polygon)
 * <li> through a line (or segment, ...) TODO
 * </ul>
 *
 * @author  matthieu
 * @version 
 */
public class AlgoPlaneThroughPoint extends AlgoElement3D {

 
	private GeoPoint3D point; // input
    private GeoCoordSys cs; // input
    private GeoPlane3D plane; // output       


    AlgoPlaneThroughPoint(Construction cons, String label, GeoPoint3D point, GeoCoordSys cs) {
        super(cons);
        this.point = point;
        this.cs = cs;
        plane = new GeoPlane3D(cons);
        //g.setStartPoint(P);
        
        setInputOutput(new GeoElement[] {point, (GeoElement) cs}, new GeoElement[] {plane});

        // compute plane 
        compute();
        plane.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoPlaneThroughPoint";
    }


    GeoPlane3D getPlane() {
        return plane;
    }


  
    protected final void compute() {
		//recompute the coord sys
		plane.resetCoordSys();
		
		plane.addPointToCoordSys(point.getCoords(), true, true);
		plane.addPointToCoordSys((GgbVector) point.getCoords().add(cs.getCoordSys().getVx()), true, true);
		
		switch (cs.getCoordSys().getDimension()){
		case 1: //line, segment, ...
			plane.addPointToCoordSys(cs.getCoordSys().getOrigin(), true, true);
			break;
		case 2: //plane, polygon, ...
			plane.addPointToCoordSys((GgbVector) point.getCoords().add(cs.getCoordSys().getVy()), true, true);
			break;
		}
		

        
    }

    final public String toString() {
    	return app.getPlain("PlaneThroughAParallelToB",point.getLabel(),((GeoElement) cs).getLabel());

    }
}
