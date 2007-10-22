package geogebra.cas.view;

import javax.swing.table.AbstractTableModel;

public class CASTableModel extends AbstractTableModel {
    private CASSession session;
    
    public CASTableModel()
    {
    	session = new CASSession();
    }

    public int getColumnCount() {
        return 1;
    }

    public int getRowCount() {
        return (2*session.count());
    }

    public String getColumnName(int col) {
        return "Console";
    }

    public Object getValueAt(int row, int col) {
    	if ((row%2) == 0)
    	{
    		// we are asking for the value of a command (since 1st row is "welcome")
    		return session.get(((row - 1)/2), false);
    	} else {
    		// we are asking for the value of a response
    		if (row == 0) 
    		{
    			return "Welcome to CAS!";
    		} else {
    			return session.get(((row - 2)/2), false);
    		} 
    	}
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return false;
        // TODO: should do something WRT this?
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        /*data[row][col] = value;
        fireTableCellUpdated(row, col);
        */
    	// TODO: should do something WRT this?
    }
}
