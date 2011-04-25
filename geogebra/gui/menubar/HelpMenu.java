package geogebra.gui.menubar;

import geogebra.GeoGebra;
import geogebra.gui.util.BrowserLauncher;
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
class HelpMenu extends BaseMenu {
	private static final long serialVersionUID = 1125756553396593316L;

	private AbstractAction
		helpAction,
		tutorialAction,
		websiteAction,
		forumAction,
		geogebratubeAction,
		infoAction
	;
	/**
	 * Creates new help menu
	 * @param app
	 */
	public HelpMenu(Application app) {
		super(app, app.getMenu("Help"));
		
		initActions();
		initItems();
		
		update();
	}
	
	/**
	 * Initialize the menu items.
	 */
	private void initItems()
	{
		JMenuItem mi = add(helpAction);
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F1,
				0);
		mi.setAccelerator(ks);

		add(tutorialAction);
		add(forumAction);
		add(geogebratubeAction);
		
		addSeparator();

		add(websiteAction);

		add(infoAction);
	}
	
	/**
	 * Initialize the actions.
	 */
	private void initActions()
	{
		helpAction = new AbstractAction(app.getMenu("Help"), app
				.getImageIcon("help.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						app.getGuiManager().openHelp("");
					}
				};
				runner.start();
			}
		};
		
		tutorialAction = new AbstractAction(app.getMenu("Tutorials")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						app.getGuiManager().openHelp("Tutorial");
					}
				};
				runner.start();
			}
		};

		forumAction = new AbstractAction("GeoGebra Forum", new ImageIcon(app
				.getInternalImage("users.png"))) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().showURLinBrowser(
						GeoGebra.GEOGEBRA_WEBSITE + "forum/");
			}
		};
		
		websiteAction = new AbstractAction("GeoGebra.org", new ImageIcon(
				app.getInternalImage("geogebra.png"))) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().showURLinBrowser(
						GeoGebra.GEOGEBRA_WEBSITE);
			}
		};

		geogebratubeAction = new AbstractAction("GeoGebraTube") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().showURLinBrowser(
						GeoGebra.GEOGEBRATUBE_WEBSITE);
			}
		};
		
		infoAction = new AbstractAction(app.getMenu("About") + " / "
				+ app.getMenu("License"), app.getImageIcon("info.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				GeoGebraMenuBar.showAboutDialog(app);
			}
		};
	}

	@Override
	public void update() {
		// TODO update labels
	}

}
