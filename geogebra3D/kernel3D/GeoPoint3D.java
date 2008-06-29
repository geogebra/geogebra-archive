/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * GeoPoint.java
 *
 * The point (x,y) has homogenous coordinates (x,y,1)
 *
 * Created on 30. August 2001, 17:39
 */

package geogebra3D.kernel3D;

import geogebra.euclidian.EuclidianView;

import geogebra.kernel.*;
import geogebra.kernel.linalg.GgbVector;

import java.util.ArrayList;


/**
 *
 * @author  Markus + Mathieu
 * @version 
 */
final public class GeoPoint3D extends GeoVec4D {   	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int pointSize = EuclidianView.DEFAULT_POINT_SIZE; 
	
	private Path path;
	PathParameter pathParameter;
        
    // temp
    public GgbVector inhom = new GgbVector(3);
    private boolean isInfinite, isDefined;
    private boolean showUndefinedInAlgebraView = true;
    
    // list of Locateables (GeoElements) that this point is start point of
    // if this point is removed, the Locateables have to be notified
    private ArrayList locateableList;         
    
    public GeoPoint3D(Construction c) { 
    	super(c,4); 
    	pathParameter = new PathParameter();
    	setUndefined(); 
    }
  
    /**
     * Creates new GeoPoint 
     */  
    public GeoPoint3D(Construction c, String label, double x, double y, double z, double w) {               
        super(c, x, y, z, w); // GeoVec3D constructor  
        pathParameter = new PathParameter();
        setLabel(label);
    }
    
    public GeoPoint3D(Construction c, String label, GgbVector v){
    	this(c,label,v.get(1),v.get(2),v.get(3),v.get(4));
    }
    
    public GeoPoint3D(Construction c, Path path) {
		super(c);
		this.path = path;
		pathParameter = new PathParameter();
	}
    
	protected String getClassName() {
		return "GeoPoint3D";
	}        
	
    protected String getTypeString() {
		return "Point3D";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_POINT3D; //TODO GEO_CLASS_POINT3D
    }
    
    public GeoPoint3D(GeoPoint3D point) {
    	super(point.cons);
    	pathParameter = new PathParameter();
        set(point);        
    }
    
    // TODO
    public void set(GeoElement geo) { }
    /*
    	if (geo.isGeoPoint()) {
	    	GeoPoint3D p = (GeoPoint3D) geo;        
	    	pathParameter.set(p.pathParameter);
	    	setCoords(p.x, p.y, p.z);     
    	}
    	else if (geo.isGeoVector()) {
    		GeoVector v = (GeoVector) geo; 
    		setCoords(v.x, v.y, 1d);   
    	}
    }
    */ 
    
    
    public GeoElement copy() {
        return new GeoPoint3D(this);        
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
		pathParameter.setT(pathParameter.getT()+a);
		
		// update point relative to path
		//TODO path.pathChanged(this);
		updateCoords();
	}
	
	public void initPathParameter(PathParameter pp) {
		pathParameter = pp;		
		
		// update point relative to path
		//TODO path.pathChanged(this);
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
    
    protected final boolean showInAlgebraView() {
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
	final public void setCoords(double x, double y, double z, double w) {
		
		//double[] coords = {x,y,z,w};		
		//super.setCoords(coords);
		super.setCoords(new double[] {x,y,z,w});
		// update point on path: this may change coords
		// so updateCoords() is called afterwards
		if (path != null) {
			// remember path parameter for undefined case
			tempPathParameter.set(pathParameter);
			//TODO path.pointChanged(this);
		}
			
		// this avoids multiple computation of inhomogenous coords;
		// see for example distance()
		updateCoords();  
		
		// undefined and on path: remember old path parameter
		if (!(isDefined || path == null)) {
			pathParameter.set(tempPathParameter);
		}				
	}  
	private PathParameter tempPathParameter = new PathParameter();
	
	final public void updateCoords() {
		// infinite point
		if (kernel.isZero(v.get(4))) {
			isInfinite = true;
			isDefined = !(Double.isNaN(v.get(1)) || Double.isNaN(v.get(2)) || Double.isNaN(v.get(3)));
			inhom.set(Double.NaN);
		} 
		// finite point
		else {
			isInfinite = false;
			isDefined = v.isDefined();
		
			if (isDefined) {
				// make sure the z coordinate is always positive
				// this is important for the orientation of a line or ray
				// computed using two points P, Q with cross(P, Q)
				//TODO cast in GgbVector				
				if (v.get(4) < 0) {
					for(int i=1;i<=4;i++)
						v.set(i,(v.get(i))*(-1.0));
				} 
				
				
				// update inhomogenous coords
				if (v.get(4) == 1.0) {
					inhom.set(1,v.get(1));
					inhom.set(2,v.get(2));
					inhom.set(3,v.get(3));
			    } else {        
					inhom.set(1,v.get(1)/v.get(4));
					inhom.set(2,v.get(2)/v.get(4));
					inhom.set(3,v.get(3)/v.get(4));
			    }
			} else {
				inhom.set(Double.NaN);
			}
		}
	}
	
	
	 
	final public void setCoords(GeoVec3D v) {
		setCoords(v.x, v.y, v.z, 1.0);
	}  
	
 
          
    /** 
     * Returns (x/w, y/w, z/w) GgbVector.
     */
    final public GgbVector getInhomCoords() {
    	return inhom.copyVector();
    }        	
        
  

    
    
    
/***********************************************************/
    
    final public String toString() {     
		sbToString.setLength(0);                               
		sbToString.append(label);		
		if (kernel.getCoordStyle() != Kernel.COORD_STYLE_AUSTRIAN) {
			sbToString.append(" = ");
		}
		sbToString.append(buildValueString());       
        return sbToString.toString();
    }
    private StringBuffer sbToString = new StringBuffer(50);        
    
    
	private StringBuffer buildValueString() { 
		sbBuildValueString.setLength(0);
    	if (isInfinite()) {
			sbBuildValueString.append(app.getPlain("undefined"));
			return sbBuildValueString;
    	}
    				
		sbBuildValueString.append('(');    
        switch (toStringMode) {
            case Kernel.COORD_POLAR:                                            
				sbBuildValueString.append(kernel.format(GeoVec2D.length(inhom.get(1), inhom.get(2))));
				sbBuildValueString.append("; ");
				sbBuildValueString.append(kernel.formatAngle(Math.atan2(inhom.get(1), inhom.get(2))));
                break;                                
                            
            default: // CARTESIAN                
				sbBuildValueString.append(kernel.format(inhom.get(1)));
				switch (kernel.getCoordStyle()) {
					case Kernel.COORD_STYLE_AUSTRIAN:
						sbBuildValueString.append(" | ");
						break;
					
					default:
						sbBuildValueString.append(", ");												
				}
				sbBuildValueString.append(kernel.format(inhom.get(2)));   
				switch (kernel.getCoordStyle()) {
				case Kernel.COORD_STYLE_AUSTRIAN:
					sbBuildValueString.append(" | ");
					break;
				
				default:
					sbBuildValueString.append(", ");												
				}
				sbBuildValueString.append(kernel.format(inhom.get(3)));                                
        }        
		sbBuildValueString.append(')');
		return sbBuildValueString;
    }
	private StringBuffer sbBuildValueString = new StringBuffer(50);   
     
    
    final public String toValueString() {
    	return buildValueString().toString();	
    }

	public boolean isEqual(GeoElement Geo) {
		// TODO Raccord de méthode auto-généré
		return false;
	}       

    
	
}
