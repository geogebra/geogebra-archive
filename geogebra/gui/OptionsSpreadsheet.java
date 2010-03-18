/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * Panel with options for the spreadsheet view.
 * G.Sturr 2010-3-5
 * 
 */
class OptionsSpreadsheet extends JPanel  implements ActionListener, FocusListener {
	
	private static final long serialVersionUID = 1L;

	private Application app;
	private Kernel kernel;
	private SpreadsheetView view;
	
	private JCheckBox cbShowGrid, cbShowRowHeader, 
	cbShowColumnHeader, cbShowHScrollbar,  cbShowVScrollbar, cbShowBrowser;
	
	private JTextField dirField, urlField;
	
	private JButton browseButton;
	private JRadioButton dirRadioButton, urlRadioButton;
	
	/**
	 * Creates a new dialog for the properties of the spreadsheet view.
	 */
	public OptionsSpreadsheet(Application app, SpreadsheetView view) {
		this.app = app;		
		this.view = view;
		kernel = app.getKernel();
		
		// build GUI
		initGUI();
		updateGUI();
	}

	
	
	private void initGUI() {
		
		removeAll();	
		setLayout(new BorderLayout());
		add(new JScrollPane(buildLayoutOptionsPanel()));
		
		/*
		JTabbedPane tabbedPane = new JTabbedPane();
		
		tabbedPane.addTab("Layout",null, new JScrollPane(buildLayoutOptionsPanel()));
		tabbedPane.addTab("Browser",null, new JScrollPane(buildBrowserOptionsPanel()));
		add(tabbedPane);
		*/
		
	    
	}
	
	
	private JPanel buildLayoutOptionsPanel() {
        		 
		JPanel layoutOptions = new JPanel();
		layoutOptions.setLayout(new BoxLayout(layoutOptions, BoxLayout.Y_AXIS));

		layoutOptions.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));	
		

        cbShowGrid = new JCheckBox(app.getMenu("Gridlines"));  
        cbShowGrid.addActionListener(this);        
        layoutOptions.add(cbShowGrid); 
        
        cbShowColumnHeader = new JCheckBox(app.getMenu("Column Header"));  
        cbShowColumnHeader.addActionListener(this);
        layoutOptions.add(cbShowColumnHeader); 
        
        cbShowRowHeader = new JCheckBox(app.getMenu("Row Header"));  
        cbShowRowHeader.addActionListener(this);
        layoutOptions.add(cbShowRowHeader); 
        
        cbShowHScrollbar = new JCheckBox(app.getMenu("Horizontal Scrollbars"));  
        cbShowHScrollbar.addActionListener(this);
        layoutOptions.add(cbShowHScrollbar); 
        
        cbShowVScrollbar = new JCheckBox(app.getMenu("Vertical Scrollbars"));  
        cbShowVScrollbar.addActionListener(this);
        layoutOptions.add(cbShowVScrollbar); 
        
        cbShowBrowser = new JCheckBox(app.getMenu("Show Browser"));  
        cbShowBrowser.addActionListener(this);        
        layoutOptions.add(cbShowBrowser); 
        
        
        return layoutOptions;
	}
	
	
	private JPanel buildBrowserOptionsPanel() {
		
		JPanel browserOptions = new JPanel();
		
		browserOptions.setLayout(new BoxLayout(browserOptions, BoxLayout.Y_AXIS));

		browserOptions.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));	
		

		  
        cbShowBrowser = new JCheckBox(app.getMenu("Show Browser"));  
        cbShowBrowser.addActionListener(this);        
        
        
        
        dirRadioButton = new JRadioButton("Directory: ");
        dirRadioButton.setSelected(true);

        urlRadioButton = new JRadioButton("URL: ");
  
        //Register a listener for the radio buttons.
        dirRadioButton.addActionListener(this);
        urlRadioButton.addActionListener(this);
  
        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(dirRadioButton);
        group.add(urlRadioButton);
        
        
        dirField = new MyTextField(app.getGuiManager());
        dirField.setAlignmentX(0.0f);
       // dirField.setMaximumSize(new Dimension(300,20));
        dirField.setColumns(30);
        dirField.addActionListener(this);
        dirField.addFocusListener(this);
        
        urlField = new MyTextField(app.getGuiManager());
        urlField.setAlignmentX(0.0f);
       // urlField.setMaximumSize(new Dimension(300,20));
        urlField.setColumns(30);
        urlField.addActionListener(this);
        urlField.addFocusListener(this);
        
        browseButton = new JButton(app.getImageIcon("aux_folder.gif"));
        browseButton.addActionListener(this);
        
        int tab = 15;
        
        JPanel dirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dirPanel.setAlignmentX(0.0f);
        dirPanel.add(Box.createHorizontalStrut(tab));
        dirPanel.add(dirRadioButton);
        dirPanel.add(browseButton);
        dirPanel.add(dirField); 
        
       
        
        
        JPanel urlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urlPanel.setAlignmentX(0.0f);
        urlPanel.add(Box.createHorizontalStrut(tab));
        urlPanel.add(urlRadioButton);
        urlPanel.add(urlField); 
        
        
        // Put it all together
        
        browserOptions.add(cbShowBrowser); 
       // browserOptions.add(Box.createVerticalGlue());
        browserOptions.add(dirPanel);
      //  browserOptions.add(Box.createVerticalGlue());
        browserOptions.add(urlPanel);
        
        browserOptions.add(Box.createVerticalGlue());
			
		
		return browserOptions;
	}
	
	
	public void updateGUI() {				
			
		cbShowGrid.removeActionListener(this);
		cbShowGrid.setSelected(view.getShowGrid());
		cbShowGrid.addActionListener(this);          
        
        cbShowRowHeader.removeActionListener(this);
        cbShowRowHeader.setSelected(view.getShowRowHeader()); 
        cbShowRowHeader.addActionListener(this);
        
        cbShowColumnHeader.removeActionListener(this);
        cbShowColumnHeader.setSelected(view.getShowColumnHeader()); 
        cbShowColumnHeader.addActionListener(this);
        
        cbShowHScrollbar.removeActionListener(this);
        cbShowHScrollbar.setSelected(view.getShowHScrollbar()); 
        cbShowHScrollbar.addActionListener(this);
        
        
        cbShowVScrollbar.removeActionListener(this);
        cbShowVScrollbar.setSelected(view.getShowVScrollbar()); 
        cbShowVScrollbar.addActionListener(this);
        
        cbShowBrowser.removeActionListener(this);
        cbShowBrowser.setSelected(view.getShowBrowserPanel()); 
        cbShowBrowser.addActionListener(this);
     
       /* 
        dirField.removeActionListener(this);
        dirField.setText(System.getProperty("user.dir"));
        dirField.setCaretPosition(0);
        dirField.addActionListener(this);
        
        urlField.removeActionListener(this);
        urlField.setText("http://www.santarosa.edu/~gsturr/data/BPS5/BPS5.xml");
        urlField.setCaretPosition(0);
        urlField.addActionListener(this);
        
        
        dirRadioButton.setEnabled(cbShowBrowser.isSelected());
        urlRadioButton.setEnabled(cbShowBrowser.isSelected());
		dirField.setEnabled(cbShowBrowser.isSelected() && dirRadioButton.isSelected());
        browseButton.setEnabled(cbShowBrowser.isSelected() && dirRadioButton.isSelected());
        urlField.setEnabled(cbShowBrowser.isSelected()&& urlRadioButton.isSelected());
        */
	}
	
	
	public void actionPerformed(ActionEvent e) {	
		doActionPerformed(e.getSource());		
	}
	
	private void doActionPerformed(Object source) {	
		
		if (source == cbShowGrid) {
			view.setShowGrid(cbShowGrid.isSelected());			
		}
		
		else if (source == cbShowRowHeader) {
			view.setShowRowHeader(cbShowRowHeader.isSelected());			
		}
			
		else if (source == cbShowColumnHeader) {
			view.setShowColumnHeader(cbShowColumnHeader.isSelected());
		}
		
		else if (source == cbShowHScrollbar) {
			view.setShowHScrollbar(cbShowHScrollbar.isSelected());
		}
		
		else if (source == cbShowVScrollbar) {
			view.setShowVScrollbar(cbShowVScrollbar.isSelected());
		}
		
		else if (source == cbShowBrowser) {
			view.setShowBrowserPanel(cbShowBrowser.isSelected());
		}
		
		else if (source == dirRadioButton) {
			dirField.selectAll();
			view.getBrowserPanel().setDirectory(new File(dirField.getText()));
		}
		
		else if (source == urlRadioButton) {
			urlField.selectAll();
			try {
				view.getBrowserPanel().setDirectory(new URL(urlField.getText()));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
		
		}
		
		else if (source == browseButton) {
			System.out.println("browse button");
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				view.getBrowserPanel().setDirectory(fc.getSelectedFile());
				dirField.setText(fc.getSelectedFile().getName());
			}
		}
		
		
		
		updateGUI();
		
	}
	

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		doActionPerformed(e.getSource());
	}


}
