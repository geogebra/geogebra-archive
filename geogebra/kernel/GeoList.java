/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * GeoLine.java
 *
 * Created on 30. August 2001, 17:39
 */

package geogebra.kernel;

import java.util.ArrayList;


/**
 * List of GeoElements
 */
public class GeoList extends GeoElement {

	private static final long serialVersionUID = 1L;
	private static String STR_OPEN = "{";
	private static String STR_CLOSE = "}";
	
	private ArrayList geoList;	  
	private boolean isDefined = true;
    
    public GeoList(Construction c) { 
    	super(c);     	
    	geoList = new ArrayList();
    }
             
    public GeoList(GeoList list) {
    	super(list.cons);
        set(list);
    }
    
    String getClassName() {
    	return "GeoList";
    }
    
    String getTypeString() {
		return "List";
	}
      
    public GeoElement copy() {
        return new GeoList(this);        
    } 
    
    public void set(GeoElement geo) {
    	// TODO: check
        GeoList l = (GeoList) geo;  
        
        // copy geoList        
        geoList = new ArrayList(l.geoList.size());
        geoList.addAll(l.geoList);        	                
    }    
    
    
        
    public boolean isDefined() {
    	//TODO: change
        return isDefined;  
    }
    
    public void setDefined(boolean flag) {
    	isDefined = flag;
    }
    
    public void setUndefined() {
    	setDefined(false);
    }
        
    boolean showInEuclidianView() {
        // TODO: change
        return false;
    }
    
    boolean showInAlgebraView() {
        // TODO: change
        return true;        
    }                
    
    public final void clear() {
    	geoList.clear();
    }
    
    public final void add(GeoElement geo) {
    	geoList.add(geo);
    }
    
    public final void remove(GeoElement geo) {
    	geoList.remove(geo);
    }
    
    /**
     * Returns the element at the specified position in this list.
     */
    public GeoElement get(int index) {
    	return (GeoElement) geoList.get(index);
    }
    
    public final int size() {
    	return geoList.size();
    }
            
    public String toString() {       
    	sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");         
		sbToString.append(buildValueString());
		return sbToString.toString();   
    }
	StringBuffer sbToString = new StringBuffer(50);   
	
	public String toValueString() {
		return buildValueString().toString();
	}
    
    private StringBuffer buildValueString() {		                               		 
       sbBuildValueString.setLength(0);
       sbBuildValueString.append(STR_OPEN);
       
       // first (n-1) elements
       int lastIndex = geoList.size()-1;
       for (int i=0; i < lastIndex; i++) {
    	   GeoElement geo = (GeoElement) geoList.get(i);
    	   sbBuildValueString.append(geo.getLabel());
    	   sbBuildValueString.append(", ");
       }
       
       // last element
       GeoElement geo = (GeoElement) geoList.get(lastIndex);
	   sbBuildValueString.append(geo.getLabel());
	   
       sbBuildValueString.append(STR_CLOSE);       
       return sbBuildValueString;   	
    }        
	private StringBuffer sbBuildValueString = new StringBuffer(50);     
        
 
    /**
     * returns all class-specific xml tags for saveXML
     */
    String getXMLtags() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.getXMLtags());
	  
        
        // TODO: geolist xml saving
/*
        sb.append("\t<eqnStyle style=\"parametric\" parameter=\"");
                sb.append(parameter);
                sb.append("\"/>\n");
                break;
  */
        return sb.toString();   
    }
    
	
	public boolean isGeoList() {
		return true;
	}
    		
}