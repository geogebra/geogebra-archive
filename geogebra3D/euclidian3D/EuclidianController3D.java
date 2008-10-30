package geogebra3D.euclidian3D;



import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra.main.Application;
import geogebra3D.Application3D;
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

public class EuclidianController3D 
implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener{

	static final boolean DEBUG = false; //conditionnal compilation
	
	
	
	//TODO link it to toolbar values
	protected static final int MOVE_NONE = 101;
	protected static final int MOVE_POINT = 102;
	protected static final int MOVE_POINT_WHEEL = 3102;
	protected static final int MOVE_VIEW = 106;
	
	
	protected int mode, moveMode = MOVE_NONE;
	
	
	
	//protected boolean keyCtrlDown = false;
	//protected boolean keyAltDown = false;
	
	
	
	
	
	
	
	protected GeoElement3D objSelected = null;
	protected GeoPoint3D movedGeoPoint3D = null;
	
	
	protected EuclidianView3D view;
	protected Kernel3D kernel3D;
	protected Application3D app3D;
	
	
	
	
	protected Point mouseLoc = new Point();
	protected Point mouseLocOld = new Point();
	protected Point startLoc = new Point();
	
	protected GgbVector mouseLoc3D, startLoc3D;
	
	//picking
	protected GgbVector pickPoint;
	
	//moving plane	
	protected int movingPlane = 0;
	static protected int MOVING_PLANE_NB = 3;
	protected GgbVector origin = new GgbVector(new double[] {0,0,0,1});
	protected GgbVector[] v1list = {EuclidianView3D.vx,EuclidianView3D.vy,EuclidianView3D.vz};
	protected GgbVector[] v2list = {EuclidianView3D.vy,EuclidianView3D.vz,EuclidianView3D.vx};
	protected GgbVector[] vnlist = {EuclidianView3D.vz,EuclidianView3D.vx,EuclidianView3D.vy};
	protected Color[] movingColorlist = {new Color(0f,0f,1f),new Color(1f,0f,0f),new Color(0f,1f,0f)};

	protected GgbVector v1=v1list[movingPlane];
	protected GgbVector v2=v2list[movingPlane];
	protected GgbVector vn=vnlist[movingPlane];
	protected Color movingColor=movingColorlist[movingPlane];
	
	//scale factor for changing angle of view : 2Pi <-> 300 pixels 
	static final public double ANGLE_SCALE = 2*Math.PI/300f;
	static final public int ANGLE_MAX = (int) ((Math.PI/2)/ANGLE_SCALE); //maximum vertical angle


	

	

	public EuclidianController3D(Kernel3D kernel3D) {
		this.kernel3D = kernel3D;
		app3D = kernel3D.getApplication3D();
		
	}
	
	
	
	
	void setView(EuclidianView3D view) {
		this.view = view;
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
				if (!view.hitsHighlighted.isEmpty()){
					
					objSelected = view.getFirstHit();		
					objSelected.setSelected(true);
					//Application.debug("selected = "+objSelected.getLabel());

					if (objSelected.getGeoClassType()==GeoElement3D.GEO_CLASS_POINT3D){

						//removes highlighting
						view.setRemoveHighlighting(true);


						moveMode = MOVE_POINT;
						movedGeoPoint3D = (GeoPoint3D) objSelected;
						startLoc3D = movedGeoPoint3D.getCoords().copyVector(); 
						
						if (!movedGeoPoint3D.hasPath()){
							view.setMoving(movedGeoPoint3D.getCoords(),origin,v1,v2,vn);
							view.setMovingColor(movingColor);
						}

					}
				}
				view.setWaitForUpdate(true);
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
				
				view.addRotXY(mouseLoc.x-mouseLocOld.x,mouseLoc.y-mouseLocOld.y,true);
			}
			break;	
			
		case MOVE_NONE:
		default: // do nothing
			break;
		
		}

		
	}
	
	
	
	protected void movePoint(boolean repaint){
		
		if (movedGeoPoint3D.hasPath1D()){
			//getting current pick point and direction v 
			GgbVector p = movedGeoPoint3D.getCoords().copyVector();
			GgbVector o = view.getPickPoint(mouseLoc.x,mouseLoc.y); 
			view.toSceneCoords3D(o);
			
			
			GgbVector v = new GgbVector(new double[] {0,0,1,0});
			view.toSceneCoords3D(v);
			
			//TODO do this just one time, when mouse is pressed
			//plane for projection
			GgbMatrix plane = movedGeoPoint3D.getPath1D().getMovingMatrix(view.getToScreenMatrix());			
			view.toSceneCoords3D(plane);
			
			//getting new position of the point
			GgbVector[] project = o.projectPlaneThruV(plane, v);
			movedGeoPoint3D.setCoords(project[0]);
				
		}else{
			//getting current pick point and direction v 
			GgbVector p = movedGeoPoint3D.getCoords().copyVector();
			GgbVector o = view.getPickFromScenePoint(p,mouseLoc.x-mouseLocOld.x,mouseLoc.y-mouseLocOld.y); 
			view.toSceneCoords3D(o);
			
			
			GgbVector v = new GgbVector(new double[] {0,0,1,0});
			view.toSceneCoords3D(v);
			
			//plane for projection
			GgbMatrix plane = view.movingPlane.getMatrixCompleted();

			GgbVector originOld = plane.getColumn(4);
			plane.set(movedGeoPoint3D.getCoords(), 4);
			GgbVector originProjected = originOld.projectPlane(plane)[0];
			plane.set(originProjected, 4);
			
			//getting new position of the point
			GgbVector[] project = o.projectPlaneThruV(plane, v);
			movedGeoPoint3D.setCoords(project[0]);
			
			//update moving plane
			view.setMovingCorners(0, 0, project[1].get(1), project[1].get(2));
			view.setMovingProjection(movedGeoPoint3D.getCoords(),vn);
		}
		
		

		
		
		if (repaint){
			view.setWaitForUpdate(true);
			//view.update();
			movedGeoPoint3D.updateRepaint();//for highlighting in algebraView
		}else{
			movedGeoPoint3D.updateCascade();//TODO modify movedGeoPoint3D.updateCascade()
		}
		
		
	}




	public void mouseClicked(MouseEvent e) {
		
		//setMouseLocation(e);
		//view.rendererPick(mouseLoc.x,mouseLoc.y);

	}




	public void mouseEntered(MouseEvent mouseEvent) {
		Component component = mouseEvent.getComponent();
	    if (!component.hasFocus()) {
	      component.requestFocusInWindow();
	    }
	    
	    
	    //view.update();
	    view.setWaitForUpdate(true);
		
	}




	public void mouseExited(MouseEvent arg0) {
		// TODO Raccord de méthode auto-généré
		
	}




	public void mouseReleased(MouseEvent e) {
		
		
		if (Application.isRightClick(e)){
			
			if (!view.hits.isEmpty()){				
				GeoElement3D geo = (GeoElement3D) view.hits.get(0);	
				app3D.getGuiManager().showPopupMenu(geo, view, mouseLoc);
			}
			
			return;
		}
		
		
		
		switch (moveMode) {
		case MOVE_VIEW:
		default:
			break;

		case MOVE_POINT:
			view.setMovingVisible(false);
			//view.update();
			view.setWaitForUpdate(true);
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
		view.rendererPick(mouseLoc.x,mouseLoc.y);
		
		//pick(true);
		
		//view.update();
		view.setWaitForUpdate(true);
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
			view.setZZero(view.getZZero()+ r/10.0);
			view.updateMatrix();
			//view.update();
			view.setWaitForUpdate(true);
			break;

		case MOVE_POINT:
		case MOVE_POINT_WHEEL:
			
			//p = p + r*vn			
			GgbVector p1 = movedGeoPoint3D.getCoords().add(vn.mul(-r*0.1)).v(); 
			movedGeoPoint3D.setCoords(p1);
			view.setMovingPoint(p1);
			
			
			
			//mouse follows the point
			/*
			try {
				moveMode = MOVE_POINT_WHEEL;
				//Application.debug("moveMode = "+moveMode);
		        Robot robot = new Robot();
		        GgbVector p = p1.copyVector();
		        view.toScreenCoords3D(p);
		        GgbVector v = view.getScreenCoords(p);
		        Component component = e.getComponent();
		        Point point = component.getLocationOnScreen();
		        //Application.debug("location = "+point.x+","+point.y);
		        robot.mouseMove((int) v.get(1) + point.x, (int) v.get(2) + point.y);
		        
		        startLoc3D = p1.copyVector();
		    } catch(AWTException awte) {}
		    */
			

			//objSelected.updateRepaint(); //TODO modify updateRepaint()
			objSelected.updateCascade();
			//view.setMovingPlane(movedGeoPoint3D.getCoords(), v1, v2, vn);
			//view.update();
			view.setWaitForUpdate(true);
			movedGeoPoint3D.updateRepaint();//for highlighting in algebraView

			
			
			break;	
		
		
		}
	
		
		

	}
	
	
	
	final protected void setMouseLocation(MouseEvent e) {

		mouseLocOld = (Point) mouseLoc.clone();
		
		mouseLoc = e.getPoint();
		
		/*
		if (mouseLoc.x<0)
			mouseLoc.x=0;
		else if (mouseLoc.x>view.getWidth())
			mouseLoc.x=view.getWidth();
		if (mouseLoc.y<0)
			mouseLoc.y=0;
		else if (mouseLoc.y>view.getHeight())
			mouseLoc.x=view.getHeight();
			*/
		//TODO adapt this for points on a path
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
			//keyAltPressed();
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
			//Application.debug("alt released");
			//keyAltReleased();
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
