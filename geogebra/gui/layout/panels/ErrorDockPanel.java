package geogebra.gui.layout.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import geogebra.gui.layout.DockPanel;
import geogebra.main.Application;

/**
 * Dock panel for error of loading (used for 3D panel not supported by ggb version < 5.0)
 */
public class ErrorDockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	private Application app;
	
	/**
	 * @param app
	 */
	public ErrorDockPanel(Application app) {
		super(
			Application.VIEW_ERROR,	// view id 
			"ErrorWindow", 			// view title phrase
			null,						// toolbar string
			false,						// style bar?
			4, 							// menu order
			'3'							// menu shortcut
		);
		
		this.app = app;
		
		setVisible(false);
		//setEmbeddedSize(50);
	}

	
	protected JComponent loadComponent() {
		/*
		JTextArea text = new JTextArea("error: 3D not supported on this version");
		Font f = app.getBoldFont();
		text.setFont(f);
		text.setLineWrap(true); 
		text.setWrapStyleWord(true); 
		
		return text;
		*/
		
		return new JPanel();
		
	}
	
	public void updatePanel() {	
		if(component == null && isVisible()) {
			component = loadComponent();
			add(component, BorderLayout.CENTER);
		}
		
	}
}
