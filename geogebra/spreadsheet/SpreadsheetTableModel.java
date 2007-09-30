/**
 * 
 */
package geogebra.spreadsheet;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 * @author brisk1
 *
 */
public class SpreadsheetTableModel extends DefaultTableModel
{
    /** Stores file name of current document */
    private JTable table;
    /**
     * Stores modified state of document
     */
    private boolean modified;


    public SpreadsheetTableModel(JTable table)
    {
        super();
        this.table=table;
        modified=false;
    }
    
    public SpreadsheetTableModel(JTable table, int numRows, int numColumns)
    {
        super(numRows, numColumns);

//        for (int row = 0; row < numRows; row++)
//           for (int col = 0; col < numColumns; col++)
//
//              // we initialize it here
//              super.setValueAt(new String(""), row, col);

        // initialize state to unmodified and file to untitled
        modified = false;
        this.table = table;
    }
    
    /**
     * This constructor is convenience for loading objects that are already in
     * an array. It fills the SharpTableModel with the objects in the array
     * making Object[0][0] be in cell A1, etc.
     * <P>
     * <B>Note: </B> This constructor does not assume that objects are of the
     * desired form. It will parse a string to see if it is a number or formula.
     *
     * @param sharp
     *            gui object to associate with this SharpTableModel
     * @param data
     *            the array of objects to place into the SharpTableModel
     */
    public SpreadsheetTableModel(JTable table, Object[][] data)
    {
       this(table, data.length, data[0].length);

       /* load the data */
       for (int i = 0; i < data.length; i++)
       {
          for (int j = 0; j < data[i].length; j++)
          {
             setValueAt(data[i][j], i, j);
          }
       }

       // initialize state to unmodified and file to untitled
       modified = false;
    }
    
    public void setValueAt( Object obj, int row, int col)
    {
        GeoElement geo = null;
        if( !(obj instanceof GeoElement))
        {
            System.out.println("null");
            geo = (GeoElement)getValueAt( row, col);
            if( geo == null)
            {
                // TODO: need to create new geo Element
            }
            else
            {
               if( obj != null)
               {
                   String newValue = obj.toString();
                   int index = newValue.indexOf("=");
                   if( index != -1)
                   {
                       newValue = newValue.substring(index + 1);
                       newValue = newValue.trim();
                       if( geo instanceof GeoNumeric)
                       {
                           GeoNumeric geoNum = (GeoNumeric )geo;
                           try{
                           geoNum.setValue(Double.parseDouble(newValue) );
                           geoNum.update();
                           }catch( NumberFormatException nfe){
                               nfe.printStackTrace();
                           }
                       }
                   }
               }
               else
               {
                   super.setValueAt(obj, row, col);
               }
                // super.setValueAt(geo, row, col);
            }
            //super.setValueAt(geo, row, col);
        }
        else
        {
            super.setValueAt(obj, row, col);
        }
    }
    
    public Object getValueAt( int row, int col)
    {
        Object obj = super.getValueAt(row,col );
        if( !(obj instanceof GeoElement ))
        {
            //System.out.println("Getting a non-geo element");
        }
        return obj;
    }
    
    public boolean isCellEditable(int row, int col) {
        //GeoElement geo = (GeoElement) getValueAt(row, col);
        GeoElement geo = null;
        Object obj = getValueAt(row, col);
        if( !(obj instanceof GeoElement))
        {
            System.out.println("null");
            return true;
        }
        else
        {
            geo = (GeoElement )obj;
            return geo.isChangeable() || geo.isRedefineable();
        }
    }

}
