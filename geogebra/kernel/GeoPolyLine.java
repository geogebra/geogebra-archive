/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.util.HashSet;

/**
 * Polygon through given points
 * 
 * @author Michael Borcherds, adapted from GeoPolygon
 */
public class GeoPolyLine extends GeoElement implements NumberValue, LineProperties {
	
	private static final long serialVersionUID = 1L;

	public static final int POLYLINE_MAX_POINTS = 500;
	
	protected GeoPointInterface [] points;
	
	protected double length;
	private boolean defined = false;		
	
	/** common constructor for 2D.
	 * @param c the construction
	 * @param points vertices 
	 */
	public GeoPolyLine(Construction cons, String label, GeoPointInterface[] points) {
		super(cons);
		this.points = points;
		setLabel(label);
	}
	

	public GeoPolyLine(Construction cons, GeoPointInterface[] points) {
		super(cons);
		this.points = points;
	}


	public String getClassName() {
		return "GeoPolyLine";
	}
	
    protected String getTypeString() {
    		return "PolyLine";
	    			
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_POLYLINE;
    }
	
    
	/** return number for points
	 * @return number for points
	 */
	public int getNumPoints(){
		return points.length;
	}
	
	/**
	 * The copy of a polygon is a number (!) with
	 * its value set to the polygons current area
	 */      
	public GeoElement copy() {
		return new GeoNumeric(cons, getLength());        
	}    
	
	public GeoElement copyInternal(Construction cons) {						
		GeoPolyLine ret = new GeoPolyLine(cons, null); 
		ret.points = GeoElement.copyPoints(cons, (GeoPoint[]) points);		
		ret.set(this);
				
		return ret;		
	} 		
	
	public void set(GeoElement geo) {
		GeoPolyLine poly = (GeoPolyLine) geo;		
		length = poly.length;
		defined = poly.defined;	
		
		// make sure both arrays have same size
		if (points.length != poly.points.length) {
			GeoPointInterface [] tempPoints = new GeoPointInterface[poly.points.length];
			for (int i=0; i < tempPoints.length; i++) {
				tempPoints[i] = i < points.length ? points[i] : new GeoPoint(cons);
			}
			points = tempPoints;
		}
		
		for (int i=0; i < points.length; i++) {				
			((GeoPoint) points[i]).set(poly.points[i]);
		}	
	}
	
	

	public boolean isFillable() {
		return false;
	}
	

	/*
	 * overwrite methods
	 */
	public boolean isDefined() {
		return defined;
   }	
   
   public void setDefined() {
   		defined = true;
   }
   
   public void setUndefined() {
		   defined = false;
	}
        
   public final boolean showInAlgebraView() {	   
	   //return defined;
	   return true;
   }
   
   
   /** 
	* Yields true if the area of this polygon is equal to the
	* area of polygon p.
	*/
	// Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		return false;
	}

	
   final public String toString() {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(kernel.format( getLength() ));
	    return sbToString.toString();
   }      
   private StringBuilder sbToString = new StringBuilder(50);
   
   final public String toValueString() {
	   return kernel.format(getLength());
   }

	 /**
     * interface NumberValue
     */    
    public MyDouble getNumber() {    	
        return new MyDouble(kernel,  getLength() );
    }     
    
    final public double getLength() {
        return 3.3333333333;
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

	protected boolean showInEuclidianView() {
		return true;//defined;
	}    
	
	public boolean isNumberValue() {
		return true;
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}   
	
	public boolean isTextValue() {
		return false;
	}   
	
	public boolean isGeoPolygon() {
		return false;
	}
	
	/*
	 * Path interface implementation
	 *
	
	public boolean isPath() {
		return true;
	}

	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	public double getMaxParameter() {
		return points.length - 1;
	}

	public double getMinParameter() {		
		return 0;
	}

	public boolean isClosedPath() {
		return false;
	}

	public boolean isOnPath(GeoPointInterface PI, double eps) {
		
		GeoPoint P = (GeoPoint) PI;
		
		if (P.getPath() == this)
			return true;
		
		// check if P is on one of the segments
		for (int i=0; i < segments.length; i++) {
			if (segments[i].isOnPath(P, eps))
				return true;
		}				
		return false;
	}

	public void pathChanged(GeoPointInterface PI) {		
		
		GeoPoint P = (GeoPoint) PI;
		
		// parameter is between 0 and segment.length,
		// i.e. floor(parameter) gives the segment index
		
		PathParameter pp = P.getPathParameter();
		pp.t = pp.t % segments.length;
		if (pp.t < 0) 
			pp.t += segments.length;
		int index = (int) Math.floor(pp.t) ;		
		GeoSegmentInterface seg = segments[index];
		double segParameter = pp.t - index;
		
		// calc point for given parameter
		P.x = seg.getPointX(segParameter);
		P.y = seg.getPointY(segParameter);
		P.z = 1.0;	
	}

	public void pointChanged(GeoPointInterface PI) {
		
		GeoPoint P = (GeoPoint) PI;
		
		double qx = P.x/P.z;
		double qy = P.y/P.z;
		double minDist = Double.POSITIVE_INFINITY;
		double resx=0, resy=0, resz=0, param=0;
		
		// find closest point on each segment
		PathParameter pp = P.getPathParameter();
		for (int i=0; i < segments.length; i++) {
			P.x = qx;
			P.y = qy;
			P.z = 1;
			segments[i].pointChanged(P);
			
			double x = P.x/P.z - qx; 
			double y = P.y/P.z - qy;
			double dist = x*x + y*y;			
			if (dist < minDist) {
				minDist = dist;
				// remember closest point
				resx = P.x;
				resy = P.y;
				resz = P.z;
				param = i + pp.t;
			}
		}				
			
		P.x = resx;
		P.y = resy;
		P.z = resz;
		pp.t = param;	
	}	 */
	
	
	
	


	

	


	
	
	/**
	 * returns all class-specific xml tags for getXML
	 * GeoGebra File Format
	 */
	protected void getXMLtags(StringBuilder sb) {
		getLineStyleXML(sb);
		getXMLvisualTags(sb);
		getXMLanimationTags(sb);
		getXMLfixedTag(sb);
		getAuxiliaryXML(sb);
		getBreakpointXML(sb);		
	}
	
	
	

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}


	public GeoPoint[] getPoints() {
		return (GeoPoint[]) points;
	}

	public void calcLength() {
		Application.debug("TODO: calcLength()");
	}


	public void setPoints(GeoPointInterface[] points) {
		this.points = points;
		
	}


	
	

}
