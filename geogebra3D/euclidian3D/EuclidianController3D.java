package geogebra3D.euclidian3D;



import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.Hits;
import geogebra.euclidian.Previewable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.Region;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import geogebra3D.gui.GuiManager3D;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoCoordSys2D;
import geogebra3D.kernel3D.GeoElement3DInterface;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

/**
 * Controller for the 3D view
 * @author matthieu
 *
 */
public class EuclidianController3D extends EuclidianController 
implements MouseListener, MouseMotionListener, MouseWheelListener{




	
	
	
	
	
	/** says if the shift-key is down */
	protected boolean isShiftDown = false;
	
	
	
	
	
	
	
	
	/** 3D point that is currently moved */
	protected GeoPoint3D movedGeoPoint3D = null;
	
	/** current plane where the movedGeoPoint3D lies */
	protected GgbMatrix4x4 currentPlane = null;
	
	
	/** 3D view controlled by this */
	protected EuclidianView3D view3D; //TODO move to EuclidianViewInterface
	
	
	
	
	private Point mouseLocOld = new Point();
	
	/** 3D location of the mouse */
	protected GgbVector mouseLoc3D;	
	/** starting 3D location of the mouse */
	protected GgbVector startLoc3D;
	
	/** picking point */
	protected GgbVector pickPoint;
	

	/** says if a rotation of the view occurred (with right-button) */
	private boolean viewRotationOccured = false;
	
	
	/** scale factor for changing angle of view : 2Pi <-> 360 pixels (so 1 pixel = 1° ) */
	static final public double ANGLE_TO_DEGREES = 2*Math.PI/360;
	/** maximum vertical angle */
	static final public int ANGLE_MAX = 90;

	
	/** for animated rotation */
	private double animatedRotSpeed;
	/** used when time is needed */
	private long timeOld;
	/** used to record x information */
	private int xOld;
	
	
	//SELECTED GEOS
	/** 2D coord sys (plane, polygon, ...) */
	protected ArrayList<GeoCoordSys2D> selectedCoordSys2D = new ArrayList<GeoCoordSys2D>();
	
	
	
	
	/**
	 * common constructor
	 * @param kernel
	 */
	public EuclidianController3D(Kernel kernel) {
		super(kernel);
	}
	
	
	/**
	 * sets the view controlled by this
	 * @param view
	 */
	public void setView(EuclidianView3D view) {
		this.view3D = view;
		super.setView(view);
	
	}
	
	
	
	
	
	

	
	
	////////////////////////////////////////////
	// setters movedGeoElement -> movedGeoPoint, ...
	public void setMovedGeoPoint(GeoElement geo){
		
		movedGeoPoint3D = (GeoPoint3D) movedGeoElement;
		startLoc3D = movedGeoPoint3D.getCoords().copyVector(); 

		if (!movedGeoPoint3D.hasPath() && !movedGeoPoint3D.hasRegion() ){
			
			GgbMatrix4x4 plane = GgbMatrix4x4.Identity(); 
			setCurrentPlane(plane);
			//update the moving plane altitude
			getCurrentPlane().set(movedGeoPoint3D.getCoords(), 4);
			
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
	private GgbMatrix4x4 getCurrentPlane(){
		return currentPlane;
	}

	/**
	 * set the current plane for moving
	 * @param plane a plane
	 */
	private void setCurrentPlane(GgbMatrix4x4 plane){
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
		
		// Michael Borcherds
		// move mouse fast, sometimes get mouseLoc = null
		if (mouseLoc == null) return;
		
		//getting current pick point and direction v 
		GgbVector o;
		if (useOldMouse)
			o = view3D.getPickFromScenePoint(point.getCoords(),mouseLoc.x-mouseLocOld.x,mouseLoc.y-mouseLocOld.y); 
		else
			o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
		view3D.toSceneCoords3D(o);
		
		
		GgbVector v = new GgbVector(new double[] {0,0,1,0});
		view3D.toSceneCoords3D(v);
		
		
		//getting new position of the point
		GgbVector[] project = o.projectPlaneThruVIfPossible(currentPlane, v);
		point.setCoords(project[0]);
	}
	
	
	
	/**
	 * set the mouse information (location and viewing direction in real world coordinates) to the point
	 * @param point a point
	 */
	protected void setMouseInformation(GeoPoint3D point){

		// Michael Borcherds
		// move mouse fast, sometimes get mouseLoc = null
		if (mouseLoc == null) return;
		
		GgbVector o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
		view3D.toSceneCoords3D(o);
		point.setWillingCoords(o);
		
		//TODO do this once
		GgbVector v = new GgbVector(new double[] {0,0,1,0});
		view3D.toSceneCoords3D(v);			
		point.setWillingDirection(v);
	}
	
	protected void movePoint(boolean repaint){
		
		
		
		
		if (movedGeoPoint3D.hasPath()){
			
			setMouseInformation(movedGeoPoint3D);		
			movedGeoPoint3D.doPath();
						
		}else if (movedGeoPoint3D.hasRegion()){
						
			if ((isShiftDown)&&(movedGeoPoint3D.getRegion()==view3D.getxOyPlane())){ 
				//frees the point and moves it along z-axis if it belong to xOy plane
				movedGeoPoint3D.freeUp();
				setCurrentPlane(GgbMatrix4x4.Identity());
			}else{
				setMouseInformation(movedGeoPoint3D);			
				movedGeoPoint3D.doRegion();
			}
					
		}else {
			
			
			if (isShiftDown && mouseLoc != null){ //moves the point along z-axis


				//getting current pick point and direction v 
				GgbVector o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
				view3D.toSceneCoords3D(o);

				GgbVector v = new GgbVector(new double[] {0,0,1,0});
				view3D.toSceneCoords3D(v);

				//getting new position of the point
				GgbVector project = movedGeoPoint3D.getCoords().projectNearLine(o, v, EuclidianView3D.vz);
				movedGeoPoint3D.setCoords(project);

				//update the moving plane altitude
				getCurrentPlane().set(movedGeoPoint3D.getCoords(), 4);


			}else{

				movePointOnCurrentPlane(movedGeoPoint3D, true);

			}
			
			//update point decorations
			view3D.updatePointDecorations(movedGeoPoint3D);

			
		}
		
		

		//update 3D cursor coordinates (false : no path or region update)
		view3D.getCursor3D().setCoords(movedGeoPoint3D.getCoords(),false);
		
		
		if (repaint){
			movedGeoPoint3D.updateRepaint();//for highlighting in algebraView
		}else{
			movedGeoPoint3D.updateCascade();//TODO modify movedGeoPoint3D.updateCascade()
		}
		
		// update previewable
		if (view.getPreviewDrawable() != null) 	
			view.updatePreviewable();
		
	}





	
	
	
	
	//////////////////////////////////////////////
	// creating a new point
	
	
	
	/**
	 * return a copy of the preview point if one
	 */
	protected GeoPointInterface getNewPoint(Hits hits,
			boolean onPathPossible, boolean inRegionPossible, boolean intersectPossible, 
			boolean doSingleHighlighting) {
				
		GeoPoint3D point = view3D.getCursor3D();
				
		GeoPoint3D ret = null;
		
		switch(view3D.getCursor3DType()){		
		case EuclidianView3D.PREVIEW_POINT_FREE:
			ret = ((Kernel3D) getKernel()).Point3D(null, 0,0,0);
			ret.setCoords(point);
			ret.updateCoords();
			break;

		case EuclidianView3D.PREVIEW_POINT_PATH:
			if (onPathPossible){
				ret = ((Kernel3D) getKernel()).Point3D(null,point.getPath());
				ret.setWillingCoords(point.getCoords());
				ret.doPath();
			}else
				return null;
			break;
			
		case EuclidianView3D.PREVIEW_POINT_REGION:
			if (inRegionPossible){
				ret = ((Kernel3D) getKernel()).Point3DIn(null,point.getRegion());
				ret.setWillingCoords(point.getCoords());
				ret.doRegion();
			}else
				return null;
			break;
			
		case EuclidianView3D.PREVIEW_POINT_DEPENDENT:
			if (intersectPossible){
			ret = ((Kernel3D) kernel).Intersect(null, 
					(GeoCoordSys1D) view3D.getCursor3DIntersectionOf(0), 
					(GeoCoordSys1D) view3D.getCursor3DIntersectionOf(1));
			}
			return ret;
			
		case EuclidianView3D.PREVIEW_POINT_NONE:
		default:
			//Application.debug("super.getNewPoint");
			return super.getNewPoint(hits, 
					onPathPossible, inRegionPossible, intersectPossible, 
					doSingleHighlighting);			

		}
		

			
		ret.update();
		//point.setEuclidianVisible(false);
		
		view3D.addToHits3D(ret);

		return ret;
	
		

		
	}
	
	/** put sourcePoint coordinates in point */
	protected void createNewPoint(GeoPointInterface sourcePoint){
		GeoPoint3D point3D = view3D.getCursor3D();
		point3D.setCoords((GeoPoint3D) sourcePoint);
		point3D.updateCoords();
		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_NONE);
	}
	
	/** put intersectionPoint coordinates in point */
	protected void createNewPointIntersection(GeoPointInterface intersectionPoint){
		GeoPoint3D point3D = view3D.getCursor3D();
		point3D.setCoords((GeoPoint3D) intersectionPoint);
		//point3D.setParentAlgorithm(((GeoPoint3D) intersectionPoint).getParentAlgorithm());
		point3D.updateCoords();
		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_DEPENDENT);
		
	}

	
	/**
	 * create a new free point
	 * or update the preview point
	 */
	protected GeoPointInterface createNewPoint(boolean forPreviewable){
		
		GeoPoint3D point3D;
		
		if (!forPreviewable)
			point3D = ((Kernel3D) getKernel()).Point3D(null, 0,0,0);
		else{
			//if xOy plane is visible, then the point is on it
			if (view3D.getxOyPlane().isPlateVisible() ||
					view3D.getxOyPlane().isGridVisible()) 
				return createNewPoint(true, (Region) view3D.getxOyPlane());
			
			point3D = view3D.getCursor3D();
			point3D.setPath(null);
			point3D.setRegion(null);
			view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_FREE);
		}
		
		setCurrentPlane(GgbMatrix4x4.Identity());
		movePointOnCurrentPlane(point3D, false);	
		
		return point3D;
	}
	
	
	/**
	 * create a new path point
	 * or update the preview point
	 */	
	protected GeoPointInterface createNewPoint(boolean forPreviewable, Path path){
			
		GeoPoint3D point3D;
		
		if (!forPreviewable)
			point3D = ((Kernel3D) getKernel()).Point3D(null,path);
		else{
			point3D = view3D.getCursor3D();
			point3D.setPath(path);
			point3D.setRegion(null);
			view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_PATH);
		}			
		
		setMouseInformation(point3D);
		point3D.doPath();
		
		//Application.debug("hop");
				
		return point3D;
	}
	
	/**
	 * create a new region point
	 * or update the preview point
	 */	
	protected GeoPointInterface createNewPoint(boolean forPreviewable, Region region){
		
		GeoPoint3D point3D;
		
		if (!forPreviewable)
			point3D = ((Kernel3D) getKernel()).Point3DIn(null,region);
		else{
			point3D = view3D.getCursor3D();
			point3D.setPath(null);
			point3D.setRegion(region);
			view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_REGION);
		}
		
		setMouseInformation(point3D);
		point3D.doRegion();
		
		return point3D;
	}
	
	
	protected void updateMovedGeoPoint(GeoPointInterface point){
		movedGeoPoint3D = (GeoPoint3D) point;
	}
	
	
	
	
	// tries to get a single intersection point for the given hits
	// i.e. hits has to include two intersectable objects.
	protected GeoPointInterface getSingleIntersectionPoint(Hits hits) {

		
		if (hits.isEmpty() || hits.size() != 2)
			return null;

		GeoElement a = (GeoElement) hits.get(0);
		GeoElement b = (GeoElement) hits.get(1);
		GeoPoint3D point = null;

		
		
		//Application.debug(""+hits);

		kernel.setSilentMode(true);
		
		
    	if (a instanceof GeoCoordSys1D){
    		if (b instanceof GeoCoordSys1D){
    			point = ((Kernel3D) kernel).Intersect(null, (GeoCoordSys1D) a, (GeoCoordSys1D) b);
    		}
    	}
    	
    	kernel.setSilentMode(false);
		
    	//Application.debug("point is defined : "+point.isDefined());
    	
    	if (point==null)
    		return null;
    	
    	if (point.isDefined()){
    		view3D.setCursor3DIntersectionOf(a, b);
    		return point;
    	}else
    		return null;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////
	// creating new objects
	
	
	/** return selected points as 3D points
	 * @return selected points
	 */
	final protected GeoPoint3D[] getSelectedPoints3D() {		

		GeoPoint3D[] ret = new GeoPoint3D[selectedPoints.size()];
		getSelectedPointsInterface(ret);
		
		//Application.printStacktrace("");
		
		return ret;	
	}
	

	
	/**
	 * @return selected 3D lines
	 */
	final protected GeoCoordSys1D[] getSelectedLines3D() {
		GeoCoordSys1D[] lines = new GeoCoordSys1D[selectedLines.size()];
		getSelectedLinesInterface(lines);

		return lines;
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
	
	// fetch the two selected points for ray
	protected void ray(){
		GeoPoint3D[] points = getSelectedPoints3D();
		((Kernel3D) getKernel()).Ray3D(null,points[0], points[1]);
	}
	
	// fetch the two selected points for vector
	protected void vector(){
		GeoPoint3D[] points = getSelectedPoints3D();
		((Kernel3D) getKernel()).Vector3D(null,points[0], points[1]);
	}
	
	
	// build polygon	
	protected void polygon(){
		((Kernel3D) getKernel()).Polygon3D(null, getSelectedPoints3D());
	}
	
	protected void circleOrSphere(NumberValue num){
		GeoPoint3D[] points = getSelectedPoints3D();	

		((Kernel3D) getKernel()).Sphere(null, points[0], num);
	}
	
	
	
	
	
	
	protected void orthogonal() {
		// fetch selected point and line
		GeoPoint3D[] points = getSelectedPoints3D();
		GeoCoordSys1D[] lines = getSelectedLines3D();
		// create new line
		((Kernel3D) getKernel()).OrthogonalPlane3D(null, points[0], lines[0]);
		
	}
	
	
	
	/** get point and line;
	 * create plane through point and line
	 * 
	 * @param hits
	 * @return true if a plane has been created
	 */
	final protected boolean pointLine(Hits hits) {
		if (hits.isEmpty())
			return false;
		
		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
				addSelectedLine(hits, 1, false);
		}

		if (selPoints() == 1) {
			if (selLines() == 1) {
				// fetch selected point and line
				GeoPoint3D[] points = getSelectedPoints3D();
				GeoCoordSys1D[] lines = getSelectedLines3D();
				// create new plane
				((Kernel3D) getKernel()).Plane3D(null, points[0], lines[0]);
				return true;
			}
		}
		return false;
	}

	
	/** get point and plane;
	 * create line through point parallel to plane
	 * 
	 * @param hits
	 * @return true if a plane has been created
	 */
	final protected boolean parallelPlane(Hits hits) {
		
		//Application.debug(hits.toString());
		
		if (hits.isEmpty())
			return false;

		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
			addSelectedCoordSys2D(hits, 1, false);
		}

		if (selPoints() == 1) {
			if (selCoordSys2D() == 1) {
				// fetch selected point and vector
				GeoPoint3D[] points = getSelectedPoints3D();
				GeoCoordSys2D[] cs = getSelectedCoordSys2D();
				// create new plane
				((Kernel3D) getKernel()).Plane3D(null, points[0], cs[0]);
				return true;
			}
		}
		return false;
	}

	
	
	
	
	///////////////////////////////////////////
	// moved GeoElements
	
	public GeoElement getMovedGeoPoint(){
		return movedGeoPoint3D;
	}
	
	
	
	///////////////////////////////////////////
	// mouse released
	
	protected void processReleaseForMovedGeoPoint(){
		
		((EuclidianView3D) view).updatePointDecorations(null);
		super.processReleaseForMovedGeoPoint();
		
	}

	
	///////////////////////////////////////////
	// mouse moved
	
	
	private boolean mouseMoved = false;
	private MouseEvent mouseEvent = null;
	
	protected void processMouseMoved(MouseEvent e) {	
		((EuclidianView3D) view).setHits3D(mouseLoc);		
		
		mouseEvent = e;
		mouseMoved = true;
		
	}
	
	/**
	 * tells to proceed mouseMoved() (for synchronization with 3D renderer)
	 */
	public void processMouseMoved(){
		if (mouseMoved){
			mouseMoved = false;
			((EuclidianView3D) view).updateCursor3D();
			
			//Application.debug(view.getHits().toString());
			
			super.processMouseMoved(mouseEvent);
		}
	}
	
	
	
	
	protected void initNewMode(int mode) {
		super.initNewMode(mode);
		
		
		
		//sets the visibility of EuclidianView3D 3D cursor
		if (mode==EuclidianView.MODE_MOVE)
			view3D.setShowCursor3D(false);
		else
			view3D.setShowCursor3D(true);
	}

	protected Previewable switchPreviewableForInitNewMode(int mode){

		Previewable previewDrawable = null;
		
		switch (mode) {

		case EuclidianView.MODE_SPHERE_TWO_POINTS:
			previewDrawable = view3D.createPreviewSphere(selectedPoints);
			break;
		default:
			previewDrawable = super.switchPreviewableForInitNewMode(mode);
			break;
		}
		
		return previewDrawable;

	}
	
	//not only moveable hits are selected in move mode
	protected boolean move(Hits hits) {		
		addSelectedGeo(hits.getTopHits(1), 1, false);	
		//Application.debug("top hits: "+hits.getTopHits());
		return false;
	}
	
	
	
	protected void mouseClickedMode(MouseEvent e, int mode){
		

		switch (mode) {
		case EuclidianView3D.MODE_VIEW_IN_FRONT_OF:
			//Application.debug("ici");
			Hits hits = view.getHits().getTopHits();
			if(!hits.isEmpty()){
				GeoElement3DInterface geo = (GeoElement3DInterface) view.getHits().getTopHits().get(0);
				GgbVector vn = geo.getViewDirection();
				if (vn!=null){
					view3D.setRotAnimation(vn);
				}
			}
			
			break;
			default:
				super.mouseClickedMode(e,mode);
		}
	}
	

	
	//TODO
	public void processModeLock(){}; 

	///////////////////////////////////////////
	// EMPTY METHODS IN EuclidianController USED FOR EuclidianView3D	
	




	/** right-press the mouse makes start 3D rotation */
	protected void processRightPressFor3D(){
		
		if (view3D.isRotAnimated()){
			view3D.stopRotAnimation();
			viewRotationOccured = true;
		}
		
		//remembers mouse location
		startLoc = mouseLoc;
		view.rememberOrigins();
		view.setMoveCursor();
		
		timeOld = System.currentTimeMillis();
		xOld = startLoc.x;
		animatedRotSpeed = 0;

	}
	

	

	
	/** right-drag the mouse makes 3D rotation 
	 * @return true*/
	protected boolean processRightDragFor3D(){

		long time = System.currentTimeMillis();
		int x = mouseLoc.x;
		animatedRotSpeed = (double) (x-xOld)/(time-timeOld);
		timeOld = time; xOld = x;
		//Application.debug("vRot="+vRot);
		view.setCoordSystemFromMouseMove(mouseLoc.x - startLoc.x, 
				mouseLoc.y - startLoc.y, 
				MOVE_ROTATE_VIEW);
		viewRotationOccured = true;
		return true;
	}
	
	
	/** right-release the mouse makes stop 3D rotation 
	 * @return true if a rotation occured */
	protected boolean processRightReleaseFor3D(){
		
		if (viewRotationOccured){
			viewRotationOccured = false;
			view.setHits(mouseLoc);
			//Application.debug("hits"+view.getHits().toString());
			((EuclidianView3D) view).updateCursor3D();
			
			view.setHitCursor();
			app.storeUndoInfo();
			

			((EuclidianView3D) view).setRotContinueAnimation(
					System.currentTimeMillis()-timeOld,
					animatedRotSpeed);
			
			//Application.debug("animatedRotSpeed="+animatedRotSpeed);
			
			return true;
		}else
			return false;
	}
	
	
	///////////////////////////////////////////
	// PROCESS MODE
	
	protected boolean switchModeForProcessMode(Hits hits, MouseEvent e){
		
		boolean changedKernel = false;
		
		switch (mode) {
		case EuclidianView3D.MODE_PLANE_THREE_POINTS:
			changedKernel = threePoints(hits, mode);
			break;
		case EuclidianView3D.MODE_PLANE_POINT_LINE:
			changedKernel = pointLine(hits);
			break;		
		
		case EuclidianView3D.MODE_ORTHOGONAL_PLANE:
			changedKernel = orthogonal(hits);
			break;
			
		case EuclidianView3D.MODE_PARALLEL_PLANE:
			changedKernel = parallelPlane(hits);
			break;
			
		case EuclidianView3D.MODE_SPHERE_TWO_POINTS:	
			changedKernel = circleOrSphere2(hits, mode);
			break;
		case EuclidianView3D.MODE_SPHERE_POINT_RADIUS:
			changedKernel = circleOrSpherePointRadius(hits);
			break;
		

		default:
			changedKernel = super.switchModeForProcessMode(hits, e);
		}
		
		
		return changedKernel;
		
	}
	
	
	
	/**
	 * for some modes, polygons are not to be removed
	 * @param hits
	 */
	protected void switchModeForRemovePolygons(Hits hits){
		switch (mode){
		case EuclidianView3D.MODE_PARALLEL_PLANE:
			((Hits3D) hits).removePolygonsIfNotOnlyCS2D();
			break;
		default:
			super.switchModeForRemovePolygons(hits);
		}
	}
	
	
	protected boolean switchModeForThreePoints(){
		
		switch (mode) {
		case EuclidianView3D.MODE_PLANE_THREE_POINTS:
			GeoPoint3D[] points = getSelectedPoints3D();
			((Kernel3D) getKernel()).Plane3D(null, points[0], points[1], points[2]);
			return true;
		default:
			return super.switchModeForThreePoints();

		}

	}

	protected void switchModeForCircleOrSphere2(int mode){
		
		
		switch (mode) {
		case EuclidianView3D.MODE_SPHERE_TWO_POINTS:
			GeoPoint3D[] points = getSelectedPoints3D();
			((Kernel3D) getKernel()).Sphere(null, points[0], points[1]);
			break;
		default:
			super.switchModeForCircleOrSphere2(mode);
			break;
		}
	}

	
	///////////////////////////////////////////
	// MOUSE PRESSED
	
	protected void switchModeForMousePressed(MouseEvent e){

		Hits hits;
		
		switch (mode) {
		case EuclidianView3D.MODE_PLANE_THREE_POINTS:	
		case EuclidianView3D.MODE_SPHERE_TWO_POINTS:	
		case EuclidianView3D.MODE_SPHERE_POINT_RADIUS:	
			view.setHits(mouseLoc);
			hits = view.getHits();hits.removePolygons();
			createNewPoint(hits, true, true, true); 
			break;
			
		case EuclidianView3D.MODE_ORTHOGONAL_PLANE:
		case EuclidianView3D.MODE_PLANE_POINT_LINE:
			view.setHits(mouseLoc);
			hits = view.getHits();hits.removePolygons();
			createNewPoint(hits, false, true, true);
			break;	
			
		case EuclidianView3D.MODE_PARALLEL_PLANE:
			view.setHits(mouseLoc);
			hits = view.getHits();hits.removePolygons();
			createNewPoint(hits, true, false, true, true);
			break;	
			
		default:
			super.switchModeForMousePressed(e);
		}
	}
	
	
	
	
	///////////////////////////////////////////
	// MOUSE RELEASED
	
	protected boolean switchModeForMouseReleased(int mode, Hits hits, boolean changedKernel){
		switch (mode) {
		case EuclidianView3D.MODE_PARALLEL_PLANE:
			return changedKernel;
		case EuclidianView3D.MODE_VIEW_IN_FRONT_OF:
			//Application.debug("hop");
			//TODO implement choose geo
			return true;
		default:
			return super.switchModeForMouseReleased(mode, hits, changedKernel);
			
		}

	}
	
	
	public void showDrawingPadPopup(Point mouseLoc){
		((GuiManager3D) app.getGuiManager()).showDrawingPadPopup3D((JPanel) view, mouseLoc);		
	}

	
	///////////////////////////////////////////
	// INTERSECTIONS
	
	/**
	 *  get two objects (lines or conics) and create intersection point 
	 *
	 */
	@SuppressWarnings("unchecked")
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
	// POINT CAPTURING
	
	protected void transformCoords() {
		//TODO point capturing
	}
		
	
	
	
	///////////////////////////////////////////
	// SELECTIONS
	
	/** selected 1D coord sys */
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
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
	
	
	
	
	
	@SuppressWarnings("unchecked")
	protected GeoElement chooseGeo(ArrayList geos, boolean includeFixed) {
		
		//Application.printStacktrace(((Hits) geos).toString());
		
		if (!geos.isEmpty()){
			//if the geo hitted is one of view3D's geos, then chooseGeo return null
			if (view3D.owns((GeoElement) geos.get(0)))
				return null;
			//doesn't use choosing dialog TODO use choosing dialog ?
			else 
				//return (GeoElement) geos.get(0);
				return super.chooseGeo(geos, includeFixed);
		}
	
		return null;
		
		//return super.chooseGeo(geos, includeFixed);
	}
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////////
	//
	

	/*
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		double r = e.getWheelRotation();

		switch (moveMode) {
		case MOVE_VIEW:
		default:
			view3D.setMoveCursor();//setZoomCursor
			view3D.setScale(view3D.getXscale()+r*10);
			view3D.updateMatrix();
			view.setHits(mouseLoc);
			((EuclidianView3D) view).updateCursor3D();
			view3D.setHitCursor();
			//((Kernel3D) getKernel()).notifyRepaint();
			
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
			
			*
			break;	
		
		
		}
	
		
		

	}
	*/
	
	
	
	final protected void setMouseLocation(MouseEvent e) {

		if (mouseLoc!=null)
			mouseLocOld = (Point) mouseLoc.clone();
		

		isShiftDown= e.isShiftDown();//Application.isAltDown(e);
		
		//mouseLoc = e.getPoint();
		super.setMouseLocation(e);
		
		
		

	}
	
	

	//////////////////////////////////////
	// SELECTED GEOS
	//////////////////////////////////////
	
	/**
	 * add selected 2D coord sys
	 * @param hits
	 * @param max
	 * @param addMoreThanOneAllowed
	 * @return if one has been added
	 */
	final protected int addSelectedCoordSys2D(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedCoordSys2D, GeoCoordSys2D.class);
	}
	
	/**
	 * @return number of selected 2D coord sys
	 */
	protected final int selCoordSys2D() {
		return selectedCoordSys2D.size();
	}
	
	/**
	 * @return selected 2D coord sys
	 */
	@SuppressWarnings("unchecked")
	final protected GeoCoordSys2D[] getSelectedCoordSys2D() {
		GeoCoordSys2D[] cs = new GeoCoordSys2D[selectedCoordSys2D.size()];
		int i = 0;
		Iterator it = selectedCoordSys2D.iterator();
		while (it.hasNext()) {
			cs[i] = (GeoCoordSys2D) it.next();
			i++;
		}
		clearSelection(selectedCoordSys2D);
		return cs;
	}
	
	
}
