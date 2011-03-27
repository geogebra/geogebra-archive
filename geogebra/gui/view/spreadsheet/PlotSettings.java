package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianView;

public class PlotSettings {
	
	public double xMin = -10;
	public double xMax = 10;
	public double xAxesInterval = 1;
	public boolean xAxesIntervalAuto = true;
	public double yMin = -10;
	public double yMax = 10;
	public double yAxesInterval = 1;
	public boolean yAxesIntervalAuto = true;
	
	public int pointCaptureStyle = EuclidianView.POINT_CAPTURING_OFF;
	
	public boolean showYAxis = false;
	public boolean showArrows = false;
	public boolean forceXAxisBuffer = false;
	public boolean forceYAxisBuffer = false;
	public boolean[] isEdgeAxis = {false,false};
	public boolean[] isPositiveOnly = {false,false};
	
	public boolean showGrid = false;
	
	public PlotSettings(){
		
	}
	
	
	public PlotSettings(double xMinEV, double xMaxEV, double yMinEV,
			double yMaxEV, boolean showYAxis, boolean showArrows,
			boolean forceXAxisBuffer, boolean[] isEdgeAxis) {
		this.xMin = xMinEV;
		this.xMax = xMaxEV;
		this.yMin = yMinEV;
		this.yMax = yMaxEV;
		this.showYAxis = showYAxis;
		this.showArrows = showArrows;
		this.forceXAxisBuffer = forceXAxisBuffer;
		this.isEdgeAxis = isEdgeAxis;
	}
}