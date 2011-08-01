package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * 
 * Creates a JPanel with an extended instance of EuclidianView and methods for 
 * creating geos in the panel.
 * 
 * @author gsturr 2010-6-30
 *
 */
public class PlotPanelEuclidianView extends EuclidianView implements ComponentListener {


	private EuclidianController ec;

	private static boolean[] showAxes = { true, true };
	private static boolean showGrid = false;

	private double xMinData, xMaxData, yMinData, yMaxData;

	private PlotSettings plotSettings;

	private MyMouseListener myMouseListener;

	private boolean enableContextMenu;
	public PlotSettings getPlotSettings() {
		return plotSettings;
	}
	public void setPlotSettings(PlotSettings plotSettings) {
		this.plotSettings = plotSettings;
		this.setEVParams();
	}




	/*************************************************
	 * Construct the panel
	 */
	public PlotPanelEuclidianView(Kernel kernel) {
		super(new PlotPanelEuclidianController(kernel), showAxes, showGrid);

		this.ec = this.getEuclidianController();

		setMouseEnabled(false);
		setMouseMotionEnabled(false);
		setMouseWheelEnabled(false);
		setAllowShowMouseCoords(false);
		setAxesCornerCoordsVisible(false);
		setContextMenuEnabled(true);


		setAntialiasing(true);
		updateFonts();
		setPreferredSize(new Dimension(300,200));
		setSize(new Dimension(300,200));
		updateSize();

		plotSettings = new PlotSettings();

		addComponentListener(this);

	}

	public void setMouseEnabled(boolean enableMouse){
		
		removeMouseListener(ec);
		if(enableMouse)
			addMouseListener(ec);
		setContextMenuEnabled(enableContextMenu);
	}

	public void setMouseMotionEnabled(boolean enableMouseMotion){
		removeMouseMotionListener(ec);	
		if(enableMouseMotion)
			addMouseMotionListener(ec);
	}

	public void setMouseWheelEnabled(boolean enableMouseWheel){
		removeMouseWheelListener(ec);	
		if(enableMouseWheel)
			addMouseWheelListener(ec);
	}

	public void setContextMenuEnabled(boolean enableContextMenu){
		this.enableContextMenu = enableContextMenu;
		if(myMouseListener == null)
			myMouseListener = new MyMouseListener();
		removeMouseListener(myMouseListener);
		if(enableContextMenu)
			addMouseListener(myMouseListener);;
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

		showGrid(plotSettings.showGrid);
		setShowAxis(EuclidianView.AXIS_Y, plotSettings.showYAxis, false);

		setAutomaticGridDistance(plotSettings.gridIntervalAuto);
		if(!plotSettings.gridIntervalAuto){
			this.setGridDistances(plotSettings.gridInterval);
		}


		if(plotSettings.showArrows){
			setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_ARROW);
		}else{
			setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_FULL);
		}

		setDrawBorderAxes(plotSettings.isEdgeAxis);
		if(!plotSettings.isEdgeAxis[0])
			setAxisCross(0,0);
		if(!plotSettings.isEdgeAxis[1])
			setAxisCross(1,0);


		setPositiveAxes(plotSettings.isPositiveOnly);


		if(plotSettings.forceXAxisBuffer){
			// ensure that the axis labels are shown
			// by forcing a fixed pixel height below the x-axis	
			double pixelOffset = 30 * app.getSmallFont().getSize()/12.0;
			double pixelHeight = this.getHeight(); 
			plotSettings.yMin = - pixelOffset * plotSettings.yMax / (pixelHeight + pixelOffset);
		}


		setAxesCornerCoordsVisible(false);


		this.setAutomaticAxesNumberingDistance(plotSettings.xAxesIntervalAuto, 0);
		this.setAutomaticAxesNumberingDistance(plotSettings.yAxesIntervalAuto, 1);
		if(!plotSettings.xAxesIntervalAuto){
			setAxesNumberingDistance(plotSettings.xAxesInterval, 0);
		}
		if(!plotSettings.yAxesIntervalAuto){
			setAxesNumberingDistance(plotSettings.yAxesInterval, 1);
		}

		setPointCapturing(plotSettings.pointCaptureStyle);

		// do this last ?
		setRealWorldCoordSystem(plotSettings.xMin, plotSettings.xMax, plotSettings.yMin, plotSettings.yMax);

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




	//==================================================
	//       Mouse Handlers
	//=================================================


	protected void processMouseEvent(MouseEvent e){
		if(e.getClickCount() > 1){
			e.consume();
			return;}

		else if(Application.isRightClick(e)){
			if(e.getID()==MouseEvent.MOUSE_RELEASED){
				ContextMenu contextMenu = new ContextMenu();
				contextMenu.show(e.getComponent(), e.getX(),e.getY());
				e.consume();
			}
			return;	}

		else
			super.processMouseEvent(e);
	}


	class MyMouseListener implements MouseListener{

		public void mouseClicked(MouseEvent e) {
			Object ob = e.getSource();	
			System.out.println("sdsadasdasdasdsadsadsd");
			// right click shows context menu
			if (Application.isRightClick(e)) {
				System.out.println("popup time!");
				e.consume();
				//app.getGuiManager().showPopupMenu(temp, table, origin);
			}
		}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}

		public void mousePressed(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) {}

	}

	
	//=============================================
	//      Context Menu
	//=============================================

	private class ContextMenu extends JPopupMenu{

		JMenuItem menuItem;

		public  ContextMenu(){
			this.setOpaque(true);
			setBackground(bgColor);	
			setFont(app.getPlainFont());

			menuItem = new JMenuItem(app.getMenu("CopyToGraphics") + "...", app.getImageIcon("edit-copy.png"));
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					
					
				}
			});
			add(menuItem);
			menuItem.setBackground(bgColor);

			menuItem = new JMenuItem(app.getPlain("ExportAsPicture") + "...");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

				}
			});
			add(menuItem);
			menuItem.setBackground(bgColor);
			
		
		}
	}


	
}
