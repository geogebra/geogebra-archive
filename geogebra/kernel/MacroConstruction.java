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

import java.util.HashSet;

/**
 * Construction for macros.
  */
class MacroConstruction extends Construction {
	
	private Construction parentCons;
	private HashSet reservedLabels;
	private boolean globalVariableLookup = false;
	
	public MacroConstruction(MacroKernel kernel) {
		super(kernel, kernel.getParentKernel().getConstruction());
		parentCons = kernel.getParentKernel().getConstruction();
		reservedLabels = new HashSet();
	}		   
	
	/**
	 * Set construction via XML string.	 
	 * @return success state
	 */
	public void loadXML(String xmlString) throws Exception {
		if (undoManager == null)
			undoManager = new UndoManager(this);
		
		undoManager.processXML(xmlString);		
	}
	
	/**
	 * Adds label to the list of reserved labels. Such labels
	 * will not be looked up in the parent construction in lookup();
	 * @param label
	 */
	public void addReservedLabel(String label) {
		if (label != null) {			
			reservedLabels.add(label);						
		}
	}
	
    /**
     * Returns a GeoElement for the given label. Note: 
     * construction index is ignored here. If no geo is found for
     * the specified label a lookup is made in the parent construction.
     * @return may return null
     */
    final public GeoElement lookupLabel(String label) {
    	if (label == null) return null;
    	    	       
        GeoElement geo =  (GeoElement) geoTable.get(label);
        if (geo == null && globalVariableLookup && !isReservedLabel(label)) {
        	// try parent construction        	
        	 geo =  (GeoElement) parentCons.geoTable.get(label);      
        }
        return geo;                   
    }
    
    private boolean isReservedLabel(String label) {
    	return reservedLabels.contains(label);        	
    }

	public boolean isGlobalVariableLookup() {
		return globalVariableLookup;
	}

	void setGlobalVariableLookup(boolean globalVariableLookup) {
		this.globalVariableLookup = globalVariableLookup;
	}
}
