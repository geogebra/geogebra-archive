/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.gui.toolbar;

import geogebra.Application;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.MySmallJButton;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

public class MyToolbar extends JPanel{
	
	public static final Integer TOOLBAR_SEPARATOR = new Integer(-1);

	private Application app;
	private ArrayList modeToggleMenus;   
	private boolean showToolBarHelp = true;	
	private JLabel modeNameLabel;
	private int mode;
	
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
    	mode = -1;
    	
        // create toolBars                       
        removeAll();
        setLayout(new BorderLayout(10,5));        
        
        JToolBar tb = new JToolBar();   
        tb.setBackground(getBackground());
        ModeToggleButtonGroup bg = new ModeToggleButtonGroup();     
        modeToggleMenus = new ArrayList();
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));           
        add(leftPanel, BorderLayout.WEST);  
        
        tb.setFloatable(false);  
        leftPanel.add(tb);                  
                  
        if (app.getAlgebraInput() != null)        	
        	bg.add(app.getAlgebraInput().getInputButton());                                 
       
       	if (showToolBarHelp) {       
       		// mode label       		
           	modeNameLabel = new JLabel();  
           	leftPanel.add(Box.createRigidArea(new Dimension(5,5)));
       		leftPanel.add(modeNameLabel);
       	}   
       	
        // add menus with modes to toolbar
       	addCustomModesToToolbar(tb, bg);
       	       	
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
        if (modeToggleMenus != null) {
        	int showMode = mode;
        	// show move mode icon for algebra input (selection) mode
        	//if (mode == EuclidianView.MODE_ALGEBRA_INPUT) {
        	//	showMode = EuclidianView.MODE_MOVE;
        	//}
        	
         	for (int i=0; i < modeToggleMenus.size(); i++) {
         		ModeToggleMenu mtm = (ModeToggleMenu) modeToggleMenus.get(i);
         		if (mtm.selectMode(showMode)) break;
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
    		helpText = macro.getToolHelp();
    		if (helpText.length() == 0)
    			helpText = macro.getNeededTypesString();	    	
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
    	Vector toolbarVec;
    	try {
	    	toolbarVec = createToolBarVec(app.getToolBarDefinition());
	    } catch (Exception e) {
			System.err.println("invalid toolbar string: " + app.getToolBarDefinition());
			toolbarVec = createToolBarVec(getDefaultToolbarString());
		}
 
    	// set toolbar
	    boolean firstButton = true;
		for (int i = 0; i < toolbarVec.size(); i++) {	        
	        Object ob = toolbarVec.get(i);
	        
	        // separator between menus
	        if (ob instanceof Integer) {
	        	tb.addSeparator();
	        	continue;
	        }
	        
	        // new menu
	        Vector menu = (Vector) ob;
	        ModeToggleMenu tm = new ModeToggleMenu(app, bg);
	        modeToggleMenus.add(tm);	      
	        
	        for (int k = 0; k < menu.size(); k++) {
	        	// separator
	        	int mode = ((Integer) menu.get(k)).intValue();
	        	if (mode < 0) {	        	       	
	        		// separator within menu: 
	        		tm.addSeparator();
	        	} 
	        	else { // standard case: add mode
	        		
	        		// check mode
	       			if (!"".equals(app.getModeText(mode))) {
		        		 tm.addMode(mode);
		        		 if (firstButton) {
		                 	tm.getJToggleButton().setSelected(true);
		                 	firstButton = false;
		                 }
	       			}	       			 
	        	}
	        }
	            
	        if (tm.getToolsCount() > 0)
	        	tb.add(tm);
		}        
    }
    
    /**
	 * Parses a toolbar definition string like "0 , 1 2 | 3 4 5 || 7 8 9"
	 * where the int values are mode numbers, "," adds a separator
	 * within a menu, "|" starts a new menu
	 * and "||" adds a separator before starting a new menu. 
	 * @return toolbar as nested Vector objects with Integers for the modes. Note: separators have negative values.
	 */
	public static Vector createToolBarVec(String strToolBar) {			
		String [] tokens = strToolBar.split(" ");
		Vector toolbar = new Vector();
		Vector menu = new Vector();		
		
	    for (int i=0; i < tokens.length; i++) {     
	         if (tokens[i].equals("|")) { // start new menu	        	 
	        	 if (menu.size() > 0)
	        		 toolbar.add(menu);	        	
	        	 menu = new Vector();
	         }
	         else if (tokens[i].equals("||")) { // separator between menus	        	 
	        	 if (menu.size() > 0)
	        		 toolbar.add(menu);
	        	 
	        	 // add separator between two menus
	        	 //menu = new Vector();
	        	 //menu.add(TOOLBAR_SEPARATOR);	        	 
	        	 //toolbar.add(menu);
	        	 toolbar.add(TOOLBAR_SEPARATOR);
	        	 
	        	 // start next menu
	        	 menu = new Vector();	        	 
	         }
	         else if (tokens[i].equals(",")) { // separator within menu
	        	 menu.add(TOOLBAR_SEPARATOR);
	         }
	         else { // add mode to menu
	        	 try  {	
	        		 if (tokens[i].length() > 0) {
	        			 int mode = Integer.parseInt(tokens[i]);	        			 
	        			 menu.add(new Integer(mode));
	        		 }
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
    
	/**
	 * Returns the default toolbar String definition.
	 */
    public String getDefaultToolbarString() {
    	StringBuffer sb = new StringBuffer();
    	
    	// move
    	sb.append(EuclidianView.MODE_MOVE);    	
    	sb.append(" ");
    	sb.append(EuclidianView.MODE_MOVE_ROTATE);
    	    	               
        // point, intersect    
    	sb.append(" || ");
    	sb.append(EuclidianView.MODE_POINT);
        sb.append(" ");
        sb.append(EuclidianView.MODE_INTERSECT);
        sb.append(" ");
        sb.append(EuclidianView.MODE_MIDPOINT);        
                             
        // line, segment, ray, vector   
        sb.append(" | "); 
        sb.append(EuclidianView.MODE_JOIN);
        sb.append(" ");
        sb.append(EuclidianView.MODE_SEGMENT);
        sb.append(" ");
        sb.append(EuclidianView.MODE_SEGMENT_FIXED);
        sb.append(" ");
        sb.append(EuclidianView.MODE_RAY);
        sb.append(" , ");        
        sb.append(EuclidianView.MODE_VECTOR);
        sb.append(" ");
        sb.append(EuclidianView.MODE_VECTOR_FROM_POINT);
        sb.append(" , ");        
        sb.append(EuclidianView.MODE_POLYGON);
        
                
        // parallel, orthogonal, line bisector, angular bisector, tangents
        sb.append(" | ");
        sb.append(EuclidianView.MODE_ORTHOGONAL);
        sb.append(" ");
        sb.append(EuclidianView.MODE_PARALLEL);     
        sb.append(" ");
        sb.append(EuclidianView.MODE_LINE_BISECTOR);
        sb.append(" ");
        sb.append(EuclidianView.MODE_ANGULAR_BISECTOR);
        sb.append(" , ");        
        sb.append(EuclidianView.MODE_TANGENTS);
        sb.append(" ");
        sb.append(EuclidianView.MODE_POLAR_DIAMETER);
        
        // circle 2, circle 3, conic 5
        sb.append(" || ");        
        sb.append(EuclidianView.MODE_CIRCLE_TWO_POINTS);
        sb.append(" ");
        sb.append(EuclidianView.MODE_CIRCLE_POINT_RADIUS);
        sb.append(" ");
        sb.append(EuclidianView.MODE_CIRCLE_THREE_POINTS);
        sb.append(" , ");        
        sb.append(EuclidianView.MODE_SEMICIRCLE);
        sb.append(" ");
        sb.append(EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS);
        sb.append(" ");
        sb.append(EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
        sb.append(" , ");        
        sb.append(EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS);
        sb.append(" ");
        sb.append(EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);
        sb.append(" , ");
        sb.append(EuclidianView.MODE_CONIC_FIVE_POINTS);               
        
        // numbers, locus
        sb.append(" || ");
        sb.append(EuclidianView.MODE_ANGLE); 
        sb.append(" ");
        sb.append(EuclidianView.MODE_ANGLE_FIXED); 
        sb.append(" , ");
        sb.append(EuclidianView.MODE_DISTANCE);
        sb.append(" ");
        sb.append(EuclidianView.MODE_SLIDER);
        sb.append(" , ");        
        sb.append(EuclidianView.MODE_LOCUS);                            
        
        // transforms
        sb.append(" | ");
        sb.append(EuclidianView.MODE_MIRROR_AT_POINT);
        sb.append(" ");
        sb.append(EuclidianView.MODE_MIRROR_AT_LINE);
        sb.append(" ");
        sb.append(EuclidianView.MODE_ROTATE_BY_ANGLE);
        sb.append(" ");
        sb.append(EuclidianView.MODE_TRANSLATE_BY_VECTOR);
        sb.append(" ");
        sb.append(EuclidianView.MODE_DILATE_FROM_POINT);
                
        // text, relation
        sb.append(" | ");
        sb.append(EuclidianView.MODE_TEXT);
        sb.append(" ");
        sb.append(EuclidianView.MODE_IMAGE);
        sb.append(" ");
        sb.append(EuclidianView.MODE_RELATION);                     
        
        // translate view, show/hide modes   
        sb.append(" || ");
        sb.append(EuclidianView.MODE_TRANSLATEVIEW);
        sb.append(" ");
        sb.append(EuclidianView.MODE_ZOOM_IN);
        sb.append(" ");
        sb.append(EuclidianView.MODE_ZOOM_OUT);
        sb.append(" , ");        
        sb.append(EuclidianView.MODE_SHOW_HIDE_OBJECT);
        sb.append(" ");
        sb.append(EuclidianView.MODE_SHOW_HIDE_LABEL);
        sb.append(" ");
        sb.append(EuclidianView.MODE_COPY_VISUAL_STYLE);
        sb.append(" , ");        
        sb.append(EuclidianView.MODE_DELETE);
        
        // macros       
        Kernel kernel = app.getKernel();
        int macroNumber = kernel.getMacroNumber();        
        if (macroNumber > 0) {    
        	sb.append(" || ");
        	int count = 0;
        	for (int i = 0; i < macroNumber; i++) {
        		Macro macro = kernel.getMacro(i);
        		if (macro.isShowInToolBar()) {
        			count++;
        			sb.append(i + EuclidianView.MACRO_MODE_ID_OFFSET);
        			sb.append(" ");
        		}        			
        	}             	                	        	
        }            
        
        //System.out.println("defToolbar: " + sb);
        
        return sb.toString();
    }

	public boolean isShowToolBarHelp() {
		return showToolBarHelp;
	}

	public void setShowToolBarHelp(boolean showToolBarHelp) {
		this.showToolBarHelp = showToolBarHelp;
	}
}
