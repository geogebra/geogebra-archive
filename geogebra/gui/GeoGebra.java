/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
package geogebra.gui;

import geogebra.Application;
import geogebra.algebra.AlgebraInput;
import geogebra.algebra.AlgebraView;
import geogebra.euclidian.EuclidianView;
import geogebra.util.Util;

import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class GeoGebra extends JFrame implements 
	WindowFocusListener, KeyEventDispatcher 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ArrayList instances = new ArrayList();
	private static GeoGebra activeInstance;
	private Application app;
	
	public GeoGebra() {	
		instances.add(this);	
		activeInstance = this;
	}
	
	public void dispose() {
		instances.remove(this);
		super.dispose();
	}		
	
	public Application getApplication() {
		return app;
	}
	
	public void setApplication(Application app) {
		this.app = app;
	}
	
	public int getInstanceNumber() {
		for (int i=0; i < instances.size(); i++) {
			if (this == instances.get(i))
				return i;
		}
		return -1;
	}
	
	public void windowGainedFocus(WindowEvent arg0) {
		activeInstance = this;
		app.updateMenusForInstances();
		System.gc();
	}

	public void windowLostFocus(WindowEvent arg0) {	
	}
    
	public void initFrame() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = 900; // width
        //int w = 1020; // width
        int h = 650; // height
        if (dim.width < w) w = dim.width - 10;
        if (dim.height < h) h = (int) (dim.height * 0.9);
                
        /*
        int offset = 20 * (instances.size() - 1);
        setLocation((dim.width - w) / 2 + offset, (dim.height - h) / 2 + offset);
        */
        setLocation((dim.width - w) / 2 , (dim.height - h) / 2);
        setSize(w, h);
    }
	
    public static void main(String[] args) {      	
    	try {           
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());                                    
        } catch (Exception e) {
            System.err.println(e);
        }  	        
  			 		        
        // check java version
        double javaVersion = Util.getJavaVersion();       
        if (javaVersion < 1.42) {
            JOptionPane.showMessageDialog(null, 
                "Sorry, GeoGebra cannot be used with your Java version " + javaVersion + 
                "\nPlease visit http://www.java.com to get a newer version of Java.");
            return;                             
        }
        
        GeoGebra ggbWin = createNewWindow(args);
        
        // for key listening 
        KeyboardFocusManager.getCurrentKeyboardFocusManager().
                addKeyEventDispatcher(ggbWin);	
        
        // check if we run on a Mac
        String lcOSName = System.getProperty("os.name").toLowerCase();
        boolean MAC_OS = lcOSName.startsWith("mac");
        
        if (MAC_OS) {	
        	// handle MacOS X file opening when ggb file is double clicked
			net.roydesign.app.Application.getInstance().addOpenDocumentListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							net.roydesign.event.ApplicationEvent mac_evt = (net.roydesign.event.ApplicationEvent) evt;							
					        // get filename and build args array
					        File fileToOpen = mac_evt.getFile();	
					        boolean isMacroFile = Application.FILE_EXT_GEOGEBRA_TOOL.equals(Application.getExtension(fileToOpen).toLowerCase());
					        activeInstance.getApplication().loadFile(fileToOpen, isMacroFile);						    
					     }
				 });
        }       
        
             
    }
    
    public static GeoGebra createNewWindow(String [] args) {
    	 // set Application's size, position and font size       
    	GeoGebra wnd = new GeoGebra();
    	Application app = new Application(args, wnd, true);        
        wnd.app = app;              
        wnd.getContentPane().add(app.buildApplicationPanel());        
        //wnd.addComponentListener(app.getGUIController());
        wnd.initFrame();  
        updateAllTitles();        
        wnd.addWindowFocusListener(wnd);
        wnd.setDropTarget(new DropTarget(wnd, new FileDropTargetListener(app)));
        app.initInBackground();
        wnd.setVisible(true);
        return wnd;
    }    
    
    public static int getInstanceCount() {
		return instances.size();
	}
    
    public static ArrayList getInstances() {
    	return instances;
    }
    
    static GeoGebra getInstance(int i) {
    	return (GeoGebra) instances.get(i);
    }
    
    public static void updateAllTitles() {
    	for (int i=0; i < instances.size(); i++) {
			Application app = ((GeoGebra) instances.get(i)).app;
			app.updateTitle();
		}				 
    }
    
    /**
     * Checks all opened GeoGebra instances if their current
     * file is the given file.
     * @param file
     * @return GeoGebra instance with file open or null
     */
    public static GeoGebra getInstanceWithFile(File file) {
    	if (file == null) return null;    
    	
    	String absPath = file.getAbsolutePath();
    	for (int i=0; i < instances.size(); i++) {
    		GeoGebra inst = (GeoGebra) instances.get(i);
			Application app = inst.app;
		
			File currFile = app.getCurrentFile();
			if (currFile != null) {
				if (absPath.equals(currFile.getAbsolutePath()))
					return inst;
			}
		}		
    	return null;
    }
    
    /* 
	 * KeyEventDispatcher implementation
	 * to handle key events globally for the application
	 */
	public boolean dispatchKeyEvent(KeyEvent e) {
		  //make sure the event is not consumed
		  if (e.isConsumed()) return true;
		  		  
		  Application app = activeInstance.getApplication();
		  
		  // if the glass pane is visible, don't do anything
		  // (there might be an animation running)
		  if (app.getGlassPane().isVisible())
			  return false;
		  
		  boolean consumed = false;
		  Object source = e.getSource();
		  
		  // catch all key events from algebra view and give
		  // them to the algebra controller	  
		  AlgebraView av = app.getAlgebraView();
		  if (source == av) {			  	
			switch (e.getID()) {
				case KeyEvent.KEY_PRESSED:
					consumed = app.getAlgebraController().
									keyPressedConsumed(e);					
					break;				
			}					
		  }		  		  		  
		  if (consumed) return true;
		  		  	
		  switch (e.getKeyCode()) {
				case KeyEvent.VK_F3:
					// F3 key: set focus to input field
			 		AlgebraInput ai = app.getAlgebraInput();
			 		if (ai != null) { 
			 			ai.setFocus();
			 			consumed = true;
			 		}
			 		break;
			 		
			 	// ESC changes to move mode
				case KeyEvent.VK_ESCAPE:											
					// ESC is also handeled by algebra input field  
					ai = app.getAlgebraInput();
					if (ai != null && ai.hasFocus()) {
						consumed = false;
					} else {
						app.setMode(EuclidianView.MODE_MOVE);
						consumed = true;
					}												
					break;									
    					
				// F4 changes to move mode
		 		case KeyEvent.VK_F4:		 		
		 			app.setMode(EuclidianView.MODE_MOVE);
		 			consumed = true;		 			
		 			break;		 					 		 
		}
			
		  /*
		 // Ctrl-key pressed
		  if (!app.isApplet() && e.isMetaDown()){
			  switch (e.getKeyChar()) {
			  							 					 		 
			  }			 
		  }*/

		  return consumed; 
	}

}