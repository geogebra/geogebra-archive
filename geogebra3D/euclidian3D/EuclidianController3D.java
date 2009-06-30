package geogebra3D.euclidian3D;



import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.Hits;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.Region;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.kernel3D.ConstructionDefaults3D;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Iterator;

public class EuclidianController3D extends EuclidianController 
implements MouseListener, MouseMotionListener, MouseWheelListener{




	static final boolean DEBUG = false; //conditionnal compilation
	
	
	
	//TODO link it to toolbar values
	protected static final int MOVE_POINT_WHEEL = 3102;
	
	
	
	
	
	//protected boolean isCtrlDown = false;
	protected boolean isShiftDown = false;
	
	
	
	
	
	
	
	protected GeoElement3D objSelected = null;
	protected GeoPoint3D movedGeoPoint3D = null;
	
	/** current plane where the movedGeoPoint3D lies */
	protected Ggb3DMatrix4x4 currentPlane = null;
	
	
	protected EuclidianView3D view3D; //TODO move to EuclidianViewInterface
	//protected Kernel3D kernel3D;
	//protected Application3D app3D;
	
	
	
	
	private Point mouseLocOld = new Point();
	
	protected Ggb3DVector mouseLoc3D, startLoc3D;
	
	//picking
	protected Ggb3DVector pickPoint;
	

	/** says if a rotation of the view occurred (with right-button) */
	private boolean viewRotationOccured = false;
	
	
	//scale factor for changing angle of view : 2Pi <-> 300 pixels 
	static final public double ANGLE_SCALE = 2*Math.PI/300f;
	static final public int ANGLE_MAX = (int) ((Math.PI/2)/ANGLE_SCALE); //maximum vertical angle


	
	
	public EuclidianController3D(Kernel kernel) {
		super(kernel);
	}
	
	
	void setView(EuclidianView3D view) {
		this.view3D = view;
		super.setView(view);
	
	}
	
	
	
	
	
	

	
	
	////////////////////////////////////////////
	// setters movedGeoElement -> movedGeoPoint, ...
	public void setMovedGeoPoint(GeoElement geo){
		
		movedGeoPoint3D = (GeoPoint3D) movedGeoElement;
		startLoc3D = movedGeoPoint3D.getCoords().copyVector(); 

		if (!movedGeoPoint3D.hasPath() && !movedGeoPoint3D.hasRegion() ){
			
			Ggb3DMatrix4x4 plane = Ggb3DMatrix4x4.Identity(); 
			setCurrentPlane(plane);
			//update the moving plane altitude
			getCurrentPlane().set(movedGeoPoint3D.getCoords(), 4);
			
			movedGeoPoint3D.setCoordDecoration(true);

		}
	}

	
	public void resetMovedGeoPoint(){
		movedGeoPoint3D = null;
	}


	
	////////////////////////////////////////////:
	// moving points
	
	
	/**
	 * return the current plane for moving
	 * @return the current plane
	 */
	private Ggb3DMatrix4x4 getCurrentPlane(){
		return currentPlane;
	}

	/**
	 * set the current plane for moving
	 * @param plane a plane
	 */
	private void setCurrentPlane(Ggb3DMatrix4x4 plane){
		currentPlane = plane;
	}

	
	/** set the current plane to the path's moving plane
	 * @param path a path
	 */
	/*
	private void setCurrentPlane(Path3D path){
		Ggb3DMatrix4x4 plane = path.getMovingMatrix(view3D.getToScreenMatrix());			
		view3D.toSceneCoords3D(plane);
		setCurrentPlane(plane);
	}
	*/

	
	/**
	 * moves the point according to the current moving plane and mouse location
	 * @param point the point to move
	 * @param useOldMouse if true, shift the point according to old mouse location
	 */
	private void movePointOnCurrentPlane(GeoPoint3D point, boolean useOldMouse){
		
		
		//getting current pick point and direction v 
		Ggb3DVector o;
		if (useOldMouse)
			o = view3D.getPickFromScenePoint(point.getCoords(),mouseLoc.x-mouseLocOld.x,mouseLoc.y-mouseLocOld.y); 
		else
			o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
		view3D.toSceneCoords3D(o);
		
		
		Ggb3DVector v = new Ggb3DVector(new double[] {0,0,1,0});
		view3D.toSceneCoords3D(v);
		
		
		//getting new position of the point
		Ggb3DVector[] project = o.projectPlaneThruVIfPossible(currentPlane, v);
		point.setCoords(project[0]);
	}
	
	
	
	/**
	 * set the mouse information (location and viewing direction in real world coordinates) to the point
	 * @param point a point
	 */
	protected void setMouseInformation(GeoPoint3D point){
		Ggb3DVector o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
		view3D.toSceneCoords3D(o);
		point.setWillingCoords(o);
		
		//TODO do this once
		Ggb3DVector v = new Ggb3DVector(new double[] {0,0,1,0});
		view3D.toSceneCoords3D(v);			
		point.setWillingDirection(v);
	}
	
	protected void movePoint(boolean repaint){
		
		
		if (movedGeoPoint3D.hasPath()){
			
			setMouseInformation(movedGeoPoint3D);		
			movedGeoPoint3D.doPath();
						
		}else if (movedGeoPoint3D.hasRegion()){
			
			setMouseInformation(movedGeoPoint3D);			
			movedGeoPoint3D.doRegion();
					
		}else if (isShiftDown){ //moves the point along z-axis
			
			//getting current pick point and direction v 
			Ggb3DVector o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
			view3D.toSceneCoords3D(o);
			
			Ggb3DVector v = new Ggb3DVector(new double[] {0,0,1,0});
			view3D.toSceneCoords3D(v);
			
			//getting new position of the point
			Ggb3DVector project = movedGeoPoint3D.getCoords().projectNearLine(o, v, EuclidianView3D.vz);
			movedGeoPoint3D.setCoords(project);
			
			//update the moving plane altitude
			getCurrentPlane().set(movedGeoPoint3D.getCoords(), 4);
			
			
		}else{
			
			movePointOnCurrentPlane(movedGeoPoint3D, true);
		
			
		}
		
		

		
		
		if (repaint){
			//kernel3D.notifyRepaint();
			//view.update();
			movedGeoPoint3D.updateRepaint();//for highlighting in algebraView
		}else{
			movedGeoPoint3D.updateCascade();//TODO modify movedGeoPoint3D.updateCascade()
		}
		
		
	}






	public void mouseReleased(MouseEvent e) {
		
		if (movedGeoPoint3D!=null)
			movedGeoPoint3D.setCoordDecoration(false);
		
		super.mouseReleased(e);

	}

	

	
	
	
	
	//////////////////////////////////////////////
	// creating a new point
	
	
	
	/**
	 * return a copy of the preview point if one
	 */
	protected GeoPointInterface getNewPoint(Hits hits,
			boolean onPathPossible, boolean inRegionPossible, boolean intersectPossible, 
			boolean doSingleHighlighting) {
		
		if (view.getPreviewDrawable()!=null){
			GeoElement geo = ((Drawable3D) view.getPreviewDrawable()).getGeoElement();

			if (geo.isGeoPoint()){
				if (geo.isEuclidianVisible()){
					GeoPoint3D point = (GeoPoint3D) geo;
					GeoPoint3D ret;
					if (point.hasPath())
						ret = ((Kernel3D) getKernel()).Point3D(null,point.getPath());
					else if (point.hasRegion())
						ret = ((Kernel3D) getKernel()).Point3DIn(null,point.getRegion());
					else{
						ret = ((Kernel3D) getKernel()).Point3D(null, 0,0,0);
						ret.setCoordDecoration(true);
					}
					ret.setCoords(point);
					ret.updateCoords();
					point.setEuclidianVisible(false);

					return ret;
				}
			}
		}
		
		return super.getNewPoint(hits, 
				onPathPossible, inRegionPossible, intersectPossible, 
				doSingleHighlighting);

		
	}
	
	
	/**
	 * create a new free point
	 * or update the preview point
	 */
	protected GeoPointInterface createNewPoint(GeoPointInterface point){
		
		//Application.debug("point = "+point);
		
		if (point == null)
			point = ((Kernel3D) getKernel()).Point3D(null, 0,0,0);
		else{
			GeoPoint3D point3D = (GeoPoint3D) point;
			point3D.setPath(null);
			point3D.setRegion(null);
			point3D.setObjColor(ConstructionDefaults3D.colPoint);
			point3D.setEuclidianVisible(true);
		}
		
		setCurrentPlane(Ggb3DMatrix4x4.Identity());
		movePointOnCurrentPlane((GeoPoint3D) point, false);	
		((GeoPoint3D) point).setCoordDecoration(true);
		
		return point;
	}
	
	
	/**
	 * create a new path point
	 * or update the preview point
	 */	
	protected GeoPointInterface createNewPoint(GeoPointInterface point, Path path){
			
		if (point == null)
			point = ((Kernel3D) getKernel()).Point3D(null,path);
		else{
			GeoPoint3D point3D = (GeoPoint3D) point;
			point3D.setPath(path);
			point3D.setRegion(null);
			point3D.setObjColor(ConstructionDefaults3D.colPathPoint);
			point3D.setCoordDecoration(false);
			point3D.setEuclidianVisible(true);
		}			
		
		setMouseInformation((GeoPoint3D) point);
		((GeoPoint3D) point).doPath();
				
		return point;
	}
	
	/**
	 * create a new region point
	 * or update the preview point
	 */	
	protected GeoPointInterface createNewPoint(GeoPointInterface point, Region region){
		
		if (point == null)
			point = ((Kernel3D) getKernel()).Point3DIn(null,region);
		else{
			GeoPoint3D point3D = (GeoPoint3D) point;
			point3D.setPath(null);
			point3D.setRegion(region);
			point3D.setObjColor(ConstructionDefaults3D.colRegionPoint);
			point3D.setCoordDecoration(false);
			point3D.setEuclidianVisible(true);
		}
		
		setMouseInformation((GeoPoint3D) point);
		((GeoPoint3D) point).doRegion();
		
		return point;
	}
	
	
	protected void updateMovedGeoPoint(GeoPointInterface point){
		movedGeoPoint3D = (GeoPoint3D) point;
	}
	
	
	///////////////////////////////////////
	// creating new objects
	
	
	/** return selected points as 3D points
	 * @return selected points
	 */
	final protected GeoPoint3D[] getSelectedPoints3D() {		

		GeoPoint3D[] ret = new GeoPoint3D[selectedPoints.size()];
		getSelectedPointsInterface(ret);
		
		return ret;	
	}
		
	// fetch the two selected points for line
	protected void join(){
		GeoPoint3D[] points = getSelectedPoints3D();
		((Kernel3D) getKernel()).Line3D(null,points[0], points[1]);
	}
	
	// fetch the two selected points for segment
	protected void segment(){
		GeoPoint3D[] points = getSelectedPoints3D();
		((Kernel3D) getKernel()).Segment3D(null,points[0], points[1]);
	}
	
	// fetch the two selected points for segment
	protected void ray(){
		GeoPoint3D[] points = getSelectedPoints3D();
		((Kernel3D) getKernel()).Ray3D(null,points[0], points[1]);
	}
	
	
	// build polygon	
	protected void polygon(){
		((Kernel3D) getKernel()).Polygon3D(null, getSelectedPoints3D());
	}
	
	
	///////////////////////////////////////////
	// moved GeoElements
	
	public GeoElement getMovedGeoPoint(){
		return movedGeoPoint3D;
	}
	
	
	
	
	
	
	
	

	///////////////////////////////////////////
	// EMPTY METHODS IN EuclidianController USED FOR EuclidianView3D	
	
	
	/** set the hits in top of mouseMoved() */
	protected void mouseMoved3D(){
		view.setHits(mouseLoc);
	}


	/** right-press the mouse makes start 3D rotation */
	protected void processRightPressFor3D(){
		//remembers mouse location
		startLoc = mouseLoc;
		view.rememberOrigins();
	}
	
	/** right-drag the mouse makes 3D rotation 
	 * @return true*/
	protected boolean processRightDragFor3D(){
		view.setCoordSystemFromMouseMove(mouseLoc.x - startLoc.x, mouseLoc.y - startLoc.y);
		viewRotationOccured = true;
		return true;
	}
	
	
	/** right-release the mouse makes stop 3D rotation 
	 * @return true if a rotation occured */
	protected boolean processRightReleaseFor3D(){
		if (viewRotationOccured){
			viewRotationOccured = false;
			return true;
		}else
			return false;
	}
	
	
	
	
	
	
	
	
	
	
	

	
	///////////////////////////////////////////
	// INTERSECTIONS
	
	/** get two objects (lines or conics) and create intersection point */
	protected boolean intersect(Hits hits) {
		if (hits.isEmpty())
			return false;		

		// when two objects are selected at once then only one single
		// intersection point should be created
		boolean singlePointWanted = selGeos() == 0;
							
		// check how many interesting hits we have
		//Application.debug("selectionPreview = "+selectionPreview);
		if (!selectionPreview && hits.size() > 2 - selGeos()) {
			Hits goodHits = new Hits();
			//goodHits.add(selectedGeos);
			hits.getHits(GeoCoordSys1D.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			//Application.debug(goodHits.toString());
			
			if (goodHits.size() > 2 - selGeos()) {
				//  choose one geo, and select only this one
				GeoElement geo = chooseGeo(goodHits, true);
				hits.clear();
				hits.add(geo);				
			} else {
				hits = goodHits;
			}
		}			
		
		// get lines, segments, etc.
		addSelectedCS1D(hits, 2, true);
		
		singlePointWanted = singlePointWanted && selGeos() == 2;
		
		if (selGeos() > 2)
			return false;

		// two 3D lines		
		if (selCS1D() == 2) {
						
			GeoCoordSys1D[] lines = getSelectedCS1D();
			((Kernel3D) kernel).Intersect(null, lines[0], lines[1]);
			return true;
			
		}
		
		
		return false;
	}

	
	
	
	
	
	
	
	
	///////////////////////////////////////////
	// SELECTIONS
	
	/** selected 1D coord sys */
	protected ArrayList selectedCS1D = new ArrayList();
	
	/** add hits to selectedCS1D
	 * @param hits hits
	 * @param max max number of hits to add
	 * @param addMoreThanOneAllowed if adding more than one is allowed
	 * @return TODO
	 */
	final protected int addSelectedCS1D(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		//Application.debug(hits.toString());
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedCS1D, GeoCoordSys1D.class);
	}
	
	
	/**
	 * return number of selected 1D coord sys
	 * @return number of selected 1D coord sys
	 */
	final int selCS1D() {
		return selectedCS1D.size();
	}	
	
	
	/** return selected 1D coord sys
	 * @return selected 1D coord sys
	 */
	final protected GeoCoordSys1D[] getSelectedCS1D() {
		GeoCoordSys1D[] lines = new GeoCoordSys1D[selectedCS1D.size()];
		int i = 0;
		Iterator it = selectedCS1D.iterator();
		while (it.hasNext()) {
			lines[i] = (GeoCoordSys1D) it.next();
			i++;
		}
		clearSelection(selectedCS1D);
		return lines;
	}	
	
	
	
	///////////////////////////////////////////
	//
	

	public void mouseWheelMoved(MouseWheelEvent e) {
		
		double r = e.getWheelRotation();

		switch (moveMode) {
		case MOVE_VIEW:
		default:
			view3D.setScale(view3D.getXscale()+r*10);
			view3D.updateMatrix();
			((Kernel3D) getKernel()).notifyRepaint();
			break;

		case MOVE_POINT:
		case MOVE_POINT_WHEEL:
			/* TODO
			//p = p + r*vn			
			Ggb3DVector p1 = (Ggb3DVector) movedGeoPoint3D.getCoords().add(EuclidianView3D.vz.mul(-r*0.1)); 
			movedGeoPoint3D.setCoords(p1);
			
			
			


			objSelected.updateCascade();

			
			movedGeoPoint3D.updateRepaint();//for highlighting in algebraView
			//kernel3D.notifyRepaint();
			
			*/
			break;	
		
		
		}
	
		
		

	}
	
	
	
	final protected void setMouseLocation(MouseEvent e) {

		if (mouseLoc!=null)
			mouseLocOld = (Point) mouseLoc.clone();
		

		isShiftDown= e.isShiftDown();//Application.isAltDown(e);
		
		//mouseLoc = e.getPoint();
		super.setMouseLocation(e);
		
		
		

	}


	
	protected void mousePressedTranslatedView(MouseEvent e){
		
		Hits hits;
		
		// check if axis is hit
		//hits = view.getHits(mouseLoc);
		view.setHits(mouseLoc);
		hits = view.getHits();hits.removePolygons();
		Application.debug("MODE_TRANSLATEVIEW - "+hits.toString());
		
		if (!hits.isEmpty()) {
			handleMousePressedForMoveMode(e, false);
		}else{
			super.mousePressedTranslatedView(e);
		}
		


	}

	
	
	
	
	
}
