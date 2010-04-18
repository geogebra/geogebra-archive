package geogebra.cas.view;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class CASTableModel extends DefaultTableModel {
   
    private Application app = null;
    GeoElement copyGeo = null;	

    public CASTableModel(CASTable table, Application app) {
        super(1, 1);
        this.app = app;
        
        // create first row
    	CASTableCellValue value = new CASTableCellValue(table.getCASView());
    	setValueAt(value, 0, CASTable.COL_CAS_CELLS);
    	fireTableCellUpdated(0, CASTable.COL_CAS_CELLS);
    }
    
    public String getRowLabel (int row)
    {
        String lbl = "";
        lbl = String.valueOf(row + 1);
        return lbl;
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
    	return true;
    }


}
