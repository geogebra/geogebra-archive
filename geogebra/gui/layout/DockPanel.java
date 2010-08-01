package geogebra.gui.layout;

import geogebra.gui.app.GeoGebraFrame;
import geogebra.io.layout.DockPanelXml;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Every object which should be dragged needs to be of type DockPanel.
 * A DockPanel will wrap around the component with the real contents
 * (e.g. the EuclidianView) and will add a title bar if the user is not in
 * the "layout fixed" mode.
 * The user will be able to move the DockPanel by dragging the title bar.
 * 
 * @author Florian Sonner
 * @version 2008-07-18
 */
public class DockPanel extends JPanel implements ActionListener, WindowListener, MouseListener {
	private static final long serialVersionUID = 1L;
	
	private DockManager dockManager;
	private Application app;
	
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
	 * The frame which holds this DockPanel if the DockPanel is opened in
	 * an additional window.
	 */
	private JFrame frame = null;

	/**
	 * All information regarding the ID and the location of this
	 * DockPanel which can be saved and loaded as XML.
	 */
	private DockPanelXml info;
	
	/**
	 * The language-key of the title of this view which will be loaded
	 * for the specific language by using Application.getPlain().
	 */
	private String viewTitle;
	
	/**
	 * The component used for this view.
	 */
	private Component view;
	
	/**
	 * Construct this dock manager. Add the title bar if we're not in the
	 * "layout fixed" mode and if the user does not use an applet.
	 * 
	 * @param dockManager
	 * @param info			A container which stores all information about this view.
	 */
	public DockPanel(DockManager dockManager, DockPanelXml info) {
		this.dockManager = dockManager;
		this.app = dockManager.getLayout().getApp();
		this.info = info;
		
		setLayout(new BorderLayout());
		
		// Insert new view types here..
		switch(info.getViewId()) {
			case Application.VIEW_EUCLIDIAN:
				viewTitle = "DrawingPad";
				break;
			case Application.VIEW_ALGEBRA:
				viewTitle = "AlgebraWindow";
				break;
			case Application.VIEW_SPREADSHEET:
				viewTitle = "Spreadsheet";
				break;
			case Application.VIEW_CAS:
				viewTitle = "CAS";
				break;
			case DockManager.VIEW_EUCLIDIAN_3D:
				viewTitle ="GraphicsView3D";
				break;
			default:
				throw new IllegalArgumentException("view ID can not be identified (#"+info.getViewId()+")");
		}
		
		if(info.isVisible()) {
			loadView();
			add(view, BorderLayout.CENTER);
		}
		
		// Construct title bar and all elements
		titlePanel = new JPanel();
		titlePanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, SystemColor.controlShadow),
				BorderFactory.createEmptyBorder(0, 2, 0, 2)));
		titlePanel.setLayout(new BorderLayout());
		
		titleLabel = new JLabel(app.getPlain(viewTitle));
		titleLabel.setFont(app.getPlainFont());
		titlePanel.add(titleLabel, BorderLayout.WEST);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		titlePanel.add(buttonPanel, BorderLayout.EAST);
		
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

	   	// make titlebar visible if necessary
		updatePanel();
		
		add(titlePanel, BorderLayout.NORTH);
	}
	
	/**
	 * @return If this DockPanel is in an extra frame / window.
	 */
	public boolean isInFrame() {
		return frame != null;
	}
	
	/**
	 * @return If this DockPanel is visible.
	 */
	public boolean isVisible() {
		return info.isVisible();
	}
	
	/**
	 * Create a frame for this DockPanel.
	 */
	public void createFrame() {
		frame = new JFrame(app.getPlain(viewTitle));
		
		// needs the higher res as used by Windows 7 for the Toolbar
	   	frame.setIconImage(app.getInternalImage("geogebra32.gif"));  
	   	frame.addWindowListener(this);	
	   	
	   	frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
            	info.setWindowRect(event.getComponent().getBounds());
            }
            
            public void componentMoved(ComponentEvent event) {
            	info.setWindowRect(event.getComponent().getBounds());
            }
        });
	   	
	   	frame.getContentPane().add(this);
	   	
	   	
	   	// TODO multimonitor supported?
	   	Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	   	
	   	// Use the previous dimension of this view
	   	Rectangle windowRect = info.getWindowRect();
	   	
	   	// resize window if necessary
	   	if(windowRect.width > screenSize.width)
	   		windowRect.width = screenSize.width - 50;
	   	if(windowRect.height > screenSize.height)
	   		windowRect.height = windowRect.height - 50;
	   	
	   	// center window if necessary
	   	if(windowRect.x + windowRect.width > screenSize.width ||
	   		windowRect.y + windowRect.height > screenSize.height) {
	   		frame.setLocationRelativeTo(null);
	   	} else {
	   		frame.setLocation(windowRect.getLocation());
	   	}
	   	info.setOpenInFrame(true);
	   	
	   	frame.setSize(windowRect.getSize());
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
		if(view == null && info.isVisible()) {
			loadView();
			add(view, BorderLayout.CENTER);
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
		
		if(frame == null) {
			titleLabel.setText(app.getPlain(viewTitle));
		} else {
			updateTitle();
		}
	}
	
	/**
	 * Update the title of the frame. This is necessary if the language changed
	 * or if the title of the main window changed (e.g. because the file was saved
	 * under a different name).
	 */
	public void updateTitle() {		
		if(info.isOpenInFrame()) {
			StringBuilder windowTitle = new StringBuilder();
			windowTitle.append(app.getPlain(viewTitle));
			
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
	
	private void loadView()
	{
		switch(info.getViewId()) {
			case Application.VIEW_EUCLIDIAN:
				view = app.getEuclidianView();
				break;
			case Application.VIEW_ALGEBRA:
				view = new JScrollPane(app.getGuiManager().getAlgebraView());
				((JScrollPane)view).setBorder(BorderFactory.createEmptyBorder(2,4,2,4));
				break;
			case Application.VIEW_SPREADSHEET:
				view = app.getGuiManager().getSpreadsheetView();
				break;
			case Application.VIEW_CAS:
				view = app.getGuiManager().getCasView().getCASViewComponent();
				break;
			case DockManager.VIEW_EUCLIDIAN_3D:
				view = dockManager.getEuclidian3D();
				break;
			default:
				throw new IllegalArgumentException("view ID can not be identified (#"+info.getViewId()+")");
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
		info.setVisible(true);
		createFrame();
	}
	
	/**
	 * Display this panel in the main window.
	 */
	private void unwindowPanel() {
		// hide the frame
		dockManager.hide(this, false);
		
		// don't display this panel in a frame the next time
		getInfo().setOpenInFrame(false);
		
		// show the panel in the main window
		dockManager.show(this);
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
		if(info.isOpenInFrame())
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
	public String getEmbeddedDef() {
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
	
	/**
	 * @return The object which stores all information for this DockPanel.
	 */
	public DockPanelXml getInfo() {
		return info;
	}
	
	/**
	 * @return An unique ID for this DockPanel.
	 */
	public int getViewId() {
		return info.getViewId();
	}
	
	/**
	 * @return The view 
	 */
	public Component getView() {
		return view;
	}
	
	/**
	 * Dock panel information as string for debugging.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[DockPanel,id=");
		sb.append(info.getViewId());
		sb.append(",visible=");
		sb.append(info.isVisible());
		sb.append(",inframe=");
		sb.append(info.isOpenInFrame());
		sb.append("]");
		return sb.toString();
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
