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
	
	/**
	 * @param app
	 * @param viewId 
	 */
	public ErrorDockPanel(Application app, int viewId) {
		super(
			Application.VIEW_ERROR,	// view id 
			"ErrorWindow (viewId="+viewId+")", 			// view title phrase
			null,						// toolbar string
			false,						// style bar?
			4, 							// menu order
			'3'							// menu shortcut
		);
		
		
		//setVisible(false);
		
		this.app = app;
	}

	
	protected JComponent loadComponent() {
		return new JPanel();
		
	}
	
	public void updatePanel() {	
		if(component == null && isVisible()) {
			component = loadComponent();
			add(component, BorderLayout.CENTER);
		}	
	}
	
	//unused methods
	public final void setFocus(boolean hasFocus) {}
	protected void closePanel() {}
}
