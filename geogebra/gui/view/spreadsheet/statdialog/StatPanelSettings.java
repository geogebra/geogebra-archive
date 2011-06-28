package geogebra.gui.view.spreadsheet.statdialog;


public class StatPanelSettings {

		public static final int TYPE_COUNT = 0;
		public static final int TYPE_RELATIVE = 1;
		public static final int TYPE_NORMALIZED = 2;
		public int type = TYPE_COUNT;

		public boolean isCumulative = false;
		public boolean useManualClasses = false;
		public boolean hasOverlayNormal = false;
		public boolean hasOverlayPolygon = false;
		
		public double classStart = 0;
		public double classWidth = 5;
		
		// graph options
		public boolean isAutomaticWindow = true;
		public boolean showGrid = false;
		public double xMin = 0;
		public double xMax = 10;
		public double xInterval = 1;
		public double yMin = -10;
		public double yMax = 0;
		public double yInterval = 1;
		
		
		public double xMinAuto = 0;
		public double xMaxAuto = 10;
		public double xIntervalAuto = 1;
		public double yMinAuto = -10;
		public double yMaxAuto = 0;
		public double yIntervalAuto = 1;
		
		public PlotPanelEuclidianView plotPanel;
		
		public int stemAdjust = 0;

		public StatPanelSettings clone(StatPanelSettings settings){
			return this;
		}
	}
	
	
