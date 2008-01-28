package geogebra.euclidian3D;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

public class EuclidianController3D 
	extends EuclidianController{
	
	
	protected EuclidianView3D view;
	
	//scale factor for changing angle of view : 2Pi <-> 100 pixels 
	final double ANGLE_SCALE = 2*Math.PI/100f;
	
	
	double aOld, bOld;
	

	public EuclidianController3D(Kernel kernel) {
		super(kernel);
	}
	
	
	
	
	void setView(EuclidianView3D view) {
		this.view = view;
		System.out.println("setView -> 3D");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public void mousePressed(MouseEvent e) {
		
		System.out.println("EC3D mousePressed");
		aOld = view.a;
		bOld = view.b;		
		
		//TODO in switch(mode)
		/*
		switch (mode) {
		// move drawing pad or axis
		case EuclidianView.MODE_TRANSLATEVIEW:			
			aOld = view.a;
			bOld = view.b;		
			break;								
				
		default:
			break;	 
		}
		*/
	
		super.mousePressed(e);
	}	
	
	
	protected void handleMouseDragged(boolean repaint) {
		
		switch (moveMode) {
		case MOVE_VIEW:
			if (repaint) {
				if (TEMPORARY_MODE) view.setMoveCursor();
				//view.setCoordSystem(xZeroOld + mouseLoc.x - startLoc.x, yZeroOld + mouseLoc.y - startLoc.y, view.getXscale(), view.getYscale());
				
				System.out.println("MOVE_VIEW : mouseLoc.x="+mouseLoc.x+"  startLoc.x="+startLoc.x);
				double dx = (double) mouseLoc.x - startLoc.x;
				double dy = (double) mouseLoc.y - startLoc.y;
				System.out.println("dx*ANGLE_SCALE="+dx*ANGLE_SCALE);
				view.setRotXY(aOld+dx*ANGLE_SCALE,bOld+dy*ANGLE_SCALE,true);
			}
			break;	
		default:
			super.handleMouseDragged(repaint);
			break;
		}
		
	}

}
