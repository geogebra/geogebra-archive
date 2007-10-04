package geogebra.spreadsheet;

import geogebra.Application;
import geogebra.View;
import geogebra.algebra.MyCellEditor;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.util.FastHashMapKeyless;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

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
 //   private DefaultTableModel tableModel;
    private SpreadsheetTableModel tableModel;
    private Application app;
    private SpreadsheetController spController; 
   
    public SpreadsheetView(Application app,int rows, int columns)
    {
       this.app =app;
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
       table.setRequestFocusEnabled(true);
       table.requestFocus();
       
       //Build the structure for the spreadsheet data
       spController = new SpreadsheetController(app, table,tableModel);
       initTableCellRendererEditor();
       attachView();	
       
     }
    
    public void attachView() {
		kernel.notifyAddAll(this);
		kernel.attach(this);		
	}
    
    
    private void initTableCellRendererEditor() {
               
         // set up cell editor
         JTextField editTF = new JTextField();
         TableCellEditor editor = new TableCellEditor(editTF);
         table.setDefaultEditor(Object.class, editor);
         // listen to editor events
         editor.addCellEditorListener(spController); 
    }
    
        /** Can be used by subclasses to customize the underlying JTable
     * @return The JTable to be used by the spreadsheet
     */
    protected JTable createTable()
    {
    //	JTextField editTF = new JTextField();
       JTable t = new JTable();
       t.setDefaultRenderer(Object.class, new MyRenderer() );
      // t.setDefaultEditor(Object.class, new TableCellEditor(editTF));
       return t;
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
    	// TODO: remove
    	System.out.println("add: " + geo);
    	Point location = geo.getSpreadsheetCoords();
    	if (location != null) 
    	{                	
        	tableModel.setValueAt(geo, location.y, location.x);        	       
        }        
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
    	// TODO: remove
    	System.out.println("remove: " + geo);
    	
    	// remove old value from table
    	Point location = geo.getSpreadsheetCoords();    	
    	if (location != null) 
    	{                	
        	tableModel.setValueAt(null, location.y, location.x);        	       
        }    
    }
    
 
	/* (non-Javadoc)
     * @see geogebra.View#rename(geogebra.kernel.GeoElement)
     */
    public void rename(GeoElement geo)
    {
    	// TODO: remove
    	System.out.println("rename: " + geo);
    	
    	// remove old value from table
    	Point location = geo.getOldSpreadsheetCoords();    	
    	if (location != null) 
    	{                	
        	tableModel.setValueAt(null, location.y, location.x);       
            tableModel.fireTableCellUpdated(location.y, location.x);
        }    
    	
    	// add new one
    	add(geo);
    }
    /* (non-Javadoc)
     * @see geogebra.View#repaintView()
     */
    public void repaintView()
    {
    	repaint();
        
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
    final public void update(GeoElement geo)
    {
    	// TODO: remove
    	System.out.println("update: " + geo);
    	
    	Point location = geo.getSpreadsheetCoords();
        tableModel.fireTableCellUpdated(location.y, location.x);    	    	
    }
    /* (non-Javadoc)
     * @see geogebra.View#updateAuxiliaryObject(geogebra.kernel.GeoElement)
     */
    public void updateAuxiliaryObject(GeoElement geo)
    {
    	// TODO: remove
    	System.out.println("update: " + geo);
    	
    	update(geo);
        
    }

    /**
	 * inner class MyRenderer for GeoElements 
	 */
	private class MyRenderer extends DefaultTableCellRenderer {
		
		private static final long serialVersionUID = 1L;				
			
		public MyRenderer() {
            super();
			//setOpaque(true);
		}
        
        public void setValue(Object value)
        {
            if( value instanceof GeoElement)
            {
                if( value instanceof GeoNumeric)
                {
                    GeoNumeric geonum = (GeoNumeric )value;
                    setText( geonum.toValueString() );
                }
                else
                {
                    setText("");
                }
            }
            else
            {
                setText("");
            }
        }
	}
}
