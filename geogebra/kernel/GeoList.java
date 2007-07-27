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

import geogebra.euclidian.Drawable;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ListValue;
import geogebra.kernel.arithmetic.MyList;

import java.awt.Color;
import java.util.ArrayList;


/**
 * List of GeoElements
 */
public class GeoList extends GeoElement implements ListValue {
	
	public final static int ELEMENT_TYPE_MIXED = -1;

	private static final long serialVersionUID = 1L;
	private static String STR_OPEN = "{";
	private static String STR_CLOSE = "}";
	
	// GeoElement list members
	private ArrayList geoList;	  
	
 	// lists will often grow and shrink dynamically,
	// so we keep a cacheList of all old list elements
	private ArrayList cacheList;	  		
	
	private boolean isDefined = true;
	private boolean isDrawable = true;
	private int elementType = ELEMENT_TYPE_MIXED;
    
    public GeoList(Construction c) { 
    	this(c, 20);
    }
    
    private GeoList(Construction c, int size) { 
    	super(c);    	
    	geoList = new ArrayList(size);
    	cacheList = new ArrayList(size);
    	setEuclidianVisible(false);
    }
    
    public void setParentAlgorithm(AlgoElement algo) { 	    	   
    	super.setParentAlgorithm(algo);
    	setEuclidianVisible(true); 
    }
             
    public GeoList(GeoList list) {
    	this(list.cons, list.size());
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
    
    /**
     * Returns the element type of this list.
     * @return ELEMENT_TYPE_MIXED or GeoElement.GEO_CLASS_xx constant
     */
    public int getElementType() {
    	return elementType;
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
	        copyListElements(l);
		}
				  
        isDefined = l.isDefined;
        elementType = l.elementType;		
    }    
    
    private void copyListElements(GeoList otherList) {
    	int otherListSize = otherList.size();
        ensureCapacity(otherListSize);
        ensureCapacity(otherListSize);      
                     
        geoList.clear();
        int cacheListSize = cacheList.size();
        
    	for (int i=0; i < otherListSize; i++) {    		
    		GeoElement otherElement = otherList.get(i);
    		GeoElement thisElement = null;
    		
    		//  try to reuse cached GeoElement
    		if (i < cacheListSize) {        			
	    		GeoElement cachedGeo = (GeoElement) cacheList.get(i);    		    		
	    		if (!cachedGeo.isLabelSet() &&
	    			 cachedGeo.getGeoClassType() == otherElement.getGeoClassType()) 
	    		{
	    			// cached geo is unlabeled and has needed object type: use it
	    			cachedGeo.set(otherElement);	    		
	    			thisElement = cachedGeo;
	            }	    				    		    
    		} 
    		
    		// could not use cached element -> get copy element
    		if (thisElement == null) {
	    		thisElement = getCopyForList(otherElement);
    		}
    		  
    		// set list element
    		add(thisElement);    		    		
    	}            	
    }

	private GeoElement getCopyForList(GeoElement geo) {
		if (geo.isLabelSet()) {
			// take original element
			return geo;
		} else {			
			// create a copy of geo
			GeoElement ret = geo.copyInternal(cons);
			return ret;
		}
	}
	
	private void applyVisualStyle(GeoElement geo) {
		// TODO: think about setVisualStyle() for lists
		if (!geo.isLabelSet()) {
			geo.setObjColor(this.getObjectColor());		
		}
	}
    
    public void setVisualStyle(GeoElement geo) {
    	super.setVisualStyle(geo);
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
    	  	
    	int size = geoList.size();	        
        for (int i=0; i < size; i++) {
        	GeoElement geo = get(i);
        	if (!geo.isLabelSet())
        		geo.setObjColor(color);
        }    	    	
	}
    
    public void setEuclidianVisible(boolean visible) {
    	super.setEuclidianVisible(visible);
    	 	
    	int size = geoList.size();	        
        for (int i=0; i < size; i++) {
        	GeoElement geo = get(i);
        	if (!geo.isLabelSet())
        		geo.setEuclidianVisible(visible);
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
        
    final public boolean isDefined() {    	
        return isDefined;  
    }
    
    public void setDefined(boolean flag) {
    	isDefined = flag;
    }
    
    public void setUndefined() {
    	setDefined(false);
    }               
    
    boolean showInEuclidianView() {
        return isDefined() && isDrawable();
    }
    
    public boolean isDrawable() {
    	return isDrawable && !isIndependent();
    }
    
    boolean showInAlgebraView() {       
        return true;        
    }                
    
    public final void clear() {
    	geoList.clear();
    }
    
    public final void add(GeoElement geo) {
    	// add geo to end of list 
    	geoList.add(geo);
    	
    	// add to cache
    	int pos = geoList.size()-1;
    	if (pos < cacheList.size()) {
    		cacheList.set(pos, geo);  
    	} else {
    		cacheList.add(geo);
    	}    	 
    	
//    	 set visual style of this list
		applyVisualStyle(geo);
    }      
    
    final void set(int pos, GeoElement geo) {
    	geoList.set(pos, geo);
    	cacheList.set(pos, geo);
    	
    	// init element type    	    
    	if (pos == 0) {   
    		isDrawable = geo.isDrawable();
    		elementType = geo.getGeoClassType();
    	}
    	// check element type
    	else if (elementType != geo.getGeoClassType()) {    		
    		elementType = ELEMENT_TYPE_MIXED;
    	}    	    	
    	isDrawable = isDrawable && geo.isDrawable(); 
    	
    	// set visual style of this list
		applyVisualStyle(geo);
    }
       
    /**
     * Removes geo from this list. Note: geo is not removed
     * from the construction.
     */
    public final void remove(GeoElement geo) {
    	geoList.remove(geo);    	
    }
    
    /**
     * Removes i-th element from this list. Note: this element is not removed
     * from the construction.
     */
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
    	cacheList.ensureCapacity(size);
    }
    
    final public int size() {
    	return geoList.size();
    }
    
    final public int getCacheSize() {
    	return cacheList.size();
    }
    
    /**
     * Returns the cached element at the specified position in this list's cache.
     */
    final public GeoElement getCached(int index) {
    	return (GeoElement) cacheList.get(index);
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
	    	   sbBuildValueString.append(geo.toDefinedValueString());
	    	   sbBuildValueString.append(", ");
	       }
	       
	       // last element
	       GeoElement geo = (GeoElement) geoList.get(lastIndex);
		   sbBuildValueString.append(geo.toDefinedValueString());
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