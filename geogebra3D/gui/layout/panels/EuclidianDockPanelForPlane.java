package geogebra3D.gui.layout.panels;

import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import geogebra.gui.layout.DockPanel;
import geogebra.gui.layout.panels.EuclidianDockPanelAbstract;
import geogebra.gui.toolbar.Toolbar;
import geogebra.main.Application;
import geogebra3D.Application3D;

/**
 * Dock panel for the primary euclidian view.
 */
public class EuclidianDockPanelForPlane extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	private Application app;
	
	/**
	 * @param app
	 */
	public EuclidianDockPanelForPlane(Application app) {
		super(
			Application.VIEW_EUCLIDIAN_FOR_PLANE,	// view id 
			"GraphicsViewForPlane", 				// view title
			null,// toolbar string
			true,						// style bar?
			-1,							// menu order
			'P'
		);
		
		this.app = app;
	}



	protected JComponent loadComponent() {
		return ((Application3D)app).getEuclidianViewForPlane();
	}
	
	protected JComponent loadStyleBar() {
		return ((Application3D)app).getEuclidianViewForPlane().getStyleBar();
	}
}
