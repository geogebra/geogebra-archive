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
package geogebra.gui.app;

import geogebra.CommandLineArguments;
import geogebra.euclidian.Drawable;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.kernel.Macro;
import geogebra.main.Application;
import geogebra.main.GeoGebraPreferences;
import geogebra.util.Util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * GeoGebra's main window. 
 */
public class GeoGebraFrame extends JFrame implements WindowFocusListener
{
	
	private static final int DEFAULT_WIDTH = 900;
	private static final int DEFAULT_HEIGHT = 650;

	private static final long serialVersionUID = 1L;

	private static ArrayList<GeoGebraFrame> instances = new ArrayList<GeoGebraFrame>();
	private static GeoGebraFrame activeInstance;
	protected Application app;
	
	public GeoGebraFrame() {
		instances.add(this);
		activeInstance = this;			
		
	}
	
//	public static void printInstances() {
//		System.out.println("FRAMES: " + instances.size()); 
//		for (int i=0; i < instances.size(); i++) {
//			GeoGebraFrame frame = (GeoGebraFrame) instances.get(i);
//			System.out.println(" " + (i+1) + ", applet: " + frame.app.isApplet() + ", "
//					+ frame); 
//		}				
//	}
	
	/**
	 * Disposes this frame and removes it from the static instance list.
	 */
	public void dispose() {
		instances.remove(this);
		if (this == activeInstance) 
			activeInstance = null;
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
		Locale defLocale = GeoGebraPreferences.getPref().getDefaultLocale();		
		
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
				GeoGebraFrame prevInstance = getInstance(instanceID - 1);
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
			GeoGebraPreferences.getPref().saveFileList();
			
			if (instances.size() == 0) {				
				super.setVisible(false);
				dispose();
				
				if (!app.isApplet()) {
					System.exit(0);
				}
			} else {
				super.setVisible(false);
				updateAllTitles();
			}		
		}		
	}
	
	public void updateSize() {
		// get frame size from layout manager
		Dimension size = app.getPreferredSize();
		
		// check if frame fits on screen
		Rectangle screenSize = app.getScreenSize();

		if (size.width > screenSize.width || 
				size.height > screenSize.height) {
			size.width = screenSize.width;
			size.height = screenSize.height;
			setLocation(0,0);
		} 
				
		setSize(size);
	}

	/** 
	 * Main method to create inital GeoGebra window.
	 * @param args: file name parameter
	 */
	public static synchronized void main(CommandLineArguments args) {		

		init(args,new GeoGebraFrame());
	}	
	
	public static synchronized void init(CommandLineArguments args, GeoGebraFrame wnd){
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
		    	
     	if (Application.MAC_OS) 
    		initMacSpecifics();
				
    	// set system look and feel
		try {		
			if (Application.MAC_OS || Application.WINDOWS)
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			else // Linux or others
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			Application.debug(e+"");
		}

		// Set GeoGebraPreferences mode (system properties or property file)
		// before it is called for the first time
		String settingsFile = args.getStringValue("settingsfile");
		if(settingsFile.length() > 0) {
			GeoGebraPreferences.setPropertyFileName(settingsFile);
		}
    	
		// load list of previously used files
		GeoGebraPreferences.getPref().loadFileList();	
    	    			
		// create first window and show it		
		createNewWindow(args,wnd);	
	}
	
	/**
	 * Returns the active GeoGebra window.
	 */
	public static synchronized GeoGebraFrame getActiveInstance() {
		return activeInstance;
	}

	/**
	 * MacOS X specific initing. Note: this method can only be run
	 * on a Mac!
	 */
	public static void initMacSpecifics() {
		try {
			// init mac application listener
			MacApplicationListener.initMacApplicationListener();
	
			//mac menu bar	
		    //System.setProperty("com.apple.macos.useScreenMenuBar", "true"); 	
		    System.setProperty("apple.laf.useScreenMenuBar", "true"); 	
		} catch (Exception e) {
			Application.debug(e+"");
		}
	}
	
	
	public static synchronized GeoGebraFrame createNewWindow(CommandLineArguments args){
		return createNewWindow(args,new GeoGebraFrame());
	}
	
	/**
	 * Creates new GeoGebra window
	 * @param args Command line arguments
	 * @return the new window
	 */
	//public abstract GeoGebra buildGeoGebra();
	public static synchronized GeoGebraFrame createNewWindow(CommandLineArguments args,GeoGebraFrame wnd){
		return createNewWindow(args,null,wnd);
	}
	
	
	/**
	 * return the application running geogebra
	 * @param args
	 * @param frame
	 * @return the application running geogebra
	 */
	protected Application createApplication(CommandLineArguments args, JFrame frame){		
		return new Application(args, frame, true);
	}
	
	
	public static synchronized GeoGebraFrame createNewWindow(CommandLineArguments args,Macro macro) {	
		return createNewWindow(args,macro,new GeoGebraFrame());
	}
	
	/**
	 * Creates new GeoGebra window
	 * @param args Command line arguments
	 * @param macro Macro to open (or null for file edit mode)
	 * @return the new window
	 */
	public static synchronized GeoGebraFrame createNewWindow(CommandLineArguments args,Macro macro,GeoGebraFrame wnd) {				
		// set Application's size, position and font size
		// TODO Add layout glass pane (F.S.)
		
		
		Application app = wnd.createApplication(args,wnd);//new Application(args, wnd, true);		
		
		if(macro!=null)app.openMacro(macro);
		//app.getApplicationGUImanager().setMenubar(new geogebra.gui.menubar.GeoGebraMenuBar(app));
		app.getGuiManager().initMenubar();
		
		// init GUI
		wnd.app = app;
		wnd.getContentPane().add(app.buildApplicationPanel());
		wnd.setDropTarget(new DropTarget(wnd, new geogebra.gui.FileDropTargetListener(app)));			
		wnd.addWindowFocusListener(wnd);
		
		updateAllTitles();		
		wnd.setVisible(true);
		
		// init some things in the background
		if (!app.isApplet())
		{
			/*
			Thread runner = new Thread() {
				public void run() {											
					// init properties dialog
					app.getGuiManager().initPropertiesDialog();		
					
					// init file chooser
					app.getGuiManager().initFileChooser();	
					
					// init CAS
					app.getKernel().getGeoGebraCAS();
					
					// init JLaTeXMath
					Graphics2D g2d = app.getEuclidianView().g2Dtemp;
					Drawable.drawEquation(app, app.getEuclidianView().g2Dtemp, 0, 0, "x^{2}", g2d.getFont(), false, Color.BLACK, Color.WHITE);
				}
			};
			*/
			Thread runner = wnd.createAppThread(app);
			runner.start();
		}
				
		return wnd;
	}
	
	private AppThread createAppThread(Application app){
		return new AppThread(app);
	}
	
	private class AppThread extends Thread{
		
		Application app;
		
		public AppThread(Application app){
			this.app=app;	
		}
		
		public void run() {											
			// init properties dialog
			this.app.getGuiManager().initPropertiesDialog();		
			
			// init file chooser
			this.app.getGuiManager().initFileChooser();	
			
			// init CAS
			this.app.getKernel().getGeoGebraCAS();
			
			// init JLaTeXMath
			Graphics2D g2d = this.app.getEuclidianView().g2Dtemp;
			Drawable.drawEquation(this.app, this.app.getEuclidianView().g2Dtemp, 0, 0, "x^{2}", g2d.getFont(), false, Color.BLACK, Color.WHITE);
		}
	}

	public static int getInstanceCount() {
		return instances.size();
	}

	public static ArrayList<GeoGebraFrame> getInstances() {
		return instances;
	}

	static GeoGebraFrame getInstance(int i) {
		return (GeoGebraFrame) instances.get(i);
	}

	public static void updateAllTitles() {
		for (int i = 0; i < instances.size(); i++) {
			Application app = ((GeoGebraFrame) instances.get(i)).app;
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
	public static GeoGebraFrame getInstanceWithFile(File file) {
		if (file == null)
			return null;

		try {
			String absPath = file.getCanonicalPath();
			for (int i = 0; i < instances.size(); i++) {
				GeoGebraFrame inst = (GeoGebraFrame) instances.get(i);
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

	public boolean isIconified() {
		return getExtendedState() == JFrame.ICONIFIED;		
	}
	
	

}