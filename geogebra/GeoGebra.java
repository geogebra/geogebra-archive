/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License v2 as published by 
 the Free Software Foundation.
 
 */

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
package geogebra;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.FileDropTargetListener;
import geogebra.gui.GeoGebraPreferences;
import geogebra.util.Util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * GeoGebra's main window. 
 */
public class GeoGebra extends JFrame implements WindowFocusListener {
	
	private static final int DEFAULT_WIDTH = 900;
	private static final int DEFAULT_HEIGHT = 650;

	private static final long serialVersionUID = 1L;

	private static ArrayList instances = new ArrayList();
	private static GeoGebra activeInstance;
	private Application app;
	
	// check if we are on a mac
	public static boolean MAC_OS = System.getProperty("os.name").toLowerCase().startsWith("mac");

	public GeoGebra() {
		instances.add(this);
		activeInstance = this;				
	}
	

	public Application getApplication() {
		return app;
	}

	public void setApplication(Application app) {
		this.app = app;
	}

	public int getInstanceNumber() {
		for (int i = 0; i < instances.size(); i++) {
			if (this == instances.get(i))
				return i;
		}
		return -1;
	}

	public void windowGainedFocus(WindowEvent arg0) {
		activeInstance = this;		
		app.updateMenuWindow();		
	}

	public void windowLostFocus(WindowEvent arg0) {	
	}
	
	public Locale getLocale() {				
		Locale defLocale = GeoGebraPreferences.getDefaultLocale();		
		
		if (defLocale == null)
			return super.getLocale();
		else 
			return defLocale;
	}

	public void setVisible(boolean flag) {				
		if (flag) {						
			updateSize();									
			
			// set location
			int instanceID = instances.size() - 1;
			if (instanceID > 0) {
				// move right and down of last instance		
				GeoGebra prevInstance = getInstance(instanceID - 1);
				Point loc = prevInstance.getLocation();				
				
				// make sure we stay on screen
				Dimension d1 = getSize();	
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				loc.x = Math.min(loc.x + 20, dim.width - d1.width);
				loc.y = Math.min(loc.y + 20, dim.height - d1.height - 25);									
				setLocation(loc);
			} else {
				// center
				setLocationRelativeTo(null);
			}
			
			super.setVisible(true);	
			app.getEuclidianView().requestFocusInWindow();
		}
		else {	
			if (!isShowing()) return;
			
			instances.remove(this);
			GeoGebraPreferences.saveFileList();
			
			if (instances.size() == 0) {			
				super.setVisible(false);
				dispose();
				
				if (!app.isApplet())
					System.exit(0);
			} else {
				super.setVisible(false);
				updateAllTitles();
			}		
		}		
	}
	
	public void updateSize() {		
		Dimension frameSize;
		
		// use euclidian view pref size to set frame size 		
		EuclidianView ev = app.getEuclidianView();		

		// no preferred size
		if (ev.hasPreferredSize()) {
			ev.setMinimumSize(new Dimension(50,50));
			Dimension evPref = ev.getPreferredSize();						
			ev.setPreferredSize(evPref);
		
			// pack frame and correct size to really get the preferred size for euclidian view
			pack(); 
			frameSize = getSize();
			Dimension evSize = ev.getSize();
			frameSize.width = frameSize.width + (evPref.width - evSize.width);
			frameSize.height = frameSize.height + (evPref.height - evSize.height);					
		} else {
			frameSize = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);			
		}
		
		// check if frame fits on screen

		// Michael Borcherds 2007-11-22 BEGIN
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle screenSize = env.getMaximumWindowBounds();

		if (frameSize.width > screenSize.width || 
				frameSize.height > screenSize.height) {
			frameSize.width = screenSize.width;
			frameSize.height = screenSize.height;
			setLocation(0,0);
		} 		
		
/*		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();		
		screenSize.height -= 30; // task bar		
		if (frameSize.width > screenSize.width || 
				frameSize.height > screenSize.height) {
			frameSize = screenSize;
			setLocation(0,0);
		} 		*/
		// Michael Borcherds 2007-11-22 END
				
		setSize(frameSize);
	}

	/** 
	 * Main method to create inital GeoGebra window.
	 * @param args: file name parameter
	 */
	public static synchronized void main(String[] args) {		
		// check java version
		double javaVersion = Util.getJavaVersion();
		if (javaVersion < 1.42) {
			JOptionPane
					.showMessageDialog(
							null,
							"Sorry, GeoGebra cannot be used with your Java version "
									+ javaVersion
									+ "\nPlease visit http://www.java.com to get a newer version of Java.");
			return;
		}
		    	
     	if (MAC_OS) 
    		initMacSpecifics();
				
    	// set system look and feel
		try {							
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println(e);
		}	
    	    
		// load list of previously used files
		GeoGebraPreferences.loadFileList();
		
		// create first window and show it
		createNewWindow(args);
	}	
	
	/**
	 * Returns the active GeoGebra window.
	 */
	public static synchronized GeoGebra getActiveInstance() {
		return activeInstance;
	}

	/**
	 * MacOS X specific initing. Note: this method can only be run
	 * on a Mac!
	 */
	public static void initMacSpecifics() {
		try {
			com.apple.eawt.Application app = new com.apple.eawt.Application();
			app.addApplicationListener(new MacApplicationListener());
	
			//mac menu bar	
		    //System.setProperty("com.apple.macos.useScreenMenuBar", "true"); 	
		    System.setProperty("apple.laf.useScreenMenuBar", "true"); 	
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	
	

	public static synchronized GeoGebra createNewWindow(String[] args) {				
		// set Application's size, position and font size
		GeoGebra wnd = new GeoGebra();
		Application app = new Application(args, wnd, true);		
		
		// init GUI
		wnd.app = app;
		wnd.getContentPane().add(app.buildApplicationPanel());					
		wnd.setDropTarget(new DropTarget(wnd, new FileDropTargetListener(app)));			
		wnd.addWindowFocusListener(wnd);
		updateAllTitles();
		app.initInBackground();		
		
		wnd.setVisible(true);
		return wnd;
	}

	public static int getInstanceCount() {
		return instances.size();
	}

	public static ArrayList getInstances() {
		return instances;
	}

	static GeoGebra getInstance(int i) {
		return (GeoGebra) instances.get(i);
	}

	public static void updateAllTitles() {
		for (int i = 0; i < instances.size(); i++) {
			Application app = ((GeoGebra) instances.get(i)).app;
			app.updateTitle();
		}
	}

	/**
	 * Checks all opened GeoGebra instances if their current file is the given
	 * file.
	 * 
	 * @param file
	 * @return GeoGebra instance with file open or null
	 */
	public static GeoGebra getInstanceWithFile(File file) {
		if (file == null)
			return null;

		try {
			String absPath = file.getCanonicalPath();
			for (int i = 0; i < instances.size(); i++) {
				GeoGebra inst = (GeoGebra) instances.get(i);
				Application app = inst.app;
	
				File currFile = app.getCurrentFile();
				if (currFile != null) {
					if (absPath.equals(currFile.getCanonicalPath()))
						return inst;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	

}