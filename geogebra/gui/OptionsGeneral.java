package geogebra.gui;

import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.main.Application;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * General options.
 * 
 * @author Florian Sonner
 */
public class OptionsGeneral extends JPanel {
	/** */
	private static final long serialVersionUID = 1L;
	
	/**
	 * An instance of the GeoGebra application.
	 */
	private Application app;
	
	/**
	 * The tabbed pane which contains the single areas which can be
	 * edited using this panel.
	 */
	private JTabbedPane tabbedPane;
	
	/**
	 * Construct a panel for the general options which is divided using tabs.
	 * 
	 * @param app
	 */
	public OptionsGeneral(Application app) {
		this.app = app;
		
		initGUI();
		updateGUI();
	}
	
	/**
	 * Initialize the GUI.
	 */
	private void initGUI() {
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("", new JPanel());
		tabbedPane.addTab("", new JPanel());
		tabbedPane.addTab("", new JPanel());
		
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
	}
	
	/**
	 * Update the GUI to take care of new settings which were applied.
	 */
	public void updateGUI() {
		// TODO Hide tabs for applets (F.S.)
	}
	
	/**
	 * Update the labels of the current panel. Should be applied if the
	 * language was changed. Will be called after initialization automatically.
	 */
	public void setLabels() {
		tabbedPane.setTitleAt(0, app.getMenu("General"));
		tabbedPane.setTitleAt(1, app.getPlain("Display"));
		
		//G.Sturr 2010-3-20: removed spreadsheet tab and renumbered
		//tabbedPane.setTitleAt(2, app.getPlain("Spreadsheet"));
		tabbedPane.setTitleAt(2, app.getMenu("Export"));
		
	}
	
	/**
	 * Save the settings of this panel.
	 */
	public void apply() {
		
	}
}
