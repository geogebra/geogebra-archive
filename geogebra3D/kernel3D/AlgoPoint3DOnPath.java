/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;


public class AlgoPoint3DOnPath extends AlgoElement3D {

	
	private PathOn path; // input
    private GeoPoint3D P; // output       

    AlgoPoint3DOnPath(
        Construction cons,
        String label,
        PathOn path,
        double x,
        double y,
        double z) {
        super(cons);
        this.path = path;
        P = new GeoPoint3D(cons, path);
        
        // 3D Path -> 2D Path -> GeoPoint on Path -> GeoPoint3D on Path
    	Path path2D = path.getPath2D();
    	GeoPoint P2D = null;
    	if (path2D!=null) {    		
    		kernel.setSilentMode(true);
    		P2D = kernel.Point(null, path2D);
    		kernel.setSilentMode(false);    		
    	}
    	P.setGeoElement2D(P2D);
    	
        P.setCoords(x, y, z, 1.0);

        setInputOutput(); // for AlgoElement

        // compute 
        compute();
        P.setLabel(label);
    }
    
    
    

    protected String getClassName() {
        return "AlgoPoint3DOnPath";
    }

    // for AlgoElement
    protected void setInputOutput() {
    	
    	if (P.getGeoElement2D()==null)
    		input = new GeoElement[1];
    	else{
    		input = new GeoElement[2];
    		input[1] = P.getGeoElement2D();
    	}
        input[0] = path.toGeoElement();

        output = new GeoElement[1];
        output[0] = P;
        setDependencies(); // done by AlgoElement
    }

    GeoPoint3D getP() {
        return P;
    }
    PathOn getPath() {
        return path;
    }

    protected final void compute() {
    	
    	
    	if (input[0].isDefined()) {	    	
	        path.pathChanged(P);
	        P.updateCoords();
    	} else {
    		P.setUndefined();
    	}
    	
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("PointOnA",input[0].getLabel()));
        
        return sb.toString();
    }
}
