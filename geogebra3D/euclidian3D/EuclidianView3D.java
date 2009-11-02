package geogebra3D.euclidian3D;


import geogebra.euclidian.Drawable;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.euclidian.Hits;
import geogebra.euclidian.Previewable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.View;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.ConstructionDefaults3D;
import geogebra3D.kernel3D.GeoAxis3D;
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
	private Renderer renderer;
	
	//viewing values
	private double XZero = 0;
	private double YZero = 0;
	private double ZZero = 0;
	
	private double XZeroOld = 0;
	private double YZeroOld = 0;
	
	//list of 3D objects
	private boolean waitForUpdate = true; //says if it waits for update...
	//public boolean waitForPick = false; //says if it waits for update...
	private boolean removeHighlighting = false; //for removing highlighting when mouse is clicked
	DrawList3D drawList3D;// = new DrawList3D();
	
	
	//matrix for changing coordinate system
	private Ggb3DMatrix4x4 m = Ggb3DMatrix4x4.Identity(); 
	private Ggb3DMatrix4x4 mInv = Ggb3DMatrix4x4.Identity();
	private Ggb3DMatrix4x4 undoRotationMatrix = Ggb3DMatrix4x4.Identity();
	double a = 0;
	double b = 0;//angles
	double aOld, bOld;
	
	

	

	//picking and hits
	Hits3D hits = new Hits3D(); //objects picked from openGL
	
	//base vectors for moving a point
	static public Ggb3DVector o = new Ggb3DVector(new double[] {0.0, 0.0, 0.0,  1.0});
	static public Ggb3DVector vx = new Ggb3DVector(new double[] {1.0, 0.0, 0.0,  0.0});
	static public Ggb3DVector vy = new Ggb3DVector(new double[] {0.0, 1.0, 0.0,  0.0});
	static public Ggb3DVector vz = new Ggb3DVector(new double[] {0.0, 0.0, 1.0,  0.0});
	
	//axis and xOy plane
	private GeoPlane3D xOyPlane;
	private GeoAxis3D[] axis;
	static final public int DRAWABLES_NB = 4;

	//preview
	private Previewable previewDrawable;
	private GeoPoint3D cursor3D;
	private GeoElement[] cursor3DIntersetionOf = new GeoElement[2]; 
	
	public static final int PREVIEW_POINT_ALREADY = 0;
	public static final int PREVIEW_POINT_FREE = 1;
	public static final int PREVIEW_POINT_PATH = 2;
	public static final int PREVIEW_POINT_REGION = 3;
	public static final int PREVIEW_POINT_DEPENDENT = 4;
	private int cursor3DType = PREVIEW_POINT_ALREADY;

	
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

		

		
		this.euclidianController3D = ec;
		this.kernel3D = (Kernel3D) ec.getKernel();
		euclidianController3D.setView(this);
		
		drawList3D = new DrawList3D(this);
		
		
		//TODO replace canvas3D with GLDisplay
		renderer = new Renderer(this);
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
		cursor3D = kernel3D.Point3D(null, 1, 1, 0);
		cursor3D.setIsPickable(false);
		cursor3D.setLabelOffset(5, -5);
		cursor3D.setEuclidianVisible(false);
		kernel3D.setSilentMode(false);
		
		
		
		
		initAxisAndPlane();
		
		
	}
	
	
	
	/**
	 * init the axis and xOy plane
	 */
	public void initAxisAndPlane(){
		
		

		//plane and axis
		
		xOyPlane = kernel3D.Plane3D("xOy", o, vx, vy);
		xOyPlane.setObjColor(new Color(0.5f,0.5f,0.5f));
		xOyPlane.setFixed(true);
		xOyPlane.setLabelVisible(false);
		add(xOyPlane);


		axis = new GeoAxis3D[3];
		
		axis[0] = kernel3D.Axis3D("Ox", o, vx);
		axis[0].setObjColor(Color.BLUE);
		axis[1] = kernel3D.Axis3D("Oy", o, vy);
		axis[1].setObjColor(Color.RED);
		axis[2] = kernel3D.Axis3D("Oz", o, vz);
		axis[2].setObjColor(Color.GREEN);
		for(int i=0;i<3;i++){
			axis[i].setFixed(true);
			axis[i].setLabelVisible(false);
			add(axis[i]);
		}
		
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
	
	
	public Renderer getRenderer(){
		return renderer;
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
					
				case GeoElement3D.GEO_CLASS_AXIS3D:	
					d = new DrawAxis3D(this, (GeoAxis3D) geo);	
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
		Ggb3DMatrix m1 = Ggb3DMatrix.Rotation3DMatrix(Ggb3DMatrix.X_AXIS, this.b*EuclidianController3D.ANGLE_TO_DEGREES - Math.PI/2.0);
		Ggb3DMatrix m2 = Ggb3DMatrix.Rotation3DMatrix(Ggb3DMatrix.Z_AXIS, this.a*EuclidianController3D.ANGLE_TO_DEGREES);
		Ggb3DMatrix m3 = m1.mul(m2);

		undoRotationMatrix.set(m3.inverse());

		//scaling
		Ggb3DMatrix m4 = Ggb3DMatrix.ScaleMatrix(new double[] {getXscale(),getYscale(),getZscale()});		
		

		//translation
		Ggb3DMatrix m5 = Ggb3DMatrix.TranslationMatrix(new double[] {getXZero(),getYZero(),getZZero()});
		
		m.set(m5.mul(m3.mul(m4)));	
		
		mInv.set(m.inverse());
		
		waitForUpdate = true;
		
		//Application.debug("m = "); m.SystemPrint();
		
	}

	
	public void setRotXYinDegrees(double a, double b, boolean repaint){
		
		//Application.debug("setRotXY");
		
		this.a = a;
		this.b = b;
		
		if (this.b>EuclidianController3D.ANGLE_MAX)
			this.b=EuclidianController3D.ANGLE_MAX;
		else if (this.b<-EuclidianController3D.ANGLE_MAX)
			this.b=-EuclidianController3D.ANGLE_MAX;
		
		
		
		updateMatrix();
		
		waitForUpdate = repaint;
		//if (repaint)
			//update();
	}
	
	
	/** Sets coord system from mouse move */
	final public void setCoordSystemFromMouseMove(int dx, int dy, int mode) {	
		switch(mode){
		case EuclidianController3D.MOVE_ROTATE_VIEW:
			setRotXYinDegrees(aOld + dx, bOld + dy, true);
			break;
		case EuclidianController3D.MOVE_VIEW:
			setXZero(XZeroOld+dx);
			setYZero(YZeroOld-dy);
			updateMatrix();
			update();
			break;
		}
	}

	public void addRotXY(int da, int db, boolean repaint){
		
		setRotXYinDegrees(a+da,b+db,repaint);
	}	

	public void setRotXY(double a, double b, boolean repaint){
		
		setRotXYinDegrees(a/EuclidianController3D.ANGLE_TO_DEGREES,b/EuclidianController3D.ANGLE_TO_DEGREES,repaint);
		
	}
	
	

	/* TODO interaction - note : methods are called by EuclidianRenderer3D.viewOrtho() 
	 * to re-center the scene */
	public double getXZero() { return XZero; }
	public double getYZero() { return YZero; }
	public double getZZero() { return ZZero; }

	public void setXZero(double val) { XZero=val; }
	public void setYZero(double val) { YZero=val; }
	public void setZZero(double val) { ZZero=val; }
	
	
	//TODO specific scaling for each direction
	private double scale = 100; 
	private double scaleMin = 10;
	public double getXscale() { return scale; }
	public double getYscale() { return scale; }
	public double getZscale() { return scale; }
	
	public void setScale(double val){
		scale = val;
		if (scale<scaleMin)
			scale=scaleMin;
	}
	
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
		
		if (waitForUpdate){
			drawList3D.updateAll();
			updateDrawables();
			waitForUpdate = false;
		}

	}
	
	
	/** tell the view that it has to be updated
	 * 
	 */
	public void setWaitForUpdate(){
		waitForUpdate = true;
	}
	
	
	
	
	
	
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
							(double) x+renderer.getLeft(),
							(double) -y+renderer.getTop(),
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
		drawList3D.clear();
		
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
		//drawList3D.clear();
		
	}

	public void update(GeoElement geo) {
		if (geo.isGeoElement3D()){
			Drawable3D d = ((GeoElement3DInterface) geo).getDrawable3D();
			if (d!=null){
				((GeoElement3DInterface) geo).getDrawable3D().update();
				//repaintView();
			}
		}
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

	
	/** init the hits for this view
	 * @param hits
	 */
	public void setHits(Hits3D hits){
		this.hits = hits;
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

		setScale(getXscale()*zoomFactor);
		updateMatrix();
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/////////////////////////////////////////
	// previewables
	
	

	
	/** return the point used for 3D cursor
	 * @return the point used for 3D cursor
	 */
	public GeoPoint3D getCursor3D(){
		return cursor3D;
	}
	
	

	
	
	public void setCursor3DType(int v){
		cursor3DType = v;
	}
	

	public int getCursor3DType(){
		return cursor3DType;
	}
	
	
	
	/** sets that the current 3D cursor is at the intersection of the two GeoElement parameters
	 * @param cursor3DIntersetionOf1 first GeoElement of intersection
	 * @param cursor3DIntersetionOf2 second GeoElement of intersection
	 */
	public void setCursor3DIntersetionOf(GeoElement cursor3DIntersetionOf1, GeoElement cursor3DIntersetionOf2){
		this.cursor3DIntersetionOf[0]=cursor3DIntersetionOf1;
		this.cursor3DIntersetionOf[1]=cursor3DIntersetionOf2;
	}
	
	/** return the i-th GeoElement of intersection
	 * @param i number of GeoElement of intersection
	 * @return GeoElement of intersection
	 */
	public GeoElement getCursor3DIntersetionOf(int i){
		return cursor3DIntersetionOf[i];
	}
	
	
	
	
	
	
	public Previewable createPreviewLine(ArrayList selectedPoints){
		
		
		//Application.debug("createPreviewLine");
		
				
		Drawable3D d = new DrawLine3D(this, selectedPoints);


		return (Previewable) d;
		
	}
	
	public Previewable createPreviewSegment(ArrayList selectedPoints){
		return new DrawSegment3D(this, selectedPoints);
	}	
	
	public Previewable createPreviewRay(ArrayList selectedPoints){
		return new DrawRay3D(this, selectedPoints);
	}	
	
	public Previewable createPreviewPolygon(ArrayList selectedPoints){
		return new DrawPolygon3D(this, selectedPoints);
	}	


	
	
	public void updatePreviewable(){


		getPreviewDrawable().updatePreview();
	}
	
	
	/**
	 * update the 3D cursor with current hits
	 */
	public void updateCursor3D(){

		//Application.debug("hits ="+getHits().toString());
		
		if (hasMouse){
			getEuclidianController().updateNewPoint(true, 
				getHits().getTopHits(), 
				true, true, true, false, //TODO doSingleHighlighting = false ? 
				false);
			
			
			
			// update cursor3D matrix
			double t;
			
			switch(getCursor3DType()){
			case PREVIEW_POINT_FREE:
				// use default directions for the cross
				t = 1/getScale();
				getCursor3D().getDrawingMatrix().setVx((Ggb3DVector) vx.mul(t));
				getCursor3D().getDrawingMatrix().setVy((Ggb3DVector) vy.mul(t));
				getCursor3D().getDrawingMatrix().setVz((Ggb3DVector) vz.mul(t));
				break;
			case PREVIEW_POINT_REGION:
				// use region drawing directions for the cross
				t = 1/getScale();
				getCursor3D().getDrawingMatrix().setVx(
						(Ggb3DVector) ((GeoElement3DInterface) getCursor3D().getRegion()).getDrawingMatrix().getVx().mul(t));
				getCursor3D().getDrawingMatrix().setVy(
						(Ggb3DVector) ((GeoElement3DInterface) getCursor3D().getRegion()).getDrawingMatrix().getVy().mul(t));
				getCursor3D().getDrawingMatrix().setVz(
						(Ggb3DVector) ((GeoElement3DInterface) getCursor3D().getRegion()).getDrawingMatrix().getVz().mul(t));
				break;
			case PREVIEW_POINT_PATH:
				// use path drawing directions for the cross
				t = 1/getScale();
				getCursor3D().getDrawingMatrix().setVx(
						(Ggb3DVector) ((GeoElement3DInterface) getCursor3D().getPath()).getDrawingMatrix().getVx().normalized().mul(t));
				t *= (10+((GeoElement) getCursor3D().getPath()).getLineThickness());
				getCursor3D().getDrawingMatrix().setVy(
						(Ggb3DVector) ((GeoElement3DInterface) getCursor3D().getPath()).getDrawingMatrix().getVy().mul(t));
				getCursor3D().getDrawingMatrix().setVz(
						(Ggb3DVector) ((GeoElement3DInterface) getCursor3D().getPath()).getDrawingMatrix().getVz().mul(t));
				break;
			case PREVIEW_POINT_DEPENDENT:
				//use size of intersection
				int t1 = getCursor3DIntersetionOf(0).getLineThickness();
				int t2 = getCursor3DIntersetionOf(1).getLineThickness();
				if (t1>t2)
					t2=t1;
				t = (t2+6)/getScale();
				getCursor3D().getDrawingMatrix().setVx((Ggb3DVector) vx.mul(t));
				getCursor3D().getDrawingMatrix().setVy((Ggb3DVector) vy.mul(t));
				getCursor3D().getDrawingMatrix().setVz((Ggb3DVector) vz.mul(t));
				break;
			}
			
			
			
			
			
			//Application.debug("update");
		}
		
	}
	



	public void setPreview(Previewable previewDrawable) {
		
		if (previewDrawable==null){
			if (this.previewDrawable!=null)
				drawList3D.remove((Drawable3D) this.previewDrawable);			
		}else{
			drawList3D.add((Drawable3D) previewDrawable);
		}
			
			
		setCursor3DType(PREVIEW_POINT_ALREADY);
		
		this.previewDrawable = previewDrawable;
		
		
		
	}

	
	
	
	
	
	
	
	
	
	
	/////////////////////////////////////////////////////
	// 
	// CURSOR
	//
	/////////////////////////////////////////////////////
	
	public void drawCursor(Renderer renderer){

		if (hasMouse){
			
			renderer.setMatrix(getCursor3D().getDrawingMatrix());
			
			switch(cursor){
			case CURSOR_DEFAULT:
				//if(getCursor3DType()!=PREVIEW_POINT_ALREADY)
				renderer.drawCursorCross();
				break;
			case CURSOR_HIT:
				switch(getCursor3DType()){
				case PREVIEW_POINT_FREE:
				case PREVIEW_POINT_REGION:
					renderer.drawCursorCross();
					break;
				case PREVIEW_POINT_PATH:
					renderer.drawCursorCylinder();
					break;
				case PREVIEW_POINT_DEPENDENT:
					renderer.drawCursorDiamond();
					break;
				}
				break;
			}
		}
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
		//TODO cursor = CURSOR_DEFAULT;
		setHitCursor();
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
	


	/**
	 * returns settings in XML format
	 * @return the XML description of 3D view settings
	 */
	public String getXML() {
		StringBuffer sb = new StringBuffer();
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
		
		
		
		sb.append("</euclidianView3D>\n");
		return sb.toString();
	}
	
	
	
	/////////////////////////////////////////////////////
	// 
	// EUCLIDIANVIEW DRAWABLES (AXIS AND PLANE)
	//
	/////////////////////////////////////////////////////
	
	
	/**
	 * toggle the visibility of axis and xOy plane
	 */
	public void toggleAxis(){
		
		boolean flag = xOyPlane.isEuclidianVisible();
		
		for(int i=0;i<3;i++)
			flag = (flag && axis[i].isEuclidianVisible());
		
		xOyPlane.setEuclidianVisible(!flag);
		for(int i=0;i<3;i++)
			axis[i].setEuclidianVisible(!flag);
		
	}
	
	
	public GeoPlane3D getxOyPlane()  {

		return xOyPlane;
		
	}
	
	
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
		xOyPlane.getDrawable3D().drawTransp(renderer);
	}
	
	
	/** draw hiding parts of view's drawables (xOy plane)
	 * @param renderer
	 */
	public void drawHiding(Renderer renderer){
		xOyPlane.getDrawable3D().drawHiding(renderer);
	}
	
	/** draw not hidden parts of view's drawables (axis)
	 * @param renderer
	 */
	public void draw(Renderer renderer){
		for(int i=0;i<3;i++)
			axis[i].getDrawable3D().draw(renderer);
	}
	
	/** draw hidden parts of view's drawables (axis)
	 * @param renderer
	 */
	public void drawHidden(Renderer renderer){
		for(int i=0;i<3;i++)
			axis[i].getDrawable3D().drawHidden(renderer);
	}
	
	
	/** draw for picking view's drawables (plane and axis)
	 * @param renderer
	 */
	public void drawForPicking(Renderer renderer){
		renderer.pick(xOyPlane.getDrawable3D());
		for(int i=0;i<3;i++)
			renderer.pick(axis[i].getDrawable3D());
	}
	
	public void updateDrawables(){
		for(int i=0;i<3;i++)
			axis[i].getDrawable3D().update();
	}
	
	


}
