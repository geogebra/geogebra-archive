package geogebra.gui.menubar;


import geogebra.gui.ToolCreationDialog;
import geogebra.main.Application;


import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;


import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * The "Macro" menu.
 * @author Zbynek Konecny
 * @version 2010-05-26
 */
class MacroMenu extends BaseMenu {
	private static final long serialVersionUID = -5154067739481481835L;
	
	private AbstractAction
		deleteAll,
		saveAction,
		saveAsAction,
		exitAction
	;

	/**
	 * Initiates a macro menu within given application.
	 * @param app Application object for the window.
	 */
	public MacroMenu(Application app) {
		super(app, app.getMenu("Macro"));
		
		initActions();
		update();
	}
	
	/**
	 * Initialize all items.
	 */
	private void updateItems()
	{
		removeAll();
		
		JMenuItem mi;
	

		// "New": reset
		mi = add(deleteAll);

	
		mi = add(saveAsAction);
	/*	TODO: Save macro
	    setMenuShortCutAccelerator(mi, 'S');
		mi = add(saveAction);*/
	
		
		// close
		addSeparator();
		mi = add(exitAction);
		if (Application.MAC_OS) {
			setMenuShortCutAccelerator(mi, 'W');
		} else {
			// Alt + F4
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F4,
					InputEvent.ALT_MASK);
			mi.setAccelerator(ks);
		}
	
	}
	
	/**
	 * Initialize all actions of this menu.
	 */
	private void initActions()
	{
		deleteAll = new AbstractAction(app.getMenu("New"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();		
				app.fileNew();
				app.setDefaultCursor();
			}
		};

		
		saveAsAction = new AbstractAction(app.getMenu("SaveAs"), app
				.getImageIcon("document-save.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				
				ToolCreationDialog tcd=new ToolCreationDialog(app);
				tcd.addInputs(app.getMacro().getMacroInput());
				tcd.addOutputs(app.getMacro().getMacroOutput());
				tcd.setNameTab(app.getMacro());
				tcd.setVisible(true);
				tcd.setTitle(app.getMenu("SaveAs"));
			}
		};


		exitAction = new AbstractAction(app.getMenu("Close"), app
				.getImageIcon("exit.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.exit();
			}
		};
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

		updateItems();
	}
}
