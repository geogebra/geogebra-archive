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
    
}
