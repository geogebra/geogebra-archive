/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/**
 * AlgebraController.java
 *
 * Created on 05. September 2001, 09:11
 */

package geogebra.gui.view.algebra;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.tree.TreePath;

public class AlgebraController
	implements MouseListener, MouseMotionListener {
	private Kernel kernel;
	private Application app;
	
	private AlgebraView view;

	//private GeoVector tempVec;
	//private boolean kernelChanged;

	/** Creates new CommandProcessor */
	public AlgebraController(Kernel kernel) {
		this.kernel = kernel;
		app = kernel.getApplication();		
	}

	void setView(AlgebraView view) {
		this.view = view;
	}

	Application getApplication() {
		return app;
	}

	Kernel getKernel() {
		return kernel;
	}
	
	/*
	 * MouseListener implementation for popup menus
	 */

	public void mouseClicked(java.awt.event.MouseEvent e) {	
		// right click is consumed in mousePressed
		if (e.isConsumed()) return;
		
		// get GeoElement at mouse location		
		TreePath tp = view.getPathForLocation(e.getX(), e.getY());
		GeoElement geo = AlgebraView.getGeoElementForPath(tp);	
	
		// check if we clicked on the 16x16 show/hide icon
		if (geo != null) {
			Rectangle rect = view.getPathBounds(tp);		
			boolean iconClicked = rect != null && e.getX() - rect.x < 16; // distance from left border				
			if (iconClicked) {
				// icon clicked: toggle show/hide
				geo.setEuclidianVisible(!geo.isSetEuclidianVisible());
				geo.update();
				app.storeUndoInfo();
				kernel.notifyRepaint();
				return;
			}		
		}
		
		// check double click
		int clicks = e.getClickCount();
		EuclidianView ev = app.getEuclidianView();
		if (clicks == 2) {										
			app.clearSelectedGeos();
			ev.resetMode();
			if (geo != null && !Application.isControlDown(e)) {
				view.startEditing(geo, e.isShiftDown());						
			}
			return;
		} 	
		
		int mode = ev.getMode();
		if (mode == EuclidianView.MODE_MOVE ) {
			// update selection	
			if (geo == null)
				app.clearSelectedGeos();
			else {					
				// handle selecting geo
				if (Application.isControlDown(e)) {
					app.toggleSelectedGeo(geo); 													
//					app.geoElementSelected(geo, true);
				} else {							
					app.clearSelectedGeos();
					app.addSelectedGeo(geo);
//					app.geoElementSelected(geo, false);
				}
			}
		} 
		else if (mode != EuclidianView.MODE_SELECTION_LISTENER) {
			// let euclidianView know about the click
			ev.clickedGeo(geo, e);
		}
		
		// Alt click: copy definition to input field
		if (geo != null && e.isAltDown() && app.showAlgebraInput()) {			
			// F3 key: copy definition to input bar
			app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(3, geo);			
		}
		
		ev.mouseMovedOver(null);		
	}

	public void mousePressed(java.awt.event.MouseEvent e) {
		view.cancelEditing();

		boolean rightClick = Application.isRightClick(e);
		
		// RIGHT CLICK
		if (rightClick) {
			e.consume();
			
			// get GeoElement at mouse location		
			TreePath tp = view.getPathForLocation(e.getX(), e.getY());
			GeoElement geo = AlgebraView.getGeoElementForPath(tp);	
			if (geo == null) return;
			
			if (!app.containsSelectedGeo(geo)) {
				app.clearSelectedGeos();					
			}
														
			// single selection: popup menu
			if (app.selectedGeosSize() < 2) {				
				app.getGuiManager().showPopupMenu(geo, view, e.getPoint());						
			} 
			// multiple selection: properties dialog
			else {														
				app.getGuiManager().showPropertiesDialog(app.getSelectedGeos());	
			}	
		}
	}

	public void mouseReleased(java.awt.event.MouseEvent e) {
	}

	public void mouseEntered(java.awt.event.MouseEvent p1) {
	}

	public void mouseExited(java.awt.event.MouseEvent p1) {		
	}

	// MOUSE MOTION LISTENER
	public void mouseDragged(MouseEvent arg0) {}

	// tell EuclidianView
	public void mouseMoved(MouseEvent e) {		
		if (view.isEditing())
			return;
		
		int x = e.getX();
		int y = e.getY();

		GeoElement geo = AlgebraView.getGeoElementForLocation(view, x, y);
		EuclidianView ev = app.getEuclidianView();
		
		// tell EuclidianView to handle mouse over
		ev.mouseMovedOver(geo);								
		if (geo != null) {
			view.setToolTipText(geo.getLongDescriptionHTML(true, true));				
		} else
			view.setToolTipText(null);						
	}
	
}
