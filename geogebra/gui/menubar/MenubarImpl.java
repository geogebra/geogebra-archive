package geogebra.gui.menubar;

import geogebra.GeoGebra;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.DefaultGuiManager;
import geogebra.gui.ToolCreationDialog;
import geogebra.gui.ToolManagerDialog;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.layout.Layout;
import geogebra.gui.toolbar.MyToolbar;
import geogebra.gui.util.BrowserLauncher;
import geogebra.gui.util.ImageSelection;
import geogebra.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.io.layout.Perspective;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.GeoGebraPreferences;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public abstract class MenubarImpl extends JMenuBar implements Menubar {
	private static final long serialVersionUID = 1736020764918189176L;
	
	protected Layout layout;

	// Actions
	protected AbstractAction refreshAction, recomputeAllViews,
			drawingPadToClipboardAction, deleteAll, newWindowAction,
			propertiesAction, constProtocolAction, drawingPadPropAction,
			toolbarConfigAction, showAlgebraViewAction, showAlgebraInputAction,
			showEuclidianViewAction, // Florian Sonner 2008-08-29
			showInputTopAction,		 // Florian Sonner 2008-09-12
			showSpreadsheetAction,     // Michael Borcherds 2008-01-14
			showCmdListAction,
			modeChangeAction, // Florian Sonner 2008-10-04
			showAuxiliaryObjectsAction, showConsProtNavigationAction,
			showConsProtNavigationPlayAction,
			showConsProtNavigationOpenProtAction, loadAction, saveAction,
			ignoreDocumentPerspectiveAction, // Florian Sonner 2008-09-14
			savePerspectiveAction, // Florian Sonner 2008-09-14
			changePerspectiveAction, // Florian Sonner 2008-10-04
			managePerspectivesAction, // Florian Sonner 2008-09-14
			showViewTitlebarAction, // Florian Sonenr 2008-10-22
			saveAsAction, //printProtocolAction, 
			printEuclidianViewAction,
			exitAction, exitAllAction, helpAction,
			// updateAction,
			infoAction, exportGraphicAction, exportWorksheet,
			exportPSTricksAction,exportPgfAction,
			showCreateToolsAction, showManageToolsAction,
			savePreferencesAction, clearPreferencesAction,
			selectAllAction, deleteAction, websiteAction, forumAction, wikiAction,
			selectCurrentLayerAction, // Michael Borcherds 2008-03-03
			selectAllDescendantsAction, selectAllAncestorsAction;

	protected JCheckBoxMenuItem cbShowAxes, cbShowGrid, 
			cbShowAlgebraView,
	        cbShowSpreadsheetView,     // Michael Borcherds 2008-01-14
	        cbShowEuclidianView, 		// Florian Sonner 2008-08-29
	        cbShowInputTop, 		 	// Florian Sonner 2008-09-12
	        cbIgnoreDocumentPerspective, // Florian Sonner 2008-09-14
	        cbShowViewTitlebar, 			// Florian Sonner 2008-10-22
			cbShowAuxiliaryObjects,
			cbShowConsProtNavigation,
			cbShowConsProtNavigationPlay,
			cbShowConsProtNavigationOpenProt,
			cbShowAlgebraInput,
			cbShowCmdList;

	protected JMenu menuAngleUnit, menuPointCapturing, menuDecimalPlaces,
			menuContinuity, menuPointStyle, menuBooleanSize, menuRightAngleStyle,
			menuCoordStyle, menuLabeling, menuWindow, menuFile, menuTools, menuViewConsProt, menuViewInput, menuViewPerspectives;

	//private JMenuItem miCloseAll;

	protected Application app;

	protected Kernel kernel;

	public MenubarImpl(Layout layout) {
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SystemColor.controlShadow));
		
		this.layout = layout;
	}
	
	public void updateMenubar() {		
		DefaultGuiManager guiMananager = (DefaultGuiManager) app.getGuiManager();
		
		EuclidianView ev = app.getEuclidianView();
		cbShowAxes.setSelected(ev.getShowXaxis() && ev.getShowYaxis());
	    cbShowGrid.setSelected(ev.getShowGrid());
	    
	    cbShowEuclidianView.setSelected(app.getGuiManager().showEuclidianView());
	    cbShowAlgebraView.setSelected(app.getGuiManager().showAlgebraView());
	    cbShowSpreadsheetView.setSelected(app.getGuiManager().showSpreadsheetView());
        cbShowAlgebraInput.setSelected(app.showAlgebraInput());
        cbShowAuxiliaryObjects.setSelected(app.showAuxiliaryObjects());

		boolean showAlgebraView = app.getGuiManager().showAlgebraView();
		cbShowAlgebraView.setSelected(showAlgebraView);
		
		cbShowAuxiliaryObjects.setEnabled(showAlgebraView);
		cbShowAuxiliaryObjects.setSelected(app.showAuxiliaryObjects());
		
		cbShowAlgebraInput.setSelected(app.showAlgebraInput());
		cbShowCmdList.setSelected(app.showCmdList());
		cbShowInputTop.setSelected(app.showInputTop());
		menuViewInput.setEnabled(app.showAlgebraInput());		
		
		cbShowConsProtNavigation.setSelected(app.showConsProtNavigation());	
		
		cbShowConsProtNavigationPlay
				.setSelected(guiMananager.isConsProtNavigationPlayButtonVisible());
		cbShowConsProtNavigationOpenProt
				.setSelected(guiMananager.isConsProtNavigationProtButtonVisible());
		
		cbShowConsProtNavigationPlay.setEnabled(app.showConsProtNavigation());
		cbShowConsProtNavigationOpenProt.setEnabled(app.showConsProtNavigation());

		// Michael Borcherds 2008-03-03 BEGIN put these back in
        updateMenuContinuity();
        updateMenuPointCapturing();
        updateMenuAngleUnit();
        updateMenuDecimalPlaces();
        updateMenuPointStyle();
        updateMenuBooleanSize(); // Michael Borcherds 2008-05-12
        updateMenuRightAngleStyle();
        updateMenuCoordStyle();	
        updateMenuLabeling();
        updateMenuTools(); // Florian Sonner 2008-09-12
        
        if(!app.isApplet())
        	updateMenuPerspective(); // Florian Sonner 2008-09-14
        
        updateActions();
        updateSelection();
		// Michael Borcherds 2008-03-03 END
                
	}

	public void updateMenuFile() {
		if (menuFile == null) return;
		
		menuFile.removeAll();
		
		JMenu menu = menuFile;
		JMenuItem mi;
		
		if (!app.isApplet()) {
			// "New" in application: new window
			mi = new JMenuItem(newWindowAction);	
			setMenuShortCutAccelerator(mi, 'N');
			menu.add(mi);
		}			
		
		// "New": reset 
		mi = menu.add(deleteAll);	
		
		mi = menu.add(loadAction);
		setMenuShortCutAccelerator(mi, 'O'); // open
		menu.addSeparator();
		mi = menu.add(saveAction);
		setMenuShortCutAccelerator(mi, 'S');
		mi = menu.add(saveAsAction);	
		// now assigned to spreadsheet: setMenuShortCutShiftAccelerator(mi, 'S');
		menu.addSeparator();
//		submenu = new JMenu(app.getMenu("PrintPreview"));
//		submenu.setIcon(app.getImageIcon("document-print-preview.png"));
//		submenu.add(printEuclidianViewAction);
//	//	submenu.add(printProtocolAction);
//		menu.add(submenu);
		mi = menu.add(printEuclidianViewAction);
		mi.setText(app.getMenu("PrintPreview"));
		mi.setIcon(app.getImageIcon("document-print-preview.png"));
		setMenuShortCutAccelerator(mi, 'P');
				
		// export
		JMenu submenu = new JMenu(app.getMenu("Export"));
		submenu.setIcon(app.getEmptyIcon());
		menu.add(submenu);
		mi = submenu.add(exportWorksheet);
		setMenuShortCutShiftAccelerator(mi, 'W');
	
		submenu.addSeparator();
		//submenu.add(htmlCPAction);
		mi = submenu.add(exportGraphicAction);
		setMenuShortCutShiftAccelerator(mi, 'P');
		
		mi = submenu.add(drawingPadToClipboardAction);
		setMenuShortCutShiftAccelerator(mi, 'C');
	
		
		submenu.addSeparator();
		mi = submenu.add(exportPSTricksAction);
		setMenuShortCutShiftAccelerator(mi, 'T');
		
		// Added by Loïc Le Coq
		mi = submenu.add(exportPgfAction);
		//End						
			
		
		// DONE HERE WHEN APPLET
		if (app.isApplet()) return;
		
		
		// LAST FILES list
		int size = Application.getFileListSize();
		if (size > 0) {
			menu.addSeparator();						
			for (int i = 0; i < 4; i++) {
				File file = Application.getFromFileList(i);
				if (file != null) {										
					mi = new JMenuItem(file.getName());
					mi.setIcon(app.getImageIcon("geogebra.gif"));					
					ActionListener al = new LoadFileListener(app, file);
					mi.addActionListener(al);
					menu.add(mi);
				}
			}						
		}
		
		// close
		menu.addSeparator();
		mi = menu.add(exitAction);
		if (Application.MAC_OS) {
			setMenuShortCutAccelerator(mi, 'W');
		} else {
			// Alt + F4
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK);
			mi.setAccelerator(ks);
		}

		// close all		
		if (GeoGebraFrame.getInstanceCount() > 1) {								
			menu.add(exitAllAction);
		}
	}

	public void initMenubar() {
		initActions();
		
		JMenu menu, submenu;
		JMenuItem mi;
		int pos;
		removeAll();

		
		// File
		menuFile = new JMenu(app.getMenu("File"));
		updateMenuFile();	
		add(menuFile);
		
		// Edit
		menu = new JMenu(app.getMenu("Edit"));
		if (app.isUndoActive()) {
			mi = menu.add(app.getGuiManager().getUndoAction());
			setMenuShortCutAccelerator(mi, 'Z');
			mi = menu.add(app.getGuiManager().getRedoAction());
			if (Application.MAC_OS)
				// Command-Shift-Z
				setMenuShortCutShiftAccelerator(mi, 'Z');
			else
				// Ctrl-Y
				setMenuShortCutAccelerator(mi, 'Y');
			menu.addSeparator();
		}
		
		// Michael Borcherds 2008-03-03 added to Edit menu
		mi = menu.add(drawingPadToClipboardAction);
		setMenuShortCutShiftAccelerator(mi, 'C');

		//mi = menu.add(DataFromClipboardAction);
		//setMenuShortCutAccelerator(mi, 'V');
				
		if (app.letDelete()) {
			mi = menu.add(deleteAction);
		
			if (Application.MAC_OS) {
				mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));							
			} else {				
				mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
			}	
		}
		menu.addSeparator();
		
		mi = menu.add(selectAllAction);
		setMenuShortCutAccelerator(mi, 'A');
		
		mi = menu.add(selectCurrentLayerAction);
		setMenuShortCutAccelerator(mi, 'L');
		
		mi = menu.add(selectAllDescendantsAction);
		setMenuShortCutShiftAccelerator(mi, 'Q');
		
		mi = menu.add(selectAllAncestorsAction);
		setMenuShortCutAccelerator(mi, 'Q');
		
		menu.addSeparator();

		mi = menu.add(propertiesAction);
		setMenuShortCutAccelerator(mi, 'E');
		add(menu);
		
		// View
		menu = new JMenu(app.getMenu("View"));
		cbShowAxes = new JCheckBoxMenuItem(app.getGuiManager().getShowAxesAction());		
		cbShowAxes.setSelected(app.getEuclidianView().getShowXaxis()
				&& app.getEuclidianView().getShowYaxis());
		menu.add(cbShowAxes);

		cbShowGrid = new JCheckBoxMenuItem(app.getGuiManager().getShowGridAction());
		cbShowGrid.setSelected(app.getEuclidianView().getShowGrid());
		menu.add(cbShowGrid);
		
		cbShowAuxiliaryObjects = new JCheckBoxMenuItem(
				showAuxiliaryObjectsAction);
		cbShowAuxiliaryObjects.setIcon(app.getEmptyIcon());
		cbShowAuxiliaryObjects.setSelected(app.getGuiManager().getAlgebraView() == null
				|| app.showAuxiliaryObjects());
		menu.add(cbShowAuxiliaryObjects);
		
		menu.addSeparator();
		
		/*
		menu.add(new JCheckBoxMenuItem(app.getPlain("DrawingPad"), app.getEmptyIcon()));
		menu.add(new JCheckBoxMenuItem(app.getPlain("AlgebraWindow"), app.getEmptyIcon()));
		menu.add(new JCheckBoxMenuItem(app.getPlain("Spreadsheet"), app.getEmptyIcon()));
		menu.add(new JCheckBoxMenuItem(app.getPlain("CAS"), app.getEmptyIcon()));
		menu.addSeparator();*/
		
		cbShowEuclidianView = new JCheckBoxMenuItem(showEuclidianViewAction);
		cbShowEuclidianView.setIcon(app.getImageIcon("document-properties.png"));
		cbShowEuclidianView.setSelected(app.getGuiManager().showEuclidianView());
		menu.add(cbShowEuclidianView);
		
		cbShowAlgebraView = new JCheckBoxMenuItem(showAlgebraViewAction);		
		cbShowAlgebraView.setIcon(app.getEmptyIcon());
		cbShowAlgebraView.setSelected(app.getGuiManager().showAlgebraView());
		setMenuShortCutShiftAccelerator(cbShowAlgebraView, 'A');
		menu.add(cbShowAlgebraView);
		
	    // Michael Borcherds 2008-01-14
		cbShowSpreadsheetView = new JCheckBoxMenuItem(showSpreadsheetAction);		
		cbShowSpreadsheetView.setIcon(app.getEmptyIcon());
		cbShowSpreadsheetView.setSelected(app.getGuiManager().showSpreadsheetView());	
		setMenuShortCutShiftAccelerator(cbShowSpreadsheetView, 'S');
		menu.add(cbShowSpreadsheetView);

		menu.addSeparator();
		
		// show/hide cmdlist, algebra input
		cbShowAlgebraInput = new JCheckBoxMenuItem(showAlgebraInputAction);		
		menu.add(cbShowAlgebraInput);
		
		menuViewInput = new JMenu(app.getMenu("InputField") + " ...");
		menuViewInput.setIcon(app.getEmptyIcon());
		cbShowCmdList = new JCheckBoxMenuItem(showCmdListAction);		
		menuViewInput.add(cbShowCmdList);
		cbShowInputTop = new JCheckBoxMenuItem(showInputTopAction);
		menuViewInput.add(cbShowInputTop);
		menu.add(menuViewInput);

		// Construction Protocol
		cbShowConsProtNavigation = new JCheckBoxMenuItem(
				showConsProtNavigationAction);
		cbShowConsProtNavigationPlay = new JCheckBoxMenuItem(
				showConsProtNavigationPlayAction);
		cbShowConsProtNavigationOpenProt = new JCheckBoxMenuItem(
				showConsProtNavigationOpenProtAction);
		
		menu.add(constProtocolAction);
		
		menuViewConsProt = new JMenu(app.getPlain("ConstructionProtocol") + " ...");
		menuViewConsProt.setIcon(app.getImageIcon("table.gif"));
		menuViewConsProt.add(cbShowConsProtNavigation);
		menuViewConsProt.add(cbShowConsProtNavigationPlay);
		menuViewConsProt.add(cbShowConsProtNavigationOpenProt);
		menu.add(menuViewConsProt);
		
		menu.addSeparator();
		
		if(!app.isApplet()) {			
			menuViewPerspectives = new JMenu("Perspectives");
			menuViewPerspectives.setIcon(app.getImageIcon("perspective.gif"));
			menu.add(menuViewPerspectives);
			
			updateMenuPerspective();
			
			menu.addSeparator();
		}
		
		mi = menu.add(refreshAction);
		setMenuShortCutAccelerator(mi, 'F');
		
		mi = menu.add(recomputeAllViews);
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0);
		mi.setAccelerator(ks);
		
		add(menu);

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
		menuDecimalPlaces = new JMenu(app.getMenu("Rounding"));
		menuDecimalPlaces.setIcon(app.getEmptyIcon());
		/*int max_dec = 15;
		String[] strDecimalSpaces = new String[max_dec + 1];
		String[] strDecimalSpacesAC = new String[max_dec + 1];
		for (int i=0; i <= max_dec; i++){ 
			strDecimalSpaces[i] = Integer.toString(i);
			strDecimalSpacesAC[i] = i + " decimals";
		}*/
		String[] strDecimalSpaces = app.getRoundingMenu();

		addRadioButtonMenuItems(menuDecimalPlaces, (ActionListener) this, strDecimalSpaces,
				Application.strDecimalSpacesAC, 0);
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

		// checkboxsize
		// Michael Borcherds 2008-05-12
		menuBooleanSize = new JMenu(app.getMenu("CheckboxSize"));
		menuBooleanSize.setIcon(app.getImageIcon("checkbox16.gif"));
		// dot, circle, cross
		String[] strBooleanSize = { app.getMenu("CheckboxSize.Regular"), app.getMenu("CheckboxSize.Large") };
		String[] strBooleanSizeAC = { "13", "26" };
		ActionListener bsal = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int size = Integer.parseInt(ae.getActionCommand());
				app.getEuclidianView().setBooleanSize(size);
			}
		};
		addRadioButtonMenuItems(menuBooleanSize, bsal, strBooleanSize,
				strBooleanSizeAC, 0);
		menu.add(menuBooleanSize);
		updateMenuBooleanSize();

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
			// perspectives submenu
			submenu = new JMenu(app.getMenu("Perspectives"));
			submenu.setIcon(app.getImageIcon("perspective.gif"));
			
			cbShowViewTitlebar = new JCheckBoxMenuItem(showViewTitlebarAction);
			cbShowViewTitlebar.setSelected(layout.isTitleBarVisible());
			
			cbIgnoreDocumentPerspective = new JCheckBoxMenuItem(ignoreDocumentPerspectiveAction);
			cbIgnoreDocumentPerspective.setSelected(app.isIgnoringDocumentPerspective());
			
			submenu.add(cbShowViewTitlebar);
			submenu.add(cbIgnoreDocumentPerspective);			
			menu.add(submenu);
			
			// Preferences
			menu.addSeparator();
			//submenu = new JMenu(app.getMenu("Settings"));			
			menu.add(savePreferencesAction);
			menu.add(clearPreferencesAction);
			//menu.add(submenu);
		}
		
		// tools menu
		menuTools = new JMenu(app.getMenu("Tools"));
		updateMenuTools();
		add(menuTools);

		if (!app.isApplet()) {
			// windows menu
			menuWindow = new JMenu(app.getMenu("Window"));
			updateMenuWindow();
			add(menuWindow);
						
			if (app.getPluginManager() != null) {			
				javax.swing.JMenu pim=app.getPluginManager().getPluginMenu();
				if(pim!=null){ add(pim);}     // H-P Ulven 2008-04-17
			}
		}

		// help
		menu = new JMenu(app.getMenu("Help"));
		menu.add(helpAction);
		menu.addSeparator();

		menu.add(websiteAction);
		menu.add(forumAction);
		menu.add(wikiAction);

		menu.addSeparator();

		menu.add(infoAction);
		add(menu);
		
		updateMenubar();
	}
	
	/**
	 * Append the menu which contains all tools to the menu bar.
	 * 
	 * @author Florian Sonner
	 * @version 2008-08-13
	 */
	public void updateMenuTools()
	{		
		if(menuTools == null) return;
		
		menuTools.removeAll();

		menuTools.add(toolbarConfigAction);
		menuTools.addSeparator();
		menuTools.add(showCreateToolsAction);
		menuTools.add(showManageToolsAction);
		menuTools.addSeparator();
		
		JMenu[] modeMenus = new JMenu[12];
		modeMenus[0] = new JMenu(app.getMenu("MovementTools"));
		modeMenus[1] = new JMenu(app.getMenu("PointTools"));
		modeMenus[2] = new JMenu(app.getMenu("BasicLineTools"));
		modeMenus[3] = new JMenu(app.getMenu("SpecialLineTools"));
		modeMenus[4] = new JMenu(app.getMenu("PolygonTools"));
		modeMenus[5] = new JMenu(app.getMenu("CircleArcTools"));
		modeMenus[6] = new JMenu(app.getMenu("ConicSectionTools"));
		modeMenus[7] = new JMenu(app.getMenu("MeasurementTools"));
		modeMenus[8] = new JMenu(app.getMenu("TransformationTools"));
		modeMenus[9] = new JMenu(app.getMenu("SpecialObjectTools"));
		modeMenus[10] = new JMenu(app.getMenu("GeneralTools"));
		modeMenus[11] = new JMenu(app.getMenu("CustomTools"));
		
		for(int i = 0; i < modeMenus.length; ++i)
		{
			modeMenus[i].setIcon(app.getEmptyIcon());
			menuTools.add(modeMenus[i]);
		}
		
		MyToolbar toolbar = new MyToolbar(app);
		Vector modes = MyToolbar.createToolBarVec(toolbar.getDefaultToolbarString());
		
		int menuIndex = 0;
		
		for(Iterator iter = modes.iterator(); iter.hasNext();)
		{
			Object next = iter.next();
			if(next instanceof Vector)
			{
				for(Iterator iter2 = ((Vector)next).iterator(); iter2.hasNext();)
				{
					Object next2 = iter2.next();
					
					if(next2 instanceof Integer)
					{
						int mode = ((Integer)next2).intValue();
						
						if(mode < 0)
							modeMenus[menuIndex].addSeparator();
						else
						{
							JMenuItem item = new JMenuItem(app.getModeText(mode));//, app.getModeIcon(mode));
							item.setActionCommand(Integer.toString(mode));
							item.addActionListener(modeChangeAction);
							item.setToolTipText(app.getModeHelp(mode));
							modeMenus[menuIndex].add(item);
						}
					}
					else
					{
						Application.debug("Nested default toolbar not supported");
					}
				}
				
				++menuIndex;
			}
		}
		
		if(modeMenus[modeMenus.length-1].getItemCount() == 0)
			modeMenus[modeMenus.length-1].setEnabled(false);
	}
	
	/**
	 * Update the menu which contains the perspectives.
	 * 
	 * @author Florian Sonner
	 * @version 2008-09-13
	 */
	public void updateMenuPerspective() {
		if(menuViewPerspectives == null) return;

		menuViewPerspectives.removeAll();

		// default perspectives
		Perspective[] defaultPerspectives = Layout.defaultPerspectives;
		
		for(int i = 0; i < defaultPerspectives.length; ++i) {
			JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
			tmpItem.setText(app.getMenu("Perspective."+defaultPerspectives[i].getId()));
			tmpItem.setIcon(app.getEmptyIcon());
			tmpItem.setActionCommand("d"+i);
			menuViewPerspectives.add(tmpItem);
		}
		
		menuViewPerspectives.addSeparator();

		// user perspectives
		Perspective[] perspectives = layout.getPerspectives();
		
		if(perspectives.length != 0) {
			for(int i = 0; i < perspectives.length; ++i) {
				JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
				tmpItem.setText(perspectives[i].getId());
				tmpItem.setIcon(app.getEmptyIcon());
				tmpItem.setActionCommand(Integer.toString(i));
				menuViewPerspectives.add(tmpItem);
			}
			menuViewPerspectives.addSeparator();
		}
		
		menuViewPerspectives.add(managePerspectivesAction);
		menuViewPerspectives.add(savePerspectiveAction);
	}

	protected void initActions() {				
		// Florian Sonner 2008-08-29
		showEuclidianViewAction = new AbstractAction(app.getPlain("DrawingPad")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().setShowEuclidianView(!app.getGuiManager().showEuclidianView());
			}
		};
		
		showAlgebraViewAction = new AbstractAction(app.getPlain("AlgebraWindow")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().setShowAlgebraView(!app.getGuiManager().showAlgebraView());
			}
		};

	    // Michael Borcherds 2008-01-14
		showSpreadsheetAction = new AbstractAction(app.getPlain("Spreadsheet")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().setShowSpreadsheetView(!app.getGuiManager().showSpreadsheetView());
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

		showCmdListAction = new AbstractAction(app.getMenu("CmdList"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowCmdList(!app.showCmdList());
				if (app.getGuiManager().getAlgebraInput() != null)
					SwingUtilities.updateComponentTreeUI(app.getGuiManager().getAlgebraInput());
			}
		};
		
		// Florian Sonner 2008-09-12
		showInputTopAction = new AbstractAction(app.getMenu("InputOnTop"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed(ActionEvent e) {
				app.setShowInputTop(!app.showInputTop());
				if(app.getGuiManager().getAlgebraInput() != null)
					SwingUtilities.updateComponentTreeUI(app.getGuiManager().getAlgebraInput());
			}
		};
		
		// Florian Sonner 2008-09-14
		savePerspectiveAction = new AbstractAction(app.getMenu("SaveCurrentPerspective"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed(ActionEvent e) {
				layout.showSaveDialog();
			}
		};
		
		// Florian Sonner 2008-09-14
		managePerspectivesAction = new AbstractAction(app.getMenu("ManagePerspectives"),
				app.getEmptyIcon()) {
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
				if(e.getActionCommand().startsWith("d")) {
					int index = Integer.parseInt(e.getActionCommand().substring(1));
					layout.applyPerspective(Layout.defaultPerspectives[index]);
				} else {
					int index = Integer.parseInt(e.getActionCommand());
					layout.applyPerspective(layout.getPerspective(index));
				}
			}
		};
		
		// Florian Sonner 2008-10-22
		showViewTitlebarAction = new AbstractAction(app.getMenu("ShowViewTitlebar"),
				app.getEmptyIcon()) {
			public static final long serialVersionUID = 1L;
			
			public void actionPerformed(ActionEvent e) {
				layout.setTitlebarVisible(!layout.isTitleBarVisible());
			}
		};
		
		// Florian Sonner 2008-09-14
		ignoreDocumentPerspectiveAction = new AbstractAction(app.getMenu("IgnoreDocumentPerspective"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed(ActionEvent e) {
				app.setIgnoreDocumentPerspective(!app.isIgnoringDocumentPerspective());
			}
		};

		showAuxiliaryObjectsAction = new AbstractAction(
				app.getPlain("AuxiliaryObjects")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowAuxiliaryObjects(!app.showAuxiliaryObjects());
				app.setUnsaved();				
			}
		};

		showConsProtNavigationAction = new AbstractAction(
				app.getPlain("ConstructionProtocolNavigation"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowConstructionProtocolNavigation(!app.showConsProtNavigation());
				app.setUnsaved();	
				app.updateCenterPanel(true);
				app.updateMenubar();
			}
		};

		showConsProtNavigationPlayAction = new AbstractAction(
				app.getPlain("PlayButton"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				ConstructionProtocolNavigation cpn =
					(ConstructionProtocolNavigation)app.getGuiManager().getConstructionProtocolNavigation();
				cpn.setPlayButtonVisible(!cpn.isPlayButtonVisible());
				cpn.initGUI();
				SwingUtilities.updateComponentTreeUI(cpn);
				app.setUnsaved();
			}
		};

		showConsProtNavigationOpenProtAction = new AbstractAction(
				app.getPlain("ConstructionProtocolButton"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				ConstructionProtocolNavigation cpn =
					(ConstructionProtocolNavigation)app.getGuiManager().getConstructionProtocolNavigation();
				cpn.setConsProtButtonVisible(!cpn.isConsProtButtonVisible());
				cpn.initGUI();
				SwingUtilities.updateComponentTreeUI(cpn);
				app.setUnsaved();
			}
		};

		deleteAll = new AbstractAction(app.getMenu("New"),
				app.getEmptyIcon()) {				
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.deleteAllGeoElements();
			}
		};

		newWindowAction = new AbstractAction(app.getMenu("NewWindow"),
				app.getImageIcon("document-new.png")) {
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

		propertiesAction = new AbstractAction(app.getPlain("Properties") + " ...",
				app.getImageIcon("document-properties.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().showPropertiesDialog();
			}
		};

		constProtocolAction = new AbstractAction(
				app.getPlain("ConstructionProtocol"),
				app.getImageIcon("table.gif")) {
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

		drawingPadPropAction = new AbstractAction(app.getPlain("DrawingPad")
				+ " ...", app.getImageIcon("document-properties.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().showDrawingPadPropertiesDialog();
			}
		};

		toolbarConfigAction = new AbstractAction(app.getMenu("Toolbar.Customize")
				+ " ...", app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().showToolbarConfigDialog();
			}
		};

		saveAction = new AbstractAction(app.getMenu("Save"),
				app.getImageIcon("document-save.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().save();
			}
		};

		saveAsAction = new AbstractAction(app.getMenu("SaveAs") + " ...",
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().saveAs();
			}
		};

		/*
		printProtocolAction = new AbstractAction(
				app.getPlain("ConstructionProtocol") + " ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						ConstructionProtocol constProtocol = app.getConstructionProtocol();
						if (constProtocol == null) {
							constProtocol = new ConstructionProtocol(app);
						}
						constProtocol.initProtocol();
						
						try {
							new PrintPreview(app, constProtocol, PageFormat.PORTRAIT);
                    	} catch (Exception e) {
                    		Application.debug("Print preview not available");
                    	}
					}
				};
				runner.start();
			}
		};
*/
		
		printEuclidianViewAction = new AbstractAction(app.getPlain("DrawingPad")
				+ " ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				showPrintPreview(app);
			}
		};

		exitAction = new AbstractAction(app.getMenu("Close"), app.getImageIcon("exit.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.exit();
			}
		};

		exitAllAction = new AbstractAction(app.getMenu("CloseAll"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.exitAll();
			}
		};

		loadAction = new AbstractAction(app.getMenu("Load") + " ...",
				app.getImageIcon("document-open.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().openFile();
			}
		};

		
		refreshAction = new AbstractAction(app.getMenu("Refresh"),new ImageIcon(app.getRefreshViewImage())) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.refreshViews();
			}
		};

		recomputeAllViews = new AbstractAction(app.getMenu("RecomputeAllViews"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getKernel().updateConstruction();
			}
		};

		drawingPadToClipboardAction = new AbstractAction(
				app.getMenu("DrawingPadToClipboard"),
				app.getImageIcon("edit-copy.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				app.clearSelectedGeos();
            	
				Thread runner = new Thread() {
					public void run() {		
						app.setWaitCursor();
						// copy drawing pad to the system clipboard
						Image img = app.getEuclidianView().getExportImage(1d);
						ImageSelection imgSel = new ImageSelection(img);
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);	
						app.setDefaultCursor();
					}
				};
				runner.start();						    			    								
			}
		};
		
		// Florian Sonner 2008-08-13
		modeChangeAction = new AbstractAction() {
			public static final long serialVersionUID = 1L;
			
			public void actionPerformed(ActionEvent e) {
				app.setMode(Integer.parseInt(e.getActionCommand()));
			}
		};

		/*
		// Michael Borcherds 2008-04-09
		DataFromClipboardAction = new AbstractAction(
				app.getMenu("PasteDataFromClipboard"),
				app.getImageIcon("edit-copy.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
            	          	 				
				Thread runner = new Thread() {
					public void run() {		
						app.setWaitCursor();
						
						String str=app.getStringFromClipboard();
						String lf = "\n"; // doesn't work: System.getProperty("line.separator");
						
						// split input at linefeed
						String strs[] = str.split(lf);
						
						// Remove Whitespace
						strs[0]=strs[0].trim();
						while (strs[0].indexOf("  ")>-1) strs[0]=strs[0].replaceAll("  ", " ");
						
						String sep = null, value;
						
						// check if data is comma tab, space, semicolon or comma (CSV) separated
						if      (strs[0].split("\t").length > 1) sep="\t"; // tab separated
						else if (strs[0].split(" ").length > 1) sep=" "; // space separated
						else if (strs[0].split(";").length > 1) sep=";"; // semicolon separated
						else if (strs[0].split(",").length > 1) sep=","; // comma separated
						
						//Application.debug("sep"+sep);
						
						String list = "{";
						
						if (sep != null) for (int row=0 ; row<strs.length ; row++)
						{
							//Application.debug("-"+strs[i]+"-");
							
							// Remove Whitespace
							strs[row]=strs[row].trim();
							while (strs[row].indexOf("  ")>-1) strs[row]=strs[row].replaceAll("  ", " ");
							
							
							if (!strs[row].equals(""))
							{
								String tempStr[] = strs[row].split(sep);
								
								String coords="(";
								for (int col=0 ; col < tempStr.length ; col++)
								{
									value = tempStr[col].trim();
									if (value.equals("")) value = "0";		
									
									value = value.replace(',', '.'); // decimal comma -> decimal point
									
									try { Double.parseDouble(value); } // check if input is a valid number
									catch (Exception e) { value = "0"; } // set to zero
									
									String command = GeoElement.getSpreadsheetCellName(col,row) + "=" + value;
									kernel.getAlgebraProcessor().processAlgebraCommand(command, false);
									coords += GeoElement.getSpreadsheetCellName(col,row);
									if (col == tempStr.length-1) coords +=")"; else coords +=",";
									//Application.debug("-"+list+"-");
								}
								if (tempStr.length == 2) // 2D coords
								{
									//kernel.getAlgebraProcessor().processAlgebraCommand(coords, false);
									list = list + coords + ",";
								}
									
							}
						}
						
						if (list.lastIndexOf(",") == list.length()-1) // list contains at least one pair of coords
						{
							list = list.substring(0,list.length()-1); 	// remove last comma
							list = list + "}"; 							// close list
							
							kernel.getAlgebraProcessor().processAlgebraCommand(list, false);							
						}
				
					app.setDefaultCursor();
					}
				};
				runner.start();						    			    								
			}
		};*/

		helpAction = new AbstractAction(app.getMenu("Help"),
				app.getImageIcon("help.png")) {
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

		/*
		 * updateAction = new AbstractAction(getMenu("Update"), getEmptyIcon()) {
		 * private static final long serialVersionUID = 1L; public void
		 * actionPerformed(ActionEvent e) { Thread runner = new Thread() {
		 * public void run() { updateGeoGebra(); } }; runner.start(); } };
		 */

		exportGraphicAction = new AbstractAction(app.getPlain("DrawingPadAsPicture")
				+ " ("	+ Application.FILE_EXT_PNG + ", " + Application.FILE_EXT_EPS + ") ...", 
				app.getImageIcon("image-x-generic.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.loadExportJar();
				
				try {
					Thread runner = new Thread() {
						public void run() {
							app.setWaitCursor();
							try {							
						    	app.clearSelectedGeos();
						    	
						    	// use reflection for					    	
					  		    JDialog d = new geogebra.export.GraphicExportDialog(app);   		
					  		    //Class casViewClass = Class.forName("geogebra.export.GraphicExportDialog");
					  		    //Object[] args = new Object[] { app };
					  		    //Class [] types = new Class[] {Application.class};
					  	        //Constructor constructor = casViewClass.getDeclaredConstructor(types);   	        
					  	        //JDialog d =  (JDialog) constructor.newInstance(args);  					    
						      
						        d.setVisible(true);
						       
							} catch (Exception e) {
								Application.debug("GraphicExportDialog not available");
							}
							app.setDefaultCursor();
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
		
		
		exportPSTricksAction = new AbstractAction(app.getPlain("DrawingPadAsPSTricks") + " ...", 
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {				
				try {				
					app.loadExportJar();
		  	        new geogebra.export.pstricks.GeoGebraToPstricks(app);	
				} catch (Exception ex) {
					Application.debug("GeoGebraToPstricks not available");
				} catch (java.lang.NoClassDefFoundError ee) {
				app.showErrorDialog(app.getError("ExportJarMissing"));
				ee.printStackTrace();
				}
			}
		};
		// Added By Loïc Le Coq
		exportPgfAction = new AbstractAction(app.getPlain("DrawingPagAsPGF") + " ...", 
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {				
				try {	
					app.loadExportJar();
		  	        new geogebra.export.pstricks.GeoGebraToPgf(app);	
				} catch (Exception ex) {
					Application.debug("GeoGebraToPGF not available");
				} catch (java.lang.NoClassDefFoundError ee) {
					app.showErrorDialog(app.getError("ExportJarMissing"));
					ee.printStackTrace();
				}
			}
		};			
		
		//End
		
		exportWorksheet = new AbstractAction(app.getPlain("DynamicWorksheetAsWebpage") + " ("
				+ Application.FILE_EXT_HTML + ") ...",
				app.getImageIcon("text-html.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.loadExportJar();
				
				try {
				
					Thread runner = new Thread() {
						public void run() {
							app.setWaitCursor();
							try {
								app.clearSelectedGeos();
								geogebra.export.WorksheetExportDialog d = new geogebra.export.WorksheetExportDialog(app); 		
															
								d.setVisible(true);
							} catch (Exception e) {
								Application.debug("WorksheetExportDialog not available");
								e.printStackTrace();
							}
							app.setDefaultCursor();
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

		showCreateToolsAction = new AbstractAction(app.getMenu("Tool.CreateNew")
				+ " ...", app.getImageIcon("tool.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				ToolCreationDialog tcd = new ToolCreationDialog(
						app);
				tcd.setVisible(true);
			}
		};

		showManageToolsAction = new AbstractAction(app.getMenu("Tool.Manage")
				+ " ...", app.getImageIcon("document-properties.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				ToolManagerDialog tmd = new ToolManagerDialog(app);
				tmd.setVisible(true);
			}
		};

		infoAction = new AbstractAction(app.getMenu("About") + " / "
				+ app.getMenu("License"), app.getImageIcon("info.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				showAboutDialog(app);
			}
		};			
		
		
		savePreferencesAction = new AbstractAction(app.getMenu("Settings.Save"),
				app.getImageIcon("document-save.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				GeoGebraPreferences.getPref().saveXMLPreferences(app);				
			}
		};
		
		clearPreferencesAction = new AbstractAction(app.getMenu("Settings.ResetDefault"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				GeoGebraPreferences.getPref().clearPreferences();				
			}
		};
		
		selectAllAction = new AbstractAction(app.getMenu("SelectAll"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				app.selectAll(-1); // Michael Borcherds 2008-03-03 pass "-1" to select all
			}
		};			
		
		selectCurrentLayerAction = new AbstractAction(app.getMenu("SelectCurrentLayer"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				
				int layer = getSelectedLayer();
				if (layer !=-1) app.selectAll(layer); // select all objects in layer
				
			}
		};			
		
		selectAllAncestorsAction = new AbstractAction(app.getMenu("SelectAncestors"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			

				
				app.selectAllPredecessors();
			}
		};			
		
		selectAllDescendantsAction = new AbstractAction(app.getMenu("SelectDescendants"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				
				app.selectAllDescendants();
			}
		};			
		
		deleteAction = new AbstractAction(app.getPlain("Delete"), 
				app.getImageIcon("delete_small.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {	
				app.deleteSelectedObjects();
			}
		};		
		
		websiteAction  = new AbstractAction("www.geogebra.org", 
							new ImageIcon(app.getInternalImage("geogebra.gif"))) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				app.getGuiManager().showURLinBrowser(Application.GEOGEBRA_WEBSITE);
			}
		};		
		
		forumAction  = new AbstractAction("GeoGebra Forum", 
				new ImageIcon(app.getInternalImage("users.png"))) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				BrowserLauncher.openURL(Application.GEOGEBRA_WEBSITE + "forum/");
			}
		};	
		
		wikiAction  = new AbstractAction("GeoGebraWiki", 
				new ImageIcon(app.getInternalImage("wiki.png"))) {
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
				
		updateActions();
	}
	/* Michael Borcherds 2008-03-03
	 * return -1 if nothing selected
	 * return -2 if objects from more than one layer selected
	 * return layer number if objects from exactly one layer are selected
	 */	
	protected int getSelectedLayer() { 
		Object [] geos = app.getSelectedGeos().toArray();
		if (geos.length == 0) return -1; // return -1 if nothing selected
		
		int layer = ((GeoElement)geos[0]).getLayer();
		
		for (int i=1; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (geo.getLayer() != layer) return -2; // return -2 if more than one layer selected
		}
		return layer;
	}
	
	public static void showPrintPreview(final Application  app) {
		app.loadExportJar();
		
		try {
			Thread runner = new Thread() {
				public void run() {			
					app.setWaitCursor();
					
					try {
						// use reflection for
			  		    // new geogebra.export.PrintPreview(app, app.getEuclidianView(), PageFormat.LANDSCAPE);		
			  		    //Class classObject = Class.forName("geogebra.export.PrintPreview");
			  		    //Object[] args = new Object[] { app , app.getEuclidianView(), new Integer(PageFormat.LANDSCAPE)};
			  		    //Class [] types = new Class[] {Application.class, Printable.class, int.class};
			  	        //Constructor constructor = classObject.getDeclaredConstructor(types);   	        
			  	        //constructor.newInstance(args); 				
			  		    new geogebra.export.PrintPreview(app, app.getEuclidianView(), PageFormat.LANDSCAPE);							
	            	} catch (Exception e) {
	            		Application.debug("Print preview not available");            		
	            	}	
	            	
	            	app.setDefaultCursor();
				}
			};
			runner.start();
		} catch (java.lang.NoClassDefFoundError ee) {
		app.showErrorDialog(app.getError("ExportJarMissing"));
		ee.printStackTrace();
		}
	}
	
	public static void showAboutDialog(final Application app) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><b>");
		sb.append(app.getPlain("ApplicationName"));
		sb.append(" ");
		sb.append(GeoGebra.VERSION_STRING +" (Java "+System.getProperty("java.version")+")"); // Michael Borcherds 2008-03-21 added java version
		sb.append("</b><br>");
		sb.append(GeoGebra.BUILD_DATE);

		// license
		String text = app.loadTextFile(Application.LICENSE_FILE);
		JTextArea textArea = new JTextArea(21, 45);
		JScrollPane scrollPane = new JScrollPane(textArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textArea.setEditable(false);
		textArea.setText(text);
		textArea.setCaretPosition(0);

		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(new JLabel(sb.toString()), BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.SOUTH);

		JOptionPane infoPane = new JOptionPane(panel,
				JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);

		final JDialog dialog = infoPane.createDialog(app.getMainComponent(),
				app.getMenu("About") + " / " + app.getMenu("License"));

		final ActionListener listener = new ActionListener() {
			public final void actionPerformed(final ActionEvent e) {
				JOptionPane.showMessageDialog(dialog, null,
						"GeoZebra forever", JOptionPane.DEFAULT_OPTION,
						app.getImageIcon("zebra.gif"));
			}
		};

		final KeyStroke keyStroke = KeyStroke.getKeyStroke(
				KeyEvent.VK_Z, 0, true);
		dialog.getRootPane().registerKeyboardAction(listener,
				keyStroke,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		dialog.setVisible(true);
	}
	
	public void updateSelection() {		
		// Michael Borcherds 2008-03-03 BEGIN
		//boolean haveSelection = !app.getSelectedGeos().isEmpty();
		//deleteAction.setEnabled(haveSelection);
		int layer = getSelectedLayer();
		deleteAction.setEnabled(layer != -1); // -1 means nothing selected, -2 means different layers selected
		selectCurrentLayerAction.setEnabled(getSelectedLayer() >= 0); // exactly one layer selected
		// Michael Borcherds 2008-03-03 END
		boolean haveSelection = !app.getSelectedGeos().isEmpty();
		selectAllDescendantsAction.setEnabled(haveSelection);
		selectAllAncestorsAction.setEnabled(haveSelection);
	}

	protected void updateActions() {		
		propertiesAction.setEnabled(!kernel.isEmpty());
		selectAllAction.setEnabled(!kernel.isEmpty());
	}

	protected void setMenuShortCutAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		mi.setAccelerator(ks);
	}
	
	protected void setMenuShortCutShiftAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() + Event.SHIFT_MASK);
		mi.setAccelerator(ks);
	}
	
	
	public void updateMenuWindow() {	
		if (menuWindow == null) return;
		
		menuWindow.removeAll();
		JMenuItem mit = menuWindow.add(newWindowAction);
		setMenuShortCutAccelerator(mit, 'N');

		ArrayList ggbInstances = GeoGebraFrame.getInstances();
		int size = ggbInstances.size();
		if (size == 1)
			return;

		menuWindow.addSeparator();
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
			menuWindow.add(mi);
		}
	}

	protected void updateMenuAngleUnit() {
		int pos;
		if (kernel.getAngleUnit() == Kernel.ANGLE_DEGREE)
			pos = 0;
		else
			pos = 1;
		if (menuAngleUnit != null) {
		((JRadioButtonMenuItem) menuAngleUnit.getMenuComponent(pos))
				.setSelected(true);
		}
	}

	protected void updateMenuPointStyle() {
		if (menuPointStyle == null) return;
		
		int pos = app.getEuclidianView().getPointStyle();
		if (pos == 1) pos=2; else if (pos == 2) pos=1; // bugfix swap 2 and 1 Michael Borcherds 2008-03-13
		((JRadioButtonMenuItem) menuPointStyle.getMenuComponent(pos))
				.setSelected(true);
	}

	protected void updateMenuBooleanSize() {
		if (menuBooleanSize == null) return;
		
		int size = app.getEuclidianView().getBooleanSize();
		int pos = (size == 13) ? 0 : 1; // only 13 and 26 allowed
		((JRadioButtonMenuItem) menuBooleanSize.getMenuComponent(pos))
				.setSelected(true);
	}

	// added by Lo�c BEGIN
	protected void updateMenuRightAngleStyle() {
		if (menuRightAngleStyle == null) return;
		
		int pos = app.getEuclidianView().getRightAngleStyle();
		((JRadioButtonMenuItem) menuRightAngleStyle.getMenuComponent(pos))
				.setSelected(true);
	}

	// END

	protected void updateMenuCoordStyle() {
		if (menuCoordStyle == null) return;
		
		int pos = kernel.getCoordStyle();
		((JRadioButtonMenuItem) menuCoordStyle.getMenuComponent(pos))
				.setSelected(true);
	}
	
	protected void updateMenuLabeling() {
		if (menuLabeling == null) return;
		
		int pos = app.getLabelingStyle();
		((JRadioButtonMenuItem) menuLabeling.getMenuComponent(pos))
				.setSelected(true);
	}
	
	protected void updateMenuPointCapturing() {
		if (menuPointCapturing == null) return;
		
		String pos = Integer.toString(app.getEuclidianView().getPointCapturingMode());
		for (int i = 0; i < 4; i++) {
			JRadioButtonMenuItem mi = (JRadioButtonMenuItem) menuPointCapturing
					.getMenuComponent(i);
			String ac = mi.getActionCommand();
			if (ac.substring(0, 1).equals(pos)) {
				mi.setSelected(true);
				break;
			}
		}
	}

	protected void updateMenuDecimalPlaces() {
		if (menuDecimalPlaces == null) return;
		int pos = -1;
		
		if (kernel.useSignificantFigures) {
			int figures = kernel.getPrintFigures();
			if (figures > 0 && figures < Application.figuresLookup.length)
				pos = Application.figuresLookup[figures];
		}
		else
		{
			int decimals = kernel.getPrintDecimals();
			
			if (decimals > 0 && decimals < Application.decimalsLookup.length)
				pos = Application.decimalsLookup[decimals];
			
		}

		try {
			((JRadioButtonMenuItem) menuDecimalPlaces.getMenuComponent(pos))
					.setSelected(true);
		} catch (Exception e) {
		}

	}

	protected void updateMenuContinuity() {
		int pos = kernel.isContinuous() ? 0 : 1;
		try {
			((JRadioButtonMenuItem) menuContinuity.getMenuComponent(pos))
					.setSelected(true);
		} catch (Exception e) {
		}
	}

	protected void addLanguageMenuItems(JMenu menu, ActionListener al) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;
		String ggbLangCode;

		JMenu submenu1 = new JMenu("A - G");
		JMenu submenu2 = new JMenu("H - Z");
		menu.add(submenu1);
		menu.add(submenu2);
		
		for (int i = 0; i < Application.supportedLocales.size(); i++) {						
			Locale loc = (Locale)  Application.supportedLocales.get(i);
			ggbLangCode = loc.getLanguage() + loc.getCountry()
					+ loc.getVariant();

			// enforce to show specialLanguageNames first
			// because here getDisplayLanguage doesn't return a good result
			String text = (String)  Application.specialLanguageNames.get(ggbLangCode);
			if (text == null)
				text = loc.getDisplayLanguage(Locale.ENGLISH);
			mi = new JRadioButtonMenuItem(text);

			if (loc == app.getLocale())
				mi.setSelected(true);
			mi.setActionCommand(ggbLangCode);
			mi.addActionListener(al);
			bg.add(mi);
			
			if (text.charAt(0) <= 'G')
				submenu1.add(mi);
			else
				submenu2.add(mi);
		}
	}

	protected void addRadioButtonMenuItems(JMenu menu, ActionListener al,
			String[] items, String[] actionCommands, int selectedPos) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;

		for (int i = 0; i < items.length; i++) {
			if (items[i] == "---") {
				menu.addSeparator();
			}
			else
			{
				mi = new JRadioButtonMenuItem(app.getMenu(items[i]));
				if (i == selectedPos)
					mi.setSelected(true);
				mi.setActionCommand(actionCommands[i]);
				mi.addActionListener(al);
				bg.add(mi);
				menu.add(mi);
			}
		}
	}
	
	 // handle language changes
    protected class LanguageActionListener implements ActionListener {
    	
    	public LanguageActionListener() {}
    	
        public void actionPerformed(ActionEvent e) {
        	app.setLanguage(Application.getLocale(e.getActionCommand()));        	
        	GeoGebraPreferences.getPref().saveDefaultLocale(app.getLocale());
        }
    }  
}
