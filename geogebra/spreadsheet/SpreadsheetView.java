package geogebra.spreadsheet;

import geogebra.Application;
import geogebra.View;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

/**
 * A class which will give the view of the Spreadsheet
 */

/**
 * @author Amy Mathew Varkey
 *
 */
public class SpreadsheetView extends JComponent implements View
{
    
    private JTable table;
    private Kernel kernel;
    private SpreadsheetTableModel tableModel;
    
    public SpreadsheetView(Application app,int rows, int columns)
    {
       table = createTable();
       kernel =app.getKernel();
       setLayout(new BorderLayout());
       newTableModel(rows, columns);

       // clobber resizing of all columns
       table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

       // set selection mode for contiguous  intervals
       table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
       table.setCellSelectionEnabled(true);
       
       // we don't allow reordering
       table.getTableHeader().setReorderingAllowed(false);
 
       JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
       add(scrollPane, BorderLayout.CENTER);

       JTable rowHeader = new JTable(new RowModel(table.getModel()));

       rowHeader.setIntercellSpacing(new Dimension(0, 0));
       table.setRequestFocusEnabled(true);
       table.requestFocus();
       
       
     }
    

    
        /** Can be used by subclasses to customize the underlying JTable
     * @return The JTable to be used by the spreadsheet
     */
    protected JTable createTable()
    {
       return new JTable();
    }
    
    private static class RowModel implements TableModel
    {
       private TableModel source;

       RowModel(TableModel source)
       {
          this.source = source;
       }

       public boolean isCellEditable(int rowIndex, int columnIndex)
       {
          return false;
       }

       public Class getColumnClass(int columnIndex)
       {
          return Object.class;
       }

       public int getColumnCount()
       {
          return 1;
       }

       public String getColumnName(int columnIndex)
       {
          return null;
       }

       public int getRowCount()
       {
          return source.getRowCount();
       }

       public void setValueAt(Object aValue, int rowIndex, int columnIndex)
       {
       }

       public Object getValueAt(int rowIndex, int columnIndex)
       {
          return null;
       }

       public void addTableModelListener(javax.swing.event.TableModelListener l)
       {
       }

       public void removeTableModelListener(javax.swing.event.TableModelListener l)
       {
       }
    }
    
    /**
     * Creates new blank SharpTableModel object with specified number of
     * rows and columns.  table is set to this table model to update screen.
     *
     * @param rows number of rows in new table model
     * @param cols number of columns in new table model
     */
    private void newTableModel(int rows, int cols)
    {
        tableModel = new SpreadsheetTableModel(table , rows, cols);
        table.setModel(tableModel);
        
    }
    /* (non-Javadoc)
     * @see geogebra.View#add(geogebra.kernel.GeoElement)
     */
    public void add(GeoElement geo)
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see geogebra.View#clearView()
     */
    public void clearView()
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see geogebra.View#remove(geogebra.kernel.GeoElement)
     */
    public void remove(GeoElement geo)
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see geogebra.View#rename(geogebra.kernel.GeoElement)
     */
    public void rename(GeoElement geo)
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see geogebra.View#repaintView()
     */
    public void repaintView()
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see geogebra.View#reset()
     */
    public void reset()
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see geogebra.View#update(geogebra.kernel.GeoElement)
     */
    public void update(GeoElement geo)
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see geogebra.View#updateAuxiliaryObject(geogebra.kernel.GeoElement)
     */
    public void updateAuxiliaryObject(GeoElement geo)
    {
        // TODO Auto-generated method stub
        
    }
   
}
