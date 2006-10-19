/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * ZoomMenu.java
 *
 * Created on 24. J�nner 2002, 14:11
 */

package geogebra;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Kernel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author  markus
 * @version 
 */
public class DrawingPadPopupMenu extends MyPopupMenu
implements ActionListener {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double px, py;
    //private JMenuItem miStandardView, miProperties; 
    
    private static double [] zoomFactors = 
		{4.0, 2.0, 1.5, 1.25, 1.0/1.25, 1.0/1.5, 0.5, 0.25};
    
    private static double [] axesRatios = 
        {1.0/1000.0, 1.0/500.0, 1.0/200.0, 1.0/100.0, 1.0/50.0, 1.0/20.0, 1.0/10.0, 1.0/5.0, 1.0/2.0,
    		1, 2, 5, 10, 20, 50, 100, 200, 500, 1000};
     
    private ImageIcon iconZoom;

    /** Creates new ZoomMenu */
    public DrawingPadPopupMenu(Application app, double px, double py) {  
        super(app);      
        
        iconZoom      = app.getImageIcon("zoom16.gif");
        
        // zoom point
        this.px = px;
        this.py = py;

        setTitle("<html>" + app.getPlain("DrawingPad") + "</html>");                                              
        
        // checkboxes for axes and grid
        EuclidianView ev = app.getEuclidianView();
        JCheckBoxMenuItem cbShowAxes = new JCheckBoxMenuItem(app.getShowAxesAction());
        cbShowAxes.setSelected(ev.getShowAxes());
        cbShowAxes.setBackground(getBackground());
        add(cbShowAxes);
        
        JCheckBoxMenuItem cbShowGrid = new JCheckBoxMenuItem(app.getShowGridAction());
        cbShowGrid.setSelected(ev.getShowGrid());
        cbShowGrid.setBackground(getBackground());
        add(cbShowGrid);
        
        addSeparator();
        
        // zoom for both axes
        JMenu zoomMenu = new JMenu(app.getMenu("Zoom"));
        zoomMenu.setIcon(iconZoom);
        zoomMenu.setBackground(getBackground());           
        addZoomItems(zoomMenu);
        add(zoomMenu);
        
        // zoom for y-axis
        JMenu yaxisMenu = new JMenu(app.getPlain("xAxis") + " : " 
        							+ app.getPlain("yAxis"));
        yaxisMenu.setBackground(getBackground());   
        addAxesRatioItems(yaxisMenu);
        add(yaxisMenu);                        
       
        
        JMenuItem miStandardView = new JMenuItem(app.getPlain("StandardView"));
        miStandardView.setActionCommand("standardView");
        miStandardView.addActionListener(this);
        miStandardView.setBackground(bgColor);
        add(miStandardView);
        
        addSeparator();          
        
        JMenuItem miProperties = new JMenuItem(app.getPlain("Properties"));
        miProperties.setActionCommand("properties");
        miProperties.addActionListener(this);
        miProperties.setBackground(bgColor);
        add(miProperties);                 
    }
        
    public void actionPerformed(ActionEvent e) {                                            
    	String cmd = e.getActionCommand();
    	
    	if (cmd.equals("standardView")) {
            app.setStandardView();        
        }
    	else if (cmd.equals("properties")) {
    		app.showDrawingPadPropertiesDialog();
    	}
    }
    
    private void addZoomItems(JMenu menu) {	  
      int perc;            
      
      ActionListener al = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            try {   
	                zoom(Double.parseDouble(e.getActionCommand()));
	            } catch (Exception ex) {
	            }       
	        }  
	    };     
      
      //ImageIcon icon;
      JMenuItem mi;
      boolean separatorAdded = false;
      StringBuffer sb = new StringBuffer();       
      for (int i=0; i < zoomFactors.length; i++) {
          perc = (int) (zoomFactors[i] * 100.0);
          
          // build text like "125%" or "75%"
          sb.setLength(0);
          if (perc > 100) {           
               
          } else {
              if (! separatorAdded) {
                  menu.addSeparator();
                  separatorAdded = true;
              }
              
          }                           
          sb.append(perc);
          sb.append("%");             
          
          mi = new JMenuItem(sb.toString());
          mi.setActionCommand("" + zoomFactors[i]);
          mi.addActionListener(al);
          mi.setBackground(getBackground());
          menu.add(mi);
      }            	
    }   
    
    private void addAxesRatioItems(JMenu menu) {	                           
    	ActionListener al = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            try {   
	                zoomYaxis(Double.parseDouble(e.getActionCommand()));
	            } catch (Exception ex) {
	            }       
	        }  
        };  
  	    
        // get current axes ratio
        double scaleRatio = app.getEuclidianView().getScaleRatio();
        Kernel kernel = app.getKernel();
        
        JMenuItem mi;		
        //int perc;   	         
        //ImageIcon icon;        
        boolean separatorAdded = false;
        StringBuffer sb = new StringBuffer();       
        for (int i=0; i < axesRatios.length; i++) {                        
            // build text like "1 : 2" 
            sb.setLength(0);
            if (axesRatios[i] > 1.0) {                                 
                sb.append((int) axesRatios[i]);
                sb.append(" : 1");
                if (! separatorAdded) {
                    menu.addSeparator();
                    separatorAdded = true;
                }
                
            } else { // factor 
            	if (axesRatios[i] == 1) 
                	menu.addSeparator(); 
                sb.append("1 : "); 
                sb.append((int) (1.0 / axesRatios[i]));                               
            }                                    
            
            mi = new JCheckBoxMenuItem(sb.toString());           
            mi.setSelected(kernel.isEqual(axesRatios[i], scaleRatio));
            mi.setActionCommand("" + axesRatios[i]);
            mi.addActionListener(al);           
            mi.setBackground(getBackground());
            menu.add(mi);
        }            	
      } 
    
    private void zoom(double zoomFactor) {
        app.zoom(px, py, zoomFactor);       
    }
    
    // ratio: yaxis / xaxis
    private void zoomYaxis(double axesRatio) {
    	app.zoomAxesRatio(axesRatio);    	
    }        
}
