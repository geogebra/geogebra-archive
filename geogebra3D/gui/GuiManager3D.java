package geogebra3D.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import geogebra.gui.GuiManager;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.layout.Layout;
import geogebra.main.Application;
import geogebra3D.Application3D;
import geogebra3D.euclidian3D.EuclidianView3D;

/**
 * Extending DefaultGuiManager class for 3D
 * 
 * @author matthieu
 *
 */
public class GuiManager3D extends GuiManager {

	
	private AbstractAction showAxes3DAction, showGrid3DAction, showPlaneAction;
	
	/** 
	 * default constructor
	 * @param app
	 */
	public GuiManager3D(Application app) {
		super(app);
	}
	
	protected void createLayout(){
		setLayout(new Layout(true));
	}
	
	/**
	 * Add 3D euclidian view to layout.
	 */
	protected void initLayoutPanels() {
		super.initLayoutPanels();
		
		String myToolBar3D =  EuclidianView3D.MODE_MOVE
		+" || "
		+EuclidianView3D.MODE_POINT_IN_REGION
		+" "
		+EuclidianView3D.MODE_INTERSECT
		+" | "
		+EuclidianView3D.MODE_JOIN
		+" "
		+EuclidianView3D.MODE_SEGMENT
		+" "
		+EuclidianView3D.MODE_RAY
		+" , "
		+EuclidianView3D.MODE_VECTOR
		+" || "
		+EuclidianView3D.MODE_POLYGON
		//+" | "
		//+EuclidianView3D.MODE_CIRCLE_THREE_POINTS
		+" || "
		+EuclidianView3D.MODE_PLANE_THREE_POINTS
		+" , "
		+EuclidianView3D.MODE_PLANE_POINT_LINE
		+" | "
		+EuclidianView3D.MODE_ORTHOGONAL_PLANE
		+" , "
		+EuclidianView3D.MODE_PARALLEL_PLANE
		+" || "
		+EuclidianView3D.MODE_SPHERE_TWO_POINTS
		+" "
		+EuclidianView3D.MODE_SPHERE_POINT_RADIUS
		+" || "
		+EuclidianView3D.MODE_TRANSLATEVIEW
		+" "
		+EuclidianView3D.MODE_ZOOM_IN
		+" "
		+EuclidianView3D.MODE_ZOOM_OUT
		+" | "
		+EuclidianView3D.MODE_VIEW_IN_FRONT_OF
		;
    	
    	DockPanel panel3D = new DockPanel(Application3D.VIEW_EUCLIDIAN3D, "GraphicsView3D", 
    			myToolBar3D, 
    			false, 4) {
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
		showAxes3DAction = new AbstractAction(app.getMenu("Axes"),
				app.getImageIcon("axes.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// toggle axes
				((Application3D) app).toggleAxis3D();
				app.getEuclidianView().repaint();
				app.storeUndoInfo();
				app.updateMenubar();
				
			}
		};

		showGrid3DAction = new AbstractAction(app.getMenu("Grid"),
				app.getImageIcon("grid.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// toggle grid
				((Application3D) app).toggleGrid3D();
				app.getEuclidianView().repaint();
				app.storeUndoInfo();
				app.updateMenubar();
				
			}
		};
		
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
	
	
	public AbstractAction getShowAxes3DAction() {
		initActions();
		return showAxes3DAction;
	}

	public AbstractAction getShowGrid3DAction() {
		initActions();
		return showGrid3DAction;
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
	/*
	public void showDrawingPadPopup(Component invoker, Point p) {
		// clear highlighting and selections in views		
		app.getEuclidianView().resetMode();
		
		// menu for drawing pane context menu
		ContextMenuGraphicsWindow3D popupMenu = new ContextMenuGraphicsWindow3D(
				app, p.x, p.y);
		popupMenu.show(invoker, p.x, p.y);
	}
	*/
	
	/**
	 * Displays the zoom menu at the position p in the coordinate space of
	 * euclidianView
	 */
	public void showDrawingPadPopup3D(Component invoker, Point p) {
		// clear highlighting and selections in views		
		((Application3D) app).getEuclidianView3D().resetMode();
		
		// menu for drawing pane context menu
		ContextMenuGraphicsWindow3D popupMenu = new ContextMenuGraphicsWindow3D(
				app, p.x, p.y);
		popupMenu.show(invoker, p.x, p.y);
	}
	

}
