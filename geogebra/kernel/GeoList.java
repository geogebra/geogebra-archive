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

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ListValue;
import geogebra.kernel.arithmetic.MyList;

import java.awt.Color;
import java.util.ArrayList;


/**
 * List of GeoElements
 */
public class GeoList extends GeoElement implements ListValue {
	
	public final static int LIST_TYPE_MIXED = 0;
	public final static int LIST_TYPE_NUMBER_VALUE = 1;
	public final static int LIST_TYPE_VECTOR_VALUE = 2;

	private static final long serialVersionUID = 1L;
	private static String STR_OPEN = "{";
	private static String STR_CLOSE = "}";
	
	private ArrayList geoList = new ArrayList();	  
	private boolean isDefined = true;
    
    public GeoList(Construction c) { 
    	super(c);     	    	    	    
    }
             
    public GeoList(GeoList list) {
    	this(list.cons);
        set(list);
    }
    
    String getClassName() {
    	return "GeoList";
    }
    
    String getTypeString() {
		return "List";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_LIST;
    }
      
    public GeoElement copy() {
        return new GeoList(this);        
    } 
    
    public void set(GeoElement geo) {
        GeoList l = (GeoList) geo;                 
               
		if (l.cons != cons && isAlgoMacroOutput) {
			// MACRO CASE
			// this object is an output object of AlgoMacro
			// we need to check the references to all geos in the list
			AlgoMacro algoMacro = (AlgoMacro) getParentAlgorithm();
			algoMacro.initList(l, this);			
		} 
		else {
			// STANDARD CASE
			// copy geoList                
	        int size = l.geoList.size();
	        geoList.clear();
	        geoList.ensureCapacity(size);
	        for (int i=0; i < size; i++) {
	        	geoList.add(l.geoList.get(i));
	        }
		}
    }    
    
    public void setVisualStyle(GeoElement geo) {
    	// TODO: think about setVisualStyle() for lists
    	
    	/*
    	if (geoList == null) return;    	
    	int size = geoList.size();	        
        for (int i=0; i < size; i++) {
        	((GeoElement) geoList.get(i)).setVisualStyle(geo);
        }*/
    }
    
    public void setObjColor(Color color) {
    	super.setObjColor(color);
    	
    	if (geoList == null) return;    	
    	int size = geoList.size();	        
        for (int i=0; i < size; i++) {
        	((GeoElement) geoList.get(i)).setObjColor(color);
        }    	    	
	}
    
    /**
     * Returns this GeoList as a MyList object.
     */
    public MyList getMyList() {        	
    	int size = geoList.size();    	
    	MyList myList = new MyList(kernel, size);
    	
    	for (int i=0; i < size; i++) {
    		myList.addListElement(new ExpressionNode(kernel, (GeoElement) geoList.get(i)));	
    	}
    	
    	return myList;
    }     
    
    /**
     * Returns all list items of this GeoList in an array.
     */
    public GeoElement [] toArray() {        	
    	int size = geoList.size();    	
    	GeoElement [] geos = new GeoElement[size];
    	
    	for (int i=0; i < size; i++) {
    		geos[i] = (GeoElement) geoList.get(i);	
    	}
    	
    	return geos;
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
    
    public final void remove(int index) {
    	geoList.remove(index);
    }
    
    /**
     * Returns the element at the specified position in this list.
     */
    final public GeoElement get(int index) {
    	return (GeoElement) geoList.get(index);
    }       
    
    final public void ensureCapacity(int size) {
    	geoList.ensureCapacity(size);
    }
    
    final public int size() {
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
       if (lastIndex > -1) {
	       for (int i=0; i < lastIndex; i++) {
	    	   GeoElement geo = (GeoElement) geoList.get(i);
	   		   sbBuildValueString.append(geo.toValueString());
	    	   sbBuildValueString.append(", ");
	       }
	       
	       // last element
	       GeoElement geo = (GeoElement) geoList.get(lastIndex);
		   sbBuildValueString.append(geo.toValueString());
       }
	   
       sbBuildValueString.append(STR_CLOSE);       
       return sbBuildValueString;   	
    }        
	private StringBuffer sbBuildValueString = new StringBuffer(50);                  
    
	
	public boolean isGeoList() {
		return true;
	}
	
	public boolean isListValue() {
		return true;
	}
	
	/**
	   * save object in xml format
	   */ 
	  public final String getXML() {
		 StringBuffer sb = new StringBuffer();
		 
		 // an independent list needs to add
		 // its expression itself
		 // e.g. {1,2,3}
		 if (isIndependent()) {
			sb.append("<expression");
				sb.append(" label =\"");
				sb.append(label);
				sb.append("\" exp=\"");
				sb.append(toString());
				// expression   
			sb.append("\"/>\n");
		 }
	  		  
		  sb.append("<element"); 
			  sb.append(" type=\"list\"");
			  sb.append(" label=\"");
			  sb.append(label);
		  sb.append("\">\n");
		  sb.append(getXMLtags());
		  sb.append("</element>\n");
		  
		  return sb.toString();
	  }
    		
}