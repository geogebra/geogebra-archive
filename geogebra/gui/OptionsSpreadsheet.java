/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui;

import geogebra.gui.view.spreadsheet.FileBrowserPanel;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
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
	cbShowColumnHeader, cbShowHScrollbar,  cbShowVScrollbar, 
	cbShowBrowser, cbAllowSpecialEditor, cbAllowToolTips, cbPrependCommands;
	
	private JTextField dirField, urlField;
	private JButton browseButton, restoreButton, setCurrentButton;
	private JRadioButton dirRadioButton, urlRadioButton;
	private JPanel locationPanel;
	
	
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
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		tabbedPane.addTab(app.getMenu("Layout"),null, new JScrollPane(buildLayoutOptionsPanel()));
		if(app.hasFullPermissions())
			tabbedPane.addTab(app.getMenu("Browser"),null, new JScrollPane(buildBrowserOptionsPanel()));
		add(tabbedPane);
		
		
	    
	}
	
	
	private JPanel buildLayoutOptionsPanel() {
        		 
		JPanel layoutOptions = new JPanel();
		layoutOptions.setLayout(new BoxLayout(layoutOptions, BoxLayout.Y_AXIS));

		layoutOptions.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));	
		

        cbShowGrid = new JCheckBox();  
        cbShowGrid.addActionListener(this);        
        layoutOptions.add(cbShowGrid); 
        
        cbShowColumnHeader = new JCheckBox();  
        cbShowColumnHeader.addActionListener(this);
        layoutOptions.add(cbShowColumnHeader); 
        
        cbShowRowHeader = new JCheckBox();  
        cbShowRowHeader.addActionListener(this);
        layoutOptions.add(cbShowRowHeader); 
        
        cbShowHScrollbar = new JCheckBox();  
        cbShowHScrollbar.addActionListener(this);
        layoutOptions.add(cbShowHScrollbar); 
        
        cbShowVScrollbar = new JCheckBox();  
        cbShowVScrollbar.addActionListener(this);
        layoutOptions.add(cbShowVScrollbar); 
     
        /*
        cbShowBrowser = new JCheckBox();  
        cbShowBrowser.addActionListener(this);        
        layoutOptions.add(cbShowBrowser);
        */
        
        cbAllowSpecialEditor = new JCheckBox();
        cbAllowSpecialEditor.addActionListener(this);        
        layoutOptions.add(cbAllowSpecialEditor);
        
        cbAllowToolTips = new JCheckBox();
        cbAllowToolTips.addActionListener(this);        
        layoutOptions.add(cbAllowToolTips);
        
        cbPrependCommands = new JCheckBox();
        cbPrependCommands.addActionListener(this);        
        layoutOptions.add(cbPrependCommands);
        
        return layoutOptions;
	}
	
	
	private JPanel buildBrowserOptionsPanel() {
		
		//====================================================
		// create GUI elements
		
        cbShowBrowser = new JCheckBox();  
        cbShowBrowser.addActionListener(this);        
         
        dirRadioButton = new JRadioButton("");
        urlRadioButton = new JRadioButton("");
  
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
        dirField.setEditable(false);
        
        urlField = new MyTextField(app.getGuiManager());
        urlField.setAlignmentX(0.0f);
       // urlField.setMaximumSize(new Dimension(300,20));
        urlField.setColumns(30);
        urlField.addActionListener(this);
        urlField.addFocusListener(this);
        urlField.setEditable(false);
        
        browseButton = new JButton("...", app.getImageIcon("aux_folder.gif"));
        browseButton.addActionListener(this);

        restoreButton = new JButton("");
        restoreButton.addActionListener(this);
        
        setCurrentButton = new JButton("");
        setCurrentButton.addActionListener(this);
        
        //====================================================
        // create sub panels 

        int tab = 15;

        locationPanel = new JPanel();
        locationPanel.setLayout(new BoxLayout(locationPanel, BoxLayout.Y_AXIS));
        locationPanel.add(dirRadioButton);
        
        JPanel dirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dirPanel.setAlignmentX(0.0f);
        dirPanel.add(Box.createHorizontalStrut(tab));
        dirPanel.add(dirField);      
       // dirPanel.add(browseButton);
        
        locationPanel.add(dirPanel);
        
        locationPanel.add(Box.createVerticalStrut(tab));
        
        locationPanel.add(urlRadioButton);
        JPanel urlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urlPanel.setAlignmentX(0.0f);
        urlPanel.add(Box.createHorizontalStrut(tab));
        urlPanel.add(urlField); 
        
        locationPanel.add(urlPanel);
        
        JPanel setButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        setButtonPanel.setAlignmentX(LEFT_ALIGNMENT);
        setButtonPanel.add(Box.createHorizontalStrut(2*tab));
        setButtonPanel.add(restoreButton);
        setButtonPanel.add(setCurrentButton);
        locationPanel.add(setButtonPanel);
        
        locationPanel.setBorder(BorderFactory.createTitledBorder(""));
        
        
        //====================================================
        // layout the browser panel
        
        
        JPanel mainPanel = new JPanel();		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));    
        mainPanel.add(cbShowBrowser); 
        mainPanel.add(Box.createVerticalStrut(tab));
        mainPanel.add(locationPanel);
    
        JPanel browserPanel = new JPanel(new BorderLayout());
    	browserPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));	
    	browserPanel.add(mainPanel, BorderLayout.NORTH);
       	
		return browserPanel;
	}
	
	
	/**
	 * Update spreadsheet panel labels. Should be applied if the
	 * language was changed. Will be called after initialization automatically.
	 */
	public void setLabels() {
		
		//TODO -- add labels as needed
		cbShowGrid.setText(app.getMenu("ShowGridlines"));              
		cbShowColumnHeader.setText(app.getMenu("ShowColumnHeader"));  	      
		cbShowRowHeader.setText(app.getMenu("ShowRowHeader"));  	        	        
		cbShowHScrollbar.setText(app.getMenu("ShowHorizontalScrollbars"));          
		cbShowVScrollbar.setText(app.getMenu("ShowVerticalScrollbars"));        
		cbShowBrowser.setText(app.getMenu("ShowFileBrowser"));  
		cbAllowSpecialEditor.setText(app.getMenu("UseButtonsAndCheckboxes"));
		cbAllowToolTips.setText(app.getMenu("AllowTooltips"));
		cbPrependCommands.setText(app.getMenu("RequireEquals"));

		locationPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("HomeDirectory")));
		dirRadioButton.setText(app.getMenu("FileSystem") + ":");
		urlRadioButton.setText(app.getMenu("URL") + ":");
		restoreButton.setText(app.getMenu("Settings.ResetDefault"));
		setCurrentButton.setText(app.getMenu("SetToCurrentLocation"));
	        
	}
	
	/**
	 * Save the settings of this panel.
	 */
	public void apply() {
		//TODO -- add any settings that need changing on Apply button click
		// or after dialog close 
	}
	
	
	
	public void updateGUI() {				
					  
        //======================================
        // layout tab GUI
		
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
        cbShowHScrollbar.setSelected(view.getShowHScrollBar()); 
        cbShowHScrollbar.addActionListener(this);
               
        cbShowVScrollbar.removeActionListener(this);
        cbShowVScrollbar.setSelected(view.getShowVScrollBar()); 
        cbShowVScrollbar.addActionListener(this);
        
      
        cbAllowSpecialEditor.removeActionListener(this);
        cbAllowSpecialEditor.setSelected(view.allowSpecialEditor()); 
        cbAllowSpecialEditor.addActionListener(this);
      
        cbAllowToolTips.removeActionListener(this);
        cbAllowToolTips.setSelected(view.getAllowToolTips()); 
        cbAllowToolTips.addActionListener(this);
        
        cbPrependCommands.removeActionListener(this);
        cbPrependCommands.setSelected(view.isEqualsRequired()); 
        cbPrependCommands.addActionListener(this);
        
        //======================================
        // browser tab GUI
        
        cbShowBrowser.removeActionListener(this);
        cbShowBrowser.setSelected(view.getShowBrowserPanel()); 
        cbShowBrowser.addActionListener(this);
        
        dirRadioButton.removeActionListener(this);
        dirRadioButton.setSelected(view.getInitialBrowserMode() == FileBrowserPanel.MODE_FILE); 
        dirRadioButton.addActionListener(this);
       
        urlRadioButton.removeActionListener(this);
        urlRadioButton.setSelected(view.getInitialBrowserMode() == FileBrowserPanel.MODE_URL); 
        urlRadioButton.addActionListener(this);
        
        dirField.removeActionListener(this);
        dirField.setText(view.getInitialFileString());
        dirField.setCaretPosition(0);
        dirField.addActionListener(this);
        
        urlField.removeActionListener(this);
        urlField.setText(view.getInitialURLString());
        urlField.setCaretPosition(0);
        urlField.addActionListener(this);
        
        // disable/enable
        dirRadioButton.setEnabled(cbShowBrowser.isSelected());
        urlRadioButton.setEnabled(cbShowBrowser.isSelected());
        restoreButton.setEnabled(cbShowBrowser.isSelected());
        setCurrentButton.setEnabled(cbShowBrowser.isSelected());
        
		dirField.setEnabled(cbShowBrowser.isSelected() && dirRadioButton.isSelected());
        browseButton.setEnabled(dirField.isEnabled());
        urlField.setEnabled(cbShowBrowser.isSelected() && urlRadioButton.isSelected());
        
	}
	
	
	public void actionPerformed(ActionEvent e) {	
		doActionPerformed(e.getSource());		
	}
	
	private void doActionPerformed(Object source) {	
		
		//========================================
		// layout options
		
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
			view.setShowHScrollBar(cbShowHScrollbar.isSelected());
		}
		
		else if (source == cbShowVScrollbar) {
			view.setShowVScrollBar(cbShowVScrollbar.isSelected());
		}
			
		else if (source == cbAllowSpecialEditor) {
			view.setAllowSpecialEditor(cbAllowSpecialEditor.isSelected());
		}
		
		else if (source == cbAllowToolTips) {
			view.setAllowToolTips(cbAllowToolTips.isSelected());
		}
		
		else if (source == cbPrependCommands) {
			view.setEqualsRequired(cbPrependCommands.isSelected());
		}
		
		//========================================
		// browser options
				
		else if (source == cbShowBrowser) {
			view.setShowFileBrowser(cbShowBrowser.isSelected());
		}

		else if (source == dirRadioButton) {
			dirField.selectAll();
			view.setInitialFileString(dirField.getText());
			view.setInitialBrowserMode(FileBrowserPanel.MODE_FILE);
			view.setFileBrowserDirectory(dirField.getText(),FileBrowserPanel.MODE_FILE);
		}
		
		else if (source == urlRadioButton) {
			urlField.selectAll();
			view.setFileBrowserDirectory(urlField.getText(), FileBrowserPanel.MODE_URL);
			view.setInitialURLString(urlField.getText());
			view.setInitialBrowserMode(FileBrowserPanel.MODE_URL);

		}

		else if (source == browseButton) {
			//System.out.println("browse button");
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				view.setFileBrowserDirectory(fc.getSelectedFile().getPath(), FileBrowserPanel.MODE_FILE);
				dirField.setText(fc.getSelectedFile().getName());
			}
		}
		
		else if (source == restoreButton) {
			view.setBrowserDefaults(true);
		}
		
		else if (source == setCurrentButton) {
			view.setBrowserDefaults(false);
		}
		
		updateGUI();
		
	}
	
	

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		doActionPerformed(e.getSource());
	}


}
