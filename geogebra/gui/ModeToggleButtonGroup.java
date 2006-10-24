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
