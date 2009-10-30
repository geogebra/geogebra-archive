package geogebra.gui.view.spreadsheet;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class MyCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;
	private Application app;
	private Kernel kernel;

	public MyCellRenderer(Application app) {
		this.app = app;		
		kernel = app.getKernel();
		
		//G.Sturr 2009-10-3:  add horizontal padding
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{		
		setBackground(table.getBackground());

		if (value == null) {		
			setText("");
			return this;
		}
				
		// set cell content
		GeoElement geo = (GeoElement)value;
		String text = null;
		if (geo.isIndependent()) {
			text = geo.toValueString();
		} else {
			switch (kernel.getAlgebraStyle()) {
				case Kernel.ALGEBRA_STYLE_VALUE:
					text = geo.toValueString();
					break;
					
				case Kernel.ALGEBRA_STYLE_DEFINITION:
					text = GeoElement.convertIndicesToHTML(geo.getDefinitionDescription());
					break;
					
				case Kernel.ALGEBRA_STYLE_COMMAND:
					text = GeoElement.convertIndicesToHTML(geo.getCommandDescription());
					break;
			}	
		}

		// make sure that we use a font that can display the cell content
		setText(text);
		setFont(app.getFontCanDisplay(text, Font.BOLD));
		
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