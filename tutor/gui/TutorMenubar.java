package tutor.gui;

import geogebra.Application;
import geogebra.GeoGebra;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.ConstructionProtocolNavigation;
import geogebra.gui.GeoGebraPreferences;
import geogebra.gui.ToolCreationDialog;
import geogebra.gui.ToolManagerDialog;
import geogebra.gui.menubar.Menubar;
import geogebra.gui.menubar.MenubarImpl;
import geogebra.gui.util.BrowserLauncher;
import geogebra.gui.util.ImageSelection;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import tutor.GeoGebraAppletTutor;
import tutor.io.StringOutputStream;
import tutor.net.util.HttpMultiPartFileUpload;
import tutor.net.util.HttpParam;

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
	
	private void addWindowMenu() {
		
		if (!app.isApplet()) {
			// windows menu
			menuWindow = new JMenu(app.getMenu("Window"));
			updateMenuWindow();
			add(menuWindow);
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
		//addViewMenu();
		
		// Tools menu
		//addToolsMenu();

		//addWindowMenu();

		addOptionsMenu();
		
		//addHelpMenu();
		
		updateMenubar();
	}

	protected void initActions() {				
		showAlgebraViewAction = new AbstractAction(app.getPlain("AlgebraWindow")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowAlgebraView(!app.showAlgebraView());
				app.updateCenterPanel(true);
			}
		};

	    // Michael Borcherds 2008-01-14
		showSpreadsheetAction = new AbstractAction(app.getPlain("Spreadsheet")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowSpreadsheet(!app.showSpreadsheet());
				//app.updateCenterPanel(true);
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
				app.getPlain("ConstructionProtocolNavigation"),
				app.getEmptyIcon()) {
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
		    			GeoGebra.createNewWindow(null);		    		
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
				app.showPropertiesDialog();
			}
		};

		constProtocolAction = new AbstractAction(
				app.getPlain("ConstructionProtocol") + " ...",
				app.getImageIcon("table.gif")) {
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
				+ " ...", app.getImageIcon("document-properties.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.showDrawingPadPropertiesDialog();
			}
		};

		toolbarConfigAction = new AbstractAction(app.getMenu("Toolbar.Customize")
				+ " ...", app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.showToolbarConfigDialog();
			}
		};

		saveAction = new AbstractAction(app.getMenu("Save"),
				app.getImageIcon("document-save.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				//app.save();
				
				System.out.println("Guardar");
				System.out.println(app.getXML());
				
				//String url = "http://localhost/agentgeom/continguts/problemes/upload_file.php";
				String url = "http://158.109.2.26/edumat/agentgeom/continguts/problemes/upload_file.php";
				
				System.out.println("1111111111111");
				
				StringOutputStream sos = new StringOutputStream();
				File fileOut = null;
				
				System.out.println("222222222222222");
				
				try {
					fileOut = File.createTempFile("tempfile",".tmp");
					System.out.println(fileOut.getAbsolutePath());
					
					FileOutputStream fos = new FileOutputStream(fileOut);
					app.getXMLio().writeGeoGebraFile(fos);
					
					HttpParam param = new HttpParam();
					param.setName("fitxer");
					param.setValue(fileOut);
					
					HttpParam pIdProblema = new HttpParam();
					pIdProblema.setName("id_problem");
					pIdProblema.setValue("1");
					
					HttpParam pIdStudent = new HttpParam();
					pIdStudent.setName("id_student");
					pIdStudent.setValue("2");
					
					List params = new ArrayList();
					params.add(param);
					params.add(pIdStudent);
					params.add(pIdProblema);

					HttpMultiPartFileUpload mpfu = new HttpMultiPartFileUpload();
					
					mpfu.send(url, params);
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
				catch (Throwable t) {
					t.printStackTrace();
				}
			}
		};

		saveAsAction = new AbstractAction(app.getMenu("SaveAs") + " ...",
				app.getEmptyIcon()) {
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
				
				GeoGebraAppletTutor ggbat = (GeoGebraAppletTutor)app.getApplet();
				ggbat.getProblem();
				ggbat.getStudent();
				String context = ggbat.getTutorView().getStrategyFilesURL();
				
				//String file = strategy.getFile();
				/*
				String strUrl = context + "/" + file;
				strategy.setUrl(strUrl);
				URL strategyUrl = new URL(strategy.getUrl());
				
				ggbat.getTutorView().getConstruction();
				*/
			}
		};

		

		refreshAction = new AbstractAction(app.getMenu("Refresh"),
				app.getImageIcon("view-refresh.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.refreshViews();
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

		helpAction = new AbstractAction(app.getMenu("Help"),
				app.getImageIcon("help.png")) {
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
				+ Application.FILE_EXT_PNG + ", " + Application.FILE_EXT_EPS + ") ...", 
				app.getImageIcon("image-x-generic.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						app.setWaitCursor();
						try {							
					    	app.clearSelectedGeos();
					    	
					    	// use reflection for
				  		    // JDialog d = new geogebra.export.GraphicExportDialog(app);   		
				  		    Class casViewClass = Class.forName("geogebra.export.GraphicExportDialog");
				  		    Object[] args = new Object[] { app };
				  		    Class [] types = new Class[] {Application.class};
				  	        Constructor constructor = casViewClass.getDeclaredConstructor(types);   	        
				  	        JDialog d =  (JDialog) constructor.newInstance(args);  					    
					      
					        d.setVisible(true);
					       
						} catch (Exception e) {
							System.err.println("GraphicExportDialog not available");
						}
						app.setDefaultCursor();
					}
				};
				runner.start();
			}
		};
		
		
		exportPSTricksAction = new AbstractAction(app.getPlain("DrawingPad") + " "
				+ app.getPlain("as") + " PSTricks ...", 
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {				
				try {		
					// use reflection for
		  		    // new geogebra.export.pstricks.GeoGebraToPstricks(app);			
		  		    Class casViewClass = Class.forName("geogebra.export.pstricks.GeoGebraToPstricks");
		  		    Object[] args = new Object[] { app };
		  		    Class [] types = new Class[] {Application.class};
		  	        Constructor constructor = casViewClass.getDeclaredConstructor(types);   	        
		  	        constructor.newInstance(args);  																
				} catch (Exception ex) {
					System.err.println("GeoGebraToPstricks not available");
				}	
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
				+ Application.FILE_EXT_HTML + ") ...",
				app.getImageIcon("text-html.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						app.setWaitCursor();
						try {
							app.clearSelectedGeos();
							
							// use reflection for
				  		    // JDialog d = new geogebra.export.WorksheetExportDialog(app); 		
				  		    Class casViewClass = Class.forName("geogebra.export.WorksheetExportDialog");
				  		    Object[] args = new Object[] { app };
				  		    Class [] types = new Class[] {Application.class};
				  	        Constructor constructor = casViewClass.getDeclaredConstructor(types);   	        
				  	        JDialog d =  (JDialog) constructor.newInstance(args); 
														
							d.setVisible(true);
						} catch (Exception e) {
							System.err.println("WorksheetExportDialog not available");
							e.printStackTrace();
						}
						app.setDefaultCursor();
					}
				};
				runner.start();
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
				GeoGebraPreferences.saveXMLPreferences(app);				
			}
		};
		
		clearPreferencesAction = new AbstractAction(app.getMenu("Settings.ResetDefault"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.clearPreferences();				
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
		
		deleteAction = new AbstractAction(app.getPlain("Delete"), 
				app.getImageIcon("delete_small.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				if (app.letDelete()) {
					Object [] geos = app.getSelectedGeos().toArray();
					for (int i=0; i < geos.length; i++) {
						GeoElement geo = (GeoElement) geos[i];
						geo.remove();
					}
					app.storeUndoInfo();
				}
			}
		};		
		
		websiteAction  = new AbstractAction("www.geogebra.org", 
							new ImageIcon(app.getInternalImage("geogebra.gif"))) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				app.showURLinBrowser(Application.GEOGEBRA_WEBSITE);
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
				new ImageIcon(app.getInternalImage("wiki.jpg"))) {
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
	
	public void updateMenubar() {	
		EuclidianView ev = app.getEuclidianView();
		if (cbShowAxes!=null) cbShowAxes.setSelected(ev.getShowXaxis() && ev.getShowYaxis());
	    if (cbShowGrid!=null) cbShowGrid.setSelected(ev.getShowGrid());
	    
	    if (cbShowAlgebraView!=null) cbShowAlgebraView.setSelected(app.showAlgebraView());
	    if (cbShowSpreadsheet!=null) cbShowSpreadsheet.setSelected(app.showSpreadsheet());     // Michael Borcherds 2008-01-14
        if (cbShowAlgebraInput!=null) cbShowAlgebraInput.setSelected(app.showAlgebraInput());
        if (cbShowAuxiliaryObjects!=null) cbShowAuxiliaryObjects.setSelected(app.showAuxiliaryObjects());

		boolean showAlgebraView = app.showAlgebraView();
		if (cbShowAlgebraView!=null) cbShowAlgebraView.setSelected(showAlgebraView);
		if (cbShowSpreadsheet!=null) cbShowAuxiliaryObjects.setVisible(showAlgebraView);
		if (cbShowAuxiliaryObjects!=null) cbShowAuxiliaryObjects.setSelected(app.showAuxiliaryObjects());

		if (cbHorizontalSplit!=null) cbHorizontalSplit.setVisible(showAlgebraView);
		if (cbHorizontalSplit!=null) cbHorizontalSplit.setSelected(app.isHorizontalSplit());
		
		if (cbShowAlgebraInput!=null) cbShowAlgebraInput.setSelected(app.showAlgebraInput());
		if (cbShowCmdList!=null) cbShowCmdList.setSelected(app.showCmdList());
		if (cbShowCmdList!=null) cbShowCmdList.setVisible(app.showAlgebraInput());		
			
		
		if (cbShowConsProtNavigation!=null) cbShowConsProtNavigation.setSelected(app.showConsProtNavigation());				
		if (cbShowConsProtNavigationPlay!=null) cbShowConsProtNavigationPlay
				.setSelected(app.isConsProtNavigationPlayButtonVisible());
		if (cbShowConsProtNavigationOpenProt!=null) cbShowConsProtNavigationOpenProt
				.setSelected(app.isConsProtNavigationProtButtonVisible());
		if (cbShowConsProtNavigationPlay!=null) cbShowConsProtNavigationPlay.setVisible(app.showConsProtNavigation());
		if (cbShowConsProtNavigationOpenProt!=null) cbShowConsProtNavigationOpenProt.setVisible(app.showConsProtNavigation());	

		// Michael Borcherds 2008-03-03 BEGIN put these back in
        updateMenuContinuity();
        updateMenuPointCapturing();
        updateMenuAngleUnit();
        updateMenuDecimalPlaces();
        updateMenuPointStyle();
        updateMenuRightAngleStyle();
        updateMenuCoordStyle();	
        updateMenuLabeling();        
        
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
		
		//mi = menu.add(saveAsAction);	
		//setMenuShortCutShiftAccelerator(mi, 'S');
		
		menu.addSeparator();
		
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
		
		mi = submenu.add(exportPSTricksAction);
		setMenuShortCutShiftAccelerator(mi, 'T');
		
		mi = submenu.add(drawingPadToClipboardAction);
		setMenuShortCutShiftAccelerator(mi, 'C');
				
		// DONE HERE WHEN APPLET
		if (app.isApplet()) return;
		
		
		// LAST FILES list
		/*int size = Application.getFileListSize();
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
		}*/
		
		// close
		menu.addSeparator();
		mi = menu.add(exitAction);
		if (GeoGebra.MAC_OS) {
			setMenuShortCutAccelerator(mi, 'W');
		} else {
			// Alt + F4
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK);
			mi.setAccelerator(ks);
		}

		// close all		
		if (GeoGebra.getInstanceCount() > 1) {								
			menu.add(exitAllAction);
		}
	}
}
