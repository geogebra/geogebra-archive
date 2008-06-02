package geogebra;
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
@version     2008-05-31
*/

import geogebra.plugin.ClassPathManipulator;
import geogebra.plugin.PluginManager;
import geogebra.util.Util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class JarManager {

    public static boolean 			JSMATHTEX_PRESENT=false;
    public static boolean 			GEOGEBRA_EXPORT_PRESENT=false;
    //public static boolean 			GEOGEBRA_EXPORT_LOADED=false;
    private static PluginManager 	plugin=null;
    private static Application	 	app=null;
    private String 					startdir=null;
	
	public JarManager(PluginManager plug, Application app) {
		
		

        plugin = plug;
        this.app = app;

        try {
        startdir=new File("").getCanonicalPath();
        }
        catch (Exception e) {startdir="";}
        //ClassPathManipulator.listClassPath();
        // TODO these don't work in applets
        //addJarToPath("geogebra_properties.jar");
        addJarToPath("geogebra_export.jar");
		
       
        GEOGEBRA_EXPORT_PRESENT = jarPresent("geogebra_export.jar");
        
        
		// Michael Borcherds 2008-05-30
		// add support for JMathTeX if the correct JARs are present
        // and we're running Java 1.5 or above
    	JSMATHTEX_PRESENT =  ( jarPresent("JMathTeX-0.7pre.jar")
                			&& jarPresent("jdom-1.1.jar")
                			&& Util.getJavaVersion() >= 1.5);
		System.out.println("JSMATHTEX_PRESENT="+JSMATHTEX_PRESENT);

		if (JSMATHTEX_PRESENT)
		{
			
			try {
				addJarToPath("JMathTeX-0.7pre.jar");
				addJarToPath("jdom-1.1.jar");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				JSMATHTEX_PRESENT=false;
			}
		}

	}
	
	public static boolean jarPresent(String jar)
	{
        if (plugin==null) return false;
		ClassLoader loader=plugin.getClass().getClassLoader();
        
		//URL codeBase=GeoGebraAppletBase.codeBase;
		URL codeBase=getAppletCodeBase();
        if (codeBase!=null) jar = codeBase.toString() + jar;
        
        
        //jar = "file:///C:/Personal/My%20Projects/JavaScript/testGeogebraApplets/geogebra_export.jar";
        
        boolean ret = (loader.getResourceAsStream(jar)!=null);
        
        System.out.println("jarPresent " + jar+" "+ret);
        
        return ret;
		
	}
	
	public static void addJarToPath(String jar)
	{
        if (plugin==null) return;
        
		//URL codeBase=GeoGebraAppletBase.codeBase;
		URL codeBase=getAppletCodeBase();
        if (codeBase!=null) jar = codeBase.toString() + jar;
        System.out.println("addJarToPath " + jar);
        
        //addPath(jar);
        ClassPathManipulator.addURL(addPathToJar(jar));
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
}
