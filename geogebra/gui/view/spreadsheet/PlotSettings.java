package geogebra.gui.view.spreadsheet;

public class PlotSettings {
	public double xMinEV = -10;
	public double xMaxEV = 10;
	public double yMinEV = -10;
	public double yMaxEV = 10;
	public boolean showYAxis = false;
	public boolean showArrows = false;
	public boolean forceXAxisBuffer = false;
	public boolean[] isEdgeAxis = {true,true};

	public PlotSettings(){
		
	}
	
	
	public PlotSettings(double xMinEV, double xMaxEV, double yMinEV,
			double yMaxEV, boolean showYAxis, boolean showArrows,
			boolean forceXAxisBuffer, boolean[] isEdgeAxis) {
		this.xMinEV = xMinEV;
		this.xMaxEV = xMaxEV;
		this.yMinEV = yMinEV;
		this.yMaxEV = yMaxEV;
		this.showYAxis = showYAxis;
		this.showArrows = showArrows;
		this.forceXAxisBuffer = forceXAxisBuffer;
		this.isEdgeAxis = isEdgeAxis;
	}
}