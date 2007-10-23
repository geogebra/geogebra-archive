package geogebra.cas.view;

import javax.swing.table.AbstractTableModel;

public class CASTableModel extends AbstractTableModel {
    private CASSession session;
    
    public CASTableModel(CASSession session)
    {
    	this.session = session;
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
    		return session.get(((row - 2)/2), false);    		 
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
    	if ((row%2) == 0)
    	{
    		// its a command row
    		return true;
    	} else {
    		return false;
    	}
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        if ((row%2) == 0)
        {
        	// command
        	session.alter(row, (String) value);
        	session.send();
        }
        //fireTableCellUpdated()
    	/*data[row][col] = value;
        fireTableCellUpdated(row, col);
        */
    	// TODO: should do something WRT this?
    }
}
