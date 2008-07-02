package geogebra3D.euclidian3D;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPoint3D;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class EuclidianController3D implements MouseListener, MouseMotionListener, MouseWheelListener {

	static final boolean DEBUG = false; //conditionnal compilation
	
	
	
	
	protected static final int MOVE_NONE = 101;
	protected static final int MOVE_POINT = 102;
	protected static final int MOVE_VIEW = 106;
	
	
	protected int moveMode = MOVE_NONE;
	
	protected GeoElement3D objSelected = null;
	
	
	protected EuclidianView3D view;
	protected Kernel kernel;
	
	
	
	
	protected Point mouseLoc = new Point();
	protected Point startLoc = new Point();
	
	//picking
	protected GgbVector pickPoint;
	
	//moving
	protected GgbVector Vn = new GgbVector(new double[] {0.0,0.0,1.0,0.0});
	
	
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
		moveMode = MOVE_NONE;
		
		if (e.isShiftDown() || e.isControlDown() || e.isMetaDown()) {
			moveMode = MOVE_VIEW;	
			if(DEBUG){System.out.println("mousePressed");}
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
				//System.out.println("selected = "+objSelected.getLabel());
				moveMode = MOVE_POINT;
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
			break;

		case MOVE_VIEW:
			if (repaint) {
				if(DEBUG){System.out.println("MOVE_VIEW : mouseLoc.x="+mouseLoc.x+"  startLoc.x="+startLoc.x);}
				double dx = (double) mouseLoc.x - startLoc.x;
				double dy = (double) mouseLoc.y - startLoc.y;
				view.setRotXY(aOld+dx*ANGLE_SCALE,bOld+dy*ANGLE_SCALE,true);
			}
			break;	
			
		default: // do nothing
		
		}

		
	}
	
	
	
	protected void movePoint(boolean repaint){
		
		if (objSelected!=null){
			//System.out.println("movePoint");
			GeoPoint3D p = (GeoPoint3D) objSelected;
			GgbVector o1 = p.getCoords(); view.toScreenCoords3D(o1);
			GgbVector o2 = view.eye.copyVector(); //view.toScreenCoords3D(o1);
			GgbVector v1 = (view.getPickPoint(mouseLoc.x,mouseLoc.y)).sub(view.eye); 
			GgbVector v = v1.copyVector(); //view.toScreenCoords3D(v);			
			GgbVector Vn2 = Vn.copyVector(); //view.toScreenCoords3D(Vn2);
			
			/*
			System.out.println("v1 = ");v1.SystemPrint();
			System.out.println("v = ");v.SystemPrint();
			System.out.println("Vn2 = ");Vn2.SystemPrint();
			*/
			
			double l = (o1.sub(o2)).dotproduct(Vn2)/(v.dotproduct(Vn2));
			
			//System.out.println("lambda = "+l);
			GgbVector p1 = (view.eye.add(v1.mul(l))).getColumn(1);
			view.toSceneCoords3D(p1);
			p.setCoords(p1);
						
		}
		
		if (repaint)
			view.repaint();
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
