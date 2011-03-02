package geogebra.gui.menubar;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.GuiManager;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.layout.Layout;
import geogebra.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.io.layout.Perspective;
import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
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
		showAlgebraInputAction,
		showKeyboardAction,
		showHandwritingAction,
		showHandwritingAutoAddAction,
		showHandwritingTimedAddAction,
		showHandwritingTimedRecogniseAction,
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
		cbShowInputTop, 					// Florian Sonner 2008-09-12
		cbShowToolBar, 						// Florian Sonner 2009-01-10
		cbShowToolBarTop, 					// Florian Sonner 2009-01-10
		cbShowConsProt,
		cbShowConsProtNavigation,
		cbShowConsProtNavigationPlay,
		cbShowConsProtNavigationOpenProt,
		cbShowAlgebraInput,
		cbShowKeyboard,
		cbShowHandwriting,
		cbShowHandwritingAutoAdd,
		cbShowHandwritingTimedAdd,
		cbShowHandwritingTimedRecognise,
		cbShowCmdList,
		cbShowAxes,
		cbShowGrid;
	
	private AbstractAction[] showViews;
	private JCheckBoxMenuItem[] cbViews;
	
	private JMenu
		menuConsProt, 
		menuViews,
		menuHandwriting,
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
		// views
		//menuViews = new JMenu(app.getMenu("Views")+" ...");
		cbShowAxes = new JCheckBoxMenuItem(app.getGuiManager().getShowAxesAction());		
		cbShowAxes.setSelected(app.getEuclidianView().getShowXaxis()
				&& app.getEuclidianView().getShowYaxis());
		add(cbShowAxes);

		cbShowGrid = new JCheckBoxMenuItem(app.getGuiManager().getShowGridAction());
		cbShowGrid.setSelected(app.getEuclidianView().getShowGrid());
		add(cbShowGrid);
		addSeparator();

		initViewItems(this);
		add(this);
		addSeparator();
		
		
		JMenuItem mi;
		
		menuPerspectives = new JMenu(app.getMenu("Perspectives") + " ...");
		menuPerspectives.setIcon(app.getImageIcon("perspective.gif"));
		
		if (!app.isApplet()) {
			add(menuPerspectives);

			updatePerspectives();

			addSeparator();
		}
		

		// show/hide keyboard
		cbShowKeyboard = new JCheckBoxMenuItem(showKeyboardAction);
		app.setEmptyIcon(cbShowKeyboard);
		add(cbShowKeyboard);
		
//		cbShowHandwriting = new JCheckBoxMenuItem(showHandwritingAction);
//		app.setEmptyIcon(cbShowHandwriting);
//		add(cbShowHandwriting);
//		
//		menuHandwriting = new JMenu(app.getMenu("Handwriting"));
//		menuHandwriting.setIcon(app.getEmptyIcon());
//		cbShowHandwritingAutoAdd = new JCheckBoxMenuItem(showHandwritingAutoAddAction);
//		app.setEmptyIcon(cbShowHandwritingAutoAdd);
//		menuHandwriting.add(cbShowHandwritingAutoAdd);
//		cbShowHandwritingTimedAdd = new JCheckBoxMenuItem(showHandwritingTimedAddAction);
//		app.setEmptyIcon(cbShowHandwritingTimedAdd);
//		menuHandwriting.add(cbShowHandwritingTimedAdd);
//		cbShowHandwritingTimedRecognise = new JCheckBoxMenuItem(showHandwritingTimedRecogniseAction);
//		app.setEmptyIcon(cbShowHandwritingTimedRecognise);
//		menuHandwriting.add(cbShowHandwritingTimedRecognise);
		
//		add(menuHandwriting);
		
		addSeparator();

		// show/hide cmdlist, algebra input

		menuInput = new JMenu(app.getMenu("InputField"));
		menuInput.setIcon(app.getEmptyIcon());
		cbShowCmdList = new JCheckBoxMenuItem(showCmdListAction);
		cbShowAlgebraInput = new JCheckBoxMenuItem(showAlgebraInputAction);
		menuInput.add(cbShowAlgebraInput);
		app.setEmptyIcon(cbShowCmdList);
		menuInput.add(cbShowCmdList);
		cbShowInputTop = new JCheckBoxMenuItem(showInputTopAction);
		app.setEmptyIcon(cbShowInputTop);
		menuInput.add(cbShowInputTop);

		add(menuInput);


		menuToolBar = new JMenu(app.getMenu("Toolbar"));
		menuToolBar.setIcon(app.getEmptyIcon());
		cbShowToolBar = new JCheckBoxMenuItem(showToolBarAction);
		app.setEmptyIcon(cbShowToolBar);
		menuToolBar.add(cbShowToolBar);
		cbShowToolBarTop = new JCheckBoxMenuItem(showToolBarTopAction);
		app.setEmptyIcon(cbShowToolBarTop);
		menuToolBar.add(cbShowToolBarTop);

		add(menuToolBar);
		
	    // Construction Protocol
		cbShowConsProt = new JCheckBoxMenuItem(
				constProtocolAction);
		app.setEmptyIcon(cbShowConsProt);
		cbShowConsProtNavigation = new JCheckBoxMenuItem(
				showConsProtNavigationAction);
		app.setEmptyIcon(cbShowConsProtNavigation);
		cbShowConsProtNavigationPlay = new JCheckBoxMenuItem(
				showConsProtNavigationPlayAction);
		app.setEmptyIcon(cbShowConsProtNavigationPlay);
		cbShowConsProtNavigationOpenProt = new JCheckBoxMenuItem(
				showConsProtNavigationOpenProtAction);
		app.setEmptyIcon(cbShowConsProtNavigationOpenProt);


		menuConsProt = new JMenu(app.getPlain("ConstructionProtocol"));
		menuConsProt.setIcon(app.getImageIcon("table.gif"));
		menuConsProt.add(cbShowConsProt);
		menuConsProt.add(cbShowConsProtNavigation);
		menuConsProt.add(cbShowConsProtNavigationPlay);
		menuConsProt.add(cbShowConsProtNavigationOpenProt);
		add(menuConsProt);

		addSeparator();

		mi = add(refreshAction);
		setMenuShortCutAccelerator(mi, 'F');

		mi = add(recomputeAllViews);
		// F9 and Ctrl-R both work, but F9 doesn't on MacOS, so we must display Ctrl-R
		setMenuShortCutAccelerator(mi, 'R');

	}
	
	/**
	 * Initialize the actions.
	 */
	private void initActions()
	{
		initViewActions();

		showKeyboardAction = new AbstractAction(app.getPlain("Keyboard")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				
				if (Application.isVirtualKeyboardActive() && !app.getGuiManager().showVirtualKeyboard()) {
					
					// if keyboard is active but hidden, just show it
					app.getGuiManager().toggleKeyboard(true);
					update();
					
				} else {
				
					Application.setVirtualKeyboardActive(!Application.isVirtualKeyboardActive());				
					app.getGuiManager().toggleKeyboard(Application.isVirtualKeyboardActive());
					update();
				}

			}
		};
		
		/*
		showHandwritingAction = new AbstractAction(app.getPlain("ShowHandwriting")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				
				if (Application.isHandwritingRecognitionActive() && !app.getGuiManager().showHandwritingRecognition()) {
					
					// if handwriting is active but hidden, just show it
					app.getGuiManager().toggleHandwriting(true);
					update();
					
				} else {
				
					Application.setHandwritingRecognitionActive(!Application.isHandwritingRecognitionActive());				
					app.getGuiManager().toggleHandwriting(Application.isHandwritingRecognitionActive());
					update();
				}

			}
		};

		showHandwritingAutoAddAction = new AbstractAction(app.getPlain("AutoAdd")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Application.setHandwritingRecognitionAutoAdd(!Application.isHandwritingRecognitionAutoAdd());
				if (Application.isHandwritingRecognitionAutoAdd() && Application.isHandwritingRecognitionTimedAdd()) {
					Application.setHandwritingRecognitionTimedAdd(!Application.isHandwritingRecognitionTimedAdd());
					app.getGuiManager().updateMenubar();
				}
				if (app.getGuiManager().showHandwritingRecognition()) {
					app.getGuiManager().getHandwriting().repaint();
				}
			}
		};

		showHandwritingTimedAddAction = new AbstractAction(app.getPlain("TimedAdd")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Application.setHandwritingRecognitionTimedAdd(!Application.isHandwritingRecognitionTimedAdd());
				if (Application.isHandwritingRecognitionTimedAdd() && Application.isHandwritingRecognitionAutoAdd()) {
					Application.setHandwritingRecognitionAutoAdd(!Application.isHandwritingRecognitionAutoAdd());
					app.getGuiManager().updateMenubar();
				}
				if (app.getGuiManager().showHandwritingRecognition()) {
					app.getGuiManager().getHandwriting().repaint();
				}
			}
		};

		showHandwritingTimedRecogniseAction = new AbstractAction(app.getPlain("TimedRecognise")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Application.setHandwritingRecognitionTimedRecognise(!Application.isHandwritingRecognitionTimedRecognise());
				if (app.getGuiManager().showHandwritingRecognition()) {
					app.getGuiManager().getHandwriting().repaint();
				}
			}
		}; */

		showAlgebraInputAction = new AbstractAction(app.getMenu("Show")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowAlgebraInput(!app.showAlgebraInput(), true);
				app.updateContentPane();
			}
		};

		showCmdListAction = new AbstractAction(app.getMenu("CmdList")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowCmdList(!app.showCmdList());
				if (app.getGuiManager().getAlgebraInput() != null)
					SwingUtilities.updateComponentTreeUI(app.getGuiManager()
							.getAlgebraInput());
			}
		};

		showInputTopAction = new AbstractAction(app.getMenu("ShowAtTop"), app
				.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowInputTop(!app.showInputTop(), true);
			}
		};

		showToolBarAction = new AbstractAction(app.getMenu("Show")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowToolBar(!app.showToolBar());
				app.updateContentPane();
			}
		};

		showToolBarTopAction = new AbstractAction(app.getMenu("ShowAtTop")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowToolBarTop(!app.showToolBarTop());
				app.updateContentPane();
			}
		};

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

		showConsProtNavigationAction = new AbstractAction(app
				.getPlain("ConstructionProtocolNavigation")) {
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
				.getPlain("PlayButton")) {
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
				.getPlain("ConstructionProtocolButton")) {
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
				.getMenu("Show")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					Thread runner = new Thread() {
						public void run() {
							GuiManager gm = app.getGuiManager();
							if (gm.isConstructionProtocolVisible())
								gm.hideConstructionProtocol();
							else gm.showConstructionProtocol();
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
		GuiManager guiMananager = (GuiManager) app
			.getGuiManager();

		updateViews();
		
		cbShowAlgebraInput.setSelected(app.showAlgebraInput());
		cbShowKeyboard.setSelected(Application.isVirtualKeyboardActive());
//		cbShowHandwriting.setSelected(Application.isHandwritingRecognitionActive());
//		cbShowHandwritingAutoAdd.setSelected(Application.isHandwritingRecognitionAutoAdd());
//		cbShowHandwritingTimedAdd.setSelected(Application.isHandwritingRecognitionTimedAdd());
//		cbShowHandwritingTimedRecognise.setSelected(Application.isHandwritingRecognitionTimedRecognise());
		cbShowCmdList.setSelected(app.showCmdList());
		cbShowInputTop.setSelected(app.showInputTop());
		cbShowToolBar.setSelected(app.showToolBar());
		cbShowToolBarTop.setSelected(app.showToolBarTop());
		
		cbShowConsProt.setSelected(app.getGuiManager().isConstructionProtocolVisible());
		cbShowConsProtNavigation.setSelected(app.showConsProtNavigation());
		
		cbShowConsProtNavigationPlay.setSelected(guiMananager
				.isConsProtNavigationPlayButtonVisible());
		cbShowConsProtNavigationOpenProt.setSelected(guiMananager
				.isConsProtNavigationProtButtonVisible());
		
		cbShowConsProtNavigationPlay.setEnabled(app.showConsProtNavigation());
		cbShowConsProtNavigationOpenProt.setEnabled(app.showConsProtNavigation());
		
		// enable menus if necessary
		//menuInput.setEnabled(app.showAlgebraInput());
		//menuToolBar.setEnabled(app.showToolBar());
	
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
	
	private void initViewActions() {
		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());
		int viewsInMenu = 0;
		
		// count visible views first..
		for(DockPanel panel : dockPanels) {
			// skip panels with negative order by design
			if(panel.getMenuOrder() < 0) {
				continue;
			}
			++viewsInMenu;
		}
		
		// construct array with menu items
		showViews = new AbstractAction[viewsInMenu];
		{
			int i = 0;
			AbstractAction action;
			
			for(DockPanel panel : dockPanels) {
				// skip panels with negative order by design
				if(panel.getMenuOrder() < 0) {
					continue;
				}
				
				final int viewId = panel.getViewId();
				
				action = new AbstractAction(app.getPlain(panel.getViewTitle())) {
					public void actionPerformed(ActionEvent arg0) {
						app.getGuiManager().setShowView(!app.getGuiManager().showView(viewId), viewId);
					}
				};
				
				showViews[i] = action;
				++i;
			}
		}
	}
	
	private void initViewItems(JMenu menu) {
		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());
		int viewsInMenu = 0;
		
		// count visible views first..
		for(DockPanel panel : dockPanels) {
			// skip panels with negative order by design
			if(panel.getMenuOrder() < 0) {
				continue;
			}
			++viewsInMenu;
		}
		
		// construct array with menu items
		cbViews = new JCheckBoxMenuItem[viewsInMenu];
		{
			int i = 0;
			JCheckBoxMenuItem cb;
			
			for(DockPanel panel : dockPanels) {
				// skip panels with negative order by design
				if(panel.getMenuOrder() < 0) {
					continue;
				}
				
				cb = new JCheckBoxMenuItem(showViews[i]);
				cb.setIcon(panel.getIcon());
				
				if(panel.hasMenuShortcut()) {
					setMenuShortCutShiftAccelerator(cb, panel.getMenuShortcut());
				}

				menu.add(cb);
				cbViews[i] = cb;
				++i;
			}
		}
	}
	
	private void updateViews() {		
		DockPanel[] dockPanels = layout.getDockManager().getPanels();
		Arrays.sort(dockPanels, new DockPanel.MenuOrderComparator());
		int viewsInMenu = 0;
		
		// count visible views first..
		for(DockPanel panel : dockPanels) {
			// skip panels with negative order by design
			if(panel.getMenuOrder() < 0) {
				continue;
			}
			++viewsInMenu;
		}
		
		// update views
		{
			int i = 0;
			
			for(DockPanel panel : dockPanels) {
				// skip panels with negative order by design
				if(panel.getMenuOrder() < 0) {
					continue;
				}
				
				cbViews[i].setSelected(app.getGuiManager().showView(panel.getViewId()));
				++i;
			}
		}
	}
}
