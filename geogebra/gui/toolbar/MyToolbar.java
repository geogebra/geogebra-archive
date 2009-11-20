/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui.toolbar;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.MySmallJButton;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.main.Application;
import geogebra.util.Util;

import java.awt.BorderLayout;
import java.awt.FontMetrics;
import java.awt.SystemColor;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

public class MyToolbar extends JPanel implements ComponentListener{

	private static final long serialVersionUID = 8983050697241236851L;

	public static final Integer TOOLBAR_SEPARATOR = new Integer(-1);

	private Application app;
	private ArrayList modeToggleMenus;   
	private boolean showToolBarHelp = true;	
	private JLabel modeNameLabel;
	private JPanel toolbarHelpPanel;
	private int selectedMode;
	
	private ModeToggleMenu temporaryModes;
	
	/**
     * Creates a panel for the application's toolbar. 
     * Note: call initToolbar() to fill the panel.
     */
	public MyToolbar(Application app)  {
		this.app = app;	
		addComponentListener(this);
	}
		
	/**
     * Creates a toolbar using the current strToolBarDefinition. 
     */
    public void initToolbar() {    	    	
    	selectedMode = -1;
    	
        // create toolBars                       
        removeAll();
        setLayout(new BorderLayout(5,2));   
        setBorder(BorderFactory.createCompoundBorder(
        	BorderFactory.createMatteBorder(1, 0, 0, 0, SystemColor.controlShadow),
        	BorderFactory.createEmptyBorder(2, 2, 1, 2)));
        
        JToolBar tb = new JToolBar();   
        tb.setFloatable(false);  
        tb.setBackground(getBackground());
        ModeToggleButtonGroup bg = new ModeToggleButtonGroup();     
        modeToggleMenus = new ArrayList();

        // wrap toolbar to be vertically centered
        JPanel toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.Y_AXIS));
        toolbarPanel.add(Box.createVerticalGlue());
        tb.setAlignmentX(LEFT_ALIGNMENT);
       	toolbarPanel.add(tb);
       	toolbarPanel.add(Box.createVerticalGlue());
        
       	if (showToolBarHelp) {       
       		// mode label       		
           	modeNameLabel = new JLabel();             	

           	// put into panel to 
           	toolbarHelpPanel = new JPanel();
           	toolbarHelpPanel.setLayout(new BoxLayout(toolbarHelpPanel, BoxLayout.Y_AXIS));
           	toolbarHelpPanel.add(Box.createVerticalGlue());
           	modeNameLabel.setAlignmentX(LEFT_ALIGNMENT);
           	toolbarHelpPanel.add(modeNameLabel);
           	toolbarHelpPanel.add(Box.createVerticalGlue());
           	
           	add(toolbarHelpPanel, BorderLayout.CENTER);
       	}   
       	       	
        // UNDO Toolbar     
       	JPanel undoPanel = null;
        if (app.isUndoActive()) {
	        // undo part                  	   
	        undoPanel = new JPanel();
	        if (app.getMaxIconSize() >= 32) {
	        	undoPanel.setLayout(new BoxLayout(undoPanel, BoxLayout.Y_AXIS));
	        } else {
	        	undoPanel.setLayout(new BoxLayout(undoPanel, BoxLayout.X_AXIS));
	        }
	        undoPanel.add(Box.createVerticalGlue());
	        
	        // undo button
	        MySmallJButton button = new MySmallJButton(app.getGuiManager().getUndoAction(), 7); 	
	        String text = app.getMenu("Undo");
	        button.setText(null);
	        button.setToolTipText(text);  
	        button.setAlignmentX(RIGHT_ALIGNMENT);
	        undoPanel.add(button);

	        // redo button
	        button = new MySmallJButton(app.getGuiManager().getRedoAction(), 7);         	        
	        text = app.getMenu("Redo");
	        button.setText(null);
	        button.setToolTipText(text);        
	        button.setAlignmentX(RIGHT_ALIGNMENT);
	        undoPanel.add(button); 
	       
	        undoPanel.add(Box.createVerticalGlue());	               
        }       
                
        // put together panel
        removeAll();
        setLayout(new BorderLayout(10,0));    
        add(toolbarPanel, BorderLayout.WEST); 
        
        if (toolbarHelpPanel != null)
        	add(toolbarHelpPanel, BorderLayout.CENTER);

        if (undoPanel != null) {
        	add(undoPanel, BorderLayout.EAST);
        }
        
        // add menus with modes to toolbar
       	addCustomModesToToolbar(tb, bg);
        
        // add invisible temporary menu
       	temporaryModes = new ModeToggleMenu(app, bg);
       	temporaryModes.setVisible(false);
       	modeToggleMenus.add(temporaryModes);
        tb.add(temporaryModes);
       	
        setSelectedMode(app.getMode());
        updateToolbarHelpText();
    }
    
    /**
     * Sets toolbar mode. This will change the selected toolbar icon.
     * @return true if mode could be selected in toolbar. 
     */
    public boolean setSelectedMode(int mode) {       
    	boolean success = false;
    	
    	// there is no special icon/button for the selection listener mode, use the move mode button instead
    	if(mode == EuclidianView.MODE_SELECTION_LISTENER) {
    		mode = EuclidianView.MODE_MOVE;
    	}
    	
    	if(temporaryModes.isVisible()) {
    		temporaryModes.clearModes();
     		temporaryModes.setVisible(false);
    	}
    	
        if (modeToggleMenus != null) {
        	// show move mode icon for algebra input (selection) mode
        	//if (mode == EuclidianView.MODE_ALGEBRA_INPUT) {
        	//	showMode = EuclidianView.MODE_MOVE;
        	//}
        	       
         	for (int i=0; i < modeToggleMenus.size(); i++) {
         		ModeToggleMenu mtm = (ModeToggleMenu) modeToggleMenus.get(i);
         		if (mtm.selectMode(mode)) {
         			success = true;
         			break;
         		}
        	}
         	
         	if (success) {
         		this.selectedMode = mode;         		
         	} else {
         		// we insert a temporary icon if possible
         		temporaryModes.addMode(mode);
         		temporaryModes.setVisible(true);
         		temporaryModes.selectMode(mode);
         	}
        }              
                
        updateToolbarHelpText();
        return success;
    }     
    
    public int getSelectedMode() {
    	return selectedMode;
    }
    
    public int getFirstMode() {
    	if (modeToggleMenus == null || modeToggleMenus.size() == 0) 
    		return  -1;
    	else {
    		ModeToggleMenu mtm = (ModeToggleMenu) modeToggleMenus.get(0);
    		return mtm.getFirstMode();
    	}
    }
    
    private void updateToolbarHelpText() {
    	if (modeNameLabel == null) return;
    	
    	int mode = app.getMode();
    	
    	String toolName = app.getToolName(mode);
    	String helpText = app.getToolHelp(mode);
    	
    	// get wrapped toolbar help text
        String wrappedText = wrappedModeText(toolName, helpText, toolbarHelpPanel);    	
    	modeNameLabel.setText(wrappedText);
    	
    	// tooltip
    	modeNameLabel.setToolTipText(app.getToolTooltipHTML(mode));
    	toolbarHelpPanel.validate();
    }
    
    /** 
     * Returns mode text and toolbar help as html text with line breaks
     * to fit in the given panel.     
     */
    private String wrappedModeText(String modeName, String helpText, JPanel panel) {
    	FontMetrics fm = getFontMetrics(app.getBoldFont());    	
   
    	// check width of panel
    	int panelWidth = panel.getWidth();
    	int charWidth = fm.stringWidth("W");    	
    	panelWidth = panelWidth - charWidth; // needed for correct line breaks
    	
    	if (panelWidth <= 0) {    	
    		return "";
    	} 
    	
    	// show no more than 2 lines
     	int maxLines = 2*fm.getHeight() < panel.getHeight() ? 2 : 1; 
     	//Math.min(2, Math.round(panel.getHeight() / (float) fm.getHeight()));    	
    	StringBuilder sbToolName = new StringBuilder();    
    	sbToolName.append("<html><b>");
    	
    	// check if mode name itself fits
    	if (fm.stringWidth(modeName) >  panelWidth)
    		return "";
    	
    	// mode name
    	BreakIterator iterator = BreakIterator.getWordInstance(app.getLocale());
		iterator.setText(modeName);
		int start = iterator.first();
		int end = iterator.next();
		int line = 1;
		
		int len = 0;
		while (end != BreakIterator.DONE)
		{
			String word = modeName.substring(start,end);
			if( len + fm.stringWidth(word) > panelWidth )
			{
				if (++line > maxLines) {
					// if the tool name doesn't fit: return an empty string
					return "";
					
					//sbToolName.append("...");
					//sbToolName.append("</b></html>");
					//return sbToolName.toString();
				}
				sbToolName.append("<br>");
				len = fm.stringWidth(word);	
			}
			else
			{
				len += fm.stringWidth(word);
			}
 
			sbToolName.append(Util.toHTMLString(word));
			start = end;
			end = iterator.next();
		}		
		sbToolName.append("</b>");
    	
		
		// mode help text
		StringBuilder sbToolHelp = new StringBuilder();   
		fm = getFontMetrics(app.getPlainFont());
		
		// try to put help text into single line
		if (line < maxLines && fm.stringWidth(helpText) < panelWidth) {
			++line;
			sbToolHelp.append("<br>");
			sbToolHelp.append(Util.toHTMLString(helpText));
		}
		else {			
			sbToolHelp.append(": ");
			iterator.setText(helpText);
			start = iterator.first();
			end = iterator.next();
			while (end != BreakIterator.DONE)
			{
				String word = helpText.substring(start,end);
				if( len + fm.stringWidth(word) >  panelWidth)
				{
					if (++line > maxLines) {						
						// show tool help only when it can be completely shown
						sbToolHelp.setLength(0);
						//sbToolHelp.append("...");
						break;
					}
					sbToolHelp.append("<br>");
					len = fm.stringWidth(word);								
				}
				else
				{
					len += fm.stringWidth(word);
				}
	 
				sbToolHelp.append(Util.toHTMLString(word));
				start = end;
				end = iterator.next();
			}
		}
		
		// show tool help only when it can be completely shown		
		sbToolName.append(sbToolHelp);
		sbToolName.append("</html>");
		return sbToolName.toString();
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
	    	toolbarVec = createToolBarVec(app.getGuiManager().getToolBarDefinition());
	    } catch (Exception e) {
			Application.debug("invalid toolbar string: " + app.getGuiManager().getToolBarDefinition());
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
	       			if (!"".equals(app.getToolName(mode))) {
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
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append(getDefaultToolbarStringNoMacros());
    	
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
        
        //Application.debug("defToolbar: " + sb);
        
        return sb.toString();
    }
    
    /**
	 * Returns the default toolbar String definition.
	 */
    public static String getDefaultToolbarStringNoMacros() {
    	StringBuilder sb = new StringBuilder();
    	
    	// move
        sb.append(EuclidianView.MODE_MOVE);        
        sb.append(" ");
        sb.append(EuclidianView.MODE_MOVE_ROTATE);       
    	sb.append(" ");
    	sb.append(EuclidianView.MODE_RECORD_TO_SPREADSHEET);                      
        
        // points   
        sb.append(" || ");
        sb.append(EuclidianView.MODE_POINT);
        sb.append(" ");        
        sb.append(EuclidianView.MODE_POINT_IN_REGION);
        sb.append(" ");
        sb.append(EuclidianView.MODE_INTERSECT);
        sb.append(" ");
        sb.append (EuclidianView.MODE_MIDPOINT);       
                            
        // basic lines 
        sb.append(" | ");
        sb.append(EuclidianView.MODE_JOIN);
        sb.append(" ");
        sb.append(EuclidianView.MODE_SEGMENT);
        sb.append(" ");
        sb.append(EuclidianView.MODE_SEGMENT_FIXED);
        sb.append(" ");
        sb.append(EuclidianView.MODE_RAY );
        sb.append(" , ");       
        sb.append(EuclidianView.MODE_VECTOR);
        sb.append(" ");
        sb.append(EuclidianView.MODE_VECTOR_FROM_POINT);                
               
        // advanced lines
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
        sb.append(" , ");
        sb.append(EuclidianView.MODE_FITLINE);
        sb.append(" , ");       
        sb.append(EuclidianView.MODE_LOCUS ); 
         
        // polygon
        sb.append(" || ");       
        sb.append(EuclidianView.MODE_POLYGON);       
        sb.append(" ");
        sb.append(EuclidianView.MODE_REGULAR_POLYGON ); 
       
        // circles, arcs
        sb.append(" | ");       
        sb.append(EuclidianView.MODE_CIRCLE_TWO_POINTS);
        sb.append(" ");
        sb.append(EuclidianView.MODE_CIRCLE_POINT_RADIUS );
        sb.append(" ");
        sb.append(EuclidianView.MODE_COMPASSES);
        sb.append(" ");
        sb.append(EuclidianView.MODE_CIRCLE_THREE_POINTS);   
        sb.append(" , ");            
        sb.append(EuclidianView.MODE_SEMICIRCLE);
        sb.append("  ");
        sb.append(EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS);
        sb.append(" ");
        sb.append(EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS);
        sb.append (" , ");       
        sb.append(EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS);
        sb.append(" ");
        sb.append(EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS);
        
        // conics
        sb.append(" | ");
        sb.append(EuclidianView.MODE_ELLIPSE_THREE_POINTS);   
        sb.append(" ");            
        sb.append(EuclidianView.MODE_HYPERBOLA_THREE_POINTS);   
        sb.append(" ");            
        sb.append(EuclidianView.MODE_PARABOLA);   
        sb.append(" , ");            
        sb.append(EuclidianView.MODE_CONIC_FIVE_POINTS);
            
        // measurements
        sb.append(" || ");
        sb.append(EuclidianView.MODE_ANGLE);
        sb.append(" ");
        sb.append(EuclidianView.MODE_ANGLE_FIXED);
        sb.append(" , ");
        sb.append(EuclidianView.MODE_DISTANCE);
        sb.append(" ");
        sb.append (EuclidianView.MODE_AREA);
        sb.append(" ");
        sb.append(EuclidianView.MODE_SLOPE);
       
        // transformations
        sb.append(" | ");
        sb.append(EuclidianView.MODE_MIRROR_AT_LINE );      
        sb.append(" ");
        sb.append(EuclidianView.MODE_MIRROR_AT_POINT);
        sb.append(" ");
        sb.append(EuclidianView.MODE_MIRROR_AT_CIRCLE);
        sb.append(" ");
        sb.append(EuclidianView.MODE_ROTATE_BY_ANGLE);
        sb.append(" ");
        sb.append(EuclidianView.MODE_TRANSLATE_BY_VECTOR);
        sb.append(" ");
        sb.append(EuclidianView.MODE_DILATE_FROM_POINT);
       
        // dialogs
        sb.append(" | ");
        sb.append(EuclidianView.MODE_SLIDER);
        sb.append(" ");
        sb.append(EuclidianView.MODE_SHOW_HIDE_CHECKBOX);
        sb.append(" ");
        sb.append(EuclidianView.MODE_BUTTON_ACTION);
        sb.append(" ");
        sb.append(EuclidianView.MODE_TEXTFIELD_ACTION);
        sb.append(" , ");  
        sb.append(EuclidianView.MODE_PEN);
        sb.append(" , ");  
        sb.append(EuclidianView.MODE_TEXT );
        sb.append(" ");
        sb.append(EuclidianView.MODE_IMAGE);        
        sb.append(" , ");
        sb.append(EuclidianView.MODE_RELATION);  
 
        // properties
        sb.append(" || ");
        sb.append(EuclidianView.MODE_TRANSLATEVIEW);
        sb.append(" ");
        sb.append(EuclidianView.MODE_ZOOM_IN);
        sb.append(" ");
        sb.append(EuclidianView.MODE_ZOOM_OUT);
        sb.append(" , ");       
        sb.append(EuclidianView.MODE_SHOW_HIDE_OBJECT);
        sb.append(" ");
        sb.append(EuclidianView.MODE_SHOW_HIDE_LABEL );
        sb.append(" ");
        sb.append(EuclidianView.MODE_COPY_VISUAL_STYLE);
        sb.append(" , ");       
        sb.append(EuclidianView.MODE_DELETE);              
        
        return sb.toString();
    }

	public boolean isShowToolBarHelp() {
		return showToolBarHelp;
	}

	public void setShowToolBarHelp(boolean showToolBarHelp) {
		this.showToolBarHelp = showToolBarHelp;
	}

	public void componentHidden(ComponentEvent e) {		
	}

	public void componentMoved(ComponentEvent e) {		
	}

	public void componentResized(ComponentEvent e) {
		if (getWidth() != oldWidth) {
			oldWidth = getWidth();
			updateToolbarHelpText();
		}		
	}	
	private int oldWidth;

	public void componentShown(ComponentEvent e) {
	}
	
	
}
