package geogebra.spreadsheet;

import java.awt.Color;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

public class MyTable extends JTable
{

	public static final int TABLE_CELL_WIDTH = 100;
	public static final int TABLE_CELL_HEIGHT = 20;
	
	private static final long serialVersionUID = 1L;

	public MyTable(MyTableModel tableModel, Kernel kernel) {
		super(tableModel);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setCellSelectionEnabled(true);
		setSelectionBackground(new Color(128, 255, 255));
		// cell size
		setRowHeight(TABLE_CELL_HEIGHT);
		for (int i = 0; i < getColumnCount(); ++ i) {
			TableColumn column = getColumnModel().getColumn(i);
			column.setPreferredWidth(TABLE_CELL_WIDTH);
		}
		setDefaultRenderer(Object.class, new MyCellRenderer());
		setDefaultEditor(Object.class, new MyCellEditor(kernel));
		setSelectionBackground(new Color(200, 220, 240));
	}

	public class MyCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		public void setValue(Object value) {
			if (value == null) {
				setText("");
			}
			else {
				setText(((GeoElement)value).toValueString());
			}
		}
	}

}
