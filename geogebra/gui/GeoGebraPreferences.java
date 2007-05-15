package geogebra.gui;

import geogebra.Application;
import geogebra.euclidian.EuclidianView;

import java.util.prefs.Preferences;

public class GeoGebraPreferences {
	
	 // preferences node name for GeoGebra 	 
	 private static Preferences ggbPrefs;
	 static {
		 ggbPrefs = Preferences.userRoot().node("/geogebra");
	 }	 
	 private static String XML_GGB_FACTORY_DEFAULT; // see loadPreferences()
     
    // Preference keys for GeoGebra
	private static final String XML_USER_PREFERENCES = "XML_USER_PREFERENCES";	
	
	/**
     * Inits factory default XML by taking the preferences XML of this
     * virign application    		
     */
    public static void initDefaultXML(Application app) {    	    	
    	if (XML_GGB_FACTORY_DEFAULT == null)
    		XML_GGB_FACTORY_DEFAULT = app.getPreferencesXML();    
    }
	
    /**
     * Loads preferences and sets application's values. 
     * @param loadXMLprefs: whether xml preferences file should be loaded (note: 
     * this clears the current construction)
     */
    public static void loadPreferences(Application app, boolean loadXMLprefs) {    	    	    	 	      	    	
    	if (loadXMLprefs) {
	    	// get preferences xml
	    	String xml = ggbPrefs.get(XML_USER_PREFERENCES, XML_GGB_FACTORY_DEFAULT);    	
	    			
	    	// load this preferences xml file in application
	    	try {
	    		app.setWaitCursor();
	    		app.setXML(xml, true);	
	    		app.updateContentPane();
	    		app.setDefaultCursor();
	    	} catch (Exception e) {	    		
	    		e.printStackTrace();
	    	}
    	}
    }
    
    /**
     * Saves preferences by taking the application's current values. 
     */
    public static void savePreferences(Application app) {
    	String xml = app.getPreferencesXML();
    	ggbPrefs.put(XML_USER_PREFERENCES, xml);     	    
    }
    
    
    /**
     * Clears all user preferences.   
     */
    public static void clearPreferences() {
    	try {
    		ggbPrefs.clear();
    	} catch (Exception e) {
    		System.err.println(e);
    	}
    }
    	     	 
}