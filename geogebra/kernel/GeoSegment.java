/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;

import java.util.HashSet;

/**
 * @author Markus Hohenwarter
 */
final public class GeoSegment extends GeoLine implements LimitedPath, NumberValue, LineProperties,
GeoSegmentInterface {

	private static final long serialVersionUID = 1L;
	// GeoSegment is constructed by AlgoJoinPointsSegment 
	//private GeoPoint A, B;
	private double length;
	private boolean defined;
	private boolean allowOutlyingIntersections = false;
	private boolean keepTypeOnGeometricTransform = true; // for mirroring, rotation, ...
	
	public static final int SEGMENT_DECORATION_NONE = 0;
	public static final int SEGMENT_DECORATION_ONE_TICK = 1;
	public static final int SEGMENT_DECORATION_TWO_TICKS = 2;
	public static final int SEGMENT_DECORATION_THREE_TICKS = 3;
//	 Michael Borcherds 20071006 start
	public static final int SEGMENT_DECORATION_ONE_ARROW = 4;
	public static final int SEGMENT_DECORATION_TWO_ARROWS = 5;
	public static final int SEGMENT_DECORATION_THREE_ARROWS = 6;
//	 Michael Borcherds 20071006 end
	
	// added by Lo�c
	public static final Integer[] getDecoTypes() {
		Integer[] ret = { new Integer(SEGMENT_DECORATION_NONE),
				new Integer(SEGMENT_DECORATION_ONE_TICK),
				new Integer(SEGMENT_DECORATION_TWO_TICKS),
				new Integer(SEGMENT_DECORATION_THREE_TICKS),
// Michael Borcherds 20071006 start
				new Integer(SEGMENT_DECORATION_ONE_ARROW),
				new Integer(SEGMENT_DECORATION_TWO_ARROWS),
				new Integer(SEGMENT_DECORATION_THREE_ARROWS)
// Michael Borcherds 20071006 end
				};
		return ret;
	}
	//end		
	
//	 Michael Borcherds 2007-11-20
	public void setDecorationType(int type) {
		if (type>=getDecoTypes().length || type<0)
			decorationType=DECORATION_NONE;
		else
			decorationType = type;
	}
//	 Michael Borcherds 2007-11-20

	public GeoSegment(Construction c, GeoPoint A, GeoPoint B) {
		super(c);		
		setStartPoint(A);
		setEndPoint(B);
	}
	
	protected String getClassName() {	
		return "GeoSegment";
 	}
	
	 protected String getTypeString() {
		return "Segment";
	}
	 
	public int getGeoClassType() {
		return GEO_CLASS_SEGMENT;
	}

	/**
	 * the copy of a segment is a number (!) with
	 * its value set to the segments current length
	 *
	public GeoElement copy() {
		return new GeoNumeric(cons, getLength());   		 
	}   */     
	 
	public GeoElement copyInternal(Construction cons) {
		GeoSegment seg = new GeoSegment(cons, 
										(GeoPoint) startPoint.copyInternal(cons), 
										(GeoPoint) endPoint.copyInternal(cons));
		seg.set(this);
		return seg;
	}		
	
	public void set(GeoElement geo) {
		super.set(geo);		
		if (!geo.isGeoSegment()) return;
		
		GeoSegment seg = (GeoSegment) geo;				
        length = seg.length;
        defined = seg.defined;      
        keepTypeOnGeometricTransform = seg.keepTypeOnGeometricTransform; 	
    	    	     		   
    	startPoint.set(seg.startPoint);
    	endPoint.set(seg.endPoint);    	
	}   

	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);
		
		if (geo.isGeoSegment()) { 
			GeoSegment seg = (GeoSegment) geo;
			allowOutlyingIntersections = seg.allowOutlyingIntersections;						
		}
	}
	
	/** 
	 * Calculates this segment's length . This method should only be called by
	 * its parent algorithm of type AlgoJoinPointsSegment
	 */
	public void calcLength() {			
		defined = startPoint.isFinite() && endPoint.isFinite();
		if (defined) length = startPoint.distance(endPoint);
		else length = Double.NaN;	
	}
	
	public double getLength() {
		return length;
	}
	
	/*
	 * overwrite GeoLine methods
	 */
	public boolean isDefined() {
		return defined;
	}	
	
	public void setUndefined() {
		super.setUndefined();
		defined = false;
	}
        
   public final boolean showInAlgebraView() {	   
	  // return defined;
	   return true;
   }
   
   protected boolean showInEuclidianView() {
	   return defined;
   }
   
   
   
   
   
   /** 
	* Yields true iff startpoint and endpoint of s are equal to
	* startpoint and endpoint of this segment.
	*/
	// Michael Borcherds 2008-05-01
   final public boolean isEqual(GeoElement geo) {      
	   if (!geo.isGeoSegment()) return false;
	   GeoSegment s = (GeoSegment)geo;
	   return startPoint.isEqual(s.startPoint) && endPoint.isEqual(s.endPoint);             	                  
   }
	
   final public String toString() {
		sbToString.setLength(0);      
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(kernel.format(length));
	   return sbToString.toString();
   }      
   private StringBuffer sbToString = new StringBuffer(30);
   
   final public String toValueString() {
	   return kernel.format(length);
   }
   
	 /**
     * interface NumberValue
     */    
    public MyDouble getNumber() {    	
        return new MyDouble(kernel,  getLength() );
    }     
    
    final public double getDouble() {
        return getLength();
    }
        
    final public boolean isConstant() {
        return false;
    }
    
    final public boolean isLeaf() {
        return true;
    }
    
    final public HashSet getVariables() {
        HashSet varset = new HashSet();        
        varset.add(this);        
        return varset;          
    }                   
    
    final public ExpressionValue evaluate() { return this; }    

	public boolean isNumberValue() {
		return true;
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
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
    
    
	/* 
	 * GeoSegmentInterface interface
	 */	 
     
    public GeoElement getStartPointAsGeoElement(){
    	return getStartPoint();
    }

    public GeoElement getEndPointAsGeoElement(){
    	return getEndPoint();
    }

    
	public double getPointX(double parameter){
		return startPoint.inhomX + parameter * y;
	}
	
	public double getPointY(double parameter){
		return startPoint.inhomY - parameter * x;
	}

	
	/* 
	 * Path interface
	 */	     	
    public void pointChanged(GeoPointInterface PI) {
		super.pointChanged(PI);
		
		GeoPoint P = (GeoPoint) PI;
			
		// ensure that the point doesn't get outside the segment
		// i.e. ensure 0 <= t <= 1 
		PathParameter pp = P.getPathParameter();
		if (pp.t < 0.0) {
			P.x = startPoint.x;
			P.y = startPoint.y;
			P.z = startPoint.z; 
			pp.t = 0.0;
		} else if  (pp.t > 1.0) {
			P.x = endPoint.x;
			P.y = endPoint.y;
			P.z = endPoint.z; 
			pp.t = 1.0;
		}
	}

	public void pathChanged(GeoPointInterface PI) {
		
		GeoPoint P = (GeoPoint) PI;
		
		PathParameter pp = P.getPathParameter();
		if (pp.t < 0.0) {
			pp.t = 0;
		} 
		else if (pp.t > 1.0) {
			pp.t = 1;
		}
		
		// calc point for given parameter
		P.x = startPoint.inhomX + pp.t * y;
		P.y = startPoint.inhomY - pp.t * x;
		P.z = 1.0;		
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
		return 1;
	}
	
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}	
	
	/**
     * returns all class-specific xml tags for saveXML
     */
    protected String getXMLtags() {
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
	 * creates new transformed segment
	 */
    public GeoElement [] createTransformedObject(int type, String label, GeoPoint Q, 
			GeoLine l, GeoVector vec, NumberValue n) {	

		if (keepTypeOnGeometricTransform) {			
			// mirror endpoints
			GeoPoint [] points = {getStartPoint(), getEndPoint()};
			points = kernel.transformPoints(type, points, Q, l, vec, n);	
			// create SEGMENT
			GeoElement [] geos = {kernel.Segment(label, points[0], points[1]), points[0], points[1]};	
			return geos;	
		} 
		else {
			//	create LINE
			GeoLine transformedLine = kernel.getTransformedLine(type, this, Q, l, vec, n);
			transformedLine.setLabel(label);
			GeoElement [] geos = {transformedLine};
			return geos;
		}							
	}
	
	public boolean isGeoSegment() {
		return true;
	}	
	
    public void setZero() {
    	setCoords(0, 1, 0);
    }
	
}
