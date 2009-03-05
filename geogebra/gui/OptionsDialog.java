package geogebra.gui;

import java.awt.BorderLayout;
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

import geogebra.main.Application;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
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
 * 
 * @author Florian Sonner
 */
public class OptionsDialog extends JDialog implements WindowListener {
	private static final long serialVersionUID = 1L;

	private Application app;

	private JTabbedPane tabbedPane;

	private JPanel defaultsPanel;

	private OptionsFont fontPanel;
	private OptionsEuclidian euclidianPanel;

	private JButton applyButton;
	private JButton closeButton;

	/**
	 * Initialize the GUI and logics.
	 * 
	 * @param app
	 */
	public OptionsDialog(Application app) {
		super(app.getFrame(), true);

		this.app = app;

		setResizable(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(this);

		initGUI();
	}

	/**
	 * Update the GUI.
	 */
	public void updateGUI() {
		fontPanel.updateGUI();
		euclidianPanel.updateGUI();
	}

	/**
	 * Select the tab which shows the euclidian view settings.
	 */
	public void showEuclidianTab() {
		tabbedPane.setSelectedIndex(3);
	}

	/**
	 * Initialize the GUI.
	 */
	private void initGUI() {
		setLayout(new BorderLayout());

		// init tabs
		defaultsPanel = new JPanel();
		fontPanel = new OptionsFont(app);
		euclidianPanel = new OptionsEuclidian(app, app.getEuclidianView());

		// init scroll panes for tabs (show no extra borders)
		JScrollPane defaultsPanelScroll = new JScrollPane(defaultsPanel);
		defaultsPanelScroll.setBorder(BorderFactory.createEmptyBorder());
		JScrollPane fontsAndLangPanelScroll = new JScrollPane(fontPanel);
		fontsAndLangPanelScroll.setBorder(BorderFactory.createEmptyBorder());
		JScrollPane euclidianPanelScroll = new JScrollPane(euclidianPanel);
		euclidianPanelScroll.setBorder(BorderFactory.createEmptyBorder());

		// init tabbing pane
		tabbedPane = new OptionsTabbedPane();
		tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif",
				SystemColor.RED), new JPanel());
		tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif",
				SystemColor.RED), defaultsPanelScroll);
		tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif",
				SystemColor.RED), fontsAndLangPanelScroll);
		tabbedPane.addTab("", app.getToolBarImage("mode_delete_32.gif",
				SystemColor.RED), euclidianPanelScroll);

		add(tabbedPane, BorderLayout.CENTER);

		// init close button
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		applyButton = new JButton();
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				apply();
			}
		});
		buttonPanel.add(applyButton);
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
	 * 
	 * TODO use real phrases (F.S.)
	 */
	public void setLabels() {
		setTitle(app.getMenu("Options"));

		closeButton.setText(app.getMenu("Close"));
		applyButton.setText(app.getPlain("Apply"));

		tabbedPane.setTitleAt(0, app.getPlain("General"));
		tabbedPane.setTitleAt(1, app.getPlain("Defaults"));
		tabbedPane.setTitleAt(2, app.getPlain("FontsAndLanguage"));
		tabbedPane.setTitleAt(3, app.getPlain("DrawingPad"));

		euclidianPanel.setLabels();
		fontPanel.setLabels();
	}

	/**
	 * Apply settings which are not applied directly after changing a value.
	 * 
	 * TODO Save permanent settings
	 */
	private void apply() {
		fontPanel.apply();
	}

	/**
	 * Close the dialog.
	 */
	private void closeDialog() {
		apply();

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
		private static final long serialVersionUID = 1L;
		
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
	 * @see http://blog.elevenworks.com/?p=4 How to customize JTabbedPanes with custom UIs.
	 */
	class OptionsTabbedPaneUI extends BasicTabbedPaneUI {
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
			g.setColor((isSelected ? SystemColor.controlHighlight
					: SystemColor.text));
			g.fillRect(x, y, w, h);
		}

		/**
		 * Fill the background with white.
		 */
		protected void paintTabArea(Graphics g, int tabPlacement,
				int selectedIndex) {
			int tw = tabPane.getBounds().width;

			g.setColor(SystemColor.text);
			g.fillRect(0, 0, tw, rects[0].height);

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
			return fontHeight + 45;
		}

		/**
		 * Reduce the tab width by 32 as the icon is not drawn in one line with
		 * the text.
		 */
		protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
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

		/**
		 * A small inset for the content area.
		 */
		protected Insets getContentBorderInsets(int tabPlacement) {
			return new Insets(3, 3, 3, 3);
		}

		/**
		 * Initialization of default values.
		 */
		protected void installDefaults() {
			super.installDefaults();
			tabAreaInsets.set(0, 4, 0, 4);
			selectedTabPadInsets.set(0, 0, 0, 0);
			tabInsets.set(5, 5, 5, 5);
		}
		
		/**
		 * Update the font.
		 */
		public void updateFont() {
			LookAndFeel.installColorsAndFont(tabPane, "TabbedPane.background",
                    "TabbedPane.foreground",
                    "TabbedPane.font");
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
