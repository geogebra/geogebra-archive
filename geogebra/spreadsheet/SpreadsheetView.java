package geogebra.spreadsheet;

import geogebra.Application;
import geogebra.View;
import geogebra.algebra.MyCellEditor;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
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
       table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
       table.setCellSelectionEnabled(true);
       
       // we don't allow reordering
       table.getTableHeader().setReorderingAllowed(false);      
 
       JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
       
       // add row headers
       JTable rowHeader = new JTable(new RowModel(table.getModel()));
       TableCellRenderer renderer = new RowHeaderRenderer();
       Component comp = renderer.getTableCellRendererComponent(rowHeader, null, false, false, rowHeader.getRowCount() - 1, 0);
       rowHeader.setIntercellSpacing(new Dimension(0, 0));
       Dimension d = rowHeader.getPreferredScrollableViewportSize();
       d.width = comp.getPreferredSize().width;
       rowHeader.setPreferredScrollableViewportSize(d);
       rowHeader.setRowHeight(table.getRowHeight());
       rowHeader.setDefaultRenderer(Object.class, renderer);
       //rowHeader.addMouseListener(ml);
       scrollPane.setRowHeaderView(rowHeader);
        add(scrollPane, BorderLayout.CENTER);
       table.setRequestFocusEnabled(true);
       table.requestFocus();
       
       //Build the structure for the spreadsheet data
       spController = new SpreadsheetController(app, table,tableModel);
       initTableCellRendererEditor();
       attachView();	
     //  ListSelectionListener lsl = new SelectionAdapter();
     //  table.getSelectionModel().addListSelectionListener(lsl);
     //  table.getColumnModel().getSelectionModel().addListSelectionListener(lsl);
       
       
     }
    
    public void attachView() {
		kernel.notifyAddAll(this);
		kernel.attach(this);		
	}
    /** Get the current selection range.
     * @return The selected range of cells, or null if no cells are selected.
     */
//    public CellRange getSelectedRange()
//    {
//       if ((table.getSelectedRowCount() != 0) && (table.getSelectedColumnCount() != 0))
//       {
//          int[] rows = table.getSelectedRows();
//          int[] cols = table.getSelectedColumns();
//          int minColIndex = 0;
//          if (cols[0] < 0)
//          {
//             if (cols.length == 1)
//             {
//                return null;
//             }
//             minColIndex++;
//          }
//
//          int minRow = rows[0];
//          int maxRow = rows[rows.length - 1];
//
//          //columns selected are in ascending order
//          int minCol = cols[minColIndex];
//          int maxCol = cols[cols.length - 1];
//
//          return new CellRange(minRow, maxRow, minCol, maxCol);
//       }
//       else
//       {
//          return null;
//       }
//    }
//    
    private void initTableCellRendererEditor() {
               
         // set up cell editor
         JTextField editTF = new JTextField();
         TableCellEditor editor = new TableCellEditor(editTF);
         table.setDefaultEditor(Object.class, editor);
         // listen to editor events
         //editor.addCellEditorListener(spController); 
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
    
//    /** Called when firing an event as a result of the selected cells changing. */
//    protected void fireSelectionChanged()
//    {
//       SpreadsheetSelectionListener[] listeners = (SpreadsheetSelectionListener[]) listenerList.getListeners(SpreadsheetSelectionListener.class);
//       SpreadsheetSelectionEvent event = new SpreadsheetSelectionEvent(this, getSelectedRange());
//       for (int i = 0; i < listeners.length; i++)
//       {
//          listeners[i].selectionChanged(event);
//       }
//    }
    
    /**
     * Creates new blank SharpTableModel object with specified number of
     * rows and columns.  table is set to this table model to update screen.
     *
     * @param rows number of rows in new table model
     * @param cols number of columns in new table model
     */
    private void newTableModel(int rows, int cols)
    {
        tableModel = new SpreadsheetTableModel(table , rows, cols,app);
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
    	if (location != null)
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
        	if (value != null) {
        		GeoElement geo = (GeoElement )value;
        		setText( geo.toValueString() );
        	} else {
        		setText("");
        	}        	           
        }
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
	 
	  private static class RowHeaderRenderer extends DefaultTableCellRenderer
	   {
	      public RowHeaderRenderer()
	      {
	         setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	         setHorizontalAlignment(RIGHT);
	      }

	      public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column)
	      {
	         if (table != null)
	         {
	            JTableHeader header = table.getTableHeader();
	            if (header != null)
	            {
	               setForeground(header.getForeground());
	               setBackground(header.getBackground());
	            }
	         }
	         setValue(String.valueOf(row + 1));

	         return this;
	      }

	      public void updateUI()
	      {
	         super.updateUI();
	         setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	      }
	   }
	  
//	   private class SelectionAdapter implements ListSelectionListener
//	   {
//	      public void valueChanged(ListSelectionEvent e)
//	      {
//	        // int c = listenerList.getListenerCount(SpreadsheetSelectionListener.class);
//	         if (c > 0)
//	         {
//	        //    fireSelectionChanged();
//	         }
//	      }
//	   }
}
