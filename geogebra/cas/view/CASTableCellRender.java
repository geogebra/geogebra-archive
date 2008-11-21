package geogebra.cas.view;

import geogebra.main.Application;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

public class CASTableCellRender extends CASTableCell implements
		TableCellRenderer {

	CASTableCellRender(CASView view, CASTable consoleTable, Application app) {
		super(view, consoleTable, app);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (value instanceof CASTableCellValue) {
			setFont(app.getPlainFont());
			CASTableCellValue tempV = (CASTableCellValue) value;
			
			
//			Application.debug("Rendering Row: " + row);
//			Application.debug("Input: " + tempV.getCommand());
//			Application.debug("Output: " + tempV.getOutput());
			// Application.debug("OutputArea: " +
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
