package geogebra3D.euclidian3D;


import geogebra.Application;
import geogebra.View;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.euclidian3D.GL.GLDisplay;
import geogebra3D.euclidian3D.GL.Renderer;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoSegment3D;
import geogebra3D.kernel3D.GeoTriangle3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;

import javax.media.j3d.GraphicsContext3D;
import javax.swing.JPanel;




public class EuclidianView3D extends JPanel implements View, Printable {

	

	private static final long serialVersionUID = -8414195993686838278L;
	
	
	static final boolean DEBUG = true; //conditionnal compilation

	
	private Kernel kernel;
	private Kernel3D kernel3D;
	private EuclidianController3D euclidianController3D;
	//protected EuclidianCanvas3D canvas3D;
	
	//viewing values
	private double XZero, YZero, ZZero;
	
	
	//list of 3D objects
	private boolean waitForUpdate = true; //says if it waits for update...
	DrawList3D drawList3D = new DrawList3D();
	
	
	//matrix for changing coordinate system
	private GgbMatrix m = GgbMatrix.Identity(4); 
	private GgbMatrix mInv = GgbMatrix.Identity(4);
	double a = 0.0;
	double b = 0.0; //angles
	
	

	//picking
	ArrayList hits = new ArrayList(); //objects picked
	
	
	//base vectors for moving a point
	static public GgbVector vx = new GgbVector(new double[] {1.0, 0.0, 0.0,  0.0});
	static public GgbVector vy = new GgbVector(new double[] {0.0, 1.0, 0.0,  0.0});
	static public GgbVector vz = new GgbVector(new double[] {0.0, 0.0, 1.0,  0.0});
	
	protected GeoPlane3D movingPlane;
	protected GeoSegment3D movingSegment;
	protected GgbVector movingPointProjected;
	
	
	
	
	public EuclidianView3D(EuclidianController3D ec){
		
		/*
		setSize(new Dimension(EuclidianGLDisplay.DEFAULT_WIDTH,EuclidianGLDisplay.DEFAULT_HEIGHT));
		setPreferredSize(new Dimension(EuclidianGLDisplay.DEFAULT_WIDTH,EuclidianGLDisplay.DEFAULT_HEIGHT));
		*/
		
		this.euclidianController3D = ec;
		this.kernel = ec.getKernel();
		euclidianController3D.setView(this);
		
		// TODO cast kernel to kernel3D
		kernel3D=new Kernel3D();
		kernel3D.setConstruction(kernel.getConstruction());
		
		//TODO replace canvas3D with GLDisplay
		EuclidianGLDisplay GLDisplay = new EuclidianGLDisplay();
		EuclidianRenderer3D renderer = new EuclidianRenderer3D(this);
		renderer.setDrawList3D(drawList3D);
		
        GLDisplay.addRenderer(renderer);
        GLDisplay.start();

        
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, GLDisplay.glCanvas);

		
		attachView();
		
		
		// register Listener			
		GLDisplay.glCanvas.addMouseMotionListener(euclidianController3D);
		GLDisplay.glCanvas.addMouseListener(euclidianController3D);
		GLDisplay.glCanvas.addMouseWheelListener(euclidianController3D);
		GLDisplay.glCanvas.addKeyListener(euclidianController3D);
		GLDisplay.glCanvas.setFocusable(true);
		
		
		//init orientation
		//setRotXY(Math.PI/6f,0.0,true);
		
		//init moving objects
		movingPlane=kernel3D.Plane3D("movingPlane",
				new GgbVector(new double[] {0.0,0.0,0.0,1.0}),
				vx,
				vy);
		movingPlane.setObjColor(new Color(0f,0f,1f));
		movingPlane.setAlgebraVisible(false); //TODO make it works
		movingPlane.setLabelVisible(false);
		

		movingSegment = kernel3D.Segment3D("movingSegment", 
				new GgbVector(new double[] {0,0,0,1}),
				new GgbVector(new double[] {0,0,0,1}));
		movingSegment.setObjColor(new Color(0f,0f,1f));
		movingSegment.setAlgebraVisible(false); //TODO make it works
		movingSegment.setLabelVisible(false);
		movingSegment.setAlphaValue(0.25f);
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
				

				case GeoElement3D.GEO_CLASS_TRIANGLE3D:
					if(DEBUG){Application.debug("GEO_CLASS_TRIANGLE3D");}
					d = new DrawTriangle3D(this, (GeoTriangle3D) geo);
					//Application.debug("new DrawPoint3D");
					break;									
				

				case GeoElement3D.GEO_CLASS_LINE3D:					
					d = new DrawLine3D(this, (GeoLine3D) geo);					
					break;									
				}
				
				
				if (d != null) {			
					//canvas3D.add(d.getBranchGroup());
					//DrawableMap.put(geo, d);
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
	final public void toScreenCoords3D(GgbVector vInOut) {	
		changeCoords(m,vInOut);		
	}
	
	final public void toScreenCoords3D(GgbMatrix mInOut) {		
		changeCoords(m,mInOut);			
	}
	
	
	final public void toSceneCoords3D(GgbVector vInOut) {	
		changeCoords(mInv,vInOut);		
	}
	
	final public void toSceneCoords3D(GgbMatrix mInOut) {		
		changeCoords(mInv,mInOut);			
	}
	
	
	final private void changeCoords(GgbMatrix mat, GgbVector vInOut){
		GgbVector v1 = vInOut.getCoordsLast1();
		vInOut.set(mat.mul(v1));		
	}

	final private void changeCoords(GgbMatrix mat, GgbMatrix mInOut){	
		GgbMatrix m1 = mInOut.copy();
		mInOut.set(mat.mul(m1));		
	}
	
	
	
	
	
	/**
	 * set Matrix for view3D
	 */	
	public void updateMatrix(){
		//rotations
		GgbMatrix m1 = GgbMatrix.Rotation3DMatrix(GgbMatrix.AXE_X, this.b - Math.PI/2.0);
		GgbMatrix m2 = GgbMatrix.Rotation3DMatrix(GgbMatrix.AXE_Z, this.a);
		GgbMatrix m3 = m1.mul(m2);
		
		//scaling TODO getZscale()
		GgbMatrix m4 = GgbMatrix.ScaleMatrix(new double[] {getXscale(),getYscale(),getZscale()});		
		
		//translation TODO getZZero()
		GgbMatrix m5 = GgbMatrix.TranslationMatrix(new double[] {getXZero(),getYZero(),getZZero()});
		
		m = m5.mul(m3.mul(m4));	
		
		mInv = m.inverse();
		
		//Application.debug("m = "); m.SystemPrint();
		
	}

	
	public void setRotXY(double a, double b, boolean repaint){
		
		//Application.debug("setRotXY");
		
		this.a = a;
		this.b = b;
		
		updateMatrix();
		setWaitForUpdate(repaint);
		
		
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

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void update(){
		//waitForUpdate = true;
		if (waitForUpdate){
			drawList3D.updateAll();	//TODO waitForUpdate for each object
			waitForUpdate = false;
		}
	}
	
	
	public void setWaitForUpdate(boolean v){
		waitForUpdate = v;
	}
	
	public void paint(Graphics g){
		setWaitForUpdate(true);
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
	public void setMoving(GgbVector point, GgbVector origin, GgbVector v1, GgbVector v2, GgbVector v3){
		
		movingPlane.setCoord(origin, v1, v2);
		GgbVector[] project = point.projectPlaneThruV(movingPlane.getMatrixCompleted(), v3);
		setMovingCorners(0, 0, project[1].get(1), project[1].get(2));
		movingPointProjected = project[0];
		setMovingPoint(point);
		
	}
	
	/** sets the moving segment from point to its projection on movingPlane */
	public void setMovingProjection(GgbVector point, GgbVector vn){
		GgbVector[] project = point.projectPlaneThruV(movingPlane.getMatrixCompleted(), vn);
		movingPointProjected = project[0];
		setMovingPoint(point);
	}
	
	/** update moving point position */
	public void setMovingPoint(GgbVector point){
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
	// picking
	
	
	/** (x,y) 2D screen coords -> 3D physical coords */
	public GgbVector getPickPoint(int x, int y){			
		
		
		Dimension d = new Dimension();
		this.getSize(d);
		
		if (d!=null){
			//Application.debug("Dimension = "+d.width+" x "+d.height);
			double w = (double) d.width;
			double h = (double) d.height;
			
			GgbVector ret = new GgbVector(
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
	public GgbVector getPickFromScenePoint(GgbVector p, int dx, int dy){
		GgbVector point = p.copyVector();
		toScreenCoords3D(point);
		GgbVector ret = new GgbVector(
				new double[] {
						point.get(1)+dx,
						point.get(2)-dy,
						0, 1.0});

		return ret;
		
	}
	
	
	public void doPick(GgbVector pickPoint){
		doPick(pickPoint,false);
	}

	public void doPick(GgbVector pickPoint, boolean list){
		if (list)
			hits = drawList3D.doPick(pickPoint,true);
		else
			drawList3D.doPick(pickPoint,false);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void attachView() {
		kernel.notifyAddAll(this);
		kernel.attach(this);
	}
	
	
	public void clearView() {
		// TODO Raccord de méthode auto-généré
		
	}

	public void remove(GeoElement geo) {
		// TODO Raccord de méthode auto-généré
		
	}

	public void rename(GeoElement geo) {
		// TODO Raccord de méthode auto-généré
		
	}

	public void repaintView() {
		// TODO Raccord de méthode auto-généré
		
		
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

}
