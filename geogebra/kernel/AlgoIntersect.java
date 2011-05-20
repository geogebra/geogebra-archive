/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.kernelND.AlgoIntersectND;
import geogebra.kernel.kernelND.GeoPointND;



public abstract class AlgoIntersect extends AlgoIntersectND {

    // gives the number of intersection algorithms
    // this algorithm is used by: see AlgoIntersectSingle
    private int numberOfUsers = 0;
    
    // used in setIntersectionPoint to remember all indices that have been set
    private boolean [] didSetIntersectionPoint;

    public AlgoIntersect(Construction c) {
        super(c);
    }
    
	/**
	 * Avoids two intersection points at same position. 
	 * This is only done as long as the second intersection point doesn't have a label yet.
	 */
	protected void avoidDoubleTangentPoint() {
		GeoPoint [] points = getIntersectionPoints();
	    if (!points[1].isLabelSet() && points[0].isEqual(points[1])) {
	    	points[1].setUndefined();	        
	    }
	}
    

    
    

    /**
     * Returns the index in output[] of the intersection point
     * that is closest to the coordinates (xRW, yRW)
     */
    int getClosestPointIndex(double xRW, double yRW) {
        GeoPoint[] P = getIntersectionPoints();
        double x, y, lengthSqr, mindist = Double.POSITIVE_INFINITY;
        int minIndex = 0;
        for (int i = 0; i < P.length; i++) {
            x = (P[i].inhomX - xRW);
            y = (P[i].inhomY - yRW);
            lengthSqr = x * x + y * y;
            if (lengthSqr < mindist) {
                mindist = lengthSqr;
                minIndex = i;
            }
        }

        return minIndex;
    }

    protected abstract GeoPoint[] getIntersectionPoints();
    protected abstract GeoPoint[] getLastDefinedIntersectionPoints();
    
   


    protected void setCoords(GeoPointND destination, GeoPointND source){
    	((GeoPoint) destination).setCoords((GeoPoint) source);
    }

}
