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
    private GeoPlane3D pIn; // input
    private GeoPlane3D pOut; // output       


    AlgoPlaneThroughPoint(Construction cons, String label, GeoPoint3D point, GeoPlane3D pIn) {
        super(cons);
        this.point = point;
        this.pIn = pIn;
        pOut = new GeoPlane3D(cons);
        //g.setStartPoint(P);
        
        setInputOutput(new GeoElement[] {point,pIn}, new GeoElement[] {pOut});

        // compute plane 
        compute();
        pOut.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoPlaneThroughPoint";
    }


    GeoPlane3D getPlane() {
        return pOut;
    }


  
    protected final void compute() {
		//recompute the coord sys
		pOut.resetCoordSys();
		
		pOut.addPointToCoordSys(point.getCoords(), true, true);
		pOut.addPointToCoordSys((GgbVector) point.getCoords().add(pIn.getVx()), true, true);
		pOut.addPointToCoordSys((GgbVector) point.getCoords().add(pIn.getVy()), true, true);
		

        
    }

    final public String toString() {
    	return app.getPlain("PlaneThroughAParallelToB",point.getLabel(),pIn.getLabel());

    }
}
