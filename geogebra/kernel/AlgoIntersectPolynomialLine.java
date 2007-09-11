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
 * Finds intersection points of a polynomial and a line
 * (using the roots of their difference)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntersectPolynomialLine extends AlgoRootsPolynomial {
                
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlgoIntersectPolynomialLine(Construction cons, GeoFunction f, GeoLine g) {
        super(cons, f, g);                      
    }
    
    String getClassName() {
        return "AlgoIntersectPolynomialLine";
    }
    
    public GeoPoint [] getIntersectionPoints() {
        return super.getRootPoints();
    }

    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(app.getPlain("IntersectionPointOf"));
        sb.append(" ");
        sb.append(input[0].getLabel());
        sb.append(", ");
        sb.append(input[1].getLabel());
        
        return sb.toString();
    }

}
