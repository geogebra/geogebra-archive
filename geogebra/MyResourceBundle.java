/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * MyResourceBundle.createBundle() should be used in stead
 * of ResourceBundle.getBundle() because it does not open
 * network connections when used with an applet.
 */

public class MyResourceBundle extends PropertyResourceBundle {	
		
	
	public MyResourceBundle(InputStream in) throws IOException {	
			super(in);
	}
	
	final  public  static ResourceBundle createBundle(String name, Locale locale) {						
		MyResourceBundle bundle, temp = null;
		
		// base properties file
		bundle = loadSingleBundleFile(name);

		// language properties file
		String lang = locale.getLanguage();
		StringBuffer sb = new StringBuffer(name);    	
    	sb.append('_');    	
    	sb.append(lang);
    	String fileNameLanguage = sb.toString();
    	
    	// load only non-English languages (English has already been loaded as base file)
    	if (!"en".equals(lang))
    		temp = loadSingleBundleFile(fileNameLanguage);
    	
    	if (temp != null) {
    		temp.setParent(bundle);
    		bundle = temp;
    	}
    	
    	// country and variant properties file
    	String country = locale.getCountry();    	
    	if (country.length() > 0) {
    		// check for variant
    		String variant = locale.getVariant();
    		if (variant.length() > 0) {
    			// country and variant
    			sb.append('_');
    			sb.append(country);
    			sb.append('_');
    			sb.append(variant);
    		} else {
    			// only country
    			sb.append('_');
        		sb.append(country);        			
    		}    		    		    		
    		
    		String fileNameLanguageCountry = sb.toString();
    		temp = loadSingleBundleFile(fileNameLanguageCountry);
    		if (temp != null) {
        		temp.setParent(bundle);
        		bundle = temp;
        	}
    	}
    	return bundle;
	}
	
    public  static MyResourceBundle loadSingleBundleFile(String name) {    	    	
    	//Application.debug("loadBundle: " + name);
    	try {    		        	    		    		
    		String fileName = name + ".properties";
    		InputStream in = MyResourceBundle.class.getResourceAsStream(fileName);    		
    		    		
    		//Application.debug("SUCCESS loadBundle : " + name);
			return new MyResourceBundle(in);
    	}
		catch (Exception e) {			
			//Application.debug("Exception: could not load bundle: " + name);
			//e.printStackTrace();
			return null;
		}
    }
    
    
        
}
