package geogebra3D.euclidian3D;


import geogebra.Application;
import geogebra.kernel.Kernel;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPoint3D;

import java.awt.AWTException;
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
	
	
	
	
	protected static final int MOVE_NONE = 101;
	protected static final int MOVE_POINT = 102;
	protected static final int MOVE_POINT_WHEEL = 3102;
	protected static final int MOVE_VIEW = 106;
	
	
	protected int moveMode = MOVE_NONE;
	
	
	
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
	protected GgbVector[] v1list = {EuclidianView3D.vx,EuclidianView3D.vy,EuclidianView3D.vz};
	protected GgbVector[] v2list = {EuclidianView3D.vy,EuclidianView3D.vz,EuclidianView3D.vx};
	protected GgbVector[] vnlist = {EuclidianView3D.vz,EuclidianView3D.vx,EuclidianView3D.vy};
	protected float[] movingPlaneRlist = {0f,1f,0f};
	protected float[] movingPlaneGlist = {0f,0f,1f};
	protected float[] movingPlaneBlist = {1f,0f,0f};

	protected GgbVector v1=v1list[movingPlane];
	protected GgbVector v2=v2list[movingPlane];
	protected GgbVector vn=vnlist[movingPlane];
	protected float movingPlaneR=movingPlaneRlist[movingPlane];
	protected float movingPlaneG=movingPlaneGlist[movingPlane];
	protected float movingPlaneB=movingPlaneBlist[movingPlane];
	
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
	
	
	
	
	
	
	
	
	public void mousePressed(MouseEvent e) {
		
		setMouseLocation(e);	
		moveMode = MOVE_NONE;
		
		if (e.isShiftDown() || e.isControlDown() || e.isMetaDown()) {
			moveMode = MOVE_VIEW;	
			if(DEBUG){Application.debug("mousePressed");}
			aOld = view.a;
			bOld = view.b;	
			startLoc.x = mouseLoc.x;
			startLoc.y = mouseLoc.y;	
			
		} else {
			
			if (objSelected!=null)
				objSelected.setSelected(false);
			pickPoint=view.getPickPoint(mouseLoc.x,mouseLoc.y);
			view.doPick(pickPoint,true);
			if (!view.hits.isEmpty()){
				objSelected = (GeoElement3D) view.hits.get(0);		
				objSelected.setSelected(true);
				//Application.debug("selected = "+objSelected.getLabel());
				
				if (objSelected.getGeoClassType()==GeoElement3D.GEO_CLASS_POINT3D){
					moveMode = MOVE_POINT;
					movedGeoPoint3D = (GeoPoint3D) objSelected;
					startLoc3D = movedGeoPoint3D.getCoords().copyVector(); 
										
					view.setMovingPlane(startLoc3D,v1,v2,movingPlaneR,movingPlaneG,movingPlaneB);					
				}
			}
			view.repaint();
			
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
		
		//projecting eye on movingPlane thru pickPoint direction	
		GgbVector o = view.eye.copyVector(); 
		view.toSceneCoords3D(o);
		
		//getting current pick point and direction eye-to-pick point 
		GgbVector v = (view.getPickPoint(mouseLoc.x,mouseLoc.y)).sub(view.eye); //supposes that point is under the mouse pointer
				
		/*
		Application.debug("mouseLoc    = "+mouseLoc.x+","+mouseLoc.y
						 +"\nmouseLocOld = "+mouseLocOld.x+","+mouseLocOld.y);
		GgbVector p1 = movedGeoPoint3D.getCoords().copyVector();
		view.toScreenCoords3D(p1);
		GgbVector p2 = view.getScreenCoords(p1);
		Application.debug("point on screen = "+p2.get(1)+","+p2.get(2));
		GgbVector v = (view.getPickPoint(((int) p2.get(1))+mouseLoc.x-mouseLocOld.x,
										 ((int) p2.get(2))+mouseLoc.y-mouseLocOld.y))
											.sub(view.eye);
		*/
		
		
		view.toSceneCoords3D(v);	
		
		//getting new position of the point
		double l = o.projectPlaneThruV(view.movingPlane.getMatrixCompleted(), v).get(3);
		
		
		mouseLoc3D = (o.add(v.mul(l))).v();
		movedGeoPoint3D.translate(mouseLoc3D.sub(startLoc3D));
		startLoc3D = mouseLoc3D.copyVector();
		
		//TODO modify objSelected.updateRepaint()
		objSelected.updateCascade();
		view.setMovingPlane(movedGeoPoint3D.getCoords(), v1, v2);
		
		if (repaint)
			view.repaint();
		
		
	}




	public void mouseClicked(MouseEvent arg0) {
		if(DEBUG){Application.debug("mouseClicked");}
		
	}




	public void mouseEntered(MouseEvent mouseEvent) {
		Component component = mouseEvent.getComponent();
	    if (!component.hasFocus()) {
	      component.requestFocusInWindow();
	    }
	    
	    
	    
		view.repaint();
		
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
			view.setMovingPlaneVisible(false);
			view.repaint();
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
		pick();
		view.repaint();
	}

	
	/** pick object under the mouse */
	public void pick(){
		//Application.debug("mouse=("+mouseLoc.x+","+mouseLoc.y+")");
		pickPoint=view.getPickPoint(mouseLoc.x,mouseLoc.y);
		view.doPick(pickPoint);
	}


	
	
	
	
	
	
	
	
	
	
	
	

	public void mouseWheelMoved(MouseWheelEvent e) {
		
		double r = e.getWheelRotation();

		switch (moveMode) {
		case MOVE_VIEW:
		default:
			view.setZZero(view.getZZero()+ r/10.0);
			view.updateMatrix();
			view.repaint();
			break;

		case MOVE_POINT:
		case MOVE_POINT_WHEEL:
			
			//p = p + r*vn			
			GgbVector p1 = movedGeoPoint3D.getCoords().add(vn.mul(-r*0.1)).v(); 
			movedGeoPoint3D.setCoords(p1);
			
			
			
			//mouse follows the point
			
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
		    
			

			//objSelected.updateRepaint(); //TODO modify updateRepaint()
			objSelected.updateCascade();
			view.setMovingPlane(movedGeoPoint3D.getCoords(), v1, v2);
			view.repaint();
			//moveMode = MOVE_POINT;
			
			
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
			movingPlaneR=movingPlaneRlist[movingPlane];
			movingPlaneG=movingPlaneGlist[movingPlane];
			movingPlaneB=movingPlaneBlist[movingPlane];			
			
			view.setMovingPlane(movedGeoPoint3D.getCoords(),v1,v2,movingPlaneR,movingPlaneG,movingPlaneB);
			view.repaint();
			break;	
			
		case MOVE_NONE:
		default:
			break;
		
		}
		
	}

	
	
	
	/*
	public void keyAltPressed(){
		
		switch (moveMode) {
		case MOVE_VIEW:
			break;

		case MOVE_POINT:			
			v1=view.vz;
			v2=view.vx;
			vn=view.vy;
			
			view.setMovingPlane(movedGeoPoint3D.getCoords(),v1,v2,0f,1f,0f);
			view.repaint();
			break;	
			
		case MOVE_NONE:
		default:
			break;
		
		}
		
	}
	

	
	
	public void keyAltReleased(){
		
		switch (moveMode) {
		case MOVE_VIEW:
			break;

		case MOVE_POINT:			
			v1=view.vx;
			v2=view.vy;
			vn=view.vz;
			
			view.setMovingPlane(movedGeoPoint3D.getCoords(),v1,v2,0f,0f,1f);
			view.repaint();
			break;	
			
		case MOVE_NONE:
		default:
			break;
		
		}
		
	}
	
	*/
	



	
	
	
	
	
	
	
	


	public void keyTyped(KeyEvent arg0) {
		// TODO Raccord de méthode auto-généré
		
	}
	
	
}
