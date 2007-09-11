/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


/**
 * Finds all local extrema of a polynomial
 * 
 * @author Markus Hohenwarter
 */
public class AlgoExtremumPolynomial extends AlgoRootsPolynomial {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlgoExtremumPolynomial(
        Construction cons,
        String[] labels,
        GeoFunction f) {
        super(cons, labels, f);
    }

    String getClassName() {
        return "AlgoExtremumPolynomial";
    }

    public GeoPoint[] getExtremumPoints() {
        return super.getRootPoints();
    }

    final void compute() {
        if (f.isDefined()) {
            yValFunction = f.getFunction();                                                                    
            
            // roots of first derivative 
            //(roots without change of sign are removed)
            calcRoots(yValFunction, 1);             
        } else {
            curRealRoots = 0;
        }

        setRootPoints(curRoots, curRealRoots);
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("Extremum"));
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
        }
        sb.append(f.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
            sb.append(app.getPlain("Extremum"));
        }
        return sb.toString();
    }

}
