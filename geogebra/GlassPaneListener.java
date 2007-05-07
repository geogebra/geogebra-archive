/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra;

import java.awt.Component;
import java.awt.Container;
import java.awt.KeyEventDispatcher;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

/**
 * Dispatches all mouse and key events from the glass pane to a given component.
 */
public class GlassPaneListener implements MouseListener, MouseMotionListener, KeyEventDispatcher {
	
	private Component glassPane, destComp;
	private Container contentPane;
	
	public GlassPaneListener(Component glassPane, Container contentPane, Component destComp) {
		this.glassPane = glassPane;
		this.contentPane = contentPane;
		this.destComp = destComp;						
	}	
 	
	public void mouseMoved(MouseEvent e) {
	    redispatchMouseEvent(e);
	}
	public void mouseDragged(MouseEvent e) {
	    redispatchMouseEvent(e);
	}
	public void mouseClicked(MouseEvent e) {
	    redispatchMouseEvent(e);	    
	}
	public void mouseEntered(MouseEvent e) {
	    redispatchMouseEvent(e);
	}
	public void mouseExited(MouseEvent e) {
	    redispatchMouseEvent(e);
	}
	public void mousePressed(MouseEvent e) {
	    redispatchMouseEvent(e);
	}
	public void mouseReleased(MouseEvent e) {
	    redispatchMouseEvent(e);
	}		

	private void redispatchMouseEvent(MouseEvent e) {						
	    Point glassPanePoint = e.getPoint();
	    Container container = contentPane;
	    Point containerPoint = SwingUtilities.convertPoint(
	                                    glassPane,
	                                    glassPanePoint,
	                                    contentPane);
        
        //Find out exactly which component the mouse event is over.
        Component component =
            SwingUtilities.getDeepestComponentAt(
                                    container,
                                    containerPoint.x,
                                    containerPoint.y);
       
        if ((component != null)
            && (component.equals(destComp))) {
            //Forward events to the destination comp
            Point componentPoint = SwingUtilities.convertPoint(
                                        glassPane,
                                        glassPanePoint,
                                        component);
            component.dispatchEvent(new MouseEvent(component,
                                                 e.getID(),
                                                 e.getWhen(),
                                                 e.getModifiers(),
                                                 componentPoint.x,
                                                 componentPoint.y,
                                                 e.getClickCount(),
                                                 e.isPopupTrigger()));
        } 
	}

	public boolean dispatchKeyEvent(KeyEvent e) {
		if (destComp != null)
			destComp.dispatchEvent(e);			
		return true;
	}	
}
