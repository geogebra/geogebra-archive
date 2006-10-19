/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
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
