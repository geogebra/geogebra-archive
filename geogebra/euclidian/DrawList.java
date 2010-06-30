/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;


/**
 * Draw a list of objects
 * @author  Markus Hohenwarter
 * @version 
 */
public final class DrawList extends Drawable {
	 	
	private GeoList geoList;	
	private ArrayList drawables = new ArrayList();
	private boolean isVisible;
	
    public DrawList(EuclidianView view, GeoList geoList) {      
    	this.view = view;          
        this.geoList = geoList;
        geo = geoList;         

        update();
    }
    
    final public void update() {    
    	isVisible = geoList.isEuclidianVisible();
    	if (!isVisible) return;    	
    	
    	// go through list elements and create and/or update drawables
    	int size = geoList.size();
    	drawables.ensureCapacity(size);
    	int oldDrawableSize = drawables.size();
    	
    	int drawablePos = 0;
    	for (int i=0; i < size; i++) {    		
    		GeoElement listElement = geoList.get(i);
    		if (!listElement.isDrawable()) 
    			continue;
    		
    		// new 3D elements are not drawn -- TODO change that
    		if (listElement.isGeoElement3D())
    			continue;
    		
    		// add drawable for listElement
    		if (addToDrawableList(listElement, drawablePos, oldDrawableSize))
    			drawablePos++;
    		
//    		// for polygons, we also need to add drawables for the segments
//    		if (listElement.isGeoPolygon()) {
//    			GeoSegment [] segments = ((GeoPolygon) listElement).getSegments();
//    			for (int k=0; k < segments.length; k++) {
//    				// add drawable for segment
//    	    		if (addToDrawableList(segments[k], drawablePos, oldDrawableSize))
//    	    			drawablePos++;
//    			}        		    	
//    		}    		
    	}    
    	
    	// remove end of list
    	for (int i=drawables.size()-1; i >= drawablePos; i--) {      		 
    		drawables.remove(i);
    	}
    	
    	//G.Sturr 2010-6-28 spreadsheet trace is now handled in GeoElement.update()
		//if (geoList.getSpreadsheetTrace())
		   // recordToSpreadsheet(geoList);
    }
    
    private boolean addToDrawableList(GeoElement listElement, int drawablePos, int oldDrawableSize) {
    	Drawable d = null;
		boolean inOldDrawableRange = drawablePos < oldDrawableSize;
		if (inOldDrawableRange) {
			// try to reuse old drawable
    		Drawable oldDrawable = (Drawable) drawables.get(drawablePos);
    		if (oldDrawable.geo == listElement) {	
    			d = oldDrawable;
    		} else {
    			d = getDrawable(listElement);  			
    		}	    		    		    	
		} else {
			d = getDrawable(listElement); 
		}
		
		if (d != null) {
			d.update();
			if (inOldDrawableRange) {    				
				drawables.set(drawablePos, d);
			} else {
				drawables.add(drawablePos, d);
			}
			return true;
		}
		
		return false;
    }
    
    private Drawable getDrawable(GeoElement listElement) {
    	Drawable d = view.getDrawable(listElement);
		if (d == null) {    			
			// create a new drawable for geo
			d = view.createDrawable(listElement);   
			d.createdByDrawList = true;
		} 
		return d;
    }

    final public void draw(Graphics2D g2) {   
    	if (isVisible) {
    		boolean doHighlight = geoList.doHighlighting();    	
    		
    		int size = drawables.size();    		
    		for (int i=0; i < size; i++) {     			     			
    			Drawable d = (Drawable) drawables.get(i);
    			// draw only those drawables that have been created by this list;
    			// if d belongs to another object, we don't want to mess with it here
    			if (createdByDrawList || !d.geo.isLabelSet()) {
    				d.geo.setHighlighted(doHighlight);    				
    				d.draw(g2);
    			}
    		}
    	}
    }
    
    /**
     * Returns whether any one of the list items is at the given screen position.
     */
    final public boolean hit(int x, int y) {
   		int size = drawables.size();
		for (int i=0; i < size; i++) {    		
			Drawable d = (Drawable) drawables.get(i);
    		if (d.hit(x, y))
    			return true;    			
    	}       
    	return false;
    }
    
    final public boolean isInside(Rectangle rect) {
   		int size = drawables.size();
		for (int i=0; i < size; i++) {    		
			Drawable d = (Drawable) drawables.get(i);
			if (!d.isInside(rect))
    			return false;   			
    	}        		
    	return size > 0;
    }
    
    /**
	 * Returns the bounding box of this DrawPoint in screen coordinates.	 
	 */
	final public Rectangle getBounds() {
		
		if (!geo.isEuclidianVisible()) return null;

		Rectangle result = null;
		
		int size = drawables.size();
		for (int i=0; i < size; i++) {    		
			Drawable d = (Drawable) drawables.get(i);
			Rectangle bb = d.getBounds();
			if (bb != null) {
				if (result == null) 
					result = new Rectangle(bb); // changed () to (bb) bugfix, otherwise top-left of screen is always included
				// add bounding box of list element
				result.add(bb);
			}			   		
    	}        		
    	
		return result;
	}        
    
    final public GeoElement getGeoElement() {
        return geo;
    }    
    
    final public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 

}


