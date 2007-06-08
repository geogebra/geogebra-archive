/* 
 GeoGebra - Dynamic Geometry and Algebra
 Copyright Markus Hohenwarter, http://www.geogebra.at

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation; either version 2 of the License, or 
 (at your option) any later version.
 */

package geogebra.gui;

import geogebra.Application;

import java.io.File;
import java.util.Locale;
import java.util.prefs.Preferences;

/**
 * Stores user settings and options as preferences.
 *  
 * @author Markus Hohenwarter
 * @date May 16, 2007
 */
public class GeoGebraPreferences {		
	
	public static final String AUTHOR = "author";
	
	// worksheet export dialog
	public static final String EXPORT_WS_RIGHT_CLICK = "export_ws_right_click";
	public static final String EXPORT_WS_RESET_ICON = "export_ws_reset_icon";
	public static final String EXPORT_WS_FRAME_POSSIBLE = "export_ws_frame_possible";
	public static final String EXPORT_WS_SHOW_MENUBAR = "export_ws_show_menubar";
	public static final String EXPORT_WS_SHOW_TOOLBAR = "export_ws_show_toolbar";
	public static final String EXPORT_WS_SHOW_TOOLBAR_HELP = "export_ws_show_toolbar_help";
	public static final String EXPORT_WS_SHOW_INPUT_FIELD = "export_ws_show_input_field";
	
	// picture export dialog
	public static final String EXPORT_PIC_FORMAT = "export_pic_format";
	public static final String EXPORT_PIC_DPI = "export_pic_dpi";
	// public static final String EXPORT_PIC_SCALE = "export_pic_scale";
	
	// print preview dialog
	public static final String PRINT_ORIENTATION = "print_orientation";
	public static final String PRINT_SHOW_SCALE = "print_show_scale";		
	
	
	 // preferences node name for GeoGebra 	 
	 private static Preferences ggbPrefs;
	 static {
		 ggbPrefs = Preferences.userRoot().node("/geogebra");
	 }	 
	 private static String XML_GGB_FACTORY_DEFAULT; // see loadPreferences()
     
    // special preference keys
	private static final String XML_USER_PREFERENCES = "xml_user_preferences";	
	private static final String TOOLS_FILE_GGT = "tools_file_ggt";	
	private static final String APP_LOCALE = "app_locale";	
	private static final String APP_CURRENT_IMAGE_PATH = "app_current_image_path";
	private static final String APP_FILE_ = "app_file_";		
		
	
	public static String loadPreference(String key, String defaultValue) {
		return ggbPrefs.get(key, defaultValue);
	}
	
	public static void savePreference(String key, String value) {
		if (key != null && value != null)
			ggbPrefs.put(key, value);
	}
	
	
	/**
     * Returns the path of the first file in the file list 
     */
    public static File getDefaultFilePath() {      	    	
    	File file = new File(ggbPrefs.get(APP_FILE_ + "1", ""));
    	if (file.exists())
    		return file.getParentFile();
    	else
    		return null;
    }       
    
    /**
     * Returns the default image path
     */
    public static File getDefaultImagePath() {      	
    	// image path
		String pathName = ggbPrefs.get(APP_CURRENT_IMAGE_PATH, null);
		if (pathName != null)
			return new File(pathName);
		else
			return null;
    }
    
    /**
     * Saves the currently set locale.
     */
    public static void saveDefaultImagePath(File imgPath) {    
    	try {
    		if (imgPath != null)
    			ggbPrefs.put(APP_CURRENT_IMAGE_PATH, imgPath.getCanonicalPath());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Returns the default locale
     */
    public static Locale getDefaultLocale() {      	
    	// language
    	String strLocale = ggbPrefs.get(APP_LOCALE, null);
    	if (strLocale != null) 
    		return Application.getLocale(strLocale);
    	else
    		return null;    	
    }
    
    /**
     * Saves the currently set locale.
     */
    public static void saveDefaultLocale(Locale locale) {    
    	// save locale (language)
    	ggbPrefs.put(APP_LOCALE, locale.toString());
    }
    
    /**
     * Loads the names of the four last used files from the preferences backing store.
     */
    public static void loadFileList() {
    	// load last four files
    	for (int i=4; i >= 1; i--) {	
    		File file = new File(ggbPrefs.get(APP_FILE_ + i, ""));
    		Application.addToFileList(file);	    		
    	}				    	
    }  
    	
    /**
     * Saves the names of the four last used files.
     */
    public static void saveFileList() {
    	try {    		    		    		    	
	    	// save last four files
	    	for (int i=1; i <= 4; i++) {	    		
	    		File file = Application.getFromFileList(i-1);
	    		if (file != null)
	    			ggbPrefs.put(APP_FILE_ + i, file.getCanonicalPath());
	    		else
	    			ggbPrefs.put(APP_FILE_ + i, "");
	    	}				    	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }    
   
	/**
     * Inits factory default XML by taking the preferences XML of this
     * virign application    		
     */
    public static void initDefaultXML(Application app) {    	    	
    	if (XML_GGB_FACTORY_DEFAULT == null)
    		XML_GGB_FACTORY_DEFAULT = app.getPreferencesXML();    
    }
    
    /**
     * Saves preferences by taking the application's current values. 
     */
    public static void saveXMLPreferences(Application app) {
    	// preferences xml
    	String xml = app.getPreferencesXML();
    	ggbPrefs.put(XML_USER_PREFERENCES, xml);  
    	    	
    	// store current tools including icon images as ggt file (byte array)
    	ggbPrefs.putByteArray(TOOLS_FILE_GGT, app.getMacroFileAsByteArray());
    	
    	try {
    		ggbPrefs.flush();
    	} catch (Exception e) {
    		System.err.println(e);
    	}
    }
    
    /**
     * Loads XML preferences (empty construction with GUI and kernel settings) and sets application accordingly.
     * This method clears the current construction in the application.
     * Note: the XML string used is the same as for ggb files. 
     */
    public static void loadXMLPreferences(Application app) {  
    	// load this preferences xml file in application
    	try {
    		app.setWaitCursor();
    		
    		// load tools from ggt file (byte array)
        	byte [] ggtFile = ggbPrefs.getByteArray(TOOLS_FILE_GGT, app.getMacroFileAsByteArray());
        	app.loadMacroFileFromByteArray(ggtFile);
    		
    		// load preferences xml
        	String xml = ggbPrefs.get(XML_USER_PREFERENCES, XML_GGB_FACTORY_DEFAULT);        
    		app.setXML(xml, true);	
    		
    		app.initUndoInfo();      		
    		app.setDefaultCursor();
    	} catch (Exception e) {	    		
    		e.printStackTrace();
    	}    	
    }
    
    
    
    /**
     * Clears all user preferences.   
     */
    public static void clearPreferences() {
    	try {
    		ggbPrefs.clear();    		
    		ggbPrefs.flush();
    	} catch (Exception e) {
    		System.err.println(e);
    	}
    }
    	     	 
}