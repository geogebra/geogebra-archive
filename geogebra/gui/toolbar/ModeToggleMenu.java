/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui.toolbar;

import geogebra.main.Application;
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
import java.awt.geom.AffineTransform;
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
			
		tbutton = new MyJToggleButton(this, app);		
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
	
	public int getFirstMode() {
		if (menuItemList == null || menuItemList.size() == 0)
			return -1;
		else {
			JMenuItem mi = (JMenuItem) menuItemList.get(0);
			return Integer.parseInt(mi.getActionCommand());
		}
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
	
	/**
	 * Removes all modes from the toggle menu. Used for the temporary perspective.
	 * 
	 * @author Florian Sonner
	 * @version 2008-10-22
	 */
	public void clearModes() {
		popMenu.removeAll();
		menuItemList.clear();
		size = 0;
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
			
			// display the popup above the button if the toolbar is at the top of the window
			if(app.showToolBarTop()) {
				popMenu.show(app.getMainComponent(), locButton.x - locApp.x, 
						locButton.y - locApp.y + tbutton.getHeight());
			}
			else {
				popMenu.show(app.getMainComponent(), locButton.x - locApp.x, 
						locButton.y - locApp.y - (int)popMenu.getPreferredSize().getHeight());
			}
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
	private GeneralPath gpDown; // the path for an arrow pointing down
	private GeneralPath gpUp; // the path for an arrow pointing up
	private boolean showToolTipText = true;
	private boolean popupTriangleHighlighting = false;
	private ModeToggleMenu menu;
	private Application app;
	
	private static final Color arrowColor = new Color(0,0,0,130);
	//private static final Color selColor = new Color(166, 11, 30,150);
	//private static final Color selColor = new Color(17, 26, 100, 200);
	private static final Color selColor = new Color(0,0,153, 200);
	private static final BasicStroke selStroke = new BasicStroke(3f);
			
	MyJToggleButton(ModeToggleMenu menu, Application app) {
		super();
		this.menu = menu;
		this.app = app;
			
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
			if (gpDown == null) initPath();							
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON);
			
			GeneralPath usedPath = app.showToolBarTop() ? gpDown : gpUp;
			
			if (popupTriangleHighlighting || menu.isPopupShowing()) {
				g2.setColor(Color.red);				
				g2.fill(usedPath);
				g2.setColor(Color.black);
				g2.draw(usedPath);				
			} else {
				g2.setColor(Color.white);
				g2.fill(usedPath);
				g2.setColor(arrowColor);
				g2.draw(usedPath);
			}
			
		}
	}
	
	private void initPath() {
		gpDown = new GeneralPath();	
		int x = BORDER + iconWidth + 2;
		int y = BORDER + iconHeight + 1;
		gpDown.moveTo(x-6, y-5);
		gpDown.lineTo(x, y-5);
		gpDown.lineTo(x-3,y);
		gpDown.closePath();

		gpUp = new GeneralPath();	
		x = BORDER + iconWidth + 2;
		y = BORDER + 2;
		gpUp.moveTo(x-6, y);
		gpUp.lineTo(x, y);
		gpUp.lineTo(x-3,y-5);
		gpUp.closePath();
	}
	
	
	private boolean inPopupTriangle(int x, int y) {
		return (menu.size > 1 && (app.showToolBarTop() ? y > iconHeight : y < 12));
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
		menu.setPopupVisible(inPopupTriangle(e.getX(), e.getY()));
		requestFocus();
		//doClick();	removed to stop mode being selected when triangle clicked (for MODE_FITLINE)	
	}

	public void mouseReleased(MouseEvent arg0) {			
	}

	public void mouseDragged(MouseEvent e) {
		if (inPopupTriangle(e.getX(), e.getY()))
			menu.setPopupVisible(true);
	}
	
	public void mouseMoved(MouseEvent e) {	
		menu.mouseOver();
		showToolTipText = !menu.isPopupShowing(); 
		
		// highlight popup menu triangle
		if (menu.size > 1 &&  
					popupTriangleHighlighting != inPopupTriangle(e.getX(), e.getY())) {
			popupTriangleHighlighting = !popupTriangleHighlighting;
			repaint();
		}
	}
}
