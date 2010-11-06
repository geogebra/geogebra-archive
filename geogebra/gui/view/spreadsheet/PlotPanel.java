package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * 
 * Creates a JPanel with an extended instance of EuclidianView and methods for 
 * creating geos in the panel.
 * 
 * @author gsturr 2010-6-30
 *
 */
public class PlotPanel extends EuclidianView implements ComponentListener {
	
	
	private static boolean[] showAxes = { true, true };
	private static boolean showGrid = false;
	
	private double xMinData, xMaxData, yMinData, yMaxData;
	
	private PlotSettings plotSettings;
	public PlotSettings getPlotSettings() {
		return plotSettings;
	}
	public void setPlotSettings(PlotSettings plotSettings) {
		this.plotSettings = plotSettings;
	}
	

	
	
	/*************************************************
	 * Construct the panel
	 */
	public PlotPanel(EuclidianController ec) {
		super(ec, showAxes, showGrid);
					
		removeMouseListener(ec);
		removeMouseMotionListener(ec);
		removeMouseWheelListener(ec);
		setAxesCornerCoordsVisible(false);
		
		setAntialiasing(true);
		updateFonts();
		setPreferredSize(new Dimension(300,200));
		setSize(new Dimension(300,200));
		updateSize();
			
		plotSettings = new PlotSettings();
		
		addComponentListener(this);
		
	}

	
	
	/**
	 * Override UpdateSize() so that our plots stay centered and scaled in a
	 * resized window.
	 */
	@Override
	public void updateSize(){
		
		// record the old coord system
		double xminTemp = getXmin();
		double xmaxTemp = getXmax();
		double yminTemp = getYmin();
		double ymaxTemp = getYmax();	
		
		// standard update: change the coord system to match new window dimensions
		// with the upper left corner fixed and the other bounds adjusted.  
		super.updateSize();		
		
		// now reset the coord system so that our view dimensions are restored 
		// using the new scaling factors. 
		setRealWorldCoordSystem(xminTemp, xmaxTemp, yminTemp, ymaxTemp);
	}	

	
	@Override
	public void setMode(int mode) {
		// .... do nothing
	}
	

	public void setEVParams(){
		
		setShowAxis(EuclidianView.AXIS_Y, plotSettings.showYAxis, false);
		
		if(plotSettings.showArrows){
			setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_ARROW);
		}else{
			setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_FULL);
		}
		
		setDrawBorderAxes(plotSettings.isEdgeAxis);
		
		// ensure that the axis labels are shown
		// by forcing a fixed pixel height below the x-axis
		
		if(plotSettings.forceXAxisBuffer){
			double pixelOffset = 30 * app.getSmallFont().getSize()/12.0;
			double pixelHeight = this.getHeight(); 
			plotSettings.yMinEV = - pixelOffset * plotSettings.yMaxEV / (pixelHeight + pixelOffset);
		}

		setRealWorldCoordSystem(plotSettings.xMinEV, plotSettings.xMaxEV, plotSettings.yMinEV, plotSettings.yMaxEV);
	
		//Application.debug("setEVParms");
		repaint();
	}
	
	
	
	
	//==================================================
	//       Component Listener  (for resizing our EV)
	//=================================================
	
	public void componentHidden(ComponentEvent arg0) {	
	}
	public void componentMoved(ComponentEvent arg0) {
	}
	public void componentResized(ComponentEvent arg0) {
		// make sure that we force a pixel buffer under the x-axis 
		setEVParams();
	}
	public void componentShown(ComponentEvent arg0) {
	}



}
