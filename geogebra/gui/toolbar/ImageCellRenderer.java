/*
    GEONExT

    Copyright (C) 2002  GEONExT-Group, Lehrstuhl für Mathematik und ihre Didaktik, Universität Bayreuth

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA. 
*/
package geogebra.gui.toolbar;
import javax.swing.*;
import java.*;
import java.awt.*;
import java.util.*;
class ImageCellRenderer implements ListCellRenderer {
	private boolean focused = false;
	private JLabel renderer;
	Color fg, bg, sfg, sbg;
	public ImageCellRenderer(Color bg, Color fg, Color sbg, Color sfg) {
		renderer = new JLabel();
		renderer.setOpaque(true);
		this.fg = fg;
		this.bg = bg;
		this.sfg = sfg;
		this.sbg = sbg;
	}
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value == null) {
			renderer.setText("");
			renderer.setIcon(null);
		} else {
			
			renderer.setText(((FrontendElement) value).description);
			renderer.setIcon(((FrontendElement) value).getIcon());
		}
		renderer.setBackground(isSelected ? sbg : bg);
		renderer.setForeground(isSelected ? sfg : fg);
		return renderer;
	}
}