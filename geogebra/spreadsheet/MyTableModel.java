package geogebra.spreadsheet;

import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import javax.swing.table.TableColumn;

public class MyTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	
	protected HashMap data;
	public int rowCount = 100;
	public int columnCount = 26;
	MyTable table;
	
	public void setRowCount(int rowCount0) {
		//super.setRowCount(rowCount0);
		rowCount = rowCount0;
		//this.fireTableStructureChanged();
	}
	
	public void setColumnCount(int columnCount0) {
		//super.setColumnCount(columnCount0);
		for (int i = columnCount; i < columnCount0; ++ i) {
			TableColumn col = new TableColumn(i);
			col.setHeaderRenderer(table.columnHeader);
			col.setPreferredWidth(MyTable.TABLE_CELL_WIDTH);
			table.addColumn(col);
		}
		columnCount = columnCount0;
		//this.fireTableStructureChanged();
	}
	
	public int getRowCount() {
		return rowCount;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public MyTableModel(int rows, int columns) {
		super(rows, columns);
		rowCount = rows;
		columnCount = columns;
		data = new HashMap();
	}
	
	public void setTable(MyTable table) {
		this.table = table;
	}
	
	public Class getColumnClass(int columnIndex) {
		return Object.class;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.get(rowIndex + "-" + columnIndex);
	}

    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	return true;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    	if (aValue == null) {
    		data.remove(rowIndex + "-" + columnIndex);
    	}
    	else {
    		data.put(rowIndex + "-" + columnIndex, aValue);
    	}
    	//fireTableCellUpdated(rowIndex, columnIndex);
    	fireTableDataChanged();
    }
        
}
