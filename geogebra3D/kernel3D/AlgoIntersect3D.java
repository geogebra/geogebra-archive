/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;

import geogebra.Matrix.CoordMatrix4x4;
import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.kernelND.AlgoIntersectND;
import geogebra.kernel.kernelND.GeoPointND;



public abstract class AlgoIntersect3D extends AlgoIntersectND {

    // gives the number of intersection algorithms
    // this algorithm is used by: see AlgoIntersectSingle
    private int numberOfUsers = 0;
    
    // used in setIntersectionPoint to remember all indices that have been set
    private boolean [] didSetIntersectionPoint;

    public AlgoIntersect3D(Construction c) {
        super(c);
    }
    
	/**
	 * Avoids two intersection points at same position. 
	 * This is only done as long as the second intersection point doesn't have a label yet.
	 */
	protected void avoidDoubleTangentPoint() {
		GeoPoint3D [] points = getIntersectionPoints();
	    if (!points[1].isLabelSet() && points[0].isEqual(points[1])) {
	    	points[1].setUndefined();	        
	    }
	}
    

    
    

    /**
     * Returns the index in output[] of the intersection point
     * that is closest to the coordinates (xRW, yRW)
     * TODO: move to an interface
     */
    int getClosestPointIndex(double xRW, double yRW, CoordMatrix4x4 mat) {
        GeoPoint3D[] P = getIntersectionPoints();
        double x, y, lengthSqr, mindist = Double.POSITIVE_INFINITY;
        int minIndex = 0;
        for (int i = 0; i < P.length; i++) {
        	Coords toSceneInhomCoords = mat.mul(P[i].getCoords().getCoordsLast1()).getInhomCoords();
        	x = (toSceneInhomCoords.getX() - xRW);
            y = (toSceneInhomCoords.getY() - yRW);
            lengthSqr = x * x + y * y;
            if (lengthSqr < mindist) {
                mindist = lengthSqr;
                minIndex = i;
            }
        }

        return minIndex;
    }

    protected abstract GeoPoint3D[] getIntersectionPoints();
    protected abstract GeoPoint3D[] getLastDefinedIntersectionPoints();
    
   

    //TODO: organize better according to types: GeoVec, GeoVec4D, GeoVec3D, Coords
    protected void setCoords(GeoPointND destination, GeoPointND source){
    	((GeoPoint3D) destination).setCoords(((GeoPoint3D) source).getCoords());
    }
    protected abstract void compute();
    protected abstract void initForNearToRelationship();
}
