/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import java.util.ArrayList;
import java.util.HashMap;

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
	
	public void removeAllMacros() {
		macroMap.clear();
		macroList.clear();
	}
	

	/**
	 * Sets the command name of a macro.
	 */
	public void setMacroCommandName(Macro macro, String cmdName) {
		macroMap.remove(macro.getCommandName());
		macro.setCommandName(cmdName);
		macroMap.put(macro.getCommandName(), macro);			
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
	
	public void setAllMacrosUnused() {
		for (int i=0; i < macroList.size(); i++) {
			((Macro) macroList.get(i)).setUnused();				
		}
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
	public ArrayList getAllMacros() {
		return macroList;
	}
	
	/**
	 * Returns an XML represenation of the specified macros in this kernel.	 
	 */
	public static String getMacroXML(ArrayList macros) {				
		if (macros == null) return "";

		StringBuffer sb = new StringBuffer();	
		// save selected macros
		for (int i=0; i < macros.size(); i++) {				
			sb.append(((Macro) macros.get(i)).getXML());
		}						
		return sb.toString();
	}

}
