/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
package geogebra;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.io.MyXMLio;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.kernel.Relation;
import geogebra.modules.JarManager;
import geogebra.plugin.GgbAPI;
import geogebra.plugin.PluginManager;
import geogebra.util.CopyURLToFile;
import geogebra.util.ImageManager;
import geogebra.util.LowerCaseDictionary;
import geogebra.util.Util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.FontUIResource;

public abstract class Application implements KeyEventDispatcher {

	// version
	public static final String buildDate = "September 28, 2008";
	public static final String versionString = "3.1.44.0";
	public static final String XML_FILE_FORMAT = "3.02";
	public static final String I2G_FILE_FORMAT = "1.00.20080731";

	// disabled parts
	public static final boolean DISABLE_I2G = false;

	// GeoGebra jar files
	public static final int JAR_FILE_GEOGEBRA = 0;
	public static final int JAR_FILE_GEOGEBRA_GUI = 1;
	public static final int JAR_FILE_GEOGEBRA_CAS = 2;
	public static final int JAR_FILE_GEOGEBRA_EXPORT = 3;
	public static final int JAR_FILE_GEOGEBRA_PROPERTIES = 4;
	public static final String[] JAR_FILES = { "geogebra.jar",
			"geogebra_gui.jar", "geogebra_cas.jar", "geogebra_export.jar",
			"geogebra_properties.jar" };

	public final static String GEOGEBRA_WEBSITE = "http://www.geogebra.org/";

	// update URL
	// public static final String UPDATE_URL =
	// "http://www.geogebra.org/webstart/unpacked/";

	// supported GUI languages (from properties files)
	public static ArrayList supportedLocales = new ArrayList();
	static {
		supportedLocales.add(new Locale("ar")); // Arabic
		supportedLocales.add(new Locale("eu")); // Basque
		supportedLocales.add(new Locale("bs")); // Bosnian
		supportedLocales.add(new Locale("bg")); // Bulgarian
		supportedLocales.add(new Locale("ca")); // Catalan
		supportedLocales.add(new Locale("zh", "CN")); // Chinese (Simplified)
		supportedLocales.add(new Locale("zh", "TW")); // Chinese (Traditional)
		supportedLocales.add(new Locale("hr")); // Croatian
		supportedLocales.add(new Locale("cz")); // Czeck
		supportedLocales.add(new Locale("da")); // Danish
		supportedLocales.add(new Locale("nl")); // Dutch
		supportedLocales.add(new Locale("en")); // English
		supportedLocales.add(new Locale("en", "UK")); // English (UK)
		supportedLocales.add(new Locale("et")); // Estonian
		supportedLocales.add(new Locale("fi")); // Finnish
		supportedLocales.add(new Locale("fr")); // French
		supportedLocales.add(new Locale("gl")); // Galician
		supportedLocales.add(new Locale("de")); // German
		supportedLocales.add(new Locale("de", "AT")); // German (Austria)
		supportedLocales.add(new Locale("el")); // Greek
		supportedLocales.add(new Locale("iw")); // Hebrew
		supportedLocales.add(new Locale("hu")); // Hungarian
		supportedLocales.add(new Locale("is")); // Icelandic
		supportedLocales.add(new Locale("in")); // Indonesian
		supportedLocales.add(new Locale("it")); // Italian
		supportedLocales.add(new Locale("ja")); // Japanese
		supportedLocales.add(new Locale("mk")); // Macedonian
		supportedLocales.add(new Locale("no", "NO")); // Norwegian (Bokmal)
		supportedLocales.add(new Locale("no", "NO", "NY")); // Norwegian
															// (Nynorsk)
		supportedLocales.add(new Locale("fa")); // Persian
		supportedLocales.add(new Locale("pl")); // Polish
		supportedLocales.add(new Locale("pt", "BR")); // Portugese (Brazil)
		supportedLocales.add(new Locale("pt", "PT")); // Portuguese (Portugal)
		supportedLocales.add(new Locale("ru")); // Russian
		supportedLocales.add(new Locale("sr")); // Serbian
		supportedLocales.add(new Locale("sk")); // Slovakian
		supportedLocales.add(new Locale("sl")); // Slovenian
		supportedLocales.add(new Locale("sv")); // Swedish
		supportedLocales.add(new Locale("es")); // Spanish
		supportedLocales.add(new Locale("tr")); // Turkish
		supportedLocales.add(new Locale("vi")); // Vietnamese
	}

	// specialLanguageNames: Java does not show an English name for all
	// languages
	// supported by GeoGebra, so some language codes have to be treated
	// specially
	public static Hashtable specialLanguageNames = new Hashtable();
	static {
		specialLanguageNames.put("en", "English (US)");
		specialLanguageNames.put("enUK", "English (UK)");
		specialLanguageNames.put("deAT", "German (Austria)");
		specialLanguageNames.put("gl", "Galician");
		specialLanguageNames.put("noNO", "Norwegian (Bokm\u00e5l)");
		specialLanguageNames.put("noNONY", "Norwegian (Nynorsk)");
		specialLanguageNames.put("bs", "Bosnian");
		specialLanguageNames.put("cz", "Czech");
		specialLanguageNames.put("ptBR", "Portuguese (Brazil)");
		specialLanguageNames.put("ptPT", "Portuguese (Portugal)");
		specialLanguageNames.put("zhCN", "Chinese (Simplified)");
		specialLanguageNames.put("zhTW", "Chinese (Traditional)");
	}

	public static final Color COLOR_SELECTION = new Color(225, 225, 245);

	// Font settings
	private static final String STANDARD_FONT_NAME_SANS_SERIF = "SansSerif";
	private static final String STANDARD_FONT_NAME_SERIF = "Serif";
	public static final int MIN_FONT_SIZE = 10;
	private String appFontNameSansSerif = "SansSerif";
	private String appFontNameSerif = "Serif";

	// file extension string
	public static final String FILE_EXT_GEOGEBRA = "ggb";
	// Added for Intergeo File Format (Yves Kreis) -->
	public static final String FILE_EXT_INTERGEO = "i2g";
	// <-- Added for Intergeo File Format (Yves Kreis)
	public static final String FILE_EXT_GEOGEBRA_TOOL = "ggt";
	public static final String FILE_EXT_PNG = "png";
	public static final String FILE_EXT_EPS = "eps";
	public static final String FILE_EXT_PDF = "pdf";
	public static final String FILE_EXT_EMF = "emf";
	public static final String FILE_EXT_SVG = "svg";
	public static final String FILE_EXT_HTML = "html";
	public static final String FILE_EXT_TEX = "tex";

	protected File currentPath, currentImagePath, currentFile = null;

	// page margin in cm
	public static final double PAGE_MARGIN_X = 1.8 * 72 / 2.54;
	public static final double PAGE_MARGIN_Y = 1.8 * 72 / 2.54;

	private static final String RB_MENU = "properties/menu";
	private static final String RB_COMMAND = "properties/command";
	private static final String RB_ERROR = "properties/error";
	private static final String RB_PLAIN = "properties/plain";

	private static final String RB_SETTINGS = "export/settings";
	private static final String RB_ALGO2COMMAND = "kernel/algo2command";
	// Added for Intergeo File Format (Yves Kreis) -->
	private static final String RB_ALGO2INTERGEO = "kernel/algo2intergeo";
	// <-- Added for Intergeo File Format (Yves Kreis)

	// private static Color COLOR_STATUS_BACKGROUND = new Color(240, 240, 240);

	private GeoGebra frame;
	private GeoGebraAppletBase applet;
	
	private GuiManager appGuiManager;
	private JComponent casView;

	private Component mainComp;
	private boolean isApplet = false;
	private boolean showResetIcon = false;
	private URL codebase;

	protected Kernel kernel;
	private MyXMLio myXMLio;

	protected EuclidianView euclidianView;
	private EuclidianController euclidianController;
	private GeoElementSelectionListener currentSelectionListener;

	// For language specific settings
	private Locale currentLocale;
	private ResourceBundle rbmenu, rbcommand, rberror, rbplain, rbsettings;
	private ImageManager imageManager;

	// Hashtable for translation of commands from
	// local language to internal name
	// key = local name, value = internal name
	private Hashtable translateCommandTable = new Hashtable();
	// command dictionary
	private LowerCaseDictionary commandDict = new LowerCaseDictionary();

	private boolean INITING = false;
	protected boolean showAlgebraView = true;
	private boolean showAuxiliaryObjects = false;
	private boolean showAlgebraInput = true;
	private boolean showCmdList = true;
	protected boolean showToolBar = true;
	protected boolean showMenuBar = true;
	protected boolean showConsProtNavigation = false;
	private boolean[] showAxes = { true, true };
	private boolean showGrid = false;
	private boolean antialiasing = true;
	private boolean showSpreadsheet = false;
	private boolean showCAS = false;
	private boolean printScaleString = false;
	private int labelingStyle = ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC;

	private boolean undoActive = true;
	private boolean rightClickEnabled = true;
	private boolean shiftDragZoomEnabled = true;
	private boolean isErrorDialogsActive = true;

	private static LinkedList fileList = new LinkedList();
	private boolean isSaved = true;
	private int appFontSize;
	public Font boldFont, plainFont, smallFont;

	protected JPanel centerPanel;

	protected JSplitPane sp;
	private JSplitPane sp2;
	private DividerChangeListener spChangeListener;
	protected int initSplitDividerLocationHOR2 = 250; // init value
	protected int initSplitDividerLocationVER2 = 300; // init value
	protected int initSplitDividerLocationHOR = 650; // init value
	protected int initSplitDividerLocationVER = 400; // init value
	protected boolean horizontalSplit = true; // 

	private ArrayList selectedGeos = new ArrayList();
		
	private JarManager jarmanager = null;
	private GgbAPI ggbapi = null;	
	private PluginManager pluginmanager = null;	

	public Application(String[] args, GeoGebra frame, boolean undoActive) {
		this(args, frame, null, undoActive);
	}

	public Application(String[] args, GeoGebraAppletBase applet,
			boolean undoActive) {
		this(args, null, applet, undoActive);
	}

	protected Application(String[] args, GeoGebra frame,
			GeoGebraAppletBase applet, boolean undoActive) {
		/*
		 * if (args != null) { for (int i=0; i < args.length; i++) {
		 * Application.debug("argument " + i + ": " + args[i]);
		 * JOptionPane.showConfirmDialog( null, "argument " + i + ": " +
		 * args[i], "Arguments", JOptionPane.DEFAULT_OPTION,
		 * JOptionPane.PLAIN_MESSAGE); } }
		 */

		// Michael Borcherds 2008-05-05
		// added to help debug applets
		Application.debug("GeoGebra " + versionString + " " + buildDate
				+ " Java " + System.getProperty("java.version"));

		isApplet = applet != null;
		if (frame != null) {
			mainComp = frame;
		} else if (isApplet) {
			mainComp = applet;
		}

		// init code base URL
		initCodeBase();

		// initialize jar manager for dynamic jar loading
		jarmanager = JarManager.getSingleton(this);

		// applet/command line options like file loading on startup
		handleOptionArgs(args); // note: the locale is set here too
		imageManager = new ImageManager(mainComp);

		if (isApplet) {
			setApplet(applet);
		} else {
			// frame
			setFrame(frame);
		}

		// init kernel
		kernel = new Kernel(this);
		kernel.setPrintDecimals(Kernel.STANDARD_PRINT_DECIMALS);

		// init xml io for construction loading
		myXMLio = new MyXMLio(kernel, kernel.getConstruction());

		// init euclidian view
		euclidianController = new EuclidianController(kernel);
		euclidianView = new EuclidianView(euclidianController, showAxes,
				showGrid);
		euclidianView.setAntialiasing(antialiasing);

		// load file on startup and set fonts
		// INITING: to avoid multiple calls of setLabels() and
		// updateContentPane()
		INITING = true;
		setFontSize(getInitFontSize());

		if (!isApplet) {
			// init preferences
			GeoGebraPreferences.getPref().initDefaultXML(this);
		}

		// open file given by startup parameter
		boolean fileLoaded = handleFileArg(args);

		if (!isApplet) {
			// load XML preferences
			currentPath = GeoGebraPreferences.getPref().getDefaultFilePath();
			currentImagePath = GeoGebraPreferences.getPref()
					.getDefaultImagePath();
			if (!fileLoaded)
				GeoGebraPreferences.getPref().loadXMLPreferences(
						this);
		}

		// init undo
		setUndoActive(undoActive);
		INITING = false;	

		// for key listening
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);

		// Mathieu Blossier - place for code to test 3D packages
	
		// init plugin manager for applications
		if (!isApplet)
			pluginmanager = getPluginManager();

		// load all jar files in background and init dialogs
		initInBackground();
	}

	/**
	 * Returns this application's GUI manager which is an instance of
	 * geogebra.gui.ApplicationGUImanager. Loads gui jar file and creates GUI
	 * manager if needed.
	 * 
	 * @return Object to avoid import geogebra.gui.ApplicationGUImanager in
	 *         Application. Note that the gui jar file may not be loaded at all
	 *         in applets.
	 */
	final public synchronized GuiManager getGuiManager() {
		if (appGuiManager == null) {
			loadGUIJar();
			
			appGuiManager = new geogebra.gui.DefaultGuiManager(Application.this);					
			
//			// this code wraps the creation of the DefaultGuiManager and is
//			// necessary to allow dynamic loading of this class
//			ActionListener al = new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					appGuiManager = new geogebra.gui.DefaultGuiManager(Application.this);					
//				}				
//			};			
//			al.actionPerformed(null);				
		}

		return appGuiManager;
	}

	final public boolean hasGuiManager() {
		return appGuiManager != null;
	}

	private void initInBackground() {
		if (!initInBackground_first_time) return;
		initInBackground_first_time = false;
		
		// init file chooser and properties dialog
		// in a background task
		Thread runner = new Thread() {
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (Exception e) {
				}
												
				// init properties dialog
				getGuiManager().initPropertiesDialog();
				//TODO: remove
				Application.debug("background: properties dialog inited");
				
				// init file chooser
				getGuiManager().initFileChooser();
				//TODO: remove
				Application.debug("background: file chooser inited");

				// init CAS
				kernel.initCAS();
				//TODO: remove
				Application.debug("background: CAS inited");
				
				// add jar files to classpath dynamically in background
				for (int i=0; i < JAR_FILES.length; i++) {
					jarmanager.addJarToClassPath(i);
				}
			}
		};
		runner.start();		
	}
	private static boolean initInBackground_first_time = true;

	public void setUnsaved() {
		isSaved = false;
	}

	public boolean isIniting() {
		return INITING;
	}

	/**
	 * Returns labeling style. See the constants in ConstructionDefaults (e.g.
	 * LABEL_VISIBLE_AUTOMATIC)
	 */
	public int getLabelingStyle() {
		return labelingStyle;
	}

	/**
	 * Sets labeling style. See the constants in ConstructionDefaults (e.g.
	 * LABEL_VISIBLE_AUTOMATIC)
	 */
	public void setLabelingStyle(int labelingStyle) {
		this.labelingStyle = labelingStyle;
	}

	/**
	 * Updates the GUI of the main component.
	 */
	public void updateContentPane() {
		updateContentPane(true);
	}

	/**
	 * Updates the GUI of the framd and its size.
	 */
	public void updateContentPaneAndSize() {
		if (INITING)
			return;

		if (frame != null) {
			updateContentPane(false);
			frame.updateSize();
			updateComponentTreeUI();
		} else {
			updateContentPane();
		}
	}

	private void updateContentPane(boolean updateComponentTreeUI) {

		if (INITING)
			return;

		Container cp;
		if (frame == null)
			cp = applet.getContentPane();
		else
			cp = frame.getContentPane();

		addMacroCommands();
		cp.removeAll();
		cp.add(buildApplicationPanel());
		setLAFFontSize();
		euclidianView.updateSize();

		if (updateComponentTreeUI)
			updateComponentTreeUI();
		setMoveMode();

		if (mainComp.isShowing())
			euclidianView.requestFocusInWindow();

		System.gc();
	}

	protected void updateComponentTreeUI() {
		if (frame == null)
			SwingUtilities.updateComponentTreeUI(applet);
		else
			SwingUtilities.updateComponentTreeUI(frame);
	}

	/**
	 * Builds a panel with all components that should be shown on screen (like
	 * toolbar, input field, algebra view).
	 */
	public JPanel buildApplicationPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		// CENTER: Algebra View, Euclidian View
		// euclidian panel with view and status bar
		centerPanel = new JPanel(new BorderLayout());

		if (showToolBar) {
			// NORTH: Toolbar
			panel.add(getGuiManager().getToolbarPanel(),
					BorderLayout.NORTH);
		}

		// updateCenterPanel
		updateCenterPanel(true);
		panel.add(centerPanel, BorderLayout.CENTER);

		// SOUTH: inputField
		if (showAlgebraInput) {
			panel.add(getGuiManager().getAlgebraInput(),
					BorderLayout.SOUTH);
		}

		// init labels
		setLabels();
		return panel;
	}

	public void updateCenterPanel(boolean updateUI) {
		centerPanel.removeAll();

		JPanel eup = new JPanel(new BorderLayout());
		eup.setBackground(Color.white);
		eup.add(euclidianView, BorderLayout.CENTER);

		if (showConsProtNavigation) {
			JComponent consProtNav = getGuiManager().getConstructionProtocolNavigation();
			eup.add(consProtNav, BorderLayout.SOUTH);
			consProtNav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
					Color.gray));
		}

		JComponent cp2 = null;
		/*
		 * if (showSpreadsheet) { if (horizontalSplit) { sp2 = new
		 * JSplitPane(JSplitPane.HORIZONTAL_SPLIT, eup, spreadsheetView);
		 * sp2.setDividerLocation(initSplitDividerLocationHOR2); } else { sp2 =
		 * new JSplitPane(JSplitPane.VERTICAL_SPLIT, eup, spreadsheetView);
		 * sp2.setDividerLocation(initSplitDividerLocationVER2); }
		 * sp2.addPropertyChangeListener("dividerLocation", new
		 * DividerChangeListener2()); cp2 = sp2; } else { cp2 = eup; }
		 * 
		 * JComponent cp1 = null; if (showAlgebraView) { if (horizontalSplit) {
		 * sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new
		 * JScrollPane(algebraView), cp2);
		 * sp.setDividerLocation(initSplitDividerLocationHOR); } else { sp = new
		 * JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(algebraView),
		 * cp2); sp.setDividerLocation(initSplitDividerLocationVER); }
		 * sp.addPropertyChangeListener("dividerLocation", new
		 * DividerChangeListener()); cp1 = sp; } else { cp1 = cp2; }
		 */
		if (showAlgebraView) {
			if (horizontalSplit) {
				sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
						new JScrollPane(getGuiManager()
								.getAlgebraView()), eup);
				sp2.setDividerLocation(initSplitDividerLocationHOR2);
			} else {
				sp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, eup,
						new JScrollPane(getGuiManager()
								.getAlgebraView()));
				sp2.setDividerLocation(initSplitDividerLocationVER2);
			}

			if (spChangeListener == null)
				spChangeListener = new DividerChangeListener();
			sp2.addPropertyChangeListener("dividerLocation", spChangeListener);
			cp2 = sp2;
		} else {
			cp2 = eup;
		}

		JComponent cp1 = null;
		if (showSpreadsheet) {
			if (horizontalSplit) {
				sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cp2,
						getGuiManager().getSpreadsheetView());
				sp.setDividerLocation(initSplitDividerLocationHOR);
			} else {
				sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cp2,
						getGuiManager().getSpreadsheetView());
				sp.setDividerLocation(initSplitDividerLocationVER);
			}

			if (spChangeListener == null)
				spChangeListener = new DividerChangeListener();
			sp.addPropertyChangeListener("dividerLocation", spChangeListener);
			cp1 = sp;
		} else {
			cp1 = cp2;
		}
		centerPanel.add(cp1, BorderLayout.CENTER);

		// border of euclidianPanel
		int eupTopBorder = !showAlgebraView && showToolBar ? 1 : 0;
		int eupBottomBorder = showToolBar
				&& !(showAlgebraView && !horizontalSplit) ? 1 : 0;
		eup.setBorder(BorderFactory.createMatteBorder(eupTopBorder, 0,
				eupBottomBorder, 0, Color.gray));

		if (updateUI)
			updateComponentTreeUI();
	}

	public JPanel getCenterPanel() {
		return centerPanel;
	}

	/**
	 * Handles command line options (like -language).
	 */
	private void handleOptionArgs(String[] args) {
		// locale should be set here either to default locale or
		// according to the -language option
		Locale initLocale = mainComp.getLocale();

		if (args != null) {
			// handle all options (starting with --)
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith("--")) {
					// option found: get option's name and value
					int equalsPos = args[i].indexOf('=');
					String optionName = equalsPos <= 2 ? args[i].substring(2)
							: args[i].substring(2, equalsPos);
					String optionValue = equalsPos < 0
							|| equalsPos == args[i].length() - 1 ? "" : args[i]
							.substring(equalsPos + 1);

					if (optionName.equals("help")) {
						// help message
						System.out
								.println("Usage: java -jar geogebra.jar [OPTION] [FILE]\n"
										+ "Start GeoGebra with the specified OPTIONs and open the given FILE.\n"
										+ "  --help\t\tprint this message\n"
										+ "  --language=LANGUGE_CODE\t\tset language using locale strings, e.g. en, de, de_AT, ...\n"
										+ "  --showAlgebraInput=BOOLEAN\tshow/hide algebra input field\n"
										+ "  --showAlgebraWindow=BOOLEAN\tshow/hide algebra window\n"
										+ "  --showSpreadsheet=BOOLEAN\tshow/hide spreadsheet\n"
										+ "  --showCAS=BOOLEAN\tshow/hide CAS window\n"
										+ "  --showAxes=BOOLEAN\tshow/hide coordinate axes"
										+ "  --antiAliasing=BOOLEAN\tturn anti-aliasing on/off");
					} else if (optionName.equals("language")) {
						initLocale = getLocale(optionValue);

						// Application.debug("lanugage option: " + optionValue);
						// Application.debug("locale: " + initLocale);
					} else if (optionName.equals("showAlgebraInput")) {
						setShowAlgebraInput(!optionValue.equals("false"));
					} else if (optionName.equals("showAlgebraWindow")) {
						setShowAlgebraView(!optionValue.equals("false"));
					} else if (optionName.equals("showSpreadsheet")) {
						setShowSpreadsheetView(!optionValue.equals("false"));
					} else if (optionName.equals("showCAS")) {
						setShowCasView(!optionValue.equals("false"));
					} else if (optionName.equals("showAxes")) {
						showAxes[0] = !optionValue.equals("false");
						showAxes[1] = showAxes[0];
					} else if (optionName.equals("showGrid")) {
						showGrid = !optionValue.equals("false");
					} else if (optionName.equals("antiAliasing")) {
						antialiasing = !optionValue.equals("false");
					}
				}
			}
		}

		setLocale(initLocale);
	}

	/**
	 * Opens a file specified as last command line argument
	 * 
	 * @return true if a file was loaded successfully
	 */
	private boolean handleFileArg(String[] args) {

		if (args == null || args.length < 1)
			return false;

		String fileArgument = null;
		// the filename is the last command line argument
		// and should not be an option, i.e. it does not start
		// with "-" like -open, -print or -language
		if (args[args.length - 1].charAt(0) == '-')
			// option
			return false;
		else {
			// take last argument as filename
			fileArgument = args[args.length - 1];
		}

		try {
			boolean success;
			String lowerCase = fileArgument.toLowerCase(Locale.US);
			boolean isMacroFile = lowerCase.endsWith(FILE_EXT_GEOGEBRA_TOOL);
			if (lowerCase.startsWith("http") || lowerCase.startsWith("file")) {

				URL url = new URL(fileArgument);
				success = loadXML(url, isMacroFile);
				updateContentPane(true);
			} else {
				File f = new File(fileArgument);
				f = f.getCanonicalFile();
				success = getGuiManager().loadFile(f, isMacroFile);
			}
			return success;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Kernel getKernel() {
		return kernel;
	}

	public void setApplet(GeoGebraAppletBase applet) {
		isApplet = true;
		this.applet = applet;
		mainComp = applet;		
	}

	public void setShowResetIcon(boolean flag) {
		if (flag != showResetIcon) {
			showResetIcon = flag;
			euclidianView.updateBackground();
		}
	}

	final public boolean showResetIcon() {
		return showResetIcon;
	}

	public void reset() {
		if (applet != null) {
			applet.resetNoThread();
		} else if (currentFile != null) {
			getGuiManager().loadFile(currentFile, false);
		} else
			deleteAllGeoElements();
	}

	public void refreshViews() {
		euclidianView.updateBackground();
		kernel.notifyRepaint();
	}

	public void setFrame(GeoGebra frame) {
		isApplet = false;
		if (frame != this.frame) {
			this.frame = frame;
			mainComp = frame;
			updateTitle();
			frame.setIconImage(getInternalImage("geogebra.gif"));
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

			// window closing listener
			WindowAdapter windowListener = new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					exit();
				}
			};
			frame.addWindowListener(windowListener);
		}
	}

	final public boolean isApplet() {
		return isApplet;
	}

	public GeoGebraAppletBase getApplet() {
		return applet;
	}

	public GeoGebra getFrame() {
		return frame;
	}

	public Component getMainComponent() {
		return mainComp;
	}

	public EuclidianView getEuclidianView() {
		return euclidianView;
	}

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		if (currentSelectionListener != null)
			currentSelectionListener.geoElementSelected(geo, addToSelection);
	}

	/**
	 * Sets a mode where clicking on an object will notify the given selection
	 * listener.
	 */
	public void setSelectionListenerMode(GeoElementSelectionListener sl) {
		if (sl == null) {
			setMode(oldMode);
		} else {
			if (getMode() != EuclidianView.MODE_ALGEBRA_INPUT)
				oldMode = getMode();
			euclidianView.setMode(EuclidianView.MODE_ALGEBRA_INPUT);

			if (appGuiManager != null) {
				// update toolbar
				getGuiManager().setToolbarMode(EuclidianView.MODE_ALGEBRA_INPUT);
			}
		}

		currentSelectionListener = sl;
	}

	private int oldMode = 0;

	public GeoElementSelectionListener getCurrentSelectionListener() {
		return currentSelectionListener;
	}

	public GeoElementSelectionListener setCurrentSelectionListener(
			GeoElementSelectionListener listener) {
		return currentSelectionListener = listener;
	}

	public void setAlgebraInputMode() {
		setMode(EuclidianView.MODE_ALGEBRA_INPUT);
	}

	public void setMoveMode() {
		setMode(EuclidianView.MODE_MOVE);
	}

	public ImageIcon getImageIcon(String filename) {
		return getImageIcon(filename, null);
	}

	public ImageIcon getImageIcon(String filename, Color borderColor) {
		loadGUIJar();
		return imageManager.getImageIcon("/geogebra/gui/images/" + filename,
				borderColor);
	}

	public ImageIcon getEmptyIcon() {
		loadGUIJar();
		return imageManager.getImageIcon("/geogebra/gui/images/empty.gif");
	}

	public Image getInternalImage(String filename) {
		loadGUIJar();
		return imageManager
				.getInternalImage("/geogebra/gui/images/" + filename);
	}

	public BufferedImage getExternalImage(String filename) {
		return imageManager.getExternalImage(filename);
	}

	public void addExternalImage(String filename, BufferedImage image) {
		imageManager.addExternalImage(filename, image);
	}

//	public void startEditing(GeoElement geo) {
//		if (showAlgebraView)
//			getApplicationGUImanager().startEditingAlgebraView(geo);
//	}

	public final void zoom(double px, double py, double zoomFactor) {
		euclidianView.zoom(px, py, zoomFactor, 15, true);
	}

	/**
	 * Sets the ratio between the scales of y-axis and x-axis, i.e. ratio =
	 * yscale / xscale;
	 * 
	 * @param zoomFactor
	 */
	public final void zoomAxesRatio(double axesratio) {
		euclidianView.zoomAxesRatio(axesratio, true);
	}

	public final void setStandardView() {
		euclidianView.setStandardView(true);
	}

	public final void setViewShowAllObjects() {
		euclidianView.setViewShowAllObjects(true);
	}

	/**********************************************************
	 * LOCALE part
	 **********************************************************/

	/**
	 * Creates a Locale object according to the given language code. The
	 * languageCode string should consist of two letters for the language, two
	 * letters for the country and two letters for the variant. E.g. "en" ...
	 * language: English , no country specified, "deAT" or "de_AT" ... language:
	 * German , country: Austria, "noNONY" or "no_NO_NY" ... language: Norwegian
	 * , country: Norway, variant: Nynorsk
	 */
	public static Locale getLocale(String languageCode) {
		// remove "_" from string
		languageCode = languageCode.replaceAll("_", "");

		Locale loc;
		if (languageCode.length() == 6) {
			// language, country
			loc = new Locale(languageCode.substring(0, 2), languageCode
					.substring(2, 4), languageCode.substring(4, 6));
		} else if (languageCode.length() == 4) {
			// language, country
			loc = new Locale(languageCode.substring(0, 2), languageCode
					.substring(2, 4));
		} else {
			// language only
			loc = new Locale(languageCode.substring(0, 2));
		}
		return loc;
	}

	/**
	 * set language via iso language string
	 */
	public void setLanguage(Locale locale) {
		if (locale == null
				|| currentLocale.toString().equals(locale.toString()))
			return;

		if (!INITING) {
			setMoveMode();
		}

		// load resource files
		setLocale(locale);

		// update right angle style in euclidian view (different for German)
		if (euclidianView != null)
			euclidianView.updateRightAngleStyle(locale);

		// make sure to update commands
		fillCommandDict();

		kernel.updateLocalAxesNames();
		setLabels(); // update display

		System.gc();
	}

	/*
	 * removed Michael Borcherds 2008-03-31 private boolean reverseLanguage =
	 * false; //FKH 20040822 final public boolean isReverseLanguage() { //FKH
	 * 20041010 // for Chinese return reverseLanguage; }
	 */

	// for basque you have to say "A point" instead of "point A"
	private boolean reverseNameDescription = false;

	final public boolean isReverseNameDescriptionLanguage() {
		// for Basque
		return reverseNameDescription;
	}

	/*
	 * in French, zero is singular, eg 0 décimale rather than 0 decimal places
	 */
	public boolean isZeroPlural(Locale locale) {
		String lang = locale.getLanguage();
		if (lang.startsWith("fr"))
			return false;
		return true;
	}

	// For Hebrew and Arabic. Guy Hed, 25.8.2008
	private boolean rightToLeftReadingOrder = false;

	final public boolean isRightToLeftReadingOrder() {
		return rightToLeftReadingOrder;
	}

	private void updateReverseLanguage(Locale locale) {
		String lang = locale.getLanguage();
		// reverseLanguage = "zh".equals(lang); removed Michael Borcherds
		// 2008-03-31
		reverseNameDescription = "eu".equals(lang);

		// Guy Hed, 25.8.2008
		rightToLeftReadingOrder = ("iw".equals(lang) || "ar".equals(lang));
		// Another option:
		// rightToLeftReadingOrder =
		// (Character.getDirectionality(getPlain("Algebra").charAt(1)) ==
		// Character.DIRECTIONALITY_RIGHT_TO_LEFT);
	}

	// Michael Borcherds 2008-02-23
	public boolean languageIs(Locale locale, String lang) {
		return locale.getLanguage().equals(lang);
	}

	/**
	 * Sets the application's fonts for the current language. The user interface
	 * of certain languages like Chinese or Hebrew looks better with special
	 * fonts.
	 */
	private void getLanguageFontName(Locale locale) throws Exception {
		String lang = locale.getLanguage();

		// new font names for language
		String fontNameSansSerif = null;
		String fontNameSerif = null;

		// CHINESE
		if ("zh".equals(lang)) {
			// last CJK unified ideograph in unicode alphabet
			char testCharacater = '\u984F';
			fontNameSansSerif = getFontCanDisplay("\u00cb\u00ce\u00cc\u00e5",
					testCharacater);
			fontNameSerif = getFontCanDisplay("\u00cb\u00ce\u00cc\u00e5",
					testCharacater);
		}
		// HEBREW
		else if ("iw".equals(lang)) {
			// Hebrew letter "tav"
			char testCharacater = '\u05ea';
			fontNameSansSerif = getFontCanDisplay("Arial", testCharacater);
			fontNameSerif = getFontCanDisplay("Times New Roman", testCharacater);
			// Guy Hed, 25.8.2008 - rearranged fonts and changed test character.
		}
		// JAPANESE
		else if ("ja".equals(lang)) {
			// Katakana letter N
			char testCharacater = '\uff9d';
			fontNameSansSerif = getFontCanDisplay("", testCharacater);
			fontNameSerif = getFontCanDisplay("", testCharacater);
		}

		// make sure we have sans serif and serif fonts
		if (fontNameSansSerif == null)
			fontNameSansSerif = STANDARD_FONT_NAME_SANS_SERIF;
		if (fontNameSerif == null)
			fontNameSerif = STANDARD_FONT_NAME_SERIF;

		// update application fonts if changed
		if (fontNameSerif != appFontNameSerif
				|| fontNameSansSerif != appFontNameSansSerif) {
			appFontNameSerif = fontNameSerif;
			appFontNameSansSerif = fontNameSansSerif;
			resetFonts();
		}

		// Application.debug("sans: " + appFontNameSansSerif + ", serif: " +
		// appFontNameSerif);

	}

	/**
	 * Tries to find a font that can display the given unicode character. Starts
	 * with standardFontName first.
	 */
	private String getFontCanDisplay(String standardFontName, char unicodeChar)
			throws Exception {
		// try standard font
		if (testFontCanDisplay(standardFontName, unicodeChar))
			return standardFontName;

		// Determine which fonts support the character unicodeChar
		Font[] allfonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAllFonts();
		for (int j = 0; j < allfonts.length; j++) {
			if (allfonts[j].canDisplay(unicodeChar))
				return allfonts[j].getFontName();
		}
		throw new Exception(
				"Sorry, there is no font for this language available on your computer.");
	}

	private static boolean testFontCanDisplay(String fontName, char unicodeChar) {
		Font testFont = new Font(fontName, Font.PLAIN, 12);
		return testFont != null && testFont.canDisplay(unicodeChar);
	}

	public void setLocale(Locale locale) {
		Locale oldLocale = currentLocale;

		// only allow special locales due to some weird server
		// problems with the naming of the property files
		currentLocale = getClosestSupportedLocale(locale);
		updateResourceBundles();

		// update font for new language (needed for e.g. chinese)
		try {
			getLanguageFontName(currentLocale);
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.getMessage());

			// go back to previous locale
			currentLocale = oldLocale;
			updateResourceBundles();
		}

		updateReverseLanguage(locale);
	}

	/**
	 * Returns a locale object that has the same country and/or language as
	 * locale. If the language of locale is not supported an English locale is
	 * returned.
	 */
	private static Locale getClosestSupportedLocale(Locale locale) {
		int size = supportedLocales.size();

		// try to find country and variant
		String country = locale.getCountry();
		String variant = locale.getVariant();

		if (country.length() > 0) {
			for (int i = 0; i < size; i++) {
				Locale loc = (Locale) supportedLocales.get(i);
				if (country.equals(loc.getCountry())
						&& variant.equals(loc.getVariant()))
					// found supported country locale
					return loc;
			}
		}

		// try to find language
		String language = locale.getLanguage();
		for (int i = 0; i < size; i++) {
			Locale loc = (Locale) supportedLocales.get(i);
			if (language.equals(loc.getLanguage()))
				// found supported country locale
				return loc;
		}

		// we didn't find a matching country or language,
		// so we take English
		return Locale.ENGLISH;
	}

	public ResourceBundle initAlgo2CommandBundle() {
		return MyResourceBundle.loadSingleBundleFile(RB_ALGO2COMMAND);
	}

	// Added for Intergeo File Format (Yves Kreis) -->
	public ResourceBundle initAlgo2IntergeoBundle() {
		return MyResourceBundle.loadSingleBundleFile(RB_ALGO2INTERGEO);
	}

	// <-- Added for Intergeo File Format (Yves Kreis)

	private void updateResourceBundles() {
		if (rbmenu != null)
			rbmenu = MyResourceBundle.createBundle(RB_MENU, currentLocale);
		if (rberror != null)
			rberror = MyResourceBundle.createBundle(RB_ERROR, currentLocale);
		if (rbplain != null)
			rbplain = MyResourceBundle.createBundle(RB_PLAIN, currentLocale);
		if (rbcommand != null)
			rbcommand = MyResourceBundle.createBundle(RB_COMMAND, currentLocale);
	}	

	private void fillCommandDict() {
		if (rbcommand == null) return;
		
		translateCommandTable.clear();
		commandDict.clear();

		// Enumeration e = rbcommand.getKeys();
		Iterator it = kernel.getAlgebraProcessor().getCmdNameIterator();
		while (it.hasNext()) {
			String internal = (String) it.next();
			// Application.debug(internal);
			if (!internal.endsWith("Syntax") && !internal.equals("Command")) {
				String local = rbcommand.getString((String) internal);
				if (local != null) {
					local = local.trim();
					// case is ignored in translating local command names to
					// internal names!
					translateCommandTable.put(local.toLowerCase(Locale.US),
							internal);
					commandDict.addEntry(local);
				}
			}
		}

		addMacroCommands();
	}

	private void addMacroCommands() {
		if (commandDict == null || kernel == null || !kernel.hasMacros())
			return;

		ArrayList macros = kernel.getAllMacros();
		for (int i = 0; i < macros.size(); i++) {
			String cmdName = ((Macro) macros.get(i)).getCommandName();
			if (!commandDict.contains(cmdName))
				commandDict.addEntry(cmdName);
		}
	}

	public void removeMacroCommands() {
		if (commandDict == null || kernel == null || !kernel.hasMacros())
			return;

		ArrayList macros = kernel.getAllMacros();
		for (int i = 0; i < macros.size(); i++) {
			String cmdName = ((Macro) macros.get(i)).getCommandName();
			commandDict.removeEntry(cmdName);
		}
	}

	public Locale getLocale() {
		return currentLocale;
	}

	/*
	 * Jar managing
	 */

	final public boolean loadPropertiesJar() {		
		return jarmanager.addJarToClassPath(JAR_FILE_GEOGEBRA_PROPERTIES);			
	}

	final public boolean loadExportJar() {
		return jarmanager.addJarToClassPath(JAR_FILE_GEOGEBRA_EXPORT);		
	}

	final public boolean loadCASJar() {
		return jarmanager.addJarToClassPath(JAR_FILE_GEOGEBRA_CAS);
	}

	final public boolean loadGUIJar() {
		return jarmanager.addJarToClassPath(JAR_FILE_GEOGEBRA_GUI);
	}

	final public boolean loadLaTeXJar() {
		return loadGUIJar();
	}

	/*
	 * properties methods
	 */

	final public String getPlain(String key) {
		if (!loadPropertiesJar())
			return key;

		if (rbplain == null) {
			initPlainResourceBundle();
		}

		try {
			return rbplain.getString(key);
		} catch (Exception e) {
			return key;
		}
	}
	
	private void initPlainResourceBundle() {
		rbplain = MyResourceBundle.createBundle(RB_PLAIN, currentLocale);
		kernel.updateLocalAxesNames();
	}

	// Michael Borcherds 2008-03-25
	// replace "%0" by arg0
	final public String getPlain(String key, String arg0) {
		String[] ss = { arg0 };
		return getPlain(key, ss);
	}

	// Michael Borcherds 2008-03-25
	// replace "%0" by arg0, "%1" by arg1
	final public String getPlain(String key, String arg0, String arg1) {
		String[] ss = { arg0, arg1 };
		return getPlain(key, ss);
	}

	// Michael Borcherds 2008-03-30
	// replace "%0" by arg0, "%1" by arg1, "%2" by arg2
	final public String getPlain(String key, String arg0, String arg1,
			String arg2) {
		String[] ss = { arg0, arg1, arg2 };
		return getPlain(key, ss);
	}

	// Michael Borcherds 2008-03-30
	// replace "%0" by arg0, "%1" by arg1, "%2" by arg2, "%3" by arg3
	final public String getPlain(String key, String arg0, String arg1,
			String arg2, String arg3) {
		String[] ss = { arg0, arg1, arg2, arg3 };
		return getPlain(key, ss);
	}

	// Michael Borcherds 2008-03-30
	// replace "%0" by arg0, "%1" by arg1, "%2" by arg2, "%3" by arg3, "%4" by
	// arg4
	final public String getPlain(String key, String arg0, String arg1,
			String arg2, String arg3, String arg4) {
		String[] ss = { arg0, arg1, arg2, arg3, arg4 };
		return getPlain(key, ss);
	}

	// Michael Borcherds 2008-03-25
	// Markus Hohenwarter 2008-09-18
	// replace "%0" by args[0], "%1" by args[1], etc
	final public String getPlain(String key, String[] args) {
		String str = getPlain(key);

		sbPlain.setLength(0);
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '%') {
				// get number after %
				i++;
				int pos = str.charAt(i) - '0';
				sbPlain.append(args[pos]);
			} else {
				sbPlain.append(ch);
			}
		}

		return sbPlain.toString();
	}

	private StringBuffer sbPlain = new StringBuffer();

	final public String getMenu(String key) {
		if (!loadPropertiesJar())
			return key;

		if (rbmenu == null)
			rbmenu = MyResourceBundle.createBundle(RB_MENU, currentLocale);

		try {
			return rbmenu.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	final public String getError(String key) {
		if (!loadPropertiesJar())
			return key;

		if (rberror == null)
			rberror = MyResourceBundle.createBundle(RB_ERROR, currentLocale);
		try {
			return rberror.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	final public String getCommand(String key) {
		if (!loadPropertiesJar())
			return key;
		
		if (rbcommand == null)						
			rbcommand = MyResourceBundle.createBundle(RB_COMMAND, currentLocale);		
		
		try {
			return rbcommand.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	final public String getSetting(String key) {
		if (rbsettings == null)
			rbsettings = MyResourceBundle.loadSingleBundleFile(RB_SETTINGS);

		try {
			return rbsettings.getString(key);
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean propertiesFilesPresent() {
		return rbplain != null;
	}

	/**
	 * translate command name to internal name. Note: the case of localname is
	 * NOT relevant
	 */
	final public String translateCommand(String localname) {
		if (localname == null)
			return null;

		// note: lookup lower case of command name!
		Object value = translateCommandTable.get(localname
				.toLowerCase(Locale.US));

		String ret;
		if (value == null)
			ret = localname;
		else
			ret = (String) value;

		return ret;
	}

	public void showRelation(GeoElement a, GeoElement b) {
		JOptionPane.showConfirmDialog(mainComp, new Relation(kernel).relation(
				a, b), getPlain("ApplicationName") + " - "
				+ getCommand("Relation"), JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void showHelp(String key) {
		String text = getPlain(key); // Michael Borcherds changed to use
										// getPlain() and removed try/catch
		JOptionPane.showConfirmDialog(mainComp, text,
				getPlain("ApplicationName") + " - " + getMenu("Help"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	public void showError(String key) {
		showErrorDialog(getError(key));
	}

	public void showError(MyError e) {
		showErrorDialog(e.getLocalizedMessage());
	}

	public void showErrorDialog(String msg) {
		if (!isErrorDialogsActive)
			return;

		JOptionPane.showConfirmDialog(mainComp, msg,
				getPlain("ApplicationName") + " - " + getError("Error"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
	}

	public void showMessage(String message) {
		JOptionPane.showConfirmDialog(mainComp, message,
				getPlain("ApplicationName") + " - " + getMenu("Info"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Downloads a bitmap from the URL and stores it in this application's
	 * imageManager. Michael Borcherds
	 * 
	 * @return fileName of image stored in imageManager
	 * 
	 *         public String getImageFromURL(String url) { try{
	 * 
	 *         BufferedImage img=javax.imageio.ImageIO.read(new URL(url));
	 *         return createImage(img, "bitmap.png");
	 * 
	 *         } catch (Exception e) {return null;} }
	 */

	public void setWaitCursor() {
		mainComp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public void setDefaultCursor() {
		mainComp.setCursor(Cursor.getDefaultCursor());
	}

	public void doAfterRedefine(GeoElement geo) {
		if (appGuiManager != null)
			getGuiManager().doAfterRedefine(geo);
	}

	/*
	 * private methods for display
	 */

	public File getCurrentFile() {
		return currentFile;
	}

	public File getCurrentPath() {
		return currentPath;
	}

	public void setCurrentFile(File file) {
		currentFile = file;
		if (currentFile != null) {
			currentPath = currentFile.getParentFile();
			addToFileList(currentFile);
		}
		updateTitle();
		updateMenubar();
	}

	public static void addToFileList(File file) {
		if (file == null || !file.exists())
			return;

		// add or move fileName to front of list
		fileList.remove(file);
		fileList.addFirst(file);
	}

	public static File getFromFileList(int i) {
		if (fileList.size() > i)
			return (File) fileList.get(i);
		else
			return null;
	}

	public static int getFileListSize() {
		return fileList.size();
	}

	public void updateTitle() {
		if (frame == null)
			return;

		StringBuffer sb = new StringBuffer();
		sb.append("GeoGebra");
		if (currentFile != null) {
			sb.append(" - ");
			sb.append(currentFile.getName());
		} else {
			if (GeoGebra.getInstanceCount() > 1) {
				int nr = frame.getInstanceNumber();
				sb.append(" (");
				sb.append(nr + 1);
				sb.append(")");
			}
		}
		frame.setTitle(sb.toString());
	}

	public void setFontSize(int points) {
		if (points == appFontSize)
			return;
		appFontSize = points;
		isSaved = false;

		resetFonts();

		if (!INITING) {
			if (applet != null)
				SwingUtilities.updateComponentTreeUI(applet);
			if (frame != null)
				SwingUtilities.updateComponentTreeUI(frame);
		}
	}

	private static int getInitFontSize() {
		/*
		 * Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); int
		 * fontsize = (int) (dim.height 0.016); if (fontsize < 10) fontsize =
		 * 10; else if (fontsize > 16) fontsize = 16; return fontsize;
		 */
		return 12;
	}

	public void resetFonts() {
		setLAFFontSize();
		updateFonts();
	}

	public void updateFonts() {
		if (euclidianView != null)
			euclidianView.updateFonts();

		if (appGuiManager != null)
			getGuiManager().updateFonts();

	}

	public Font getBoldFont() {
		return boldFont;
	}

	public Font getPlainFont() {
		return plainFont;
	}

	public Font getSmallFont() {
		return smallFont;
	}

	private void setLAFFontSize() {
		int size = appFontSize;
		// create similar font with the specified size
		FontUIResource plain = new FontUIResource(appFontNameSansSerif,
				Font.PLAIN, size);
		plainFont = plain;
		smallFont = new FontUIResource(appFontNameSansSerif, Font.PLAIN,
				size - 2);
		boldFont = plainFont.deriveFont(Font.BOLD);

		// Dialog
		UIManager.put("ColorChooser.font", plain);
		UIManager.put("FileChooser.font", plain);

		// Panel, Pane, Bars
		UIManager.put("Panel.font", plain);
		UIManager.put("TextPane.font", plain);
		UIManager.put("OptionPane.font", plain);
		UIManager.put("OptionPane.messageFont", plain);
		UIManager.put("OptionPane.buttonFont", plain);
		UIManager.put("EditorPane.font", plain);
		UIManager.put("ScrollPane.font", plain);
		UIManager.put("TabbedPane.font", plain);
		UIManager.put("ToolBar.font", plain);
		UIManager.put("ProgressBar.font", plain);
		UIManager.put("Viewport.font", plain);
		UIManager.put("TitledBorder.font", plain);

		// Buttons
		UIManager.put("Button.font", plain);
		UIManager.put("RadioButton.font", plain);
		UIManager.put("ToggleButton.font", plain);
		UIManager.put("ComboBox.font", plain);
		UIManager.put("CheckBox.font", plain);

		// Menus
		UIManager.put("Menu.font", plain);
		UIManager.put("Menu.acceleratorFont", plain);
		UIManager.put("PopupMenu.font", plain);
		UIManager.put("MenuBar.font", plain);
		UIManager.put("MenuItem.font", plain);
		UIManager.put("MenuItem.acceleratorFont", plain);
		UIManager.put("CheckBoxMenuItem.font", plain);
		UIManager.put("RadioButtonMenuItem.font", plain);

		// Fields, Labels
		UIManager.put("Label.font", plain);
		UIManager.put("Table.font", plain);
		UIManager.put("TableHeader.font", plain);
		UIManager.put("Tree.font", plain);
		UIManager.put("Tree.rowHeight", new Integer(size + 5));
		UIManager.put("List.font", plain);
		UIManager.put("TextField.font", plain);
		UIManager.put("PasswordField.font", plain);
		UIManager.put("TextArea.font", plain);
		UIManager.put("ToolTip.font", plain);
	}

	public int getFontSize() {
		return appFontSize;
	}

	private void setLabels() {
		if (INITING)
			return;

		if (appGuiManager != null)
			getGuiManager().setLabels();
		
		updateCommandDictionary();
	}

	/**
	 * Returns text description for given mode number
	 */
	public String getModeText(int mode) {
		// macro
		if (mode >= EuclidianView.MACRO_MODE_ID_OFFSET) {
			int macroID = mode - EuclidianView.MACRO_MODE_ID_OFFSET;
			try {
				Macro macro = kernel.getMacro(macroID);
				String modeText = macro.getToolName();
				if ("".equals(modeText))
					modeText = macro.getCommandName();
				return modeText;
			} catch (Exception e) {
				Application
						.debug("Application.getModeText(): macro does not exist: ID = "
								+ macroID);
				// e.printStackTrace();
				return "";
			}
		} else
			// standard case
			return getMenu(EuclidianView.getModeText(mode));
	}

	public ImageIcon getModeIcon(int mode) {
		ImageIcon icon;

		Color border = Color.lightGray;

		// macro
		if (mode >= EuclidianView.MACRO_MODE_ID_OFFSET) {
			int macroID = mode - EuclidianView.MACRO_MODE_ID_OFFSET;
			try {
				Macro macro = kernel.getMacro(macroID);
				String iconName = macro.getIconFileName();
				BufferedImage img = getExternalImage(iconName);
				if (img == null)
					// default icon
					icon = getImageIcon("mode_tool_32.png", border);
				else
					// use image as icon
					icon = new ImageIcon(ImageManager.addBorder(img, border));
			} catch (Exception e) {
				Application.debug("macro does not exist: ID = " + macroID);
				return null;
			}
		} else {
			// standard case
			String modeText = EuclidianView.getModeText(mode);
			// bugfix for Turkish locale added Locale.US
			String iconName = "mode_" + modeText.toLowerCase(Locale.US)
					+ "_32.gif";
			icon = getImageIcon(iconName, border);
			if (icon == null) {
				Application.debug("icon missing for mode " + modeText + " ("
						+ mode + ")");
			}
		}
		return icon;
	}

	public void setSplitDividerLocationHOR(int loc) {
		initSplitDividerLocationHOR = loc;
		// debug(loc+"");
	}

	public void setSplitDividerLocationVER(int loc) {
		initSplitDividerLocationVER = loc;
		// debug(loc+"");
	}

	public void setSplitDividerLocationHOR2(int loc) {
		initSplitDividerLocationHOR2 = loc;
		// debug(loc+"");
	}

	public void setSplitDividerLocationVER2(int loc) {
		initSplitDividerLocationVER2 = loc;
		// debug(loc+"");
	}

	public void setHorizontalSplit(boolean flag) {
		if (flag == horizontalSplit)
			return;

		horizontalSplit = flag;
		if (sp == null)
			return;
		if (flag) {
			sp.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		} else {
			sp.setOrientation(JSplitPane.VERTICAL_SPLIT);
		}
	}

	public boolean isHorizontalSplit() {
		return horizontalSplit;
	}

	public void setShowAlgebraView(boolean flag) {
		if (showAlgebraView == flag)
			return;

		showAlgebraView = flag;
		if (showAlgebraView) {
			getGuiManager().attachAlgebraView();
			getGuiManager().setShowAuxiliaryObjects(showAuxiliaryObjects);
		} else {
			if (hasGuiManager())
			getGuiManager().detachAlgebraView();
		}

		updateMenubar();
		isSaved = false;
	}

	public void setShowSpreadsheetView(boolean flag) {
		if (showSpreadsheet == flag)
			return;

		showSpreadsheet = flag;
		if (showSpreadsheet) {
			getGuiManager().attachSpreadsheetView();
		} else {
			getGuiManager().detachSpreadsheetView();
		}

		updateMenubar();
		isSaved = false;
	}

	public boolean showCasView() {
		return showCAS;
	}

	public void setShowCasView(boolean flag) {
		if (showCAS == flag)
			return;
		showCAS = flag;

		if (casView == null) {
			loadCASJar();
			
			// this code wraps the creation of the cas view and is
			// necessary to allow dynamic loading of this class
			ActionListener al = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					casView = new geogebra.cas.view.CASView(Application.this);					
				}				
			};			
			al.actionPerformed(null);	
			
			// create JFrame for CAS view
			casFrame = createCasFrame(casView);
		}

		// show or hide CAS window
		
		casFrame.setVisible(showCAS);

		updateMenubar();
		isSaved = false;
	}

	private JFrame casFrame;

	public JComponent getCasView() {
		return casView;
	}

	private static JFrame createCasFrame(JComponent casView) {
		JFrame spFrame = new JFrame();
		Container contentPane = spFrame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(casView, BorderLayout.CENTER);
		spFrame.setBackground(Color.white);
		spFrame.setResizable(true);
		spFrame.setTitle("GeoGebra CAS");
		spFrame.pack();
		return spFrame;
	}

	final public boolean showAlgebraView() {
		return showAlgebraView;
	}

	// Michael Borcherds 2008-01-14
	final public boolean showSpreadsheetView() {
		return showSpreadsheet;
	}

	public boolean showAlgebraInput() {
		return showAlgebraInput;
	}

	public void setShowAlgebraInput(boolean flag) {
		showAlgebraInput = flag;
		updateMenubar();
	}

	public boolean showCmdList() {
		return showCmdList;
	}

	public void setShowCmdList(boolean flag) {
		if (showCmdList == flag)
			return;

		showCmdList = flag;
		getGuiManager().updateAlgebraInput();
		updateMenubar();
	}

	/**
	 * Displays the construction protocol navigation
	 */
	public void setShowConstructionProtocolNavigation(boolean flag) {
		if (flag == showConsProtNavigation)
			return;
		showConsProtNavigation = flag;
		
		getGuiManager().setShowConstructionProtocolNavigation(flag);			
		updateMenubar();
	}

	public boolean showConsProtNavigation() {
		return showConsProtNavigation;
	}

	public boolean showAuxiliaryObjects() {
		return showAuxiliaryObjects;
	}

	public void setShowAuxiliaryObjects(boolean flag) {
		showAuxiliaryObjects = flag;

		if (showAlgebraView)
			getGuiManager().setShowAuxiliaryObjects(flag);
		updateMenubar();
	}

	public void setShowMenuBar(boolean flag) {
		showMenuBar = flag;
	}

	public void setShowToolBar(boolean toolbar, boolean help) {
		showToolBar = toolbar;

		if (showToolBar) {
			getGuiManager().setShowToolBarHelp(help);
		}
	}

	public boolean showToolBar() {
		return showToolBar;
	}

	public boolean showMenuBar() {
		return showMenuBar;
	}

	public void setUndoActive(boolean flag) {
		undoActive = flag;
		kernel.setUndoActive(flag);

		if (appGuiManager != null)
			getGuiManager().updateActions();

		isSaved = true;
	}

	public boolean isUndoActive() {
		return undoActive;
	}

	/**
	 * Enables or disables right clicking in this application. This is useful
	 * for applets.
	 */
	public void setRightClickEnabled(boolean flag) {
		rightClickEnabled = flag;
	}

	final public boolean isRightClickEnabled() {
		return rightClickEnabled;
	}

	public boolean letRename() {
		return true;
	}

	public boolean letDelete() {
		return true;
	}

	public boolean letRedefine() {
		return true;
	}

	public boolean letShowPopupMenu() {
		return rightClickEnabled;
	}

	public boolean letShowPropertiesDialog() {
		return rightClickEnabled;
	}

	public void updateToolBar() {
		if (!showToolBar)
			return;

		getGuiManager().updateToolbar();

		if (!INITING) {
			if (applet != null)
				SwingUtilities.updateComponentTreeUI(applet);
			if (frame != null)
				SwingUtilities.updateComponentTreeUI(frame);
		}

		setMoveMode();
	}

	public void updateMenubar() {
		if (!showMenuBar || !hasGuiManager())
			return;

		getGuiManager().updateMenubar();
		getGuiManager().updateActions();
		System.gc();
	}

	private void updateSelection() {
		if (!showMenuBar || !hasGuiManager())
			return;

		getGuiManager().updateMenubarSelection();
	}

	public void updateMenuWindow() {
		if (!showMenuBar || !hasGuiManager())
			return;

		getGuiManager().updateMenuWindow();
		getGuiManager().updateMenuFile();
		System.gc();
	}

	public void updateCommandDictionary() {
		// make sure all macro commands are in dictionary
		fillCommandDict();

		if (appGuiManager != null) {
			getGuiManager().updateAlgebraInput();
		}
	}

	/**
	 * // think about this Downloads the latest jar files from the
	 * GeoGebra server.
	 * 
	 * private void updateGeoGebra() { try { File dest = new File(codebase +
	 * Application.JAR_FILE); URL jarURL = new URL(Application.UPDATE_URL +
	 * Application.JAR_FILE);
	 * 
	 * if (dest.exists()) { // check if jarURL is newer then dest try {
	 * URLConnection connection = jarURL.openConnection(); if
	 * (connection.getLastModified() <= dest.lastModified()) { 
	 * showMessage("No update available"); return; }
	 * 
	 * } catch (Exception e) { // we don't know if the file behind jarURL is
	 * newer than dest // so don't do anything 
	 * showMessage("No update available: " + (e.getMessage())); return; } }
	 * 
	 * // copy JAR_FILE if (!CopyURLToFile.copyURLToFile(this, jarURL, dest))
	 * return;
	 * 
	 * // copy properties file dest = new File(codebase +
	 * Application.PROPERTIES_FILE); jarURL = new URL(Application.UPDATE_URL +
	 * Application.PROPERTIES_FILE); if (!CopyURLToFile.copyURLToFile(this,
	 * jarURL, dest)) return;
	 * 
	 * // copy jscl file dest = new File(codebase + Application.JSCL_FILE);
	 * jarURL = new URL(Application.UPDATE_URL + Application.JSCL_FILE); if
	 * (!CopyURLToFile.copyURLToFile(this, jarURL, dest)) return;
	 * 
	 * 
	 * showMessage("Update finished. Please restart GeoGebra."); } catch
	 * (Exception e) {  showError("Update failed: "+
	 * e.getMessage()); } }
	 */

	public void deleteAllGeoElements() {
		// delete all
		Object[] geos = kernel.getConstruction().getGeoSetConstructionOrder()
				.toArray();
		if (geos.length == 0)
			return;

		if (isSaved() || saveCurrentFile()) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (geo.isLabelSet())
					geo.remove();
			}
			kernel.initUndoInfo();
			setCurrentFile(null);
			setMoveMode();
		}

		/*
		 * if (isSaved() || saveCurrentFile()) { clearAll();
		 * setCurrentFile(null); updateMenubar(); }
		 */
	}

	public void exit() {
		// glassPane is active: don't exit now!
		if (glassPaneListener != null)
			return;

		if (isSaved() || applet != null || saveCurrentFile()) {
			if (applet != null) {
				setApplet(applet);
				applet.showApplet();
			} else {
				frame.setVisible(false);
			}
		}
	}

	public synchronized void exitAll() {
		// glassPane is active: don't exit now!
		if (glassPaneListener != null)
			return;

		ArrayList insts = GeoGebra.getInstances();
		GeoGebra[] instsCopy = new GeoGebra[insts.size()];
		for (int i = 0; i < instsCopy.length; i++) {
			instsCopy[i] = (GeoGebra) insts.get(i);
		}

		for (int i = 0; i < instsCopy.length; i++) {
			instsCopy[i].getApplication().exit();
		}
	}

	// returns true for YES or NO and false for CANCEL
	public boolean saveCurrentFile() {
		return getGuiManager().saveCurrentFile();
	}

	/*
	 * public void updateStatusLabelAxesRatio() { if (statusLabelAxesRatio !=
	 * null) statusLabelAxesRatio.setText(
	 * euclidianView.getXYscaleRatioString()); }
	 */

	public void setMode(int mode) {
		currentSelectionListener = null;

		if (euclidianView != null)
			euclidianView.setMode(mode);

		if (appGuiManager != null)
			getGuiManager().setMode(mode);

	}

	public int getMode() {
		return euclidianView.getMode();
	}

	/***********************************
	 * SAVE / LOAD methodes
	 ***********************************/

	/**
	 * Loads construction file
	 * 
	 * @return true if successful
	 */
	final public boolean loadXML(File file, boolean isMacroFile) {
		try {

			FileInputStream fis = null;
			fis = new FileInputStream(file);
			boolean success = loadXML(fis, isMacroFile);
			if (success && !isMacroFile) {
				setCurrentFile(file);
			}
			return success;
		} catch (Exception e) {
			setCurrentFile(null);
			e.printStackTrace();
			showError(getError("LoadFileFailed") + ":\n" + file);
			return false;
		}
	}

	/**
	 * Loads construction file from URL
	 * 
	 * @return true if successful
	 */
	final public boolean loadXML(URL url, boolean isMacroFile) {
		try {
			return loadXML(url.openStream(), isMacroFile);
		} catch (Exception e) {
			setCurrentFile(null);
			e.printStackTrace();
			showError(getError("LoadFileFailed") + ":\n" + url);
			return false;
		}
	}

	private boolean loadXML(InputStream is, boolean isMacroFile)
			throws Exception {
		try {
			if (!isMacroFile) {
				setMoveMode();
			}

			BufferedInputStream bis = new BufferedInputStream(is);
			myXMLio.readZipFromInputStream(bis, isMacroFile);
			is.close();
			bis.close();

			if (!isMacroFile) {
				kernel.initUndoInfo();
				isSaved = true;
				setCurrentFile(null);
			}

			// command list may have changed due to macros
			updateCommandDictionary();
			return true;
		} catch (MyError err) {
			setCurrentFile(null);
			showError(err);
			return false;
		}
	}

	/**
	 * Saves all objects.
	 * 
	 * @return true if successful
	 */
	final public boolean saveGeoGebraFile(File file) {
		try {
			setWaitCursor();
			myXMLio.writeGeoGebraFile(file);
			isSaved = true;
			setDefaultCursor();
			return true;
		} catch (Exception e) {
			setDefaultCursor();
			showError("SaveFileFailed");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Saves given macros to file.
	 * 
	 * @return true if successful
	 */
	final public boolean saveMacroFile(File file, ArrayList macros) {
		try {
			setWaitCursor();
			myXMLio.writeMacroFile(file, macros);
			setDefaultCursor();
			return true;
		} catch (Exception e) {
			setDefaultCursor();
			showError("SaveFileFailed");
			e.printStackTrace();
			return false;
		}
	}

	// FKH 20040826
	public String getXML() {
		return myXMLio.getFullXML();
	}

	public void setXML(String xml, boolean clearAll) {
		setCurrentFile(null);

		try {
			myXMLio.processXMLString(xml, clearAll, false);
		} catch (MyError err) {
			err.printStackTrace();
			showError(err);
		} catch (Exception e) {
			e.printStackTrace();
			showError("LoadFileFailed");
		}
	}

	// endFKH

	public String getPreferencesXML() {
		return myXMLio.getPreferencesXML();
	}

	public byte[] getMacroFileAsByteArray() {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			myXMLio.writeMacroStream(os, kernel.getAllMacros());
			os.flush();
			return os.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void loadMacroFileFromByteArray(byte[] byteArray,
			boolean removeOldMacros) {
		try {
			if (removeOldMacros)
				kernel.removeAllMacros();

			if (byteArray != null) {
				ByteArrayInputStream is = new ByteArrayInputStream(byteArray);
				myXMLio.readZipFromInputStream(is, true);
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	final public MyXMLio getXMLio() {
		return myXMLio;
	}

	public boolean isSaved() {
		return isSaved;
	}

	public void storeUndoInfo() {
		if (undoActive) {
			kernel.storeUndoInfo();
			updateMenubar();
			isSaved = false;
		}
	}

	public void restoreCurrentUndoInfo() {
		if (undoActive) {
			kernel.restoreCurrentUndoInfo();
			updateMenubar();
			isSaved = false;
		}
	}

	/*
	 * final public void clearAll() { // load preferences
	 * GeoGebraPreferences.loadXMLPreferences(this); updateContentPane();
	 * 
	 * // clear construction kernel.clearConstruction(); kernel.initUndoInfo();
	 * 
	 * isSaved = true; System.gc(); }
	 */

	/**
	 * Returns gui settings in XML format
	 */
	public String getGUItagXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<gui>\n");

		sb.append("\t<show");
		sb.append(" algebraView=\"");
		sb.append(showAlgebraView);

		// Michael Borcherds 2008-04-25
		sb.append("\" spreadsheetView=\"");
		sb.append(showSpreadsheet);

		sb.append("\" auxiliaryObjects=\"");
		sb.append(showAuxiliaryObjects);
		sb.append("\" algebraInput=\"");
		sb.append(showAlgebraInput);
		sb.append("\" cmdList=\"");
		sb.append(showCmdList);
		sb.append("\"/>\n");

		if (sp != null) {
			sb.append("\t<splitDivider");
			sb.append(" loc=\"");
			sb.append(initSplitDividerLocationHOR);
			sb.append("\" locVertical=\"");
			sb.append(initSplitDividerLocationVER);
			sb.append("\" loc2=\""); // bugfix Michael Borcherds 2008-04-24
										// added \" at start
			sb.append(initSplitDividerLocationHOR2);
			sb.append("\" locVertical2=\"");
			sb.append(initSplitDividerLocationVER2);
			sb.append("\" horizontal=\"");
			sb.append(sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT);
			sb.append("\"/>\n");
		}

		// save custom toolbar if we have one
		if (appGuiManager != null) {
			String cusToolbar = getGuiManager()
					.getCustomToolbarDefinition();

			if (cusToolbar != null
					&& !cusToolbar.equals(getGuiManager()
							.getDefaultToolbarString())) {
				sb.append("\t<toolbar");
				sb.append(" str=\"");
				sb.append(cusToolbar);
				sb.append("\"/>\n");
			}
		}

		// labeling style
		if (labelingStyle != ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC) {
			sb.append("\t<labelingStyle ");
			sb.append(" val=\"");
			sb.append(labelingStyle);
			sb.append("\"/>\n");
		}

		sb.append("\t<font ");
		sb.append(" size=\"");
		sb.append(appFontSize);
		sb.append("\"/>\n");

		sb.append(getConsProtocolXML());

		sb.append("</gui>\n");

		return sb.toString();
	}

	public String getCompleteUserInterfaceXML() {
		StringBuffer sb = new StringBuffer();

		// save gui tag settings
		sb.append(getGUItagXML());

		// save euclidianView settings
		sb.append(getEuclidianView().getXML());

		// save spreadsheetView settings
		if (showSpreadsheet) {
			getGuiManager().getSpreadsheetViewXML();
		}

		// save cas view seeting and cas session
		if (casView != null) {
			sb.append(((geogebra.cas.view.CASView) casView).getGUIXML());
			sb.append(((geogebra.cas.view.CASView) casView).getSessionXML());
		}

		return sb.toString();
	}

	public String getConsProtocolXML() {
		if (appGuiManager == null)
			return "";

		StringBuffer sb = new StringBuffer();

		// construction protocol
		if (getGuiManager().isUsingConstructionProtocol()) {
			sb.append(getGuiManager().getConsProtocolXML());
		}


		return sb.toString();
	}

	/**
	 * Returns the CodeBase URL as String.
	 */
	public URL getCodeBase() {
		return codebase;
	}

	private void initCodeBase() {
		try {
			if (applet != null) {
				codebase = applet.getCodeBase();
			} else {
				String path = Application.class.getProtectionDomain()
						.getCodeSource().getLocation().toExternalForm();
				if (path.endsWith(JAR_FILES[0])) // remove "geogebra.jar" from
													// end
					path = path.substring(0, path.length()
							- JAR_FILES[0].length());
				codebase = new URL(path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Application.debug("codebase: " + codebase);
	}

	/*  selection handling */

	final public int selectedGeosSize() {
		return selectedGeos.size();
	}

	final public ArrayList getSelectedGeos() {
		return selectedGeos;
	}

	final public GeoElement getLastCreatedGeoElement() {
		return kernel.getConstruction().getLastGeoElement();
	}

	/**
	 * geos must contain GeoElement objects only.
	 * 
	 * @param geos
	 */
	final public void setSelectedGeos(ArrayList geos) {
		clearSelectedGeos(false);
		if (geos != null) {
			for (int i = 0; i < geos.size(); i++) {
				GeoElement geo = (GeoElement) geos.get(i);
				addSelectedGeo(geo, false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	/*
	 * Michael Borcherds 2008-03-03 modified to select all of a layer pass
	 * layer==-1 to select all objects
	 */
	final public void selectAll(int layer) {
		clearSelectedGeos(false);

		Iterator it = kernel.getConstruction().getGeoSetLabelOrder().iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();
			if (layer == -1 || geo.getLayer() == layer)
				addSelectedGeo(geo, false);
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	final public void selectAllPredecessors() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = (GeoElement) selectedGeos.get(i);
			TreeSet tree = geo.getAllPredecessors();
			Iterator it2 = tree.iterator();
			while (it2.hasNext())
				addSelectedGeo((GeoElement) it2.next(), false);
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	final public void selectAllDescendants() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = (GeoElement) selectedGeos.get(i);
			Set tree = geo.getAllChildren();
			Iterator it2 = tree.iterator();
			while (it2.hasNext())
				addSelectedGeo((GeoElement) it2.next(), false);
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	final public void clearSelectedGeos() {
		clearSelectedGeos(true);
		updateSelection();
	}

	public void clearSelectedGeos(boolean repaint) {
		int size = selectedGeos.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				GeoElement geo = (GeoElement) selectedGeos.get(i);
				geo.setSelected(false);
			}
			selectedGeos.clear();
			if (repaint)
				kernel.notifyRepaint();
		}
		updateSelection();
	}

	/**
	 * @param element
	 */
	final public void toggleSelectedGeo(GeoElement geo) {
		toggleSelectedGeo(geo, true);
	}

	final public void toggleSelectedGeo(GeoElement geo, boolean repaint) {
		if (geo == null)
			return;

		boolean contains = selectedGeos.contains(geo);
		if (contains) {
			selectedGeos.remove(geo);
			geo.setSelected(false);
		} else {
			selectedGeos.add(geo);
			geo.setSelected(true);
		}

		if (repaint)
			kernel.notifyRepaint();
		updateSelection();
	}

	final public boolean containsSelectedGeo(GeoElement geo) {
		return selectedGeos.contains(geo);
	}

	final public void removeSelectedGeo(GeoElement geo) {
		removeSelectedGeo(geo, true);
	}

	final public void removeSelectedGeo(GeoElement geo, boolean repaint) {
		if (geo == null)
			return;

		selectedGeos.remove(geo);
		geo.setSelected(false);
		if (repaint)
			kernel.notifyRepaint();
		updateSelection();
	}

	final public void addSelectedGeo(GeoElement geo) {
		addSelectedGeo(geo, true);
	}

	final public void addSelectedGeo(GeoElement geo, boolean repaint) {
		if (geo == null || selectedGeos.contains(geo))
			return;

		selectedGeos.add(geo);
		geo.setSelected(true);
		if (repaint)
			kernel.notifyRepaint();
		updateSelection();
	}

	// remember split divider location
	public class DividerChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			Number value = (Number) e.getNewValue();
			int newDivLoc = value.intValue();

			// first split pane
			if (e.getSource() == sp) {
				if (sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
					setSplitDividerLocationHOR(newDivLoc);
				else
					setSplitDividerLocationVER(newDivLoc);
				isSaved = false;
			}
			// second split pane
			else if (e.getSource() == sp2) {
				if (sp2.getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
					setSplitDividerLocationHOR2(newDivLoc);
				else
					setSplitDividerLocationVER2(newDivLoc);
				isSaved = false;
			}

			if (applet != null)
				SwingUtilities.updateComponentTreeUI(applet);

			

		}
	}

	/* Event dispatching */
	private GlassPaneListener glassPaneListener;

	public void startDispatchingEventsTo(JComponent comp) {
		if (appGuiManager != null) {
			getGuiManager().closeOpenDialogs();
		}

		if (glassPaneListener == null) {
			Component glassPane = getGlassPane();
			glassPaneListener = new GlassPaneListener(glassPane,
					getContentPane(), comp);

			// mouse
			glassPane.addMouseListener(glassPaneListener);
			glassPane.addMouseMotionListener(glassPaneListener);

			// keys
			KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.addKeyEventDispatcher(glassPaneListener);

			glassPane.setVisible(true);
		}
	}

	public void stopDispatchingEvents() {
		if (glassPaneListener != null) {
			Component glassPane = getGlassPane();
			glassPane.removeMouseListener(glassPaneListener);
			glassPane.removeMouseMotionListener(glassPaneListener);

			KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.removeKeyEventDispatcher(glassPaneListener);

			glassPane.setVisible(false);
			glassPaneListener = null;
		}
	}

	public Component getGlassPane() {
		if (mainComp == applet)
			return applet.getGlassPane();
		else if (mainComp == frame)
			return frame.getGlassPane();
		else
			return null;
	}

	public Container getContentPane() {
		if (mainComp == applet)
			return applet.getContentPane();
		else if (mainComp == frame)
			return frame.getContentPane();
		else
			return null;
	}

	/*
	 * KeyEventDispatcher implementation to handle key events globally for the
	 * application
	 */
	public boolean dispatchKeyEvent(KeyEvent e) {
		// make sure the event is not consumed
		if (e.isConsumed())
			return true;

		// if the glass pane is visible, don't do anything
		// (there might be an animation running)
		if (getGlassPane().isVisible())
			return false;

		// only handle key events of this mainComponent
		Component rootComp = SwingUtilities.getRoot(e.getComponent());
		if (rootComp != mainComp) {
			// ESC for dialog: close it
			if (rootComp instanceof JDialog
					&& e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				((JDialog) rootComp).setVisible(false);
				return true;
			}
			return false;
		}

		// let gui manager handle this
		if (appGuiManager != null)
			return getGuiManager().dispatchKeyEvent(e);

		return false;
	}

	public boolean isPrintScaleString() {
		return printScaleString;
	}

	public void setPrintScaleString(boolean printScaleString) {
		this.printScaleString = printScaleString;
	}

	public File getCurrentImagePath() {
		return currentImagePath;
	}

	public void setCurrentImagePath(File currentImagePath) {
		this.currentImagePath = currentImagePath;
	}

	/**
	 * Copies all jar files of this application to the given directory
	 * 
	 * @param destDir
	 */
	public void copyJarsTo(String destDir, boolean includeExportJar)
			throws Exception {
		// try to copy from temp dir
		File jarDir = jarmanager.getLocalJarDir();
		URL srcDir = jarDir.toURL();

		// Application.debug("temp jar file: " + tempJarFile);
		// Application.debug("   exists " + tempJarFile.exists());

		// copy jar files to tempDir
		for (int i = 0; i < JAR_FILES.length; i++) {
			if (!includeExportJar && i == JAR_FILE_GEOGEBRA_EXPORT)
				continue;

			File dest = new File(destDir, JAR_FILES[i]);
			URL src = new URL(srcDir, JAR_FILES[i]);
			CopyURLToFile.copyURLToFile(src, dest);
		}

		// Application.debug("copied geogebra jar files from " + srcDir + " to "
		// + destDir);
	}

	public final boolean isErrorDialogsActive() {
		return isErrorDialogsActive;
	}

	public final void setErrorDialogsActive(boolean isErrorDialogsActive) {
		this.isErrorDialogsActive = isErrorDialogsActive;
	}

	public final boolean isShiftDragZoomEnabled() {
		return shiftDragZoomEnabled;
	}

	public final void setShiftDragZoomEnabled(boolean shiftDragZoomEnabled) {
		this.shiftDragZoomEnabled = shiftDragZoomEnabled;
	}

	/**
	 * PluginManager gets API with this H-P Ulven 2008-04-16
	 */
	public GgbAPI getGgbApi() {
		if (ggbapi == null) {
			ggbapi = new GgbAPI(this);	
		}
		
		return ggbapi;
	}

	/*
	 * GgbAPI needs this H-P Ulven 2008-05-25
	 */
	public PluginManager getPluginManager() {
		if (pluginmanager == null) {
			pluginmanager = new PluginManager(this);
		}		
		return pluginmanager;
	}// getPluginManager()

	// Michael Borcherds 2008-06-22
	public static void debug(String s) {
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();

		// String calleeMethod = elements[0].getMethodName();
		String callerMethodName = elements[1].getMethodName();
		String callerClassName = elements[1].getClassName();

		// Application.debug("CallerClassName=" + callerClassName +
		// " , Caller method name: " + callerMethodName);
		// Application.debug("Callee method name: " + calleeMethod);

		Calendar calendar = new GregorianCalendar();

		int min = calendar.get(Calendar.MINUTE);
		String minS = (min < 10) ? "0" + min : "" + min;

		int sec = calendar.get(Calendar.SECOND);
		String secS = (sec < 10) ? "0" + sec : "" + sec;

		String srcStr = "[" + callerClassName + "." + callerMethodName
				+ "] at " + calendar.get(Calendar.HOUR) + ":" + minS + ":"
				+ secS;

		// multi line message
		if (s.indexOf("\n") > -1) {
			System.out.println("*** BEGIN Message from " + srcStr);
			System.out.println(s);
			System.out.println("*** END Message from " + srcStr + "\n");
		}
		// one line message
		else {
			System.out.println("*** Message from " + srcStr);
			System.out.println("  " + s + "\n");
		}

	}

	// Michael Borcherds 2008-06-22
	public static void printStacktrace(String message) {
		try {

			throw new Exception(message);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	// check if we are on a mac
	public static boolean MAC_OS = System.getProperty("os.name").toLowerCase(
			Locale.US).startsWith("mac");
	public static boolean WINDOWS = System.getProperty("os.name").toLowerCase(
			Locale.US).startsWith("windows"); // Michael Borcherds 2008-03-21

	public static boolean isControlDown(InputEvent e) {

		/*
		 * debug("isMetaDown = "+e.isMetaDown());
		 * debug("isControlDown = "+e.isControlDown());
		 * debug("isShiftDown = "+e.isShiftDown());
		 * debug("isAltDown = "+e.isAltDown());
		 * debug("isAltGrDown = "+e.isAltGraphDown());
		 * debug("fakeRightClick = "+fakeRightClick);
		 */

		if (fakeRightClick)
			return false;

		boolean ret = (MAC_OS && e.isMetaDown()) // Mac: meta down for multiple
													// selection
				|| (!MAC_OS && e.isControlDown()); // non-Mac: Ctrl down for
													// multiple selection

		// debug("isPopupTrigger = "+e.isPopupTrigger());
		// debug("ret = " + ret);
		return ret;
		// return e.isControlDown();
	}

	private static boolean fakeRightClick = false;

	public static boolean isRightClick(MouseEvent e) {

		// right-click returns isMetaDown on MAC_OS
		// so we want to return true for isMetaDown
		// if it occurred first at the same time as
		// a popup trigger
		if (MAC_OS && !e.isMetaDown())
			fakeRightClick = false;

		if (MAC_OS && e.isPopupTrigger() && e.isMetaDown())
			fakeRightClick = true;

		/*
		 * debug("isMetaDown = "+e.isMetaDown());
		 * debug("isControlDown = "+e.isControlDown());
		 * debug("isShiftDown = "+e.isShiftDown());
		 * debug("isAltDown = "+e.isAltDown());
		 * debug("isAltGrDown = "+e.isAltGraphDown());
		 * debug("isPopupTrigger = "+e.isPopupTrigger());
		 * debug("fakeRightClick = "+fakeRightClick);
		 */

		if (fakeRightClick)
			return true;

		boolean ret =
		// e.isPopupTrigger() ||
		(MAC_OS && e.isControlDown()) // Mac: ctrl click = right click
				|| (!MAC_OS && e.isMetaDown()); // non-Mac: right click = meta
												// click

		// debug("ret = " + ret);
		return ret;
		// return e.isMetaDown();
	}

	public final String getAppFontNameSansSerif() {
		return appFontNameSansSerif;
	}

	public final String getAppFontNameSerif() {
		return appFontNameSerif;
	}

	// used by PropertyDialogGeoElement and MenuBarImpl
	// for the Rounding Menus
	final public static int roundingMenuLookup[] = { 0, 1, 2, 3, 4, 5, 10, 15,
			-1, 3, 5, 10, 15 };
	final public static int decimalsLookup[] = { 0, 1, 2, 3, 4, 5, -1, -1, -1,
			-1, 6, -1, -1, -1, -1, 7 };
	final public static int figuresLookup[] = { -1, -1, -1, 9, -1, 10, -1, -1,
			-1, -1, 11, -1, -1, -1, -1, 12 };

	public String[] getRoundingMenu() {
		String[] strDecimalSpaces = {
				getPlain("ADecimalPlaces", "0"),
				getPlain("ADecimalPlace", "1"),
				getPlain("ADecimalPlaces", "2"),
				getPlain("ADecimalPlaces", "3"),
				getPlain("ADecimalPlaces", "4"),
				getPlain("ADecimalPlaces", "5"),
				getPlain("ADecimalPlaces", "10"),
				getPlain("ADecimalPlaces", "15"),
				"---", // separator
				getPlain("ASignificantFigures", "3"),
				getPlain("ASignificantFigures", "5"),
				getPlain("ASignificantFigures", "10"),
				getPlain("ASignificantFigures", "15") };

		// zero is singular in eg French
		if (!isZeroPlural(getLocale()))
			strDecimalSpaces[0] = getPlain("ADecimalPlace", "0");

		return strDecimalSpaces;
	}

	final public static String[] strDecimalSpacesAC = { "0 decimals",
			"1 decimals", "2 decimals", "3 decimals", "4 decimals",
			"5 decimals", "10 decimals", "15 decimals", "", "3 figures",
			"5 figures", "10 figures", "15 figures" };

	// Rounding Menus end

	public void deleteSelectedObjects() {
		if (letDelete()) {
			Object[] geos = getSelectedGeos().toArray();
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				geo.remove();
			}
			storeUndoInfo();
		}

	}

	/**
	 * stores an image in the application's imageManager.
	 * 
	 * @return fileName of image stored in imageManager
	 */
	public String createImage(BufferedImage img, String fileName) {
		try {
			// Michael Borcherds 2007-12-10 START moved MD5 code from GeoImage
			// to here
			String zip_directory = "";
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				if (img == null)
					Application.debug("image==null");
				ImageIO.write(img, "png", baos);
				byte[] fileData = baos.toByteArray();

				MessageDigest md;
				md = MessageDigest.getInstance("MD5");
				byte[] md5hash = new byte[32];
				md.update(fileData, 0, fileData.length);
				md5hash = md.digest();
				zip_directory = convertToHex(md5hash);
			} catch (Exception e) {
				Application.debug("MD5 Error");
				zip_directory = "images";
				// e.printStackTrace();
			}

			String fn = fileName;
			int index = fileName.lastIndexOf(File.separator);
			if (index != -1)
				fn = fn.substring(index + 1, fn.length()); // filename without
			// path
			fn = Util.processFilename(fn);

			// filename will be of form
			// "a04c62e6a065b47476607ac815d022cc\liar.gif"
			fileName = zip_directory + File.separator + fn;

			// Michael Borcherds 2007-12-10 END

			// write and reload image to make sure we can save it
			// without problems
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			myXMLio.writeImageToStream(os, fileName, img);
			os.flush();
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

			// reload the image
			img = ImageIO.read(is);
			is.close();
			os.close();

			setDefaultCursor();
			if (img == null) {
				showError("LoadFileFailed");
				return null;
			}

			// make sure this filename is not taken yet
			BufferedImage oldImg = imageManager.getExternalImage(fileName);
			if (oldImg != null) {
				// image with this name exists already
				if (oldImg.getWidth() == img.getWidth()
						&& oldImg.getHeight() == img.getHeight()) {
					// same size and filename => we consider the images as equal
					return fileName;
				} else {
					// same name but different size: change filename
					// Michael Borcherds: this bit of code should now be
					// redundant as it
					// is near impossible for the filename to be the same unless
					// the files are the same
					int n = 0;
					do {
						n++;
						int pos = fileName.lastIndexOf('.');
						String firstPart = pos > 0 ? fileName.substring(0, pos)
								: "";
						String extension = pos < fileName.length() ? fileName
								.substring(pos) : "";
						fileName = firstPart + n + extension;
					} while (imageManager.getExternalImage(fileName) != null);
				}
			}

			imageManager.addExternalImage(fileName, img);

			return fileName;
		} catch (Exception e) {
			setDefaultCursor();
			e.printStackTrace();
			showError("LoadFileFailed");
			return null;
		}
	}

	// code from freenet
	// http://emu.freenetproject.org/pipermail/cvs/2007-June/040186.html
	// GPL2
	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public static String getExtension(File file) {
		String fileName = file.getName();
		int dotPos = fileName.lastIndexOf('.');

		if (dotPos <= 0 || dotPos == fileName.length() - 1)
			return "";
		else
			return fileName.substring(dotPos + 1).toLowerCase(Locale.US); // Michael
																			// Borcherds
																			// 2008
																			// -
																			// 02
																			// -
																			// 06
																			// added
																			// .
																			// toLowerCase
																			// (
																			// Locale
																			// .
																			// US
																			// )
	}

	public static File addExtension(File file, String fileExtension) {
		if (file == null)
			return null;
		if (getExtension(file).equals(fileExtension))
			return file;
		else
			return new File(file.getParentFile(), // path
					file.getName() + '.' + fileExtension); // filename
	}

	public static File removeExtension(File file) {
		if (file == null)
			return null;
		String fileName = file.getName();
		int dotPos = fileName.indexOf('.');

		if (dotPos <= 0)
			return file;
		else
			return new File(file.getParentFile(), // path
					fileName.substring(0, dotPos));
	}

	public final LowerCaseDictionary getCommandDictionary() {
		return commandDict;
	}
	
}
