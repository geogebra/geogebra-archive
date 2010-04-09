package geogebra3D.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import geogebra.gui.DefaultGuiManager;
import geogebra.main.Application;
import geogebra3D.Application3D;

/**
 * Extending DefaultGuiManager class for 3D
 * 
 * @author matthieu
 *
 */
public class DefaultGuiManager3D extends DefaultGuiManager {

	
	private AbstractAction showPlaneAction;
	
	/** 
	 * default constructor
	 * @param app
	 */
	public DefaultGuiManager3D(Application app) {
		super(app);
	}
	
	
	
	
	//////////////////////////////
	// ACTIONS
	//////////////////////////////
	
	
	protected boolean initActions() {
		
		if (!super.initActions())
			return false;
		
		showPlaneAction = new AbstractAction(app.getMenu("Plane"),
				app.getImageIcon("axes.gif")) {
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
