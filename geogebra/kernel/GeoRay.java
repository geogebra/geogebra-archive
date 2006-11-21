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

import geogebra.kernel.arithmetic.NumberValue;

/**
 * @author Markus Hohenwarter
 */
final public class GeoRay extends GeoLine implements LimitedPath {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean allowOutlyingIntersections = false;
	private boolean keepTypeOnGeometricTransform = true;
	
	/**
	 * Creates ray with start point A.
	 */
	public GeoRay(Construction c, GeoPoint A) {
		super(c);		
		setStartPoint(A);
	}
	
	String getClassName() {	
		return "GeoRay";
 	}
	
	 String getTypeString() {
		return "Ray";
	}

	public int getGeoClassType() {
		return GEO_CLASS_RAY;
	}

	 
	/**
	 * the copy of a ray is an independent line
	 *
	public GeoElement copy() {
		return new GeoLine(this); 
	}*/
	 
	
	public GeoElement copyInternal() {
		GeoRay ray = new GeoRay(cons, startPoint);
		ray.setInternal(this);
		return ray;
	}
	
	public void setInternal(GeoElement geo) {
		GeoRay ray = (GeoRay) geo;		
		setStartPoint(ray.startPoint);		
		super.set(ray);
	}
	
	/* 
	 * Path interface
	 */	 
	public void pointChanged(GeoPoint P) {
		super.pointChanged(P);
		
		// ensure that the point doesn't get outside the ray
		// i.e. ensure 0 <= t <= 1 
		if (P.pathParameter.t < 0.0) {
			P.x = startPoint.x;
			P.y = startPoint.y;
			P.z = startPoint.z; 
			P.pathParameter.t = 0.0;
		} 
	}

	public void pathChanged(GeoPoint P) {
		// calc point for given parameter
		P.x = startPoint.inhomX + P.pathParameter.t * y;
		P.y = startPoint.inhomY - P.pathParameter.t * x;
		P.z = 1.0;		
	}
	
	public boolean allowOutlyingIntersections() {
		return allowOutlyingIntersections;
	}
	
	public void setAllowOutlyingIntersections(boolean flag) {
		allowOutlyingIntersections = flag;		
	}
	
	public boolean keepsTypeOnGeometricTransform() {		
		return keepTypeOnGeometricTransform;
	}

	public void setKeepTypeOnGeometricTransform(boolean flag) {
		keepTypeOnGeometricTransform = flag;
	}
	
	final public boolean isLimitedPath() {
		return true;
	}
	
    public boolean isIntersectionPointIncident(GeoPoint p, double eps) {
    	if (allowOutlyingIntersections)
			return isOnFullLine(p, eps);
		else
			return isOnPath(p, eps);
    }
    
    /** returns true if P lies on this segment */
    public boolean isOnPath(GeoPoint P, double eps) {       	
    	// check if P lies on line first
    	if (!isOnFullLine(P, eps))
    		return false;
    	
    	// idea: calculate path parameter and check
		//       if it is > 0
		
		// remember the old values
		double px = P.x, py = P.y, pz = P.z;
		tempPP.set(P.pathParameter);

		super.pointChanged(P);
		boolean result = 	P.pathParameter.t >= -eps;
	
		// restore old values
		P.x = px; P.y = py; P.z = pz;
		P.pathParameter.set(tempPP);
		
		return result;
    }
    private PathParameter tempPP = new PathParameter();
	
	/**
	 * Returns the smallest possible parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * @return
	 */
	public double getMinParameter() {
		return 0;
	}
	
	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * @return
	 */
	public double getMaxParameter() {
		return Double.POSITIVE_INFINITY;
	}
	
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}
	
	/**
     * returns all class-specific xml tags for saveXML
     */
    String getXMLtags() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.getXMLtags());
		
        // allowOutlyingIntersections
        sb.append("\t<outlyingIntersections val=\"");
        sb.append(allowOutlyingIntersections);
        sb.append("\"/>\n");
        
        // keepTypeOnGeometricTransform
        sb.append("\t<keepTypeOnTransform val=\"");
        sb.append(keepTypeOnGeometricTransform);
        sb.append("\"/>\n");
        
        return sb.toString();   
    }

   
    /**
     * Creates a new ray using a geometric transform.
     * @param type of transform (Kernel constant)
     */

	public GeoElement [] createTransformedObject(int type, String label, GeoPoint Q, 
													GeoLine l, GeoVector vec, NumberValue n) {	
		AlgoElement algoParent = keepTypeOnGeometricTransform ?
				getParentAlgorithm() : null;				
		
		// CREATE RAY
		if (algoParent instanceof AlgoJoinPointsRay) {	
			//	transform points
			AlgoJoinPointsRay algo = (AlgoJoinPointsRay) algoParent;
			GeoPoint [] points = {algo.getP(), algo.getQ()};
			points = kernel.transformPoints(type, points, Q, l, vec, n);	
			GeoElement [] geos = {kernel.Ray(label, points[0], points[1]), points[0], points[1]};
			return geos;
		}
		else if (algoParent instanceof AlgoRayPointVector) {			
			// transform startpoint
			GeoPoint [] points = {getStartPoint()};
			points = kernel.transformPoints(type, points, Q, l, vec, n);					
						
			// get transformed line from this ray
			GeoLine transformedLine = kernel.getTransformedLine(type, this, Q, l, vec, n);
			cons.removeFromConstructionList(transformedLine.getParentAlgorithm());
									
			// get direction of transformed line
			AlgoDirection algoDir = new AlgoDirection(cons, transformedLine);
			cons.removeFromConstructionList(algoDir);
			GeoVector direction = algoDir.getVector();
			
			// ray through transformed point with direction of transformed line
			GeoElement [] geos = {kernel.Ray(label, points[0], direction), points[0], direction};
			return geos;					
			
		} else {
			//	create LINE	
			GeoLine transformedLine = kernel.getTransformedLine(type, this, Q, l, vec, n);
			transformedLine.setLabel(label);
			GeoElement [] ret = { transformedLine };
			return ret;
		}	
	}		
	
	public boolean isGeoRay() {
		return true;
	}
	
}
