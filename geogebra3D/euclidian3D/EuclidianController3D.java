package geogebra3D.euclidian3D;


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
	protected GeoPoint3D movedGeoPoint3D = null;
	
	
	protected EuclidianView3D view;
	protected Kernel kernel;
	
	
	
	
	protected Point mouseLoc = new Point();
	protected Point startLoc = new Point();
	
	protected GgbVector mouseLoc3D, startLoc3D;
	
	//picking
	protected GgbVector pickPoint;
	
	//moving
	protected GgbVector v1, v2, vn;// = new GgbVector(new double[] {0.0,0.0,1.0,0.0});
	
	
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
				
				if (objSelected.getGeoClassType()==GeoElement3D.GEO_CLASS_POINT3D){
					moveMode = MOVE_POINT;
					movedGeoPoint3D = (GeoPoint3D) objSelected;
					startLoc3D = movedGeoPoint3D.getCoords().copyVector(); 
					
					v1=view.vx;
					v2=view.vy;
					vn=view.vz;
					
					view.setMovingPlane(startLoc3D,v1,v2,0f,0f,1f);
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
		
			
		GgbVector o2 = view.eye.copyVector(); 
		view.toSceneCoords3D(o2);

		GgbVector v = (view.getPickPoint(mouseLoc.x,mouseLoc.y)).sub(view.eye); 
		view.toSceneCoords3D(v);	

		double l = (startLoc3D.sub(o2)).dotproduct(vn)/(v.dotproduct(vn));

		mouseLoc3D = (o2.add(v.mul(l))).getColumn(1);
		movedGeoPoint3D.translate(mouseLoc3D.sub(startLoc3D));
		startLoc3D = mouseLoc3D.copyVector();
						
		
		
		if (repaint){
			//objSelected.updateRepaint(); //TODO modify updateRepaint()
			objSelected.updateCascade();
			view.setMovingPlane(movedGeoPoint3D.getCoords(), v1, v2);
			view.repaint();
		}else
			objSelected.updateCascade();		
		
		
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

		switch (moveMode) {
		case MOVE_VIEW:
		default:
			view.setZZero(view.getZZero()+ r/10.0);
			view.updateMatrix();
			view.repaint();
			break;

		case MOVE_POINT:
			//p = p + r*z			
			GgbVector p1 = movedGeoPoint3D.getCoords(); 
			p1.set(3,p1.get(3)-r*0.1);
			movedGeoPoint3D.setCoords(p1);
			

			//objSelected.updateRepaint(); //TODO modify updateRepaint()
			objSelected.updateCascade();
			view.setMovingPlane(movedGeoPoint3D.getCoords(), v1, v2);
			view.repaint();

			break;	
			
		
		
		}
	
		
		

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
