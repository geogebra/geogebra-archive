/**
 * 
 */
package geogebra.spreadsheet;
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

        for (int row = 0; row < numRows; row++)
           for (int col = 0; col < numColumns; col++)

              // we initialize it here
//              super.setValueAt(new Cell(""), row, col);

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

}
