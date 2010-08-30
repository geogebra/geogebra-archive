package geogebra.gui.layout.panels;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import geogebra.gui.layout.DockPanel;
import geogebra.main.Application;

/**
 * Dock panel for the secondary euclidian view.
 */
public class Euclidian2DockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	private Application app;
	
	/**
	 * @param app
	 */
	public Euclidian2DockPanel(Application app) {
		super(
			Application.VIEW_EUCLIDIAN2, 	// view id
			"DrawingPad2", 					// view title phrase
			null,							// toolbar string
			true,							// style bar?
			5								// menu order
		);
		
		this.app = app;
	}
	
	public ImageIcon getIcon() {
		return app.getImageIcon("document-properties.png");
	}
	
	protected JComponent loadStyleBar() {
		return app.getGuiManager().getEuclidianView2().getStyleBar();
	}
	
	protected JComponent loadComponent() {
		// the fact that this object is of type EuclidianView is
		// used for the limited focus subsystem, see DockPanel::updatePanel()
		return app.getGuiManager().getEuclidianView2();
	}
}
