package geogebra.gui.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import geogebra.gui.app.GeoGebraFrame;
import geogebra.main.Application;

/**
 * The "Windows" menu.
 */
class WindowMenu extends BaseMenu {
	private static final long serialVersionUID = -5087344097832121548L;
	
	private AbstractAction
		newWindowAction
	;

	public WindowMenu(Application app) {
		super(app, app.getMenu("Window"));
		
		initActions();
		update();
	}
	
	/**
	 * Initialize and update the items.
	 */
	private void updateItems()
	{
		removeAll();
		JMenuItem mit = add(newWindowAction);
		setMenuShortCutAccelerator(mit, 'N');

		ArrayList<GeoGebraFrame> ggbInstances = GeoGebraFrame.getInstances();
		int size = ggbInstances.size();
		if (size == 1)
			return;

		addSeparator();
		StringBuffer sb = new StringBuffer();
		ButtonGroup bg = new ButtonGroup();
		JRadioButtonMenuItem mi;
		for (int i = 0; i < size; i++) {
			GeoGebraFrame ggb = (GeoGebraFrame) ggbInstances.get(i);
			Application application = ggb.getApplication();

			sb.setLength(0);
			sb.append(i + 1);
			if (application != null) // Michael Borcherds 2008-03-03 bugfix
				if (application.getCurrentFile() != null) {
					sb.append(" ");
					sb.append(application.getCurrentFile().getName());
				}

			mi = new JRadioButtonMenuItem(sb.toString());
			if (application == this.app)
				mi.setSelected(true);
			ActionListener al = new RequestFocusListener(ggb);
			mi.addActionListener(al);
			bg.add(mi);
			add(mi);
		}
	}
	
	/**
	 * Initialize the actions.
	 */
	private void initActions()
	{
		newWindowAction = new AbstractAction(app.getMenu("NewWindow"), app
				.getImageIcon("document-new.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						app.setWaitCursor();
						GeoGebraFrame.createNewWindow(null);
						app.setDefaultCursor();
					}
				};
				runner.start();
			}
		};
	}

	@Override
	public void update() {
		updateItems();
		
		// TODO update labels
	}
}
