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
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package geogebra.euclidian;

import geogebra.euclidian.DrawableList.DrawableIterator;
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
	private DrawableList drawables = new DrawableList();
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
    	
    	updateDrawables();       
    }
    
    private void updateDrawables() {    	          	  
    	// go through list elements and create and/or update drawables
    	int size = geoList.size();

    	// build new drawable list
    	drawables.clear();    
    	for (int i=0; i < size; i++) {    		
    		GeoElement listElement = geoList.get(i);
    		    	
    		// try to get existing drawable for list element
    		Drawable d = view.getDrawable(listElement);
    		if (d == null) {    			
    			// create a new drawable for geo
    			d = view.createDrawable(listElement);    			
    		}    
    		
    		// add drawable to list
    		if (d != null) {        			
    			drawables.add(d);
    			d.update();
			}
    	}    
    }

    final public void draw(Graphics2D g2) {   
    	if (isVisible) {
    		drawables.drawAll(g2);
    	}
    }
    
    /**
     * Returns whether any one of the list items is at the given screen position.
     */
    final public boolean hit(int x, int y) {
    	DrawableIterator it = drawables.getIterator();
    	while (it.hasNext()) {
    		if (((Drawable) it.next()).hit(x, y))
    			return true;    			
    	}       
    	return false;
    }
    
    final public boolean isInside(Rectangle rect) {
    	DrawableIterator it = drawables.getIterator();
    	while (it.hasNext()) {
    		if (!((Drawable) it.next()).isInside(rect))
    			return false;    			
    	}       
    	return true;
    }
    
    
    
    final public GeoElement getGeoElement() {
        return geo;
    }    
    
    final public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 

}


