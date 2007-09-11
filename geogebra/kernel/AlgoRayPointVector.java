/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


public class AlgoRayPointVector extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint P; // input
    private GeoVector v; // input
    private GeoRay ray; // output       

    AlgoRayPointVector(
        Construction cons,
        String label,
        GeoPoint P,
        GeoVector v) {
        super(cons);
        this.P = P;
        this.v = v;
        ray = new GeoRay(cons, P);
        setInputOutput(); // for AlgoElement

        // compute line through P, Q
        compute();
        ray.setLabel(label);
    }

    String getClassName() {
        return "AlgoRayPointVector";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = v;

        output = new GeoElement[1];
        output[0] = ray;
        setDependencies(); // done by AlgoElement
    }

    GeoRay getRay() {
        return ray;
    }
    GeoPoint getP() {
        return P;
    }
    GeoVector getv() {
        return v;
    }

    // calc the line g through P and Q    
    final void compute() {
        // g = cross(P, v)
        GeoVec3D.lineThroughPointVector(P, v, ray);
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("RayThrough"));
            sb.append(" ");
            sb.append(P.getLabel());
            sb.append(" ");
            sb.append(app.getPlain("withDirection"));
            sb.append(" ");
            sb.append(v.getLabel());
        } else {
            sb.append(v.getLabel());
            sb.append(" ");
            sb.append(app.getPlain("withDirection"));
            sb.append(" ");
            sb.append(app.getPlain("RayThrough"));
            sb.append(" ");
            sb.append(P.getLabel());
            sb.append(" ");
        }
        return sb.toString();
    }

}
