/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/
package geogebra.gui.toolbar;

import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;

public class ModeToggleButtonGroup extends ButtonGroup {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPopupMenu activePopMenu;
	
	
	public void setActivePopupMenu(JPopupMenu popMenu) {
		activePopMenu = popMenu;			
	}	
	
	public JPopupMenu getActivePopupMenu() {
		return activePopMenu;
	}
	
	
}
