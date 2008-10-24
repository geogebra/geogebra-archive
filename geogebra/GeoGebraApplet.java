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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import javax.swing.JApplet;

/**
 * GeoGebra applet
 * 
 * @see geogebra.main.AppletImplementation for the actual implementation
 * @author Markus Hohenwarter
 * @date 2008-10-23
 */
public class GeoGebraApplet extends JApplet implements JavaScriptAPI {

	private static final long serialVersionUID = -350682076336303151L;
	
	private JavaScriptAPI appletImplementation = null;	
	private Image splashImage;
	
	/**
	 * Loads necessary jar files and initializes applet. During the loading
	 * of jar files, an "is loading" message is shown
	 */
	public void init() {   
		splashImage = getImageResource("splash.gif");
		
        // initialize applet GUI
        SwingWorker worker = new SwingWorker() {
			public Object construct() {				
				// TODO: remove
            	System.out.println("initing...");
				initAppletImplementation();
				return null;
			}        	
        };
        worker.start();        
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
	private synchronized void initAppletImplementation() {
		// load geogebra_main.jar file
        JarManager jarManager = JarManager.getSingleton(true);
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
	}
	
	
	/**
     * Paints the applet or a loading screen while the applet is being initialized.
     */
    final public void paint(Graphics g) {
    	if (appletImplementation == null) 
    		paintLoadingScreen(g);
    	else
    		super.paint(g);		    	                 
    }

    /**
     * Paints a loading screen to show progress with downloading jar files.
     */
    private void paintLoadingScreen(Graphics g) {
    	int width = getWidth();
    	int height = getHeight();
    	
    	// white background
    	g.setColor(Color.white);
    	g.clearRect(0, 0, width, height);
    	    	
    	// splash image position
    	int x = -1;
    	int y = -1;    	    	
    	if (splashImage != null) {
    		x = (width - splashImage.getWidth(null)) / 2;
    		y = (height - splashImage.getHeight(null)) / 2;
    	}
    	
    	//splash image fits into content pane
    	if (x >=0 && y >=0 ) {
    		// draw image
    		g.drawImage(splashImage, x, y, null);
    	} else {
    		// text
    		g.setColor(Color.black);
    		g.drawString("GeoGebra ...", width/2-10, height/2-5);
    	}
    }
    	
    
	private Image getImageResource(String name) {
		Toolkit toolKit = Toolkit.getDefaultToolkit();
 		MediaTracker tracker = new MediaTracker(this);
    	
   		 Image img = null;
   		 try {
   		    java.net.URL url = GeoGebraApplet.class.getResource(name);	
   		    if (url != null) {		   
   				img = toolKit.getImage(url);	
   				tracker.addImage(img, 0);
   				try {
   				   tracker.waitForAll();
   				} catch (InterruptedException e) {
   					System.err.println("Interrupted while loading Image: " + name);
   				}
   				tracker.removeImage(img);
   			}			   
   		 } catch (Exception e) {
   			System.err.println(e.toString());
   		 }
   		 if (img == null) 
   			 System.err.println("Image " + name + " not found");
   		 return img;
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

	public void openFileNoThread(String strURL) {
		getAppletImplementation().openFileNoThread(strURL);
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

	public void resetNoThread() {
		getAppletImplementation().resetNoThread();
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
