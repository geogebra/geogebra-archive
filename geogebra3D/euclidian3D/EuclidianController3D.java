package geogebra3D.euclidian3D;


import geogebra.Application;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Kernel;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPoint3D;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

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
	protected Kernel kernel;
	
	
	
	
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
	final double ANGLE_SCALE = 2*Math.PI/300f;


	
	double aOld, bOld;
	

	public EuclidianController3D(Kernel kernel) {
		this.kernel = kernel;
		
	}
	
	
	
	
	void setView(EuclidianView3D view) {
		this.view = view;
		//Application.debug("setView -> 3D");
	}
	
	
	
	
	Kernel getKernel() {
		return kernel;
	}
	
	
	
	
	
	public void setMode(int newMode){
		initNewMode(newMode);
	}
	
	
	protected void initNewMode(int mode) {
		this.mode = mode;
	}
	
	
	
	
	
	
	
	
	public void mousePressed(MouseEvent e) {
		
		setMouseLocation(e);	
		moveMode = MOVE_NONE;
		
		switch (mode) {

		//move an object
		case EuclidianView.MODE_MOVE:
			if (e.isShiftDown() || e.isControlDown() || e.isMetaDown()) {
				moveMode = MOVE_VIEW;	
			} else {

				if (objSelected!=null)
					objSelected.setSelected(false);
				pickPoint=view.getPickPoint(mouseLoc.x,mouseLoc.y);
				view.doPick(pickPoint,true,true);
				if (!view.hits.isEmpty()){
					objSelected = (GeoElement3D) view.hits.get(0);		
					objSelected.setSelected(true);
					//Application.debug("selected = "+objSelected.getLabel());

					if (objSelected.getGeoClassType()==GeoElement3D.GEO_CLASS_POINT3D){
						moveMode = MOVE_POINT;
						movedGeoPoint3D = (GeoPoint3D) objSelected;
						startLoc3D = movedGeoPoint3D.getCoords().copyVector(); 

						view.setMoving(movedGeoPoint3D.getCoords(),origin,v1,v2,vn);
						view.setMovingColor(movingColor);

					}
				}
				view.setWaitForUpdate(true);

			}
			break;

		// move drawing pad or axis
		case EuclidianView.MODE_TRANSLATEVIEW:	
			moveMode = MOVE_VIEW;
			break;


		default:
			moveMode = MOVE_NONE;


		}
		
		if (moveMode==MOVE_VIEW){
			if(DEBUG){Application.debug("mousePressed");}
			aOld = view.a;
			bOld = view.b;	
			startLoc.x = mouseLoc.x;
			startLoc.y = mouseLoc.y;	
			if(DEBUG){Application.debug("Start MOVE_VIEW : mouseLoc.x="+mouseLoc.x+"  startLoc.x="+startLoc.x);}
		}


	
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
				if(DEBUG){Application.debug("MOVE_VIEW : mouseLoc.x="+mouseLoc.x+"  startLoc.x="+startLoc.x);}
				double dx = (double) mouseLoc.x - startLoc.x;
				double dy = (double) mouseLoc.y - startLoc.y;
				view.setRotXY(aOld+dx*ANGLE_SCALE,bOld+dy*ANGLE_SCALE,true);
			}
			break;	
			
		case MOVE_NONE:
		default: // do nothing
			break;
		
		}

		
	}
	
	
	
	protected void movePoint(boolean repaint){
		
		//getting current pick point and direction v 
		GgbVector p=movedGeoPoint3D.getCoords().copyVector();
		GgbVector o = view.getPickFromScenePoint(p,mouseLoc.x-mouseLocOld.x,mouseLoc.y-mouseLocOld.y); 
		view.toSceneCoords3D(o);
		
		//Application.debug("mouseLocDelta = "+(mouseLoc.x-mouseLocOld.x)+","+(mouseLoc.y-mouseLocOld.y));
		
		GgbVector v = new GgbVector(new double[] {0,0,1,0});
		view.toSceneCoords3D(v);	
		
		//getting new position of the point
		GgbMatrix plane = view.movingPlane.getMatrixCompleted();
		
		GgbVector originOld = plane.getColumn(4);
		plane.set(movedGeoPoint3D.getCoords(), 4);
		GgbVector originProjected = originOld.projectPlane(plane)[0];
		plane.set(originProjected, 4);
		 
		GgbVector[] project = o.projectPlaneThruV(plane, v);
		
		
		/*
		mouseLoc3D = project[0];
		movedGeoPoint3D.translate(mouseLoc3D.sub(startLoc3D));
		startLoc3D = mouseLoc3D.copyVector();
		*/
		movedGeoPoint3D.setCoords(project[0]);
		
		//TODO modify objSelected.updateRepaint()
		objSelected.updateCascade();
		view.setMovingCorners(0, 0, project[1].get(1), project[1].get(2));
		view.setMovingProjection(movedGeoPoint3D.getCoords(),vn);
		
		
		if (repaint){
			view.setWaitForUpdate(true);
			movedGeoPoint3D.updateRepaint();//for highlighting in algebraView
		}
		
		
	}




	public void mouseClicked(MouseEvent e) {
		//Application.debug("mouseClicked");
		setMouseLocation(e);
		view.rendererPick(mouseLoc.x,mouseLoc.y);

	}




	public void mouseEntered(MouseEvent mouseEvent) {
		Component component = mouseEvent.getComponent();
	    if (!component.hasFocus()) {
	      component.requestFocusInWindow();
	    }
	    
	    
	    
	    view.setWaitForUpdate(true);
		
	}




	public void mouseExited(MouseEvent arg0) {
		// TODO Raccord de méthode auto-généré
		
	}




	public void mouseReleased(MouseEvent arg0) {
		
		
		switch (moveMode) {
		case MOVE_VIEW:
		default:
			break;

		case MOVE_POINT:
			view.setMovingVisible(false);
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
		pick(true);
		//view.rendererPick(mouseLoc.x,mouseLoc.y);
		view.setWaitForUpdate(true);
	}

	
	/** pick object under the mouse */
	public void pick(boolean repaint){
		//Application.debug("mouse=("+mouseLoc.x+","+mouseLoc.y+")");
		pickPoint=view.getPickPoint(mouseLoc.x,mouseLoc.y);
		view.doPick(pickPoint, repaint);
	}


	
	
	
	
	
	
	
	
	
	
	
	

	public void mouseWheelMoved(MouseWheelEvent e) {
		
		double r = e.getWheelRotation();

		switch (moveMode) {
		case MOVE_VIEW:
		default:
			view.setZZero(view.getZZero()+ r/10.0);
			view.updateMatrix();
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
			view.setWaitForUpdate(true);
			movedGeoPoint3D.updateRepaint();//for highlighting in algebraView

			
			
			break;	
		
		
		}
	
		
		

	}
	
	
	
	final protected void setMouseLocation(MouseEvent e) {

		mouseLocOld = (Point) mouseLoc.clone();
		mouseLoc = e.getPoint();

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
			Application.debug("shift released");
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
			view.setWaitForUpdate(true);
			break;	
			
		case MOVE_NONE:
		default:
			break;
		
		}
		
	}

	
	
	
	
	
	


	public void keyTyped(KeyEvent arg0) {
		// TODO Raccord de méthode auto-généré
		
	}
	
	
}
