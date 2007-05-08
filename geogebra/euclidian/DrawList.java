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
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public final class DrawList extends Drawable {
	 	
	private GeoList geoList;	
	private ArrayList listItems = new ArrayList();
	private DrawableList drawables = new DrawableList();
   
    public DrawList(EuclidianView view, GeoList geoList) {      
    	this.view = view;          
        this.geoList = geoList;
        geo = geoList;         

        update();
    }
    
    final public void update() {               
    	if (geoList.size() == listItems.size()) 
    		updateDrawables();
    	else    	
    		buildNewLists();    	
    }
    
    private void updateDrawables() {
    	DrawableIterator it = drawables.getIterator();
    	while (it.hasNext()) {
    		((Drawable) it.next()).update();    			    		
    	}           	
    }
    
    private void buildNewLists() {
    	// the size has changed     	
    	// we need to remove all old GeoElements from view
    	// and add all new ones  
    	int oldSize = listItems.size();
    	for (int i=0; i < oldSize; i++ ) {
    		view.remove((GeoElement) listItems.get(i));
    	}    	
    	
    	// build new lists
    	listItems.clear();
    	drawables.clear();
    	
    	int size = geoList.size();
    	for (int i=0; i < size; i++ ) {
    		// add all list itmes of geolist to our local list
    		GeoElement geo = (GeoElement) geoList.get(i);
    		listItems.add(geo);
    		
    		// create drawable    		
    		// check if there is already a drawable for geo
    		Drawable d = view.getDrawable(geo);
    		if (d != null) {
    			// remember existing drawable
    			drawables.add(d);
    		} else {
    			// try to create a new drawable for geo
    			d = view.createDrawable(geo);
    			if (d != null) { 
            		// new drawable was created
        			//d.setGeoElement(geoList);        			
        			drawables.add(d);
    			}
    		}    			
    	}    
    }

    final public void draw(Graphics2D g2) {   
       // nothing to do: all list items have been added
       // in update() 
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


