package geogebra.gui;

import geogebra.gui.util.IconTabbedPane;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.main.Application;
import geogebra.main.GeoGebraPreferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * A central dialog with all important options.
 */
public class OptionsDialog extends JDialog implements WindowListener {
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Index of the defaults tab. 
	 */
	public static final int TAB_DEFAULTS = 0;
	
	/**
	 * Index of the euclidian tab.
	 */
	public static final int TAB_EUCLIDIAN = 1;
	
	/**
	 * Index of the spreadsheet tab.
	 */
	public static final int TAB_SPREADSHEET = 2;

	/**
	 * Index of the CAS tab.
	 */
	public static final int TAB_CAS = 3; 

	/**
	 * Index of the advanced tab. 
	 */
	public static final int TAB_ADVANCED = 4;
	
	/**
	 * An instance of the Application object of this window.
	 */
	private Application app;

	/**
	 * The tabbed pane which is used to switch between the different pages
	 * of the options menu.
	 */
	private JTabbedPane tabbedPane;
	
	/**
	 * The panel where the user can select new default values for
	 * certain objects.
	 */
	private OptionsDefaults defaultsPanel;
	
	/**
	 * The panel with all settings for the euclidian view. The "Drawing Pad Properties" dialog
	 * is not longer used, all settings are stored here for now.
	 */
	private OptionsEuclidian euclidianPanel;
	
	/**
	 * The panel with all settings for the spreadsheet view. 
	 */
	private OptionsSpreadsheet spreadsheetPanel;
	
	/**
	 * The panel with all settings for the CAS view.
	 */
	private OptionsCAS casPanel;

	/**
	 * The panel with general options.
	 */
	private OptionsAdvanced advancedPanel;

	/**
	 * The button to apply settings without closing the window.
	 */
	private JButton saveButton;
	
	/**
	 * The button which closes the window and stores all changes. 
	 */
	private JButton closeButton;

	/**
	 * Initialize the GUI and logics.
	 * 
	 * @param app
	 */
	public OptionsDialog(Application app) {
		super(app.getFrame(), false);

		this.app = app;

		setResizable(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(this);

		initGUI();
		updateGUI();
	}

	/**
	 * Update the GUI.
	 */
	public void updateGUI() {
		defaultsPanel.updateGUI();
		euclidianPanel.updateGUI();
		spreadsheetPanel.updateGUI();
		casPanel.updateGUI();
		advancedPanel.updateGUI();
	}

	/**
	 * Select the tab which shows the euclidian view settings.
	 * 
	 * @param index Index of the tab to hide, use the constants defined in this class for that
	 */
	public void showTab(int index) {
		if(index == TAB_EUCLIDIAN){
			euclidianPanel.setView(app.getGuiManager().getActiveEuclidianView());
		}
		tabbedPane.setSelectedIndex(index);
	}

	/**
	 * Initialize the GUI.
	 */
	private void initGUI() {
		setLayout(new BorderLayout());

		// init tabs
		defaultsPanel = new OptionsDefaults(app);
		euclidianPanel = new OptionsEuclidian(app, app.getEuclidianView());
		spreadsheetPanel = new OptionsSpreadsheet(app, (SpreadsheetView)app.getGuiManager().getSpreadsheetView());
		casPanel = new OptionsCAS(app);
		advancedPanel = new OptionsAdvanced(app);
		
		// init scroll panes for tabs (show no extra borders)
		JScrollPane euclidianPanelScroll = new JScrollPane(euclidianPanel);
		euclidianPanelScroll.setBorder(BorderFactory.createEmptyBorder());

		// init tabbing pane
		tabbedPane = new IconTabbedPane();
		
		// defaults
		tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif",
				Color.RED), defaultsPanel);
		
		// euclidian properties
		tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif",
				Color.RED), euclidianPanelScroll);
		
		// spreadsheet properties
		tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif",
				Color.RED), spreadsheetPanel);
		
		// CAS properties
		tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif",
				Color.RED), casPanel);
		
		// advanced
		tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif",
				Color.RED), advancedPanel);
		
		// disable some tabs for applets
		if(app.isApplet()) {
			tabbedPane.setEnabledAt(TAB_DEFAULTS, false);
		}

		add(tabbedPane, BorderLayout.CENTER);

		// init close button
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, SystemColor.controlDkShadow));
		buttonPanel.setBackground(Color.white);
		
		saveButton = new JButton();
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GeoGebraPreferences.getPref().saveXMLPreferences(app);
			}
		});
		buttonPanel.add(saveButton);
		closeButton = new JButton();
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});
		buttonPanel.add(closeButton);

		add(buttonPanel, BorderLayout.SOUTH);

		setLabels(); // update all labels

		setPreferredSize(new Dimension(640, 480));
		pack();

		setLocationRelativeTo(null);
	}

	/**
	 * Update the labels of the components (e.g. if the language changed).
	 */
	public void setLabels() {
		setTitle(app.getMenu("Settings"));

		closeButton.setText(app.getMenu("Close"));
		saveButton.setText(app.getMenu("Settings.Save"));

		tabbedPane.setTitleAt(TAB_DEFAULTS, app.getPlain("Defaults"));
		tabbedPane.setTitleAt(TAB_EUCLIDIAN, app.getPlain("DrawingPad"));
		tabbedPane.setTitleAt(TAB_SPREADSHEET, app.getPlain("Spreadsheet")); 
		tabbedPane.setTitleAt(TAB_CAS, app.getMenu("CAS")); 
		tabbedPane.setTitleAt(TAB_ADVANCED, app.getMenu("Advanced"));
		
		euclidianPanel.setLabels();
		defaultsPanel.setLabels();
		spreadsheetPanel.setLabels();
		casPanel.setLabels();
		advancedPanel.setLabels();
	}

	/**
	 * Close the dialog.
	 */
	private void closeDialog() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		app.storeUndoInfo();
		setCursor(Cursor.getDefaultCursor());
		setVisible(false);
	}

	/**
	 * Simulate the pressing of the close button if the window is closed.
	 */
	public void windowClosing(WindowEvent e) {
		closeButton.doClick();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
}
