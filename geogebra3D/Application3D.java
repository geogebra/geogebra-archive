/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
package geogebra3D;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import geogebra.gui.app.GeoGebraFrame;
import geogebra.kernel.Kernel;
import geogebra.main.AppletImplementation;
import geogebra.main.Application;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.kernel3D.Kernel3D;


public abstract class Application3D extends Application{


    private EuclidianView3D euclidianView3D;
    private EuclidianController3D euclidianController3D;      
    protected Kernel3D kernel3D;
    
    
    private JSplitPane sp3D;
    private int initSplitDividerLocationHOR3D = 50; // init value
    private boolean show3DView = true; 


    public Application3D(String[] args, GeoGebraFrame frame, boolean undoActive) {
        this(args, frame, null, undoActive);
    }

    public Application3D(String[] args, AppletImplementation applet, boolean undoActive) {
    	this(args, null, applet, undoActive);
    }
    
    
    private Application3D(String[] args, GeoGebraFrame frame, AppletImplementation applet, boolean undoActive) { 
    	
    	super(args, frame, applet, undoActive);

		euclidianController3D = new EuclidianController3D(kernel3D);
        euclidianView3D = new EuclidianView3D(euclidianController3D);  
        
	    //TODO remove 3D test : just comment following line        
        new Test3D(kernel3D);
        
        //init 3D view
        euclidianView3D.setZZero(-7.0);
        euclidianView3D.setRotXY(-4*Math.PI/6,Math.PI/6,true);
        
        
        //init toolbar
        String myToolBar3D =  EuclidianView3D.MODE_MOVE
        						+" || "
        						+EuclidianView3D.MODE_POINT
        						+" || "
        						+EuclidianView3D.MODE_TRANSLATEVIEW;
        getGuiManager().setToolBarDefinition( myToolBar3D );
        updateToolBar();
 		
    }      
    
    
	public void initKernel(){
		Application.debug("initKernel() : Application3D");
		kernel3D = new Kernel3D(this);
		kernel = kernel3D;
	}
    

    
    
	public void setMode(int mode) {
		currentSelectionListener = null;

		if (appGuiManager != null)
			getGuiManager().setMode(mode);
		else if (euclidianView != null)
			euclidianView.setMode(mode);
		
		if (euclidianView3D != null)
			euclidianView3D.setMode(mode);
		
	}
    
    
    
    
    
    
    
    
    
    

    public void updateCenterPanel(boolean updateUI) {
    	centerPanel.removeAll();
        JDesktopPane dtp = new JDesktopPane();
        dtp.setPreferredSize(new Dimension(800,500));
        centerPanel.add(dtp);
        
        //algebra internal frame
        JInternalFrame frameAlgebra = new JInternalFrame("Algebra view", true,true, true, true);
        frameAlgebra.setContentPane(getGuiManager().getAlgebraView());
        frameAlgebra.setSize(200, 480);
        frameAlgebra.setLocation(0, 0);
        frameAlgebra.setVisible(true);
        dtp.add(frameAlgebra);
        
        
        //2D internal frame
        JInternalFrame frame2D = new JInternalFrame("2D view", true,true, true, true);
        frame2D.setContentPane(euclidianView);
        frame2D.setSize(640, 480);
        frame2D.setLocation(200, 0);
        frame2D.setVisible(true);
        dtp.add(frame2D);
        
        //3D internal frame
        JInternalFrame frame3D = new JInternalFrame("3D view - very early version", true,true, true, true);
        frame3D.setContentPane(euclidianView3D);
        frame3D.setSize(640, 480);
        frame3D.setLocation(250, 0);        
        frame3D.setVisible(true);        
        dtp.add(frame3D);
        frame3D.moveToFront();

    	
    	
    	/*
    	JPanel eup = new JPanel(new BorderLayout());
    	//Mathieu Blossier - start
    	//adding a 3D view splitting the euclidian view
    	//TODO : call super()
        if (show3DView) {       
 
        	eup.setBackground(Color.white);
            eup.add(euclidianView, BorderLayout.CENTER);
            

        	
        } else { 
            eup.setBackground(Color.white);
            eup.add(euclidianView, BorderLayout.CENTER);
        }
       
        
        //Mathieu Blossier - end
        
        
        if (showConsProtNavigation) {
        	eup.add(getGuiManager().getConstructionProtocolNavigation(), BorderLayout.SOUTH);
        	getGuiManager().getConstructionProtocolNavigation().setBorder(BorderFactory.
       		     createMatteBorder(1, 0, 0, 0, Color.gray));
        }                    
        
        if (showAlgebraView) {        	     
            if (horizontalSplit) {
                sp =  new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                        new JScrollPane(getGuiManager().getAlgebraView()), eup);
                sp.setDividerLocation(initSplitDividerLocationHOR);                
            }               
            else {
                sp =  new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                             eup, new JScrollPane(getGuiManager().getAlgebraView()));
                sp.setDividerLocation(initSplitDividerLocationVER);
            }               
            sp.addPropertyChangeListener("dividerLocation",
                        new DividerChangeListener());                                       
            
            centerPanel.add(sp, BorderLayout.CENTER);
        } else { 
            centerPanel.add(eup, BorderLayout.CENTER);
        }
        
        // border of euclidianPanel        
        int eupTopBorder = !showAlgebraView && showToolBar ? 1 : 0;
        int eupBottomBorder = showToolBar && !(showAlgebraView && !horizontalSplit) ? 1 : 0;
        eup.setBorder(BorderFactory.
        		createMatteBorder(eupTopBorder, 0, eupBottomBorder, 0, Color.gray));
        
        if (updateUI)        	
        	updateComponentTreeUI();       
        	
        */
    }  
    
 
    	
}
