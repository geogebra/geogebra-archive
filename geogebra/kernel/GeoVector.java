/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoVector.java
 *
 * The vector (x,y) has homogenous coordinates (x,y,0)
 *
 * Created on 30. August 2001, 17:39
 */

package geogebra.kernel;

import geogebra.Application;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.VectorValue;

import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author  Markus
 * @version 
 */
final public class GeoVector extends GeoVec3D
implements Path, VectorValue, Locateable, Rotateable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GeoPoint startPoint;
	
	// for path interface we use a segment
	private GeoSegment pathSegment;
	private GeoPoint pathStartPoint, pathEndPoint;
	private boolean waitingForStartPoint = false;
	private HashSet waitingPointSet;
    
    /** Creates new GeoVector */
    public GeoVector(Construction c) {
    	super(c); 
    	//setEuclidianVisible(false);
    }
    
	protected String getClassName() {
		return "GeoVector";
	}
	
    protected String getTypeString() {
		return "Vector";
	}

	public int getGeoClassType() {
		return GEO_CLASS_VECTOR;
	}    
    
    /** Creates new GeoVector */
    public GeoVector(Construction c, String label, double x, double y, double z) {
        super(c, x, y, z); // GeoVec3D constructor                 
        setLabel(label); 
        //setEuclidianVisible(false);
    }
    
    public GeoVector(GeoVector vector) {
    	super(vector.cons);
        set(vector);
        //setEuclidianVisible(false);
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
	
	public void set(GeoElement geo) {
		super.set(geo);	
		if (!geo.isGeoVector()) return;
		
		GeoVector vec = (GeoVector) geo;		
	
		// don't set start point for macro output
		// see AlgoMacro.initRay()
		if (geo.cons != cons && isAlgoMacroOutput())
			return;
	
		try {
			if (vec.startPoint != null) {
				if (vec.hasAbsoluteLocation()) {
					//	create new location point	
					setStartPoint(new GeoPoint(vec.startPoint));
				} else {
					//	take existing location point	
					setStartPoint(vec.startPoint);
				}
			}
		}
		catch (CircularDefinitionException e) {
			Application.debug("set GeoVector: CircularDefinitionException");
		}		
	}
    
    public GeoElement copy() {
        return new GeoVector(this);        
    } 
    
    final public void setPolarCoords(double r, double phi) {
        // convert angle to radiant              
        x = r * Math.cos( phi );
        y = r * Math.sin( phi );        
        z = 0.0d;        
    }
    
    final public void setCoords(GeoVec2D v) {
        x = v.x;
        y = v.y;
        z = 0.0d;
    }      

    /** Converts the homogenous coordinates (x,y,z)
     * of this GeoVec3D to the inhomogenous coordinates (x/z, y/z)
     * of a new GeoVec2D.
     */
    final public GeoVec2D getInhomVec() {
        return new GeoVec2D(kernel, x, y);
    }
    
    /**
     * Retuns starting point of this vector or null.
     */
    public GeoPoint getStartPoint() {
		return startPoint;
    }   
    
	public GeoPoint [] getStartPoints() {
		if (startPoint == null)
			return null;
	
		GeoPoint [] ret = new GeoPoint[1];
		ret[0] = startPoint;
		return ret;			
	}
    
	public boolean hasAbsoluteLocation() {
		return startPoint == null || startPoint.isAbsoluteStartPoint();
	}
	
	public void setStartPoint(GeoPoint p, int number)  throws CircularDefinitionException {
		setStartPoint(p);
	}
	
	/**
	 * Sets the startpoint without performing any checks.
	 * This is needed for macros.	 
	 */
	public void initStartPoint(GeoPoint p, int number) {
		startPoint = p;
	}
	
	public void removeStartPoint(GeoPoint p) {    
		if (startPoint == p) {
			try {
				setStartPoint(null);
			} catch(Exception e) {}
		}
	}
    
    public void setStartPoint(GeoPoint p) throws CircularDefinitionException {  
    	if (startPoint == p) return;
    	
    	// macro output uses initStartPoint() only
		if (isAlgoMacroOutput()) return; 				
    	
		// check for circular definition
		if (isParentOf(p))
			throw new CircularDefinitionException();
		
		// remove old dependencies
		if (startPoint != null) startPoint.unregisterLocateable(this);	
	
		// set new location	
		startPoint = p;		
		
		//	add new dependencies
		if (startPoint != null) startPoint.registerLocateable(this);	
		
		// reinit path
    	if (pathSegment != null) {
    		initPathSegment();
    	}

    	// update the waiting points
		if (waitingForStartPoint) {
			waitingForStartPoint = false;
			
			if (waitingPointSet != null) {
	        	updatePathSegment();
		
				GeoPoint P;
				Iterator it = waitingPointSet.iterator();
				while (it.hasNext()) {
					P = (GeoPoint) it.next();
					pathSegment.pointChanged(P);
					P.updateCoords();
				}	
			}
		}
    }
    
	public void setWaitForStartPoint() {
	  // the startpoint should not be used as long
	  // as waitingForStartPoint is true
	  // This is important for points on this vector:
	  // their coords should not be changed until 
	  // the startPoint was finally set
		waitingForStartPoint = true;
	}
    
    protected void doRemove() {
    	super.doRemove();
		// tell startPoint	
		if (startPoint != null) startPoint.unregisterLocateable(this);
    }
    
    final public boolean isFinite() {
        return !isInfinite();
    }
    
    final public boolean isInfinite() {
       return Double.isInfinite(x) || Double.isInfinite(y);  
    }
    
    final protected boolean showInEuclidianView() {               
        return isDefined() && !isInfinite();
    }    
    
    final protected boolean showInAlgebraView() {
        // independent or defined
       // return isIndependent() || isDefined();
    	return true;
    }    
    
     /** 
     * Yields true if the coordinates of this vector are equal to
     * those of vector v. Infinite points are checked for linear dependency.
     */
	// Michael Borcherds 2008-05-01
    final public boolean isEqual(GeoElement geo) {        
    	
    	if (!geo.isGeoVector()) return false;
    	
    	GeoVector v = (GeoVector)geo;
    	
        if (!(isFinite() && v.isFinite())) return false;                                        
        else return kernel.isEqual(x, v.x) && kernel.isEqual(y, v.y);                                            
    }
    
    
/***********************************************************
 * MOVEMENTS
 ***********************************************************/
    /**
     * rotate this vector by angle phi around (0,0)
     */
    final public void rotate(NumberValue phi) {    	
    	double ph = phi.getDouble();
        double cos = Math.cos(ph);
        double sin = Math.sin(ph);
        
        double x0 = x * cos - y * sin;
        y = x * sin + y * cos;
        x = x0;        
    }            
    
/*********************************************************************/   
    
    final public String toString() {            
		sbToString.setLength(0);
		sbToString.append(label);
		if (kernel.getCoordStyle() != Kernel.COORD_STYLE_AUSTRIAN) 
			sbToString.append(" = ");
		sbToString.append(buildValueString());
  		return sbToString.toString();
    }
	private StringBuffer sbToString = new StringBuffer(50); 
    
	final public String toValueString() {
		return buildValueString().toString();
	}
	
	private StringBuffer buildValueString() {
		sbBuildValueString.setLength(0);
		sbBuildValueString.append("(");		
		switch (toStringMode) {
			case Kernel.COORD_POLAR:                	
			sbBuildValueString.append(kernel.format(GeoVec2D.length(x, y)));
			sbBuildValueString.append("; ");
			sbBuildValueString.append(kernel.formatAngle(Math.atan2(y, x)));
				break;
                    
			default: // CARTESIAN
			sbBuildValueString.append(kernel.format(x));
			switch (kernel.getCoordStyle()) {
				case Kernel.COORD_STYLE_AUSTRIAN:
					sbBuildValueString.append(" | ");
					break;
				
				default:
					sbBuildValueString.append(", ");												
			}
			sbBuildValueString.append(kernel.format(y));
				break;       
		}
		sbBuildValueString.append(")");
		return sbBuildValueString;
	}
	private StringBuffer sbBuildValueString = new StringBuffer(50); 
    
     /**
     * interface VectorValue implementation
     */    
    public GeoVec2D getVector() {        
        GeoVec2D ret = new GeoVec2D(kernel, x, y);
        ret.setMode(toStringMode);
        return ret;
    }        
        
    public boolean isConstant() {
        return false;
    }
    
    public boolean isLeaf() {
        return true;
    }
    
    public HashSet getVariables() {
        HashSet varset = new HashSet();        
        varset.add(this);        
        return varset;          
    }
        
    
    /** POLAR or CARTESIAN */
         
    
    public ExpressionValue evaluate() { return this; }
    
    /**
     * returns all class-specific xml tags for saveXML
     */
    protected String getXMLtags() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.getXMLtags());
		//	line thickness and type  
		sb.append(getLineStyleXML());	  
        
        // polar or cartesian coords
		switch(toStringMode) {
			 case Kernel.COORD_POLAR:
				 sb.append("\t<coordStyle style=\"polar\"/>\n");
				 break;

			 default:
				 sb.append("\t<coordStyle style=\"cartesian\"/>\n");
		}
		
		//	startPoint of vector
		if (startPoint != null) {
			sb.append(startPoint.getStartPointXML());
		}

        return sb.toString();   
    }   
    
	public boolean isNumberValue() {
		return false;
	}

	public boolean isVectorValue() {
		return true;
	}

	public boolean isPolynomialInstance() {
		return false;
	}   

	public boolean isTextValue() {
		return false;
	}   
	
	/* 
	 * Path interface
	 */	 
	
	public boolean isClosedPath() {
		return false;
	}
	
	public void pointChanged(GeoPoint P) {
		if (waitingForStartPoint) {
			// remember waiting points
			if (waitingPointSet == null) waitingPointSet = new HashSet();
			waitingPointSet.add(P);
			return;
		}

		if (pathSegment == null) updatePathSegment();
		pathSegment.pointChanged(P);
	}

	public void pathChanged(GeoPoint P) {		
		updatePathSegment();
		pathSegment.pathChanged(P);
	}
	
	public boolean isOnPath(GeoPoint P, double eps) {
		updatePathSegment(); // Michael Borcherds 2008-06-10 bugfix
		return pathSegment.isOnPath(P, eps);
	}
	
	public boolean isPath() {
		return true;
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
	
	private void initPathSegment() {
		if (startPoint != null) {
			pathStartPoint = startPoint;
		} else {
			pathStartPoint = new GeoPoint(cons);
			pathStartPoint.setCoords(0, 0, 1);
		}

		pathEndPoint = new GeoPoint(cons);
		pathSegment = new GeoSegment(cons, pathStartPoint, pathEndPoint);
	}
	
	private void updatePathSegment() {
		if (pathSegment == null) initPathSegment();

		// update segment
		pathEndPoint.setCoords(pathStartPoint.inhomX + x,
								pathStartPoint.inhomY + y, 
								1.0);
								
		GeoVec3D.lineThroughPoints(pathStartPoint, pathEndPoint, pathSegment);
	}
	
	public boolean isGeoVector() {
		return true;
	}

	
}
