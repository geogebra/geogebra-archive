package geogebra.gui.layout.panels;

import geogebra.gui.layout.DockPanel;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Abstract class for all "euclidian" panels
 * 
 * @author matthieu
 *
 */
public abstract class EuclidianDockPanelAbstract extends DockPanel {
	


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private boolean hasEuclidianFocus;
	
	private JLabel euclidianFocus;

	/**
	 * default constructor
	 * @param id
	 * @param title
	 * @param toolbar
	 * @param hasStyleBar
	 * @param menuOrder
	 */
	public EuclidianDockPanelAbstract(int id, String title, String toolbar,
			boolean hasStyleBar, int menuOrder, char shortcut) {
		super(id, title, toolbar, hasStyleBar, menuOrder, shortcut);
	}

	public void mousePressed(MouseEvent arg0) {
		super.mousePressed(arg0);
		dockManager.setFocusedPanel(this);
	}
	
	/**
	 * sets this euclidian panel to have the "euclidian focus"
	 * @param hasFocus
	 */
	public final void setEuclidianFocus(boolean hasFocus) {
		hasEuclidianFocus = hasFocus;
		euclidianFocus.setVisible(hasEuclidianFocus);
	}
	
	
	
	
	/**
	 * create the focus panel (composed of titleLabel, and, for EuclidianDockPanels, focus icon)
	 * @return the focus panel
	 */
	protected JComponent createFocusPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		//add title label
		panel.add(super.createFocusPanel(), BorderLayout.WEST);
		
		//euclidian focus
		euclidianFocus = new JLabel(app.getImageIcon("view-euclidian-focus.png"));
		euclidianFocus.setVisible(false);
		panel.add(euclidianFocus, BorderLayout.EAST);
		
		return panel;
	}

}
