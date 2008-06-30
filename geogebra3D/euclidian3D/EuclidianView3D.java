package geogebra3D.euclidian3D;


import geogebra.View;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoSegment3D;

import java.awt.BorderLayout;
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
	
	
	static final boolean DEBUG = false; //conditionnal compilation

	
	private Kernel kernel;
	private EuclidianController3D euclidianController3D;
	protected EuclidianCanvas3D canvas3D;
	
	//viewing values
	private double XZero, YZero, ZZero;
	
	
	//list of 3D objects
	DrawList3D drawList3D = new DrawList3D();
	
	
	//matrix for changing coordinate system
	private GgbMatrix m = GgbMatrix.Identity(4); 
	private GgbMatrix mInv = GgbMatrix.Identity(4);
	double a = 0.0;
	double b = 0.0; //angles
	
	

	//picking
	//TODO get eye real position
	GgbVector eye = new GgbVector(new double[] {0.0,0.0,2.4,1.0});
	ArrayList hits = new ArrayList(); //objects picked
	
	
	
	public EuclidianView3D(EuclidianController3D ec){
		
		
		
		this.euclidianController3D = ec;
		this.kernel = ec.getKernel();
		euclidianController3D.setView(this);
		
		
		canvas3D = new EuclidianCanvas3D();
		canvas3D.setDoubleBufferEnable(true);
		//canvas3D.getView().setSceneAntialiasingEnable(true);
		
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, canvas3D);
		
		attachView();
		
		
		// register Listener		
		canvas3D.addMouseMotionListener(euclidianController3D);
		canvas3D.addMouseListener(euclidianController3D);
		canvas3D.addMouseWheelListener(euclidianController3D);
		
		
		//init orientation
		//setRotXY(Math.PI/6f,0.0,true);
		
		
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
					if(DEBUG){System.out.println("GEO_CLASS_POINT3D");}
					d = new DrawPoint3D(this, (GeoPoint3D) geo);
					if(DEBUG){System.out.println("new DrawPoint3D");}
					break;									
								
				case GeoElement3D.GEO_CLASS_SEGMENT3D:
					if(DEBUG){System.out.println("GEO_CLASS_SEGMENT3D");}
					d = new DrawSegment3D(this, (GeoSegment3D) geo);
					//System.out.println("new DrawPoint3D");
					break;									
				

				case GeoElement3D.GEO_CLASS_PLANE3D:
					if(DEBUG){System.out.println("GEO_CLASS_PLANE3D");}
					d = new DrawPlane3D(this, (GeoPlane3D) geo);
					//System.out.println("new DrawPoint3D");
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
		
		//System.out.println("m = "); m.SystemPrint();
		
	}

	

	//TODO interaction
	public double getXZero() { return XZero; }
	public double getYZero() { return YZero; }
	public double getZZero() { return ZZero; }

	public void setXZero(double val) { XZero=val; }
	public void setYZero(double val) { YZero=val; }
	public void setZZero(double val) { ZZero=val; }
	
	public double getXscale() { return 1; }
	public double getYscale() { return 1; }
	public double getZscale() { return 1; }

	
	
	
	
	
	
	
	
	public void setRotXY(double a, double b, boolean repaint){
		
		this.a = a;
		this.b = b;
		
		updateMatrix();
		
		if (repaint) {			
			repaint();
		}
		
	}
	
	
	
	
	
	public void paint(Graphics g){
	//public void repaint(){
		
		
		drawList3D.updateAll();
		GraphicsContext3D gc = canvas3D.getGraphicsContext3D();
		gc.setBufferOverride(true);
		gc.clear();
		drawList3D.drawAll(gc);
		

		canvas3D.swap();
		
		//super.paint(g);
		//super.repaint();
		//System.out.println("paint");
	}
	
	
	
	
	//////////////////////////////////////
	// picking
	
	public GgbVector getPickPoint(int x, int y){			
		
		
		Dimension d = new Dimension();
		this.getSize(d);
		
		if (d!=null){
			//System.out.println("Dimension = "+d.width+" x "+d.height);
			double w = (double) d.width/2;
			double h = (double) d.height/2;
			
			GgbVector ret = new GgbVector(
					new double[] {
							((double) (x-w)/w),
							((double) (-y+h)/w),
							0, 1.0});
			
			return ret;
		}else
			return null;
		
		
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
