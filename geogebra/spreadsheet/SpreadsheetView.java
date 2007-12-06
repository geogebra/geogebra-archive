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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
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
public class SpreadsheetView extends JComponent implements View, ActionListener, ListSelectionListener, ItemListener
{
    
    private JTable table;
    private Kernel kernel;
    private SpreadsheetTableModel tableModel;
    private Application app;
    private SpreadsheetController spController; 
    private ButtonGroup buttonGroup;
    private JCheckBox rowCheck;
    private JCheckBox columnCheck;
    private JCheckBox cellCheck;
    private JTextArea output;
    private ListSelectionEvent levent;
    private boolean ALLOW_ROW_SELECTION = true;
    private boolean ALLOW_COLUMN_SELECTION = false;
    
    int selectedColStart;
    int selectedRowStart;
    //Adding a Menu Bar to do the Copying
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem cutmenuItem, copymenuItem, pastemenuItem;
    
    public SpreadsheetView(Application app,int rows, int columns)
    {
    	
       this.app =app;
       table = createTable();
       kernel =app.getKernel();
     //  setLayout(new BorderLayout());
       setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
       newTableModel(rows, columns);
       // clobber resizing of all columns
       table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

       // set selection mode for contiguous  intervals
    //   table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    //   table.setCellSelectionEnabled(true);
       table.getSelectionModel().addListSelectionListener(new RowListener());
       table.getColumnModel().getSelectionModel().addListSelectionListener(new ColumnListener());
    
       table.getTableHeader().setReorderingAllowed(false);      
 
       JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
       
       //Create Menu Bar
       menuBar=  new JMenuBar();
       //Build the first Menu
       menu = new JMenu("Edit");
       menuBar.add(menu);
       cutmenuItem = new JMenuItem("Cut");
       menu.add(cutmenuItem);
       copymenuItem = new JMenuItem("Copy");
       menu.add(copymenuItem);
       pastemenuItem = new JMenuItem("Paste");
       menu.add(pastemenuItem);
       
       add(menuBar);
       
       cutmenuItem.addActionListener(this);
       copymenuItem.addActionListener(this);
       pastemenuItem.addActionListener(this);
       
       add(new JLabel("Selection Mode"));
       buttonGroup = new ButtonGroup();
       addRadio("Multiple Interval Selection").setSelected(true);
       addRadio("Single Selection");
       addRadio("Single Interval Selection");
       
       add(new JLabel("Selection Options"));
       rowCheck = addCheckBox("Row Selection");
       rowCheck.setSelected(true);
       columnCheck = addCheckBox("Column Selection");
       cellCheck = addCheckBox("Cell Selection");
       cellCheck.setEnabled(false);

       table.setColumnSelectionAllowed(true);
//		table.setRowSelectionAllowed(true);
       table.setRowSelectionAllowed(false);     
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
       
       //allowing slection
      // table.addKeyListener(l);
       
       //Build the structure for the spreadsheet data
       spController = new SpreadsheetController(app, table,tableModel);
       initTableCellRendererEditor();
       attachView();	
       
     
       //Popup menu code
       createPopupMenu();
       
       
       table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
       if (ALLOW_ROW_SELECTION) 
       { // true by default
           ListSelectionModel rowSM = table.getSelectionModel();
           rowSM.addListSelectionListener(new ListSelectionListener() {
               public void valueChanged(ListSelectionEvent e) {
                   //Ignore extra messages.
                   if (e.getValueIsAdjusting()) return;

                   ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                   if (lsm.isSelectionEmpty()) 
                   {
                       System.out.println("No rows are selected.");
                   } 
                   else 
                   {
                       int selectedRowStart = lsm.getMinSelectionIndex();
                       int selectedRowEnd	= lsm.getMaxSelectionIndex();
                       System.out.println("Rows " + (selectedRowStart + 1) +"to" +(selectedRowEnd + 1)
                                          + " is now selected.");
                   }
                   
               }
           });
       }
       else 
       {
           table.setRowSelectionAllowed(false);
       }
       
	       if (ALLOW_COLUMN_SELECTION) 
	       { // false by default
	         //  if (ALLOW_ROW_SELECTION) {
	               //We allow both row and column selection, which
	               //implies that we *really* want to allow individual
	               //cell selection.
	               table.setCellSelectionEnabled(true);
           }
           table.setColumnSelectionAllowed(true);
           ListSelectionModel colSM =
               table.getColumnModel().getSelectionModel();
           colSM.addListSelectionListener(new ListSelectionListener() {
               public void valueChanged(ListSelectionEvent e) 
               {
                   //Ignore extra messages.
                   if (e.getValueIsAdjusting()) return;

                   ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                   if (lsm.isSelectionEmpty()) 
                   {
                       System.out.println("No columns are selected.");
                   } 
                   else 
                   {
                       int selectedColStart = lsm.getMinSelectionIndex();
                       int selectedColEnd = lsm.getMaxSelectionIndex();
                       System.out.println("Column " + (selectedColStart + 1)+"to" +(selectedColEnd + 1)
                                          + " is now selected.");
                   }
               }
           });
           
           //to check when we try to copy a cell value to another cell
          // if(ALLOW_ROW_SELECTION)
           
       }
    // }
    
    
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
         //editor.addCellEditorListener(spController); 
    }
    
        /** Can be used by subclasses to customize the underlying JTable
     * @return The JTable to be used by the spreadsheet
     */
    protected JTable createTable()
    {
       JTable t = new JTable();
       t.setDefaultRenderer(Object.class, new MyRenderer() );
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
    	if (location != null) {
    		tableModel.fireTableCellUpdated(location.y, location.x);
    	}
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

	public void actionPerformed(ActionEvent event) 
	{//TODO:Localize the strings used here
		 Object obj =	event.getSource();	
		 int row=0, col=0;
		 if(event.getSource() == cutmenuItem)
		 {
			 
		 }
		 else if(event.getSource() == copymenuItem)
		 {
			 tableModel.copy(tableModel.getValueAt(selectedRowStart, selectedColStart), selectedRowStart, selectedColStart);
		 }
		 else if(event.getSource() == pastemenuItem)
		 {
			 
		 }
		 else
		 {
		 String command = event.getActionCommand();
	        //Cell selection is disabled in Multiple Interval Selection
	        //mode. The enabled state of cellCheck is a convenient flag
	        //for this status.

	        if ("Row Selection" == command) 
	        {
	            table.setRowSelectionAllowed(rowCheck.isSelected());
	            //In MIS mode, column selection allowed must be the
	            //opposite of row selection allowed.
	            if (!cellCheck.isEnabled()) 
	            {
	                table.setColumnSelectionAllowed(!rowCheck.isSelected());
	            }
	        } 
	        else if ("Column Selection" == command) 
	        {
	        	ALLOW_COLUMN_SELECTION=true;
	            table.setColumnSelectionAllowed(columnCheck.isSelected());
	            //In MIS mode, row selection allowed must be the
	            //opposite of column selection allowed.
	            if (!cellCheck.isEnabled()) 
	            {
	                table.setRowSelectionAllowed(!columnCheck.isSelected());
	            }
	        } 
	        else if ("Cell Selection" == command) 
	        {
	            table.setCellSelectionEnabled(cellCheck.isSelected());
	        } 
	        else if ("Multiple Interval Selection" == command) 
	        { 
	            table.setSelectionMode(
	                    ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	            //If cell selection is on, turn it off.
	            if (cellCheck.isSelected()) 
	            {
	                cellCheck.setSelected(false);
	                table.setCellSelectionEnabled(false);
	            }
	            //And don't let it be turned back on.
	            cellCheck.setEnabled(false);
	        } 
	        else if ("Single Interval Selection" == command) 
	        {
	            table.setSelectionMode(
	                    ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	            //Cell selection is ok in this mode.
	            cellCheck.setEnabled(true);
	        } 
	        else if ("Single Selection" == command) 
	        {
	            table.setSelectionMode(
	                    ListSelectionModel.SINGLE_SELECTION);
	            //Cell selection is ok in this mode.
	            cellCheck.setEnabled(true);
	        }

	        //Update checkboxes to reflect selection mode side effects.
	        rowCheck.setSelected(table.getRowSelectionAllowed());
	        columnCheck.setSelected(table.getColumnSelectionAllowed());
	        if (cellCheck.isEnabled()) 
	        {
	            cellCheck.setSelected(table.getCellSelectionEnabled());
	        }
	 //get the cells which are selected
		 }  
	        
	}
	  private JRadioButton addRadio(String text) 
	  {
	        JRadioButton b = new JRadioButton(text);
	        b.addActionListener(this);
	        buttonGroup.add(b);
	        add(b);
	        return b;
	  }
	  
	  private JCheckBox addCheckBox(String text) 
	  {
	        JCheckBox checkBox = new JCheckBox(text);
	        checkBox.addActionListener(this);
	        add(checkBox);
	        return checkBox;
	  }
	  private class RowListener implements ListSelectionListener 
	  {
	        public void valueChanged(ListSelectionEvent event) 
	        {
	            if (event.getValueIsAdjusting()) 
	            {
	            	
	                return;
	            }
	       }
	    }

	    private class ColumnListener implements ListSelectionListener 
	    {
	        public void valueChanged(ListSelectionEvent event) 
	        {
	            if (event.getValueIsAdjusting()) 
	            {
	                return;
	            }
	        }
	    }
	    
	    public void createPopupMenu() 
	    {
	        JMenuItem menuItem;

	        //Create the popup menu.
	        final JPopupMenu popup = new JPopupMenu();
	        menuItem = new JMenuItem("Copy");
	        menuItem.addActionListener(this);
	        popup.add(menuItem);
	        menuItem = new JMenuItem("Paste");
	        menuItem.addActionListener(this);
	        popup.add(menuItem);

	        //Add listener to the text area so the popup menu can come up.
	        MouseListener popupListener = new PopupListener(popup);
	        this.addMouseListener(popupListener);
	        
	        this.addMouseListener(new MouseAdapter(){
	            public void mouseReleased(MouseEvent Me){
	              if(Me.isPopupTrigger()){
	            	  popup.show(Me.getComponent(), Me.getX(), Me.getY());
	              }
	            }
	          });
	        
	    }
	    class PopupListener extends MouseAdapter 
	    {
	        JPopupMenu popup;

	        PopupListener(JPopupMenu popupMenu) 
	        {
	            popup = popupMenu;
	        }

	        public void mousePressed(MouseEvent e) 
	        {
	            maybeShowPopup(e);
	        }

	        public void mouseReleased(MouseEvent e) 
	        {
	            maybeShowPopup(e);
	        }

	        private void maybeShowPopup(MouseEvent e) 
	        {
	            if (e.isPopupTrigger()) 
	            {
	                popup.show(e.getComponent(),
	                           e.getX(), e.getY());
	            }
	        }
	    }

	
		public void valueChanged(ListSelectionEvent arg0) {
			
			
		}


		public void itemStateChanged(ItemEvent arg0) {
			// TODO Auto-generated method stub
			
		}
}
