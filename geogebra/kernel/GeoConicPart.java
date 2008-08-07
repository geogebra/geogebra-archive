/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/* 
 * Created on 03.12.2004
 */
package geogebra.kernel;

import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.integration.EllipticArcLength;


/**
 * GeoCirclePart for 
 * @author Markus Hohenwarter
 * 
 */
public class GeoConicPart extends GeoConic
implements LimitedPath, NumberValue {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int CONIC_PART_ARC = 1;
	public static final int CONIC_PART_SECTOR = 2;
		
	// parameters (e.g. angles) for arc
	private	double paramStart, paramEnd, paramExtent;
	private boolean posOrientation;
	private int conic_part_type;
	
	private double value;
	private boolean value_defined;
	
	private EllipticArcLength ellipticArcLength;
	private boolean allowOutlyingIntersections = false;
	private boolean keepTypeOnGeometricTransform = true;
			
	// GeoCirclePart is constructed by AlgoCirclePart... 
	public GeoConicPart(Construction c, int type) {
		super(c);				
		conic_part_type = type;			
	}
	
	protected String getClassName() {	
		return "GeoConicPart";
 	}
	
    public int getGeoClassType() {
    	return GEO_CLASS_CONICPART;
    }
	
	protected String getTypeString() {      
		switch (conic_part_type) {
			case CONIC_PART_ARC: 
				return "Arc";
			
			case CONIC_PART_SECTOR: 
				return "Sector";
			
			default:
				return super.getTypeString();
		}                       
	}  
	
	public GeoElement copyInternal(Construction cons) {
		GeoConicPart ret = new GeoConicPart(cons, conic_part_type);
		ret.set(this);
		return ret;
	}
	
	public void set(GeoElement geo) {		
		super.set(geo);
		if (!geo.isGeoConicPart()) return;
		
		GeoConicPart cp = (GeoConicPart) geo;				

		// class specific attributes
		paramStart = cp.paramStart;
		paramEnd = cp.paramEnd;
		paramExtent = cp.paramExtent;
		posOrientation = cp.posOrientation;
		conic_part_type = cp.conic_part_type;
		
		value = cp.value;
		value_defined = cp.value_defined;	
		
		keepTypeOnGeometricTransform = cp.keepTypeOnGeometricTransform;		
	}
	
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);
		
		if (geo.isGeoConicPart()) { 
			GeoConicPart cp = (GeoConicPart) geo;
			allowOutlyingIntersections = cp.allowOutlyingIntersections;
		}
	}
	
	final public int getConicPartType() {
		return conic_part_type;
	}
	
	final public double getParameterStart() {
		return paramStart;
	}
	
	final public double getParameterEnd() {
		return paramEnd;
	}
	
	final public double getParameterExtent() {
		return paramExtent;
	}
	
	final public boolean positiveOrientation() {
		return posOrientation;
	}	
	
	/**
	 * Returns wheter c is equal to this conic part
	 */
	// Michael Borcherds 2008-05-01
	final public boolean isEqual(GeoElement geo) {
		
		if (!geo.isGeoConicPart()) return false;
		
		GeoConicPart c = (GeoConicPart)geo;
		
		return 
			posOrientation == c.posOrientation &&
			conic_part_type == c.conic_part_type &&		
			kernel.isEqual(paramStart, c.paramStart) &&
			kernel.isEqual(paramEnd, c.paramEnd) &&
			super.isEqual(c);			 
	}
	
	/** 
	 * Sets parameters and calculates this object's value.
	 * For type CONIC_PART_ARC the value is
	 * the length, for CONIC_PART_SECTOR the value is an area. 
	 * This method should only be called by the parent algorithm 
	 */
	final public void setParameters(double a, double b, boolean positiveOrientation) {
		value_defined = super.isDefined();
		if (!value_defined) {
			value = Double.NaN;	
			return;
		}
		
		posOrientation = positiveOrientation;
		if (!posOrientation) {
			// internally we always use positive orientation, i.e. a <= b
			// the orientation flag is important for points on this path (see pathChanged())
			double temp = a;
			a = b;
			b = temp;
		}
		
		// handle conic types
		switch (type) {
			case GeoConic.CONIC_CIRCLE:
				paramStart = kernel.convertToAngleValue(a);
				paramEnd = kernel.convertToAngleValue(b);		
				paramExtent = paramEnd - paramStart;
				if (paramExtent < 0) paramExtent += Kernel.PI_2;
				
				double r = halfAxes[0];
				if (conic_part_type == CONIC_PART_ARC) {
					value = r * paramExtent; // length
				} else {
					value = r*r * paramExtent / 2.0; // area		
				}
				value_defined = !Double.isNaN(value) &&
								!Double.isInfinite(value);
				break;
			
			case GeoConic.CONIC_ELLIPSE:					
				paramStart = kernel.convertToAngleValue(a);
				paramEnd = kernel.convertToAngleValue(b);		
				paramExtent = paramEnd - paramStart;
				if (paramExtent < 0) paramExtent += Kernel.PI_2;
				
				if (ellipticArcLength == null)
					ellipticArcLength = new EllipticArcLength(this);
				
				if (conic_part_type == CONIC_PART_ARC) {
					// length
					value = ellipticArcLength.compute(paramStart, paramEnd);
				} else {
					// area
					value = halfAxes[0] * halfAxes[1] * paramExtent / 2.0; 		
				}
				value_defined = !Double.isNaN(value) &&
								!Double.isInfinite(value);
				
				break;
				
			// a circular arc through 3 points may degenerate
			// to a segment or two rays
			case GeoConic.CONIC_PARALLEL_LINES:
				if (conic_part_type == CONIC_PART_ARC && posOrientation) {
				    // length of segment 
					// bugfix Michael Borcherds 2008-05-27
					GeoPoint startPoint=lines[0].getStartPoint();
					GeoPoint endPoint=lines[0].getEndPoint();
				    if (startPoint!=null && endPoint!=null)
				    {
				    	value = startPoint.distance(endPoint);
				    }
				    else 
				    {
				    	value = Double.POSITIVE_INFINITY; 
						value_defined = false;
					    break;
				    }
//				  bugfix end

				} else { //sector or two rays
					value = Double.POSITIVE_INFINITY; // area or length of rays	
				}
				value_defined = true;
			    break;
			
			default:
				value_defined = false;
				//Application.debug("GeoConicPart: unsupported conic part for conic type: " + type);
		}		
			
	}
	
	final public boolean isDefined() {
		return value_defined;
	}		
	
	public void setUndefined() {
		value_defined = false;
	}
	
	final public double getValue() {
		return value;
	}
	
    final public String toString() {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(toValueString());			     
        return sbToString.toString();
    }
	private StringBuffer sbToString = new StringBuffer(50);
	
	final public String toValueString() {
		return kernel.format(value);	
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
			return isOnFullConic(p, eps);
		else
			return isOnPath(p, eps);
    }
	
	/** 
	 * states wheter P lies on this conic part or not 
	 */
	public boolean isOnPath(GeoPoint P, double eps) {
		if (P.getPath() == this)
			return true;
		
		// check if P lies on conic first
    	if (!isOnFullConic(P, eps))
    		return false;
    	
		// idea: calculate path parameter and check
		//       if it is in [0, 1]
		
		// remember the old values
		double px = P.x, py = P.y, pz = P.z;
		PathParameter tempPP = getTempPathParameter();
		PathParameter pPP = P.getPathParameter();
		tempPP.set(pPP);
		
		switch (type) {
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
				setEllipseParameter(P);
			break;
			
			// degenerate case: two rays or one segment
			case CONIC_PARALLEL_LINES: 		
				if (posOrientation) {
					// segment
					lines[0].pointChanged(P);
				} else {
					// two rays: no point should lie on them
					P.getPathParameter().t = -1;
				}
			break;

			default:
				pPP.t = -1;
				//Application.debug("GeoConicPart.isIncident: unsupported conic part for conic type: " + type);
		}					
		
		boolean result = 	pPP.t >= -eps && 
							pPP.t <= 1 + eps;
		
		// restore old values
		P.x = px; P.y = py; P.z = pz;
		pPP.set(tempPP);
		
		return result;
	}
	
	private PathParameter tempPP;
	private PathParameter getTempPathParameter() {
		if (tempPP == null)
			tempPP = new PathParameter();
		return tempPP;
	}
	
	/*
	 * Path Interface implementation
	 */
	
	public boolean isClosedPath() {
		return false;
	}
	
	public void pointChanged(GeoPoint P) {
		PathParameter pp = P.getPathParameter(); 
		pp.pathType = type;
		
		switch (type) {
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
				setEllipseParameter(P);
				clipEllipseParameter(P);		
			break;
			
			// degenerate case: two rays or one segment
			case CONIC_PARALLEL_LINES: 		
				if (posOrientation) {
					// segment
					lines[0].pointChanged(P);
					
					// make sure we don't get outside [0,1]
					if (pp.t < 0) {
						pp.t = 0;
						pathChanged(P);
					}
					else if (pp.t > 1) {
						pp.t = 1;
						pathChanged(P);
					}
				} else {
					// two rays
					// we take point at infinty
					P.x = -lines[0].y;
					P.y = lines[0].x;
					P.z = 0.0;
				}
			break;

			default:
				pp.t = Double.NaN;
				//Application.debug("GeoConicPart.pointChanged(): unsupported conic part for conic type: " + type);
		}		
	}
	
	private void setEllipseParameter(GeoPoint P) {
		// let GeoConic do the work
		super.pointChanged(P);		
		
		// now transform parameter t from [paramStart, paramEnd] to [0, 1]
		PathParameter pp = P.getPathParameter();
		if (pp.t < 0)
			pp.t += Kernel.PI_2;
		double t = pp.t - paramStart;
		if (t < 0) 
			t += Kernel.PI_2;
		pp.t = t / paramExtent;	
	}
	
	private void clipEllipseParameter(GeoPoint P) {
		// make sure we don't get outside [0,1]
		// the values of the path parameter are now
		// between [0, 2pi/paramExtent]
		// [0, 1] is ok.
		// handle [1, 2pi/paramExtent]:
		// take 0 for parameter > (1 + 2pi/paramExtent)/2
		// else take 1
		PathParameter pp = P.getPathParameter();
		if (pp.t > 0.5 + Math.PI/paramExtent) {
			if (posOrientation)
				pp.t = 0;
			else
				pp.t = 1;
			pathChanged(P);
		}
		else if (pp.t > 1) {
			if (posOrientation)
				pp.t = 1;
			else
				pp.t = 0;
			pathChanged(P);
		}
		else if (!posOrientation) {
			pp.t = 1.0 - pp.t;
		}	
	}
	
	public void pathChanged(GeoPoint P) {	
		PathParameter pp = P.getPathParameter();
		if (pp.t < 0.0) {
			pp.t = 0;
		} 
		else if (pp.t > 1.0) {
			pp.t = 1;
		}
		
		// handle conic types	
		switch (type) {
			case CONIC_CIRCLE:	
			case CONIC_ELLIPSE:	
				// if type of path changed (other conic) then we
				// have to recalc the parameter with pointChanged()
				if (pp.pathType != type) {					
					pointChanged(P);
					return;
				}		
				
				// calc Point on conic using this parameter (in eigenvector space)
				double t = posOrientation ?
						pp.t:
						1.0 - pp.t;
				double angle = paramStart + t * paramExtent;
				P.x = halfAxes[0] * Math.cos(angle);	
				P.y = halfAxes[1] * Math.sin(angle);
				P.z = 1.0;
				
				//	transform to real world coord system
				coordsEVtoRW(P);								
			break;	
			
			case CONIC_PARALLEL_LINES:
				if (posOrientation) { // segment
					// if type of path changed (other conic) then we
					// have to recalc the parameter with pointChanged()
					if (pp.pathType != type) {					
						pointChanged(P);						
					}	else {
						lines[0].pathChanged(P);
					}
				} else {
					// two rays
					// we take point at infinty
					P.x = -lines[0].y;
					P.y = lines[0].x;
					P.z = 0.0;
				}
			break;

			default:
				//Application.debug("GeoConicPart.pathChanged(): unsupported conic part for conic type: " + type);
		}
	}
	
	/**
	 * Returns the smallest possible parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * @return
	 */
	public double getMinParameter() {
		switch (type) {
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
				return 0;		
		
		// degenerate case: two rays or one segment
			case CONIC_PARALLEL_LINES: 		
				if (posOrientation)
					// segment
					return 0;
				else
					// two rays					
					return Double.NEGATIVE_INFINITY;			
	
			default:
				return Double.NaN;
		}
	}
	
	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * @return
	 */
	public double getMaxParameter() {
		switch (type) {
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
				return 1;		
		
		// degenerate case: two rays or one segment
			case CONIC_PARALLEL_LINES: 		
				if (posOrientation)
					// segment
					return 1;
				else
					// two rays					
					return Double.POSITIVE_INFINITY;			
	
			default:
				return Double.NaN;
		}
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
        sb.append("\"/>");
        
        // keepTypeOnGeometricTransform
        sb.append("\t<keepTypeOnTransform val=\"");
        sb.append(keepTypeOnGeometricTransform);
        sb.append("\"/>");
        
        return sb.toString();   
    }

	 /**
     * interface NumberValue
     */    
    public MyDouble getNumber() {    	
        return new MyDouble(kernel,  getValue() );
    }     
    
    final public double getDouble() {
        return getValue();
    }
    
	public boolean isNumberValue() {
		return true;
	}
	
	public boolean isGeoConicPart() {
		return true;
	}

	
	public GeoElement [] createTransformedObject(int type, String label, GeoPoint Q, 
			GeoLine l, GeoVector vec, NumberValue n) {	

		AlgoElement algoParent = keepTypeOnGeometricTransform ?
				getParentAlgorithm() : null;			
		
		// CREATE CONIC PART
		if (algoParent instanceof AlgoConicPartCircle) {	
			//	transform points
			AlgoConicPartCircle algo = (AlgoConicPartCircle) algoParent;
			GeoPoint [] points = { algo.getCenter(), algo.getStartPoint(), algo.getEndPoint() };
			
		    // create circle with center through startPoint        
	        AlgoCircleTwoPoints algoCircle = new AlgoCircleTwoPoints(cons, points[0], points[1]);
	        cons.removeFromConstructionList(algoCircle);
	        GeoConic circle = algoCircle.getCircle();
	        
	        // transform points and circle
			points = kernel.transformPoints(type, points, Q, l, vec, n);			
			GeoConic transformedCircle = kernel.getTransformedConic(type,  circle, Q, l, vec, n);	
			cons.removeFromConstructionList(transformedCircle.getParentAlgorithm());			
										
			// create a new arc from the transformed circle using startPoint and endPoint
			AlgoConicPartConicPoints algoResult = new AlgoConicPartConicPoints(cons, label, transformedCircle, points[1], points[2], conic_part_type);			
			GeoElement [] geos = {algoResult.getConicPart(), points[0], points[1], points[2]};
			return geos;					
		}
		
		else if (algoParent instanceof AlgoConicPartCircumcircle) {
			GeoPoint [] points ={ (GeoPoint) algoParent.input[0], 
					 (GeoPoint) algoParent.input[1],  (GeoPoint) algoParent.input[2]};			
			points = kernel.transformPoints(type, points, Q, l, vec, n);
			
			AlgoConicPartCircumcircle algo = new AlgoConicPartCircumcircle(cons, label, points[0], points[1], points[2], conic_part_type);
			GeoConicPart res = algo.getConicPart();
			res.setLabel(label);
			GeoElement [] geos = {res, points[0], points[1], points[2]};
			return geos;
		}
				
		else if (algoParent instanceof AlgoConicPartConicParameters) {
			AlgoConicPartConicParameters algo = (AlgoConicPartConicParameters) algoParent;			
						
			GeoConic transformedConic = kernel.getTransformedConic(type,  algo.conic, Q, l, vec, n);	
			cons.removeFromConstructionList(transformedConic.getParentAlgorithm());			
										
			algo = new AlgoConicPartConicParameters(cons, label, transformedConic, algo.startParam, algo.endParam, conic_part_type);			
			GeoElement [] geos = {algo.getConicPart()};
			return geos;
		}
		
		else if (algoParent instanceof AlgoConicPartConicPoints) {
			AlgoConicPartConicPoints algo = (AlgoConicPartConicPoints) algoParent;			
			GeoPoint [] points ={ algo.getStartPoint(), algo.getEndPoint() };			
			points = kernel.transformPoints(type, points, Q, l, vec, n);
						
			GeoConic transformedConic = kernel.getTransformedConic(type,  algo.getConic(), Q, l, vec, n);	
			cons.removeFromConstructionList(transformedConic.getParentAlgorithm());			
										
			algo = new AlgoConicPartConicPoints(cons, label, transformedConic, points[0], points[1], conic_part_type);			
			GeoElement [] geos = {algo.getConicPart(), points[0], points[1]};
			return geos;
		}
		
		else if (algoParent instanceof AlgoSemicircle) {			
			AlgoSemicircle algo = (AlgoSemicircle) algoParent;			
			GeoPoint [] points ={ algo.getA(), algo.getB() };			
			points = kernel.transformPoints(type, points, Q, l, vec, n);
			
			GeoConic semCirc;
			if (type == Kernel.TRANSFORM_MIRROR_AT_LINE) {
				semCirc = kernel.Semicircle(label, points[1], points[0]);
			} else {
				semCirc = kernel.Semicircle(label, points[0], points[1]);
			}
			
			GeoElement [] geos = {semCirc, points[0], points[1]};
			return geos;
		}
		
		else {
			//	create CONIC
			GeoConic transformedConic = kernel.getTransformedConic(type, this, Q, l, vec, n);
			transformedConic.setLabel(label);
			GeoElement [] ret = { transformedConic };
			return ret;
		}	
	}
	
}
