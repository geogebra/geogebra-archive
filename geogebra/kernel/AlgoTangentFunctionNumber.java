/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * AlgoTangents.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.NumberValue;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoTangentFunctionNumber extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NumberValue n; // input
    private GeoElement ngeo;
    private GeoFunction f; // input
    private GeoLine tangent; // output  

    private GeoPoint T;

    AlgoTangentFunctionNumber(
        Construction cons,
        String label,
        NumberValue n,
        GeoFunction f) {
        super(cons);
        this.n = n;
        ngeo = n.toGeoElement();
        this.f = f;

        tangent = new GeoLine(cons);
        T = new GeoPoint(cons);
        tangent.setStartPoint(T);

        setInputOutput(); // for AlgoElement                
        compute();
        tangent.setLabel(label);
    }

    String getClassName() {
        return "AlgoTangentFunctionNumber";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = ngeo;
        input[1] = f;

        output = new GeoElement[1];
        output[0] = tangent;
        setDependencies(); // done by AlgoElement
    }

    GeoLine getTangent() {
        return tangent;
    }
    GeoFunction getFunction() {
        return f;
    }

    // calc tangent at x=a
    final void compute() {
        double a = n.getDouble();
        if (!f.isDefined() || Double.isInfinite(a) || Double.NaN == a) {
            tangent.setUndefined();
            return;
        }

        // first derivative
        Function df = f.getFunction().getDerivative(1);
        if (df == null) {
            tangent.setUndefined();
            return;
        }

        // calc the tangent;    
        double fa = f.evaluate(a);
        double slope = df.evaluate(a);
        tangent.setCoords(-slope, 1.0, a * slope - fa);
        T.setCoords(a, fa, 1.0);
    }

    public final String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("TangentLine"));
            sb.append(' ');
        }
        sb.append(app.getPlain("to"));
        sb.append(' ');
        sb.append(f.getLabel());
        sb.append(' ');
        sb.append(app.getPlain("in"));
        sb.append(" x = ");
        sb.append(ngeo.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("TangentLine"));
        }
        return sb.toString();
    }
}
