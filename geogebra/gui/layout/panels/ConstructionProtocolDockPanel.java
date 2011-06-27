package geogebra.gui.layout.panels;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import geogebra.gui.layout.DockPanel;
import geogebra.main.Application;

public class ConstructionProtocolDockPanel extends DockPanel {

	/**
	 * @param app
	 */
	public ConstructionProtocolDockPanel(Application app) {
		super(
			Application.VIEW_CONSTRUCTION_PROTOCOL, 	// view id
			"ConstructionProtocol", 					// view title phrase 
			null,	// toolbar string
			false,					// style bar?
			5,						// menu order
			'L' // ctrl-shift-L
		);
		
		this.app = app; 
	}

	protected JComponent loadComponent() {
		return (JComponent) app.getGuiManager().getConstructionProtocolView();
	}

}
