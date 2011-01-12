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
public class AlgoOrthoPlanePoint extends AlgoElement3D {

 
	private GeoPointND point; // input
    private GeoLineND line; // input
    private GeoPlane3D plane; // output       


    public AlgoOrthoPlanePoint(Construction cons, String label, GeoPointND point, GeoLineND line) {
        super(cons);
        this.point = point;
        this.line = line;
        plane = new GeoPlane3D(cons);
        //g.setStartPoint(P);
        
        setInputOutput(new GeoElement[] {(GeoElement) point, (GeoElement) line}, new GeoElement[] {plane});

        // compute plane 
        compute();
        plane.setLabel(label);
    }

    public String getClassName() {
        return "AlgoOrthoPlanePoint";
    }


    public GeoPlane3D getPlane() {
        return plane;
    }


  
    protected final void compute() {
    	
    	CoordSys coordsys = plane.getCoordSys();
    	
		//recompute the coord sys
    	coordsys.resetCoordSys();
		
		//if cs has "no" direction vector, set undefined and return
    	Coords vz = ((GeoElement) line).getMainDirection();
		if (vz.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			plane.setUndefined();
			return;
		}
			
		

		//Application.debug(m.toString());

		coordsys.addPoint(point.getCoordsInD(3));
		
		//gets an ortho matrix with coord sys direction vector
		Coords[] v = vz.completeOrthonormal();
		coordsys.addVectorWithoutCheckMadeCoordSys(v[1]);
		coordsys.addVectorWithoutCheckMadeCoordSys(v[0]);
		
		coordsys.makeOrthoMatrix(true);
		
		coordsys.makeEquationVector();
		

		

        
    }

    final public String toString() {
    	return app.getPlain("PlaneThroughAPerpendicularToB",point.getLabel(),((GeoElement) line).getLabel());

    }
}
