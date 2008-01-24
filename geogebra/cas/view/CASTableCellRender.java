package geogebra.cas.view;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class CASTableCellRender extends CASTableCell implements 
	TableCellRenderer{

	CASTableCellRender(CASView view) {
		super(view);
	}

	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
//		if (value instanceof Integer) {
//			setSelectedIndex(((Integer) value).intValue());
//		}
		return this;
	}	
	
}
