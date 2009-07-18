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

import geogebra.gui.DefaultGuiManager;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.layout.Layout;
import geogebra.main.AppletImplementation;
import geogebra.main.Application;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;


public abstract class Application3D extends Application{


    private EuclidianView3D euclidianView3D;
    private EuclidianController3D euclidianController3D;      
    protected Kernel3D kernel3D;
    
	JFrame f2d = new JFrame("2d view");
    JFrame fspr = new JFrame("spreadsheet view");


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
        new Test3D(kernel3D,euclidianView,euclidianView3D,this);
        
        //init 3D view
        /*
        euclidianView3D.setZZero(-7.0);
        euclidianView3D.setRotXY(-Math.PI/6,Math.PI/6,true);
         */
        
        //init toolbar
        
        String myToolBar3D =  EuclidianView3D.MODE_MOVE
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
        						+" || "
        						+EuclidianView3D.MODE_POLYGON
        						+" || "
        						+EuclidianView3D.MODE_TRANSLATEVIEW;
        
        DefaultGuiManager dgm = (DefaultGuiManager) getGuiManager();
        dgm.setToolBarDefinition( myToolBar3D );
        //dgm.getLayout().getPerspective(0).setToolbarDefinition(myToolBar3D);
        
        updateToolBar();
 		
    }      
    
    
	public void initKernel(){
		//Application.debug("initKernel() : Application3D");
		kernel3D = new Kernel3D(this);
		kernel = kernel3D;
	}
    

    
    
	public void setMode(int mode) {
		super.setMode(mode);
		
		if (euclidianView3D != null)
			euclidianView3D.setMode(mode);
		
	}
	
    
    
    
    
    
    
    
    
    
    /*

    public void updateCenterPanel(boolean updateUI) {
    	centerPanel.removeAll();
    	
    	
        JDesktopPane dtp = new JDesktopPane();
        dtp.setPreferredSize(new Dimension(800,500));
        centerPanel.add(dtp);
        
        //algebra internal frame
        JInternalFrame frameAlgebra = new JInternalFrame("Algebra view", true,true, true, true);
        JScrollPane scrollPane = new JScrollPane(getGuiManager().getAlgebraView());
        frameAlgebra.setContentPane(scrollPane);
        //frameAlgebra.setContentPane(getGuiManager().getAlgebraView());
        frameAlgebra.setSize(200, 480);
        frameAlgebra.setLocation(0, 0);
        frameAlgebra.setVisible(true);
        dtp.add(frameAlgebra);
        
        
        //2D internal frame
        
        JInternalFrame frame2D = new JInternalFrame("2D view", true,true, true, true);
        frame2D.setContentPane(euclidianView);
        frame2D.setSize(200, 480);
        frame2D.setLocation(840, 0);
        frame2D.setVisible(true);
        dtp.add(frame2D);
        
        
        //3D internal frame
        JInternalFrame frame3D = new JInternalFrame("3D view - very early version", true,true, true, true);
        frame3D.setContentPane(euclidianView3D);
        frame3D.setSize(640, 480);
        //frame3D.setLocation(400, 0);        
        frame3D.setLocation(200, 0);        
        frame3D.setVisible(true);        
        dtp.add(frame3D);
        frame3D.moveToFront();


    	centerPanel.add(euclidianView3D);
 
    } 
    
    */
    
	
	/*
    public void updateCenterPanel(boolean updateUI) {
    	centerPanel.removeAll();
    	centerPanel.add(euclidianView3D);
    	
    	
    }
    */
    
	////////////////////////////////////////////////
	//
	// Neutralizing center panel stuff to prevent opengl crash
	// TODO remove this
	//
	////////////////////////////////////////////////
	
	private boolean init3D = true;
	
	public void updateContentPane(){}
	
	public void updateContentPaneAndSize() {}
	
	
    public void updateCenterPanel(boolean updateUI) {
    
    	if (init3D)
    		createCenterPanel();
    	
    	init3D = false;
    }
    

    
    
    
    public void createCenterPanel() {
            
   	
   	
		centerPanel.removeAll();

		JPanel euclidianPanel = new JPanel(new BorderLayout());
		euclidianPanel.setBackground(Color.white);
		euclidianPanel.add(euclidianView3D, BorderLayout.CENTER);
	
		if (showConsProtNavigation) {
			JComponent consProtNav = getGuiManager()
					.getConstructionProtocolNavigation();
			euclidianPanel.add(consProtNav, BorderLayout.SOUTH);
			consProtNav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
					Color.gray));			
		}

		JComponent cp2 = null;

		JSplitPane sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				new JScrollPane(getGuiManager().getAlgebraView()), euclidianPanel);
		sp2.setDividerLocation(250);


		cp2 = sp2;

		JComponent cp1 = null;

		cp1 = cp2;
		centerPanel.add(cp1, BorderLayout.CENTER);

		

		f2d.getContentPane().add(euclidianView);
		f2d.setPreferredSize(new Dimension(400,400));
		f2d.setSize(new Dimension(400,400));
		f2d.setVisible(true);
		

		getGuiManager().attachSpreadsheetView();
		fspr.getContentPane().add(getGuiManager().getSpreadsheetView());
		fspr.setLocation(400, 0);
		fspr.setPreferredSize(new Dimension(600,400));
		fspr.setSize(new Dimension(600,400));
		fspr.setVisible(true);	


	}
    
    
    

 
    	
}
