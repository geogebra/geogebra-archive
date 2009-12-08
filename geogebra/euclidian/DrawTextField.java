/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.euclidian;

import geogebra.kernel.GeoButton;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoTextField;
import geogebra.main.Application;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextField;


/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Michael
 * @version
 */
public final class DrawTextField extends Drawable {

	private GeoButton geoButton;

	private boolean isVisible;

	private boolean hit = false;
	private String oldCaption;

	private Point textSize = new Point(0,0);
	
	JTextField textField;
	JLabel label;
	ButtonListener bl;
    Container box = Box.createHorizontalBox();


	public DrawTextField(EuclidianView view, GeoButton geoButton) {
		this.view = view;
		this.geoButton = geoButton;
		geo = geoButton;

		// action listener for checkBox
		bl = new ButtonListener();
		textField = new JTextField(20);	
		label = new JLabel("Label");
		label.setLabelFor(textField);
		textField.setVisible(true);
		label.setVisible(true);
		textField.addFocusListener(bl);
		label.addMouseListener(bl);
		label.addMouseMotionListener(bl);
		textField.addKeyListener(bl);
	    box.add(label);
	    box.add(textField);
		view.add(box);

		
		update();
	}

	private class ButtonListener implements 
			MouseListener, MouseMotionListener, FocusListener, KeyListener {

		private boolean dragging = false;
		private EuclidianController ec = view.getEuclidianController();

		/**
		 * Handles click on check box. Changes value of GeoBoolean.
		 */
		public void itemStateChanged(ItemEvent e) {
		}

		public void mouseDragged(MouseEvent e) {	
			
			dragging = true;			
			e.translatePoint(box.getX(), box.getY());
			ec.mouseDragged(e);
			view.setToolTipText(null);
		}

		public void mouseMoved(MouseEvent e) {			
			
			e.translatePoint(box.getX(), box.getY());
			ec.mouseMoved(e);
			view.setToolTipText(null);
		}

		public void mouseClicked(MouseEvent e) {
			
			if (e.getClickCount() > 1) return;
			
			e.translatePoint(box.getX(), box.getY());
			ec.mouseClicked(e);
		}

		public void mousePressed(MouseEvent e) {
			
			dragging = false;	
			e.translatePoint(box.getX(), box.getY());
			ec.mousePressed(e);		
		}

		public void mouseReleased(MouseEvent e) {	
			
			if (!dragging && !e.isMetaDown() && !e.isPopupTrigger()
					&& view.getMode() == EuclidianView.MODE_MOVE) 
			{
				// handle LEFT CLICK
				//geoBool.setValue(!geoBool.getBoolean());
				//geoBool.updateRepaint();
				//geo.runScript();
				//
				
				// make sure itemChanged does not change
		    	// the value back my faking a drag
		    	dragging = true;				
			}
			else {
				// handle right click and dragging
				e.translatePoint(box.getX(), box.getY());
				ec.mouseReleased(e);	
			}
			
		}

		public void mouseEntered(MouseEvent arg0) {
			hit = true;
			view.setToolTipText(null);
		}

		public void mouseExited(MouseEvent arg0) {
			hit = false;
		}

		public void focusGained(FocusEvent e) {
			view.getEuclidianController().textfieldHasFocus(true);
			
		}

		public void focusLost(FocusEvent e) {
			view.getEuclidianController().textfieldHasFocus(false);
			
			GeoElement linkedGeo = ((GeoTextField)geo).getLinkedGeo();
			
			if (linkedGeo != null) {
				try {
					linkedGeo = geo.getKernel().getAlgebraProcessor().changeGeoElementNoExceptionHandling(linkedGeo, textField.getText(), false, true);
				} catch (Exception e1) {
					geo.getKernel().getApplication().showError(e1.getMessage());
					updateText();
					return;
				}			
				((GeoTextField)geo).setLinkedGeo(linkedGeo);
				
				updateText();

				
			}
			
			else geo.runScripts(textField.getText());
			
		}

		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyChar() == '\n') {
				//geo.runScripts(textField.getText());
				
				// this should be enough to trigger script event
				// ie in focusLost
				view.requestFocus();
			}
			
		}

		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		
	}
	
	private void updateText() {
		
		GeoElement linkedGeo = ((GeoTextField)geo).getLinkedGeo();
		if (linkedGeo != null)
			textField.setText(linkedGeo.getAlgebraDescription());
	}


	final public void update() {
		isVisible = geo.isEuclidianVisible();
		//textField.setVisible(isVisible);
		//label.setVisible(isVisible);
		box.setVisible(isVisible);
		if (!isVisible)
			return;		

		// show hide label by setting text
		if (geo.isLabelVisible()) {
			// get caption to show r
			String caption = geo.getCaption();
			if (!caption.equals(oldCaption)) {
				oldCaption = caption;
				labelDesc = GeoElement.indicesToHTML(caption, false);
			}	
			label.setText(labelDesc);
		} else {
			// don't show label
// Michael Borcherds 2007-10-18 BEGIN changed so that vertical position of checkbox doesn't change when label is shown/hidden
//			checkBox.setText("");
			label.setText(" ");
// Michael Borcherds 2007-10-18 END
		}			
		
		textField.setOpaque(true);		
		label.setOpaque(false);		
		textField.setFont(view.fontPoint);
		label.setFont(view.fontPoint);
		textField.setForeground(geo.getObjectColor());
		label.setForeground(geo.getObjectColor());
		
		textField.setFocusable(true);
		textField.setEditable(true);
		updateText();
		// set checkbox state		
		//jButton.removeItemListener(bl);
		//jButton.setSelected(geo.getBoolean());
		//jButton.addItemListener(bl);
		
		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;		
		Dimension prefSize = box.getPreferredSize();
		labelRectangle.setBounds(xLabel, yLabel, prefSize.width,
				prefSize.height);
		box.setBounds(labelRectangle);	}

	private void updateLabel() {
		/*
		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;		

		labelRectangle.setBounds(xLabel, yLabel,
				 ((textSize == null) ? 0 : textSize.x),
				12);*/

	}

	final public void draw(Graphics2D g2) {
/*
		if (isVisible) {		

			g2.setFont(view.fontPoint);
			g2.setStroke(objStroke); 

			g2.setPaint(geo.getObjectColor());
			textSize = Drawable.drawIndexedString(g2, labelDesc, button.labelOffsetX, button.labelOffsetY + (13 + 9) / 2 + 5);

			updateLabel();
		}
*/
	}

	/**
	 * Removes button from view again
	 */
	final public void remove() {
		view.remove(textField);
	}
	
	/**
	 * was this object clicked at? (mouse pointer
	 * location (x,y) in screen coords)
	 */
	final public boolean hit(int x, int y) {
		return hit;				      
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


}
