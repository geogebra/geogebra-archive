/* 
GeoGebra - Dynamic Mathematics for Everyone
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.JApplet;

/**
 * GeoGebra applet
 * 
 * @see geogebra.main.AppletImplementation for the actual implementation
 * @author Markus Hohenwarter
 * @date 2008-10-24
 */
public class GeoGebraApplet extends JApplet implements JavaScriptAPI {

	private static final long serialVersionUID = -350682076336303151L;
	
	// splash screen settings
	private static final int SPLASH_IMAGE_WIDTH = 320;
	private static final int SPLASH_IMAGE_HEIGHT = 106;
	private static final int PROGRESS_IMAGE_WIDTH = 16;
	private static final int PROGRESS_IMAGE_HEIGHT = 16;
	private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 11);
	
	// applet member variables
	private JavaScriptAPI appletImplementation = null;	
	private JarManager jarManager;
	private int width, height;
	
	// splash screen stuff
	private Image splashImage, progressImage;
	private Image splashScreenImage;
	private Graphics splashScreenImageGraphics;
		
	/**
	 * Loads necessary jar files and initializes applet. During the loading
	 * of jar files, an "is loading" message is shown
	 */
	public void init() {  
		Thread runner = new Thread() {
			public void run() {							
				initAppletImplementation();	        				
			}
		};
		runner.start();	
		
		// init splash screen
		initSplashScreen();		 	      
	}

	public void start() {
		repaint();		
		System.gc();
	}

	public void stop() {
		repaint();		
		System.gc();
	}

	public void destroy() {
		appletImplementation = null;
		System.gc();
	}
	
	/**
	 * Returns the appletImplementation object. 	 
	 */
	private synchronized JavaScriptAPI getAppletImplementation() {
		if (appletImplementation == null) {
			initAppletImplementation();
		}
		
		return appletImplementation;
	}
	
	/**
	 * Initializes the appletImplementation object. 
	 * Loads geogebra_main.jar file and initializes applet if necessary.
	 */
	private void initAppletImplementation() {
		// load geogebra_main.jar file   
		// init jar manager to load jar files for applet
		jarManager = JarManager.getSingleton(true);
		jarManager.addJarToClassPath(JarManager.JAR_FILE_GEOGEBRA_MAIN);

		// create delegate object that implements our applet's methods
		geogebra.main.DefaultApplet applImpl = new geogebra.main.DefaultApplet(this);
		
		// initialize applet's user interface, this changes the content pane
		applImpl.initGUI();
				
		// remember the applet implementation
		appletImplementation = applImpl;	
		
		// update applet GUI, see paint()
		validate();
		
		// load all jar files in background and init dialogs
		applImpl.getApplication().initInBackground();
		
		// clear splash images
		splashScreenImage = null;
		splashScreenImageGraphics = null;
		splashImage = null;
		progressImage = null;
		System.gc();
	}
		
	/**
     * Paints the applet or a loading screen while the applet is being initialized.
     */
    final public void paint(Graphics g) {
    	if (appletImplementation == null) { 
    		g.drawImage(splashScreenImage, 0, 0, null);
    	} else
    		super.paint(g);		    	                 
    }
    
    /**
     * Initializes a loading screen to show progress of downloading jar files.
     */
    private void initSplashScreen() {
    	// create splash screen image for fast drawing
		width = getWidth();
		height = getHeight();
		splashScreenImage = createImage(width, height);
		splashScreenImageGraphics = splashScreenImage.getGraphics();
		
		// load splash image and animated progress image
		splashImage = getImage(GeoGebraApplet.class.getResource("splash.gif"));
    	progressImage = getImage(GeoGebraApplet.class.getResource("progress.gif")); 
    	
    	// update splash screen image and paint it
    	updateSplashScreenImage();
    }
      
    /**
     * Paints a loading screen to show progress with downloading jar files.
     */
    private synchronized void updateSplashScreenImage() {
    	if (splashScreenImageGraphics == null) return;
    	
    	Graphics2D g = (Graphics2D) splashScreenImageGraphics;
    	
    	// white background
    	g.setColor(Color.white);
    	g.clearRect(0, 0, width, height);
    	    	
    	// splash image position
    	int splashX = -1;
    	int splashY = -1;    	    	    
    	if (splashImage != null) {
    		splashX = (width - SPLASH_IMAGE_WIDTH) / 2;
    		splashY = (height - SPLASH_IMAGE_HEIGHT) / 2 - PROGRESS_IMAGE_HEIGHT;
    	}
    	
    	// progress image position
    	int progressX = (width - PROGRESS_IMAGE_WIDTH)/2 ;
    	int progressY = (height - PROGRESS_IMAGE_HEIGHT)/2;
    	
    	// Splash image fits into content pane: draw splash image
    	if (splashX >=0 && splashY >=0 ) {    
    		g.drawImage(splashImage, splashX, splashY, this);
    		
    		// put progress image below splash image
    		progressY = splashY + SPLASH_IMAGE_HEIGHT;        
    	} 
    		    	
    	// draw progress image
    	g.drawImage(progressImage, progressX, progressY, this);
    	
    	// draw status message of JarManager below progress image    	
    	if (jarManager != null) {  
    		String statusMessage = jarManager.getStatusMessage();
    		
    		if (statusMessage != null) {
	        	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);            
	        	g.setColor(Color.black);
	    		g.setFont(DEFAULT_FONT);
	        	g.drawString(statusMessage, width/2 - (int) (2.5*statusMessage.length()), progressY + PROGRESS_IMAGE_HEIGHT + 2*DEFAULT_FONT.getSize());	        
    		}   
    	}
    	
    	repaint();
    }
    
    /**
     * Updates the progress image (animated gif) during appletImplementation loading. 
     * Overrides ImageObserver.
     */
    public boolean imageUpdate( Image img, int flags, int x, int y, 
    	    int w, int h ) 
	  {	   
    	// repaint applet to update progress image
    	updateSplashScreenImage();
	   
	    // stop the image updating when the appletImplementation is loaded
	    return appletImplementation == null;
	  }
   

	/*
	 * JAVASCRIPT interface
	 * 
	 * To add a new JavaScript method, do the following: 1) add the method stub
	 * to the interface geogebra.JavaScriptAPI 2) implement the method in
	 * geogebra.main.AppletImplementation 3) impplement the method here in
	 * geogebra.GeoGebraApplet by delegating it to
	 * geogebra.main.AppletImplementation
	 */

	public void deleteObject(String objName) {
		getAppletImplementation().deleteObject(objName);
	}

	public boolean evalCommand(String cmdString) {
		return getAppletImplementation().evalCommand(cmdString);
	}

	public void evalXML(String xmlString) {
		getAppletImplementation().evalCommand(xmlString);
	}

	public String evalYacas(String cmdString) {
		return getAppletImplementation().evalYacas(cmdString);
	}

	public boolean exists(String objName) {
		return getAppletImplementation().exists(objName);
	}

	public String[] getAllObjectNames() {
		return getAppletImplementation().getAllObjectNames();
	}

	public String getColor(String objName) {
		return getAppletImplementation().getColor(objName);
	}

	public String getCommandString(String objName) {
		return getAppletImplementation().getCommandString(objName);
	}

	public String getDefinitionString(String objName) {
		return getAppletImplementation().getDefinitionString(objName);
	}

	public byte[] getGGBfile() {
		return getAppletImplementation().getGGBfile();
	}

	public String getHostname() {
		return getAppletImplementation().getHostname();
	}

	public String getIPAddress() {
		return getAppletImplementation().getIPAddress();
	}

	public int getLayer(String objName) {
		return getAppletImplementation().getLayer(objName);
	}

	public String getObjectName(int i) {
		return getAppletImplementation().getObjectName(i);
	}

	public int getObjectNumber() {
		return getAppletImplementation().getObjectNumber();
	}

	public String getObjectType(String objName) {
		return getAppletImplementation().getObjectType(objName);
	}

	public double getValue(String objName) {
		return getAppletImplementation().getValue(objName);
	}

	public String getValueString(String objName) {
		return getAppletImplementation().getValueString(objName);
	}

	public String getXML() {
		return getAppletImplementation().getXML();
	}

	public double getXcoord(String objName) {
		return getAppletImplementation().getXcoord(objName);
	}

	public double getYcoord(String objName) {
		return getAppletImplementation().getYcoord(objName);
	}

	public boolean isDefined(String objName) {
		return getAppletImplementation().isDefined(objName);
	}

	public void openFile(String strURL) {
		getAppletImplementation().openFile(strURL);
	}

	public void refreshViews() {
		getAppletImplementation().refreshViews();
	}

	public void registerAddListener(String JSFunctionName) {
		getAppletImplementation().registerAddListener(JSFunctionName);
	}

	public void registerClearListener(String JSFunctionName) {
		getAppletImplementation().registerClearListener(JSFunctionName);
	}

	public void registerObjectUpdateListener(String objName, String JSFunctionName) {
		getAppletImplementation().registerObjectUpdateListener(objName, JSFunctionName);
	}

	public void registerRemoveListener(String JSFunctionName) {
		getAppletImplementation().registerRemoveListener(JSFunctionName);
	}

	public void registerRenameListener(String JSFunctionName) {
		getAppletImplementation().registerRenameListener(JSFunctionName);
	}

	public void registerUpdateListener(String JSFunctionName) {
		getAppletImplementation().registerUpdateListener(JSFunctionName);
	}

	public void reset() {
		getAppletImplementation().reset();
	}

	public void setAxesVisible(boolean xVisible, boolean yVisible) {
		getAppletImplementation().setAxesVisible(xVisible, yVisible);
	}

	public void setColor(String objName, int red, int green, int blue) {
		getAppletImplementation().setColor(objName, red, green, blue);
	}

	public void setCoordSystem(double xmin, double xmax, double ymin, double ymax) {
		getAppletImplementation().setCoordSystem(xmin, xmax, ymin, ymax);
	}

	public void setCoords(String objName, double x, double y) {
		getAppletImplementation().setCoords(objName, x, y);
	}

	public void setErrorDialogsActive(boolean flag) {
		getAppletImplementation().setErrorDialogsActive(flag);
	}

	public void setFixed(String objName, boolean flag) {
		getAppletImplementation().setFixed(objName, flag);
	}

	public void setGridVisible(boolean flag) {
		getAppletImplementation().setGridVisible(flag);
	}

	public void setLabelMode(String objName, boolean visible) {
		getAppletImplementation().setLabelMode(objName, visible);
	}

	public void setLabelStyle(String objName, int style) {
		getAppletImplementation().setLabelStyle(objName, style);
	}

	public void setLabelVisible(String objName, boolean visible) {
		getAppletImplementation().setLabelVisible(objName, visible);
	}

	public void setLayer(String objName, int layer) {
		getAppletImplementation().setLayer(objName, layer);
	}

	public void setLayerVisible(int layer, boolean visible) {
		getAppletImplementation().setLayerVisible(layer, visible);
	}

	public void setMode(int mode) {
		getAppletImplementation().setMode(mode);
	}

	public void setRepaintingActive(boolean flag) {
		getAppletImplementation().setRepaintingActive(flag);
	}

	public void setTrace(String objName, boolean flag) {
		getAppletImplementation().setTrace(objName, flag);
	}

	public void setValue(String objName, double x) {
		getAppletImplementation().setValue(objName, x);
	}

	public void setVisible(String objName, boolean visible) {
		getAppletImplementation().setVisible(objName, visible);
	}

	public void setXML(String xml) {
		getAppletImplementation().setXML(xml);
	}

	public void unregisterAddListener(String JSFunctionName) {
		getAppletImplementation().unregisterAddListener(JSFunctionName);
	}

	public void unregisterClearListener(String JSFunctionName) {
		getAppletImplementation().unregisterClearListener(JSFunctionName);
	}

	public void unregisterObjectUpdateListener(String objName) {
		getAppletImplementation().unregisterObjectUpdateListener(objName);
	}

	public void unregisterRemoveListener(String JSFunctionName) {
		getAppletImplementation().unregisterRemoveListener(JSFunctionName);
	}

	public void unregisterRenameListener(String JSFunctionName) {
		getAppletImplementation().unregisterRenameListener(JSFunctionName);
	}

	public void unregisterUpdateListener(String JSFunctionName) {
		getAppletImplementation().unregisterUpdateListener(JSFunctionName);
	}

}
