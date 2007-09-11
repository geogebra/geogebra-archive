/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.gui.toolbar;

import geogebra.Application;
import geogebra.euclidian.EuclidianView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

/**
 * JToggle button combined with popup menu for mode selction
 */
public class ModeToggleMenu extends JPanel {
	
	//private static final Color bgSelColor = new Color(164, 174, 188);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ModeToggleButtonGroup bg;
	private MyJToggleButton tbutton;
	private JPopupMenu popMenu;
	private ArrayList menuItemList;
	
	private ActionListener popupMenuItemListener;
	private Application app;
	int size;
	
	public ModeToggleMenu(Application app, ModeToggleButtonGroup bg) {
		this.app = app;
		this.bg = bg;
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			
		tbutton = new MyJToggleButton(this);		
		tbutton.setAlignmentY(BOTTOM_ALIGNMENT);
		add(tbutton);
		
		popMenu = new JPopupMenu();		
		menuItemList = new ArrayList();
		popupMenuItemListener = new MenuItemListener();
		size = 0;
	}
	
	public int getToolsCount() {
		return size;
	}
	
	public JToggleButton getJToggleButton() {
		return tbutton;
	}			
	
	public boolean selectMode(int mode) {
		String modeText = mode + "";
		
		for (int i=0; i < size; i++) {
			JMenuItem mi = (JMenuItem) menuItemList.get(i);
			// found item for mode?
			if (mi.getActionCommand().equals(modeText)) {
				selectItem(mi);
				return true;
			}
		}
		return false;
	}
	
	private void selectItem(JMenuItem mi) {		
		// check if the menu item is already selected
		if (tbutton.isSelected() && tbutton.getActionCommand() == mi.getActionCommand()) {			
			return;
		}
		
		tbutton.setIcon(mi.getIcon());
		tbutton.setToolTipText(mi.getText());			
		tbutton.setActionCommand(mi.getActionCommand());
		tbutton.setSelected(true);				
		tbutton.requestFocus();		
	}
	
	public void addMode(int mode) {
		String modeText = app.getModeText(mode);	
		ImageIcon icon = app.getModeIcon(mode);
		
		// add menu item to popu menu
		String actionText = mode + "";
		JMenuItem mi = new JMenuItem();
		mi.setFont(app.getPlainFont());
		if (mode < EuclidianView.MACRO_MODE_ID_OFFSET)
			mi.setText(app.getMenu(modeText));
		else 
			mi.setText(modeText); // no translation for macro mode text
	    
		mi.setIcon(icon);
		mi.addActionListener(popupMenuItemListener);
		mi.setActionCommand(actionText);
		
		popMenu.add(mi);	
		menuItemList.add(mi);
		size++;
		
		if (size == 1) {
			// init tbutton
			tbutton.setIcon(icon);
			tbutton.setActionCommand(actionText);
			tbutton.setToolTipText(mi.getText());
			// add button to button group
			bg.add(tbutton);
		}
	}			
	
	public void addSeparator() {
		popMenu.addSeparator();
	}	
		
	// sets new mode when item in popup menu is selected
	private class MenuItemListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			JMenuItem item = (JMenuItem) e.getSource();
			selectItem(item);	
			tbutton.doClick();
		}
	}
	
	public void mouseOver() {
		//	popup menu is showing
		JPopupMenu activeMenu = bg.getActivePopupMenu();		
		if (activeMenu != null && activeMenu.isShowing()) {
			setPopupVisible(true);
		}					
	}
	
	//	shows popup menu 
	public void setPopupVisible(boolean flag) {		
		if (flag) {
			bg.setActivePopupMenu(popMenu);	
			if (popMenu.isShowing()) return;
			Point locButton = tbutton.getLocationOnScreen();
			Point locApp = app.getMainComponent().getLocationOnScreen();		
			popMenu.show(app.getMainComponent(), locButton.x - locApp.x, 
											locButton.y - locApp.y + tbutton.getHeight());
		} else {
			popMenu.setVisible(false);
		}
	}		
	
	public boolean isPopupShowing() {
		return popMenu.isShowing();
	}
	
	public void setMode(int mode) {
		app.setMode(mode);
	}
	
}

class MyJToggleButton extends JToggleButton 
implements MouseListener, MouseMotionListener, ActionListener {
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private static int BORDER = 6;
	private int iconWidth, iconHeight;
	private GeneralPath gp;
	private boolean showToolTipText = true;
	private boolean popupTriangleHighlighting = false;
	private ModeToggleMenu menu;		
	
	private static final Color arrowColor = new Color(0,0,0,130);
	//private static final Color selColor = new Color(166, 11, 30,150);
	//private static final Color selColor = new Color(17, 26, 100, 200);
	private static final Color selColor = new Color(0,0,153, 200);
	private static final BasicStroke selStroke = new BasicStroke(3f);
			
	MyJToggleButton(ModeToggleMenu menu) {
		super();
		this.menu = menu;
			
		// add own listeners
		addMouseListener(this);
		addMouseMotionListener(this);
		addActionListener(this);
	}
	
	public String getToolTipText() {
		if (showToolTipText)
			return super.getToolTipText();
		else
			return null;
	}
	
	// set mode
	public void actionPerformed(ActionEvent e) {		
		menu.setMode(Integer.parseInt(e.getActionCommand()));		
	}

	public void setIcon(Icon icon) {
		super.setIcon(icon);  
		iconWidth = icon.getIconWidth();
		iconHeight = icon.getIconHeight();					
		Dimension dim = new Dimension(iconWidth + 2*BORDER,
								iconHeight + 2*BORDER);
		setPreferredSize(dim); 
		setMinimumSize(dim);
		setMaximumSize(dim);		
	}
	
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;			
		Stroke oldStroke = g2.getStroke();
		
		super.paint(g2);	
				
		if (isSelected()) {										
			g2.setColor(selColor);
			g2.setStroke(selStroke);
			g2.drawRect(BORDER-1,BORDER-1, iconWidth+1, iconHeight+1);			

			g2.setStroke(oldStroke);				
		}		
							
		// draw little arrow (for popup menu)
		if (menu.size > 1) {
			if (gp == null) initPath();							
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON);
			
			if (popupTriangleHighlighting || menu.isPopupShowing()) {
				g2.setColor(Color.red);				
				g2.fill(gp);
				g2.setColor(Color.black);
				g2.draw(gp);				
			} else {
				g2.setColor(Color.white);
				g2.fill(gp);
				g2.setColor(arrowColor);
				g2.draw(gp);
			}
			
		}
	}
	
	private void initPath() {
		gp = new GeneralPath();	
		int x = BORDER + iconWidth + 2;
		int y = BORDER + iconHeight + 1;
		gp.moveTo(x-6, y-5);
		gp.lineTo(x, y-5);
		gp.lineTo(x-3,y);
		gp.closePath();			
	}
	
	
	private boolean popupTriangleClicked(int x, int y) {
		return (menu.size > 1 && y > iconHeight);
	}

	public void mouseClicked(MouseEvent e) {	
		if (e.getClickCount() == 2) {
			menu.setPopupVisible(true);
			requestFocus();
		}		
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
		if (popupTriangleHighlighting) {
			popupTriangleHighlighting = false;
			repaint();
		}
	}

	public void mousePressed(MouseEvent e) {		
		menu.setPopupVisible(popupTriangleClicked(e.getX(), e.getY()));
		requestFocus();
		doClick();		
	}

	public void mouseReleased(MouseEvent arg0) {			
	}

	public void mouseDragged(MouseEvent e) {
		if (popupTriangleClicked(e.getX(), e.getY()))
			menu.setPopupVisible(true);
	}
	
	public void mouseMoved(MouseEvent e) {	
		menu.mouseOver();
		showToolTipText = !menu.isPopupShowing(); 
		
		// highlight popup menu triangle
		if (menu.size > 1 &&  
				popupTriangleHighlighting != popupTriangleClicked(e.getX(), e.getY())) {
			popupTriangleHighlighting = !popupTriangleHighlighting;
			repaint();
		}			
	}
}
