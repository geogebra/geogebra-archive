/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/**
 * Used as internal return type in Parser.
 * Stores a label. 
 */ 
package geogebra.kernel.arithmetic;

import java.util.Vector;

public abstract class ValidExpression {                
        
    private Vector labels;
            
    public void addLabel(String label) {  
    	initLabels();
        labels.add(label);
    }   
    
    private void initLabels() {
    	if (labels == null)
    		labels = new Vector();
    }
    
    public void addNullLabel() {
    	initLabels();
    	labels.add(null);
    }
    
    public void addLabel(Vector labellist) { 
    	initLabels();
        labels.addAll(labellist);
    }     
    
    public int labelCount() {
    	if (labels == null) 
    		return 0;
    	else
    		return labels.size();
    }
    
    public String getLabel(int index) {
        if (index < 0 || index >= labelCount()) 
        	return null;
        else 
        	return (String) labels.get(index);
    }
    
    public String [] getLabels() {
    	int size = labelCount();
    	if (size == 0) 
    		return null;
    	
        String [] ret = new String[size];
        for (int i=0; i < size; i++) {
            ret[i] = (String) labels.get(i);
        }
        return ret;
    }
    
    public String getLabel() {
        return getLabel(0);
    }
    
    public void setLabel(String label) {
    	initLabels();
        labels.clear();
        labels.add(label);
    }
    
	public void setLabels(String [] str) {
		initLabels();
		labels.clear();
		if (str == null) return;
		for (int i=0; i < str.length; i++) {
			labels.add(str[i]);
		}
	}
	
	final public boolean isVariable() {
		return false;
	}   
	 
	final public boolean isGeoElement() {
	   return false;
	}
}