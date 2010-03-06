/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.main;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.prefs.Preferences;

import geogebra.main.Application;
import java.net.URL;
import java.io.*;
import java.util.Properties;
/**
 * Class GeoGebraPortablePreferences
 * 
 * Stores user settings and options as a property file.
 * For use in portable GeoGebra on usb and Cd/DVD.
 * 
 * This class is returned by GeoGebraPreferences.getPrefs() instead
 * of GeoGebraPreferences itself, if the file
 * preferences.properties exists in the geogebra.jar-folder.
 * This opens up for three modes:
 * 	-PREFERENCES_IN_SYSTEM_PREFERENCES		(Normal)
 *  -PREFERENCES_IN_PROPERTY_FILE			(Portable GeoGebra)
 *  -PREFERENCES_IN_PROPERTY_FILE_READ_ONLY	(Read-only network share or CD/DVD)
 *  
 *  geogebra.properties must have one line:
 *  is_read_only=true (or false)
 *  
 * A cleaner implementation would be to rewrite GeoGebraPreferences to an abstract class, as
 * an interface for GeoGebraSystemPreferences and GeoGebraPropertyFile, but as there probably
 * never will be a demand for a third class later, I chose a solution which makes it unneccessary 
 * to change calling classes, by just making this one and do a small rewrite of getPrefs() in 
 * GeoGebraPreferences.
 *  
 * This class implements all the commands of GeoGebraPreferences, but stores in
 * 		preferenes.properties			file
 * 
 * Options/ToDo:
 * 		Might as well store:
 * 			xml	in user.xml
 * 			ggt in macro.bin
 *      to avoid escaping "=", b64 encode/decoding and save some time...
 *      Also useful to have the xml in a separate file for editing?
 * 		
 * @author Hans-Petter Ulven
 * @version 2010-03-06
 */
public class GeoGebraPortablePreferences extends GeoGebraPreferences{
	
	//*** ToDo: Make these protected in GeoGebraPreferences to avoid  duplicates here...
	
	 private  String XML_GGB_FACTORY_DEFAULT; // see loadPreferences()
    
   // special preference keys (copied from GeoGebraPreferences
	private  final String XML_USER_PREFERENCES = "xml_user_preferences";	
	private  final String TOOLS_FILE_GGT = "tools_file_ggt";	
	private  final String APP_LOCALE = "app_locale";	
	private  final String APP_CURRENT_IMAGE_PATH = "app_current_image_path";
	private  final String APP_FILE_ = "app_file_";		
	
	/// --- --- ///
	private final static boolean 	DEBUG 	= 	true;
	private final static String		ERROR	=	"Error?";		//For debugging
	private final static String		EQUAL	=	"§EQUALS§";		//For substituting in xml and ggt, better than escape-complications?	
	public  final static String 	PROPERTY_FILENAME = "preferences.properties";	
	private       static String		folderpath=null;				//property folder
	private		  static String		path=null;
	private 	  static File		propertyfile=null;
	private 	  static Properties	properties=new Properties();
		
	/// --- Properties --- ///
	
	private static GeoGebraPortablePreferences singleton=null;
	///////private static System.Property					    properties;
	
	
	/// --- Interface --- ///
	
	/* Singleton getInstance()->getPref() */
	public synchronized static GeoGebraPreferences getPref() {
		if (singleton == null){
			singleton = new GeoGebraPortablePreferences();
			singleton.loadPreferences();
		}//if
		return singleton;
	}//getPref()

	private  void loadPreferences(){
		try{
			propertyfile=geogebra.util.Util.findFile(PROPERTY_FILENAME);
			if(propertyfile!=null){
				folderpath=propertyfile.getParent();									//debug("folderpath: "+folderpath);
				path=propertyfile.getCanonicalPath();									debug("path: "+path);
				BufferedInputStream fis=new BufferedInputStream(new FileInputStream(propertyfile));
				properties.load(fis);	
				fis.close();															properties.list(System.out);
			}else{
				debug("Found no preferences.properties...");		
			}//if
		}catch(Exception e){
			debug("Problem loading preferences.properties...");
			//e.printStackTrace();
		}//try-catch			
	}//loadPreferences
	
	private void storePreferences(){
		try {
            BufferedOutputStream os=new BufferedOutputStream(new FileOutputStream(new File(path)));
	   		properties.store(os,"Portable Preferences");
	   	} catch (Exception e) {
	   		Application.debug("Problem with storing of preferences.properties..."+e.toString());
	   	}//try-catch		
	}//storePreferences()
	
	
	/// --- GeoGebraPreferences interface --- ///
	public  String loadPreference(String key, String defaultValue) {
		return get(key,defaultValue);
	}//loadPreference(key,def)
	
	public  void savePreference(String key, String value) {
		set(key,value);
	}//savePreferences(key,val)
	
	
	/**
    * Returns the path of the first file in the file list 
    */
   public  File getDefaultFilePath() {    
   	File file = new File(properties.getProperty(APP_FILE_ + "1", ""));
   	if (file.exists())
   		return file.getParentFile();
   	else   
   		return null;
   }//getDefaultFilePath()       
   
   /**
    * Returns the default image path
    */
   public  File getDefaultImagePath() {      	
   	// image path
		String pathName = properties.getProperty(APP_CURRENT_IMAGE_PATH, null);
		if (pathName != null)
			return new File(pathName);
		else
			return null;
   }
   
   /**
    * Saves the currently set locale.
    */
   public  void saveDefaultImagePath(File imgPath) {    
   	try {
   		if (imgPath != null)
   			set(APP_CURRENT_IMAGE_PATH, imgPath.getCanonicalPath());
   	} catch (Exception e) {
   		e.printStackTrace();
   	}
   }//saveDefaultImagePath(File)
   
   /**
    * Returns the default locale
    */
   public  Locale getDefaultLocale() {      	
   	// language
   	String strLocale = get(APP_LOCALE, null);
   	if (strLocale != null) 
   		return Application.getLocale(strLocale);
   	else
   		return null;    	
   }//getDefaultLocale()
   
   /**
    * Saves the currently set locale.
    */
   public  void saveDefaultLocale(Locale locale) {    
   		// save locale (language)
   		set(APP_LOCALE, locale.toString());
   }//saveDefaultLocle(Locale)
   
   /**
    * Loads the names of the four last used files from the preferences backing store.
    */
   public  void loadFileList() {
   	// load last four files
   	for (int i=4; i >= 1; i--) {	
   		File file = new File(get(APP_FILE_ + i, ""));
   		Application.addToFileList(file);	    		
   	}				    	
   }//loadFileList()
   	
   /**
    * Saves the names of the four last used files.
    */
   public  void saveFileList() {
   	try {    		    		    		    	
	    	// save last four files
	    	for (int i=1; i <= 4; i++) {	    		
	    		File file = Application.getFromFileList(i-1);
	    		if (file != null)
	    			set(APP_FILE_ + i, file.getCanonicalPath());
	    		else
	    			set(APP_FILE_ + i, "");
	    	}				    	
   	} catch (Exception e) {
   		e.printStackTrace();
   	}
   }//saveFileList()   
  
	/** Nothing to change: Inherited
    * Inits factory default XML by taking the preferences XML of this
    * virign application    		

   public  void initDefaultXML(Application app) {    	    	
   	if (XML_GGB_FACTORY_DEFAULT == null)
   		XML_GGB_FACTORY_DEFAULT = app.getPreferencesXML();    
   }
	***/
   
   /**
    * Saves preferences by taking the application's current values. 
    * Apparently no limit on property length! (# preferences: 8192),
    * so no need to split up in pieces :-)
    * But we have to convert byte[]--b64-->String
    */
   public  void saveXMLPreferences(Application app) {
	// preferences xml
   	String xml = app.getPreferencesXML();
   	
   	set(XML_USER_PREFERENCES, xml);  
   	
   	byte[]	macrofile	=app.getMacroFileAsByteArray();
   	String	macrostring =geogebra.util.Base64.encode(macrofile,0);
   	
   	set(TOOLS_FILE_GGT,macrostring);
    
   	//Fore writing, "flush":
   	storePreferences();
   }//saveXMLPreferences(Application)
   

   
   /**
    * Loads XML preferences (empty construction with GUI and kernel settings) and sets application accordingly.
    * This method clears the current construction in the application.
    * Note: the XML string used is the same as for ggb files. 
    */
   public  void loadXMLPreferences(Application app) {  
   	app.setWaitCursor();  
   	
   	// load this preferences xml file in application
   	try {    		      		    	    
   		// load tools from ggt file (byte array)
   		// Must convert String--b64-->byte
   		String  ggtString=get(TOOLS_FILE_GGT,ERROR);
   		if(ggtString.equals(ERROR)){
   			debug("problem with getting GGT...");
   		}else{
   			byte [] ggtFile = geogebra.util.Base64.decode(ggtString);
   			app.loadMacroFileFromByteArray(ggtFile, true);
   		}//if error
       	    		
   		// load preferences xml
       	String xml = get(XML_USER_PREFERENCES, XML_GGB_FACTORY_DEFAULT);        
   		app.setXML(xml, true);	   
   	} catch (Exception e) {	    		
   		e.printStackTrace();
   	}//try-catch    	

   	app.setDefaultCursor();
   }//loadXMLPreferences(Application)
   
   
   
   /**
    * Clears all user preferences.   
    */
   public  void clearPreferences() {
   	try {
   		properties.clear();    		
   		//ggbPrefs.flush();
   		properties.store(new FileOutputStream(new File(path)),"Portable Preferences");
   	} catch (Exception e) {
   		Application.debug(e+"");
   	}
   }
   
   /// --- Private --- ///
   // get/set with check
   private final String get(String key,String def){
	   if(properties!=null){
		   return properties.getProperty(key,def);
	   }else{
		   return ERROR;
	   }//if
   }//get()
   
   public final void set(String key,String val){
	  if(properties!=null){ properties.setProperty(key, val);} 
   }//set()
   
   
 // --- SNIP ---------------------------------------------------
   
   
	// /// ----- Debug ----- /////
	private final static void debug(String s) {
		if (DEBUG) {
			Application.debug(s);
		}// if()
	}// debug()
	
	public final static void main(String[] args){
		GeoGebraPreferences gp=GeoGebraPortablePreferences.getPref();
	}//main()
   

   /* getPreferencesfrom system preferences 
    * 	 // preferences node name for GeoGebra 	 
	//  Only copied here to 
	 private  Preferences ggbPrefs;
	  {
		  try {
			  ggbPrefs = Preferences.userRoot().node("/geogebra");
		  } catch (Exception e) {
			  // thrown when running unsigned JAR
			  ggbPrefs = null;
		  }
	 }	 
*/
   
 // --- SNIP ---------------------------------------------------

}//class GeoGebraPortablePreferences
