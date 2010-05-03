/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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

import java.awt.geom.Point2D;
import java.util.TreeSet;

import geogebra.Matrix.GgbVector;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.LocateableList;
import geogebra.kernel.Path;
import geogebra.kernel.PathParameter;
import geogebra.kernel.PointProperties;
import geogebra.kernel.Region;
import geogebra.kernel.RegionParameters;
import geogebra.kernel.arithmetic3D.Vector3DValue;
import geogebra.main.Application;
import geogebra.util.Util;


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
	private GgbVector willingCoords = null; //= new Ggb3DVector( new double[] {0,0,0,1.0});
	private GgbVector willingDirection = null; //new Ggb3DVector( new double[] {0,0,1,0.0});
	
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
    public GgbVector inhom = new GgbVector(3);


    // list of Locateables (GeoElements) that this point is start point of
    // if this point is removed, the Locateables have to be notified
    private LocateableList locateableList;         

    
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
    
    public GeoPoint3D(Construction c, String label, GgbVector v){
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
    // GEOPOINTINTERFACE (TODO move it to GeoPointND)
    
    public double distance(GeoPointInterface P){
    	return distance((GeoPoint3D) P);
    }
    
    // euclidian distance between this GeoPoint3D and P
    final public double distance(GeoPoint3D P) {       
        return getInhomCoords().distance(P.getInhomCoords());
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
	final public void setCoords(GgbVector v, boolean doPathOrRegion) {
		
		
		
		super.setCoords(v);
		
		updateCoords(); 
		
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
	

	
	
	
	
	
	final public void setCoords(GgbVector v) {
		setCoords(v,true);
	}
	
	
	final public void setCoords(double x, double y, double z, double w) {
		
		setWillingCoords(null);
		setCoords(new GgbVector(new double[] {x,y,z,w}));
		
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
    final public GgbVector getInhomCoords() {
    	return inhom.copyVector();
    }   
    
    
    final public double[] vectorTo(GeoPointInterface QI){
    	GeoPoint3D Q = (GeoPoint3D) QI;
    	//Application.debug("v=\n"+Q.getCoords().sub(getCoords()).get());
    	return Q.getCoords().sub(getCoords()).get();
    }
        
  
    
    
    
    
    
    

    
   
    
    
    
    
    
    
    
    
    

	protected boolean movePoint(GgbVector rwTransVec, Point2D.Double endPosition) {
	
		boolean movedGeo = false;
		
		if (endPosition != null) {					
			//setCoords(endPosition.x, endPosition.y, 1);
			//movedGeo = true;
		} 
		
		// translate point
		else {	
			
								
			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			/*
			if (Math.abs(rwTransVec.getX()) > Kernel.MIN_PRECISION)
				x  = kernel.checkDecimalFraction(x);
			if (Math.abs(rwTransVec.getY()) > Kernel.MIN_PRECISION) 
				y = kernel.checkDecimalFraction(y);
				*/
				
			// set translated point coords
			if (hasPath()){
				double t=
					getPathParameter().getT()
					+rwTransVec.getX()
					+rwTransVec.getY()
					+rwTransVec.getZ()
					;
				//TODO use path unit and direction
				getPathParameter().setT(t);
				getParentAlgorithm().update();
			}else if (hasRegion()){
				/* TODO make this work :)
				double x = getRegionParameters().getT1() +rwTransVec.getX();
				double y = getRegionParameters().getT2() +rwTransVec.getY();
				getRegionParameters().setT1(x);getRegionParameters().setT2(y);
				Application.debug("(x,y)="+x+","+y);
				//TODO use path unit and direction
				getParentAlgorithm().update();
				*/
			}else{
				GgbVector coords = (GgbVector) getInhomCoords().add(rwTransVec);
				setCoords(coords);	
			}
			
			movedGeo = true;
		}
		
		
		
		return movedGeo;
	
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
    		GgbVector coords;
    		GgbVector[] project;
    		
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
	
	public void setWillingCoords(GgbVector willingCoords){
		this.willingCoords = willingCoords;
	}
	
	public void setWillingCoords(double x, double y, double z, double w){
		setWillingCoords(new GgbVector(new double[] {x,y,z,w}));
	}	
	
	public void setWillingDirection(GgbVector willingDirection){
		this.willingDirection = willingDirection;
	}
	
	public GgbVector getWillingCoords(){
		return willingCoords;
	}
	
	public GgbVector getWillingDirection(){
		return willingDirection;
	}
	
 
    
    ///////////////////////////////////////////////////////////
    // FREE UP THE POINT
	
	/**
	 * free up the point from is region (TODO path, other algorithms)
	 */
	public void freeUp(){
		if (hasRegion()){
			//remove the parent algorithm
			//Application.debug("algo : "+getParentAlgorithm().toString());
			AlgoElement parent = getParentAlgorithm();
			int index = parent.getConstructionIndex();
			getRegion().toGeoElement().removeAlgorithm(parent);
			getConstruction().removeFromAlgorithmList(parent);			
			setParentAlgorithm(null);
			getConstruction().removeFromConstructionList(parent);
			getConstruction().addToConstructionList(this, index);
			
			//remove the region
			setRegion(null);
			//change the color
			if (getObjectColor().equals(ConstructionDefaults.colRegionPoint))
				setObjColor(ConstructionDefaults.colPoint);
			// move from Dependent to Independent in AlgebraView
			if (app.hasGuiManager())
				((AlgebraView)(app.getGuiManager().getAlgebraView())).rename((GeoElement)this);
		}
	
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
        set((GeoElement) point);        
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
	
	
	public void set(GeoPointInterface P){
		set((GeoElement) P);
	}
	
	public void set(GeoElement geo) {

    	if (geo.isGeoPoint()) {
	    	GeoPoint3D p = (GeoPoint3D) geo;  
	    	if (p.getPathParameter() != null) {
	    		PathParameter pathParameter = getPathParameter();
		    	pathParameter.set(p.getPathParameter());
	    	}
	    	setCoords(p);     
	    	updateCoords();
	    	//TODO setMode(p.toStringMode); // complex etc
    	}
    	/* TODO
    	else if (geo.isGeoVector()) {
    		GeoVector v = (GeoVector) geo; 
    		setCoords(v.x, v.y, 1d);   
	    	setMode(v.toStringMode); // complex etc
    	}
    	*/
		
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
		
		StringBuilder sbToString = getSbToString();
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
	// XML
	
    /**
     * returns all class-specific xml tags for saveXML
     * GeoGebra File Format
     */
	protected void getXMLtags(StringBuilder sb) {
        super.getXMLtags(sb); 
        
        
		// point size
		sb.append("\t<pointSize val=\"");
			sb.append(pointSize);
		sb.append("\"/>\n");
 
    }
	
	
	
    public String getStartPointXML() {
    	StringBuilder sb = new StringBuilder();    	
		sb.append("\t<startPoint ");
		
    	if (isAbsoluteStartPoint()) {		
			sb.append(" x=\"" + getCoords().get(1) + "\"");
			sb.append(" y=\"" + getCoords().get(2) + "\"");
			sb.append(" z=\"" + getCoords().get(3) + "\"");			
			sb.append(" w=\"" + getCoords().get(4) + "\"");			
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
 

	

	
	//////////////////////////////////
	// LocateableList
	
	
	public LocateableList getLocateableList(){
		if (locateableList == null)
			locateableList = new LocateableList(this);
		return locateableList;
	}
	
	/**
	 * Tells Locateables that their start point is removed
	 * and calls super.remove()
	 */
	protected void doRemove() {
		if (locateableList != null) {
			
			locateableList.doRemove();

		}
		

		
		super.doRemove();
	}
	
	
	/**
	 * Calls super.update() and updateCascade() for all registered locateables.	 
	 */
	public void update() {  	
		super.update();
						
		// update all registered locatables (they have this point as start point)
		if (locateableList != null) {	
			GeoElement.updateCascade(locateableList, getTempSet());
		}			
	}
	
	private static TreeSet tempSet;	
	protected static TreeSet getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet();
		}
		return tempSet;
	}
	
	
	//////////////////////////////////
	// GeoPointInterface interface
	
	
	public boolean isFinite(){
		return isDefined && !isInfinite;
	}
	
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
