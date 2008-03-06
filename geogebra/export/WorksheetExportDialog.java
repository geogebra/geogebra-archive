/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.export;

import geogebra.Application;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.GeoGebraPreferences;
import geogebra.gui.InputPanel;
import geogebra.gui.TitlePanel;
import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.util.Util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 * Dialog which provides for exporting into an HTML page 
 * enriched with an Applet.
 * 
 * @author Markus Hohenwarter
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 */
public class WorksheetExportDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final int BUTTON_WIDTH = 200;
	private static final int BUTTON_HEIGHT = 40;
	private static final int DEFAULT_HTML_PAGE_WIDTH = 600;
	private static final int DEFAULT_APPLET_WIDTH = 600;
	private static final int DEFAULT_APPLET_HEIGHT = 500;	

	private Application app;
	private Kernel kernel;
	private InputPanel textAbove, textBelow;
	private JCheckBox cbShowFrame, cbEnableRightClick, cbShowResetIcon,
					cbShowMenuBar, cbShowToolBar, cbShowToolBarHelp, cbShowInputField,
					cbOnlineArchive;
	private GraphicSizePanel sizePanel;
	private boolean useWorksheet = true, kernelChanged = false;			
	private JTabbedPane tabbedPane;

	public WorksheetExportDialog(Application app) {
		super(app.getFrame(), true);
		this.app = app;
		kernel = app.getKernel();
		
		initGUI();					
	} 
		
	
	/**
	 * Checks if the EuclidianView has a selected rectangle. 
	 * In this case we will automatically move the coord system
	 * to put the selection rectangle into the upper left
	 * corner of the euclidian view.
	 */
	private void checkEuclidianView() {
		EuclidianView ev = app.getEuclidianView();
		
		// 1) selection rectangle
		Rectangle rect = ev.getSelectionRectangle();
		if (rect != null) {
			double xZero = ev.getXZero() - rect.x;
			double yZero = ev.getYZero() - rect.y;
			rect.x = 0;
			rect.y = 0;			
			ev.setCoordSystem(xZero, yZero, ev.getXscale(), ev.getYscale(), true);

			// update size panel
			int width = sizePanel.getSelectedWidth() - (ev.getWidth() - rect.width);
			int height = sizePanel.getSelectedHeight() - (ev.getHeight() - rect.height);
			sizePanel.setValues(width, height, false);												
		}
	}

	private void initGUI() {	
		
		// title, author, date
		TitlePanel tp = new TitlePanel(app);
		ActionListener kernelChangedListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				kernelChanged = true;
			}
		};
		tp.addActionListener(kernelChangedListener);
		tp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tabbedPane.addTab(app.getMenu("General"), createGeneralSettingsTab());
		tabbedPane.addTab(app.getMenu("Advanced"), createAdvancedSettingsTab());

		// Cancel and Export Button
		JButton cancelButton = new JButton(app.getPlain("Cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		JButton exportButton = new JButton(app.getMenu("Export"));
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				Thread runner = new Thread() {
					public void run() {
						setVisible(false);
						if (kernelChanged)
							app.storeUndoInfo();
						exportHTML();
					}
				};
				runner.start();
			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(exportButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		buttonPanel.add(cancelButton);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tp, BorderLayout.NORTH);		
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		Util.registerForDisposeOnEscape(this);

		setTitle(app.getMenu("Export") + ": "
				+ app.getPlain("DynamicWorksheet") + " ("
				+ Application.FILE_EXT_HTML + ")");
		setResizable(false);
		centerOnScreen();
	}
	
	private void loadPreferences() {
		try {
	    	cbEnableRightClick.setSelected( Boolean.valueOf(GeoGebraPreferences.loadPreference(
	    			GeoGebraPreferences.EXPORT_WS_RIGHT_CLICK, "false")).booleanValue() );
	    	cbShowResetIcon.setSelected( Boolean.valueOf(GeoGebraPreferences.loadPreference(
	    			GeoGebraPreferences.EXPORT_WS_RESET_ICON, "false")).booleanValue() );
	    	cbShowFrame.setSelected( Boolean.valueOf(GeoGebraPreferences.loadPreference(
	    			GeoGebraPreferences.EXPORT_WS_FRAME_POSSIBLE, "false")).booleanValue() );
	    	cbShowMenuBar.setSelected( Boolean.valueOf(GeoGebraPreferences.loadPreference(
	    			GeoGebraPreferences.EXPORT_WS_SHOW_MENUBAR, "false")).booleanValue() );
	    	cbShowToolBar.setSelected( Boolean.valueOf(GeoGebraPreferences.loadPreference(
	    			GeoGebraPreferences.EXPORT_WS_SHOW_TOOLBAR, "false")).booleanValue() );
	    	cbShowToolBarHelp.setSelected( Boolean.valueOf(GeoGebraPreferences.loadPreference(
	    			GeoGebraPreferences.EXPORT_WS_SHOW_TOOLBAR_HELP, "false")).booleanValue() );
	    	cbShowToolBarHelp.setEnabled(cbShowToolBar.isSelected());
	    	cbShowInputField.setSelected( Boolean.valueOf(GeoGebraPreferences.loadPreference(
	    			GeoGebraPreferences.EXPORT_WS_SHOW_INPUT_FIELD, "false")).booleanValue() );
	    	cbOnlineArchive.setSelected( Boolean.valueOf(GeoGebraPreferences.loadPreference(
	    			GeoGebraPreferences.EXPORT_WS_ONLINE_ARCHIVE, "false")).booleanValue() );
	    	
	    	addHeight();
	    
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	private void addHeight() {
		int height = 0;
		
		if (cbShowToolBar.isSelected()) {
			height += app.getToolBarHeight();			
		}
		if (cbShowMenuBar.isSelected()) {
			height += app.getMenuBarHeight();
		}
		if (cbShowInputField.isSelected()) {
			height +=app.getAlgebraInputHeight();
		}
		
		sizePanel.setValues(sizePanel.getSelectedWidth(), 
				sizePanel.getSelectedHeight() + height, 
				false);	
	}
    
    private void savePreferences() {    	    	
    	GeoGebraPreferences.savePreference(GeoGebraPreferences.EXPORT_WS_RIGHT_CLICK, Boolean.toString(cbEnableRightClick.isSelected()));
    	GeoGebraPreferences.savePreference(GeoGebraPreferences.EXPORT_WS_RESET_ICON, Boolean.toString(cbShowResetIcon.isSelected()));    	    	
    	GeoGebraPreferences.savePreference(GeoGebraPreferences.EXPORT_WS_FRAME_POSSIBLE, Boolean.toString(cbShowFrame.isSelected()));
    	GeoGebraPreferences.savePreference(GeoGebraPreferences.EXPORT_WS_SHOW_MENUBAR, Boolean.toString(cbShowMenuBar.isSelected()));
    	GeoGebraPreferences.savePreference(GeoGebraPreferences.EXPORT_WS_SHOW_TOOLBAR, Boolean.toString(cbShowToolBar.isSelected()));
    	GeoGebraPreferences.savePreference(GeoGebraPreferences.EXPORT_WS_SHOW_TOOLBAR_HELP, Boolean.toString(cbShowToolBarHelp.isSelected()));
    	GeoGebraPreferences.savePreference(GeoGebraPreferences.EXPORT_WS_SHOW_INPUT_FIELD, Boolean.toString(cbShowInputField.isSelected()));    
    	GeoGebraPreferences.savePreference(GeoGebraPreferences.EXPORT_WS_ONLINE_ARCHIVE, Boolean.toString(cbOnlineArchive.isSelected()));        
    }

	/**
	 * The General Settings Tab contains some of the more general settings.
	 */
	private JPanel createGeneralSettingsTab() {
		JPanel tab = new JPanel(new BorderLayout(5, 5));
		tab.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// text areas
		JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
		JLabel label = new JLabel(app.getPlain("TextBeforeConstruction") + ":");
		textAbove = new InputPanel(null, app, 5, 40, true, true);				
		//JScrollPane scrollPane = new JScrollPane(textAbove);

		JPanel p = new JPanel(new BorderLayout());
		p.add(label, BorderLayout.NORTH);
		p.add(textAbove, BorderLayout.CENTER);
		centerPanel.add(p, BorderLayout.NORTH);

		label = new JLabel(app.getPlain("TextAfterConstruction") + ":");
		textBelow = new InputPanel(null, app, 8, 40, true, true);	
		//scrollPane = new JScrollPane(textBelow);
		p = new JPanel(new BorderLayout());
		p.add(label, BorderLayout.NORTH);
		p.add(textBelow, BorderLayout.CENTER);
		centerPanel.add(p, BorderLayout.SOUTH);

		// set line wrapping
		JTextArea ta =  (JTextArea) textAbove.getTextComponent();
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		
		ta =  (JTextArea) textBelow.getTextComponent();
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);

		// init text areas
		Construction cons = kernel.getConstruction();
		String text = cons.getWorksheetText(0);
		if (text.length() > 0)
			textAbove.setText(text);
		text = cons.getWorksheetText(1);
		if (text.length() > 0)
			textBelow.setText(text);

		// action listener for radio buttons
		ActionListener lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				useWorksheet = cmd.equals("worksheet");
				updateEnabledStates();
			}
		};

		// applet panel:
		// radio buttons for dynamic worksheet and open button
		JPanel appletPanel = new JPanel(new BorderLayout());

		appletPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5,
				5)));
		ButtonGroup bg = new ButtonGroup();
		JRadioButton rb = new JRadioButton(app.getPlain("DynamicWorksheet"));
		rb.setActionCommand("worksheet");
		rb.addActionListener(lst);
		rb.setSelected(true);
		bg.add(rb);
		appletPanel.add(rb, BorderLayout.NORTH);

		// open button
		rb = new JRadioButton(app.getPlain("OpenButton"));
		rb.setActionCommand("openButton");
		rb.addActionListener(lst);
		bg.add(rb);			
		appletPanel.add(rb, BorderLayout.SOUTH);
		
		centerPanel.add(appletPanel, BorderLayout.CENTER);
		tab.add(centerPanel, BorderLayout.CENTER);		

		return tab;
	}

	private JPanel createAdvancedSettingsTab() {
		JPanel tab = new JPanel();
		tab.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tab.setLayout(new BoxLayout(tab, BoxLayout.Y_AXIS));				
				
		// functionality panel
		JPanel funcPanel = new JPanel();
		funcPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Functionality")));
		funcPanel.setLayout(new BoxLayout(funcPanel, BoxLayout.Y_AXIS));
		tab.add(funcPanel);				
		
		// enable right click
		cbEnableRightClick = new JCheckBox(app.getMenu("EnableRightClick"));	
		cbEnableRightClick.setEnabled(true);
		funcPanel.add(cbEnableRightClick);	
		
		// showResetIcon
		cbShowResetIcon = new JCheckBox(app.getMenu("ShowResetIcon"));		
		funcPanel.add(cbShowResetIcon);
		
		// framPossible
		cbShowFrame = new JCheckBox(app.getPlain("DoubleClickToOpen"));		
		funcPanel.add(cbShowFrame);
		
		// GUI panel
		JPanel guiPanel = new JPanel();
		guiPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("UserInterface")));
		guiPanel.setLayout(new BoxLayout(guiPanel, BoxLayout.Y_AXIS));
		tab.add(guiPanel);
		
		// showMenuBar
		cbShowMenuBar = new JCheckBox(app.getMenu("ShowMenuBar"));		
		guiPanel.add(cbShowMenuBar);				
		
		// showToolBar
		cbShowToolBar = new JCheckBox(app.getMenu("ShowToolBar"));		
		guiPanel.add(cbShowToolBar);
		
		// showToolBarHelp				
		cbShowToolBarHelp = new JCheckBox(app.getMenu("ShowToolBarHelp"));
		cbShowToolBarHelp.setEnabled(cbShowToolBar.isSelected());
		JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,0));
		tempPanel.add(Box.createHorizontalStrut(20));
		tempPanel.add(cbShowToolBarHelp);
		tempPanel.setAlignmentX(LEFT_ALIGNMENT);
		guiPanel.add(tempPanel);
		
				
		// showAlgebraInput
		cbShowInputField = new JCheckBox(app.getMenu("ShowInputField"));		
		guiPanel.add(cbShowInputField);
		
		// width and height of applet, info about double clicking
		int width, height;
		JPanel appCP = app.getCenterPanel();
		if (appCP != null) {
			width = appCP.getWidth();
			height = appCP.getHeight();
		} else {
			width = DEFAULT_APPLET_WIDTH;
			height = DEFAULT_APPLET_HEIGHT;
		}		
		sizePanel = new GraphicSizePanel(app, width, height, false);
		sizePanel.setAlignmentX(LEFT_ALIGNMENT);
		guiPanel.add(sizePanel);
		
		// Applet panel
		JPanel appletPanel = new JPanel();
		appletPanel.setBorder(BorderFactory.createTitledBorder("Java Applet"));
		appletPanel.setLayout(new BoxLayout(appletPanel, BoxLayout.Y_AXIS));
		tab.add(appletPanel);
		
		// showAlgebraInput
		cbOnlineArchive = new JCheckBox("archive=\"http://www.geogebra.org/webstart/geogebra.jar\"");		
		appletPanel.add(cbOnlineArchive);
		
		
		ActionListener heightChanger = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JCheckBox src = (JCheckBox) ae.getSource();				
				
				int heightChange = 0;
				if (src == cbShowToolBar) {
					heightChange = app.getToolBarHeight();
					cbShowToolBarHelp.setEnabled(cbShowToolBar.isSelected());
				}
				else if (src == cbShowMenuBar) {
					heightChange = app.getMenuBarHeight();
				}
				else if (src == cbShowInputField) {
					heightChange = app.getAlgebraInputHeight();
				}
				
				if (!src.isSelected())
					heightChange = -heightChange;
				
				sizePanel.setValues(sizePanel.getSelectedWidth(), 
									sizePanel.getSelectedHeight() + heightChange, 
									false);
			}
		};
		
		cbShowToolBar.addActionListener(heightChanger);
		cbShowMenuBar.addActionListener(heightChanger);
		cbShowInputField.addActionListener(heightChanger);
		
		return tab;
	}
	
	/**
	 * Appends all selected applet parameters
	 */
	private void appendAppletParameters(StringBuffer sb) {
		// framePossible (double click opens GeoGebra window)
		sb.append("\t<param name=\"framePossible\" value=\"");
		sb.append(cbShowFrame.isSelected());
		sb.append("\"/>\n");
		
		// showResetIcon
		sb.append("\t<param name=\"showResetIcon\" value=\"");
		sb.append(cbShowResetIcon.isSelected());
		sb.append("\"/>\n");
			
		// enable right click
		sb.append("\t<param name=\"enableRightClick\" value=\"");
		sb.append(cbEnableRightClick.isSelected());
		sb.append("\"/>\n");
		
		// showMenuBar
		sb.append("\t<param name=\"showMenuBar\" value=\"");
		sb.append(cbShowMenuBar.isSelected());
		sb.append("\"/>\n");
		
		// showToolBar
		sb.append("\t<param name=\"showToolBar\" value=\"");
		sb.append(cbShowToolBar.isSelected());
		sb.append("\"/>\n");
					
		// showToolBarHelp
		sb.append("\t<param name=\"showToolBarHelp\" value=\"");
		sb.append(cbShowToolBarHelp.isSelected());
		sb.append("\"/>\n");
		
		// showAlgebraInput
		sb.append("\t<param name=\"showAlgebraInput\" value=\"");
		sb.append(cbShowInputField.isSelected());
		sb.append("\"/>\n");	
	}

	public void setVisible(boolean flag) {
		if (flag) {
			checkEuclidianView();
			loadPreferences();
			super.setVisible(true);
		} else {
			// store the texts of the text ares in
			// the current construction
			Construction cons = kernel.getConstruction();
			cons.setWorksheetText(textAbove.getText(), 0);
			cons.setWorksheetText(textBelow.getText(), 1);
		
			savePreferences();
			super.setVisible(false);
		}		
	}

	private void updateEnabledStates() {				
		tabbedPane.setEnabledAt(1, useWorksheet);
	}

	private void centerOnScreen() {
		pack();
		setLocationRelativeTo(app.getFrame());
	}

	/**
	 * Exports construction as html worksheet and returns success state
	 */
	private void exportHTML() {
		File htmlFile = null;

		File currFile = Application.removeExtension(app.getCurrentFile());
		if (currFile != null)
			htmlFile = Application
					.addExtension(currFile, Application.FILE_EXT_HTML);

		htmlFile = app.showSaveDialog(Application.FILE_EXT_HTML, htmlFile, app
				.getPlain("html")
				+ " " + app.getMenu("Files"));
		if (htmlFile == null)
			return;
		
		try {
			// save construction file
			// as file_worksheet.ggb
			String ggbFileName = Application.removeExtension(htmlFile).getName()
					+ ".ggb";
			final File ggbFile = new File(htmlFile.getParent(), ggbFileName);
			app.getXMLio().writeGeoGebraFile(ggbFile);

			// write html string to file
			FileWriter fw = new FileWriter(htmlFile);
			fw.write(getHTML(ggbFile));
			fw.close();

			final File HTMLfile = htmlFile;
			// copy files and open browser
			Thread runner = new Thread() {
	    		public void run() {    
	    			try {
		    			//copy jar to same directory as ggbFile
	    				if (!cbOnlineArchive.isSelected()) 
	    					app.copyJarsTo(HTMLfile.getParent(), cbShowMenuBar.isSelected());
		    			
		    			// open html file in browser
		    			app.showURLinBrowser(HTMLfile.toURL());
	    			} catch (Exception ex) {			
	    				app.showError("SaveFileFailed");
	    				System.err.println(ex.toString());
	    			} 
	    		}
			};
			runner.start();
						
		} catch (Exception ex) {			
			app.showError("SaveFileFailed");
			System.err.println(ex.toString());
		} 
	}

	/**
	 * Returns a html page with the applet included
	 * 
	 * @param ggbFile
	 *            construction File
	 */
	private String getHTML(File ggbFile) {
		StringBuffer sb = new StringBuffer();

		// applet width
		int appletWidth, appletHeight;
		if (!useWorksheet) { // change width and height for open button
			appletWidth = BUTTON_WIDTH;
			appletHeight = BUTTON_HEIGHT;
		} else {
			appletWidth = sizePanel.getSelectedWidth();
			appletHeight = sizePanel.getSelectedHeight();
		}

		// width for table
		int pageWidth = Math.max(appletWidth, DEFAULT_HTML_PAGE_WIDTH);
		
		// xhtml header
		// Michael Borcherds 2008-03-06
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n");
		sb.append("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");

		sb.append("<head>\n");
		sb.append("<title>");
		Construction cons = kernel.getConstruction();
		String title = cons.getTitle();
		if (!title.equals("")) {
			sb.append(Util.toHTMLString(title));
			sb.append(" - ");
		}
		sb.append(Util.toHTMLString(app.getPlain("ApplicationName") + " "
				+ app.getPlain("DynamicWorksheet")));
		sb.append("</title>\n");
		// charset
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html charset=utf-8\"/>\n");
		// sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html charset=iso-8859-1\"/>\n");
		
		sb.append("<meta name=\"generator\" content=\"GeoGebra\"/>\n");
		String css = app.getSetting("cssDynamicWorksheet");
		if (css != null) {
			sb.append(css);
			sb.append("\n");
		}
		sb.append("</head>\n");

		sb.append("<body>\n");
		sb.append("<table border=\"0\" width=\"" + pageWidth + "\">\n");
		sb.append("<tr><td>\n");

		// header with title
		if (!title.equals("")) {
			sb.append("<h2>");
			sb.append(Util.toHTMLString(title));
			sb.append("</h2>\n");
		}

		// text before applet
		String text = textAbove.getText();
		if (text != null) {
			sb.append("<p>\n");
			sb.append(Util.toHTMLString(text));
			sb.append("</p>\n");
		}

		// include applet
		sb.append("\n<applet name=\"ggbApplet\" code=\"geogebra.GeoGebraApplet\"");
		sb.append(" codebase=\"./\"");
		
		if (cbOnlineArchive.isSelected()) {
			// use online geogebra.jar
			sb.append(" archive=\"http://www.geogebra.org/webstart/geogebra.jar\"");
		} else {
			// use local geogebra.jar
			sb.append(" archive=\"geogebra.jar\"");
		}
				
		sb.append(" width=\"");
		sb.append(appletWidth);
		sb.append("\" height=\"");
		sb.append(appletHeight);
		sb.append("\">\n");

		// parameters
		sb.append("\t<param name=\"filename\" value=\"");
		sb.append(ggbFile.getName());
		sb.append("\"/>\n");

		if (useWorksheet) {
			appendAppletParameters(sb);			
		} else {// button type
			sb.append("\t<param name=\"type\" value=\"button\"/>\n");
			// white background
			sb.append("\t<param name=\"bgcolor\" value=\"#FFFFFF\"/>\n");
		}

		sb.append("Sorry, the GeoGebra Applet could not be started. Please make sure that ");
		sb.append("Java 1.4.2 (or later) is installed and active in your browser ");
		sb.append("(<a href=\"http://java.sun.com/getjava\">Click here to install Java now</a>)\n");
		sb.append("</applet>\n\n");

		// text after applet
		text = textBelow.getText();
		if (text != null) {
			sb.append("<p>\n");
			sb.append(Util.toHTMLString(text));
			sb.append("</p>\n");
		}

		// footer
		// author and date information for footer
		String author = cons.getAuthor();
		String date = cons.getDate();
		String line = null;
		if (!author.equals("")) {
			line = author;
		}
		if (!date.equals("")) {
			if (line == null)
				line = date;
			else
				line = line + ", " + date;
		}

		sb.append("<p>");
		sb.append("<span style=\"font-size:small\">");
		if (line != null) {
			sb.append(Util.toHTMLString(line));
			sb.append(", ");
		}
		sb.append(app.getCreatedWithHTML());
		sb.append("</span>");
		sb.append("</p>");

		sb.append("</td></tr>\n");
		sb.append("</table>");
		sb.append("</body>\n");
		sb.append("</html>");

		return sb.toString();
	}

}
