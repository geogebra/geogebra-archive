package geogebra.gui.view.spreadsheet;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class MyCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;
	private Application app;

	public MyCellRenderer(Application app) {
		this.app = app;		
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{		
		setFont(app.boldFont);
		setBackground(table.getBackground());

		if (value == null) {		
			setText("");
			return this;
		}
				
		GeoElement geo = (GeoElement)value;
		
		// cell content
		setText(geo.toValueString());
		
		// foreground and background color
		setForeground(geo.getAlgebraColor());
		if (isSelected || geo.doHighlighting()) {
			setBackground(MyTable.SELECTED_BACKGROUND_COLOR);
		}		
		
		// horizontal alignment
		if (geo.isGeoText()) {
			setHorizontalAlignment(JLabel.LEFT);
		} else {
			setHorizontalAlignment(JLabel.RIGHT);
		}		
		
		return this;
	}

}