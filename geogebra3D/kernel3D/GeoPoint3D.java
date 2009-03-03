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
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.PointProperties;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DVector;


/**
 *
 * @author  Markus + ggb3D
 * @version 
 */
final public class GeoPoint3D extends GeoVec4D
implements GeoPointInterface, PointProperties{   	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private boolean isInfinite,isDefined;
	public int pointSize = EuclidianView.DEFAULT_POINT_SIZE; 
	
	//paths
	private Path3D path;
	private PathParameters pps;
        
    // temp
    public Ggb3DVector inhom = new Ggb3DVector(3);
    
    
    public GeoPoint3D(Construction c) { 
    	super(c,4); 
    	setUndefined(); 
    }
  
    /**
     * Creates new GeoPoint 
     */  
    public GeoPoint3D(Construction c, String label, double x, double y, double z, double w) {               
        super(c, x, y, z, w); // GeoVec4D constructor  
        setLabel(label);
        
    }
    
    public GeoPoint3D(Construction c, String label, Ggb3DVector v){
    	this(c,label,v.get(1),v.get(2),v.get(3),v.get(4));
    }
    
    
    
    
    public GeoPoint3D(Construction c, Path3D path) {
		super(c,4);
		this.path = path;
	}
    
    /*
    public GeoPoint3D(Construction c, PathIn path) {
		super(c,4);
		this.pathIn = path;
	}    
    */
    

    

    
    
    ///////////////////////////////////////////////////////////
    // COORDINATES
    
    
    public double getX(){
    	return getCoords().get(1);
    }
    public double getY(){
    	return getCoords().get(2);
    }
    public double getZ(){
    	return getCoords().get(3);
    }
    
    
    

    
	/** Sets homogenous coordinates and updates
	 * inhomogenous coordinates
	 */
	final public void setCoords(Ggb3DVector v, boolean a_path) {
		
		super.setCoords(v);
		updateCoords(); 
		
		if (a_path){
			if (hasPath()) {
				// remember path parameter for undefined case
				//PathParameter tempPathParameter = getTempPathparameter();
				//tempPathParameter.set(getPathParameter());
				path.pointChanged(this);
			/*
			} else if (hasPathIn()) {
				pathIn.pointChanged(this);
				*/
			}
			updateCoords(); 
		}
		
		 
		
	}  
	
	
	
	final public void setCoords(Ggb3DVector v) {
		setCoords(v,true);
	}
	
	
	final public void setCoords(double x, double y, double z, double w) {
		
		setCoords(new Ggb3DVector(new double[] {x,y,z,w}));
		
	}  	

	
	
	
	
	
	
	
	
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
		
		//sets the drawing matrix to coords
		getDrawingMatrix().set(getCoords(), 4);

	}
	 
	final public void setCoords(GeoVec3D v) {
		setCoords(v.x, v.y, v.z, 1.0);
	}  
          
    /** 
     * Returns (x/w, y/w, z/w) GgbVector.
     */
    final public Ggb3DVector getInhomCoords() {
    	return inhom.copyVector();
    }        	
        
  
    
    
    
    
    
    

    
   
    
    
    
    
    
    
    
    
    
    
    
    
    
    ///////////////////////////////////////////////////////////
    // PATHS
    
	public boolean hasPath() {
		return path != null;
	}
	

    
    
	public Path3D getPath() {
		return path;
	}
	

	

    
    /*
	public boolean hasPathIn() {
		return pathIn != null;
	}
	

    
    
	public PathIn getPathIn() {
		return pathIn;
	}
	*/
	
    final public PathParameters getPathParameters(int n) {
    	if (pps == null)
    		pps = new PathParameters(n);
    	return pps;
    }	
	
  
	
	// adding hasPath() condition to be independent
	// because 3D points that have a Path have an algoParent
	public boolean isIndependent() {
		return super.isIndependent() || hasPath();
	}
	
	
	
	
	
 
    

    
    
    
    
    
	
	
    ///////////////////////////////////////////////////////////
    // COMMON STUFF
   
    
	protected String getClassName() {
		return "GeoPoint3D";
	}        
	
    protected String getTypeString() {
		return "Point3D";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_POINT3D;  
    }
    
    public GeoPoint3D(GeoPoint3D point) {
    	super(point.cons);
        set(point);        
    }
    

    
    
    public GeoElement copy() {
        return new GeoPoint3D(this);        
    }                 
       
	
	final public boolean isGeoPoint() {
		return true;
	}


	public boolean isDefined() {
		// TODO Auto-generated method stub
		return true;
	}
	public void set(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}
	public void setUndefined() {
		// TODO Auto-generated method stub
		
	}
	protected boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return true;
	}
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	final public String toString() {
		
		String s = getLabel();
		
		s+=" = "; //TODO use kernel property
		
		
		
		//TODO point undefined...
		//TODO use point property
		s+="("+kernel.format(inhom.get(1))+", "+kernel.format(inhom.get(2))+", "+kernel.format(inhom.get(3))+")";
		
		return s;
	}
	
	
	public String toValueString() {
		// TODO Auto-generated method stub
		return "todo";
	}




	public boolean isEqual(GeoElement Geo) {
		// TODO Raccord de méthode auto-généré
		return false;
	}

	
	/**
	 * Returns whether this point has three changeable numbers as coordinates, 
	 * e.g. point A = (a, b, c) where a, b and c are free GeoNumeric objects.
	 */	
	public boolean hasChangeableCoordParentNumbers() {
		return false;
	}
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////
	// PointProperties
	
	

	public int getPointSize() {
		return pointSize;
	}

	public int getPointStyle() {
		//TODO
		return 0;
	}

	public void setPointSize(int size) {
		pointSize = size;		
	}

	public void setPointStyle(int type) {
		// TODO 
		
	};
    
    
    
    
   

}
