/*
    GEONExT

    Copyright (C) 2002  GEONExT-Group, Lehrstuhl für Mathematik und ihre Didaktik, Universität Bayreuth

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA. 
*/
package geogebra.gui.toolbar;
import javax.swing.*;
import javax.swing.tree.*;
import java.*;
import java.awt.*;
import java.util.*;
class ImageTreeCellRenderer extends javax.swing.tree.DefaultTreeCellRenderer {
	private boolean focused = false;
	private JLabel renderer;
	Color fg, bg, sfg, sbg;
	public ImageTreeCellRenderer() {
		renderer = new JLabel();
		renderer.setOpaque(true);
	}
	public ImageTreeCellRenderer(Color bg, Color fg, Color sbg, Color sfg) {
		renderer = new JLabel();
		renderer.setOpaque(true);
		this.fg = fg;
		this.bg = bg;
		this.sfg = sfg;
		this.sbg = sbg;
	}
	public Component getTreeCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value == null) {
			renderer.setText("");
			renderer.setIcon(null);
		} else {
			renderer.setText(((ModeLabel) value).description);
			renderer.setIcon(((ModeLabel) value).getIcon());
		}
		//	renderer.setBackground(isSelected ? sbg : bg);
		//	renderer.setForeground(isSelected ? sfg : fg);
		return this;
	}
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		//renderer = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		String stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);
		setEnabled(tree.isEnabled());
		//this.hasFocus = hasFocus;
		setText(stringValue);
		if (sel)
			setForeground(getTextSelectionColor());
		else
			setForeground(getTextNonSelectionColor());
		if (leaf) {
			setIcon(getLeafIcon());
		} else
			if (expanded) {
				setIcon(getOpenIcon());
			} else {
				setIcon(getClosedIcon());
			}
		selected = sel;
		/*if(((DefaultMutableTreeNode)value).getUserObject() instanceof ModeLabel)
					renderer.setText(((ModeLabel)((DefaultMutableTreeNode)value).getUserObject()).description);
					renderer.setIcon(((ModeLabel)((DefaultMutableTreeNode)value).getUserObject()).getIcon());
		*/
		Object ob = ((DefaultMutableTreeNode) value).getUserObject();
		if (ob instanceof Integer) {
			setText(((ModeLabel) ((DefaultMutableTreeNode) value).getUserObject()).description);
			setIcon(((ModeLabel) ((DefaultMutableTreeNode) value).getUserObject()).getIcon());
		}
		return this;
	}
}