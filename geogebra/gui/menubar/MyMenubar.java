package geogebra.gui.menubar;

import geogebra.Application;
import geogebra.ConstructionProtocolNavigation;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.GeoGebra;
import geogebra.gui.GeoGebraPreferences;
import geogebra.gui.ToolCreationDialog;
import geogebra.gui.ToolManagerDialog;
import geogebra.kernel.Kernel;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.AbstractAction;
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

public class MyMenubar extends JMenuBar implements ActionListener {

	// Actions
	private AbstractAction refreshAction,
			drawingPadToClipboardAction, newFileAction, newWindowAction,
			propertiesAction, constProtocolAction, drawingPadPropAction,
			toolbarConfigAction, showAlgebraViewAction, showAlgebraInputAction,
			showCmdListAction, horizontalSplitAction,
			showAuxiliaryObjectsAction, showConsProtNavigationAction,
			showConsProtNavigationPlayAction,
			showConsProtNavigationOpenProtAction, loadAction, saveAction,
			saveAsAction, //printProtocolAction, 
			printEuclidianViewAction,
			exitAction, exitAllAction, helpAction,
			// updateAction,
			infoAction, exportGraphicAction, exportWorksheet,
			showCreateToolsAction, showManageToolsAction,
			savePreferencesAction, clearPreferencesAction;

	private JCheckBoxMenuItem cbShowAxes, cbShowGrid, cbShowAlgebraView,
			cbShowAuxiliaryObjects, cbHorizontalSplit,
			cbShowConsProtNavigation, cbShowConsProtNavigationPlay,
			cbShowConsProtNavigationOpenProt, cbShowAlgebraInput,
			cbShowCmdList;

	private JMenu menuAngleUnit, menuPointCapturing, menuDecimalPlaces,
			menuContinuity, menuPointStyle, menuRightAngleStyle,
			menuCoordStyle, menuLabeling, menuWindow, menuFile, menuTools;

	//private JMenuItem miCloseAll;

	private Application app;

	private Kernel kernel;

	public MyMenubar(Application app) {
		this.app = app;
		kernel = app.getKernel();
	}
	
	public void updateMenubar() {	
		EuclidianView ev = app.getEuclidianView();
		cbShowAxes.setSelected(ev.getShowXaxis() && ev.getShowYaxis());
	    cbShowGrid.setSelected(ev.getShowGrid());
	    
	    cbShowAlgebraView.setSelected(app.showAlgebraView());
        cbShowAlgebraInput.setSelected(app.showAlgebraInput());
        cbShowAuxiliaryObjects.setSelected(app.showAuxiliaryObjects());

		boolean showAlgebraView = app.showAlgebraView();
		cbShowAlgebraView.setSelected(showAlgebraView);
		cbShowAuxiliaryObjects.setVisible(showAlgebraView);
		cbShowAuxiliaryObjects.setSelected(app.showAuxiliaryObjects());

		cbHorizontalSplit.setVisible(showAlgebraView);
		cbHorizontalSplit.setSelected(app.isHorizontalSplit());
		
		cbShowAlgebraInput.setSelected(app.showAlgebraInput());
		cbShowCmdList.setSelected(app.showCmdList());
		cbShowCmdList.setVisible(app.showAlgebraInput());		
			
		
		cbShowConsProtNavigation.setSelected(app.showConsProtNavigation());				
		cbShowConsProtNavigationPlay
				.setSelected(app.isConsProtNavigationPlayButtonVisible());
		cbShowConsProtNavigationOpenProt
				.setSelected(app.isConsProtNavigationProtButtonVisible());
		cbShowConsProtNavigationPlay.setVisible(app.showConsProtNavigation());
		cbShowConsProtNavigationOpenProt.setVisible(app.showConsProtNavigation());	
	     	           	
        updateMenuContinuity();
        updateMenuPointCapturing();
        updateMenuAngleUnit();
        updateMenuDecimalPlaces();
        updateMenuPointStyle();
        updateMenuRightAngleStyle();
        updateMenuCoordStyle();	
        updateMenuLabeling();        
        
        updateActions();
	}

	public void updateMenuFile() {
		if (menuFile == null) return;
		
		menuFile.removeAll();
		
		JMenu menu = menuFile;
		JMenuItem mi = menu.add(newFileAction);
		setCtrlAccelerator(mi, 'N');
		if (!app.isApplet()) {
			mi = new JMenuItem(newWindowAction);
			mi.setIcon(app.getEmptyIcon());
			setCtrlShiftAccelerator(mi, 'N');
			menu.add(mi);
			menu.addSeparator();
		}
		
		mi = menu.add(loadAction);
		setCtrlAccelerator(mi, 'O'); // open
		menu.addSeparator();
		mi = menu.add(saveAction);
		setCtrlAccelerator(mi, 'S');
		mi = menu.add(saveAsAction);	
		setCtrlShiftAccelerator(mi, 'S');
		menu.addSeparator();
//		submenu = new JMenu(app.getMenu("PrintPreview"));
//		submenu.setIcon(app.getImageIcon("print.gif"));
//		submenu.add(printEuclidianViewAction);
//	//	submenu.add(printProtocolAction);
//		menu.add(submenu);
		mi = menu.add(printEuclidianViewAction);
		mi.setText(app.getMenu("PrintPreview"));
		mi.setIcon(app.getImageIcon("print.gif"));
		setCtrlAccelerator(mi, 'P');
				
		// export
		JMenu submenu = new JMenu(app.getMenu("Export"));
		submenu.setIcon(app.getEmptyIcon());
		menu.add(submenu);
		mi = submenu.add(exportWorksheet);
		setCtrlShiftAccelerator(mi, 'W');
		
		submenu.addSeparator();
		//submenu.add(htmlCPAction);
		mi = submenu.add(exportGraphicAction);
		setCtrlShiftAccelerator(mi, 'P');
		mi = submenu.add(drawingPadToClipboardAction);
		setCtrlShiftAccelerator(mi, 'C');
		
		
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
					mi.setIcon(app.getEmptyIcon());
					//mi.setIcon( new ImageIcon(app.getInternalImage("geogebra22.gif")));
					ActionListener al = new LoadFileListener(app, file);
					mi.addActionListener(al);
					menu.add(mi);
				}
			}						
		}
		
		// close
		menu.addSeparator();
		menu.add(exitAction);
					
		// close all		
		if (GeoGebra.getInstanceCount() > 1) {								
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
			mi = menu.add(app.getUndoAction());
			setCtrlAccelerator(mi, 'Z');
			mi = menu.add(app.getRedoAction());
			setCtrlAccelerator(mi, 'Y');
			menu.addSeparator();
		}

		menu.add(propertiesAction);
		add(menu);

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
		cbShowAlgebraView.setSelected(app.showAlgebraView());
		setCtrlAccelerator(cbShowAlgebraView, 'A');
		menu.add(cbShowAlgebraView);

		cbShowAuxiliaryObjects = new JCheckBoxMenuItem(
				showAuxiliaryObjectsAction);
		cbShowAuxiliaryObjects.setSelected(app.getAlgebraView() == null
				|| app.getAlgebraView().showAuxiliaryObjects());
		cbShowAuxiliaryObjects.setEnabled(app.showAlgebraView());
		menu.add(cbShowAuxiliaryObjects);

		cbHorizontalSplit = new JCheckBoxMenuItem(horizontalSplitAction);				
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
		setCtrlAccelerator(mi, 'F');
		
		
		add(menu);

		// Options
		menu = new JMenu(app.getMenu("Options"));

		// point capturing
		menuPointCapturing = new JMenu(app.getMenu("PointCapturing"));
		String[] strPointCapturing = { "on",
				app.getMenu("on") + " (" + app.getMenu("Grid") + ")", "off" };
		String[] strPointCapturingAC = { "1 PointCapturing",
				"2 PointCapturing", "0 PointCapturing" };
		addRadioButtonMenuItems(menuPointCapturing, this, strPointCapturing,
				strPointCapturingAC, 0);
		menu.add(menuPointCapturing);
		updateMenuPointCapturing();

		// Angle unit
		menuAngleUnit = new JMenu(app.getMenu("AngleUnit"));
		String[] strAngleUnit = { "Degree", "Radiant" };
		addRadioButtonMenuItems(menuAngleUnit, this, strAngleUnit, strAngleUnit,
				0);
		menu.add(menuAngleUnit);
		updateMenuAngleUnit();

		// decimal places
		menuDecimalPlaces = new JMenu(app.getMenu("DecimalPlaces"));
		String[] strDecimalSpaces = { "0", "1", "2", "3", "4", "5" };
		String[] strDecimalSpacesAC = { "0 decimals", "1 decimals",
				"2 decimals", "3 decimals", "4 decimals", "5 decimals" };
		addRadioButtonMenuItems(menuDecimalPlaces, this, strDecimalSpaces,
				strDecimalSpacesAC, 0);
		menu.add(menuDecimalPlaces);
		updateMenuDecimalPlaces();

		// continuity
		menuContinuity = new JMenu(app.getMenu("Continuity"));
		String[] strContinuity = { "on", "off" };
		String[] strContinuityAC = { "true Continuity", "false Continuity" };
		addRadioButtonMenuItems(menuContinuity, this, strContinuity,
				strContinuityAC, 0);
		menu.add(menuContinuity);
		updateMenuContinuity();

		menu.addSeparator();

		// point style
		menuPointStyle = new JMenu(app.getMenu("PointStyle"));
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

		// added by Loïc BEGIN
		// right angle style

		menuRightAngleStyle = new JMenu(app.getMenu("RightAngleStyle"));
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
		String[] lstr = { "Labeling.automatic", "Labeling.on", "Labeling.off", "Labeling.pointsOnly"  };		
		String[] lastr = { "0_labeling", "1_labeling", "2_labeling", "3_labeling"  };
		addRadioButtonMenuItems(menuLabeling, this, lstr, lastr, 0);
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

		// Font size
		submenu = new JMenu(app.getMenu("FontSize"));
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

		addRadioButtonMenuItems(submenu, this, fsfi, fsfi, pos);
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
		LanguageActionListener langListener = new LanguageActionListener();
		submenu = new JMenu(app.getMenu("Language"));
		addLanguageMenuItems(submenu, langListener);
		menu.add(submenu);

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
		
		
		// tools menu		
		menuTools = new JMenu(app.getMenu("Tools"));
		add(menuTools);
		menuTools.add(showCreateToolsAction);
		menuTools.add(showManageToolsAction);
		menuTools.addSeparator();
		menuTools.add(toolbarConfigAction);

		if (!app.isApplet()) {
			// windows menu
			menuWindow = new JMenu(app.getMenu("Window"));
			updateMenuWindow();
			add(menuWindow);
		}

		// help
		menu = new JMenu(app.getMenu("Help"));
		menu.add(helpAction);
		menu.addSeparator();

		// menu.add(updateAction);
		// menu.addSeparator();

		menu.add(infoAction);
		add(menu);
		
		updateMenubar();
	}

	/***************************************************************************
	 * ActionListener
	 **************************************************************************/

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

	private void initActions() {				
		showAlgebraViewAction = new AbstractAction(app.getPlain("AlgebraWindow")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowAlgebraView(!app.showAlgebraView());
				app.updateCenterPanel(true);
			}
		};

		showAlgebraInputAction = new AbstractAction(app.getMenu("InputField")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowAlgebraInput(!app.showAlgebraInput());
				app.updateContentPane();
			}
		};

		showCmdListAction = new AbstractAction(app.getMenu("CmdList")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowCmdList(!app.showCmdList());
				if (app.getAlgebraInput() != null)
					SwingUtilities.updateComponentTreeUI(app.getAlgebraInput());
			}
		};

		horizontalSplitAction = new AbstractAction(app.getPlain("HorizontalSplit")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setHorizontalSplit(!app.isHorizontalSplit());
				app.updateCenterPanel(true);
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
				app.getPlain("ConstructionProtocolNavigation")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowConstructionProtocolNavigation(!app.showConsProtNavigation());
				app.setUnsaved();	
				app.updateCenterPanel(true);
			}
		};

		showConsProtNavigationPlayAction = new AbstractAction(
				app.getPlain("PlayButton")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				ConstructionProtocolNavigation cpn =
					app.getConstructionProtocolNavigation();
				cpn.setPlayButtonVisible(!cpn.isPlayButtonVisible());
				cpn.initGUI();
				SwingUtilities.updateComponentTreeUI(cpn);
				app.setUnsaved();
			}
		};

		showConsProtNavigationOpenProtAction = new AbstractAction(
				app.getPlain("ConstructionProtocolButton")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				ConstructionProtocolNavigation cpn =
					app.getConstructionProtocolNavigation();
				cpn.setConsProtButtonVisible(!cpn.isConsProtButtonVisible());
				cpn.initGUI();
				SwingUtilities.updateComponentTreeUI(cpn);
				app.setUnsaved();
			}
		};

		newFileAction = new AbstractAction(app.getMenu("New"),
				app.getImageIcon("new.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.newFile();
			}
		};

		newWindowAction = new AbstractAction(app.getMenu("NewWindow") + " ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
		    		public void run() {   
		    			app.setWaitCursor();
		    			GeoGebra wnd = GeoGebra.createNewWindow(null);    			    	    		    	    	
		    			wnd.setVisible(true);
		    			app.setDefaultCursor();
		    		}
		    	};
		    	runner.start();				
			}
		};

		propertiesAction = new AbstractAction(app.getPlain("Properties") + " ...",
				app.getImageIcon("properties.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.showPropertiesDialog();
			}
		};

		constProtocolAction = new AbstractAction(
				app.getPlain("ConstructionProtocol") + " ...",
				app.getImageIcon("properties.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						app.showConstructionProtocol();
					}
				};
				runner.start();
			}
		};

		drawingPadPropAction = new AbstractAction(app.getPlain("DrawingPad")
				+ " ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.showDrawingPadPropertiesDialog();
			}
		};

		toolbarConfigAction = new AbstractAction(app.getMenu("Toolbar.Customize")
				+ " ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.showToolbarConfigDialog();
			}
		};

		saveAction = new AbstractAction(app.getMenu("Save"),
				app.getImageIcon("save.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.save();
			}
		};

		saveAsAction = new AbstractAction(app.getMenu("SaveAs") + " ...",
				app.getImageIcon("saveas.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.saveAs();
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
                    		System.err.println("Print preview not available");
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

		exitAction = new AbstractAction(app.getMenu("Close"), app.getEmptyIcon()) {
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
				app.getImageIcon("open.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.openFile();
			}
		};

		

		refreshAction = new AbstractAction(app.getMenu("Refresh")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.refreshViews();
			}
		};

		drawingPadToClipboardAction = new AbstractAction(
				app.getMenu("DrawingPadToClipboard")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						// copy drawing pad to the system clipboard
						Toolkit tools = Toolkit.getDefaultToolkit();
						Clipboard clip = tools.getSystemClipboard();
						app.clearSelectedGeos();
						clip.setContents(app.getEuclidianView(), app.getEuclidianView());
					}
				};
				runner.start();
			}
		};

		helpAction = new AbstractAction(app.getMenu("Help"),
				app.getImageIcon("help.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						app.openHelp();
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

		exportGraphicAction = new AbstractAction(app.getPlain("DrawingPad") + " "
				+ app.getPlain("as") + " " + app.getPlain("Picture") + " ("
				+ Application.FILE_EXT_PNG + ", " + Application.FILE_EXT_EPS + ") ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						try {
					    	app.clearSelectedGeos();
					        geogebra.export.GraphicExportDialog d = new geogebra.export.GraphicExportDialog(app);
					        d.setVisible(true);
						} catch (Exception e) {
							System.err.println("GraphicExportDialog not available");
						}
					}
				};
				runner.start();
			}
		};

		/*
		htmlCPAction = new AbstractAction(app.getPlain("ConstructionProtocol")
				+ " " + app.getPlain("as") + " " + app.getPlain("html") + " ("
				+ Application.FILE_EXT_HTML + ") ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						app.exportConstructionProtocolHTML();
					}
				};
				runner.start();
			}
		};*/

		exportWorksheet = new AbstractAction(app.getPlain("DynamicWorksheet") + " "
				+ app.getPlain("as") + " " + app.getPlain("html") + " ("
				+ Application.FILE_EXT_HTML + ") ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						try {
							app.clearSelectedGeos();
							geogebra.export.WorksheetExportDialog d = new geogebra.export.WorksheetExportDialog(app);
							d.setVisible(true);
						} catch (Exception e) {
							System.err.println("WorksheetExportDialog not available");
						}
					}
				};
				runner.start();
			}
		};

		showCreateToolsAction = new AbstractAction(app.getMenu("Tool.CreateNew")
				+ " ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				ToolCreationDialog tcd = new ToolCreationDialog(
						app);
				tcd.setVisible(true);
			}
		};

		showManageToolsAction = new AbstractAction(app.getMenu("Tool.Manage")
				+ " ...") {
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
				app.getImageIcon("save.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				GeoGebraPreferences.saveXMLPreferences(app);				
			}
		};
		
		clearPreferencesAction = new AbstractAction(app.getMenu("Settings.ResetDefault")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.clearPreferences();				
			}
		};
				
		updateActions();
	}
	
	public static void showPrintPreview(final Application  app) {
		Thread runner = new Thread() {
			public void run() {						
				try {
					new geogebra.export.PrintPreview(app, app.getEuclidianView(),
							PageFormat.LANDSCAPE);
            	} catch (Exception e) {
            		System.err.println("Print preview not available");
            	}												
			}
		};
		runner.start();
	}
	
	public static void showAboutDialog(final Application app) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><b>");
		sb.append(app.getPlain("ApplicationName"));
		sb.append(" ");
		sb.append(Application.versionString);
		sb.append("</b><br>");
		sb.append(Application.buildDate);

		// license
		String text = readTextFromJar("license_message.txt");
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

	private void updateActions() {		
		propertiesAction.setEnabled(!kernel.isEmpty());
	}

	private void setCtrlAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc, Event.CTRL_MASK);
		mi.setAccelerator(ks);
	}
	
	private void setCtrlShiftAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc, Event.CTRL_MASK + Event.SHIFT_MASK);
		mi.setAccelerator(ks);
	}
	
	public void updateMenuWindow() {	
		if (menuWindow == null) return;
		
		menuWindow.removeAll();
		JMenuItem mit = menuWindow.add(newWindowAction);
		setCtrlShiftAccelerator(mit, 'N');

		ArrayList ggbInstances = GeoGebra.getInstances();
		int size = ggbInstances.size();
		if (size == 1)
			return;

		menuWindow.addSeparator();
		StringBuffer sb = new StringBuffer();
		ButtonGroup bg = new ButtonGroup();
		JRadioButtonMenuItem mi;
		for (int i = 0; i < size; i++) {
			GeoGebra ggb = (GeoGebra) ggbInstances.get(i);
			Application application = ggb.getApplication();

			sb.setLength(0);
			sb.append(i + 1);
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

	private void updateMenuAngleUnit() {
		int pos;
		if (kernel.getAngleUnit() == Kernel.ANGLE_DEGREE)
			pos = 0;
		else
			pos = 1;
		((JRadioButtonMenuItem) menuAngleUnit.getMenuComponent(pos))
				.setSelected(true);
	}

	private void updateMenuPointStyle() {
		if (menuPointStyle == null) return;
		
		int pos = app.getEuclidianView().getPointStyle();
		((JRadioButtonMenuItem) menuPointStyle.getMenuComponent(pos))
				.setSelected(true);
	}

	// added by Loïc BEGIN
	private void updateMenuRightAngleStyle() {
		if (menuRightAngleStyle == null) return;
		
		int pos = app.getEuclidianView().getRightAngleStyle();
		((JRadioButtonMenuItem) menuRightAngleStyle.getMenuComponent(pos))
				.setSelected(true);
	}

	// END

	private void updateMenuCoordStyle() {
		if (menuCoordStyle == null) return;
		
		int pos = kernel.getCoordStyle();
		((JRadioButtonMenuItem) menuCoordStyle.getMenuComponent(pos))
				.setSelected(true);
	}
	
	private void updateMenuLabeling() {
		if (menuLabeling == null) return;
		
		int pos = app.getLabelingStyle();
		((JRadioButtonMenuItem) menuLabeling.getMenuComponent(pos))
				.setSelected(true);
	}
	
	private void updateMenuPointCapturing() {
		if (menuPointCapturing == null) return;
		
		String pos = Integer.toString(app.getEuclidianView().getPointCapturingMode());
		for (int i = 0; i < 3; i++) {
			JRadioButtonMenuItem mi = (JRadioButtonMenuItem) menuPointCapturing
					.getMenuComponent(i);
			String ac = mi.getActionCommand();
			if (ac.substring(0, 1).equals(pos)) {
				mi.setSelected(true);
				break;
			}
		}
	}

	private void updateMenuDecimalPlaces() {
		if (menuDecimalPlaces == null) return;
		
		int pos = kernel.getPrintDecimals();
		try {
			((JRadioButtonMenuItem) menuDecimalPlaces.getMenuComponent(pos))
					.setSelected(true);
		} catch (Exception e) {
		}
	}

	private void updateMenuContinuity() {
		int pos = kernel.isContinuous() ? 0 : 1;
		try {
			((JRadioButtonMenuItem) menuContinuity.getMenuComponent(pos))
					.setSelected(true);
		} catch (Exception e) {
		}
	}

	private void addLanguageMenuItems(JMenu menu, ActionListener al) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;
		String ggbLangCode;

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
			menu.add(mi);
		}
	}

	private void addRadioButtonMenuItems(JMenu menu, ActionListener al,
			String[] items, String[] actionCommands, int selectedPos) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;

		for (int i = 0; i < items.length; i++) {
			mi = new JRadioButtonMenuItem(app.getMenu(items[i]));
			if (i == selectedPos)
				mi.setSelected(true);
			mi.setActionCommand(actionCommands[i]);
			mi.addActionListener(al);
			bg.add(mi);
			menu.add(mi);
		}
	}
	
	 // handle language changes
    private class LanguageActionListener implements ActionListener {                        
        public void actionPerformed(ActionEvent e) {
        	app.setLanguage(app.getLocale(e.getActionCommand()));        	
        	GeoGebraPreferences.saveDefaultLocale(app.getLocale());
        }
    }
    
    private static String readTextFromJar(String s) {
        StringBuffer sb = new StringBuffer();        
        try {
          InputStream is = GeoGebra.class.getResourceAsStream(s);
          BufferedReader br = new BufferedReader
             (new InputStreamReader(is));
          String thisLine;
          while ((thisLine = br.readLine()) != null) {  
             sb.append(thisLine);
             sb.append("\n");
             }
          }
        catch (Exception e) {
          e.printStackTrace();
          }
          return sb.toString();
      }
 


}
