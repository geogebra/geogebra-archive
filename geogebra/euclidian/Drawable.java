/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Drawable.java
 *
 * Created on 13. Oktober 2001, 17:40
 */

package geogebra.euclidian;

import geogebra.Application;
import geogebra.kernel.GeoElement;
import hoteqn.sHotEqn;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

/**
 *
 * @author  Markus
 * @version 
 */
public abstract class Drawable {
	BasicStroke objStroke = EuclidianView.getDefaultStroke();
	BasicStroke selStroke = EuclidianView.getDefaultSelectionStroke();
	BasicStroke decoStroke = EuclidianView.getDefaultStroke();

	private int lineThickness = -1;
	public int lineType = -1;

	protected EuclidianView view;
	protected GeoElement geo;
	public int xLabel, yLabel;
	int mouseX, mouseY; // for Previewables
	protected String labelDesc; // label Description
	private String oldLabelDesc;	
	private boolean labelHasIndex = false;
	Rectangle labelRectangle = new Rectangle(); // for label hit testing
	Shape strokedShape, strokedShape2;
	
	// tracing	
	protected boolean isTracing = false;
	
	boolean createdByDrawList = false;	

	public abstract void update();
	public abstract void draw(Graphics2D g2);
	public abstract boolean hit(int x, int y);
	public abstract boolean isInside(Rectangle rect);
	public abstract GeoElement getGeoElement();
	public abstract void setGeoElement(GeoElement geo);
	
	public double getxLabel() {
		return xLabel;
	}
	
	public double getyLabel() {
		return yLabel;
	}
	
	void updateFontSize() {
		//	this enforces reiniting of labelRectangle in drawLabel()
		labelHasIndex = true;				
	}
	
	/**
	 * Returns the bounding box of this Drawable in screen coordinates. 
	 * @return null when this Drawable is infinite or undefined	 
	 */
	public Rectangle getBounds() {		
		return null;	
	}
	
	final protected void drawLabel(Graphics2D g2) {
		if (labelDesc == null) return;
		
		// no index in label
		if (oldLabelDesc == labelDesc && !labelHasIndex) {
			g2.drawString(labelDesc, xLabel, yLabel);
			labelRectangle.setLocation(xLabel, yLabel - g2.getFont().getSize());
		} else { // label with index
			// label description has changed, search for possible indices
			oldLabelDesc = labelDesc;
					
			Point p = drawIndexedString(g2, labelDesc, xLabel, yLabel);
			labelHasIndex = p.y > 0;
			int fontSize = g2.getFont().getSize();
			labelRectangle.setBounds(xLabel, yLabel - fontSize, p.x, fontSize + p.y);			
		}		
	}			
	
	// Michael Borcherds 2008-06-10
	final float textWidth(String str, Font font, FontRenderContext frc)
	{
		if (str.equals("")) return 0f;
		TextLayout layout = new TextLayout(str , font, frc);
		return layout.getAdvance();	
		
	}
	
	final void drawMultilineLaTeX(Graphics2D g2, boolean serifFont, int fontStyle) {
		
		int fontSize = g2.getFont().getSize();
		float lineSpread = fontSize * 1.0f;
		float lineSpace = fontSize * 0.5f;

		Font font = g2.getFont();
		FontRenderContext frc = g2.getFontRenderContext();
		int xoffset = 0;
		float height=0;
		
		//System.out.println(labelDesc.split("\\$")[0]+"xxx"+labelDesc.split("\\$")[1]+"xxx"+labelDesc.split("\\$")[2]+"xxx"+labelDesc.split("\\$")[3]+"xxx");
		// line1$ \sqrt{ line2 }$line3$ \frac{ line }{4 }$ 
		
		boolean lastLine;
		
		Dimension dim;
		
		String[] lines=labelDesc.split("\n");
		for (int k=0 ; k<lines.length ; k++)
		{

			String[] strings=lines[k].split("\\$");

			boolean latex=false;
			if (lines[k].indexOf('$') == -1 && lines.length == 1) 
			{
				latex=true; // just latex
				//System.out.println("just latex");
			}

			for (int j=0 ; j<strings.length ; j++)
			{
				//if (j==0) firstLine=true; else firstLine=false;
				if (k==lines.length-1 && j==strings.length-1) lastLine=true; else lastLine=false;
				
				//System.out.println(j+strings[j]);
				g2.setFont(font);

				if (!strings[j].equals(str(" ",strings[j].length()))) // check not empty or just spaces
				{
					//System.out.println("strings["+j+"]=XXX"+strings[j]+"XXX"+strings[j].length());
					if (latex)
					{
						//if (!firstLine) height += lineSpace;
						
						dim = drawEquation(g2,xLabel,(int)(yLabel + height), strings[j], fontSize, serifFont, fontStyle);
						int width=dim.width;
						if (width > xoffset) xoffset = width;		
						
						height += dim.height;
						//if (!lastLine) height += lineSpace;
						//System.out.println(dim);
					}
					else
					{
						height += lineSpread;
						
						Point p = drawIndexedString(g2, strings[j], xLabel, yLabel + height);

						if (p.x > xoffset) xoffset = p.x;		
						if (p.y > lineSpread) height += p.y - lineSpread;
						
					}
					//if (!lastLine)
					height += lineSpace;
				}
				latex=!latex;
				//System.out.println(latex);
				
				

			}
		}
		labelRectangle.setBounds(xLabel, yLabel, xoffset, (int)height);
		
		//g2.setFont(font);
	}	

	// returns a string consisting of n consecutive "str"s
	final private String str(String str, int n)
	{
		if (n==0) return "";
		
		String ret="";
		for (int i=0 ; i<n ; i++) ret+=str;
		return ret;
	}
	
	final public Dimension drawEquation(Graphics2D g2, int x, int y, String text, int fontSize, boolean serifFont, int fontStyle)
	{
		Dimension dim;
			sHotEqn eqn = new sHotEqn(text);
			//System.out.println(eqn.getSize());
			eqn.setDoubleBuffered(false);
			eqn.setEditable(false);	
			eqn.removeMouseListener(eqn);
			eqn.removeMouseMotionListener(eqn);				
			eqn.setDebug(false);
			eqn.setOpaque(false);	
			eqn.setFontname(Application.STANDARD_FONT_NAME);

			//setEqnFontSize();																												
			int size = (fontSize / 2) * 2; 
			if (size < 10) 
				size = 10;
			else if (size > 28) 
				size = 28;
			
			eqn.setFontname(serifFont ? "Serif" : "SansSerif");
			eqn.setFontsizes(size, size - 2, size - 4, size - 6);
			eqn.setFontStyle(fontStyle);

			
			
			
			//eqn.paintComponent(g2Dtemp,0,0);		
			//dim=eqn.getSizeof(text);
			eqn.paintComponent(g2,x,y);		
			dim=eqn.getSize();
			
			//System.out.println(size);
			return dim;
	}
	
	final void drawMultilineText(Graphics2D g2) {
		int lines = 0;				
		int fontSize = g2.getFont().getSize();
		float lineSpread = fontSize * 1.5f;

		Font font = g2.getFont();
		FontRenderContext frc = g2.getFontRenderContext();
		int xoffset = 0, yoffset = 0;

		// no index in text
		if (oldLabelDesc == labelDesc && !labelHasIndex) {		
			// draw text line by line
			int lineBegin = 0;
			int length = labelDesc.length();
			for (int i=0; i < length-1; i++) {
				if (labelDesc.charAt(i) == '\n') {
					//end of line reached: draw this line
					g2.drawString(labelDesc.substring(lineBegin, i), xLabel, yLabel + lines * lineSpread);

					int width=(int)textWidth(labelDesc.substring(lineBegin, i), font, frc);
					if (width > xoffset) xoffset = width;			
					
					lines++;
					lineBegin = i + 1;					
				}
			}
			
			float ypos = yLabel + lines * lineSpread;
			g2.drawString(labelDesc.substring(lineBegin), xLabel, ypos);

			int width=(int)textWidth(labelDesc.substring(lineBegin), font, frc);
			if (width > xoffset) xoffset = width;			
			
			// Michael Borcherds 2008-06-10
			// changed setLocation to setBounds (bugfix)
			// and added final float textWidth()
			//labelRectangle.setLocation(xLabel, yLabel - fontSize);
			int height = (int) ( (lines +1)*lineSpread);
			labelRectangle.setBounds(xLabel, yLabel - fontSize, xoffset, height );
		} 
		else { 			
			// text with indices
			// label description has changed, search for possible indices
			oldLabelDesc = labelDesc;
			
			// draw text line by line
			int lineBegin = 0;
			int length = labelDesc.length();
			xoffset = 0;
			yoffset = 0;
			for (int i=0; i < length-1; i++) {
				if (labelDesc.charAt(i) == '\n') {
					//end of line reached: draw this line
					Point p = drawIndexedString(g2, labelDesc.substring(lineBegin, i), xLabel, yLabel + lines * lineSpread);
					if (p.x > xoffset) xoffset = p.x;
					if (p.y > yoffset) yoffset = p.y;
					lines++;
					lineBegin = i + 1;					
				}
			}
					
			float ypos = yLabel + lines * lineSpread;
			Point p = drawIndexedString(g2, labelDesc.substring(lineBegin), xLabel, ypos);
			if (p.x > xoffset) xoffset = p.x;
			if (p.y > yoffset) yoffset = p.y;
			labelHasIndex = yoffset > 0;			
			int height = (int) ( (lines +1)*lineSpread);
			labelRectangle.setBounds(xLabel, yLabel - fontSize, xoffset, height );
		}
	}		
	
	/**
	 * Draws a string str with possible indices to g2 at position x, y. 
	 * The indices are drawn using the given indexFont. 
	 * Examples for strings with indices: "a_1" or "s_{ab}"
	 * @param g2
	 * @param str
	 * @return additional pixel needed to draw str (x-offset, y-offset) 
	 */
	final public static Point drawIndexedString(Graphics2D g2, String str, float xPos, float yPos) {
		Font g2font = g2.getFont();
		Font indexFont = getIndexFont(g2font);
		Font font = g2font;
		TextLayout layout;
		FontRenderContext frc = g2.getFontRenderContext();

		int indexOffset = indexFont.getSize() / 2;
		float maxY = 0;
		int depth = 0;
		float x = xPos;
		float y = yPos;
		int startPos = 0;
		int length = str.length();

		for (int i=0; i < length; i++) {
			switch (str.charAt(i)) {
				case '_':			
					//	draw everything before _															
					if (i > startPos) {
						font = (depth == 0) ? g2font : indexFont;
						y = yPos + depth * indexOffset;
						if (y > maxY) maxY = y;			
						String tempStr = str.substring(startPos, i);
						layout = new TextLayout(tempStr, font, frc);
						g2.setFont(font);						
						g2.drawString(tempStr, x, y);			 	
						x += layout.getAdvance();		
					}					
					startPos = i + 1;
					depth++;
			
					// check if next character is a '{' (beginning of index with several chars)
					if (startPos < length && str.charAt(startPos) != '{') {
						font = (depth == 0) ? g2font : indexFont;										
						y = yPos + depth * indexOffset;
						if (y > maxY) maxY = y;
						String tempStr = str.substring(startPos, startPos+1);
						layout = new TextLayout(tempStr, font, frc);
						g2.setFont(font);
						g2.drawString(tempStr, x, y);
						x += layout.getAdvance();	
						depth--;																									
					}
					i++;
					startPos++; 
					break;				
			
				case '}': // end of index with several characters
					if (depth > 0) {						
						if (i > startPos) {
							font = (depth == 0) ? g2font : indexFont;		
							y = yPos + depth * indexOffset;
							if (y > maxY) maxY = y;
							String tempStr = str.substring(startPos, i);
							layout = new TextLayout(tempStr, font, frc);
							g2.setFont(font);
							g2.drawString(tempStr, x, y);
							x += layout.getAdvance();
						}												
						startPos = i+1;
						depth--;		
					}
					break;		
			}		
		}
	
		if (startPos < length) {
			font = (depth == 0) ? g2font : indexFont;
			y = yPos + depth * indexOffset;
			if (y > maxY) maxY = y;
			String tempStr = str.substring(startPos);
			layout = new TextLayout(tempStr, font, frc);
			g2.setFont(font);
			g2.drawString(tempStr, x, y);
			x += layout.getAdvance();
		}	
		g2.setFont(g2font);
		return new Point(Math.round(x - xPos), Math.round(maxY - yPos));
	}
	
	private static Font getIndexFont(Font f) {
		//	index font size should be at least 8pt
		int newSize = Math.max( (int) (f.getSize() * 0.9) , 8);	
		return f.deriveFont(f.getStyle(), newSize);	 	 
	}

	/**
	 * Adds geo's label offset to xLabel and yLabel.
	 * @return whether something was changed
	 */
	final protected boolean addLabelOffset() {
		if (geo.labelOffsetX == 0 && geo.labelOffsetY == 0) return false;
			
		int x = xLabel + geo.labelOffsetX;
		int y = yLabel + geo.labelOffsetY;
		
		// don't let offset move label out of screen!
		int xmax = view.width - 15;
		int ymax = view.height - 5;
		if (x < 5 || x > xmax ) return false;
		if (y < 15 || y > ymax) return false;
		
		xLabel = x;
		yLabel = y;
		return true;
	}
	
	/**
	 * Was the label clicked at? (mouse pointer
	 * location (x,y) in screen coords)
	 */		
	public boolean hitLabel(int x, int y) {
		return labelRectangle.contains(x, y);
	}

	final void updateStrokes(GeoElement geo) {
		strokedShape = null;
		strokedShape2 = null;		
		
		if (lineThickness != geo.lineThickness) {
			lineThickness = geo.lineThickness;
			lineType = geo.lineType;

			float width = lineThickness / 2.0f;
			objStroke = EuclidianView.getStroke(width, lineType);
			decoStroke = EuclidianView.getStroke(width, EuclidianView.LINE_TYPE_FULL);
			selStroke =
				EuclidianView.getStroke(
					width + EuclidianView.SELECTION_ADD,
					EuclidianView.LINE_TYPE_FULL);
		} else if (lineType != geo.lineType) {
			lineType = geo.lineType;

			float width = lineThickness / 2.0f;
			objStroke = EuclidianView.getStroke(width, lineType);
		}
	}
	
	final public static void drawGeneralPath(Shape shape, Graphics2D g2) {
		Object oldHint = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);			
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);	
		
		g2.draw(shape);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, oldHint);			
	}
	
	final public static void fillGeneralPath(Shape shape, Graphics2D g2) {
		Object oldHint = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);			
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);	
		g2.fill(shape);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, oldHint);			
	}

}