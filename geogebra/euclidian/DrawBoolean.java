/* 
 GeoGebra - Dynamic Geometry and Algebra
 Copyright Markus Hohenwarter, http://www.geogebra.at

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation; either version 2 of the License, or 
 (at your option) any later version.
 */

package geogebra.euclidian;

import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JCheckBox;

/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Markus Hohenwarter
 * @version
 */
public final class DrawBoolean extends Drawable {

	private GeoBoolean geoBool;

	private boolean isVisible;

	private JCheckBox checkBox;
	private boolean hit = false;
	private String oldCaption;
	private BooleanCheckBoxListener cbl;

	/** Creates new DrawText */
	public DrawBoolean(EuclidianView view, GeoBoolean geoBool) {
		this.view = view;
		this.geoBool = geoBool;
		geo = geoBool;
		
		// action listener for checkBox
		cbl = new BooleanCheckBoxListener();
		checkBox = new JCheckBox();	
		checkBox.addItemListener(cbl);
		checkBox.addMouseListener(cbl);
		checkBox.addMouseMotionListener(cbl);
		checkBox.setFocusable(false);	
		view.add(checkBox);
		
		update();
	}

	private class BooleanCheckBoxListener implements ItemListener,
			MouseListener, MouseMotionListener {

		private boolean dragging = false;
		private EuclidianController ec = view.getEuclidianController();

		/**
		 * Handles click on check box. Changes value of GeoBoolean.
		 */
		public void itemStateChanged(ItemEvent e) {
			if (dragging) {
				checkBox.removeItemListener(this);
				checkBox.setSelected(!checkBox.isSelected());
				checkBox.addItemListener(this);
				return;
			}
			
			Object source = e.getItemSelectable();
		    if (source == checkBox) {
		    	 if (e.getStateChange() == ItemEvent.DESELECTED) {
		    		 geoBool.setValue(false);
		    	 } else {
		    		 geoBool.setValue(true);
		    	 }
		    	 geoBool.updateRepaint();
		    	 
		    	 // make sure mouseReleased does not change
		    	 // the value back my faking a drag
		    	 dragging = true;
		    } 		   
		}

		public void mouseDragged(MouseEvent e) {	
			dragging = true;			
			e.translatePoint(checkBox.getX(), checkBox.getY());
			ec.mouseDragged(e);
			view.setToolTipText(null);
		}

		public void mouseMoved(MouseEvent e) {				
			e.translatePoint(checkBox.getX(), checkBox.getY());
			ec.mouseMoved(e);
			view.setToolTipText(null);
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) return;
			
			e.translatePoint(checkBox.getX(), checkBox.getY());
			ec.mouseClicked(e);
		}

		public void mousePressed(MouseEvent e) {
			dragging = false;	
			e.translatePoint(checkBox.getX(), checkBox.getY());
			ec.mousePressed(e);		
		}

		public void mouseReleased(MouseEvent e) {	
			if (!dragging && !e.isMetaDown() && !e.isPopupTrigger()
					&& view.getMode() == EuclidianView.MODE_MOVE) 
			{
				// handle LEFT CLICK
				geoBool.setValue(!geoBool.getBoolean());
				geoBool.updateRepaint();
				
				// make sure itemChanged does not change
		    	// the value back my faking a drag
		    	dragging = true;				
			}
			else {
				// handle right click and dragging
				e.translatePoint(checkBox.getX(), checkBox.getY());
				ec.mouseReleased(e);	
			}
		}

		public void mouseEntered(MouseEvent arg0) {
			hit = true;
			view.setToolTipText(null);
		}

		public void mouseExited(MouseEvent arg0) {
			hit = false;
		}

		
	}

	final public void update() {
		isVisible = geo.isEuclidianVisible();
		checkBox.setVisible(isVisible);
		if (!isVisible)
			return;		

		// show hide label by setting text
		if (geo.isLabelVisible()) {
			// get caption to show r
			String caption = geoBool.getCaption();
			if (!caption.equals(oldCaption)) {
				oldCaption = caption;
				labelDesc = GeoElement.indicesToHTML(caption, true);
			}	
			checkBox.setText(labelDesc);
		} else {
			// don't show label
			checkBox.setText("");
		}			
		
		checkBox.setOpaque(false);		
		checkBox.setFont(view.fontPoint);
		checkBox.setForeground(geoBool.getObjectColor());
		
		// set checkbox state		
		checkBox.removeItemListener(cbl);
		checkBox.setSelected(geoBool.getBoolean());
		checkBox.addItemListener(cbl);
		
		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;		
		Dimension prefSize = checkBox.getPreferredSize();
		labelRectangle.setBounds(xLabel, yLabel, prefSize.width,
				prefSize.height);
		checkBox.setBounds(labelRectangle);
		
		
	}

	final public void draw(Graphics2D g2) {
		/*
		if (isVisible) {		
			// the button is drawn as a swing component by the view
			// They are Swing components and children of the view

			// draw label rectangle
			if (geo.doHighlighting()) {
				g2.setStroke(objStroke);
				g2.setPaint(Color.lightGray);
				g2.draw(labelRectangle);
				
				System.out.println("highlight drawn");
				checkBox.setBorder(BorderFactory.createEtchedBorder());
			}				
		}
		*/
	}

	/**
	 * Removes button from view again
	 */
	final public void remove() {
		view.remove(checkBox);
	}

	/*
	 * See the mouse listener of the check box in the constructor.
	 */
	final public boolean hit(int x, int y) {
		return hit;
	}

	final public boolean isInside(Rectangle rect) {
		return rect.contains(labelRectangle);
	}

	/**
	 * Returns false
	 */
	public boolean hitLabel(int x, int y) {
		return false;
	}

	final public GeoElement getGeoElement() {
		return geo;
	}

	final public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

}
