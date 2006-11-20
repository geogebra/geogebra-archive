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

import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;

import java.awt.Color;
import java.util.HashSet;

/**
 * @author Markus Hohenwarter
 */
final public class GeoPolygon extends GeoElement implements NumberValue {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int POLYGON_MAX_POINTS = 100;

	// GeoSegment is constructed by AlgoPolygon 
	private GeoPoint [] points;
	private GeoSegment [] segments;
	private double area;
	private boolean defined = false;		
	
	public GeoPolygon(Construction c, GeoPoint [] points) {
		super(c);
		this.points = points;
		setLabelVisible(false);
		setAlphaValue(ConstructionDefaults.DEFAULT_POLYGON_ALPHA);
	}
	
	String getClassName() {
		return "GeoPolygon";
	}
	
    String getTypeString() {
		return "Polygon";
	}
	
	
	public void setSegments(GeoSegment [] segments) {
		this.segments = segments;
	}

	/**
	 * The copy of a polygon is a number (!) with
	 * its value set to the polygons current area
	 */      
	public GeoElement copy() {
		return new GeoNumeric(cons, getArea());        
	}    
	
	public GeoElement copyInternal() {
		GeoPolygon ret = new GeoPolygon(cons, points); 
		ret.setInternal(this);
		return ret;		
	} 
	
	public void setInternal(GeoElement geo) {
		GeoPolygon poly = (GeoPolygon) geo;
		points = poly.points;
		segments = poly.segments;
		area = poly.area;
		defined = poly.defined;	
	}

	public GeoPoint [] getPoints() {
		return points;
	}

	public boolean isFillable() {
		return true;
	}
	
	/** 
	 * Calculates this polygon's area . This method should only be called by
	 * its parent algorithm of type AlgoPolygon
	 */
	public void calcArea() {
		area = calcArea(points);	
		defined = !(Double.isNaN(area) || Double.isInfinite(area));
	}
	
	public double getArea() {
		if (defined)
			return area;				        
		else 
			return Double.NaN;			        	
	}	

	/**
	 * Returns the area of a polygon given by points P
	 */	
	final static public double calcArea(GeoPoint [] P) {
	   int i = 0;   
	   for (; i < P.length; i++) {
		   if (P[i].isInfinite())
			return Double.NaN;
	   }
    
	   // area = 1/2 | det(P[i], P[i+1]) |
	   int last = P.length - 1;
	   double sum = 0;                     
	   for (i=0; i < last; i++) {
			sum += GeoPoint.det(P[i], P[i+1]);
	   }
	   sum += GeoPoint.det(P[last], P[0]);
	   return Math.abs(sum) / 2.0;      	
   }   
	
	/**
	 * Calculates the centroid of this polygon and writes
	 * the result to the given point.
	 */
	public void calcCentroid(GeoPoint centroid) {
		if (!defined) {
			centroid.setUndefined();
			return;
		}
	
		double xsum = 0;
		double ysum = 0;
		for (int i=0; i < points.length; i++) {
			xsum += points[i].inhomX;
			ysum += points[i].inhomY;
		}
		centroid.setCoords(xsum,
									 ysum,
									  points.length);
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
        
   final boolean showInAlgebraView() {	   
	   return defined;        
   }
   
   
   /** 
	* Yields true if the area of this polygon ist equal to the
	* area of polygon p.
	*/
   final public boolean equals(GeoPolygon p) {        
	   return kernel.isEqual(getArea(), p.getArea());                	                  
   }
   
   public void setEuclidianVisible(boolean visible) {
		super.setEuclidianVisible(visible);
		if (segments != null) {
			for (int i=0; i < segments.length; i++) {
				segments[i].setEuclidianVisible(visible);			
				segments[i].update();
			}
		}		
   }  

   public void setObjColor(Color color) {
   		super.setObjColor(color);
   		if (segments != null) {
   			for (int i=0; i < segments.length; i++) {
   				segments[i].setObjColor(color);
   				segments[i].update();
   			}
   		}	
   }
   
   public void setLineType(int type) {
		super.setLineType(type);
		if (segments != null) {
			for (int i=0; i < segments.length; i++) {
				segments[i].setLineType(type);	
				segments[i].update();
			}
		}	
   }
   
   public void setLineThickness(int th) {
		super.setLineThickness(th);
		if (segments != null) {
			for (int i=0; i < segments.length; i++) {
				segments[i].setLineThickness(th);
				segments[i].update();
			}
		}	
   }
	
   final public String toString() {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(kernel.format( getArea() ));
	    return sbToString.toString();
   }      
   private StringBuffer sbToString = new StringBuffer(50);
   
   final public String toValueString() {
	   return kernel.format(getArea());
   }
   
   public String typeString() {
		switch (points.length) {
			case 3: return "Triangle";
			case 4: return "Quadrangle";
			case 5: return "Pentagon";
			case 6: return "Hexagon";
			default: return "Polygon";	
		}
   }
	
	 /**
     * interface NumberValue
     */    
    public MyDouble getNumber() {    	
        return new MyDouble(kernel,  getArea() );
    }     
    
    final public double getDouble() {
        return getArea();
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

	public void set(GeoElement geo) {
		// dummy
	}

	public void setMode(int mode) {
		// dummy		
	}

	public int getMode() {
		// dummy
		return 0;
	}

	boolean showInEuclidianView() {
		return defined;
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
		return true;
	}

}
