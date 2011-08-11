package geogebra.cas.view;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNodeConstants;

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
			dummyField.setFont(view.getFont());
			
			CASTableCellValue tempV = (CASTableCellValue) value;	
			
			Kernel kernel = tempV.getKernel();
			int oldCASPrintForm = kernel.getCASPrintForm();
			kernel.setCASPrintForm(ExpressionNodeConstants.STRING_TYPE_LATEX);
			
			tempV.setRow(row);
			setValue(tempV);

			kernel.setCASPrintForm(oldCASPrintForm);
							
			// update row height
			updateTableRowHeight(table, row);	
			
			// set inputPanel width to match table column width
			// -1 = set to table column width (even if larger than viewport)
			setInputPanelWidth(-1);
		}
		return this;
	}

}
