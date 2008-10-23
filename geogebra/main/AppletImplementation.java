/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.main;

import geogebra.JavaScriptAPI;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.plugin.GgbAPI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import netscape.javascript.JSObject;


/**
 * GeoGebra applet implementation operating on a given JApplet object.
 */
public abstract class AppletImplementation implements JavaScriptAPI {

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
	public boolean enableLabelDrags = true;
	boolean enableShiftDragZoom = true;
	public boolean showMenuBar = false;
	//public boolean showSpreadsheet = false;
	//public boolean showAlgebraView = false;
	boolean showResetIcon = false;
	private boolean firstAppOpen = true;
	Color bgColor, borderColor;
	private String fileStr, customToolBar;	
	public boolean showFrame = true;
	private JFrame wnd;
	private JSObject browserWindow;
	//public static URL codeBase=null;
	//public static URL documentBase=null;
	
	private boolean javascriptLoadFile=false, javascriptReset=false;
	private String javascriptLoadFileName="";
	private GgbAPI  ggbApi=null;					//Ulven 29.05.08

	/** Creates a new instance of GeoGebraApplet */
	public AppletImplementation(JApplet applet) {
		this.applet = applet;
	}
	
	public JApplet getJApplet() {
		return applet;
	}

	public void init() {
		
		//codeBase=this.getCodeBase();
		//documentBase=this.getDocumentBase();
		
		//Application.debug("codeBase="+codeBase);
		//Application.debug("documentBase="+documentBase);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
	
		// get parameters
		// filename of construction
		fileStr = applet.getParameter("filename");
		if (fileStr != null && 
			!( fileStr.startsWith("http") || fileStr.startsWith("file") )) {
			fileStr = applet.getCodeBase() + fileStr;			
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
		
		// enableLabelDrags, default is "true"
		enableLabelDrags = !"false".equals(applet.getParameter("enableLabelDrags"));
		
		// enableShiftDragZoom, default is "true"
		enableShiftDragZoom = !"false".equals(applet.getParameter("enableShiftDragZoom"));		
		
		undoActive = showToolBar || showMenuBar;
		
		// set language manually by iso language string
		String language = applet.getParameter("language");
		String country = applet.getParameter("country");		
		if (language != null) {
			if (country != null)
				applet.setLocale(new Locale(language, country));
			else
				applet.setLocale(new Locale(language));
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

		//	build application and open file
		/*
		if (fileStr == null) {
			app = new CustomApplication(null, this, undoActive);
		} else {						
			String[] args = { fileStr };
			app = new CustomApplication(args, this, undoActive);
		}
		*/
		
		if (fileStr == null) {
			app = buildApplication(null, undoActive);
		} else {						
			String[] args = { fileStr };
			app = buildApplication(args, undoActive);
		}

		kernel = app.getKernel();
		
		/* Ulven 29.05.08 */
		ggbApi=app.getGgbApi();			
		
		initGUI();		
 
		// Michael Borcherds 2008-04-20
		// code to allow JavaScript methods reset() and openFile() to access files
		// even if the code is untrusted
    	Thread runner = new Thread() {
		public void run(){
			while (true) {
				try {Thread.sleep(200);} catch(Exception e) {}
			
				//Application.debug("thread");
			    	if (javascriptReset) resetNoThread();
			    	
			    	if (javascriptLoadFile) openFileNoThread(javascriptLoadFileName);
			    	
			    	javascriptLoadFile=false;
			    	javascriptReset=false;
			 
			    }
			
	    	
		}
		 
		};
		runner.start();
	}

	protected abstract Application buildApplication(String[] args, boolean ua);
		

	protected void initGUI() {
		applet.getContentPane().removeAll();
		
		// show only button to open application window	
		if (showOpenButton) {
			btOpen =
				new JButton(
					app.getPlain("Open")
						+ " "
						+ app.getPlain("ApplicationName"));
			btOpen.addActionListener(new ButtonClickListener());
			Container cp = applet.getContentPane();
			cp.setBackground(bgColor);
			cp.setLayout(new FlowLayout(FlowLayout.CENTER));
			cp.add(btOpen);

		}
		// show interactive drawing pad
		else {
			JPanel panel = createGeoGebraAppletPanel();
			applet.getContentPane().add(panel);			
			
			// border around applet panel
			panel.setBorder(BorderFactory.createLineBorder(borderColor));			

			if (showFrame) {
				//	open frame on double click
				dcListener = new DoubleClickListener();				
				ev.addMouseListener(dcListener);
			}						
		}
		app.setMoveMode();
	}
	
	protected JPanel createGeoGebraAppletPanel() {
		JPanel appletPanel = new JPanel(new BorderLayout());
		
		app.setUndoActive(undoActive);			
		app.setShowMenuBar(showMenuBar);
		//app.setShowSpreadsheetView(showSpreadsheet);
		//app.setShowAlgebraView(showAlgebraView);
		app.setShowAlgebraInput(showAlgebraInput);
		app.setShowToolBar(showToolBar, showToolBarHelp);	
		app.setRightClickEnabled(enableRightClick);
		app.setLabelDragsEnabled(enableLabelDrags);
		app.setShiftDragZoomEnabled(enableShiftDragZoom);
		if (customToolBar != null && customToolBar.length() > 0 && showToolBar)
			app.getGuiManager().setToolBarDefinition(customToolBar);
		app.setShowResetIcon(showResetIcon);
		
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
		if (showOpenButton) {
			btOpen.setEnabled(false);
		} else {
			//	clear applet		 
			Container cp = applet.getContentPane();
			cp.removeAll();
			if (ev != null)
				ev.removeMouseListener(dcListener);

			JPanel p = new JPanel(new BorderLayout());
			p.setBackground(Color.white);
			JLabel label = new JLabel(app.getPlain("WindowOpened") + "...");
			label.setFont(app.getPlainFont());
			p.add(label, BorderLayout.CENTER);
			cp.add(p);
			SwingUtilities.updateComponentTreeUI(applet);				
		}
		
//		 build application panel 
		if (firstAppOpen) {
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
			
		app.updateContentPane();
		app.resetFonts();

		// open frame
		if (firstAppOpen) {
			wnd.setVisible(true);
			firstAppOpen = false;
		} else {
			wnd.setVisible(true);
		}
	}

	public void showApplet() {
		
		wnd.setVisible(false); // hide frame
		
		if (showOpenButton) {
			btOpen.setEnabled(true);
		} else {		
			reinitGUI();
		}
	}
	
	private void reinitGUI() {
		Container cp = applet.getContentPane();
		cp.removeAll();
		
		app.setApplet(this);
		initGUI();
		
		app.resetFonts();
		app.refreshViews();
		SwingUtilities.updateComponentTreeUI(applet);
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
		return app.getXML();
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
		app.setXML(xmlString, false);
	}

	
	/**
	 * Evaluates the given string as if it was entered into GeoGebra's 
	 * input text field. 	 
	 */
	public synchronized boolean evalCommand(String cmdString) {
		GeoElement [] result = kernel.getAlgebraProcessor().
								processAlgebraCommand(cmdString, false);
		// return success
		return result != null;
	}

	/**
	 * Evaluates the given string as if it was entered into Yacas's 
	 * input text field. 	 
	 */
	public synchronized String evalYacas(String cmdString) {
		return 	kernel.evaluateYACASRaw(cmdString);

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
	 * ...but the actual code is in a thread to avoid JavaScript security issues 
	 */
	public synchronized void reset() {
		
		javascriptReset=true; // send message to thread to avoid security issues
		/*
		try {		
			URL ggbURL = new URL(fileStr);
			app.loadXML(ggbURL, fileStr.toLowerCase(Locale.US).endsWith(Application.FILE_EXT_GEOGEBRA_TOOL));
			reinitGUI();	
		} catch (Exception e) {
			e.printStackTrace();
		} 			*/		
	}
	
	/**
	 * Resets the initial construction (given in filename parameter) of this applet.	 
	 */
	public synchronized void resetNoThread() {
		
		try {		
			URL ggbURL = new URL(fileStr);
			app.loadXML(ggbURL, fileStr.toLowerCase(Locale.US).endsWith(Application.FILE_EXT_GEOGEBRA_TOOL));
			reinitGUI();	
		} catch (Exception e) {
			e.printStackTrace();
		} 					
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
		return app.IPAddress;
	}
			
	/* returns hostname
	 * 
	 */
	public synchronized String getHostname() {
		return app.hostName;
	}
			
	/**
	 * Loads a construction from a  file (given URL).	
	 * ...but the actual code is in a thread to avoid JavaScript security issues  
	 */
	public synchronized void openFile(String strURL) {
		
		javascriptLoadFileName=strURL;
		javascriptLoadFile=true;
		
		/*
		
		try {
			String lowerCase = strURL.toLowerCase(Locale.US);
			if (!( lowerCase.startsWith("http") || lowerCase.startsWith("file") )) {
				strURL = getCodeBase() + strURL;
			}		
			URL ggbURL = new URL(strURL);				
			app.loadXML(ggbURL, lowerCase.endsWith(Application.FILE_EXT_GEOGEBRA_TOOL));
			reinitGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}		*/
	}
	
	/**
	 * Loads a construction from a  file (given URL).	
	 */
	public synchronized void openFileNoThread(String strURL) {
		
		try {
			String lowerCase = strURL.toLowerCase(Locale.US);
			if (!( lowerCase.startsWith("http") || lowerCase.startsWith("file") )) {
				strURL = applet.getCodeBase() + strURL;
			}		
			URL ggbURL = new URL(strURL);				
			app.loadXML(ggbURL, lowerCase.endsWith(Application.FILE_EXT_GEOGEBRA_TOOL));
			reinitGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}		
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
	
	/**
	 * Returns the color of the object as an hex string. Note that the hex-string 
	 * starts with # and uses upper case letters, e.g. "#FF0000" for red.
	 */
	public synchronized String getColor(String objName) {
		return ggbApi.getColor(objName);
	}	
	
	/**
	 * Deletes the object with the given name.
	 */
	public synchronized void deleteObject(String objName) {		
		ggbApi.deleteObject(objName);
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
		app.getEuclidianView().showAxes(xVisible, yVisible);
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
	
	
	/*
	 * Change listener implementation
	 * Java to JavaScript
	 */
	
	// maps between GeoElement and JavaScript function names
	private HashMap updateListenerMap;
	private ArrayList addListeners, removeListeners, renameListeners, updateListeners, clearListeners;
	private JavaToJavaScriptView javaToJavaScriptView;
	
	/**
	 * Registers a JavaScript function as an add listener for the applet's construction.
	 *  Whenever a new object is created in the GeoGebraApplet's construction, the JavaScript 
	 *  function JSFunctionName is called using the name of the newly created object as a single argument. 
	 */
	public synchronized void registerAddListener(String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;				
						
		// init view
		initJavaScriptView();
		
		// init list
		if (addListeners == null) {
			addListeners = new ArrayList();			
		}		
		addListeners.add(JSFunctionName);				
		Application.debug("registerAddListener: " + JSFunctionName);
	}
	
	/**
	 * Removes a previously registered add listener 
	 * @see registerAddListener() 
	 */
	public synchronized void unregisterAddListener(String JSFunctionName) {
		if (addListeners != null) {
			addListeners.remove(JSFunctionName);
			Application.debug("unregisterAddListener: " + JSFunctionName);
		}	
	}	
	
	/**
	 * Registers a JavaScript function as a remove listener for the applet's construction.
	 * Whenever an object is deleted in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the deleted object as a single argument. 	
	 */
	public synchronized void registerRemoveListener(String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;				
						
		// init view
		initJavaScriptView();
		
		// init list
		if (removeListeners == null) {
			removeListeners = new ArrayList();			
		}		
		removeListeners.add(JSFunctionName);				
		Application.debug("registerRemoveListener: " + JSFunctionName);
	}
	
	/**
	 * Removes a previously registered remove listener 
	 * @see registerRemoveListener() 
	 */
	public synchronized void unregisterRemoveListener(String JSFunctionName) {
		if (removeListeners != null) {
			removeListeners.remove(JSFunctionName);
			Application.debug("unregisterRemoveListener: " + JSFunctionName);
		}	
	}	
	
	/**
	 * Registers a JavaScript function as a clear listener for the applet's construction.
	 * Whenever the construction in the GeoGebraApplet's is cleared (i.e. all objects are removed), the JavaScript 
	 * function JSFunctionName is called using no arguments. 	
	 */
	public synchronized void registerClearListener(String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;				
						
		// init view
		initJavaScriptView();
		
		// init list
		if (clearListeners == null) {
			clearListeners = new ArrayList();			
		}		
		clearListeners.add(JSFunctionName);				
		Application.debug("registerClearListener: " + JSFunctionName);
	}
	
	/**
	 * Removes a previously registered clear listener 
	 * @see registerClearListener() 
	 */
	public synchronized void unregisterClearListener(String JSFunctionName) {
		if (clearListeners != null) {
			clearListeners.remove(JSFunctionName);
			Application.debug("unregisterClearListener: " + JSFunctionName);
		}	
	}	
	
	/**
	 * Registers a JavaScript function as a rename listener for the applet's construction.
	 * Whenever an object is renamed in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the deleted object as a single argument. 	
	 */
	public synchronized void registerRenameListener(String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;				
						
		// init view
		initJavaScriptView();
		
		// init list
		if (renameListeners == null) {
			renameListeners = new ArrayList();			
		}		
		renameListeners.add(JSFunctionName);				
		Application.debug("registerRenameListener: " + JSFunctionName);
	}
	
	/**
	 * Removes a previously registered rename listener.
	 * @see registerRenameListener() 
	 */
	public synchronized void unregisterRenameListener(String JSFunctionName) {
		if (renameListeners != null) {
			renameListeners.remove(JSFunctionName);
			Application.debug("unregisterRenameListener: " + JSFunctionName);
		}	
	}	
	
	/**
	 * Registers a JavaScript function as an update listener for the applet's construction.
	 * Whenever any object is updated in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the updated object as a single argument. 	
	 */
	public synchronized void registerUpdateListener(String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;				
						
		// init view
		initJavaScriptView();
		
		// init list
		if (updateListeners == null) {
			updateListeners = new ArrayList();			
		}		
		updateListeners.add(JSFunctionName);				
		Application.debug("registerUpdateListener: " + JSFunctionName);
	}
	
	/**
	 * Removes a previously registered update listener.
	 * @see registerRemoveListener() 
	 */
	public synchronized void unregisterUpdateListener(String JSFunctionName) {
		if (updateListeners != null) {
			updateListeners.remove(JSFunctionName);
			Application.debug("unregisterUpdateListener: " + JSFunctionName);
		}	
	}	
	
	/**
	 * Registers a JavaScript update listener for an object. Whenever the object with 
	 * the given name changes, a JavaScript function named JSFunctionName 
	 * is called using the name of the changed object as the single argument. 
	 * If objName previously had a mapping JavaScript function, the old value 
	 * is replaced.
	 * 
	 * Example: First, set a change listening JavaScript function:
	 * ggbApplet.setChangeListener("A", "myJavaScriptFunction");
	 * Then the GeoGebra Applet will call the Javascript function
	 * myJavaScriptFunction("A");
	 * whenever object A changes.	
	 */
	public synchronized void registerObjectUpdateListener(String objName, String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;		
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;
				
		// init view
		initJavaScriptView();
		
		// init map and view
		if (updateListenerMap == null) {
			updateListenerMap = new HashMap();			
		}
		
		// add map entry
		updateListenerMap.put(geo, JSFunctionName);		
		Application.debug("registerUpdateListener: object: " + objName + ", function: " + JSFunctionName);
	}
	
	/**
	 * Removes a previously set change listener for the given object.
	 * @see setChangeListener
	 */
	public synchronized void unregisterObjectUpdateListener(String objName) {
		if (updateListenerMap != null) {
			GeoElement geo = kernel.lookupLabel(objName);
			if (geo != null) {
				updateListenerMap.remove(geo);
				Application.debug("unregisterUpdateListener for object: " + objName);
			}
		}
	}				

	/**
	 * Implements the View interface for
	 * Java to JavaScript communication, see
	 * addChangeListener() and removeChangeListener()
	 */	
	private class JavaToJavaScriptView implements View {
		
		/**
		 * Calls all registered add listeners.
		 * @see registerAddListener()
		 */
		public void add(GeoElement geo) {
			if (addListeners != null && geo.isLabelSet()) { 	
				Object [] args = { geo.getLabel() };
				notifyListeners(addListeners, args);
			}
		}
		
		/**
		 * Calls all registered remove listeners.
		 * @see registerRemoveListener()
		 */
		public void remove(GeoElement geo) {
			if (removeListeners != null && geo.isLabelSet()) {  
				Object [] args = { geo.getLabel() };
				notifyListeners(removeListeners, args);						
			}			
		}
		
		/**
		 * Calls all registered clear listeners.
		 * @see registerClearListener()
		 */
		public void clearView() {
			/* 
			 * This code would make sense for a "reload" 
			 * 
			// try to keep all update listeners
			if (updateListenerMap != null) {			
				HashMap newGeoJSfunMap = new HashMap(); 
				
				// go through all geos and update their maps
				Iterator it = updateListenerMap.keySet().iterator();
				while (it.hasNext()) {
					// try to find new geo with same label
					GeoElement oldGeo = (GeoElement) it.next();				
					GeoElement newGeo = kernel.lookupLabel(oldGeo.getLabel());
					
					if (newGeo != null)
						// add mapping to new map
						newGeoJSfunMap.put(newGeo,(String) updateListenerMap.get(oldGeo));				
				}
				
				// use new map
				updateListenerMap.clear();
				updateListenerMap = newGeoJSfunMap;			
			}
			*/
			
			ggbApi.lastGeoElementsIteratorSize = 0;	//ulven 29.08.05: should have been a method...
			updateListenerMap = null;			
			if (clearListeners != null) {  				
				notifyListeners(clearListeners, null);						
			}
		}
		
		/**
		 * Calls all registered rename listeners.
		 * @see registerRenameListener()
		 */
		public void rename(GeoElement geo) {						
			if (renameListeners != null && geo.isLabelSet()) {
				Object [] args = { geo.getOldLabel(), geo.getLabel() };
				notifyListeners(renameListeners, args);				
			}			
		}
		
		/**
		 * Calls all JavaScript functions (listeners) using 
		 * the specified arguments.
		 */
		private void notifyListeners(ArrayList listeners, Object [] args) {										
			int size = listeners.size();
			for (int i=0; i < size; i++) {
				String jsFunction = (String) listeners.get(i);										
				callJavaScript(jsFunction, args);					
			}			
		}	
																	
		/**
		 * Calls all registered update and updateObject listeners.
		 * @see registerUpdateListener()
		 */
		public void update(GeoElement geo) {						
			// update listeners
			if (updateListeners != null && geo.isLabelSet()) {
				Object [] args = { geo.getLabel() };
				notifyListeners(updateListeners, args);	
			}
			
			// updateObject listeners
			if (updateListenerMap != null) {			
				String jsFunction = (String) updateListenerMap.get(geo);		
				if (jsFunction != null) {	
					Object [] args = { geo.getLabel() };
					callJavaScript(jsFunction, args);
				}
			}
		}
				
		public void updateAuxiliaryObject(GeoElement geo) {
			update(geo);
		}				
					
		public void reset() {							
		}
				
    	public void repaintView() {
    		// no repaint should occur here: views that are
    		// part of the applet do this on their own    		
    	}    	    	
	}
		
	private void initJavaScriptView() {
		if (javaToJavaScriptView == null) {
			javaToJavaScriptView = new JavaToJavaScriptView();
			kernel.attach(javaToJavaScriptView); // register view
			
			try {							
				browserWindow = JSObject.getWindow(applet);
			} catch (Exception e) {							
				Application.debug("Exception: could not initialize JSObject.getWindow() for GeoGebraApplet");
			}    			
		}
	}
	
	private void callJavaScript(String jsFunction, Object [] args) {		
		//Application.debug("callJavaScript: " + jsFunction);		
		
		try {			
			if (browserWindow != null)
				browserWindow.call(jsFunction, args);
		} catch (Exception e) {						
			e.printStackTrace();
		}    
	}

	
}
