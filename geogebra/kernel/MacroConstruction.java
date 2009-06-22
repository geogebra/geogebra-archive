/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
    final GeoElement lookupLabel(String label, boolean autoCreate) {
    	if (label == null) return null;
    	
    	// local var handling
        if (!localVariableTable.isEmpty()) {        	
        	GeoElement localGeo = (GeoElement) localVariableTable.get(label);        
            if (localGeo != null) return localGeo;
        }
    	    	       
        // global var handling        
        GeoElement geo = geoTabelVarLookup(label);

        if (geo == null && globalVariableLookup && !isReservedLabel(label)) {
        	// try parent construction        	
        	 geo =  parentCons.lookupLabel(label, autoCreate); 
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
