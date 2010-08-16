/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.main;

import geogebra.AppletImplementationInterface;
import geogebra.CommandLineArguments;
import geogebra.GeoGebra;
import geogebra.GeoGebraAppletPreloader;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.MyBoolean;
import geogebra.plugin.GgbAPI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import netscape.javascript.JSObject;


/**
 * GeoGebra applet implementation operating on a given JApplet object.
 */
public class AppletImplementation implements AppletImplementationInterface {

	private static final long serialVersionUID = 1L;

	private JApplet applet;

	protected Application app;
	protected Kernel kernel;
	private JButton btOpen;
	private DoubleClickListener dcListener;
	private EuclidianView ev;
	public boolean showOpenButton, undoActive;
	public boolean showToolBar, showToolBarHelp, showAlgebraInput;
	public boolean enableRightClick = true;
	public boolean enableChooserPopups = true;
	public boolean errorDialogsActive = true;
	public boolean enableLabelDrags = true;
	boolean enableShiftDragZoom = true;
	boolean allowRescaling = true;
	public boolean showMenuBar = false;
	//public boolean showSpreadsheet = false;
	//public boolean showAlgebraView = false;
	boolean showResetIcon = false;	
	Color bgColor, borderColor;
	private String fileStr, customToolBar;	
	private int maxIconSize;
	public boolean showFrame = true;
	private JFrame wnd;
	private JSObject browserWindow;
	public int width, height;
	//public static URL codeBase=null;
	//public static URL documentBase=null;

	//private JavaScriptMethodHandler javaScriptMethodHandler;
	//private boolean javascriptLoadFile=false, javascriptReset=false;
	//private String javascriptLoadFileName="";
	private GgbAPI  ggbApi=null;					//Ulven 29.05.08

	public String ggbOnInitParam = null;

	/** Creates a new instance of GeoGebraApplet */	
	public AppletImplementation(final JApplet applet) {
		this.applet = applet;


		// Allow rescaling eg ctrl+ ctrl- in Firefox
		applet.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{

				Component c = e.getComponent();
				Application.debug("Applet resized to: "+c.getWidth()+", "+c.getHeight());

				if (allowRescaling && app != null && !app.runningInFrame && app.onlyGraphicsViewShowing())
				{
					// use just horizontal scale factors
					// under normal circumstances, these should be the same			
					double zoomFactor = (double)c.getWidth() / (double)width;// (double)c.getHeight() / (double)height ;
					app.getEuclidianView().zoomAroundCenter(zoomFactor);

				}

				// these always need updating eg draw reset icon, play/pause icon
				width = c.getWidth();
				height = c.getHeight();
			}


		}); 

		init();
	}


	public void dispose() {
		app = null;		
		kernel = null;
		browserWindow = null;
		ev = null;		

		if (wnd != null) {
			// see GeoGebraFrame.dispose()
			wnd.dispose();
			wnd = null;
		}
	}	

	/**
	 * Initializes the CAS, GUI components, and downloads jar files 
	 * in a separate thread.
	 */
	public void initInBackground() {	
		// call JavaScript function ggbOnInit()
		initJavaScript();
		Object [] noArgs = { };
		Object [] arg = { ggbOnInitParam };

		callJavaScript("ggbOnInit", (ggbOnInitParam == null) ? noArgs : arg );

		// give applet time to repaint
		Thread initingThread = new Thread() {
			public void run() {				
				// wait a bit for applet to draw first time
				// then start background initing of GUI elements
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// for applets with an "open GeoGebra" button or "double click to open window" 
				// init window in background 
				if (showOpenButton) {
					initGeoGebraFrame();											
				} 
				else if (showFrame) {
					wnd = app.getFrame();						
				}	

				// load all jar files in background
				GeoGebraAppletPreloader.loadAllJarFiles();

				System.gc();
			}									
		};
		initingThread.start();
	}

	private void init() {
		//codeBase=this.getCodeBase();
		//documentBase=this.getDocumentBase();

		//Application.debug("codeBase="+codeBase);
		//Application.debug("documentBase="+documentBase);

		// get parameters
		// filename of construction
		fileStr = applet.getParameter("filename");
		if (fileStr != null && 
				!( fileStr.startsWith("http") || fileStr.startsWith("file") )) 
		{
			// add document base to file name 
			String documentBase = applet.getDocumentBase().toString();
			String path = documentBase.substring(0, documentBase.lastIndexOf('/')+1);
			if (fileStr.startsWith("/")) {
				fileStr = fileStr.substring(1);
			}
			fileStr = path + fileStr;			
		} else {
			// check if ggb file is encoded as base 64
			String fileBase64 = applet.getParameter("ggbBase64");
			if (fileBase64 != null)
				fileStr = "base64://" + fileBase64;
		}

		// type = "button" or parameter is not available 
		String typeStr = applet.getParameter("type");
		showOpenButton = typeStr != null && typeStr.equals("button");

		// showToolBar = "true" or parameter is not available
		showToolBar = "true".equals(applet.getParameter("showToolBar"));

		// showToolBar = "true" or parameter is not available
		showToolBarHelp = showToolBar && "true".equals(applet.getParameter("showToolBarHelp"));

		// customToolBar = "0 1 2 | 3 4 5 || 7 8 12" to set the visible toolbar modes
		customToolBar = applet.getParameter("customToolBar");

		// showMenuBar = "true" or parameter is not available
		showMenuBar = "true".equals(applet.getParameter("showMenuBar"));

		// showSpreadsheet = "true" or parameter is not available
		//showSpreadsheet = "true".equals(applet.getParameter("showSpreadsheet"));

		// showAlgebraView = "true" or parameter is not available
		//showAlgebraView = "true".equals(applet.getParameter("showAlgebraView"));

		// showResetIcon = "true" or parameter is not available
		showResetIcon = "true".equals(applet.getParameter("showResetIcon"));

		// showAlgebraInput = "true" or parameter is not available
		showAlgebraInput = "true".equals(applet.getParameter("showAlgebraInput"));

		// showFrame = "true" or "false"  states whether it is possible
		// to open the application frame by double clicking on the drawing pad
		// !false is used for downward compatibility	
		showFrame = !"false".equals(applet.getParameter("framePossible"));

		// rightClickActive, default is "true"
		enableRightClick = !"false".equals(applet.getParameter("enableRightClick"));

		// enableChooserPopups, default is "true"
		enableChooserPopups = !"false".equals(applet.getParameter("enableChooserPopups"));

		// errorDialogsActive, default is "true"
		errorDialogsActive = !"false".equals(applet.getParameter("errorDialogsActive"));

		// enableLabelDrags, default is "true"
		enableLabelDrags = !"false".equals(applet.getParameter("enableLabelDrags"));

		// paramter for JavaScript ggbOnInit() call
		ggbOnInitParam = applet.getParameter("ggbOnInitParam");

		// enableShiftDragZoom, default is "true"
		enableShiftDragZoom = !"false".equals(applet.getParameter("enableShiftDragZoom"));		

		// allowRescaling, default is "false"
		allowRescaling = "true".equals(applet.getParameter("allowRescaling"));		

		undoActive = (showToolBar || showMenuBar);

		// set language manually by iso language string
		String language = applet.getParameter("language");
		String country = applet.getParameter("country");		
		Locale loc = null;
		if (language != null) {
			if (country != null) 
				loc = new Locale(language, country);
			else
				loc = new Locale(language);
			applet.setLocale(loc);
		}	

		// bgcolor = "#CCFFFF" specifies the background color to be used for
		// the button panel
		try {
			bgColor = Color.decode(applet.getParameter("bgcolor"));
		} catch (Exception e) {
			bgColor = Color.white;
		}

		// borderColor = "#CCFFFF" specifies the border color to be used for
		// the applet panel
		try {
			borderColor = Color.decode(applet.getParameter("borderColor"));
		} catch (Exception e) {
			borderColor = Color.gray;
		}

		// maximum icon size to be used in the toolbar
		try {
			maxIconSize = Integer.parseInt(applet.getParameter("maxIconSize"));
		} catch (Exception e) {
			maxIconSize = Application.DEFAULT_ICON_SIZE;
		}

		//	build application and open file
		/*
		if (fileStr == null) {
			app = new CustomApplication(null, this, undoActive);
		} else {						
			String[] args = { fileStr };
			app = new CustomApplication(args, this, undoActive);
		}
		 */

		try {		
			if (Application.MAC_OS || Application.WINDOWS)
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			else // Linux or others
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			Application.debug(e+"");
		}

		if (fileStr == null) {
			app = buildApplication(null, undoActive);
		} else {						
			String[] args = { fileStr };
			app = buildApplication(new CommandLineArguments(args), undoActive);
		}
		
		// needed to make sure unicodeZero works
		if (loc != null) app.setLanguage(loc);

		kernel = app.getKernel();

		/* Ulven 29.05.08 */
		ggbApi=app.getGgbApi();
	}

	protected Application buildApplication(CommandLineArguments args, boolean undoActive) 
	{
		return new Application(args, this, undoActive);
	}

	public Application getApplication() {
		return app;
	}
	
	/**
	 * @return Whether this applet just needs the euclidian view
	 * 	or a part of the real GUI. In the first case many unnecessary
	 *  elements (jar packages and classes) are not loaded.
	 */
	public boolean needsGui() {
		return true;
		/*return showOpenButton
			|| showAlgebraInput
			|| showToolBar
			|| showMenuBar
			|| enableRightClick
			|| showFrame 
			|| showOpenButton;*/
	}

	public void initGUI() {		
		JPanel myContenPane;

		// show only button to open application window	
		if (showOpenButton) {
			btOpen =
				new JButton(
						app.getPlain("Open")
						+ " "
						+ app.getPlain("ApplicationName"));
			btOpen.addActionListener(new ButtonClickListener());

			// prepare content pane
			myContenPane = new JPanel();
			myContenPane.setBackground(bgColor);
			myContenPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			myContenPane.add(btOpen);

		}
		// show interactive drawing pad
		else {
			// TODO use Appication methods (F.S.)
			// create applet panel
			myContenPane = createGeoGebraAppletPanel();

			// border around applet panel
			myContenPane.setBorder(BorderFactory.createLineBorder(borderColor));			

			if (showFrame) {
				//	open frame on double click
				dcListener = new DoubleClickListener();				
				ev.addMouseListener(dcListener);
			}									
		}

		// replace applet's content pane
		Container cp = applet.getContentPane();

		Application.debug("Initial size = "+cp.getWidth()+", "+cp.getHeight());
		//Application.debug("EuclidianView size = "+app.getEuclidianView().getPreferredSize().getWidth()+", "+app.getEuclidianView().getPreferredSize().getHeight());

		width = cp.getWidth();
		height = cp.getHeight();

		setInitialScaling();

		cp.setBackground(bgColor);
		cp.removeAll();
		cp.add(myContenPane);

		// set move mode
		app.setMoveMode();		
	}

	/*
	 * rescales if the width is not what's expected
	 * eg if browser is zoomed
	 */
	private void setInitialScaling() {
		if (allowRescaling) {			
			if (!app.runningInFrame && app.onlyGraphicsViewShowing())
			{
				double zoomFactorX = (double)width / (double)app.getEuclidianView().getPreferredSize().getWidth();
				double zoomFactorY = (double)height / (double)app.getEuclidianView().getPreferredSize().getHeight();
				double zoomFactor = Math.min(zoomFactorX, zoomFactorY);
				app.getEuclidianView().zoomAroundCenter(zoomFactor);
			}
		}

	}

	protected JPanel createGeoGebraAppletPanel() {
		JPanel appletPanel = new JPanel(new BorderLayout());
		appletPanel.setBackground(bgColor);

		app.setUndoActive(undoActive);			
		app.setShowMenuBar(showMenuBar);
		//app.setShowSpreadsheetView(showSpreadsheet);
		//app.setShowAlgebraView(showAlgebraView);
		app.setShowAlgebraInput(showAlgebraInput);
		app.setShowToolBar(showToolBar, showToolBarHelp);	
		app.setRightClickEnabled(enableRightClick);
		app.setChooserPopupsEnabled(enableChooserPopups);
		app.setErrorDialogsActive(errorDialogsActive);
		app.setLabelDragsEnabled(enableLabelDrags);
		app.setShiftDragZoomEnabled(enableShiftDragZoom);
		if (customToolBar != null && customToolBar.length() > 0 && showToolBar)
			app.getGuiManager().setToolBarDefinition(customToolBar);
		app.setShowResetIcon(showResetIcon);
		app.setMaxIconSize(maxIconSize);

		appletPanel.add(app.buildApplicationPanel(), BorderLayout.CENTER);		
		ev = app.getEuclidianView();		
		ev.updateBackground();

		return appletPanel;
	}



	private class DoubleClickListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {			
				showFrame();										
			}
		}
	}

	private class ButtonClickListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			showFrame();				
		}
	}

	private void showFrame() {
		Thread worker = new Thread() {
			public void run() {	

				app.runningInFrame = true;

				applet.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				doShowFrame();	

				applet.setCursor(Cursor.getDefaultCursor());
			}        	
		};
		worker.start();			
	}

	private synchronized void doShowFrame() {
		if (showOpenButton) {
			btOpen.setEnabled(false);

			if (wnd == null)
				initGeoGebraFrame();			
		} else {
			//	clear applet		 
			Container cp = applet.getContentPane();
			cp.removeAll();
			if (ev != null)
				ev.removeMouseListener(dcListener);

			JPanel p = new JPanel(new BorderLayout());
			p.setBackground(Color.white);
			JLabel label = new JLabel("GeoGebra " + app.getPlain("WindowOpened") + "...");
			label.setFont(app.getPlainFont());
			p.add(label, BorderLayout.CENTER);
			cp.add(p);

			// initialize the GeoGebra frame's UIG
			initGeoGebraFrame();			
			applet.validate();
		}				

		// show frame		
		wnd.setVisible(true);		
	}

	private synchronized void initGeoGebraFrame() {
		// build application panel 
		if (wnd == null) {
			wnd = app.getFrame();		
		}

		app.setFrame(wnd);		
		app.setShowMenuBar(true);
		app.setShowAlgebraInput(true);		
		app.setUndoActive(true);
		app.setShowToolBar(true, true);	
		app.setRightClickEnabled(true);
		
		if (customToolBar != null && customToolBar.length() > 0)
			app.getGuiManager().setToolBarDefinition(customToolBar);

		if(app.hasFullGui())
			app.getGuiManager().updateLayout();

		app.updateContentPane();
		app.resetFonts();					
	}

	public void showApplet() {			
		Thread worker = new Thread() {
			public void run() {	
				applet.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				wnd.setVisible(false); // hide frame

				if (showOpenButton) {
					btOpen.setEnabled(true);
				} else {		
					reinitGUI();
				}		

				applet.setCursor(Cursor.getDefaultCursor());
			}        	
		};
		worker.start();				
	}

	private void reinitGUI() {

		app.runningInFrame = false;

		Container cp = applet.getContentPane();
		cp.removeAll();

		app.setApplet(this);

		if(app.hasFullGui())
			app.getGuiManager().updateLayout();
		
		initGUI();

		app.resetFonts();
		app.refreshViews();

		applet.validate();
		System.gc();
	}


	/* JAVA SCRIPT INTERFACE */
	/* Rewritten by Ulven 29.05.08:
	 * Moved method contents to GgbAPI
	 * and put in redirections to GgbApi.
	 * (Oneliners left as they are, nothing to gain...)
	 * 
	 * 
	 * 
	 */

	/**
	 * Returns current construction as a ggb file in form of a byte array.
	 * @return null if something went wrong 
	 */
	public synchronized byte [] getGGBfile() {
		return ggbApi.getGGBfile();						//Ulven 29.05.08
	}

	/**
	 * Returns current construction in XML format. May be used for saving.
	 */
	public synchronized String getXML() {
		return ggbApi.getXML();
	}

	/**
	 * Returns the GeoGebra XML string for the given GeoElement object, 
	 * i.e. only the <element> tag is returned. 
	 */
	public String getXML(String objName) {
		return ggbApi.getXML(objName);	
	}

	/**
	 * For a dependent GeoElement objName the XML string of 
	 * the parent algorithm and all its output objects is returned. 
	 * For a free GeoElement objName "" is returned.
	 */
	public String getAlgorithmXML(String objName) {
		return ggbApi.getAlgorithmXML(objName);
	}	

	/**
	 * Opens construction given in XML format. May be used for loading constructions.
	 */
	public synchronized void setXML(String xml) {
		app.setXML(xml, true);
	}

	/**
	 * Evaluates the given XML string and changes the current construction. 
	 * Note: the construction is NOT cleared before evaluating the XML string. 	 
	 */
	public synchronized void evalXML(String xmlString) {		
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT + "\">\n");
		ev.getXML(sb);
		sb.append("<construction>\n");
		sb.append(xmlString);
		sb.append("</construction>\n");
		sb.append("</geogebra>\n");
		app.setXML(sb.toString(), false);
	}


	/**
	 * Evaluates the given string as if it was entered into GeoGebra's 
	 * input text field. 	 
	 */
	public synchronized boolean evalCommand(final String cmdString) {		
		//waitForCAS();

		// avoid security problems calling from JavaScript
		MyBoolean ret = AccessController.doPrivileged(new PrivilegedAction<MyBoolean>() {
			public MyBoolean run() {
				// perform the security-sensitive operation here
				
				// make sure translated command names are loaded
				app.initTranslatedCommands();
				
				return new MyBoolean(app.getGgbApi().evalCommand(cmdString));
				
			}
		});

		//return success
		return ret.getBoolean();
	}

	/**
	 * Evaluates the given string using the MathPiper CAS.
	 */
	public synchronized String evalMathPiper(String cmdString) {
		//waitForCAS();

		final String str = cmdString;

		// avoid security problems calling from JavaScript
		return (String)AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				// perform the security-sensitive operation here
				return kernel.evaluateMathPiper(str);

			}
		});
	}

	/**
	 * Evaluates the given string using the Yacas CAS.
	 * @deprecated: use evalMathPiper() instead
	 */
	public synchronized String evalYacas(String cmdString) {
		return evalMathPiper(cmdString);
	}

	/**
	 * prints a string to the Java Console
	 */
	public synchronized void debug(String string) {		
		Application.debug(string);
	}

	//	/**
	//	 * Waits until the GeoGebraCAS has been loaded in the background.
	//	 * Note: the GeoGebraCAS is automatically inited in Application.initInBackground();
	//	 */
	//	private synchronized void waitForCAS() {
	//		if (kernel.isGeoGebraCASready()) return;
	//		
	//		// TODO: remove
	//		System.out.println("waiting for CAS to be inited ...");		
	//		
	//		while (!kernel.isGeoGebraCASready()) {
	//			try { Thread.sleep(50); } catch (Exception e) {}			
	//		}		
	//		
	//		// TODO: remove
	//		System.out.println("   CAS loaded!");
	//	}

	/**
	 * Turns on the fly creation of points in graphics view on (true) or off (false). 
	 * Note: this is useful if you don't want tools to have the side effect
	 * of creating points. For example, when this flag is set to false, the 
	 * tool "line through two points" will not create points on the fly
	 * when you click on the background of the graphics view. 
	 */
	public synchronized void setOnTheFlyPointCreationActive(boolean flag) {
		app.setOnTheFlyPointCreationActive(flag);
	}

	public synchronized void setUndoPoint() {
		app.getKernel().getConstruction().storeUndoInfo();
	}

	/**
	 * Turns showing of error dialogs on (true) or (off). 
	 * Note: this is especially useful together with evalCommand().
	 */
	public synchronized void setErrorDialogsActive(boolean flag) {
		app.setErrorDialogsActive(flag);
	}

	/**
	 * Resets the initial construction (given in filename parameter) of this applet.	 
	 */
	public synchronized void reset() {	

		if (fileStr.startsWith("base64://")) {
			byte[] zipFile;
			try {
				zipFile = geogebra.util.Base64.decode(fileStr
						.substring(9));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			app.loadXML(zipFile);
			return;
		}

		// avoid security problems calling from JavaScript
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				// perform the security-sensitive operation here
				app.setWaitCursor();
				try {							
					URL ggbURL = new URL(fileStr);
					app.loadXML(ggbURL, fileStr.toLowerCase(Locale.US).endsWith(Application.FILE_EXT_GEOGEBRA_TOOL));
					reinitGUI();
					applet.validate();
				} catch (Exception e) {
					e.printStackTrace();
				} 		
				app.setDefaultCursor();	

				return null;

			}
		});
	}

	/**
	 * Refreshs all views. Note: clears traces in
	 * geometry window.
	 */
	public synchronized void refreshViews() {
		app.refreshViews();		 				
	}

	/* returns IP address
	 * 
	 */
	public synchronized String getIPAddress() {
		return (String) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				try {
					InetAddress addr = InetAddress.getLocalHost();
					// Get host name
					return addr.getHostAddress();
				} catch (UnknownHostException e) {
					return "";
				}
			}
		});
	}

	/* returns hostname
	 * 
	 */
	public synchronized String getHostname() {
		return (String) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				try {
					InetAddress addr = InetAddress.getLocalHost();
					// Get host name
					return addr.getHostName();
				} catch (UnknownHostException e) {
					return "";
				}
			}
		});
	}

	/**
	 * Loads a construction from a  file (given URL).	
	 */
	public synchronized void openFile(final String strURL) {
		// avoid security problems calling from JavaScript
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				// perform the security-sensitive operation here
				// load file
				app.setWaitCursor();
				try {
					String myStrURL = strURL;
					String lowerCase = strURL.toLowerCase(Locale.US);
					if (!( lowerCase.startsWith("http") || lowerCase.startsWith("file") )) {
						myStrURL = applet.getCodeBase() + myStrURL;
					}		
					URL ggbURL = new URL(myStrURL);				
					app.loadXML(ggbURL, lowerCase.endsWith(Application.FILE_EXT_GEOGEBRA_TOOL));
					reinitGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}	
				app.setDefaultCursor();						

				return null;
			}
		});
	}

	/**
	 * @return The border color of the applet.
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/*
	public synchronized void setLanguage(String isoLanguageString) {	
		app.setLanguage(new Locale(isoLanguageString));
	}

	public synchronized void setLanguage(String isoLanguageString, String isoCountryString) {
		app.setLanguage(new Locale(isoLanguageString, isoCountryString));
	}
	 */

	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	public synchronized void setVisible(String objName, boolean visible) {
		ggbApi.setVisible(objName, visible);
	}

	public synchronized boolean getVisible(String objName) {
		return ggbApi.getVisible(objName);
	}	

	/**
	 * Sets the layer of the object with the given name in the geometry window.
	 * Michael Borcherds 2008-02-27
	 */
	public synchronized void setLayer(String objName, int layer) {
		ggbApi.setLayer(objName, layer);

	}

	/**
	 * Returns the layer of the object with the given name in the geometry window.
	 * returns layer, or -1 if object doesn't exist
	 * Michael Borcherds 2008-02-27
	 */
	public synchronized int getLayer(String objName) {
		return ggbApi.getLayer(objName);
	}

	/**
	 * Shows or hides a complete layer
	 * Michael Borcherds 2008-02-27
	 */
	public synchronized void setLayerVisible(int layer, boolean visible) {
		ggbApi.setLayerVisible(layer,visible);
	}



	/**
	 * Sets the fixed state of the object with the given name.
	 */
	public synchronized void setFixed(String objName, boolean flag) {
		ggbApi.setFixed(objName, flag);
	}

	/**
	 * Turns the trace of the object with the given name on or off.
	 */
	public synchronized void setTrace(String objName, boolean flag) {
		ggbApi.setTrace(objName, flag);
	}

	/**
	 * Shows or hides the label of the object with the given name in the geometry window.
	 */
	public synchronized void setLabelVisible(String objName, boolean visible) {
		ggbApi.setLabelVisible(objName, visible);
	}

	/**
	 * Sets the label style of the object with the given name in the geometry window.
	 * Possible label styles are NAME = 0, NAME_VALUE = 1 and VALUE = 2.
	 */
	public synchronized void setLabelStyle(String objName, int style) {
		ggbApi.setLabelStyle(objName, style);
	}

	/**
	 * Shows or hides the label of the object with the given name in the geometry window.
	 */
	public synchronized void setLabelMode(String objName, boolean visible) {
		ggbApi.setLabelMode(objName, visible);
	}

	/**
	 * Sets the color of the object with the given name.
	 */
	public synchronized void setColor(String objName, int red, int green, int blue) {
		ggbApi.setColor(objName, red, green, blue);
	}	

	public synchronized void setLineStyle(String objName, int style) {
		ggbApi.setLineStyle(objName, style);
	}	

	public synchronized void setLineThickness(String objName, int thickness) {
		ggbApi.setLineThickness(objName, thickness);
	}	

	public synchronized void setPointStyle(String objName, int style) {
		ggbApi.setPointStyle(objName, style);
	}	

	public synchronized void setPointSize(String objName, int style) {
		ggbApi.setPointSize(objName, style);
	}	

	public synchronized void setFilling(String objName, double filling) {
		ggbApi.setFilling(objName, filling);
	}	

	/*
	 * used by the automatic file tester (from JavaScript)
	 */
	public synchronized String getGraphicsViewCheckSum(final String algorithm, final String format) {
		// avoid security problems calling from JavaScript
		return (String)AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				// perform the security-sensitive operation here
				return ggbApi.getGraphicsViewCheckSum(algorithm, format);
			}
		});



	}

	/**
	 * Returns the color of the object as an hex string. Note that the hex-string 
	 * starts with # and uses upper case letters, e.g. "#FF0000" for red.
	 */
	public synchronized String getColor(String objName) {
		return ggbApi.getColor(objName);
	}

	public synchronized double getFilling(String objName) {
		return ggbApi.getFilling(objName);
	}	

	public synchronized int getLineStyle(String objName) {
		return ggbApi.getLineStyle(objName);
	}	

	public synchronized int getLineThickness(String objName) {
		return ggbApi.getLineThickness(objName);
	}	

	public synchronized int getPointStyle(String objName) {
		return ggbApi.getPointStyle(objName);
	}	

	public synchronized int getPointSize(String objName) {
		return ggbApi.getPointSize(objName);
	}		

	/**
	 * Deletes the object with the given name.
	 */
	public synchronized void deleteObject(String objName) {		
		ggbApi.deleteObject(objName);
	}	

	public synchronized void setAnimating(String objName, boolean animate) {
		ggbApi.setAnimating(objName, animate);
	}

	public synchronized void setAnimationSpeed(String objName, double speed) {
		ggbApi.setAnimationSpeed(objName, speed);
	}

	public synchronized void startAnimation() {
		kernel.getAnimatonManager().startAnimation();		
	}

	public synchronized void stopAnimation() {
		kernel.getAnimatonManager().stopAnimation();		
	}

	public void hideCursorWhenDragging(boolean hideCursorWhenDragging) {
		kernel.getApplication().setUseTransparentCursorWhenDragging(hideCursorWhenDragging);
	}	

	public synchronized boolean isAnimationRunning() {
		return kernel.getAnimatonManager().isRunning();		
	}

	/**
	 * Renames an object from oldName to newName.
	 * @return whether renaming worked
	 */
	public synchronized boolean renameObject(String oldName, String newName) {		
		return ggbApi.renameObject(oldName, newName);
	}	

	/**
	 * Returns true if the object with the given name exists.
	 */
	public synchronized boolean exists(String objName) {	
		return ggbApi.exists(objName);
	}	

	/**
	 * Returns true if the object with the given name has a vaild
	 * value at the moment.
	 */
	public synchronized boolean isDefined(String objName) {			
		return ggbApi.isDefined(objName);
	}	

	/**
	 * Returns the value of the object with the given name as a string.
	 */
	public synchronized String getValueString(String objName) {
		return ggbApi.getValueString(objName);
	}

	/**
	 * Returns the definition of the object with the given name as a string.
	 */
	public synchronized String getDefinitionString(String objName) {
		return ggbApi.getDefinitionString(objName);
	}

	/**
	 * Returns the command of the object with the given name as a string.
	 */
	public synchronized String getCommandString(String objName) {	
		return ggbApi.getCommandString(objName);
	}

	/**
	 * Returns the x-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getXcoord(String objName) {
		return ggbApi.getXcoord(objName);
	}

	/**
	 * Returns the y-coord of the object with the given name. Note: returns 0 if
	 * the object is not a point or a vector.
	 */
	public synchronized double getYcoord(String objName) {
		return ggbApi.getYcoord(objName);
	}

	/**
	 * Sets the coordinates of the object with the given name. Note: if the
	 * specified object is not a point or a vector, nothing happens.
	 */
	public synchronized void setCoords(String objName, double x, double y) {
		ggbApi.setCoords(objName,x,y);
	}

	/**
	 * Returns the double value of the object with the given name. Note: returns 0 if
	 * the object does not have a value.
	 */
	public synchronized double getValue(String objName) {
		return ggbApi.getValue(objName);
	}

	/**
	 * Sets the double value of the object with the given name. Note: if the
	 * specified object is not a number, nothing happens.
	 */
	public synchronized void setValue(String objName, double x) {
		ggbApi.setValue(objName,x);
	}

	/**
	 * Turns the repainting of all views on or off.
	 */
	public synchronized void setRepaintingActive(boolean flag) {		
		//Application.debug("set repainting: " + flag);
		ggbApi.setRepaintingActive(flag);
	}	


	/*
	 * Methods to change the geometry window's properties	 
	 */

	/**
	 * Sets the Cartesian coordinate system in the graphics window.
	 */
	public synchronized void setCoordSystem(double xmin, double xmax, double ymin, double ymax) {
		app.getEuclidianView().setRealWorldCoordSystem(xmin, xmax, ymin, ymax);
	}

	/**
	 * Shows or hides the x- and y-axis of the coordinate system in the graphics window.
	 */
	public synchronized void setAxesVisible(boolean xVisible, boolean yVisible) {		
		ggbApi.setAxesVisible(xVisible, yVisible);
	}	

	/**
	 * Shows or hides the coordinate grid in the graphics window.
	 */
	public synchronized void setGridVisible(boolean flag) {		
		app.getEuclidianView().showGrid(flag);
	}



	/**
	 * Returns an array with all object names.
	 */
	public synchronized String [] getAllObjectNames() {			
		return ggbApi.getObjNames();
	}	

	/**
	 * Returns the number of objects in the construction.
	 */
	public synchronized int getObjectNumber() {					
		return ggbApi.getObjNames().length;			
	}	

	/**
	 * Returns the name of the n-th object of this construction.
	 */
	public synchronized String getObjectName(int i) {	
		return ggbApi.getObjectName(i);
	}

	/**
	 * Returns the type of the object with the given name as a string (e.g. point, line, circle, ...)
	 */
	public synchronized String getObjectType(String objName) {
		return ggbApi.getObjectType(objName);
	}

	/**
	 * Sets the mode of the geometry window (EuclidianView). 
	 */
	public synchronized void setMode(int mode) {
		app.setMode(mode);
	}

	public synchronized void initJavaScript() {
		if (browserWindow == null) {
			try {							
				browserWindow = JSObject.getWindow(applet);
			} catch (Exception e) {							
				System.err.println("Exception: could not initialize JSObject.getWindow() for GeoGebraApplet");
			}    			
		}
	}


	public void callJavaScript(String jsFunction, Object [] args) {		
		//Application.debug("callJavaScript: " + jsFunction);		

		try {			
			if (browserWindow != null)
				browserWindow.call(jsFunction, args);
		} catch (Exception e) {						
			System.err.println("Warning: JavaScript function '"+jsFunction+"' not found");
		}    
	} 	
	
	public JApplet getJApplet() {
		return applet;
	}

	public synchronized void registerAddListener(String JSFunctionName) {
		app.getScriptManager().registerAddListener(JSFunctionName);
	}

	public synchronized void unregisterAddListener(String JSFunctionName) {
		app.getScriptManager().registerAddListener(JSFunctionName);
	}

	public synchronized void registerRemoveListener(String JSFunctionName) {
		app.getScriptManager().registerRemoveListener(JSFunctionName);
	}

	public synchronized void unregisterRemoveListener(String JSFunctionName) {
		app.getScriptManager().unregisterRemoveListener(JSFunctionName);
	}

	public synchronized void registerClearListener(String JSFunctionName) {
		app.getScriptManager().registerClearListener(JSFunctionName);
	}

	public synchronized void unregisterClearListener(String JSFunctionName) {
		app.getScriptManager().unregisterClearListener(JSFunctionName);
	}

	public synchronized void registerRenameListener(String JSFunctionName) {
		app.getScriptManager().registerRenameListener(JSFunctionName);
	}

	public synchronized void unregisterRenameListener(String JSFunctionName) {
		app.getScriptManager().unregisterRenameListener(JSFunctionName);
	}

	public synchronized void registerUpdateListener(String JSFunctionName) {
		app.getScriptManager().registerUpdateListener(JSFunctionName);
	}

	public synchronized void unregisterUpdateListener(String JSFunctionName) {
		app.getScriptManager().unregisterUpdateListener(JSFunctionName);
	}

	public synchronized void registerObjectUpdateListener(String objName, String JSFunctionName) {
		app.getScriptManager().registerObjectUpdateListener(objName, JSFunctionName);
	}

	public synchronized void unregisterObjectUpdateListener(String objName) {
		app.getScriptManager().unregisterObjectUpdateListener(objName);
	}


}
