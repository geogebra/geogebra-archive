/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.kernel;

import java.util.HashMap;

/**
 * Manages macros (user defined tools).
 * 
 * @author Markus Hohenwarter
 */
public class MacroManager {
	
	private HashMap macroMap; // maps macro name to macro object
	
	public MacroManager() {
		macroMap = new HashMap();
	}
		
	public void addMacro(Macro macro) {						
		macroMap.put(macro.getName(), macro);
	}
	
	public Macro getMacro(String name) {
		return (Macro) macroMap.get(name);
	}
	
	public void removeMacro(Macro macro) {
		macroMap.remove(macro.getName());		
	}	
	
	/**
	 * Returns the current number of macros handled by this MacroManager. 
	 */
	public int getMacroNumber() {
		return macroMap.size();
	}

}
