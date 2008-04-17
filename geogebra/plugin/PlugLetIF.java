package geogebra.plugin;
/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

/**
<h3>PlugLetInterface - Interface for GeoGebra plugin modules </h3>
@author     H-P Ulven
@version    15.04.08
*/

public interface PlugLetIF {

	/** Should also implement the Singleton DP
	 *	public static PlugLetIF getInstance();  
	 * @return PlugLet instance (implementing this interface)
     */
    
    
	/** The method to run the plugin program
	 *  @param GgbAPI - The API the plugin can use
	 */
	public void execute(GgbAPI api);
	
	/** For GeoGebra to get information from the PlugLet 
	 *  @return String  with menu text
	 */
	public String getMenuText();
	
	
}//interface PlugLetIF
