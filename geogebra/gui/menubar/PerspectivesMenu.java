package geogebra.gui.menubar;

import geogebra.GeoGebra;
import geogebra.gui.layout.Layout;
import geogebra.gui.util.BrowserLauncher;
import geogebra.io.layout.Perspective;
import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * The "Help" menu.
 */
class PerspectivesMenu extends BaseMenu {
	private static final long serialVersionUID = 1125756553396593316L;

	private Layout layout;

	private AbstractAction
	changePerspectiveAction,
	managePerspectivesAction,
	savePerspectiveAction
	;
	/**
	 * Creates new help menu
	 * @param app
	 */
	public PerspectivesMenu(Application app, Layout layout) {
		super(app, app.getMenu("Perspectives"));
		
		this.layout = layout;
		
		initActions();
		initItems();
		
		update();
	}
	
	/**
	 * Initialize the menu items.
	 */
	private void initItems()
	{
		Perspective[] defaultPerspectives = Layout.defaultPerspectives;

		for (int i = 0; i < defaultPerspectives.length; ++i) {
			JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
			tmpItem.setText(app.getMenu("Perspective."
					+ defaultPerspectives[i].getId()));
			tmpItem.setIcon(app.getEmptyIcon());
			tmpItem.setActionCommand("d" + i);
			add(tmpItem);
		}

		addSeparator();

		// user perspectives
		Perspective[] perspectives = layout.getPerspectives();

		if (perspectives.length != 0) {
			for (int i = 0; i < perspectives.length; ++i) {
				JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
				tmpItem.setText(perspectives[i].getId());
				tmpItem.setIcon(app.getEmptyIcon());
				tmpItem.setActionCommand(Integer.toString(i));
				add(tmpItem);
			}
			addSeparator();
		}

		add(managePerspectivesAction);
		add(savePerspectiveAction);
	}
	
	/**
	 * Initialize the actions.
	 */
	private void initActions()
	{
		savePerspectiveAction = new AbstractAction(app
				.getMenu("SaveCurrentPerspective"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.showSaveDialog();
			}
		};

		managePerspectivesAction = new AbstractAction(app
				.getMenu("ManagePerspectives"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.showManageDialog();
			}
		};

		changePerspectiveAction = new AbstractAction() {
			public static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// default perspectives start with a "d"
				if (e.getActionCommand().startsWith("d")) {
					int index = Integer.parseInt(e.getActionCommand()
							.substring(1));
					layout.applyPerspective(Layout.defaultPerspectives[index]);
				} else {
					int index = Integer.parseInt(e.getActionCommand());
					layout.applyPerspective(layout.getPerspective(index));
				}
			}
		};
	}

	@Override
	public void update() {
		// TODO update labels
	}

}
