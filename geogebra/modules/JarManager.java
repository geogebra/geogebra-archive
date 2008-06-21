package geogebra.modules;
/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
/** 
<pre>
<h3>JarManager for GeoGebra</h3>

</pre>
@author      Michael Borcherds
@version     2008-06-15
*/

import geogebra.Application;
import geogebra.GeoGebraAppletBase;
import geogebra.spreadsheet.SpreadsheetView;
import geogebra.util.CopyURLToFile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


public class JarManager {

    public static boolean 			JSMATHTEX_PRESENT=false;
    public static boolean 			JSMATHTEX_LOADED=false;
    public static boolean 			GEOGEBRA_EXPORT_PRESENT=false;
    public static boolean 			GEOGEBRA_EXPORT_LOADED=false;
    public static boolean 			GEOGEBRA_PROPERTIES_PRESENT=false;
    public static boolean 			GEOGEBRA_PROPERTIES_LOADED=false;
    public static boolean 			GEOGEBRA_CAS_PRESENT=false;
    public static boolean 			GEOGEBRA_CAS_LOADED=false;
    //public static boolean 			GEOGEBRA_GUI_PRESENT=false;
    //public static boolean 			GEOGEBRA_GUI_LOADED=false;
    //public static boolean 			GEOGEBRA_SPREADSHEET_PRESENT=false;
    //public static boolean 			GEOGEBRA_SPREADSHEET_LOADED=false;
    public static boolean 			IS_WEBSTART=false;
    private static Application	 	app=null;
	
	private static boolean MAINJAR_COPIED=false;
	private static boolean EXPORTJAR_COPIED=false;
	private static boolean PROPERTIESJAR_COPIED=false;
	private static boolean CASJAR_COPIED=false;
	//private static boolean GUIJAR_COPIED=false;
	//private static boolean SPREADSHEETJAR_COPIED=false;
	
	//public static SpreadsheetView spreadsheetView;

	public JarManager(Application app) {
		
		

        this.app = app;

    	// Download jar files to temp directory in background
    	// this is done because Java WebStart uses strange jar file
    	// names in its cache. However, we need the GeoGebra jar files
    	// to export dynamic worksheets, thus we copy the jar files 
    	// to the temp directory where we can find them.
    	Thread runner = new Thread() {
    		public void run() {    	 
    			try {
    				Thread.sleep(10000);
    			} catch (Exception e) {}

    			// Cas can be called from JavaScript commands, so load just in case
				addCasJarToClassPath();

    			
	    			if (JarManager.app.showMenuBar) {    				
	    				copyMainJarToTempDir();
	    				copyExportJarToTempDir();
	    				copyPropertiesJarToTempDir();
	    				copyCasJarToTempDir();
	    				//copyGuiJarToTempDir();
	    				//copySpreadsheetJarToTempDir();
	    			}	    				
    			}	    		    		
    	};
    	runner.start();

        
        
        
        
        //String webstartCodebase=null;
        
        if (!Application.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm()
        		.toLowerCase().endsWith(".jar")) {
        	System.out.println("not running from jar - set IS_WEBSTART=true");
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
        		System.out.println("JNLP codebase "+codeBaseURL.toString());
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
            //GEOGEBRA_GUI_PRESENT=true;
            //GEOGEBRA_GUI_LOADED=true;
            //GEOGEBRA_SPREADSHEET_PRESENT=true;
            //GEOGEBRA_SPREADSHEET_LOADED=true;
            // init spreadsheet view
         	//spreadsheetView = new SpreadsheetView(app, 26, 100);
            System.out.println("IS_WEBSTART="+IS_WEBSTART);
            return;
         } catch (javax.jnlp.UnavailableServiceException ex) {
             IS_WEBSTART=false;
             System.out.println("IS_WEBSTART="+IS_WEBSTART);
            //ex.printStackTrace();
         }		    
         
         ClassPathManipulator.addURL(addPathToJar("."), null);
       
         GEOGEBRA_CAS_PRESENT = jarPresent("geogebra_cas.jar");
         //addCasJarToClassPath();
         
         /*
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
         }*/
         
         
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
        
        /*
        if (loadExport != null)
        {
        		addJarToPath(loadExport, geogebra.gui.menubar.MenubarImpl.class.getClassLoader());
        		GEOGEBRA_EXPORT_LOADED=true;
        }*/
    	System.out.println("GEOGEBRA_EXPORT_LOADED="+GEOGEBRA_EXPORT_LOADED);
        

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
	
        
    	System.out.println("GEOGEBRA_PROPERTIES_LOADED="+GEOGEBRA_PROPERTIES_LOADED);
        
        
        /*
        
		// Michael Borcherds 2008-05-30
		// add support for JMathTeX if the correct JARs are present
        // and we're running Java 1.5 or above
        
        String loadJMathTeX = null;
        String loadJDom = null;
        
        
    	JSMATHTEX_PRESENT =  ( jarPresent("JMathTeX-0.7pre.jar")
                			&& jarPresent("jdom-1.1.jar")
                			&& Util.getJavaVersion() >= 1.5);
		System.out.println("JSMATHTEX_PRESENT="+JSMATHTEX_PRESENT);
		
		if (JSMATHTEX_PRESENT) {
    	if (app.getApplet()==null){
    		// might be running as webstart, copy jar to temporary folder
        	if (copyJarToTempDir("JMathTeX-0.7pre.jar")) loadJMathTeX = System.getProperty("java.io.tmpdir") + "JMathTeX-0.7pre.jar";
        	if (copyJarToTempDir("jdom-1.1.jar")) loadJDom = System.getProperty("java.io.tmpdir") + "jdom-1.1.jar";
    		
    	}
    	else if (app.getApplet().showMenuBar || app.getApplet().showToolBar) 
    	{
    		// running as applet with menu
    		loadJMathTeX = "JMathTeX-0.7pre.jar";
    		loadJDom = "jdom-1.1.jar";
    	}
		}
    
    if (loadJMathTeX != null && loadJDom !=null)
    {
		addJarToPath(loadJMathTeX, null);
		addJarToPath(loadJDom, null);
		JSMATHTEX_LOADED=true;
    }
     
     System.out.println("JSMATHTEX_LOADED="+JSMATHTEX_LOADED);
     */

	
	
	


	}
	
	public synchronized void loadSpreadsheet()
	{
	
	}
	
	public static boolean jarPresent(String jar)
	{
        //if (plugin==null) return false;
		ClassLoader loader=app.getClass().getClassLoader();
        
		//URL codeBase=GeoGebraAppletBase.codeBase;
		URL codeBase=getAppletCodeBase();
        if (codeBase!=null) jar = codeBase.toString() + jar;
        
        
        //jar = "file:///C:/Personal/My%20Projects/JavaScript/testGeogebraApplets/geogebra_export.jar";
        
        boolean ret = (loader.getResourceAsStream(jar)!=null);
        
        System.out.println("jarPresent " + jar+" "+ret);
        
        return ret;
		
	}
	
	public static void addJarToPath(String jar, ClassLoader loader)
	{
        
		//URL codeBase=GeoGebraAppletBase.codeBase;
		URL codeBase=getAppletCodeBase();
        if (codeBase!=null) jar = codeBase.toString() + jar;
        System.out.println("addJarToPath " + jar);
        
        //addPath(jar);
        ClassPathManipulator.addURL(addPathToJar(jar), loader);       
	}
	
	private static URL getAppletCodeBase()
	{
		GeoGebraAppletBase applet;
		applet = app.getApplet();
		if (applet == null) return null;
		return applet.getCodeBase();
		
	}
	
    public static URL addPathToJar(String path){
        File file=null;
        URL  url=null;        
        try{
        	if(path.startsWith("http://")){	//url!
                System.out.println("addPath1 "+path);
        		url=new URL(path);
        	}
        	else if (path.startsWith("file:/")) { // local file in correct form
                System.out.println("addPath2 "+path);
        		url = new URL(path);
        	}else{							//file without path!

        		//URL codeBase=getAppletCodeBase();
        		URL codeBase=GeoGebraAppletBase.codeBase;
        		//URL codeBase=app.getAppletCodeBase();;
                if (codeBase!=null)
                {
                    System.out.println("addPath3"+path);
                	url = new URL(codeBase.toString()+path); // running as applet
                }
                else
                {
                    System.out.println("addPath4"+path);
                	file=new File(path);
        		    url=file.toURL();
                }
        	}
            System.out.println("addPath "+url.toString());
        	return url;
        }catch(MalformedURLException e) {
            System.out.println("PluginManager.addPath: MalformedURLExcepton for "+path);
            return null;
        }catch(Throwable e){
            System.out.println("PluginManager.addPath: "+e.getMessage()+" for "+path);
            return null;
        }//try-catch        
    }//addPath(String)
    
    public static boolean copyJarToTempDir(String jar) {
		try {		
			String tempDir = System.getProperty("java.io.tmpdir"); 
			
			// copy jar files to tempDir
				File dest = new File(tempDir, jar);
				URL src = new URL(app.getCodeBase() + jar);
				CopyURLToFile.copyURLToFile(src, dest);
			System.out.println("copied "+jar+" to temp directory " + tempDir);
				return true;
			
		} catch (Exception e) {		
			System.err.println("copyJarToTempDir: " + e.getMessage());
			return false;
		}			
	}
	
	
    public static synchronized boolean copyMainJarToTempDir()
	{
		if (MAINJAR_COPIED) return true;
		MAINJAR_COPIED=copyJarToTempDir("geogebra.jar");
		return MAINJAR_COPIED;
	}
	
	public static synchronized boolean copyExportJarToTempDir()
	{
		if (EXPORTJAR_COPIED) return true;
		EXPORTJAR_COPIED=copyJarToTempDir("geogebra_export.jar");
		return EXPORTJAR_COPIED;
	}
	
	public static synchronized boolean copyPropertiesJarToTempDir()
	{
		if (PROPERTIESJAR_COPIED) return true;
		PROPERTIESJAR_COPIED=copyJarToTempDir("geogebra_properties.jar");
		return PROPERTIESJAR_COPIED;
	}
	
	public static synchronized boolean addPropertiesJarToClassPath()
	{
		if (IS_WEBSTART) return true;
		if (!GEOGEBRA_PROPERTIES_PRESENT) return false;
		if (GEOGEBRA_PROPERTIES_LOADED) return true;
		if (app.getApplet()==null)
		{
			// not applet
			if (!copyPropertiesJarToTempDir()) return false;
			addJarToPath(System.getProperty("java.io.tmpdir") + "geogebra_properties.jar",null);
		}
		else
		{
			// applet
			addJarToPath("geogebra_properties.jar",null);			
		}
		GEOGEBRA_PROPERTIES_LOADED=true;
		return true;
	}
	
	public static synchronized boolean addExportJarToClassPath()
	{
		if (IS_WEBSTART) return true;
		if (!GEOGEBRA_EXPORT_PRESENT) return false;
		if (GEOGEBRA_EXPORT_LOADED) return true;
		if (app.getApplet()==null)
		{
			// not applet
			if (!copyExportJarToTempDir()) return false;
			addJarToPath(System.getProperty("java.io.tmpdir") + "geogebra_export.jar",geogebra.gui.menubar.MenubarImpl.class.getClassLoader());
		}
		else
		{
			// applet
			addJarToPath("geogebra_export.jar",geogebra.gui.menubar.MenubarImpl.class.getClassLoader());	
		}
		GEOGEBRA_EXPORT_LOADED=true;
		return true;
	}
	
	public static synchronized boolean copyCasJarToTempDir()
	{
		if (CASJAR_COPIED) return true;
		CASJAR_COPIED=copyJarToTempDir("geogebra_cas.jar");
		return CASJAR_COPIED;
	}
	
	public static synchronized boolean addCasJarToClassPath()
	{
		if (IS_WEBSTART) return true;
		if (!GEOGEBRA_CAS_PRESENT) return false;
		if (GEOGEBRA_CAS_LOADED) return true;
		if (app.getApplet()==null)
		{
			// not applet
			if (!copyCasJarToTempDir()) return false;
			addJarToPath(System.getProperty("java.io.tmpdir") + "geogebra_cas.jar",null);
		}
		else
		{
			// applet
			addJarToPath("geogebra_cas.jar",null);	
		}
		GEOGEBRA_CAS_LOADED=true;
		return true;
	}
	/*
	public static synchronized boolean copyGuiJarToTempDir()
	{
		if (GUIJAR_COPIED) return true;
		GUIJAR_COPIED=copyJarToTempDir("geogebra_gui.jar");
		return GUIJAR_COPIED;
	}
	
	public static synchronized boolean addGuiJarToClassPath()
	{
		if (IS_WEBSTART) return true;
		if (!GEOGEBRA_GUI_PRESENT) return false;
		if (GEOGEBRA_GUI_LOADED) return true;
		if (app.getApplet()==null)
		{
			// not applet
			if (!copyGuiJarToTempDir()) return false;
			addJarToPath(System.getProperty("java.io.tmpdir") + "geogebra_gui.jar",null);
		}
		else
		{
			// applet
			addJarToPath("geogebra_gui.jar",null);	
		}
		GEOGEBRA_GUI_LOADED=true;
		return true;
	}*/
	
	/*
	public static synchronized boolean copySpreadsheetJarToTempDir()
	{
		if (SPREADSHEETJAR_COPIED) return true;
		SPREADSHEETJAR_COPIED=copyJarToTempDir("geogebra_spreadsheet.jar");
		return SPREADSHEETJAR_COPIED;
	}
	
	public static synchronized boolean addSpreadsheetJarToClassPath()
	{
		if (IS_WEBSTART) return true;
		if (!GEOGEBRA_SPREADSHEET_PRESENT) return false;
		if (GEOGEBRA_SPREADSHEET_LOADED) return true;
		if (app.getApplet()==null)
		{
			// not applet
			if (!copySpreadsheetJarToTempDir()) return false;
			addJarToPath(System.getProperty("java.io.tmpdir") + "geogebra_spreadsheet.jar",geogebra.gui.menubar.MenubarImpl.class.getClassLoader());
		}
		else
		{
			// applet
			//addJarToPath("geogebra_spreadsheet.jar",null);	
			
			addJarToPath("geogebra_spreadsheet.jar",geogebra.Application.class.getClassLoader());	
			//addJarToPath("geogebra_spreadsheet.jar",geogebra.gui.GeoGebraPreferences.class.getClassLoader());	
			//addJarToPath("geogebra_spreadsheet.jar",geogebra.gui.menubar.MenubarImpl.class.getClassLoader());	
		}
		GEOGEBRA_SPREADSHEET_LOADED=true;
        // init spreadsheet view
     	//spreadsheetView = new SpreadsheetView(app, 26, 100);
		return true;
	}
	
	public static synchronized void initSpreadsheetView() {
        // init spreadsheet view
		if (spreadsheetView != null) return;
     	spreadsheetView = new SpreadsheetView(app, 26, 100);
		
	}*/
}
