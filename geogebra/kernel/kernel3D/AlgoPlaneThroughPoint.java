/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra.kernel.kernel3D;

import geogebra.Matrix.GgbCoordSys;
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


    public AlgoPlaneThroughPoint(Construction cons, String label, GeoPoint3D point, GeoCoordSys cs) {
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

    public String getClassName() {
        return "AlgoPlaneThroughPoint";
    }


    public GeoPlane3D getPlane() {
        return plane;
    }


  
    protected final void compute() {
    	
    	GgbCoordSys coordsys = plane.getCoordSys();
    	
		//recompute the coord sys
    	coordsys.resetCoordSys();
		
    	coordsys.addPoint(point.getCoords());
    	coordsys.addPoint((GgbVector) point.getCoords().add(cs.getCoordSys().getVx()));
		
		switch (cs.getCoordSys().getDimension()){
		case 1: //line, segment, ...
			coordsys.addPoint(cs.getCoordSys().getOrigin());
			break;
		case 2: //plane, polygon, ...
			coordsys.addPoint((GgbVector) point.getCoords().add(cs.getCoordSys().getVy()));
			break;
		}
		
		coordsys.makeOrthoMatrix(true);
		
		coordsys.makeEquationVector();
		

        
    }

    final public String toString() {
    	return app.getPlain("PlaneThroughAParallelToB",point.getLabel(),((GeoElement) cs).getLabel());

    }
}
