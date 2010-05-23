/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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
import geogebra.kernel.GeoElement;
import geogebra.main.AppletImplementation;
import geogebra.main.Application;
import geogebra.main.GuiManager;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.Kernel3D;
import geogebra3D.util.ImageManager3D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
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
    	
    	super(args, frame, applet, null, undoActive);


    	//euclidianView3D.initAxisAndPlane();
    	
        
	    //TODO remove 3D test : just comment following line        
        new Test3D(kernel3D,euclidianView,euclidianView3D,this);
        
        initToolBar3D();
        
    }
    
    private void initToolBar3D(){
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
        						+EuclidianView3D.MODE_SPHERE_TWO_POINTS
        						+" "
        						+EuclidianView3D.MODE_SPHERE_POINT_RADIUS
        						+" || "
        						+EuclidianView3D.MODE_TRANSLATEVIEW
        						+" "
        						+EuclidianView3D.MODE_ZOOM_IN
        						+" "
        						+EuclidianView3D.MODE_ZOOM_OUT
        						+" | "
        						+EuclidianView3D.MODE_VIEW_IN_FRONT_OF
        						;
        
        DefaultGuiManager dgm = (DefaultGuiManager) getGuiManager();
        dgm.setToolBarDefinition( myToolBar3D );
        //dgm.getLayout().getPerspective(0).setToolbarDefinition(myToolBar3D);
        
        super.updateToolBar();
 		
    }      
    
    
    //TODO remove this - keep until perspective manager process toolbar
    public void updateToolBar() {
    	
    }

    
  
    
    
    
    
    
    
    
    
    
    
    
    
    
	public void initKernel(){
		kernel3D = new Kernel3D(this);
		kernel = kernel3D;
	}
	
	
	protected void initImageManager(Component component){
		imageManager = new ImageManager3D(component);
	}
	
	
	/**
	 * init the EuclidianView (and EuclidianView3D for 3D)
	 */
	public void initEuclidianViews(){
		
		//init the 2D euclidian view
		super.initEuclidianViews();
		
		//init the 3D euclidian view
		euclidianController3D = new EuclidianController3D(kernel3D);
        euclidianView3D = new EuclidianView3D(euclidianController3D);  
        
        
	}
    

    
    
	public void setMode(int mode) {
		super.setMode(mode);
		
		//if (euclidianView3D != null)
			euclidianView3D.setMode(mode);
		
	}
	
    
	
	
	
	
	public String getCompleteUserInterfaceXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();

		// save super settings
		sb.append(super.getCompleteUserInterfaceXML(asPreference));

		// save euclidianView3D settings
		//if (euclidianView3D != null) //TODO remove this
			sb.append(euclidianView3D.getXML());


		return sb.toString();
	}
	
	
	
	/** return the 3D euclidian view
	 * @return the 3D euclidian view
	 */
	public EuclidianView3D getEuclidianView3D(){
		return euclidianView3D;
	}
	
	
	public BufferedImage getExportImage(double maxX, double maxY) throws OutOfMemoryError {
		//TODO use maxX, maxY values
		return getEuclidianView3D().getRenderer().getExportImage();
	}
	
	
	
	public boolean saveGeoGebraFile(File file) {
		
		
		//TODO generate it before
		getEuclidianView3D().getRenderer().needExportImage();
		


		
		return super.saveGeoGebraFile(file);
	}
	
	
	
	

	
	
	
	/** return 2D (and 3D) views settings
	 * @return 2D (and 3D) views settings
	 */
	public String getEuclidianViewsXML() {
		return getEuclidianView().getXML()+""+getEuclidianView3D().getXML();
	}	
	
	
	
	
	
	
	
	
	/////////////////////////////////
	// GUI
	/////////////////////////////////
	
	public void toggleAxis(){
		
		// toggle axis
		getEuclidianView3D().toggleAxis();
		
	}
	
	public void togglePlane(){
		
		// toggle xOy plane
		getEuclidianView3D().togglePlane();
		
	}
	
	public void toggleGrid(){
		
		// toggle xOy grid
		getEuclidianView3D().toggleGrid();
		
	}
	
	
	public void setShowAxesSelected(JCheckBoxMenuItem cb){
		cb.setSelected(getEuclidianView3D().axesAreAllVisible());
	}
	
	
	
	/** set the show plane combo box selected if the plane is visible
	 * @param cb
	 */
	public void setShowPlaneSelected(JCheckBoxMenuItem cb){
		GeoPlane3D p = getEuclidianView3D().getxOyPlane();
		cb.setSelected(p.isPlateVisible());
	}
	
	/** set the show grid combo box selected if the plane is visible
	 * @param cb
	 */
	public void setShowGridSelected(JCheckBoxMenuItem cb){
		GeoPlane3D p = getEuclidianView3D().getxOyPlane();
		cb.setSelected(p.isGridVisible());
	}
	
    
	public synchronized GuiManager createGuiManager() {
		
		return new geogebra3D.gui.DefaultGuiManager3D(this);
		
	}
    
	
	
	
	
	
	///////////////////////////////////////
	// COMMANDS
	///////////////////////////////////////
	
	
	public String getCommandSyntax(String key) {
		
		String command3D = getCommand(key+"Syntax3D");
		if (!command3D.equals(key)) return command3D;
		
		return super.getCommandSyntax(key);
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
