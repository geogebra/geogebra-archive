package tutor.gui;

import geogebra.Application;
import geogebra.GeoGebra;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.menubar.Menubar;
import geogebra.gui.menubar.MenubarImpl;
import geogebra.kernel.Kernel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class TutorMenubar extends MenubarImpl implements Menubar, ActionListener {

	public TutorMenubar(Application app) {
		super();
		this.app = app;
		kernel = app.getKernel();
	}

	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		
		// change angle unit
		if (cmd.equals("Degree")) {
			kernel.setAngleUnit(Kernel.ANGLE_DEGREE);
			kernel.updateConstruction();
			app.setUnsaved();
		} else if (cmd.equals("Radiant")) {
			kernel.setAngleUnit(Kernel.ANGLE_RADIANT);
			kernel.updateConstruction();
			app.setUnsaved();
		}

		// change graphics quality
		else if (cmd.equals("LowQuality")) {
			app.getEuclidianView().setAntialiasing(false);
		} else if (cmd.equals("HighQuality")) {
			app.getEuclidianView().setAntialiasing(true);
		}

		// font size
		else if (cmd.endsWith("pt")) {
			try {
				app.setFontSize(Integer.parseInt(cmd.substring(0, 2)));
				app.setUnsaved();
				System.gc();
			} catch (Exception e) {
				app.showError(e.toString());
			}
			;
		}

		// decimal places
		else if (cmd.endsWith("decimals")) {
			try {
				kernel.setPrintDecimals(Integer.parseInt(cmd.substring(0, 1)));
				kernel.updateConstruction();
				app.setUnsaved();
			} catch (Exception e) {
				app.showError(e.toString());
			}			
		}

		// Point capturing
		else if (cmd.endsWith("PointCapturing")) {
			int mode = Integer.parseInt(cmd.substring(0, 1));
			app.getEuclidianView().setPointCapturing(mode);
			app.setUnsaved();
		}

		// Continuity
		else if (cmd.endsWith("Continuity")) {
			boolean state = cmd.startsWith("true");
			kernel.setContinuous(state);
			kernel.updateConstruction();
			app.setUnsaved();
		}
		
		
		// Labeling
		else if (cmd.endsWith("labeling")) {
			int style = Integer.parseInt(cmd.substring(0, 1));
			app.setLabelingStyle(style);
			app.setUnsaved();
		}
	}
	
	private void addFileMenu() {
		
		menuFile = new JMenu(app.getMenu("File"));
		updateMenuFile();	
		add(menuFile);
	}
	
	private void addEditMenu() {
		
		JMenu menu, submenu;
		JMenuItem mi;
		int pos;
		
		menu = new JMenu(app.getMenu("Edit"));
		if (app.isUndoActive()) {
			mi = menu.add(app.getUndoAction());
			setMenuShortCutAccelerator(mi, 'Z');
			mi = menu.add(app.getRedoAction());
			if (GeoGebra.MAC_OS)
				// Command-Shift-Z
				setMenuShortCutShiftAccelerator(mi, 'Z');
			else
				// Ctrl-Y
				setMenuShortCutAccelerator(mi, 'Y');
			menu.addSeparator();
		}
		
		if (app.letDelete()) {
			mi = menu.add(deleteAction);
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));	
		}
		menu.addSeparator();
		
		mi = menu.add(selectAllAction);
		setMenuShortCutAccelerator(mi, 'A');
		menu.addSeparator();

		mi = menu.add(propertiesAction);
		setMenuShortCutAccelerator(mi, 'E');
		add(menu);
	}
	
	private void addHelpMenu() {
		
		JMenu menu, submenu;
		JMenuItem mi;
		int pos;
		
		menu = new JMenu(app.getMenu("Help"));
		menu.add(helpAction);
		menu.addSeparator();

		menu.add(websiteAction);
		menu.add(forumAction);
		menu.add(wikiAction);

		menu.addSeparator();

		menu.add(infoAction);
		add(menu);
	}
	
	private void addViewMenu() {

		JMenu menu, submenu;
		JMenuItem mi;
		int pos;

		// View
				menu = new JMenu(app.getMenu("View"));
				cbShowAxes = new JCheckBoxMenuItem(app.getShowAxesAction());		
				cbShowAxes.setSelected(app.getEuclidianView().getShowXaxis()
						&& app.getEuclidianView().getShowYaxis());
				menu.add(cbShowAxes);

				cbShowGrid = new JCheckBoxMenuItem(app.getShowGridAction());
				cbShowGrid.setSelected(app.getEuclidianView().getShowGrid());
				menu.add(cbShowGrid);
				menu.addSeparator();

				cbShowAlgebraView = new JCheckBoxMenuItem(showAlgebraViewAction);		
				cbShowAlgebraView.setIcon(app.getEmptyIcon());
				cbShowAlgebraView.setSelected(app.showAlgebraView());
				setMenuShortCutShiftAccelerator(cbShowAlgebraView, 'A');
				menu.add(cbShowAlgebraView);

			    // Michael Borcherds 2008-01-14
				cbShowSpreadsheet = new JCheckBoxMenuItem(showSpreadsheetAction);		
				cbShowSpreadsheet.setIcon(app.getEmptyIcon());
				cbShowSpreadsheet.setSelected(app.showSpreadsheet());
				setMenuShortCutShiftAccelerator(cbShowSpreadsheet, 'S');
				menu.add(cbShowSpreadsheet);

				cbShowAuxiliaryObjects = new JCheckBoxMenuItem(
						showAuxiliaryObjectsAction);
				cbShowAuxiliaryObjects.setIcon(app.getEmptyIcon());
				cbShowAuxiliaryObjects.setSelected(app.getAlgebraView() == null
						|| app.getAlgebraView().showAuxiliaryObjects());
				cbShowAuxiliaryObjects.setEnabled(app.showAlgebraView());
				menu.add(cbShowAuxiliaryObjects);

				cbHorizontalSplit = new JCheckBoxMenuItem(horizontalSplitAction);				
				cbHorizontalSplit.setIcon(app.getEmptyIcon());
				menu.add(cbHorizontalSplit);

				menu.addSeparator();

				// show/hide cmdlist, algebra input
				cbShowAlgebraInput = new JCheckBoxMenuItem(showAlgebraInputAction);		
				menu.add(cbShowAlgebraInput);

				cbShowCmdList = new JCheckBoxMenuItem(showCmdListAction);		
				menu.add(cbShowCmdList);
				menu.addSeparator();

				// Construction Protocol
				cbShowConsProtNavigation = new JCheckBoxMenuItem(
						showConsProtNavigationAction);
				cbShowConsProtNavigationPlay = new JCheckBoxMenuItem(
						showConsProtNavigationPlayAction);
				cbShowConsProtNavigationOpenProt = new JCheckBoxMenuItem(
						showConsProtNavigationOpenProtAction);	
				menu.add(constProtocolAction);
				menu.add(cbShowConsProtNavigation);
				menu.add(cbShowConsProtNavigationPlay);
				menu.add(cbShowConsProtNavigationOpenProt);		
				
				menu.addSeparator();
				mi = menu.add(refreshAction);
				setMenuShortCutAccelerator(mi, 'F');
				
				add(menu);
	}
	private void addToolsMenu() {
		
		menuTools = new JMenu(app.getMenu("Tools"));
		add(menuTools);
		menuTools.add(showCreateToolsAction);
		menuTools.add(showManageToolsAction);
		menuTools.addSeparator();
		menuTools.add(toolbarConfigAction);		
	}
	
	private void addOptionsMenu() {
		
		JMenu menu, submenu;
		JMenuItem mi;
		int pos;
		
		// Options
		menu = new JMenu(app.getMenu("Options"));

		// point capturing
		menuPointCapturing = new JMenu(app.getMenu("PointCapturing"));
		menuPointCapturing.setIcon(app.getImageIcon("magnet.gif"));
		String[] strPointCapturing = { 
				"Labeling.automatic",
				"on",
				app.getMenu("on") + " (" + app.getMenu("Grid") + ")", 
				"off" };
		String[] strPointCapturingAC = { 
				"3 PointCapturing" ,
				"1 PointCapturing",
				"2 PointCapturing", 
				"0 PointCapturing" };
		addRadioButtonMenuItems(menuPointCapturing, (ActionListener) this, strPointCapturing,
				strPointCapturingAC, 0);
		menu.add(menuPointCapturing);
		updateMenuPointCapturing();

		// Angle unit
		menuAngleUnit = new JMenu(app.getMenu("AngleUnit"));
		menuAngleUnit.setIcon(app.getImageIcon("mode_angle_16.gif"));
		String[] strAngleUnit = { "Degree", "Radiant" };
		addRadioButtonMenuItems(menuAngleUnit, (ActionListener) this, strAngleUnit, strAngleUnit,
				0);
		menu.add(menuAngleUnit);
		updateMenuAngleUnit();

		// decimal places
		menuDecimalPlaces = new JMenu(app.getMenu("DecimalPlaces"));
		menuDecimalPlaces.setIcon(app.getEmptyIcon());
		String[] strDecimalSpaces = { "0", "1", "2", "3", "4", "5" };
		String[] strDecimalSpacesAC = { "0 decimals", "1 decimals",
				"2 decimals", "3 decimals", "4 decimals", "5 decimals" };
		addRadioButtonMenuItems(menuDecimalPlaces, (ActionListener) this, strDecimalSpaces,
				strDecimalSpacesAC, 0);
		menu.add(menuDecimalPlaces);
		updateMenuDecimalPlaces();

		// continuity
		menuContinuity = new JMenu(app.getMenu("Continuity"));
		menuContinuity.setIcon(app.getEmptyIcon());
		String[] strContinuity = { "on", "off" };
		String[] strContinuityAC = { "true Continuity", "false Continuity" };
		addRadioButtonMenuItems(menuContinuity, (ActionListener) this, strContinuity,
				strContinuityAC, 0);
		menu.add(menuContinuity);
		updateMenuContinuity();

		menu.addSeparator();

		// point style
		menuPointStyle = new JMenu(app.getMenu("PointStyle"));
		menuPointStyle.setIcon(app.getImageIcon("mode_point_16.gif"));
		// dot, circle, cross
		String[] strPointStyle = { "\u25cf", "\u25cb", "\u2716" };
		String[] strPointStyleAC = { "0", "2", "1" };
		ActionListener psal = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int style = Integer.parseInt(ae.getActionCommand());
				app.getEuclidianView().setPointStyle(style);
			}
		};
		addRadioButtonMenuItems(menuPointStyle, psal, strPointStyle,
				strPointStyleAC, 0);
		menu.add(menuPointStyle);
		updateMenuPointStyle();

		// added by Lo�c BEGIN
		// right angle style

		menuRightAngleStyle = new JMenu(app.getMenu("RightAngleStyle"));
		menuRightAngleStyle.setIcon(app.getImageIcon("right_angle.gif"));
		// dot, none, square
		String[] strAngleStyle = { app.getPlain("off"), "\u25a1", "\u2219" };
		String[] strAngleStyleAC = {
				String.valueOf(EuclidianView.RIGHT_ANGLE_STYLE_NONE),
				String.valueOf(EuclidianView.RIGHT_ANGLE_STYLE_SQUARE),
				String.valueOf(EuclidianView.RIGHT_ANGLE_STYLE_DOT) };
		ActionListener asal = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int style = Integer.parseInt(ae.getActionCommand());
				app.getEuclidianView().setRightAngleStyle(style);
			}
		};
		addRadioButtonMenuItems(menuRightAngleStyle, asal, strAngleStyle,
				strAngleStyleAC, 0);
		menu.add(menuRightAngleStyle);
		updateMenuRightAngleStyle();
		// END

		// coordinate style
		menuCoordStyle = new JMenu(app.getPlain("Coordinates"));
		menuCoordStyle.setIcon(app.getEmptyIcon());
		// dot, circle, cross
		String[] strCoordStyle = { "A = (x, y)", "A(x | y)" };
		String[] strCoordStyleAC = { "0", "1" };
		ActionListener csal = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int style = Integer.parseInt(ae.getActionCommand());
				kernel.setCoordStyle(style);
				kernel.updateConstruction();
			}
		};
		addRadioButtonMenuItems(menuCoordStyle, csal, strCoordStyle,
				strCoordStyleAC, 0);
		menu.add(menuCoordStyle);
		updateMenuCoordStyle();
		
		// Labeling
		menuLabeling = new JMenu(app.getMenu("Labeling"));
		menuLabeling.setIcon(app.getImageIcon("mode_showhidelabel_16.gif"));
		String[] lstr = { "Labeling.automatic", "Labeling.on", "Labeling.off", "Labeling.pointsOnly"  };		
		String[] lastr = { "0_labeling", "1_labeling", "2_labeling", "3_labeling"  };
		addRadioButtonMenuItems(menuLabeling, (ActionListener) this, lstr, lastr, 0);
		menu.add(menuLabeling);		

		/*
		// Graphics quality
		submenu = new JMenu(app.getMenu("GraphicsQuality"));
		String[] gqfi = { "LowQuality", "HighQuality" };
		if (app.getEuclidianView().getAntialiasing())
			pos = 1;
		else
			pos = 0;
		addRadioButtonMenuItems(submenu, this, gqfi, gqfi, pos);
		menu.add(submenu);
		*/
		
		menu.addSeparator();

		// Font size
		submenu = new JMenu(app.getMenu("FontSize"));
		submenu.setIcon(app.getImageIcon("font.png"));
		String[] fsfi = { "12 pt", "14 pt", "16 pt", "18 pt", "20 pt", "24 pt",
				"28 pt", "32 pt" };

		// find current pos
		String strFS = app.getFontSize() + " pt";
		pos = 0;
		for (int i = 0; i < fsfi.length; i++) {
			if (strFS.equals(fsfi[i])) {
				pos = i;
				break;
			}
		}

		addRadioButtonMenuItems(submenu, (ActionListener) this, fsfi, fsfi, pos);
		menu.add(submenu);

		/*
		 * // FontName menuFontName = new JMenu(getMenu("PointCapturing"));
		 * String[] strFontName = { "Sans Serif", "Serif" }; String[]
		 * strFontNameAC = { "SansSerif", "Serif" };
		 * addRadioButtonMenuItems(menuFontName, al, strFontName, strFontNameAC,
		 * 0); menu.add(menuFontName); updateMenuFontName();
		 */

		//menu.addSeparator();

		// Language		
		if (app.propertiesFilesPresent()) {
			LanguageActionListener langListener = new LanguageActionListener();
			submenu = new JMenu(app.getMenu("Language"));
			submenu.setIcon(app.getImageIcon("globe.png"));
			addLanguageMenuItems(submenu, langListener);
			menu.add(submenu);
		}

		menu.addSeparator();

		// drawing pad properteis
		menu.add(drawingPadPropAction);
		add(menu);
		
		if (!app.isApplet()) {
			// Preferences
			menu.addSeparator();
			//submenu = new JMenu(app.getMenu("Settings"));			
			menu.add(savePreferencesAction);
			menu.add(clearPreferencesAction);
			//menu.add(submenu);
		}
	}
	
	public void initMenubar() {
		initActions();
		
		JMenu menu, submenu;
		JMenuItem mi;
		int pos;
		removeAll();

		// File menu
		addFileMenu();
		
		// Edit menu
		addEditMenu();
		
		// View Menu
		addViewMenu();
		
		// Tools menu
		addToolsMenu();

		if (!app.isApplet()) {
			// windows menu
			menuWindow = new JMenu(app.getMenu("Window"));
			updateMenuWindow();
			add(menuWindow);
		}

		addOptionsMenu();
		
		addHelpMenu();
		
		updateMenubar();
	}

}
