package geogebra.gui;

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
		tabbedPane = new OptionsTabbedPane();
		
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

	/**
	 * Tabbed pane for the options dialog.
	 * 
	 * This tabbed pane will just use the special OptionsTabbedPaneUI class for
	 * it's UI. It's required to reconstruct the UI object if updateUI() is
	 * called (e.g. because the font size changed).
	 * 
	 * @author Florian Sonner
	 */
	class OptionsTabbedPane extends JTabbedPane {
		/** */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Set the UI of this component to the OptionsTabbedPaneUI.
		 */
		public OptionsTabbedPane() {
			setUI(new OptionsTabbedPaneUI());
		}
		
		/**
		 * Ignore any non OptionsTabbedPaneUI objects.
		 */
		public void setUI(TabbedPaneUI ui) {
			if(ui instanceof OptionsTabbedPaneUI) {
				super.setUI(ui);
			}
		}
		
		/**
		 * Update the UI of this component.
		 * 
		 * This will lead to an update of the fonts of the UI as just the font size
		 * should change.
		 */
		public void updateUI() {
			if(ui instanceof OptionsTabbedPaneUI) {
				((OptionsTabbedPaneUI)getUI()).updateFont();
			}
		}
	}

	/**
	 * Custom UI for the tabs in the options dialog.
	 * 
	 * @author Florian Sonner
	 */
	class OptionsTabbedPaneUI extends BasicTabbedPaneUI {
		/**
		 * The background color for tabs which are neither active not hovered.
		 */
		private Color bgColor;
		
		/**
		 * The background color of active tabs (i.e. the content of this tab is currently
		 * displayed).
		 */
		private Color bgActiveColor;
		
		/**
		 * The background color of tabs the mouse is over at the moment. Will not apply
		 * to active tabs.
		 */
		private Color bgHoverColor;

		/**
		 * Initialization of default values.
		 */
		protected void installDefaults() {
			super.installDefaults();
			tabAreaInsets = new Insets(0, 15, 0, 15);
			contentBorderInsets = new Insets(3, 3, 3, 3);
			tabInsets = new Insets(10, 10, 10, 10);
			selectedTabPadInsets = new Insets(0, 0, 0, 0);
			
			bgColor = Color.white;
			bgActiveColor = new Color(193, 210, 238);
			bgHoverColor = new Color(224, 232, 246);
		}
		
		/**
		 * Uninstall our custom defaults.
		 */
		protected void uninstallDefaults() {
			super.uninstallDefaults();
			
			bgColor = null;
			bgActiveColor = null;
			bgHoverColor = null;
		}
		
		/**
		 * Update the font.
		 */
		public void updateFont() {
			LookAndFeel.installColorsAndFont(tabPane, "TabbedPane.background",
                    "TabbedPane.foreground",
                    "TabbedPane.font");
		}
		
		/**
		 * Paint the tab border.
		 */
		protected void paintTabBorder(Graphics g, int tabPlacement,
				int tabIndex, int x, int y, int w, int h, boolean isSelected) {
			g.setColor(SystemColor.controlShadow);
			g.drawLine(x, y, x, y + h - 1);
			g.drawLine(x + w, y, x + w, y + h - 1);
		}

		/**
		 * Paint the background of the tabs.
		 */
		protected void paintTabBackground(Graphics g, int tabPlacement,
				int tabIndex, int x, int y, int w, int h, boolean isSelected) {
			g.setColor(isSelected ? bgActiveColor : (tabIndex == getRolloverTab() ? bgHoverColor : bgColor));
			g.fillRect(x, y, w, h);
		}
		
		/**
		 * Repaint the tabbed pane if the mouse is hovering a new tab.
		 */
		protected void setRolloverTab(int index) {
			if(getRolloverTab() != index) {
				super.setRolloverTab(index);
				repaint();
			}
		}

		/**
		 * Fill the background with white.
		 */
		protected void paintTabArea(Graphics g, int tabPlacement,
				int selectedIndex) {
			g.setColor(Color.white);
			
			g.fillRect(0, 0,
				tabPane.getBounds().width,
				calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight)
			);

			super.paintTabArea(g, tabPlacement, selectedIndex);
		}

		/**
		 * Use a custom layout for the label (icon centered, text below icon).
		 * 
		 * Copy 'n' paste from the original source of BasicTabbedPaneUI. 
		 */
		protected void layoutLabel(int tabPlacement, FontMetrics metrics,
				int tabIndex, String title, Icon icon, Rectangle tabRect,
				Rectangle iconRect, Rectangle textRect, boolean isSelected) {
			textRect.x = 0;
			textRect.y = 0;
			textRect.width = 0;
			textRect.height = 0;
			iconRect.x = 0;
			iconRect.y = 0;
			iconRect.width = 0;
			iconRect.height = 0;
			
			// -- just this has to be changed to change the layout of the tabs --
			SwingUtilities.layoutCompoundLabel(tabPane, metrics, title, icon,
					SwingConstants.CENTER, SwingConstants.CENTER,
					SwingConstants.BOTTOM, SwingConstants.CENTER, tabRect,
					iconRect, textRect, textIconGap);

			int shiftX = getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
			int shiftY = getTabLabelShiftY(tabPlacement, tabIndex, isSelected);

			iconRect.x += shiftX;
			iconRect.y += shiftY;

			textRect.x += shiftX;
			textRect.y += shiftY;
		}

		/**
		 * The tab should always have enough space for a 32x32 icon and the
		 * label.
		 */
		protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
			if(!tabbedPane.isEnabledAt(tabIndex))
				return 0;
			
			return fontHeight + 45;
		}

		/**
		 * Reduce the tab width by 32 as the icon is not drawn in one line with
		 * the text.
		 */
		protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
			if(!tabbedPane.isEnabledAt(tabIndex))
				return 0;
			
			return super.calculateTabWidth(tabPlacement, tabIndex, metrics) - 32;
		}

		/**
		 * Do not move the label if we select a tab (always return 0 as shift).
		 */
		protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected) {
			return 0;
		}

		/**
		 * Paint the top border.
		 */
		protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
			g.setColor(SystemColor.controlDkShadow);
			g.drawLine(x, y, x + w, y);
			g.setColor(SystemColor.controlLtHighlight);
			g.drawLine(x, y + 1, x + w, y + 1);
		}

		protected void paintFocusIndicator(Graphics g, int tabPlacement,
				Rectangle[] rects, int tabIndex, Rectangle iconRect,
				Rectangle textRect, boolean isSelected) {
			/* paint nothing.. */
		}

		protected void paintContentBorderRightEdge(Graphics g,
				int tabPlacement, int selectedIndex, int x, int y, int w, int h) { 
			/* paint nothing */
		}

		protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
				int selectedIndex, int x, int y, int w, int h) {
			/* paint nothing */
		}

		protected void paintContentBorderBottomEdge(Graphics g,
				int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
			/* paint nothing */
		}
	}
}
