/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;

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

	private GeoElement lastSelectedGeo = null;
	private boolean skipSelection;

	public void mouseClicked(java.awt.event.MouseEvent e) {	
		// right click is consumed in mousePressed, but in GeoGebra 3D,
		// where heavyweight popup menus are enabled this doesn't work
		// so make sure that this is no right click as well (ticket #302)
		if (e.isConsumed() || Application.isRightClick(e)) {
			return;
		}
		
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
		//EuclidianView ev = app.getEuclidianView();
		EuclidianViewInterface ev = app.getGuiManager().getActiveEuclidianView();
		if (clicks == 2) {										
			app.clearSelectedGeos();
			ev.resetMode();
			if (geo != null && !Application.isControlDown(e)) {
				view.startEditing(geo, e.isShiftDown());						
			}
			return;
		} 	
		
		int mode = ev.getMode();
		if (!skipSelection && mode == EuclidianView.MODE_MOVE ) {
			// update selection	
			if (geo == null)
				app.clearSelectedGeos();
			else {					
				// handle selecting geo
				if (Application.isControlDown(e)) {
					app.toggleSelectedGeo(geo); 													
					if (app.getSelectedGeos().contains(geo)) lastSelectedGeo = geo;
				} else if (e.isShiftDown() && lastSelectedGeo != null) {
					boolean nowSelecting = true;
					boolean selecting = false;
					boolean aux = geo.isAuxiliaryObject();
					boolean ind = geo.isIndependent();
					boolean aux2 = lastSelectedGeo.isAuxiliaryObject();
					boolean ind2 = lastSelectedGeo.isIndependent();
					
					if ((aux == aux2 && aux == true) || (aux == aux2 && ind == ind2)) {
						
						Iterator<GeoElement> it = kernel.getConstruction().getGeoSetLabelOrder().iterator();
						
						boolean direction = geo.getLabel().compareTo(lastSelectedGeo.getLabel()) < 0;
						
						while (it.hasNext()) {
							GeoElement geo2 = it.next();
							if ((geo2.isAuxiliaryObject() == aux && aux == true)
									|| (geo2.isAuxiliaryObject() == aux && geo2.isIndependent() == ind)) {
								
								if (direction && geo2 == lastSelectedGeo) selecting = !selecting;
								if (!direction && geo2 == geo) selecting = !selecting;
								
								if (selecting) {
									app.toggleSelectedGeo(geo2);
									nowSelecting = app.getSelectedGeos().contains(geo2);
								}
								
								if (!direction && geo2 == lastSelectedGeo) selecting = !selecting;
								if (direction && geo2 == geo) selecting = !selecting;
							}
						}
					}

					if (nowSelecting) {
						app.addSelectedGeo(geo); 
						lastSelectedGeo = geo;
					} else {
						app.removeSelectedGeo(lastSelectedGeo);
						lastSelectedGeo = null;
					}
					
				} else {							
					app.clearSelectedGeos();
					app.addSelectedGeo(geo);
					lastSelectedGeo = geo;
				}
			}
		} 
		else if (mode != EuclidianView.MODE_SELECTION_LISTENER) {
			// let euclidianView know about the click
			ev.clickedGeo(geo, e);
		} else 
			// tell selection listener about click
			app.geoElementSelected(geo, false);

		
		// Alt click: copy definition to input field
		if (geo != null && e.isAltDown() && app.showAlgebraInput()) {			
			// F3 key: copy definition to input bar
			app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(3, geo);			
		}
		
		ev.mouseMovedOver(null);		
	}

	public void mousePressed(java.awt.event.MouseEvent e) {
		view.cancelEditing();

		boolean rightClick = app.isRightClickEnabled() && Application.isRightClick(e);
		
		// RIGHT CLICK
		if (rightClick) {
			e.consume();
			
			// get GeoElement at mouse location		
			TreePath tp = view.getPathForLocation(e.getX(), e.getY());
			GeoElement geo = AlgebraView.getGeoElementForPath(tp);
			
			if (geo != null && !app.containsSelectedGeo(geo)) {
				app.clearSelectedGeos();					
			}
														
			// single selection: popup menu
			if (app.selectedGeosSize() < 2) {				
				if(geo == null) {
					AlgebraContextMenu contextMenu = new AlgebraContextMenu(app);
					contextMenu.show(view, e.getPoint().x, e.getPoint().y);
				} else {
                    ArrayList<GeoElement> temp = new ArrayList<GeoElement>();
                    temp.add(geo);
					app.getGuiManager().showPopupMenu(temp, view, e.getPoint());
				}			
			} 
			// multiple selection: popup menu (several geos)
			else {
				if(geo != null) {
					app.getGuiManager().showPopupMenu(app.getSelectedGeos(), view, e.getPoint());
				}
			}	
			
		// LEFT CLICK	
		}else{
			
			// When a single, new selection is made with no key modifiers
			// we need to handle selection in mousePressed, not mouseClicked.
			// By doing this selection early, a DnD drag will come afterwards
			// and grab the new selection. 
			// All other selection types must be handled later in mouseClicked. 
			// In this case a DnD drag starts first and grabs the previously selected 
			// geos (e.g. cntrl-selected or EV selected) as the user expects.
			
			skipSelection = false; // flag to prevent duplicate selection in MouseClicked
			
			TreePath tp = view.getPathForLocation(e.getX(), e.getY());
			GeoElement geo = AlgebraView.getGeoElementForPath(tp);	
			EuclidianViewInterface ev = app.getGuiManager().getActiveEuclidianView();
			int mode = ev.getMode();

			if (mode == EuclidianView.MODE_MOVE  && 
					!Application.isControlDown(e) && !e.isShiftDown() 
					&& geo != null  && !app.containsSelectedGeo(geo)) 
			{					
				app.clearSelectedGeos();
				app.addSelectedGeo(geo);
				lastSelectedGeo = geo;
				skipSelection = true;
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
				
		// tell EuclidianView to handle mouse over
		//EuclidianView ev = app.getEuclidianView();
		EuclidianViewInterface ev = app.getGuiManager().getActiveEuclidianView();
		ev.mouseMovedOver(geo);								
		
		if (geo != null) {
			app.setTooltipFlag();
			view.setToolTipText(geo.getLongDescriptionHTML(true, true));
			app.clearTooltipFlag();
		} else
			view.setToolTipText(null);						
	}
	
}
