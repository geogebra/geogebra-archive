/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra3D.kernel3D;

import geogebra.Matrix.CoordSys;
import geogebra.Matrix.CoordMatrix4x4;
import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;


/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author  matthieu
 * @version 
 */
public class AlgoOrthoPlanePoint extends AlgoOrthoPlane {

 
	private GeoPointND point; // input
    private GeoLineND line; // input   


    public AlgoOrthoPlanePoint(Construction cons, String label, GeoPointND point, GeoLineND line) {
        super(cons);
        this.point = point;
        this.line = line;
        
        setInputOutput(new GeoElement[] {(GeoElement) point, (GeoElement) line}, new GeoElement[] {getPlane()});

        // compute plane 
        compute();
        getPlane().setLabel(label);
    }

    public String getClassName() {
        return "AlgoOrthoPlanePoint";
    }




    protected Coords getNormal(){
    	return ((GeoElement) line).getMainDirection();
    }

    protected Coords getPoint(){
    	return point.getInhomCoordsInD(3);
    }

    final public String toString() {
    	return app.getPlain("PlaneThroughAPerpendicularToB",point.getLabel(),((GeoElement) line).getLabel());

    }
}
