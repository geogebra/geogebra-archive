/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoJavaScriptButton;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;


/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Markus Hohenwarter
 * @version
 */
public final class DrawJavaScriptButton extends Drawable {

	private GeoJavaScriptButton button;

	private boolean isVisible;

	private boolean hit = false;
	private String oldCaption;

	private Point textSize = new Point(0,0);


	/** Creates new DrawText */
	public DrawJavaScriptButton(EuclidianView view, GeoJavaScriptButton button) {
		this.view = view;
		this.button = button;
		geo = button;

		update();
	}


	final public void update() {
		isVisible = geo.isEuclidianVisible();

		if (!isVisible)
			return;		

		updateStrokes(button);

		// show hide label by setting text
		if (geo.isLabelVisible()) {
			// get caption to show r
			String caption = button.getCaption();
			if (!caption.equals(oldCaption)) {
				oldCaption = caption;
				labelDesc = caption; //GeoElement.indicesToHTML(caption, true);
			}	

		} else {

			updateLabel();
		}		
	}

	private void updateLabel() {
		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;		

		labelRectangle.setBounds(xLabel, yLabel,
				 ((textSize == null) ? 0 : textSize.x),
				12);

	}

	final public void draw(Graphics2D g2) {

		if (isVisible) {		

			g2.setFont(view.fontPoint);
			g2.setStroke(objStroke); 

			g2.setPaint(geo.getObjectColor());
			textSize = Drawable.drawIndexedString(g2, labelDesc, button.labelOffsetX, button.labelOffsetY + (13 + 9) / 2 + 5);

			updateLabel();
		}

	}

	/**
	 * was this object clicked at? (mouse pointer
	 * location (x,y) in screen coords)
	 */
	final public boolean hit(int x, int y) {
		return super.hitLabel(x, y);				      
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
