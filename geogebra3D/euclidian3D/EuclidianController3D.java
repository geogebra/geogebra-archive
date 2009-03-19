package geogebra3D.euclidian3D;



import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra3D.Application3D;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;
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

public class EuclidianController3D extends EuclidianController
implements MouseListener, MouseMotionListener, MouseWheelListener{




	static final boolean DEBUG = false; //conditionnal compilation
	
	
	
	//TODO link it to toolbar values
	protected static final int MOVE_POINT_WHEEL = 3102;
	
	
	
	
	
	//protected boolean isCtrlDown = false;
	protected boolean isAltDown = false;
	
	
	
	
	
	
	
	protected GeoElement3D objSelected = null;
	protected GeoPoint3D movedGeoPoint3D = null;
	
	
	protected EuclidianView3D view3D; //TODO move to EuclidianViewInterface
	//protected Kernel3D kernel3D;
	//protected Application3D app3D;
	
	
	
	
	private Point mouseLocOld = new Point();
	
	protected Ggb3DVector mouseLoc3D, startLoc3D;
	
	//picking
	protected Ggb3DVector pickPoint;
	
	//moving plane	
	protected int movingPlane = 0;
	static protected int MOVING_PLANE_NB = 3;
	protected Ggb3DVector origin = new Ggb3DVector(new double[] {0,0,0,1});
	protected Ggb3DVector[] v1list = {EuclidianView3D.vx,EuclidianView3D.vy,EuclidianView3D.vz};
	protected Ggb3DVector[] v2list = {EuclidianView3D.vy,EuclidianView3D.vz,EuclidianView3D.vx};
	protected Ggb3DVector[] vnlist = {EuclidianView3D.vz,EuclidianView3D.vx,EuclidianView3D.vy};
	protected Color[] movingColorlist = {new Color(0f,0f,1f),new Color(1f,0f,0f),new Color(0f,1f,0f)};

	protected Ggb3DVector v1=v1list[movingPlane];
	protected Ggb3DVector v2=v2list[movingPlane];
	protected Ggb3DVector vn=vnlist[movingPlane];
	protected Color movingColor=movingColorlist[movingPlane];
	
	
	
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

		if (!movedGeoPoint3D.hasPath()){
			view3D.setMoving(movedGeoPoint3D.getCoords(),origin,v1,v2,vn);
			view3D.setMovingColor(movingColor);
		}
	}

	
	
	

	
	
	
	
	
	protected void movePoint(boolean repaint){
		
		if (movedGeoPoint3D.hasPath()){
			//getting current pick point and direction v 
			Ggb3DVector o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
			view3D.toSceneCoords3D(o);
			
			
			Ggb3DVector v = new Ggb3DVector(new double[] {0,0,1,0});
			view3D.toSceneCoords3D(v);
			
			//TODO do this just one time, when mouse is pressed
			//plane for projection
			Ggb3DMatrix plane = movedGeoPoint3D.getPath().getMovingMatrix(view3D.getToScreenMatrix());			
			view3D.toSceneCoords3D(plane);
			
			//getting new position of the point
			Ggb3DVector[] project = o.projectPlaneThruVIfPossible(plane, v);
			movedGeoPoint3D.setCoords(project[0]);
			
			
		}else if (isAltDown){ //moves the point along z-axis
			
			//getting current pick point and direction v 
			Ggb3DVector o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
			view3D.toSceneCoords3D(o);
			
			Ggb3DVector v = new Ggb3DVector(new double[] {0,0,1,0});
			view3D.toSceneCoords3D(v);
			
			//getting new position of the point
			Ggb3DVector project = movedGeoPoint3D.getCoords().projectNearLine(o, v, EuclidianView3D.vz);
			movedGeoPoint3D.setCoords(project);
			
			//update moving plane
			view3D.setMovingProjection(movedGeoPoint3D.getCoords(),vn);			
			
		}else{
			//getting current pick point and direction v 
			Ggb3DVector p = movedGeoPoint3D.getCoords().copyVector();
			Ggb3DVector o = view3D.getPickFromScenePoint(p,mouseLoc.x-mouseLocOld.x,mouseLoc.y-mouseLocOld.y); 
			view3D.toSceneCoords3D(o);
			
			
			Ggb3DVector v = new Ggb3DVector(new double[] {0,0,1,0});
			view3D.toSceneCoords3D(v);
			
			//plane for projection
			Ggb3DMatrix4x4 plane = new Ggb3DMatrix4x4();
			plane.set(view3D.movingPlane.getMatrix4x4());

			Ggb3DVector originOld = plane.getColumn(4);
			plane.set(movedGeoPoint3D.getCoords(), 4);
			Ggb3DVector originProjected = originOld.projectPlane(plane)[0];
			plane.set(originProjected, 4);
			
			//getting new position of the point
			Ggb3DVector[] project = o.projectPlaneThruVIfPossible(plane, v);
			movedGeoPoint3D.setCoords(project[0]);
			
			//update moving plane
			view3D.setMovingCorners(0, 0, project[1].get(1), project[1].get(2));
			view3D.setMovingProjection(movedGeoPoint3D.getCoords(),vn);
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
		
		view3D.setMovingVisible(false);		
		super.mouseReleased(e);

	}


	
	
	
	
	//////////////////////////////////////////////
	// creating a new point
	
	
	protected GeoPointInterface createNewPoint(){
		
		//getting current pick point and direction v 
		Ggb3DVector o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
		view3D.toSceneCoords3D(o);
		
		
		Ggb3DVector v = new Ggb3DVector(new double[] {0,0,1,0});
		view3D.toSceneCoords3D(v);
		
		//plane for projection
		Ggb3DMatrix4x4 plane = Ggb3DMatrix4x4.Identity();
		
		//getting new position of the point
		Ggb3DVector[] project = o.projectPlaneThruVIfPossible(plane, v);
		
		
		return ((Kernel3D) getKernel()).Point3D(null, project[0].get(1),  project[0].get(2), 0);
	}
	
	protected void updateMovedGeoPoint(GeoPointInterface point){
		movedGeoPoint3D = (GeoPoint3D) point;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

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
			
			//p = p + r*vn			
			Ggb3DVector p1 = (Ggb3DVector) movedGeoPoint3D.getCoords().add(vn.mul(-r*0.1)); 
			movedGeoPoint3D.setCoords(p1);
			view3D.setMovingPoint(p1);
			
			
			


			objSelected.updateCascade();

			
			movedGeoPoint3D.updateRepaint();//for highlighting in algebraView
			//kernel3D.notifyRepaint();
			
			
			break;	
		
		
		}
	
		
		

	}
	
	
	
	final protected void setMouseLocation(MouseEvent e) {

		if (mouseLoc!=null)
			mouseLocOld = (Point) mouseLoc.clone();
		

		isAltDown=Application.isAltDown(e);
		
		//mouseLoc = e.getPoint();
		super.setMouseLocation(e);
		

	}



	
	
	
	
	
}
