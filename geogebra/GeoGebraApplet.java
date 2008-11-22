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
import javax.swing.SwingUtilities;

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
	private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN,
			11);

	// applet member variables
	private JavaScriptAPI appletImplementation = null;
	private JarManager jarManager;
	private int width, height;
	private boolean animationRunningAtLastStop = false;

	// splash screen stuff
	private Image splashImage, progressImage;
	private Image splashScreenImage;
	private Graphics splashScreenImageGraphics;

	private boolean appletIniting = true;

	/**
	 * Loads necessary jar files and initializes applet. During the loading of
	 * jar files, a splash screen with progress information is shown.
	 */
	public void init() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {

					// start initing application
					Thread runner = new Thread() {
						public void run() {
							getAppletImplementation();
						}
					};
					runner.start();

					// init splash screen
					initSplashScreen();

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		// restart animation if necessary
		if (animationRunningAtLastStop) {
			appletImplementation.startAnimation();
		}

		repaint();
		System.gc();
	}

	public void stop() {
		// stop animation and remember that it needs to be restarted later
		animationRunningAtLastStop = appletImplementation.isAnimationRunning();
		if (animationRunningAtLastStop) {
			appletImplementation.stopAnimation();
		}

		System.gc();
	}

	public void destroy() {
		// stop animation
		appletImplementation.stopAnimation();
		appletImplementation = null;
		System.gc();
	}

	/**
	 * Returns the appletImplementation object.
	 */
	private synchronized JavaScriptAPI getAppletImplementation() {
		if (appletIniting) {
			initAppletImplementation();
		}

		return appletImplementation;
	}

	/**
	 * Initializes the appletImplementation object. Loads geogebra_main.jar file
	 * and initializes applet if necessary.
	 */
	private synchronized void initAppletImplementation() {
		if (!appletIniting) return;
		
		// init jar manager to load jar files for applet
		jarManager = JarManager.getSingleton(true);

		// load geogebra_main.jar file
		jarManager.addJarToClassPath(JarManager.JAR_FILE_GEOGEBRA_MAIN);

		// create delegate object that implements our applet's methods
		geogebra.main.DefaultApplet applImpl = new geogebra.main.DefaultApplet(
				this);

		// initialize applet's user interface, this changes the content pane
		applImpl.initGUI();// update applet GUI, see paint()
		validate();

		// remember the applet implementation
		appletImplementation = applImpl;
		repaint();
		
		// load all jar files in background and init dialogs
		applImpl.getApplication().initInBackground();

		// clear splash images
		splashScreenImage = null;
		splashScreenImageGraphics = null;
		splashImage = null;
		progressImage = null;
		System.gc();

		// applet initing finished
		appletIniting = false;
		
		// notify JavaScript that the applet is initialized.
		notifyAppletInitialized();
	}

	/**
	 * Paints the applet or a loading screen while the applet is being
	 * initialized.
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
		progressImage = getImage(GeoGebraApplet.class
				.getResource("progress.gif"));

		// update splash screen image and paint it
		updateSplashScreenImage();
	}

	/**
	 * Paints a loading screen to show progress with downloading jar files.
	 */
	private synchronized void updateSplashScreenImage() {
		if (splashScreenImageGraphics == null)
			return;

		Graphics2D g = (Graphics2D) splashScreenImageGraphics;

		// white background
		g.setColor(Color.white);
		g.clearRect(0, 0, width, height);

		// splash image position
		int splashX = -1;
		int splashY = -1;
		if (splashImage != null) {
			splashX = (width - SPLASH_IMAGE_WIDTH) / 2;
			splashY = (height - SPLASH_IMAGE_HEIGHT) / 2
					- (int) (1.5 * PROGRESS_IMAGE_HEIGHT);
		}

		// progress image position
		int progressX = (width - PROGRESS_IMAGE_WIDTH) / 2;
		int progressY = (height - PROGRESS_IMAGE_HEIGHT) / 2;

		// Splash image fits into content pane: draw splash image
		if (splashX >= 0 && splashY >= 0) {
			g.drawImage(splashImage, splashX, splashY, this);

			// put progress image below splash image
			progressY = splashY + SPLASH_IMAGE_HEIGHT;
		}

		// draw progress image
		g.drawImage(progressImage, progressX, progressY, this);

		// draw status message of JarManager below progress image
		if (jarManager != null) {
			String statusMessage = jarManager.getDownloadStatusMessage();

			if (statusMessage != null) {
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.setColor(Color.darkGray);
				g.setFont(DEFAULT_FONT);
				g.drawString(statusMessage, width / 2
						- (int) (2.5 * statusMessage.length()), progressY
						+ PROGRESS_IMAGE_HEIGHT + 2 * DEFAULT_FONT.getSize());
			}
		}

		repaint();
	}

	/**
	 * Updates the progress image (animated gif) during appletImplementation
	 * loading. Overrides ImageObserver.
	 */
	public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
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

	public synchronized void deleteObject(String objName) {
		getAppletImplementation().deleteObject(objName);
	}

	public synchronized boolean evalCommand(String cmdString) {
		return getAppletImplementation().evalCommand(cmdString);
	}

	public synchronized void evalXML(String xmlString) {
		getAppletImplementation().evalXML(xmlString);
	}

	public synchronized String evalYacas(String cmdString) {
		return getAppletImplementation().evalYacas(cmdString);
	}

	public synchronized boolean exists(String objName) {
		return getAppletImplementation().exists(objName);
	}

	public synchronized String[] getAllObjectNames() {
		return getAppletImplementation().getAllObjectNames();
	}

	public synchronized String getColor(String objName) {
		return getAppletImplementation().getColor(objName);
	}

	public synchronized String getCommandString(String objName) {
		return getAppletImplementation().getCommandString(objName);
	}

	public synchronized String getDefinitionString(String objName) {
		return getAppletImplementation().getDefinitionString(objName);
	}

	public synchronized byte[] getGGBfile() {
		return getAppletImplementation().getGGBfile();
	}

	public synchronized String getHostname() {
		return getAppletImplementation().getHostname();
	}

	public synchronized String getIPAddress() {
		return getAppletImplementation().getIPAddress();
	}

	public synchronized int getLayer(String objName) {
		return getAppletImplementation().getLayer(objName);
	}

	public synchronized String getObjectName(int i) {
		return getAppletImplementation().getObjectName(i);
	}

	public synchronized int getObjectNumber() {
		return getAppletImplementation().getObjectNumber();
	}

	public synchronized String getObjectType(String objName) {
		return getAppletImplementation().getObjectType(objName);
	}

	public synchronized double getValue(String objName) {
		return getAppletImplementation().getValue(objName);
	}

	public synchronized String getValueString(String objName) {
		return getAppletImplementation().getValueString(objName);
	}

	public synchronized String getXML() {
		return getAppletImplementation().getXML();
	}

	public synchronized String getXML(String objName) {
		return getAppletImplementation().getXML(objName);
	}

	public synchronized String getAlgorithmXML(String objName) {
		return getAppletImplementation().getAlgorithmXML(objName);
	}

	public synchronized double getXcoord(String objName) {
		return getAppletImplementation().getXcoord(objName);
	}

	public synchronized double getYcoord(String objName) {
		return getAppletImplementation().getYcoord(objName);
	}

	public synchronized boolean isDefined(String objName) {
		return getAppletImplementation().isDefined(objName);
	}

	public synchronized void openFile(String strURL) {
		getAppletImplementation().openFile(strURL);
	}

	public synchronized void refreshViews() {
		getAppletImplementation().refreshViews();
	}

	public synchronized void registerAddListener(String JSFunctionName) {
		getAppletImplementation().registerAddListener(JSFunctionName);
	}

	public synchronized void registerClearListener(String JSFunctionName) {
		getAppletImplementation().registerClearListener(JSFunctionName);
	}

	public synchronized void registerObjectUpdateListener(String objName,
			String JSFunctionName) {
		getAppletImplementation().registerObjectUpdateListener(objName,
				JSFunctionName);
	}

	public synchronized void registerRemoveListener(String JSFunctionName) {
		getAppletImplementation().registerRemoveListener(JSFunctionName);
	}

	public synchronized void registerRenameListener(String JSFunctionName) {
		getAppletImplementation().registerRenameListener(JSFunctionName);
	}

	public synchronized void registerUpdateListener(String JSFunctionName) {
		getAppletImplementation().registerUpdateListener(JSFunctionName);
	}

	public synchronized boolean renameObject(String oldObjName,
			String newObjName) {
		return getAppletImplementation().renameObject(oldObjName, newObjName);
	}
	public synchronized void setAnimating(String objName, boolean animate) {
		getAppletImplementation().setAnimating(objName, animate);
	}
	
	public synchronized void setAnimationSpeed(String objName, double speed) {
		getAppletImplementation().setAnimationSpeed(objName, speed);
	}

	public synchronized void startAnimation() {
		getAppletImplementation().startAnimation();
	}

	public synchronized void stopAnimation() {
		getAppletImplementation().stopAnimation();
	}

	public synchronized boolean isAnimationRunning() {
		return getAppletImplementation().isAnimationRunning();
	}

	public synchronized void reset() {
		getAppletImplementation().reset();
	}

	public synchronized void setAxesVisible(boolean xVisible, boolean yVisible) {
		getAppletImplementation().setAxesVisible(xVisible, yVisible);
	}

	public synchronized void setColor(String objName, int red, int green,
			int blue) {
		getAppletImplementation().setColor(objName, red, green, blue);
	}

	public synchronized void setCoordSystem(double xmin, double xmax,
			double ymin, double ymax) {
		getAppletImplementation().setCoordSystem(xmin, xmax, ymin, ymax);
	}

	public synchronized void setCoords(String objName, double x, double y) {
		getAppletImplementation().setCoords(objName, x, y);
	}

	public synchronized void setErrorDialogsActive(boolean flag) {
		getAppletImplementation().setErrorDialogsActive(flag);
	}

	public synchronized void setFixed(String objName, boolean flag) {
		getAppletImplementation().setFixed(objName, flag);
	}

	public synchronized void setGridVisible(boolean flag) {
		getAppletImplementation().setGridVisible(flag);
	}

	public synchronized void setLabelMode(String objName, boolean visible) {
		getAppletImplementation().setLabelMode(objName, visible);
	}

	public synchronized void setLabelStyle(String objName, int style) {
		getAppletImplementation().setLabelStyle(objName, style);
	}

	public synchronized void setLabelVisible(String objName, boolean visible) {
		getAppletImplementation().setLabelVisible(objName, visible);
	}

	public synchronized void setLayer(String objName, int layer) {
		getAppletImplementation().setLayer(objName, layer);
	}

	public synchronized void setLayerVisible(int layer, boolean visible) {
		getAppletImplementation().setLayerVisible(layer, visible);
	}

	public synchronized void setMode(int mode) {
		getAppletImplementation().setMode(mode);
	}

	public synchronized void setRepaintingActive(boolean flag) {
		getAppletImplementation().setRepaintingActive(flag);
	}

	public synchronized void setTrace(String objName, boolean flag) {
		getAppletImplementation().setTrace(objName, flag);
	}

	public synchronized void setValue(String objName, double x) {
		getAppletImplementation().setValue(objName, x);
	}

	public synchronized void setVisible(String objName, boolean visible) {
		getAppletImplementation().setVisible(objName, visible);
	}

	public synchronized void setXML(String xml) {
		getAppletImplementation().setXML(xml);
	}

	public synchronized void unregisterAddListener(String JSFunctionName) {
		getAppletImplementation().unregisterAddListener(JSFunctionName);
	}

	public synchronized void unregisterClearListener(String JSFunctionName) {
		getAppletImplementation().unregisterClearListener(JSFunctionName);
	}

	public synchronized void unregisterObjectUpdateListener(String objName) {
		getAppletImplementation().unregisterObjectUpdateListener(objName);
	}

	public synchronized void unregisterRemoveListener(String JSFunctionName) {
		getAppletImplementation().unregisterRemoveListener(JSFunctionName);
	}

	public synchronized void unregisterRenameListener(String JSFunctionName) {
		getAppletImplementation().unregisterRenameListener(JSFunctionName);
	}

	public synchronized void unregisterUpdateListener(String JSFunctionName) {
		getAppletImplementation().unregisterUpdateListener(JSFunctionName);
	}
	
	/**
	 * Calls a JavaScript function when the applet is initialized.
	 */
	public void notifyAppletInitialized() {
		getAppletImplementation().notifyAppletInitialized();
	}

}
