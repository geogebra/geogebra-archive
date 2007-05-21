/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/**
 * AlgebraController.java
 *
 * Created on 05. September 2001, 09:11
 */

package geogebra.algebra;

import geogebra.Application;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.Translateable;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
		if (!Character.isLetter(ch)) return;		
		
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
			geo.setLabelVisible(true);
			geo.updateRepaint();
			app.showRenameDialog(geo, true, Character.toString(ch));					
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
		//System.out.println("source: " + src);
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
			
		    // delete selected geos
			case KeyEvent.VK_DELETE:
				if (app.letDelete()) {
					Object [] geos = app.getSelectedGeos().toArray();
					for (int i=0; i < geos.length; i++) {
						GeoElement geo = (GeoElement) geos[i];
						geo.remove();
					}
					app.storeUndoInfo();
					consumed = true;
				}
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
		switch (keyCode) {								
			case KeyEvent.VK_UP :
				changeVal = base;
				if (geo.isChangeable()) {
					if (geo.isTranslateable()) {				
						tempVec.setCoords(0.0, changeVal * geo.animationStep, 0.0);
						((Translateable) geo).translate(tempVec);
						geo.updateRepaint();
						return true;
					}
					else if (geo.isGeoBoolean()) {
						GeoBoolean bool = (GeoBoolean) geo;
						bool.setValue(!bool.getBoolean());
						geo.updateRepaint();
						return true;
					}					
				}				
				break;

			case KeyEvent.VK_DOWN :
				changeVal = -base;
				if (geo.isChangeable()) {
					if (geo.isTranslateable()) {
						tempVec.setCoords(0.0, changeVal * geo.animationStep, 0.0);
						((Translateable) geo).translate(tempVec);	
						geo.updateRepaint();
						return true;
					}
					else if (geo.isGeoBoolean()) {
						GeoBoolean bool = (GeoBoolean) geo;
						bool.setValue(!bool.getBoolean());
						geo.updateRepaint();
						return true;
					}					
				}
				break;

			case KeyEvent.VK_RIGHT :
				changeVal = base;
				if (geo.isChangeable() && geo.isTranslateable()) {
					tempVec.setCoords(changeVal * geo.animationStep, 0.0, 0.0);
					((Translateable) geo).translate(tempVec);
					geo.updateRepaint();
					return true;
				}
				break;

			case KeyEvent.VK_LEFT :
				changeVal = -base;
				if (geo.isChangeable() && geo.isTranslateable()) {
					tempVec.setCoords(changeVal * geo.animationStep, 0.0, 0.0);
					((Translateable) geo).translate(tempVec);
					geo.updateRepaint();
					return true;
				}
				break;

			case KeyEvent.VK_F2 :
				view.startEditing(geo);				
				return true;				
		}
		
		
		
	
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
			return true;
		}

		return false;
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
		
		// RIGHT CLICK
		if (e.isPopupTrigger() || e.isMetaDown()) {
			GeoElement geo = AlgebraView.getGeoElementForLocation(view, e.getX(), e.getY());
			
			if (!app.containsSelectedGeo(geo)) {
				app.clearSelectedGeos();					
			}
														
			// single selection: popup menu
			if (app.selectedGeosSize() < 2) {				
				app.showPopupMenu(geo, view, e.getPoint());						
			} 
			// multiple selection: properties dialog
			else {														
				app.showPropertiesDialog(app.getSelectedGeos());	
			}								
		}
		// LEFT CLICK
		else {
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
				} else if (clicks == 1) {
					if (geo == null)
						app.clearSelectedGeos();
					else {						
						if (e.isControlDown()) {
							app.toggleSelectedGeo(geo); 													
						} else {							
							app.clearSelectedGeos();
							app.addSelectedGeo(geo);
						}		
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

	public void mousePressed(java.awt.event.MouseEvent e) {}

	public void mouseReleased(java.awt.event.MouseEvent e) {}

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
			if (geo != null)
				view.setToolTipText(geo.getLongDescriptionHTML(true, true));
			else
				view.setToolTipText(null);
		}
		
	}

	
}
