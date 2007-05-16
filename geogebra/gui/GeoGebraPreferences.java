package geogebra.gui;

import geogebra.Application;

import java.io.File;
import java.util.Locale;
import java.util.prefs.Preferences;

public class GeoGebraPreferences {		
	
	 // preferences node name for GeoGebra 	 
	 private static Preferences ggbPrefs;
	 static {
		 ggbPrefs = Preferences.userRoot().node("/geogebra");
	 }	 
	 private static String XML_GGB_FACTORY_DEFAULT; // see loadPreferences()
     
    // Preference keys for GeoGebra
	private static final String XML_USER_PREFERENCES = "xml_user_preferences";	
	private static final String APP_LOCALE = "app_locale";	
	private static final String APP_CURRENT_IMAGE_PATH = "app_current_image_path";
	private static final String APP_FILE_ = "app_file_";
		
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
    	String xml = app.getPreferencesXML();
    	ggbPrefs.put(XML_USER_PREFERENCES, xml);  
    	
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
    	// get preferences xml
    	String xml = ggbPrefs.get(XML_USER_PREFERENCES, XML_GGB_FACTORY_DEFAULT);    	
    			
    	// load this preferences xml file in application
    	try {
    		app.setWaitCursor();
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