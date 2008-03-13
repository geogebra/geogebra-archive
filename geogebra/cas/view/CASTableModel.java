package geogebra.cas.view;

import geogebra.Application;
import geogebra.kernel.GeoElement;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class CASTableModel extends DefaultTableModel {
   
	private CASSession session;
   
	private JTable table;
    private boolean modified;
    private Application app = null;
    GeoElement copyGeo= null;
	
    public CASTableModel(CASSession session, JTable table)
    {
    	super();
    	
        this.table=table;
        modified=false;    	
    	this.session = session;
    }

    public CASTableModel(JTable table, int numRows, CASSession session, Application app)
    {
        super(numRows, CASPara.numOfCol);
        this.app = app;
        
        for(int i=0; i<numRows; i++){
        	CASTableCellValue value = new CASTableCellValue();
        	super.setValueAt(value, i, CASPara.contCol);
        	fireTableCellUpdated(i, CASPara.contCol);
        }
        
        // initialize state to unmodified and file to untitled
        modified = false;
        this.table = table;
        this.session = session;
    }   
    
    public CASTableModel(JTable table, Object[] data, CASSession session, Application app)
    {
       this(table, data.length, session, app);

       /* load the data */
       for (int i = 0; i < data.length; i++)
       {
             super.setValueAt(data[i], i, CASPara.contCol);
       }

       // initialize state to unmodified and file to untitled
       modified = false;
       this.session = session;
    }
    
    public String getRowLabel (int row)
    {
        String lbl = "";
        lbl = String.valueOf(row + 1);
        return lbl;
    }
    
//    /*Used to set value of a geoelement in a cell in the CASView*/
//    public void setValueAt(Object obj, int row)
//    {
//    	if(obj instanceof CASTableCellValue){
//    		setValueAt(obj, row, CASPara.contCol);
//    		fireTableCellUpdated(row, CASPara.contCol);
//    		//System.out.println("Value Updated: ");
//    		//System.out.println(((CASTableCellValue)obj).getCommand());
//    		//System.out.println(((CASTableCellValue)obj).getOutput());
//    		//System.out.println(((CASTableCellValue)obj).getOutputAreaInclude());
//    	}
//    }

//    public Object getValueAt(int row) {
//        Object obj = super.getValueAt(row, CASPara.contCol);
//        if( !(obj instanceof CASTableCellValue ))
//        {
//        	System.out.println("Getting a non-CASTableCellValue"); 
//        	System.out.println(obj.getClass().getName());
//        }
//        return obj;
//    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
    	if(col == CASPara.indexCol)
    		return false;
    	else
    		return true;
    }

}
