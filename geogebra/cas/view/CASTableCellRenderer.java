package geogebra.cas.view;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class CASTableCellRenderer extends CASTableCell implements
		TableCellRenderer {
	
	CASTableCellRenderer(CASView view) {
		super(view);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (value instanceof CASTableCellValue) {
			inputPanel.setFont(view.getFont());
			
			CASTableCellValue tempV = (CASTableCellValue) value;				
			setValue(tempV);
							
			// update row height
			updateTableRowHeight(table, row);		
		}
		return this;
	}

}
