package geogebra.gui.layout;

import geogebra.gui.app.GeoGebraFrame;
import geogebra.io.layout.DockPanelXml;
import geogebra.main.Application;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Every object which should be dragged needs to be of type DockPanel.
 * A DockPanel will wrap around the component with the real contents
 * (e.g. the EuclidianView) and will add a title bar if the user is not in
 * the "layout fixed" mode. The user can move the DockPanel by dragging the
 * title bar.
 * 
 * To add a new dock panel one has to subclass DockPanel, implement the abstract
 * method DockPanel::loadComponent() and maybe replace DockPanel::getIcon() 
 * and DockPanel::getStyleBar().
 * 
 * One can add a panel using Layout::registerPanel(), the GuiManager also provides
 * GuiManager()::initLayoutPanels() as an easy access point to add new panels. This
 * is also important because it matters at which point of execution a panel is added,
 * see Layout::registerPanel() for further information.  
 * 
 * @author Florian Sonner
 */
public abstract class DockPanel extends JPanel implements ActionListener, WindowListener, MouseListener {
	private static final long serialVersionUID = 1L;
	
	private DockManager dockManager;
	private Application app;
	
	/**
	 * The ID of this dock panel.
	 */
	private int id;
	
	/**
	 * The title of this dock panel.
	 */
	private String title;
	
	/**
	 * If this panel is visible.
	 */
	private boolean visible = false;
	
	/**
	 * If this panel has focus.
	 */
	private boolean hasFocus = false;
	
	/**
	 * The dimensions of the external window of this panel.
	 */
	private Rectangle frameBounds = new Rectangle(50, 50, 500, 500);
	
	/**
	 * If this panel should be opened in a frame the next time it's visible.
	 */
	private boolean openInFrame = true;
	
	/**
	 * If there is a style bar associated with this panel.
	 */
	private boolean hasStyleBar = false;
	
	/**
	 * If the style bar is visible.
	 */
	private boolean showStyleBar = false;
	
	/**
	 * String which stores the position of the panel in the layout.
	 */
	private String embeddedDef = "1";
	
	/**
	 * The size of the panel in the layout, may be either the width or height depending upon
	 * embeddedDef. 
	 */
	private int embeddedSize = 150;
	
	/**
	 * The panel at the top where the title and the close button
	 * is displayed normally.
	 */
	private JPanel titlePanel;
	
	/**
	 * The label with the view title.
	 */
	private JLabel titleLabel;
	
	/**
	 * The panel which holds all buttons.
	 */
	private JPanel buttonPanel;
	
	/**
	 * The close button.
	 */
	private JButton closeButton;
	
	/**
	 * Button which opens the panel in a new window.
	 */
	private JButton windowButton;
	
	/**
	 * A button which brings the panel back to the main window.
	 */
	private JButton unwindowButton;
	
	/**
	 * Button used to show / hide the style bar.
	 */
	private JButton toggleStyleBarButton;
	
	/**
	 * Panel for the styling bar if one is available.
	 */
	private JPanel styleBarPanel;
	
	/**
	 * The frame which holds this DockPanel if the DockPanel is opened in
	 * an additional window.
	 */
	private JFrame frame = null;
	
	/**
	 * The component used for this view.
	 */
	private Component component;
	
	/**
	 * The location of this panel in the view menu. If -1 this panel won't appear there at all.
	 */
	private int menuOrder;
	
	/**
	 * Shortcut to show this panel, SHIFT is automatically used as modifier, \u0000 is the default value.
	 */
	private char menuShortcut;
	
	/**
	 * Prepare dock panel. DockPanel::register() has to be called to make this panel fully functional!
	 * No shortcut is assigned to the view in this construtor.
	 * 
	 * @param id 			The id of the panel
	 * @param title			The title phrase of the view located in plain.properties
	 * @param hasStyleBar	If a style bar exists
	 * @param menuOrder		The location of this view in the view menu, -1 if the view should not appear at all
	 */
	public DockPanel(int id, String title, boolean hasStyleBar, int menuOrder) {
		this(id, title, hasStyleBar, menuOrder, '\u0000');
	}
	
	/**
	 * Prepare dock panel. DockPanel::register() has to be called to make this panel fully functional!
	 * 
	 * @param id 			The id of the panel
	 * @param title			The title phrase of the view located in plain.properties
	 * @param hasStyleBar	If a style bar exists
	 * @param menuOrder		The location of this view in the view menu, -1 if the view should not appear at all
	 * @param menuShortcut	The shortcut character which can be used to make this view visible
	 */
	public DockPanel(int id, String title, boolean hasStyleBar, int menuOrder, char menuShortcut) {
		this.id = id;
		this.title = title;
		this.menuOrder = menuOrder;
		this.menuShortcut = menuShortcut;
		this.hasStyleBar = hasStyleBar;
		
		setLayout(new BorderLayout());
	}
	
	/**
	 * @return The icon of the menu item, if this method
	 * 		was not overwritten it will return the empty icon or 
	 * 		null for Win Vista / 7 to prevent the "checkbox bug" 
	 */
	public ImageIcon getIcon() { 
		if(Application.WINDOWS_VISTA_OR_LATER) {
			return null; 
		} else {
			return app.getEmptyIcon();
		}
	}
	
	/**
	 * @return The style bar if one exists
	 */
	protected JComponent loadStyleBar() {
		return null; 
	}
	
	protected abstract JComponent loadComponent();
	
	/**
	 * Bind this view to a dock manager. Also initializes the whole GUI as just
	 * at this point the application is available.
	 * 
	 * @param dockManager
	 */
	public void register(DockManager dockManager) {
		this.dockManager = dockManager;
		this.app = dockManager.getLayout().getApp();
		
		// the meta panel holds both title and style bar panel
		JPanel metaPanel = new JPanel(new BorderLayout());
		
		// Construct title bar and all elements
		titlePanel = new JPanel();
		titlePanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, SystemColor.controlShadow),
				BorderFactory.createEmptyBorder(0, 2, 0, 2)));
		titlePanel.setLayout(new BorderLayout());
		
		titleLabel = new JLabel(app.getPlain(title));
		titleLabel.setFont(app.getPlainFont());
		titlePanel.add(titleLabel, BorderLayout.WEST);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		titlePanel.add(buttonPanel, BorderLayout.EAST);
		
		// Show / hide styling bar if one exists
		if(hasStyleBar) {
			toggleStyleBarButton = new JButton(app.getImageIcon("view-showtoolbar.png"));
			toggleStyleBarButton.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
			toggleStyleBarButton.addActionListener(this);
			toggleStyleBarButton.setFocusPainted(false);
			toggleStyleBarButton.setPreferredSize(new Dimension(16,16));
			buttonPanel.add(toggleStyleBarButton);
		}
		
		// Insert the view in the main window
		unwindowButton = new JButton(app.getImageIcon("view-unwindow.png"));
		unwindowButton.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
		unwindowButton.addActionListener(this);
		unwindowButton.setFocusPainted(false);
		unwindowButton.setPreferredSize(new Dimension(16,16));
		buttonPanel.add(unwindowButton);
		
		// Display the view in a separate window
		windowButton = new JButton(app.getImageIcon("view-window.png"));
		windowButton.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
		windowButton.addActionListener(this);
		windowButton.setFocusPainted(false);
		windowButton.setPreferredSize(new Dimension(16,16));
		buttonPanel.add(windowButton);
		
		// Close the title bar
		closeButton = new JButton(app.getImageIcon("view-close.png"));
		closeButton.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
		closeButton.addActionListener(this);
		closeButton.setFocusPainted(false);
		closeButton.setPreferredSize(new Dimension(16,16));
		buttonPanel.add(closeButton);
		
		metaPanel.add(titlePanel);
		
		// Style bar panel
		if(hasStyleBar) {
			styleBarPanel = new JPanel(new BorderLayout());

			styleBarPanel.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0, SystemColor.controlShadow),
					BorderFactory.createEmptyBorder(0, 2, 0, 2)));
			
			metaPanel.add(styleBarPanel, BorderLayout.SOUTH);
		}

	   	// make titlebar visible if necessary
		updatePanel();
		
		add(metaPanel, BorderLayout.NORTH);
	}
	
	/**
	 * Create a frame for this DockPanel.
	 */
	public void createFrame() {
		frame = new JFrame(app.getPlain(title));
		
		// needs the higher res as used by Windows 7 for the Toolbar
	   	frame.setIconImage(app.getInternalImage("geogebra32.gif"));  
	   	frame.addWindowListener(this);	
	   	
	   	frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
            	setFrameBounds(event.getComponent().getBounds());
            }
            
            public void componentMoved(ComponentEvent event) {
            	setFrameBounds(event.getComponent().getBounds());
            }
        });
	   	
	   	frame.getContentPane().add(this);
	   	
	   	
	   	// TODO multimonitor supported?
	   	Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	   	
	   	// Use the previous dimension of this view
	   	Rectangle windowBounds = getFrameBounds();
	   	
	   	// resize window if necessary
	   	if(windowBounds.width > screenSize.width)
	   		windowBounds.width = screenSize.width - 50;
	   	if(windowBounds.height > screenSize.height)
	   		windowBounds.height = windowBounds.height - 50;
	   	
	   	// center window if necessary
	   	if(windowBounds.x + windowBounds.width > screenSize.width ||
	   		windowBounds.y + windowBounds.height > screenSize.height) {
	   		frame.setLocationRelativeTo(null);
	   	} else {
	   		frame.setLocation(windowBounds.getLocation());
	   	}
	   	setOpenInFrame(true);
	   	
	   	frame.setSize(windowBounds.getSize());
	   	frame.setVisible(true);
		
	   	// make titlebar visible if necessary
		updatePanel();
		
		frame.repaint();
	}
	
	/**
	 * Remove the frame.
	 */
	public void removeFrame() {
		frame.removeAll();
		frame.setVisible(false);
		frame = null;
	}
	
	/**
	 * Update all elements in the title bar.
	 */
	public void updateTitleBar() {		
		// The view is in the main window
		if(frame == null) {
			closeButton.setVisible(true);
			windowButton.setVisible(true);
			titleLabel.setVisible(true);
			unwindowButton.setVisible(false);
			
			if(titlePanel.getMouseListeners().length == 0) {
				titlePanel.addMouseListener(this);
			}
		} else {
			closeButton.setVisible(true);
			unwindowButton.setVisible(true);
			
			windowButton.setVisible(false);
			titleLabel.setVisible(false);
			
			titlePanel.removeMouseListener(this);
		}
		
		updateLabels();
	}
	
	/**
	 * Update the panel.
	 */
	public void updatePanel() {
		// load content if panel was hidden till now
		if(component == null && isVisible()) {
			component = loadComponent();
			add(component, BorderLayout.CENTER);
			
			if(hasStyleBar) {
				styleBarPanel.add(loadStyleBar(), BorderLayout.CENTER);
			}
		}
		
		if(hasStyleBar && isVisible()) {
			styleBarPanel.setVisible(showStyleBar);
		}
		
		titlePanel.setVisible(dockManager.getLayout().isTitleBarVisible());
		
		// update the title bar if necessary
		if(dockManager.getLayout().isTitleBarVisible()) {
			updateTitleBar();
		}
	}
	
	/**
	 * Update all labels of this DockPanel. Called while initializing and if
	 * the language was changed.
	 */
	public void updateLabels() {		
		closeButton.setToolTipText(app.getPlain("Close"));
		windowButton.setToolTipText(app.getPlain("ViewOpenExtraWindow"));
		unwindowButton.setToolTipText(app.getPlain("ViewCloseExtraWindow"));
		
		if(hasStyleBar) {
			toggleStyleBarButton.setToolTipText(app.getPlain("ToggleStyleBar"));
		}
		
		if(frame == null) {
			titleLabel.setText(app.getPlain(title));
		} else {
			updateTitle();
		}
	}
	
	/**
	 * Update fonts.
	 */
	public void updateFonts() {
		titleLabel.setFont(app.getPlainFont());
	}
	
	/**
	 * Update the title of the frame. This is necessary if the language changed
	 * or if the title of the main window changed (e.g. because the file was saved
	 * under a different name).
	 */
	public void updateTitle() {
		if(isOpenInFrame()) {
			StringBuilder windowTitle = new StringBuilder();
			windowTitle.append(app.getPlain(title));
			
	        if (app.getCurrentFile() != null) {
	        	windowTitle.append(" - ");
	            windowTitle.append(app.getCurrentFile().getName());
	        } else {
	        	if (GeoGebraFrame.getInstanceCount() > 1) {
	        		int nr = ((GeoGebraFrame)app.getFrame()).getInstanceNumber();        	
	        		windowTitle.append(" - (");
	        		windowTitle.append(nr+1);
	        		windowTitle.append(")");
	        	}
	        }
			
			frame.setTitle(windowTitle.toString());
		}
	}
	
	/**
	 * Close this panel.
	 */
	private void closePanel() {
		dockManager.hide(this);
		dockManager.getLayout().getApp().updateMenubar();
	}
	
	/**
	 * Display this panel in an external window.
	 */
	private void windowPanel() {
		dockManager.hide(this, false);
		setVisible(true);
		createFrame();
	}
	
	/**
	 * Display this panel in the main window.
	 */
	private void unwindowPanel() {
		// hide the frame
		dockManager.hide(this, false);
		
		// don't display this panel in a frame the next time
		setOpenInFrame(false);
		
		// show the panel in the main window
		dockManager.show(this);
	}
	
	/**
	 * Toggle the style bar.
	 */
	private void toggleStyleBar() {
		if(showStyleBar) {
			styleBarPanel.setVisible(false);
			showStyleBar = false;
		} else {
			styleBarPanel.setVisible(true);
			showStyleBar = true;
		}
	}

	/**
	 * One of the buttons were pressed.
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == closeButton) {
			closePanel();
		} else if(e.getSource() == windowButton) {
			windowPanel();
		} else if(e.getSource() == unwindowButton) {
			unwindowPanel();
		} else if(e.getSource() == toggleStyleBarButton) {
			toggleStyleBar();
		}
	}

	/**
	 * Hide the view if the window was closed or if the close button was
	 * pressed. 
	 */
	public void windowClosing(WindowEvent e) {
		closePanel();
	}
	
	/**
	 * Start dragging if the mouse was pressed while it was on the
	 * title panel.
	 */
	public void mousePressed(MouseEvent arg0) {
		dockManager.drag(this);
	}
	
	/**
	 * @return The parent DockSplitPane or null.
	 */
	public DockSplitPane getParentSplitPane() {
		if(isOpenInFrame())
			return null;
		
		Container parent = getParent();
		
		if(parent == null || !(parent instanceof DockSplitPane))
			return null;
		else
			return (DockSplitPane)parent;
	}

	/**
	 * Return the embedded def string for this DockPanel.
	 * @return
	 */
	public String calculateEmbeddedDef() {
		StringBuilder def = new StringBuilder();

		Component current = this;
		Component parent = this.getParent();
		DockSplitPane parentDSP;
		
		while(parent instanceof DockSplitPane) {
			int defType = -1;
			
			parentDSP = (DockSplitPane)parent;
			
			if(parentDSP.getOrientation() == DockSplitPane.HORIZONTAL_SPLIT) {
				if(current == parentDSP.getLeftComponent()) // left
					defType = 3;
				else // right
					defType = 1;
			} else {
				if(current == parentDSP.getLeftComponent()) // top
					defType = 0;
				else // bottom
					defType = 2;
			}
			
			if(def.length() == 0) {
				def.append(defType);
			} else {
				def.append(","+defType);
			}
			
			current = parent;
			parent = current.getParent();
		}
		
		return def.reverse().toString();
	}
	
	public DockPanelXml createInfo() {
		return new DockPanelXml(id, visible, openInFrame, showStyleBar, frameBounds, embeddedDef, embeddedSize);
	}
	
	/**
	 * @return If this DockPanel is in an extra frame / window.
	 */
	public boolean isInFrame() {
		return frame != null;
	}
	
	public void setOpenInFrame(boolean openInFrame) {
		this.openInFrame = openInFrame;
	}
	
	public void setShowStyleBar(boolean showStyleBar) {
		this.showStyleBar = showStyleBar;
	}
	
	public boolean isOpenInFrame() {
		return openInFrame;
	}
	
	public void setFrameBounds(Rectangle frameBounds) {
		this.frameBounds = frameBounds;
	}
	
	public Rectangle getFrameBounds() {
		return this.frameBounds;
	}

	/**
	 * @param embeddedDef the embeddedDef to set
	 */
	public void setEmbeddedDef(String embeddedDef) {
		this.embeddedDef = embeddedDef;
	}
	
	public String getEmbeddedDef() {
		return embeddedDef;
	}

	/**
	 * @param embeddedSize the embeddedSize to set
	 */
	public void setEmbeddedSize(int embeddedSize) {
		this.embeddedSize = embeddedSize;
	}

	/**
	 * @return the embeddedSize
	 */
	public int getEmbeddedSize() {
		return embeddedSize;
	}
	
	/**
	 * @return If this DockPanel is visible.
	 */
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean hasFocus() {
		return hasFocus;
	}
	
	public void setFocus(boolean hasFocus) {
		this.hasFocus = hasFocus;
		
		if(hasFocus) {
			titlePanel.setBackground(SystemColor.control.brighter());
		} else {
			titlePanel.setBackground(SystemColor.control);
		}
	}
	
	/**
	 * @return An unique ID for this DockPanel.
	 */
	public int getViewId() {
		return id;
	}
	
	public String getViewTitle() {
		return title;
	}
	
	public int getMenuOrder() {
		return menuOrder;
	}
	
	public boolean hasMenuShortcut() {
		return menuShortcut != '\u0000';
	}
	
	public char getMenuShortcut() {
		return menuShortcut;
	}
	
	/**
	 * Dock panel information as string for debugging.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[DockPanel,id=");
		sb.append(getViewId());
		sb.append(",visible=");
		sb.append(isVisible());
		sb.append(",inframe=");
		sb.append(isOpenInFrame());
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Helper class to compare dock panels for sorting in the menu. 
	 * 
	 * @author Florian Sonner
	 */
	public static class MenuOrderComparator implements Comparator<DockPanel>  {
		public int compare(DockPanel a, DockPanel b) {
			return a.getMenuOrder() - b.getMenuOrder();
		}
	}

	public void windowClosed(WindowEvent e) { }
	public void windowActivated(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) {	}
	public void mouseExited(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
}
