/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ListValue;
import geogebra.kernel.arithmetic.MyList;
import geogebra.util.Util;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;


/**
 * List of GeoElements
 */
public class GeoList extends GeoElement implements ListValue, LineProperties, PointProperties, TextProperties {
	
	public final static int ELEMENT_TYPE_MIXED = -1;

	private static final long serialVersionUID = 1L;
	private static String STR_OPEN = "{";
	private static String STR_CLOSE = "}";
	
	private ArrayList condListenersShowObject; // Michael Borcherds 2008-04-02

	
	// GeoElement list members
	private ArrayList geoList;	  
	
 	// lists will often grow and shrink dynamically,
	// so we keep a cacheList of all old list elements
	private ArrayList cacheList;	  		
	
	private boolean isDefined = true;
	private boolean isDrawable = true;
	private int elementType = ELEMENT_TYPE_MIXED;
	
	private ArrayList colorFunctionListener; // Michael Borcherds 2008-04-02
    
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
    
    protected String getClassName() {
    	return "GeoList";
    }
    
    protected String getTypeString() {
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
        
		if (l.cons != cons && isAlgoMacroOutput()) {
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
        geoList.clear();      
        
    	for (int i=0; i < otherListSize; i++) {    		    		    		
    		GeoElement otherElement = otherList.get(i);
    		GeoElement thisElement = null;
    		
    		//  try to reuse cached GeoElement
    		if (i < cacheList.size()) {      				
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
			ret.setParentAlgorithm(getParentAlgorithm());			
			return ret;		
		}
	}
	
	private void applyVisualStyle(GeoElement geo) {
		
		if (!geo.isLabelSet()) {
			geo.setObjColor(this.getObjectColor());	
			
			geo.setLineThickness(this.getLineThickness());
			geo.setLineType(this.getLineType());
			
			if (geo instanceof PointProperties) {
				((PointProperties)geo).setPointSize(this.getPointSize());
				((PointProperties)geo).setPointStyle(this.getPointStyle());
			}
			
			if (geo instanceof TextProperties) {
				((TextProperties)geo).setFontSize(this.getFontSize());
				((TextProperties)geo).setFontStyle(this.getFontStyle());
				((TextProperties)geo).setSerifFont(this.isSerifFont());
				if (useSignificantFigures)
					((TextProperties)geo).setPrintFigures(this.getPrintFigures(),false);
				else
					((TextProperties)geo).setPrintDecimals(this.getPrintDecimals(),false);

			}
			
			geo.setAlphaValue(this.getAlphaValue());
			
			geo.setLayer(this.getLayer());
			
			// copy ShowObjectCondition, unless it generates a CirclularDefinitionException
			try { geo.setShowObjectCondition(this.getShowObjectCondition());}
			catch (Exception e) {}

			setElementEuclidianVisible(geo, isSetEuclidianVisible());  
		}
	}
	
	public final void setColorFunction(GeoList col) {
		super.setColorFunction(col);
		
    	if (geoList == null || geoList.size() == 0) return;    
    	
    	int size = geoList.size();	        
        for (int i=0; i < size; i++) {
			GeoElement geo = (GeoElement)geoList.get(i);
			if (!geo.isLabelSet())
        		geo.setColorFunction(col);
        }
			
	}
	
	public final void setLayer(int layer) {
		super.setLayer(layer);
		
    	if (geoList == null || geoList.size() == 0) return;    
    	
    	int size = geoList.size();	        
        for (int i=0; i < size; i++) {
			GeoElement geo = (GeoElement)geoList.get(i);
			if (!geo.isLabelSet())
        		geo.setLayer(layer);
        }
			
	}
	
	public final void setShowObjectCondition(GeoBoolean bool) 
	throws CircularDefinitionException {
		super.setShowObjectCondition(bool);
		
    	if (geoList == null || geoList.size() == 0) return;    
    	
    	int size = geoList.size();	        
        for (int i=0; i < size; i++) {
			GeoElement geo = (GeoElement)geoList.get(i);
			if (!geo.isLabelSet())
				geo.setShowObjectCondition(bool);
        }
			
	}

    
    public void setVisualStyle(GeoElement style) {
    	super.setVisualStyle(style);
    	
    	if (geoList == null || geoList.size() == 0) return;    	
    	
    	int size = geoList.size();	        
        for (int i=0; i < size; i++) {
			GeoElement geo = (GeoElement)geoList.get(i);
			if (!geo.isLabelSet())
				geo.setVisualStyle(style);
        }
    } 
    
    public void setObjColor(Color color) {        	
    	super.setObjColor(color);
    	  	
    	if (geoList == null || geoList.size() == 0) return;    	

    	int size = geoList.size();	        
        for (int i=0; i < size; i++) {
        	GeoElement geo = get(i);
        	if (!geo.isLabelSet())
        		geo.setObjColor(color);
        }    	    	
	}
    
    public void setEuclidianVisible(boolean visible) {
    	super.setEuclidianVisible(visible);
    	 	
    	if (geoList == null || geoList.size() == 0) return;    	

    	int size = geoList.size();	        
        for (int i=0; i < size; i++) {
        	GeoElement geo = get(i);
        	setElementEuclidianVisible(geo, visible);        
        }
	}
    
    private void setElementEuclidianVisible(GeoElement geo, boolean visible) {
    	if (!geo.isLabelSet() && !geo.isGeoNumeric())
    		geo.setEuclidianVisible(visible);
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
    
    protected boolean showInEuclidianView() {
        return isDefined() && isDrawable();
    }
    
    public boolean isDrawable() {
    	return isDrawable;
    }
    
    public boolean showInAlgebraView() {       
        return true;        
    }                
    
    public final void clear() {
    	geoList.clear();
    }
    
    /*
     * free up memory and set undefined
     */
    public final void clearCache() {
    	if (cacheList.size() > 0)
	    	for (int i = 0 ; i < cacheList.size() ; i++) {
	    		GeoElement geo = (GeoElement)cacheList.get(i);
	    		if (geo != null && !geo.isLabelSet())
	    			geo.remove();
	    	}
    	cacheList.clear();
    	clear();
    	setUndefined();
    	System.gc();
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
    	//if (!geo.isLabelSet())
		//  geo.setVisualStyle(this);
    	
    	
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
	    	   sbBuildValueString.append(geo.toOutputValueString());
	    	   sbBuildValueString.append(", ");
	       }
	       
	       // last element
	       GeoElement geo = (GeoElement) geoList.get(lastIndex);
		   sbBuildValueString.append(geo.toOutputValueString());
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
	
	public ArrayList getMoveableParentPoints() {
		return null;
	}
	
	/**
	   * save object in XML format
	   */ 
	  public final String getXML() {
		 StringBuffer sb = new StringBuffer();
		 		 
		 // an independent list needs to add
		 // its expression itself
		 // e.g. {1,2,3}
		 if (isIndependent()) {
			sb.append("<expression");
				sb.append(" label =\"");
				sb.append(Util.encodeXML(label));
				sb.append("\" exp=\"");
				sb.append(Util.encodeXML(toValueString()));			 
			sb.append("\"/>\n");
		 }
	  		  
		  sb.append("<element"); 
			  sb.append(" type=\"list\"");
			  sb.append(" label=\"");
			  sb.append(label);
		  sb.append("\">\n");
		  sb.append(getXMLtags());
		  
		  // point style
			sb.append("\t<pointSize val=\"");
				sb.append(pointSize);
			sb.append("\"/>\n");

			sb.append("\t<pointStyle val=\"");
				sb.append(pointStyle);
			sb.append("\"/>\n");

			// font settings
			if (serifFont || fontSize != 0 || fontStyle != 0) {
				sb.append("\t<font serif=\"");
				sb.append(serifFont);
				sb.append("\" size=\"");
				sb.append(fontSize);
				sb.append("\" style=\"");
				sb.append(fontStyle);
				sb.append("\"/>\n");
			}
			
			// print decimals
			if (printDecimals >= 0 && !useSignificantFigures) {
				sb.append("\t<decimals val=\"");
				sb.append(printDecimals);
				sb.append("\"/>\n");
			}
							
			// print significant figures
			if (printFigures >= 0 && useSignificantFigures) {
				sb.append("\t<significantfigures val=\"");
				sb.append(printFigures);
				sb.append("\"/>\n");
			}
		  sb.append("</element>\n");
		  
		  return sb.toString();
	  }
	  	/**
		 * Registers geo as a listener for updates
		 * of this boolean object. If this object is
		 * updated it calls geo.updateConditions()
		 * @param geo
		 */
		public void registerConditionListener(GeoElement geo) {
			if (condListenersShowObject == null)
				condListenersShowObject = new ArrayList();
			condListenersShowObject.add(geo);
		}
		
		public void unregisterConditionListener(GeoElement geo) {
			if (condListenersShowObject != null) {
				condListenersShowObject.remove(geo);
			}
		}
		
		public void registerColorFunctionListener(GeoElement geo) {
			if (colorFunctionListener == null)
				colorFunctionListener = new ArrayList();
			colorFunctionListener.add(geo);
		}
		
		public void unregisterColorFunctionListener(GeoElement geo) {
			if (colorFunctionListener != null) {
				colorFunctionListener.remove(geo);
			}
		}
		

		/**
		 * Calls super.update() and update() for all registered condition listener geos.	
		 * 	// Michael Borcherds 2008-04-02 
		 */
		public void update() {  	
			super.update();
			// update all registered locatables (they have this point as start point)
			if (colorFunctionListener != null) {
				//Application.debug("GeoList update listeners");
				for (int i=0; i < colorFunctionListener.size(); i++) {
					GeoElement geo = (GeoElement) colorFunctionListener.get(i);		
					kernel.notifyUpdate(geo);
					//geo.toGeoElement().updateCascade();
				}		
			}
		}
		/**
		 * Tells conidition listeners that their condition is removed
		 * and calls super.remove()
		 * 	// Michael Borcherds 2008-04-02
		 */
		protected void doRemove() {
			if (colorFunctionListener != null) {
				// copy conditionListeners into array
				Object [] geos = colorFunctionListener.toArray();	
				colorFunctionListener.clear();
				
				// tell all condition listeners 
				for (int i=0; i < geos.length; i++) {		
					GeoElement geo = (GeoElement) geos[i];
					geo.removeColorFunction();				
					kernel.notifyUpdate(geo);			
				}			
			}
			
			super.doRemove();
		}
		/**
		 * return whether this list equals GeoList list 
		 * Michael Borcherds 2008-04-12
		 */
		final public boolean isEqual(GeoElement geo) {
			
		if (!geo.isGeoList()) return false;
		
		GeoList list = (GeoList)geo;
		
		// check sizes
		if (geoList.size() != list.size()) return false;
		
		// check each element
		for (int i=0 ; i<list.geoList.size() ; i++)
		{
			GeoElement geoA=(GeoElement)geoList.get(i);
			GeoElement geoB=list.get(i);
			
			if (!geoA.isEqual(geoB)) return false;
			/*
			if (geoA.isGeoNumeric() && geoB.isGeoNumeric()) 
			{
				if (!((GeoNumeric)geoA).equals((GeoNumeric)geoB)) return false; 
			}
			else if (geoA.isGeoConicPart() && geoB.isGeoConicPart()) 
			{
				if (!((GeoConicPart)geoA).equals((GeoConicPart)geoB)) return false; 
			}
			else if (geoA.isGeoConic() && geoB.isGeoConic()) 
			{
				if (!((GeoConic)geoA).equals((GeoConic)geoB)) return false; 
			}
			else if (geoA.isGeoAngle() && geoB.isGeoAngle()) 
			{
				if (!((GeoAngle)geoA).equals((GeoAngle)geoB)) return false; 
			}
			else if (geoA.isGeoPoint() && geoB.isGeoPoint()) 
			{
				if (!((GeoPoint)geoA).equals((GeoPoint)geoB)) return false; 
			}
			else if (geoA.isGeoPolygon() && geoB.isGeoPolygon()) 
			{
				if (!((GeoPolygon)geoA).equals((GeoPolygon)geoB)) return false; 
			}
			else if (geoA.isGeoSegment() && geoB.isGeoSegment()) 
			{
				if (!((GeoSegment)geoA).equals((GeoSegment)geoB)) return false; 
			}
			else if (geoA.isGeoList() && geoB.isGeoList()) 
			{
				if (!((GeoList)geoA).equals((GeoList)geoB)) return false; 
			}
			else if (!geoA.equals(geoB)) return false;*/
		}
		
		// all list elements equal
		return true;
		}
		public void setZero() {
			geoList.clear();
		}
		
		public void setLineThickness(int thickness) {
			
			super.setLineThickness(thickness);
			
			if (geoList == null || geoList.size() == 0) return;
			
			for (int i=0 ; i < geoList.size() ; i++) {
				GeoElement geo = (GeoElement)geoList.get(i);
				 if (!geo.isLabelSet())
					 geo.setLineThickness(thickness);
			}
			
			//Application.debug("GeoList.setLineThickness "+thickness);
		}
		public int getLineThickness() {
			return super.getLineThickness();
		}


		public void setLineType(int type) {
			
			super.setLineType(type);
			
			if (geoList == null || geoList.size() == 0) return;

			for (int i=0 ; i < geoList.size() ; i++) {
				GeoElement geo = (GeoElement)geoList.get(i);
				 if (!geo.isLabelSet())
					 geo.setLineType(type);
			}
			
			//Application.debug("GeoList.setLineType");
		
		}

		
		
		
		public int getLineType() {
			return super.getLineType();
		}

		public int pointSize = EuclidianView.DEFAULT_POINT_SIZE; 
		private int pointStyle = -1; // use global option if -1
		
		public void setPointSize(int size) {
			pointSize = size;
			
			if (geoList == null || geoList.size() == 0) return;

			for (int i=0 ; i < geoList.size() ; i++) {
				GeoElement geo = (GeoElement)geoList.get(i);
				if (geo instanceof PointProperties && !geo.isLabelSet())
					((PointProperties)geo).setPointSize(size);
			}

			
		}
		
		public int getPointSize() {
			return pointSize;			
		}
		
		public void setPointStyle(int style) {
			
			pointStyle = style;
			
			if (geoList == null || geoList.size() == 0) return;

			for (int i=0 ; i < geoList.size() ; i++) {
				GeoElement geo = (GeoElement)geoList.get(i);
		    	if (geo instanceof PointProperties && !geo.isLabelSet())
					((PointProperties)geo).setPointStyle(style);
			}

			
		}

		public void setAlphaValue(float alpha) {
			super.setAlphaValue(alpha);
			
			if (geoList == null || geoList.size() == 0) return;

			for (int i=0 ; i < geoList.size() ; i++) {
				GeoElement geo = (GeoElement)geoList.get(i);
				 if (!geo.isLabelSet())
					geo.setAlphaValue(alpha);
			}

		
		}
		
		public int getPointStyle() {
			return pointStyle; 
		}
		
		public boolean isFillable() {
			if (geoList == null || geoList.size() == 0) return false;

			for (int i=0 ; i < geoList.size() ; i++) {
				GeoElement geo = (GeoElement)geoList.get(i);
					if (geo.isLabelSet() || !geo.isFillable()) return false;
			}
			
			return true;
		}

		
		public GeoElement getGeoElementForPropertiesDialog() {
			if (geoList.size() > 0 && elementType != ELEMENT_TYPE_MIXED) {
				return get(0).getGeoElementForPropertiesDialog(); // getGeoElementForPropertiesDialog() to cope with lists of lists
			} else {
				return this;
			}
		}
		
		/* is this a list in the form { {1,2}, {3,4}, {5,6} } etc
		 * 
		 */
		public boolean isMatrix() {
			
			if (getElementType() != GeoElement.GEO_CLASS_LIST) return false;
			
			if (size() > 0) {

				GeoElement geo0 = get(0);
				if (geo0.isGeoList()) {

					int length = ((GeoList)geo0).size();
					
					if (length == 0 || ((GeoList)geo0).getElementType() != GeoElement.GEO_CLASS_NUMERIC) {
						return false;
					}
					else
					{				
						if (size() > 0)
						for (int i = 1 ; i < size() ; i++) {
							GeoElement geoi = get(i);
							//Application.debug(((GeoList)geoi).get(0).getGeoClassType()+"");
							if (!get(i).isGeoList() || ((GeoList)geoi).size() == 0 || 
								((GeoList)geoi).getElementType() != GeoElement.GEO_CLASS_NUMERIC ||
								((GeoList)geoi).size() != length)
									return false;
						}
					}
				}
				
			}
		
			return true;
		}
		
		// font options
		private boolean serifFont = false;
		private int fontStyle = Font.PLAIN;
		private int fontSize = 0; // size relative to default font size
		private int printDecimals = -1;
		private int printFigures = -1;
		public boolean useSignificantFigures = false;
		
		public int getFontSize() {
			return fontSize;
		}

		public void setFontSize(int size) {
			fontSize = size;

			if (geoList == null || geoList.size() == 0) return;

			for (int i=0 ; i < geoList.size() ; i++) {
				GeoElement geo = (GeoElement)geoList.get(i);
		    	if (geo instanceof TextProperties && !geo.isLabelSet())
					((TextProperties)geo).setFontSize(size);
			}
}
		
		public int getFontStyle() {
			return fontStyle;
		}
		
		public void setFontStyle(int fontStyle) {
			this.fontStyle = fontStyle;

		
			if (geoList == null || geoList.size() == 0) return;

			for (int i=0 ; i < geoList.size() ; i++) {
				GeoElement geo = (GeoElement)geoList.get(i);
		    	if (geo instanceof TextProperties && !geo.isLabelSet())
					((TextProperties)geo).setFontStyle(fontStyle);
			}
}
		
		final public int getPrintDecimals() {
			return printDecimals;
		}
		
		final public int getPrintFigures() {
			return printFigures;
		}
		public void setPrintDecimals(int printDecimals, boolean update) {		
				this.printDecimals = printDecimals;
				for (int i=0 ; i < geoList.size() ; i++) {
					GeoElement geo = (GeoElement)geoList.get(i);
			    	if (geo instanceof TextProperties && !geo.isLabelSet())
						((TextProperties)geo).setPrintDecimals(printDecimals, update);
				}
		}
		
		public void setPrintFigures(int printFigures, boolean update) {		
				this.printFigures = printFigures;
				for (int i=0 ; i < geoList.size() ; i++) {
					GeoElement geo = (GeoElement)geoList.get(i);
			    	if (geo instanceof TextProperties && !geo.isLabelSet())
						((TextProperties)geo).setPrintFigures(printFigures, update);
				}
		}
		
		public boolean useSignificantFigures() {
			return useSignificantFigures;

		}
		
		public boolean isSerifFont() {
			return serifFont;
		}
		
		public void setSerifFont(boolean serifFont) {
			this.serifFont = serifFont;
			for (int i=0 ; i < geoList.size() ; i++) {
				GeoElement geo = (GeoElement)geoList.get(i);
		    	if (geo instanceof TextProperties && !geo.isLabelSet())
					((TextProperties)geo).setSerifFont(serifFont);
			}
		}

		public boolean isVector3DValue() {
			// TODO Auto-generated method stub
			return false;
		}
  		
}