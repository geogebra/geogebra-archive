/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.gui;

import geogebra.Application;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

public class MyToolbar extends JPanel{

	private Application app;
	private ArrayList moveToggleMenus;   
	private boolean showToolBarHelp = true;	
	private JLabel modeNameLabel;
	private int mode = -1;
	
	/**
     * Creates a panel for the application's toolbar. 
     * Note: call initToolbar() to fill the panel.
     */
	public MyToolbar(Application app) {
		this.app = app;		
	}
		
	/**
     * Creates a toolbar using the current strToolBarDefinition. 
     */
    public void initToolbar() {    	    	
        // create toolBars                       
        removeAll();
        setLayout(new BorderLayout(10,5));        
        
        JToolBar tb = new JToolBar();   
        tb.setBackground(getBackground());
        ModeToggleButtonGroup bg = new ModeToggleButtonGroup();     
        moveToggleMenus = new ArrayList();
        
        //JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));           
        tb.setFloatable(false);        
        add(tb, BorderLayout.WEST);             
                  
        if (app.getAlgebraInput() != null)        	
        	bg.add(app.getAlgebraInput().getInputButton());     
                       
        // add menus with modes to toolbar
       	addCustomModesToToolbar(tb, bg);
       	
       	// mode label
       	modeNameLabel = new JLabel();
       	if (showToolBarHelp) {       		
       		add(modeNameLabel, BorderLayout.CENTER);       		
       	}     
       	
        // UNDO Toolbar     
        if (app.isUndoActive()) {
	        // undo part            
	        JPanel undoPanel = new JPanel(new BorderLayout(0,0));        	   
	        	        
	        MySmallJButton button = new MySmallJButton(app.getUndoAction(), 7); 	
	        String text = app.getMenu("Undo");
	        button.setText(null);
	        button.setToolTipText(text);                     
	        undoPanel.add(button, BorderLayout.NORTH);
	        
	        button = new MySmallJButton(app.getRedoAction(), 7);         	        
	        text = app.getMenu("Redo");
	        button.setText(null);
	        button.setToolTipText(text);        
	        undoPanel.add(button, BorderLayout.SOUTH);   
	        
	        add(undoPanel, BorderLayout.EAST);
        }                     
        setMode(app.getMode());
    }
    
    /**
     * Sets toolbar mode. This will change the selected toolbar icon. 
     */
    public void setMode(int mode) {   
    	if (mode == this.mode) return;    	
    	
    	this.mode = mode;
        if (moveToggleMenus != null && mode != EuclidianView.MODE_ALGEBRA_INPUT) {
         	for (int i=0; i < moveToggleMenus.size(); i++) {
         		ModeToggleMenu mtm = (ModeToggleMenu) moveToggleMenus.get(i);
         		if (mtm.selectMode(mode)) break;
        	}
        }                            
        updateModeLabel();
    }
    
    private void updateModeLabel() {
    	if (modeNameLabel == null) return;
     	        	
    	String modeText, helpText;    	
    	if (mode >= EuclidianView.MACRO_MODE_ID_OFFSET) {
    		// macro    
    		Macro macro = app.getKernel().getMacro(mode - EuclidianView.MACRO_MODE_ID_OFFSET);
    		modeText = macro.getToolName();    	
    		if (modeText.length() == 0)		
    			modeText = macro.getCommandName();
    		helpText = macro.getToolHelpOrNeededTypes();	    	
    	} else {
        	// standard case    		
	    	modeText = EuclidianView.getModeText(app.getMode());
	    	helpText = app.getMenu(modeText + ".Help");        
	    	modeText = app.getMenu(modeText);
    	}
    	
    	StringBuffer sb = new StringBuffer();
    	sb.append("<html><b>");
        sb.append(modeText);
        sb.append("</b><br>");
        sb.append(helpText);
        sb.append("</html>");    	
        modeNameLabel.setText(sb.toString());
    }
    
    /**
     * Adds the given modes to a two-dimensional toolbar. 
     * The toolbar definition string looks like "0 , 1 2 | 3 4 5 || 7 8 9"
	 * where the int values are mode numbers, "," adds a separator
	 * within a menu, "|" starts a new menu
	 * and "||" adds a separator before starting a new menu. 
     * @param modes
     * @param tb
     * @param bg
     */    
    private void addCustomModesToToolbar(JToolBar tb, ModeToggleButtonGroup bg) {        	    	
    	Vector toolbarVec = handleCustomToolBar(app.getCustomToolBar());
        if (toolbarVec == null) {
            // set default toolbar
        	addDefaultModesToToolbar(tb, bg);           	
        }        
        else {
        	// set custom toolbar
		    boolean firstButton = true;
			for (int i = 0; i < toolbarVec.size(); i++) {
		        ModeToggleMenu tm = new ModeToggleMenu(app, bg);
		        moveToggleMenus.add(tm);
		        Vector menu = (Vector) toolbarVec.get(i);
		        for (int k = 0; k < menu.size(); k++) {
		        	// separator
		        	int mode = ((Integer) menu.get(k)).intValue();
		        	if (mode < 0) {
		        		if (k==0) // separator at first position of new menu: toolbar separator 
		        			tb.addSeparator();
		        		else // separator within menu: 
		        			tm.addSeparator();
		        	} 
		        	else { // standard case: add mode
		        		 tm.addMode(mode);
		        		 if (firstButton) {
		                 	tm.getJToggleButton().setSelected(true);
		                 	firstButton = false;
		                 }
		        	}
		        }
		                               
		        tb.add(tm);
			}
        }
    }
    
    /**
	 * Parses a toolbar definition string like "0 , 1 2 | 3 4 5 || 7 8 9"
	 * where the int values are mode numbers, "," adds a separator
	 * within a menu, "|" starts a new menu
	 * and "||" adds a separator before starting a new menu. 
	 * @return toolbar as nested Vector objects with Integers for the modes. Note: separators have negative values.
	 */
	private static Vector handleCustomToolBar(String strToolBar) {
		if (strToolBar == null || strToolBar.length() == 0) 
			return null;
		
		String [] tokens = strToolBar.split(" ");
		Vector toolbar = new Vector();
		Vector menu = new Vector();
		int maxMenuLength = 0;
		
	    for (int i=0; i < tokens.length; i++) {     
	         if (tokens[i].equals("|")) { // start new menu	        	 
	        	 toolbar.add(menu);
	        	 if (menu.size() > maxMenuLength)
	        		 maxMenuLength = menu.size();
	        	 menu = new Vector();
	         }
	         else if (tokens[i].equals("||")) { // start new menu with separator	        	 
	        	 toolbar.add(menu);
	        	 menu = new Vector();
	        	 menu.add(new Integer(-1)); // separator = negative mode
	         }
	         else if (tokens[i].equals(",")) { // separator within menu
	        	 menu.add(new Integer(-1));
	         }
	         else { // add mode to menu
	        	 try  {	
	        		 menu.add(new Integer(Integer.parseInt(tokens[i])));
	        	 }
	     		catch(Exception e) {
	     			e.printStackTrace();
	     			return null;
	     		}
	         }
	    }

	    // add last menu to toolbar
	    if (menu.size() > 0)
	    	toolbar.add(menu);	   
	    return toolbar;				
	}
    
    private void addDefaultModesToToolbar(JToolBar tb, ModeToggleButtonGroup bg) {
    	// add move mode
        ModeToggleMenu tm = new ModeToggleMenu(app, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_MOVE);
        tm.addMode(EuclidianView.MODE_MOVE_ROTATE);
        tm.getJToggleButton().setSelected(true);        
        tb.add(tm);
        tb.addSeparator();        
        
        // point, intersect 
        tm = new ModeToggleMenu(app, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_POINT);
        tm.addMode(EuclidianView.MODE_INTERSECT);
        tm.addMode(EuclidianView.MODE_MIDPOINT);        
        tb.add(tm);     
                    
        // line, segment, ray, vector
        tm = new ModeToggleMenu(app, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_JOIN);
        tm.addMode(EuclidianView.MODE_SEGMENT);
        tm.addMode(EuclidianView.MODE_SEGMENT_FIXED);   
        tm.addMode(EuclidianView.MODE_RAY);             
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_VECTOR);
        tm.addMode(EuclidianView.MODE_VECTOR_FROM_POINT);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_POLYGON);
        tb.add(tm);                     
                
        // parallel, orthogonal, line bisector, angular bisector, tangents
        tm = new ModeToggleMenu(app, bg);  
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_ORTHOGONAL);
        tm.addMode(EuclidianView.MODE_PARALLEL);        
        tm.addMode(EuclidianView.MODE_LINE_BISECTOR);
        tm.addMode(EuclidianView.MODE_ANGULAR_BISECTOR);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_TANGENTS);
        tm.addMode(EuclidianView.MODE_POLAR_DIAMETER);
        tb.add(tm);
        
        tb.addSeparator();        

        // circle 2, circle 3, conic 5
        tm = new ModeToggleMenu(app, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_CIRCLE_TWO_POINTS);
        tm.addMode(EuclidianView.MODE_CIRCLE_POINT_RADIUS);
        tm.addMode(EuclidianView.MODE_CIRCLE_THREE_POINTS);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_SEMICIRCLE);
        tm.addMode(EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS);
        tm.addMode(EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS);     
        tm.addMode(EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_CONIC_FIVE_POINTS);       
        tb.add(tm);    
        
        // numbers, locus
        tm = new ModeToggleMenu(app, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_ANGLE); 
        tm.addMode(EuclidianView.MODE_ANGLE_FIXED); 
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_DISTANCE);   
        tm.addMode(EuclidianView.MODE_SLIDER);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_LOCUS);  
        tb.add(tm);   
        
        tb.addSeparator();   
        
        // transforms
        tm = new ModeToggleMenu(app, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_MIRROR_AT_POINT);
        tm.addMode(EuclidianView.MODE_MIRROR_AT_LINE);
        tm.addMode(EuclidianView.MODE_ROTATE_BY_ANGLE);
        tm.addMode(EuclidianView.MODE_TRANSLATE_BY_VECTOR);
        tm.addMode(EuclidianView.MODE_DILATE_FROM_POINT);     
        tb.add(tm);
                          
        // text, relation
        tm = new ModeToggleMenu(app, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_TEXT);
        tm.addMode(EuclidianView.MODE_IMAGE);       
        tm.addMode(EuclidianView.MODE_RELATION);        
        tb.add(tm); 
        
        tb.addSeparator();   
        
        // macros
        Kernel kernel = app.getKernel();
        if (kernel.getMacroNumber() > 0) {
        	tm = new ModeToggleMenu(app, bg);        	
        	int size = kernel.getMacroNumber();
        	for (int i = 0; i < size; i++) {
        		Macro macro = kernel.getMacro(i);
        		if (macro.isShowInToolBar())
        			tm.addMacro(i, macro);
        	}     
        	if (tm.getComponentCount() > 0) {
        		moveToggleMenus.add(tm);
        		tb.add(tm);
        		tb.addSeparator();          		
        	}
        }            
        
        // translate view, show/hide modes
        tm = new ModeToggleMenu(app, bg);
        moveToggleMenus.add(tm);
        tm.addMode(EuclidianView.MODE_TRANSLATEVIEW);
        tm.addMode(EuclidianView.MODE_ZOOM_IN);
        tm.addMode(EuclidianView.MODE_ZOOM_OUT);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_SHOW_HIDE_OBJECT);
        tm.addMode(EuclidianView.MODE_SHOW_HIDE_LABEL);
        tm.addMode(EuclidianView.MODE_COPY_VISUAL_STYLE);
        tm.addSeparator();
        tm.addMode(EuclidianView.MODE_DELETE);        
        tb.add(tm);    
    }

	public boolean isShowToolBarHelp() {
		return showToolBarHelp;
	}

	public void setShowToolBarHelp(boolean showToolBarHelp) {
		this.showToolBarHelp = showToolBarHelp;
	}
}
