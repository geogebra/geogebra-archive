/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.euclidian;

import geogebra.Application;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import atp.sHotEqn;


/**
 * Toggle Button for free GeoBoolean object.
 * @author  Markus Hohenwarter
 * @version 
 */
public final class DrawBoolean extends Drawable implements ActionListener {
	       
    private GeoBoolean geoBool;    
    private boolean isVisible;                
    
    private JToggleButton button;       
    
    /** Creates new DrawText */
    public DrawBoolean(EuclidianView view, GeoBoolean geoBool) {      
    	this.view = view;          
        this.geoBool = geoBool;
        geo = geoBool;
             
        button = new JToggleButton();                       
        view.add(button);          
        update();
    }
    
    final public void update() {       
        isVisible = geo.isEuclidianVisible();       				         
        button.setVisible(isVisible);
        if (!isVisible) return;        
        
        // TODO: use caption instead of label
		labelDesc = geo.getLabel();							
					
		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;
		button.setLocation(xLabel, yLabel);		
		button.setText(labelDesc);
		button.setFont(view.fontPoint);
		button.setSelected(geoBool.getBoolean());
		Dimension prefSize = button.getPreferredSize();
		labelRectangle.setBounds(xLabel, yLabel, prefSize.width, prefSize.height);														             
    }

    final public void draw(Graphics2D g2) {   	   
        if (isVisible) {      
		// the button is drawn as a swing component by the view 
		// They are Swing components and children of the view           	
			  
			// draw label rectangle
			if (geo.doHighlighting()) {
				g2.setStroke(objStroke);
				g2.setPaint(Color.lightGray);		
				g2.draw(labelRectangle);         
			}   
        }
    }
    
    /**
     * Removes button from view again    
     */
	final public void remove() {    	
    	view.remove(button);
    }	
   
    /**
     * was this object clicked at? (mouse pointer
     * location (x,y) in screen coords)
     */
    final public boolean hit(int x, int y) {
		return super.hitLabel(x, y);				      
    }
    
    final public boolean isInside(Rectangle rect) {
    	return rect.contains(labelRectangle);  
    }
    
    /**
     * Returns false     
     */ 
	public boolean hitLabel(int x, int y) {
		return false;
	}
    
    final public GeoElement getGeoElement() {
        return geo;
    }    
    
    final public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }

    /**
     * Listenes for button clicks
     */
	public void actionPerformed(ActionEvent ev) {
		geoBool.setValue(button.isSelected());
		geoBool.updateRepaint();
	} 
      

}


