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
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;


/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author  matthieu
 * @version 
 */
public class AlgoOrthoLinePointLine3D extends AlgoOrtho {

 

    public AlgoOrthoLinePointLine3D(Construction cons, String label, GeoPointND point, GeoLineND line) {
        super(cons,label,point, (GeoElement) line);
    }

    public String getClassName() {
        return "AlgoOrthoLinePointLine";
    }


    private GeoLineND getInputLine(){
    	return (GeoLineND) getInputOrtho();
    }

  
    protected final void compute() {
    	
    	GeoLineND line = getInputLine();
    	GgbVector o = line.getPointInD(3, 0);
    	GgbVector v1 = line.getPointInD(3, 1).sub(o);
    	GgbVector o2 = getPoint().getCoordsInD(3);
    	GgbVector v2 = o2.sub(o);
    	
    	GgbVector v3 = v1.crossProduct(v2);
    	GgbVector v = v3.crossProduct(v1);
    	
    	if (v.equalsForKernel(0, Kernel.STANDARD_PRECISION))
    		getLine().setUndefined();
    	else
    		getLine().setCoord(getPoint().getCoordsInD(3), v.normalize());
        
    }

}
