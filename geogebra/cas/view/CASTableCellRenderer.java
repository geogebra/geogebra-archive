package geogebra.cas.view;

import geogebra.main.Application;

import java.awt.Component;
import java.awt.Dimension;

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
			setFont(app.getPlainFont());
			
			CASTableCellValue tempV = (CASTableCellValue) value;				
			setInput(tempV.getInput());
			setOutput(tempV.getOutput(), tempV.isOutputError());
			
			// update row height
			updateTableRowHeight(table, row);

		//	System.out.println("RENDER row: " + row + ", input: " + tempV.getInput() + ", output: " + tempV.getOutput());
		}
		return this;
	}

}
