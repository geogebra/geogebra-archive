package geogebra3D.euclidian3D;


import geogebra.euclidian.Drawable;
import geogebra.euclidian.DrawableND;
import geogebra.euclidian.EuclidianConstants;
import geogebra.euclidian.EuclidianPen;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.euclidian.Hits;
import geogebra.euclidian.Previewable;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.GeoConicPart;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolyLine;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.kernel.Matrix.CoordMatrix;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.CoordMatrixUtil;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoQuadricND;
import geogebra.kernel.kernelND.GeoRayND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.kernel.kernelND.GeoVectorND;
import geogebra.main.Application;
import geogebra.util.Unicode;
import geogebra3D.euclidian3D.opengl.PlotterCursor;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoAxis3D;
import geogebra3D.kernel3D.GeoConic3D;
import geogebra3D.kernel3D.GeoCurveCartesian3D;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPlane3DConstant;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoQuadric3D;
import geogebra3D.kernel3D.GeoQuadric3DPart;
import geogebra3D.kernel3D.GeoSurfaceCartesian3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JPanel;

import org.freehep.graphicsio.emf.gdi.IntersectClipRect;


/**
 * Class for 3D view
 * @author matthieu
 *
 */
public class EuclidianView3D extends JPanel implements Printable, EuclidianViewInterface {

	

	private static final long serialVersionUID = -8414195993686838278L;
	
	
	
	//private Kernel kernel;
	private Kernel3D kernel3D;
	protected Application app;
	private EuclidianController3D euclidianController3D;
	private Renderer renderer;
	
	
	
	// distances between grid lines
	protected boolean automaticGridDistance = true;
	// since V3.0 this factor is 1, before it was 0.5
	final public static double DEFAULT_GRID_DIST_FACTOR = 1;
	public static double automaticGridDistanceFactor = DEFAULT_GRID_DIST_FACTOR;

	double[] gridDistances = { 2, 2, 2 };
	

	protected boolean[] showAxesNumbers = { true, true, true };
	protected String[] axesLabels = { "x", "y", "z" };
	protected String[] axesUnitLabels = { null, null, null };

	protected boolean[] piAxisUnit = { false, false, false };
	
	
	protected double[] axesNumberingDistances = { 2, 2, 2 };
	protected boolean[] automaticAxesNumberingDistances = { true, true, true };


	protected int[] axesTickStyles = { AXES_TICK_STYLE_MAJOR,
			AXES_TICK_STYLE_MAJOR, AXES_TICK_STYLE_MAJOR };
	

	private double[] axisCross = {0,0,0};
	private boolean[] positiveAxes = {false, false, false};
	private boolean[] drawBorderAxes = {false,false, false};
	
	//viewing values
	private double XZero = 0;
	private double YZero = -117;
	private double ZZero = 0;
	
	private double XZeroOld = 0;
	private double YZeroOld = 0;
	
	//list of 3D objects
	private boolean waitForUpdate = true; //says if it waits for update...
	//public boolean waitForPick = false; //says if it waits for update...
	private Drawable3DLists drawable3DLists;// = new DrawList3D();
	/** list for drawables that will be added on next frame */
	private LinkedList<Drawable3D> drawable3DListToBeAdded;// = new DrawList3D();
	/** list for drawables that will be removed on next frame */
	private LinkedList<Drawable3D> drawable3DListToBeRemoved;// = new DrawList3D();
	/** list for Geos to that will be added on next frame */
	private TreeMap<String,GeoElement> geosToBeAdded;
	/** set for geos to had to hits */
	private TreeSet<GeoElement> geosToAddToHits;
	
	
	// Map (geo, drawable) for GeoElements and Drawables
	private TreeMap<GeoElement,Drawable3D> drawable3DMap = new TreeMap<GeoElement,Drawable3D>();
	
	//matrix for changing coordinate system
	private CoordMatrix4x4 m = CoordMatrix4x4.Identity(); 
	private CoordMatrix4x4 mInv = CoordMatrix4x4.Identity();
	private CoordMatrix4x4 undoRotationMatrix = CoordMatrix4x4.Identity();
	
	
	public final static double ANGLE_ROT_OZ=-60;
	public final static double ANGLE_ROT_XOY=20;
	
	private double a = ANGLE_ROT_OZ;
	private double b = ANGLE_ROT_XOY;//angles (in degrees)
	private double aOld, bOld;
	private double aNew, bNew;
	
	

	

	//picking and hits
	private Hits3D hits = new Hits3D(); //objects picked from openGL
	
	//base vectors for moving a point
	/** origin */
	static public Coords o = new Coords(new double[] {0.0, 0.0, 0.0,  1.0});
	/** vx vector */
	static public Coords vx = new Coords(new double[] {1.0, 0.0, 0.0,  0.0});
	/** vy vector */
	static public Coords vy = new Coords(new double[] {0.0, 1.0, 0.0,  0.0});
	/** vz vector */
	static public Coords vz = new Coords(new double[] {0.0, 0.0, 1.0,  0.0});
	/** vzNeg vector */
	static public Coords vzNeg = new Coords(new double[] {0.0, 0.0, -1.0,  0.0});
	
	/** direction of view */
	private Coords viewDirection = vz.copyVector();
	private Coords eyePosition = new Coords(4);

	
	//axis and xOy plane
	private GeoPlane3D xOyPlane;
	private GeoAxis3D[] axis;
	
	private DrawPlane3D xOyPlaneDrawable;
	private DrawAxis3D[] axisDrawable;
	
	
	/** number of drawables linked to this view (xOy plane, Ox, Oy, Oz axis) */
	static final public int DRAWABLES_NB = 4;
	/** id of z-axis */
	static final int AXIS_Z = 2; //AXIS_X and AXIS_Y already defined in EuclidianViewInterface

	//point decorations	
	private DrawPointDecorations pointDecorations;
	private boolean decorationVisible = false;

	//preview
	private Previewable previewDrawable;
	private GeoPoint3D cursor3D;
	public DrawLine3D previewDrawLine3D;
	public DrawConic3D previewDrawConic3D;
	public GeoLine3D previewLine;
	public GeoConic3D previewConic;
	private GeoElement[] cursor3DIntersectionOf = new GeoElement[2]; 
	
	//cursor
	/** no point under the cursor */
	public static final int PREVIEW_POINT_NONE = 0;
	/** free point under the cursor */
	public static final int PREVIEW_POINT_FREE = 1;
	/** path point under the cursor */
	public static final int PREVIEW_POINT_PATH = 2;
	/** region point under the cursor */
	public static final int PREVIEW_POINT_REGION = 3;
	/** dependent point under the cursor */
	public static final int PREVIEW_POINT_DEPENDENT = 4;
	/** already existing point under the cursor */
	public static final int PREVIEW_POINT_ALREADY = 5;
	
	
	
	private int cursor3DType = PREVIEW_POINT_NONE;

	
	private static final int CURSOR_DEFAULT = 0;
	private static final int CURSOR_DRAG = 1;
	private static final int CURSOR_MOVE = 2;
	private static final int CURSOR_HIT = 3;
	private int cursor = CURSOR_DEFAULT;
	

	//mouse
	private boolean hasMouse = false;
	
	
	
	// animation
	
	/** tells if the view is under animation for scale */
	private boolean animatedScale = false;
	/** starting and ending scales */
	private double animatedScaleStart, animatedScaleEnd;
	/** velocity of animated scaling */
	private double animatedScaleTimeFactor;
	/** starting time for animated scale */
	private long animatedScaleTimeStart;
	/** x start of animated scale */
	private double animatedScaleStartX;
	/** y start of animated scale */
	private double animatedScaleStartY;
	/** x end of animated scale */
	private double animatedScaleEndX;
	/** y end of animated scale */
	private double animatedScaleEndY;
	
	
	/** tells if the view is under continue animation for rotation */
	private boolean animatedContinueRot = false;
	/** speed for animated rotation */
	private double animatedRotSpeed;
	/** starting time for animated rotation */
	private long animatedRotTimeStart;
	
	/** tells if the view is under animation for rotation */
	private boolean animatedRot = false;

	
	
	
	
	
	/** says if the view is frozen (see freeze()) */
	private boolean isFrozen = false;
	
	
	
	/**  selection rectangle  TODO */
	protected Rectangle selectionRectangle = new Rectangle();


	
	/**
	 * common constructor
	 * @param ec controller on this
	 */
	public EuclidianView3D(EuclidianController3D ec){

		

		
		this.euclidianController3D = ec;
		this.kernel3D = (Kernel3D) ec.getKernel();
		euclidianController3D.setView(this);
		euclidianController3D.setPen(new EuclidianPen(app,this));
		app = ec.getApplication();	
		
		start();
		
		initView(false);
	}
	
	
	public Application getApplication() {
		return app;
	}
	
	private void start(){
		
		drawable3DLists = new Drawable3DLists(this);
		drawable3DListToBeAdded = new LinkedList<Drawable3D>();
		drawable3DListToBeRemoved = new LinkedList<Drawable3D>();
		
		geosToBeAdded = new TreeMap<String,GeoElement>();
		
		geosToAddToHits = new TreeSet<GeoElement>();
		
		//TODO replace canvas3D with GLDisplay
		Application.debug("create gl renderer");
		renderer = new Renderer(this);
		renderer.setDrawable3DLists(drawable3DLists);
		
		
		
        //JPanel canvas = this;
		
        Component canvas = renderer.canvas;
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, canvas);
		
		
		
		attachView();
		
		// register Listener
		canvas.addMouseMotionListener(euclidianController3D);
		canvas.addMouseListener(euclidianController3D);
		canvas.addMouseWheelListener(euclidianController3D);
		canvas.setFocusable(true);
		
		
		
		//previewables
		//kernel3D.setSilentMode(true);
		cursor3D = new GeoPoint3D(kernel3D.getConstruction());
		cursor3D.setCoords(0,0,0,1);
		cursor3D.setIsPickable(false);
		//cursor3D.setLabelOffset(5, -5);
		//cursor3D.setEuclidianVisible(false);
		cursor3D.setMoveNormalDirection(EuclidianView3D.vz);
		//kernel3D.setSilentMode(false);
		
		
		
		
		initAxisAndPlane();
		
		//point decorations
		initPointDecorations();
		
		//x, y, min, max
		xminObject = new GeoNumeric(kernel3D.getConstruction());
		xmaxObject = new GeoNumeric(kernel3D.getConstruction());
		yminObject = new GeoNumeric(kernel3D.getConstruction());
		ymaxObject = new GeoNumeric(kernel3D.getConstruction());
		
		
		
	}
	
	
	

	
	
	
	/**
	 * init the axis and xOy plane
	 */
	public void initAxisAndPlane(){
		
		


		//axis
		axis = new GeoAxis3D[3];
		axisDrawable = new DrawAxis3D[3];
		axis[0] = kernel3D.getXAxis3D();
		axis[1] = kernel3D.getYAxis3D();
		axis[2] = kernel3D.getZAxis3D();
		
		
		for(int i=0;i<3;i++){
			axis[i].setLabelVisible(true);
			axisDrawable[i] = (DrawAxis3D) createDrawable(axis[i]);
		}
		
		
		//plane	
		xOyPlane = kernel3D.getXOYPlane();
		xOyPlane.setEuclidianVisible(true);
		xOyPlane.setGridVisible(true);
		xOyPlane.setPlateVisible(false);
		xOyPlaneDrawable = (DrawPlane3D) createDrawable(xOyPlane);

		
		
			
	}

	// POINT_CAPTURING_STICKY_POINTS locks onto these points
	// not implemented yet in 3D
	public ArrayList<GeoPointND> getStickyPointList() {
		return null;
	}
	
	
	/** return the 3D kernel
	 * @return the 3D kernel
	 */
	public Kernel3D getKernel(){
		return kernel3D;
	}
	
	
	
	
	/**
	 * @return controller
	 */
	public EuclidianController3D getEuclidianController(){
		return euclidianController3D;
	}
	
	
	/**
	 * @return gl renderer
	 */
	public Renderer getRenderer(){
		return renderer;
	}
	
	

	/**
	 * adds a GeoElement3D to this view
	 */	
	public void add(GeoElement geo) {
		
		if (geo.isVisibleInView3D()){
			setWaitForUpdate();
			geosToBeAdded.put(geo.getLabel(),geo);
		}
	}
	
	/**
	 * add the geo now
	 * @param geo
	 */
	private void addNow(GeoElement geo){
		
		//check if geo has been already added
		if (getDrawableND(geo)!=null)
			return;
		
		//create the drawable
		Drawable3D d = null;
		d = createDrawable(geo);
		if (d != null) {
			drawable3DLists.add(d);
			//if geo wait to be hitted by mouse, add its new drawable
			if(geosToAddToHits.remove(geo)){
				addToHits3D(d);
				//Application.debug(geo+"\n"+hits);
			}
		}
	}
	
	
	/**
	 * add the drawable to the lists of drawables
	 * @param d
	 */
	public void addToDrawable3DLists(Drawable3D d){
		
		/*
		if (d.getGeoElement().getLabel().equals("a")){
			Application.debug("d="+d);
		}
		*/
		
		setWaitForUpdate();
		drawable3DListToBeAdded.add(d);
	}

	/**
	 * Create a {@link Drawable3D} linked to the {@link GeoElement3D}
	 * 
	 * <h3> Exemple:</h3>
	  
	  For a GeoElement3D called "GeoNew3D", add in the switch the following code:
	    <p>
	    <code>
	    case GeoElement3D.GEO_CLASS_NEW3D: <br> &nbsp;&nbsp;                   
           d = new DrawNew3D(this, (GeoNew3D) geo); <br> &nbsp;&nbsp;
           break; <br> 
        }
        </code>

	 * 
	 * @param geo GeoElement for which the drawable is created
	 * @return the drawable
	 */
	protected Drawable3D createDrawable(GeoElement geo) {
		Drawable3D d=null;
		if (geo.hasDrawable3D()){

			switch (geo.getGeoClassType()) {

			// 2D also shown in 3D
			case GeoElement3D.GEO_CLASS_LIST:
				d = new DrawList3D(this, (GeoList) geo);
				break;				

				// 3D stuff
			case GeoElement.GEO_CLASS_POINT:
			case GeoElement3D.GEO_CLASS_POINT3D:
				d = new DrawPoint3D(this, (GeoPointND) geo);
				break;									

			case GeoElement3D.GEO_CLASS_VECTOR:
			case GeoElement3D.GEO_CLASS_VECTOR3D:
				d = new DrawVector3D(this, (GeoVectorND) geo);
				break;									

			case GeoElement.GEO_CLASS_SEGMENT:
			case GeoElement3D.GEO_CLASS_SEGMENT3D:
				d = new DrawSegment3D(this, (GeoSegmentND) geo);
				break;									


			case GeoElement3D.GEO_CLASS_PLANE3D:
				if (geo instanceof GeoPlane3DConstant)
					d = new DrawPlaneConstant3D(this, (GeoPlane3D) geo,
							axisDrawable[AXIS_X],axisDrawable[AXIS_Y]);
				else
					d = new DrawPlane3D(this, (GeoPlane3D) geo);

				break;		
				

			case GeoElement3D.GEO_CLASS_POLYGON:
			case GeoElement3D.GEO_CLASS_POLYGON3D:
				d = new DrawPolygon3D(this, (GeoPolygon) geo);
				break;									

			case GeoElement3D.GEO_CLASS_POLYLINE:
			case GeoElement3D.GEO_CLASS_POLYLINE3D:
				d = new DrawPolyLine3D(this, (GeoPolyLine) geo);
				break;									

				
			case GeoElement.GEO_CLASS_LINE:	
			case GeoElement3D.GEO_CLASS_LINE3D:	
				d = new DrawLine3D(this, (GeoLineND) geo);	
				break;									

			case GeoElement.GEO_CLASS_RAY:
			case GeoElement3D.GEO_CLASS_RAY3D:
				d = new DrawRay3D(this, (GeoRayND) geo);					
				break;	

			case GeoElement3D.GEO_CLASS_CONIC:					
			case GeoElement3D.GEO_CLASS_CONIC3D:					
				d = new DrawConic3D(this, (GeoConicND) geo);
				break;	
				
			case GeoElement3D.GEO_CLASS_CONICPART:					
				d = new DrawConicPart3D(this, (GeoConicPart) geo);
				break;	

			case GeoElement3D.GEO_CLASS_AXIS3D:	
				d = new DrawAxis3D(this, (GeoAxis3D) geo);	
				break;	

			case GeoElement3D.GEO_CLASS_CURVECARTESIAN3D:	
				d = new DrawCurve3D(this, (GeoCurveCartesian3D) geo);	
				break;									




			case GeoElement3D.GEO_CLASS_QUADRIC:					
				d = new DrawQuadric3D(this, (GeoQuadric3D) geo);
				break;									

			case GeoElement3D.GEO_CLASS_QUADRIC_PART:					
				d = new DrawQuadric3DPart(this, (GeoQuadric3DPart) geo);
				break;	

			case GeoElement.GEO_CLASS_FUNCTION_NVAR:
				GeoFunctionNVar geoFun = (GeoFunctionNVar) geo;
				switch(geoFun.getVarNumber()){
				case 2:
					d = new DrawFunction2Var(this, geoFun);
					break;
				case 3:
					d = new DrawImplicitFunction3Var(this, geoFun);
					break;
				}
				break;	
				
				
			case GeoElement3D.GEO_CLASS_SURFACECARTESIAN3D:	
				d = new DrawSurface3D(this, (GeoSurfaceCartesian3D) geo);
				break;	
				

			case GeoElement3D.GEO_CLASS_TEXT:
				d = new DrawText3D(this,(GeoText) geo);
				break;

			}
			
								


		}

		
		if (d != null) 			
			drawable3DMap.put(geo, d);
		
		
		return d;
	}
	
	
	
	
	
	public DrawableND createDrawableND(GeoElement geo) {
		return createDrawable(geo);
	}
	
	
	
	
	
	
	
	
	/**
	 * converts the vector to scene coords
	 * @param vInOut
	 */
	final public void toSceneCoords3D(Coords vInOut) {	
		changeCoords(mInv,vInOut);		
	}
	
	
	final private void changeCoords(CoordMatrix mat, Coords vInOut){
		Coords v1 = vInOut.getCoordsLast1();
		vInOut.set(mat.mul(v1));		
	}
	
	/** return the matrix : screen coords -> scene coords.
	 * @return the matrix : screen coords -> scene coords.
	 */
	final public CoordMatrix4x4 getToSceneMatrix(){
		
		return mInv;
	}
	
	/** return the matrix : scene coords -> screen coords.
	 * @return the matrix : scene coords -> screen coords.
	 */
	final public CoordMatrix4x4 getToScreenMatrix(){
		
		return m;
	}	
	
	/** return the matrix undoing the rotation : scene coords -> screen coords.
	 * @return the matrix undoing the rotation : scene coords -> screen coords.
	 */
	final public CoordMatrix4x4 getUndoRotationMatrix(){
		
		return undoRotationMatrix;
	}	
	
	/**
	 * set Matrix for view3D
	 */	
	public void updateMatrix(){
		
		//TODO use Ggb3DMatrix4x4
		
		//rotations
		CoordMatrix m1 = CoordMatrix.Rotation3DMatrix(CoordMatrix.X_AXIS, (this.b-90)*EuclidianController3D.ANGLE_TO_DEGREES);
		CoordMatrix m2 = CoordMatrix.Rotation3DMatrix(CoordMatrix.Z_AXIS, (-this.a-90)*EuclidianController3D.ANGLE_TO_DEGREES);
		CoordMatrix m3 = m1.mul(m2);

		undoRotationMatrix.set(m3.inverse());

		//scaling
		CoordMatrix m4 = CoordMatrix.ScaleMatrix(new double[] {getXscale(),getYscale(),getZscale()});		
		

		//translation
		CoordMatrix m5 = CoordMatrix.TranslationMatrix(new double[] {getXZero(),getYZero(),getZZero()});
		
		m.set(m5.mul(m3.mul(m4)));	
		
		mInv.set(m.inverse());
		
		updateEye();
			
		//Application.debug("Zero = ("+getXZero()+","+getYZero()+","+getZZero()+")");
		
	}
	
	private void updateEye(){

		//update view direction
		if (projection==PROJECTION_CAV)
			viewDirection=renderer.getCavOrthoDirection().copyVector();
		else
			viewDirection = vzNeg.copyVector();
		toSceneCoords3D(viewDirection);	
		viewDirection.normalize();
		
		//update eye position
		if (projection==PROJECTION_ORTHOGRAPHIC || projection==PROJECTION_CAV)
			eyePosition=viewDirection;
		else{
			eyePosition=renderer.getPerspEye().copyVector();
			toSceneCoords3D(eyePosition);	
		}
	}
	
	/**
	 * 
	 * @return ortho direction of the eye
	 */
	public Coords getViewDirection(){
		if (projection==PROJECTION_ORTHOGRAPHIC || projection==PROJECTION_CAV)
			return viewDirection;
		else
			return viewDirectionPersp;
	}

	/**
	 * 
	 * @return eye position
	 */
	public Coords getEyePosition(){
		return eyePosition;
	}
	
	/**
	 * sets the rotation matrix
	 * @param a
	 * @param b
	 */
	public void setRotXYinDegrees(double a, double b){
		
		//Application.debug("setRotXY: "+a+","+b);
		
		this.a = a;
		this.b = b;
		
		if (this.b>EuclidianController3D.ANGLE_MAX)
			this.b=EuclidianController3D.ANGLE_MAX;
		else if (this.b<-EuclidianController3D.ANGLE_MAX)
			this.b=-EuclidianController3D.ANGLE_MAX;
		
		

		updateMatrix();

		setViewChangedByRotate();
		setWaitForUpdate();
	}
	
	
	/** Sets coord system from mouse move */
	final public void setCoordSystemFromMouseMove(int dx, int dy, int mode) {	
		switch(mode){
		case EuclidianController3D.MOVE_ROTATE_VIEW:
			setRotXYinDegrees(aOld - dx, bOld + dy);
			break;
		case EuclidianController3D.MOVE_VIEW:
			setXZero(XZeroOld+dx);
			setYZero(YZeroOld-dy);
			updateMatrix();
			setViewChangedByTranslate();
			setWaitForUpdate();
			break;
		}
	}


	

	/* TODO interaction - note : methods are called by EuclidianRenderer3D.viewOrtho() 
	 * to re-center the scene */
	public double getXZero() { return XZero; }
	public double getYZero() { return YZero; }
	/** @return the z-coord of the origin */
	public double getZZero() { return ZZero; }

	/** set the x-coord of the origin 
	 * @param val */
	public void setXZero(double val) { 
		XZero=val; 
	}
	
	/** set the y-coord of the origin 
	 * @param val */
	public void setYZero(double val) { 
		YZero=val; 
	}
	
	/** set the z-coord of the origin 
	 * @param val */
	public void setZZero(double val) { 
		ZZero=val; 
	}
	
	
	public double getXRot(){ return a;}
	public double getZRot(){ return b;}
	
	

	/**  @return min-max value for x-axis (linked to grid)*/
	public double[] getXMinMax(){ return axisDrawable[AXIS_X].getDrawMinMax(); }
	/**  @return min value for y-axis (linked to grid)*/
	public double[] getYMinMax(){ return axisDrawable[AXIS_Y].getDrawMinMax(); }
	/**  @return min value for z-axis */
	public double[] getZMinMax(){ 
		return axisDrawable[AXIS_Z].getDrawMinMax(); 
	}

	
	//TODO specific scaling for each direction
	private double scale = 100; 


	public double getXscale() { return scale; }
	public double getYscale() { return scale; }
	
	/** @return the z-scale */
	public double getZscale() { return scale; }
	
	/**
	 * set the all-axis scale
	 * @param val
	 */
	public void setScale(double val){
		scale = val;
		setViewChangedByZoom();
	}
	
	/**
	 * @return the all-axis scale
	 */
	public double getScale(){
		return scale;
	}

	
	/** remembers the origins values (xzero, ...) */
	public void rememberOrigins(){
		aOld = a;
		bOld = b;
		XZeroOld = XZero;
		YZeroOld = YZero;
	}

	
	
	
	

	
	
	//////////////////////////////////////
	// update
	
	


	/** update the drawables for 3D view */
	public void update(){
		
		if (isAnimated()){
			animate();
			setWaitForUpdate();
		}
		
		if (waitForUpdate){
			//drawList3D.updateAll();

			// I've placed remove() before add(), otherwise when the two lists
			// contains the same element, the element will NOT be added. ---Tam, 2011/7/15
			/*
			if (!drawable3DListToBeRemoved.isEmpty()){
				Application.debug("before remove:\n"+drawable3DLists.toString());
				StringBuilder sb = new StringBuilder("remove:\n");
				for (Drawable3D d: drawable3DListToBeRemoved){
					sb.append(d);
					sb.append(" -- ");
					sb.append(d.getGeoElement().getLabel());
					sb.append("\n");
				}
				Application.debug(sb.toString());
			}
			*/
			drawable3DLists.remove(drawable3DListToBeRemoved);
			/*
			if (!drawable3DListToBeRemoved.isEmpty())
				Application.debug("after remove:\n"+drawable3DLists.toString());
			 */
			drawable3DListToBeRemoved.clear();
			
			
			/*
			if (!drawable3DListToBeAdded.isEmpty()){
				Application.debug("before add:\n"+drawable3DLists.toString());	
				StringBuilder sb = new StringBuilder("add:\n");
				for (Drawable3D d: drawable3DListToBeAdded){
					sb.append(d);
					sb.append(" -- ");
					sb.append(d.getGeoElement().getLabel());
					sb.append("\n");
				}
				Application.debug(sb.toString());
			}		
			*/	
			
			//add drawables (for preview)
			drawable3DLists.add(drawable3DListToBeAdded);
			/*
			if (!drawable3DListToBeAdded.isEmpty())
				Application.debug("after add:\n"+drawable3DLists.toString());	
			 */		
			drawable3DListToBeAdded.clear();
			
			//add geos
			for (GeoElement geo : geosToBeAdded.values())
				addNow(geo);
			geosToBeAdded.clear();

			
		
			
			
			
			viewChangedOwnDrawables();
			setWaitForUpdateOwnDrawables();
			
			
			
			waitForUpdate = false;
		}


		// update decorations
		pointDecorations.update();
	}
	
	
	/** 
	 * tell the view that it has to be updated
	 * 
	 */
	public void setWaitForUpdate(){
		waitForUpdate = true;
	}
	
	
	
	
	
	
	
	
	private boolean isStarted = false;
	
	/**
	 * @return if the view has been painted at least once
	 */
	public boolean isStarted(){
		return isStarted;
	}
	
	
	public void paint(Graphics g){
		
		
		if (!isStarted){
			//Application.debug("ici");
			isStarted = true;
		}
		
		
		//update();
		//setWaitForUpdate();
		if (isFrozen)
			super.paint(g);
	}
	
	
	
	
	//////////////////////////////////////
	// toolbar and euclidianController3D
	
	/** sets EuclidianController3D mode */
	public void setMode(int mode){
		if (mode == euclidianController3D.getMode()) return;
		euclidianController3D.setMode(mode);
		getStyleBar().setMode(mode);
	}
	
	
	
	
	

	
	
	//////////////////////////////////////
	// picking
	
	private Coords pickPoint = new Coords(0,0,0,1);
	private Coords viewDirectionPersp = new Coords(4);
	
	/** (x,y) 2D screen coords -> 3D physical coords 
	 * @param x 
	 * @param y 
	 * @return 3D physical coords of the picking point */
	public Coords getPickPoint(int x, int y){			
		
		
		Dimension d = new Dimension();
		this.getSize(d);
		
		if (d!=null){
			
			pickPoint.setX(x+renderer.getLeft());
			pickPoint.setY(-y+renderer.getTop());

			if (projection==PROJECTION_PERSPECTIVE||projection==PROJECTION_ANAGLYPH){
				viewDirectionPersp = pickPoint.sub(renderer.getPerspEye());
				toSceneCoords3D(viewDirectionPersp);
				viewDirectionPersp.normalize();
			}
			
			return pickPoint.copyVector();
		}else
			return null;
		
		
	}
	
	
	/** p scene coords, (dx,dy) 2D mouse move -> 3D physical coords 
	 * @param p 
	 * @param dx 
	 * @param dy 
	 * @return 3D physical coords  */
	public Coords getPickFromScenePoint(Coords p, int dx, int dy){
		
		Coords point = getToScreenMatrix().mul(p);

		pickPoint.setX(point.get(1)+dx);
		pickPoint.setY(point.get(2)-dy);
		
		if (projection==PROJECTION_PERSPECTIVE||projection==PROJECTION_ANAGLYPH){
			viewDirectionPersp = pickPoint.sub(renderer.getPerspEye());
			toSceneCoords3D(viewDirectionPersp);
			viewDirectionPersp.normalize();
		}

		return pickPoint.copyVector();
		
	}
	

		
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * attach the view to the kernel
	 */
	public void attachView() {
		kernel3D.notifyAddAll(this);
		kernel3D.attach(this);
	}
	
	
	public void clearView() {
		drawable3DLists.clear();
		//getEuclidianController().initNewMode(getMode()); //TODO: put in a better place
		initView(false);
	}
	
	protected void initView(boolean repaint) {
		setBackground(Color.white);
	}

	/**
	 * remove a GeoElement3D from this view
	 */	
	public void remove(GeoElement geo) {
		
		//Application.printStacktrace("geo:"+geo.getLabel());

		if (geo.hasDrawable3D()){
			//Drawable3D d = ((GeoElement3DInterface) geo).getDrawable3D();
			Drawable3D d = drawable3DMap.get(geo);
			//drawable3DLists.remove(d);
			remove(d);
			
			//for GeoList : remove all 3D drawables linked to it
			if (geo.isGeoList()){
				if (d!=null)
					for (DrawableND d1 : ((DrawList3D) d).getDrawables3D()){
						if (d1.createdByDrawList())
							remove((Drawable3D) d1);
					}
			}
		}
		
		drawable3DMap.remove(geo);
	}
	
	/**
	 * remove the drawable d
	 * @param d
	 */
	public void remove(Drawable3D d) {
		setWaitForUpdate();
		drawable3DListToBeRemoved.add(d);
		
	}


	public void rename(GeoElement geo) {
		// TODO Raccord de m??thode auto-g??n??r??

		
	}

	public void repaintView() {
		
		//reset();
		
		//update();
		//setWaitForUpdate();
		
		//Application.debug("repaint View3D");
		
	}

	public void reset() {
		
		//Application.debug("reset View3D");
		resetAllDrawables();
		//updateAllDrawables();
		viewChangedOwnDrawables();
		setViewChanged();
		setWaitForUpdate();
		
		//update();
		
	}

	public void update(GeoElement geo) {
		if (geo.hasDrawable3D()){
			Drawable3D d = drawable3DMap.get(geo);
				//((GeoElement3DInterface) geo).getDrawable3D();
			if (d!=null){
				update(d);
				//update(((GeoElement3DInterface) geo).getDrawable3D());
			}
		}
	}
	
	public void updateVisualStyle(GeoElement geo) {
		//Application.debug(geo);
		if (geo.hasDrawable3D()){
			Drawable3D d = drawable3DMap.get(geo);
			if (d!=null){
				d.setWaitForUpdateColor();
			}
		}
	}
	
	private void updateAllDrawables(){
		for (Drawable3D d:drawable3DMap.values())
			update(d);
		setWaitForUpdateOwnDrawables();
		
	}
	
	/**
	 * says this drawable to be updated
	 * @param d
	 */
	public void update(Drawable3D d){
		d.setWaitForUpdate();
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Raccord de m??thode auto-g??n??r??
		
	}

	public int print(Graphics arg0, PageFormat arg1, int arg2) throws PrinterException {
		// TODO Raccord de m??thode auto-g??n??r??
		return 0;
	}













	//////////////////////////////////////////////
	// EuclidianViewInterface





	public Drawable getDrawableFor(GeoElement geo) {
		// TODO Auto-generated method stub
		return null;
	}

	public DrawableND getDrawableND(GeoElement geo) {
		if (geo.hasDrawable3D()){

			return drawable3DMap.get(geo);
			//return ((GeoElement3DInterface) geo).getDrawable3D();
		}
		
		return null;
	}










	public double getGridDistances(int i) {
		// TODO Auto-generated method stub
		return 0;
	}














	public int getGridType() {
		// TODO Auto-generated method stub
		return 0;
	}














	public Hits getHits() {
		//return hits;
		return hits.clone();
	}














	public double getInvXscale() {
		// TODO Auto-generated method stub
		return 0;
	}














	public double getInvYscale() {
		// TODO Auto-generated method stub
		return 0;
	}














	public GeoElement getLabelHit(Point p) {
		
		//Application.debug("getLabelHit");

		//sets the flag and mouse location for openGL picking
		//renderer.setMouseLoc(p.x,p.y,EuclidianRenderer3D.PICKING_MODE_LABELS);

		//calc immediately the hits
		//renderer.display();
		
		
		//Application.debug("end-getLabelHit");			

		//return null;
		return hits.getLabelHit();
	}

























	public Previewable getPreviewDrawable() {
		
		return previewDrawable;
	}














	public Rectangle getSelectionRectangle() {
		return selectionRectangle;
	}













	public boolean getShowMouseCoords() {
		// TODO Auto-generated method stub
		return false;
	}














	public boolean getShowXaxis() {
		return axis[AXIS_X].isEuclidianVisible();
	}














	public boolean getShowYaxis() {
		return axis[AXIS_Y].isEuclidianVisible();
	}




	public void setShowAxis(int axis, boolean flag, boolean update){
		this.axis[axis].setEuclidianVisible(flag);
	}


	public void setShowAxes(boolean flag, boolean update){
		setShowAxis(AXIS_X, flag, false);
		setShowAxis(AXIS_Y, flag, false);
		setShowAxis(AXIS_Z, flag, true);
	}

	
	
	/** sets the visibility of xOy plane
	 * @param flag
	 */
	public void setShowPlane(boolean flag){
		getxOyPlane().setEuclidianVisible(flag);
	}
	
	
	/** sets the visibility of xOy plane plate
	 * @param flag
	 */
	public void setShowPlate(boolean flag){
		getxOyPlane().setPlateVisible(flag);
	}

	/** sets the visibility of xOy plane grid
	 * @param flag
	 */
	public void setShowGrid(boolean flag){
		getxOyPlane().setGridVisible(flag);
	}
	
	


	public int getViewHeight() {
		return getHeight();
	}


	public int getViewWidth() {
		return getWidth();
	}







	



	public boolean hitAnimationButton(MouseEvent e) {
		// TODO Auto-generated method stub
		return false;
	}














	public boolean isGridOrAxesShown() {
		// TODO Auto-generated method stub
		return false;
	}














	public void repaintEuclidianView() {
		//Application.debug("repaintEuclidianView");
		
	}














	public void resetMode() {
		// TODO Auto-generated method stub
		
	}











	//////////////////////////////////////////////////
	// ANIMATION
	//////////////////////////////////////////////////


	/** tells if the view is under animation */
	private boolean isAnimated(){
		return animatedScale || isRotAnimated();
	}
	
	/** tells if the view is under rot animation 
	 * @return true if there is a rotation animation*/
	public boolean isRotAnimated(){
		return  animatedContinueRot || animatedRot;
	}
	
	/** 
	 * @return true if there is a continue rotation animation*/
	public boolean isRotAnimatedContinue(){
		return  animatedContinueRot;
	}
	
	public void setAnimatedCoordSystem(double ox, double oy, double f, double newScale,
			int steps, boolean storeUndo) {
		
		animatedScaleStartX=getXZero();
		animatedScaleStartY=getYZero();
		double centerX = ox+renderer.getLeft();
		double centerY = -oy+renderer.getTop();
		animatedScaleEndX=centerX+(animatedScaleStartX-centerX)*f;
		animatedScaleEndY=centerY+(animatedScaleStartY-centerY)*f;
		
		//Application.debug("mouse = ("+ox+","+oy+")"+"\nscale end = ("+animatedScaleEndX+","+animatedScaleEndY+")"+"\nZero = ("+animatedScaleStartX+","+animatedScaleStartY+")");
		
		animatedScaleStart = getScale();
		animatedScaleTimeStart = System.currentTimeMillis();
		animatedScaleEnd = newScale;
		animatedScale = true;
		
		animatedScaleTimeFactor = 0.005; //it will take about 1/2s to achieve it
		
		//this.storeUndo = storeUndo;

		
	}
	
	
	/** sets a continued animation for rotation
	 * if delay is too long, no animation
	 * if speed is too small, no animation
	 * @param delay delay since last drag
	 * @param rotSpeed speed of rotation
	 */
	public void setRotContinueAnimation(long delay, double rotSpeed){
		//Application.debug("delay="+delay+", rotSpeed="+rotSpeed);

		//if last drag occured more than 200ms ago, then no animation
		if (delay>200)
			return;
		
		//if speed is too small, no animation
		if (Math.abs(rotSpeed)<0.01){
			stopRotAnimation();
			return;
		}
		
		//if speed is too large, use max speed
		if (rotSpeed>0.1)
			rotSpeed=0.1;
		else if (rotSpeed<-0.1)
			rotSpeed=-0.1;
			
		
			
		animatedContinueRot = true;
		animatedRot = false;
		animatedRotSpeed = -rotSpeed;
		animatedRotTimeStart = System.currentTimeMillis() - delay;
		bOld = b;
		aOld = a;
	}
	
	
	/**
	 * start a rotation animation to be in the vector direction
	 * @param vn
	 */
	public void setRotAnimation(Coords vn){
		Coords spheric = CoordMatrixUtil.sphericalCoords(vn);		
		setRotAnimation(spheric.get(2)*180/Math.PI,spheric.get(3)*180/Math.PI,true);
	}
		

	/**
	 * start a rotation animation to go to the new values
	 * @param aN
	 * @param bN
	 * @param checkSameValues if true, check new values are same than old, 
	 * in this case revert the view
	 */
	public void setRotAnimation(double aN, double bN, boolean checkSameValues){


		//app.storeUndoInfo();
		
		animatedRot = true;
		animatedContinueRot = false;
		aOld = this.a % 360;
		bOld = this.b % 360;
		
		aNew = aN;
		bNew = bN;
		
		
		//if (aNew,bNew)=(0??,90??), then change it to (90??,90??) to have correct xOy orientation
		if (Kernel.isEqual(aNew, 0, Kernel.STANDARD_PRECISION) &&
				Kernel.isEqual(Math.abs(bNew), 90, Kernel.STANDARD_PRECISION))
			aNew=-90;
		
		
		//looking for the smallest path
		if (aOld-aNew>180)
			aOld-=360;
		else if (aOld-aNew<-180)
			aOld+=360;
			

		else if (checkSameValues) 
			if (Kernel.isEqual(aOld, aNew, Kernel.STANDARD_PRECISION))
				if (Kernel.isEqual(bOld, bNew, Kernel.STANDARD_PRECISION)){
					if (!Kernel.isEqual(Math.abs(bNew), 90, Kernel.STANDARD_PRECISION))
						aNew+=180;
					bNew*=-1;
					//Application.debug("ici");
				}
		if (bOld>180)
			bOld-=360;

		animatedRotTimeStart = System.currentTimeMillis();
		
	}
	
	
	/**
	 * stops the rotation animation
	 */
	public void stopRotAnimation(){
		animatedContinueRot = false;
		animatedRot = false;
		
		
	}


	/** animate the view for changing scale, orientation, etc. */
	private void animate(){
		if (animatedScale){
			double t = (System.currentTimeMillis()-animatedScaleTimeStart)*animatedScaleTimeFactor;
			t+=0.2; //starting at 1/4
			
			if (t>=1){
				t=1;
				animatedScale = false;
			}
			
			//Application.debug("t="+t+"\nscale="+(startScale*(1-t)+endScale*t));
			
			setScale(animatedScaleStart*(1-t)+animatedScaleEnd*t);
			setXZero(animatedScaleStartX*(1-t)+animatedScaleEndX*t);
			setYZero(animatedScaleStartY*(1-t)+animatedScaleEndY*t);
			updateMatrix();
			
		}
		
		if (animatedContinueRot){
			double da = (System.currentTimeMillis()-animatedRotTimeStart)*animatedRotSpeed;			
			setRotXYinDegrees(aOld + da, bOld);
		}
		
		if (animatedRot){
			double t = (System.currentTimeMillis()-animatedRotTimeStart)*0.001;
			t*=t;
			//t+=0.2; //starting at 1/4
			
			if (t>=1){
				t=1;
				animatedRot = false;
			}
			
			setRotXYinDegrees(aOld*(1-t)+aNew*t, bOld*(1-t)+bNew*t);
		}

			
		
		
	}










	public void setAnimatedRealWorldCoordSystem(double xmin, double xmax,
			double ymin, double ymax, int steps, boolean storeUndo) {
		// TODO Auto-generated method stub
		
	}














	public boolean setAnimationButtonsHighlighted(boolean hitAnimationButton) {
		// TODO Auto-generated method stub
		return false;
	}














	public void setCoordSystem(double x, double y, double xscale, double yscale) {
		// TODO Auto-generated method stub
		
	}















































	/*
	Point pOld = null;


	public void setHits(Point p) {
		
		
		
		if (p.equals(pOld)){
			//Application.printStacktrace("");
			return;
		}
		
		
		pOld = p;
		
		//sets the flag and mouse location for openGL picking
		renderer.setMouseLoc(p.x,p.y,Renderer.PICKING_MODE_LABELS);

		//calc immediately the hits
		renderer.display();
		

	}
	
	*/
	
	// empty method : setHits3D() used instead
	public void setHits(Point p) {
		
	}
	
	
	/** sets the 3D hits regarding point location
	 * @param p point location
	 */
	public void setHits3D(Point p) {
		
		//sets the flag and mouse location for openGL picking
		renderer.setMouseLoc(p.x,p.y,Renderer.PICKING_MODE_LABELS);

		//calc immediately the hits
		//renderer.display();
		

	}
	



	/** add a drawable to the current hits
	 * (used when a new object is created)
	 * @param d drawable to add
	 */
	public void addToHits3D(Drawable3D d){
		hits.addDrawable3D(d, false);
		hits.sort();
	}
	
	/** add the drawable of the geo to the current hits
	 * (used when a new object is created)
	 * @param geo
	 */
	public void addToHits3D(GeoElement geo){
		

		DrawableND d = getDrawableND(geo);
		
		
		if (d!=null) //add it immediately
			addToHits3D((Drawable3D) d);
		else //wait for drawable created
			geosToAddToHits.add(geo);
		
	}	

	
	/** init the hits for this view
	 * @param hits
	 */
	public void setHits(Hits3D hits){
		this.hits = hits;
	}



	public Hits3D getHits3D(){
		return hits;
	}





	public void setHits(Rectangle rect) {
		// TODO Auto-generated method stub
		
	}




































	public void setSelectionRectangle(Rectangle selectionRectangle) {
		// TODO Auto-generated method stub
		
	}














	public void setShowAxesRatio(boolean b) {
		// TODO Auto-generated method stub
		
	}














	public void setShowMouseCoords(boolean b) {
		// TODO Auto-generated method stub
		
	}
























	public double toRealWorldCoordX(double minX) {
		// TODO Auto-generated method stub
		return 0;
	}














	public double toRealWorldCoordY(double maxY) {
		// TODO Auto-generated method stub
		return 0;
	}














	public void updateSize() {
		// TODO Auto-generated method stub
		
	}














	public void zoom(double px, double py, double zoomFactor, int steps,
			boolean storeUndo) {
						
		setScale(getXscale()*zoomFactor);
		updateMatrix();
		setWaitForUpdate();
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/////////////////////////////////////////
	// previewables
	
	

	
	/** return the point used for 3D cursor
	 * @return the point used for 3D cursor
	 */
	public GeoPoint3D getCursor3D(){
		return cursor3D;
	}
	
	

	
	
	/**
	 * sets the type of the cursor
	 * @param v
	 */
	public void setCursor3DType(int v){
		cursor3DType = v;
		//Application.debug(""+v);
	}
	

	/**
	 * @return the type of the cursor
	 */
	public int getCursor3DType(){
		return cursor3DType;
	}
	
	
	
	/** sets that the current 3D cursor is at the intersection of the two GeoElement parameters
	 * @param cursor3DIntersectionOf1 first GeoElement of intersection
	 * @param cursor3DIntersectionOf2 second GeoElement of intersection
	 */
	public void setCursor3DIntersectionOf(GeoElement cursor3DIntersectionOf1, GeoElement cursor3DIntersectionOf2){
		this.cursor3DIntersectionOf[0]=cursor3DIntersectionOf1;
		this.cursor3DIntersectionOf[1]=cursor3DIntersectionOf2;
	}
	
	/** return the i-th GeoElement of intersection
	 * @param i number of GeoElement of intersection
	 * @return GeoElement of intersection
	 */
	public GeoElement getCursor3DIntersectionOf(int i){
		return cursor3DIntersectionOf[i];
	}
	
	
	
	/**
	 * @return the list of 3D drawables
	 */
	public Drawable3DLists getDrawList3D(){
		return drawable3DLists;
	}
	
	
	@SuppressWarnings("rawtypes")
	public Previewable createPreviewLine(ArrayList selectedPoints){

		previewDrawLine3D = new DrawLine3D(this, selectedPoints);
		return previewDrawLine3D;
		
	}

	public Previewable createPreviewLine(){
		if (previewDrawLine3D==null) {
			previewLine = new GeoLine3D(getKernel().getConstruction());
			previewLine.setObjColor(Color.YELLOW);
			previewLine.setIsPickable(false);
			previewDrawLine3D = new DrawLine3D(this, previewLine);
		}
		return previewDrawLine3D;
		
	}
	

	
	public Previewable createPreviewConic(){
		if (previewDrawConic3D==null) {
			previewConic = new GeoConic3D(getKernel().getConstruction());
			previewConic.setObjColor(Color.YELLOW);
			previewConic.setIsPickable(false);
			previewDrawConic3D = new DrawConic3D(this, previewConic);
		}
		return previewDrawConic3D;
		
	}
	
	@SuppressWarnings("rawtypes")
	public Previewable createPreviewSegment(ArrayList selectedPoints){
		return new DrawSegment3D(this, selectedPoints);
	}	
	
	@SuppressWarnings("rawtypes")
	public Previewable createPreviewRay(ArrayList selectedPoints){
		return new DrawRay3D(this, selectedPoints);
	}	
	

	@SuppressWarnings("rawtypes")
	public Previewable createPreviewVector(ArrayList selectedPoints){
		return new DrawVector3D(this, selectedPoints);
	}
	
	@SuppressWarnings("rawtypes")
	public Previewable createPreviewPolygon(ArrayList selectedPoints){
		return new DrawPolygon3D(this, selectedPoints);
	}	
	
	public Previewable createPreviewConic(int mode, ArrayList selectedPoints){
		return null;
	}

	/**
	 * @param selectedPoints
	 * @return a preview sphere (center-point)
	 */
	@SuppressWarnings("rawtypes")
	public Previewable createPreviewSphere(ArrayList selectedPoints){
		return new DrawQuadric3D(this, selectedPoints, GeoQuadricND.QUADRIC_SPHERE);
	}	

	/**
	 * @param selectedPolygon
	 * @return a preview right prism (basis and height)
	 */
	@SuppressWarnings("rawtypes")
	public Previewable createPreviewRightPrism(ArrayList selectedPolygons){
		return new DrawPolyhedron3D(this, selectedPolygons);
	}	
	
	
	public void updatePreviewable(){

		getPreviewDrawable().updatePreview();
	}
	
	
	/**
	 * update the 3D cursor with current hits
	 * @param hits 
	 */
	public void updateCursor3D(Hits hits){
		if (hasMouse){
			getEuclidianController().updateNewPoint(true, 
				hits, 
				true, true, true, false, //TODO doSingleHighlighting = false ? 
				false, false);
			

			
			updateMatrixForCursor3D();
		}
		
	}
	
	
	/**
	 * update the 3D cursor with current hits
	 */
	public void updateCursor3D(){
		//updateCursor3D(getHits().getTopHits()); 
		
	
		//we also want to see different pick orders in preview, e.g. line/plane intersection
		//For now we follow the practice of EView2D: we reserve only points if there are any, 
		//and return the clone if there are no points.
		//TODO: define this behavior better
		if (getHits().containsGeoPoint())
			updateCursor3D(getHits().getTopHits());
		else
			updateCursor3D(getHits());
		
	}

	/**
	 * update cursor3D matrix
	 */
	public void updateMatrixForCursor3D(){		
		double t;

		CoordMatrix4x4 matrix;
		CoordMatrix4x4 m2;
		Coords v;
		CoordMatrix m;
		if (getEuclidianController().getMode()==EuclidianConstants.MODE_VIEW_IN_FRONT_OF){

			switch(getCursor3DType()){

			case PREVIEW_POINT_REGION:
				// use region drawing directions for the arrow
				t = 1/getScale();
				v = getCursor3D().getMoveNormalDirection();		
				if (v.dotproduct(getViewDirection())>0)
					v=v.mul(-1);

				matrix = new CoordMatrix4x4(getCursor3D().getDrawingMatrix().getOrigin(),v,CoordMatrix4x4.VZ);
				matrix.mulAllButOrigin(t);
				getCursor3D().setDrawingMatrix(matrix);
				
				break;
			case PREVIEW_POINT_PATH:
				// use path drawing directions for the arrow
				t = 1/getScale();
				v = ((GeoElement)getCursor3D().getPath()).getMainDirection().normalized();
				if (v.dotproduct(getViewDirection())>0)
					v=v.mul(-1);
				
				matrix = new CoordMatrix4x4(getCursor3D().getDrawingMatrix().getOrigin(),v,CoordMatrix4x4.VZ);
				matrix.mulAllButOrigin(t);
				getCursor3D().setDrawingMatrix(matrix);

				break;
		

			}
		}else
			switch(getCursor3DType()){

			case PREVIEW_POINT_FREE:
				// use default directions for the cross
				t = 1/getScale();
				getCursor3D().getDrawingMatrix().setVx((Coords) vx.mul(t));
				getCursor3D().getDrawingMatrix().setVy((Coords) vy.mul(t));
				getCursor3D().getDrawingMatrix().setVz((Coords) vz.mul(t));
				break;
			case PREVIEW_POINT_REGION:
				
				
				// use region drawing directions for the cross
				t = 1/getScale();

				v = getCursor3D().getMoveNormalDirection();	
				
				matrix = new CoordMatrix4x4(getCursor3D().getDrawingMatrix().getOrigin(),v,CoordMatrix4x4.VZ);
				matrix.mulAllButOrigin(t);
				getCursor3D().setDrawingMatrix(matrix);

				break;
			case PREVIEW_POINT_PATH:
				// use path drawing directions for the cross
				t = 1/getScale();

				v = ((GeoElement)getCursor3D().getPath()).getMainDirection();
				m = new CoordMatrix(4, 2);
				m.set(v, 1);
				m.set(4, 2, 1);
				matrix = new CoordMatrix4x4(m);


				getCursor3D().getDrawingMatrix().setVx(
						(Coords) matrix.getVx().normalized().mul(t));
				t *= (10+((GeoElement) getCursor3D().getPath()).getLineThickness());
				getCursor3D().getDrawingMatrix().setVy(
						(Coords) matrix.getVy().mul(t));
				getCursor3D().getDrawingMatrix().setVz(
						(Coords) matrix.getVz().mul(t));


				break;
			case PREVIEW_POINT_DEPENDENT:
				//use size of intersection
				int t1 = getCursor3DIntersectionOf(0).getLineThickness();
				int t2 = getCursor3DIntersectionOf(1).getLineThickness();
				if (t1>t2)
					t2=t1;
				t = (t2+6)/getScale();
				getCursor3D().getDrawingMatrix().setVx((Coords) vx.mul(t));
				getCursor3D().getDrawingMatrix().setVy((Coords) vy.mul(t));
				getCursor3D().getDrawingMatrix().setVz((Coords) vz.mul(t));
				break;			
			case PREVIEW_POINT_ALREADY:
				//use size of point
				t = 1/getScale();//(getCursor3D().getPointSize()/6+2)/getScale();

				if (getCursor3D().hasPath()){
					v = ((GeoElement)getCursor3D().getPath()).getMainDirection();
					m = new CoordMatrix(4, 2);
					m.set(v, 1);
					m.set(4, 2, 1);
					m2 = new CoordMatrix4x4(m);

					matrix = new CoordMatrix4x4();
					matrix.setVx(m2.getVy());
					matrix.setVy(m2.getVz());
					matrix.setVz(m2.getVx());
					matrix.setOrigin(m2.getOrigin());


				}else if (getCursor3D().hasRegion()){
					
					v = getCursor3D().getMoveNormalDirection();	

					matrix = new CoordMatrix4x4(getCursor3D().getCoordsInD(3), v, CoordMatrix4x4.VZ);
				}else
					matrix = CoordMatrix4x4.Identity();

				getCursor3D().getDrawingMatrix().setVx(
						(Coords) matrix.getVx().normalized().mul(t));
				getCursor3D().getDrawingMatrix().setVy(
						(Coords) matrix.getVy().mul(t));
				getCursor3D().getDrawingMatrix().setVz(
						(Coords) matrix.getVz().mul(t));
				break;
			}





		//Application.debug("getCursor3DType()="+getCursor3DType());

		
	}
	



	public void setPreview(Previewable previewDrawable) {
		
		if (this.previewDrawable!=null)
			this.previewDrawable.disposePreview();
		
		if (previewDrawable!=null){
			if (((Drawable3D) previewDrawable).getGeoElement()!=null)
				addToDrawable3DLists((Drawable3D) previewDrawable);
			//drawable3DLists.add((Drawable3D) previewDrawable);
		}
		
		//Application.debug("drawList3D :\n"+drawList3D);
			
		
			
		//setCursor3DType(PREVIEW_POINT_NONE);
		
		this.previewDrawable = previewDrawable;
		
		
		
	}
	

	
	
	
	
	
	
	
	/////////////////////////////////////////////////////
	// 
    // POINT DECORATION 
	//
	/////////////////////////////////////////////////////
	
	private void initPointDecorations(){
		//Application.debug("hop");
		pointDecorations = new DrawPointDecorations(this);
	}
	
	
	/** update decorations for localizing point in the space
	 *  if point==null, no decoration will be drawn
	 * @param point
	 */
	public void updatePointDecorations(GeoPoint3D point){
		
		if (point==null)
			decorationVisible = false;
		else{
			decorationVisible = true;
			pointDecorations.setPoint(point);
		}
		
		//Application.debug("point :\n"+point.getDrawingMatrix()+"\ndecorations :\n"+decorationMatrix);
		
		
	}
	
	

	

	/////////////////////////////////////////////////////
	// 
	// CURSOR
	//
	/////////////////////////////////////////////////////
	
	
	
	/** 
	 * draws the cursor
	 * @param renderer
	 */
	public void drawCursor(Renderer renderer){

		
		//Application.debug("hasMouse="+hasMouse+"\n!getEuclidianController().mouseIsOverLabel() "+!getEuclidianController().mouseIsOverLabel() +"\ngetEuclidianController().cursor3DVisibleForCurrentMode(getCursor3DType())" + getEuclidianController().cursor3DVisibleForCurrentMode(getCursor3DType())+"\ncursor="+cursor+"\ngetCursor3DType()="+getCursor3DType());		
		
		if (hasMouse 
				&& !getEuclidianController().mouseIsOverLabel() 
				&& getEuclidianController().cursor3DVisibleForCurrentMode(getCursor3DType())
		){
			renderer.setMatrix(getCursor3D().getDrawingMatrix());
			
			switch(cursor){
			case CURSOR_DEFAULT:
				switch(getCursor3DType()){
				case PREVIEW_POINT_FREE: //free point on xOy plane
					renderer.drawCursor(PlotterCursor.TYPE_CROSS2D);					
					break;
				case PREVIEW_POINT_ALREADY: //showing arrows directions
					drawPointAlready();				
					break;				
				}
				break;
				/*
			case CURSOR_DRAG:
				if(getCursor3DType()==PREVIEW_POINT_ALREADY)
					drawPointAlready();
				break;
				*/
			case CURSOR_HIT:									
				switch(getCursor3DType()){
				case PREVIEW_POINT_FREE:
					renderer.drawCursor(PlotterCursor.TYPE_CROSS2D);
					break;
				case PREVIEW_POINT_REGION:
					if (getEuclidianController().getMode()==EuclidianConstants.MODE_VIEW_IN_FRONT_OF)
						renderer.drawViewInFrontOf();
					else
						renderer.drawCursor(PlotterCursor.TYPE_CROSS2D);
					break;
				case PREVIEW_POINT_PATH:
					if (getEuclidianController().getMode()==EuclidianConstants.MODE_VIEW_IN_FRONT_OF)
						renderer.drawViewInFrontOf();
					else
						renderer.drawCursor(PlotterCursor.TYPE_CYLINDER);
					break;
				case PREVIEW_POINT_DEPENDENT:
					renderer.drawCursor(PlotterCursor.TYPE_DIAMOND);
					break;

				case PREVIEW_POINT_ALREADY:
					drawPointAlready();
					break;
				}
				break;
			}
		
		}
	}
	
	
	private void drawPointAlready(){
		
		//Application.debug(getCursor3D().getMoveMode());
		
		switch (getCursor3D().getMoveMode()){
		case GeoPointND.MOVE_MODE_XY:
			renderer.drawCursor(PlotterCursor.TYPE_ALREADY_XY);
			break;
		case GeoPointND.MOVE_MODE_Z:
			renderer.drawCursor(PlotterCursor.TYPE_ALREADY_Z);
			break;
		}
	}
	

	
	
	public void setMoveCursor(){
		
		// 3D cursor
		cursor = CURSOR_MOVE;
		
	}
	
	private boolean defaultCursorWillBeHitCursor = false;
	
	/**
	 * next call to setDefaultCursor() will call setHitCursor() instead
	 */
	public void setDefaultCursorWillBeHitCursor(){
		defaultCursorWillBeHitCursor = true;
	}
	
	public void setDragCursor(){

		// 2D cursor is invisible
		//setCursor(app.getTransparentCursor());

		// 3D cursor
		cursor = CURSOR_DRAG;
		//Application.printStacktrace("setDragCursor");
		
	}
	
	public void setDefaultCursor(){
		//Application.printStacktrace("setDefaultCursor:"+defaultCursorWillBeHitCursor);
		
		if (defaultCursorWillBeHitCursor){
			defaultCursorWillBeHitCursor=false;
			setHitCursor();
			return;
		}
		
		// 2D cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		// 3D cursor
		cursor = CURSOR_DEFAULT;
	}
	
	public void setHitCursor(){
		//Application.printStacktrace("setHitCursor");
		cursor = CURSOR_HIT;
	}
	
	
	
	
	public void mouseEntered(){
		//Application.debug("mouseEntered");
		hasMouse = true;
	}
	
	public void mouseExited(){
		//Application.debug("mouseExited");
		hasMouse = false;
	}
	
	public boolean hasMouse(){
		return hasMouse;
	}
	


	/**
	 * returns settings in XML format, read by xml handlers
	 * @see geogebra.io.MyXMLHandler
	 * @see geogebra3D.io.MyXMLHandler3D
	 * @return the XML description of 3D view settings
	 */
	public String getXML() {
		
		//Application.debug("getXML: "+a+","+b);
		
		//if (true)	return "";
		
		StringBuilder sb = new StringBuilder();
		sb.append("<euclidianView3D>\n");
		
		
		// coord system
		sb.append("\t<coordSystem");
		
		sb.append(" xZero=\"");
		sb.append(getXZero());
		sb.append("\"");
		sb.append(" yZero=\"");
		sb.append(getYZero());
		sb.append("\"");
		sb.append(" zZero=\"");
		sb.append(getZZero());
		sb.append("\"");	
		
		sb.append(" scale=\"");
		sb.append(getXscale());
		sb.append("\"");

		sb.append(" xAngle=\"");
		sb.append(b);
		sb.append("\"");
		sb.append(" zAngle=\"");
		sb.append(a);
		sb.append("\"");	
		
		sb.append("/>\n");
		
		
		
		
		// axis settings
		for (int i = 0; i < 3; i++) {
			sb.append("\t<axis id=\"");
			sb.append(i);
			sb.append("\" show=\"");
			sb.append(axis[i].isEuclidianVisible());			
			sb.append("\" label=\"");
			sb.append(axis[i].getAxisLabel());
			sb.append("\" unitLabel=\"");
			sb.append(axis[i].getUnitLabel());
			sb.append("\" tickStyle=\"");
			sb.append(axis[i].getTickStyle());
			sb.append("\" showNumbers=\"");
			sb.append(axis[i].getShowNumbers());

			// the tick distance should only be saved if
			// it isn't calculated automatically
			/*
			if (!automaticAxesNumberingDistances[i]) {
				sb.append("\" tickDistance=\"");
				sb.append(axesNumberingDistances[i]);
			}
			*/

			sb.append("\"/>\n");
		}
		
		
		// xOy plane settings
		sb.append("\t<plate show=\"");
		sb.append(getxOyPlane().isPlateVisible());		
		sb.append("\"/>\n");

		sb.append("\t<grid show=\"");
		sb.append(getxOyPlane().isGridVisible());		
		sb.append("\"/>\n");
		
		
		// background color
		sb.append("\t<bgColor r=\"");
		sb.append(bgColor.getRed());
		sb.append("\" g=\"");
		sb.append(bgColor.getGreen());
		sb.append("\" b=\"");
		sb.append(bgColor.getBlue());
		sb.append("\"/>\n");
		
		
		
		sb.append("</euclidianView3D>\n");
		return sb.toString();
	}
	
	
	
	/////////////////////////////////////////////////////
	// 
	// EUCLIDIANVIEW DRAWABLES (AXIS AND PLANE)
	//
	/////////////////////////////////////////////////////
	
	
	/**
	 * toggle the visibility of axes 
	 */
	public void toggleAxis(){
		
		boolean flag = axesAreAllVisible();
		
		for(int i=0;i<3;i++)
			axis[i].setEuclidianVisible(!flag);
		
	}
	

	/** says if all axes are visible
	 * @return true if all axes are visible
	 */
	public boolean axesAreAllVisible(){
		boolean flag = true;

		for(int i=0;i<3;i++)
			flag = (flag && axis[i].isEuclidianVisible());

		return flag;
	}
	
	
	/**
	 * toggle the visibility of xOy plane
	 */
	public void togglePlane(){
		
		boolean flag = xOyPlane.isPlateVisible();
		xOyPlane.setPlateVisible(!flag);
		
	}	
	
	/**
	 * toggle the visibility of xOy grid
	 */
	public void toggleGrid(){
		
		boolean flag = xOyPlane.isGridVisible();
		xOyPlane.setGridVisible(!flag);
		
	}
	
	
	/**
	 * @return the xOy plane
	 */
	public GeoPlane3D getxOyPlane()  {

		return xOyPlane;
		
	}
	
	
	/**
	 * says if this geo is owned by the view (xOy plane, ...)
	 * @param geo
	 * @return if this geo is owned by the view (xOy plane, ...)
	 */
	public boolean owns(GeoElement geo){
		
		boolean ret = (geo == xOyPlane);
		
		for(int i=0;(!ret)&&(i<3);i++)
			ret = (geo == axis[i]);
		
		return ret;
		
	}
	
	
	
	
	
	
	/** draw transparent parts of view's drawables (xOy plane)
	 * @param renderer
	 */
	public void drawTransp(Renderer renderer){
		
		
		if (xOyPlane.isPlateVisible())
			xOyPlaneDrawable.drawTransp(renderer);
				
	}
	
	
	/** draw hiding parts of view's drawables (xOy plane)
	 * @param renderer
	 */
	public void drawHiding(Renderer renderer){
		
		
		if (xOyPlane.isPlateVisible())
			xOyPlaneDrawable.drawHiding(renderer);
				
				
		
	}
	
	/** draw not hidden parts of view's drawables (axis)
	 * @param renderer
	 */
	public void draw(Renderer renderer){
		for(int i=0;i<3;i++)
			axisDrawable[i].drawOutline(renderer);
		
		/*
		if (decorationVisible)
			pointDecorations.drawOutline(renderer);
			*/
	}
	
	/** draw hidden parts of view's drawables (axis)
	 * @param renderer
	 */
	public void drawHidden(Renderer renderer){
		for(int i=0;i<3;i++)
			axisDrawable[i].drawHidden(renderer);
		
		xOyPlaneDrawable.drawHidden(renderer);
		
		
		if (decorationVisible)
			pointDecorations.drawHidden(renderer);
			
		
	}
	
	
	/** draw for picking view's drawables (plane and axis)
	 * @param renderer
	 */
	public void drawForPicking(Renderer renderer){
		renderer.pick(xOyPlaneDrawable);
		for(int i=0;i<3;i++)
			renderer.pick(axisDrawable[i]);
	}
	
	
	
	/** draw ticks on axis
	 * @param renderer
	 */
	public void drawLabel(Renderer renderer){
		
		for(int i=0;i<3;i++)
			axisDrawable[i].drawLabel(renderer);
		

	}
	
	
	
	
	
	/**
	 * says all drawables owned by the view that the view has changed
	 */
	/*
	public void viewChangedOwnDrawables(){
		
		//xOyPlaneDrawable.viewChanged();
		xOyPlaneDrawable.setWaitForUpdate();
		
		for(int i=0;i<3;i++)
			axisDrawable[i].viewChanged();
		
		
	}
	*/
	
	/**
	 * tell all drawables owned by the view to be udpated
	 */
	public void setWaitForUpdateOwnDrawables(){
		
		xOyPlaneDrawable.setWaitForUpdate();
		
		for(int i=0;i<3;i++)
			axisDrawable[i].setWaitForUpdate();
		
		
	}
	
	/**
	 * says all labels owned by the view that the view has changed
	 */
	public void resetOwnDrawables(){
		
		xOyPlaneDrawable.setWaitForReset();
		
		for(int i=0;i<3;i++){
			axisDrawable[i].setWaitForReset();
		}
				
		pointDecorations.setWaitForReset();
	}
	

	
	/**
	 * says all labels to be recomputed
	 */
	public void resetAllDrawables(){
		
		resetOwnDrawables();
		drawable3DLists.resetAllDrawables();
		
	}
	
	/**
	 * reset all drawables colors
	 */
	public void resetAllColors(){
		
		// own drawables
		xOyPlaneDrawable.setWaitForUpdateColor();
		
		for(int i=0;i<3;i++){
			axisDrawable[i].setWaitForUpdateColor();
		}
				
		pointDecorations.setWaitForUpdateColor();
		
		// other drawables
		drawable3DLists.resetAllColors();
		
	}
	
	private void viewChangedOwnDrawables(){
		
		// calc draw min/max for x and y axis
		for(int i=0;i<2;i++){
			axisDrawable[i].updateDrawMinMax();
		}
		
		//for z axis, use bottom to top min/max
		double zmin = (renderer.getBottom()-getYZero())/getScale();
		double zmax = (renderer.getTop()-getYZero())/getScale();
		axisDrawable[AXIS_Z].setDrawMinMax(zmin, zmax);
		
		//update decorations and wait for update
		for(int i=0;i<3;i++){
			axisDrawable[i].updateDecorations();
			axisDrawable[i].setWaitForUpdate();
		}
		/*
		// sets min/max for the plane and axis
		double xmin = axisDrawable[AXIS_X].getDrawMin(); 
		double ymin = axisDrawable[AXIS_Y].getDrawMin();
		double xmax = axisDrawable[AXIS_X].getDrawMax(); 
		double ymax = axisDrawable[AXIS_Y].getDrawMax();
		
		// update xOyPlane
		xOyPlane.setGridCorners(xmin,ymin,xmax,ymax);
		xOyPlane.setGridDistances(axis[AXIS_X].getNumbersDistance(), axis[AXIS_Y].getNumbersDistance());

		 */
	}
	
	
	/**
	 * update all drawables now
	 */
	public void updateOwnDrawablesNow(){
		
		for(int i=0;i<3;i++){
			axisDrawable[i].update();
		}
		
		// update xOyPlane
		xOyPlaneDrawable.update();
		
		// update intersection curves in controller
		//((EuclidianController3D) getEuclidianController()).updateIntersectionCurves();

		
	}
	
	
	
	
	//////////////////////////////////////////////////////
	// AXES
	//////////////////////////////////////////////////////

	public String[] getAxesLabels(){
		return axesLabels;
	}
	
	public void setAxesLabels(String[] axesLabels){
		this.axesLabels = axesLabels;
		for (int i = 0; i < 3; i++) {
			if (axesLabels[i] != null && axesLabels[i].length() == 0) {
				axesLabels[i] = null;
			}
		}
	}
	
	public void setAxisLabel(int axis, String axisLabel){
		if (axisLabel != null && axisLabel.length() == 0) 
			axesLabels[axis] = null;
		else
			axesLabels[axis] = axisLabel;
	}
	
	public String[] getAxesUnitLabels(){
		return axesUnitLabels;
	}
	public void setShowAxesNumbers(boolean[] showAxesNumbers){
		this.showAxesNumbers = showAxesNumbers;
	}
	
	public void setAxesUnitLabels(String[] axesUnitLabels){
		this.axesUnitLabels = axesUnitLabels;

		// check if pi is an axis unit
		for (int i = 0; i < 3; i++) {
			piAxisUnit[i] = axesUnitLabels[i] != null
					&& axesUnitLabels[i].equals(Unicode.PI_STRING);
		}
		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);
		setAxesIntervals(getZscale(), 2);
	}
	
	public boolean[] getShowAxesNumbers(){
		return showAxesNumbers;
	}
	
	public void setShowAxisNumbers(int axis, boolean showAxisNumbers){
		showAxesNumbers[axis]=showAxisNumbers;
	}
	
	public void setAxesNumberingDistance(double dist, int axis){
		axesNumberingDistances[axis] = dist;
		setAutomaticAxesNumberingDistance(false, axis);
	}
	
	public int[] getAxesTickStyles(){
		return axesTickStyles;
	}

	public void setAxisTickStyle(int axis, int tickStyle){
		axesTickStyles[axis]=tickStyle;
	}



	
	/////////////////////////////
	// OPTIONS
	////////////////////////////


	public int toScreenCoordX(double minX) {
		// TODO Auto-generated method stub
		return 0;
	}





	public int toScreenCoordY(double maxY) {
		// TODO Auto-generated method stub
		return 0;
	}





	@SuppressWarnings("unchecked")
	public Previewable createPreviewParallelLine(ArrayList selectedPoints,
			ArrayList selectedLines) {
		// TODO Auto-generated method stub
		return null;
	}





	@SuppressWarnings("unchecked")
	public Previewable createPreviewPerpendicularLine(ArrayList selectedPoints,
			ArrayList selectedLines) {
		// TODO Auto-generated method stub
		return null;
	}





	@SuppressWarnings("unchecked")
	public Previewable createPreviewPerpendicularBisector(
			ArrayList selectedPoints) {
		// TODO Auto-generated method stub
		return null;
	}





	@SuppressWarnings("unchecked")
	public Previewable createPreviewAngleBisector(ArrayList selectedPoints) {
		// TODO Auto-generated method stub
		return null;
	}

	public Previewable createPreviewPolyLine(ArrayList selectedPoints) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	public void setAxisCross(int axis, double cross) {
		axisCross[axis] = cross;
		
	}

	public void setPositiveAxis(int axis, boolean isPositiveAxis) {
		positiveAxes[axis] = isPositiveAxis;
	}

	public double[] getAxesCross() {
		return axisCross;
	}

	public void setAxesCross(double[] axisCross) {
		this.axisCross = axisCross;
	}

	public boolean[] getPositiveAxes() {
		return positiveAxes;
	}

	public void setPositiveAxes(boolean[] positiveAxis) {
		this.positiveAxes = positiveAxis;
	}


	public Color getAxesColor() {
		// TODO Auto-generated method stub
		return null;
	}


	public Color getGridColor() {
		// TODO Auto-generated method stub
		return null;
	}


	public boolean getShowGrid() {
		return xOyPlane.isGridVisible();
	}


	public boolean getGridIsBold() {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean getAllowShowMouseCoords() {
		// TODO Auto-generated method stub
		return false;
	}


	public double getXmin() {
		// TODO Auto-generated method stub
		return 0;
	}


	public double getXmax() {
		// TODO Auto-generated method stub
		return 0;
	}


	public double getYmin() {
		// TODO Auto-generated method stub
		return 0;
	}


	public double getYmax() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int getAxesLineStyle() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int getGridLineStyle() {
		// TODO Auto-generated method stub
		return 0;
	}


	public boolean isAutomaticGridDistance() {
		return automaticGridDistance;
	}


	public double[] getGridDistances() {
		return gridDistances;
	}


	public void setAxesColor(Color showColorChooser) {
		// TODO Auto-generated method stub
		
	}


	public void setGridColor(Color showColorChooser) {
		// TODO Auto-generated method stub
		
	}


	public void showGrid(boolean selected) {
		xOyPlane.setGridVisible(selected);
		
	}


	public void setGridIsBold(boolean selected) {
		// TODO Auto-generated method stub
		
	}


	public void setAllowShowMouseCoords(boolean selected) {
		// TODO Auto-generated method stub
		
	}


	public void setGridType(int selectedIndex) {
		// TODO Auto-generated method stub
		
	}


	public void setAxesLineStyle(int selectedIndex) {
		// TODO Auto-generated method stub
		
	}


	public void setGridLineStyle(int type) {
		// TODO Auto-generated method stub
		
	}


	public void setAutomaticGridDistance(boolean flag) {
		automaticGridDistance = flag;
		setAxesIntervals(getXscale(), 0);
		setAxesIntervals(getYscale(), 1);
		setAxesIntervals(getZscale(), 1);
		
	}


	private void setAxesIntervals(double yscale, int i) {
		Application.printStacktrace("TODO");
		
	}


	public void setRealWorldCoordSystem(double min, double max, double ymin,
			double ymax) {
		// TODO Auto-generated method stub
		
	}


	public void updateBackground() {
		// TODO Auto-generated method stub
		
	}


	public void setGridDistances(double[] dist) {
		gridDistances = dist;
		setAutomaticGridDistance(false);
	}


	public void setAutomaticAxesNumberingDistance(boolean b, int axis) {
		// TODO Auto-generated method stub
		
	}


	public void setAxesTickStyles(int[] styles) {
		// TODO Auto-generated method stub
		
	}


	public boolean[] getDrawBorderAxes() {
		return drawBorderAxes;
	}


	public void setDrawBorderAxes(boolean[] drawBorderAxes) {
		this.drawBorderAxes = drawBorderAxes;
		
	}


	public boolean[] isAutomaticAxesNumberingDistance() {
		return automaticAxesNumberingDistances;
	}


	public double[] getAxesNumberingDistances() {
		return axesNumberingDistances;
	}

	
	////////////////////////////////////////
	// ALGEBRA VIEW
	////////////////////////////////////////
	

	public int getMode() {
		return euclidianController3D.getMode();
	}

	protected Hits3D tempArrayList = new Hits3D();
	
	public void clickedGeo(GeoElement geo, MouseEvent e) {
		if (geo == null)
			return;

		tempArrayList.clear();
		tempArrayList.add(geo);
		boolean changedKernel = euclidianController3D.processMode(tempArrayList,
				e);
		if (changedKernel)
			app.storeUndoInfo();
		getKernel().notifyRepaint();
		
	}

	final public void mouseMovedOver(GeoElement geo) {
		Hits geos = null;
		if (geo != null) {
			tempArrayList.clear();
			tempArrayList.add(geo);
			geos = tempArrayList;
		}
		boolean repaintNeeded = euclidianController3D.refreshHighlighting(geos);
		if (repaintNeeded)
			getKernel().notifyRepaint();
	}
	
	
	
	
	public void changeLayer(GeoElement geo, int oldlayer, int newlayer){
		getApplication().getEuclidianView().changeLayer(geo, oldlayer, newlayer);
	}


	public boolean isZoomable() {
		// TODO Auto-generated method stub
		return false;
	}


	NumberValue xminObject, xmaxObject, yminObject, ymaxObject;
	/**
	 * @return the xminObject
	 */
	public GeoNumeric getXminObject() {
		return (GeoNumeric) xminObject;
	}

	/**
	 * @param xminObjectNew the xminObject to set
	 */
	public void setXminObject(NumberValue xminObjectNew) {
		if(xminObjectNew == null) return;
		if(xminObject !=null)
		((GeoNumeric)xminObject).removeEVSizeListener(this);
		this.xminObject = xminObjectNew;
		setSizeListeners();	
	}

	/**
	 * @return the xmaxObject
	 */
	public GeoNumeric getXmaxObject() {
		return (GeoNumeric) xmaxObject;
	}

	/**
	 * @param xmaxObjectNew the xmaxObject to set
	 */
	public void setXmaxObject(NumberValue xmaxObjectNew) {
		if(xmaxObjectNew == null) return;
		if(xmaxObject !=null)
		((GeoNumeric)xmaxObject).removeEVSizeListener(this);
		this.xmaxObject = xmaxObjectNew;
		setSizeListeners();	
	}

	/**
	 * @return the yminObject
	 */
	public GeoNumeric getYminObject() {
		return (GeoNumeric) yminObject;
	}

	/**
	 * @param yminObjectNew the yminObject to set
	 */
	public void setYminObject(NumberValue yminObjectNew) {
		if(yminObjectNew == null) return;
		if(yminObject !=null)
		((GeoNumeric)yminObject).removeEVSizeListener(this);
		this.yminObject = yminObjectNew;
		setSizeListeners();		
	}

	private void setSizeListeners() {		
		((GeoNumeric)xminObject).addEVSizeListener(this);
		((GeoNumeric)yminObject).addEVSizeListener(this);
		((GeoNumeric)xmaxObject).addEVSizeListener(this);
		((GeoNumeric)ymaxObject).addEVSizeListener(this);
	}

	/**
	 * @return the ymaxObject
	 */
	public GeoNumeric getYmaxObject() {
		return (GeoNumeric) ymaxObject;
	}

	/**
	 * @param ymaxObjectNew the ymaxObject to set
	 */
	public void setYmaxObject(NumberValue ymaxObjectNew) {
		if(ymaxObjectNew == null) return;
		if(ymaxObject !=null)
			((GeoNumeric)ymaxObject).removeEVSizeListener(this);
		this.ymaxObject = ymaxObjectNew;
		setSizeListeners();
	}

	public void setResizeXAxisCursor() {
		// TODO Auto-generated method stub
		
	}


	public void setResizeYAxisCursor() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	/////////////////////////////////////////////////
	// UPDATE VIEW : ZOOM, TRANSLATE, ROTATE
	/////////////////////////////////////////////////
	
	private boolean viewChangedByZoom = true;
	private boolean viewChangedByTranslate = true;
	private boolean viewChangedByRotate = true;



	private int pointCapturingMode;



	private int pointStyle;
	
	private void setViewChangedByZoom(){viewChangedByZoom = true;}
	private void setViewChangedByTranslate(){viewChangedByTranslate = true;}
	private void setViewChangedByRotate(){viewChangedByRotate = true;}
	private void setViewChanged(){
		viewChangedByZoom = true;
		viewChangedByTranslate = true;
		viewChangedByRotate = true;
	}
	
	public boolean viewChangedByZoom(){return viewChangedByZoom;}
	public boolean viewChangedByTranslate(){return viewChangedByTranslate;}
	public boolean viewChangedByRotate(){return viewChangedByRotate;}
	public boolean viewChanged(){
		return viewChangedByZoom || viewChangedByTranslate || viewChangedByRotate;
	}
	
	public void resetViewChanged(){
		viewChangedByZoom = false;
		viewChangedByTranslate = false;
		viewChangedByRotate = false;
	}
	
	
	
	
	
	/**
	 * Returns point capturing mode.
	 */
	final public int getPointCapturingMode() {
		return pointCapturingMode;
	}

	/**
	 * Set capturing of points to the grid.
	 */
	public void setPointCapturing(int mode) {
		pointCapturingMode = mode;
	}
	
	final public int getPointStyle() {
		return pointStyle;
	}
	
	
	
	/** 
	 * Get styleBar 
	 */
	EuclidianStyleBar3D styleBar;
	public EuclidianStyleBar3D getStyleBar(){
		if(styleBar==null){
			styleBar = new EuclidianStyleBar3D(this);
		}
		
		return styleBar;
	}


	public String getFromPlaneString(){
		return "space";
	}
	
	public String getTranslatedFromPlaneString(){
		return app.getPlain("space");
	}


	public Previewable createPreviewAngle(ArrayList selectedPoints) {
		// TODO Auto-generated method stub
		return null;
	}
	

	public boolean isDefault2D(){
		return false;
	}
	

	public boolean hasForParent(GeoElement geo){
		return false;
	}
	

	public boolean isMoveable(GeoElement geo){
		return geo.isMoveable();
	}
	

	public ArrayList<GeoPoint> getFreeInputPoints(AlgoElement algoParent){
		return algoParent.getFreeInputPoints();
	}
	
	
	/////////////////////////////////////////////////
	// PROJECTION (ORTHO/PERSPECTIVE/...)
	/////////////////////////////////////////////////
	
	
	final static public int PROJECTION_ORTHOGRAPHIC = 0;
	final static public int PROJECTION_PERSPECTIVE = 1;
	final static public int PROJECTION_ANAGLYPH = 2;
	final static public int PROJECTION_CAV = 3;
	
	private int projection = PROJECTION_ORTHOGRAPHIC;
	
	
	public void setProjection(int projection){
		if(this.projection!=projection){
			this.projection=projection;
			updateEye();
			setViewChanged();
			setWaitForUpdate();
			//resetAllDrawables();
			resetAllColors();
			renderer.setWaitForUpdateClearColor();
		}
	}
	
	public int getProjection(){
		return projection;
	}
	
	
	public void setProjectionOrthographic(){
		renderer.updateOrthoValues();
		setProjection(PROJECTION_ORTHOGRAPHIC);
	}
	
	
	private double projectionPerspectiveValue = 1500;
	

	public void setProjectionPerspective(){
		updateProjectionPerspectiveValue();
		setProjection(PROJECTION_PERSPECTIVE);
	}
	
	
	/**
	 * set the near distance regarding the angle (in degrees)
	 * @param angle
	 */
	public void setProjectionPerspectiveValue(double angle){
		projectionPerspectiveValue = angle;
		if (projection!=PROJECTION_PERSPECTIVE && projection!=PROJECTION_ANAGLYPH)
			projection=PROJECTION_PERSPECTIVE;
		updateProjectionPerspectiveValue();
	}
	
	private void updateProjectionPerspectiveValue(){
		/*
		if (projectionPerspectiveValue<0)
			renderer.setNear(0);
		else
		*/
		renderer.setNear(projectionPerspectiveValue);
	}
	
	/**
	 * 
	 * @return angle for perspective projection
	 */
	public double getProjectionPerspectiveValue(){
		return projectionPerspectiveValue;
	}
	
	
	public void setAnaglyph(){
		updateProjectionPerspectiveValue();
		renderer.updateAnaglyphValues();
		setProjection(PROJECTION_ANAGLYPH);
	}
	
	private boolean isAnaglyphGrayScaled = true;
	
	public boolean isAnaglyphGrayScaled(){
		return isAnaglyphGrayScaled;
	}
	
	public void setAnaglyphGrayScaled(boolean flag){
		
		if (isAnaglyphGrayScaled==flag)
			return;
		
		isAnaglyphGrayScaled=flag;
		resetAllDrawables();
	}
	
	public boolean isGrayScaled(){
		return projection==PROJECTION_ANAGLYPH && isAnaglyphGrayScaled();
	}
	
	private boolean isAnaglyphShutDownGreen = false;
	
	public boolean isAnaglyphShutDownGreen(){
		return isAnaglyphShutDownGreen;
	}

	public void setAnaglyphShutDownGreen(boolean flag){

		if (isAnaglyphShutDownGreen==flag)
			return;

		isAnaglyphShutDownGreen=flag;
		renderer.setWaitForUpdateClearColor();
	}
	
	public boolean isShutDownGreen(){
		return projection==PROJECTION_ANAGLYPH && isAnaglyphShutDownGreen();
	}
	
	private double eyeSepFactor = 0.03;

	public void setEyeSepFactor(double val){
		eyeSepFactor = val;
		renderer.updateAnaglyphValues();
	}
	
	public double getEyeSepFactor(){
		return eyeSepFactor;
	}


	public boolean isUnitAxesRatio() {
		// TODO Auto-generated method stub
		return false;
	}
	public int getViewID() {
		return Application.VIEW_EUCLIDIAN3D;
	}
	
	private double cavAngle = 30;
	private double cavFactor = 0.5;
	
	public void setCav(){
		renderer.updateCavValues();
		setProjection(PROJECTION_CAV);
	}
	
	public void setCavAngle(double angle){
		cavAngle = angle;
		renderer.updateCavValues();
	}
	
	public double getCavAngle(){
		return cavAngle;
	}
	
	public void setCavFactor(double factor){
		cavFactor = factor;
		renderer.updateCavValues();
	}
	
	public double getCavFactor(){
		return cavFactor;
	}

	

	//////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////


	public void updateBoundObjects() {
		if(isZoomable()){
			((GeoNumeric)xminObject).setValue(getXmin());
			((GeoNumeric)xmaxObject).setValue(getXmax());
			((GeoNumeric)yminObject).setValue(getYmin());
			((GeoNumeric)ymaxObject).setValue(getYmax());
		}
	}



	public void updateBounds() {
		// TODO Auto-generated method stub
		
	}


	public boolean getShowAxis(int axis) {
		return this.axis[axis].isEuclidianVisible();
	}
	

	public void replaceBoundObject(GeoNumeric num, GeoNumeric geoNumeric){
		
	}
	
	
	protected Color bgColor;
	
	public Color getBackground() {
		return bgColor;
	}

	public void setBackground(Color bgColor) {
		if (bgColor != null){
			this.bgColor = bgColor;
			if (renderer!=null)
				renderer.setWaitForUpdateClearColor();
		}
	}
	
	
	
	//////////////////////////////////////
	// PICKING
	
	
	public void addOneGeoToPick(){
		renderer.addOneGeoToPick();
	}
	
	public void removeOneGeoToPick(){
		renderer.removeOneGeoToPick();
	}
	
	
	//////////////////////////////////////
	// SOME LINKS WITH 2D VIEW
	
	public Graphics2D getGraphicsForPen(){
		return app.getEuclidianView().getGraphicsForPen();
		
	}


	public int getFontSize() {
		
		return app.getFontSize();
	}
}
