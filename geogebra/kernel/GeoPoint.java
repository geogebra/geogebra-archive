/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoPoint.java
 *
 * The point (x,y) has homogenous coordinates (x,y,1)
 *
 * Created on 30. August 2001, 17:39
 */

package geogebra.kernel;

import geogebra.Application;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.VectorValue;
import geogebra.util.Util;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author  Markus
 * @version 
 */
final public class GeoPoint extends GeoVec3D 
implements VectorValue, 
Translateable, PointRotateable, Mirrorable, Dilateable {   	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int pointSize = EuclidianView.DEFAULT_POINT_SIZE; 
	
	/**
	 * The individual style of this point. If the value is -1, the
	 * global point style is used.
	 */
	private int pointStyle = -1; // Florian Sonner 2008-07-17
	
	private Path path;
	private PathParameter pathParameter;
        
    // temp
    public double inhomX, inhomY;
    private boolean isInfinite, isDefined;
    private boolean showUndefinedInAlgebraView = true;
    
    // list of Locateables (GeoElements) that this point is start point of
    // if this point is removed, the Locateables have to be notified
    private ArrayList locateableList;         
    
    public GeoPoint(Construction c) { 
    	super(c);     	
    	setUndefined(); 
    }
  
    /**
     * Creates new GeoPoint 
     */  
    public GeoPoint(Construction c, String label, double x, double y, double z) {               
        super(c, x, y, z); // GeoVec3D constructor          
        setLabel(label);
    }
    
    public GeoPoint(Construction c, Path path) {
		super(c);
		this.path = path;	
	}
    
    public void setZero() {
    	setCoords(0,0,1);
    }
    
    final public PathParameter getPathParameter() {
    	if (pathParameter == null)
    		pathParameter = new PathParameter();
    	return pathParameter;
    }
    
	protected String getClassName() {
		return "GeoPoint";
	}        
	
    protected String getTypeString() {
		return "Point";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_POINT;
    }
    
    public GeoPoint(GeoPoint point) {
    	super(point.cons);    	
        set(point);        
    }
    
    
    public void set(GeoElement geo) { 
    	if (geo.isGeoPoint()) {
	    	GeoPoint p = (GeoPoint) geo;  
	    	if (p.pathParameter != null) {
	    		pathParameter = getPathParameter();
		    	pathParameter.set(p.pathParameter);
	    	}
	    	setCoords(p.x, p.y, p.z);     
    	}
    	else if (geo.isGeoVector()) {
    		GeoVector v = (GeoVector) geo; 
    		setCoords(v.x, v.y, 1d);   
    	}
    } 
    
    
    public GeoElement copy() {
        return new GeoPoint(this);        
    }                 
       
    /*
	void initSetLabelVisible() {
		setLabelVisible(true);
	}*/
	
	/**
	 * @param i
	 */
	public void setPointSize(int i) {
		pointSize = i;
	}

	/**
	 * @return
	 */
	public int getPointSize() {
		return pointSize;
	}
	
	/**
	 * @author Florian Sonner
	 * @version 2008-07-17
	 */
	final public int getPointStyle() {
		return pointStyle;
	}
	
	/**
	 * @author Florian Sonner
	 * @version 2008-07-17
	 * @param int style the new style to use
	 */
	public void setPointStyle(int style) {switch (style) {
		// TODO: Remove violation of the DRY principle (EucledianView.setPointStyle)
	
		case 0:
		case 1:
		case 2:
			pointStyle = style;
			break;
	
		default:
			pointStyle = -1;
		}
	}
	
	public boolean isChangeable() {
		return !isFixed() && (isPointOnPath() || isIndependent());   
	}	 
	
	final public boolean isPointOnPath() {
		return path != null;
	}
	
	public boolean hasPath() {
		return path != null;
	}
	
	public Path getPath() {
		return path;
	}
	
	public void addToPathParameter(double a) {
		PathParameter pathParameter = getPathParameter();
		pathParameter.t += a;
		
		// update point relative to path
		path.pathChanged(this);
		updateCoords();
	}
	
	/**
	 * Returns true if this point's path is a circle or ellipse 	 
	 *
	public boolean hasAnglePathParameter() {
		return (path != null) && 
					(path instanceof GeoConic) &&
					(((GeoConic)path).isElliptic());		
	}*/
    
    final public boolean isInfinite() {
       return isInfinite;  
    }
    
    final public boolean isFinite() {
       return isDefined && !isInfinite;
    }
    
    final public boolean showInEuclidianView() {               
    	return isDefined && !isInfinite;
    }    
    
    final protected boolean showInAlgebraView() {
        // intersection points
        return isDefined || showUndefinedInAlgebraView;
    }   
    
	final public boolean isDefined() { 
		return isDefined;        
	}     
	
	final public boolean isFixable() {
		return path != null || super.isFixable();
	}		
    
	/** Sets homogenous coordinates and updates
	 * inhomogenous coordinates
	 */
	final public void setCoords(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;	
		
		// update point on path: this may change coords
		// so updateCoords() is called afterwards
		if (path != null) {
			// remember path parameter for undefined case
			PathParameter tempPathParameter = getTempPathparameter();
			tempPathParameter.set(getPathParameter());
			path.pointChanged(this);
		}
			
		// this avoids multiple computation of inhomogenous coords;
		// see for example distance()
		updateCoords();  
		
		// undefined and on path: remember old path parameter
		if (!isDefined && path != null) {
			PathParameter pathParameter = getPathParameter();
			PathParameter tempPathParameter = getTempPathparameter();
			pathParameter.set(tempPathParameter);
		}				
	}  
	
	private PathParameter tempPathParameter;
	private PathParameter getTempPathparameter() {
		if (tempPathParameter == null) {
			tempPathParameter = new PathParameter();
		}
		return tempPathParameter;
	}
	
	final public void updateCoords() {
		// infinite point
		if (kernel.isZero(z)) {
			isInfinite = true;
			isDefined = !(Double.isNaN(x) || Double.isNaN(y));
			inhomX = Double.NaN;
			inhomY = Double.NaN;
		} 
		// finite point
		else {
			isInfinite = false;
			isDefined = !(Double.isNaN(x) || Double.isNaN(y)
                    					  || Double.isNaN(z));
		
			if (isDefined) {
				// make sure the z coordinate is always positive
				// this is important for the orientation of a line or ray
				// computed using two points P, Q with cross(P, Q)
				if (z < 0) {
					x = -x;
					y = -y;
					z = -z;		
				} 
				
				// update inhomogenous coords
				if (z == 1.0) {
					inhomX = x;
					inhomY = y;
			    } else {        
					inhomX = x / z;
					inhomY = y / z;                              
			    }
			} else {
				inhomX = Double.NaN;
				inhomY = Double.NaN;
			}
		}
	}
	
	final public void setPolarCoords(double r, double phi) {        
	   setCoords( r * Math.cos( phi ), r * Math.sin( phi ), 1.0d);        
	}   
	
	final public void setCoords(GeoVec3D v) {
		 setCoords(v.x, v.y, v.z);
	 } 
	 
	final public void setCoords(GeoVec2D v) {
		setCoords(v.x, v.y, 1.0);
	}  
	
 
    
    /** 
     * Yields true if the inhomogenous coordinates of this point are equal to
     * those of point P. Infinite points are checked for linear dependency.
     */
	// Michael Borcherds 2008-04-30
    final public boolean isEqual(GeoElement geo) {
    	
    	if (!geo.isGeoPoint()) return false;
    	
    	GeoPoint P = (GeoPoint)geo;
    	
        if (!(isDefined() && P.isDefined())) return false;                        
        
        // both finite
        if (isFinite() && P.isFinite())
			return kernel.isEqual(inhomX, P.inhomX) && 
                    	kernel.isEqual(inhomY, P.inhomY);
		else if (isInfinite() && P.isInfinite())
			return linDep(P);
		else return false;                        
    }
        
    /** 
     * Writes (x/z, y/z) to res.
     */
    final public void getInhomCoords(double [] res) {
       	res[0] = inhomX;
       	res[1] = inhomY;
    }        	
        
    // euclidian distance between this GeoPoint and P
    final public double distance(GeoPoint P) {       
        return GeoVec2D.length(	P.inhomX - inhomX, 
        						P.inhomY - inhomY);
    }            
    
    /** returns the square distance of this point and P (may return
     * infinty or NaN).            
     */
    final public double distanceSqr(GeoPoint P) {          
        double vx = P.inhomX - inhomX;
        double vy = P.inhomY - inhomY;        
        return vx*vx + vy*vy;
    }
    
    /** 
     * Returns whether the three points A, B and C are collinear. 
     */
	public static boolean collinear(GeoPoint A, GeoPoint B, GeoPoint C) {
		// A, B, C are collinear iff det(ABC) == 0
		
		// calculate the determinante of ABC
		// det(ABC) = sum1 - sum2		
		double sum1 = A.x * B.y * C.z + 
					  B.x * C.y * A.z +
					  C.x * A.y * B.z;
		double sum2 = A.z * B.y * C.x +
					  B.z * C.y * A.x +
					  C.z * A.y * B.x;
				
		// det(ABC) == 0  <=>  sum1 == sum2		
		return Kernel.isEqual(sum1, sum2, Kernel.MIN_PRECISION);
	}
    
    /**
     * Calcs determinant of P and Q. Note: no test for defined or infinite is done here.
     */
	public static final double det(GeoPoint P, GeoPoint Q) {		 
		return (P.x * Q.y - Q.x * P.y) / (P.z * Q.z); 
	}	
	
	/**
	 * Returns the affine ratio for three collinear points A, B and C. 
	 * The ratio is lambda with C = A + lambda * AB, i.e. lambda = AC/AB.
	 * Note: the collinearity is not checked in this method.
	 */
	public static final double affineRatio(GeoPoint A, GeoPoint B, GeoPoint C) {		
		double ABx = B.inhomX - A.inhomX;
		double ABy = B.inhomY - A.inhomY;
		
		// avoid division by a number close to zero
		if (Math.abs(ABx) > Math.abs(ABy)) {
			return (C.inhomX - A.inhomX) / ABx;
		} else {
			return (C.inhomY - A.inhomY) / ABy;
		}		
	}
    
/***********************************************************
 * MOVEMENTS
 ***********************************************************/
    
    /**
     * translate by vector v
     */
    final public void translate(GeoVector v) {        
        setCoords(x + v.x * z,
        				y + v.y * z,
        				z);        
    }        
    
	final public boolean isTranslateable() {
		return true;
	}
    
    /**
     * dilate from S by r
     */
    final public void dilate(NumberValue rval, GeoPoint S) {  
       double r = rval.getDouble();	
       double temp = (1 - r);
       setCoords(r * x + temp * S.inhomX * z,
       			 r * y + temp * S.inhomY * z,
				 z);    
    } 
    
    /**
     * rotate this point by angle phi around (0,0)
     */
    final public void rotate(NumberValue phiValue) {
    	double phi = phiValue.getDouble();
        double cos = Math.cos(phi);
        double sin = Math.sin(phi);
        
        setCoords( x * cos - y * sin,
      					 x * sin + y * cos,
      					 z );
    }
        
    /**
     * rotate this point by angle phi around Q
     */    
    final public void rotate(NumberValue phiValue, GeoPoint Q) {
    	double phi = phiValue.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);   
        double qx = z * Q.inhomX;
        double qy = z * Q.inhomY;
        
		setCoords( (x - qx) * cos + (qy - y) * sin + qx,
        				 (x - qx) * sin + (y - qy) * cos + qy,
      					 z );         
    }
    
    /**
     * mirror this point at point Q
     */
    final public void mirror(GeoPoint Q) {
		double qx = z * Q.inhomX;
		double qy = z * Q.inhomY;
        
        setCoords( 2.0 * qx - x,
        				 2.0 * qy - y,
        				 z );
    }
    
// Michael Borcherds 2008-02-10
    final public void mirror(GeoConic c) {
    	if (c.getType()==GeoConic.CONIC_CIRCLE)
    	{ // Mirror point in circle
    		double r =  c.getHalfAxes()[0];
    		GeoVec2D midpoint=c.getTranslationVector();
    		double a=midpoint.x;
    		double b=midpoint.y;
    		double sf=r*r/((inhomX-a)*(inhomX-a)+(inhomY-b)*(inhomY-b));
            setCoords( a+sf*(inhomX-a), b+sf*(inhomY-b) ,1.0);
    	}
    	else
    	{
    		setUndefined();
    	}
    }
    
    /**
     * mirror this point at line g
     */
    final public void mirror(GeoLine g) {
        // Y = S(phi).(X - Q) + Q
        // where Q is a point on g, S(phi) is the mirrorTransform(phi)
        // and phi/2 is the line's slope angle
        
        // get arbitrary point of line
        double qx, qy; 
        if (Math.abs(g.x) > Math.abs(g.y)) {
            qx = -z * g.z / g.x;
            qy = 0.0d;
        } else {
            qx = 0.0d;
            qy = -z * g.z / g.y;
        }
        
        // translate -Q
        x -= qx;
        y -= qy;        
        
        // S(phi)        
        mirror(2.0 * Math.atan2(-g.x, g.y));
        
        // translate back +Q
        x += qx;
        y += qy;
        
         // update inhom coords
         updateCoords();
    }
    
    /**
     * mirror transform with angle phi
     *  [ cos(phi)       sin(phi)   ]
     *  [ sin(phi)      -cos(phi)   ]  
     */
    final private void mirror(double phi) {
        double cos = Math.cos(phi);
        double sin = Math.sin(phi);
                
        double x0 = x * cos + y * sin;
        y = x * sin - y * cos;
        x = x0;        
    }
 
/***********************************************************/
    
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
    	if (isInfinite()) {
			sbBuildValueString.append(app.getPlain("undefined"));
			return sbBuildValueString;
    	}
    				
        switch (toStringMode) {
        case Kernel.COORD_POLAR:                                            
    		sbBuildValueString.append('(');    
			sbBuildValueString.append(kernel.format(GeoVec2D.length(inhomX, inhomY)));
			sbBuildValueString.append("; ");
			sbBuildValueString.append(kernel.formatAngle(Math.atan2(inhomY, inhomX)));
			sbBuildValueString.append(')');
            break;                                
                        
        case Kernel.COORD_COMPLEX:                                            
			sbBuildValueString.append(kernel.format(inhomX));
			sbBuildValueString.append(" ");
			sbBuildValueString.append(kernel.formatSigned(inhomY));
			sbBuildValueString.append("i");
            break;                                
                        
           default: // CARTESIAN                
       			sbBuildValueString.append('(');    
				sbBuildValueString.append(kernel.format(inhomX));
				switch (kernel.getCoordStyle()) {
					case Kernel.COORD_STYLE_AUSTRIAN:
						sbBuildValueString.append(" | ");
						break;
					
					default:
						sbBuildValueString.append(", ");												
				}
				sbBuildValueString.append(kernel.format(inhomY));                                
				sbBuildValueString.append(')');
        }        
		return sbBuildValueString;
    }
	private StringBuffer sbBuildValueString = new StringBuffer(50);   
    
    /**
     * interface VectorValue implementation
     */    
    public GeoVec2D getVector() {
        GeoVec2D ret = new GeoVec2D(kernel, inhomX, inhomY);
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
     * GeoGebra File Format
     */
    protected String getXMLtags() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.getXMLtags()); 
        
        /* should not be needed
        if (path != null) {        	
        	pathParameter.appendXML(sb);
        }*/
        	       
        // polar or cartesian coords
        switch(toStringMode) {
        case Kernel.COORD_POLAR:
            sb.append("\t<coordStyle style=\"polar\"/>\n");
            break;

        case Kernel.COORD_COMPLEX:
            sb.append("\t<coordStyle style=\"complex\"/>\n");
            break;

            default:
                sb.append("\t<coordStyle style=\"cartesian\"/>\n");
        }
        
		// point size
		sb.append("\t<pointSize val=\"");
			sb.append(pointSize);
		sb.append("\"/>\n");
		
    
		// point style, Florian Sonner 2008-07-17
		sb.append("\t<pointStyle val=\"");
			sb.append(pointStyle);
		sb.append("\"/>\n");
            
        return sb.toString();   
    }
    
    public String getStartPointXML() {
    	StringBuffer sb = new StringBuffer();    	
		sb.append("\t<startPoint ");
		
    	if (isAbsoluteStartPoint()) {		
			sb.append(" x=\"" + x + "\"");
			sb.append(" y=\"" + y + "\"");
			sb.append(" z=\"" + z + "\"");			
    	} else {
			sb.append("exp=\"");
			boolean oldValue = kernel.isTranslateCommandName();
			kernel.setTranslateCommandName(false);
			sb.append(Util.encodeXML(getLabel()));
			kernel.setTranslateCommandName(oldValue);
			sb.append("\"");			    	
    	}
		sb.append("/>\n");
		return sb.toString();
    }
    
	final public boolean isAbsoluteStartPoint() {
		return isIndependent() && !isLabelSet();
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
	
	 
	/**
	 * Calls super.update() and updateCascade() for all registered locateables.	 
	 */
	public void update() {  	
		super.update();
				
		// update all registered locatables (they have this point as start point)
		if (locateableList != null) {
			for (int i=0; i < locateableList.size(); i++) {
				Locateable loc = (Locateable) locateableList.get(i);
				loc.toGeoElement().updateCascade();													
			}		
		}		
		EuclidianView view = kernel.getApplication().getEuclidianView();
        // record to spreadsheet tool
    	if (this == view.getEuclidianController().recordObject) {
	    	double [] coords = new double[2];
	    	getInhomCoords(coords);
	    	StringBuffer command = new StringBuffer();
	    	
	    	String row = view.getTraceRow() + "";
	    	
	    	// x coord
	    	command.append(getTraceColumn1());
	    	command.append(row);
	    	command.append("=");
	    	command.append(coords[0]);
	    	//Application.debug(command.toString());
	    	try {
					GeoElement [] geos = view.getKernel().getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(command.toString(), false);
		   	 		    geos[0].setAuxiliaryObject(true);
	    	}
	    	catch (Exception e) {}
	    	
	    	// y coord
	    	command.setLength(0);
	    	command.append(getTraceColumn2());
	    	command.append(row);
	    	command.append("=");
	    	command.append(coords[1]);
	    	try {
					GeoElement [] geos = view.getKernel().getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(command.toString(), false);
		   	 		    geos[0].setAuxiliaryObject(true);
	    	}
	    	catch (Exception e) {}
    	}    	
	}
	
	
	/**
	 * Tells this point that the given Locateable has this point
	 * as start point.
	 */
	public void registerLocateable(Locateable l) {	
		if (locateableList == null) locateableList = new ArrayList();
		if (locateableList.contains(l)) return;
		
		// add only locateables that are not already
		// part of the updateSet of this point
		AlgoElement parentAlgo = l.toGeoElement().getParentAlgorithm();
		if (parentAlgo == null ||
			!(getAlgoUpdateSet().contains(parentAlgo))) {
			// add the locatable
			locateableList.add(l);			
		}
	}
	
	/**
	 * Tells this point that the given Locatable no longer has this point
	 * as start point.
	 */
	public void unregisterLocateable(Locateable l) {
		if (locateableList != null) {
			locateableList.remove(l);			
		}
	}
	
	/**
	 * Tells Locateables that their start point is removed
	 * and calls super.remove()
	 */
	protected void doRemove() {
		if (locateableList != null) {
			// copy locateableList into array
			Object [] locs = locateableList.toArray();	
			locateableList.clear();
			
			// tell all locateables 
			for (int i=0; i < locs.length; i++) {		
				Locateable loc = (Locateable) locs[i];
				loc.removeStartPoint(this);				
				loc.toGeoElement().updateCascade();			
			}			
		}
		
		super.doRemove();
	}
	
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);
		
		if (geo.isGeoPoint()) {
			pointSize = ((GeoPoint) geo).pointSize;
		}
	}
	
    
	final public boolean isGeoPoint() {
		return true;
	}
	
	void showUndefinedInAlgebraView(boolean flag) {
		showUndefinedInAlgebraView = flag;
	}

	
	private String traceColumn1 = "";
	private String traceColumn2 = "";
	
	public String getTraceColumn1() {
		if (traceColumn1.equals("")) traceColumn1 = kernel.getApplication().getEuclidianView().getNextTraceColumn();
		return traceColumn1;
	}
		
	public String getTraceColumn2() {
		if (traceColumn2.equals("")) traceColumn2 = kernel.getApplication().getEuclidianView().getNextTraceColumn();
		return traceColumn2;
	}
		
}
