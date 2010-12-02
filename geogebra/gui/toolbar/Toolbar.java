/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui.toolbar;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.layout.DockPanel;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.main.Application;
import geogebra3D.euclidian3D.EuclidianView3D;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JToolBar;

/**
 * Base class for a single toolbar, either for a dock panel or as a general toolbar.
 * Toolbars are always visible as part of a ToolbarContainer.
 */
public class Toolbar extends JToolBar {
	private static final long serialVersionUID = 1L;

	/**
	 * Integer used to indicate a separator in the toolbar.
	 */
	public static final Integer SEPARATOR = new Integer(-1);

	/**
	 * Instance of the application.
	 */
	private Application app;
	
	/**
	 * Dock panel associated to this toolbar or null if this is the general toolbar. Just
	 * a single toolbar might have no dock panel, otherwise the ToolbarContainer logic
	 * will not work properly.
	 */
	private DockPanel dockPanel;
	
	/**
	 * The mode selected at the moment.
	 */
	private int selectedMode;

	private ArrayList<ModeToggleMenu> modeToggleMenus;
	private ModeToggleMenu temporaryModes;
	
	/**
	 * Creates general toolbar.
	 * 
	 * @param app
	 */
	public Toolbar(Application app) {
		this(app, null);
	}
	
	/**
	 * Creates toolbar for a specific dock panel. Call buildGui() to actually
	 * create the GUI of this toolbar.
	 * 
	 * @param app
	 * @param dockPanel
     */
	public Toolbar(Application app, DockPanel dockPanel)  {
		this.app = app;	
		this.dockPanel = dockPanel;
		
        setFloatable(false);
        setBackground(getBackground());
	}
		
	/**
     * Creates a toolbar using the current strToolBarDefinition. 
     */
    public void buildGui() {    	    	
    	selectedMode = -1;
        
        ModeToggleButtonGroup bg = new ModeToggleButtonGroup();     
        modeToggleMenus = new ArrayList<ModeToggleMenu>();
    	
        // create toolbar
        removeAll(); 

        setAlignmentX(LEFT_ALIGNMENT);
       
        // add menus with modes to toolbar
       	addCustomModesToToolbar(bg);
        
        // add invisible temporary menu
       	temporaryModes = new ModeToggleMenu(app, this, bg);
       	temporaryModes.setVisible(false);
       	modeToggleMenus.add(temporaryModes);
        add(temporaryModes);
        
        setMode(app.getMode());
    }
    
    /**
     * Sets toolbar mode. This will change the selected toolbar icon.
     * @return true if mode could be selected in toolbar. 
     */
    public boolean setMode(int mode) {       
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
         		// don't display move mode icon in other views, this is a bit irritating
         		if(dockPanel == null || mode != EuclidianView.MODE_MOVE) {
	         		// we insert a temporary icon if possible
	         		temporaryModes.addMode(mode);
	         		temporaryModes.setVisible(true);
	         		temporaryModes.selectMode(mode);
         		}
         	}
        } 
        
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
    private void addCustomModesToToolbar(ModeToggleButtonGroup bg) {  
    	Vector toolbarVec;
    	try {
    		if(dockPanel != null) {
    			toolbarVec = parseToolbarString(dockPanel.getToolbarString());    			
    		} else {
    			toolbarVec = parseToolbarString(app.getGuiManager().getToolbarDefinition());
    		}
	    } catch (Exception e) {
    		if(dockPanel != null) {
    			Application.debug("invalid toolbar string: " + dockPanel.getToolbarString());    			
    		} else {
    			Application.debug("invalid toolbar string: " + app.getGuiManager().getToolbarDefinition());
    		}
			toolbarVec = parseToolbarString(getDefaultToolbarString());
		}
 
    	// set toolbar
	    boolean firstButton = true;
		for (int i = 0; i < toolbarVec.size(); i++) {	        
	        Object ob = toolbarVec.get(i);
	        
	        // separator between menus
	        if (ob instanceof Integer) {
	        	addSeparator();
	        	continue;
	        }
	        
	        // new menu
	        Vector menu = (Vector) ob;
	        ModeToggleMenu tm = new ModeToggleMenu(app, this, bg);
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
	        	add(tm);
		}
    }
    
    /**
     * @return The dock panel associated with this toolbar or null if this
     * is the general toolbar.
     */
    public DockPanel getDockPanel() {
    	return dockPanel;
    }
    
    /**
     * @return The top-most panel of the window this toolbar belongs to.
     */
    public Component getMainComponent() {
    	// if this is the general toolbar the main component is the application main
    	// component (not true for toolbars in EV)
    	if(dockPanel == null) {
    		return app.getMainComponent();
    	}
    	
    	// this toolbar belongs to a dock panel
    	else {
    		// in frame?
    		if(dockPanel.isOpenInFrame()) {
    			return dockPanel;
    		}
    		
    		// otherwise use the application main component
    		else {
    			return app.getMainComponent();
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
	public static Vector parseToolbarString(String toolbarString) {			
		String [] tokens = toolbarString.split(" ");
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
	        	 //menu.add(SEPARATOR);	        	 
	        	 //toolbar.add(menu);
	        	 toolbar.add(SEPARATOR);
	        	 
	        	 // start next menu
	        	 menu = new Vector();	        	 
	         }
	         else if (tokens[i].equals(",")) { // separator within menu
	        	 menu.add(SEPARATOR);
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
	 * @return The default definition of this toolbar with macros.
	 */
    public String getDefaultToolbarString() {
    	if(dockPanel != null) {
    		return dockPanel.getDefaultToolbarString();
    	} else {
    		return Toolbar.getAllTools(app);
    	}
    }
    
    /**
     * @param app
     * @return All tools as a toolbar definition string
     */
    public static String getAllTools(Application app) {
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append(getAllToolsNoMacros());
    	
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
        
        return sb.toString();
    }
    
    /**
	 * @return The default definition of the general tool bar without macros.
	 */
    public static String getAllToolsNoMacros() {
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
        sb.append(" ");
        sb.append (EuclidianView.MODE_ATTACH_DETACH);       
                            
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
        sb.append(" ");
        sb.append(EuclidianView.MODE_RIGID_POLYGON ); 
        sb.append(" ");
        sb.append(EuclidianView.MODE_POLYLINE ); 
       
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
        sb.append(" , ");  
        sb.append(EuclidianView.MODE_TEXT );
        sb.append(" ");
        sb.append(EuclidianView.MODE_DYNAMIC_TEXT );
        sb.append(" ");
        sb.append(EuclidianView.MODE_IMAGE);        
        sb.append(" ");
        sb.append(EuclidianView.MODE_PEN);        
        sb.append(" , ");
        sb.append(EuclidianView.MODE_RELATION);
        sb.append(" ");
        sb.append(EuclidianView.MODE_PROBABILITY_CALCULATOR);
 
        // objects with actions
        sb.append(" | ");
        sb.append(EuclidianView.MODE_SHOW_HIDE_CHECKBOX);
        sb.append(" ");
        sb.append(EuclidianView.MODE_BUTTON_ACTION);
        sb.append(" ");
        sb.append(EuclidianView.MODE_TEXTFIELD_ACTION);

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
    
    

	/**
	 * @return default toolbar (3D)
	 */
    public static String getAllToolsNoMacros3D() {
		return EuclidianView3D.MODE_MOVE
		+" || "
		+EuclidianView3D.MODE_POINT_IN_REGION
		+" "
		+EuclidianView3D.MODE_INTERSECT
		+" | "
		+EuclidianView3D.MODE_JOIN
		+" "
		+EuclidianView3D.MODE_SEGMENT
		+" "
		+EuclidianView3D.MODE_RAY
		+" , "
		+EuclidianView3D.MODE_VECTOR
		+" || "
		+EuclidianView3D.MODE_POLYGON
		//+" | "
		//+EuclidianView3D.MODE_CIRCLE_THREE_POINTS
		+" || "
		+EuclidianView3D.MODE_PLANE_THREE_POINTS
		+" , "
		+EuclidianView3D.MODE_PLANE_POINT_LINE
		+" | "
		+EuclidianView3D.MODE_ORTHOGONAL_PLANE
		+" , "
		+EuclidianView3D.MODE_PARALLEL_PLANE
		+" || "
		+EuclidianView3D.MODE_RIGHT_PRISM		
		//+" , "
		//+EuclidianView3D.MODE_PRISM		
		+" | "
		+EuclidianView3D.MODE_SPHERE_TWO_POINTS
		+" "
		+EuclidianView3D.MODE_SPHERE_POINT_RADIUS
		+" || "
		+EuclidianView3D.MODE_ROTATEVIEW
		+" "
		+EuclidianView3D.MODE_TRANSLATEVIEW
		+" "
		+EuclidianView3D.MODE_ZOOM_IN
		+" "
		+EuclidianView3D.MODE_ZOOM_OUT
		+" | "
		+EuclidianView3D.MODE_VIEW_IN_FRONT_OF
		;
	}
}
