/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.Application;

import java.util.TreeSet;

public abstract class ConstructionElement 
implements Comparable {
	
	protected transient Construction cons; // parent construction of this element
	protected transient Kernel kernel;      // parent kernel of this element
	protected transient Application app;  // parent application of this element
	
	private int constIndex = -1; // index in construction list 
	
	private static long ceIDcounter = Long.MIN_VALUE;
	private long ceID; // creation ID of this ConstructionElement, used for sorting
	
	public ConstructionElement(Construction c) {
		ceID = ceIDcounter++;	
		setConstruction(c);
	}
	

	void setConstruction(Construction c) {
		cons = c;
		kernel = c.getKernel();
		app = c.getApplication();
	}
	
	public Construction getConstruction() {
		return cons;
	}
	
	public final Kernel getKernel() {
		return kernel;
	}

	/**
	 * Returns the smallest possible construction index for this object in its construction.
	 */
	public abstract int getMinConstructionIndex();

	/**
	 * Returns the largest possible construction index for this object in its construction.
	 */	
	public abstract int getMaxConstructionIndex();
	
	/**
	 * Returns construction index in current construction.
	 */
	public int getConstructionIndex() {
		return constIndex;
	}
	
	/**
	 * Sets construction index in current construction.
	 * This method should only be called from Construction.
	 */
	void setConstructionIndex(int index) {
		constIndex = index;
	}
	
	/**
	 * Returns whether this construction element is in the
	 * construction list of its construction.	 
	 */	
	final public boolean isInConstructionList() {
		return constIndex > -1;
	}
	
	/**
	 * Returns whether this element is a breakpoint 
	 * in the construction protocol	 
	 */
	abstract public boolean isConsProtocolBreakpoint();		
	
	/**
	 * Returns whether this object is available at
	 * the given construction step (this depends on
	 * this object's construction index).
	 */
	boolean isAvailableAtConstructionStep(int step) {
		// Note: this method is overwritten by
		// GeoAxis in order to make the axes available
		// in empty constructions too (for step == -1)
		int pos = getConstructionIndex();
		return (pos >= 0 && pos <= step);
	}
	
	/**
	 * Returns true for an independent GeoElement and false otherwise.
	 */
	public abstract boolean isIndependent();
	
	/**
	 * Returns all independent predecessors (of type GeoElement) that this object depends on.
	 * The predecessors are sorted topologically.
	 */
	public abstract TreeSet getAllIndependentPredecessors();
	
	/**
	 * Returns XML representation of this object.
	 * @return xml String
	 */
    public abstract String getXML();
    
	/**
	  * Removes this object from the current construction.	 
	  */
    public abstract void remove();    
    
    /**
     * Updates this object.
     */
    abstract void update();
		
	/**
	  * Notifies all views to remove this object.	 
	  */
	public abstract void notifyRemove();			
    
	/**
	  * Notifies all views to add this object.	 
	  */
	public abstract void notifyAdd();
	
	/**
	 * Returns an array with all GeoElements of this construction element.
	 */
	public abstract GeoElement [] getGeoElements();
	
	public abstract boolean isGeoElement();
	
	public abstract boolean isAlgoElement();
	
	/**
	 * Returns type and name of this construction element (e.g. "Point A").
	 * Note: may return ""
	 */	 
	public abstract String getNameDescription();
	
	/**
	 * Returns algebraic representation (e.g. coordinates, equation)
	 * of this construction element.
	 */
	public abstract String getAlgebraDescription();
	
	/**
	 * Returns textual description of the definition
	 * of this construction element  (e.g. "Line through A and B").
	 * Note: may return ""
	 */
	public abstract String getDefinitionDescription();
	
	/**
	 * Returns command that defines
	 * this construction element  (e.g. "Line[A, B]").
	 * Note: may return ""
	 */	
	public abstract String getCommandDescription();
	
	/**
	 * Returns name of class. This is needed to allow code obfuscation.	
	 */
	abstract protected String getClassName();
	
	
	/* Comparable interface */
	
	/**
	 * Compares using creation ID. Older construction elements are larger.
	 * Note: 0 is only returned for this == obj.
	 */
    public int compareTo( Object obj) {
    	if (this == obj) return 0;
    	
    	ConstructionElement ce = (ConstructionElement) obj;   
    	if (ceID < ce.ceID)
    		return -1;
    	else
    		return 1;
    }
        
	public boolean equals(Object obj) {
		return this == obj;
	}   
	
}
