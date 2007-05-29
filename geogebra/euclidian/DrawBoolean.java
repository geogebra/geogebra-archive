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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

/**
 * Toggle Button for free GeoBoolean object.
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

	/** Creates new DrawText */
	public DrawBoolean(EuclidianView view, GeoBoolean geoBool) {
		this.view = view;
		this.geoBool = geoBool;
		geo = geoBool;

		checkBox = new JCheckBox();
		checkBox.setOpaque(false);
		view.add(checkBox);

		// action listener for checkBox
		BooleanCheckBoxListener cbl = new BooleanCheckBoxListener();
		checkBox.addActionListener(cbl);
		checkBox.addMouseListener(cbl);
		checkBox.addMouseMotionListener(cbl);
		checkBox.setFocusable(false);

		update();
	}

	private class BooleanCheckBoxListener implements ActionListener,
			MouseListener, MouseMotionListener {

		private boolean dragging = false;
		private EuclidianController ec = view.getEuclidianController();

		/**
		 * Handles click on check box. Changes value of GeoBoolean.
		 */
		public void actionPerformed(ActionEvent ev) {
			if (dragging) {
				checkBox.setSelected(geoBool.getBoolean());
				return;
			}
				
			geoBool.setValue(checkBox.isSelected());
			geoBool.updateRepaint();
		}

		public void mouseDragged(MouseEvent e) {	
			dragging = true;
			e.translatePoint(checkBox.getX(), checkBox.getY());
			ec.mouseDragged(e);
		}

		public void mouseMoved(MouseEvent e) {				
			e.translatePoint(checkBox.getX(), checkBox.getY());
			ec.mouseMoved(e);			
		}

		public void mouseClicked(MouseEvent e) {
			e.translatePoint(checkBox.getX(), checkBox.getY());
			ec.mouseClicked(e);			
		}

		public void mousePressed(MouseEvent e) {
			dragging = false;	
			e.translatePoint(checkBox.getX(), checkBox.getY());
			ec.mousePressed(e);		
		}

		public void mouseReleased(MouseEvent e) {
			e.translatePoint(checkBox.getX(), checkBox.getY());
			ec.mouseReleased(e);		
		}

		public void mouseEntered(MouseEvent arg0) {
			hit = true;
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

		// update caption string
		String caption = geoBool.getCaption();
		if (!caption.equals(oldCaption)) {
			oldCaption = caption;
			labelDesc = GeoElement.indicesToHTML(caption, true);
		}
		checkBox.setText(labelDesc);
		checkBox.setFont(view.fontPoint);
		checkBox.setForeground(geoBool.getObjectColor());
		checkBox.setSelected(geoBool.getBoolean());
		
		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;		
		Dimension prefSize = checkBox.getPreferredSize();
		labelRectangle.setBounds(xLabel, yLabel, prefSize.width,
				prefSize.height);
		checkBox.setBounds(labelRectangle);
	}

	final public void draw(Graphics2D g2) {
		if (isVisible) {
			// the button is drawn as a swing component by the view
			// They are Swing components and children of the view

			// draw label rectangle
			if (geo.doHighlighting()) {
				g2.setStroke(objStroke);
				g2.setPaint(Color.lightGray);
				g2.draw(labelRectangle);
				
				// TODO: remove
				System.out.println("highlight drawn");
				checkBox.setBorder(BorderFactory.createEtchedBorder());
			}
		}
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
