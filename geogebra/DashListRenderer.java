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

import geogebra.euclidian.EuclidianView;
import geogebra.util.FastHashMapKeyless;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * used by LineStylePanel for rendering a combobox with different
 * line styles (dashing)
 */
class DashListRenderer extends JPanel implements ListCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// map with (type, dashStrokes for type) pairs
	private FastHashMapKeyless dashStrokeMap;
	private BasicStroke dashStroke;
	//private Color bgColor;
	private boolean nullValue = false;

	public DashListRenderer() {
		//	init stroke map 
		dashStrokeMap = new FastHashMapKeyless();
		Integer[] types = EuclidianView.getLineTypes();
		int type;
		BasicStroke stroke;
		for (int i = 0; i < types.length; i++) {
			type = types[i].intValue();
			stroke = EuclidianView.getStroke(1.0f, type);
			dashStrokeMap.put(type, stroke);
		}
	}

	public Component getListCellRendererComponent(
		JList list,
		Object value,
		int index,
		boolean isSelected,
		boolean cellHasFocus) {
		if (isSelected)
			setBackground(list.getSelectionBackground());
		else
			setBackground(list.getBackground());

		nullValue = value == null;
		if (nullValue)
			return this;

		// value is an Integer with the line type's int value
		int type = ((Integer) value).intValue();
		// get the dashpanel for this dashing type
		dashStroke = (BasicStroke) dashStrokeMap.get(type);
		return this;
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// clear background
		g2.setBackground(getBackground());
		g2.clearRect(0, 0, getWidth(), getHeight());
		if (nullValue)
			return;

		// draw dashed line
		g2.setPaint(Color.black);
		g2.setStroke(dashStroke);
		int mid = getHeight() / 2;
		g2.drawLine(0, mid, getWidth(), mid);
	}
} // DashListRenderer