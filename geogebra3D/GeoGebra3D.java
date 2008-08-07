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
package geogebra3D;

import geogebra.Application;
import geogebra.GeoGebra;
import geogebra.gui.FileDropTargetListener;
import geogebra.gui.GeoGebraPreferences;
import geogebra.gui.menubar.GeoGebraMenuBar;
import geogebra.util.Util;

import java.awt.dnd.DropTarget;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * GeoGebra3D's main window. 
 */
public class GeoGebra3D extends GeoGebra
{

	private static final long serialVersionUID = 1L;
	

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
		    	
     	if (Application.MAC_OS) 
    		initMacSpecifics();
				
    	// set system look and feel
		try {							
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Application.debug(e+"");
		}	
    	    
		// load list of previously used files
		GeoGebraPreferences.loadFileList();
		
		// create first window and show it
		
		createNewWindow(args);
	}	
	
	
	

	public static synchronized GeoGebra createNewWindow(String[] args) {				
		// set Application's size, position and font size
		GeoGebra3D wnd = new GeoGebra3D();
		
		//GeoGebra wnd = buildGeoGebra();

		Application3D app = new GeoGebraApplication3D(args, wnd, true); //ggb3D 
		app.setMenubar(new GeoGebraMenuBar(app));
		app.initMenubar();
		
		// init GUI
		wnd.app = (Application) app;
		wnd.getContentPane().add(app.buildApplicationPanel());
		wnd.setDropTarget(new DropTarget(wnd, new FileDropTargetListener(app)));			
		wnd.addWindowFocusListener(wnd);
		
		updateAllTitles();
		app.initInBackground();		
		
		wnd.setVisible(true);
		return wnd;
	}

	

}