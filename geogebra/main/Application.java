/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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
package geogebra.main;

import geogebra.CommandLineArguments;
import geogebra.GeoGebra;
import geogebra.cas.jacomax.JacomaxAutoConfigurator;
import geogebra.cas.jacomax.MaximaConfiguration;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.export.WorksheetExportDialog;
import geogebra.gui.GuiManager;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.inputbar.AlgebraInput;
import geogebra.gui.inputbar.InputBarHelpPanel;
import geogebra.gui.util.ImageSelection;
import geogebra.io.MyXMLio;
import geogebra.io.layout.Perspective;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.kernel.Relation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.plugin.GgbAPI;
import geogebra.plugin.PluginManager;
import geogebra.plugin.ScriptManager;
import geogebra.sound.MidiManager;
import geogebra.util.DownloadManager;
import geogebra.util.ImageManager;
import geogebra.util.LowerCaseDictionary;
import geogebra.util.Util;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;

public class Application implements KeyEventDispatcher {
	
	// disabled parts
	private static final boolean PRINT_DEBUG_MESSAGES = true;
	
	// license file
	public static final String LICENSE_FILE = "/geogebra/gui/_license.txt";

	// jar file names
	public final static String CAS_JAR_NAME = "geogebra_cas.jar";
	public final static String JAVASCRIPT_JAR_NAME = "geogebra_javascript.jar";
	public static final String[] JAR_FILES = { 
			"geogebra.jar", 
			"geogebra_main.jar",
			"geogebra_gui.jar", 
			CAS_JAR_NAME, 
			"geogebra_algos.jar",
			"geogebra_export.jar",
			JAVASCRIPT_JAR_NAME, // don't put at end (sometimes omitted, see WorksheetExportDialog)
			"jlatexmath.jar", // LaTeX
			"jlm_greek.jar", // Greek Unicode codeblock (for LaTeX texts)
			"jlm_cyrillic.jar",  // Cyrillic Unicode codeblock (for LaTeX texts)
			"geogebra_properties.jar" };
	
	public static final String LOADING_GIF = "http://www.geogebra.org/webstart/loading.gif";

	// supported GUI languages (from properties files)
	public static ArrayList<Locale> supportedLocales = new ArrayList<Locale>();
	static {
		supportedLocales.add(new Locale("sq")); // Albanian
		
		supportedLocales.add(new Locale("ar")); // Arabic
		supportedLocales.add(new Locale("eu")); // Basque
		supportedLocales.add(new Locale("bs")); // Bosnian
		supportedLocales.add(new Locale("bg")); // Bulgarian
		supportedLocales.add(new Locale("ca")); // Catalan
		supportedLocales.add(new Locale("zh", "CN")); // Chinese (Simplified)
		supportedLocales.add(new Locale("zh", "TW")); // Chinese (Traditional)
		supportedLocales.add(new Locale("hr")); // Croatian
		supportedLocales.add(new Locale("cs")); // Czech
		supportedLocales.add(new Locale("da")); // Danish
		supportedLocales.add(new Locale("nl")); // Dutch
		supportedLocales.add(new Locale("en")); // English
		supportedLocales.add(new Locale("en", "GB")); // English (UK)
		supportedLocales.add(new Locale("en", "AU")); // English (Australia)
		supportedLocales.add(new Locale("et")); // Estonian
		supportedLocales.add(new Locale("fi")); // Finnish
		supportedLocales.add(new Locale("fr")); // French
		supportedLocales.add(new Locale("gl")); // Galician
		supportedLocales.add(new Locale("hi")); // Hindi
		supportedLocales.add(new Locale("ka")); // Georgian
		supportedLocales.add(new Locale("de")); // German
		supportedLocales.add(new Locale("de", "AT")); // German (Austria)
		supportedLocales.add(new Locale("el")); // Greek
		// supportedLocales.add(new Locale("gu")); // Gujarati
		supportedLocales.add(new Locale("iw")); // Hebrew
		// supportedLocales.add(new Locale("hi")); // Hindi
		supportedLocales.add(new Locale("hu")); // Hungarian
		supportedLocales.add(new Locale("is")); // Icelandic
		supportedLocales.add(new Locale("in")); // Indonesian
		supportedLocales.add(new Locale("it")); // Italian
		supportedLocales.add(new Locale("ja")); // Japanese
		supportedLocales.add(new Locale("ko")); // Korean
		supportedLocales.add(new Locale("lt")); // Lithuanian
		supportedLocales.add(new Locale("ml")); // Malayalam (Virtual Keyboard & numerals only)
		supportedLocales.add(new Locale("mk")); // Macedonian
		supportedLocales.add(new Locale("mr")); // Marati
		supportedLocales.add(new Locale("ms")); // Malay
		// supportedLocales.add(new Locale("ne")); // Nepalese
		supportedLocales.add(new Locale("no", "NO")); // Norwegian (Bokmal)
		supportedLocales.add(new Locale("no", "NO", "NY")); // Norwegian(Nynorsk)
		// supportedLocales.add(new Locale("oc")); // Occitan
		supportedLocales.add(new Locale("fa")); // Persian
		supportedLocales.add(new Locale("pl")); // Polish
		supportedLocales.add(new Locale("pt", "BR")); // Portugese (Brazil)
		supportedLocales.add(new Locale("pt", "PT")); // Portuguese (Portugal)
		// supportedLocales.add(new Locale("pa")); // Punjabi
		supportedLocales.add(new Locale("ro")); // Romanian
		supportedLocales.add(new Locale("ru")); // Russian
		supportedLocales.add(new Locale("sr")); // Serbian
		// TODO: remove IS_PRE_RELEASE
		supportedLocales.add(new Locale("si")); // Sinhala (Sri Lanka)
		
		supportedLocales.add(new Locale("sk")); // Slovakian
		supportedLocales.add(new Locale("sl")); // Slovenian
		supportedLocales.add(new Locale("es")); // Spanish
		supportedLocales.add(new Locale("sv")); // Swedish
		// supportedLocales.add(new Locale("ty")); // Tahitian
		supportedLocales.add(new Locale("ta")); // Tamil
		
		// supportedLocales.add(new Locale("te")); // Telugu
		supportedLocales.add(new Locale("th")); // Thai

		supportedLocales.add(new Locale("tr")); // Turkish
		 supportedLocales.add(new Locale("uk")); // Ukrainian
		// supportedLocales.add(new Locale("ur")); // Urdu
		supportedLocales.add(new Locale("vi")); // Vietnamese
		supportedLocales.add(new Locale("cy")); // Welsh

		if (GeoGebra.IS_PRE_RELEASE)
			supportedLocales.add(new Locale("ji")); // Yiddish
	}

	// specialLanguageNames: Java does not show an English name for all
	// languages
	// supported by GeoGebra, so some language codes have to be treated
	// specially
	public static Hashtable<String, String> specialLanguageNames = new Hashtable<String, String>();
	static {
		specialLanguageNames.put("bs", "Bosnian");
		specialLanguageNames.put("zhCN", "Chinese Simplified");
		specialLanguageNames.put("zhTW", "Chinese Traditional");
		specialLanguageNames.put("en", "English (US)");
		specialLanguageNames.put("enGB", "English (UK)");
		specialLanguageNames.put("enAU", "English (Australia)");
		specialLanguageNames.put("deAT", "German (Austria)");
		specialLanguageNames.put("gl", "Galician");
		specialLanguageNames.put("noNO", "Norwegian (Bokm\u00e5l)");
		specialLanguageNames.put("noNONY", "Norwegian (Nynorsk)");
		specialLanguageNames.put("ptBR", "Portuguese (Brazil)");
		specialLanguageNames.put("ptPT", "Portuguese (Portugal)");
		specialLanguageNames.put("si", "Sinhala"); // better than Sinhalese
	}

	public static final Color COLOR_SELECTION = new Color(230, 230, 245);

	// Font settings
	public static final int MIN_FONT_SIZE = 10;

	// currently used application fonts
	private int appFontSize;

	// maximum number of files to (save &) show in File -> Recent submenu
	public static final int MAX_RECENT_FILES = 8;

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
	public static final String FILE_EXT_HTM = "htm";
	public static final String FILE_EXT_TEX = "tex";
	
	protected File currentPath, currentImagePath, currentFile = null;

	// page margin in cm
	public static final double PAGE_MARGIN_X = 1.8 * 72 / 2.54;
	public static final double PAGE_MARGIN_Y = 1.8 * 72 / 2.54;

	private static final String RB_MENU = "/geogebra/properties/menu";
	private static final String RB_COMMAND = "/geogebra/properties/command";
	private static final String RB_ERROR = "/geogebra/properties/error";
	private static final String RB_PLAIN = "/geogebra/properties/plain";
	private static final String RB_SYMBOL = "/geogebra/properties/symbols";
	public static final String RB_JAVA_UI = "/geogebra/properties/javaui";
	public static final String RB_COLORS = "/geogebra/properties/colors";

	private static final String RB_SETTINGS = "/geogebra/export/settings";
	private static final String RB_ALGO2COMMAND = "/geogebra/kernel/algo2command";
	// Added for Intergeo File Format (Yves Kreis) -->
	private static final String RB_ALGO2INTERGEO = "/geogebra/kernel/algo2intergeo";
	// <-- Added for Intergeo File Format (Yves Kreis)

	// private static Color COLOR_STATUS_BACKGROUND = new Color(240, 240, 240);
	
	private boolean hasGui = false;
	
	public static final int VIEW_ERROR = 0;
	public static final int VIEW_EUCLIDIAN = 1;
	public static final int VIEW_ALGEBRA = 2;
	public static final int VIEW_SPREADSHEET = 4;
	public static final int VIEW_CAS = 8;
	public static final int VIEW_EUCLIDIAN2 = 16;
    public static final int VIEW_EUCLIDIAN3D = 512;

	/**
	 * The preferred size of this application. Used in case the frame size should be updated.
	 */
	private Dimension preferredSize = new Dimension();
	
	public static final int DEFAULT_ICON_SIZE = 32;

	private JFrame frame;
	private static AppletImplementation appletImpl;
	private FontManager fontManager;
	
	protected GuiManager guiManager;

	private Component mainComp;
	private boolean isApplet = false;
	private boolean showResetIcon = false;
	public boolean runningInFrame = false; // don't want to show resetIcon if running in Frame

	protected Kernel kernel;
	private MyXMLio myXMLio;

	protected EuclidianView euclidianView;
	private EuclidianController euclidianController;
	protected GeoElementSelectionListener currentSelectionListener;
	private GlobalKeyDispatcher globalKeyDispatcher;

	// For language specific settings
	private Locale currentLocale, englishLocale = null;
	private ResourceBundle rbmenu, rbcommand, rbcommandEnglish, rbcommandOld, rberror, rbcolors, rbplain, rbsymbol, rbsettings;
	protected ImageManager imageManager;
	private int maxIconSize = DEFAULT_ICON_SIZE;

	// Hashtable for translation of commands from
	// local language to internal name
	// key = local name, value = internal name
	private Hashtable translateCommandTable;
	// command dictionary
	private LowerCaseDictionary commandDict;
	
	// array of dictionaries corresponding to the sub command tables
	private LowerCaseDictionary[] subCommandDict;
	
	private boolean initing = false;
	protected boolean showAlgebraView = true;	
	private boolean showAuxiliaryObjects = false;
	private boolean showAlgebraInput = true;
	private boolean showInputTop = false;
	private boolean showCmdList = true;
	protected boolean showToolBar = true;
	private boolean showToolBarTop = true;
	protected boolean showMenuBar = true;
	protected boolean showConsProtNavigation = false;
	private boolean[] showAxes = { true, true };
	private boolean showGrid = false;
	private boolean antialiasing = true;
	private boolean printScaleString = false;
	private int labelingStyle = ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC;
	private boolean allowToolTips = true;


	private boolean rightClickEnabled = true;
	private boolean chooserPopupsEnabled = true;	
	private boolean labelDragsEnabled = true;
	private boolean shiftDragZoomEnabled = true;
	private boolean isErrorDialogsActive = true;
	private boolean isErrorDialogShowing = false;
	private boolean isOnTheFlyPointCreationActive = true;

	private static LinkedList fileList = new LinkedList();
	private boolean isSaved = true;
//	private int guiFontSize;
//	private int axesFontSize;
//	private int euclidianFontSize;

	protected JPanel centerPanel;

	private ArrayList<GeoElement> selectedGeos = new ArrayList<GeoElement>();
	
	private ArrayList<Perspective> tmpPerspectives = new ArrayList<Perspective>();

	private GgbAPI ggbapi = null;
	private PluginManager pluginmanager = null;
	private ScriptManager scriptManager = null;

	
	// GUI elements to support a sidebar help panel for the input bar.
	// The help panel slides open on a button press from the input bar.
	private JSplitPane applicationSplitPane;
	private InputBarHelpPanel inputHelpPanel;
	public InputBarHelpPanel getInputHelpPanel() {
		return inputHelpPanel;
	}
	

	public Application(CommandLineArguments args, JFrame frame, boolean undoActive) {
		this(args, frame, null, null, undoActive);
	}

	public Application(CommandLineArguments args, AppletImplementation appletImpl,
			boolean undoActive) {
		this(args, null, appletImpl, null, undoActive);
	}

	public Application(CommandLineArguments args, Container comp, boolean undoActive) {
		this(args, null, null, comp, undoActive);
	}

	protected Application(CommandLineArguments args, JFrame frame,
			AppletImplementation appletImpl, Container comp, boolean undoActive) {
		// help debug applets
		System.out.println("GeoGebra " + GeoGebra.VERSION_STRING + " " +  GeoGebra.BUILD_DATE
				+ " Java " + System.getProperty("java.version"));

		isApplet = appletImpl != null;
		
		JApplet applet = null;
		if (frame != null) {
			mainComp = frame;
		} else if (isApplet) {
			applet = appletImpl.getJApplet();
			mainComp = applet;
			setApplet(appletImpl);
		} else {
			mainComp = comp;
		}
		
		hasGui = !isApplet || appletImpl.needsGui();
		
		// don't want to redirect System.out and System.err when running as Applet
		// or eg from Eclipse
		getCodeBase(); // initialize runningFromJar
		
		if (!isApplet && runningFromJar) setUpLogging();

		// needed for JavaScript getCommandName(), getValueString() to work
		// (security problem running non-locally)
		if (isApplet) {
			AlgoElement.initAlgo2CommandBundle(this);
			preferredSize = appletImpl.getJApplet().getSize();
			// needs command.properties in main.jar
			// causes problems when not in English
			// initCommandBundle();
		} else {
			preferredSize = new Dimension(800, 600);
		}

		fontManager = new FontManager();
		initImageManager(mainComp);

		// set locale
		setLocale(mainComp.getLocale());
		
		// init kernel
		initKernel();
		kernel.setPrintDecimals(Kernel.STANDARD_PRINT_DECIMALS);
	
		// init xml io for construction loading
		myXMLio = new MyXMLio(kernel, kernel.getConstruction());
	
		// load the gui manager if necessary
		if(hasFullGui()) {
			initGuiManager();
		}

		// init euclidian view
		initEuclidianViews();
	
		// set frame
		if (!isApplet && frame != null) {
			setFrame(frame);
		}

		// load file on startup and set fonts
		// set flag to avoid multiple calls of setLabels() and
		// updateContentPane()
		initing = true;
		setFontSize(12);
		
		// initialize layout
		if(hasFullGui()) {
			getGuiManager().initialize();
		}
		
		if(!isApplet) {
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
				GeoGebraPreferences.getPref().loadXMLPreferences(this);
		}
		
		if(hasFullGui() && !fileLoaded) {			
			getGuiManager().getLayout().setPerspectives(tmpPerspectives);	
		}
		
		setUndoActive(undoActive);
		
		// applet/command line options like file loading on startup
		handleOptionArgs(args); 
		
		initing = false;

		// for key listening
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);	

		// init plugin manager for applications
		if (!isApplet)
			pluginmanager = getPluginManager();
		
		isSaved = true;
	}
		
	/**
	 * init the kernel (used for 3D)
	 */
	public void initKernel(){
		kernel = new Kernel(this);
	}
	
	/**
	 * init the EuclidianView (and EuclidianView3D for 3D)
	 */
	public void initEuclidianViews(){

		euclidianController = new EuclidianController(kernel);
		euclidianView = new EuclidianView(euclidianController, showAxes,
				showGrid);
		euclidianView.setAntialiasing(antialiasing);
	}
	
	/**
	 * init the ImageManager (and ImageManager3D for 3D)
	 * @param component
	 */
	protected void initImageManager(Component component){
		imageManager = new ImageManager(component);
	}
	
	/**
	 * @return True if the whole GUI is available, false if
	 * 		just the euclidian view is displayed.
	 */
	final public synchronized boolean hasFullGui() {
		return hasGui;
	}
	
	/**
	 * Initialize the gui manager. Needs to be in a separate method to allow the 3D application
	 * to load its own gui manager. 
	 */
	protected void initGuiManager() {
		setWaitCursor();
		guiManager = new GuiManager(Application.this);
		setDefaultCursor();
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
		return guiManager;
	}

	final public JApplet getJApplet() {
		if (appletImpl == null)
			return null;
		else
			return appletImpl.getJApplet();
	}
	
	final public Font getBoldFont() {
		return fontManager.getBoldFont();
	}
	
	final public Font getItalicFont() {
		return fontManager.getItalicFont();
	}
	
	final public Font getPlainFont() {
		return fontManager.getPlainFont();
	}
	
	final public Font getSerifFont() {
		return fontManager.getSerifFont();
	}
	
	final public Font getSmallFont() {
		return fontManager.getSmallFont();
	}
	
	final public Font getFont(boolean serif, int style, int size) {
		String name = serif ? 
				fontManager.getSerifFont().getFontName() :
				fontManager.getPlainFont().getFontName();	
		return FontManager.getFont(name, style, size);
	}
	
	/**
	 * Returns a font that can display testString.
	 */
	public Font getFontCanDisplay(String testString) {
		return getFontCanDisplay(testString, false, Font.PLAIN, appFontSize);
	}	
	
	/**
	 * Returns a font that can display testString.
	 */
	public Font getFontCanDisplay(String testString, int fontStyle) {
		return getFontCanDisplay(testString, false, fontStyle, appFontSize);
	}
	
	/**
	 * Returns a font that can display testString.
	 */
	public Font getFontCanDisplay(String testString, boolean serif, int fontStyle, int fontSize) {
		return fontManager.getFontCanDisplay(testString, serif, fontStyle, fontSize);
	}
	
	/**
	 * Sets state of application to "saved", 
	 * so that no warning appears on close.
	 * @author Zbynek Konecny
	 * @version 2010-05-26
	 */
	public void setSaved() {
		isSaved = true;
	}
	/**
	 * Sets application state to "unsaved"
	 * so that user is reminded on close.
	 */
	public void setUnsaved() {
		isSaved = false;
	}

	public boolean isIniting() {
		return initing;
	}
	
	public void fileNew() {
		// clear all 
		clearConstruction();
		
		// clear input bar
		if (hasFullGui() && showAlgebraInput()) {
			AlgebraInput ai = (AlgebraInput)(getGuiManager().getAlgebraInput());
			ai.clear();
		}
		
		// reset spreadsheet columns, reset trace columns
		if (hasFullGui()) {
			getGuiManager().resetSpreadsheet();
		}
		
		getEuclidianView().resetMaxLayerUsed();
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

	
	
	public boolean getAllowToolTips() {
		return allowToolTips;
	}

	/**
	 * Sets allowToolTips flag and toggles tooltips for the application. 
	 */
	public void setAllowToolTips(boolean allowToolTips) {
		this.allowToolTips = allowToolTips;
		ToolTipManager.sharedInstance().setEnabled(allowToolTips);
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
		if (initing)
			return;

		updateContentPane(false);
		if (frame != null && frame.isShowing()) {			
			getGuiManager().updateFrameSize();
		}		
		updateComponentTreeUI();		
	}

	private void updateContentPane(boolean updateComponentTreeUI) {
		if (initing)
			return;

		Container cp;
		if (isApplet)
			cp = appletImpl.getJApplet().getContentPane();
		else if (frame != null)
			cp = frame.getContentPane();
		else
			cp = (Container) mainComp;
		
		addMacroCommands();
		cp.removeAll();
		cp.add(buildApplicationPanel());
		fontManager.setFontSize(appFontSize);
		
		// update sizes		
		euclidianView.updateSize();
		
		// update layout
		if (updateComponentTreeUI) {
			updateComponentTreeUI();
		}
		
		// reset mode and focus
		setMoveMode();
		if (mainComp.isShowing())
			euclidianView.requestFocusInWindow();

		System.gc();
	}

	protected void updateComponentTreeUI() {
		if (frame != null) {
			SwingUtilities.updateComponentTreeUI(frame);
		} 
		else if (appletImpl != null) {
			SwingUtilities.updateComponentTreeUI(appletImpl.getJApplet());
		}
		else if (mainComp != null) {
			SwingUtilities.updateComponentTreeUI(mainComp);
		}
			
	}

	/**
	 * Builds a panel with all components that should be shown on screen (like
	 * toolbar, input field, algebra view).
	 */
	public JPanel buildApplicationPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		// CENTER: Algebra View, Euclidian View
		// euclidian panel with view and status bar
		if (centerPanel != null) centerPanel.removeAll();			
		centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, SystemColor.controlShadow));
		updateCenterPanel(true);

		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel bottomPanel = new JPanel(new BorderLayout());

		if(showAlgebraInput) {
			if(showInputTop) {
				topPanel.add(getGuiManager().getAlgebraInput(), BorderLayout.SOUTH);
			} else {
				bottomPanel.add(getGuiManager().getAlgebraInput(), BorderLayout.SOUTH);
			}
		}
		
		// initialize toolbar panel even if it's not used (hack)
		getGuiManager().getToolbarPanel();
		
		if(showToolBar) {
			if(showToolBarTop) {
				topPanel.add(getGuiManager().getToolbarPanel(), BorderLayout.NORTH);
			} else {
				bottomPanel.add(getGuiManager().getToolbarPanel(), BorderLayout.NORTH);
			}
		}
		
		//==================
		// G.Sturr 2010-11-14
		// Create a help panel for the input bar and a JSplitPane to contain it.
		// The splitPane defaults with the application on the left and null on the right. 
		// Our help panel will be added/removed as needed by the input bar.
		inputHelpPanel = new InputBarHelpPanel(this);
		applicationSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerPanel, null);
		applicationSplitPane.setBorder(BorderFactory.createEmptyBorder());
		// help panel is on the right, so set all resize weight to the left pane
		applicationSplitPane.setResizeWeight(1.0);
		applicationSplitPane.setDividerSize(0);
		
		
		panel.add(topPanel, BorderLayout.NORTH);
		panel.add(applicationSplitPane, BorderLayout.CENTER);
		panel.add(bottomPanel, BorderLayout.SOUTH);
		
		// init labels
		setLabels();
		
		// Menubar; if the main component is a JPanel, we need to add the 
		// menubar manually to the north
		if (showMenuBar() && mainComp instanceof JPanel) {
			JPanel menuBarPanel = new JPanel(new BorderLayout());
			menuBarPanel.add(getGuiManager().getMenuBar(), BorderLayout.NORTH);
			menuBarPanel.add(panel, BorderLayout.CENTER);
			return menuBarPanel;
		} else {
			// standard case: return 
			return panel;
		}
	}

	/**
	 * Open/close the sidebar help panel for the input bar
	 */
	public void setShowInputHelpPanel(boolean isVisible){
		if(isVisible){
			applicationSplitPane.setRightComponent(inputHelpPanel);
			if(applicationSplitPane.getLastDividerLocation()<=0)
				applicationSplitPane.setLastDividerLocation(
						applicationSplitPane.getWidth() - inputHelpPanel.getMinimumSize().width);
			applicationSplitPane.setDividerLocation(applicationSplitPane.getLastDividerLocation());
			applicationSplitPane.setDividerSize(8);

		}else{
			applicationSplitPane.setLastDividerLocation(applicationSplitPane.getDividerLocation());
			applicationSplitPane.setRightComponent(null);
			applicationSplitPane.setDividerSize(0);
		}
	}

	

	
	private Macro macro;
	
	/**
	 * Returns macro if in macro editing mode.
	 * @return macro being edited (in unchanged state)
	 */
	public Macro getMacro(){
		return macro;
	}
	
	
	
	/**
	 * Switches the application to macro editing mode
	 * @author Zbynek Konecny
	 * @version 2010-05-26
	 * @param macro Tool to be edited
	 */
	public void openMacro(Macro macro){
		String allXml=getXML();
		String header=allXml.substring(0,allXml.indexOf("<construction"));
		String footer=allXml.substring(allXml.indexOf("</construction>"),allXml.length());
		StringBuilder sb = new StringBuilder();
		macro.getXML(sb);
		String macroXml=sb.toString();
		String newXml= header+macroXml.substring(macroXml.indexOf("<construction"),macroXml.indexOf("</construction>"))+footer;
		this.macro= macro;
		setXML(newXml,true);
	}
	
	/**
	 * Adds a macro from XML
	 * @param xml macro code (including &lt;macro> wrapper)
	 * @return True if successful
	 */
	public boolean addMacroXML(String xml) {
		boolean ok=true;
		try {
			myXMLio.processXMLString("<geogebra format=\""+GeoGebra.XML_FILE_FORMAT+"\">"+xml+"</geogebra>", false, true);
		} catch (MyError err) {
			err.printStackTrace();
			showError(err);
			ok=false;
		} catch (Exception e) {
			e.printStackTrace();
			ok=false;
			showError("LoadFileFailed");
		}
		return ok;
	}
	
	public void updateCenterPanel(boolean updateUI) {
		if (centerPanel == null) return;
		
		centerPanel.removeAll();
		
		if(hasFullGui()) {
			centerPanel.add(getGuiManager().getLayout().getRootComponent(), BorderLayout.CENTER);
		} else {
			centerPanel.add(getEuclidianView(), BorderLayout.CENTER);
		}
		
		if (updateUI)
			updateComponentTreeUI();
	}

	public JPanel getCenterPanel() {
		return centerPanel;
	}
	
	public void validateComponent() {
		if(isApplet) {
			appletImpl.getJApplet().validate();
		} else {
			frame.validate();
		}
	}

	/**
	 * Handles command line options (like -language).
	 */
	private void handleOptionArgs(CommandLineArguments args) {
		
		if (args == null) return;
		
		if(args.getStringValue("help").length() > 0) {
			// help message
			System.out
					.println("Usage: java -jar geogebra.jar [OPTION] [FILE]\n"
							+ "Start GeoGebra with the specified OPTIONs and open the given FILE.\n"
							+ "  --help\t\tprint this message\n"
							+ "  --language=LANGUAGE_CODE\t\tset language using locale strings, e.g. en, de, de_AT, ...\n"
							+ "  --showAlgebraInput=BOOLEAN\tshow/hide algebra input field\n"
							+ "  --showAlgebraInputTop=BOOLEAN\tshow algebra input at top/bottom\n"
							+ "  --showAlgebraWindow=BOOLEAN\tshow/hide algebra window\n"
							+ "  --showSpreadsheet=BOOLEAN\tshow/hide spreadsheet\n"
							+ "  --showCAS=BOOLEAN\tshow/hide CAS window\n"
							+ "  --showSplash=BOOLEAN\tenable/disable the splash screen\n"
							+ "  --enableUndo=BOOLEAN\tenable/disable Undo\n"
							+ "  --fontSize=NUMBER\tset default font size\n"
							+ "  --showAxes=BOOLEAN\tshow/hide coordinate axes\n"
							+ "  --settingsFile=PATH|FILENAME\tLoad/save settings from/in a local file\n"
							+ "  --CAS=[MATHPIPER|MAXIMA]\tselect which CAS to use, default MathPiper\n"
							+ "  --maximaPath=PATH\tspecify where Maxima is installed and select Maxima as the current CAS\n"
							+ "  --antiAliasing=BOOLEAN\tturn anti-aliasing on/off\n");
			System.exit(0);
		}
		
		String language = args.getStringValue("language");
		if(language.length() > 0) {
			setLocale(getLocale(language));
		}
		
		boolean showAlgebraInput = args.getBooleanValue("showAlgebraInput", true);
		if(!showAlgebraInput) {
			setShowAlgebraInput(false, false);
		}
		
		boolean showAlgebraInputTop = args.getBooleanValue("showAlgebraInputTop", true);
		if(!showAlgebraInputTop) {
			setShowInputTop(false, false);
		}
		
		if(args.containsArg("showAlgebraWindow")) {
			boolean showAlgebraWindow = args.getBooleanValue("showAlgebraWindow", true);
			getGuiManager().setShowView(showAlgebraWindow, Application.VIEW_ALGEBRA);
		}
		
		if(args.containsArg("showSpreadsheet")) {
			boolean showSpreadsheet = args.getBooleanValue("showSpreadsheet", true);
			getGuiManager().setShowView(showSpreadsheet, Application.VIEW_SPREADSHEET);
		}
		
		if(args.containsArg("showCAS")) {
			boolean showCAS = args.getBooleanValue("showCAS", true);
			getGuiManager().setShowView(showCAS, Application.VIEW_CAS);
		}
		
		String cas = args.getStringValue("CAS");
		if (cas.toLowerCase(Locale.US).equals("maxima")) {
			setDefaultCAS(CAS_MAXIMA);
			
			if(args.containsArg("maximaPath")) {
				setMaximaPath(args.getStringValue("maximaPath"));
			}
		}
		
		String fontSize = args.getStringValue("fontSize");
		if(fontSize.length() > 0) {
			setFontSize(Integer.parseInt(fontSize));
		}
		
		boolean enableUndo = args.getBooleanValue("enableUndo", true);
		if(!enableUndo) {
			setUndoActive(false);
		}
		
		if(args.containsArg("showAxes")) {
			boolean showAxes = args.getBooleanValue("showAxes", true);
			this.showAxes[0] = showAxes;
			this.showAxes[1] = showAxes;
		}
		
		if(args.containsArg("showGrid")) {
			boolean showGrid = args.getBooleanValue("showGrid", false);
			this.showGrid = showGrid;
		}
		
		if(args.containsArg("primary")) {
			boolean primary = args.getBooleanValue("primary", false);
			if (primary) {
				
				getGuiManager().getLayout().applyPerspective("Primary");
				GlobalKeyDispatcher.changeFontsAndGeoElements(this, 20, false);
				setLabelingStyle(ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF);
				getEuclidianView().setCapturingThreshold(10);
				GeoAngle defaultAngle = (GeoAngle)getKernel().getConstruction().getConstructionDefaults().getDefaultGeo(ConstructionDefaults.DEFAULT_ANGLE);
				defaultAngle.setAllowReflexAngle(false);
			}
		}
		
		boolean antiAliasing = args.getBooleanValue("antiAliasing", true);
		if(!antiAliasing) {
			this.antialiasing = false;
		}
	}
	
	/**
	 * Opens a file specified as last command line argument
	 * 
	 * @return true if a file was loaded successfully
	 */
	private boolean handleFileArg(CommandLineArguments args) {
		if(args == null || args.getNoOfFiles() == 0) 
			return false;
		
		boolean successRet = true;
		
		for (int i = 0 ; i < args.getNoOfFiles() ; i++) {
			
			final String fileArgument = args.getStringValue("file"+i);
			
			if (i > 0) { // load in new Window
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {		
						String[] argsNew = {fileArgument};
						GeoGebraFrame.createNewWindow(new CommandLineArguments(argsNew));
					}
				});
			} else {
	
				try {
					boolean success;
					String lowerCase = fileArgument.toLowerCase(Locale.US);
					boolean isMacroFile = lowerCase.endsWith(FILE_EXT_GEOGEBRA_TOOL);
					
					if (lowerCase.startsWith("http:") || lowerCase.startsWith("file:")) {
						//replace all whitespace characters by %20 in URL string
						String fileArgument2 = fileArgument.replaceAll("\\s", "%20");
						URL url = new URL(fileArgument2);
						success = loadXML(url, isMacroFile);
						
						if(success && !isMacroFile && hasFullGui() && !getGuiManager().getLayout().isIgnoringDocument()) {
							getGuiManager().getLayout().setPerspectives(tmpPerspectives);
						} else {
							updateContentPane(true);
						}
					} else if (lowerCase.startsWith("base64://")) {
						
						// substring to strip off base64://
						byte [] zipFile = geogebra.util.Base64.decode(fileArgument.substring(9));
						success = loadXML(zipFile);
						
						if(success && !isMacroFile && hasFullGui() && !getGuiManager().getLayout().isIgnoringDocument()) {
							getGuiManager().getLayout().setPerspectives(tmpPerspectives);
						} else {
							updateContentPane(true);
						}
					} else {
						File f = new File(fileArgument);
						f = f.getCanonicalFile();
						success = getGuiManager().loadFile(f, isMacroFile);
					}
					
					successRet = successRet && success;
				} catch (Exception e) {
					e.printStackTrace();
					successRet = false;
				}
			}
		}
		return successRet;
	}

	final public Kernel getKernel() {
		return kernel;
	}
	
	public void setApplet(AppletImplementation appletImpl) {
		isApplet = true;
		this.appletImpl = appletImpl;
		mainComp = appletImpl.getJApplet();
	}
	
	public AppletImplementation getApplet() {
		return appletImpl;
	}

	public void setShowResetIcon(boolean flag) {
		if (flag != showResetIcon) {
			showResetIcon = flag;
			euclidianView.updateBackground();
		}
	}
	
	final public boolean showResetIcon() {
		return showResetIcon && !runningInFrame;
	}
	

	public void reset() {
		if (appletImpl != null) {
			appletImpl.reset();
		} else if (currentFile != null) {
			getGuiManager().loadFile(currentFile, false);
		} else
			clearConstruction();
	}

	public void refreshViews() {
		euclidianView.updateBackground();
		kernel.notifyRepaint();
	}

	public void setFrame(JFrame frame) {
		isApplet = false;
		mainComp = frame;
				
		this.frame = frame;	
		updateTitle();
		
		// Windows 7 uses this for the Toolbar icon too
		// (needs to be larger)
		frame.setIconImage(getInternalImage("geogebra32.gif"));

		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		WindowListener [] wl = frame.getWindowListeners();
		if (wl == null || wl.length == 0) {
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

	public boolean isStandaloneApplication() {
		return !isApplet && (mainComp instanceof JFrame);
	}
	
	public synchronized JFrame getFrame() {
		if (frame == null) {
			frame = getGuiManager().createFrame();	
		}
		
		return frame;
	}

	public Component getMainComponent() {
		return mainComp;
	}
	
	public Dimension getPreferredSize() {
		return preferredSize;
	}
	
	public void setPreferredSize(Dimension size) {
		preferredSize = size;
	}
	
	/**
	 * Save all perspectives included in a document into an array with temporary
	 * perspectives.
	 * 
	 * @param perspectives
	 */
	public void setTmpPerspectives(ArrayList<Perspective> perspectives) {
		tmpPerspectives = perspectives;
	}
	
	public ArrayList<Perspective> getTmpPerspectives() {
		return tmpPerspectives;
	}

	public EuclidianView getEuclidianView() {
		return euclidianView;
	}
	
	public BufferedImage getExportImage(double maxX, double maxY) throws OutOfMemoryError {
		
		double scale = Math.min(maxX / getEuclidianView().getSelectedWidth(), 
				maxY / getEuclidianView().getSelectedHeight());
		
		return getEuclidianView().getExportImage(scale);
	}
	

	public void toggleAxis(){
		
		boolean bothAxesShown = getEuclidianView().getShowXaxis()
			&& getEuclidianView().getShowYaxis();
		//getEuclidianView().showAxes(!bothAxesShown, !bothAxesShown);
		getEuclidianView().setShowAxes(!bothAxesShown, true);
		/*
		getEuclidianView().repaint();
		storeUndoInfo();
		updateMenubar();
		*/
	}
	
	public void setShowAxesSelected(JCheckBoxMenuItem cb){
		cb.setSelected(getEuclidianView().getShowXaxis() && getEuclidianView().getShowYaxis());
	}
	
	public void toggleGrid(){
		
		getEuclidianView().showGrid(!getEuclidianView().getShowGrid());

	}
	
	public void setShowGridSelected(JCheckBoxMenuItem cb){
		cb.setSelected(getEuclidianView().getShowGrid());
	}
	
	
	
	
	/** return 2D (and 3D) views settings
	 * @return 2D (and 3D) views settings
	 */
	public String getEuclidianViewsXML() {
		return getEuclidianView().getXML();
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
		currentSelectionListener = sl;
		if (sl != null) 
			setMode(EuclidianView.MODE_SELECTION_LISTENER);
		else
			setMoveMode();
	}	

	public GeoElementSelectionListener getCurrentSelectionListener() {
		return currentSelectionListener;
	}

	public void setMoveMode() {
		setMode(EuclidianView.MODE_MOVE);
	}
	
		
	/** 
	 * Sets the maximum pixel size (width and height) of 
	 * all icons in the user interface. Larger icons are scaled
	 * down.
	 * @param pixel: max icon size between 16 and 32 pixels
	 */
	public void setMaxIconSize(int pixel) {
		maxIconSize = Math.min(32, Math.max(16, pixel));
	}
	
	public int getMaxIconSize() {
		return maxIconSize;
	}

	public ImageIcon getImageIcon(String filename) {
		return getImageIcon(filename, null);
	}

	public ImageIcon getImageIcon(String filename, Color borderColor) {
		return imageManager.getImageIcon("/gui/images/" + filename,
				borderColor);
	}
	
	public ImageIcon getToolBarImage(String filename, Color borderColor) {
		String path = "/gui/toolbar/images/" + filename;
		ImageIcon icon = imageManager.getImageIcon(path, borderColor);
		
		/* mathieu 2010-04-10
		 * see ImageManager3D.getImageResourceGeoGebra()
		if (icon == null) {
			// load3DJar();
			// try to find this image in 3D extension
			path = "/geogebra/geogebra3D/images/" + filename;
			icon = imageManager.getImageIcon(path, borderColor);
		}
		*/
		
		if (icon == null) {
			icon = getToolIcon(borderColor);
		}
				 
		// scale icon if necessary
		icon = ImageManager.getScaledIcon(icon, Math.min(icon.getIconWidth(), maxIconSize), 
					Math.min(icon.getIconHeight(), maxIconSize));
		
		return icon;
	}
	
	public ImageIcon getToolIcon(Color border) {
		return imageManager.getImageIcon("/gui/toolbar/images/mode_tool_32.png", border);
	}

	public ImageIcon getEmptyIcon() {
		return imageManager.getImageIcon("/gui/images/empty.gif");
	}

	public Image getInternalImage(String filename) {
		return imageManager.getInternalImage("/gui/images/" + filename);
	}
	
	public Image getRefreshViewImage() {		
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage("/main/view-refresh.png");		
	}
	
	public Image getPlayImage() {		
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage("/main/nav_play.png");		
	}
	
	public Image getPauseImage() {		
		// don't need to load gui jar as reset image is in main jar
		return imageManager.getInternalImage("/main/nav_pause.png");		
	}

	public BufferedImage getExternalImage(String filename) {
		return imageManager.getExternalImage(filename);
	}

	public void addExternalImage(String filename, BufferedImage image) {
		imageManager.addExternalImage(filename, image);
	}

	// public void startEditing(GeoElement geo) {
	// if (showAlgebraView)
	// getApplicationGUImanager().startEditingAlgebraView(geo);
	// }

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

	/***************************************************************************
	 * LOCALE part
	 **************************************************************************/

	/**
	 * Creates a Locale object according to the given language code. The
	 * languageCode string should consist of two letters for the language, two
	 * letters for the country and two letters for the variant. E.g. "en" ...
	 * language: English , no country specified, "deAT" or "de_AT" ... language:
	 * German , country: Austria, "noNONY" or "no_NO_NY" ... language: Norwegian ,
	 * country: Norway, variant: Nynorsk
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

		if (!initing) {
			setMoveMode();
		}

		// load resource files
		setLocale(locale);
		
		// update right angle style in euclidian view (different for German)
		if (euclidianView != null)
			euclidianView.updateRightAngleStyle(locale);
		
		// make sure digits are updated in all numbers
		getKernel().updateConstruction();
		setUnsaved();


		setLabels(); // update display
				
		System.gc();
	}

	/*
	 * removed Michael Borcherds 2008-03-31 private boolean reverseLanguage =
	 * false; //FKH 20040822 final public boolean isReverseLanguage() { //FKH
	 * 20041010 // for Chinese return reverseLanguage; }
	 */



	/*
	 * in French, zero is singular, eg 0 dcimale rather than 0 decimal places
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

	// For Hebrew and Arabic. 
	private boolean rightToLeftDigits = false;

	final public boolean isRightToLeftDigits() {
		return true;//rightToLeftDigits;
	}

	// For eg Hebrew and Arabic. 
	public static char unicodeZero = '0';
	public static char unicodeDecimalPoint = '.';
	public static char unicodeComma = '.'; // \u060c for Arabic comma
	//public static char unicodeThousandsSeparator = ','; // \u066c for Arabic

	// for Basque and Hungarian you have to say "A point" instead of "point A"
	private boolean reverseNameDescription = false;
	private boolean isAutoCompletePossible = true;

	final public boolean isReverseNameDescriptionLanguage() {
		// for Basque and Hungarian 
		return reverseNameDescription;
	}
	
	StringBuilder sbOrdinal;
	/*
	 * given 1, return eg 1st, 1e, 1:e according to the language
	 * 
	 * http://en.wikipedia.org/wiki/Ordinal_indicator
	 */
	public String getOrdinalNumber(int n) {
		String lang = getLocale().getLanguage();

		// check here for languages where 1st = 1
		if ("pt".equals(lang)
				|| "ar".equals(lang)
				|| "cy".equals(lang)
				|| "fa".equals(lang)
				|| "ja".equals(lang)
				|| "ko".equals(lang)
				|| "lt".equals(lang)
				|| "mr".equals(lang)
				|| "ms".equals(lang)
				|| "nl".equals(lang)
				|| "si".equals(lang)
				|| "th".equals(lang)
				|| "vi".equals(lang)
				|| "zh".equals(lang)
				) return n+"";
		
		if (sbOrdinal == null) sbOrdinal = new StringBuilder();
		else sbOrdinal.setLength(0);
		
		
		// prefixes
		if ("in".equals(lang)) {
			sbOrdinal.append("ke-");
		} else if ("iw".equals(lang)) {
			// prefix and postfix for Hebrew
			sbOrdinal.append("\u200f\u200e");
		}
			
		sbOrdinal.append(n);

		if ("cs".equals(lang)
				 || "da".equals(lang)
				 || "et".equals(lang)
				 || "eu".equals(lang)
				 || "hr".equals(lang)
				 || "hu".equals(lang)
				 || "is".equals(lang)
				 || "no".equals(lang)
				 || "sk".equals(lang)
				 || "sr".equals(lang)
				 || "tr".equals(lang)
		) {
			sbOrdinal.append('.');
		} else if ("de".equals(lang)) {
			sbOrdinal.append("th");
		} else if ("fi".equals(lang)) {
			sbOrdinal.append(":s");
		} else if ("el".equals(lang)) {
			sbOrdinal.append('\u03b7');
		} else if ("ro".equals(lang)
				|| "ca".equals(lang)
				|| "es".equals(lang)
				|| "it".equals(lang)
				|| "pt".equals(lang)
			) {
			sbOrdinal.append("�");
		} else if ("bs".equals(lang)
				|| "sl".equals(lang)) {
			sbOrdinal.append("-ti");
		} else if ("sq".equals(lang)) {
			sbOrdinal.append("-te");
		} else if ("gl".equals(lang)) {
			sbOrdinal.append("ava");
		} else if ("mk".equals(lang)) {
			sbOrdinal.append("-\u0442\u0438");
		} else if ("ka".equals(lang)) {
			sbOrdinal.append("-\u10d4");
		} else if ("iw".equals(lang)) {
			sbOrdinal.append("\u200e\u200f");
		} else if ("ru".equals(lang)
				|| "uk".equals(lang)) {
			sbOrdinal.append("-\u0433\u043e");
		} else if ("fr".equals(lang)) {
			if (n == 1)
				sbOrdinal.append("er"); // could also be "re" for feminine...
			else
				sbOrdinal.append("e"); // could also be "es" for plural...
		} else if ("sv".equals(lang)) {
			int unitsDigit = n % 10;
			if (unitsDigit == 1 || unitsDigit == 2)
				sbOrdinal.append(":a"); 
			else
				sbOrdinal.append(":e"); 
		} else if ("en".equals(lang)) {

					
			/* http://en.wikipedia.org/wiki/Names_of_numbers_in_English
			 * If the tens digit of a number is 1, then write "th" after the number. For example: 13th, 19th, 112th, 9,311th.
		If the tens digit is not equal to 1, then use the following table:
		If the units digit is:		0	1	2	3	4	5	6	7	8	9
		write this after the number	th	st	nd	rd	th	th	th	th	th	th
			 */
		
			int tensDigit = (n / 10) % 10;
			
			if (tensDigit == 1) {
				sbOrdinal.append("th");
				return sbOrdinal.toString();
			}
			
			int unitsDigit = n % 10;
	
			switch (unitsDigit) {
			case 1:
				sbOrdinal.append("st");
				break;
			case 2:
				sbOrdinal.append("nd");
				break;
			case 3:
				sbOrdinal.append("rd");
				break;
			default:
				sbOrdinal.append("th");
				break;
			}
		}
		
		return sbOrdinal.toString();
		
	}
	
	/**
	 * Returns whether autocomplete should be used at all. 
	 * Certain languages make problems with auto complete turned on (e.g. Korean).
	 */
	final public boolean isAutoCompletePossible() {
		return isAutoCompletePossible;
	}
	
	private void updateReverseLanguage(Locale locale) {
		
		String lang = locale.getLanguage();
		// reverseLanguage = "zh".equals(lang); removed Michael Borcherds
		// 2008-03-31
		reverseNameDescription = "eu".equals(lang) || "hu".equals(lang);

		// used for axes labels
		rightToLeftDigits = ("ar".equals(lang) || "fa".equals(lang));

		// Guy Hed, 25.8.2008
		// Guy Hed, 26.4.2009 - added Yiddish and Persian as RTL languages
		rightToLeftReadingOrder = ("iw".equals(lang) || "ar".equals(lang) || "fa".equals(lang) || "ji".equals(lang));

		// Another option:
		// rightToLeftReadingOrder =
		// (Character.getDirectionality(getPlain("Algebra").charAt(1)) ==
		// Character.DIRECTIONALITY_RIGHT_TO_LEFT);
		
		// turn off auto-complete for Korean
		isAutoCompletePossible = true;//!"ko".equals(lang);
		
		// defaults
		unicodeDecimalPoint = '.';
		unicodeComma = ',';
		// unicodeThousandsSeparator=',';
		
		if (lang.startsWith("ar")) { // Arabic
			unicodeZero = '\u0660'; // Arabic-Indic digit 0
			unicodeDecimalPoint = '\u066b'; // Arabic-Indic decimal point
			unicodeComma = '\u060c'; // Arabic comma
			//unicodeThousandsSeparator = '\u066c'; // Arabic Thousands separator
		} else if (lang.startsWith("fa")) { // Persian
			unicodeZero = '\u06f0'; // Persian digit 0 (Extended Arabic-Indic)
			unicodeDecimalPoint = '\u066b'; // Arabic comma
			unicodeComma = '\u060c'; // Arabic-Indic decimal point
			//unicodeThousandsSeparator = '\u066c'; // Arabic Thousands separators
		} else if (lang.startsWith("ml")) {
			unicodeZero = '\u0d66'; // Malayalam digit 0
		} else if (lang.startsWith("th")) {
			unicodeZero = '\u0e50'; // Thai digit 0
		} else if (lang.startsWith("ta")) {
			unicodeZero = '\u0be6'; // Tamil digit 0
		} else if (lang.startsWith("sd")) {
			unicodeZero = '\u1bb0'; // Sudanese digit 0
		} else if (lang.startsWith("kh")) {
			unicodeZero = '\u17e0'; // Khmer digit 0
		} else if (lang.startsWith("mn")) {
			unicodeZero = '\u1810'; // Mongolian digit 0
		} else if (lang.startsWith("mm")) {
			unicodeZero = '\u1040'; // Mayanmar digit 0
		} else {
			unicodeZero = '0';
		}
	}
	
	


	// Michael Borcherds 2008-02-23
	public boolean languageIs(Locale locale, String lang) {
		return locale.getLanguage().equals(lang);
	}

	StringBuilder testCharacters = new StringBuilder();

	public void setLocale(Locale locale) {
		if (locale == currentLocale) return;
		Locale oldLocale = currentLocale;

		// only allow special locales due to some weird server
		// problems with the naming of the property files
		currentLocale = getClosestSupportedLocale(locale);
		updateResourceBundles();

		// update font for new language (needed for e.g. chinese)
		try {
			fontManager.setLanguage(currentLocale);
		} catch (Exception e) {
			e.printStackTrace();
			showError(e.getMessage());

			// go back to previous locale
			currentLocale = oldLocale;
			updateResourceBundles();
		}

		updateReverseLanguage(locale);
		
		String  language = getLocale().getLanguage();

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
		if (rbcolors != null)
			rbcolors = MyResourceBundle.createBundle(RB_COLORS, currentLocale);
	}
	
	/*
	private void updateSecondaryResourceBundles() {
		//if (rbmenuSecondary != null)
		//	rbmenuSecondary = MyResourceBundle.createBundle(RB_MENU, currentLocale);
		//if (rberrorSecondary != null)
		//	rberrorSecondary = MyResourceBundle.createBundle(RB_ERROR, currentLocale);
		//if (rbplainSecondary != null)
		//	rbplainSecondary = MyResourceBundle.createBundle(RB_PLAIN, currentLocale);
		if (rbcommandSecondary != null)
			rbcommandSecondary = MyResourceBundle.createBundle(RB_COMMAND, secondaryLocale);
		//if (rbcolorsSecondary != null)
		//	rbcolorsSecondary = MyResourceBundle.createBundle(RB_COLORS, currentLocale);
	} //*/
	
	private void fillCommandDict() {
		if (rbcommand == rbcommandOld)
			return;
		rbcommandOld = rbcommand;

		// translation table for all command names in command.properties
		if (translateCommandTable == null) 
			translateCommandTable = new Hashtable();

		// command dictionary for all public command names available in
		// GeoGebra's input field
		if (commandDict == null) 
			commandDict = new LowerCaseDictionary();		
		translateCommandTable.clear();
		commandDict.clear();

		Enumeration e = rbcommand.getKeys();
		Set publicCommandNames = kernel.getAlgebraProcessor().getPublicCommandSet();
		
		//=====================================
		// init sub command dictionaries
		Set[] publicSubCommandNames = kernel.getAlgebraProcessor().getPublicCommandSubSets();
		if(subCommandDict == null){
			subCommandDict = new LowerCaseDictionary[publicSubCommandNames.length];
			for(int i=0; i<subCommandDict.length; i++)
				subCommandDict[i] = new LowerCaseDictionary();	
		}
		for(int i=0; i<subCommandDict.length; i++)
			subCommandDict[i].clear();
		//=====================================
		
		while (e.hasMoreElements()) {
			String internal = (String) e.nextElement();
			// Application.debug(internal);
			if (!internal.endsWith("Syntax") && !internal.endsWith("Syntax3D") && !internal.endsWith("SyntaxCAS") && !internal.equals("Command")) {
				String local = rbcommand.getString((String) internal);
				if (local != null) {
					local = local.trim();
					// case is ignored in translating local command names to
					// internal names!
					translateCommandTable.put(local.toLowerCase(), internal);
					
					// only add public commands to the command dictionary
					if (publicCommandNames.contains(internal))
						commandDict.addEntry(local);
					
					// add public commands to the sub-command dictionaries
					for(int i=0; i<subCommandDict.length; i++){
						if (publicSubCommandNames[i].contains(internal))
							subCommandDict[i].addEntry(local);
					}
					
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
	 * properties methods
	 */

	final public String getColor(String key) {
		if (rbcolors == null) {
			initColorsResourceBundle();
		}

		try {
			return rbcolors.getString(key.toLowerCase(Locale.US));
		} catch (Exception e) {
			return key;
		}
	}

	final public String reverseGetColor(String str) {
		str = Util.removeSpaces(str.toLowerCase(Locale.US));
		if (rbcolors == null) {
			initColorsResourceBundle();
		}

		try {
			
			Enumeration<String> enumer = rbcolors.getKeys();
			while (enumer.hasMoreElements()) {
				String key = enumer.nextElement();
				if (str.equals(Util.removeSpaces(rbcolors.getString(key).toLowerCase(Locale.US))))
					return key;
			}
			
			
			
			return str;
		} catch (Exception e) {
			return str;
		}
	}

	final public String getPlain(String key) {
		if (rbplain == null) {
			initPlainResourceBundle();
		}

		try {
			return rbplain.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	final public String getSymbol(int key) {
		if (rbsymbol == null) {
			initSymbolResourceBundle();
		}

		try {
			return rbsymbol.getString("S."+key);
		} catch (Exception e) {
			return null;
		}
	}

//	final public String reverseGetPlain(String str) {
//		if (rbplain == null) {			
//			initPlainResourceBundle();
//		}
//		
//		str = str.toLowerCase();
//
//		try {
//			Enumeration enumer = rbplain.getKeys();
//			
//			while (enumer.hasMoreElements()) {
//				String key = (String)enumer.nextElement();
//				if (rbplain.getString(key).toLowerCase().equals(str))
//					return key;
//			}
//			
//			return str;
//		} catch (Exception e) {
//			return str;
//		}
//	}

	private void initPlainResourceBundle() {
		rbplain = MyResourceBundle.createBundle(RB_PLAIN, currentLocale);
		if (rbplain != null)
			kernel.updateLocalAxesNames();
	}

	private void initSymbolResourceBundle() {
		rbsymbol = MyResourceBundle.createBundle(RB_SYMBOL, currentLocale);
	}

	private void initColorsResourceBundle() {
		rbcolors = MyResourceBundle.createBundle(RB_COLORS, currentLocale);
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
				if (pos >= 0 && pos < args.length)
					// success
					sbPlain.append(args[pos]);
				else
					// failed
					sbPlain.append(ch);
			} else {
				sbPlain.append(ch);
			}
		}

		return sbPlain.toString();
	}

	private StringBuilder sbPlain = new StringBuilder();

	final public String getMenu(String key) {
		if (rbmenu == null) {
			rbmenu = MyResourceBundle.createBundle(RB_MENU, currentLocale);
		}

		try {
			return rbmenu.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	final public String getError(String key) {
		if (rberror == null) {
			rberror = MyResourceBundle.createBundle(RB_ERROR, currentLocale);
		}
		
		try {
			return rberror.getString(key);
		} catch (Exception e) {
			return key;
		}
	}
	
	/**
	 * Initializes the translated command names for this application. Note: this will 
	 * load the properties files first.
	 */
	final public void initTranslatedCommands() {
		if (rbcommand == null) {
			rbcommand = MyResourceBundle
					.createBundle(RB_COMMAND, currentLocale);
			fillCommandDict();
			kernel.updateLocalAxesNames();
		}
	}

	final public String getCommand(String key) {
		initTranslatedCommands();		

		try {
			return rbcommand.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	final public String getEnglishCommand(String key) {
		
		if (englishLocale == null) englishLocale = new Locale("en");
		
		if (rbcommandEnglish == null) 
			rbcommandEnglish = MyResourceBundle
					.createBundle(RB_COMMAND, englishLocale);


		try {
			return rbcommandEnglish.getString(key);
		} catch (Exception e) {
			return key;
		}
	}
	
	public String getCommandSyntax(String key) {
		
		return getCommand(key+"Syntax");
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
		if (translateCommandTable == null)
			return localname;

		// note: lookup lower case of command name!
		Object value = translateCommandTable.get(localname.toLowerCase());		
		if (value == null)
			return localname;
		else
			return (String) value;
	}

	public void showRelation(GeoElement a, GeoElement b) {
		JOptionPane.showConfirmDialog(mainComp, new Relation(kernel).relation(
    				a, b), getPlain("ApplicationName") + " - "
    				+ getCommand("Relation"), JOptionPane.DEFAULT_OPTION,
    				JOptionPane.INFORMATION_MESSAGE);

	}

	public void showHelp(String key) {
		final String text = getPlain(key); // Michael Borcherds changed to use
		// getPlain() and removed try/catch
		
		JOptionPane.showConfirmDialog(mainComp, text,
    				getPlain("ApplicationName") + " - " + getMenu("Help"),
    				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	public void showError(String key) {
		showErrorDialog(getError(key));
	}

	public void showError(MyError e) {
		String command = e.getcommandName();
		
		// make sure splash screen not showing (will be in front)
		  if (GeoGebra.splashFrame != null) GeoGebra.splashFrame.setVisible(false);

		
		if (command == null) {
			showErrorDialog(e.getLocalizedMessage());
			return;
		}
		
		Object[] options = {getPlain("OK"), getPlain("ShowOnlineHelp")};
		int n = JOptionPane.showOptionDialog(mainComp,
				e.getLocalizedMessage(),
				getPlain("ApplicationName") + " - " + getError("Error"),
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,     //do not use a custom Icon
				options,  //the titles of buttons
				options[0]); //default button title

		if (n == 1) getGuiManager().openHelp(command);

	}

	public void showErrorDialog(final String msg) {
		if (!isErrorDialogsActive)
			return;
		
		// make sure splash screen not showing (will be in front)
		  if (GeoGebra.splashFrame != null) GeoGebra.splashFrame.setVisible(false);
		
		Application.printStacktrace("showErrorDialog: "+msg);
		isErrorDialogShowing = true;
		
		// use SwingUtilities to make sure this gets executed in the correct (=GUI) thread.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {		
		//TODO investigate why this freezes Firefox sometimes
				JOptionPane.showConfirmDialog(mainComp, msg,
						getPlain("ApplicationName") + " - " + getError("Error"),
						JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
				isErrorDialogShowing = false;
			}
		});
	}
	
	public boolean isErrorDialogShowing() {
		return isErrorDialogShowing;
	}
	
	public void showMessage(final String message) {		
		//Application.debug("showMessage: "+message);
		
		// use SwingUtilities to make sure this gets executed in the correct (=GUI) thread.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showConfirmDialog(mainComp, message,
	    				getPlain("ApplicationName") + " - " + getMenu("Info"),
	    				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);		
			}
		});
	}

	/**
	 * Downloads a bitmap from the URL and stores it in this application's
	 * imageManager. Michael Borcherds
	 * 
	 * @return fileName of image stored in imageManager
	 * 
	 * public String getImageFromURL(String url) { try{
	 * 
	 * BufferedImage img=javax.imageio.ImageIO.read(new URL(url)); return
	 * createImage(img, "bitmap.png"); } catch (Exception e) {return null;} }
	 */

	public void setWaitCursor() {
		Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		mainComp.setCursor(waitCursor);
		
		if (euclidianView != null )
			euclidianView.setCursor(waitCursor);
		
		if (guiManager != null)
			guiManager.allowGUIToRefresh();
	}

	public void setDefaultCursor() {
		mainComp.setCursor(Cursor.getDefaultCursor());
		if (euclidianView != null )
			euclidianView.setCursor(Cursor.getDefaultCursor());
	}

	public void doAfterRedefine(GeoElement geo) {
		if (guiManager != null)
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
	

	public void setCurrentPath(File file) {
		currentPath=file;
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

		getGuiManager().updateFrameTitle();
	}


	public void setFontSize(int points) {
		setFontSize(points, true);
	}
	
	public void setFontSize(int points, boolean update) {
		if (points == appFontSize)
			return;
		appFontSize = points;
		isSaved = false;
		if (!update) return;
		
		resetFonts();

		if (!initing) {
			if (appletImpl != null)
				SwingUtilities.updateComponentTreeUI(appletImpl.getJApplet());
			if (frame != null)
				SwingUtilities.updateComponentTreeUI(frame);
		}
	}
	
	public void resetFonts() {
		fontManager.setFontSize(appFontSize);
		updateFonts();
	}

	public void updateFonts() {
		if (euclidianView != null)
			euclidianView.updateFonts();

		if (guiManager != null)
			getGuiManager().updateFonts();

	}

	public int getFontSize() {
		return appFontSize;
	}

	private void setLabels() {
		if (initing)
			return;

		if (guiManager != null) {
			getGuiManager().setLabels();
		}
		
		if (rbplain != null)
			kernel.updateLocalAxesNames();

		updateCommandDictionary();
	}

	/**
	 * Returns name of given tool.
	 * @param mode number
	 */
	public String getToolName(int mode) {
		return getToolNameOrHelp(mode, true);
	}
	
    /**
     * Returns the tool help text for the given tool. 
     * @param mode number
     */
    public String getToolHelp(int mode) {    
    	return getToolNameOrHelp(mode, false);
    }
    
    /**
     * Returns the tool name and tool help text for the given tool as
     * an HTML text that is useful for tooltips. 
     * @param mode: tool ID
     */
    public String getToolTooltipHTML(int mode) {
    	StringBuilder sbTooltip = new StringBuilder();
		sbTooltip.append("<html><b>");
		sbTooltip.append(Util.toHTMLString(getToolName(mode)));
		sbTooltip.append("</b><br>");		
		sbTooltip.append(Util.toHTMLString(getToolHelp(mode)));
		sbTooltip.append("</html>");
		return sbTooltip.toString();
    }
    
    private String getToolNameOrHelp(int mode, boolean toolName) {
		// macro
    	String ret; 
    	
		if (mode >= EuclidianView.MACRO_MODE_ID_OFFSET) {
			// MACRO
			int macroID = mode - EuclidianView.MACRO_MODE_ID_OFFSET;
			try {
				Macro macro = kernel.getMacro(macroID);
				if (toolName) {
					// TOOL NAME
					ret = macro.getToolName();
					if ("".equals(ret))
						ret = macro.getCommandName();
				} else {
					// TOOL HELP
					ret = macro.getToolHelp();
					if ("".equals(ret))
						ret = macro.getNeededTypesString();
				}
			} catch (Exception e) {
				Application
						.debug("Application.getModeText(): macro does not exist: ID = "
								+ macroID);
				// e.printStackTrace();
				return "";
			}
		} else {
			// STANDARD TOOL
			String modeText = getKernel().getModeText(mode);
			if (toolName) {
				// tool name
				ret = getMenu(modeText);
			} else {
				// tool help			
		    	ret = getMenu(modeText + ".Help");  
			}
		}
		
		return ret;
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
					icon = getToolBarImage("mode_tool_32.png", border);
				else
					// use image as icon
					icon = new ImageIcon(ImageManager.addBorder(img, border));
			} catch (Exception e) {
				Application.debug("macro does not exist: ID = " + macroID);
				return null;
			}
		} else {
			// standard case
			String modeText = getKernel().getModeText(mode);
			// bugfix for Turkish locale added Locale.US
			String iconName = "mode_" + modeText.toLowerCase(Locale.US)
					+ "_32.gif";
			icon = getToolBarImage(iconName, border);
			if (icon == null) {
				Application.debug("icon missing for mode " + modeText + " ("
						+ mode + ")");
			}
		}
		return icon;
	}
	
	public boolean onlyGraphicsViewShowing() {
		if(!hasFullGui()) {
			return true;
		}
		
		return getGuiManager().getLayout().isOnlyVisible(Application.VIEW_EUCLIDIAN);
	}

	public boolean showAlgebraInput() {
		return showAlgebraInput;
	}

	public void setShowAlgebraInput(boolean flag, boolean update) {
		showAlgebraInput = flag;
		
		if(update) {
			updateMenubar();
		}
	}
	
	public boolean showInputTop() {
		return showInputTop;
	}
	
	public void setShowInputTop(boolean flag, boolean update) {
		if(flag == showInputTop)
			return;
		
		showInputTop = flag;
		
		if(update && !isIniting())
			updateContentPane();
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
	
	public boolean showToolBarTop() {
		return showToolBarTop;
	}
	
	public void setShowToolBarTop(boolean flag) {
		if(flag == showToolBarTop)
			return;
		
		showToolBarTop = flag;
		
		if(!isIniting())
			updateContentPane();
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

		if (hasFullGui())
			getGuiManager().setShowAuxiliaryObjects(flag);
		
		updateMenubar();
	}

	public void setShowMenuBar(boolean flag) {
		showMenuBar = flag;
	}
	
	public void setShowToolBar(boolean toolbar) {
		showToolBar = toolbar;
		
		if(!isIniting()) {
			updateContentPane();
			updateMenubar();
		}
	}
	
	public void setShowToolBarNoUpdate(boolean toolbar) {
		showToolBar = toolbar;
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
		// don't allow undo when running with restricted permissions
		if (flag && !hasFullPermissions) {
			flag = false;
		}
		
		if (kernel.isUndoActive() == flag)
			return;
		
		kernel.setUndoActive(flag);
		if (flag) {
			kernel.initUndoInfo();
		}

		if (guiManager != null)
			getGuiManager().updateActions();

		isSaved = true;
	}

	public boolean isUndoActive() {
		return kernel.isUndoActive();
	}

	/**
	 * Enables or disables right clicking in this application. This is useful
	 * for applets.
	 */
	public void setRightClickEnabled(boolean flag) {
		rightClickEnabled = flag;
	}
	
	/**
	 * Enables or disables popups when multiple objects selected This is useful
	 * for applets.
	 */
	public void setChooserPopupsEnabled(boolean flag) {
		chooserPopupsEnabled = flag;
	}
	
	/**
	 * Enables or disables label dragging in this application. This is useful
	 * for applets.
	 */
	public void setLabelDragsEnabled(boolean flag) {
		labelDragsEnabled = flag;
	}

	final public boolean isRightClickEnabled() {
		return rightClickEnabled;
	}

	final public boolean areChooserPopupsEnabled() {
		return chooserPopupsEnabled;
	}

	final public boolean isLabelDragsEnabled() {
		return labelDragsEnabled;
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

		if (!initing) {
			if (appletImpl != null)
				SwingUtilities.updateComponentTreeUI(appletImpl.getJApplet());
			if (frame != null)
				SwingUtilities.updateComponentTreeUI(frame);
		}

		setMoveMode();
	}

	public void updateMenubar() {
		if (!showMenuBar || !hasFullGui())
			return;

		getGuiManager().updateMenubar();
		getGuiManager().updateActions();
		System.gc();
	}

	private void updateSelection() {
		if (!showMenuBar || !hasFullGui())
			return;

		getGuiManager().updateMenubarSelection();
		
		if (getEuclidianView().getMode() == EuclidianView.MODE_VISUAL_STYLE) {
			if (selectedGeos.size() > 0) {				
				getEuclidianView().getStyleBar().applyVisualStyle(selectedGeos);				
			}
		}
		
		if (getEuclidianView().getMode() == EuclidianView.MODE_MOVE) {			
				getEuclidianView().getStyleBar().updateStyleBar();	
				getGuiManager().getEuclidianView2().getStyleBar().updateStyleBar();
		}
		
		
	}

	public void updateMenuWindow() {
		if (!showMenuBar || !hasFullGui())
			return;

		getGuiManager().updateMenuWindow();
		getGuiManager().updateMenuFile();
		System.gc();
	}

	public void updateCommandDictionary() {
		// make sure all macro commands are in dictionary
		fillCommandDict();

		if (guiManager != null) {
			getGuiManager().updateAlgebraInput();
		}
	}

	/**
	 * // think about this Downloads the latest jar files from the GeoGebra
	 * server.
	 * 
	 * private void updateGeoGebra() { try { File dest = new File(codebase +
	 * Application.JAR_FILE); URL jarURL = new URL(Application.UPDATE_URL +
	 * Application.JAR_FILE);
	 * 
	 * if (dest.exists()) { // check if jarURL is newer then dest try {
	 * URLConnection connection = jarURL.openConnection(); if
	 * (connection.getLastModified() <= dest.lastModified()) { showMessage("No
	 * update available"); return; } } catch (Exception e) { // we don't know if
	 * the file behind jarURL is newer than dest // so don't do anything
	 * showMessage("No update available: " + (e.getMessage())); return; } } //
	 * copy JAR_FILE if (!CopyURLToFile.copyURLToFile(this, jarURL, dest))
	 * return; // copy properties file dest = new File(codebase +
	 * Application.PROPERTIES_FILE); jarURL = new URL(Application.UPDATE_URL +
	 * Application.PROPERTIES_FILE); if (!CopyURLToFile.copyURLToFile(this,
	 * jarURL, dest)) return; // copy jscl file dest = new File(codebase +
	 * Application.JSCL_FILE); jarURL = new URL(Application.UPDATE_URL +
	 * Application.JSCL_FILE); if (!CopyURLToFile.copyURLToFile(this, jarURL,
	 * dest)) return;
	 * 
	 * 
	 * showMessage("Update finished. Please restart GeoGebra."); } catch
	 * (Exception e) { showError("Update failed: "+ e.getMessage()); } }
	 */

	/**
	 * Clears the current construction. Used for File-New.
	 */
	public void clearConstruction() {
		if (isSaved() || saveCurrentFile()) {
			kernel.clearConstruction();
			
			kernel.initUndoInfo();
			setCurrentFile(null);
			setMoveMode();
		}
	}

	public void exit() {
		// glassPane is active: don't exit now!
		if (glassPaneListener != null)
			return;

		if (isSaved() || appletImpl != null || saveCurrentFile()) {
			if (appletImpl != null) {
				setApplet(appletImpl);
				appletImpl.showApplet();
			} else {
				frame.setVisible(false);
			}
		}
	}

	public synchronized void exitAll() {
		// glassPane is active: don't exit now!
		if (glassPaneListener != null)
			return;

		getGuiManager().exitAll();
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
		if (mode != EuclidianView.MODE_SELECTION_LISTENER)
			currentSelectionListener = null;

		if (guiManager != null)
			getGuiManager().setMode(mode);
		
		else if (euclidianView != null)
			euclidianView.setMode(mode);
	}

	final public int getMode() {
		return euclidianView.getMode();
	}

	/***************************************************************************
	 * SAVE / LOAD methodes
	 **************************************************************************/

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
			boolean success = loadXML(url.openStream(), isMacroFile);
			
			// set current file
			if (!isMacroFile && url.toExternalForm().startsWith("file")) {
				String path = url.getPath();
				path = path.replaceAll("%20", " ");
				File f = new File(path);
				if (f.exists())
					setCurrentFile(f);
			}
			
			return success;
		} catch (Exception e) {
			setCurrentFile(null);
			return false;
		}
	}
	
	public boolean loadXML(byte [] zipFile) {
		try {
			myXMLio.readZipFromString(zipFile);
			
			kernel.initUndoInfo();
			isSaved = true;
			setCurrentFile(null);
			// command list may have changed due to macros
			updateCommandDictionary();
			return true;
		} catch (Exception err) {
			setCurrentFile(null);
			err.printStackTrace();
			return false;
		}
	}

	/*
	 * loads an XML file as a String
	 */
	public boolean loadXML(String xml) {
		try {
			myXMLio.processXMLString(xml, true, false);
			
			kernel.initUndoInfo();
			isSaved = true;
			setCurrentFile(null);
			// command list may have changed due to macros
			updateCommandDictionary();
			return true;
		} catch (Exception err) {
			setCurrentFile(null);
			err.printStackTrace();
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
	public boolean saveGeoGebraFile(File file) {
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
	
	public String getMacroXML() {
		ArrayList macros = kernel.getAllMacros();
		return myXMLio.getFullMacroXML(macros);
	}
	

	public void setXML(String xml, boolean clearAll) {
		if (clearAll)
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
		if (isUndoActive()) {
			kernel.storeUndoInfo();
			isSaved = false;
		}
	}

	public void restoreCurrentUndoInfo() {
		if (isUndoActive()) {
			kernel.restoreCurrentUndoInfo();
			isSaved = false;
		}
	}

	/*
	 * final public void clearAll() { // load preferences
	 * GeoGebraPreferences.loadXMLPreferences(this); updateContentPane(); //
	 * clear construction kernel.clearConstruction(); kernel.initUndoInfo();
	 * 
	 * isSaved = true; System.gc(); }
	 */

	/**
	 * Returns gui settings in XML format
	 */
	public String getGuiXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();
		sb.append("<gui>\n");

		// save the dimensions of the current window
		sb.append("\t<window width=\"");
		
		if(frame != null && frame.getWidth() > 0)
			sb.append(frame.getWidth());
		else
			sb.append(800);
		
		sb.append("\" height=\"");
		
		if(frame != null && frame.getHeight() > 0)
			sb.append(frame.getHeight());
		else
			sb.append(600);
		
		sb.append("\" />");
		
		getGuiManager().getLayout().getXml(sb, asPreference);

		// labeling style
		if (labelingStyle != ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC) {
			sb.append("\t<labelingStyle ");
			sb.append(" val=\"");
			sb.append(labelingStyle);
			sb.append("\"/>\n");
		}

		// just save font size as preference
		if(asPreference) {
			sb.append("\t<font ");
			sb.append(" size=\"");
			sb.append(appFontSize);
			sb.append("\"/>\n");
		}

		sb.append(getConsProtocolXML());

		sb.append("</gui>\n");

		return sb.toString();
	}

	public String getCompleteUserInterfaceXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();

		// save gui tag settings
		sb.append(getGuiXML(asPreference));

		// save euclidianView settings
		getEuclidianView().getXML(sb);

		// save spreadsheetView settings
		if (getGuiManager().hasSpreadsheetView()){
			getGuiManager().getSpreadsheetViewXML(sb);
		}
		
		// coord style, decimal places settings etc
		kernel.getKernelXML(sb);

		// save cas view seeting and cas session
//		if (casView != null) {
//			sb.append(((geogebra.cas.view.CASView) casView).getGUIXML());
//			sb.append(((geogebra.cas.view.CASView) casView).getSessionXML());
//		}

		return sb.toString();
	}

	public String getConsProtocolXML() {
		if (guiManager == null)
			return "";

		StringBuilder sb = new StringBuilder();

		// construction protocol
		if (getGuiManager().isUsingConstructionProtocol()) {
			getGuiManager().getConsProtocolXML(sb);
		}

		return sb.toString();
	}

	/**
	 * Returns the CodeBase URL.
	 */
	public static URL getCodeBase() {
		if (codebase == null) {
			initCodeBase();
		}
		return codebase;
	}
	
	private static URL codebase;
	private static boolean hasFullPermissions = false;
	private static boolean runningFromJar = false;
	
	private static void initCodeBase() {
		try {
			// application codebase
			String path = GeoGebra.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm();
			// remove "geogebra.jar" from end of codebase string
			if (path.endsWith(JAR_FILES[0])) {
				runningFromJar = true;
				path = path.substring(0, path.length() -  JAR_FILES[0].length());
				
			}
			// set codebase
			codebase = new URL(path);	
			hasFullPermissions = true;
		} 
		catch (Exception e) {
			System.out.println("GeoGebra is running with restricted permissions.");
			hasFullPermissions = false;
			
			if (appletImpl != null) {
				// applet codebase
				codebase = appletImpl.getJApplet().getCodeBase();
			}
		}
		
	}
	
	final public static boolean isWebstart() {
		if (codebase == null) initCodeBase();
		return codebase.toString().startsWith(GeoGebra.GEOGEBRA_WEBSITE+"webstart");
	}
	
	final public static boolean hasFullPermissions() {
		return hasFullPermissions;
	}
	
	/* selection handling */

	final public int selectedGeosSize() {
		return selectedGeos.size();
	}

	final public ArrayList<GeoElement> getSelectedGeos() {
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

	final public void invertSelection() {

		Iterator it = kernel.getConstruction().getGeoSetLabelOrder().iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();
			if (selectedGeos.contains(geo))
				removeSelectedGeo(geo, false);
			else
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

	final public void showHideSelection() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = (GeoElement) selectedGeos.get(i);
			geo.setEuclidianVisible(!geo.isEuclidianVisible());
			geo.update();
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	final public void showHideSelectionLabels() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = (GeoElement) selectedGeos.get(i);
			geo.setLabelVisible(!geo.isLabelVisible());
			geo.update();
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

	final public void selectNextGeo() {
		
		TreeSet<GeoElement> tree = kernel.getConstruction().getGeoSetLabelOrder();
		
		TreeSet<GeoElement> copy = new TreeSet(tree);
		
		Iterator<GeoElement> it = copy.iterator();
		
		// remove geos that don't have isSelectionAllowed()==true
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (!geo.isSelectionAllowed()) tree.remove(geo);
		}
		
		it = tree.iterator();

		// none selected, select first geo
		if (selectedGeos.size() == 0) {
			if (it.hasNext()) addSelectedGeo(it.next());
			return;
		}
		
		if (selectedGeos.size() != 1) return;
		
		// one selected, select next one
		GeoElement selGeo = selectedGeos.get(0);
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (selGeo == geo) {
				removeSelectedGeo(selGeo);
				if (!it.hasNext()) it = tree.iterator();					
				addSelectedGeo(it.next());
				break;
			}
		}
	}

	final public void selectLastGeo() {
		if (selectedGeos.size() != 1) return;
		GeoElement selGeo = selectedGeos.get(0);
		GeoElement lastGeo = null;
		TreeSet<GeoElement> tree = kernel.getConstruction().getGeoSetLabelOrder();
		TreeSet<GeoElement> copy = new TreeSet(tree);
		Iterator<GeoElement> it = copy.iterator();
		
		// remove geos that don't have isSelectionAllowed()==true
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (!geo.isSelectionAllowed()) tree.remove(geo);
		}
		
		it = tree.iterator();
		while (it.hasNext()) { lastGeo = it.next(); }
			
		it = tree.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (selGeo == geo) {
				removeSelectedGeo(selGeo);			
				addSelectedGeo(lastGeo);
				break;
			}
			lastGeo = geo;
		}
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

	/* Event dispatching */
	private GlassPaneListener glassPaneListener;

	public void startDispatchingEventsTo(JComponent comp) {
		if (guiManager != null) {
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
		if (mainComp == frame)
			return frame.getGlassPane();
		else if (appletImpl != null && mainComp == appletImpl.getJApplet())
			return appletImpl.getJApplet().getGlassPane();
		else
			return null;
	}
	
	public void setGlassPane(Component component) {
		if (appletImpl != null && mainComp == appletImpl.getJApplet())
			appletImpl.getJApplet().setGlassPane(component);
		else if (mainComp == frame)
			frame.setGlassPane(component);
	}

	public Container getContentPane() {
		if (mainComp == frame)
			return frame.getContentPane();
		else if (appletImpl != null && mainComp == appletImpl.getJApplet())
			return appletImpl.getJApplet().getContentPane();
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
		
		controlDown = isControlDown(e); //G.Sturr 2010-5-30
		
		// check if key event came from this main component
		// (needed to take care of multiple application windows or applets)
		Component eventPane = SwingUtilities.getRootPane(e.getComponent());
		Component mainPane = SwingUtilities.getRootPane(mainComp);
		if (eventPane != mainPane && !getGuiManager().getLayout().inExternalWindow(eventPane)) {			
			// ESC from dialog: close it			
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				Component rootComp = SwingUtilities.getRoot(e.getComponent());
				if (rootComp instanceof JDialog) {					
					((JDialog) rootComp).setVisible(false);
					return true;
				}
			}
			
			// key event came from another window or applet: ignore it								
			return false;			
		}						

		// if the glass pane is visible, don't do anything
		// (there might be an animation running)
		Component glassPane = getGlassPane();
		if (glassPane != null && glassPane.isVisible())
			return false;

		// handle global keys like ESC and function keys		
		return getGlobalKeyDispatcher().dispatchKeyEvent(e);
	}
	
	final public GlobalKeyDispatcher getGlobalKeyDispatcher() {
		if (globalKeyDispatcher == null)
			globalKeyDispatcher = new GlobalKeyDispatcher(this);
		return globalKeyDispatcher;
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
	 * Loads text file and returns content as String.
	 */
	public String loadTextFile(String s) {
        StringBuilder sb = new StringBuilder();        
        try {
          InputStream is = Application.class.getResourceAsStream(s);
          BufferedReader br = new BufferedReader(new InputStreamReader(is));
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
	
	public final boolean isOnTheFlyPointCreationActive() {
		return isOnTheFlyPointCreationActive;
	}

	public final void setOnTheFlyPointCreationActive(boolean isOnTheFlyPointCreationActive) {
		this.isOnTheFlyPointCreationActive = isOnTheFlyPointCreationActive;
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
	
	public ScriptManager getScriptManager() {
		if (scriptManager == null) {
			scriptManager = new ScriptManager(this);
		}
		return scriptManager;
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
	
	public static void debug(Object s) {
		if(s==null)
			doDebug("<null>", false, false, 0);
		else
			doDebug(s.toString(), false, false, 0);
	}
	
	public static void debug(Object s[]) {
		debug(s, 0);
	}
	
	static StringBuilder debugSb = null;
	
	public static void debug(Object[] s, int level) {
		if (debugSb == null) debugSb = new StringBuilder(); 
		else debugSb.setLength(0);
		
		for (int i = 0 ; i < s.length ; i++) {
			debugSb.append(s[i]);
			debugSb.append('\n');
		}
		
		debug(debugSb, level);
	}
	
	public static void debug(Object s, int level) {
		doDebug(s.toString(), false, false, level);
	}
	
	public static void debug(Object s, boolean showTime, boolean showMemory, int level) {
		doDebug(s.toString(), showTime, showMemory, level);
	}
	
	// Michael Borcherds 2008-06-22
	private static void doDebug(String s, boolean showTime, boolean showMemory, int level) {
		if (s == null) s = "<null>";
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();

		// String calleeMethod = elements[0].getMethodName();
		String callerMethodName = elements[2].getMethodName();
		String callerClassName = elements[2].getClassName();
		
		StringBuilder sb = new StringBuilder("*** Message from ");
		sb.append("[");
		sb.append(callerClassName);
		sb.append(".");
		sb.append(callerMethodName);
		sb.append("]");

		if (showTime) {
			Calendar calendar = new GregorianCalendar();
			int min = calendar.get(Calendar.MINUTE);
			String minS = (min < 10) ? "0" + min : "" + min;
			int sec = calendar.get(Calendar.SECOND);
			String secS = (sec < 10) ? "0" + sec : "" + sec;
	
			sb.append(" at ");
			sb.append(calendar.get(Calendar.HOUR));
			sb.append(":");
			sb.append(minS);
			sb.append(":");
			sb.append(secS);
		}
		
		if (showMemory) {
			sb.append(" free memory: ");
			sb.append(Runtime.getRuntime().freeMemory());
		}
			
		PrintStream debug = System.out;
		
		if (level > 0) debug = System.err;

		// multi line message
		if (s.indexOf("\n") > -1) {
			debug.println(sb.toString());
			debug.println(s);
			debug.println("*** END Message.");
		}
		// one line message
		else {
			debug.println(sb.toString());
			debug.print("\t");
			debug.println(s);
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
	public static String OS = System.getProperty("os.name").toLowerCase(
			Locale.US);
	public static boolean MAC_OS = OS.startsWith("mac");
	public static boolean WINDOWS = OS.startsWith("windows"); // Michael Borcherds 2008-03-21
	
			/* current possible values http://mindprod.com/jgloss/properties.html
			 * AIX
		Digital Unix
		FreeBSD
		HP UX
		Irix
		Linux
		Mac OS
		Mac OS X
		MPE/iX
		Netware 4.11
		OS/2
		Solaris
		Windows 2000
		Windows 7
		Windows 95
		Windows 98
		Windows NT
		Windows Vista
		Windows XP */
	
	// make sure still works in the future on eg Windows 9 
	public static boolean WINDOWS_VISTA_OR_LATER = WINDOWS && !OS.startsWith("windows 2000")
														   && !OS.startsWith("windows 95")
														   && !OS.startsWith("windows 98")
														   && !OS.startsWith("windows nt")
														   && !OS.startsWith("windows xp");
	/*
	 * needed for padding in Windows XP or earlier
	 * without check, checkbox isn't shown in Vista, Win 7
	 */
    public void setEmptyIcon(JCheckBoxMenuItem cb) {
        if (!WINDOWS_VISTA_OR_LATER)
                cb.setIcon(getEmptyIcon());
    }


	
	/*
	 * check for alt pressed (but not ctrl)
	 * (or ctrl but not alt on MacOS)
	 */
	public static boolean isAltDown(InputEvent e) {
		// we don't want to act when AltGr is down
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown())
			return false;

		return MAC_OS ? e.isControlDown() : e.isAltDown();
	}

	
	// G.Sturr 2010-5-30: Added global controlDown flag.
	// Application.dispatchKeyEvent sets this on every keyEvent.
	// The flag is needed for ctrl-select in the spreadsheet.
	
	private static boolean controlDown = false;
	
	public static boolean getControlDown () {
		return controlDown;
	}

	//END G.Sturr
	
	
	public static boolean isControlDown(InputEvent e) {

		/*
		 * debug("isMetaDown = "+e.isMetaDown()); debug("isControlDown =
		 * "+e.isControlDown()); debug("isShiftDown = "+e.isShiftDown());
		 * debug("isAltDown = "+e.isAltDown()); debug("isAltGrDown =
		 * "+e.isAltGraphDown()); debug("fakeRightClick = "+fakeRightClick);
		 */

		if (fakeRightClick)
			return false;

		boolean ret = (MAC_OS && e.isMetaDown()) // Mac: meta down for
				// multiple
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
		 * debug("isMetaDown = "+e.isMetaDown()); debug("isControlDown =
		 * "+e.isControlDown()); debug("isShiftDown = "+e.isShiftDown());
		 * debug("isAltDown = "+e.isAltDown()); debug("isAltGrDown =
		 * "+e.isAltGraphDown()); debug("isPopupTrigger = "+e.isPopupTrigger());
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
				if (!geo.isFixed())
					geo.removeOrSetUndefinedIfHasFixedDescendent();
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
		} catch (java.lang.OutOfMemoryError t) {
			Application.debug("Out of memory");
			System.gc();
			setDefaultCursor();
			//t.printStackTrace();
			// TODO change to OutOfMemoryError
			showError("LoadFileFailed");
			return null;
		}
	}

	// code from freenet
	// http://emu.freenetproject.org/pipermail/cvs/2007-June/040186.html
	// GPL2
	public static String convertToHex(byte[] data) {
		StringBuilder buf = new StringBuilder();
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

	public static String removeExtension(String fileName) {
		if (fileName == null)
			return null;
		int dotPos = fileName.indexOf('.');

		if (dotPos <= 0)
			return fileName;
		else
			return fileName.substring(0, dotPos);
	}

	public final LowerCaseDictionary getCommandDictionary() {
		return commandDict;
	}
	
	/**
	 * Returns an array of command dictionaries corresponding to the categorized 
	 * sub command sets created in CommandDispatcher.  
	 */
	public final LowerCaseDictionary[] getSubCommandDictionary() {
		return subCommandDict;
	}
	
	final static int MEMORY_CRITICAL = 100*1024;
	static Runtime runtime = Runtime.getRuntime();
	
	public boolean freeMemoryIsCritical() {
		
		if (runtime.freeMemory() > MEMORY_CRITICAL) return false;
		
		System.gc();
		
		return runtime.freeMemory() < MEMORY_CRITICAL;
	}
	
	public long freeMemory() {
		return runtime.freeMemory();
	}
	
	public long getHeapSize() {
		return runtime.maxMemory();
	}
	
	public void traceMethodsOn(boolean on) {
		runtime.traceMethodCalls(on);
	}
	
	public void copyGraphicsViewToClipboard() {
		
		clearSelectedGeos();
		
		Thread runner = new Thread() {
			public void run() {		
				setWaitCursor();
				
				// copy drawing pad to the system clipboard
				Image img = getEuclidianView().getExportImage(2d);
				ImageSelection imgSel = new ImageSelection(img);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);	

				// this doesn't work very well
				// eg can't paste into Paint (WinXP)
				//GraphicExportDialog export = new GraphicExportDialog(app);
				//export.setDPI("150");
				//export.exportPNG(true);

				
				setDefaultCursor();
			}
		};
		runner.start();						    			    								
		
	}
	
	private Rectangle screenSize = null;
	
	/*
	 * gets the screensize (taking into account toolbars etc)
	 */
	public Rectangle getScreenSize() {
		if (screenSize == null) {
			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
			screenSize = env.getMaximumWindowBounds(); 
		}
		
		return screenSize;

	}
	
	
	Cursor transparentCursor = null;
	public boolean useTransparentCursorWhenDragging = false;
	
	public void setUseTransparentCursorWhenDragging(boolean useTransparentCursorWhenDragging) {
		this.useTransparentCursorWhenDragging = useTransparentCursorWhenDragging;
	}
	
	public Cursor getTransparentCursor() {
		
		if (transparentCursor == null) {
			int[] pixels = new int[16 * 16];
			Image image = Toolkit.getDefaultToolkit().createImage(
			        new MemoryImageSource(16, 16, pixels, 0, 16));
			
			 transparentCursor =
			        Toolkit.getDefaultToolkit().createCustomCursor
			             (image, new Point(0, 0), "invisibleCursor");
		}
		return transparentCursor;
	}
	
	Cursor eraserCursor = null;
	
	public Cursor getEraserCursor() {
		
		if (eraserCursor == null) {
			
			Dimension dim = Toolkit.getDefaultToolkit().getBestCursorSize(48, 48);
			
			Application.debug("getBestCursorSize = "+dim.width+" "+dim.width);
			
			int size = Math.max(dim.width, dim.height);
			
			size = Math.max(48, size); // basically we want a size of 48
			
			Image image = new BufferedImage(size,size, BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g = (Graphics2D) image.getGraphics();
			EuclidianView.setAntialiasing(g);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);	

			g.setColor(Color.DARK_GRAY);
			g.setStroke(EuclidianView.getStroke(2,  EuclidianView.LINE_TYPE_FULL ));
			
			g.drawOval(10 * size / 48, 10 * size / 48, 30 * size / 48, 30 * size / 48);
			
			eraserCursor =
			        Toolkit.getDefaultToolkit().createCustomCursor
			             (image, new Point(size / 2, size / 2), "eraserCursor");
		}
		return eraserCursor;
	}
	
	private HashMap colors = null;

	public HashMap<String, Color> getColorsHashMap() {
		
		if (colors == null) {
			 colors = new HashMap();
				
			 // HTML 3.2
		        colors.put("AQUA", new Color(0x00FFFF));
		        colors.put("BLACK", new Color(0x000000));
		        colors.put("BLUE", new Color(0x0000FF));
		        colors.put("FUCHSIA", new Color(0xFF00FF));
		        colors.put("GRAY", new Color(0x808080));
		        colors.put("GREEN", new Color(0x008000));
		        colors.put("LIME", new Color(0x00FF00));
		        colors.put("MAROON", new Color(0x800000));
		        colors.put("NAVY", new Color(0x000080));
		        colors.put("OLIVE", new Color(0x808000));
		        colors.put("PURPLE", new Color(0x800080));
		        colors.put("RED", new Color(0xFF0000));
		        colors.put("SILVER", new Color(0xC0C0C0));
		        colors.put("TEAL", new Color(0x008080));
		        colors.put("WHITE", new Color(0xFFFFFF));
		        colors.put("YELLOW", new Color(0xFFFF00));

		        colors.put("ALICEBLUE", new Color(0xEFF7FF));
		        colors.put("ANTIQUEWHITE", new Color(0xF9E8D2));
		        colors.put("AQUAMARINE", new Color(0x43B7BA));
		        colors.put("AZURE", new Color(0xEFFFFF));
		        colors.put("BEIGE", new Color(0xF5F3D7));
		        colors.put("BISQUE", new Color(0xFDE0BC));
		        colors.put("BLANCHEDALMOND", new Color(0xFEE8C6));
		        colors.put("BLUEVIOLET", new Color(0x7931DF));
		        colors.put("BROWN", new Color(0x980516));
		        colors.put("BURLYWOOD", new Color(0xEABE83));
		        colors.put("CADETBLUE", new Color(0x578693));
		        colors.put("CHARTREUSE", new Color(0x8AFB17));
		        colors.put("CHOCOLATE", new Color(0xC85A17));
		        colors.put("CORAL", new Color(0xF76541));
		        colors.put("CORNFLOWERBLUE", new Color(0x151B8D));
		        colors.put("CORNSILK", new Color(0xFFF7D7));
		        colors.put("CRIMSON", new Color(0xE41B17));
		        colors.put("CYAN", new Color(0x00FFFF));
		        colors.put("DARKBLUE", new Color(0x2F2F4F));
		        colors.put("DARKCYAN", new Color(0x57FEFF));
		        colors.put("DARKGOLDENROD", new Color(0xAF7817));
		        colors.put("DARKGRAY", new Color(0x7A7777));
		        colors.put("DARKGREEN", new Color(0x254117));
		        colors.put("DARKKHAKI", new Color(0xB7AD59));
		        colors.put("DARKMAGENTA", new Color(0xF43EFF));
		        colors.put("DARKOLIVEGREEN", new Color(0xCCFB5D));
		        colors.put("DARKORANGE", new Color(0xF88017));
		        colors.put("DARKORCHID", new Color(0x7D1B7E));
		        colors.put("DARKRED", new Color(0xE41B17));
		        colors.put("DARKSALMON", new Color(0xE18B6B));
		        colors.put("DARKSEAGREEN", new Color(0x8BB381));
		        colors.put("DARKSLATEBLUE", new Color(0x2B3856));
		        colors.put("DARKSLATEGRAY", new Color(0x253856));
		        colors.put("DARKTURQUOISE", new Color(0x3B9C9C));
		        colors.put("DARKVIOLET", new Color(0x842DCE));
		        colors.put("DEEPPINK", new Color(0xF52887));
		        colors.put("DEEPSKYBLUE", new Color(0x3BB9FF));
		        colors.put("DIMGRAY", new Color(0x463E41));
		        colors.put("DODGERBLUE", new Color(0x1589FF));
		        colors.put("FIREBRICK", new Color(0x800517));
		        colors.put("FLORALWHITE", new Color(0xFFF9EE));
		        colors.put("FORESTGREEN", new Color(0x4E9258));
		        colors.put("GAINSBORO", new Color(0xD8D9D7));
		        colors.put("GHOSTWHITE", new Color(0xF7F7FF));
		        colors.put("GOLD", new Color(0xD4A017));
		        colors.put("GOLDENROD", new Color(0xEDDA74));
		        colors.put("GREENYELLOW", new Color(0xB1FB17));
		        colors.put("HONEYDEW", new Color(0xF0FEEE));
		        colors.put("INDIANRED", new Color(0x5E2217));
		        colors.put("INDIGO", new Color(0x307D7E));
		        colors.put("IVORY", new Color(0xFFFFEE));
		        colors.put("KHAKI", new Color(0xADA96E));
		        colors.put("LAVENDER", new Color(0xE3E4FA));
		        colors.put("LAVENDERBLUSH", new Color(0xFDEEF4));
		        colors.put("LAWNGREEN", new Color(0x87F717));
		        colors.put("LEMONCHIFFON", new Color(0xFFF8C6));
		        colors.put("LIGHTBLUE", new Color(0xADDFFF));
		        colors.put("LIGHTCORAL", new Color(0xE77471));
		        colors.put("LIGHTCYAN", new Color(0xE0FFFF));
		        colors.put("LIGHTGOLDENRODYELLOW", new Color(0xFAF8CC));
		        colors.put("LIGHTGREEN", new Color(0xCCFFCC));
		        colors.put("LIGHTGRAY", Color.LIGHT_GRAY);
		        colors.put("LIGHTPINK", new Color(0xFAAFBA));
		        colors.put("LIGHTSALMON", new Color(0xF9966B));
		        colors.put("LIGHTSEAGREEN", new Color(0x3EA99F));
		        colors.put("LIGHTSKYBLUE", new Color(0x82CAFA));
		        colors.put("LIGHTSLATEGRAY", new Color(0x6D7B8D));
		        colors.put("LIGHTSTEELBLUE", new Color(0x728FCE));
		        colors.put("LIGHTYELLOW", new Color(0xFFFEDC));
		        colors.put("LIMEGREEN", new Color(0x41A317));
		        colors.put("LINEN", new Color(0xF9EEE2));
		        colors.put("MAGENTA", new Color(0xFF00FF));
		        colors.put("MEDIUMAQUAMARINE", new Color(0x348781));
		        colors.put("MEDIUMBLUE", new Color(0x152DC6));
		        colors.put("MEDIUMORCHID", new Color(0xB048B5));
		        colors.put("MEDIUMPURPLE", new Color(0x8467D7));
		        colors.put("MEDIUMSEAGREEN", new Color(0x306754));
		        colors.put("MEDIUMSLATEBLUE", new Color(0x5E5A80));
		        colors.put("MEDIUMSPRINGGREEN", new Color(0x348017));
		        colors.put("MEDIUMTURQUOISE", new Color(0x48CCCD));
		        colors.put("MEDIUMVIOLETRED", new Color(0xCA226B));
		        colors.put("MIDNIGHTBLUE", new Color(0x151B54));
		        colors.put("MINTCREAM", new Color(0xF5FFF9));
		        colors.put("MISTYROSE", new Color(0xFDE1DD));
		        colors.put("MOCCASIN", new Color(0xFDE0AC));
		        colors.put("NAVAJOWHITE", new Color(0xFDDAA3));
		        colors.put("OLDLACE", new Color(0xFCF3E2));
		        colors.put("OLIVEDRAB", new Color(0x658017));
		        colors.put("ORANGE", new Color(0xF87A17));
		        colors.put("ORANGERED", new Color(0xF63817));
		        colors.put("ORCHID", new Color(0xE57DED));
		        colors.put("PALEGOLDENROD", new Color(0xEDE49E));
		        colors.put("PALETURQUOISE", new Color(0xAEEBEC));
		        colors.put("PALEVIOLETRED", new Color(0xD16587));
		        colors.put("PAPAYAWHIP", new Color(0xFEECCF));
		        colors.put("PEACHPUFF", new Color(0xFCD5B0));
		        colors.put("PERU", new Color(0xC57726));
		        colors.put("PINK", new Color(0xFAAFBE));
		        colors.put("PLUM", new Color(0xB93B8F));
		        colors.put("POWDERBLUE", new Color(0xADDCE3));
		        colors.put("ROSYBROWN", new Color(0xB38481));
		        colors.put("ROYALBLUE", new Color(0x2B60DE));
		        colors.put("SADDLEBROWN", new Color(0xF63526));
		        colors.put("SALMON", new Color(0xF88158));
		        colors.put("SANDYBROWN", new Color(0xEE9A4D));
		        colors.put("SEAGREEN", new Color(0x4E8975));
		        colors.put("SEASHELL", new Color(0xFEF3EB));
		        colors.put("SIENNA", new Color(0x8A4117));
		        colors.put("SKYBLUE", new Color(0x6698FF));
		        colors.put("SLATEBLUE", new Color(0x737CA1));
		        colors.put("SLATEGRAY", new Color(0x657383));
		        colors.put("SNOW", new Color(0xFFF9FA));
		        colors.put("SPRINGGREEN", new Color(0x4AA02C));
		        colors.put("STEELBLUE", new Color(0x4863A0));
		        colors.put("TAN", new Color(0xD8AF79));
		        colors.put("THISTLE", new Color(0xD2B9D3));
		        colors.put("TOMATO", new Color(0xF75431));
		        colors.put("TURQUOISE", new Color(0x43C6DB));
		        colors.put("VIOLET", new Color(0x8D38C9));
		        colors.put("WHEAT", new Color(0xF3DAA9));
		        colors.put("WHITESMOKE", new Color(0xFFFFFF));
		        colors.put("YELLOWGREEN", new Color(0x52D017));
		    }
		
		return colors;
	}
	
	private static boolean virtualKeyboardActive = false;
	
	public static boolean isVirtualKeyboardActive() {
		return virtualKeyboardActive;
	}
	
	public static void setVirtualKeyboardActive(boolean active) {
		virtualKeyboardActive = active;
		//Application.debug("VK active:"+virtualKeyboardActive);
	}
	
	private static boolean handwritingRecognitionActive = false;
	
	public static boolean isHandwritingRecognitionActive() {
		return handwritingRecognitionActive;
	}
	
	public static void setHandwritingRecognitionActive(boolean active) {
		handwritingRecognitionActive = active;
	}
	
	private static boolean handwritingRecognitionAutoAdd = true;
	
	public static boolean isHandwritingRecognitionAutoAdd() {
		return handwritingRecognitionAutoAdd;
	}
		
	public static void setHandwritingRecognitionAutoAdd(boolean show) {
		handwritingRecognitionAutoAdd = show;
	}
	
	private static boolean handwritingRecognitionTimedAdd = false;
	
	public static boolean isHandwritingRecognitionTimedAdd() {
		return handwritingRecognitionTimedAdd;
	}
		
	public static void setHandwritingRecognitionTimedAdd(boolean show) {
		handwritingRecognitionTimedAdd = show;
	}
	
	private static boolean handwritingRecognitionTimedRecognise = false;
	
	public static boolean isHandwritingRecognitionTimedRecognise() {
		return handwritingRecognitionTimedRecognise;
	}
		
	public static void setHandwritingRecognitionTimedRecognise(boolean show) {
		handwritingRecognitionTimedRecognise = show;
	}
	
	private static boolean miniPropertiesActive = true;
	
	public static boolean isMiniPropertiesActive() {
		return miniPropertiesActive;
	}
	
	public static void setMiniPropertiesActive(boolean active) {
		miniPropertiesActive = active;
		//Application.debug("miniprops active:"+miniPropertiesActive);
	}

	// determines which CAS is being used
	final public static int CAS_MATHPIPER = ExpressionNode.STRING_TYPE_MATH_PIPER;
	final public static int CAS_MAXIMA = ExpressionNode.STRING_TYPE_MAXIMA;

	
	public void setDefaultCAS(int CAS) {
		boolean success = false;
		if (CAS == CAS_MAXIMA) {
			Application.debug("Attempting to set CAS=Maxima");
			success = setMaximaCAS();
		}
		
		// fallback / default option
		if (!success) {
			Application.debug("Attempting to set CAS=MathPiper");
			kernel.setDefaultCAS(CAS_MATHPIPER);	
		}
		
	}
	
	public MaximaConfiguration maximaConfiguration = null;

	/* eg --maximaPath=
	 */
	private void setMaximaPath(String optionValue) {
		maximaConfiguration = new MaximaConfiguration();
		maximaConfiguration.setMaximaExecutablePath(optionValue);
		kernel.setDefaultCAS(CAS_MAXIMA);				
	}

	
	/* eg --CAS=maxima
	 */
	private boolean setMaximaCAS(){
		
		maximaConfiguration = JacomaxAutoConfigurator.guessMaximaConfiguration();

		if (maximaConfiguration != null) {		
			kernel.setDefaultCAS(CAS_MAXIMA);		
			return true;
		}
		
		return false;
	}
	
	/*
	 * stops eg TAB automatically transferring focus between panes
	 */
	public void removeTraversableKeys(JPanel p) {
	    Set<AWTKeyStroke> set = p.getFocusTraversalKeys(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS);
	    set.clear();
	    p.setFocusTraversalKeys(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, set);
	    p.setFocusTraversalKeys(KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS, set);
	    p.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, set);
	    p.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set);

	}
	
	LogManager logManager;
	//String logFile = DownloadManager.getTempDir()+"GeoGebraLog.txt";
	//public String logFile = "c:\\GeoGebraLog.txt";
	public StringBuilder logFile = null;
	
	/*
	 * code from http://blogs.sun.com/nickstephen/entry/java_redirecting_system_out_and
	 */
	private void setUpLogging() {
		
	    // initialize logging to go to rolling log file
        logManager = LogManager.getLogManager();
        logManager.reset();
        
        logFile = new StringBuilder(30);
        
        logFile.append(DownloadManager.getTempDir());
        logFile.append("GeoGebraLog_");
        // randomize filename
        for (int i = 0 ; i < 10 ; i++) logFile.append((char)('a'+Math.round(Math.random()*25)));
        logFile.append(".txt");
        
        Application.debug(logFile.toString());
    

        // log file max size 10K, 1 file, append-on-open
        Handler fileHandler;
		try {
			fileHandler = new FileHandler(logFile.toString(), 10000, 1, false);
		} catch (Exception e) {
			logFile = null;
			return;
			
		} 
        fileHandler.setFormatter(new SimpleFormatter());
        Logger.getLogger("").addHandler(fileHandler);
        
        
        

        // preserve old stdout/stderr streams in case they might be useful      
        //PrintStream stdout = System.out;                                        
        //PrintStream stderr = System.err;                                        

        // now rebind stdout/stderr to logger                                   
        Logger logger;                                                          
        LoggingOutputStream los;                                                

        logger = Logger.getLogger("stdout");                                    
        los = new LoggingOutputStream(logger, StdOutErrLevel.STDOUT);           
        System.setOut(new PrintStream(los, true));                              

        logger = Logger.getLogger("stderr");                                    
        los= new LoggingOutputStream(logger, StdOutErrLevel.STDERR);            
        System.setErr(new PrintStream(los, true)); 
        // show stdout going to logger
        //System.out.println("Hello world!");

        // now log a message using a normal logger
        //logger = Logger.getLogger("test");
        //logger.info("This is a test log message");

        // now show stderr stack trace going to logger
        //try {
        //    throw new RuntimeException("Test");
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}

        // and output on the original stdout
        //stdout.println("Hello on old stdout");
	}

	/*
	 * return folder that the jars are running from
	 * eg needed to find local Maxima install
	 */
	public static String getCodeBaseFolder() {
		String codeBaseFolder = getCodeBase().toString();

		if (!codeBaseFolder.startsWith("file:/")) return null;
		
		// change %20 to <space>
		if (WINDOWS) {
			codeBaseFolder = codeBaseFolder.replaceAll("%20", " ");
		}

		// strip "file:/", leave leading / for Mac & Linux
		return codeBaseFolder.substring(WINDOWS ? 6:5);
	}

	public void exportToLMS() {
		clearSelectedGeos();
		WorksheetExportDialog d = new WorksheetExportDialog(this); 		

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		JPanel appCP = getCenterPanel();
		int width, height;
		if (appCP != null) {
			width = appCP.getWidth();
			height = appCP.getHeight();
		} else {
			width = WorksheetExportDialog.DEFAULT_APPLET_WIDTH;
			height = WorksheetExportDialog.DEFAULT_APPLET_HEIGHT;
		}		

		clipboard.setContents(new StringSelection(d.getAppletTag(this, null, width, height, false, true, false)), null);
		d.setVisible(false);
		d.dispose();
		
		showMessage(getMenu("ClipboardMessage"));
		
	}

	/*
	 * gets a String from the clipboard
	 * @return null if not possible
	 */
	public String getStringFromClipboard() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		String str = null;
		try {
			str = (String)contents.getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
		} catch (IOException e) {
		}		
		return str;
	}

	public ImageManager getImageManager() {
		return imageManager;
	}

	
	private MidiManager midiSoundManager = null;

	public MidiManager getMidiSoundManager() {
		if(midiSoundManager == null){
			midiSoundManager = new MidiManager(this);
		}
		return midiSoundManager;
	}
	
	private static String CASVersionString = "MathPiper"; // default

	public static void setCASVersionString(String string) {
		CASVersionString = string;
		
	}

	public static String getCASVersionString() {
		return CASVersionString;
		
	}
	
	private boolean useBrowserForJavaScript = true;

	public void setUseBrowserForJavaScript(boolean useBrowserForJavaScript) {
		this.useBrowserForJavaScript = useBrowserForJavaScript;
	}

	public boolean useBrowserForJavaScript() {
		return useBrowserForJavaScript;
	}


	/**
	 * @return the blockUpdateScripts
	 */
	public boolean isBlockUpdateScripts() {
		return blockUpdateScripts;
	}


	/**
	 * @param blockUpdateScripts the blockUpdateScripts to set
	 */
	public void setBlockUpdateScripts(boolean blockUpdateScripts) {
		this.blockUpdateScripts = blockUpdateScripts;
	}




	private boolean blockUpdateScripts=false;

	
		
}


