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
import geogebra.util.CopyToFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.zip.ZipInputStream;

public class JarManager {
	
	// types of application
	private static final int TYPE_APPLET = 1; // Java applet
	private static final int TYPE_WEBSTART = 2; // Java webstart application
	private static final int TYPE_LOCAL_JARS = 3; // local application with jar files
	private static final int TYPE_LOCAL_NO_JARS = 4; // running from Eclipse without jar files
	
	// singleton instance of JarManager
	private static JarManager singleton;
	
	// classloader of main application
	private ClassLoader appClassLoader;
	
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
		// use classloader of Application class: important for applets
		appClassLoader = Application.class.getClassLoader();
		
		// init codebase and strCodebase
		codebase = app.getCodeBase();	
		 				
		// init application type as TYPE_APPLET, TYPE_WEBSTART, or TYPE_LOCAL_JARS
		initApplicationType(app);				
			
		// init localJar directory where the jar files can be found locally
		initLocalJarDir();
				
		// TODO: remove
		Application.debug("app type: " + main_app_type);
		Application.debug("localJarDir: " + localJarDir);								
	}
		
	/**
	 * Returns whether the given jar file is on the classpath.
	 * 
	 * @param jarFileIndex: Application.JAR_FILE_GEOGEBRA, JAR_FILE_GEOGEBRA_GUI, JAR_FILE_GEOGEBRA_CAS, etc.
	 */
	final public boolean isOnClassPath(int jarFileIndex) {
		return jarFileOnClasspath[jarFileIndex];
	}
	
	/**
	 * Loads the the given jar file and adds it to the classpath. Note: if the codebase is
	 * online (applet, webstart), the jar file is downloaded to a temporary local directory first.
	 * 
	 * @param jarFileIndex: Application.JAR_FILE_GEOGEBRA, JAR_FILE_GEOGEBRA_GUI, JAR_FILE_GEOGEBRA_CAS, etc.
	 */
	final synchronized public boolean addJarToClassPath(int jarFileIndex) {		
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
				// Webstart already has all files on the classpath
				// Eclipse doesn't use jar files, so nothing to do here 
				jarFileOnClasspath[jarFileIndex] = true;
				return true;
		
			case TYPE_APPLET:
				// we download the needed jar file to the local directory first
				downloadFile(jarFileName, localJarDir);	
												
			case TYPE_LOCAL_JARS:
				// no download needed for local jar files
				break;			
		}
						
		// jar file is now in localJarDir 
		File localJarFile = new File(localJarDir, jarFileName);
		
		try {			
			jarFileOnClasspath[jarFileIndex] = 
				// make sure jar file can be opened
				checkJarFile(localJarFile) && 
				// add jar file in localJarDir to classpath
				ClassPathManipulator.addURL(localJarFile.toURI().toURL(), appClassLoader);					
		} 
		catch (Exception e) {
			System.err.println("Could not add to classpath: " + localJarFile);
			jarFileOnClasspath[jarFileIndex] = false;
		}		
				
		// TODO: remove			
		Application.debug("Added to classpath: " + localJarFile + ", success: " + jarFileOnClasspath[jarFileIndex]);									
		return jarFileOnClasspath[jarFileIndex];
	}
	
	/**
	 * Checks if the given jar file can be opened and read. If this fails we try to
	 * download the jar file and check again.
	 * 
	 * @return success
	 */
	public synchronized boolean checkJarFile(File localJarFile) {
		if (isJarFileReadable(localJarFile))
			return true;
		
		// something is wrong: the jar file could not be opened
		try {
			// delete corrupt jar file
			if (localJarFile.exists())
				localJarFile.delete();
			
			// download jar file again
			downloadFile(localJarFile.getName(), localJarDir);
			
			// check jar file again
			return isJarFileReadable(localJarFile);
		} 
		catch (Exception e) {
			// TODO: remove			
			Application.debug("Jar file could not be downloaded: " + localJarFile);									
			e.printStackTrace();
			return false;
		}	
	}
	
	/**
	 * Returns whether the given jar file can be opened and read.
	 * 
	 * @return
	 */
	private synchronized boolean isJarFileReadable(File localJarFile) {	
		if (!localJarFile.exists())
			return false;
		
		try {
			// open zip file and try to read first entry
			FileInputStream fis = new FileInputStream(localJarFile);
			ZipInputStream zis = new ZipInputStream(fis);
			boolean readable = zis.getNextEntry() != null;
			zis.close();
			fis.close();
			return readable;
		}
		catch (Exception e) {
			// TODO: remove			
			Application.debug("Jar file is not readable: " + localJarFile);									
			e.printStackTrace();
			return false;
		}						
	}
	
	/**
	 * Downloads the given file to destination directory. This is needed for applets and
	 * webstart applications (to export dynamic worksheets).
	 * 
	 * @return true if successful
	 */
	public synchronized boolean downloadFile(String fileName, File destDir) {    	
		// download jar file to localJarDir
		File destFile = new File(destDir, fileName);
		if (destFile.exists()) {
			// TODO: remove
			Application.debug("File found, no download needed for " + fileName + " in directory " + destDir);		
			
			// destination file exists already
			return true;
		}
				
		try {											
			// download jar from URL to destFile
			URL src = new URL(codebase, fileName);			
			CopyToFile.copyURLToFile(src, destFile);
			
			// TODO: remove
			Application.debug("downloaded " + fileName + " to directory " + destDir);		
			return true;						
		} catch (Exception e) {		
			System.err.println("Download error: " + e.getMessage());
			destFile.delete();
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
			// check code base of application
			// e.g. http://... or file://...					
			if (codebase.toString().startsWith("file")) {
				
				  try {		
					  // decode special %xy characters in local codebase
					  codebase = new URL(URLDecoder.decode(codebase.toString(), "UTF-8"));
	              }
	              catch (Exception e) {}
				
				// check if jar file exists
				File main_jar_file = new File(codebase.getFile(), 
						Application.JAR_FILES[Application.JAR_FILE_GEOGEBRA]);															
				
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
	
	
	/**
	 * Loads text file and returns content as String.
	 */
	public String loadTextFile(String s) {
        StringBuffer sb = new StringBuffer();        
        try {
          InputStream is = appClassLoader.getResourceAsStream(s);
          BufferedReader br = new BufferedReader(new InputStreamReader(is));
          String thisLine;
          while ((thisLine = br.readLine()) != null) {  
             sb.append(thisLine);
             sb.append("\n");
         }
      }
        catch (Exception e) {
          e.printStackTrace();
          }
          return sb.toString();
      }
  
   
}
