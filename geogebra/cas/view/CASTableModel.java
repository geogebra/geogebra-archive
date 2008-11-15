package geogebra.cas.view;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class CASTableModel extends DefaultTableModel {
   
    private Application app = null;
    GeoElement copyGeo= null;
	
    public CASTableModel(JTable table)
    {
    	super();	   	
    }

    public CASTableModel(JTable table, int numRows, Application app)
    {
        super(numRows, CASPara.numOfCol);
        this.app = app;
        
        for(int i=0; i<numRows; i++){
        	CASTableCellValue value = new CASTableCellValue();
        	super.setValueAt(value, i, CASPara.contCol);
        	fireTableCellUpdated(i, CASPara.contCol);
        }
    }   
    
    public CASTableModel(JTable table, Object[] data, Application app)
    {
       this(table, data.length, app);

       /* load the data */
       for (int i = 0; i < data.length; i++)
       {
             super.setValueAt(data[i], i, CASPara.contCol);
       }
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
    	if(col == CASPara.indexCol)
    		return true; //It is supposed to be false
    	else
    		return true;
    }

}
