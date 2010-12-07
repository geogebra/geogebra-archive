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
import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
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
public class AlgoOrthoLinePointPlane extends AlgoOrtho {

 

    public AlgoOrthoLinePointPlane(Construction cons, String label, GeoPointND point, GeoPlane3D plane) {
        super(cons,label,point,plane);
    }

    public String getClassName() {
        return "AlgoOrthoLinePoint";
    }


    private GeoPlane3D getPlane(){
    	return (GeoPlane3D) getInputOrtho();
    }

  
    protected final void compute() {
    	
    	GgbCoordSys coordsys = getPlane().getCoordSys();
    	
    	getLine().setCoord(getPoint().getCoordsInD(3), coordsys.getVz());
        
    }

}
