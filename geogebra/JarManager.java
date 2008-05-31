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

import geogebra.plugin.PluginManager;
import geogebra.util.Util;

public class JarManager {

    public static boolean 			JSMATHTEX_PRESENT=false;
    //public static boolean 			GEOGEBRA_EXPORT_PRESENT=false;
    //public static boolean 			GEOGEBRA_EXPORT_LOADED=false;
    private static PluginManager 	plugin=null;
	
	
	public JarManager(PluginManager plug) {
		
		
		if (plugin != null) return;
        plugin = plug;

        // TODO these don't work in applets
        //addJarToPath("geogebra_properties.jar");
        //addJarToPath("geogebra_export.jar");
		
       
        //GEOGEBRA_EXPORT_PRESENT = jarPresent("geogebra_export.jar")
        
        
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
        
        return (loader.getResourceAsStream(jar)!=null);
		
	}
	
	public static void addJarToPath(String jar)
	{
        if (plugin==null) return;
        plugin.addPath(jar);
	}
}
