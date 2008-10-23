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
import geogebra.gui.app.GeoGebraFrame;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class AlgebraController
	implements KeyListener, MouseListener, MouseMotionListener {
	private Kernel kernel;
	private Construction cons;
	private Application app;
	
	private AlgebraView view;

	//private GeoVector tempVec;
	//private boolean kernelChanged;

	/** Creates new CommandProcessor */
	public AlgebraController(Kernel kernel) {
		this.kernel = kernel;
		this.cons = kernel.getConstruction();
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

	/**
	* KeyListener implementation for AlgebraView
	*/
	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent event) {
		// we open the rename dialog when a letter is typed
		
		char ch = event.getKeyChar();
		
		
		// Michael Borcherds 2008-03-22 give focus to input bar if <enter> pressed
		if (ch == KeyEvent.VK_ENTER)
		{
			if (app.showAlgebraInput())
				app.getGuiManager().getAlgebraInput().requestFocus(); 
			return;
		}
		
		// we want both of these to work on Mac and Windows
		// although only one is displayed as a shortcut in the Edit menu
		if (ch == KeyEvent.VK_DELETE || ch == KeyEvent.VK_BACK_SPACE)
		{
			app.deleteSelectedObjects();			
		}
		
		if (!Character.isLetter(ch) || 
			 event.isMetaDown() ||
			 event.isAltDown() ||
			 event.isControlDown()) return;		
		
		GeoElement geo;					
		if (app.selectedGeosSize() == 1) {
			// selected geo
			geo = (GeoElement) app.getSelectedGeos().get(0);										
		}				
		else {
			// last created geo
			geo = app.getLastCreatedGeoElement();			
		}	
		
		// open rename dialog
		if (geo != null) {							
			app.getGuiManager().showRenameDialog(geo, true, Character.toString(ch), false);					
		}
	}

	/** handle function keys and delete key */
	public void keyPressed(KeyEvent event) {
		if (app.keyPressedConsumed(event))
			event.consume();		
	}

	


	
	/*
	 * MouseListener implementation for popup menus
	 */

	public void mouseClicked(java.awt.event.MouseEvent e) {	
		view.cancelEditing();
		
		//if (kernelChanged) {
		//	app.storeUndoInfo();
		//	kernelChanged = false;
		//}
		
		boolean rightClick = Application.isRightClick(e);
		
		
		// LEFT CLICK
		if (!rightClick) {
			int x = e.getX();
			int y = e.getY();		
			if (view.hitClosingCross(x, y)) {			
				app.setWaitCursor();
				app.setShowAlgebraView(false);
				app.updateCenterPanel(true);
				app.setDefaultCursor();
				return;				
			}
			
			GeoElement geo = AlgebraView.getGeoElementForLocation(view, x, y);			
			EuclidianView ev = app.getEuclidianView();
			if (ev.getMode() == EuclidianView.MODE_MOVE) {
				// double click to edit
				int clicks = e.getClickCount();
				if (clicks == 2) {
					app.clearSelectedGeos();
					if (geo != null) {
						view.startEditing(geo);						
					}
				} 										
			} else {
				if (geo == null) {				
					app.clearSelectedGeos();
				} else {
					ev.clickedGeo(geo, e);
				}
			}

			ev.mouseMovedOver(null);			
		}
	}

	public void mousePressed(java.awt.event.MouseEvent e) {
		
		boolean rightClick = Application.isRightClick(e);
		
		// RIGHT CLICK
		if (rightClick) {
			GeoElement geo = AlgebraView.getGeoElementForLocation(view, e.getX(), e.getY());
			
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
		
		// left click
		else {
			int x = e.getX();
			int y = e.getY();	
			GeoElement geo = AlgebraView.getGeoElementForLocation(view, x, y);			
			EuclidianView ev = app.getEuclidianView();
			if (ev.getMode() == EuclidianView.MODE_MOVE) {		
				if (geo == null)
					app.clearSelectedGeos();
				else {						
					if (Application.isControlDown(e)) {
						app.toggleSelectedGeo(geo); 													
					} else {							
						app.clearSelectedGeos();
						app.addSelectedGeo(geo);
					}		
				}	
			}
		}
		
	}

	public void mouseReleased(java.awt.event.MouseEvent e) {

	}

	public void mouseEntered(java.awt.event.MouseEvent p1) {
		view.setClosingCrossHighlighted(false);
	}

	public void mouseExited(java.awt.event.MouseEvent p1) {		
		view.setClosingCrossHighlighted(false);
		//if (kernelChanged) {
		//	app.storeUndoInfo();
		//	kernelChanged = false;
		//}
	}

	// MOUSE MOTION LISTENER
	public void mouseDragged(MouseEvent arg0) {}

	// tell EuclidianView
	public void mouseMoved(MouseEvent e) {		
		if (view.isEditing())
			return;
		
		int x = e.getX();
		int y = e.getY();
		if (view.hitClosingCross(x, y)) {
			view.setClosingCrossHighlighted(true);
		} else {
			view.setClosingCrossHighlighted(false);
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

	
}
