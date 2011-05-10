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
 * Compute a circle with axis and through a point 
 *
 * @author  matthieu
 * @version 
 */
public class AlgoCircle3DAxisPoint extends AlgoElement3D {

 
    private GeoLineND axis; // input
	private GeoPointND point; // input
    private GeoConic3D circle; // output       
    private CoordSys coordsys;

    public AlgoCircle3DAxisPoint(Construction cons, String label, GeoLineND axis, GeoPointND point) {
        super(cons);
        
        this.axis = axis;
        this.point = point;
        circle = new GeoConic3D(cons);
        coordsys = new CoordSys(2);
		circle.setCoordSys(coordsys);
		
        setInputOutput(new GeoElement[] {(GeoElement) axis, (GeoElement) point}, new GeoElement[] {circle});

        // compute line 
        compute();
        circle.setLabel(label);
    }


    /**
     * 
     * @return circle
     */
    public GeoConic3D getCircle() {
        return circle;
    }
    

    
    protected final void compute() {
    	
    	Coords p = point.getCoordsInD(3);
    	Coords o = axis.getPointInD(3, 0);
    	Coords d = axis.getPointInD(3, 1).sub(o);
    	
    	// project the point on the axis   	
    	Coords center = p.projectLine(o, d)[0];
    	
    	//create the coord sys containing the circle
    	
    	
		//recompute the coord sys
    	coordsys.resetCoordSys();
		
    	coordsys.addPoint(center);
    	Coords v1 = p.sub(center);
		coordsys.addVector(v1);
		coordsys.addVector(d.crossProduct(v1));
		
		coordsys.makeOrthoMatrix(false,false);
		
    	
		//set the circle
		v1.calcNorm();
    	circle.setSphereND(new Coords(0,0), v1.getNorm());

    }

    public String getClassName() {
        return "AlgoCircleAxisPoint";
    }

    
    final public String toString() {
    	return app.getPlain("CircleOfAxisAThroughB",((GeoElement) axis).getLabel(),point.getLabel());
    }
}
