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
import geogebra.kernel.Kernel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JMenuItem;


public class GUIController extends WindowAdapter
implements ActionListener
//, ComponentListener
{
    private Application app;    
    private Kernel kernel;

    public GUIController(Application app ) {        
        this.app = app;
        kernel = app.getKernel();
    }

/* ***********************
 * WindowAdapter
 ************************/
    
    public void windowClosing(WindowEvent event) {     
            app.exit(); 
    }    
    
/* **********************
 * ActionListener
 ************************/
 
    public void actionPerformed(ActionEvent event) {                
            if (event.getSource() instanceof JMenuItem) {
                    processMenuCommand(event.getActionCommand());                   
            }            
    }
    
/************************
 * MENU
 ************************/   
    
    private void processMenuCommand( String cmd ) {                            
        
        // change angle unit        
        if (cmd.equals("Degree")) {
            kernel.setAngleUnit( Kernel.ANGLE_DEGREE );
			kernel.updateConstruction();
			app.setUnsaved();
        }
        else if (cmd.equals("Radiant")) {
			kernel.setAngleUnit( Kernel.ANGLE_RADIANT );
			kernel.updateConstruction();		
			app.setUnsaved();
        }
        
        // change graphics quality       
        else if (cmd.equals("LowQuality")) {
            app.getEuclidianView().setAntialiasing(false);
        }
        else if (cmd.equals("HighQuality")) {
			app.getEuclidianView().setAntialiasing(true);
        }
        
        // font size
        else if (cmd.endsWith("pt")) {
            try{
                app.setFontSize(Integer.parseInt(cmd.substring(0,2)));
				app.setUnsaved();
                System.gc();
            } catch (Exception e) {            	
               app.showError(e.toString());   
            };
        }   
        
		// decimal places
		else if (cmd.endsWith("decimals")) {
			try{
				kernel.setPrintDecimals(Integer.parseInt(cmd.substring(0,1)));	
				kernel.updateConstruction();	
				app.setUnsaved();			
			} catch (Exception e) {            	
			   app.showError(e.toString());   
			};
		}          
		
		// Point capturing
		else if (cmd.endsWith("PointCapturing")) {				
			int mode = Integer.parseInt(cmd.substring(0,1));			
			app.getEuclidianView().setPointCapturing(mode);														
			app.setUnsaved();
		}      
        
        // Continuity 
		else if (cmd.endsWith("Continuity")) {				
			boolean state = cmd.startsWith("true");			
			kernel.setContinuous(state);	
			kernel.updateConstruction();
			app.setUnsaved();
		}   
          
    }
    
	
	
	/*
	public void componentResized(ComponentEvent arg0) {
		//JFrame frame = app.getFrame();
		//if (frame != null) {
			//SwingUtilities.updateComponentTreeUI(frame);
		//}
	}

	public void componentMoved(ComponentEvent arg0) {	
	}

	public void componentShown(ComponentEvent arg0) {
	}

	public void componentHidden(ComponentEvent arg0) {
	}
	*/
	
	
}
