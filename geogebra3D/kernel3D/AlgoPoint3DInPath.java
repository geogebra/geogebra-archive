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


public class AlgoPoint3DInPath extends AlgoElement3D {

	
	private PathIn path; // input
    private GeoPoint3D P; // output       

    AlgoPoint3DInPath(
        Construction cons,
        String label,
        PathIn path,
        double x,
        double y,
        double z) {
        super(cons);
        this.path = path;
        P = new GeoPoint3D(cons, path);
        P.setCoords(x, y, z, 1.0);

        setInputOutput(); // for AlgoElement

        // compute line through P, Q
        compute();
        P.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoPoint3DInPath";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = path.toGeoElement();

        output = new GeoElement[1];
        output[0] = P;
        setDependencies(); // done by AlgoElement
    }

    GeoPoint3D getP() {
        return P;
    }
    PathIn getPath() {
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
        sb.append(app.getPlain("PointInA",input[0].getLabel()));
        
        return sb.toString();
    }
}
