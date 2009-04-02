/* 
GeoGebra - Dynamic Mathematics for Everyone
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra;
 
public class GeoGebra extends Object {
	
	// GeoGebra version
	public static final String BUILD_DATE = "April 2, 2009";
	public static final String VERSION_STRING = "3.3.11.0";
	
	// File format versions
	public static final String XML_FILE_FORMAT = "3.3";
	public static final String I2G_FILE_FORMAT = "1.00.20080731";
	
	
    public static void main(String[] args) {
    	// load geogebra_main.jar and geogebra_gui.jar file
    	// they are needed to created the application window
    	JarManager jarManager = JarManager.getSingleton(false);
    	jarManager.addJarToClassPath(JarManager.JAR_FILE_GEOGEBRA_MAIN);      
    	jarManager.addJarToClassPath(JarManager.JAR_FILE_GEOGEBRA_GUI);
    	
    	// create and open first GeoGebra window        	
    	geogebra.gui.app.GeoGebraFrame.main(args); 
    }
    
}