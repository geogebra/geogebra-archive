/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


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
        P = new GeoPoint(cons, path);
        P.setCoords(x, y, 1.0);

        setInputOutput(); // for AlgoElement

        // compute line through P, Q
        compute();
        P.setLabel(label);
    }

    String getClassName() {
        return "AlgoPointOnPath";
    }

    // for AlgoElement
    void setInputOutput() {
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

    final void compute() {
    	if (input[0].isDefined()) {	    	
	        path.pathChanged(P);
	        P.updateCoords();
    	} else {
    		P.setUndefined();
    	}
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("PointOn"));
            sb.append(' ');
        }
        sb.append(input[0].getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("PointOn"));
        }
        return sb.toString();
    }
}
