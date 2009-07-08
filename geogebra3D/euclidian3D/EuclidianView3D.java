package geogebra3D.euclidian3D;


import geogebra.euclidian.Drawable;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.euclidian.Hits;
import geogebra.euclidian.Previewable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPointInterface;
import geogebra.main.Application;
import geogebra.main.View;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.opengl.EuclidianRenderer3D;
import geogebra3D.kernel3D.ConstructionDefaults3D;
import geogebra3D.kernel3D.GeoConic3D;
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
import java.util.ArrayList;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLJPanel;
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
	//private boolean waitForUpdate = true; //says if it waits for update...
	//public boolean waitForPick = false; //says if it waits for update...
	private boolean removeHighlighting = false; //for removing highlighting when mouse is clicked
	DrawList3D drawList3D = new DrawList3D();
	
	
	//matrix for changing coordinate system
	private Ggb3DMatrix4x4 m = Ggb3DMatrix4x4.Identity(); 
	private Ggb3DMatrix4x4 mInv = Ggb3DMatrix4x4.Identity();
	private Ggb3DMatrix4x4 undoRotationMatrix = Ggb3DMatrix4x4.Identity();
	int a = 0;
	int b = 0;//angles
	int aOld, bOld;
	
	

	

	//picking and hits
	Hits3D hits = new Hits3D(); //objects picked from openGL
	
	//base vectors for moving a point
	static public Ggb3DVector vx = new Ggb3DVector(new double[] {1.0, 0.0, 0.0,  0.0});
	static public Ggb3DVector vy = new Ggb3DVector(new double[] {0.0, 1.0, 0.0,  0.0});
	static public Ggb3DVector vz = new Ggb3DVector(new double[] {0.0, 0.0, 1.0,  0.0});
	

	//preview
	private Previewable previewDrawable;
	private GeoPoint3D previewPoint;
	private GeoElement[] previewPointIntersetionOf = new GeoElement[2]; 
	
	public static final int PREVIEW_POINT_ALREADY = 0;
	public static final int PREVIEW_POINT_FREE = 1;
	public static final int PREVIEW_POINT_PATH = 2;
	public static final int PREVIEW_POINT_REGION = 3;
	public static final int PREVIEW_POINT_DEPENDENT = 4;
	private int previewPointType = PREVIEW_POINT_ALREADY;

	
	//cursor
	private static final int CURSOR_DEFAULT = 0;
	private static final int CURSOR_DRAG = 1;
	private static final int CURSOR_MOVE = 2;
	private static final int CURSOR_HIT = 3;
	private int cursor = CURSOR_DEFAULT;
	

	//mouse
	private boolean hasMouse = false;
	
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
		
		
		
		//previewables
		kernel3D.setSilentMode(true);
		previewPoint = kernel3D.Point3D(null, 1, 1, 0);
		previewPoint.setIsPickable(false);
		previewPoint.setLabelOffset(5, -5);
		previewPoint.setEuclidianVisible(false);
		kernel3D.setSilentMode(false);

		
		
	}
	
	
	
	/** return the 3D kernel
	 * @return the 3D kernel
	 */
	public Kernel3D getKernel(){
		return kernel3D;
	}
	
	
	
	
	public EuclidianController getEuclidianController(){
		return euclidianController3D;
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
					d = new DrawPoint3D(this, (GeoPoint3D) geo);
					break;									
								
				case GeoElement3D.GEO_CLASS_VECTOR3D:
					d = new DrawVector3D(this, (GeoVector3D) geo);
					break;									
								
				case GeoElement3D.GEO_CLASS_SEGMENT3D:
					d = new DrawSegment3D(this, (GeoSegment3D) geo);
					break;									
				

				case GeoElement3D.GEO_CLASS_PLANE3D:
					d = new DrawPlane3D(this, (GeoPlane3D) geo);
					break;									
				

				case GeoElement3D.GEO_CLASS_POLYGON3D:
					d = new DrawPolygon3D(this, (GeoPolygon3D) geo);
					break;									
				

				case GeoElement3D.GEO_CLASS_LINE3D:					
					d = new DrawLine3D(this, (GeoLine3D) geo);					
					break;									

				case GeoElement3D.GEO_CLASS_RAY3D:					
					d = new DrawRay3D(this, (GeoRay3D) geo);					
					break;	
					
				case GeoElement3D.GEO_CLASS_CONIC3D:					
					d = new DrawConic3D(this, (GeoConic3D) geo);
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
	
	
	/** return the matrix : screen coords -> scene coords.
	 * @return the matrix : screen coords -> scene coords.
	 */
	final public Ggb3DMatrix4x4 getToSceneMatrix(){
		
		return mInv;
	}
	
	/** return the matrix : scene coords -> screen coords.
	 * @return the matrix : scene coords -> screen coords.
	 */
	final public Ggb3DMatrix4x4 getToScreenMatrix(){
		
		return m;
	}	
	
	/** return the matrix undoing the rotation : scene coords -> screen coords.
	 * @return the matrix undoing the rotation : scene coords -> screen coords.
	 */
	final public Ggb3DMatrix4x4 getUndoRotationMatrix(){
		
		return undoRotationMatrix;
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

		undoRotationMatrix.set(m3.inverse());

		//scaling
		Ggb3DMatrix m4 = Ggb3DMatrix.ScaleMatrix(new double[] {getXscale(),getYscale(),getZscale()});		
		

		//translation
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
		//setWaitForUpdate(repaint);
		if (repaint)
			update();
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


		/*
		if (waitForUpdate){

			//picking
			if ((waitForPick)&&(!removeHighlighting)){

				waitForPick = false;
			}
			

			//other
			drawList3D.updateAll();	//TODO waitForUpdate for each object
			
			waitForUpdate = false;

		}
		
		*/
		
		
		drawList3D.updateAll();	//TODO waitForUpdate for each object



	}


	
	/*
	private void setWaitForUpdate(boolean v){
		waitForUpdate = v;
	}
	*/
	
	
	public void setRemoveHighlighting(boolean flag){
		removeHighlighting = flag;
	}
	
	
	public void paint(Graphics g){
		update();
		//setWaitForUpdate(true);
	}
	
	
	
	
	//////////////////////////////////////
	// toolbar and euclidianController3D
	
	/** sets EuclidianController3D mode */
	public void setMode(int mode){
		euclidianController3D.setMode(mode);
	}
	
	
	
	
	

	
	
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
		//setWaitForUpdate(true);
		
		update();
		
		//Application.debug("repaint View3D");
		
	}

	public void reset() {
		// TODO Raccord de méthode auto-généré
		
	}

	public void update(GeoElement geo) {
		//Application.debug("update(GeoElement geo)");
		repaintView();
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














	public int getPointCapturingMode() {
		// TODO Auto-generated method stub
		return 0;
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
		//Application.debug("repaintEuclidianView");
		
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

















































	public void setHits(Point p) {
		
		//Application.debug("setHits");

		//sets the flag and mouse location for openGL picking
		//renderer.setMouseLoc(p.x,p.y,EuclidianRenderer3D.PICKING_MODE_OBJECTS);
		renderer.setMouseLoc(p.x,p.y,EuclidianRenderer3D.PICKING_MODE_LABELS);

		//calc immediately the hits
		renderer.display();
		
		
		//Application.debug("end-setHits");		

	}


	public void setHits(Point p, boolean condition){
		if (condition)
			setHits(p);
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/////////////////////////////////////////
	// previewables
	
	
	// only used in 3D mode
	public Previewable createPreviewPoint(ArrayList selectedPoints){
		return new DrawPoint3D(this);
	}
	
	/** return the point used for previewables
	 * @return the point used for previewables
	 */
	public GeoPoint3D getPreviewPoint(){
		return previewPoint;
	}
	
	

	
	
	public void setPreviewPointType(int v){
		previewPointType = v;
	}
	

	public int getPreviewPointType(){
		return previewPointType;
	}
	
	
	
	public void setPreviewPointIntersetionOf(GeoElement previewPointIntersetionOf1, GeoElement previewPointIntersetionOf2){
		this.previewPointIntersetionOf[0]=previewPointIntersetionOf1;
		this.previewPointIntersetionOf[1]=previewPointIntersetionOf2;
	}
	
	public GeoElement getPreviewPointIntersetionOf(int i){
		return previewPointIntersetionOf[i];
	}
	
	
	
	
	
	
	public Previewable createPreviewLine(ArrayList selectedPoints){
		
		
		Application.debug("createPreviewLine");
		
		//selectedPoints = new ArrayList();
		//GeoPoint3D p1 = getKernel().Point3D("line1", 1, 1, 0);selectedPoints.add(p1);
		//GeoPoint3D p2 = getKernel().Point3D("line2", 2, 1, 0);selectedPoints.add(p2);
		

				
		Drawable3D d = new DrawLine3D(this, selectedPoints);
		//drawList3D.add(d);
		return (Previewable) d;
		
		
		//return null;
	}
	
	public Previewable createPreviewSegment(ArrayList selectedPoints){
		return new DrawSegment3D(this, selectedPoints);
	}	
	
	public Previewable createPreviewRay(ArrayList selectedPoints){
		return new DrawRay3D(this, selectedPoints);
	}	
	
	public Previewable createPreviewPolygon(ArrayList selectedPoints){
		return null;
	}	


	public void updatePreviewable(){
		EuclidianController ec = getEuclidianController();
		//if (ec.getMode()==EuclidianView.MODE_POINT_IN_REGION){
			//GeoPointInterface point = (GeoPointInterface) ((Drawable3D) getPreviewDrawable()).getGeoElement();
			//GeoPoint3D point = getPreviewPoint();
			ec.updateNewPoint(true, 
					getHits().getTopHits(), 
					true, true, true, false, //TODO doSingleHighlighting = false ? 
					false);
			/*
		}else{
			Point mouseLoc = getEuclidianController().getMouseLoc();
			getPreviewDrawable().updateMousePos(mouseLoc.x, mouseLoc.y);
		}
		*/
			getPreviewDrawable().updatePreview();
	}
	



	public void setPreview(Previewable previewDrawable) {
		
		if (previewDrawable==null){
			if (this.previewDrawable!=null)
				drawList3D.remove((Drawable3D) this.previewDrawable);			
		}else{
			drawList3D.add((Drawable3D) previewDrawable);
		}
			
			
		setPreviewPointType(PREVIEW_POINT_ALREADY);
		
		this.previewDrawable = previewDrawable;
		
		
		
	}

	
	
	
	
	
	
	
	
	
	
	/////////////////////////////////////////////////////
	// 
	// CURSOR
	//
	/////////////////////////////////////////////////////
	
	public void drawCursor(EuclidianRenderer3D renderer){

		if (hasMouse){
			switch(cursor){
			case CURSOR_DEFAULT:
				if(getPreviewPointType()!=PREVIEW_POINT_ALREADY)
					drawCursorCross(renderer);
				break;
			case CURSOR_HIT:
				switch(getPreviewPointType()){
				case PREVIEW_POINT_FREE:
				case PREVIEW_POINT_REGION:
					drawCursorCross(renderer);
					break;
				case PREVIEW_POINT_PATH:
					drawCursorOnPath(renderer);
					break;
				case PREVIEW_POINT_DEPENDENT:
					drawCursorDependent(renderer);
					break;
				}
				break;
			}
		}
	}
	
	private void drawCursorCross(EuclidianRenderer3D renderer){


		renderer.setMatrix(getPreviewPoint().getDrawingMatrix());
		renderer.setThickness(0.025);
		renderer.drawCrossWithEdges(0.12);


	}
	
	private void drawCursorOnPath(EuclidianRenderer3D renderer){
		renderer.setMatrix(getPreviewPoint().getDrawingMatrix());
		renderer.setMaterial(ConstructionDefaults3D.colPathPoint,1.0f);
		renderer.drawSphere(Drawable3D.POINT3D_RADIUS*Drawable3D.POINT_ON_PATH_DILATATION*3);
	}
	
	private void drawCursorDependent(EuclidianRenderer3D renderer){
		renderer.setMatrix(getPreviewPoint().getDrawingMatrix());
		renderer.setMaterial(ConstructionDefaults3D.colDepPoint,1.0f);
		renderer.drawSphere(Drawable3D.POINT3D_RADIUS*Drawable3D.POINT_ON_PATH_DILATATION*3);
	}
	
	public void setMoveCursor(){

		//Application.printStacktrace("setMoveCursor");
		cursor = CURSOR_MOVE;
	}
	
	public void setDragCursor(){
		//Application.printStacktrace("setDragCursor");
		cursor = CURSOR_DRAG;
	}
	
	public void setDefaultCursor(){
		//Application.printStacktrace("setDefaultCursor");
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
		hasMouse = false;
	}
	

}
