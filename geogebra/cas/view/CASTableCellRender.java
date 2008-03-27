package geogebra.cas.view;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class CASTableCellRender extends CASTableCell implements
		TableCellRenderer {

	CASTableCellRender(CASView view, JTable consoleTable) {
		super(view, consoleTable);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (value instanceof CASTableCellValue) {
			CASTableCellValue tempV = (CASTableCellValue) value;
//			System.out.println("Rendering Row: " + row);
//			System.out.println("Input: " + tempV.getCommand());
//			System.out.println("Output: " + tempV.getOutput());
			// System.out.println("OutputArea: " +
			// tempV.getOutputAreaInclude());
			// tempV.isBBorderVisible();
			String tempIn = tempV.getCommand();
			String tempOut = tempV.getOutput();
			
			setInput(tempIn);
			setOutput(tempOut);

		}
		return this;
	}

}
