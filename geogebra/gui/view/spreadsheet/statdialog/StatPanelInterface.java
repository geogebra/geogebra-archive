package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.kernel.GeoList;

import java.awt.Font;

public interface StatPanelInterface {

	public void updateFonts(Font font);
	
	public void setLabels();
	
	public void updatePanel(GeoList selectedData);
	
}
