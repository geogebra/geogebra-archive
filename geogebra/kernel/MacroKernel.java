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

import geogebra.algebra.AlgebraController;

/**
 * Kernel with its own construction for macros.
 */
public class MacroKernel extends Kernel  {

	private Kernel parentKernel;
	private MacroConstruction macroCons;
	private AlgebraController algCtrl;
	
	public MacroKernel(Kernel parentKernel) {
		this.parentKernel = parentKernel;
		app = parentKernel.app;
		
		macroCons = new MacroConstruction(this);
		cons = macroCons;
		cons.initUndoInfo(); // needed for loadXML
	}
	
	public Kernel getParentKernel() {
		return parentKernel;
	}
	
	public AlgebraController getAlgebraController() {
		if (algCtrl == null) {
			algCtrl = new AlgebraController(this);
		}
		return algCtrl;
	}
	
	public void addReservedLabel(String label) {
		macroCons.addReservedLabel(label);
	}
	
	public void setGlobalVariableLookup(boolean flag) {
		macroCons.setGlobalVariableLookup(flag);
	}
	
	/**
	 * Sets macro construction of this kernel via XML string.	 
	 * @return success state
	 */
	public void loadXML(String xmlString) throws Exception {
		macroCons.loadXML(xmlString);
	}
	
	final public GeoElement lookupLabel(String label) {
		return macroCons.lookupLabel(label);
	}

	final double getXmax() {
		return parentKernel.getXmax();
	}
	final double getXmin() {
		return parentKernel.getXmin();
	}
	final double getXscale() {
		return parentKernel.getXscale();
	}
	final double getYmax() {
		return parentKernel.getYmax();
	}
	final double getYmin() {
		return parentKernel.getYmin();
	}
	final double getYscale() {
		return parentKernel.getYscale();
	}	
	
	
	/**
	 * Adds a new macro to the parent kernel.
	 */
	public void addMacro(Macro macro) {
		parentKernel.addMacro(macro);
	}
	
	/**
	 * Returns the macro object for the given macro name.
	 * Note: null may be returned.
	 */
	public Macro getMacro(String name) {
		return parentKernel.getMacro(name);	
	}			
	
}
