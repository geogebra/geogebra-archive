package geogebra.gui.menubar;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.DefaultGuiManager;
import geogebra.gui.layout.Layout;
import geogebra.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.gui.virtualkeyboard.keys;
import geogebra.gui.virtualkeyboard.start_vk;
import geogebra.gui.virtualkeyboard.vk_gui;
import geogebra.io.layout.Perspective;
import geogebra.main.Application;
import geogebra.main.MyResourceBundle;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * The "View" menu. 
 */
class ViewMenu extends BaseMenu {
	private static final long serialVersionUID = -8719255878019899997L;

	private Layout layout;
	
	private AbstractAction 
		showAlgebraViewAction,
		showSpreadsheetAction,
		showEuclidianViewAction,
		showCASViewAction,
		showAuxiliaryObjectsAction,
		showAlgebraInputAction,
		showKeyboardAction,
		showCmdListAction,
		showInputTopAction,
		showToolBarAction,
		showToolBarTopAction,
		constProtocolAction,
		showConsProtNavigationAction,
		showConsProtNavigationOpenProtAction,
		showConsProtNavigationPlayAction,
		refreshAction,
		recomputeAllViews,
		changePerspectiveAction,
		managePerspectivesAction,
		savePerspectiveAction
	;
	
	private JCheckBoxMenuItem
		cbShowAxes,
		cbShowGrid,
		cbShowAlgebraView,
		cbShowSpreadsheetView, 				// Michael Borcherds 2008-01-14
		cbShowEuclidianView, 				// Florian Sonner 2008-08-29
		cbShowCASView,
		cbShowInputTop, 					// Florian Sonner 2008-09-12
		cbShowToolBar, 						// Florian Sonner 2009-01-10
		cbShowToolBarTop, 					// Florian Sonner 2009-01-10
		cbShowAuxiliaryObjects,
		cbShowConsProtNavigation,
		cbShowConsProtNavigationPlay,
		cbShowConsProtNavigationOpenProt,
		cbShowAlgebraInput,
		cbShowKeyboard,
		cbShowCmdList;
	
	private JMenu
		menuConsProt, 
		menuInput,
		menuToolBar, 
		menuPerspectives
	;
	
	public ViewMenu(Application app, Layout layout) {
		super(app, app.getMenu("View"));
		
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
		JMenuItem mi;
		
		cbShowAxes = new JCheckBoxMenuItem(app.getGuiManager()
				.getShowAxesAction());
		cbShowAxes.setSelected(app.getEuclidianView().getShowXaxis()
				&& app.getEuclidianView().getShowYaxis());
		add(cbShowAxes);

		cbShowGrid = new JCheckBoxMenuItem(app.getGuiManager()
				.getShowGridAction());
		cbShowGrid.setSelected(app.getEuclidianView().getShowGrid());
		add(cbShowGrid);

		cbShowAuxiliaryObjects = new JCheckBoxMenuItem(
				showAuxiliaryObjectsAction);
		cbShowAuxiliaryObjects.setIcon(app.getEmptyIcon());
		cbShowAuxiliaryObjects
				.setSelected(app.getGuiManager().getAlgebraView() == null
						|| app.showAuxiliaryObjects());
		add(cbShowAuxiliaryObjects);

		addSeparator();

		/*
		 * add(new JCheckBoxMenuItem(app.getPlain("DrawingPad"),
		 * app.getEmptyIcon())); add(new
		 * JCheckBoxMenuItem(app.getPlain("AlgebraWindow"),
		 * app.getEmptyIcon())); add(new
		 * JCheckBoxMenuItem(app.getPlain("Spreadsheet"), app.getEmptyIcon()));
		 * add(new JCheckBoxMenuItem(app.getPlain("CAS"),
		 * app.getEmptyIcon())); addSeparator();
		 */

		cbShowEuclidianView = new JCheckBoxMenuItem(showEuclidianViewAction);
		cbShowEuclidianView
				.setIcon(app.getImageIcon("document-properties.png"));
		cbShowEuclidianView
				.setSelected(app.getGuiManager().showEuclidianView());
		add(cbShowEuclidianView);

		cbShowAlgebraView = new JCheckBoxMenuItem(showAlgebraViewAction);
		cbShowAlgebraView.setIcon(app.getEmptyIcon());
		cbShowAlgebraView.setSelected(app.getGuiManager().showAlgebraView());
		setMenuShortCutShiftAccelerator(cbShowAlgebraView, 'A');
		add(cbShowAlgebraView);

		// Michael Borcherds 2008-01-14
		cbShowSpreadsheetView = new JCheckBoxMenuItem(showSpreadsheetAction);
		cbShowSpreadsheetView.setIcon(app.getEmptyIcon());
		cbShowSpreadsheetView.setSelected(app.getGuiManager()
				.showSpreadsheetView());
		setMenuShortCutShiftAccelerator(cbShowSpreadsheetView, 'S');
		add(cbShowSpreadsheetView);
		
		// Florian Sonner 2009-03-29
		cbShowCASView = new JCheckBoxMenuItem(showCASViewAction);
		cbShowCASView.setIcon(app.getEmptyIcon());
		cbShowCASView.setSelected(app.getGuiManager().showCASView());
		add(cbShowCASView);

		addSeparator();
		
		// show/hide keyboard
		cbShowKeyboard = new JCheckBoxMenuItem(showKeyboardAction);
		add(cbShowKeyboard);

		addSeparator();

		// show/hide cmdlist, algebra input
		cbShowAlgebraInput = new JCheckBoxMenuItem(showAlgebraInputAction);
		add(cbShowAlgebraInput);

		menuInput = new JMenu(app.getMenu("InputField") + " ...");
		menuInput.setIcon(app.getEmptyIcon());
		cbShowCmdList = new JCheckBoxMenuItem(showCmdListAction);
		menuInput.add(cbShowCmdList);
		cbShowInputTop = new JCheckBoxMenuItem(showInputTopAction);
		menuInput.add(cbShowInputTop);

		add(menuInput);

		cbShowToolBar = new JCheckBoxMenuItem(showToolBarAction);
		add(cbShowToolBar);

		menuToolBar = new JMenu(app.getMenu("Toolbar"));
		menuToolBar.setIcon(app.getEmptyIcon());
		cbShowToolBarTop = new JCheckBoxMenuItem(showToolBarTopAction);
		menuToolBar.add(cbShowToolBarTop);

		add(menuToolBar);

		// Construction Protocol
		cbShowConsProtNavigation = new JCheckBoxMenuItem(
				showConsProtNavigationAction);
		cbShowConsProtNavigationPlay = new JCheckBoxMenuItem(
				showConsProtNavigationPlayAction);
		cbShowConsProtNavigationOpenProt = new JCheckBoxMenuItem(
				showConsProtNavigationOpenProtAction);

		add(constProtocolAction);

		menuConsProt = new JMenu(app.getPlain("ConstructionProtocol")
				+ " ...");
		menuConsProt.setIcon(app.getImageIcon("table.gif"));
		menuConsProt.add(cbShowConsProtNavigation);
		menuConsProt.add(cbShowConsProtNavigationPlay);
		menuConsProt.add(cbShowConsProtNavigationOpenProt);
		add(menuConsProt);

		addSeparator();

		menuPerspectives = new JMenu("Perspectives");
		menuPerspectives.setIcon(app.getImageIcon("perspective.gif"));
		
		if (!app.isApplet()) {
			add(menuPerspectives);

			updatePerspectives();

			addSeparator();
		}

		mi = add(refreshAction);
		setMenuShortCutAccelerator(mi, 'F');

		mi = add(recomputeAllViews);
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0);
		mi.setAccelerator(ks);
	}
	
	/**
	 * Initialize the actions.
	 */
	private void initActions()
	{
		// Florian Sonner 2008-08-29
		showEuclidianViewAction = new AbstractAction(app.getPlain("DrawingPad")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().setShowEuclidianView(
						!app.getGuiManager().showEuclidianView());
			}
		};

		showAlgebraViewAction = new AbstractAction(app
				.getPlain("AlgebraWindow")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().setShowAlgebraView(
						!app.getGuiManager().showAlgebraView());
			}
		};

		// Michael Borcherds 2008-01-14
		showSpreadsheetAction = new AbstractAction(app.getPlain("Spreadsheet")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().setShowSpreadsheetView(
						!app.getGuiManager().showSpreadsheetView());
			}
		};
		
		// Florian Sonner 2009-03-29
		showCASViewAction= new AbstractAction(app.getPlain("CAS")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().setShowCASView(
						!app.getGuiManager().showCASView());
			}
		};

		showKeyboardAction = new AbstractAction(app.getMenu("ShowKeyboard"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				app.toggleKeyboard();

			}
		};

		showAlgebraInputAction = new AbstractAction(app.getMenu("InputField"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowAlgebraInput(!app.showAlgebraInput());
				app.updateContentPane();
			}
		};

		showCmdListAction = new AbstractAction(app.getMenu("CmdList"), app
				.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowCmdList(!app.showCmdList());
				if (app.getGuiManager().getAlgebraInput() != null)
					SwingUtilities.updateComponentTreeUI(app.getGuiManager()
							.getAlgebraInput());
			}
		};

		// Florian Sonner 2008-09-12
		showInputTopAction = new AbstractAction(app.getMenu("InputOnTop"), app
				.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowInputTop(!app.showInputTop());
				app.updateContentPane();
			}
		};

		// Florian Sonner 2009-01-10
		showToolBarAction = new AbstractAction(app.getMenu("Toolbar"), app
				.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowToolBar(!app.showToolBar());
				app.updateContentPane();
			}
		};

		// Florian Sonner 2009-01-10
		showToolBarTopAction = new AbstractAction(app.getMenu("ToolBarOnTop"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowToolBarTop(!app.showToolBarTop());
				app.updateContentPane();
			}
		};

		// Florian Sonner 2008-09-14
		savePerspectiveAction = new AbstractAction(app
				.getMenu("SaveCurrentPerspective"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.showSaveDialog();
			}
		};

		// Florian Sonner 2008-09-14
		managePerspectivesAction = new AbstractAction(app
				.getMenu("ManagePerspectives"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.showManageDialog();
			}
		};

		// Florian Sonner 2008-09-13
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

		showAuxiliaryObjectsAction = new AbstractAction(app
				.getPlain("AuxiliaryObjects")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowAuxiliaryObjects(!app.showAuxiliaryObjects());
				app.setUnsaved();
			}
		};

		showConsProtNavigationAction = new AbstractAction(app
				.getPlain("ConstructionProtocolNavigation"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowConstructionProtocolNavigation(!app
						.showConsProtNavigation());
				app.setUnsaved();
				app.updateCenterPanel(true);
				app.updateMenubar();
			}
		};

		showConsProtNavigationPlayAction = new AbstractAction(app
				.getPlain("PlayButton"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				ConstructionProtocolNavigation cpn = (ConstructionProtocolNavigation) app
						.getGuiManager().getConstructionProtocolNavigation();
				cpn.setPlayButtonVisible(!cpn.isPlayButtonVisible());
				// cpn.initGUI();
				SwingUtilities.updateComponentTreeUI(cpn);
				app.setUnsaved();
			}
		};

		showConsProtNavigationOpenProtAction = new AbstractAction(app
				.getPlain("ConstructionProtocolButton"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				ConstructionProtocolNavigation cpn = (ConstructionProtocolNavigation) app
						.getGuiManager().getConstructionProtocolNavigation();
				cpn.setConsProtButtonVisible(!cpn.isConsProtButtonVisible());
				// cpn.initGUI();
				SwingUtilities.updateComponentTreeUI(cpn);
				app.setUnsaved();
			}
		};

		constProtocolAction = new AbstractAction(app
				.getPlain("ConstructionProtocol"), app
				.getImageIcon("table.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.loadExportJar();
				try {
					Thread runner = new Thread() {
						public void run() {
							app.getGuiManager().showConstructionProtocol();
							app.updateMenubar();
						}
					};
					runner.start();
				}

				catch (java.lang.NoClassDefFoundError ee) {
					app.showErrorDialog(app.getError("ExportJarMissing"));
					ee.printStackTrace();
				}
			}
		};

		refreshAction = new AbstractAction(app.getMenu("Refresh"),
				new ImageIcon(app.getRefreshViewImage())) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.refreshViews();
			}
		};

		recomputeAllViews = new AbstractAction(
				app.getMenu("RecomputeAllViews"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getKernel().updateConstruction();
			}
		};

		recomputeAllViews = new AbstractAction(
				app.getMenu("RecomputeAllViews"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getKernel().updateConstruction();
			}
		};
	}
	
	@Override
	public void update() {
		DefaultGuiManager guiMananager = (DefaultGuiManager) app
			.getGuiManager();

		EuclidianView ev = app.getEuclidianView();
		cbShowAxes.setSelected(ev.getShowXaxis() && ev.getShowYaxis());
		cbShowGrid.setSelected(ev.getShowGrid());
		
		cbShowEuclidianView
				.setSelected(app.getGuiManager().showEuclidianView());
		cbShowAlgebraView.setSelected(app.getGuiManager().showAlgebraView());
		cbShowSpreadsheetView.setSelected(app.getGuiManager()
				.showSpreadsheetView());
		cbShowCASView.setSelected(app.getGuiManager().showCASView());
		cbShowAuxiliaryObjects.setSelected(app.showAuxiliaryObjects());
		
		boolean showAlgebraView = app.getGuiManager().showAlgebraView();
		cbShowAlgebraView.setSelected(showAlgebraView);
		
		cbShowAuxiliaryObjects.setEnabled(showAlgebraView);
		cbShowAuxiliaryObjects.setSelected(app.showAuxiliaryObjects());
		
		cbShowAlgebraInput.setSelected(app.showAlgebraInput());
		cbShowKeyboard.setSelected(app.showVirtualKeyboard());
		cbShowCmdList.setSelected(app.showCmdList());
		cbShowInputTop.setSelected(app.showInputTop());
		cbShowToolBar.setSelected(app.showToolBar());
		cbShowToolBarTop.setSelected(app.showToolBarTop());
		
		cbShowConsProtNavigation.setSelected(app.showConsProtNavigation());
		
		cbShowConsProtNavigationPlay.setSelected(guiMananager
				.isConsProtNavigationPlayButtonVisible());
		cbShowConsProtNavigationOpenProt.setSelected(guiMananager
				.isConsProtNavigationProtButtonVisible());
		
		cbShowConsProtNavigationPlay.setEnabled(app.showConsProtNavigation());
		cbShowConsProtNavigationOpenProt.setEnabled(app
				.showConsProtNavigation());
		
		// enable menus if necessary
		menuInput.setEnabled(app.showAlgebraInput());
		menuToolBar.setEnabled(app.showToolBar());
	
		if(!app.isApplet())
			updatePerspectives();
		
		// TODO update labels
	}
	
	/**
	 * Update the list of available perspectives.
	 */
	private void updatePerspectives()
	{
		menuPerspectives.removeAll();

		// default perspectives
		Perspective[] defaultPerspectives = Layout.defaultPerspectives;

		for (int i = 0; i < defaultPerspectives.length; ++i) {
			JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
			tmpItem.setText(app.getMenu("Perspective."
					+ defaultPerspectives[i].getId()));
			tmpItem.setIcon(app.getEmptyIcon());
			tmpItem.setActionCommand("d" + i);
			menuPerspectives.add(tmpItem);
		}

		menuPerspectives.addSeparator();

		// user perspectives
		Perspective[] perspectives = layout.getPerspectives();

		if (perspectives.length != 0) {
			for (int i = 0; i < perspectives.length; ++i) {
				JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
				tmpItem.setText(perspectives[i].getId());
				tmpItem.setIcon(app.getEmptyIcon());
				tmpItem.setActionCommand(Integer.toString(i));
				menuPerspectives.add(tmpItem);
			}
			menuPerspectives.addSeparator();
		}

		menuPerspectives.add(managePerspectivesAction);
		menuPerspectives.add(savePerspectiveAction);
	}
}
