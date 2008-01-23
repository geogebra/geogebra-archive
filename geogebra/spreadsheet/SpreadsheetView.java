
package geogebra.spreadsheet;

import java.awt.Component;
import java.awt.Point;
import javax.swing.AbstractListModel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.UIManager;

import geogebra.Application;
import geogebra.View;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

public class SpreadsheetView extends JScrollPane implements View
{
	public static final int TABLE_CELL_WIDTH = 100;
	public static final int TABLE_CELL_HEIGHT = 20;
	
	private static final long serialVersionUID = 1L;

	protected JTable table;
	protected MyTableModel tableModel;
	
	public SpreadsheetView(Application app, int columns, int rows) {
		Kernel kernel = app.getKernel();
		kernel.notifyAddAll(this);
		kernel.attach(this);
		tableModel = new MyTableModel(rows, columns);
		table = new JTable(tableModel);
		setupTableLook();
		table.setDefaultRenderer(Object.class, new MyCellRenderer());
		table.setDefaultEditor(Object.class, new MyCellEditor(kernel));
	}
	
	public void add(GeoElement geo) {
		Point location = geo.getSpreadsheetCoords();
		if (location != null) {
			tableModel.setValueAt(geo, location.y, location.x);
		}
	}
	
	public void remove(GeoElement geo) {
		Point location = geo.getSpreadsheetCoords();
		if (location != null) {
			tableModel.setValueAt(null, location.y, location.x);
		}
	}
	
	public void rename(GeoElement geo) {
		Point location = geo.getOldSpreadsheetCoords();
		if (location != null) {
			tableModel.setValueAt(null, location.y, location.x);
		}
		add(geo);
	}
	
	public void update(GeoElement geo) {
		Point location = geo.getSpreadsheetCoords();
		if (location != null) {
		}
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		update(geo);
	}
	
	public void repaintView() {
	}
	
	public void reset() {
	}
	
	public void clearView() {
	}
	
	protected void setupTableLook() {
		// new a table
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		table.setCellSelectionEnabled(true);
		// cell size
		table.setRowHeight(TABLE_CELL_HEIGHT);
		for (int i = 0; i < table.getColumnCount(); ++ i) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(TABLE_CELL_WIDTH);
		}
		// use a list as the row labels in the table
		MyListModel listModel = new MyListModel(tableModel.getRowCount());
		JList rowHeader = new JList(listModel);
		rowHeader.setFixedCellWidth(TABLE_CELL_WIDTH);
		rowHeader.setFixedCellHeight(table.getRowHeight()); // + table.getRowMargin();
		rowHeader.setCellRenderer(new RowHeaderRenderer(table));
		// put the table and the row list into a scroll plane
		setRowHeaderView(rowHeader);
		setViewportView(table);
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

	// a trivial class 
	public static class MyListModel extends AbstractListModel {
		
		private static final long serialVersionUID = 1L;

		protected String[] headers;
		
		public MyListModel(int rows) {
			headers = new String[rows];
			for (int i = 0; i < headers.length; ++ i) {
				headers[i] = "" + (i + 1);
			}
		}
		
		public int getSize() {
			return headers.length;
		}
		
		public Object getElementAt(int index) {
			return headers[index];
		}
		
    }

	// a trivial class 
    public static class RowHeaderRenderer extends JLabel implements ListCellRenderer {
	
    	private static final long serialVersionUID = 1L;
	
		public RowHeaderRenderer(JTable table) {
			JTableHeader header = table.getTableHeader() ;
			setOpaque(true);
			setBorder(UIManager.getBorder("TableHeader.cellBorder" ));
			setHorizontalAlignment(CENTER) ;
			setForeground(header.getForeground()) ;
			setBackground(header.getBackground());
			setFont(header.getFont());
		}
	
		public Component getListCellRendererComponent(JList list, Object value,	int index, boolean  isSelected, boolean cellHasFocus) {
			setText ((value == null) ? ""  : value.toString());
			return this;
		}
	
	}

}
