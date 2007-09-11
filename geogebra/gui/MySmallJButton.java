/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.gui;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;


public class MySmallJButton extends JButton {
	
	private static final long serialVersionUID = 1L;	

	public MySmallJButton(Action action, int addPixel) {
		super(action);
		setSmallSize(addPixel, addPixel);
	}
	
	public MySmallJButton(Icon icon, int addPixel) {
		setIcon(icon);
		setSmallSize(addPixel, addPixel);
	}
	
	public MySmallJButton(Icon icon, int pixelX, int pixelY) {
		setIcon(icon);
		setSmallSize(pixelX, pixelY);
	}
	
	private void setSmallSize(int pixelX, int pixelY) {
		Icon icon = getIcon();
		Dimension dim = new Dimension(icon.getIconWidth()+ pixelX, 
									  icon.getIconHeight()+ pixelY);
		setPreferredSize(dim);
		setMaximumSize(dim);
		setMinimumSize(dim);				
	}

}
