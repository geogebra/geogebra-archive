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


public abstract class AlgoIntersect extends AlgoElement {

    // gives the number of intersection algorithms
    // this algorithm is used by: see AlgoIntersectSingle
    private int numberOfUsers = 0;
    
    // used in setIntersectionPoint to remember all indices that have been set
    private boolean [] didSetIntersectionPoint;

    public AlgoIntersect(Construction c) {
        super(c);
    }
    


    void addUser() {
        numberOfUsers++;
    }

    void removeUser() {
        numberOfUsers--;

        if (numberOfUsers == 0 && !isPrintedInXML()) {
            //  this algorithm has no users and no labeled output   
            super.remove();
            kernel.removeIntersectionAlgorithm(this);
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

    abstract GeoPoint[] getIntersectionPoints();
    abstract GeoPoint[] getLastDefinedIntersectionPoints();
    
    /**
     * Sets the index-th intersection point to the coords of p. 
     * This is needed when
     * loading constructions from a file to make sure the intersection points
     * remain at their saved positions.
     */
    void setIntersectionPoint(int index, GeoPoint p) {
    	GeoPoint [] points = getIntersectionPoints();
    	GeoPoint [] defpoints = getLastDefinedIntersectionPoints();
    	
    	if (didSetIntersectionPoint == null) {
    		didSetIntersectionPoint = new boolean[points.length];
    	}
    	
    	// set coords of intersection point to those of p
    	points[index].setCoords(p);  
    	if (defpoints != null) defpoints[index].setCoords(p);
		didSetIntersectionPoint[index] = true;

		// all other intersection points should be set undefined
		for (int i=0; i < points.length; i++) {
			if (!didSetIntersectionPoint[i]) {
				points[i].setUndefined();
				if (defpoints != null) defpoints[i].setUndefined();
			}
		}		
    }
    
    

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("IntersectionPointOf"));
            sb.append(" ");
        }
        sb.append(input[0].getLabel());
        sb.append(", ");
        sb.append(input[1].getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(" ");
            sb.append(app.getPlain("IntersectionPointOf"));
        }
        return sb.toString();
    }

    public void remove() {
        if (numberOfUsers == 0) {
            //  this algorithm has no users and no labeled output       
            super.remove();
            kernel.removeIntersectionAlgorithm(this);
        } else {
            // there are users of this algorithm, so we keep it
            // remove only output
            // delete dependent objects        
            for (int i = 0; i < output.length; i++) {
                output[i].doRemove();
            }
            setPrintedInXML(false);
        }
    }

}
