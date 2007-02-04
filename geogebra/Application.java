/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
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
import geogebra.export.GraphicExportDialog;
import geogebra.export.WorksheetExportDialog;
import geogebra.gui.DrawingPadPopupMenu;
import geogebra.gui.EuclidianPropDialog;
import geogebra.gui.GUIController;
import geogebra.gui.GeoGebra;
import geogebra.gui.HelpBrowser;
import geogebra.gui.ModeToggleButtonGroup;
import geogebra.gui.ModeToggleMenu;
import geogebra.gui.MyPopupMenu;
import geogebra.gui.PrintPreview;
import geogebra.gui.PropertiesDialog;
import geogebra.gui.SliderDialog;
import geogebra.gui.TextInputDialog;
import geogebra.io.MyXMLio;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.Relation;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.util.AngleInputDialog;
import geogebra.util.ImageManager;
import geogebra.util.InputDialog;
import geogebra.util.InputHandler;
import geogebra.util.LaTeXinputHandler;
import geogebra.util.Util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.FontUIResource;


public class Application {

    public static final String buildDate = "22. January 2006";
	
    public static final String versionString = "Pre-Release";    
    public static final String XML_FILE_FORMAT = "3.0";    
  
    // GeoGebra basic jar file
    public static final String JAR_FILE = "geogebra.jar";
    // GeoGebra GUI jar file
    public static final String GUI_FILE = "geogebra_gui.jar";
	// GeoGebra uses a language file for all languages except en and de
	public final static String PROPERTIES_FILE = "geogebra_properties.jar";
	// GeoGebra uses a computer algebra system 
	public final static String CAS_FILE = "geogebra_cas.jar";
	
	public final static String GEOGEBRA_WEBSITE = "http://www.geogebra.org";
	
	// update URL
	//public static final String UPDATE_URL = "http://www.geogebra.at/webstart/unpacked/";
    
    // supported GUI languages (from properties files)
    private static ArrayList supportedLocales = new ArrayList();
    static {
    	supportedLocales.add( new Locale("ar") );           // Arabic
    	supportedLocales.add( new Locale("eu") );           // Basque
    	supportedLocales.add( new Locale("bs") );          	// Bosnian
    	supportedLocales.add( new Locale("bg") );          	// Bulgarian
    	supportedLocales.add( new Locale("ca") );           // Catalan
        supportedLocales.add( new Locale("zh") );          	// Chinese
        supportedLocales.add( new Locale("hr") );          	// Croatian
    	supportedLocales.add( new Locale("cz") );          	// Czeck
    	supportedLocales.add( new Locale("da") );     	 	// Danish   
    	supportedLocales.add( new Locale("nl") );     	 	// Dutch
    	supportedLocales.add( new Locale("en"));          	// English
    	supportedLocales.add( new Locale("fr") );     		 // French
    	supportedLocales.add( new Locale("gl") );           // Galician
        supportedLocales.add( new Locale("de") );          	// German   
    	supportedLocales.add( new Locale("de", "AT") ); 	// German (Austria)
        supportedLocales.add( new Locale("el") );            // Greek                
        supportedLocales.add( new Locale("hu") );          	// Hungarian
        supportedLocales.add( new Locale("it") );     		 	// Italian
        supportedLocales.add( new Locale("no", "NO") );     	 // Norwegian (Bokmal)
        supportedLocales.add( new Locale("no", "NO", "NY") );  // Norwegian (Nynorsk)
        supportedLocales.add( new Locale("fa") );             	// Persian
        supportedLocales.add( new Locale("pt", "BR") );     // Portugese (Brazil)
        supportedLocales.add( new Locale("pt", "PT") );     // Portuguese (Portugal)        
        supportedLocales.add( new Locale("sr") );           	// Serbian
        supportedLocales.add( new Locale("sk") );          	// Slovakian  
        supportedLocales.add( new Locale("sl") );           	// Slovenian
        supportedLocales.add( new Locale("es") );          	// Spanish   
        supportedLocales.add( new Locale("tr") );          	// Turkish
    }    
    
    // specialLanguageNames: Java does show an English name for all languages
    //   supported by GeoGebra, so some language codes have to be treated specially
    private static Hashtable specialLanguageNames = new Hashtable();
    static {
    	specialLanguageNames.put("deAT", "German (Austria)");
    	specialLanguageNames.put("noNO", "Norsk (bokm\u00e5l)");
    	specialLanguageNames.put("noNONY", "Norsk (nynorsk)");
    	specialLanguageNames.put("bs", "Bosnian");
    	specialLanguageNames.put("cz", "Czeck");
    	specialLanguageNames.put("ptBR", "Portuguese (Brazil)");
    	specialLanguageNames.put("ptPT", "Portuguese (Portugal)");    	    	
    }
    
    public static final Color COLOR_SELECTION = new Color(225, 225, 245);
    public static final String STANDARD_FONT_NAME = "SansSerif";  

    // file extension string
    public static final String FILE_EXT_GEOGEBRA = "ggb";
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
    private GeoGebraApplet applet;
    private Component mainComp;
    private boolean isApplet = false;    
    private boolean showResetIcon = false;
    private String codebase;

    private AlgebraView algebraView;
    private EuclidianView euclidianView;
    private Kernel kernel;
    private MyXMLio myXMLio;

    private AlgebraController algebraController;
    private EuclidianController euclidianController;
    private GUIController guiController;    
    private LanguageActionListener langListener;
    private GeoElementSelectionListener currentSelectionListener;

    // For language specific settings
    private Locale currentLocale;
    private ResourceBundle rbmenu, rbcommand, rberror, rbplain, rbsettings;
    private Hashtable translateCommandTable;

    // Actions
    private AbstractAction 
        showAxesAction,
        showGridAction,
        refreshAction,
        drawingPadToClipboardAction,
        newFileAction,
        newWindowAction,
        propertiesAction,
        constProtocolAction,
        drawingPadPropAction,
        showAlgebraViewAction,     
        showAlgebraInputAction,
        showCmdListAction,
        horizontalSplitAction,
        showAuxiliaryObjectsAction,
        showConsProtNavigationAction,
        showConsProtNavigationPlayAction,
        showConsProtNavigationOpenProtAction,
        loadAction,
        saveAction,
        saveAsAction,
        printProtocolAction,
        printEuclidianViewAction,
        undoAction,
        redoAction,
        exitAction,
        exitAllAction,
        helpAction,        
        //updateAction,
        infoAction,
        exportGraphicAction,
        htmlCPAction,
        exportWorksheet;

    private PropertiesDialog propDialog;
    private ConstructionProtocol constProtocol;
    private ConstructionProtocolNavigation constProtocolNavigation;
    private ImageManager imageManager;
    private HelpBrowser helpBrowser;

    private boolean INITING = false;
    private boolean showAlgebraView = true; 
    private boolean showAuxiliaryObjects = true;
    private boolean showAlgebraInput = true;
    private boolean showCmdList = true;    
    private boolean showToolBar = true;
    private boolean showMenuBar = true;
    private boolean showConsProtNavigation = false;
    private boolean showAxes = true;
    private boolean showGrid = false;
            
    private boolean undoActive = true;

    private File currentPath, currentImagePath, currentFile = null;
    private boolean isSaved = true;
    private boolean actionsInited = false;
    private int appFontSize;
    public Font boldFont, plainFont, smallFont;
    private String FONT_NAME = STANDARD_FONT_NAME;
    
    private String strToolBarDefinition;
    private JPanel appToolBarPanel;     
    private ArrayList moveToggleMenus;
    private JFileChooser fileChooser;
    private JMenuBar menuBar;
    private AlgebraInput algebraInput;
    private JPanel centerPanel;
    private JLabel modeNameLabel, statusLabelAxesRatio;
    private JCheckBoxMenuItem cbShowAxes, cbShowGrid, cbShowAlgebraView,
                cbShowAuxiliaryObjects, cbHorizontalSplit, cbShowConsProtNavigation,
                cbShowConsProtNavigationPlay, cbShowConsProtNavigationOpenProt,
				cbShowAlgebraInput, cbShowCmdList;
    private JMenu menuAngleUnit, menuPointCapturing, menuDecimalPlaces,
			      menuPointStyle,menuRightAngleStyle, menuCoordStyle, menuWindow, menuFile;
    private JMenuItem miCloseAll;

    private JSplitPane sp;
    private int initSplitDividerLocationHOR = 250; // init value
    private int initSplitDividerLocationVER = 400; // init value
    private boolean horizontalSplit = true; // 
    
    private ArrayList selectedGeos = new ArrayList();
    
    // command dictionary
    private LowerCaseDictionary commandDict;

    public Application(String[] args, GeoGebra frame, boolean undoActive) {
        this(args, frame, null, undoActive);
    }

    public Application(String[] args, GeoGebraApplet applet, boolean undoActive) {
    	this(args, null, applet, undoActive);
    }
    
    private Application(String[] args, GeoGebra frame, GeoGebraApplet applet, boolean undoActive) {

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
    	
    	this.undoActive = undoActive;    	    	
    		
		isApplet = applet != null;
		if (isApplet) {
			mainComp = applet;
		} else {
			mainComp = frame;
		}
		initCodeBase();
		handleOptionArgs(args); // note: the locale is set here too
		
		imageManager = new ImageManager(mainComp);				
		
		if (isApplet) 
			setApplet(applet); 
		else 
			setFrame(frame);

        //  init kernel
        kernel = new Kernel(this);
        kernel.setPrintDecimals(Kernel.STANDARD_PRINT_DECIMALS);
        
        //  init xml io for construction loading
        myXMLio = new MyXMLio(kernel, kernel.getConstruction());

        //  init Controllers and Views      
        guiController = new GUIController(this);   
        // for key listening 
        KeyboardFocusManager.getCurrentKeyboardFocusManager().
                addKeyEventDispatcher(guiController);	     
        algebraController = new AlgebraController(kernel, kernel.getConstruction());
        euclidianController = new EuclidianController(kernel);
        euclidianView = new EuclidianView(euclidianController, showAxes, showGrid);  
	
        // load file on startup and set fonts
        //  INITING:    to avoid multiple calls of setLabels() and updateContentPane()
        INITING = true; 
    		if (!handleFileArg(args) && undoActive) {
				kernel.initUndoInfo();
			}
    		    		    		
   			if (appFontSize == 0) setFontSize(getInitFontSize());
        INITING = false;	
        
        isSaved = true;             
    }      
    
    public void initInBackground() {
    	// init file chooser and properties dialog
    	// in a background task
    	Thread runner = new Thread() {
    		public void run() {    	 
    			try {
    				Thread.sleep(2000);
    			} catch (Exception e) {}
    			
    			if (!isApplet) {
	    			initFileChooser();
	    			initPropertiesDialog();
    			}	    		
    		}
    	};
    	runner.start();
    }
    
	private synchronized void initFileChooser() {
		if (fileChooser == null) {
        	fileChooser = new JFileChooser(currentImagePath);
        }
	}
	
	private synchronized void initPropertiesDialog() {
		if (propDialog == null) {
			propDialog = new PropertiesDialog(this);
        }		
	}
    
    public void setUnsaved() {
        isSaved = false;
    }
    
    public boolean isIniting() {
        return INITING;
    }

    /**
     * Updates the GUI of the main component.
     */
    public void updateContentPane() {
        if (INITING)
            return;        
        
        Container cp;
        if (frame == null)
        	cp = applet.getContentPane();
        else
        	cp = frame.getContentPane();                
    	
        cp.removeAll();
        cp.add(buildApplicationPanel());
        euclidianView.updateSize();
        setLAFFontSize();                
        updateComponentTreeUI();
        setMoveMode();
        
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
        	if (appToolBarPanel == null) {
        		appToolBarPanel = new JPanel();
        		 appToolBarPanel.setLayout(new BorderLayout()); 
        	}        	        	
            
        	 // NORTH: Toolbar       
        	panel.add(appToolBarPanel, BorderLayout.NORTH);        	
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
        
        /*
        // show statusBar too
        if (showToolBar) { 
        	initStatusBar();   
        	JPanel statusPanel = new JPanel(new BorderLayout());
        	statusPanel.add(eup, BorderLayout.CENTER);
        	statusPanel.add(statusBar, BorderLayout.SOUTH); 
        	statusBar.setBorder(BorderFactory.
        		     createMatteBorder(1, 0, 0, 0, Color.gray));
        		       
        	eup = statusPanel;
        }*/
        
        if (showAlgebraView) {        	
        	if (algebraView == null) {
     	        initAlgebraView(); 	
        	}
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
    
    /*
    private void initStatusBar() {
    	if (statusLabelAxesRatio == null) {    	
    		statusLabelAxesRatio = new JLabel();    		
    	}
    	statusBar = new JPanel(new BorderLayout(5, 5));  
        statusBar.add(statusLabelAxesRatio, BorderLayout.EAST);         
    }*/
    
    public JPanel getCenterPanel() {
        return centerPanel;
    }
    
    BufferedImage getCenterPanelImage() {
        // center Panel to image
    	if (sp == null || algebraView == null)
			return euclidianView.getExportImage(euclidianView.getHeight());
  	
    	if (sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
    		// left - right (algebra view - euclidian view)
    		Component left = sp.getLeftComponent(); 
    		int leftWidth = Math.min(left.getWidth(), algebraView.getWidth());
    		int leftHeight = Math.min(left.getHeight(), algebraView.getHeight());	
        	int height = leftHeight;
            double evScale = height / (double) euclidianView.getHeight();
            int width = (int) (leftWidth + euclidianView.getWidth() * evScale + 2);    
            
            // image for center panel
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);       
            Graphics2D g = img.createGraphics();
            g.setColor(Color.black);
            g.fillRect(0,0, width, height);    
            // paint algebra view    
            algebraView.paint(g);
            // leave black line	
            g.translate(leftWidth + 2, 0);  
           
            // paint euclidian view 
            euclidianView.exportPaint(g, evScale);
            g.dispose();
            return img;
    	}
    	else {
    		// top - bottom (euclidian view - algebra view)
    		Component bottom = sp.getRightComponent(); 
    		int bottomWidth = Math.min(euclidianView.getWidth(), algebraView.getWidth());
    		int bottomHeight = Math.min(bottom.getHeight(), algebraView.getHeight());
			int width = bottomWidth;
        	int height = euclidianView.getHeight() + 
						 bottomHeight
						 + 2;  
            
            // image for center panel
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);       
            Graphics2D g = img.createGraphics();
            g.setColor(Color.black);
            g.fillRect(0,0, width, height);    
            // paint euclidian view 
            g.setClip(0,0, width, height);
            euclidianView.exportPaint(g, 1.0);
            g.setClip(0,0,width, height);
            // leave black line 
            g.translate(0, height - bottomHeight); 
            //  paint algebra view    
            algebraView.paint(g);  
            g.dispose();
            return img;
    	}
    }
    
    public AbstractAction getShowAxesAction() {
    	if (showAxesAction == null) {
    		initShowAxesGridActions();
    	}    		
    	return showAxesAction;
    }
    
    public AbstractAction getShowGridAction() {
    	if (showGridAction == null) {
    		initShowAxesGridActions();
    	}  
    	return showGridAction;
    }    
    
    public LowerCaseDictionary getCommandDictionary() {
    	if (commandDict == null)
    		initCommandResources();
        return commandDict;
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
    					showAxes = !optionValue.equals("false");    					
    				}
    				else if (optionName.equals("showGrid")) {    					
    					showGrid = !optionValue.equals("false");    					
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
            if (fileArgument.startsWith("http") || 
            	fileArgument.startsWith("file")) {         
            	
                URL url = new URL(args[0]);                
                success = loadXML(url);              
                updateContentPane();                          
            } else {                       	
                File f = new File(fileArgument);
                f = f.getCanonicalFile();                
                success = loadFile(f);
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
    
    public void setApplet(GeoGebraApplet applet) {
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
    		loadFile(currentFile);   
    	else
    		newFile();
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
   			frame.addWindowListener(guiController);  
           // frame.addComponentListener(guiController);    
    	}
    }

    final public boolean isApplet() {
        return isApplet;
    }

    public GeoGebraApplet getApplet() {
        return applet;
    }
    
    public JFrame getFrame() {
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
    
    public GUIController getGUIController() {
        return guiController;
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
        currentSelectionListener = sl;
        
		if (sl == null) {
			setMode(oldMode);
		} else {			
			if (getMode() != EuclidianView.MODE_ALGEBRA_INPUT)
				oldMode = getMode();			
	        euclidianView.setMode(EuclidianView.MODE_ALGEBRA_INPUT);
		}
    }
    private int oldMode = 0;
    
    public void setAglebraInputMode() {        
        setMode(EuclidianView.MODE_ALGEBRA_INPUT);
    }       

    
    public void setMoveMode() {
    	setMode(EuclidianView.MODE_MOVE);        
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
        euclidianView.zoom(px, py, zoomFactor, true);
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
    public Locale getLocale(String languageCode) {    	
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
    	    
        // update font for new language (needed for e.g. chinese)
        try {
            String fontName = getLanguageFontName(locale);
            if (fontName != FONT_NAME) {
                FONT_NAME = fontName;
                resetFonts();
            }
        } catch (Exception e) {
            showError(e.getMessage());
            locale = currentLocale;
        }
        
        // change rightAngleStyle for German to
        // EuclidianView.RIGHT_ANGLE_STYLE_DOT
        if (euclidianView.getRightAngleStyle() != EuclidianView.RIGHT_ANGLE_STYLE_NONE) {
	        if (locale.getLanguage().equals("de")) {
	        	euclidianView.setRightAngleStyle(EuclidianView.RIGHT_ANGLE_STYLE_DOT);
	        } else {
	        	euclidianView.setRightAngleStyle(EuclidianView.RIGHT_ANGLE_STYLE_SQUARE);
	        }
        }

        // load resource files
        setLocale(locale);
        // make sure to update commands
        if (rbcommand != null)
        	initCommandResources();  
        updateReverseLanguage(locale);
        kernel.updateLocalAxesNames();
        setLabels(); // update display

        //  reinit helpBrowser
        if (helpBrowser != null) {
            try {
                URL helpURL = getHelpURL(currentLocale);
                helpBrowser.setHomePage(helpURL);
            } catch (Exception e) {
                helpBrowser.setVisible(false);
                helpBrowser.dispose();
                helpBrowser = null;
            }   
        }
        System.gc();
    }
    
    private boolean reverseLanguage = false; //FKH 20040822    
    final public boolean isReverseLanguage() { //FKH 20041010
        // for Chinese
        return reverseLanguage;
    }
    
    // for basque you have to say "A point" instead of "point A"
    private boolean reverseNameDescription = false;
    final public boolean isReverseNameDescriptionLanguage() { 
        // for Basque
        return reverseNameDescription;
    }
    
    private void updateReverseLanguage(Locale locale) {
    	String lang = locale.getLanguage();
        reverseLanguage = "zh".equals(lang);
        reverseNameDescription = "eu".equals(lang);
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
        if (plainFont.canDisplayUpTo(chinesesample) == -1)
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
		return MyResourceBundle.loadSingleBundleFile(RB_ALGO2COMMAND, getCodeBase());
    }

    private void updateResourceBundles() {      	
    	if (rbmenu != null)
    		rbmenu = MyResourceBundle.createBundle(RB_MENU, currentLocale, getCodeBase());
        if (rberror != null)
        	rberror = MyResourceBundle.createBundle(RB_ERROR, currentLocale, getCodeBase());
        if (rbplain != null) 
        	rbplain = MyResourceBundle.createBundle(RB_PLAIN, currentLocale, getCodeBase());
      //  if (rbcommand != null || (applet != null && applet.enableJavaScript))
        	initCommandResources();                        
    }
    
    private void initCommandResources() {    	    
    	//System.out.println("init command resources");    	
        rbcommand = MyResourceBundle.createBundle(RB_COMMAND, currentLocale, getCodeBase());    
        
        // build Hashtable for translation of commands from
        // local language to internal name
        // key = local name, value = internal name                  
        if (translateCommandTable == null) 
        	translateCommandTable = new Hashtable();
        
        //  build command dictionary of local command names
        if (commandDict == null) 
        	commandDict = new LowerCaseDictionary();           
                
        translateCommandTable.clear();
        commandDict.clear();
        
        Enumeration e = rbcommand.getKeys();     
        while (e.hasMoreElements()) {            
            String internal = (String) e.nextElement();
            //System.out.println(internal);
            String local = rbcommand.getString((String) internal);
            if (!internal.endsWith("Syntax") && 
            	!internal.equals("Command") ) {
            	// case is ignored in translating local command names to internal names! 
                translateCommandTable.put(local.toLowerCase(), internal);                
                commandDict.addEntry(local);
            }
        }       
    }    

    public Locale getLocale() {
        return currentLocale;
    }

    /**
     * 
     * @return all command names for the current locale
     */
    final public String[] getCommandNames() {  
    	if (rbcommand == null)
    		initCommandResources();
    	    	
    	// get local names from command dictionary
    	Collection coll = commandDict.values();
        String[] ret = new String[coll.size()];
        Iterator it = coll.iterator();
        int i=0;
        while (it.hasNext()) {
            ret[i++] = (String) it.next();
        }       
   
        // sort the name list
        Arrays.sort(ret);
        return ret;                
    }

    final public String getPlain(String key) {
    	if (rbplain == null) {
    		rbplain = MyResourceBundle.createBundle(RB_PLAIN, currentLocale, getCodeBase());
    	}
    	
        try {
            return rbplain.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }
    
    final public String getMenu(String key) {
    	if (rbmenu == null) 
    		rbmenu = MyResourceBundle.createBundle(RB_MENU, currentLocale, getCodeBase());
    	
        try {
            return rbmenu.getString(key);
        } catch (MissingResourceException e) {        	   
        	return key;
        }
    }

    final public String getSetting(String key) {
    	if (rbsettings == null) 
    		rbsettings = MyResourceBundle.loadSingleBundleFile(RB_SETTINGS, getCodeBase());
    	
        try {
            return rbsettings.getString(key);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    final public String getError(String key) {
    	if (rberror == null) 
    		rberror = MyResourceBundle.createBundle(RB_ERROR, currentLocale, getCodeBase());    	
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
        } catch (MissingResourceException e) {
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
            getCommand("Relation"),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void showHelp(String key) {
        String text;
        try {
            text = rbplain.getString(key);
        } catch (MissingResourceException e) {
            text = key;
        }
        JOptionPane.showConfirmDialog(
        		mainComp,
            text,
            getMenu("Help"),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE);
    }

    public void showError(String key) {    	    	
        String text = getError(key);       
        JOptionPane.showConfirmDialog(
        		mainComp,
            text,
            getError("Error"),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE);
    }

    public void showError(MyError e) {    
        JOptionPane.showConfirmDialog(
        		mainComp,
            e.getLocalizedMessage(),
            getError("Error"),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE);
    }
    
    public void showMessage(String message) {               
        JOptionPane.showConfirmDialog(
        		mainComp,
            message,
            null,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    
      public String readTextFromJar(String s) {
        StringBuffer sb = new StringBuffer();
        String thisLine;
        try {
          InputStream is = getClass().getResourceAsStream(s);
          BufferedReader br = new BufferedReader
             (new InputStreamReader(is));
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

    /**
     * Displays the zoom menu at the position p in the coordinate space 
     * of euclidianView
     */
    public void showDrawingPadPopup(Component invoker, Point p) {
        // clear highlighting and selections in views
        euclidianView.resetMode();

        // menu for drawing pane context menu
        DrawingPadPopupMenu popupMenu = new DrawingPadPopupMenu(this, p.x, p.y);
        popupMenu.show(invoker, p.x, p.y);
    }

    /**
     * Displays the popup menu for geo at the position p in the coordinate space 
     * of the component invoker
     */
    public void showPopupMenu(GeoElement geo, Component invoker, Point p) {
    	if (geo == null) return;
    	
        if (kernel.isAxis(geo)) {
        	showDrawingPadPopup(invoker, p);
        	return;
        }
        
        // clear highlighting and selections in views        
        euclidianView.resetMode();
        Point screenPos = invoker.getLocationOnScreen();
        screenPos.translate(p.x, p.y);

        MyPopupMenu popupMenu = new MyPopupMenu(this, geo, screenPos);
        popupMenu.show(invoker, p.x, p.y);
    }    

    /**
       * Displays the porperties dialog for geos
       */
    public void showPropertiesDialog(ArrayList geos) {
        if (!letShowPropertiesDialog()) return;
        
        // save the geos list: it will be cleared by resetMode()
        ArrayList selGeos = null;
        if (geos != null) {
        	tempGeos.clear();
        	tempGeos.addAll(geos);
        	selGeos = tempGeos;
        } 
        
        euclidianView.resetMode();
        initPropertiesDialog();
        setMoveMode();
        propDialog.setVisible(selGeos);
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
      EuclidianPropDialog euclidianViewDialog = 
      	new EuclidianPropDialog(this, euclidianView);      
      euclidianViewDialog.setVisible(true);
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
    public void showRenameDialog(GeoElement geo, boolean storeUndo) {
        InputHandler handler = new RenameInputHandler(geo, storeUndo);
        InputDialog id =
            new InputDialog(
                this,
                getPlain("NewName")
                    + " "
                    + getPlain("for")
                    + " "
                    + geo.getNameDescription(),
                getPlain("Rename"),
                geo.getLabel(),
                false,
                handler);               
        id.setVisible(true);
        id.selectText();        
    }
    
    private class RenameInputHandler implements InputHandler {
        private GeoElement geo;
        private boolean storeUndo;
                 
        private RenameInputHandler(GeoElement geo, boolean storeUndo) {
            this.geo = geo;
            this.storeUndo = storeUndo;
        }
        
        public boolean processInput(String inputValue) {
            if (inputValue == null) return false;
            try {
            	if (!checkName(geo, inputValue)) {
            		 showError("InvalidInput");
            		 return false;
            	}
            	
                String newLabel = algebraController.parseLabel(inputValue);
                
                // is there a geo with this name?
                Construction cons = geo.getConstruction();
                GeoElement existingGeo = cons.lookupLabel(newLabel);
                if (existingGeo != null) {
                	// rename this geo too:
                	String tempLabel = existingGeo.getIndexLabel(newLabel);
                	existingGeo.rename(tempLabel);
                }
                
                if (geo.rename(newLabel) && storeUndo) {
                    storeUndoInfo();                    
                }
                return true;
            } catch (Exception e) {
                showError("InvalidInput");
            } catch (MyError err) {
                showError(err);
            }
            return false;
        }   
                      
        // check if name is valid for geo
        private boolean checkName(GeoElement geo, String name) {
        	if (geo.isGeoFunction()) {
        		for (int i=0; i < invalidFunctionNames.length; i++) {
        			if (invalidFunctionNames[i].equals(name))
        				return false;
        		}
        	}        
        	
        	return true;
        }
    }
    private static String [] invalidFunctionNames = 
    {
    		"gamma", "x", "y", 
			"abs", "sgn", "sqrt", "exp", "log", "ln",
			"cos", "sin", "tan", "acos", "asin", "atan",
			"cosh", "sinh", "tanh", "acosh", "asinh", "atanh",
			"floor", "ceil", "round", "min", "max"
    };
    
    
    /**
       * Displays the redefine dialog for geo
       */
    public void showRedefineDialog(GeoElement geo) {
    	//doBeforeRedefine();    	 
    	
    	if (geo.isGeoText()) {
			showTextDialog((GeoText) geo);
			return;
    	}
    	
    	InputHandler handler = new RedefineInputHandler(geo);                
        StringBuffer initSB = new StringBuffer(geo.isIndependent() ? 
                            geo.toValueString() :
                            geo.getCommandDescription());
                        
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
                                                                                                                          
        InputDialog id =
            new InputDialog(
                this, 
                geo.getNameDescription(),
                getPlain("Redefine"),
                initSB.toString(),
                true,
                handler);                      
        id.setVisible(true);     
        id.selectText();            
    }
    
    private class RedefineInputHandler implements InputHandler {
        private GeoElement geo;
        
        private RedefineInputHandler(GeoElement geo) {
            this.geo = geo;
        }
        
        public boolean processInput(String inputValue) {
            if (inputValue == null) return false;
            try {
            	GeoElement newGeo = algebraController.changeGeoElement(geo, inputValue, true);
            	doAfterRedefine(newGeo);
                return newGeo != null;
            } catch (Exception e) {
                showError("ReplaceFailed");
            } catch (MyError err) {
                showError(err);
            } finally {
            	doAfterRedefine(null);
            }
            return false;
        }   
    }
    
    /**
     * Creates a new slider at given location (screen coords).
     * @return whether a new slider (number) was create or not
     */   
	public boolean showSliderCreationDialog(int x, int y) {
	      SliderDialog dialog = new SliderDialog(this, x, y);
	      dialog.setVisible(true);
	      GeoElement geo = dialog.getResult();
	      return (geo != null);	      	
	}
	
    /**
     * Creates a new image at the given location (real world coords).
     * @return whether a new image was create or not
     */   
	public boolean showImageCreationDialog(GeoPoint loc) {		
		File imageFile = showImageFileChooser();
		if (imageFile == null) return false;				
		
		try {
			setWaitCursor();
			BufferedImage img = ImageIO.read(imageFile);
			setDefaultCursor();
			if (img == null) {
				showError("LoadFileFailed");
				return false;
			}			
			
			// add image to imageManager
			String fileName = imageFile.getCanonicalPath();			
			imageManager.addExternalImage(fileName, img);
			
			// create GeoImage object for this fileName
			GeoImage geoImage = new GeoImage(kernel.getConstruction());					
			geoImage.setFileName(fileName);			
			geoImage.setCorner(loc, 0);	
			geoImage.setLabel(null);
					     	        	        
	        GeoImage.updateInstances();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			showError("LoadFileFailed");
			return false;
		}		
		
	}
	
	public Color showColorChooser(Color currentColor) {
		// there seems to be a bug concerning ToolTips in JColorChooser 
		// so we turn off ToolTips
		ToolTipManager.sharedInstance().setEnabled(false);
		try {
			Color newColor =
				JColorChooser.showDialog(
					null,
					getPlain("ChooseColor"),
					currentColor);
			ToolTipManager.sharedInstance().setEnabled(true);
			return newColor;
		} catch (Exception e) {
			ToolTipManager.sharedInstance().setEnabled(true);
			return null;
		}
	}
	
	private File showImageFileChooser() {
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
        

        File file = null;
        int returnVal = fileChooser.showOpenDialog(mainComp);
        if (returnVal == JFileChooser.APPROVE_OPTION) {             
        	file = fileChooser.getSelectedFile();   
        	if (file != null) currentImagePath = file.getParentFile();
        }       
        return file;
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
        boolean isLaTeX;        
        String initString, descString;
        boolean createText = text == null;     
        //String label = null;
        
        if (createText) {
            //initString = "\"\"";
        	initString = null;
            descString = getPlain("Text");
            isLaTeX = false;
        }           
        else {                                
        	//label = text.getLabel();
          
            initString = text.isIndependent() ? 
                           // "\"" + text.toValueString() + "\"" :
            		 		text.toValueString() :
                            text.getCommandDescription(); 
            descString = text.getNameDescription();
            isLaTeX = text.isLaTeX();
        }           
        
        LaTeXinputHandler handler = new TextInputHandler(text, startPoint);            
        
        InputDialog id =
            new TextInputDialog(
                this,
                descString,
                getPlain("Text"),
                initString, 
                isLaTeX, 
                false,
                handler);       
        id.setVisible(true);          
       // id.setCaretPosition(1);                     
    }
    
    private void doAfterRedefine(GeoElement geo) {
    	// select geoElement with label again
    	 if (propDialog != null && propDialog.isShowing()) {    	 	
         	//propDialog.setViewActive(true);
            propDialog.geoElementSelected(geo, false);          
         }
    }
    
    private class TextInputHandler implements LaTeXinputHandler {
        private GeoText text;
        private GeoPoint startPoint;
        private boolean isLaTeX;
        
        private TextInputHandler(GeoText text, GeoPoint startPoint) {
            this.text = text;
            this.startPoint = startPoint;
        }
        
        public void setLaTeX(boolean flag) {
            isLaTeX = flag;
        }
        
        public boolean processInput(String inputValue) {
            if (inputValue == null) return false;                        
          
            // no quotes?
        	if (inputValue.indexOf('"') < 0) {
            	// this should become either
            	// (1) a + "" where a is an object label or
            	// (2) "text", a plain text 
        	
        		// ad (1) OBJECT LABEL 
        		// add empty string to end to make sure
        		// that this will become a text object
        		if (kernel.lookupLabel(inputValue.trim()) != null) {
        			inputValue = "(" + inputValue + ") + \"\"";
        		} 
        		// ad (2) PLAIN TEXT
        		// add quotes to string
        		else {
        			inputValue = "\"" + inputValue + "\"";
        		}        			
        	} 
        	else {
        	   // replace \n\" by \"\n, this is useful for e.g.:
        	  //    "a = " + a + 
        	  //	"b = " + b 
        		inputValue = inputValue.replaceAll("\n\"", "\"\n");
        	}
            
            if (inputValue.equals("\"\"")) return false;
            
            // create new text
            boolean createText = text == null;
            if (createText) {
                GeoElement [] ret = 
                    algebraController.processAlgebraCommand(inputValue, false);
                if (ret != null && ret[0].isTextValue()) {
                    GeoText t = (GeoText) ret[0];
                    t.setLaTeX(isLaTeX, true);                 
                    
                    if (startPoint.isLabelSet()) {
                    	  try { t.setStartPoint(startPoint); }catch(Exception e){};                          
                    } else {
                    	// startpoint contains mouse coords
                    	t.setAbsoluteScreenLoc(euclidianView.toScreenCoordX(startPoint.inhomX), 
                    			euclidianView.toScreenCoordY(startPoint.inhomY));
                    	t.setAbsoluteScreenLocActive(true);
                    }
                    t.updateRepaint();
                    storeUndoInfo();                    
                    return true;                
                }
                return false;
            }
                    
            // change existing text
            try {           
                text.setLaTeX(isLaTeX, true);
                GeoText newText = (GeoText) algebraController.changeGeoElement(text, inputValue, true);
                doAfterRedefine(newText);
                return newText != null;
			} catch (Exception e) {
                showError("ReplaceFailed");
                return false;
            } catch (MyError err) {
                showError(err);
                return false;
            } 
        }   
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
		InputDialog id = new InputDialog(this,  message, title, initText, false, handler, true);       
        id.setVisible(true); 
        
        cons.setSuppressLabelCreation(oldVal);
        return handler.num;
	}
    
    /**
     * Shows a modal dialog to enter an angle or angle variable name.
     * @return: Object[] with { NumberValue, AngleInputDialog } pair
     */
    public Object [] showAngleInputDialog(String title, String message, String initText) {   
		NumberInputHandler handler = new NumberInputHandler();
		AngleInputDialog id = new AngleInputDialog(this,  message, title, initText, false, handler, true);       
        id.setVisible(true); 
        Object [] ret = {handler.num, id};
        return ret;
	}
    
    private class NumberInputHandler implements InputHandler {
		NumberValue num = null;
		
		public boolean processInput(String inputString) {			
			GeoElement [] result = algebraController.processAlgebraCommand(inputString, false);			
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
    
    private void setCurrentFile(File file) {
        currentFile = file;
        if (currentFile != null)
            currentPath = currentFile.getParentFile();   
        updateTitle();
        updateMenuWindow();
    }

    public void updateTitle() {   
    	if (frame == null) return;
    	
        StringBuffer sb = new StringBuffer();
        sb.append(getPlain("ApplicationName"));        
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
        
        if (modeNameLabel != null) {
            modeNameLabel.setFont(getPlainFont());  
            updateModeLabel();
        }
        
        if (statusLabelAxesRatio != null)
            statusLabelAxesRatio.setFont(getPlainFont());
                   
        if (appToolBarPanel != null && showToolBar)
        	createToolbar();

        if (propDialog != null)
            propDialog.initGUI();
        if (constProtocol != null)
            constProtocol.initGUI();
        if (constProtocolNavigation != null)
        	constProtocolNavigation.initGUI();
        if (helpBrowser != null)
            helpBrowser.updateFonts();
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

        // init actions for toolbar buttons and mode menu items
        if (showToolBar || showMenuBar) initActions();

        if (showToolBar) createToolbar();
        if (showMenuBar) setMenuBar(guiController);
        if (modeNameLabel != null) updateModeLabel();
        if (statusLabelAxesRatio != null) updateStatusLabelAxesRatio();
        if (algebraView != null) algebraView.setLabels(); // update views    
        if (algebraInput != null) algebraInput.setLabels();
        
        
        if (propDialog != null)
            propDialog.initGUI();
        if (constProtocol != null)
            constProtocol.initGUI();
        if (constProtocolNavigation != null)
        	constProtocolNavigation.setLabels();
        if (helpBrowser != null)
            helpBrowser.setLabels();
    }    
    
    public void setCustomToolBar(String toolBarDefinition) {
    	strToolBarDefinition = 	toolBarDefinition;
    }

    public String getCustomToolBar() {
    	return strToolBarDefinition;
    }
        
    
    /**
     * Creates a toolbar using the current strToolBarDefinition. 
     */
    private void createToolbar() {
    	if (appToolBarPanel == null) 
    		return;
    	
        // create toolBars                       
        appToolBarPanel.removeAll();
        appToolBarPanel.setLayout(new BorderLayout(10,5));        
        
        JToolBar tb = new JToolBar();   
        tb.setBackground(appToolBarPanel.getBackground());
        ModeToggleButtonGroup bg = new ModeToggleButtonGroup();     
        moveToggleMenus = new ArrayList();
        
        //JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));           
        tb.setFloatable(false);        
        appToolBarPanel.add(tb, BorderLayout.WEST);             
                  
        if (algebraInput != null)        	
        	bg.add(algebraInput.getInputButton());     
                       
        // add menus with modes to toolbar
       	addCustomModesToToolbar(tb, bg);
       	
       	// mode label
       	modeNameLabel = new JLabel();
       	appToolBarPanel.add(modeNameLabel, BorderLayout.CENTER);  
       	
        // UNDO Toolbar     
        if (undoActive) {
	        // undo part            
	        JPanel undoPanel = new JPanel(new BorderLayout(0,0));        	   
	        	        
	        MySmallJButton button = new MySmallJButton(undoAction, 7); 	
	        String text = getMenu("Undo");
	        button.setText(null);
	        button.setToolTipText(text);                     
	        undoPanel.add(button, BorderLayout.NORTH);
	        
	        button = new MySmallJButton(redoAction, 7);         	        
	        text = getMenu("Redo");
	        button.setText(null);
	        button.setToolTipText(text);        
	        undoPanel.add(button, BorderLayout.SOUTH);   
	        
	        appToolBarPanel.add(undoPanel, BorderLayout.EAST);
        }                   
    }
    
    /**
     * Adds the given modes to a two-dimensional toolbar. 
     * The toolbar definition string looks like "0 , 1 2 | 3 4 5 || 7 8 9"
	 * where the int values are mode numbers, "," adds a separator
	 * within a menu, "|" starts a new menu
	 * and "||" adds a separator before starting a new menu. 
     * @param modes
     * @param tb
     * @param bg
     */    
    private void addCustomModesToToolbar(JToolBar tb, ModeToggleButtonGroup bg) {        	    	
    	Vector toolbarVec = handleCustomToolBar(strToolBarDefinition);
        if (toolbarVec == null) {
            // set default or custom toolbar
        	addDefaultModesToToolbar(tb, bg);
        	return;
        }
        
        boolean firstButton = true;
    	for (int i = 0; i < toolbarVec.size(); i++) {
            ModeToggleMenu tm = new ModeToggleMenu(this, bg);
            moveToggleMenus.add(tm);
            Vector menu = (Vector) toolbarVec.get(i);
            for (int k = 0; k < menu.size(); k++) {
            	// separator
            	int mode = ((Integer) menu.get(k)).intValue();
            	if (mode < 0) {
            		if (k==0) // separator at first position of new menu: toolbar separator 
            			tb.addSeparator();
            		else // separator within menu: 
            			tm.addSeparator();
            	} 
            	else { // standard case: add mode
            		 tm.addMode(mode);
            		 if (firstButton) {
                     	tm.getJToggleButton().setSelected(true);
                     	firstButton = false;
                     }
            	}
            }
                                   
            tb.add(tm);
    	}
    }
    
    /**
	 * Parses a toolbar definition string like "0 , 1 2 | 3 4 5 || 7 8 9"
	 * where the int values are mode numbers, "," adds a separator
	 * within a menu, "|" starts a new menu
	 * and "||" adds a separator before starting a new menu. 
	 * @return toolbar as nested Vector objects with Integers for the modes. Note: separators have negative values.
	 */
	private static Vector handleCustomToolBar(String strToolBar) {
		if (strToolBar == null || strToolBar.length() == 0) 
			return null;
		
		String [] tokens = strToolBar.split(" ");
		Vector toolbar = new Vector();
		Vector menu = new Vector();
		int maxMenuLength = 0;
		
	    for (int i=0; i < tokens.length; i++) {     
	         if (tokens[i].equals("|")) { // start new menu	        	 
	        	 toolbar.add(menu);
	        	 if (menu.size() > maxMenuLength)
	        		 maxMenuLength = menu.size();
	        	 menu = new Vector();
	         }
	         else if (tokens[i].equals("||")) { // start new menu with separator	        	 
	        	 toolbar.add(menu);
	        	 menu = new Vector();
	        	 menu.add(new Integer(-1)); // separator = negative mode
	         }
	         else if (tokens[i].equals(",")) { // separator within menu
	        	 menu.add(new Integer(-1));
	         }
	         else { // add mode to menu
	        	 try  {	
	        		 menu.add(new Integer(Integer.parseInt(tokens[i])));
	        	 }
	     		catch(Exception e) {
	     			e.printStackTrace();
	     			return null;
	     		}
	         }
	    }

	    // add last menu to toolbar
	    if (menu.size() > 0)
	    	toolbar.add(menu);	   
	    return toolbar;				
	}
    
    private void addDefaultModesToToolbar(JToolBar tb, ModeToggleButtonGroup bg) {
    	 // add move mode
        ModeToggleMenu tm = new ModeToggleMenu(this, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_MOVE);
        tm.addMode(EuclidianView.MODE_MOVE_ROTATE);
        tm.getJToggleButton().setSelected(true);        
        tb.add(tm);
        tb.addSeparator();        
        
        // point, intersect 
        tm = new ModeToggleMenu(this, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_POINT);
        tm.addMode(EuclidianView.MODE_INTERSECT);
        tm.addMode(EuclidianView.MODE_MIDPOINT);        
        tb.add(tm);     
                    
        // line, segment, ray, vector
        tm = new ModeToggleMenu(this, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_JOIN);
        tm.addMode(EuclidianView.MODE_SEGMENT);
        tm.addMode(EuclidianView.MODE_SEGMENT_FIXED);   
        tm.addMode(EuclidianView.MODE_RAY);             
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_VECTOR);
        tm.addMode(EuclidianView.MODE_VECTOR_FROM_POINT);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_POLYGON);
        tb.add(tm);                     
                
        // parallel, orthogonal, line bisector, angular bisector, tangents
        tm = new ModeToggleMenu(this, bg);  
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_ORTHOGONAL);
        tm.addMode(EuclidianView.MODE_PARALLEL);        
        tm.addMode(EuclidianView.MODE_LINE_BISECTOR);
        tm.addMode(EuclidianView.MODE_ANGULAR_BISECTOR);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_TANGENTS);
        tm.addMode(EuclidianView.MODE_POLAR_DIAMETER);
        tb.add(tm);
        
        tb.addSeparator();        

        // circle 2, circle 3, conic 5
        tm = new ModeToggleMenu(this, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_CIRCLE_TWO_POINTS);
        tm.addMode(EuclidianView.MODE_CIRCLE_POINT_RADIUS);
        tm.addMode(EuclidianView.MODE_CIRCLE_THREE_POINTS);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_SEMICIRCLE);
        tm.addMode(EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS);
        tm.addMode(EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS);     
        tm.addMode(EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_CONIC_FIVE_POINTS);       
        tb.add(tm);    
        
        // numbers, locus
        tm = new ModeToggleMenu(this, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_ANGLE); 
        tm.addMode(EuclidianView.MODE_ANGLE_FIXED); 
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_DISTANCE);   
        tm.addMode(EuclidianView.MODE_SLIDER);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_LOCUS);  
        tb.add(tm);   
        
        tb.addSeparator();   
        
        // transforms
        tm = new ModeToggleMenu(this, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_MIRROR_AT_POINT);
        tm.addMode(EuclidianView.MODE_MIRROR_AT_LINE);
        tm.addMode(EuclidianView.MODE_ROTATE_BY_ANGLE);
        tm.addMode(EuclidianView.MODE_TRANSLATE_BY_VECTOR);
        tm.addMode(EuclidianView.MODE_DILATE_FROM_POINT);     
        tb.add(tm);
                          
        // text, relation
        tm = new ModeToggleMenu(this, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_TEXT);
        tm.addMode(EuclidianView.MODE_IMAGE);       
        tm.addMode(EuclidianView.MODE_RELATION);        
        tb.add(tm); 
        
        tb.addSeparator();         
        
        // translate view, show/hide modes
        tm = new ModeToggleMenu(this, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_TRANSLATEVIEW);
        tm.addMode(EuclidianView.MODE_ZOOM_IN);
        tm.addMode(EuclidianView.MODE_ZOOM_OUT);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_SHOW_HIDE_OBJECT);
        tm.addMode(EuclidianView.MODE_SHOW_HIDE_LABEL);
        tm.addMode(EuclidianView.MODE_COPY_VISUAL_STYLE);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_DELETE);        
        tb.add(tm);    
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

    public void setShowAlgebraView(boolean flag) {
        if (showAlgebraView != flag) {
            showAlgebraView = flag;
            if (showAlgebraView) {
            	if (algebraView == null) {
            		initAlgebraView();           		
            	}
            	else {
            		algebraView.attachView();    		
            	}
            	algebraView.setShowAuxiliaryObjects(showAuxiliaryObjects);
            }         
            else {
            	if (algebraView != null) algebraView.detachView();
            }                
                
            if (showMenuBar) {            	
	            if (cbShowAuxiliaryObjects != null)
	                cbShowAuxiliaryObjects.setEnabled(showAlgebraView);
	            if (cbHorizontalSplit != null)
	                cbHorizontalSplit.setEnabled(showAlgebraView);
	            if (cbShowAlgebraView != null)
	            	cbShowAlgebraView.setSelected(showAlgebraView);
            }
            isSaved = false;
        }
    }
    
    private void initAlgebraView() {    	
    	algebraView = new AlgebraView(algebraController);
    	algebraView.setShowAuxiliaryObjects(showAuxiliaryObjects);
    	
    		
    }

    public boolean showAlgebraView() {
        return showAlgebraView;
    }        
    
    public boolean showAlgebraInput() {
    	return showAlgebraInput;
    }
    
    public void setShowAlgebraInput(boolean flag) {
    	showAlgebraInput = flag;
    	
    	if (showMenuBar) {  
    		if (cbShowCmdList != null)
	    		cbShowCmdList.setEnabled(showAlgebraInput);
	    	if (cbShowAlgebraInput != null)
	    		cbShowAlgebraInput.setSelected(flag);	    	    	
    	}
    }
    
    public boolean showCmdList() {
        return showCmdList;
    }   
    
    public void setShowCmdList(boolean flag) {
    	showCmdList = flag;
    	    	
    	if (cbShowCmdList != null)
    		cbShowCmdList.setSelected(flag);
    	if (algebraInput != null)
    		algebraInput.initGUI();
    }
    
    /**
     * Displays the construction protocol navigation 
     */
	 public void setShowConstructionProtocolNavigation(boolean flag) {
	  	  if (flag == showConsProtNavigation) return;
	  	  showConsProtNavigation = flag;
	  	  
	  	  if (constProtocolNavigation == null) {
	  	  	constProtocolNavigation = new ConstructionProtocolNavigation(getConstructionProtocol(), true, true);
	  	  }	  	 	  	  	  	  
	  	  
	  	  if (showConsProtNavigation) {
	  	    if (euclidianView != null) euclidianView.resetMode();
	  	  	constProtocolNavigation.register();
	  	  } else {
	  	  	constProtocolNavigation.unregister();
	  	  }
	  	  
	  	if (cbShowConsProtNavigation != null) {
	  		cbShowConsProtNavigation.setSelected(showConsProtNavigation);
	  		cbShowConsProtNavigationPlay.setEnabled(showConsProtNavigation);
			cbShowConsProtNavigationOpenProt.setEnabled(showConsProtNavigation);
	  	}	  	
	  }
	 
	 public boolean showConsProtNavigation() {
	 	return showConsProtNavigation;
	 }
	 
	 public ConstructionProtocolNavigation getConstructionProtocolNavigation(boolean playButton, boolean protButton) { 
	 	if (constProtocolNavigation == null) {
	 		constProtocolNavigation = new ConstructionProtocolNavigation(getConstructionProtocol(), playButton, protButton);
	 	} else {
	 		constProtocolNavigation.setPlayButtonVisible(playButton);
	 		constProtocolNavigation.setConsProtButtonVisible(protButton);
	 		constProtocolNavigation.initGUI();
	 	}
	 	return constProtocolNavigation;
	 }
	 
	 private boolean isConsProtNavigationPlayButtonVisible() {
	 	if (constProtocolNavigation != null)
	 		return constProtocolNavigation.isPlayButtonVisible();
	 	else
	 		return true;
	 }
	 
	 private boolean isConsProtNavigationProtButtonVisible() {
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
		if (cbShowAuxiliaryObjects != null)
			cbShowAuxiliaryObjects.setSelected(flag);
	}
    
    void setShowMenuBar(boolean flag) {
    	showMenuBar = flag;
    }
    
    void setShowToolBar(boolean flag) {
    	showToolBar = flag;
    }
    
    public boolean showToolBar() {
    	return  showToolBar;        
    }
    
    void setUndoActive(boolean flag) {
    	undoActive = flag;
    	kernel.setUndoActive(flag);
    }
    
    public boolean letRename() {
    	return true;
        //return frame != null && frame.isShowing();
    }
    
    public boolean letDelete() {
    	return true;
        //return frame != null && frame.isShowing();
    }
    
    public boolean letRedefine() {
    	return true;
        //return frame != null && frame.isShowing();
    }
    
    public boolean letShowPropertiesDialog() {
        //return !isApplet();
        return true;
    }

    public void updateMenuBar() {
    	if (!showMenuBar) return;
    	
        cbShowAxes.setSelected(euclidianView.getShowAxes());
        cbShowGrid.setSelected(euclidianView.getShowGrid());
        cbShowAlgebraView.setSelected(showAlgebraView);
        cbShowAlgebraInput.setSelected(showAlgebraInput);
        cbShowCmdList.setSelected(showCmdList);
        cbShowAuxiliaryObjects.setSelected(showAuxiliaryObjects);
        
        cbShowConsProtNavigation.setSelected(showConsProtNavigation);
        cbShowConsProtNavigationPlay.setSelected(isConsProtNavigationPlayButtonVisible());
        cbShowConsProtNavigationOpenProt.setSelected(isConsProtNavigationProtButtonVisible());
     
        updateMenuPointCapturing();
        updateMenuAngleUnit();
        updateMenuDecimalPlaces();
        updateMenuPointStyle();
        updateMenuRightAngleStyle();
        updateMenuCoordStyle();
    }
    
    private void updateMenuFile() {
    	if (frame == null || menuFile == null) return;    	    	                      
    	
    	if (miCloseAll == null) {
    		miCloseAll = new JMenuItem(exitAllAction);
    	}
    	    	
    	menuFile.remove(miCloseAll);
        if (GeoGebra.getInstanceCount() > 1) {
        	menuFile.add(miCloseAll);
        }      
   }
    
    public void updateMenusForInstances() {
    	updateMenuWindow();
    	updateMenuFile();
    }
    
    private void updateMenuWindow() {  
    	if (frame == null || menuWindow == null) return;

    	menuWindow.removeAll();
    	menuWindow.add(newWindowAction);
    	
    	ArrayList ggbInstances = GeoGebra.getInstances();
    	int size = ggbInstances.size();
    	if (size == 1) return;
    	
    	menuWindow.addSeparator();
    	StringBuffer sb = new StringBuffer();
    	ButtonGroup bg = new ButtonGroup();  
    	JRadioButtonMenuItem mi;  
    	for (int i = 0; i < size; i++) {    		    		
    		GeoGebra ggb = (GeoGebra) ggbInstances.get(i);
    		Application app = ggb.getApplication();
    		
    		sb.setLength(0);
    		sb.append(i+1);
    		if (app.currentFile != null) {
    			sb.append(" ");
    			sb.append(app.currentFile.getName());
    		}
    		
    		 mi = new JRadioButtonMenuItem(sb.toString());
             if (app == this)
                 mi.setSelected(true);
             ActionListener al = new RequestFocusListener(ggb);
             mi.addActionListener(al);
             bg.add(mi);
             menuWindow.add(mi);
    	}       
    }
   
    private void setMenuBar(ActionListener al) {    
        JMenu menu, submenu;
        JMenuItem mi;
        int pos;
        if (menuBar == null) 
        	menuBar = new JMenuBar();
        else
        	menuBar.removeAll();

        // File          
        menuFile = new JMenu(getMenu("File"));
        menu = menuFile;    	    	
    	mi = menu.add(newFileAction);
        setCtrlAccelerator(mi, 'N');
        mi = new JMenuItem(newWindowAction);
        mi.setIcon(getEmptyIcon());
        menu.add(mi);
        menu.addSeparator();
        mi = menu.add(loadAction);
        setCtrlAccelerator(mi, 'O'); // open            
        menu.addSeparator();
        mi = menu.add(saveAction);
        setCtrlAccelerator(mi, 'S');
        mi = menu.add(saveAsAction);
        menu.addSeparator();
        submenu = new JMenu(getMenu("PrintPreview"));
        submenu.setIcon(getImageIcon("print.gif"));
        submenu.add(printEuclidianViewAction);
        submenu.add(printProtocolAction);
        menu.add(submenu);
        submenu = new JMenu(getMenu("Export"));
        submenu.setIcon(getEmptyIcon());
        menu.add(submenu);
        submenu.add(exportWorksheet);
        submenu.addSeparator();
        submenu.add(htmlCPAction);
        submenu.add(exportGraphicAction);

        
        submenu.addSeparator();
        submenu.add(drawingPadToClipboardAction);        
        menu.addSeparator(); 
        menu.add(exitAction);        
        updateMenuFile();
        menuBar.add(menuFile);              

        // Edit
        menu = new JMenu(getMenu("Edit"));
        if (undoActive) {
	        mi = menu.add(undoAction);
	        setCtrlAccelerator(mi, 'Z');
	        mi = menu.add(redoAction);
	        setCtrlAccelerator(mi, 'Y');
	        menu.addSeparator();
        }
        
        menu.add(propertiesAction);
        menuBar.add(menu);
      

        // View
        menu = new JMenu(getMenu("View"));
        cbShowAxes = new JCheckBoxMenuItem(showAxesAction);
        cbShowAxes.setSelected(euclidianView.getShowAxes());
        menu.add(cbShowAxes);

        cbShowGrid = new JCheckBoxMenuItem(showGridAction);
        cbShowGrid.setSelected(euclidianView.getShowGrid());
        menu.add(cbShowGrid);
        menu.addSeparator();
        
        mi = menu.add(refreshAction);
        setCtrlAccelerator(mi, 'F');                
        menu.addSeparator();
        
        
        cbShowAlgebraView = new JCheckBoxMenuItem(showAlgebraViewAction);
        cbShowAlgebraView.setSelected(showAlgebraView);
        setCtrlAccelerator(cbShowAlgebraView, 'A');
        menu.add(cbShowAlgebraView);
        
        cbShowAuxiliaryObjects = new JCheckBoxMenuItem(showAuxiliaryObjectsAction);
        cbShowAuxiliaryObjects.setSelected(algebraView == null ||
        							algebraView.showAuxiliaryObjects());
        cbShowAuxiliaryObjects.setEnabled(showAlgebraView);
        menu.add(cbShowAuxiliaryObjects);
        
        cbHorizontalSplit = new JCheckBoxMenuItem(horizontalSplitAction);
        cbHorizontalSplit.setSelected(horizontalSplit);     
        cbHorizontalSplit.setEnabled(showAlgebraView);
        menu.add(cbHorizontalSplit);

        menu.addSeparator();
        

        // show/hide cmdlist, algebra input               
        cbShowAlgebraInput = new JCheckBoxMenuItem(showAlgebraInputAction);
        cbShowAlgebraInput.setSelected(showAlgebraInput);       
        menu.add(cbShowAlgebraInput);
        
        cbShowCmdList = new JCheckBoxMenuItem(showCmdListAction);
        cbShowCmdList.setSelected(showCmdList);  
    	cbShowCmdList.setEnabled(showAlgebraInput);
    
        menu.add(cbShowCmdList);
        menu.addSeparator();
        
        
        // Construction Protocol     
        cbShowConsProtNavigation = new JCheckBoxMenuItem(showConsProtNavigationAction);
        cbShowConsProtNavigationPlay = new JCheckBoxMenuItem(showConsProtNavigationPlayAction);
        cbShowConsProtNavigationOpenProt = new JCheckBoxMenuItem(showConsProtNavigationOpenProtAction);
     
        cbShowConsProtNavigation.setSelected(showConsProtNavigation);
        cbShowConsProtNavigationPlay.setSelected(isConsProtNavigationPlayButtonVisible());
        cbShowConsProtNavigationOpenProt.setSelected(isConsProtNavigationProtButtonVisible());
  		cbShowConsProtNavigationPlay.setEnabled(showConsProtNavigation);
		cbShowConsProtNavigationOpenProt.setEnabled(showConsProtNavigation);

        
        menu.add(cbShowConsProtNavigation);   
        menu.add(cbShowConsProtNavigationPlay);
        menu.add(cbShowConsProtNavigationOpenProt);
                  
        menu.addSeparator();
        menu.add(constProtocolAction);
        menuBar.add(menu);

        // Options
        menu = new JMenu(getMenu("Options"));
        
        // point capturing
        menuPointCapturing = new JMenu(getMenu("PointCapturing"));
        String[] strPointCapturing = { "on",         	
        		getMenu("on") + " (" + getMenu("Grid") + ")", 
        		"off" };   
        String[] strPointCapturingAC = { "1 PointCapturing", "2 PointCapturing",  "0 PointCapturing" };
        addRadioButtonMenuItems(menuPointCapturing, al, strPointCapturing, strPointCapturingAC, 0);     
        menu.add(menuPointCapturing);
        updateMenuPointCapturing();
                
        // Angle unit
        menuAngleUnit = new JMenu(getMenu("AngleUnit"));
        String[] strAngleUnit = { "Degree", "Radiant" };
        addRadioButtonMenuItems(menuAngleUnit, al, strAngleUnit, strAngleUnit, 0);
        menu.add(menuAngleUnit);
        updateMenuAngleUnit();
        
        // decimal places
        menuDecimalPlaces = new JMenu(getMenu("DecimalPlaces"));
        String[] strDecimalSpaces = { "0", "1", "2", "3", "4", "5" };           
        String[] strDecimalSpacesAC = { "0 decimals", "1 decimals", "2 decimals", 
                                                            "3 decimals", "4 decimals", "5 decimals" };
        addRadioButtonMenuItems(menuDecimalPlaces, al, strDecimalSpaces, strDecimalSpacesAC, 0);                
        menu.add(menuDecimalPlaces);
        updateMenuDecimalPlaces();
        
        menu.addSeparator();
        
        // point style
        menuPointStyle = new JMenu(getMenu("PointStyle"));
        // dot, circle, cross
        String[] strPointStyle = { "\u25cf", "\u25cb", "\u2716" };
        String[] strPointStyleAC = { "0", "2", "1" };
        ActionListener psal = new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
        		int style = Integer.parseInt(ae.getActionCommand());
        		euclidianView.setPointStyle(style);
        	}
        };
        addRadioButtonMenuItems(menuPointStyle, psal, strPointStyle, strPointStyleAC, 0);
        menu.add(menuPointStyle);
        updateMenuPointStyle();
        
        
        // added by Loïc BEGIN
        // right angle style

        menuRightAngleStyle = new JMenu(getMenu("RightAngleStyle"));
        // dot, none, square
        String[] strAngleStyle = { getPlain("off"), "\u25a1", "\u2219" };
        String[] strAngleStyleAC = { String.valueOf(EuclidianView.RIGHT_ANGLE_STYLE_NONE),
        		String.valueOf(EuclidianView.RIGHT_ANGLE_STYLE_SQUARE),
        		String.valueOf(EuclidianView.RIGHT_ANGLE_STYLE_DOT)};
        ActionListener asal = new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
        		int style = Integer.parseInt(ae.getActionCommand());
        		euclidianView.setRightAngleStyle(style);
        	}
        };
        addRadioButtonMenuItems(menuRightAngleStyle, asal, strAngleStyle, strAngleStyleAC, 0);
        menu.add(menuRightAngleStyle);
        updateMenuRightAngleStyle();
     //END
        
        // coordinate style
        menuCoordStyle = new JMenu(getPlain("Coordinates"));
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
        addRadioButtonMenuItems(menuCoordStyle, csal, strCoordStyle, strCoordStyleAC, 0);
        menu.add(menuCoordStyle);
        updateMenuCoordStyle();
        
        menu.addSeparator();

        // Graphics quality         
        submenu = new JMenu(getMenu("GraphicsQuality"));
        String[] gqfi = { "LowQuality", "HighQuality" };
        if (euclidianView.getAntialiasing())
            pos = 1;
        else
            pos = 0;
        addRadioButtonMenuItems(submenu, al, gqfi, gqfi, pos);
        menu.add(submenu);

        // Font size         
        submenu = new JMenu(getMenu("FontSize"));
        String[] fsfi =
            {                           
                "12 pt",               
                "14 pt",
                "16 pt",
                "18 pt",
				"20 pt", 
				"24 pt" ,				
				"28 pt", 
				"32 pt" };
        
        // find current pos
        String strFS = appFontSize + " pt";
        pos = 0;
        for (int i=0; i < fsfi.length; i++) {
        	if (strFS.equals(fsfi[i])) {
        		pos = i;
        		break;
        	}
        }               
        
        addRadioButtonMenuItems(submenu, al, fsfi, fsfi, pos);
        menu.add(submenu);
        
        /*
        // FontName
        menuFontName = new JMenu(getMenu("PointCapturing"));
        String[] strFontName = { "Sans Serif", "Serif" };           
        String[] strFontNameAC = { "SansSerif", "Serif" };
        addRadioButtonMenuItems(menuFontName, al, strFontName, strFontNameAC, 0);     
        menu.add(menuFontName);
        updateMenuFontName();
        */

        menu.addSeparator();

        // Language       
    	if (langListener == null)
    		langListener = new LanguageActionListener();
        submenu = new JMenu(getMenu("Language"));
        addLanguageMenuItems(submenu, langListener);
        menu.add(submenu);
        
        menu.addSeparator();
        // drawing pad properteis
        menu.add(drawingPadPropAction);
        
        menuBar.add(menu);
        
        // windows menu
        menuWindow = new JMenu(getMenu("Window"));
        updateMenuWindow();
        menuBar.add(menuWindow);

        // help
        menu = new JMenu(getMenu("Help"));
        menu.add(helpAction);
        menu.addSeparator();
        
      //  menu.add(updateAction);        
      //  menu.addSeparator();
                
        menu.add(infoAction);
        menuBar.add(menu);

        if (isApplet) 
        	applet.setJMenuBar(menuBar);
        else 
        	frame.setJMenuBar(menuBar);
    }

    private void updateMenuAngleUnit() {
        int pos;
        if (kernel.getAngleUnit() == Kernel.ANGLE_DEGREE)
            pos = 0;
        else
            pos = 1;
        (
            (JRadioButtonMenuItem) menuAngleUnit.getMenuComponent(
                pos)).setSelected(
            true);
    }
    
    private void updateMenuPointStyle() {
        int pos = euclidianView.getPointStyle();        
        ( (JRadioButtonMenuItem) menuPointStyle.getMenuComponent(
                pos)).setSelected(
            true);
    }
    // added by Loïc BEGIN
    private void updateMenuRightAngleStyle() {
        int pos = euclidianView.getRightAngleStyle();        
        ( (JRadioButtonMenuItem) menuRightAngleStyle.getMenuComponent(
                pos)).setSelected(
            true);
    }
    //END
    
    private void updateMenuCoordStyle() {
        int pos = kernel.getCoordStyle();        
        ( (JRadioButtonMenuItem) menuCoordStyle.getMenuComponent(
                pos)).setSelected(
            true);
    }
    
    private void updateMenuPointCapturing() {
    	String pos = Integer.toString(euclidianView.getPointCapturingMode());
    	for (int i=0; i < 3; i++) {
    		JRadioButtonMenuItem mi = (JRadioButtonMenuItem) menuPointCapturing.getMenuComponent(i);    		
    		String ac = mi.getActionCommand();    	    	
    		if (ac.substring(0,1).equals(pos)) {
    			mi.setSelected(true);
    			break;
    		}
    	}      
    }
    
    private void updateMenuDecimalPlaces() {
        int pos = kernel.getPrintDecimals();
        try {
        ((JRadioButtonMenuItem) menuDecimalPlaces.
        		getMenuComponent(
                pos)).setSelected(
            true);
        } catch (Exception e) {        	
        }
    }

    private void setCtrlAccelerator(JMenuItem mi, char acc) {
        KeyStroke ks = KeyStroke.getKeyStroke(acc, Event.CTRL_MASK);
        mi.setAccelerator(ks);
    }

    /*
    private void addMenuItems(JMenu menu, ActionListener al, String[] items) {
        JMenuItem mi;
        String label;

        for (int i = 0; i < items.length; i++) {
            if (items[i].equalsIgnoreCase("SEPARATOR")) {
                menu.addSeparator();
            } else {
                mi = new JMenuItem(getMenu(items[i]));
                mi.addActionListener(al);
                menu.add(mi);
            }
        }
    }*/

    private void addLanguageMenuItems(JMenu menu, ActionListener al) {
        JRadioButtonMenuItem mi;
        ButtonGroup bg = new ButtonGroup();
        //String label;
        String ggbLangCode;

        for (int i = 0; i < supportedLocales.size(); i++) {
            Locale loc = (Locale) supportedLocales.get(i);
            ggbLangCode = loc.getLanguage() + loc.getCountry() + loc.getVariant();
            
            // enforce to show specialLanguageNames first 
            // because here getDisplayLanguage doesn't return a good result
            String text = (String) specialLanguageNames.get(ggbLangCode);
            if (text == null)
            	text = loc.getDisplayLanguage(Locale.ENGLISH);            
            mi = new JRadioButtonMenuItem(text);           
            
            if (loc == currentLocale)
                mi.setSelected(true);
            mi.setActionCommand(ggbLangCode);
            mi.addActionListener(al);
            bg.add(mi);
            menu.add(mi);
        }
    }

    private void addRadioButtonMenuItems(
        JMenu menu,
        ActionListener al,
        String[] items, String [] actionCommands,
        int selectedPos) {
        JRadioButtonMenuItem mi;
        ButtonGroup bg = new ButtonGroup();
        //String label;

        for (int i = 0; i < items.length; i++) {
            mi = new JRadioButtonMenuItem(getMenu(items[i]));
            if (i == selectedPos)
                mi.setSelected(true);
            mi.setActionCommand(actionCommands[i]);
            mi.addActionListener(al);
            bg.add(mi);
            menu.add(mi);
        }
    }
    
    private void initShowAxesGridActions() {
    	showAxesAction = new AbstractAction(getMenu("Axes")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
                // toggle axes
            	euclidianView.showAxes(!euclidianView.getShowAxes());
                euclidianView.repaint();
                storeUndoInfo();
                cbShowAxes.setSelected(euclidianView.getShowAxes());
            }
        };

        showGridAction = new AbstractAction(getMenu("Grid")) {
        	private static final long serialVersionUID = 1L;
        	
            public void actionPerformed(ActionEvent e) {
                    // toggle grid
                euclidianView.showGrid(!euclidianView.getShowGrid());
                euclidianView.repaint();
                storeUndoInfo();
                cbShowGrid.setSelected(euclidianView.getShowGrid());
            }
        };
    }

    private void initActions() {
    	actionsInited = true;    	    

        initShowAxesGridActions();
      
        showAlgebraViewAction = new AbstractAction(getPlain("AlgebraWindow")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                setShowAlgebraView(!showAlgebraView);               
                updateCenterPanel(true);             
            }
        };
        
        showAlgebraInputAction = new AbstractAction(getMenu("InputField")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                setShowAlgebraInput(!showAlgebraInput); 
                updateContentPane();                    
            }
        };
        
        showCmdListAction = new AbstractAction(getMenu("CmdList")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                setShowCmdList(!showCmdList);   
                if (algebraInput != null)
                	SwingUtilities.updateComponentTreeUI(algebraInput);
            }
        };
        
        horizontalSplitAction = new AbstractAction(getPlain("HorizontalSplit")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                setHorizontalSplit(!horizontalSplit);               
                updateCenterPanel(true);                
            }
        };
        
        showAuxiliaryObjectsAction = new AbstractAction(getPlain("AuxiliaryObjects")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                setShowAuxiliaryObjects(!showAuxiliaryObjects);               
                isSaved = false;
            }
        };              
        
        showConsProtNavigationAction = new AbstractAction(
        		getPlain("ConstructionProtocolNavigation")
				) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                setShowConstructionProtocolNavigation(!showConsProtNavigation);               
                isSaved = false;
                updateCenterPanel(true);
            }
        };      
        
	    showConsProtNavigationPlayAction = new AbstractAction(
		        		getPlain("PlayButton")
				) {
	    	private static final long serialVersionUID = 1L;
		    public void actionPerformed(ActionEvent e) {
		        constProtocolNavigation.setPlayButtonVisible(!constProtocolNavigation.isPlayButtonVisible());
		        constProtocolNavigation.initGUI();
		        SwingUtilities.updateComponentTreeUI(constProtocolNavigation);
		        isSaved = false;		       
		    }
		};              	
        	
        showConsProtNavigationOpenProtAction = new AbstractAction(
		        		getPlain("ConstructionProtocolButton")
				) {
        	private static final long serialVersionUID = 1L;
		    public void actionPerformed(ActionEvent e) {
		        constProtocolNavigation.setConsProtButtonVisible(!constProtocolNavigation.isConsProtButtonVisible());
		        constProtocolNavigation.initGUI();
		        SwingUtilities.updateComponentTreeUI(constProtocolNavigation);
		        isSaved = false;		       
		    }
		};      

        newFileAction =
            new AbstractAction(getMenu("New"), getImageIcon("new.gif")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                newFile();
            }
        };
        
        newWindowAction =
            new AbstractAction(getMenu("NewWindow") + " ...") {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                newWindow(null);
            }
        };

        propertiesAction =
            new AbstractAction(
                getPlain("Properties") + " ...",
                getEmptyIcon()) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                showPropertiesDialog();
            }
        };

        constProtocolAction =
            new AbstractAction(
            		getPlain("ConstructionProtocol") +  " ..."  ,
					getImageIcon("properties.gif")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                Thread runner = new Thread() {
                    public void run() {
                        showConstructionProtocol();
                    }
                };
                runner.start();
            }
        };
        
        drawingPadPropAction =  
            new AbstractAction(
                getPlain("DrawingPad") + " ...") {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                showDrawingPadPropertiesDialog();
            }
        };


        saveAction =
            new AbstractAction(getMenu("Save"), getImageIcon("save.gif")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                	save();
            }
        };

        saveAsAction =
            new AbstractAction(
                getMenu("SaveAs") + " ...",
                getImageIcon("saveas.gif")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                saveAs();
            }
        };

        printProtocolAction =
            new AbstractAction(getPlain("ConstructionProtocol") + " ...") {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                Thread runner = new Thread() {
                    public void run() {
                        if (constProtocol == null) {
                            constProtocol =
                                new ConstructionProtocol(Application.this);
                        }
                        constProtocol.initProtocol();
                        new PrintPreview(
                            Application.this,
                            constProtocol,
                            PageFormat.PORTRAIT);
                    }
                };
                runner.start();
            }
        };

        printEuclidianViewAction =
            new AbstractAction(getPlain("DrawingPad") + " ...") {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                Thread runner = new Thread() {
                    public void run() {
                    	mainComp.setCursor(
                            Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        new PrintPreview(
                            Application.this,
                            euclidianView,
                            PageFormat.LANDSCAPE);
                        mainComp.setCursor(Cursor.getDefaultCursor());
                    }
                };
                runner.start();
            }
        };

        exitAction = new AbstractAction(getMenu("Close"), getEmptyIcon()) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        };
        
        exitAllAction = new AbstractAction(getMenu("CloseAll"), getEmptyIcon()) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                exitAll();
            }
        };

        loadAction =
            new AbstractAction(
                getMenu("Load") + " ...",
                getImageIcon("open.gif")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
            	load();            	      	               
            }
        };

        undoAction =
            new AbstractAction(getMenu("Undo"), getImageIcon("undo.gif")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                kernel.undo();
                updateActions();                
                updateMenuBar();
                System.gc();
            }
        };

        redoAction =
            new AbstractAction(getMenu("Redo"), getImageIcon("redo.gif")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                kernel.redo();
                updateActions();
                updateMenuBar();
                System.gc();
            }
        };
        
        refreshAction =
            new AbstractAction(getMenu("Refresh")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
            	refreshViews();                
            }
        };
                
        drawingPadToClipboardAction =
            new AbstractAction(getMenu("DrawingPadToClipboard")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                Thread runner = new Thread() {
                    public void run() {
                        // copy drawing pad to the system clipboard
                        Toolkit tools = Toolkit.getDefaultToolkit();
                        Clipboard clip = tools.getSystemClipboard();
                        clip.setContents(euclidianView, euclidianView);
                    }
                };
                runner.start();
            }
        };

        helpAction =
            new AbstractAction(getMenu("Help"), getImageIcon("help.gif")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                Thread runner = new Thread() {
                    public void run() {
                        openHelp();
                    }
                };
                runner.start();
            }
        };
        
        /*
        updateAction =
            new AbstractAction(getMenu("Update"), getEmptyIcon()) {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                Thread runner = new Thread() {
                    public void run() {
                        updateGeoGebra();
                    }
                };
                runner.start();
            }
        };*/       

        exportGraphicAction =
            new AbstractAction(
                getPlain("DrawingPad")
                    + " "
                    + getPlain("as")
                    + " "
                    + getPlain("Picture")
                    + " ("
                    + FILE_EXT_PNG
                    + ", "
                    + FILE_EXT_EPS
                    + ") ...") {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                Thread runner = new Thread() {
                    public void run() {
                        exportGraphic();
                    }
                };
                runner.start();
            }
        };
               


        htmlCPAction =
            new AbstractAction(
                getPlain("ConstructionProtocol")
                    + " "
                    + getPlain("as")
                    + " "
                    + getPlain("html")
                    + " ("
                    + FILE_EXT_HTML
                    + ") ...") {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                Thread runner = new Thread() {
                    public void run() {
                        exportConstructionProtocolHTML();
                    }
                };
                runner.start();
            }
        };

        exportWorksheet =
            new AbstractAction(
                getPlain("DynamicWorksheet")
                    + " "
                    + getPlain("as")
                    + " "
                    + getPlain("html")
                    + " ("
                    + FILE_EXT_HTML
                    + ") ...") {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                Thread runner = new Thread() {
                    public void run() {
                        exportDynamicWorksheetHTML();
                    }
                };
                runner.start();
            }
        };

        infoAction =
            new AbstractAction(getMenu("About") + " / " + getMenu("License"), getImageIcon("info.gif")) {
        	private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                StringBuffer sb = new StringBuffer();
                sb.append("<html><b>");
                sb.append(getPlain("ApplicationName"));
                sb.append(" ");
                sb.append(Application.versionString);
                sb.append("</b><br>");
                sb.append(Application.buildDate);                             
                
                // license                 
                String text = readTextFromJar("gui/license_message.txt");                  
                JTextArea textArea = new JTextArea(21, 43);
                JScrollPane scrollPane = 
                    new JScrollPane(textArea,
                                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                textArea.setEditable(false);
                textArea.setText(text);
                textArea.setCaretPosition(0);
                
                JPanel panel = new JPanel(new BorderLayout(5,5));
                panel.add(new JLabel(sb.toString()), BorderLayout.NORTH);
                panel.add(scrollPane, BorderLayout.SOUTH);
                
                JOptionPane infoPane = new JOptionPane( panel,
                		JOptionPane.PLAIN_MESSAGE,
                		JOptionPane.DEFAULT_OPTION);
                                             
                final JDialog dialog = infoPane.createDialog(mainComp, getMenu("About") + " / " + getMenu("License"));   
                
                final ActionListener listener = new ActionListener() {
                    public final void actionPerformed(final ActionEvent e) {                    	
                			JOptionPane.showMessageDialog(dialog,
                                    null,
                                    "GeoZebra forever",
                                    JOptionPane.DEFAULT_OPTION,
                                    getImageIcon("zebra.gif"));                		
                    }
                };
                
                final KeyStroke keyStroke =
                    KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0, true);
                dialog.getRootPane().registerKeyboardAction(listener, keyStroke,
                        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);                            
                
                dialog.setVisible(true);
            }
        };

        updateActions();
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

    private void openHelp() {
        try {
            URL helpURL = getHelpURL(currentLocale);
            
            if (applet != null) {
            	applet.getAppletContext().showDocument(helpURL, "_blank");
            	return;
            }

            if (helpBrowser == null) {
                // init helpBrowser
                helpBrowser = new HelpBrowser(this);
                Dimension screenSize = frame.getToolkit().getScreenSize();
                int width = 600;
                int height = 600;
                helpBrowser.setBounds(
                    (screenSize.width - width) / 2,
                    (screenSize.height - height) / 2,
                    width,
                    height);
                helpBrowser.setIconImage(frame.getIconImage());
                helpBrowser.setHomePage(helpURL);
            }
            helpBrowser.setVisible(true);
            helpBrowser.requestFocus();

        } catch (MyError e) {
            helpBrowser = null;
            showError(e);
        } catch (Exception e) {
            helpBrowser = null;
            System.err.println(
                "openHelp error: " + e.toString() + e.getMessage());
            showError(e.toString());
        }
    }

    // like openHelp for external use
    public void openHelpBrowser() {
        openHelp();
    }

    private URL getHelpURL(Locale locale) throws Exception {
        String  language = locale.getLanguage();

        // try to get help for current language
        URL helpURL = getHelpURL(language);
        if (helpURL != null) return helpURL;
                
        // for Catalan and Basque we take the 
        // Spanish help instead of the English one
        if (language.equals("eu") || language.equals("ca")) {        	
        	helpURL = getHelpURL("es"); // Spanish
            if (helpURL != null) return helpURL;
        }
                
        // last attempt: try to get English help 
        helpURL = getHelpURL("en");
        if (helpURL != null) return helpURL;
        
        // sorry, no help available
        throw new Exception("HelpNotFound");
    }
    
    private URL getHelpURL(String languageISOcode)  {
    	// try to get help for given language
        String strFile = "docu" + languageISOcode + "/index.html";
        String strURL = GEOGEBRA_WEBSITE + "/help/" + strFile;
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

    private boolean save() {
    	if (propDialog != null && propDialog.isShowing()) 
    		propDialog.cancel();
    	
        if (currentFile != null)
			return saveGeoGebraFile(currentFile);
		else
			return saveAs();
    }

    private boolean saveAs() {
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
                file =
                    addExtension(fileChooser.getSelectedFile(), fileExtension);
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
			return fileName.substring(dotPos+1); 
   }

    private void load() {    	
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
	        fileFilter.setDescription(
	            getPlain("ApplicationName") + " " + getMenu("Files"));
	        fileChooser.resetChoosableFileFilters();
	        fileChooser.setFileFilter(fileFilter);
	
	        int returnVal = fileChooser.showOpenDialog(mainComp);		        
	        
	        File [] files = null;
	        if (returnVal == JFileChooser.APPROVE_OPTION) {             
	            files = fileChooser.getSelectedFiles();  	               	            
	        }	  
	        
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
	        			GeoGebra inst = GeoGebra.getInstanceWithFile(file);
	        			if (inst == null) {
		        			counter++;
		        			if (counter == 1) {
		        				// open first file in current window		        				
								loadFile(file); 								
		        			} else {		        				
		        				// create new window for 
		        				String [] args = { file.getAbsolutePath() };
		        				newWindow(args);			        			
		        			}	 	   
	        			} else if (counter == 0){
	        				// there is an instance with this file opened
	        				inst.requestFocus();
	        			}
	        		}			       
	        	}	        	        	
	        }	      
	        setDefaultCursor();
	       
	        if (currentFile == null) {	        	
	        	setCurrentFile(oldCurrentFile);
	        }	        	
	        fileChooser.setMultiSelectionEnabled(false);
        }
    }

    public boolean loadFile(final File file) {
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
   
        boolean success;
        
        success = loadXML(file);        
        updateContentPane();       
		return success;
        
        /*
	    final SwingWorker worker = new SwingWorker() {
	    	
	        public Object construct() {
	        	 setWaitCursor();	        	 
	        	 Boolean ret = new Boolean(loadXML(file));
	        	
	             return ret;
	        }
	
	        //Runs on the event-dispatching thread.
	        public void finished() {
	        	 updateContentPane();
	             setMoveMode();      
	     		setDefaultCursor();  
	        }
	    };
	    worker.start(); 
	    return ((Boolean)worker.get()).booleanValue();
	    */				

    }

    private void newFile() {
        if (isSaved() || saveCurrentFile()) {
            clearAll();
            setCurrentFile(null);   
            updateMenuBar();
        }
    }
    
    private void newWindow(String [] args) {
    	GeoGebra wnd = GeoGebra.createNewWindow(args);    	
    	updateMenusForInstances();
    	
    	Application app = wnd.getApplication();
    	if (app.getLocale() != getLocale()) {
    		app.setLanguage(getLocale());
    	}
    	if (app.currentPath == null)
    		app.currentPath = currentPath;
    	if (app.currentImagePath == null)
    		app.currentImagePath = currentImagePath;
    	if (app.getFontSize() != getFontSize())
    		app.setFontSize(getFontSize());
    }

    public void exit() {
    	// glassPane is active: don't exit now!
    	if (glassPaneListener != null) return;
    	
        if (isSaved() || applet != null || saveCurrentFile()) {        	
            if (applet != null) {            	
                applet.showApplet();                
            } else {
            	frame.dispose();
            	
            	// last frame closed?
            	if (GeoGebra.getInstanceCount() == 0)
            		System.exit(0);
            	else
            		GeoGebra.updateAllTitles();
            }
        }
    }
    
    public void exitAll() {
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
    	
        int returnVal =
            JOptionPane.showConfirmDialog(
                mainComp,
                getMenu("SaveCurrentFileQuestion"),
                getPlain("Question"),
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

    public void updateModeLabel() {
    	if (modeNameLabel == null) return;
 
    	String modeText = EuclidianView.getModeText(euclidianView.getMode());
        StringBuffer sb = new StringBuffer();
        sb.append("<html><b>");
        sb.append(getMenu(modeText));
        sb.append("</b><br>");
        sb.append(getMenu(modeText + ".Help"));
        sb.append("</html>");
    	modeNameLabel.setText(sb.toString());
    }
    
    public void updateStatusLabelAxesRatio() {
    	if (statusLabelAxesRatio != null)   
    		statusLabelAxesRatio.setText(
    				euclidianView.getXYscaleRatioString());
    }
    
    public void setMode(int mode) {      		    	
        euclidianView.setMode(mode);
        
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
        if (moveToggleMenus != null && mode != EuclidianView.MODE_ALGEBRA_INPUT) {
        	for (int i=0; i < moveToggleMenus.size(); i++) {
        		ModeToggleMenu mtm = (ModeToggleMenu) moveToggleMenus.get(i);
        		if (mtm.selectMode(mode)) break;
        	}
        }                            
        updateModeLabel();
            	
    	// if the properties dialog is showing, move mode is a selection mode
        // for the properties dialog
    	if (mode == EuclidianView.MODE_MOVE &&
    			propDialog != null && propDialog.isShowing()) {    		
    		setSelectionListenerMode(propDialog);	
    	}
    }
    
    public int getMode() {
        return euclidianView.getMode();
    }
    
   
    private void updateActions() {
    	if (!actionsInited) return;
		undoAction.setEnabled(kernel.undoPossible());
		redoAction.setEnabled(kernel.redoPossible());
        propertiesAction.setEnabled(!kernel.isEmpty());
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
         *  Exports construction protocol as html 
         */
    final public void exportDynamicWorksheetHTML() {
        WorksheetExportDialog d = new WorksheetExportDialog(this);
        d.setVisible(true);
    }

    /**
         *  Exports drawing pad as graphic
         */
    final public void exportGraphic() {
    	clearSelectedGeos();
        GraphicExportDialog d = new GraphicExportDialog(this);
        d.setVisible(true);
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
            showError("SaveFileFailed");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads all objects. 
     * @return true if successful
     */
    final public boolean loadXML(File file) {
        try {
        	boolean success = loadXML(file.toURL());
        	if (success) {
        		setCurrentFile(file);
        	}
        	return success;       
        } catch (Exception e) {
            setCurrentFile(null);
            e.printStackTrace();
            showError("LoadFileFailed");
            return false;
        }
    }

    /**
      * Loads all objects. 
      * @return true if successful
      */
    final public boolean loadXML(URL url) {
        try {
            myXMLio.readZipFromURL(url);
            kernel.initUndoInfo();
            isSaved = true;
            setCurrentFile(null);
            return true;
        } catch (MyError err) {
            setCurrentFile(null);
            showError(err);
            return false;
        } catch (Exception e) {
            setCurrentFile(null);
            e.printStackTrace();
            showError("LoadFileFailed");
            return false;
        }
    }
    
    //FKH 20040826
    public String getXML() {
        return myXMLio.getFullXML();
    }
    
    public void setXML(String xml, boolean clearAll) {    	
        try {        
            myXMLio.processXMLString(xml, clearAll);
        } catch (MyError err) {  
        	setCurrentFile(null); 
        	err.printStackTrace();
            showError(err);                        
        } catch (Exception e) {
            setCurrentFile(null);
            e.printStackTrace();            
            showError("LoadFileFailed");
        }
    }
    //endFKH

    final public MyXMLio getXMLio() {
        return myXMLio;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void storeUndoInfo() { 
    	if (undoActive) { 	    	
			kernel.storeUndoInfo();
			updateActions();
			isSaved = false;
    	}
    }

    public void restoreCurrentUndoInfo() {
    	if (undoActive) {     		
    		kernel.restoreCurrentUndoInfo();    		
    		updateActions();
    		isSaved = false;
    	}
    }

    final public void clearAll() {
        kernel.clearAll();
        kernel.initUndoInfo();
        updateActions();
        isSaved = true;
        System.gc();        
    }

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
    public String getCodeBase() {
    	return codebase;
    }
    
    private void initCodeBase() {
    	String path = null;
    	
        if (applet != null) {
            path =  applet.getCodeBase().toExternalForm();
        } else {
           	path = Application.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm();	     	
	    	if (path.endsWith("geogebra.jar")) // remove "geogebra.jar" from end	    
	    		path = path.substring(0, path.length() - 12);	    		    		    	
        }
        
    	//System.out.println("codebase: " + path);
    	codebase = path;
    }
    
/* ** selection handling ***/
    
    final public int selectedGeosSize() {
    	return selectedGeos.size();
    }
    
    final public ArrayList getSelectedGeos() {
        return selectedGeos;
    }
    
    /**
     * Object [] geos must contain GeoElement objects only.
     * @param geos
     */
    final public void setSelectedGeos(Object [] geos) {    
    	clearSelectedGeos(false);
    	if (geos != null) {    		
	        for (int i=0; i < geos.length; i++) {
	        	GeoElement geo = (GeoElement) geos[i];
	        	addSelectedGeo(geo, false);
	        }
    	}
        kernel.notifyRepaint();
    }
    
    final public void clearSelectedGeos() {
    	clearSelectedGeos(true);
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
    }
    
    final public void addSelectedGeo(GeoElement geo) {
    	addSelectedGeo(geo, true);
    }    
    
    final public void addSelectedGeo(GeoElement geo, boolean repaint) {
    	if (geo == null || selectedGeos.contains(geo)) return;
    	
    	selectedGeos.add(geo);
    	geo.setSelected(true);     	    
    	if (repaint) kernel.notifyRepaint();
    }
    
    /**
     * Returns the location of the jar file of this application. 
     * @param app
     */
    public URL getJarURL() {
        try {
            return getClass().getProtectionDomain().getCodeSource().getLocation();
        } catch (Exception e) {
            return null;
        }       
    }
    
    /**
     * Returns the location of the geogebra_properties.jar file. 
     * @param app
     */
    public URL getPropertiesFileURL() {
        try {
            return new URL(getCodeBase() + PROPERTIES_FILE);
        } catch (Exception e) {
            return null;
        }       
    }
    
    /**
     * Returns the location of the geogebra_cas.jar file. 
     * @param app
     */
    public URL getCASFileURL() {
        try {
            return new URL(getCodeBase() + CAS_FILE);
        } catch (Exception e) {
            return null;
        }       
    }
    
    /**
     * Returns the location of the geogebra_gui.jar file. 
     * @param app
     */
    public URL getGUIFileURL() {
        try {
            return new URL(getCodeBase() + GUI_FILE);
        } catch (Exception e) {
            return null;
        }       
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

    // handle language changes
    private class LanguageActionListener implements ActionListener {                        
        public void actionPerformed(ActionEvent e) {
        	setLanguage(getLocale(e.getActionCommand()));        	
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
                   removeKeyEventDispatcher(guiController);	
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
    	     KeyboardFocusManager.getCurrentKeyboardFocusManager().
			 	addKeyEventDispatcher(guiController);	
    		
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
    	   
}
