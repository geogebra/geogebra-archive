/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * Sum of squares of a list
 * @author Michael Borcherds
 * @version 2008-02-23
 */

public class AlgoDoubleListSigmaYY extends AlgoStats2D {

	private static final long serialVersionUID = 1L;

    AlgoDoubleListSigmaYY(Construction cons, String label, GeoList geoListx, GeoList geoListy) {
        super(cons,label,geoListx,geoListy,AlgoStats2D.STATS_SIGMAYY);
    }

    protected String getClassName() {
        return "AlgoDoubleListSigmaYY";
    }
}
