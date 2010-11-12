package geogebra.gui.layout.panels;

import geogebra.gui.layout.DockPanel;

import java.awt.event.MouseEvent;


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

	/**
	 * default constructor
	 * @param id
	 * @param title
	 * @param toolbar
	 * @param hasStyleBar
	 * @param menuOrder
	 */
	public EuclidianDockPanelAbstract(int id, String title, String toolbar,
			boolean hasStyleBar, int menuOrder) {
		super(id, title, toolbar, hasStyleBar, menuOrder);
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
		setTitleLabelFocus();
	}
	
	protected void setTitleLabelFocus(){
		if(hasFocus) 
			titleLabel.setFont(app.getBoldFont());
		else if (hasEuclidianFocus)
			titleLabel.setFont(app.getItalicFont());
		else
			titleLabel.setFont(app.getPlainFont());
	}

}
