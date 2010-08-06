package geogebra3D.gui;

import javax.swing.JCheckBoxMenuItem;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.ContextMenuGraphicsWindow;
import geogebra.main.Application;
import geogebra3D.Application3D;

/** Extending ContextMenuGraphicsWindow class for 3D
 * @author matthieu
 *
 */
public class ContextMenuGraphicsWindow3D extends ContextMenuGraphicsWindow {

	/** default constructor
	 * @param app
	 * @param px
	 * @param py
	 */
	public ContextMenuGraphicsWindow3D(Application app, double px, double py) {
		super(app, px, py);
		// TODO Auto-generated constructor stub
	}
	
	

	protected void addAxesAndGridCheckBoxes(){

		super.addAxesAndGridCheckBoxes();

		EuclidianView ev = app.getEuclidianView();
		JCheckBoxMenuItem cbShowPlane = new JCheckBoxMenuItem(((GuiManager3D) app.getGuiManager()).getShowPlaneAction());
		((Application3D) app).setShowPlaneSelected(cbShowPlane);
		cbShowPlane.setBackground(getBackground());
		add(cbShowPlane);
	}

}
