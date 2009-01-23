package geogebra.gui.menubar;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import geogebra.gui.util.BrowserLauncher;
import geogebra.main.Application;

/**
 * The "Help" menu.
 */
class HelpMenu extends BaseMenu {
	private static final long serialVersionUID = 1125756553396593316L;

	private AbstractAction
		helpAction,
		websiteAction,
		forumAction,
		wikiAction,
		infoAction
	;
	
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
		add(helpAction);
		addSeparator();

		add(websiteAction);
		add(forumAction);
		add(wikiAction);

		addSeparator();

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
						app.getGuiManager().openHelp(null);
					}
				};
				runner.start();
			}
		};
		
		websiteAction = new AbstractAction("www.geogebra.org", new ImageIcon(
				app.getInternalImage("geogebra.gif"))) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().showURLinBrowser(
						Application.GEOGEBRA_WEBSITE);
			}
		};

		forumAction = new AbstractAction("GeoGebra Forum", new ImageIcon(app
				.getInternalImage("users.png"))) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				BrowserLauncher
						.openURL(Application.GEOGEBRA_WEBSITE + "forum/");
			}
		};

		wikiAction = new AbstractAction("GeoGebraWiki", new ImageIcon(app
				.getInternalImage("wiki.png"))) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				String url = Application.GEOGEBRA_WEBSITE;
				if (app.getLocale().getLanguage().equals("de"))
					url += "de/wiki/";
				else
					url += "en/wiki/";

				BrowserLauncher.openURL(url);
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
