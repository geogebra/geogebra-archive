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
 * Polygon through given points
 * 
 * @author Markus Hohenwarter
 */
final public class GeoPolygon extends GeoElement implements NumberValue, Path {
	
	private static final long serialVersionUID = 1L;

	public static final int POLYGON_MAX_POINTS = 100;
	
	private GeoPoint [] points;
	private GeoSegment [] segments;
	
	private double area;
	private boolean defined = false;		
	
	public GeoPolygon(Construction c, GeoPoint [] points) {
		super(c);
		setPoints(points);
		setLabelVisible(false);
		setAlphaValue(ConstructionDefaults.DEFAULT_POLYGON_ALPHA);
	}
	
	String getClassName() {
		return "GeoPolygon";
	}
	
    String getTypeString() {
    	if (points == null) 
    		return "Polygon";
    	
    	switch (points.length) {
    		case 3:
    			return "Triangle";
    			
    		case 4:
    			return "Quadrilateral";
    			
    		case 5:
    			return "Pentagon";
    		
    		case 6:
    			return "Hexagon";
    		
    		default:
    			return "Polygon";    	
    	}    	    			
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_POLYGON;
    }
	
    public void setPoints(GeoPoint [] points) {
		this.points = points;
		updateSegments();
		
//		if (points != null) {
//		System.out.println("*** " + this + " *****************");
//        System.out.println("POINTS: " + points.length);
//        for (int i=0; i < points.length; i++) {
//			System.out.println(" " + i + ": " + points[i]);		     	        	     	
//		}   
//        System.out.println("SEGMENTS: " + segments.length);
//        for (int i=0; i < segments.length; i++) {
//			System.out.println(" " + i + ": " + segments[i]);		     	        	     	
//		}  
//        System.out.println("********************");
//		}
	}        

    /**
     * Inits the labels of this polygon, its segments and its points.
     * labels[0] for polygon itself, labels[1..n] for segments,
     * labels[n+1..2n-2] for points (only used for regular polygon)
     * @param labels
     */
    public void initLabels(String [] labels) {     	
    	// System.out.println("INIT LABELS");
    	
    	// label polygon
    	if (labels == null || labels.length == 0) {    		
        	// System.out.println("no labels given");
        	
             setLabel(null);
             if (segments != null) {
            	 defaultSegmentLabels();
             }
             return;
    	}
    	
    	// label polygon              
        // first label for polygon itself
        setLabel(labels[0]);        
    	
    	// label segments and points
    	if (points != null && segments != null) {
    		
    		// additional labels for the polygon's segments
    		// poly + segments + points - 2 for AlgoPolygonRegular
    		if (labels.length == 1 + segments.length + points.length - 2) {
    			//System.out.println("labels for segments and points");
    			
	            int i=1;
    			for (int k=0; k < segments.length; k++, i++) {
	                segments[k].setLabel(labels[i]);
	            }		            
    			for (int k=2; k < points.length; k++, i++) {
	                points[k].setLabel(labels[i]);
	            }
	        } 
    		    		
    		// additional labels for the polygon's segments
    		// poly + segments for AlgoPolygon
    		else if (labels.length == 1 + segments.length) {
    			//System.out.println("labels for segments");
    			
            	int i=1;
    			for (int k=0; k < segments.length; k++, i++) {
	                segments[k].setLabel(labels[i]);
	            }		            	           	            
	        } 
    		
	        else { 
	        	System.out.println("label for polygon (autoset segment labels)");     	
	        	defaultSegmentLabels();
	        }
    	}    	        
    }
    
    private void defaultSegmentLabels() {
    	//  no labels for segments specified
        //  set labels of segments according to point names
        if (points.length == 3) {          
           setLabel(segments[0], points[2]);
           setLabel(segments[1], points[0]);
           setLabel(segments[2], points[1]); 
        } else {
           for (int i=0; i < points.length; i++) {
               setLabel(segments[i], points[i]);
           }
        }
    }
    
    private void setLabel(GeoSegment s, GeoPoint p) {
        if (!p.isLabelSet() || p.getLabel() == null) 
        	s.setLabel(null);
        else 
        	s.setLabel(p.getLabel().toLowerCase());
    }
	
    /**
     * Updates all segments of this polygon for its point array.
     * Note that the point array may be changed: this method makes
     * sure that segments are reused if possible.
     */
	 private void updateSegments() {  	
		 if (points == null) return;
		 
		GeoSegment [] oldSegments = segments;				
		segments = new GeoSegment[points.length]; // new segments
				
		if (oldSegments != null) {
			// reuse or remove old segments
			for (int i=0; i < oldSegments.length; i++) {        	
	        	if (i < segments.length &&
	        		oldSegments[i].getStartPoint() == points[i] && 
	        		oldSegments[i].getEndPoint() == points[(i+1) % points.length]) 
	        	{
        			// reuse old segment
        			segments[i] = oldSegments[i];          		
        		} 
	        	else {
        			// remove old segment
        			((AlgoJoinPointsSegment) oldSegments[i].getParentAlgorithm()).removeSegmentOnly();	        		
	        	}	        	
			}
		}			
		
		// create missing segments
        for (int i=0; i < segments.length; i++) {
        	GeoPoint startPoint = points[i];
        	GeoPoint endPoint = points[(i+1) % points.length];
        	
        	if (segments[i] == null) {
        		AlgoJoinPointsSegment algoSegment = new AlgoJoinPointsSegment(cons, startPoint, endPoint, this);            
                cons.removeFromConstructionList(algoSegment);                       
                segments[i] = algoSegment.getSegment(); 
                // refresh color to ensure segments have same color as polygon:
                segments[i].setObjColor(getObjectColor()); 
        	}     
        }                            
    }

	/**
	 * The copy of a polygon is a number (!) with
	 * its value set to the polygons current area
	 */      
	public GeoElement copy() {
		return new GeoNumeric(cons, getArea());        
	}    
	
	public GeoElement copyInternal(Construction cons) {						
		GeoPolygon ret = new GeoPolygon(cons, null); 
		ret.points = GeoElement.copyPoints(cons, points);		
		ret.set(this);
				
		return ret;		
	} 		
	
	public void set(GeoElement geo) {
		GeoPolygon poly = (GeoPolygon) geo;		
		area = poly.area;
		defined = poly.defined;	
		
		for (int i=0; i < points.length; i++) {				
			points[i].set(poly.points[i]);
		}	
		updateSegments();
	}

	/**
	 * Returns the points of this polygon.
	 * Note that this array may change dynamically.
	 */
	public GeoPoint [] getPoints() {
		return points;
	}
	
	/**
	 * Returns the segments of this polygon.
	 * Note that this array may change dynamically.
	 */
	public GeoSegment [] getSegments() {
		return segments;
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
		if (P == null || P.length < 3)
			return Double.NaN;
		
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
	   //return defined;
	   return true;
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
	
	/*
	 * Path interface implementation
	 */
	
	public boolean isPath() {
		return true;
	}

	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	public double getMaxParameter() {
		return segments.length;
	}

	public double getMinParameter() {		
		return 0;
	}

	public boolean isClosedPath() {
		return true;
	}

	public boolean isOnPath(GeoPoint P, double eps) {
		if (P.getPath() == this)
			return true;
		
		// check if P is on one of the segments
		for (int i=0; i < segments.length; i++) {
			if (segments[i].isOnPath(P, eps))
				return true;
		}				
		return false;
	}

	public void pathChanged(GeoPoint P) {		
		// parameter is between 0 and segment.length,
		// i.e. floor(parameter) gives the segment index
		
		int index = (int) Math.floor(P.pathParameter.t);
		if (index >= segments.length) 
			index = segments.length - 1;
		GeoSegment seg = segments[index];
		double segParameter = P.pathParameter.t - index;
		
		// calc point for given parameter
		P.x = seg.startPoint.inhomX + segParameter * seg.y;
		P.y = seg.startPoint.inhomY - segParameter * seg.x;
		P.z = 1.0;	
	}

	public void pointChanged(GeoPoint P) {
		double qx = P.x/P.z;
		double qy = P.y/P.z;
		double minDist = Double.POSITIVE_INFINITY;
		double resx=0, resy=0, resz=0, param=0;
		
		// find closest point on each segment
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
				param = i + P.pathParameter.t;
			}
		}				
			
		P.x = resx;
		P.y = resy;
		P.z = resz;
		P.pathParameter.t = param;	
	}	

}
