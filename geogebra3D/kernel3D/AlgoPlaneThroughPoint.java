/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra3D.kernel3D;

import geogebra.Matrix.GgbCoordSys;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;


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
public abstract class AlgoPlaneThroughPoint extends AlgoElement3D {

 
	private GeoPointND point; // input
    private GeoElement cs; // input
    
    
    private GeoPlane3D plane; // output       


    public AlgoPlaneThroughPoint(Construction cons, GeoPointND point) {
        super(cons);
        this.point = point;
        plane = new GeoPlane3D(cons);
        
    }

    public String getClassName() {
        return "AlgoPlaneThroughPoint";
    }


    public GeoPlane3D getPlane() {
        return plane;
    }


    protected GeoPointND getPoint(){
    	return point;
    }
  
    
    abstract protected GeoElement getSecondInput();

    final public String toString() {
    	return app.getPlain("PlaneThroughAParallelToB",point.getLabel(),getSecondInput().getLabel());

    }
}
