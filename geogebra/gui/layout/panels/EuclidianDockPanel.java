package geogebra.gui.layout.panels;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import geogebra.gui.layout.DockPanel;
import geogebra.main.Application;

/**
 * Dock panel for the primary euclidian view.
 */
public class EuclidianDockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	private Application app;
	
	/**
	 * @param app
	 */
	public EuclidianDockPanel(Application app) {
		super(
			Application.VIEW_EUCLIDIAN,	// view id 
			"DrawingPad", 				// view title
			null,						// toolbar string
			true,						// style bar?
			1							// menu order
		);
		
		this.app = app;
	}

	@Override
	public ImageIcon getIcon() {
		return app.getImageIcon("document-properties.png");
	}

	@Override
	protected JComponent loadStyleBar() {
		return app.getEuclidianView().getStyleBar();
	}

	@Override
	protected JComponent loadComponent() {
		// the fact that this object is of type EuclidianView is
		// used for the limited focus subsystem, see DockPanel::updatePanel() 
		return app.getEuclidianView();
	}

}
