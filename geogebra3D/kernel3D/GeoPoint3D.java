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
import geogebra.kernel.PathParameter;
import geogebra.kernel.PointProperties;
import geogebra.kernel.arithmetic3D.Vector3DValue;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DVector;


/**
 *
 * @author  Markus + ggb3D
 * @version 
 */
final public class GeoPoint3D extends GeoVec4D
implements GeoPointInterface, PointProperties, Vector3DValue{   	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private boolean isInfinite,isDefined;
	public int pointSize = EuclidianView.DEFAULT_POINT_SIZE; 
	
	
	//mouse moving
	private Ggb3DVector mouseLoc = null; //= new Ggb3DVector( new double[] {0,0,0,1.0});
	private Ggb3DVector mouseDirection = null; //new Ggb3DVector( new double[] {0,0,1,0.0});
	
	//paths
	private Path3D path;
	private PathParameter pp;
        
    // temp
    public Ggb3DVector inhom = new Ggb3DVector(3);


    /** says if decoration have to be drawn for coordinates */
	private boolean coordDecoration = false;
    
    
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
		//Application.debug("GeoPoint3D");
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
	
	
	final public void doPath(){
		path.pointChanged(this);
		updateCoords(); 
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
		getDrawingMatrix().setOrigin(getCoords());

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
	
	final public boolean isPointOnPath() {
		return path != null;
	}
    
    
	public Path3D getPath() {
		return path;
	}
	


	
	
    final public PathParameter getPathParameter() {
    	if (pp == null)
    		pp = new PathParameter();
    	return pp;
    }	
	
  
	
	// adding hasPath() condition to be independent
	// because 3D points that have a Path have an algoParent
    /*
	public boolean isIndependent() {
		return super.isIndependent() || hasPath();
	}
	*/
    
    //copied on GeoPoint
	public boolean isChangeable() {
		return !isFixed() && (isIndependent() || isPointOnPath());// || isPointInRegion()); 
	}	
	
    ///////////////////////////////////////////////////////////
    // TODO REGION
 	
	/** says if the point is in a Region
	 * @return true if the point is in a Region
	 */
	public boolean hasRegion() {
		return false;
	}
	
	
	
    ///////////////////////////////////////////////////////////
    // MOUSE
	
	public void setMouseLoc(Ggb3DVector mouseLoc){
		this.mouseLoc = mouseLoc;
	}
	
	public void setMouseDirection(Ggb3DVector mouseDirection){
		this.mouseDirection = mouseDirection;
	}
	
	public Ggb3DVector getMouseLoc(){
		return mouseLoc;
	}
	
	public Ggb3DVector getMouseDirection(){
		return mouseDirection;
	}
	
 
    
    ///////////////////////////////////////////////////////////
    // DRAWING

	public void setCoordDecoration(boolean val){
		coordDecoration = val;
	}
    
    public boolean hasCoordDecoration(){
    	return coordDecoration;
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

		return isDefined;
	}
	
	public void set(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}
	public void setUndefined() {
		isDefined = false;
		
	}
	
	public boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return true;
	}
	public boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	final public String toString() {
		
		String s = getLabel();
		
		s+=" = "; //TODO use kernel property
		
		s+=toValueString();
		
		return s;
	}
	
	
	public String toValueString() {
		//TODO point undefined...
		//TODO use point property
		return "("+kernel.format(inhom.get(1))+", "+kernel.format(inhom.get(2))+", "+kernel.format(inhom.get(3))+")";
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
		
	}

	
	
	
	
	
	//////////////////////////////////
	// GeoPointInterface interface
	
	
	public boolean isInfinite() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return true;
	}



	public double[] getPointAsDouble() {
		return getInhomCoords().get();
	};
    
    
    
    
   

}
