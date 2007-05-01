/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.gui.toolbar;
import geogebra.Application;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class ModeCellRenderer extends DefaultTreeCellRenderer {
	
	private Application app;
	
	public ModeCellRenderer(Application app) {
		setOpaque(true);
		this.app = app;
	}

	
	public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean selected,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) {		
		
		// mode number
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object ob = node.getUserObject();
		if (ob instanceof Integer) {
			tree.setRowHeight(-1);
			int mode = ((Integer) ob).intValue();	
			if (mode == -1)
				setText("____");
			else {
				setText(app.getModeText(mode));
				setIcon(app.getModeIcon(mode));
			}			
		} else {
			super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		}
		return this;
	}
	
}