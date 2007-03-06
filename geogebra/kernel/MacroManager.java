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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Manages macros (user defined tools).
 * 
 * @author Markus Hohenwarter
 */
public class MacroManager {
	
	private HashMap macroMap; // maps macro name to macro object
	private ArrayList macroList; // lists all macros
	
	public MacroManager() {
		macroMap = new HashMap();
		macroList = new ArrayList();
	}
		
	public void addMacro(Macro macro) {						
		macroMap.put(macro.getCommandName(), macro);
		macroList.add(macro);
	}
	
	public Macro getMacro(String name) {
		return (Macro) macroMap.get(name);
	}
	
	public void removeMacro(Macro macro) {
		macroMap.remove(macro.getCommandName());	
		macroList.remove(macro);
	}	
	
	public Macro getMacro(int i) {
		return (Macro) macroList.get(i);		
	}
	
	public int getMacroID(Macro macro) {		
		for (int i=0; i < macroList.size(); i++) {
			if (macro == macroList.get(i))
				return i;			
		}
		return -1;				
	}
	
	/**
	 * Returns the current number of macros handled by this MacroManager. 
	 */
	public int getMacroNumber() {
		return macroList.size();
	}
	
	/**
	 * Returns an array of all macros handled by this MacroManager. 
	 */
	public Macro [] getAllMacros() {
		int size = macroList.size();
		Macro [] macros = new Macro[size];
		for (int i=0; i < size; i++) {
			macros[i] = (Macro) macroList.get(i);
		}
		return macros;
	}
	
	/**
	 * Returns an XML represenation of the specified macros in this kernel.	 
	 */
	public String getMacroXML(Macro [] macros) {				
		if (macros == null) return "";

		StringBuffer sb = new StringBuffer();	
		// save selected macros
		for (int i=0; i < macros.length; i++) {				
			sb.append(macros[i].getXML());
		}						
		return sb.toString();
	}

}
