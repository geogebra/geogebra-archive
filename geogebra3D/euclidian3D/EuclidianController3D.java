package geogebra3D.euclidian3D;

import geogebra.kernel.Kernel;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.euclidian3D.EuclidianView3D;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class EuclidianController3D implements MouseListener, MouseMotionListener, MouseWheelListener {

	static final boolean DEBUG = false; //conditionnal compilation
	
	
	protected EuclidianView3D view;
	protected Kernel kernel;
	
	
	
	
	protected Point mouseLoc = new Point();
	protected Point startLoc = new Point();
	
	//picking
	protected GgbVector pickPoint;
	
	
	//scale factor for changing angle of view : 2Pi <-> 300 pixels 
	final double ANGLE_SCALE = 2*Math.PI/300f;


	
	double aOld, bOld;
	

	public EuclidianController3D(Kernel kernel) {
		this.kernel = kernel;
		
	}
	
	
	
	
	void setView(EuclidianView3D view) {
		this.view = view;
		//System.out.println("setView -> 3D");
	}
	
	
	
	
	Kernel getKernel() {
		return kernel;
	}
	
	
	
	
	
	
	
	
	public void mousePressed(MouseEvent e) {
		
		setMouseLocation(e);		
		if(DEBUG){System.out.println("mousePressed");}
		aOld = view.a;
		bOld = view.b;	
		startLoc.x = mouseLoc.x;
		startLoc.y = mouseLoc.y;
	
	}	
	
	
	protected void handleMouseDragged(boolean repaint) {
		
		//TODO view.setMoveCursor();
		
		if(DEBUG){System.out.println("MOVE_VIEW : mouseLoc.x="+mouseLoc.x+"  startLoc.x="+startLoc.x);}
		double dx = (double) mouseLoc.x - startLoc.x;
		double dy = (double) mouseLoc.y - startLoc.y;
		//if(DEBUG){System.out.println("dx*ANGLE_SCALE="+dx*ANGLE_SCALE);}
		view.setRotXY(aOld+dx*ANGLE_SCALE,bOld+dy*ANGLE_SCALE,true);
		
	}




	public void mouseClicked(MouseEvent arg0) {
		if(DEBUG){System.out.println("mouseClicked");}
		
	}




	public void mouseEntered(MouseEvent arg0) {
		
		view.repaint();
		
	}




	public void mouseExited(MouseEvent arg0) {
		// TODO Raccord de méthode auto-généré
		
	}




	public void mouseReleased(MouseEvent arg0) {
		// TODO Raccord de méthode auto-généré
		
	}




	public void mouseDragged(MouseEvent e) {
		if(DEBUG){System.out.println("mouseDragged");}
		setMouseLocation(e);
		handleMouseDragged(true);	
	}

	
	
	
	
	
	
	
	



	public void mouseMoved(MouseEvent e) {
		setMouseLocation(e);
		pick();
		view.repaint();
	}

	
	/** pick object under the mouse */
	public void pick(){
		//System.out.println("mouse=("+mouseLoc.x+","+mouseLoc.y+")");
		pickPoint=view.getPickPoint(mouseLoc.x,mouseLoc.y);
		view.doPick(pickPoint);
	}


	
	
	
	
	
	
	
	
	
	
	
	

	public void mouseWheelMoved(MouseWheelEvent e) {
		
		double r = e.getWheelRotation();
		//System.out.println("mouseWheelMoved : "+r);
		
		view.setZZero(view.getZZero()+ r/10.0);
		view.updateMatrix();
		view.repaint();
	}
	
	
	
	final protected void setMouseLocation(MouseEvent e) {
		mouseLoc = e.getPoint();
		
		/*
		if (mouseLoc.x < 0)
			mouseLoc.x = 0;		
		else if (mouseLoc.x > view.width)
			mouseLoc.x = view.width;
		if (mouseLoc.y < 0)
			mouseLoc.y = 0;
		else if (mouseLoc.y > view.height)
			mouseLoc.y = view.height;
		*/
	}
	
	
}
