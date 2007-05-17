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
package geogebra.gui;

import geogebra.Application;
import geogebra.euclidian.EuclidianView;
import geogebra.util.Util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class GeoGebra extends JFrame implements WindowFocusListener {
	
	private static final int DEFAULT_WIDTH = 900;
	private static final int DEFAULT_HEIGHT = 650;

	private static final long serialVersionUID = 1L;

	private static ArrayList instances = new ArrayList();

	private static GeoGebra activeInstance;

	private Application app;

	public GeoGebra() {
		instances.add(this);
		activeInstance = this;				
	}

	public void dispose() {
		instances.remove(this);
		GeoGebraPreferences.saveFileList();
		
		if (instances.size() == 0) {			
			super.dispose();
			System.exit(0);
		} else {
			super.dispose();
			updateAllTitles();
		}				
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
				loc.x += 20;
				loc.y += 20;
				setLocation(loc);
			} else {
				// center
				setLocationRelativeTo(null);
			}								

		}
				
		super.setVisible(flag);
	}
	
	public void updateSize() {
		// use euclidian view pref size to set frame size 
		EuclidianView ev = app.getEuclidianView();										
		
		// no preferred size
		if (ev.hasPreferredSize()) {
			ev.setMinimumSize(new Dimension(50,50));
			Dimension evPref = ev.getPreferredSize();						
			ev.setPreferredSize(evPref);
							
			// pack frame and correct size to really get the preferred size for euclidian view
			pack(); 
			Dimension evSize = ev.getSize();
			Dimension frameSize = getSize();
			frameSize.width = frameSize.width + (evPref.width - evSize.width);
			frameSize.height = frameSize.height + (evPref.height - evSize.height);
			
			// check if frame fits on screen
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			if (frameSize.width > screenSize.width || 
					frameSize.height > screenSize.height) {
				setSize(screenSize);
				setLocation(0,0);
			} else {
				// everything ok
				setSize(frameSize);
			}
		} else {
			setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);			
		}				
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println(e);
		}

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
		
		// load list of previously used files
		GeoGebraPreferences.loadFileList();		

		// create window
		GeoGebra wnd = createNewWindow(args);

		// check if we run on a Mac
		String lcOSName = System.getProperty("os.name").toLowerCase();
		boolean MAC_OS = lcOSName.startsWith("mac");

		if (MAC_OS) {
			// handle MacOS X file opening when ggb file is double clicked
			net.roydesign.app.Application.getInstance()
					.addOpenDocumentListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							net.roydesign.event.ApplicationEvent mac_evt = (net.roydesign.event.ApplicationEvent) evt;
							
							// open file in new window												
							Application app = activeInstance.getApplication();
							File [] files = { mac_evt.getFile() };
							app.doOpenFiles(files, false);
						}
					});
		}

		// show window
		wnd.setVisible(true);
	}	

	public static GeoGebra createNewWindow(String[] args) {
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