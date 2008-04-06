/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.statistics;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Construction;

/**
 * Sxy of a list
 * @author Michael Borcherds
 * @version 2008-02-23
 */

public class AlgoDoubleListSXY extends AlgoStats2D {

	private static final long serialVersionUID = 1L;

	public AlgoDoubleListSXY(Construction cons, String label, GeoList geoListx, GeoList geoListy) {
        super(cons,label,geoListx,geoListy,AlgoStats2D.STATS_SXY);
    }

    protected String getClassName() {
        return "AlgoDoubleListSXY";
    }
}
