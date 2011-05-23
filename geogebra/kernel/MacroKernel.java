/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.kernel;

import geogebra.io.MyXMLHandler;
import geogebra.kernel.arithmetic.ExpressionNodeEvaluator;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.main.MyError;

import java.util.LinkedHashMap;



/**
 * Kernel with its own construction for macros.
 */
public class MacroKernel extends Kernel  {

	private Kernel parentKernel;
	private MacroConstruction macroCons;
	
	public MacroKernel(Kernel parentKernel) {
		this.parentKernel = parentKernel;
		
		app = parentKernel.app;
		setUndoActive(false);
		setAllowVisibilitySideEffects(false);
		
		macroCons = new MacroConstruction(this);
		cons = macroCons;	
		
		//does 3D as parentKernel
		setManager3D(getParentKernel().newManager3D(this));
	}
	
	public final boolean isMacroKernel() {
		return true;
	}
	
	public Kernel getParentKernel() {
		return parentKernel;
	}		
	
	public void addReservedLabel(String label) {
		macroCons.addReservedLabel(label);
	}
	
	void setGlobalVariableLookup(boolean flag) {
		macroCons.setGlobalVariableLookup(flag);
	}
	
	/**
	 * Sets macro construction of this kernel via XML string.	 
	 * @return success state
	 */
	public void loadXML(String xmlString) throws Exception {
		macroCons.loadXML(xmlString);
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
	
	
	////////////////////////////////////////
	// METHODS USING KERNEL3D
	////////////////////////////////////////
	
	public MyXMLHandler newMyXMLHandler(Construction cons){
		return parentKernel.newMyXMLHandler(this, cons);		
	}
	
	
	protected AlgebraProcessor newAlgebraProcessor(Kernel kernel){
		return parentKernel.newAlgebraProcessor(kernel);
	}
	
	protected ExpressionNodeEvaluator newExpressionNodeEvaluator(){
		return parentKernel.newExpressionNodeEvaluator();
	}
	
	public GeoElement createGeoElement(Construction cons, String type) throws MyError {    
		return parentKernel.createGeoElement(cons, type);
	}
	
	public boolean handleCoords(GeoElement geo, LinkedHashMap<String, String> attrs) {
		return parentKernel.handleCoords(geo, attrs);
	}
	
}
