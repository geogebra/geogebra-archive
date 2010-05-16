/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra;

import geogebra.main.Application;

import java.awt.Frame;
import java.awt.Toolkit;
import java.net.URL;
 
public class GeoGebra extends Object {
	
	// GeoGebra version
	public static final String BUILD_DATE = "16 May 2010";
	public static final String VERSION_STRING = "3.9.5.0";
	public static final String SHORT_VERSION_STRING = "4.0"; // used for online archive

	// File format versions
	public static final String XML_FILE_FORMAT = "3.3";
	public static final String GGB_XSD_FILENAME = "ggb.xsd"; // for ggb files
	public static final String GGT_XSD_FILENAME = "ggt.xsd"; // for macro files 
	public static final String I2G_FILE_FORMAT = "0.1.20080731";
	
	// pre-releases and I2G
	public static final boolean IS_PRE_RELEASE = true; //!VERSION_STRING.endsWith(".0");
	public static final boolean DISABLE_I2G = !IS_PRE_RELEASE;	

	// URLs
	public final static String GEOGEBRA_ONLINE_ARCHIVE_BASE = "http://www.geogebra.org/webstart/" + SHORT_VERSION_STRING + "/";
	public static final String LOADING_GIF = "http://www.geogebra.org/webstart/loading.gif";
	public final static String GEOGEBRA_WEBSITE = "http://www.geogebra.org/";
	
	// max possible heap space for applets in MB
	public final static int MAX_HEAP_SPACE = 512;
	
    public static void main(String[] args) {  
    	
		Frame splashFrame = null;
    	boolean showSplash = true;
    	for (int i = 0 ; i < args.length ; i++) {
    		if (args[i].equals("--showSplash=false")) showSplash = false;
    		else if (args[i].equals("--help")) showSplash = false;
    	}
    	
    	if (showSplash) {
    	  // Show splash screen
		  URL imageURL = GeoGebra.class.getResource("/geogebra/splash.png");
		  if (imageURL != null) {
		      splashFrame = SplashWindow.splash(
		          Toolkit.getDefaultToolkit().createImage(imageURL)
		      );
		  } else {
		      System.err.println("Splash image not found");
		  }
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
    	// create and open first GeoGebra window        	
    	geogebra.gui.app.GeoGebraFrame.main(args);
    }
    
}