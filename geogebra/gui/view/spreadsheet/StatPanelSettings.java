package geogebra.gui.view.spreadsheet;

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
		
		public int stemAdjust = 0;

		public StatPanelSettings clone(StatPanelSettings settings){
			return this;
		}
	}
	
	
