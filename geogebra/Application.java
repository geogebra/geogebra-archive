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

import geogebra.algebra.AlgebraController;
import geogebra.algebra.AlgebraInput;
import geogebra.algebra.AlgebraView;
import geogebra.algebra.autocomplete.LowerCaseDictionary;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.AngleInputDialog;
import geogebra.gui.CheckboxCreationDialog;
import geogebra.gui.ConstructionProtocol;
import geogebra.gui.ConstructionProtocolNavigation;
import geogebra.gui.ContextMenuGeoElement;
import geogebra.gui.ContextMenuGraphicsWindow;
import geogebra.gui.FileDropTargetListener;
import geogebra.gui.GeoGebraPreferences;
import geogebra.gui.ImagePreview;
import geogebra.gui.InputDialog;
import geogebra.gui.InputHandler;
import geogebra.gui.PropertiesDialogGeoElement;
import geogebra.gui.PropertiesDialogGraphicsWindow;
import geogebra.gui.RedefineInputHandler;
import geogebra.gui.RenameInputHandler;
import geogebra.gui.SliderDialog;
import geogebra.gui.TextInputDialog;
import geogebra.gui.menubar.GeoGebraMenuBar;
import geogebra.gui.menubar.Menubar;
import geogebra.gui.toolbar.MyToolbar;
import geogebra.gui.toolbar.ToolbarConfigDialog;
import geogebra.gui.util.BrowserLauncher;
import geogebra.io.MyXMLio;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.kernel.Relation;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.util.CopyURLToFile;
import geogebra.util.ImageManager;
import geogebra.util.Util;
import geogebra.plugin.PluginManager;
import geogebra.plugin.GgbAPI;

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
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.FontUIResource;


public abstract class Application implements	KeyEventDispatcher {

    public static final String buildDate = "Januar 26, 2008";
	
    public static final String versionString = "3.1 Pre-Release";    
    public static final String XML_FILE_FORMAT = "3.0";    
  
    // GeoGebra jar files    
    public static final String [] JAR_FILES = 
    	{ "geogebra.jar",  
    	  "geogebra_properties.jar",
    //	  "geogebra_cas.jar",
    	  "geogebra_export.jar"
    	};
 
	public final static String GEOGEBRA_WEBSITE = "http://www.geogebra.org/";
		
	// update URL
	//public static final String UPDATE_URL = "http://www.geogebra.org/webstart/unpacked/";
    
    // supported GUI languages (from properties files)
    public static ArrayList supportedLocales = new ArrayList();
    static {
    	supportedLocales.add( new Locale("ar") );           // Arabic
    	supportedLocales.add( new Locale("eu") );           // Basque
    	supportedLocales.add( new Locale("bs") );          	// Bosnian
    	supportedLocales.add( new Locale("bg") );          	// Bulgarian
    	supportedLocales.add( new Locale("ca") );           // Catalan
        // supportedLocales.add( new Locale("zh") );          	// Chinese (Simplified)
        supportedLocales.add( new Locale("zh","TW") );      // Chinese (Traditional)
        supportedLocales.add( new Locale("hr") );          	// Croatian
    	supportedLocales.add( new Locale("cz") );          	// Czeck
    	supportedLocales.add( new Locale("da") );     	 	// Danish   
    	supportedLocales.add( new Locale("nl") );     	 	// Dutch
    	supportedLocales.add( new Locale("en"));          	// English
    	supportedLocales.add( new Locale("en", "UK"));       // English (UK)
    	supportedLocales.add( new Locale("et"));          	// Estonian
    	supportedLocales.add( new Locale("fi") );  			// Finnish
    	supportedLocales.add( new Locale("fr") );     		 // French
    	supportedLocales.add( new Locale("gl") );           // Galician
        supportedLocales.add( new Locale("de") );          	// German   
    	supportedLocales.add( new Locale("de", "AT") ); 	// German (Austria)
        supportedLocales.add( new Locale("el") );            // Greek   
        supportedLocales.add( new Locale("iw") );            // Hebrew
        supportedLocales.add( new Locale("hu") );          	// Hungarian
        supportedLocales.add( new Locale("it") );     		 	// Italian
        supportedLocales.add( new Locale("mk") );     		 	// Macedonian      
        supportedLocales.add( new Locale("no", "NO") );     	 // Norwegian (Bokmal)
        supportedLocales.add( new Locale("no", "NO", "NY") );  // Norwegian (Nynorsk)
        supportedLocales.add( new Locale("fa") );             	// Persian
        supportedLocales.add( new Locale("pl") );     		// Polish
        supportedLocales.add( new Locale("pt", "BR") );     // Portugese (Brazil)
        supportedLocales.add( new Locale("pt", "PT") );     // Portuguese (Portugal)        
        supportedLocales.add( new Locale("sr") );           	// Serbian
        supportedLocales.add( new Locale("sk") );          	// Slovakian  
        supportedLocales.add( new Locale("sl") );           	// Slovenian
        supportedLocales.add( new Locale("es") );          	// Spanish   
        supportedLocales.add( new Locale("tr") );          	// Turkish
        supportedLocales.add( new Locale("vi") );          	// Vietnamese
    }    
    
    // specialLanguageNames: Java does not show an English name for all languages
    //   supported by GeoGebra, so some language codes have to be treated specially
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
    	// specialLanguageNames.put("zh", "Chinese (Simplified)"); 
    	specialLanguageNames.put("zhTW", "Chinese (Traditional)" ); 
    }
        
    public static final Color COLOR_SELECTION = new Color(225, 225, 245);
    public static final String STANDARD_FONT_NAME = "SansSerif";  

    // file extension string
    public static final String FILE_EXT_GEOGEBRA = "ggb";
    public static final String FILE_EXT_GEOGEBRA_TOOL = "ggt";
    public static final String FILE_EXT_PNG = "png";
    public static final String FILE_EXT_EPS = "eps";    
    public static final String FILE_EXT_PDF = "pdf";
    public static final String FILE_EXT_EMF = "emf";
    public static final String FILE_EXT_SVG = "svg";
    public static final String FILE_EXT_HTML = "html";    
       	
    // page margin in cm
    public static final double PAGE_MARGIN_X = 1.8 * 72 / 2.54;
    public static final double PAGE_MARGIN_Y = 1.8 * 72 / 2.54;

    private static final String RB_MENU = "properties/menu";
    private static final String RB_COMMAND = "properties/command";
    private static final String RB_ERROR = "properties/error";
    private static final String RB_PLAIN = "properties/plain";  
    
    private static final String RB_SETTINGS = "export/settings";
    private static final String RB_ALGO2COMMAND = "kernel/algo2command";    

    //private static Color COLOR_STATUS_BACKGROUND = new Color(240, 240, 240);
    
    private GeoGebra frame;
    private GeoGebraAppletBase applet;
    private Component mainComp;
    private boolean isApplet = false;    
    private boolean showResetIcon = false;
    private URL codebase;

    private AlgebraView algebraView;
    private EuclidianView euclidianView;
    private Kernel kernel;
    private MyXMLio myXMLio;

    private AlgebraController algebraController;
    private EuclidianController euclidianController;        
    private GeoElementSelectionListener currentSelectionListener;

    // For language specific settings
    private Locale currentLocale;
    private ResourceBundle rbmenu, rbcommand, rberror, rbplain, rbsettings;
    private Hashtable translateCommandTable;

    // Actions
    private AbstractAction 
        showAxesAction,
        showGridAction,
        undoAction, redoAction;

    protected PropertiesDialogGeoElement propDialog;
    private ConstructionProtocol constProtocol;
    private ConstructionProtocolNavigation constProtocolNavigation;
    private ImageManager imageManager;

    private boolean INITING = false;
    private boolean showAlgebraView = true; 
    private boolean showAuxiliaryObjects = false;
    private boolean showAlgebraInput = true;
    private boolean showCmdList = true;    
    private boolean showToolBar = true;
    private boolean showMenuBar = true;
    private boolean showConsProtNavigation = false;
    private boolean [] showAxes = {true, true};
    private boolean showGrid = false;
    private boolean showSpreadsheet = false;
    private boolean showCAS = false;
    private boolean printScaleString = false;
    private int labelingStyle = ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC;    
                
    private boolean undoActive = true;
    private boolean rightClickEnabled = true;
    private boolean shiftDragZoomEnabled = true;
    private boolean isErrorDialogsActive = true;

    private static LinkedList fileList = new LinkedList();
    protected File currentPath, currentImagePath, currentFile = null;
    private boolean isSaved = true;    
    private int appFontSize;
    public Font boldFont, plainFont, smallFont;
    private String FONT_NAME = STANDARD_FONT_NAME;
    
    private String strCustomToolbarDefinition;
    private MyToolbar appToolbarPanel;     
    
    private JFileChooser fileChooser;
    private Menubar menuBar;
    private AlgebraInput algebraInput;
    private JPanel centerPanel;   

    private JSplitPane sp;
    private int initSplitDividerLocationHOR = 250; // init value
    private int initSplitDividerLocationVER = 400; // init value
    private boolean horizontalSplit = true; // 
    
    private ArrayList selectedGeos = new ArrayList();

    // command dictionary
    private LowerCaseDictionary commandDict;

    // plugins H-P Ulven
    private GgbAPI              ggbapi=         null;
    private PluginManager       pluginmanager=  null;   
    
    public Application(String[] args, GeoGebra frame, boolean undoActive) {
        this(args, frame, null, undoActive);
    }

    public Application(String[] args, GeoGebraAppletBase applet, boolean undoActive) {
    	this(args, null, applet, undoActive);
    }
    
    private Application(String[] args, GeoGebra frame, GeoGebraAppletBase applet, boolean undoActive) {    	
    	/*
    	if (args != null) {
    		for (int i=0; i < args.length; i++) {
    			System.out.println("argument " + i + ": " + args[i]);
    			JOptionPane.showConfirmDialog(
    	        		null,
    	        		"argument " + i + ": " + args[i], 
    	           "Arguments",
    	            JOptionPane.DEFAULT_OPTION,
    	            JOptionPane.PLAIN_MESSAGE);
    		}
    	}*/     	
    		
		isApplet = applet != null;
		if (frame != null) {
			mainComp = frame;
		} 
		else if (isApplet) {
			mainComp = applet;
		} 
		
		initCodeBase();		
		handleOptionArgs(args); // note: the locale is set here too	     			
		imageManager = new ImageManager(mainComp);				
				
		if (isApplet) 
			setApplet(applet); 
		else {
			// frame
			setFrame(frame);			
		}

        //  init kernel
        kernel = new Kernel(this);
        kernel.setPrintDecimals(Kernel.STANDARD_PRINT_DECIMALS);
        
        //  init xml io for construction loading
        myXMLio = new MyXMLio(kernel, kernel.getConstruction());

        //  init Controllers and Views              
        algebraController = new AlgebraController(kernel);
        euclidianController = new EuclidianController(kernel);
        euclidianView = new EuclidianView(euclidianController, showAxes, showGrid);  
    	algebraView = new AlgebraView(algebraController);
    	algebraView.setDropTarget(new DropTarget(algebraView, new FileDropTargetListener(this)));
   
        
        // load file on startup and set fonts
        //  INITING:    to avoid multiple calls of setLabels() and updateContentPane()
        INITING = true; 
        	setFontSize(getInitFontSize());
        	if (!isApplet)
        		GeoGebraPreferences.initDefaultXML(this);   
        	
        	// open file given by startup parameter
        	boolean fileLoaded = handleFileArg(args);        	 		    		    		   		
   			
   			// load XML preferences
        	if (!isApplet) {        		
        		currentPath = GeoGebraPreferences.getDefaultFilePath();
        		currentImagePath = GeoGebraPreferences.getDefaultImagePath();
        		if (!fileLoaded)
        			GeoGebraPreferences.loadXMLPreferences(this);
        	}        		        		    	
        	        	
        // init undo
       	setUndoActive(undoActive);  	
        INITING = false;	                     
   
        initShowAxesGridActions();
        
        // for key listening
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);	
		
		
		// TODO: remove spreadsheet and CAS testing
		if (showSpreadsheet) {			
			openSpreadsheet(this);
		}
		
		// TODO: remove spreadsheet and CAS testing
		if (showCAS) {
			openCAS(this);
		}
		
		//Mathieu Blossier - place for code to test 3D packages	
		
		
		
		// H-P Ulven 2008-04-16
		// plugins
	    // Last in constructor, has to be sure everything else is in place:
	        ggbapi=          new GgbAPI(this);    
	        pluginmanager=   new PluginManager(this);
	     		
    }      
    
    public static void openCAS(Application app) {
    	try {
    		// use reflection for
  		    // JComponent casView = new geogebra.cas.view.CASView(app);    		
  		    Class casViewClass = Class.forName("geogebra.cas.view.CASView");
  		    Object[] args = new Object[] { app };
  		    Class [] types = new Class[] {Application.class};
  	        Constructor constructor = casViewClass.getDeclaredConstructor(types);   	        
  	        JComponent casView = (JComponent) constructor.newInstance(args);  	          	      
			
			JFrame spFrame = new JFrame();
	        Container contentPane = spFrame.getContentPane();
	        contentPane.setLayout(new BorderLayout());
	        contentPane.add(casView, BorderLayout.CENTER);
	        spFrame.setResizable(true);
	        spFrame.setTitle("GeoGebra CAS");
	        spFrame.pack();
	        spFrame.setVisible(true);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	private static JFrame spFrame= new JFrame();
    
    public static void openSpreadsheet(Application app) {
    	try {
	    	// use reflection for
  		    // JComponent sp = new geogebra.spreadsheet.SpreadsheetView(app, 10, 10); 		
  		    Class SpreadsheetView = Class.forName("geogebra.spreadsheet.SpreadsheetView");
  		    Object[] args = new Object[] { app, new Integer(26), new Integer(100)};
  		    Class [] types = new Class[] {Application.class, int.class, int.class};
  	        Constructor constructor = SpreadsheetView.getDeclaredConstructor(types); 	        
  	        JComponent sp = (JComponent) constructor.newInstance(args);  
  	         	
	        Container contentPane = spFrame.getContentPane();
	        contentPane.setLayout(new BorderLayout());
	        contentPane.add(sp, BorderLayout.CENTER);
	        spFrame.setResizable(true);
	        spFrame.setTitle("GeoGebra Spreadsheet");
	        spFrame.pack();
	        spFrame.setVisible(true);
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }

    // Michael Borcherds 2008-01-14
    public static void closeSpreadsheet() {
    	try {
	        spFrame.setVisible(false);
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void initInBackground() {
    	// init file chooser and properties dialog
    	// in a background task
    	Thread runner = new Thread() {
    		public void run() {    	 
    			try {
    				Thread.sleep(1000);
    			} catch (Exception e) {}
	    			if (letShowPropertiesDialog())
	    				initPropertiesDialog();
	    			
	    			if (showMenuBar) {
	    				initFileChooser();	    				
	    			}	
	    			
	    			kernel.initCAS();
    			}	    		    		
    	};
    	runner.start();
    	
    	
    	// Download jar files to temp directory in background
    	// this is done because Java WebStart uses strange jar file
    	// names in its cache. However, we need the GeoGebra jar files
    	// to export dynamic worksheets, thus we copy the jar files 
    	// to the temp directory where we can find them.
    	Thread runner2 = new Thread() {
    		public void run() {    	 
    			try {
    				Thread.sleep(5000);
    			} catch (Exception e) {}
	    			if (showMenuBar) {    				
	    				copyJarsToTempDir();
	    			}	    				
    			}	    		    		
    	};
    	runner2.start();
    }
    
	private synchronized void initFileChooser() {
		if (fileChooser == null) {
        	fileChooser = new JFileChooser(currentImagePath);
        }
	}
	
	private synchronized void initPropertiesDialog() {
		if (propDialog == null) {
			propDialog = new PropertiesDialogGeoElement(this);
        }		
	}
    
    public void setUnsaved() {
        isSaved = false;
    }
    
    public boolean isIniting() {
        return INITING;
    }        
    
    public int getToolBarHeight() {
    	if (showToolBar)
    		return appToolbarPanel.getHeight();
    	else
    		return 0;
    }    
    
    public String getDefaultToolbarString() {
    	if (appToolbarPanel == null)
    		return "";
    	
    	return appToolbarPanel.getDefaultToolbarString();    	
    }
    
    public int getMenuBarHeight() {
    	if (menuBar == null)
    		return 0;
    	else
    		return ((JMenuBar)menuBar).getHeight();
    }
    
    public int getAlgebraInputHeight() {
    	if (showAlgebraInput)
    		return algebraInput.getHeight();
    	else
    		return 0;
    }
    
    /**
     * Returns labeling style. See the constants in
     * ConstructionDefaults (e.g. LABEL_VISIBLE_AUTOMATIC)
     */
	public int getLabelingStyle() {
		return labelingStyle;
	}

    /**
     * Sets labeling style. See the constants in
     * ConstructionDefaults (e.g. LABEL_VISIBLE_AUTOMATIC)
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
    
    private void updateComponentTreeUI() {
    	if (frame == null)
        	SwingUtilities.updateComponentTreeUI(applet);
        else
        	SwingUtilities.updateComponentTreeUI(frame);          
    }

    /**
     * Builds a panel with all components that should be shown
     * on screen (like toolbar, input field, algebra view).
     */
    public JPanel buildApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        //  CENTER: Algebra View, Euclidian View 
        // euclidian panel with view and status bar  
        centerPanel = new JPanel(new BorderLayout());       
        
        if (showToolBar) {
        	if (appToolbarPanel == null) {
        		appToolbarPanel = new MyToolbar(this);        		  
        	}                	
        	       	        	            
        	 // NORTH: Toolbar       
        	panel.add(appToolbarPanel, BorderLayout.NORTH);        	
        }           
    
        // updateCenterPanel
        updateCenterPanel(false);
        panel.add(centerPanel, BorderLayout.CENTER);                
        
        // SOUTH: inputField       
        if (showAlgebraInput) {
        	if (algebraInput == null)
        		algebraInput = new AlgebraInput(this);
        	panel.add(algebraInput, BorderLayout.SOUTH);            
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
        	eup.add(constProtocolNavigation, BorderLayout.SOUTH);
        	constProtocolNavigation.setBorder(BorderFactory.
       		     createMatteBorder(1, 0, 0, 0, Color.gray));
        }                    
        
        if (showAlgebraView) {        	     
            if (horizontalSplit) {
                sp =  new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                        new JScrollPane(algebraView), eup);
                sp.setDividerLocation(initSplitDividerLocationHOR);                
            }               
            else {
                sp =  new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                             eup, new JScrollPane(algebraView));
                sp.setDividerLocation(initSplitDividerLocationVER);
            }               
            sp.addPropertyChangeListener("dividerLocation",
                        new DividerChangeListener());                                       
            
            centerPanel.add(sp, BorderLayout.CENTER);
        } else {           
            centerPanel.add(eup, BorderLayout.CENTER);
        }
        
        // border of euclidianPanel        
        int eupTopBorder = !showAlgebraView && showToolBar ? 1 : 0;
        int eupBottomBorder = showToolBar && !(showAlgebraView && !horizontalSplit) ? 1 : 0;
        eup.setBorder(BorderFactory.
        		createMatteBorder(eupTopBorder, 0, eupBottomBorder, 0, Color.gray));
        
        if (updateUI)        	
        	updateComponentTreeUI();               
    }    
    
    public JPanel getCenterPanel() {
        return centerPanel;
    }
    
    
    
    public AbstractAction getShowAxesAction() {    	  	
    	return showAxesAction;
    }
    
    public AbstractAction getShowGridAction() {    	
    	return showGridAction;
    }    
    
    public LowerCaseDictionary getCommandDictionary() {
    	if (commandDict == null)
    		initCommandResources();
        return commandDict;
    }
    
    public void showAboutDialog() {
    	GeoGebraMenuBar.showAboutDialog(this);
    }
    
    public void showPrintPreview() {
    	GeoGebraMenuBar.showPrintPreview(this);
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
    		for (int i=0; i < args.length; i++) {
    			if (args[i].startsWith("--")) { 
    				// option found: get option's name and value 
    				int equalsPos = args[i].indexOf('=');
    				String optionName = equalsPos <= 2 ? args[i].substring(2) : args[i].substring(2, equalsPos);
    				String optionValue = equalsPos < 0 || equalsPos == args[i].length()-1 ? "" : args[i].substring(equalsPos+1);
    				
    				if (optionName.equals("help")) {
    			    	// help message
    			    	System.out.println(
    			    				"Usage: java -jar geogebra.jar [OPTION] [FILE]\n" + 
    								"Start GeoGebra with the specified OPTIONs and open the given FILE.\n" +
    								"  --help\t\tprint this message\n" +
									"  --language=LANGUGE_CODE\t\tset language using locale strings, e.g. en, de, de_AT, ...\n" +
    								"  --showAlgebraInput=BOOLEAN\tshow/hide algebra input field\n" +
									"  --showAlgebraWindow=BOOLEAN\tshow/hide algebra window\n" +
									"  --showAxes=BOOLEAN\tshow/hide coordinate axes"     								    								
    							);
    				}
    				else if (optionName.equals("language")) {
    					initLocale = getLocale(optionValue);
    					
    					//System.out.println("lanugage option: " + optionValue);
    					//System.out.println("locale: " + initLocale);
    				}
    				else if (optionName.equals("showAlgebraInput")) {
    					showAlgebraInput = !optionValue.equals("false"); 
    				}
    				else if (optionName.equals("showAlgebraWindow")) {    					
    					showAlgebraView = !optionValue.equals("false"); 
    				}    				
    				else if (optionName.equals("showAxes")) {    					
    					showAxes[0] = !optionValue.equals("false");
    					showAxes[1] = showAxes[0];
    				}
    				else if (optionName.equals("showGrid")) {    					
    					showGrid = !optionValue.equals("false");    					
    				}
    				
    				// TODO: remove spreadsheet parameter, introduced only for testing
    				else if (optionName.equals("showSpreadsheet")) {    					
    					showSpreadsheet = !optionValue.equals("false");    					
    				}
    				
    				// TODO: remove spreadsheet parameter, introduced only for testing
    				else if (optionName.equals("showCAS")) {    					
    					showCAS = !optionValue.equals("false");    					
    				}
    			}
    		}
    	}
                			    	                
        setLocale(initLocale);                 
    }
    
  
     /**
      *  Opens a file specified as last command line argument
      *  @return true if a file was loaded successfully
      */
    private boolean handleFileArg(String[] args) {
        if (args == null || args.length < 1)
            return false;
        
        String fileArgument = null;
        // the filename is the last command line argument
        // and should not be an option, i.e. it does not start
        // with "-" like -open, -print or -language
        if (args[args.length-1].charAt(0) == '-')
			// option
        	return false;
		else {
        	// take last argument as filename
        	fileArgument = args[args.length - 1];
        }                 
        
        try {             	
        	boolean success;      
        	String lowerCase = fileArgument.toLowerCase();
        	boolean isMacroFile = lowerCase.endsWith(FILE_EXT_GEOGEBRA_TOOL);
            if (lowerCase.startsWith("http") || 
            	lowerCase.startsWith("file")) {         
            	
                URL url = new URL(fileArgument);                   
                success = loadXML(url, isMacroFile);              
                updateContentPane();                          
            } else {                       	
                File f = new File(fileArgument);
                f = f.getCanonicalFile();                
                success = loadFile(f, isMacroFile);
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
    	if (applet != null)
    		applet.reset();
    	else if (currentFile != null)
    		loadFile(currentFile, false);   
    	else
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

    public AlgebraView getAlgebraView() {    	
        return algebraView;
    }

    final public AlgebraController getAlgebraController() {
        return algebraController;
    }
       
    public AlgebraInput getAlgebraInput() {
        return algebraInput;
    }

    public void geoElementSelected(GeoElement geo, boolean addToSelection) {
    	if (currentSelectionListener != null)
    		currentSelectionListener.geoElementSelected(geo, addToSelection);
    }

    /**
     * Sets a mode where clicking on an object will
     * notify the given selection listener.
     */
    public void setSelectionListenerMode(GeoElementSelectionListener sl) {        
		if (sl == null) {
			setMode(oldMode);
		} else {			
			if (getMode() != EuclidianView.MODE_ALGEBRA_INPUT)
				oldMode = getMode();			
	        euclidianView.setMode(EuclidianView.MODE_ALGEBRA_INPUT);	        
	        appToolbarPanel.setSelectedMode(EuclidianView.MODE_ALGEBRA_INPUT);
		}
		 
		currentSelectionListener = sl;	      
    }
    private int oldMode = 0;
    
    public GeoElementSelectionListener getCurrentSelectionListener() {
    	return currentSelectionListener;
    }
    
    public void setAglebraInputMode() {        
        setMode(EuclidianView.MODE_ALGEBRA_INPUT);
    }       

    
    public void setMoveMode() {
    	setMode(EuclidianView.MODE_MOVE);       
    	    	
    	// check if toolbar shows move mode
    	// if not we set the first mode of toolbar
    	if (showToolBar && appToolbarPanel != null) {    		
    		if (appToolbarPanel.getSelectedMode() != EuclidianView.MODE_MOVE) {
    			int firstMode = appToolbarPanel.getFirstMode();
    			if (firstMode > 0)
    				setMode(firstMode);    		
    		}
    	}  
    }
    
    public ImageIcon getImageIcon(String filename) {
        return getImageIcon(filename, null);
    }
    
    public ImageIcon getImageIcon(String filename, Color borderColor) {
        return imageManager.getImageIcon("/geogebra/gui/images/" + filename, borderColor);
    }

    public ImageIcon getEmptyIcon() {
        return imageManager.getImageIcon("/geogebra/gui/images/empty.gif");
    }

    public Image getInternalImage(String filename) {
        return imageManager.getInternalImage("/geogebra/gui/images/" + filename);
    }
    
    public BufferedImage getExternalImage(String filename) {
        return imageManager.getExternalImage(filename);
    }
    
    public void addExternalImage(String filename, BufferedImage image) {    	
        imageManager.addExternalImage(filename, image);
    }

    public void startEditing(GeoElement geo) {
    	if (algebraView != null)
    		algebraView.startEditing(geo);
    }

    public final void zoom(double px, double py, double zoomFactor) {
        euclidianView.zoom(px, py, zoomFactor, 15, true);
    }
    
    /**
     * Sets the ratio between the scales of y-axis and x-axis,
     * i.e. ratio = yscale / xscale;
     * @param zoomFactor
     */
    public final void zoomAxesRatio(double axesratio) {
        euclidianView.zoomAxesRatio(axesratio, true);
    }

    public final void setStandardView() {
        euclidianView.setStandardView(true);
    }

    /**********************************************************
     * LOCALE part
     **********************************************************/
   
    /**
     * Creates a Locale object according to the given language code.
     * The languageCode string should consist of two letters for the language, 
     * two letters for the country and two letters for the variant.
     * E.g. "en" ... language: English , no country specified, "deAT" or "de_AT" ... language: German , country: Austria,
     * "noNONY" or "no_NO_NY" ... language: Norwegian , country: Norway, variant: Nynorsk    	
     */
    public static Locale getLocale(String languageCode) {    	
        // remove "_" from string
    	languageCode = languageCode.replaceAll("_", "");
    	
    	Locale loc;                
        if (languageCode.length() == 6) {
            //  language, country
            loc = new Locale(languageCode.substring(0, 2),
            			languageCode.substring(2, 4),
						languageCode.substring(4,6));  
        } 
        else if (languageCode.length() == 4) {
            //  language, country
            loc = new Locale(languageCode.substring(0, 2), languageCode.substring(2, 4));  
        } 
        else {
            // language only
            loc = new Locale(languageCode.substring(0, 2)); 
        }
        return loc;
    }
    
    
    /**
     * set language via iso language string
     */
    public void setLanguage(Locale locale) {    	
    	if (locale == null || 
    		currentLocale.toString().equals(locale.toString())) return;    	             	
        
    	if (!INITING) {
    		setMoveMode();
    	}
    	
        // load resource files
        setLocale(locale);
        
        // update right angle style in euclidian view (different for German)
        if (euclidianView != null) 
        	euclidianView.updateRightAngleStyle(locale);       
        
        // make sure to update commands
        if (rbcommand != null)
        	initCommandResources();  
       
        kernel.updateLocalAxesNames();
        setLabels(); // update display
       
        System.gc();
    }
    
    
    /* removed Michael Borcherds 2008-03-31
    private boolean reverseLanguage = false; //FKH 20040822    
    final public boolean isReverseLanguage() { //FKH 20041010
        // for Chinese
        return reverseLanguage;
    }*/
    
    // for basque you have to say "A point" instead of "point A"
    private boolean reverseNameDescription = false;
    final public boolean isReverseNameDescriptionLanguage() { 
        // for Basque
        return reverseNameDescription;
    }
    
    private void updateReverseLanguage(Locale locale) {
    	String lang = locale.getLanguage();
        //reverseLanguage = "zh".equals(lang); removed Michael Borcherds 2008-03-31
        reverseNameDescription = "eu".equals(lang);
    }
    
    // Michael Borcherds 2008-02-23
    public boolean languageIs(Locale locale, String lang) {
    	return locale.getLanguage().equals(lang);
    }
    
    private String getLanguageFontName(Locale locale) throws Exception {
        String fontName = null;
        //  chinese font
        if ("zh".equals(locale.getLanguage())) { // CHINESE
            fontName = getChineseFontName();    
        } 
        // standard font
        if (fontName == null) fontName = STANDARD_FONT_NAME;
        return fontName;
    }
    
    private String getChineseFontName() throws Exception {
        String chinesesample = "\u4e00";
        if (plainFont != null && plainFont.canDisplayUpTo(chinesesample) == -1)
			return plainFont.getFontName();
        
        // Determine which fonts support Chinese here ...
        Font[] allfonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        //int fontcount = 0;
        for (int j = 0; j < allfonts.length; j++) {
            if (allfonts[j].canDisplayUpTo(chinesesample) == -1)
				return allfonts[j].getFontName();
        }
        throw new Exception("Sorry, there is no font available on your computer\nthat could display Chinese characters.");
    }

    public void setLocale(Locale locale) {    
        // only allow special locales due to some weird server
        // problems with the naming of the property files   
        currentLocale = getClosestSupportedLocale(locale);                     
        updateResourceBundles();
        
        // update font for new language (needed for e.g. chinese)
        try {
            String fontName = getLanguageFontName(locale);
            if (fontName != FONT_NAME) {
                FONT_NAME = fontName;
                resetFonts();
            }
        }        
        catch (Exception e) {
        	e.printStackTrace();
            showError(e.getMessage());
            locale = currentLocale;
        }                   	
        updateReverseLanguage(locale);
    }
    
    /**
     * Returns a locale object that has the same country and/or language
     * as locale. If the language of locale is not supported
     * an English locale is returned.
     */
    private static Locale getClosestSupportedLocale(Locale locale) {
        int size = supportedLocales.size();
        
        // try to find country and variant
        String country = locale.getCountry();
        String variant = locale.getVariant();       
        
        if (country.length() > 0) {
            for (int i=0; i < size; i++) {
                Locale loc = (Locale) supportedLocales.get(i);
                if (country.equals(loc.getCountry()) &&
                		variant.equals(loc.getVariant()))
					// found supported country locale
                    return loc;
            }
        }
        
        // try to find language
        String language = locale.getLanguage();
        for (int i=0; i < size; i++) {
            Locale loc = (Locale) supportedLocales.get(i);
            if (language.equals(loc.getLanguage()))
				// found supported country locale
                return loc;
        }
     
        // we didn't find a matching country or language,
        // so  we take English
        return Locale.ENGLISH;
    }
    
    public ResourceBundle initAlgo2CommandBundle() {    	
		return MyResourceBundle.loadSingleBundleFile(RB_ALGO2COMMAND);
    }

    private void updateResourceBundles() {      	
    	if (rbmenu != null)
    		rbmenu = MyResourceBundle.createBundle(RB_MENU, currentLocale);
        if (rberror != null)
        	rberror = MyResourceBundle.createBundle(RB_ERROR, currentLocale);
        if (rbplain != null) 
        	rbplain = MyResourceBundle.createBundle(RB_PLAIN, currentLocale);
      //  if (rbcommand != null || (applet != null && applet.enableJavaScript))
        	//initCommandResources();                        
    }
    
    public boolean propertiesFilesPresent() {
    	return rbplain != null;
    }
    
    private void initCommandResources() {    	    
    	//System.out.println("init command resources");    	
        rbcommand = MyResourceBundle.createBundle(RB_COMMAND, currentLocale);    
        
        // build Hashtable for translation of commands from
        // local language to internal name
        // key = local name, value = internal name                  
        if (translateCommandTable == null) 
        	translateCommandTable = new Hashtable();
        
        //  build command dictionary of local command names
        if (commandDict == null) 
        	commandDict = new LowerCaseDictionary();           
                
       fillCommandDict();
    }    
    
    private void fillCommandDict() {    	
    	if (rbcommand == null) return;
    	
    	 translateCommandTable.clear();
         commandDict.clear();
         
        // Enumeration e = rbcommand.getKeys();
         Iterator it = kernel.getAlgebraProcessor().getCmdNameIterator();
         while (it.hasNext()) {            
             String internal = (String) it.next();
             //System.out.println(internal);
             if (!internal.endsWith("Syntax") && !internal.equals("Command") ) {
            	 String local = rbcommand.getString((String) internal);
            	 if (local != null) {
            		 local = local.trim();
	             	 // case is ignored in translating local command names to internal names! 
	                 translateCommandTable.put(local.toLowerCase(), internal);                
	                 commandDict.addEntry(local);
            	 }
             }
         }   
         
         addMacroCommands();
    }
    
    private void addMacroCommands() {
    	if (commandDict == null || kernel == null || !kernel.hasMacros()) return;
    	
    	ArrayList macros = kernel.getAllMacros();
		for (int i=0; i < macros.size(); i++) {
			String cmdName = ((Macro)macros.get(i)).getCommandName();
			if (!commandDict.contains(cmdName))
				commandDict.addEntry(cmdName);
		}
    }
    
    public void removeMacroCommands() {
    	if (commandDict == null || kernel == null || !kernel.hasMacros()) return;
    	
    	ArrayList macros = kernel.getAllMacros();
		for (int i=0; i < macros.size(); i++) {
			String cmdName = ((Macro)macros.get(i)).getCommandName();		
			commandDict.removeEntry(cmdName);
		}
    }

    public Locale getLocale() {
        return currentLocale;
    }
    

    final public String getPlain(String key) {
    	if (rbplain == null) {
    		rbplain = MyResourceBundle.createBundle(RB_PLAIN, currentLocale);
    	}
    	
        try {
            return rbplain.getString(key);
        } catch (Exception e) {
            return key;
        }
    }
    
    // Michael Borcherds 2008-03-25
    // replace "%0" by arg0
    final public String getPlain(String key,String arg0) {
    	String[] ss={arg0};
    	return getPlain(key,ss);
    }
    
    // Michael Borcherds 2008-03-25
    // replace "%0" by arg0, "%1" by arg1
    final public String getPlain(String key,String arg0, String arg1) {
    	String[] ss={arg0,arg1};
    	return getPlain(key,ss);
    }
    
    // Michael Borcherds 2008-03-30
    // replace "%0" by arg0, "%1" by arg1, "%2" by arg2
    final public String getPlain(String key,String arg0, String arg1, String arg2) {
    	String[] ss={arg0,arg1,arg2};
    	return getPlain(key,ss);
    }
    
    // Michael Borcherds 2008-03-30
    // replace "%0" by arg0, "%1" by arg1, "%2" by arg2, "%3" by arg3
    final public String getPlain(String key,String arg0, String arg1, String arg2, String arg3) {
    	String[] ss={arg0,arg1,arg2,arg3};
    	return getPlain(key,ss);
    }
    
    // Michael Borcherds 2008-03-30
    // replace "%0" by arg0, "%1" by arg1, "%2" by arg2, "%3" by arg3, "%4" by arg4
    final public String getPlain(String key,String arg0, String arg1, String arg2, String arg3, String arg4) {
    	String[] ss={arg0,arg1,arg2,arg3,arg4};
    	return getPlain(key,ss);
    }
    
    // Michael Borcherds 2008-03-25
    // replace "%0" by args[0], "%1" by args[1], etc
    final public String getPlain(String key,String[] args) {
    	String ret = getPlain(key);
  	
    	for (int i=0 ; i<args.length ; i++)
    	    ret=ret.replaceAll("%"+i,args[i]);

    	return ret;
    }
    
    final public String getMenu(String key) {
    	if (rbmenu == null) 
    		rbmenu = MyResourceBundle.createBundle(RB_MENU, currentLocale);
    	
        try {
            return rbmenu.getString(key);
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

    final public String getError(String key) {
    	if (rberror == null) 
    		rberror = MyResourceBundle.createBundle(RB_ERROR, currentLocale);    	
        try {
            return rberror.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    final public String getCommand(String key) {
    	if (rbcommand == null) 
    		initCommandResources();
    	
        try {
            return rbcommand.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    /**
     *  translate command name to internal name.
     * Note: the case of localname is NOT relevant 
     */
    final public String translateCommand(String localname) {    
    	if (localname == null)
    		return null;
    	
    	if (translateCommandTable == null)
    		initCommandResources();
    	    		    	
        // note: lookup lower case of command name!
        Object value = translateCommandTable.get(localname.toLowerCase());
              
        String ret;
        if (value == null) 
            ret = localname;
        else
            ret = (String) value;
           	 
    	return ret;
    }

    public void showRelation(GeoElement a, GeoElement b) {
        JOptionPane.showConfirmDialog(
            mainComp,
            new Relation(kernel).relation(a, b),
            getPlain("ApplicationName") + " - " + getCommand("Relation"),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void showHelp(String key) {
        String text;
        try {
            text = rbplain.getString(key);
        } catch (Exception e) {
            text = key;
        }
        JOptionPane.showConfirmDialog(
        		mainComp,
            text,
            getPlain("ApplicationName") + " - " +getMenu("Help"),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE);
    }

    public void showError(String key) {       	
    	showErrorDialog(getError(key));              
    }

    public void showError(MyError e) {   
    	showErrorDialog(e.getLocalizedMessage());
    }
    
    private void showErrorDialog(String msg) {
    	if (!isErrorDialogsActive) return;
    	
    	 JOptionPane.showConfirmDialog(
         		mainComp,
             msg,
             getPlain("ApplicationName") + " - " + getError("Error"),
             JOptionPane.DEFAULT_OPTION,
             JOptionPane.WARNING_MESSAGE);
    }
        
    
    public void showMessage(String message) {               
        JOptionPane.showConfirmDialog(
        		mainComp,
            message,
            getPlain("ApplicationName") + " - " + getMenu("Info"),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    
     

    /**
     * Displays the zoom menu at the position p in the coordinate space 
     * of euclidianView
     */
    public void showDrawingPadPopup(Component invoker, Point p) {
        // clear highlighting and selections in views
        euclidianView.resetMode();        

        // menu for drawing pane context menu
        ContextMenuGraphicsWindow popupMenu = new ContextMenuGraphicsWindow(this, p.x, p.y);
        popupMenu.show(invoker, p.x, p.y);
    }

    /**
     * Displays the popup menu for geo at the position p in the coordinate space 
     * of the component invoker
     */
    public void showPopupMenu(GeoElement geo, Component invoker, Point p) {
    	if (geo == null || !letShowPopupMenu()) return;    	
    	
        if (kernel.isAxis(geo)) {
        	showDrawingPadPopup(invoker, p);
        	return;
        }
        
        // clear highlighting and selections in views        
        euclidianView.resetMode();
        Point screenPos = invoker.getLocationOnScreen();
        screenPos.translate(p.x, p.y);

        ContextMenuGeoElement popupMenu = new ContextMenuGeoElement(this, geo, screenPos);
        popupMenu.show(invoker, p.x, p.y);
    }    

    /**
       * Displays the porperties dialog for geos
       */
    public void showPropertiesDialog(ArrayList geos) {
        if (!letShowPropertiesDialog()) return;
        
        // save the geos list: it will be cleared by setMoveMode()
        ArrayList selGeos = null;
        if (geos == null)
        	geos = getSelectedGeos();
        
        if (geos != null) {
        	tempGeos.clear();
        	tempGeos.addAll(geos);
        	selGeos = tempGeos;
        }
                
        initPropertiesDialog();
        setMoveMode();
        
        propDialog.setVisibleWithGeos(selGeos);
    }
    private ArrayList tempGeos = new ArrayList();
    
    public void showPropertiesDialog() {
    	showPropertiesDialog(null);
    }
    
    /**
     * Displays the porperties dialog for the drawing pad
     */
    public void showDrawingPadPropertiesDialog() {
      if (!letShowPropertiesDialog()) return;
      euclidianView.resetMode();      
      PropertiesDialogGraphicsWindow euclidianViewDialog = 
      	new PropertiesDialogGraphicsWindow(this, euclidianView);      
      euclidianViewDialog.setVisible(true);
    }
    
    /**
     * Displays the configuration dialog for the toolbar
     */
    public void showToolbarConfigDialog() {      
      euclidianView.resetMode();
      ToolbarConfigDialog dialog = new ToolbarConfigDialog(this);      
      dialog.setVisible(true);
    }
       

    /**
       * Displays the construction protocol dialog 
       */
    public void showConstructionProtocol() {
        euclidianView.resetMode();        
        constProtocol = getConstructionProtocol();        
        constProtocol.setVisible(true);
    }
    
    public ConstructionProtocol getConstructionProtocol() {
    	  if (constProtocol == null) {
            constProtocol = new ConstructionProtocol(this);
          }
    	  return constProtocol;
    }   

    /**
       * Displays the rename dialog for geo
       */
    public void showRenameDialog(GeoElement geo, boolean storeUndo, String initText, boolean selectInitText) {
		if (!rightClickEnabled) return;
    	
    	geo.setLabelVisible(true);
		geo.updateRepaint();
    	
    	InputHandler handler = new RenameInputHandler(this, geo, storeUndo);
    	
    	
    	// Michael Borcherds 2008-03-25
    	// a Chinese friendly version
        InputDialog id =
            new InputDialog(
                this,
                 "<html>" + 
                 getPlain("NewNameForA","<b>"+geo.getNameDescription()+"</b>") + // eg New name for <b>Segment a</b>
                 "</html>",
                getPlain("Rename"),
                initText,
                false,
                handler, true, selectInitText);        
    	
    	/*
        InputDialog id =
            new InputDialog(
                this,
                 "<html>" + getPlain("NewName")
                    + " "
                    + getPlain("for")
                    + " <b>"
                    + geo.getNameDescription()
                    + "</b></html>",
                getPlain("Rename"),
                initText,
                false,
                handler, true, selectInitText);        */
    	
        id.setVisible(true);              
    }
    
    
    
    
    /**
       * Displays the redefine dialog for geo
       */
    public void showRedefineDialog(GeoElement geo) {
    	//doBeforeRedefine();    	 
    	
    	if (geo.isGeoText()) {
			showTextDialog((GeoText) geo);
			return;
    	}

    	// Michael Borcherds 2007-12-31 BEGIN
    	//InputHandler handler = new RedefineInputHandler(this, geo);                
        	String str = geo.isIndependent() ? 
                            geo.toValueString() :
                            geo.getCommandDescription();
        InputHandler handler = new RedefineInputHandler(this, geo, str);                
//                          Michael Borcherds 2007-12-31 END    
        /*
        String str = initSB.toString();        
        // add label to make renaming possible too
        if (str.indexOf('=') == -1) { // no equal sign in definition string
        	// functions need either "f(x) =" or "f ="
        	if (!geo.isGeoFunction())        		
        		initSB.insert(0, geo.getLabel() + " = ");  
        	else if (str.indexOf('[') == -1) // no command
        		initSB.insert(0, geo.getLabel() + "(x) = ");  
        } else {
        	// make sure that initSB does not already contain the label,
        	// e.g. like for functions: f(x) = a x^2
        	if (!str.startsWith(geo.getLabel())) {        		
        		initSB.insert(0, geo.getLabel() + ": ");
        	}
        }
        */
                                                                                                                          
        InputDialog id =
            new InputDialog(
                this, 
                geo.getNameDescription(),
                getPlain("Redefine"),
                str,
                true,
                handler);  
        id.showSpecialCharacters(true);
        id.setVisible(true);     
        id.selectText();            
    }

    
    /**
     * Creates a new slider at given location (screen coords).
     * @return whether a new slider (number) was create or not
     */   
	public boolean showSliderCreationDialog(int x, int y) {
	      SliderDialog dialog = new SliderDialog(this, x, y);
	      dialog.setVisible(true);	      
	      GeoNumeric num = (GeoNumeric) dialog.getResult();
	      if (num != null) {
	    	// make sure that we show name and value of slider
	    	num.setLabelMode(GeoElement.LABEL_NAME_VALUE);					
	    	num.setLabelVisible(true);
	    	num.update();
	      }
	      return num != null;	      	
	}
	
    /**
     * Creates a new image at the given location (real world coords).
     * @return whether a new image was create or not
     */   
	public boolean loadImage(GeoPoint loc, boolean fromClipboard) {		
		String fileName = getImage(fromClipboard);
		if (fileName == null) return false;						
		
		// create GeoImage object for this fileName
		GeoImage geoImage = new GeoImage(kernel.getConstruction());					
		geoImage.setFileName(fileName);			
		geoImage.setCorner(loc, 0);	
		geoImage.setLabel(null);
				     	        	        
        GeoImage.updateInstances();
		return true;		
	}

	public Color showColorChooser(Color currentColor) {
		// there seems to be a bug concerning ToolTips in JColorChooser 
		// so we turn off ToolTips
		//ToolTipManager.sharedInstance().setEnabled(false);
		try {
			Color newColor =
				JColorChooser.showDialog(
					null,
					getPlain("ChooseColor"),
					currentColor);
			//ToolTipManager.sharedInstance().setEnabled(true);
			return newColor;
		} catch (Exception e) {
			//ToolTipManager.sharedInstance().setEnabled(true);
			return null;
		}
	}
	
	/**
	 * gets String from clipboard
	 * Michael Borcherds 2008-04-09
	 */
	public String getStringFromClipboard()
	{
    String selection = null;
    
	Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
	Transferable transfer = clip.getContents(null);
	
    try {
	if (transfer.isDataFlavorSupported(DataFlavor.stringFlavor))
	  selection = (String)transfer.getTransferData(DataFlavor.stringFlavor);
	else if (transfer.isDataFlavorSupported(DataFlavor.plainTextFlavor))
	  {
	    StringBuffer sbuf = new StringBuffer();
	    InputStreamReader reader;
	    char readBuf[] = new char[1024*64];
	    int numChars;
 	  
	    reader = new InputStreamReader 
	      ((InputStream) 
	    		 transfer.getTransferData(DataFlavor.plainTextFlavor), "UNICODE");
 	  
	    while (true)
	      {
		numChars = reader.read(readBuf);
		if (numChars == -1)
		  break;
		sbuf.append(readBuf, 0, numChars);
	      }
 	  
	    selection = new String(sbuf);
	  }
      }
    catch (Exception e)
      {
      }
    
    return selection;
	}
	
	/**
	 * Shows a file open dialog to choose an image file,
	 * [or gets an image from the clipboard Michael Borcherds 2008-02-17]
	 * Then the image file is loaded and stored in this
	 * application's imageManager.
	 * @return fileName of image stored in imageManager
	 */
	public String getImage(boolean fromClipboard) {

		BufferedImage img = null;
		String fileName = null;
		try {
			setWaitCursor();

			if (fromClipboard)
			{
				
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable transfer = clip.getContents(null);
		          
				/*
				if(transfer.isDataFlavorSupported(DataFlavor.plainTextFlavor)){
					String str = (Text)transfer.getTransferData(DataFlavor.plainTextFlavor);
					DataFlavor.getReaderForText(transfer)
					System.out.println("str"+str);
				} else System.out.println("notxt");
				*/
				
				try{
					if(transfer.isDataFlavorSupported(DataFlavor.imageFlavor)){
						img = (BufferedImage)transfer.getTransferData(DataFlavor.imageFlavor);
					}
				} catch(UnsupportedFlavorException ufe){
					showError("PasteImageFailed");
					return null;
					//ufe.printStackTrace();
				} catch(IOException ioe){
					showError("PasteImageFailed");
					return null;
					//ioe.printStackTrace();
				}
				
				if (img==null)
				{
					showError("PasteImageFailed");
					return null;	    	
				}
				
				fileName="clipboard.png"; // extension determines what format it will be in ggb file
			}
			else
			{
				
				
				initFileChooser();
				fileChooser.setCurrentDirectory(currentImagePath);
				MyFileFilter fileFilter = new MyFileFilter();
				fileFilter.addExtension("jpg");
				fileFilter.addExtension("png");
				fileFilter.addExtension("gif");
				fileFilter.addExtension("tif");
				fileFilter.setDescription(getPlain("Image"));
				fileChooser.resetChoosableFileFilters();
				fileChooser.setFileFilter(fileFilter);    
				
				// add image previewto fileChooser, Philipp Weissenbacher
				ImagePreview preview = new ImagePreview(fileChooser);
				fileChooser.setAccessory(preview);
				fileChooser.addPropertyChangeListener(preview);

				File imageFile = null;
				int returnVal = fileChooser.showOpenDialog(mainComp);
				if (returnVal == JFileChooser.APPROVE_OPTION) {             
					imageFile = fileChooser.getSelectedFile();   
					if (imageFile != null) {
						currentImagePath = imageFile.getParentFile();
						if (!isApplet) {
							GeoGebraPreferences.saveDefaultImagePath(currentImagePath);
						}
					}
				}      
				
				// remove image preview in order to reset fileChooser
				fileChooser.removePropertyChangeListener(preview);
				fileChooser.setAccessory(null);
				
				if (imageFile == null) return null;				
				
				
				
				// get file name
				fileName = imageFile.getCanonicalPath();	
				
				// load image
				img = ImageIO.read(imageFile);	
			}	
			// Michael Borcherds 2007-12-10 START moved MD5 code from GeoImage to here
			String zip_directory="";
			try
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				if (img==null) System.out.println("image==null");
				ImageIO.write(img, "png", baos);		
				byte [] fileData= baos.toByteArray();
				
				MessageDigest md;
				md = MessageDigest.getInstance("MD5");
				byte[] md5hash = new byte[32];
				md.update(fileData, 0, fileData.length);
				md5hash = md.digest();
				zip_directory=convertToHex(md5hash);
			}
			catch (Exception e)
			{
				System.err.println("MD5 Error");
				zip_directory="images";
				//e.printStackTrace();
			}
			
			String fn=fileName;
			int index = fileName.lastIndexOf(File.separator);
			if( index != -1 )
			fn = fn.substring( index+1,fn.length() ); // filename without path
			fn=Util.processFilename(fn);
			
			// filename will be of form "a04c62e6a065b47476607ac815d022cc\liar.gif"
			fileName=zip_directory+File.separator+fn;

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
				if (oldImg.getWidth() == img.getWidth() &&
						oldImg.getHeight() == img.getHeight()) {
					// same size and filename => we consider the images as equal
					return fileName;
				} else {
					// same name but different size: change filename
					// TODO Michael Borcherds: this bit of code should now be redundant as it
					// is near impossible for the filename to be the same unless the files are the same
					int n = 0;
					do {
						n++;
						int pos = fileName.lastIndexOf('.');
						String firstPart = pos > 0 ? fileName.substring(0, pos) : "";
						String extension = pos < fileName.length() ? fileName.substring(pos) : "";
						fileName = firstPart + n + extension; 						
					} while(imageManager.getExternalImage(fileName) != null);					
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
	
//	 code from freenet
//	http://emu.freenetproject.org/pipermail/cvs/2007-June/040186.html
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
        	} while(two_halfs++ < 1);
        }
        return buf.toString();
    }

	public void setWaitCursor() {
		mainComp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));        		     					
	}
	
	public void setDefaultCursor() {
		mainComp.setCursor(Cursor.getDefaultCursor());
	}
    
    /**
       * Displays the text dialog for a given text. 
       */
    public void showTextDialog(GeoText text) {
        showTextDialog(text, null);
    }
        
        
    /**
       * Creates a new text at given startPoint
       */   
    public void showTextCreationDialog(GeoPoint startPoint) {
        showTextDialog(null, startPoint);
    }
    
    
    private void showTextDialog(GeoText text, GeoPoint startPoint) {            	   
    	JDialog dialog = createTextDialog(text, startPoint);
    	dialog.setVisible(true);
    }
        
    public TextInputDialog createTextDialog(GeoText text, GeoPoint startPoint) {            	         
        TextInputDialog id =
            new TextInputDialog(
                this,
                getPlain("Text"),
                text, startPoint,
                30, 6);
        return id;
    }
    
    public void doAfterRedefine(GeoElement geo) {
    	// select geoElement with label again
    	if (propDialog != null && propDialog.isShowing()) {    	 	
         	//propDialog.setViewActive(true);
            propDialog.geoElementSelected(geo, false);          
        }    	     
    }
    
    /**
     * Creates a new text at given startPoint
     */   
	  public void showBooleanCheckboxCreationDialog(Point loc, GeoBoolean bool) {
		  CheckboxCreationDialog d = new CheckboxCreationDialog(this, loc, bool);		   
		  d.setVisible(true);
	  }    
    
    /**
     * Shows a modal dialog to enter a number or number variable name.
     */
    public NumberValue showNumberInputDialog(String title, String message, String initText) {
		// avoid labeling of num
    	Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		
		NumberInputHandler handler = new NumberInputHandler();
		InputDialog id = new InputDialog(this,  message, title, initText, false, handler, true, false);       
        id.setVisible(true); 
        
        cons.setSuppressLabelCreation(oldVal);
        return handler.num;
	}
    
    
    
    /**
     * Shows a modal dialog to enter an angle or angle variable name.
     * @return: Object[] with { NumberValue, AngleInputDialog } pair
     */
    public Object [] showAngleInputDialog(String title, String message, String initText) {   
    	// avoid labeling of num
    	Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
    	
    	NumberInputHandler handler = new NumberInputHandler();
		AngleInputDialog id = new AngleInputDialog(this,  message, title, initText, false, handler, true);       
        id.setVisible(true); 
        
        cons.setSuppressLabelCreation(oldVal);
        Object [] ret = {handler.num, id};
        return ret;
	}
    
    private class NumberInputHandler implements InputHandler {
		NumberValue num = null;
		
		public boolean processInput(String inputString) {			
			GeoElement [] result = kernel.getAlgebraProcessor().processAlgebraCommand(inputString, false);			
			boolean success = result != null && result[0].isNumberValue();
			if (success) {
				num = (NumberValue) result[0];				
			}
			return success;
		}	
	}
    
    /* *********************************************************
     * private methods for display
     **********************************************************/

    public File getCurrentFile() {
    	return currentFile;
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
    	if (file == null || !file.exists()) return;    	    	    	    	 
    	
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
    	if (frame == null) return;
    	
        StringBuffer sb = new StringBuffer();
        sb.append("GeoGebra");        
        if (currentFile != null) {
            sb.append(" - ");
            sb.append(currentFile.getName());
        } else {
        	if (GeoGebra.getInstanceCount() > 1) {
        		int nr = frame.getInstanceNumber();        	
        		sb.append(" (");
        		sb.append(nr+1);
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
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int fontsize = (int) (dim.height * 0.016);
        if (fontsize < 10)
            fontsize = 10;
        else if (fontsize > 16)
            fontsize = 16;
        return fontsize;*/
        return 12;
    }

    public void resetFonts() {
        setLAFFontSize();
        updateFonts();
    }
    
    public void updateFonts() {
        if (euclidianView != null)
            euclidianView.updateFonts();
        if (algebraView != null)
            algebraView.updateFonts();
        if (algebraInput != null)
            algebraInput.updateFonts();
                   
        if (appToolbarPanel != null)
        	appToolbarPanel.initToolbar();

        if (propDialog != null)
            propDialog.initGUI();
        if (constProtocol != null)
            constProtocol.initGUI();
        if (constProtocolNavigation != null)
        	constProtocolNavigation.initGUI();       
        if (fileChooser != null){
        	fileChooser.setFont(getPlainFont());
        	SwingUtilities.updateComponentTreeUI(fileChooser);
        }        	
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
        FontUIResource plain = new FontUIResource(FONT_NAME, Font.PLAIN, size);
        plainFont = plain;      
        smallFont = new FontUIResource(FONT_NAME, Font.PLAIN, size - 2);
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
        
        initShowAxesGridActions();
            
        if (showMenuBar) {
        	initMenubar();        	
	        if (isApplet) 
	        	applet.setJMenuBar((JMenuBar)menuBar);
	        else 
	        	frame.setJMenuBar((JMenuBar)menuBar);
        }
        
        if (appToolbarPanel != null) appToolbarPanel.initToolbar();              
        if (algebraView != null) algebraView.setLabels(); // update views    
        if (algebraInput != null) algebraInput.setLabels();
                
        if (propDialog != null)
            propDialog.initGUI();
        if (constProtocol != null)
            constProtocol.initGUI();
        if (constProtocolNavigation != null)
        	constProtocolNavigation.setLabels(); 
    }    
    
    
    public void clearPreferences() {
    	if (isSaved() || saveCurrentFile()) {
    		setWaitCursor();
			GeoGebraPreferences.clearPreferences();
			
			// clear custom toolbar definition
			strCustomToolbarDefinition = null;			
			
			GeoGebraPreferences.loadXMLPreferences(this); // this will load the default settings
			setLanguage(mainComp.getLocale());
			updateContentPaneAndSize();
			setDefaultCursor();
		}
    }
    
    public void setToolBarDefinition(String toolBarDefinition) {    	  
    	strCustomToolbarDefinition = toolBarDefinition;    	
    }

    public String getToolBarDefinition() {
    	if (strCustomToolbarDefinition == null && appToolbarPanel != null)
    		return appToolbarPanel.getDefaultToolbarString();
    	else
    		return strCustomToolbarDefinition;
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
				System.err.println("Application.getModeText(): macro does not exist: ID = " + macroID);				
				//e.printStackTrace();
				return "";
			}    		
		}
		else		
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
				System.err.println("macro does not exist: ID = " + macroID);
				return null;
			}    		
		}
		else {	    
	    	// standard case
			String modeText = EuclidianView.getModeText(mode);
			String iconName = "mode_" + modeText.toLowerCase() + "_32.gif";
			icon = getImageIcon(iconName, border);						
			if (icon == null) {
				System.err.println("icon missing for mode " + modeText + " (" + mode + ")");			
			}			
		}			
		return icon;
    }

    public void setSplitDividerLocationHOR(int loc) {       
        initSplitDividerLocationHOR = loc;              
    }
    
    public void setSplitDividerLocationVER(int loc) {       
        initSplitDividerLocationVER = loc;          
    }
    
    public void setHorizontalSplit(boolean flag) {
        if (flag == horizontalSplit) return;                
        
        horizontalSplit = flag;
        if (sp == null) return;     
        if (flag) {         
            sp.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        }           
        else {          
            sp.setOrientation(JSplitPane.VERTICAL_SPLIT);
        }           
    }
    
    public boolean isHorizontalSplit() {
    	return horizontalSplit;
    }

    public void setShowAlgebraView(boolean flag) {
        if (showAlgebraView == flag) return;
        
        showAlgebraView = flag;
        if (showAlgebraView) {        	
        	algebraView.attachView();    		        	
        	algebraView.setShowAuxiliaryObjects(showAuxiliaryObjects);
        }         
        else {
        	algebraView.detachView();
        }                
            
        updateMenubar();            
        isSaved = false;        
    }
 
    // Michael Borcherds 2008-01-14
    public void setShowSpreadsheet(boolean flag) {
        if (showSpreadsheet == flag) return;
        
        showSpreadsheet = flag;
        if (showSpreadsheet) {        	
    		openSpreadsheet(this);
        }         
        else {
    		closeSpreadsheet();

        }                
            
        updateMenubar();            
        isSaved = false;        
    }
       
    final public boolean showAlgebraView() {
        return showAlgebraView;
    }        
    
    // Michael Borcherds 2008-01-14
    final public boolean showSpreadsheet() {
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
    	showCmdList = flag;
    	    	    	    	
    	if (algebraInput != null)
    		algebraInput.initGUI();    	
    	updateMenubar();    	
    }
    
    /**
     * Displays the construction protocol navigation 
     */
	 public void setShowConstructionProtocolNavigation(boolean flag) {
	  	  if (flag == showConsProtNavigation) return;
	  	  showConsProtNavigation = flag;
	  	  
	  	  if (constProtocolNavigation == null) {
	  	  	constProtocolNavigation = new ConstructionProtocolNavigation(getConstructionProtocol());
	  	  }	  	 	  	  	  	  
	  	  
	  	  if (showConsProtNavigation) {
	  	    if (euclidianView != null) euclidianView.resetMode();
	  	  	constProtocolNavigation.register();
	  	  } else {
	  	  	constProtocolNavigation.unregister();
	  	  }
	  	  
	  	  updateMenubar();  	
	  }
	 
	 public boolean showConsProtNavigation() {
	 	return showConsProtNavigation;
	 }
	 
	 public ConstructionProtocolNavigation getConstructionProtocolNavigation() { 
	 	if (constProtocolNavigation == null) {
	 		constProtocolNavigation = new ConstructionProtocolNavigation(getConstructionProtocol());
	 	} else {	 		
	 		constProtocolNavigation.initGUI();
	 	}
	 	return constProtocolNavigation;
	 }
	 
	 public boolean isConsProtNavigationPlayButtonVisible() {
	 	if (constProtocolNavigation != null)
	 		return constProtocolNavigation.isPlayButtonVisible();
	 	else
	 		return true;
	 }
	 
	 public boolean isConsProtNavigationProtButtonVisible() {
	 	if (constProtocolNavigation != null)
	 		return constProtocolNavigation.isConsProtButtonVisible();
	 	else
	 		return true;
	 }
    
	public boolean showAuxiliaryObjects() {
		return showAuxiliaryObjects;
	}
	
	public void setShowAuxiliaryObjects(boolean flag) {
		showAuxiliaryObjects = flag;
		if (algebraView != null) 
			algebraView.setShowAuxiliaryObjects(flag);
		updateMenubar();		
	}
    
    public void setShowMenuBar(boolean flag) {
    	showMenuBar = flag;    	
    }
    
    public void initMenubar() {
    	if (menuBar == null) {
    		menuBar = new GeoGebraMenuBar(this);    	    		
    	}  
    	menuBar.initMenubar();
    }
    
    public Menubar getMenuBar() {
    	return menuBar;
    }
    
    public void setMenubar(Menubar newMenuBar) {
    	menuBar = newMenuBar;
    }
    
    public void setShowToolBar(boolean toolbar, boolean help) {
    	showToolBar = toolbar;
    	
    	if (showToolBar) {
    		if (appToolbarPanel == null) {
    			appToolbarPanel = new MyToolbar(this);
    			appToolbarPanel.setShowToolBarHelp(help);
    		}
    	}    	    	    	
    }
    
    public boolean showToolBar() {
    	return  showToolBar;        
    }
    
    public void setUndoActive(boolean flag) {
    	undoActive = flag;
    	kernel.setUndoActive(flag);  
    	updateActions();
    	isSaved = true;
    }
    
    public boolean isUndoActive() {
    	return undoActive;    	
    }
    
    /**
     * Enables or disables right clicking in this application.
     * This is useful for applets.
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
    	if (appToolbarPanel != null) {
    		appToolbarPanel.initToolbar();    	
    	}
    	    	  
    	if (!INITING) {
        	if (applet != null)
        		SwingUtilities.updateComponentTreeUI(applet);
        	if (frame != null)
        		SwingUtilities.updateComponentTreeUI(frame);
        }
    	    	    	    	
    	setMoveMode();
    }
    
    public void removeFromToolbarDefinition(int mode) {    	
    	if (strCustomToolbarDefinition != null) {    		
    		//System.out.println("before: " + strCustomToolbarDefinition + ",  delete " + mode);
    		
    		strCustomToolbarDefinition = 
    			strCustomToolbarDefinition.replaceAll(Integer.toString(mode), "");
    		
    		if (mode >= EuclidianView.MACRO_MODE_ID_OFFSET) {
    			// if a macro mode is removed all higher macros get a new id (i.e. id-1)
    			int lastID = kernel.getMacroNumber() + EuclidianView.MACRO_MODE_ID_OFFSET-1;
    			for (int id=mode+1; id <= lastID; id++) {
    				strCustomToolbarDefinition = 
    					strCustomToolbarDefinition.replaceAll(Integer.toString(id), Integer.toString(id-1));
    			}
    		}

    		//System.out.println("after: " + strCustomToolbarDefinition);
    	}    	
    }
    
    public void addToToolbarDefinition(int mode) {    	
    	if (strCustomToolbarDefinition != null) {
    		strCustomToolbarDefinition = 
    			strCustomToolbarDefinition + " | " + mode;
    	}  
    }
    
    public void updateMenubar() {
    	if (menuBar != null)
    		menuBar.updateMenubar();
    	updateActions();
    	System.gc();
    }
    
    private void updateSelection() {
    	if (menuBar != null)
    		menuBar.updateSelection();   	       
    }
    
    public void updateMenuWindow() {
    	if (menuBar != null) {
    		menuBar.updateMenuWindow();
    		menuBar.updateMenuFile();
    		System.gc();
    	}    	
    }
    
    public void updateCommandDictionary() {
    	if (commandDict != null) {
    		// make sure all macro commands are in dictionary
    		fillCommandDict();
    		
    		if (algebraInput != null) {
    			algebraInput.setCommandNames();    		
    		}
    	}    	  
    }      
    
    private void initShowAxesGridActions() {
    	showAxesAction = new AbstractAction(getMenu("Axes"),
    			getImageIcon("axes.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
                // toggle axes
				boolean bothAxesShown = euclidianView.getShowXaxis() && euclidianView.getShowYaxis();
				euclidianView.showAxes(!bothAxesShown, !bothAxesShown);				            	
                euclidianView.repaint();
                storeUndoInfo();
                updateMenubar();
            }
        };

        showGridAction = new AbstractAction(getMenu("Grid"),
        		getImageIcon("grid.gif")) {
        	private static final long serialVersionUID = 1L;
        	
            public void actionPerformed(ActionEvent e) {
                    // toggle grid
                euclidianView.showGrid(!euclidianView.getShowGrid());
                euclidianView.repaint();
                storeUndoInfo();                
                updateMenubar();
            }
        };
        
        undoAction = new AbstractAction(getMenu("Undo"),
				getImageIcon("edit-undo.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (propDialog != null && propDialog.isShowing()) 
		    		propDialog.cancel();
				setWaitCursor();
				kernel.undo();
				updateActions();
				setDefaultCursor();
				System.gc();
			}
		};

		redoAction = new AbstractAction(getMenu("Redo"),
				getImageIcon("edit-redo.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (propDialog != null && propDialog.isShowing()) 
		    		propDialog.cancel();
				
				setWaitCursor();
				kernel.redo();	
				updateActions();
				setDefaultCursor();
				System.gc();
			}
		};
		
		updateActions();
    }
    
    private void updateActions() {    
    	if (undoAction == null || !undoActive) return;
    	undoAction.setEnabled(kernel.undoPossible());
    	redoAction.setEnabled(kernel.redoPossible());    	
	}

   
   
    /**
     * // TODO: think about this
     * Downloads the latest jar files from the GeoGebra server.    
     *
    private void updateGeoGebra() {    	 
    	try {
	    	File dest = new File(codebase + Application.JAR_FILE);
			URL jarURL = new URL(Application.UPDATE_URL + Application.JAR_FILE);
	
			if (dest.exists()) {
				//  check if jarURL is newer then dest
				try {
					URLConnection connection = jarURL.openConnection();
					if (connection.getLastModified() <= dest.lastModified()) {
						// TODO: localize
						showMessage("No update available");
						return;
					}
						
				} catch (Exception e) {
					// we don't know if the file behind jarURL is newer than dest
					// so don't do anything
					// TODO: localize
					showMessage("No update available: " + (e.getMessage()));
					return;
				}
			}
			
			// copy JAR_FILE
		    if (!CopyURLToFile.copyURLToFile(this, jarURL, dest)) return;
			
			// copy properties file
			dest = new File(codebase + Application.PROPERTIES_FILE);
			jarURL = new URL(Application.UPDATE_URL + Application.PROPERTIES_FILE);		
			if (!CopyURLToFile.copyURLToFile(this, jarURL, dest)) return;
			
			// copy jscl file		
			dest = new File(codebase + Application.JSCL_FILE);
			jarURL = new URL(Application.UPDATE_URL + Application.JSCL_FILE);		
			if (!CopyURLToFile.copyURLToFile(this, jarURL, dest)) return;
						
			// TODO: localize
			showMessage("Update finished. Please restart GeoGebra.");
    	} catch (Exception e) {
    		// TODO: localize
			showError("Update failed: "+ e.getMessage());
    	}
    } */

    public void openHelp() {
        try {
            URL helpURL = getHelpURL(currentLocale);
            showURLinBrowser(helpURL);
        } catch (MyError e) {           
            showError(e);
        } catch (Exception e) {           
            System.err.println(
                "openHelp error: " + e.toString() + e.getMessage());
            showError(e.getMessage());
        }
    }    
    
    public void showURLinBrowser(String strURL) {
    	try {
    		URL url = new URL(strURL);
    		showURLinBrowser(url);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void showURLinBrowser(URL url) {
    //	if (applet != null) {
     //   	applet.getAppletContext().showDocument(url, "_blank");
     //   } else {
        	BrowserLauncher.openURL(url.toExternalForm());
      //  }
    }

    private URL getHelpURL(Locale locale) throws Exception {
    	 // try to get help for current locale (language + country + variant)
        URL helpURL = getHelpURL(locale.toString());
        if (helpURL != null) {        	
        	return helpURL;
        }
    	
        
        /* Michael Borcherds 2008-03-26
         * removed and replaced with dummy help files which redirect
        // try to get help for current language
        String  language = locale.getLanguage();     
        helpURL = getHelpURL(language);
        if (helpURL != null) {        	
        	return helpURL;
        }
                
        // for Catalan and Basque we take the 
        // Spanish help instead of the English one
        if (language.equals("eu") || language.equals("ca")) {        	
        	helpURL = getHelpURL("es"); // Spanish
        	if (helpURL != null) {            	
            	return helpURL;
            }
        }
        */
                
        // last attempt: try to get English help 
        helpURL = getHelpURL("en");
        if (helpURL != null) {        	
        	return helpURL;
        }
        
        // sorry, no help available
        throw new Exception("HelpNotFound");
    }
    
    private URL getHelpURL(String languageISOcode)  {
    	// try to get help for given language
        String strFile = "docu" + languageISOcode + "/index.html";
        String strURL = GEOGEBRA_WEBSITE + "help/" + strFile;
        
        if (GeoGebra.MAC_OS) {
        	strFile = codebase.getPath().substring(0, codebase.getPath().lastIndexOf("/Java/")) + "/Help/" + strFile;
        }
        
        try {
            File f = new File(strFile);
            if (f.exists())
				return f.toURL();
			else { // web url 
                URL url =   new URL(strURL);
                if (Util.existsHttpURL(url)) return url;
            }
        } catch (Exception e) {        	
        }
        return null;
    }



    /**
     * Returns text "Created with <ApplicationName>" and link
     * to application homepage in html.
     */
    public String getCreatedWithHTML() {
        StringBuffer sb = new StringBuffer();
        sb.append(getPlain("CreatedWith"));
        sb.append(" ");
        sb.append("<a href=\"");
        sb.append(getPlain("ApplicationURL"));
        sb.append("\" target=\"_blank\" >");
        sb.append(Util.toHTMLString(getPlain("ApplicationName")));
        sb.append("</a>");
        return sb.toString();
    }

    public boolean save() {
    	if (propDialog != null && propDialog.isShowing()) 
    		propDialog.cancel();
    	
        if (currentFile != null)
			return saveGeoGebraFile(currentFile);
		else
			return saveAs();
    }

    public boolean saveAs() {
        File file =
            showSaveDialog(
                FILE_EXT_GEOGEBRA, currentFile,
                getPlain("ApplicationName") + " " + getMenu("Files"));
        if (file == null)
            return false;

        boolean success = saveGeoGebraFile(file);
        if (success)
            setCurrentFile(file);
        return success;
    }

    public File showSaveDialog(String fileExtension, File selectedFile,
    		String fileDescription) {
        boolean done = false;
        File file = null;

        initFileChooser();
        fileChooser.setCurrentDirectory(currentPath);
        
        // set selected file
        if (selectedFile == null) {
        	selectedFile = removeExtension(fileChooser.getSelectedFile());        	
        }
        if (selectedFile != null) {             	
        	selectedFile = addExtension(selectedFile, fileExtension);
        	fileChooser.setSelectedFile(selectedFile);
        }

        MyFileFilter fileFilter = new MyFileFilter();
        fileFilter.addExtension(fileExtension);
        if (fileDescription != null)
            fileFilter.setDescription(fileDescription);
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(fileFilter);

        while (!done) {
            // show save dialog
            int returnVal = fileChooser.showSaveDialog(mainComp);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
            	file = fileChooser.getSelectedFile();
            	
            	// remove all special characters from HTML filename
                if (fileExtension == Application.FILE_EXT_HTML) {    
                	file = Application.removeExtension(file);
                	file = new File(file.getParent(), Util.keepOnlyLettersAndDigits(file.getName()));
                }
                
                // remove "*<>/\?|:
                file = new File(file.getParent(), Util.processFilename(file.getName())); // Michael Borcherds 2007-11-23
            	
            	// add file extension
                file = addExtension(file, fileExtension);
                fileChooser.setSelectedFile(file);
                                                
                if (file.exists()) {
                    // ask overwrite question
                    int n =
                        JOptionPane.showConfirmDialog(
                            mainComp,
                            getPlain("OverwriteFile")
                                + "\n"
                                + file.getAbsolutePath(),
                            getPlain("Question"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    done = (n == JOptionPane.YES_OPTION);
                } else {
                    done = true;
                }
            } else
				return null;
        }
        return file;
    }

    public static File addExtension(File file, String fileExtension) {
    	if (file == null) return null;
        if (getExtension(file).equals(fileExtension)) 
			return file;
		else
			return new File(file.getParentFile(), // path
            		file.getName() + '.' + fileExtension); // filename      
    }
    
    public static File removeExtension(File file) {
    	if (file == null) return null;
    	 String fileName = file.getName();
         int dotPos = fileName.indexOf('.');

         if (dotPos <= 0)
			return file;
		else
			return new File(file.getParentFile(), // path
            				fileName.substring(0, dotPos) ); 
    }
    
    public static String getExtension(File file) {
   	 String fileName = file.getName();
        int dotPos = fileName.lastIndexOf('.');

        if (dotPos <= 0 || dotPos == fileName.length()-1)
			return "";
		else
			return fileName.substring(dotPos+1).toLowerCase(); // Michael Borcherds 2008-02-06 added .toLowerCase()
   }

    public void openFile() {    	      	
    	if (propDialog != null && propDialog.isShowing()) 
    		propDialog.cancel();
    	
        if (isSaved() || saveCurrentFile()) {
        	File oldCurrentFile = currentFile;
        	currentFile = null;
        	
       	 	initFileChooser();
	        fileChooser.setCurrentDirectory(currentPath);
	        fileChooser.setMultiSelectionEnabled(true);
	
	        MyFileFilter fileFilter = new MyFileFilter();
	        fileFilter.addExtension(FILE_EXT_GEOGEBRA);
	        fileFilter.addExtension(FILE_EXT_GEOGEBRA_TOOL);
	        fileFilter.setDescription(
	            getPlain("ApplicationName") + " " + getMenu("Files"));
	        fileChooser.resetChoosableFileFilters();
	        fileChooser.setFileFilter(fileFilter);
	
	        int returnVal = fileChooser.showOpenDialog(mainComp);		        
	        
	        File [] files = null;
	        if (returnVal == JFileChooser.APPROVE_OPTION) {             
	            files = fileChooser.getSelectedFiles();  	               	            
	        }	  
	        doOpenFiles(files, true);
	       
	        if (currentFile == null) {	        	
	        	setCurrentFile(oldCurrentFile);
	        }	        	
	        fileChooser.setMultiSelectionEnabled(false);
        }
    }    
    
    public synchronized void doOpenFiles(File [] files, boolean allowOpeningInThisInstance) {    	
        // there are selected files
        setWaitCursor();
        if (files != null) {
        	File file;
        	int counter = 0;	        	
        	for (int i=0; i < files.length; i++) {
        		file = files[i];
        		
        		if (!file.exists()) {
	            	file = addExtension(file, FILE_EXT_GEOGEBRA);
	            } 
		        
        		if (file.exists()) {	        			
        			if (FILE_EXT_GEOGEBRA_TOOL.equals(getExtension(file).toLowerCase())) {	        			
        				// load macro file
        				loadFile(file, true); 
        			} 	        			
        			else {	        
        				// standard GeoGebra file
            			GeoGebra inst = GeoGebra.getInstanceWithFile(file);
            			if (inst == null) {
                			counter++;
                			if (counter == 1 && allowOpeningInThisInstance) {
                				// open first file in current window		        				
        						loadFile(file, false); 								
                			} else {		        				
                				// create new window for file
                				try {
        							String [] args = { file.getCanonicalPath() };
        							GeoGebra wnd = GeoGebra.createNewWindow(args);
        							wnd.toFront();
        							wnd.requestFocus();
        						} catch (Exception e) {
        							e.printStackTrace();
        						}
                			}	 	   
            			} else if (counter == 0){
            				// there is an instance with this file opened
            				inst.toFront();
            				inst.requestFocus();
            			}
        			}
        		}		       
        	}	        	        	
        }	      
        setDefaultCursor();    	
    }
    
    public boolean loadFile(final File file, boolean isMacroFile) {
        if (!file.exists()) {
            // show file not found message
            JOptionPane.showConfirmDialog(
            		mainComp,
                getError("FileNotFound") + ":\n" + file.getAbsolutePath(),
                getError("Error"),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE);
            return false;
        }             
        
        if (!isMacroFile) {
            // hide navigation bar for construction steps if visible
            setShowConstructionProtocolNavigation(false); 
        }
        
        boolean success = loadXML(file, isMacroFile);  
       
        if (isMacroFile) {
        	updateToolBar();        	
        	updateContentPane();
        } else {	        
	        // update GUI                
	        if (euclidianView.hasPreferredSize()) {
	            // update GUI: size of euclidian view was set
	        	updateContentPaneAndSize();                              
	        } else {
	        	updateContentPane();
	        }
        }
        
		return success;               
    }

    public void deleteAllGeoElements() {
    	// delete all
    	Object [] geos = kernel.getConstruction().getGeoSetConstructionOrder().toArray();
    	if (geos.length == 0) return;
    	
    	if (isSaved() || saveCurrentFile()) {
	    	for (int i=0; i < geos.length; i++) {
	    		GeoElement geo = (GeoElement) geos[i];
	    		if (geo.isLabelSet()) 
	    			geo.remove();
	    	}
    		kernel.initUndoInfo();
    		setCurrentFile(null);
    		setMoveMode();
    	}
    	
    	/*
        if (isSaved() || saveCurrentFile()) {
            clearAll();
            setCurrentFile(null);               
            updateMenubar();
        }*/
    }        

    public void exit() {
    	// glassPane is active: don't exit now!
    	if (glassPaneListener != null) return;
    	
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
    	if (glassPaneListener != null) return;
    	
    	ArrayList insts = GeoGebra.getInstances();
    	GeoGebra [] instsCopy = new GeoGebra[insts.size()];
    	for (int i=0; i < instsCopy.length; i++) {
    		instsCopy[i] = (GeoGebra) insts.get(i);
    	}
    	
    	for (int i=0; i < instsCopy.length; i++) {
    		instsCopy[i].getApplication().exit();
    	}    	    	
    }

    // returns true for YES or NO and false for CANCEL
    public boolean saveCurrentFile() {    	
    	if (propDialog != null && propDialog.isShowing()) 
    		propDialog.cancel();
    	euclidianView.reset();
    	
    	// use null component for iconified frame
    	Component comp = frame != null && !frame.isIconified() ? frame : null;
    	
        int returnVal =
            JOptionPane.showConfirmDialog(
            		comp,
                getMenu("SaveCurrentFileQuestion"),
                getPlain("ApplicationName") + " - " + getPlain("Question"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        switch (returnVal) {
            case JOptionPane.YES_OPTION :
                return save();

            case JOptionPane.NO_OPTION :
                return true;

            default : // case JOptionPane.CANCEL_OPTION:
                return false;
        }
    }

   
    /*
    public void updateStatusLabelAxesRatio() {
    	if (statusLabelAxesRatio != null)   
    		statusLabelAxesRatio.setText(
    				euclidianView.getXYscaleRatioString());
    }*/
    
    public void setMode(int mode) {  
    	// if the properties dialog is showing, close it
    	if (propDialog != null && propDialog.isShowing()) {    		
    		propDialog.setVisible(false);	
    	}
    	
    	currentSelectionListener = null;
    	
    	 if (euclidianView != null)
    		 euclidianView.setMode(mode);
        if (algebraView != null)
        	algebraView.reset();
        
        if (algebraInput != null && showAlgebraInput) {
        	if (mode == EuclidianView.MODE_ALGEBRA_INPUT) {
            	currentSelectionListener = algebraInput.getTextField();            	                       	
            	algebraInput.getInputButton().setSelected(true);
            }
        	else {
        		algebraInput.getInputButton().setSelected(false);
        	}
        }                
        
        // select toolbar button
        if (appToolbarPanel != null) {
        	appToolbarPanel.setSelectedMode(mode);
        }
    }
    
   
    
    public int getMode() {
        return euclidianView.getMode();
    }
    
   
  

    /***********************************
     * SAVE / LOAD methodes
     ***********************************/

    /**
        *  Exports construction protocol as html 
        */
    final public void exportConstructionProtocolHTML() {
        if (constProtocol == null) {
            constProtocol = new ConstructionProtocol(this);
        }
        constProtocol.initProtocol();
        constProtocol.showHTMLExportDialog();
    }

  
    

    /**
     * Saves all objects.
     * @return true if successful
     */
    final public boolean saveGeoGebraFile(File file) {
        try {
        	mainComp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            myXMLio.writeGeoGebraFile(file);
            isSaved = true;
            mainComp.setCursor(Cursor.getDefaultCursor());
            return true;
        } catch (Exception e) {
        	mainComp.setCursor(Cursor.getDefaultCursor());
            showError("SaveFileFailed");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Saves given macros to file.
     * @return true if successful
     */
    final public boolean saveMacroFile(File file, ArrayList macros) {
        try {
        	mainComp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            myXMLio.writeMacroFile(file, macros);            
            mainComp.setCursor(Cursor.getDefaultCursor());
            return true;
        } catch (Exception e) {
        	mainComp.setCursor(Cursor.getDefaultCursor());
            showError("SaveFileFailed");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads construction file
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
      * @return true if successful
      */
    final public boolean loadXML(URL url, boolean isMacroFile) {
    	try {
    		return loadXML(url.openStream(), isMacroFile);
    	} 
    	catch (Exception e) {
            setCurrentFile(null);
            e.printStackTrace();
            showError(getError("LoadFileFailed") + ":\n" + url);
            return false;
        }
    }
    
    private boolean loadXML(InputStream is, boolean isMacroFile) throws Exception {
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
            return true;
        } catch (MyError err) {
            setCurrentFile(null);
            showError(err);
            return false;
        }
    }
    
    //FKH 20040826
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
    //endFKH
    
    public String getPreferencesXML() {
    	return myXMLio.getPreferencesXML();
    }
    
    public byte [] getMacroFileAsByteArray() {
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
    
    public void loadMacroFileFromByteArray(byte [] byteArray, boolean removeOldMacros) {
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
    final public void clearAll() {       
        // load preferences
        GeoGebraPreferences.loadXMLPreferences(this);        
        updateContentPane();
    	
    	// clear construction
    	kernel.clearConstruction();
        kernel.initUndoInfo();
    	
        isSaved = true;
        System.gc();        
    }*/

   /**
     * Returns gui settings in XML format
     */
    public String getUserInterfaceXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<gui>\n");

        sb.append("\t<show");
        sb.append(" algebraView=\"");
        sb.append(showAlgebraView);     
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
            sb.append("\" horizontal=\"");
            sb.append(sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT);
            sb.append("\"/>\n");
        }
        
        // custom toolbar
        if (strCustomToolbarDefinition != null && 
        	!strCustomToolbarDefinition.equals(getDefaultToolbarString())) {
        	sb.append("\t<toolbar");
        	sb.append(" str=\"");
        	sb.append(strCustomToolbarDefinition);     
        	sb.append("\"/>\n");
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
    
    public String getConsProtocolXML() {
    	StringBuffer sb = new StringBuffer();    	
		    		
    	// construction protocol
		if (constProtocol != null) {
			sb.append(constProtocol.getConsProtocolXML());			
		}
				
		// navigation bar of construction protocol
		if (showConsProtNavigation) {
	    	sb.append("\t<consProtNavigationBar ");    	    	        	
	    		sb.append("show=\"");
	    		sb.append(showConsProtNavigation);
	    		sb.append("\"");
	    		sb.append(" playButton=\"");
	    		sb.append(constProtocolNavigation.isPlayButtonVisible());
	    		sb.append("\"");    		    	
	    		sb.append(" playDelay=\"");
	    		sb.append(constProtocolNavigation.getPlayDelay());
	    		sb.append("\"");
	    		sb.append(" protButton=\"");
	    		sb.append(constProtocolNavigation.isConsProtButtonVisible());	    		
	    		sb.append("\"");
	    		sb.append(" consStep=\"");
	    		sb.append(kernel.getConstructionStep());	    		
	    		sb.append("\"");
	    	sb.append("/>\n");	
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
	        	codebase =  applet.getCodeBase();
	        } else {
	         	String path = Application.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm();	     	
		    	if (path.endsWith(JAR_FILES[0])) // remove "geogebra.jar" from end	    
		    		path = path.substring(0, path.length() - JAR_FILES[0].length());	    		    		    	
		    	codebase = new URL(path);        
	        }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        
    	//System.out.println("codebase: " + codebase);
    }
    
    public PropertiesDialogGeoElement getPropDialog() {
    	return propDialog;
    }
    
/* ** selection handling ***/
    
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
     * @param geos
     */
    final public void setSelectedGeos(ArrayList geos) { 
    	clearSelectedGeos(false);
    	if (geos != null) {    		
	        for (int i=0; i < geos.size(); i++) {
	        	GeoElement geo = (GeoElement) geos.get(i);
	        	addSelectedGeo(geo, false);
	        }
    	}
        kernel.notifyRepaint();
        updateSelection();   	    
    }
    
    /* Michael Borcherds 2008-03-03
     * modified to select all of a layer
    * pass layer==-1 to select all objects
    */
    final public void selectAll(int layer) {    
    	clearSelectedGeos(false);
    	
    	Iterator it = kernel.getConstruction().getGeoSetLabelOrder().iterator();    	
    	while (it.hasNext()) {    		
        	GeoElement geo = (GeoElement) it.next();
        	if (layer == -1 || geo.getLayer() == layer) addSelectedGeo(geo, false);
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
	    	for (int i=0; i < size; i++) {
	    		GeoElement geo = (GeoElement) selectedGeos.get(i);
	    		geo.setSelected(false);
	    	}    	
	    	selectedGeos.clear();
	    	if (repaint) kernel.notifyRepaint();
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
    	if (geo == null) return;    	
    	
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
    	if (geo == null) return;
        
    	selectedGeos.remove(geo);
    	geo.setSelected(false);     	    
    	if (repaint) kernel.notifyRepaint();
    	updateSelection();
    }
    
    final public void addSelectedGeo(GeoElement geo) {
    	addSelectedGeo(geo, true);
    }    
    
    final public void addSelectedGeo(GeoElement geo, boolean repaint) {
    	if (geo == null || selectedGeos.contains(geo)) return;
    	
    	selectedGeos.add(geo);
    	geo.setSelected(true);     	    
    	if (repaint) kernel.notifyRepaint();
    	updateSelection();
    }

    // remember split divider location
    private class DividerChangeListener implements PropertyChangeListener {                     
        public void propertyChange(PropertyChangeEvent e) {
            Number value = (Number) e.getNewValue();
            int newDivLoc = value.intValue();
            if (sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
                initSplitDividerLocationHOR = newDivLoc;
            else 
                initSplitDividerLocationVER = newDivLoc;
            isSaved = false;

            if (applet != null)
                SwingUtilities.updateComponentTreeUI(applet);
            if (frame != null)                   
                SwingUtilities.updateComponentTreeUI(frame);
        }
    }
      
    
    /* Event dispatching */
    private GlassPaneListener glassPaneListener;
    
    public void startDispatchingEventsTo(JComponent comp) {
    	// close open windows
    	if (propDialog != null && propDialog.isShowing())
    		propDialog.cancel();    	
    	if (constProtocol != null && constProtocol.isShowing())
    		constProtocol.setVisible(false);
    	
    	
    	if (glassPaneListener == null) { 
    		Component glassPane = getGlassPane();
    		glassPaneListener = new GlassPaneListener(glassPane,
    				getContentPane(), comp);
    		
    		// mouse
    		glassPane.addMouseListener(glassPaneListener);
    		glassPane.addMouseMotionListener(glassPaneListener);
    		
    		// keys           
            KeyboardFocusManager.getCurrentKeyboardFocusManager().
            	addKeyEventDispatcher(glassPaneListener);	
    		
    		glassPane.setVisible(true);     		
    	}    	
    }
    
    public void stopDispatchingEvents() {   
    	if (glassPaneListener != null) {  
    		Component glassPane = getGlassPane();
    		glassPane.removeMouseListener(glassPaneListener);
    		glassPane.removeMouseMotionListener(glassPaneListener);
    		
    	     KeyboardFocusManager.getCurrentKeyboardFocusManager().
             	removeKeyEventDispatcher(glassPaneListener);    	
    		
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

	public AbstractAction getRedoAction() {
		return redoAction;
	}

	public AbstractAction getUndoAction() {		
		return undoAction;
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
			if (rootComp instanceof JDialog &&
				e.getKeyCode() == KeyEvent.VK_ESCAPE) 
			{
				((JDialog) rootComp).setVisible(false);
				return true;
			}
			return false;
		}						

		boolean consumed = false;
		Object source = e.getSource();		

		// catch all key events from algebra view and give
		// them to the algebra controller
		AlgebraView av = getAlgebraView();
		if (source == av) {
			switch (e.getID()) {
			case KeyEvent.KEY_PRESSED:
				consumed = getAlgebraController().keyPressedConsumed(e);
				break;
			}
		}
		if (consumed)
			return true;

		switch (e.getKeyCode()) {
		case KeyEvent.VK_F3:
			// F3 key: set focus to input field
			AlgebraInput ai = getAlgebraInput();
			if (ai != null) {
				ai.setFocus();
				consumed = true;
			}
			break;

		// ESC changes to move mode
		case KeyEvent.VK_ESCAPE:
			// ESC is also handeled by algebra input field
			ai = getAlgebraInput();
			if (ai != null && ai.hasFocus()) {
				consumed = false;
			} else {						
				setMode(EuclidianView.MODE_MOVE);
				consumed = true;
			}
			break;
		}
		if (consumed)
			return true;		

		return consumed;
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
	 * Copies all jar files of this application to the 
	 * temp directory
	 */
	private void copyJarsToTempDir() {
		try {		
			String tempDir = System.getProperty("java.io.tmpdir"); 
			
			// copy jar files to tempDir
			for (int i=0; i < JAR_FILES.length; i++) {
				File dest = new File(tempDir, JAR_FILES[i]);
				URL src = new URL(getCodeBase() + JAR_FILES[i]);
				CopyURLToFile.copyURLToFile(src, dest);
			}
			
			//System.out.println("copied geogebra jar files to temp directory " + tempDir);
			
		} catch (Exception e) {		
			System.err.println("copyJarsToTempDir: " + e.getMessage());
		}			
	}

	
	/**
	 * Copies all jar files of this application to the 
	 * given directory
	 * @param destDir
	 */
	public void copyJarsTo(String destDir, boolean copyExportJAR) throws Exception {
		// try to copy from temp dir
		String tempDir = System.getProperty("java.io.tmpdir"); 
		
		URL srcDir;			
		File tempJarFile = new File(tempDir, JAR_FILES[0]);			
		
//		System.out.println("temp jar file: " + tempJarFile);
//		System.out.println("   exists " + tempJarFile.exists());	
		
		if (tempJarFile.exists()) {
			// try to copy from temp dir 
			srcDir = new File(tempDir).toURL();
		} else {
			//	try to copy from codebas
			srcDir = getCodeBase();
		}
		
		// copy jar files to tempDir
		for (int i=0; i < JAR_FILES.length; i++) {	
			if (!copyExportJAR && JAR_FILES[i].endsWith("export.jar"))
				continue;
			
			File dest = new File(destDir, JAR_FILES[i]);	
			URL src = new URL(srcDir, JAR_FILES[i]);
			CopyURLToFile.copyURLToFile(src, dest);
		}

//		System.out.println("copied geogebra jar files from " + srcDir + " to " + destDir);	
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
	
    /** PluginManager gets API with this */
	//	 H-P Ulven 2008-04-16
    public GgbAPI getGgbApi(){return this.ggbapi;}		

    
    /** MenuBarImpl gets pluginmenu from Application with this */
	// H-P Ulven 2008-04-16
    public javax.swing.JMenu  getPluginMenu(){
        return pluginmanager.getPluginMenu();
    }//getPluginMenu()
	
	
}
