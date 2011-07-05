package geogebra3D.euclidian3D;



import geogebra.Matrix.CoordMatrix4x4;
import geogebra.Matrix.Coords;
import geogebra.euclidian.EuclidianConstants;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.Hits;
import geogebra.euclidian.Previewable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSurfaceFinite;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.Region;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.kernel.kernelND.GeoVectorND;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoQuadricND;
import geogebra.kernel.kernelND.Region3D;
import geogebra.main.Application;
import geogebra3D.gui.GuiManager3D;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoPolygon3D;
import geogebra3D.kernel3D.GeoQuadric3D;

import java.awt.Color;
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




	
	
	
	
	
	
	
	
	
	
	/** 3D point that is currently moved */
	//protected GeoPoint3D movedGeoPoint3D = null;
	
	/** min/max values for moving a point */
	private double[] xMinMax, yMinMax, zMinMax;
	
	/** current plane where the movedGeoPoint3D lies */
	protected CoordMatrix4x4 currentPlane = null;
	
	
	/** 3D view controlled by this */
	protected EuclidianView3D view3D; //TODO move to EuclidianViewInterface
	
	
	
	private Point mouseLocOld = new Point();
	private Coords positionOld = new Coords(4);
	
	
	
	/** picking point */
	protected Coords pickPoint;
	
	
	
	/** says that a free point has just been created (used for 3D cursor) */
	private boolean freePointJustCreated = false;
	

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
	
	private ArrayList<GeoPolygon3D> selectedPolygons3D = new ArrayList<GeoPolygon3D>();
	
	
	/**
	 * common constructor
	 * @param kernel
	 */
	public EuclidianController3D(Kernel kernel) {
		super(kernel);
		
		// inits min max
		xMinMax = new double[2];
		yMinMax = new double[2];
		zMinMax = new double[2];
		
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
	
	private double[] getMinMax(double min, double val, double max){
		
		if (val<min)
			min = val;
		else if (val>max)
			max = val;
		
		return new double[] {min,max};
	}
	
	public void setMovedGeoPoint(GeoElement geo){
		
		movedGeoPoint = (GeoPointND) geo;
		Coords coords = movedGeoPoint.getInhomCoordsInD(3);
		
		// sets the min/max values
		double[] minmax;
		minmax = view3D.getXMinMax();
		xMinMax = getMinMax(minmax[0], coords.getX(), minmax[1]);
		minmax = view3D.getYMinMax();
		yMinMax = getMinMax(minmax[0], coords.getY(), minmax[1]);
		minmax = view3D.getZMinMax();
		zMinMax = getMinMax(minmax[0], coords.getZ(), minmax[1]);
			

		//Application.debug("xMinMax="+xMinMax[0]+","+xMinMax[1]);
		
		if (!movedGeoPoint.hasPath() && !movedGeoPoint.hasRegion() ){
			
			CoordMatrix4x4 plane = CoordMatrix4x4.Identity(); 
			setCurrentPlane(plane);
			//update the moving plane altitude
			getCurrentPlane().set(coords, 4);
			
		}
		
		view3D.setDragCursor();
	}



	
	////////////////////////////////////////////:
	// moving points
	
	
	
	/**
	 * return the current plane for moving
	 * @return the current plane
	 */
	private CoordMatrix4x4 getCurrentPlane(){
		return currentPlane;
	}

	/**
	 * set the current plane for moving
	 * @param plane a plane
	 */
	private void setCurrentPlane(CoordMatrix4x4 plane){
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
		Coords o;
		if (useOldMouse){
			//if (movePointMode != MOVE_POINT_MODE_XY){
				mouseLocOld = (Point) mouseLoc.clone();
				positionOld = point.getCoords().copyVector();
				//movePointMode = MOVE_POINT_MODE_XY;
			//}
			o = view3D.getPickFromScenePoint(positionOld,mouseLoc.x-mouseLocOld.x,mouseLoc.y-mouseLocOld.y); 
		}else
			o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
		view3D.toSceneCoords3D(o);
		
		
		
		//getting new position of the point
		Coords[] projects = o.projectPlaneThruVIfPossible(getCurrentPlane(), view3D.getViewDirection());
		Coords project = projects[0];
		
		
		//min-max x and y values
		checkXYMinMax(project);

	
		point.setCoords(project);
	}
	
	
	private void checkXYMinMax(Coords v){
		//min-max x value
		if (v.getX()>xMinMax[1])
			v.setX(xMinMax[1]);
		else if (v.getX()<xMinMax[0])
			v.setX(xMinMax[0]);
		
		//min-max y value
		if (v.getY()>yMinMax[1])
			v.setY(yMinMax[1]);
		else if (v.getY()<yMinMax[0])
			v.setY(yMinMax[0]);
	}
	
	
	/**
	 * set the mouse information (location and viewing direction in real world coordinates) to the point
	 * @param point a point
	 */
	protected void setMouseInformation(GeoPoint3D point){

		// Michael Borcherds
		// move mouse fast, sometimes get mouseLoc = null
		if (mouseLoc == null) return;
		
		Coords o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
		view3D.toSceneCoords3D(o);
		
		
		point.setWillingCoords(o);
		
		//TODO do this once
		//GgbVector v = new GgbVector(new double[] {0,0,1,0});
		//view3D.toSceneCoords3D(v);			
		point.setWillingDirection(view3D.getViewDirection());
	}
	
	

	
	protected void movePoint(boolean repaint){
		
		
		//Application.debug("movePointMode="+movePointMode);
		

		if (movedGeoPoint instanceof GeoPoint3D){
			GeoPoint3D movedGeoPoint3D = (GeoPoint3D) movedGeoPoint;

			if (movedGeoPoint3D.hasPath()){

				setMouseInformation(movedGeoPoint3D);		
				movedGeoPoint3D.doPath();

			}else if (movedGeoPoint3D.hasRegion()){	
				
				setMouseInformation(movedGeoPoint3D);			
				movedGeoPoint3D.doRegion();
				if (movedGeoPoint3D.getRegion()==view3D.getxOyPlane()){
					Coords coords = movedGeoPoint3D.getCoords();
					checkXYMinMax(coords);
					movedGeoPoint3D.setWillingCoords(coords);
					movedGeoPoint3D.setWillingDirection(null);
					movedGeoPoint3D.doRegion();
				}
				view3D.getCursor3D().setMoveNormalDirection(movedGeoPoint3D.getRegionParameters().getNormal());


			}else {


				//if (isShiftDown && mouseLoc != null){ //moves the point along z-axis
				if (movedGeoPoint.getMoveMode() == GeoPointND.MOVE_MODE_Z){ //moves the point along z-axis

					/*
					//getting current pick point and direction v 
					if (movePointMode != MOVE_POINT_MODE_Z){
						mouseLocOld = (Point) mouseLoc.clone();
						positionOld = movedGeoPoint3D.getCoords().copyVector();
						movePointMode = MOVE_POINT_MODE_Z;
					}
					*/
					Coords o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
					view3D.toSceneCoords3D(o);
					//GgbVector o = view3D.getPickFromScenePoint(positionOld,mouseLoc.x-mouseLocOld.x,mouseLoc.y-mouseLocOld.y);
					//view3D.toSceneCoords3D(o);



					//getting new position of the point
					Coords project = movedGeoPoint3D.getCoords().projectNearLine(o, view3D.getViewDirection(), EuclidianView3D.vz);


					//max z value
					if (project.getZ()>zMinMax[1])
						project.setZ(zMinMax[1]);
					else if (project.getZ()<zMinMax[0])
						project.setZ(zMinMax[0]);


					movedGeoPoint3D.setCoords(project);

					//update the moving plane altitude
					getCurrentPlane().set(movedGeoPoint3D.getCoords(), 4);


				}else{

					movePointOnCurrentPlane(movedGeoPoint3D, false);

				}

				//update point decorations
				view3D.updatePointDecorations(movedGeoPoint3D);
				


			}



			//update 3D cursor coordinates (false : no path or region update)
			view3D.getCursor3D().setCoords(movedGeoPoint3D.getCoords(),false);
			view3D.updateMatrixForCursor3D();

			if (repaint){
				movedGeoPoint3D.updateRepaint();//for highlighting in algebraView
			}else{
				movedGeoPoint3D.updateCascade();//TODO modify movedGeoPoint3D.updateCascade()
			}

			// update previewable
			if (view.getPreviewDrawable() != null) 	
				view.updatePreviewable();
			
			// geo point has been moved
			movedGeoPointDragged = true;
			
		}else{
			Coords o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
			view3D.toSceneCoords3D(o);
			//TODO do this once
			//GgbVector v = new GgbVector(new double[] {0,0,1,0});
			//view3D.toSceneCoords3D(view3D.getViewDirection());		
			Coords coords = o.projectPlaneThruVIfPossible(CoordMatrix4x4.Identity(), view3D.getViewDirection())[1]; //TODO use current region instead of identity
			xRW = coords.getX(); yRW = coords.getY();
			super.movePoint(repaint);
			
			view3D.getCursor3D().setCoords(movedGeoPoint.getCoordsInD(3),false);
			
		}
	}





	
	
	
	
	//////////////////////////////////////////////
	// creating a new point
	
	
	protected Hits getRegionHits(Hits hits){
		return hits.getHits(Region3D.class, tempArrayList);
	}
	
	
	/**
	 * return a copy of the preview point if one
	 */
	protected GeoPointND getNewPoint(Hits hits,
			boolean onPathPossible, boolean inRegionPossible, boolean intersectPossible, 
			boolean doSingleHighlighting) {
		
				
		GeoPoint3D point = view3D.getCursor3D();
				
		GeoPoint3D point3D;
		GeoPointND ret;
		
		switch(view3D.getCursor3DType()){		
		case EuclidianView3D.PREVIEW_POINT_FREE:
			point3D = (GeoPoint3D) kernel.getManager3D().Point3D(null, 0,0,0);
			point3D.setCoords((GeoPointND) point);
			point3D.updateCoords();
			ret = point3D;
			view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_ALREADY);
			view3D.updateMatrixForCursor3D();
			view3D.getCursor3D().setMoveMode(point3D.getMoveMode());
			if (mode==EuclidianView.MODE_POINT || mode==EuclidianView.MODE_POINT_ON_OBJECT)
				freePointJustCreated = true;
			break;

		case EuclidianView3D.PREVIEW_POINT_PATH:
			if (onPathPossible){
				Path path = point.getPath();
				if (((GeoElement) path).isGeoElement3D()){
					point3D = (GeoPoint3D) getKernel().getManager3D().Point3D(null,path);
					point3D.setWillingCoords(point.getCoords());
					point3D.doPath();
					point3D.setWillingCoords(null);
					point3D.setWillingDirection(null);
					ret = point3D;
					
				}else{
					Coords coords = point.getCoordsInD(2);
					return super.createNewPoint2D(false, path, coords.getX(), coords.getY()); 
				}
	
			}else
				return null;
			break;
			
		case EuclidianView3D.PREVIEW_POINT_REGION:
			if (inRegionPossible){
				Region region = point.getRegion();
				if (((GeoElement) region).isGeoElement3D()){
					point3D = (GeoPoint3D) getKernel().getManager3D().Point3DIn(null,region);			
					point3D.setWillingCoords(point.getCoords());
					point3D.doRegion();
					point3D.setWillingCoords(null);
					point3D.setWillingDirection(null);
					ret = point3D;
				}else{
					Coords coords = point.getCoordsInD(2);
					return super.createNewPoint2D(false, region, coords.getX(), coords.getY()); 
				}
			}else
				return null;
			break;
			
		case EuclidianView3D.PREVIEW_POINT_DEPENDENT:
			if (intersectPossible){
				point3D = (GeoPoint3D) getKernel().getManager3D().Intersect(null, 
						 view3D.getCursor3DIntersectionOf(0), 
						 view3D.getCursor3DIntersectionOf(1));
			}else
				point3D = null;
			return point3D;
			
			
		case EuclidianView3D.PREVIEW_POINT_ALREADY:
			//Application.debug(hits);
			//if (mode==EuclidianView3D.MODE_POINT || mode==EuclidianView3D.MODE_POINT_ON_OBJECT)
			GeoPointND firstPoint = (GeoPointND) hits.getFirstHit(GeoPointND.class);			
			if (firstPoint==null)
				firstPoint = (GeoPointND) getMovedGeoPoint(); //keep current point
			//view3D.getCursor3D().setRegionParameters(firstPoint.getRegionParameters());
			//Application.debug(view3D.getCursor3D().getRegionParameters().getNormal());
			return firstPoint;
		case EuclidianView3D.PREVIEW_POINT_NONE:
		default:
			return super.getNewPoint(hits, 
					onPathPossible, inRegionPossible, intersectPossible, 
					doSingleHighlighting);			

		}
		

			
		((GeoElement) ret).update();
		
		//view3D.addToHits3D((GeoElement) ret);

		setMovedGeoPoint(point3D);
		
		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_ALREADY);
		view3D.updateMatrixForCursor3D();
		
		return ret;
	
		

		
	}
	
	/** put sourcePoint coordinates in point */
	protected void createNewPoint(GeoPointND sourcePoint){
		GeoPoint3D point3D = view3D.getCursor3D();
		
		// set coords
		point3D.setCoords(sourcePoint.getCoordsInD(3),false);
		
		// set/remove path/region
		point3D.setPath(sourcePoint.getPath());
		point3D.setRegion(sourcePoint.getRegion());
		

		// update cursor 3D infos
		if (((GeoElement) sourcePoint).isIndependent() || !((GeoElement) sourcePoint).isGeoElement3D())
			point3D.setMoveNormalDirection(EuclidianView3D.vz);
		else if (sourcePoint.hasRegion())
			point3D.setMoveNormalDirection(sourcePoint.getRegionParameters().getNormal());
		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_ALREADY);
		point3D.setMoveMode(sourcePoint.getMoveMode());
		
		//Application.debug("sourcePoint:\n"+sourcePoint.getCoordsInD(3)+"\ncursor:\n"+view3D.getCursor3D().getCoordsInD(3));
	}
	
	/** put intersectionPoint coordinates in point */
	protected void createNewPointIntersection(GeoPointND intersectionPoint){
		GeoPoint3D point3D = view3D.getCursor3D();
		point3D.setCoords(intersectionPoint.getCoordsInD(3),false);
		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_DEPENDENT);
		
	}
	

	
	/**
	 * create a new free point
	 * or update the preview point
	 */
	protected GeoPointND createNewPoint(boolean forPreviewable){
		
		GeoPoint3D point3D;
		
			
		if (!forPreviewable){
			//if there's "no" 3D cursor, no point is created
			if (view3D.getCursor3DType()==EuclidianView3D.PREVIEW_POINT_NONE)
				return null;
			else
				point3D = (GeoPoint3D) kernel.getManager3D().Point3D(null, 0,0,0);
		}else{
			point3D = (GeoPoint3D) createNewPoint(true, (Region) view3D.getxOyPlane());
			if (point3D==null)
				return null;
			point3D.setPath(null);
			point3D.setRegion(null);
			view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_FREE);
			return point3D;
		}
		
		setCurrentPlane(CoordMatrix4x4.Identity());
		movePointOnCurrentPlane(point3D, false);	
		
		return point3D;
	}
	
	
	/**
	 * create a new path point
	 * or update the preview point
	 */	
	protected GeoPointND createNewPoint(boolean forPreviewable, Path path){
			
		GeoPoint3D point3D;
		
		if (!forPreviewable)
			point3D = (GeoPoint3D) getKernel().getManager3D().Point3D(null,path);
		else{
			point3D = view3D.getCursor3D();
			point3D.setPath(path);
			point3D.setRegion(null);
			view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_PATH);
		}			
		
		setMouseInformation(point3D);
		/*
		if (((GeoElement) path).isGeoList())
			Application.printStacktrace("TODO: path==GeoList");
		else*/
			point3D.doPath();
				
		return point3D;
	}
	
	/**
	 * create a new region point
	 * or update the preview point
	 */	
	protected GeoPointND createNewPoint(boolean forPreviewable, Region region){
		
		GeoPoint3D point3D;
		
		
		point3D = view3D.getCursor3D();			
		point3D.setPath(null);
		point3D.setRegion(region);
		
		setMouseInformation(point3D);
		point3D.doRegion();
		point3D.setMoveNormalDirection(point3D.getRegionParameters().getNormal());
		
		if (region==view3D.getxOyPlane()){
			Coords coords = point3D.getInhomCoords();
			if (
					coords.getX()<view3D.getxOyPlane().getXmin()
					||
					coords.getX()>view3D.getxOyPlane().getXmax()
					||
					coords.getY()<view3D.getxOyPlane().getYmin()
					||
					coords.getY()>view3D.getxOyPlane().getYmax()
			){
				view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_NONE);
				return null;
			}
		}
		

		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_REGION);


		if (!forPreviewable){
			GeoPoint3D ret = (GeoPoint3D) getKernel().getManager3D().Point3DIn(null,region);
			ret.set((GeoElement) point3D);
			//ret.setRegion(region);
			ret.doRegion();
			
			Application.debug("ici");
			

			return ret;
		}else
			return point3D;
			
	}
	
	/*
	protected void updateMovedGeoPoint(GeoPointND point){
		//movedGeoPoint3D = (GeoPoint3D) point;
		setMovedGeoPoint((GeoPoint3D) point);
	}
	*/
	
	
	
	// tries to get a single intersection point for the given hits
	// i.e. hits has to include two intersectable objects.
	protected GeoPointND getSingleIntersectionPoint(Hits hits) {
		//Application.debug(hits);
		
		if (hits.isEmpty() || hits.size() != 2)
			return null;

		GeoElement a = (GeoElement) hits.get(0);
		GeoElement b = (GeoElement) hits.get(1);
		GeoPoint3D point = null;

		
		
		//Application.debug(""+hits);

		kernel.setSilentMode(true);
		
		
    	if ( (a instanceof GeoCoordSys1D || a instanceof GeoCoordSys2D) && (b instanceof GeoCoordSys1D)
    			||(a instanceof GeoCoordSys1D && b instanceof GeoCoordSys2D) ){
    			point = (GeoPoint3D) getKernel().getManager3D().Intersect(null,  a,  b);
    		}
    	//TODO: enable other intersectionPoints  to be previewable
    	//TODO: enable intersectionPaths to be previewable
    	
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
		getSelectedPointsND(ret);
		
		//Application.printStacktrace("");
		
		return ret;	
	}	
	
	/**
	 * @return selected 3D lines
	 */
	final protected GeoCoordSys1D[] getSelectedLines3D() {
		GeoCoordSys1D[] lines = new GeoCoordSys1D[selectedLines.size()];
		getSelectedLinesND(lines);

		return lines;
	}


	// fetch the two selected points for vector
	protected GeoElement[] vector(){
		GeoPointND[] points = getSelectedPointsND();
		GeoElement[] ret = { null };
		ret[0] = getKernel().getManager3D().Vector3D(null,points[0], points[1]);
		return ret;
	}


	// build polygon	
	/*
	protected void polygon(){
		//check if there is a 3D point
			GeoPointND[] points = getSelectedPointsND();
			
			boolean point3D = false;
			for (int i=0; i<points.length && !point3D; i++)
				point3D = point3D || ((GeoElement) points[i]).isGeoElement3D();
			if (point3D)
				kernel.getManager3D().Polygon3D(null, points);
			else
				kernel.Polygon(null, getSelectedPointsND());
	}
	*/
	
	protected void circleOrSphere(NumberValue num){
		GeoPointND[] points = getSelectedPointsND();	

		getKernel().getManager3D().Sphere(null, points[0], num);
	}
	
	
	/**
	 * get center point and number
	 * @param hits 
	 * @return true if sphere created
	 */
	final protected boolean spherePointRadius(Hits hits) {
		if (hits.isEmpty())
			return false;

		addSelectedPoint(hits, 1, false);		

		// we got the center point
		if (selPoints() == 1) {	
			((GuiManager3D) app.getGuiManager()).showNumberInputDialogSpherePointRadius(app.getMenu(getKernel().getModeText(mode)),
					getSelectedPointsND()[0]);
			return true;
		}
		return false;
	}
	
	/**
	 * get point and line or vector;
	// create plane through point orthogonal to line or vector
	 * 
	 * @param hits
	 * @return orthogonal plane
	 */
	final protected GeoElement[] orthogonalPlane(Hits hits) {
		if (hits.isEmpty())
			return null;
		
		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
			if (selLines() == 0) {
				addSelectedVector(hits, 1, false);
			}
			if (selVectors() == 0) {
				addSelectedLine(hits, 1, false);
			}
		}

		if (selPoints() == 1) {
			if (selVectors() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = getSelectedPointsND();
				GeoVectorND[] vectors = getSelectedVectorsND();
				// create new plane
				GeoElement[] ret = { null };
				ret[0] = (GeoPlane3D) getKernel().getManager3D().OrthogonalPlane3D(null, points[0], vectors[0]);
				return ret;

			} else if (selLines() == 1) {
				// fetch selected point and line
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new plane
				GeoElement[] ret = { null };
				ret[0] = (GeoPlane3D) getKernel().getManager3D().OrthogonalPlane3D(null, points[0], lines[0]);
				return ret;
			}
		}
		return null;
	}


	/**
	 * get axis and point
	 * create circle with axis and through the point
	 * @param hits 
	 * @return true if circle created
	 * 
	 */
	final protected boolean circleAxisPoint(Hits hits) {
		if (hits.isEmpty())
			return false;
		
		
		addSelectedPoint(hits, 1, false);
		addSelectedLine(hits, 1, false);


		if (selPoints() == 1 && selLines() == 1) {
			getKernel().getManager3D().Circle3D(null, getSelectedLinesND()[0], getSelectedPointsND()[0]);

			return true;

		} 
		
		return false;
		
	}	
	
	/**
	 * get point, direction, enter radius
	 * create circle with center, radius, axis parallel to direction
	 * @param hits 
	 * @return true if circle created
	 * 
	 */
	final protected boolean circlePointRadiusDirection(Hits hits) {
		if (hits.isEmpty())
			return false;
		
		
		if (addSelectedPoint(hits, 1, false)==0)
			addSelectedDirection(hits, 1, false);


		if (selPoints() == 1 && selDirections() == 1) {
			((GuiManager3D) app.getGuiManager()).showNumberInputDialogCirclePointDirectionRadius(app.getMenu(getKernel().getModeText(mode)),
					getSelectedPointsND()[0],getSelectedDirections()[0]);

			return true;

		} 
		
		return false;
		
	}

	
	
	
	
	
	/** get point and line;
	 * create plane through point and line
	 * 
	 * @param hits
	 * @return true if a plane has been created
	 */
	final protected boolean planePointLine(Hits hits) {
		if (hits.isEmpty())
			return false;
		
		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
				addSelectedLine(hits, 1, false);
		}

		if (selPoints() == 1) {
			if (selLines() == 1) {
				// fetch selected point and line
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new plane
				getKernel().getManager3D().Plane3D(null, points[0], (GeoLineND) lines[0]);
				return true;
			}
		}
		return false;
	}

	/**
	 * process view in front of mode
	 * @param hits
	 * @return false (kernel won't change)
	 */
	final protected boolean viewInFrontOf(Hits hits) {
		if (hits.isEmpty())
			return false;
		
		//Application.debug(hits);
		
		addSelectedGeo(hits.getTopHits(), 1, false);//TODO hits.getTopHits() ?

		

		if (selGeos() == 1){ //clear selection
			GeoElement geo = getSelectedGeos()[0];

			//GeoElement geo = (GeoElement) hits.get(0);
			//Application.debug(view3D.hasMouse());
			Coords vn = geo.getMainDirection();
			if (vn!=null){
				if (view3D.hasMouse())
					view3D.setRotAnimation(view3D.getCursor3D().getDrawingMatrix().getVz());
				else {//doesn't come from 3D view
					if (vn.dotproduct(view3D.getViewDirection())<0)
						view3D.setRotAnimation(vn.mul(-1));
					else
						view3D.setRotAnimation(vn);
				}
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
				GeoPointND[] points = getSelectedPointsND();
				GeoCoordSys2D[] cs = getSelectedCoordSys2D();//TODO
				// create new plane
				getKernel().getManager3D().Plane3D(null, points[0], (GeoCoordSys2D) cs[0]);
				return true;
			}
		}
		return false;
	}

	
	/** get basis and height;
	 * create prism
	 * 
	 * @param hits
	 * @return true if a prism has been created
	 */
	final protected boolean rightPrism(Hits hits) {
		
		/*
		String s=hits.toString();
		s+="\nselectedPolygons=\n";
		for (int i=0;i<selectedPolygons.size();i++)
			s+=selectedPolygons.get(i)+"\n";
		*/
		
		if (hits.isEmpty())
			return false;


		
		addSelectedPolygon(hits, 1, false);
		//hits.removePolygons();
		//addSelectedNumberValue(hits, 1, false);
		addSelectedNumeric(hits, 1, false);
		
		/*
		s+="\nAprès=\n";
		for (int i=0;i<selectedPolygons.size();i++)
			s+=selectedPolygons.get(i)+"\n";
		s+="\nNumeric=\n";
		for (int i=0;i<selectedNumberValues.size();i++)
			s+=selectedNumberValues.get(i)+"\n";
		
		if (!selectionPreview)
			Application.debug(s);
		*/


		if (selPolygons() == 1) {
			if (selNumbers() == 1) {
				// fetch selected point and vector
				GeoPolygon[] basis = getSelectedPolygons();
				//NumberValue[] height = getSelectedNumberValues();
				GeoNumeric[] height = getSelectedNumbers();
				// create new plane
				getKernel().getManager3D().Prism(null, basis[0], height[0]);
				return true;
			}
		}
		return false;
	}
	
	
	///////////////////////////////////////////
	// moved GeoElements
	
	public GeoElement getMovedGeoPoint(){
		return (GeoElement) movedGeoPoint;
	}
	
	
	
	///////////////////////////////////////////
	// mouse released
	
	protected void processReleaseForMovedGeoPoint(MouseEvent e){
		
		
		
		((EuclidianView3D) view).updatePointDecorations(null);
		
		
		if (mode==EuclidianView.MODE_POINT 
				|| mode==EuclidianView.MODE_POINT_ON_OBJECT
				|| mode==EuclidianView.MODE_MOVE
		){
			if(freePointJustCreated)
				//avoid switch if the point is created by a click
				freePointJustCreated=false;
			else{
				//switch the direction of move (xy or z) in case of left-click
				//if (!movedGeoPointDragged){
				if (!DRAGGING_OCCURED && !Application.isRightClick(e)){
					movedGeoPoint.switchMoveMode();
					((EuclidianView3D) view).getCursor3D().setMoveMode(movedGeoPoint.getMoveMode());
					//Application.debug(movedGeoPoint+"\nmove mode="+movedGeoPoint.getMoveMode()+"\ncursor move mode="+((EuclidianView3D) view).getCursor3D().getMoveMode());
					((EuclidianView3D) view).setDefaultCursorWillBeHitCursor();
				}
			}
		}
		
		super.processReleaseForMovedGeoPoint(e);
		
	}

	
	///////////////////////////////////////////
	// mouse moved
	
	
	private boolean mouseMoved = false;
	//private boolean mousePressed = false;
	private MouseEvent mouseEvent = null;
	
	protected void processMouseMoved(MouseEvent e) {	
		((EuclidianView3D) view).setHits3D(mouseLoc);		
		
		mouseEvent = e;
		mouseMoved = true;
		
	}
	
	public void mousePressed(MouseEvent e) {
		mouseMoved = false;
		//mousePressed = true;
		super.mousePressed(e);
	}
	
	/*
	public void mouseReleased(MouseEvent e) {	
		mousePressed = false;
		super.mouseReleased(e);
	}
	
	public void mouseClicked(MouseEvent e) {	
		mousePressed = false;
		super.mouseClicked(e);
	}
	*/
	
	/**
	 * tells to proceed mouseMoved() (for synchronization with 3D renderer)
	 */
	public void processMouseMoved(){
		
		if (mouseMoved){
			
			mouseMoved = false;
			/*
			if (movedGeoPoint instanceof GeoPoint3D){
				GeoPoint3D movedGeoPoint3D = (GeoPoint3D) movedGeoPoint;
				if (movedGeoPoint3D.hasRegion())
					view3D.getCursor3D().setRegionParameters(movedGeoPoint3D.getRegionParameters());
			}
			*/
			((EuclidianView3D) view).updateCursor3D();
			
			super.processMouseMoved(mouseEvent);
				
			
		}
	}
	
	
	
	protected void initNewMode(int mode) {
		super.initNewMode(mode);
		
		
		
		//sets the visibility of EuclidianView3D 3D cursor
		/*
		if (mode==EuclidianView.MODE_MOVE)
			view3D.setShowCursor3D(false);
		else
			view3D.setShowCursor3D(true);
			*/
		
		
		//Application.printStacktrace("");
		
		//init the move point mode
		/*
		if (mode==EuclidianView.MODE_MOVE)
			movePointMode = MOVE_POINT_MODE_XY;
			*/
	}

	protected Previewable switchPreviewableForInitNewMode(int mode){

		Previewable previewDrawable = null;
		
		switch (mode) {

		case EuclidianView.MODE_SPHERE_TWO_POINTS:
			previewDrawable = view3D.createPreviewSphere(selectedPoints);
			break;
			
		case EuclidianView.MODE_RIGHT_PRISM:
			previewDrawable = view3D.createPreviewRightPrism(selectedPolygons);
			break;
			
		case EuclidianView.MODE_INTERSECTION_CURVE: // line through two points
			
			//only for two planes
			previewDrawable = view3D.createPreviewLineFromPlanes(selectedCoordSys2D);
			break;
			
		default:
			previewDrawable = super.switchPreviewableForInitNewMode(mode);
			break;
		}
		
		return previewDrawable;

	}
	
	//not only moveable hits are selected in move mode
	protected boolean move(Hits hits) {	
		Hits top = hits.getTopHits(1);
		//Application.debug(hits+"\ntop "+top);
		addSelectedGeo(top, 1, false);
		
		//display correctly oriented 3D cursor
		GeoPointND point = (GeoPointND) top.getFirstHit(GeoPointND.class);
		if (point!=null)
			view3D.updateCursor3D(hits);

		
		return false;
	}
	
	
	/*
	protected void mouseClickedMode(MouseEvent e, int mode){
		

		switch (mode) {
		case EuclidianView3D.MODE_VIEW_IN_FRONT_OF:
			//Application.debug("ici");
			Hits hits = view.getHits().getTopHits();
			if(!hits.isEmpty()){
				GeoElement geo = (GeoElement) view.getHits().getTopHits().get(0);
				Coords vn = geo.getMainDirection();
				if (vn!=null){
					view3D.setRotAnimation(view3D.getCursor3D().getDrawingMatrix().getVz());
				}
			}
			
			break;
			default:
				super.mouseClickedMode(e,mode);
		}
	}
	*/

	
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
	protected boolean processRotate3DView(){

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
		case EuclidianView3D.MODE_INTERSECTION_CURVE:
			changedKernel = intersectionCurve(hits); 
			break;
		case EuclidianView3D.MODE_PLANE_THREE_POINTS:
			changedKernel = (threePoints(hits, mode) != null);
			break;
		case EuclidianView3D.MODE_PLANE_POINT_LINE:
			changedKernel = planePointLine(hits);
			break;		
		
		case EuclidianView3D.MODE_ORTHOGONAL_PLANE:
			changedKernel = (orthogonalPlane(hits) != null);
			break;
			
		case EuclidianView3D.MODE_PARALLEL_PLANE:
			changedKernel = parallelPlane(hits);
			break;
			
		case EuclidianView3D.MODE_RIGHT_PRISM:
			changedKernel = rightPrism(hits);
			break;
			
		case EuclidianView3D.MODE_SPHERE_TWO_POINTS:	
			changedKernel = (circleOrSphere2(hits, mode) != null);
			break;
		case EuclidianView3D.MODE_SPHERE_POINT_RADIUS:
			changedKernel = spherePointRadius(hits);
			break;
			
		case EuclidianView3D.MODE_VIEW_IN_FRONT_OF:
			changedKernel = viewInFrontOf(hits);
			break;

		case EuclidianView3D.MODE_CIRCLE_AXIS_POINT:
			changedKernel = circleAxisPoint(hits);
			break;

		case EuclidianView3D.MODE_CIRCLE_POINT_RADIUS_DIRECTION:
			changedKernel = circlePointRadiusDirection(hits);
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
		case EuclidianView3D.MODE_RIGHT_PRISM:
			//String s = hits.toString();
			hits.removeAllPolygonsButOne();
			//s+="\nAprès:\n"+hits.toString();
			//Application.debug(s);
			break;
		default:
			super.switchModeForRemovePolygons(hits);
		}
	}
	
	
	protected GeoElement[] switchModeForThreePoints(){
		
		switch (mode) {
		case EuclidianView3D.MODE_PLANE_THREE_POINTS:
			GeoPointND[] points = getSelectedPointsND();
			GeoPlane3D ret0 = (GeoPlane3D)getKernel().getManager3D().Plane3D(null, points[0], points[1], points[2]);
			GeoElement[] ret = { ret0 };
			return ret;
		default:
			return super.switchModeForThreePoints();

		}

	}

	protected GeoElement[] switchModeForCircleOrSphere2(int mode){
		
		
		switch (mode) {
		case EuclidianView3D.MODE_SPHERE_TWO_POINTS:
			GeoPointND[] points = getSelectedPointsND();
			GeoElement[] ret = { null };
			ret[0] = getKernel().getManager3D().Sphere(null, points[0], points[1]);
			return ret;
		default:
			return super.switchModeForCircleOrSphere2(mode);
		}
	}

	
	///////////////////////////////////////////
	// MOUSE PRESSED
	
	protected void createNewPointForModePoint(Hits hits){
		createNewPoint(hits, true, true, true, true);
	}
	
	protected void createNewPointForModeOther(Hits hits){
		createNewPoint(hits, true, true, true, true);
	}

	
	protected void switchModeForMousePressed(MouseEvent e){

		Hits hits;
		
		switch (mode) {
		case EuclidianView3D.MODE_PLANE_THREE_POINTS:	
		case EuclidianView3D.MODE_SPHERE_TWO_POINTS:	
		case EuclidianView3D.MODE_SPHERE_POINT_RADIUS:	
			view.setHits(mouseLoc);
			hits = view.getHits();hits.removePolygons();
			createNewPoint(hits, true, true, true, true);
			break;
			
		case EuclidianView3D.MODE_ORTHOGONAL_PLANE:
		case EuclidianView3D.MODE_PLANE_POINT_LINE:
			view.setHits(mouseLoc);
			hits = view.getHits();hits.removePolygons();
			createNewPoint(hits, false, false, true);
			break;	
			
		case EuclidianView3D.MODE_PARALLEL_PLANE:
			view.setHits(mouseLoc);
			hits = view.getHits();hits.removePolygons();
			createNewPoint(hits, true, false, false, true);
			break;	
			
		case EuclidianView3D.MODE_RIGHT_PRISM:
			view.setHits(mouseLoc);
			hits = view.getHits();
			switchModeForRemovePolygons(hits);
			//Application.debug(hits.toString());
			rightPrism(hits);
			view3D.updatePreviewable();
			break;
			
		case EuclidianView3D.MODE_ROTATEVIEW:
			startLoc = mouseLoc; 
			view.rememberOrigins();
			moveMode = MOVE_ROTATE_VIEW;
			break;
			
		case EuclidianView.MODE_CIRCLE_AXIS_POINT:	
		case EuclidianView.MODE_CIRCLE_POINT_RADIUS_DIRECTION:	
			view.setHits(mouseLoc);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.size() == 0)
				createNewPoint(hits, false, true, true);
			break;
		case EuclidianView.MODE_INTERSECTION_CURVE:
			//no need to do anything for preview when mouse is pressed
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
			return true;
		case EuclidianView3D.MODE_RIGHT_PRISM:
			((DrawPolyhedron3D) view3D.getPreviewDrawable()).createPolyhedron();
			//view3D.setPreview(null);//remove current previewable
			//view3D.setPreview(view3D.createPreviewRightPrism(selectedPolygons));//init new one	
			return true;
		case EuclidianView3D.MODE_VIEW_IN_FRONT_OF:
			//Application.debug("hop");
			//TODO implement choose geo
			return true;
		default:
			return super.switchModeForMouseReleased(mode, hits, changedKernel);
			
		}

	}
	
	protected Hits addPointCreatedForMouseReleased(Hits hits){
		
		hits.add(getMovedGeoPoint());	
		return hits;
	}
	
	
	public void showDrawingPadPopup(Point mouseLoc){
		((GuiManager3D) app.getGuiManager()).showDrawingPadPopup3D((JPanel) view, mouseLoc);		
	}

	
	///////////////////////////////////////////
	// INTERSECTIONS
	
	///////////////////////////////////////////
	// INTERSECTIONS
	
	/**
	 *  get two objects (lines or conics) and create intersection point 
	 */
	protected GeoElement[] intersect(Hits hits) {
		
		if (hits.isEmpty())
			return null;		
		
		// when two objects are selected at once then only one single
		// intersection point should be created	
		
		//not working
		boolean singlePointWanted = selGeos() == 0;
		
		// check how many interesting hits we have
		if (!selectionPreview  && hits.size() > 2 - selGeos()) {
			
			Hits goodHits = new Hits();

			hits.getHits(GeoLineND.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			hits.getHits(GeoCoordSys2D.class, tempArrayList);
			goodHits.addAll(tempArrayList);			
			hits.getHits(GeoConicND.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			hits.getHits(GeoPolygon.class, tempArrayList);
			goodHits.addAll(tempArrayList);

			hits = goodHits;
		}
		
		//not working
		//boolean addedCurves = false;
		
		// get lines, segments, etc.
		addSelectedLine(hits, 10, true);
		// currently tested only for 3D conics
		addSelectedConic(hits, 10, true);
		// currently tested only for planes
		addSelectedCS2D(hits, 10, true);
		
		// polygons
		addSelectedPolygon(hits, 10, true);
		
		singlePointWanted = singlePointWanted && selGeos() == 2;
		
		//if (selGeos() > 2)
		//	return null;

		//Application.debug("lines="+selLines()+"\ncs2D="+selCS2D()+"\nconics="+selConicsND());
		
		
		if (selLines() >= 2) {// two lines	
			GeoLineND[] lines = getSelectedLinesND();
			GeoElement[] ret = { null };
			ret[0] = getKernel().getManager3D().Intersect(null, (GeoElement) lines[0], (GeoElement) lines[1]);
			return ret;

		} else if (selLines() ==1){
			if (selConics()>=1 ) {// line-conic
				GeoLineND line = getSelectedLinesND()[0];
				GeoConicND conic = getSelectedConicsND()[0];
				GeoElement[] ret = new GeoElement[2];

				if (singlePointWanted) {
					Coords picked = view3D.getPickPoint(mouseLoc.x, mouseLoc.y);
				
					
					ret[0] = getKernel().getManager3D().IntersectLineConicSingle(null, line, conic, 
							picked.getX(),picked.getY(), view3D.getToSceneMatrix());
				} else {
					GeoPointND[] points = getKernel().getManager3D().IntersectLineConic(null, line, conic);
					for(int i=0;i<2; i++)
						ret[i] = (GeoElement) points[i];
				}

				
				
	
				return ret;
			}else if (selCS2D()>=1) {// line-CS2D
				
				GeoLineND line = getSelectedLinesND()[0];
				GeoCoordSys2D cs2Ds = getSelectedCS2D()[0];
				/*
				int firstP = 0;
				for (firstP = 0; firstP<cs2Ds.length; firstP++) {
					if (cs2Ds[firstP] instanceof GeoPolygon)
						return getKernel().getManager3D().Intersect(
								new String[] {null},
								line,
								(GeoSurfaceFinite) cs2Ds[firstP]);
				}
				*/
				
				if (cs2Ds instanceof GeoPolygon) {
					return getKernel().getManager3D().IntersectionPoint(
								new String[] {null},
								(GeoLineND) line,
								(GeoPolygon) cs2Ds
								);
				} else {
					GeoElement[] ret = new GeoElement[1];
					ret[0] = getKernel().getManager3D().Intersect(
					null, (GeoElement) line, (GeoElement) cs2Ds);
					return ret;
				}
		/*	}else if (selPolygons()>=1) {// line-polygon
				GeoLineND line = getSelectedLinesND()[0];
				GeoPolygon polygon = getSelectedPolygons()[0];
				GeoElement[] ret = { null };
				ret[0] = getKernel().getManager3D().Intersect(null, (GeoElement) line, (GeoElement) polygon);
		 */
			}
		} else if (selConics()>=2 ) {// conic-conic
			GeoConicND[] conics = getSelectedConicsND();
			GeoElement[] ret = new GeoElement[4];
			GeoPointND[] points = getKernel().getManager3D().IntersectConics(null, conics[0], conics[1]);
			for(int i=0;i<4; i++)
				ret[i] = (GeoElement) points[i];
			return ret;
		} else if (selCS2D()>=2 
				&& selPolygons()>=1 
				&& selPolygons() <selCS2D()) { // plane-polygon
			int n = selCS2D();
			GeoPolygon polygon = getSelectedPolygons()[0];
			GeoCoordSys2D[] CS2Ds = getSelectedCS2D();
			for (int i = 0; i<n; i++) {
				if (CS2Ds[i] != polygon) {
					return getKernel().getManager3D().IntersectionPoint(null, 
							(GeoPlane3D) CS2Ds[i], polygon);
				}
			}
		}
		
		return null;
	}

	//TODO: color should not be created here
	public Color intersectionCurveColorPlanarPlanar = new Color(127, 0, 255);

		/**
		 * 
		 * @param hits
		 * @return true if a curve is created
		 */
		private boolean intersectionCurve(Hits hits) {
			
			/**
			 * rationale: only look at surfaces; for simplicity at this point,
			 * choose the first two surfaces in hits, then find out the intersection
			 * algorithm: 
			 *  - if hits is empty, do nothing.
			 *  - if hits contains no surfaces, do nothing.
			 *   Note: surfaces include planes, (planar) region, quadric, and general surfaces.
			 *   now focus on planes and quadric.
			 */
			if (hits.isEmpty())
				return false;	

			//////////////////////////////////////////////////
			/* TODO too many hits
			if (!selectionPreview  && hits.size() > 2 - selGeos()) {
				Hits goodHits = new Hits();
				
				hits.getHits(GeoCoordSys2D.class, tempArrayList);
				goodHits.addAll(tempArrayList);
				hits.getHits(GeoQuadric3D.class, tempArrayList);
				goodHits.addAll(tempArrayList);
			
				hits = goodHits;
				
				
	
			}
			*/

			addSelectedCS2D(hits, 10, true);
			addSelectedQuadric(hits, 10, true);

			
			if (selCS2D()>=2)  { // cs2D-cs2D
				
				//Application.debug(selCS2D());
				
				GeoCoordSys2D[] cs2Ds = getSelectedCS2D();
			
				int pIndex = 0;
				int npIndex = 0;
				boolean foundP = false;
				boolean foundNp = false;
				for (int i = 0; i<cs2Ds.length; i++){
					if ( cs2Ds[i] instanceof GeoPolygon ) {
						if (!foundP) {
							pIndex = i;
							foundP = true;		
						}
					} else {
						if ( !foundNp ){
							npIndex = i;
							foundNp = true;
						}
					}
					if (foundP && foundNp)
						break;
				}
				
				if (!foundP) {
					GeoElement[] ret = new GeoElement[1];
					ret[0] = getKernel().getManager3D().Intersect(null, 
							(GeoElement) cs2Ds[0], (GeoElement) cs2Ds[1]);
					return ret[0].isDefined();	
				} else if (foundP && foundNp) {
					GeoElement[] ret = getKernel().getManager3D().IntersectionSegment(new String[] {null}, 
							(GeoPlane3D) cs2Ds[npIndex], (GeoSurfaceFinite) cs2Ds[pIndex]);

					return (ret==null || ret[0]==null);
				}
			}

	
			else if ((selCS2D() >= 1) &&  (selQuadric() >= 1)) { //plane-quadric
				
				
				GeoElement plane = (GeoElement) getSelectedCS2D()[0];
				GeoQuadric3D quad = getSelectedQuadric()[0];
				GeoElement[] ret = {kernel.getManager3D().Intersect( null, (GeoPlaneND) plane, (GeoQuadricND) quad)};
				return ret[0].isDefined();
			}
			
	
			
			
			////////////////////////////////////////
			
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
	
	
	///////////////////////////////////////////
	// selectedCS2D list, similar to selectedCS1D 
	
	/** selected 2D coord sys */
	@SuppressWarnings("unchecked")
	protected ArrayList<GeoCoordSys2D> selectedCS2D = new ArrayList<GeoCoordSys2D>();	
	
	/** add hits to selectedCS2D
	 * @param hits hits
	 * @param max max number of hits to add
	 * @param addMoreThanOneAllowed if adding more than one is allowed
	 * @return TODO
	 */
	final protected int addSelectedCS2D(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedCS2D, GeoCoordSys2D.class);
	}
	
	
	/**
	 * return number of selected 2D coord sys
	 * @return number of selected 2D coord sys
	 */
	final int selCS2D() {
		return selectedCS2D.size();
	}	
	
	
	/** return selected 2D coord sys
	 * also clear all selected 2D coord sys. 
	 * @return selected 2D coord sys
	 */
	@SuppressWarnings("unchecked")
	final protected GeoCoordSys2D[] getSelectedCS2D() {
		GeoCoordSys2D[] planes = new GeoCoordSys2D[selectedCS2D.size()];
		int i = 0;
		Iterator it = selectedCS2D.iterator();
		while (it.hasNext()) {
			planes[i] = (GeoCoordSys2D) it.next();
			i++;
		}
		clearSelection(selectedCS2D);
		return planes;
	}	
	
	///for quadric
	protected ArrayList<GeoQuadric3D> selectedQuadric = new ArrayList<GeoQuadric3D>();	
	
	final int selQuadric() {
		return selectedQuadric.size();
	}	
	
	final protected int addSelectedQuadric(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedQuadric, GeoQuadric3D.class);
	}	
	final protected GeoQuadric3D[] getSelectedQuadric() {
		GeoQuadric3D[] quads = new GeoQuadric3D[selectedQuadric.size()];
		int i = 0;
		Iterator it = selectedQuadric.iterator();
		while (it.hasNext()) {
			quads[i] = (GeoQuadric3D) it.next();
			i++;
		}
		clearSelection(selectedQuadric);
		return quads;
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
	
	
	/////////////////////////////////////////////////////
	// 
	// CURSOR
	//
	/////////////////////////////////////////////////////

	/**
	 * @param cursorType type of the cursor
	 * @return if the 3D cursor is visible for current mode
	 */
	public boolean cursor3DVisibleForCurrentMode(int cursorType){
				
		if (cursorType==EuclidianView3D.PREVIEW_POINT_ALREADY){
			switch(mode){
			//cross arrows for moving point
			case EuclidianView.MODE_MOVE:
			case EuclidianView.MODE_POINT:
			case EuclidianView.MODE_POINT_ON_OBJECT:
				return true;//!mousePressed; //don't display cursor if dragging 
			default:
				return false;			
			}		
		} else if (cursorType==EuclidianView3D.PREVIEW_POINT_DEPENDENT) {
			switch(mode){
			//modes in which the result is not a dependent point 
			case EuclidianView.MODE_POINT:
			case EuclidianView.MODE_INTERSECT:
				return true;

			default:
				return false;					
			}
		} else{
			switch(mode){
			//modes where point can be created on path/region
			
			case EuclidianView.MODE_POINT_ON_OBJECT:
				
			case EuclidianView.MODE_JOIN:
			case EuclidianView.MODE_SEGMENT:
				
			case EuclidianView.MODE_RAY:
			case EuclidianView.MODE_VECTOR:

			case EuclidianView.MODE_VECTOR_FROM_POINT:

			case EuclidianView.MODE_POLYGON:
			case EuclidianView.MODE_POLYLINE:
			case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
			case EuclidianView.MODE_PLANE_THREE_POINTS:
			case EuclidianView.MODE_SPHERE_TWO_POINTS:
				return true;

			default:
				return false;			
			}
		}
	}
	





	////////////////////////////////////////
	// HANDLING PARTS OF PREVIEWABLES
	////////////////////////////////////////
	
	private GeoElement handledGeo;
	
	/**
	 * sets the geo as an handled geo (for previewables)
	 * @param geo
	 */
	public void setHandledGeo(GeoElement geo){		
		handledGeo = geo;
		if (handledGeo==null)
			return;
		setStartPointLocation();
		handledGeo.recordChangeableCoordParentNumbers();
	}
	
	
	protected boolean viewHasHitsForMouseDragged(){
		//Application.debug(moveMode);
		if (moveMode==MOVE_POINT && view3D.getCursor3DType()==EuclidianView3D.PREVIEW_POINT_ALREADY)
			return view.getHits().containsGeoPoint(); //if a point is under the mouse, don't try to find another hit
		else
			return super.viewHasHitsForMouseDragged();
	}
	
	
	public void mouseDragged(MouseEvent e) {
		if (handledGeo!=null){
			setMouseLocation(e);
			updateTranslationVector();
			handledGeo.moveFromChangeableCoordParentNumbers(translationVec3D, startPoint3D, view3D.getViewDirection(), null, null);
			//view3D.updatePreviewable();
			kernel.notifyRepaint();
			return;
		}
		
		super.mouseDragged(e);
	}
	
	
	////////////////////////////////////////
	// MOVE OBJECTS
	////////////////////////////////////////
	
	private Coords startPoint3D;

	private Coords translationVec3D = new Coords(4);
	
	private void updateTranslationVector(){
		Coords point = view3D.getPickPoint(mouseLoc.x, mouseLoc.y);
		view3D.toSceneCoords3D(point);
		translationVec3D = point.sub(startPoint3D);
	}
	
	public void setStartPointLocation(){
		if (mouseLoc==null)//case that it's algebra view calling
			return;
		startPoint3D = view3D.getPickPoint(mouseLoc.x, mouseLoc.y);
		view3D.toSceneCoords3D(startPoint3D);
		
		super.setStartPointLocation();
	}

	protected void moveDependent(boolean repaint) {

		updateTranslationVector();
		GeoElement.moveObjects(translateableGeos, translationVec3D, startPoint3D, view3D.getViewDirection());	
	
		kernel.notifyRepaint();
	}
	
	protected void moveMultipleObjects(boolean repaint) {	
		/*
		translationVec.setX(xRW - startPoint.x);
		translationVec.setY(yRW - startPoint.y);
		startPoint.setLocation(xRW, yRW);
		startLoc = mouseLoc;

		// move all selected geos
		GeoElement.moveObjects(app.getSelectedGeos(), translationVec, new GgbVector(xRW, yRW, 0));									
			*/
		
		Application.debug("TODO");
	}	
	
	final protected int addSelectedPolygon3D(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPolygons3D, GeoPolygon3D.class);
	}
	final protected GeoPolygon[] getSelectedPolygons3D() {				
		GeoPolygon[] ret = new GeoPolygon[selectedPolygons3D.size()];
		for (int i = 0; i < selectedPolygons3D.size(); i++) {		
			ret[i] = (GeoPolygon) selectedPolygons3D.get(i);
		}
		clearSelection(selectedPolygons3D);
		return ret;
	}

	protected final int selPolygons3D() {
		return selectedPolygons3D.size();
	}

	


	
	protected GeoElement[] orthogonal(GeoPointND point, GeoLineND line){
		if (((GeoElement) point).isGeoElement3D() || ((GeoElement) line).isGeoElement3D())
			return new GeoElement[] {(GeoElement) getKernel().getManager3D().OrthogonalLine3D(null,point, line)};		
		else
			return orthogonal2D(point, line);
	
	}
	

	
}

