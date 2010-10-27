/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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

import geogebra.Matrix.GgbMatrix;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.arithmetic.Evaluatable;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;

public class GeoLine extends GeoVec3D 
implements Path, 
Translateable,PointRotateable, Mirrorable, Dilateable, LineProperties,
GeoLineND, MatrixTransformable, GeoFunctionable, Evaluatable {
	
	private static final long serialVersionUID = 1L;
	// modes
    public static final int EQUATION_IMPLICIT = 0;
    public static final int EQUATION_EXPLICIT = 1;
    public static final int PARAMETRIC = 2;		
    public static final int EQUATION_IMPLICIT_NON_CANONICAL = 3;		
    
	protected char op = '='; // eg '=', '<' for GeoLinearInequality
	
    private String parameter = "\u03bb";	
    GeoPoint startPoint, endPoint;    
    
    //  enable negative sign of first coefficient in implicit equations
	private static boolean KEEP_LEADING_SIGN = true;
    private static final String [] vars = { "x", "y" };
    
    public GeoLine(Construction c) { 
    	super(c); 
    	setMode( GeoLine.EQUATION_IMPLICIT );
    }
    
    public GeoLine(Construction c, int mode) { 
    	super(c); 
    	setMode( mode );
    }
    
    /** Creates new GeoLine */     
    public GeoLine(Construction cons, String label, double a, double b, double c) {
        super(cons, a, b, c);	// GeoVec3D constructor                 
        setMode( GeoLine.EQUATION_IMPLICIT );
        setLabel(label);                
    }
      
    public GeoLine(GeoLine line) {
    	super(line.cons);
        set(line);
    }
    
    public String getClassName() {
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
		
		//Application.debug("x="+x+", y="+y+", z="+z);
	}     
    
	final public void setCoords(GeoVec3D v) {
		
		setCoords(v.x, v.y, v.z);
		/*
		 x = v.x;
		 y = v.y;
		 z = v.z;
		 */
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
	
    /**
     * Returns whether this point lies on this line, segment or ray.     
     */
    final public boolean isOnPath(GeoPointND PI, double eps) {  
    	
    	GeoPoint P = (GeoPoint) PI;
    	
    	if (P.getPath() == this)
			return true;
    	
    	// check if P lies on line first
  		if (!isOnFullLine(P, eps))
    		return false;    	
    	
    	// for a line we are done here: the point is on the line
    	// for rays and segments we need to continue
    	int classType = getGeoClassType();
    	if (classType == GEO_CLASS_LINE)
    		return true;
    	
    	// idea: calculate path parameter and check
		// if it is in [0, 1] for a segment or greater than 0 for a ray
		
		// remember the old point coordinates
		double px = P.x, py = P.y, pz = P.z;
		PathParameter tempPP = getTempPathParameter();
		PathParameter pp = P.getPathParameter();
		tempPP.set(pp);
		
		// make sure we use point changed for a line to get parameters on 
		// the entire line when this is a segment or ray
		doPointChanged(P);		
		
		boolean result;
		switch (classType) {
			case GEO_CLASS_SEGMENT:
				// segment: parameter in [0,1]
				result =   pp.t >= -eps && 
							pp.t <= 1 + eps;				
				break;
				
			case GEO_CLASS_RAY:
				// ray: parameter > 0
				result =   pp.t >= -eps;					
				break;
				
			default:
				// line: any parameter
				result = true;
		}
	
		// restore old values
		P.x = px; P.y = py; P.z = pz;
		pp.set(tempPP);
		
		return result;
    }
    
    
    /**
     * return a possible parameter for the point P
     * (return the parameter for the projection of P on the path)
     * @param P
     * @return a possible parameter for the point P
     */
    public double getPossibleParameter(GeoPoint P){
    	
    	// remember the old point coordinates
		double px = P.x, py = P.y, pz = P.z;
		PathParameter tempPP = getTempPathParameter();
		PathParameter pp = P.getPathParameter();
		tempPP.set(pp);
		
		// make sure we use point changed for a line to get parameters on 
		// the entire line when this is a segment or ray
		doPointChanged(P);		
		
		double result = pp.t;
	
		// restore old values
		P.x = px; P.y = py; P.z = pz;
		pp.set(tempPP);
		
		return result;
		
    }
    
    private PathParameter tempPP;
    private PathParameter getTempPathParameter() {
    	if (tempPP == null) 
    		tempPP = new PathParameter();
    	return tempPP;
    }
    
    
    /** returns true if this line and g are parallel */
    final public boolean isParallel(GeoLine g) {        
        return kernel.isEqual(g.x * y, g.y * x);        
    }
    
    /** returns true if this line and g are parallel (signed)*/
    final public boolean isSameDirection(GeoLine g) {        
    	// check x and g.x have the same sign
    	// also y and g.y
        return (g.x * x >= 0) && (g.y * y >= 0) && isParallel(g);        
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
    
    public boolean showInAlgebraView() {
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
		if (geo.isGeoRay() || geo.isGeoSegment()) return false;
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
    final public void translate(GgbVector v) {        
        z -= x * v.getX() + y * v.getY();
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
    	rotateXY(phiVal);        
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
        
        // change orientation
        x = -x;
        y = -y;
        z = -z;
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
                
            case EQUATION_IMPLICIT_NON_CANONICAL:
                toStringMode = EQUATION_IMPLICIT_NON_CANONICAL;
                break;
                
    		default:
    	            toStringMode = EQUATION_IMPLICIT;
    	}
    }            
    
    /** output depends on mode: PARAMETRIC or EQUATION */
    public String toString() {    
    	StringBuilder sbToString = getSbToString();
    	sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(": ");         
		sbToString.append(buildValueString());
		return sbToString.toString();   
    }
    
	private StringBuilder sbToString;
	private StringBuilder getSbToString() {
		if (sbToString == null)
			sbToString = new StringBuilder(50);
		return sbToString;
	}
	
	
	public String toValueString() {
		return buildValueString().toString();
	}
    
    private StringBuilder buildValueString() {		
        double [] P = new double[2];                       			 
        double [] g = new double[3];	
    	
       	switch (toStringMode) {     
            case EQUATION_EXPLICIT:   ///EQUATION    
                g[0] = x;
                g[1] = y;
                g[2] = z;  
                return kernel.buildExplicitLineEquation(g, vars, op);
            
            case PARAMETRIC:       				                  
				  	getInhomPointOnLine(P); // point
				  	StringBuilder sbBuildValueString = getSbBuildValueString();
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
				  
            case EQUATION_IMPLICIT_NON_CANONICAL:
                g[0] = x;
                g[1] = y;
                g[2] = z;                
                if (kernel.isZero(x) || kernel.isZero(y)) 
					return kernel.buildExplicitLineEquation(g, vars, op);
                else
                    return kernel.buildImplicitEquation(g, vars, KEEP_LEADING_SIGN, false, op);
            
            default:   // EQUATION_IMPLICIT    
                g[0] = x;
                g[1] = y;
                g[2] = z;                
                if (kernel.isZero(x) || kernel.isZero(y)) 
					return kernel.buildExplicitLineEquation(g, vars, op);
                else
                    return kernel.buildImplicitEquation(g, vars, KEEP_LEADING_SIGN, true, op);
        }    	    	
    }        
    
	private StringBuilder sbBuildValueString = new StringBuilder(50);
	private StringBuilder getSbBuildValueString() {
		if (sbBuildValueString == null)
			sbBuildValueString = new StringBuilder(50);
		return sbBuildValueString;
	}
    
    /** left hand side as String : ax + by + c */
    final public StringBuilder toStringLHS() {		  
        double [] g = new double[3];	
        
        if (isDefined()) {
			g[0] = x;
			g[1] = y;
			g[2] = z;  
			return kernel.buildLHS(g, vars, KEEP_LEADING_SIGN, true); 
        } else
			return sbToStringLHS;                           	                   	               
    }
	private static StringBuilder sbToStringLHS = new StringBuilder("\u221E");     
 
    /**
     * returns all class-specific xml tags for saveXML
     * GeoGebra File Format
     */
	protected void getXMLtags(StringBuilder sb) {
        super.getXMLtags(sb);
		//	line thickness and type  
		getLineStyleXML(sb);
        
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
           
            case GeoLine.EQUATION_IMPLICIT_NON_CANONICAL:
                // don't want anything here
                break;
           
            default:
                sb.append("\t<eqnStyle style=\"implicit\"/>\n");
        }        

    }

	/* 
	 * Path interface
	 */
    
	public boolean isClosedPath() {
		return false;
	}
	 
	public void pointChanged(GeoPointND P) {
		doPointChanged(P);
	}
		
	
	
	private void doPointChanged(GeoPointND P) {
		
		GgbVector coords = P.getCoordsInD(2);
		
		
	
		// project P on line
		double px = coords.getX()/coords.getZ();
		double py = coords.getY()/coords.getZ();
		// param of projection point on perpendicular line
		double t = -(z + x*px + y*py) / (x*x + y*y); 
		// calculate projection point using perpendicular line
		px += t * x;
		py += t * y;
		P.setCoords2D(px, py, 1);
		P.updateCoordsFrom2D(false);
						
		// set path parameter
		if (startPoint != null) {
			PathParameter pp = P.getPathParameter();
			if (Math.abs(x) <= Math.abs(y)) {	
				pp.t = (startPoint.z * px - startPoint.x) / (y * startPoint.z);								
			} 
			else {		
				pp.t = (startPoint.y - startPoint.z * py) / (x * startPoint.z);			
			}
		}		
	}				

	public void pathChanged(GeoPointND PI) {
		
		GeoPoint P = (GeoPoint) PI;
		
		// calc point for given parameter
		if (startPoint != null) {
			PathParameter pp = P.getPathParameter();
			P.x = startPoint.inhomX + pp.t * y;
			P.y = startPoint.inhomY - pp.t * x;
			P.z = 1.0;		
		} else  {
			pointChanged(P);		
		}
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
		}
		
		public void init(GeoPoint p) {	
			// we need a start point for pathChanged() to work correctly
			// with our path parameters
			if (startPoint == null) {
				moverStartPoint = new GeoPoint(cons);
				setStartPoint(moverStartPoint);				
			}
			
			if (moverStartPoint != null) {
				moverStartPoint.setCoords(p);
				// point p is on the line and we use it's location
				// as the startpoint, thus p needs to get path parameter 0
				PathParameter pp = p.getPathParameter();
				pp.t = 0;	
			}
			
			super.init(p);
						
//			//	we need a point on the line:		
//			// p is a point on the line ;-)
//			moverStartPoint.setCoords(p);
//			PathParameter pp = p.getPathParameter();
//			pp.t = 0;												
//			start_param = 0;						
//			
//			min_param = -1 + PathMover.OPEN_BORDER_OFFSET;
//			max_param =  1 - PathMover.OPEN_BORDER_OFFSET;			
//			
//			param_extent = max_param - min_param;
//			max_step_width = param_extent / MIN_STEPS;		
//			posOrientation = true; 											
//			
//			resetStartParameter();
		}							
		
//		protected void calcPoint(GeoPoint p) {
//			PathParameter pp = p.getPathParameter();
//			pp.t = PathMoverGeneric.infFunction(curr_param);	
//			p.x = moverStartPoint.inhomX + pp.t * y;
//			p.y = moverStartPoint.inhomY - pp.t * x;
//			p.z = 1.0;	
//			p.updateCoords();
//		}
//		
//		public boolean hasNext() {						
//			// check if we pass the start parameter 0:
//			// i.e. check if the sign will change from 
//			// last_param to the next parameter curr_param	
//			double next_param = curr_param + step_width;	
//			if (posOrientation)
//				return !(curr_param < 0 && next_param >= 0);
//			else
//				return !(curr_param > 0 && next_param <= 0);
//		}					
	}

	   public void add(GeoLine line) {
		   x += line.x;
		   y += line.y;
		   z += line.z;
	   }
	    
	   public void subtract(GeoLine line) {
		   x -= line.x;
		   y -= line.y;
		   z -= line.z;
	   }
	    
	   public void multiply(GeoLine line) {
		   x *= line.x;
		   y *= line.y;
		   z *= line.z;
	   }
	    
	   public void divide(GeoLine line) {
		   x /= line.x;
		   y /= line.y;
		   z /= line.z;
	   }
	   						
    public void setZero() {
    	setCoords(0, 1, 0);
    }

	public boolean isVector3DValue() {
		return false;
	}
    
	 public String getAssignmentOperator() {
		 return ": ";
		 
	 }
	public void matrixTransform(double p,double q,double r, double s) {
		
		double x1,y1;
		
		if (Kernel.isZero(y)) {
			x1 = s;
			y1 = -q;
			setCoords(x1 * x, y1 * x , -q * r * z + s * p * z );
		} else {
			x1 = r * y - s * x;
			y1 = q * x - p * y;
			setCoords(x1 * y, y1 * y , q * z * x1 + s * z * y1 );
			
		}


	}
	
	/**
	 * Creates a GeoFunction of the form f(x) = thisNumber 
	 * needed for SumSquaredErrors[FitLine[]]
	 * @return constant function
	 */	
	public GeoFunction getGeoFunction() {
		GeoFunction ret;
		
		FunctionVariable fv = new FunctionVariable(kernel);
		
		
		ExpressionNode xCoord = new ExpressionNode(kernel, 
                this,
                ExpressionNode.XCOORD, 
                null);

		ExpressionNode yCoord = new ExpressionNode(kernel, 
                this,
                ExpressionNode.YCOORD, 
                null);

		ExpressionNode zCoord = new ExpressionNode(kernel, 
                this,
                ExpressionNode.ZCOORD, 
                null);

		
		// f(x_var) = -x/y x_var - z/y

		ExpressionNode temp = new ExpressionNode(kernel, 
				xCoord,
                ExpressionNode.DIVIDE, 
                yCoord);
		
		temp = new ExpressionNode(kernel, 
				new MyDouble(kernel, -1.0),
                ExpressionNode.MULTIPLY, 
                temp);
		
		temp = new ExpressionNode(kernel, 
                temp,
                ExpressionNode.MULTIPLY, 
                fv);		
		
		temp = new ExpressionNode(kernel, 
                temp,
                ExpressionNode.MINUS, 
                new ExpressionNode(kernel, 
                		zCoord,
                        ExpressionNode.DIVIDE, 
                        yCoord));		
		
		
		
		
		
		
		// f(x_var) = -x/y x_var - z/y
		/*
		ExpressionNode temp = new ExpressionNode(kernel, 
                new MyDouble(kernel, -x / y),
                ExpressionNode.MULTIPLY, 
                fv);
		
		temp = new ExpressionNode(kernel, 
                temp, 
                ExpressionNode.PLUS, 
                new MyDouble(kernel, -z / y)
            );*/
		
		Function fun = new Function(temp, fv);			

		
		// we get a dependent function if this line has a label or is dependent
		
		if (isLabelSet() || !isIndependent()) {
			// don't create a label for the new dependent function
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			ret = kernel.DependentFunction(null, fun);
			cons.setSuppressLabelCreation(oldMacroMode);
		} else 
		{
			ret = new GeoFunction(cons);
			ret.setFunction(fun);
		}					
				
		return ret;
	}
	
	public boolean isGeoFunctionable() {
		return true;
	}
	

	
	public boolean isMatrixTransformable() { 
		return true;
	}
	
	public void toGeoConic(GeoConic con){
		con.lines = new GeoLine[2];
		con.lines[0] = this;
		con.lines[1] = this;
		con.type = GeoConic.CONIC_LINE;		
	}

	public double evaluate(double x_var) {
		if (Kernel.isZero(y)) return Double.NaN;
		return (-x * x_var - z) / y;
	}

	


	//////////////////////////////////////
	// 3D stuff
	//////////////////////////////////////
	

  	public boolean hasDrawable3D() {
		return true;
	}
    
  	public GgbVector getLabelPosition(){
		return getPointInD(3, 0.5);
	}
 	
  	public GgbVector getPointInD(int dimension, double lambda){

  		if (dimension<2 || dimension>3)
  			return null;
  		
  		GgbVector startCoords;
  		 
  		//TODO merge with getPointOnLine
		// point defined by parent algorithm
		if (startPoint != null && startPoint.isFinite()) {
			startCoords=startPoint.getCoordsInD(dimension);
		} 
		// point on axis
		else {
			startCoords = new GgbVector(dimension+1);
			if (Math.abs(x) > Math.abs(y)) {
				startCoords.setX(-z / x);
			} else {
				startCoords.setY(-z / y);
			}   
			startCoords.set(dimension+1, 1); //last homogeneous coord
		}  
		
		GgbVector direction = new GgbVector(dimension+1);
		direction.setX(y*lambda);direction.setY(-x*lambda);
		
  		return startCoords.add(direction);
	}
  	
	public GgbVector getViewDirection(){
		return getPointInD(3, 1).sub(getPointInD(3, 0));
	}
	
	public GgbVector getCartesianEquationVector(GgbMatrix m){
		return new GgbVector(x, y, z);
	}
	
	public GgbVector getStartInhomCoords(){
		return startPoint.getInhomCoords();
	}
	

}