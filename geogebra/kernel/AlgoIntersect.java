/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;



public abstract class AlgoIntersect extends AlgoIntersectAbstract {

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
	void avoidDoubleTangentPoint() {
		GeoPoint [] points = getIntersectionPoints();
	    if (!points[1].isLabelSet() && points[0].isEqual(points[1])) {
	    	points[1].setUndefined();	        
	    }
	}
    
    boolean showUndefinedPointsInAlgebraView() {
    	return false;
    }
    
    void noUndefinedPointsInAlgebraView() {
    	 GeoPoint [] points = getIntersectionPoints();
    	 for (int i=1; i < points.length; i++) {
    		 points[i].showUndefinedInAlgebraView(false);
    	 }
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
    final void setIntersectionPoint(int index, GeoPoint p) {  
    	GeoPoint [] points = getIntersectionPoints();
    	GeoPoint [] defpoints = getLastDefinedIntersectionPoints();
    	
    	if (!p.isDefined() || index >= points.length) {
    		return;
    	}

    	// init didSetIntersectionPoint array
    	if (didSetIntersectionPoint == null) {
    		didSetIntersectionPoint = new boolean[points.length];
    	} 
    	else if (didSetIntersectionPoint.length < points.length) {
    		boolean [] temp = new boolean[points.length];
    		for (int i=0; i < points.length; i++) {
    			if (i < didSetIntersectionPoint.length)
    				temp[i] = didSetIntersectionPoint[i];
    			else
    				temp[i] = false;
    		}
    		didSetIntersectionPoint = temp;
    	}
    	
    	// set coords of intersection point to those of p
    	points[index].setCoords(p);  
    	if (defpoints != null) defpoints[index].setCoords(p);
    	// we only remember setting the point if we used a defined point
		didSetIntersectionPoint[index] = true;

		// all other intersection points should be set undefined
		// unless they have been set before
		for (int i=0; i < points.length; i++) {
			if (!didSetIntersectionPoint[i]) {				
				points[i].setUndefined();
				if (defpoints != null) defpoints[i].setUndefined();
			}
		}	
		
//		Application.debug("SET INTERSECTION POINT");	
//		for (int i=0; i < points.length; i++) {
//			Application.debug("    point " + i + ": " + points[i] + ", defPoint " + defpoints[i]);										
//		}						
    }
    
    /**
     * Returns true if setIntersectionPoint was called for index-th point.
     */
    boolean didSetIntersectionPoint(int index) {
    	return didSetIntersectionPoint != null && didSetIntersectionPoint[index];
    }

    public String toString() {      
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("IntersectionPointOfAB",input[0].getLabel(),input[1].getLabel());
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
