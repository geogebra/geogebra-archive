/*
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.gui;

import geogebra.Application;
import geogebra.kernel.GeoElement;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

/**
 * ListCellRenderer for GeoElements
 * @author Markus Hohenwarter
 */
public class GeoTreeCellRenderer extends DefaultTreeCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Application app;
	
	public GeoTreeCellRenderer(Application app) {
		this.app = app;
		setOpaque(true);
		
	}

	public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean selected,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) {			
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;	
		TreeNode root = node.getRoot();
		setIcon(null);
		
		if (root !=  node && root != node.getParent()) {	
			// GeoElement
			GeoElement geo = (GeoElement) node.getUserObject();								
			if (geo == null) return this;
			
			setFont(app.boldFont);
			setForeground(geo.labelColor);			
			setText(geo.getLabelTextOrHTML());		
			
			if (geo.doHighlighting())				   
				setBackground(Application.COLOR_SELECTION);
			else 
				setBackground(getBackgroundNonSelectionColor());								
		} 
		else { 
			// type node			
			setFont(app.plainFont);
			setForeground(Color.black);
			
			if (selected)
				setBackground(Application.COLOR_SELECTION);
			else
				setBackground(getBackgroundNonSelectionColor());
			
			setBorder(null);					
			setText(value.toString());
		}	
		
		return this;
		
	}
}