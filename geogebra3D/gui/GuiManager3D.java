package geogebra3D.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import geogebra.gui.GuiManager;
import geogebra.gui.layout.DockPanel;
import geogebra.main.Application;
import geogebra3D.Application3D;

/**
 * Extending DefaultGuiManager class for 3D
 * 
 * @author matthieu
 *
 */
public class GuiManager3D extends GuiManager {

	
	private AbstractAction showPlaneAction;
	
	/** 
	 * default constructor
	 * @param app
	 */
	public GuiManager3D(Application app) {
		super(app);
	}
	
	/**
	 * Add 3D euclidian view to layout.
	 */
	protected void initLayoutPanels() {
		super.initLayoutPanels();
    	
    	DockPanel panel3D = new DockPanel(Application3D.VIEW_EUCLIDIAN3D, "GraphicsView3D", false, 4) {
    		protected JComponent loadStyleBar() {
				return null;
			}
			
    		protected JComponent loadComponent() {
				return ((Application3D)app).getEuclidianView3D();
			}
		};
		panel3D.setMaximumSize(new Dimension(0,0));
		panel3D.setMinimumSize(new Dimension(0,0));
		
    	getLayout().registerPanel(panel3D);
	}
	
	
	
	
	//////////////////////////////
	// ACTIONS
	//////////////////////////////
	
	
	protected boolean initActions() {
		
		if (!super.initActions())
			return false;
		
		showPlaneAction = new AbstractAction(app.getMenu("Plane"),
				app.getImageIcon("plane.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// toggle plane
				((Application3D) app).togglePlane();
				
			}
		};

		
		return true;
		
	}
	
	
	public AbstractAction getShowPlaneAction() {
		initActions();
		return showPlaneAction;
	}
	
	
	
	
	
	
	
	//////////////////////////////
	// POPUP MENU
	//////////////////////////////
	
	
	/**
	 * Displays the zoom menu at the position p in the coordinate space of
	 * euclidianView
	 */
	public void showDrawingPadPopup(Component invoker, Point p) {
		// clear highlighting and selections in views		
		app.getEuclidianView().resetMode();
		
		// menu for drawing pane context menu
		ContextMenuGraphicsWindow3D popupMenu = new ContextMenuGraphicsWindow3D(
				app, p.x, p.y);
		popupMenu.show(invoker, p.x, p.y);
	}

}
