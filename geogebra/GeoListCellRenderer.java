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

import geogebra.kernel.GeoElement;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * ListCellRenderer for GeoElements
 * @author Markus Hohenwarter
 */
public class GeoListCellRenderer extends DefaultListCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Component getListCellRendererComponent(
		JList list,
		Object value,
		int index,
		boolean isSelected,
		boolean cellHasFocus) {

		super.getListCellRendererComponent(
			list,
			value,
			index,
			isSelected,
			cellHasFocus);	
		
		if (value instanceof GeoElement) {
			GeoElement geo = (GeoElement) value;
			//setForeground(geo.labelColor);
			setText(geo.getNameDescriptionHTML(true, true));
			setToolTipText(geo.getLongDescriptionHTML(true, true));
		} else {
			setText(value.toString());
			setToolTipText(null);
		}
		return this;
	}
}