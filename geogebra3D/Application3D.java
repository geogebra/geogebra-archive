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
import javax.swing.*;

import geogebra.Application;
import geogebra.GeoGebra;
import geogebra.GeoGebraAppletBase;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidian3D.EuclidianView3D;


public abstract class Application3D extends Application{


    private EuclidianView3D euclidianView3D;
    private EuclidianController3D euclidianController3D;        
    private JSplitPane sp3D;
    private int initSplitDividerLocationHOR3D = 250; // init value
    private boolean show3DView = true; 


    public Application3D(String[] args, GeoGebra frame, boolean undoActive) {
        this(args, frame, null, undoActive);
    }

    public Application3D(String[] args, GeoGebraAppletBase applet, boolean undoActive) {
    	this(args, null, applet, undoActive);
    }
    
    
    private Application3D(String[] args, GeoGebra frame, GeoGebraAppletBase applet, boolean undoActive) { 
    	
    	super(args, frame, applet, undoActive);

		euclidianController3D = new EuclidianController3D(kernel);
        euclidianView3D = new EuclidianView3D(euclidianController3D);  

              
        
	    //TODO remove 3D test : just comment following lines
        
        Test3D test = new Test3D(kernel);
        test.testSegment2();  
        test.test1(1);
        test.testSegment(0,0,0,1,1,1);test.testSegment(0.3333,-0.25,-0.25,0.3333,1.25,1.25);test.testSegment(0.6667,-0.25,-0.25,0.6667,1.25,1.25);
        //test.testPlane(0, 0, 0, 1, 0, 0, 0, 1, 0);test.testPlane(0, 0, 0, 1, 0, 0, 0, 0, 1);
        test.testPlane();
        //test.testPointSegment();
        //test.testRepere();
        //test.testSegmentSegment();
        
        //euclidianView3D.setCoordSystem(300.0, 300.0, 150.0, 150.0);
        //euclidianView3D.setRotXY(0,0,true);
        euclidianView3D.setRotXY(-Math.PI/6,Math.PI/6,true);
        euclidianView3D.setZZero(-3.0);
        //euclidianView3D.repaint();
       	
		
    }      
    

    public void updateCenterPanel(boolean updateUI) {
    	centerPanel.removeAll();
    	
    	
    	JPanel eup = new JPanel(new BorderLayout());
    	//Mathieu Blossier - start
    	//adding a 3D view splitting the euclidian view
    	//TODO : call super()
        if (show3DView) {        	     
            sp3D =  new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, euclidianView, euclidianView3D);
            sp3D.setDividerLocation(initSplitDividerLocationHOR3D);                
            //sp3D.addPropertyChangeListener("dividerLocation3D",new DividerChangeListener());        
            
            eup.add(sp3D, BorderLayout.CENTER);
            
        } else { 
            eup.setBackground(Color.white);
            eup.add(euclidianView, BorderLayout.CENTER);
        }
       
        
        //Mathieu Blossier - end
        
        
        if (showConsProtNavigation) {
        	eup.add(constProtocolNavigation, BorderLayout.SOUTH);
        	constProtocolNavigation.setBorder(BorderFactory.
       		     createMatteBorder(1, 0, 0, 0, Color.gray));
        }                    
        
        if (showAlgebraView) {        	     
            if (horizontalSplit) {
                sp =  new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                        new JScrollPane(algebraView), eup);
                sp.setDividerLocation(initSplitDividerLocationHOR);                
            }               
            else {
                sp =  new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                             eup, new JScrollPane(algebraView));
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
    }    
    	
}
