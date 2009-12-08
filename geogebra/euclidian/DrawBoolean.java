/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.euclidian;

import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;


/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Markus Hohenwarter
 * @version
 */
public final class DrawBoolean extends Drawable {

	private GeoBoolean geoBool;

	private boolean isVisible;

	//private JCheckBox checkBox;
	private boolean hit = false;
	private String oldCaption;
	//private BooleanCheckBoxListener cbl;
	
	private Point textSize = new Point(0,0);
	
	private CheckBoxIcon checkBoxIcon;

	/** Creates new DrawText */
	public DrawBoolean(EuclidianView view, GeoBoolean geoBool) {
		this.view = view;
		this.geoBool = geoBool;
		geo = geoBool;
		
		checkBoxIcon = new CheckBoxIcon(view);
		
		// action listener for checkBox
		//cbl = new BooleanCheckBoxListener();
		//checkBox = new JCheckBox();
		//checkBox.addItemListener(cbl);
		//checkBox.addMouseListener(cbl);
		//checkBox.addMouseMotionListener(cbl);
		//checkBox.setFocusable(false);
		//checkBox.setVisible(false);
		//view.add(checkBox);
		
		update();
	}


	final public void update() {
		isVisible = geo.isEuclidianVisible();
		//checkBox.setVisible(isVisible);
    	// don't return here to make sure that getBounds() works for offscreen points too
		
		updateStrokes(geoBool);

		// show hide label by setting text
		if (geo.isLabelVisible()) {
			// get caption to show r
			String caption = geoBool.getCaption();
			if (!caption.equals(oldCaption)) {
				oldCaption = caption;
				labelDesc = caption; //GeoElement.indicesToHTML(caption, true);
			}	
			//checkBox.setText(labelDesc);
		} else {
			// don't show label
			oldCaption = "";
			labelDesc = "";
// Michael Borcherds 2007-10-18 BEGIN changed so that vertical position of checkbox doesn't change when label is shown/hidden
//			checkBox.setText("");
			//checkBox.setText(" ");
// Michael Borcherds 2007-10-18 END
		}			
		
		//checkBox.setOpaque(false);		
		//checkBox.setFont(view.fontPoint);
		//checkBox.setForeground(geoBool.getObjectColor());
		
		// set checkbox state		
		//checkBox.removeItemListener(cbl);
		//checkBox.setSelected(geoBool.getBoolean());
		//checkBox.addItemListener(cbl);
		
		updateLabel();
		
		//checkBox.

		
		
	}
	
	private void updateLabel() {
		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;		
		int size = view.getBooleanSize();
		Dimension prefSize = new Dimension(size + 12,size + 12);//checkBox.getPreferredSize();
		labelRectangle.setBounds(xLabel, yLabel,
				prefSize.width + ((textSize == null) ? 0 : textSize.x),
				prefSize.height);
			
		//checkBox.setBounds(labelRectangle);
		
	}

	final public void draw(Graphics2D g2) {

		if (isVisible) {		
			
			int size = view.getBooleanSize();

			g2.setFont(view.fontPoint);
			g2.setStroke(EuclidianView.getDefaultStroke()); 
			
			checkBoxIcon.paintIcon(geoBool.getBoolean(), geoBool.doHighlighting(), g2, geoBool.labelOffsetX + 5, geoBool.labelOffsetY + 5);
			
			g2.setPaint(geo.getObjectColor());
			textSize = Drawable.drawIndexedString(g2, labelDesc, geoBool.labelOffsetX + size + 9, geoBool.labelOffsetY + (size + 9) / 2 + 5);
			
			updateLabel();
		}
			
		/*
		if (isVisible) {		
			// the button is drawn as a swing component by the view
			// They are Swing components and children of the view

			// draw label rectangle
			if (geo.doHighlighting()) {
				g2.setStroke(objStroke);
				g2.setPaint(Color.lightGray);
				g2.draw(labelRectangle);
				
				Application.debug("highlight drawn");
				checkBox.setBorder(BorderFactory.createEtchedBorder());
			}				
		}
		*/
	}

	/**
	 * Removes button from view again
	 */
	final public void remove() {
		//view.remove(checkBox);
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
	
	public static class CheckBoxIcon {
		
		// Michael Borcherds 2008-05-11
		// adapted from http://www.java2s.com/Open-Source/Java-Document/6.0-JDK-Modules-com.sun.java/swing/com/sun/java/swing/plaf/windows/WindowsIconFactory.java.htm
		// references to XPStyle removed
		// option for double-size added
		// replaced UIManager.getColor() with numbers from:
		// http://www.java2s.com/Tutorial/Java/0240__Swing/ListingUIDefaultProperties.htm
		
		/*
         * Copyright 1998-2006 Sun Microsystems, Inc.  All Rights Reserved.
         * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
         *
         * This code is free software; you can redistribute it and/or modify it
         * under the terms of the GNU General Public License version 2 only, as
         * published by the Free Software Foundation.  Sun designates this
         * particular file as subject to the "Classpath" exception as provided
         * by Sun in the LICENSE file that accompanied this code.
         *
         * This code is distributed in the hope that it will be useful, but WITHOUT
         * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
         * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
         * version 2 for more details (a copy is included in the LICENSE file that
         * accompanied this code).
         *
         * You should have received a copy of the GNU General Public License version
         * 2 along with this work; if not, write to the Free Software Foundation,
         * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
         *
         * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
         * CA 95054 USA or visit www.sun.com if you need additional information or
         * have any questions.
         */
		//int csize = 13;
		
		EuclidianView ev;
		
		public static Color highlightBackground = new Color(230, 230, 230);
		
		public CheckBoxIcon(EuclidianView ev)
		{
			this.ev=ev;
		}
		
        public void paintIcon(boolean checked, boolean highlighted, Graphics g, int x, int y) {

            
            int csize = ev.getBooleanSize();
            
            {
                // outer bevel
                if (true) {
                    // Outer top/left
                    //g.setColor(UIManager.getColor("CheckBox.shadow"));
                    g.setColor(new Color(128,128,128));
                    g.drawLine(x, y, x + (csize-2), y);
                    g.drawLine(x, y + 1, x, y + (csize-2));

                    // Outer bottom/right
                    //g.setColor(UIManager.getColor("CheckBox.highlight"));
                    g.setColor(Color.white);
                    g.drawLine(x + (csize-1), y, x + (csize-1), y + (csize-1));
                    g.drawLine(x, y + (csize-1), x + (csize-2), y + (csize-1));

                    // Inner top.left
                    //g.setColor(UIManager.getColor("CheckBox.darkShadow"));
                    g.setColor(new Color(64,64,64));
                    g.drawLine(x + 1, y + 1, x + (csize-3), y + 1);
                    g.drawLine(x + 1, y + 2, x + 1, y + (csize-3));

                    // Inner bottom/right
                    //g.setColor(UIManager.getColor("CheckBox.light"));
                    g.setColor(new Color(212,208,200));
                    g.drawLine(x + 1, y + (csize-2), x + (csize-2), y + (csize-2));
                    g.drawLine(x + (csize-2), y + 1, x + (csize-2), y + (csize-3));

                    // inside box 
                    if (highlighted) {
                        //g.setColor(UIManager.getColor("CheckBox.background"));
                        g.setColor(highlightBackground);
                    } else {
                        //g.setColor(UIManager.getColor("CheckBox.interiorBackground"));
                        g.setColor(Color.white);
                    }
                    g.fillRect(x + 2, y + 2, csize - 4, csize - 4);
                } else {
                    //g.setColor(UIManager.getColor("CheckBox.shadow"));
                    g.setColor(new Color(128,128,128));
                    g.drawRect(x + 1, y + 1, csize - 3, csize - 3);

                    if (true) {
                        //g.setColor(UIManager.getColor("CheckBox.background"));
                        g.setColor(highlightBackground);
                    } else {
                        //g.setColor(UIManager.getColor("CheckBox.interiorBackground"));
                        g.setColor(Color.white);
                    }
                    g.fillRect(x + 2, y + 2, csize - 4, csize - 4);
                }

                if (true) {
                    //g.setColor(UIManager.getColor("CheckBox.foreground"));
                    g.setColor(new Color(0,0,0));
                } else {
                    //g.setColor(UIManager.getColor("CheckBox.shadow"));
                    g.setColor(new Color(128,128,128));
                }

                // paint check
                
                if (checked) {
                  if (csize == 13)
                  {
                	
                	  for (int i=5 ; i<=9 ; i++)
                          g.drawLine(x+i, y+12-i, x+i, y+14-i);
                	  
                	  for (int i=3 ; i<=4 ; i++)
                          g.drawLine(x+i, y+i+2, x+i, y+i+4);
                	  /*
                	g.drawLine(x + 9, y + 3, x + 9, y + 3);
                    g.drawLine(x + 8, y + 4, x + 9, y + 4);
                    g.drawLine(x + 7, y + 5, x + 9, y + 5);
                    g.drawLine(x + 6, y + 6, x + 8, y + 6);
                    g.drawLine(x + 3, y + 7, x + 7, y + 7);
                    g.drawLine(x + 4, y + 8, x + 6, y + 8);
                    g.drawLine(x + 5, y + 9, x + 5, y + 9);
                    g.drawLine(x + 3, y + 5, x + 3, y + 5);
                    g.drawLine(x + 3, y + 6, x + 4, y + 6);*/
                    
                  }
                  else
                  { // csize == 26
                	  
                	  for (int i=10 ; i<=18 ; i++)
                          g.drawLine(x+i, y+24-i, x+i, y+29-i);
                	  
                	  for (int i=5 ; i<=9 ; i++)
                          g.drawLine(x+i, y+i+4, x+i, y+i+9);
                		  
                		  
                	/*	  
                    g.drawLine(x + 18, y +  6, x + 18, y +  6);
                    g.drawLine(x + 17, y +  7, x + 18, y +  7);

                    g.drawLine(x + 16, y +  8, x + 18, y +  8);
                    g.drawLine(x + 15, y +  9, x + 18, y +  9);

                    g.drawLine(x + 14, y + 10, x + 18, y + 10);
                    g.drawLine(x + 13, y + 11, x + 18, y + 11);
                    
                    g.drawLine(x + 12, y + 12, x + 17, y + 12);
                    g.drawLine(x + 11, y + 13, x + 16, y + 13);
                    
                    g.drawLine(x +  5, y + 14, x + 15, y + 14);
                    g.drawLine(x +  6, y + 15, x + 14, y + 15);
                    
                    g.drawLine(x +  7, y + 16, x + 13, y + 16);
                    g.drawLine(x +  8, y + 17, x + 12, y + 17);
                    
                    g.drawLine(x + 9, y + 18, x + 11, y + 18);
                    g.drawLine(x + 10, y + 19, x + 10, y + 19);
                    
                    g.drawLine(x +  5, y + 9, x +  5, y + 9);
                    g.drawLine(x +  5, y + 10, x +  6, y + 10);
                    
                    g.drawLine(x +  5, y + 11, x +  7, y + 11);
                    g.drawLine(x +  5, y + 12, x +  8, y + 12);
                    
                    g.drawLine(x +  5, y + 13, x +  9, y + 13);*/
                  }
                }
            }
        }

        public int getIconWidth() {
     
                return ev.getBooleanSize();
           
        }

        public int getIconHeight() {
                return ev.getBooleanSize();
            
        }
    }
	}
