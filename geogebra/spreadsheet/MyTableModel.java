package geogebra.spreadsheet;

import javax.swing.table.DefaultTableModel;
import java.util.HashMap;

public class MyTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	
	protected HashMap data;
	
	public MyTableModel(int rows, int columns) {
		super(rows, columns);
		data = new HashMap();
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
    	fireTableCellUpdated(rowIndex, columnIndex);
    }
    
}
