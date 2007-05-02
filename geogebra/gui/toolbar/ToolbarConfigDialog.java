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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class ToolbarConfigDialog extends JDialog {
	
	private Application app;	
	public ToolbarConfigPanel confPanel;

	public ToolbarConfigDialog(Application app) {
		super(app.getFrame(), true);
		this.app = app;
		
		setTitle(app.getMenu("CustomizeToolbar"));				
	
		setLayout(new BorderLayout(5, 5));
		confPanel = new ToolbarConfigPanel(app);
		add(confPanel, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(null);
	}
		
	private void apply() {		
		app.setToolBarDefinition(confPanel.getToolBarString());
		app.updateToolBar();
		setVisible(false);
		dispose();
	}
	
	private JPanel createButtonPanel() {		
		JPanel btPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));		
		btPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 5));
							
		final JButton btApply = new JButton();
		btPanel.add(btApply);		
		btApply.setText(app.getPlain("Apply") );		
		
		final JButton btCancel = new JButton();
		btPanel.add(Box.createRigidArea(new Dimension(10,0)));
		btPanel.add(btCancel);		
		btCancel.setText(app.getPlain("Cancel"));	
		
		ActionListener ac = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object src = e.getSource();
				if (src == btApply) {
					apply();
				}
				else if (src == btCancel) {
					setVisible(false);
					dispose();
				}
			}			
		};
		btCancel.addActionListener(ac);		
		btApply.addActionListener(ac);
		
		return btPanel;
	}			
	
}