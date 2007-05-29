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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class ToolbarConfigDialog extends JDialog {
	
	private Application app;	
	public ToolbarConfigPanel confPanel;

	public ToolbarConfigDialog(Application app) {
		super(app.getFrame(), true);
		this.app = app;
		
		setTitle(app.getMenu("Toolbar.Customize"));				
	
		getContentPane().setLayout(new BorderLayout(5, 5));
		confPanel = new ToolbarConfigPanel(app);
		getContentPane().add(confPanel, BorderLayout.CENTER);
		getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(app.getFrame());
	}
		
	private void apply() {				
		app.setToolBarDefinition(confPanel.getToolBarString());
		app.updateToolBar();
		setVisible(false);
		dispose();
	}
	
	private JPanel createButtonPanel() {		
		JPanel btPanel = new JPanel();
		btPanel.setLayout(new BoxLayout(btPanel, BoxLayout.X_AXIS));
		btPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		
		/*
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(app.getMenu("Toolbar.Default"));
		//model.addElement(app.getMenu("Basic"));
		model.addElement(app.getMenu("Toolbar.UserDefined"));
		JComboBox cbToolbar = new JComboBox(model);
		*/
		
		final JButton btDefaultToolbar = new JButton();
		btPanel.add(btDefaultToolbar);		
		btDefaultToolbar.setText(app.getMenu("Toolbar.ResetDefault") );				
		
		btPanel.add(Box.createHorizontalGlue());	
		final JButton btApply = new JButton();
		btPanel.add(btApply);		
		btApply.setText(app.getPlain("Apply") );		
		
		final JButton btCancel = new JButton();
		btPanel.add(Box.createRigidArea(new Dimension(5,0)));
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
				else if (src == btDefaultToolbar) {
					confPanel.setToolBarString(app.getDefaultToolbarString());
				}
			}			
		};		
		btCancel.addActionListener(ac);		
		btApply.addActionListener(ac);
		btDefaultToolbar.addActionListener(ac);
		
		return btPanel;
	}			
	
}