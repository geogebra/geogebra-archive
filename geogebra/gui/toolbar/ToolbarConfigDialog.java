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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

public class ToolbarConfigDialog extends JDialog implements ActionListener {
	
	private Application app;	
	public ToolbarConfigPanel confPanel;

	public ToolbarConfigDialog(Application app) {
		super();
		this.app = app;
	
		setLayout(new BorderLayout(5, 5));
		confPanel = new ToolbarConfigPanel(app);
		add(confPanel, BorderLayout.CENTER);
		
		pack();
		setLocationRelativeTo(null);
	}
	
	public void actionPerformed(ActionEvent e) {
		String source = e.getActionCommand();		
		if (source.equals("apply")) {
			apply();
		} else if (source.equals("cancel")) {
			dispose();		
		}	
	}
	
	private void apply() {
		// TODO: implement
	}
	
}