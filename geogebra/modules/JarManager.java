/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.modules;

/** 
<h3>JarManager</h3>

Dynamically adds jar files to classpath when needed. For example "geogebra_cas.jar" is only loaded when 
the CAS is used. This is important for online applets to keep downloading times small. The JarManager uses a
local directory (in system's temp directory) to keep jar files of a version for future use. 

@author      Markus Hohenwarter, Michael Borcherds
@version     2008-09-26
*/

import geogebra.Application;
import geogebra.util.CopyURLToFile;

import java.io.File;
import java.net.URL;

public class JarManager {
	
	// types of application
	private static final int TYPE_APPLET = 1; // Java applet
	private static final int TYPE_WEBSTART = 2; // Java webstart application
	private static final int TYPE_LOCAL_JARS = 3; // local application with jar files
	private static final int TYPE_LOCAL_NO_JARS = 4; // running from Eclipse without jar files
	
	// singleton instance of JarManager
	private static JarManager singleton;
	
	// codebase where the jar files can be found (either http: or file:)
	private URL codebase;
	
	// application type: TYPE_APPLET, TYPE_WEBSTART, or TYPE_LOCAL_JARS
	private int main_app_type;
	
	// directory with local jars
	private File localJarDir;	
	
	// boolean variables to store whether a certain jar file is on classpath /
	// was tried to put on the classpath
	// e.g. use value of jarFileOnClasspath[Application.JAR_FILE_GEOGEBRA_CAS]
    private boolean [] jarFileOnClasspath = new boolean[Application.JAR_FILES.length];		
    private boolean [] jarFileTriedToPutOnClasspath = new boolean[Application.JAR_FILES.length];	
    
	/**
	 * Returns a singleton instance of JarManager.
	 */
	public synchronized static JarManager getSingleton(Application app) {
		if (singleton == null) {
			singleton = new JarManager(app);
		}
		
		return singleton;		
	}
	
	/**
	 * Creates the singleton instance of JarManager. Note that the type of application (applet, webstart, local)
	 * can be decided by looking at the first application instance only. That's why we only need one JarManager 
	 * instance even if we have multiple application instances (windows).
	 */
	private JarManager(Application app) {	
		codebase = app.getCodeBase();
				
		// init application type as TYPE_APPLET, TYPE_WEBSTART, or TYPE_LOCAL_JARS
		initApplicationType(app);				
			
		// init localJar directory where the jar files can be found locally
		initLocalJarDir();
		
		// geogebra.jar is always on classpath as it is loaded on startup
		jarFileOnClasspath[Application.JAR_FILE_GEOGEBRA] = true;
		jarFileTriedToPutOnClasspath[Application.JAR_FILE_GEOGEBRA] = true;
		
		// TODO: remove
		Application.debug("app type: " + main_app_type);
		Application.debug("localJarDir: " + localJarDir);	
		
		
		//TODO: for WebStart and applet: download jar files to local directory in background (thread)
		//copyJarFilesToTempDir(app);
		
	}
	
	/**
	 * Loads the the given jar file and adds it to the classpath. Note: if the codebase is
	 * online (applet, webstart), the jar file is downloaded to a temporary local directory first.
	 * 
	 * @param jarFileIndex: Application.JAR_FILE_GEOGEBRA, JAR_FILE_GEOGEBRA_GUI, JAR_FILE_GEOGEBRA_CAS, etc.
	 */
	final public boolean addJarToClassPath(int jarFileIndex) {		
		// check if file is already on classpath
		if (jarFileOnClasspath[jarFileIndex]) {			
			//Application.debug("jar file already in classpath " + Application.JAR_FILES[jarFileIndex]);
			return true;
		}	
		// check if we already tried to put jar file on classpath
		else if (jarFileTriedToPutOnClasspath[jarFileIndex]) {
			//Application.debug("do nothing: tried to put on classpath already: " + Application.JAR_FILES[jarFileIndex]);			
			return false;
		}
		
		// try to add jar file to classpath 		
		return doAddJarToClassPath(jarFileIndex);
	}
	
	private synchronized boolean doAddJarToClassPath(int jarFileIndex) {	
		// remember that we tried to put this file on classpath to make sure we don't try again
		jarFileTriedToPutOnClasspath[jarFileIndex] = true;
					
		// get jar file name for index
		String jarFileName = Application.JAR_FILES[jarFileIndex];
	
		switch (main_app_type) {
			case TYPE_WEBSTART:
			case TYPE_LOCAL_NO_JARS:		
				// Webstart puts all jar files on classpath itself
				// Eclipse doesn't use jar files, so nothing to do here 
				jarFileOnClasspath[jarFileIndex] = true;
				return true;
		
			case TYPE_APPLET:
				// we download the needed jar file to the local directory first
				downloadFile(codebase, jarFileName, localJarDir);				
				break;
				
			case TYPE_LOCAL_JARS:
				// no download needed for local jar files
				break;			
		}
		
		// add jar file in localJarDir to classpath
		File localJarFile = new File(localJarDir, jarFileName);
		if (localJarFile.exists()) {
			// add jar file to classpath
			boolean success = ClassPathManipulator.addFile(localJarFile);
			
			// remember that this jar file is on classpath
			jarFileOnClasspath[jarFileIndex] = success;
			
			// TODO: remove
			Application.debug("Added to classpath: " + localJarFile);
		} else {
			System.err.println("Could not add to classpath: " + localJarFile);
			jarFileOnClasspath[jarFileIndex] = false;
		}
		
		return jarFileOnClasspath[jarFileIndex];
	}
	
	/**
	 * Downloads the given file to destination directory. This is needed for applets and
	 * webstart applications (to export dynamic worksheets).
	 * 
	 * @return true if successful
	 */
	public synchronized static boolean downloadFile(URL codebase, String fileName, File destDir) {    	    
		try {					
			// download jar file to localJarDir
			File destFile = new File(destDir, fileName);
			if (destFile.exists()) {
				// TODO: remove
				Application.debug("DID NOT download, file exists already: " + fileName + " in directory " + destDir);		
				
				// destination file exists already
				return true;
			}
							
			// download jar from URL to destFile			
			URL src = new URL(codebase.toExternalForm() + fileName);			
			CopyURLToFile.copyURLToFile(src, destFile);
			
			// TODO: remove
			Application.debug("downloaded " + fileName + " to directory " + destDir);		
			return true;			
			
		} catch (Exception e) {		
			System.err.println("Download error: " + e.getMessage());
			return false;
		}			
	}

	
	/**
	 * Sets main_app_type to type of application.
	 * @return TYPE_APPLET, TYPE_WEBSTART, or TYPE_LOCAL_JARS
	 */
	private void initApplicationType(Application app) {			
		// init main_app_type: applet, webstart, or local jar files
		if (app.isApplet()) {
			// APPLET
			main_app_type = TYPE_APPLET;
		} 
		else {
			// get code base of first application
			// e.g. http://... or file://...
			String strCodeBase = codebase.toString();
			if (strCodeBase.startsWith("file")) {
				// check if jar file exists
				File main_jar_file = new File(strCodeBase + Application.JAR_FILES[Application.JAR_FILE_GEOGEBRA]);
				if (main_jar_file.exists()) {
					// LOCAL JARS
					main_app_type =  TYPE_LOCAL_JARS;
				} else {
					// running local without jar files
					main_app_type = TYPE_LOCAL_NO_JARS;
				}				
				
			} else {
				// WEBSTART
				main_app_type =  TYPE_WEBSTART;
			}
		}
	}
	
	/**
	 * Returns the local folder name that includes the GeoGebra jar files.
	 * For 
	 */
	public File getLocalJarDir() {			
		return localJarDir;
	}
			
	/**
	 * Sets localJarDir to the folder where the GeoGebra jar files can be found locally.
	 */
	private void initLocalJarDir() {				
		switch (main_app_type) {
			case TYPE_LOCAL_JARS:
			case TYPE_LOCAL_NO_JARS:
				// local jar files: use local directory of jar files
				localJarDir = new File(codebase.getFile());
				break;
				
			case TYPE_APPLET:
			case TYPE_WEBSTART:			
				// applet or webstart: we need to use a local directory to download jar files
				localJarDir = createLocalDir();
				break;				
		}
	}
	
	/**
	 * Creates a temporary directory using the current version number, e.g. "geogebra3.1.3"
	 */
	private synchronized static File createLocalDir() {	
		// initialize local jar directory		
		String baseDir = System.getProperty("java.io.tmpdir");
		
		// Mac OS doesn't add "/" at the end of directory path name
		if (!baseDir.endsWith(File.separator)) 
			baseDir += File.separator;			
											
		// directory name, e.g. /tmp/geogebra3.1.43.0/
		File tempDir = new File(baseDir + "geogebra" + Application.versionString + File.separator);		
		if (tempDir.exists())	{
			// TODO: remove
			Application.debug("use existing local directory : " + tempDir);
			
		} else {
			// create local directory, e.g. /tmp/geogebra3.1.43.0/
			try {				
				tempDir.mkdirs();
				
				// TODO: remove
				Application.debug("local directory created: " + tempDir);
			} 
			catch (Exception e)	{
				System.err.println(e.getMessage());
				tempDir = new File(baseDir);
				
				// TODO: remove
				Application.debug("COULD NOT create directory, use instead: " + tempDir);
			}
		}
					
		return tempDir;
	}
	
	
	/*
		//Application.debug("java.io.tmpdir = "+tempDir);
        //String webstartCodebase=null;
        
        if (!Application.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm()
        		.toLowerCase(Locale.US).endsWith(".jar")) {
        	Application.debug("not running from jar - set IS_WEBSTART=true");
            IS_WEBSTART=true;
        }
       
        try {
        	if (!IS_WEBSTART)
        	{
        		javax.jnlp.BasicService basicService = (javax.jnlp.BasicService)javax.jnlp.ServiceManager.lookup("javax.jnlp.BasicService");
        		java.net.URL codeBaseURL = basicService.getCodeBase();
        		//URL url = new java.net.URL(codeBaseURL,"");
        		//webstartCodebase=codeBaseURL.toString();
        		IS_WEBSTART=true;
        		Application.debug("JNLP codebase "+codeBaseURL.toString());
        	}
            // JMathTeX files specified in the JNLP file, don't need to add them to classpath
            // JSMATHTEX_LOADED=(Util.getJavaVersion() >= 1.5);
            
            // all specified in the JNLP file, don't need to be added to classpath
            GEOGEBRA_EXPORT_PRESENT=true;
            GEOGEBRA_EXPORT_LOADED=true;
            GEOGEBRA_PROPERTIES_PRESENT=true;
            GEOGEBRA_PROPERTIES_LOADED=true;
            GEOGEBRA_CAS_PRESENT=true;
            GEOGEBRA_CAS_LOADED=true;
            GEOGEBRA_GUI_PRESENT=true;
            GEOGEBRA_GUI_LOADED=true;
            //GEOGEBRA_SPREADSHEET_PRESENT=true;
            //GEOGEBRA_SPREADSHEET_LOADED=true;
            // init spreadsheet view
         	//spreadsheetView = new SpreadsheetView(app, 26, 100);
            Application.debug("IS_WEBSTART="+IS_WEBSTART);
            return;
         } catch (Exception ex) {
             IS_WEBSTART=false;
             Application.debug("IS_WEBSTART="+IS_WEBSTART);
            //ex.printStackTrace();
         }		    
         
 		// get applet codebase
         // NB applet.getCodeBase() doesn't work (returns base of HTML)
 		appletCodeBase = (app.getApplet()!=null) ? app.getCodeBase() : null;
 		Application.debug("appletCodeBase="+appletCodeBase);
 		
 		if (appletCodeBase != null)
 		{
 			appletCodeBaseStr = appletCodeBase.toString();
 			
 			// if it's a local path, put spaces back in
 			if (appletCodeBaseStr.startsWith("file:"))
 				appletCodeBaseStr = appletCodeBaseStr.replaceAll("%20", " ");
 		}

 		Application.debug("appletCodeBaseStr="+appletCodeBaseStr);
         
         ClassPathManipulator.addURL(addPathToJar("."), null);
       
         GEOGEBRA_CAS_PRESENT = jarPresent("geogebra_cas.jar");
         //addCasJarToClassPath();
         
         
         GEOGEBRA_GUI_PRESENT = jarPresent("geogebra_gui.jar");
         if (GEOGEBRA_GUI_PRESENT){
         	if (app.getApplet()==null){
         		addGuiJarToClassPath();
             	//if (copyExportJarToTempDir()) loadExport = System.getProperty("java.io.tmpdir") + "geogebra_export.jar";
         		
         	}
         	else if (app.getApplet().showMenuBar ||
        			app.getApplet().enableRightClick ||
        			app.getApplet().showFrame)

         	{
         		// running as applet with menu
         		addGuiJarToClassPath();
             	//loadExport = "geogebra_export.jar"; // fallback, shouldn't be needed
         	}
         }
         
         
         //GEOGEBRA_SPREADSHEET_PRESENT = jarPresent("geogebra_spreadsheet.jar");
         //addSpreadsheetJarToClassPath();
     	
         
         
         
         GEOGEBRA_EXPORT_PRESENT = jarPresent("geogebra_export.jar");
        
        if (GEOGEBRA_EXPORT_PRESENT){
        	if (app.getApplet()==null){
        		addExportJarToClassPath();
            	//if (copyExportJarToTempDir()) loadExport = System.getProperty("java.io.tmpdir") + "geogebra_export.jar";
        		
        	}
        	else if (app.getApplet().showMenuBar) 
        	{
        		// running as applet with menu
        		addExportJarToClassPath();
            	//loadExport = "geogebra_export.jar"; // fallback, shouldn't be needed
        	}
        }
        
        
        if (loadExport != null)
        {
        		addJarToPath(loadExport, geogebra.gui.menubar.MenubarImpl.class.getClassLoader());
        		GEOGEBRA_EXPORT_LOADED=true;
        }
        Application.debug("GEOGEBRA_EXPORT_LOADED="+GEOGEBRA_EXPORT_LOADED);
        

        GEOGEBRA_PROPERTIES_PRESENT = jarPresent("geogebra_properties.jar");        
        
        //String loadProperties=null;
        
        if (GEOGEBRA_PROPERTIES_PRESENT){
        	if (app.getApplet()==null){
        		addPropertiesJarToClassPath();
            	//if (copyPropertiesJarToTempDir()) loadProperties = System.getProperty("java.io.tmpdir") + "geogebra_properties.jar";
        		
        	}
        	else if (app.getApplet().showMenuBar || 
        			app.getApplet().showToolBar ||
        			app.getApplet().showAlgebraInput ||
        			app.getApplet().enableRightClick ||
        			app.getApplet().showOpenButton ||
        			app.getApplet().showFrame)
        			//app.showAlgebraView() ||
        			//app.showSpreadsheet())
        		
        		// TODO load properties.jar when algebra or spreadsheet views opened
        		

        	{
        		// running as applet with possibility of translations needed
        		addPropertiesJarToClassPath();
        		//addJarToPath("geogebra_properties.jar", null);
        		//GEOGEBRA_PROPERTIES_LOADED=true;
        	}
        }
	
        
        Application.debug("GEOGEBRA_PROPERTIES_LOADED="+GEOGEBRA_PROPERTIES_LOADED);
        */
     

  
   
}
