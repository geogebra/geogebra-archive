
package geogebra.spreadsheet;

import java.awt.Component;
import java.awt.Point;
import javax.swing.AbstractListModel;
import javax.swing.ListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.UIManager;

import geogebra.Application;
import geogebra.View;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

public class SpreadsheetView extends JScrollPane implements View
{
	
	private static final long serialVersionUID = 1L;

	protected MyTable table;
	protected MyTableModel tableModel;
	
	public SpreadsheetView(Application app, int columns, int rows) {
		Kernel kernel = app.getKernel();
		kernel.notifyAddAll(this);
		kernel.attach(this);
		// table
		tableModel = new MyTableModel(rows, columns);
		table = new MyTable(tableModel, kernel);
		// row header list
		MyListModel listModel = new MyListModel(tableModel.getRowCount());
		JList rowHeader = new JList(listModel);
		rowHeader.setFixedCellWidth(MyTable.TABLE_CELL_WIDTH);
		rowHeader.setFixedCellHeight(table.getRowHeight()); // + table.getRowMargin();
		rowHeader.setCellRenderer(new RowHeaderRenderer(table));
		// put the table and the row header list into a scroll plane
		setRowHeaderView(rowHeader);
		setViewportView(table);
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
