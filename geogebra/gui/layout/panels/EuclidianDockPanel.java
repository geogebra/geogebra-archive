package geogebra.gui.layout.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import geogebra.euclidian.EuclidianViewInterface;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.toolbar.Toolbar;
import geogebra.main.Application;

/**
 * Dock panel for the primary euclidian view.
 */
public class EuclidianDockPanel extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	private Application app;
	
	/**
	 * Panel to hold euclidian view and navigation bar if necessary. 
	 */
	private JPanel panel;
	
	/**
	 * Component of the construction protocol navigation bar,
	 * invisible if not needed.
	 */
	private JComponent consProtNav;
	
	/**
	 * @param app
	 */
	public EuclidianDockPanel(Application app, String toolbar) {
		super(
			Application.VIEW_EUCLIDIAN,	// view id 
			"DrawingPad", 				// view title
			toolbar,						// toolbar string
			true,						// style bar?
			1,							// menu order
			'1' // ctrl-shift-1
		);
		
		this.app = app;
	}

//	@Override
//	public ImageIcon getIcon() {
//		return app.getImageIcon("document-properties.png");
//	}

	@Override
	protected JComponent loadStyleBar() {		
		return app.getEuclidianView().getStyleBar();
	}

	@Override
	protected JComponent loadComponent() {
		if(panel == null) {
			panel = new JPanel(new BorderLayout());
			
			panel.add(app.getEuclidianView(), BorderLayout.CENTER);
			
			consProtNav = app.getGuiManager().getConstructionProtocolNavigation();
			consProtNav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray));
			consProtNav.setVisible(app.showConsProtNavigation());
			
			panel.add(consProtNav, BorderLayout.SOUTH); // may be invisible, but made visible later
		}
		
		return panel;
	}
	
	/**
	 * As the component of this panel is not just the euclidian view 
	 * as asserted in EuclidianDockPanelAbstract we have to override
	 * this method to provide the correct euclidian view.
	 */
	@Override
	public EuclidianViewInterface getEuclidianView() {
		return app.getEuclidianView();
	}
}
