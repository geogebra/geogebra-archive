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
        	setValueAt(value, i, CASPara.contCol);
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
             setValueAt(data[i], i);
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
    
    /*Used to set value of a geoelement in a cell in the CASView*/
    public void setValueAt(Object obj, int row)
    {
    	/**********Old things
    	if ((row%2) == 0)
        {
        	// command
        	session.alter(row, (String) value);
        	session.send();
        }
        //fireTableCellUpdated()
    	/*data[row][col] = value;
        fireTableCellUpdated(row, col);
        ************/
    	
//    	GeoElement geo = null;
//        // input is the panel of a pair
//        if(obj instanceof String)
//        {
//        	String inputString = ((String) obj).trim();           	
//            //geo = (GeoElement)getValueAt( row, 1);
//        	geo = null;
//                        
//            // delete old cell object if empty input string
//            if (inputString.length() == 0) {
//            	if (geo != null) geo.remove();
//            	return;
//            }
//            
//            // cell is empty at the moment:
//            if( geo == null )
//            {
//            	String str;
//                if( inputString.startsWith("=") )
//                {
//                    //TODO: do the equation here
//                    str = getRowLabel(row) + inputString;                   
//                }
//                else 
//                {
//                     str = getRowLabel(row) + "=" + inputString;                    
//                }
//                System.out.println(str);  
//                //super.setValueAt(str, row, 1);
//                //app.getKernel().getAlgebraProcessor().processAlgebraCommand( str, true );
//                fireTableDataChanged();
//            }
//            
//            // we have a GeoElement in this cell already:
//            else
//            {
//              
//               String newValue = inputString;                  
//               if( inputString.startsWith("=") )
//               {
//                   // case of formula
//                   newValue = newValue.substring(1);
//               }
//              
//               if ( geo.isIndependent()) {
//          	     // change geo, but don't redefine       
//          	     	app.getKernel().getAlgebraProcessor().changeGeoElement(geo, newValue, false);  
//              	} else {
//              	    // redefine geo, note that redefining changes the entire construction and produces new GeoElement objects
//              	    app.getKernel().getAlgebraProcessor().changeGeoElement(geo, newValue, true);
//              	}               
//            
//            }
//        }
//        else
//        {
//            super.setValueAt(obj, row, 1);
//            fireTableCellUpdated(row, 0);
//        }
    	if(obj instanceof CASTableCellValue){
    		setValueAt(obj, row, CASPara.contCol);
    		fireTableCellUpdated(row, CASPara.contCol);
    		System.out.println("Updated");
    		System.out.println(((CASTableCellValue)obj).getCommand());
    		System.out.println(((CASTableCellValue)obj).getOutput());
    	}
    }

    public Object getValueAt(int row) {
        Object obj = super.getValueAt(row, CASPara.contCol);
        if( !(obj instanceof CASTableCellValue ))
        {
        	System.out.println("Getting a non-CASTableCellValue"); 
        	System.out.println(obj.getClass().getName());
        }
        return obj;
    }

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
