/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoLine.java
 *
 * Created on 30. August 2001, 17:39
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

public class GeoLine extends GeoVec3D 
implements Path, 
Translateable,PointRotateable, Mirrorable, Dilateable {
	
	private static final long serialVersionUID = 1L;
	// modes
    public static final int EQUATION_IMPLICIT = 0;
    public static final int EQUATION_EXPLICIT = 1;
    public static final int PARAMETRIC = 2;		
    
    private String parameter = "\u03bb";	
    GeoPoint startPoint, endPoint;    
    
    //  enable negative sign of first coefficient in implicit equations
	private static boolean KEEP_LEADING_SIGN = true;
    
    // temp
    private double [] P = new double[2],                       			 
                      			 g = new double[3];	 
    private static final String [] vars = { "x", "y" };
    
    public GeoLine(Construction c) { 
    	super(c); 
    	setMode( GeoLine.EQUATION_IMPLICIT );
    }
    
    /** Creates new GeoPoint */     
    public GeoLine(Construction cons, String label, double a, double b, double c) {
        super(cons, a, b, c);	// GeoVec3D constructor                 
        setMode( GeoLine.EQUATION_IMPLICIT );
        setLabel(label);                
    }
      
    public GeoLine(GeoLine line) {
    	super(line.cons);
        set(line);
    }
    
    protected String getClassName() {
    	return "GeoLine";
    }
    
    protected String getTypeString() {
		return "Line";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_LINE;
    }
      
    public GeoElement copy() {
        return new GeoLine(this);        
    }    
    
	final public void setCoords(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}     
    
	final public void setCoords(GeoVec3D v) {
		 x = v.x;
		 y = v.y;
		 z = v.z;
	 } 
    
    /** returns true if P lies on this line */
    public boolean isIntersectionPointIncident(GeoPoint p, double eps) {    
        return isOnFullLine(p, eps);
    }
    
    /** 
	 * States wheter P lies on this line or not.
	 */
	final boolean isOnFullLine(GeoPoint P, double eps) {						
		if (!P.isDefined()) return false;		
			
		double simplelength =  Math.abs(x) + Math.abs(y);
		if (P.isInfinite()) {		
			return Math.abs(x * P.x + y * P.y) < eps * simplelength;
		}
		else {
			// STANDARD CASE: finite point			
			return Math.abs(x * P.inhomX + y * P.inhomY + z) < eps * simplelength;
		}
	}
    
    /** returns true if this line and g are parallel */
    final public boolean isParallel(GeoLine g) {        
        return kernel.isEqual(g.x * y, g.y * x);        
    }
    
    /** returns true if this line and g are perpendicular */
    final public boolean isPerpendicular(GeoLine g) {        
        return kernel.isEqual(g.x * x, -g.y * y);        
    }
        
    /** Calculates the euclidian distance between this GeoLine and GeoPoint P.
     */
    final public double distance(GeoPoint p) {                        
        return Math.abs( (x * p.inhomX + y * p.inhomY + z) / 
                            GeoVec2D.length(x, y) );
    }
    
	/** Calculates the euclidian distance between this GeoLine and GeoPoint P.
	 * Here the inhomogenouse coords of p are calculated and p.inhomX,
	 * p.inhomY are not used.
	 */
	final public double distanceHom(GeoPoint p) {                        
		return Math.abs( (x * p.x / p.z + y * p.y / p.z + z) / 
							GeoVec2D.length(x, y) );
	}
    
    /** Calculates the euclidian distance between this GeoLine and GeoLine g.
     */
    final public double distance(GeoLine g) {          
        // parallel
        if (kernel.isZero(g.x * y - g.y * x)) {
            // get a point (px, py) of g and calc distance
            double px, py; 
            if (Math.abs(g.x) > Math.abs(g.y)) {
                px = -g.z / g.x;
                py = 0.0d;
            } else {
                px = 0.0d;
                py = -g.z / g.y;
            }
            return Math.abs( (x * px + y * py + z) / GeoVec2D.length(x, y) );
        } else
			return 0.0;
    }
    
    final public void getDirection(GeoVec3D out) {
        out.setCoords(y, -x, 0.0d);
    }
    
    /**
     * Writes coords of direction vector to array dir.
     * @param dir: array of length 2
     */
    final public void getDirection(double [] dir) {
        dir[0] = y;
        dir[1] = -x;
    }
        
    /** 
     * Set array p to (x,y) coords of a point on this line 
	 */
    final public void getInhomPointOnLine(double [] p) {  
    	// point defined by parent algorithm
    	if (startPoint != null && startPoint.isFinite()) {
    		p[0] = startPoint.inhomX;
    		p[1] = startPoint.inhomY;
    	} 
    	// point on axis
    	else {
			if (Math.abs(x) > Math.abs(y)) {
				p[0] = -z / x;
				p[1] = 0.0d;
		    } else {
			   p[0] = 0.0d;
			   p[1] = -z / y;
		    }        
    	}  
    }
    
	/** 
	 * Sets point p p to coords of some point on this line 
	 */
	final public void getPointOnLine(GeoPoint p) {  
		// point defined by parent algorithm
		if (startPoint != null && startPoint.isFinite()) {
			p.setCoords(startPoint);
		} 
		// point on axis
		else {
			if (Math.abs(x) > Math.abs(y)) {
				p.setCoords(-z / x, 0.0, 1.0);
			} else {
				p.setCoords(0.0, -z / y,  1.0);
			}        
		}  
	}
   
    final void setStartPoint(GeoPoint P) {        	
    	startPoint = P;	    	
    }
    
    final void setEndPoint(GeoPoint Q) {    	
    	endPoint = Q;
    }
    
	/**
	 * Retuns first defining point of this line or null.
	 */
	final public GeoPoint getStartPoint() {
		return startPoint;
	}   
    
	/**
	 * Retuns second point of this line or null.
	 */
	final public GeoPoint getEndPoint() {
		return endPoint;
	}   

    public boolean isDefined() {
        return (!(Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) &&
                !(kernel.isZero(x) && kernel.isZero(y)));  
    }
        
    protected boolean showInEuclidianView() {
        // defined
        return isDefined();
    }
    
    protected boolean showInAlgebraView() {
        // independent or defined
        //return isIndependent() || isDefined();
    	return isLabelSet();
    }                
    
    public void set(GeoElement geo) { 
    	super.set(geo);
    	
        GeoLine l = (GeoLine) geo;                      
        parameter = l.parameter;        
    }    
    
    /** 
     * Yields true if the coefficients of this line are linear dependent on
     * those of line g.
     */
	// Michael Borcherds 2008-04-30
	public boolean isEqual(GeoElement geo) {
		// return false if it's a different type, otherwise use equals() method
		if (geo.isGeoLine()) return linDep((GeoLine)geo); else return false;
	}

    /**
     * yields true if this line is defined as a tangent of conic c
     */
    final public boolean isDefinedTangent(GeoConic c) {        
        boolean isTangent = false;
        
        Object ob = getParentAlgorithm();        
        if (ob instanceof AlgoTangentLine || ob instanceof AlgoTangentPoint) {        
            GeoElement [] input = ((AlgoElement) ob).getInput();
            for (int i=0; i < input.length; i++) {
                if (input[i] == c) {
                    isTangent = true;
                    break;
                }
            }
        }        
        return isTangent;
    }
    
    /**
     * yields true if this line is defined as a asymptote of conic c
     */
    final public boolean isDefinedAsymptote(GeoConic c) {        
        boolean isAsymptote = false;
        
        Object ob = getParentAlgorithm();        
        if (ob instanceof AlgoAsymptote) {        
            GeoElement [] input = ((AlgoElement) ob).getInput();
            for (int i=0; i < input.length; i++) {
                if (input[i] == c) {
                    isAsymptote = true;
                    break;
                }
            }
        }        
        return isAsymptote;
    }
    
    
/***********************************************************
 * MOVEMENTS
 ***********************************************************/
    
    /**
     * translate by vector v
     */
    final public void translate(GeoVector v) {        
        z -= x * v.x + y * v.y;
    }  
    
	final public boolean isTranslateable() {
		return true;
	}
    
    /**
     * dilate from S by r
     */
    final public void dilate(NumberValue rval, GeoPoint S) {
       double r = rval.getDouble();        
       double temp = (r - 1);
       z = temp * (x * S.inhomX + y * S.inhomY) + r * z;
       
       x *= r;
       y *= r;
       z *= r;
    } 
    
    /**
     * rotate this line by angle phi around (0,0)
     */
    final public void rotate(NumberValue phiVal) {
    	double phi = phiVal.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);
        
        double x0 = x * cos - y * sin;
        y = x * sin + y * cos;
        x = x0;        
    }
        
    /**
     * rotate this line by angle phi around Q
     */
     final public void rotate(NumberValue phiVal, GeoPoint Q) {
        double phi = phiVal.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);     
        double qx = Q.inhomX;
        double qy = Q.inhomY;              
        
        double x0 = x * cos - y * sin;
        double y0 = x * sin + y * cos;                        
        z = z + (x*qx + y*qy) * (1.0-cos) + (y*qx - x*qy) * sin;        
        x = x0;  
        y = y0;
    }
    
    /**
     * mirror this line at point Q
     */
    final public void mirror(GeoPoint Q) {
        double qx = x*Q.inhomX;
        double qy = y*Q.inhomY;
        
        z = z + 2.0 * (qx + qy);
        x = -x;
        y = -y;
    }
    
   /**
     * mirror this point at line g
     */
    final public void mirror(GeoLine g) {
        // Y = S(phi).(X - Q) + Q
        // where Q is a point on g, S(phi) is the mirror transform
        // and phi/2 is the line's slope angle
        
        // get arbitrary point of line
        double qx, qy;        
        if (Math.abs(g.x) > Math.abs(g.y)) {
            qx = -g.z / g.x;
            qy = 0.0d;            
        } else {
            qx = 0.0d;
            qy = -g.z / g.y;                        
        }                
              
        double phi = 2.0 * Math.atan2(-g.x, g.y);                
        double cos = Math.cos(phi);
        double sin = Math.sin(phi);                
        
        double x0 = x * cos + y * sin;
        double y0 = x * sin - y * cos;       
        double xqx = x * qx;
        double yqy = y * qy;
        z += (xqx + yqy) + (yqy - xqx) * cos - (x*qy + y*qx) * sin;        
        x = x0;  
        y = y0;        
    }        
            
    /***********************************************************/
            
    final public void setToParametric(String parameter) {
            setMode( GeoLine.PARAMETRIC );
            if (parameter != null && parameter.length() > 0)
                    this.parameter = parameter;
    }
    
    final public void setToExplicit() {
        setMode(EQUATION_EXPLICIT);
    }
    
    final public void setToImplicit() {
        setMode(EQUATION_IMPLICIT);
    }
    
            
    final public void setMode( int mode ) {
    	switch (mode) {
    		case PARAMETRIC:    	    			
                    toStringMode = PARAMETRIC;	
                    break;    			
                        
                case EQUATION_EXPLICIT:
                    toStringMode = EQUATION_EXPLICIT;
                    break;
                    
    		default:
    	            toStringMode = EQUATION_IMPLICIT;
    	}
    }            
    
    /** output depends on mode: PARAMETRIC or EQUATION */
    public String toString() {       
    	sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(": ");         
		sbToString.append(buildValueString());
		return sbToString.toString();   
    }
	StringBuffer sbToString = new StringBuffer(50);   
	
	public String toValueString() {
		return buildValueString().toString();
	}
    
    private StringBuffer buildValueString() {		                               		 
       	switch (toStringMode) {     
            case EQUATION_EXPLICIT:   ///EQUATION    
                g[0] = x;
                g[1] = y;
                g[2] = z;  
                return kernel.buildExplicitLineEquation(g, vars);
            
            case PARAMETRIC:       				                  
				  	getInhomPointOnLine(P); // point
					sbBuildValueString.setLength(0);			                  
					sbBuildValueString.append("X = (");
					sbBuildValueString.append(kernel.format(P[0]));
					sbBuildValueString.append(", ");
					sbBuildValueString.append(kernel.format(P[1]));
					sbBuildValueString.append(") + ");
					sbBuildValueString.append(parameter);
					sbBuildValueString.append(" ("); 
					sbBuildValueString.append(kernel.format(y));
					sbBuildValueString.append(", ");
					sbBuildValueString.append(kernel.format(-x));
					sbBuildValueString.append(")");                    
				  return sbBuildValueString;                                                                           
            
            default:   // EQUATION_IMPLICIT            	            
                g[0] = x;
                g[1] = y;
                g[2] = z;                
                if (kernel.isZero(x) || kernel.isZero(y)) 
					return kernel.buildExplicitLineEquation(g, vars);
                else
                    return kernel.buildImplicitEquation(g, vars, KEEP_LEADING_SIGN);
        }    	    	
    }        
	private StringBuffer sbBuildValueString = new StringBuffer(50);     
    
    /** left hand side as String : ax + by + c */
    final public StringBuffer toStringLHS() {		  
        if (isDefined()) {
			g[0] = x;
			g[1] = y;
			g[2] = z;  
			return kernel.buildLHS(g, vars, KEEP_LEADING_SIGN); 
        } else
			return sbToStringLHS;                           	                   	               
    }
	private StringBuffer sbToStringLHS = new StringBuffer("\u221E");     
 
    /**
     * returns all class-specific xml tags for saveXML
     */
    protected String getXMLtags() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.getXMLtags());
		//	line thickness and type  
		sb.append(getLineStyleXML());	  
        
        // prametric, explicit or implicit mode
        switch(toStringMode) {
            case GeoLine.PARAMETRIC:
            	sb.append("\t<eqnStyle style=\"parametric\" parameter=\"");
                sb.append(parameter);
                sb.append("\"/>\n");
                break;
                
            case GeoLine.EQUATION_EXPLICIT:
                sb.append("\t<eqnStyle style=\"explicit\"/>\n");
                break;
           
            default:
                sb.append("\t<eqnStyle style=\"implicit\"/>\n");
        }        

        return sb.toString();   
    }


	/* 
	 * Path interface
	 */
    
	public boolean isClosedPath() {
		return false;
	}
	 
	public void pointChanged(GeoPoint P) {
		// project P on line
		double px = P.x/P.z;
		double py = P.y/P.z;
		// param of projection point on perpendicular line
		double t = -(z + x*px + y*py) / (x*x + y*y); 
		// calculate projection point using perpendicular line
		P.x = px + t * x;
		P.y = py + t * y;
		P.z = 1.0;	
						
		// set path parameter
		if (startPoint != null) {
			if (Math.abs(x) <= Math.abs(y)) {	
				P.pathParameter.t = (startPoint.z * P.x - startPoint.x) / (y * startPoint.z);								
			} 
			else {		
				P.pathParameter.t = (startPoint.y - startPoint.z * P.y) / (x * startPoint.z);			
			}
		}		
	}				

	public void pathChanged(GeoPoint P) {
		// calc point for given parameter
		if (startPoint != null) {
			P.x = startPoint.inhomX + P.pathParameter.t * y;
			P.y = startPoint.inhomY - P.pathParameter.t * x;
			P.z = 1.0;		
		} else  {
			pointChanged(P);		
		}
	}
	
    public boolean isOnPath(GeoPoint P, double eps) {
    	if (P.getPath() == this)
			return true;
    	
    	return isOnFullLine(P, eps);
    }
    
	public boolean isPath() {
		return true;
	}
	
	public boolean isGeoLine() {
		return true;
	}
    
	/**
	 * Returns the smallest possible parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * @return
	 */
	public double getMinParameter() {
		return Double.NEGATIVE_INFINITY;
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
		return new PathMoverLine();
	}		
		
	
	private class PathMoverLine extends PathMoverGeneric {
				
		private GeoPoint moverStartPoint;	
		
		public PathMoverLine() {
			super(GeoLine.this);
			moverStartPoint = new GeoPoint(cons);
		}
		
		public void init(GeoPoint p) {						
			//	we need a point on the line:		
			// p is a point on the line ;-)
			moverStartPoint.set(p);
			moverStartPoint.setConstruction(p.cons);
			p.pathParameter.t = 0;												
			start_param = 0; 								
			
			min_param = -1 + PathMover.OPEN_BORDER_OFFSET;
			max_param =  1 - PathMover.OPEN_BORDER_OFFSET;		
			param_extent = max_param - min_param;			
			init_step_width = INIT_STEP_WIDTH;
			max_step_width  = param_extent / MIN_STEPS;			
			posOrientation = true; 								
			
			/*
			System.out.println("init_step_width: " + init_step_width);
			System.out.println("MAX_STEP_WIDTH: " + MAX_STEP_WIDTH);
			System.out.println("MIN_STEP_WIDTH: " + MIN_STEP_WIDTH);
			*/
			
			resetStartParameter();
		}							
		
		protected void calcPoint(GeoPoint p) {
			p.pathParameter.t = PathMoverGeneric.infFunction(curr_param);	
			p.x = moverStartPoint.inhomX + p.pathParameter.t * y;
			p.y = moverStartPoint.inhomY - p.pathParameter.t * x;
			p.z = 1.0;	
			p.updateCoords();
		}
		
		public boolean hasNext() {						
			// check if we pass the start parameter 0:
			// i.e. check if the sign will change from 
			// last_param to the next parameter curr_param				
			if (posOrientation)
				return !(curr_param < 0 && (curr_param + step_width) >= 0);
			else
				return !(curr_param > 0 && (curr_param + step_width) <= 0);									
		}					
	}
}