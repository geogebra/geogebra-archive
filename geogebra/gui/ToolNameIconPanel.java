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
import geogebra.kernel.Macro;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ToolNameIconPanel extends JPanel {	
	public static final int ICON_WIDTH = 32;
	public static final int ICON_HEIGHT = 32;
	
	private JTextField tfCmdName;
	private JTextField tfToolHelp;
	private JTextField tfToolName;	
	private JCheckBox cbShowInToolBar;
	private JLabel labelIcon;		
	private String iconFileName;			
	
	private Application app;			
	
	public ToolNameIconPanel(final Application app) {
		this.app = app;
		
		GridBagLayout namePanelLayout = new GridBagLayout();
		namePanelLayout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1, 0.0};
		namePanelLayout.rowHeights = new int[] {7, 7, 7, 20, 7};
		namePanelLayout.columnWidths = new int[] {7, 7, 7};
		namePanelLayout.columnWeights = new double[] {0.1, 0.9, 0.1};
		namePanelLayout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1, 0.1};
		namePanelLayout.rowHeights = new int[] {7, 7, 7, 20, 20};
		namePanelLayout.columnWidths = new int[] {7, 7, 7};
		namePanelLayout.columnWeights = new double[] {0.1, 0.9, 0.1};
		namePanelLayout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1};
		namePanelLayout.rowHeights = new int[] {7, 7, 7, 20};
		namePanelLayout.columnWeights = new double[] {0.1, 0.9, 0.1};
		namePanelLayout.columnWidths = new int[] {7, 7, 7};
		setLayout(namePanelLayout);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		{
			JLabel labelToolName = new JLabel();			
			add(labelToolName, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0));
			labelToolName.setText(app.getMenu("ToolName"));
		}
		{
			JLabel labelCmdName = new JLabel();			
			add(labelCmdName, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0));
			labelCmdName.setText(app.getMenu("CommandName"));
		}
		{
			JLabel labelToolHelp = new JLabel();			
			add(labelToolHelp, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0));
			labelToolHelp.setText(app.getMenu("ToolHelp"));
		}	
		KeyListener kl = new KeyListener() {
			public void keyPressed(KeyEvent e) {
				
			}
			public void keyReleased(KeyEvent e) {
				updateCmdName(e.getSource());
			}
			public void keyTyped(KeyEvent arg0) {}			
		};
		{			
			tfToolName = new JTextField();
			int n = app.getKernel().getMacroNumber()+1;
			tfToolName.setText(app.getMenu("Tool")+n);
			add(tfToolName, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));											
			tfToolName.addKeyListener(kl);
		}	
		{
			tfCmdName = new JTextField();
			tfCmdName.setText(tfToolName.getText());
			add(tfCmdName, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));									
			FocusListener fl = new FocusListener() {			
				public void focusGained(FocusEvent arg0) {					
				}
				public void focusLost(FocusEvent e) {
					updateCmdName(e.getSource());
				}			
			};
			tfCmdName.addFocusListener(fl);
		}
		{
			tfToolHelp = new JTextField();				
			add(tfToolHelp, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		}
		{
			labelIcon = new JLabel();		
			labelIcon.setIcon(app.getImageIcon("mode_tool_32.png"));
			add(labelIcon, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}	
	
		final JButton btIconFile = new JButton();
		add(btIconFile, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		btIconFile.setText(app.getMenu("Icon") + " ...");
		ActionListener ac = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileName = app.showImageFileChooser();
				if (fileName != null) {
					BufferedImage image = app.getExternalImage(fileName);
					if (image.getWidth() != ICON_WIDTH || image.getHeight() != ICON_HEIGHT) {
						image = ImageResizer.resizeImage(image, ICON_WIDTH, ICON_HEIGHT);
						app.addExternalImage(fileName, image);
					}
					iconFileName = fileName;
					labelIcon.setIcon(new ImageIcon(image));
				}
			}				
		};
		btIconFile.addActionListener(ac);
						
	
		cbShowInToolBar = new JCheckBox();
		add(cbShowInToolBar, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		cbShowInToolBar.setText(app.getMenu("ShowInToolBar"));
		cbShowInToolBar.setSelected(true);		
		ActionListener ac2 = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean active = cbShowInToolBar.isSelected();
				labelIcon.setEnabled(active);
				btIconFile.setEnabled(active);
			}				
		};
		cbShowInToolBar.addActionListener(ac2);				
	}
	
	public void init(Macro macro) {		
		if (macro == null) {
			tfToolName.setText("");		
			tfCmdName.setText("");				
			tfToolHelp.setText("");	
			cbShowInToolBar.setSelected(false);
			labelIcon.setIcon(null);
		} else {			
			tfToolName.setText(macro.getToolName());		
			tfCmdName.setText(macro.getCommandName());				
			tfToolHelp.setText(macro.getToolHelp());	
			cbShowInToolBar.setSelected(macro.isShowInToolBar());
			
			BufferedImage img = app.getExternalImage(macro.getIconFileName());
			if (img != null)
				labelIcon.setIcon(new ImageIcon(img));
			else
				labelIcon.setIcon(app.getImageIcon("mode_tool_32.png"));
		}
	}
	
	public void requestFocus() {
		super.requestFocus();
		
		tfToolName.requestFocusInWindow();
		tfToolName.setSelectionStart(0);
		tfToolName.setSelectionEnd(tfToolName.getText().length());	
	}
	
	public String getCommandName() {
		return tfCmdName.getText();
	}
	
	public String getToolName() {
		return tfToolName.getText();
	}
	
	public String getToolHelp() {
		return tfToolHelp.getText();
	}
	
	public boolean showInToolBar() {
		return cbShowInToolBar.isSelected();
	}
	
	public String getIconFileName() {
		return iconFileName;
	}
	
	private void updateCmdName(Object source) {				
		String cmdName = source == tfToolName ?
				tfToolName.getText() :
				tfCmdName.getText();
								
		// remove spaces
		cmdName = cmdName.replaceAll(" ", "");
		try {
			String parsed = app.getAlgebraController().parseLabel(cmdName);
			if (parsed != tfCmdName.getText())
				tfCmdName.setText(parsed);
		} catch (Error err) {
			tfCmdName.setText(defaultToolName());
		}
		catch (Exception ex) {	
			tfCmdName.setText(defaultToolName());
		}
	}
	
	private String defaultToolName() {
		int n = app.getKernel().getMacroNumber()+1;				
		return app.getMenu("Tool") + n;
	}
	
}