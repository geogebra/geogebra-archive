/* 
GeoGebra - Dynamic Mathematics for Everyone
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra;

import java.awt.Frame;
import java.awt.Toolkit;
import java.net.URL;
 
public class GeoGebra extends Object {
	
	// GeoGebra version
	public static final String BUILD_DATE = "September 29, 2009";
	public static final String VERSION_STRING = "3.3.25.0";
	//public static final String VERSION_STRING = "3D alpha";
		
	// File format versions
	public static final String XML_FILE_FORMAT = "3.3";
	public static final String I2G_FILE_FORMAT = "1.00.20080731";

	// URLs, license file
	public final static String GEOGEBRA_ONLINE_ARCHIVE = "http://www.geogebra.org/webstart/3.2/geogebra.jar";
	public final static String GEOGEBRA_WEBSITE = "http://www.geogebra.org/";
	
	// max possible heap space for applets in MB
	public final static int MAX_HEAP_SPACE = 512;
	
    public static void main(String[] args) {    	
    	  // Show splash screen
		  Frame splashFrame = null;
		  URL imageURL = GeoGebra.class.getResource("/geogebra/splash.gif");
		  if (imageURL != null) {
		      splashFrame = SplashWindow.splash(
		          Toolkit.getDefaultToolkit().createImage(imageURL)
		      );
		  } else {
		      System.err.println("Splash image not found");
		  }
		  
		  // Start GeoGebra
		  try {        			  		
			  startGeoGebra(args);                	
		  } catch (Throwable e) {
		      e.printStackTrace();
		      System.err.flush();
		      System.exit(10);
		  }
		  
		  // Hide splash screen
		  if (splashFrame != null) splashFrame.setVisible(false);    	
    }
    
    private static void startGeoGebra(String [] args) {
    	// load geogebra_main.jar and geogebra_gui.jar file
    	// they are needed to created the application window
    	JarManager jarManager = JarManager.getSingleton(false);
    	jarManager.addJarToClassPath(JarManager.JAR_FILE_GEOGEBRA_MAIN);      
    	jarManager.addJarToClassPath(JarManager.JAR_FILE_GEOGEBRA_GUI);
    	
    	// create and open first GeoGebra window        	
    	geogebra.gui.app.GeoGebraFrame.main(args);
    }
    
}