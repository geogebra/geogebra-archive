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

package geogebra.algebra;

import geogebra.Application;
import geogebra.GeoGebra;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;

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

	private GeoVector tempVec;
	private boolean kernelChanged;

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
			if (app.hasApplicationGUImanager())
				app.getApplicationGUImanager().getAlgebraInput().setFocus(); 
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
			app.getApplicationGUImanager().showRenameDialog(geo, true, Character.toString(ch), false);					
		}
	}

	/** handle function keys and delete key */
	public void keyPressed(KeyEvent event) {
		if (keyPressedConsumed(event))
			event.consume();		
	}

	/**
	 * Handle pressed key and returns whether event was
	 * consumed.
	 */
	public boolean keyPressedConsumed(KeyEvent event) {
		//Object src = event.getSource();
		//Application.debug("source: " + src);
		//if (src != view) return;					
		
		boolean consumed = false;
		int keyCode = event.getKeyCode();		
		
		switch (keyCode) {
			// ESCAPE: clear all selections in views
			case KeyEvent.VK_ESCAPE:
				app.clearSelectedGeos();
				app.getEuclidianView().reset();
				consumed = true;
				break;			   
			
			default:
				//	handle selected GeoElements
				ArrayList geos = app.getSelectedGeos();
				for (int i = 0; i < geos.size(); i++) {
					GeoElement geo = (GeoElement) geos.get(i);
					consumed = handleKeyPressed(event, geo) || consumed;
				}		
				if (consumed) kernelChanged = true;
		}								
		

		// something was done in handleKeyPressed
		if (consumed) {			
			app.setUnsaved();									
		}
		return consumed;
	}

	// handle pressed key
	private boolean handleKeyPressed(KeyEvent event, GeoElement geo) {
		if (geo == null)
			return false;

		if (tempVec == null)
			tempVec = new GeoVector(cons);
		
		int keyCode = event.getKeyCode();
		// SPECIAL KEYS			
		int changeVal = 0; //	later:  changeVal = base or -base			
		// Ctrl : base = 10
		// Alt : base = 100
		int base = 1;
		if (event.isControlDown())
			base = 10;
		if (event.isAltDown())
			base = 100;

		// ARROW KEYS
		boolean moved = false;
		switch (keyCode) {								
			case KeyEvent.VK_UP :
				changeVal = base;
				tempVec.setCoords(0.0, changeVal * geo.animationStep, 0.0);
				moved = handleArrowKeyMovement(geo, tempVec);		
				break;

			case KeyEvent.VK_DOWN :
				changeVal = -base;
				tempVec.setCoords(0.0, changeVal * geo.animationStep, 0.0);
				moved = handleArrowKeyMovement(geo, tempVec);				
				break;

			case KeyEvent.VK_RIGHT :
				changeVal = base;
				tempVec.setCoords(changeVal * geo.animationStep, 0.0, 0.0);
				moved = handleArrowKeyMovement(geo, tempVec);
				break;

			case KeyEvent.VK_LEFT :
				changeVal = -base;
				tempVec.setCoords(changeVal * geo.animationStep, 0.0, 0.0);
				moved = handleArrowKeyMovement(geo, tempVec);				
				break;

			case KeyEvent.VK_F2 :
				view.startEditing(geo);				
				return true;				
		}
		
		if (moved) return true;
		
	
		// PLUS, MINUS keys	
		switch (keyCode) {
			case KeyEvent.VK_PLUS :
			case KeyEvent.VK_ADD :
			case KeyEvent.VK_UP :
			case KeyEvent.VK_RIGHT :
				changeVal = base;
				break;

			case KeyEvent.VK_MINUS :
			case KeyEvent.VK_SUBTRACT :
			case KeyEvent.VK_DOWN :
			case KeyEvent.VK_LEFT :
				changeVal = -base;
				break;
		}
		
		if (changeVal == 0) {
			char keyChar = event.getKeyChar();
			if (keyChar == '+')
				changeVal = base;
			else if (keyChar == '-')
				changeVal = -base;
		}
	
		if (changeVal != 0) {								
			if (geo.isChangeable()) {
				if (geo.isNumberValue()) {
					GeoNumeric num = (GeoNumeric) geo;
					num.setValue(kernel.checkInteger(
							num.getValue() + changeVal * num.animationStep));					
					num.updateRepaint();
				} else if (geo.isGeoPoint()) {
					GeoPoint p = (GeoPoint) geo;
					if (p.hasPath()) {						
						p.addToPathParameter(changeVal * p.animationStep);
						p.updateRepaint();
					}
				}
			} 
			
			// update random algorithms
			if (!geo.isIndependent()) {
				if (geo.getParentAlgorithm().updateRandomAlgorithm())
					geo.updateRepaint();				
			}	
			
			return true;
		}		
		
		
		
		return false;
	}

	
	private boolean handleArrowKeyMovement(GeoElement geo, GeoVector vec) {
		// try to move objvect
		
		boolean moved = !geo.isGeoNumeric() && geo.moveObject(tempVec);				
		if (!moved) {	
			// toggle boolean value
			if (geo.isChangeable() && geo.isGeoBoolean()) {
				GeoBoolean bool = (GeoBoolean) geo;
				bool.setValue(!bool.getBoolean());
				bool.updateCascade();
				moved = true;
			}					
		}	
		
		if (moved) 
			kernel.notifyRepaint();
		
		return moved;
	}
	


	
	/*
	 * MouseListener implementation for popup menus
	 */

	public void mouseClicked(java.awt.event.MouseEvent e) {	
		view.cancelEditing();
		
		if (kernelChanged) {
			app.storeUndoInfo();
			kernelChanged = false;
		}
		
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
				app.getApplicationGUImanager().showPopupMenu(geo, view, e.getPoint());						
			} 
			// multiple selection: properties dialog
			else {														
				app.getApplicationGUImanager().showPropertiesDialog(app.getSelectedGeos());	
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
		if (kernelChanged) {
			app.storeUndoInfo();
			kernelChanged = false;
		}
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
