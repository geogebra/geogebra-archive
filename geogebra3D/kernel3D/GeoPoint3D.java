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
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.PathParameter;
import geogebra.kernel.PointProperties;
import geogebra.kernel.Region;
import geogebra.kernel.RegionParameters;
import geogebra.kernel.arithmetic3D.Vector3DValue;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.EuclidianView3D;


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
	private Ggb3DVector willingCoords = null; //= new Ggb3DVector( new double[] {0,0,0,1.0});
	private Ggb3DVector willingDirection = null; //new Ggb3DVector( new double[] {0,0,1,0.0});
	
	//paths
	private Path path;
	private PathParameter pp;
	
	//region
	private Region region;
	private RegionParameters regionParameters;
	/** 2D coord sys when point is on a region */
	//private GeoCoordSys2D coordSys2D = null;
	/** 2D x-coord when point is on a region */
	private double x2D = 0;
	/** 2D y-coord when point is on a region */
	private double y2D = 0;
        
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
    
    
    
    
    public GeoPoint3D(Construction c, Path path) {
		super(c,4);
		setPath(path);
	}
    
    public void setPath(Path path){
    	this.path = path;
    }
    
    
    public GeoPoint3D(Construction c, Region region) {
		super(c,4);
		setRegion(region);
	}
    
    public void setRegion(Region region){
    	this.region = region;

		
    }
    

    

    
    
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
	 * @param v coords
	 * @param doPathOrRegion says if path (or region) calculations have to be done
	 */    
	final public void setCoords(Ggb3DVector v, boolean doPathOrRegion) {
		
		
		
		super.setCoords(v);
		
		updateCoords(); 
		
		//TODO understand a_path
		if (doPathOrRegion){
			
			// region
			if (hasRegion()){
				//Application.printStacktrace(getLabel());
				
				region.pointChangedForRegion(this);
			}
			
			// path
			if (hasPath()) {
				// remember path parameter for undefined case
				//PathParameter tempPathParameter = getTempPathparameter();
				//tempPathParameter.set(getPathParameter());
				path.pointChanged(this);

			}
			updateCoords(); 
		}
		
	}  
	

	
	
	
	
	
	final public void setCoords(Ggb3DVector v) {
		setCoords(v,true);
	}
	
	
	final public void setCoords(double x, double y, double z, double w) {
		
		setWillingCoords(null);
		setCoords(new Ggb3DVector(new double[] {x,y,z,w}));
		
	}  	

	
	
	
	
	
	
	
	
	final public void updateCoords() {
		
		
		
		// infinite point
		if (kernel.isZero(v.get(4))) {
			//Application.debug("infinite");
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
		getLabelMatrix().setOrigin(getCoords());

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
    
    
	public Path getPath() {
		return path;
	}
	

	
	
    final public PathParameter getPathParameter() {
    	if (pp == null)
    		pp = new PathParameter();
    	return pp;
    }	
	
  
	final public void doPath(){
		path.pointChanged(this);
		updateCoords(); 
	}
	

    
    //copied on GeoPoint
	public boolean isChangeable() {
		return !isFixed() && (isIndependent() || isPointOnPath() || hasRegion()); 
	}	
	
    ///////////////////////////////////////////////////////////
    // REGION
 	
	/** says if the point is in a Region
	 * @return true if the point is in a Region
	 */
	final public boolean hasRegion() {
		return region != null;
	}	
	

	final public void doRegion(){
		region.pointChangedForRegion(this);
		
		updateCoords(); 
	}
	
    final public RegionParameters getRegionParameters() {
    	if (regionParameters == null)
    		regionParameters = new RegionParameters();
    	return regionParameters;
    }
    
    final public Region getRegion(){
    	return region;
    }
	
    
    /** set the 2D coord sys where the region lies
     * @param cs 2D coord sys
     */
    /*
    public void setCoordSys2D(GeoCoordSys2D cs){
    	this.coordSys2D = cs;
    }
    */
    
    
    /**
     * update the 2D coords on the region (regarding willing coords and direction)
     */
    public void updateCoords2D(){
    	if (region!=null){ //use region 2D coord sys
    		Ggb3DVector coords;
    		Ggb3DVector[] project;
    		
    		if (getWillingCoords()!=null) //use willing coords
    			coords = getWillingCoords();
    		else //use real coords
    			coords = getCoords();

    		if (getWillingDirection()==null){ //use normal direction for projection
    			project = ((Region3D) region).getNormalProjection(coords);
    			//coords.projectPlane(coordSys2D.getMatrix4x4());
    		}else{ //use willing direction for projection
    			project = ((Region3D) region).getProjection(coords,getWillingDirection());
    			//project = coords.projectPlaneThruV(coordSys2D.getMatrix4x4(),getWillingDirection());
    		}
    			
    		x2D = project[1].get(1);
    		y2D = project[1].get(2);
    		
    	}else{//project on xOy plane
    		x2D = getX();
    		y2D = getY();
    	}
    	
    	//Application.debug("x2D = "+x2D+", y2D = "+y2D);
    			
    }
    
    
    /** set 2D coords
     * @param x x-coord
     * @param y y-coord
     */
    public void setCoords2D(double x, double y){
    	x2D = x;
    	y2D = y;
    }
    
	public double getX2D(){
		return x2D;
	}
	
	public double getY2D(){
		return y2D;
	}
	
	
	public int getMode() { 
		return Kernel.COORD_CARTESIAN; //TODO other modes
	}
	
	
	/**
	 * update 3D coords regarding 2D coords on region coord sys
	 * @param doPathOrRegion says if the path or the region calculations have to be done
	 */
	public void updateCoordsFrom2D(boolean doPathOrRegion){
		setCoords(((Region3D) region).getPoint(getX2D(), getY2D()), doPathOrRegion);
	}
	
	
    ///////////////////////////////////////////////////////////
    // WILLING COORDS
	
	public void setWillingCoords(Ggb3DVector willingCoords){
		this.willingCoords = willingCoords;
	}
	
	public void setWillingCoords(double x, double y, double z, double w){
		setWillingCoords(new Ggb3DVector(new double[] {x,y,z,w}));
	}	
	
	public void setWillingDirection(Ggb3DVector willingDirection){
		this.willingDirection = willingDirection;
	}
	
	public Ggb3DVector getWillingCoords(){
		return willingCoords;
	}
	
	public Ggb3DVector getWillingDirection(){
		return willingDirection;
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
		
		StringBuffer sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = "); 
		
		sbToString.append(toValueString());
		
		return sbToString.toString();  
	}
	
	
	public String toValueString() {
    	if (isInfinite()) 
			return app.getPlain("undefined");
    	
    	
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
		return isInfinite;
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return true;
	}



	public double[] getPointAsDouble() {
		return getInhomCoords().get();
	};
    
    
	
	public boolean getSpreadsheetTrace() {
		return false;
	}

	public Geo3DVec get3DVec() {
		return new Geo3DVec(kernel, getX(), getY(), getZ());
	}
    
    
   

}
