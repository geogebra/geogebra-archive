/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.euclidian;

import geogebra.Application;
import geogebra.JarManager;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import hoteqn.sHotEqn;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.JLabel;

import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXFormula;



/**
 *
 * @author  Markus
 * @version 
 */
public final class DrawText extends Drawable {
	 
	//private static final int SELECTION_DIAMETER_ADD = 4;     
	//private static final int SELECTION_OFFSET = SELECTION_DIAMETER_ADD / 2;
	       
    private GeoText text;    
    boolean isVisible, isLaTeX;  
    private boolean serifFont = false;
    private int fontSize = -1;
    private int fontStyle = -1;
    private Font textFont;
    private GeoPoint loc; // text location
    
    private sHotEqn eqn;
    //private Image eqnImage;
    private Dimension eqnSize;        
    
    /** Creates new DrawText */
    public DrawText(EuclidianView view, GeoText text) {      
    	this.view = view;          
        this.text = text;
        geo = text;
        
        textFont = view.fontPoint;
          
        update();
    }
    
    final public void update() {       
        isVisible = geo.isEuclidianVisible();       				 
        if (eqn != null)
    		eqn.setVisible(isVisible);
        if (!isVisible) return;          
        
        String newText = text.getTextString();
       // boolean textChanged = labelDesc == null || !labelDesc.equals(newText);
		labelDesc = newText;
		isLaTeX = text.isLaTeX();			
        		
        // compute location of text		
		if (text.isAbsoluteScreenLocActive()) {
			xLabel = text.getAbsoluteScreenLocX();
			yLabel = text.getAbsoluteScreenLocY(); 
		} else {
			loc = text.getStartPoint();
	        if (loc == null) {
				xLabel = (int) view.xZero;
				yLabel = (int) view.yZero;
	        } else {
	        	if (!loc.isDefined()) {
	        		isVisible = false;
	        		return;
	        	}
				xLabel = view.toScreenCoordX(loc.inhomX);
				yLabel = view.toScreenCoordY(loc.inhomY);        	
	        }
	        xLabel += text.labelOffsetX;
			yLabel += text.labelOffsetY; 
			
		}        
		
		/*
		// use hotEqn for LaTeX
		if (isLaTeX && eqn == null && !JarManager.JSMATHTEX_LOADED) {
				eqn = new sHotEqn();
				eqn.setDoubleBuffered(false);
				eqn.setEditable(false);	
				eqn.removeMouseListener(eqn);
				eqn.removeMouseMotionListener(eqn);				
				eqn.setDebug(false);
				eqn.setOpaque(false);	
				//eqn.setFont(view.getFont());		
				
				eqn.setFontname(Application.STANDARD_FONT_NAME);
				setEqnFontSize();																												
				view.add(eqn);					
		}*/
		
		updateFontSize();				
			
		// avoid unnecessary updates of LaTeX equation
		if (isLaTeX) {
			
			if (JarManager.JSMATHTEX_LOADED)
			{
				TeXFormula formula;
				Icon icon;
				try{
					formula = new TeXFormula(labelDesc);
					icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,fontSize);
				}
				catch (Exception e)
				{
					formula = new TeXFormula("LaTeXerror");
					icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,fontSize);
					
				}

				labelRectangle.setBounds(xLabel, yLabel, icon.getIconWidth(), icon.getIconHeight());										
			}
			else
			{
				eqn = new sHotEqn();
				eqn.setDoubleBuffered(false);
				eqn.setEditable(false);	
				eqn.removeMouseListener(eqn);
				eqn.removeMouseMotionListener(eqn);				
				eqn.setDebug(false);
				eqn.setOpaque(false);	
				
				eqn.setFontname(Application.STANDARD_FONT_NAME);
				setEqnFontSize();																												
				eqn.setForeground(geo.getObjectColor());		
				eqn.setBackground(view.getBackground());
	
//			// set equation 
//			eqn.setEquation(labelDesc);
//			
//			// draw equation once to get it's image and size
//			eqn.paintComponent(view.getTempGraphics2D());
//			eqnImage = eqn.getImage();
//			eqnSize = eqn.getSize();
				
				eqnSize = eqn.getSizeof(labelDesc);

				// set bounding box of text component
				labelRectangle.setBounds(xLabel, yLabel, eqnSize.width, eqnSize.height);										
				eqn.setBounds(labelRectangle);		
			}
		} 
		else if (text.isNeedsUpdatedBoundingBox()) {
			// ensure that bounding box gets updated by drawing text once
			drawMultilineText(view.getTempGraphics2D());	
		}
		
		if (text.isNeedsUpdatedBoundingBox()) {
			// Michael Borcherds 2007-11-26 BEGIN update corners for Corner[] command
			double xRW = view.toRealWorldCoordX(labelRectangle.x);
			double yRW = view.toRealWorldCoordY(labelRectangle.y);		
			Rectangle2D rect2=new Rectangle2D.Double(
					xRW, yRW,				                                 
					view.toRealWorldCoordX(labelRectangle.x+labelRectangle.width)-xRW,
					view.toRealWorldCoordY(labelRectangle.y+labelRectangle.height)-yRW);		                                 
			text.setBoundingBox(rect2);
			// Michael Borcherds 2007-11-26 END
		}
    }

    final public void draw(Graphics2D g2) {   	   
        if (isVisible) {      
        	if (!isLaTeX) {
        		g2.setPaint(geo.getObjectColor());				
    			g2.setFont(textFont);    			
    			drawMultilineText(g2);   
        	} else 
        	{
        		drawEquation(g2,xLabel,yLabel);
        	}
        	
			  
			// draw label rectangle
			if (geo.doHighlighting()) {
				g2.setStroke(objStroke);
				g2.setPaint(Color.lightGray);		
				g2.draw(labelRectangle);         
			}   
        }
    }
    
	final public void drawEquation(Graphics2D g2, int x, int y)
	{
		if (!JarManager.JSMATHTEX_LOADED)
		{
			eqn = new sHotEqn(labelDesc);
			eqn.setDoubleBuffered(false);
			eqn.setEditable(false);	
			eqn.removeMouseListener(eqn);
			eqn.removeMouseMotionListener(eqn);				
			eqn.setDebug(false);
			eqn.setOpaque(false);	
			eqn.setFontname(Application.STANDARD_FONT_NAME);
			setEqnFontSize();																												
			eqn.paintComponent(g2,xLabel,yLabel);					
		}
		else
		{
   	 // TEST CODE FOR JMathTeX
			TeXFormula formula;
			Icon icon;
			try{
				formula = new TeXFormula(labelDesc);
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,fontSize);
			}
			catch (Exception e)
			{
				formula = new TeXFormula("LaTeXerror");
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,fontSize);
				
			}
			icon.paintIcon(new JLabel(), g2, x, y); // component can't be null
		}

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
     * Removes HotEqn from view again    
     */
	final public void remove() {    	
    	//if (eqn != null) view.remove(eqn);
    }

	final void updateFontSize() {	
		// text's font size is relative to the global font size
		int newFontSize = view.fontSize + text.getFontSize();		
		int newFontStyle = text.getFontStyle();	
		boolean newSerifFont = text.isSerifFont();
		
		if (fontSize !=newFontSize || fontStyle != newFontStyle || newSerifFont != serifFont) {					
			super.updateFontSize();
			
			fontSize = newFontSize;
			fontStyle = newFontStyle;
			serifFont = newSerifFont;
						
			if (isLaTeX) {
				//setEqnFontSize();				
			} else {				
				textFont = new Font(serifFont ? "Serif" : "SansSerif", fontStyle, fontSize);				
			}		
		}			
	}
	
	private void setEqnFontSize() {		
		
		// hot eqn may only have even font sizes from 10 to 28
		int size = (fontSize / 2) * 2; 
		if (size < 10) 
			size = 10;
		else if (size > 28) 
			size = 28;
		
		eqn.setFontname(serifFont ? "Serif" : "SansSerif");
		eqn.setFontsizes(size, size - 2, size - 4, size - 6);
		eqn.setFontStyle(fontStyle);
		
	}
}


