/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
	
	protected String getClassName() {	
		return "GeoRay";
 	}
	
	 protected String getTypeString() {
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
	 
	
	public GeoElement copyInternal(Construction cons) {
		GeoRay ray = new GeoRay(cons, (GeoPoint) startPoint.copyInternal(cons));
		ray.set(this);
		return ray;
	}
	
	public void set(GeoElement geo) {
		super.set(geo);	
		if (!geo.isGeoRay()) return;
		
		GeoRay ray = (GeoRay) geo;		
		keepTypeOnGeometricTransform = ray.keepTypeOnGeometricTransform; 
										
		startPoint.set((GeoElement) ray.startPoint);		
	}
	
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);
		
		if (geo.isGeoRay()) { 
			GeoRay ray = (GeoRay) geo;
			allowOutlyingIntersections = ray.allowOutlyingIntersections;
		}
	}
	
	/* 
	 * Path interface
	 */	 
	public void pointChanged(GeoPointInterface PI) {
		super.pointChanged(PI);
		
		GeoPoint P = (GeoPoint) PI;
		
		// ensure that the point doesn't get outside the ray
		// i.e. ensure 0 <= t <= 1 
		PathParameter pp = P.getPathParameter();
		if (pp.t < 0.0) {
			P.x = startPoint.x;
			P.y = startPoint.y;
			P.z = startPoint.z; 
			pp.t = 0.0;
		} 
	}

	public void pathChanged(GeoPointInterface PI) {
		
		GeoPoint P = (GeoPoint) PI;
		
		PathParameter pp = P.getPathParameter();
		if (pp.t < 0.0) {
			pp.t = 0;
		} 		
		
		// calc point for given parameter
		P.x = startPoint.inhomX + pp.t * y;
		P.y = startPoint.inhomY - pp.t * x;
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
	protected void getXMLtags(StringBuilder sb) {
        super.getXMLtags(sb);
		
        // allowOutlyingIntersections
        sb.append("\t<outlyingIntersections val=\"");
        sb.append(allowOutlyingIntersections);
        sb.append("\"/>\n");
        
        // keepTypeOnGeometricTransform
        sb.append("\t<keepTypeOnTransform val=\"");
        sb.append(keepTypeOnGeometricTransform);
        sb.append("\"/>\n");
 
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
			boolean oldSuppressLabelCreation = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			AlgoDirection algoDir = new AlgoDirection(cons, transformedLine);
			cons.removeFromConstructionList(algoDir);
			GeoVector direction = algoDir.getVector();
			cons.setSuppressLabelCreation(oldSuppressLabelCreation);
			
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
    // Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type, otherwise check direction and start point
		if (!geo.isGeoRay()) return false;
		
		return isSameDirection((GeoLine)geo) && ((GeoRay)geo).getStartPoint().isEqual(getStartPoint());

	}
	
}
