package geogebra3D.euclidian3D;



import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.main.Application;
import geogebra3D.Application3D;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Iterator;

public class EuclidianController3D extends EuclidianController
implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener{

	static final boolean DEBUG = false; //conditionnal compilation
	
	
	
	//TODO link it to toolbar values
	protected static final int MOVE_NONE = 101;
	protected static final int MOVE_POINT = 102;
	protected static final int MOVE_POINT_WHEEL = 3102;
	protected static final int MOVE_VIEW = 106;
	
	
	protected int mode, moveMode = MOVE_NONE;
	
	
	
	//protected boolean isCtrlDown = false;
	protected boolean isAltDown = false;
	
	
	
	
	
	
	
	protected GeoElement3D objSelected = null;
	protected GeoPoint3D movedGeoPoint3D = null;
	
	
	protected EuclidianView3D view3D; //TODO move to EuclidianViewInterface
	protected Kernel3D kernel3D;
	protected Application3D app3D;
	
	
	
	
	//protected Point mouseLoc = new Point();
	protected Point mouseLocOld = new Point();
	protected Point startLoc = new Point();
	
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


	

	

	public EuclidianController3D(Kernel3D kernel3D) {
		super(kernel3D);
		this.kernel3D = kernel3D;
		app3D = kernel3D.getApplication3D();
		
	}
	
	
	
	
	void setView(EuclidianView3D view) {
		this.view3D = view;
		super.setView(view);
		//Application.debug("setView -> 3D");
	}
	
	
	
	
	public Kernel3D getKernel3D() {
		return kernel3D;
	}
	
	
	
	
	
	public void setMode(int newMode){
		initNewMode(newMode);
	}
	
	
	protected void initNewMode(int mode) {
		this.mode = mode;
		
		switch (mode) {	
		//create a new point
		case EuclidianView3D.MODE_POINT:			
			Application.debug("mode : create new point");
			//movedGeoPoint3D = kernel3D.Point3D("essai", 1, 1, 1);
			break;
		//move an object
		case EuclidianView3D.MODE_MOVE:
			break;
		}
	}
	
	
	
	
	
	
	
	
	public void mousePressed(MouseEvent e) {
		
		setMouseLocation(e);	
		moveMode = MOVE_NONE;
		
		if (Application.isRightClick(e))
			return;
		
		switch (mode) {		
		//create a new point
		case EuclidianView3D.MODE_POINT:			
			Application.debug("create new point");
			break;
			
		//move an object
		case EuclidianView3D.MODE_MOVE:
			if (e.isShiftDown() || Application.isControlDown(e) ) {
				moveMode = MOVE_VIEW;	
			} else {
				if (objSelected!=null)		
					objSelected.setSelected(false);
				//pickPoint=view.getPickPoint(mouseLoc.x,mouseLoc.y);
				//view.doPick(pickPoint,true,true);
				if (!((Hits3D) view3D.getHits()).getHitsHighlighted().isEmpty()){
					
					objSelected = (GeoElement3D) view3D.getHits().getTopHits().get(0);		
					objSelected.setSelected(true);
					//Application.debug("selected = "+objSelected.getLabel());

					if (objSelected.getGeoClassType()==GeoElement3D.GEO_CLASS_POINT3D){

						//removes highlighting
						view3D.setRemoveHighlighting(true);


						moveMode = MOVE_POINT;
						movedGeoPoint3D = (GeoPoint3D) objSelected;
						startLoc3D = movedGeoPoint3D.getCoords().copyVector(); 
						
						if (!movedGeoPoint3D.hasPath()){
							view3D.setMoving(movedGeoPoint3D.getCoords(),origin,v1,v2,vn);
							view3D.setMovingColor(movingColor);
						}

					}
				}
				kernel3D.notifyRepaint();
				//view.update();

			}
			break;

		// move drawing pad or axis
		case EuclidianView3D.MODE_TRANSLATEVIEW:	
			moveMode = MOVE_VIEW;
			break;


		default:
			moveMode = MOVE_NONE;


		}
		
		
		//start moving drawing pad
		/*
		if (moveMode==MOVE_VIEW){
			if(DEBUG){Application.debug("mousePressed");}			
			aOld = view.a;
			bOld = view.b;	
			startLoc.x = mouseLoc.x;
			startLoc.y = mouseLoc.y;				
			if(DEBUG){Application.debug("Start MOVE_VIEW : mouseLoc.x="+mouseLoc.x+"  startLoc.x="+startLoc.x);}
		}
		*/


	
	}	
	
	
	protected void handleMouseDragged(boolean repaint) {
		
		//TODO view.setMoveCursor();
		
		// moveMode was set in mousePressed()
		switch (moveMode) {
		case MOVE_POINT:
			movePoint(repaint);
			//Application.debug("movePoint  -- "+moveMode);
			break;
		
		case MOVE_POINT_WHEEL:
			moveMode = MOVE_POINT;
			break;

		case MOVE_VIEW:
			if (repaint) {
				//if(DEBUG){Application.debug("MOVE_VIEW : mouseLoc.x="+mouseLoc.x+"  startLoc.x="+startLoc.x);}
				/*
				double dx = (double) mouseLoc.x - startLoc.x;
				double dy = (double) mouseLoc.y - startLoc.y;
				view.setRotXY(aOld+dx*ANGLE_SCALE,bOld+dy*ANGLE_SCALE,true);
				*/
				
				view3D.addRotXY(mouseLoc.x-mouseLocOld.x,mouseLoc.y-mouseLocOld.y,true);
			}
			break;	
			
		case MOVE_NONE:
		default: // do nothing
			break;
		
		}

		
	}
	
	
	
	protected void movePoint(boolean repaint){
		
		if (movedGeoPoint3D.hasPathOn()){
			//getting current pick point and direction v 
			Ggb3DVector o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
			view3D.toSceneCoords3D(o);
			
			
			Ggb3DVector v = new Ggb3DVector(new double[] {0,0,1,0});
			view3D.toSceneCoords3D(v);
			
			//TODO do this just one time, when mouse is pressed
			//plane for projection
			Ggb3DMatrix plane = movedGeoPoint3D.getPathOn().getMovingMatrix(view3D.getToScreenMatrix());			
			view3D.toSceneCoords3D(plane);
			
			//getting new position of the point
			Ggb3DVector[] project = o.projectPlaneThruVIfPossible(plane, v);
			movedGeoPoint3D.setCoords(project[0]);
				
		}else if (movedGeoPoint3D.hasPathIn()){
			//getting current pick point and direction v 
			Ggb3DVector o = view3D.getPickPoint(mouseLoc.x,mouseLoc.y); 
			view3D.toSceneCoords3D(o);
			
			Ggb3DVector v = new Ggb3DVector(new double[] {0,0,1,0});
			view3D.toSceneCoords3D(v);
			
			//TODO do this just one time, when mouse is pressed
			//plane for projection
			Ggb3DMatrix plane = movedGeoPoint3D.getPathIn().getMovingMatrix(view3D.getToScreenMatrix());						
			
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







	public void mouseEntered(MouseEvent mouseEvent) {
		Component component = mouseEvent.getComponent();
	    if (!component.hasFocus()) {
	      component.requestFocusInWindow();
	    }
	    
	    
	    //view.update();
	    //view.setWaitForUpdate(true);
		
	}






	public void mouseReleased(MouseEvent e) {
		
		
		if (Application.isRightClick(e)){
			
			if (!view3D.hits.isEmpty()){				
				GeoElement3D geo = (GeoElement3D) view3D.getHits().getTopHits().get(0);	
				app3D.getGuiManager().showPopupMenu(geo, view3D, mouseLoc);
			}
			
			return;
		}
		
		
		
		switch (moveMode) {
		case MOVE_VIEW:
		default:
			break;

		case MOVE_POINT:
			view3D.setMovingVisible(false);
			//view.update();
			kernel3D.notifyRepaint();
			break;	
		
		}

		moveMode = MOVE_NONE;
		
	}




	public void mouseDragged(MouseEvent e) {
		if(DEBUG){Application.debug("mouseDragged");}
		setMouseLocation(e);
		handleMouseDragged(true);	
	}

	
	
	
	
	
	
	
	



	public void mouseMoved(MouseEvent e) {
		//Application.debug("mouseMoved");
		setMouseLocation(e);
		view3D.setHits(mouseLoc);
		

		kernel3D.notifyRepaint();
	}

	
	/** pick object under the mouse */
	/*
	private void pick(boolean repaint){
		//Application.debug("mouse=("+mouseLoc.x+","+mouseLoc.y+")");
		pickPoint=view.getPickPoint(mouseLoc.x,mouseLoc.y);
		view.doPick(pickPoint, repaint);
	}
	*/


	
	
	
	
	
	
	
	
	
	
	
	

	public void mouseWheelMoved(MouseWheelEvent e) {
		
		double r = e.getWheelRotation();

		switch (moveMode) {
		case MOVE_VIEW:
		default:
			view3D.setZZero(view3D.getZZero()+ r/10.0);
			view3D.updateMatrix();
			//view.update();
			kernel3D.notifyRepaint();
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
		
		mouseLoc = e.getPoint();
		//super.setMouseLocation(e);
		

	}



	
	
	
	
	/////////////////////////////////////////////////
	// keylistener

	public void keyPressed(KeyEvent e) {
		
		switch(e.getKeyCode()){
		case KeyEvent.VK_SHIFT:
			Application.debug("shift pressed");
			break;
		case KeyEvent.VK_CONTROL:
			//Application.debug("ctrl pressed");
			keyCtrlPressed();
			break;
		case KeyEvent.VK_ALT:
			//Application.debug("alt pressed");
			isAltDown = true;
			break;
		default:
				break;
		}
		
	}
	
	

	


	public void keyReleased(KeyEvent e) {
		
		switch(e.getKeyCode()){
		case KeyEvent.VK_SHIFT:
			//Application.debug("shift released");
			break;
		case KeyEvent.VK_CONTROL:
			//Application.debug("ctrl released");
			keyCtrlReleased();
			break;
		case KeyEvent.VK_ALT:
			isAltDown = false;
			break;
		default:
				break;
		}
	}


	
	
	
	
	public void keyCtrlPressed(){
		
		switch (moveMode) {
		case MOVE_VIEW:
			break;

		case MOVE_POINT:	
			break;	
			
		case MOVE_NONE:
		default:
			break;
		
		}
		
	}
	
	public void keyCtrlReleased(){
		
		
		/* 
		switch (moveMode) {
		case MOVE_VIEW:
			break;

		case MOVE_POINT:			
			movingPlane++;
			if (movingPlane==MOVING_PLANE_NB)
				movingPlane = 0;
			v1=v1list[movingPlane];
			v2=v2list[movingPlane];
			vn=vnlist[movingPlane];
			movingColor=movingColorlist[movingPlane];
					
			
			view.setMoving(movedGeoPoint3D.getCoords(),origin,v1,v2,vn);
			view.setMovingColor(movingColor);
			//view.update();
			view.setWaitForUpdate(true);
			break;	
			
		case MOVE_NONE:
		default:
			break;
		
		}
		*/
	}

	
	
	
	
	
	


	public void keyTyped(KeyEvent arg0) {
		// TODO Raccord de méthode auto-généré
		
	}
	
	
}
