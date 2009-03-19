package geogebra3D.euclidian3D;


import geogebra.euclidian.Drawable;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.euclidian.Hits;
import geogebra.euclidian.Previewable;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra.main.View;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoElement3DInterface;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoPolygon3D;
import geogebra3D.kernel3D.GeoQuadric;
import geogebra3D.kernel3D.GeoRay3D;
import geogebra3D.kernel3D.GeoSegment3D;
import geogebra3D.kernel3D.GeoVector3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.media.opengl.GLCanvas;
import javax.swing.JPanel;


public class EuclidianView3D extends JPanel implements View, Printable, EuclidianConstants3D, EuclidianViewInterface {

	

	private static final long serialVersionUID = -8414195993686838278L;
	
	
	static final boolean DEBUG = false; //conditionnal compilation

	
	//private Kernel kernel;
	private Kernel3D kernel3D;
	private EuclidianController3D euclidianController3D;
	private EuclidianRenderer3D renderer;
	
	//viewing values
	private double XZero, YZero, ZZero;
	
	
	//list of 3D objects
	private boolean waitForUpdate = true; //says if it waits for update...
	public boolean waitForPick = false; //says if it waits for update...
	private boolean removeHighlighting = false; //for removing highlighting when mouse is clicked
	DrawList3D drawList3D = new DrawList3D();
	
	
	//matrix for changing coordinate system
	private Ggb3DMatrix4x4 m = Ggb3DMatrix4x4.Identity(); 
	private Ggb3DMatrix4x4 mInv = Ggb3DMatrix4x4.Identity();
	int a = 0;
	int b = 0;//angles
	int aOld, bOld;
	
	
	//values for view frutum
	double left = 0; double right = 640;
	double bottom = 0; double top = 480;
	double front = -1000; double back = 1000;
	
	

	//picking and hits
	Hits3D hits = new Hits3D(); //objects picked from openGL
	
	//base vectors for moving a point
	static public Ggb3DVector vx = new Ggb3DVector(new double[] {1.0, 0.0, 0.0,  0.0});
	static public Ggb3DVector vy = new Ggb3DVector(new double[] {0.0, 1.0, 0.0,  0.0});
	static public Ggb3DVector vz = new Ggb3DVector(new double[] {0.0, 0.0, 1.0,  0.0});
	
	protected GeoPlane3D movingPlane;
	protected GeoSegment3D movingSegment;
	protected Ggb3DVector movingPointProjected;
	


	
	//stuff TODO
	protected Rectangle selectionRectangle = new Rectangle();

	
	
	public EuclidianView3D(EuclidianController3D ec){
		
		/*
		setSize(new Dimension(EuclidianGLDisplay.DEFAULT_WIDTH,EuclidianGLDisplay.DEFAULT_HEIGHT));
		setPreferredSize(new Dimension(EuclidianGLDisplay.DEFAULT_WIDTH,EuclidianGLDisplay.DEFAULT_HEIGHT));
		*/
		
		this.euclidianController3D = ec;
		this.kernel3D = (Kernel3D) ec.getKernel();
		euclidianController3D.setView(this);
		
		// TODO cast kernel to kernel3D
		/*
		kernel3D=new Kernel3D();
		kernel3D.setConstruction(kernel.getConstruction());
		*/
		
		//TODO replace canvas3D with GLDisplay
		renderer = new EuclidianRenderer3D(this);
		renderer.setDrawList3D(drawList3D);
		
		

 
        GLCanvas canvas = renderer.canvas;

        
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, canvas);

		
		attachView();
		
		
		// register Listener		
		
		canvas.addMouseMotionListener(euclidianController3D);
		canvas.addMouseListener(euclidianController3D);
		canvas.addMouseWheelListener(euclidianController3D);
		canvas.setFocusable(true);
		
		
		//init orientation
		//setRotXY(Math.PI/6f,0.0,true);
		
		//init moving objects
		movingPlane=kernel3D.Plane3D("movingPlane",
				new Ggb3DVector(new double[] {0.0,0.0,0.0,1.0}),
				vx,
				vy);
		movingPlane.setObjColor(new Color(0f,0f,1f));
		movingPlane.setAlgebraVisible(false); //TODO make it works
		movingPlane.setLabelVisible(false);


		movingSegment = kernel3D.Segment3D("movingSegment", 
				new Ggb3DVector(new double[] {0,0,0,1}),
				new Ggb3DVector(new double[] {0,0,0,1}));
		movingSegment.setObjColor(new Color(0f,0f,1f));
		movingSegment.setAlgebraVisible(false); //TODO make it works
		movingSegment.setLabelVisible(false);
		movingSegment.setLineThickness(1);
		
		setMovingVisible(false);
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * adds a GeoElement3D to this view
	 */	
	public void add(GeoElement geo) {
		
		if (geo.isGeoElement3D()){
			Drawable3D d = null;
			d = createDrawable(geo);
			if (d != null) {
				drawList3D.add(d);
				//repaint();			
			}
		}
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
		if (geo.isGeoElement3D()){
			if (d == null){
	
				switch (geo.getGeoClassType()) {
				
				case GeoElement3D.GEO_CLASS_POINT3D:
					if(DEBUG){Application.debug("GEO_CLASS_POINT3D");}
					d = new DrawPoint3D(this, (GeoPoint3D) geo);
					if(DEBUG){Application.debug("new DrawPoint3D");}
					break;									
								
				case GeoElement3D.GEO_CLASS_VECTOR3D:
					if(DEBUG){Application.debug("GEO_CLASS_VECTOR3D");}
					d = new DrawVector3D(this, (GeoVector3D) geo);
					if(DEBUG){Application.debug("new GeoVector3D");}
					break;									
								
				case GeoElement3D.GEO_CLASS_SEGMENT3D:
					if(DEBUG){Application.debug("GEO_CLASS_SEGMENT3D");}
					d = new DrawSegment3D(this, (GeoSegment3D) geo);
					//Application.debug("new DrawPoint3D");
					break;									
				

				case GeoElement3D.GEO_CLASS_PLANE3D:
					if(DEBUG){Application.debug("GEO_CLASS_PLANE3D");}
					d = new DrawPlane3D(this, (GeoPlane3D) geo);
					//Application.debug("new DrawPoint3D");
					break;									
				

				case GeoElement3D.GEO_CLASS_POLYGON3D:
					d = new DrawPolygon3D(this, (GeoPolygon3D) geo);
					Application.debug("new DrawPolygon3D");
					break;									
				

				case GeoElement3D.GEO_CLASS_LINE3D:					
					d = new DrawLine3D(this, (GeoLine3D) geo);					
					break;									

				case GeoElement3D.GEO_CLASS_RAY3D:					
					d = new DrawRay3D(this, (GeoRay3D) geo);					
					break;									


				case GeoElement3D.GEO_CLASS_QUADRIC:					
					d = new DrawQuadric(this, (GeoQuadric) geo);					
					break;									
				}
				
				
			
			}
		}
		
		
		return d;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Converts real world coordinates to screen coordinates.
	 * 
	 * @param inOut:
	 *            input and output array with x, y, z, w coords (
	 */
	final public void toScreenCoords3D(Ggb3DVector vInOut) {	
		changeCoords(m,vInOut);		
	}
	
	final public void toScreenCoords3D(Ggb3DMatrix mInOut) {		
		changeCoords(m,mInOut);			
	}
	
	
	final public void toSceneCoords3D(Ggb3DVector vInOut) {	
		changeCoords(mInv,vInOut);		
	}
	
	final public void toSceneCoords3D(Ggb3DMatrix mInOut) {		
		changeCoords(mInv,mInOut);			
	}
	
	
	final private void changeCoords(Ggb3DMatrix mat, Ggb3DVector vInOut){
		Ggb3DVector v1 = vInOut.getCoordsLast1();
		vInOut.set(mat.mul(v1));		
	}

	final private void changeCoords(Ggb3DMatrix mat, Ggb3DMatrix mInOut){	
		Ggb3DMatrix m1 = mInOut.copy();
		mInOut.set(mat.mul(m1));		
	}
	
	
	final public Ggb3DMatrix4x4 getToSceneMatrix(){
		//return mInv.copy();
		return mInv;
	}
	
	final public Ggb3DMatrix4x4 getToScreenMatrix(){
		//return m.copy();
		return m;
	}	
	
	
	
	/**
	 * set Matrix for view3D
	 */	
	public void updateMatrix(){
		
		//TODO use Ggb3DMatrix4x4
		
		//rotations
		Ggb3DMatrix m1 = Ggb3DMatrix.Rotation3DMatrix(Ggb3DMatrix.X_AXIS, this.b*EuclidianController3D.ANGLE_SCALE - Math.PI/2.0);
		Ggb3DMatrix m2 = Ggb3DMatrix.Rotation3DMatrix(Ggb3DMatrix.Z_AXIS, this.a*EuclidianController3D.ANGLE_SCALE);
		Ggb3DMatrix m3 = m1.mul(m2);
		

		Ggb3DMatrix m4 = Ggb3DMatrix.ScaleMatrix(new double[] {getXscale(),getYscale(),getZscale()});		
		

		Ggb3DMatrix m5 = Ggb3DMatrix.TranslationMatrix(new double[] {getXZero(),getYZero(),getZZero()});
		
		m.set(m5.mul(m3.mul(m4)));	
		
		mInv.set(m.inverse());
		
		//Application.debug("m = "); m.SystemPrint();
		
	}

	
	public void setRotXY(int a, int b, boolean repaint){
		
		//Application.debug("setRotXY");
		
		this.a = a;
		this.b = b;
		
		if (this.b>EuclidianController3D.ANGLE_MAX)
			this.b=EuclidianController3D.ANGLE_MAX;
		else if (this.b<-EuclidianController3D.ANGLE_MAX)
			this.b=-EuclidianController3D.ANGLE_MAX;
		
		
		
		updateMatrix();
		setWaitForUpdate(repaint);
		//update();
	}
	
	
	/** Sets coord system from mouse move */
	final public void setCoordSystemFromMouseMove(int dx, int dy) {		
		setRotXY(aOld + dx, bOld + dy, true);
	}

	public void addRotXY(int da, int db, boolean repaint){
		
		setRotXY(a+da,b+db,repaint);
	}	

	public void setRotXY(double a, double b, boolean repaint){
		
		setRotXY((int) (a/EuclidianController3D.ANGLE_SCALE),(int) (b/EuclidianController3D.ANGLE_SCALE),repaint);
		
	}
	
	

	//TODO interaction
	public double getXZero() { XZero = 300; return XZero; }
	public double getYZero() { YZero = 200; return YZero; }
	public double getZZero() { return ZZero; }

	public void setXZero(double val) { XZero=val; }
	public void setYZero(double val) { YZero=val; }
	public void setZZero(double val) { ZZero=val; }
	
	private double scale = 100; 
	public double getXscale() { return scale; }
	public double getYscale() { return scale; }
	public double getZscale() { return scale; }
	
	public void setScale(double val){
		scale = val;
	}

	
	/** remembers the origins values (xzero, ...) */
	public void rememberOrigins(){
		aOld = a;
		bOld = b;
	}

	
	
	
	

	
	
	//////////////////////////////////////
	// update
	

	/** update the drawables for 3D view, called by EuclidianRenderer3D */
	public void update(){
				
		
		if (waitForUpdate){
			
			//picking
			if ((waitForPick)&&(!removeHighlighting)){
				
				
				/*
				for (Iterator iter = hits.getHitsHighlighted().iterator(); iter.hasNext();) {
					Drawable3D d = (Drawable3D) iter.next();
					GeoElement3D geo = (GeoElement3D) d.getGeoElement();
					geo.setWasHighlighted();
					geo.setWillBeHighlighted(false);			
				}	
				*/				
				
				//hits.dispatch();
				
				/*
				for (Iterator iter = hits.getHitsHighlighted().iterator(); iter.hasNext();) {
					Drawable3D d = (Drawable3D) iter.next();
					GeoElement3D geo = (GeoElement3D) d.getGeoElement();
					geo.setWasHighlighted(); //TODO setWasHighlighted() may be called twice
					geo.setWillBeHighlighted(true);				
				}			

				for (Iterator iter = drawList3D.iterator(); iter.hasNext();) {
					Drawable3D d = (Drawable3D) iter.next();
					GeoElement3D geo = (GeoElement3D) d.getGeoElement();
					geo.updateHighlighted(true);				
				}
				*/

				waitForPick = false;
			}
			
			/*
			//remove highlighting when an object is selected
			if (removeHighlighting){
				//for (Iterator iter = hits.iterator(); iter.hasNext();) {
				for (Iterator iter = hits.getHitsHighlighted().iterator(); iter.hasNext();) {
					Drawable3D d = (Drawable3D) iter.next();
					GeoElement3D geo = (GeoElement3D) d.getGeoElement();
					geo.setWasHighlighted();
					geo.setWillBeHighlighted(false);
					geo.updateHighlighted(true);
				}
				removeHighlighting = false;
			}		
			*/	
			
			//other
			drawList3D.updateAll();	//TODO waitForUpdate for each object
			
			
			waitForUpdate = false;
		}
		
		
		
	}
	
	
	
	
	private void setWaitForUpdate(boolean v){
		waitForUpdate = v;
	}
	
	
	
	public void setRemoveHighlighting(boolean flag){
		removeHighlighting = flag;
	}
	
	
	public void paint(Graphics g){
		setWaitForUpdate(true);
	}
	
	
	
	
	//////////////////////////////////////
	// toolbar and euclidianController3D
	
	/** sets EuclidianController3D mode */
	public void setMode(int mode){
		euclidianController3D.setMode(mode);
	}
	
	
	
	
	//////////////////////////////////////
	// moving objects
	
	/** set colors of moving objects */
	public void setMovingColor(Color c){

		movingPlane.setObjColor(c);
		movingSegment.setObjColor(c);
		setMovingVisible(true);
		
	}
	
	/** sets moving plane to (origin,v1,v2,v3) and other objects regarding to point */
	public void setMoving(Ggb3DVector point, Ggb3DVector origin, Ggb3DVector v1, Ggb3DVector v2, Ggb3DVector v3){
		
		movingPlane.setCoord(origin, v1, v2);
		Ggb3DVector[] project = point.projectPlaneThruV(movingPlane.getMatrix4x4(), v3);
		setMovingCorners(0, 0, project[1].get(1), project[1].get(2));
		movingPointProjected = project[0];
		setMovingPoint(point);
		
	}
	
	/** sets the moving segment from point to its projection on movingPlane */
	public void setMovingProjection(Ggb3DVector point, Ggb3DVector vn){
		Ggb3DVector[] project = point.projectPlaneThruV(movingPlane.getMatrix4x4(), vn);
		movingPointProjected = project[0];
		setMovingPoint(point);
	}
	
	/** update moving point position */
	public void setMovingPoint(Ggb3DVector point){
		movingSegment.setCoord(movingPointProjected, point.sub(movingPointProjected));
	}
	
	
	/** update visibility of moving objects */
	public void setMovingVisible(boolean val){
		movingPlane.setEuclidianVisible(val);
		movingSegment.setEuclidianVisible(val);
		
	}
	
	/** sets the corner of the movingPlane */
	public void setMovingCorners(double x1, double  y1, double  x2, double  y2){
		
		movingPlane.setGridCorners(x1,y1,x2,y2);
		
	}
	
	
	

	
	//////////////////////////////////////
	// dimensions
	
	
	
	
	//////////////////////////////////////
	// picking
	
	
	/** (x,y) 2D screen coords -> 3D physical coords */
	public Ggb3DVector getPickPoint(int x, int y){			
		
		
		Dimension d = new Dimension();
		this.getSize(d);
		
		if (d!=null){
			//Application.debug("Dimension = "+d.width+" x "+d.height);
			double w = (double) d.width;
			double h = (double) d.height;
			
			Ggb3DVector ret = new Ggb3DVector(
					new double[] {
							(double) x,
							(double) -y+h,
							//((double) (x-w)/w),
							//((double) (-y+h)/w),
							0, 1.0});
			
			//ret.SystemPrint();
			return ret;
		}else
			return null;
		
		
	}
	
	
	/** p scene coords, (dx,dy) 2D mouse move -> 3D physical coords */
	public Ggb3DVector getPickFromScenePoint(Ggb3DVector p, int dx, int dy){
		Ggb3DVector point = p.copyVector();
		toScreenCoords3D(point);
		Ggb3DVector ret = new Ggb3DVector(
				new double[] {
						point.get(1)+dx,
						point.get(2)-dy,
						0, 1.0});

		return ret;
		
	}
	

		
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void attachView() {
		kernel3D.notifyAddAll(this);
		kernel3D.attach(this);
	}
	
	
	public void clearView() {
		// TODO Raccord de méthode auto-généré
		
	}

	/**
	 * remove a GeoElement3D from this view
	 */	
	public void remove(GeoElement geo) {

		if (geo.isGeoElement3D()){
			Drawable3D d = ((GeoElement3DInterface) geo).getDrawable3D();
			drawList3D.remove(d);
		}
	}


	public void rename(GeoElement geo) {
		// TODO Raccord de méthode auto-généré
		
	}

	public void repaintView() {
		setWaitForUpdate(true);
		
		//Application.debug("repaint View3D");
		
	}

	public void reset() {
		// TODO Raccord de méthode auto-généré
		
	}

	public void update(GeoElement geo) {
		// TODO Raccord de méthode auto-généré
		
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Raccord de méthode auto-généré
		
	}

	public int print(Graphics arg0, PageFormat arg1, int arg2) throws PrinterException {
		// TODO Raccord de méthode auto-généré
		return 0;
	}












	//////////////////////////////////////////////
	// EuclidianViewInterface





	public Drawable getDrawableFor(GeoElement geo) {
		// TODO Auto-generated method stub
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
		return hits;
	}














	public double getInvXscale() {
		// TODO Auto-generated method stub
		return 0;
	}














	public double getInvYscale() {
		// TODO Auto-generated method stub
		return 0;
	}














	public GeoElement getLabelHit(Point mouseLoc) {
		// TODO Auto-generated method stub
		return null;
	}














	public int getPointCapturingMode() {
		// TODO Auto-generated method stub
		return 0;
	}














	public Previewable getPreviewDrawable() {
		// TODO Auto-generated method stub
		return null;
	}














	public Rectangle getSelectionRectangle() {
		return selectionRectangle;
	}













	public boolean getShowMouseCoords() {
		// TODO Auto-generated method stub
		return false;
	}














	public boolean getShowXaxis() {
		// TODO Auto-generated method stub
		return false;
	}














	public boolean getShowYaxis() {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		
	}














	public void resetMode() {
		// TODO Auto-generated method stub
		
	}














	public void setAnimatedCoordSystem(double ox, double oy, double newScale,
			int steps, boolean storeUndo) {
		// TODO Auto-generated method stub
		
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














	public void setDefaultCursor() {
		// TODO Auto-generated method stub
		
	}














	public void setDragCursor() {
		// TODO Auto-generated method stub
		
	}














	public void setHitCursor() {
		// TODO Auto-generated method stub
		
	}














	public void setHits(Point p) {
		//sets the flag and mouse location for openGL picking
		renderer.setMouseLoc(p.x,p.y);
	}














	public void setHits(Rectangle rect) {
		// TODO Auto-generated method stub
		
	}














	public void setMoveCursor() {
		// TODO Auto-generated method stub
		
	}














	public void setPreview(Previewable previewDrawable) {
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














	public void showAxes(boolean b, boolean showYaxis) {
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
		// TODO Auto-generated method stub
		
	}

}
