/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.main.Application;


public class AlgoPointOnPath extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private Path path; // input
    private GeoPoint P; // output       

    AlgoPointOnPath(
        Construction cons,
        String label,
        Path path,
        double x,
        double y) {
        super(cons);
        this.path = path;
        
        // create point on path and compute current location
        P = new GeoPoint(cons);
        P.setPath(path);
        P.setCoords(x, y, 1.0);                   

        setInputOutput(); // for AlgoElement
       
        P.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoPointOnPath";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = path.toGeoElement();

        output = new GeoElement[1];
        output[0] = P;
        setDependencies(); // done by AlgoElement
    }

    GeoPoint getP() {
        return P;
    }
    Path getPath() {
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
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation        
        return app.getPlain("PointOnA", input[0].getLabel());
    }
}
