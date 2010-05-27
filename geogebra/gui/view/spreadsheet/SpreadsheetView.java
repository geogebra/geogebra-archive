
package geogebra.gui.view.spreadsheet;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class SpreadsheetView extends JSplitPane implements View, ComponentListener, FocusListener
{

	private static final long serialVersionUID = 1L;

	protected Application app;
	private Kernel kernel;
	
	// spreadsheet table and row header
	protected MyTable table;
	protected DefaultTableModel tableModel;
	public JList rowHeader;
	private RowHeaderRenderer rowHeaderRenderer;
	private MyListModel listModel;
	private SpreadsheetView view;
	
	
	// if these are increased above 32000, you need to change traceRow to an int[]
	public static int MAX_COLUMNS = 9999; // TODO make sure this is actually used
	public static int MAX_ROWS = 9999; // TODO make sure this is actually used
	
	private static int DEFAULT_COLUMN_WIDTH = 70;
	public static final int ROW_HEADER_WIDTH = 35; // wide enough for "9999"
	
	public int highestUsedColumn = -1; // for trace
	
	//short[] traceRow = new short[MAX_COLUMNS + 1]; // for trace
	
	private SpreadsheetTraceManager traceManager;
	private TraceDialog traceDialog;
	
	
	//G.STURR 2010-1-9: needed for resizing rows
	public static Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
	private Cursor otherCursor = resizeCursor; 
	private int mouseYOffset, resizingRow; 
	private boolean doRowResize = false;
	//END GSTURR
	
	//G.STURR 
	// note: MyTable uses its own minSelectionRow and maxSelectionRow.
	// The rowHeaderRenderer keeps them in sync.
	private int minSelectionRow = -1 ; 
	private int maxSelectionRow = -1 ; 
	
	
	// G.STURR 2010-2-12: needed for split panel, fileBrowser and toolbar
	private JScrollPane spreadsheet;
	private FileBrowserPanel browserPanel;
	private int defaultDividerLocation = 150;
	private CellFormatToolBar toolBar;
	private JPanel spreadsheetPanel;
	private JPanel restorePanel;
	
	
	//Properties
	private boolean showGrid = true;
	private boolean showRowHeader = true;
	private boolean showColumnHeader = true;	
	private boolean showVScrollBar = true;
	private boolean showHScrollBar = true;
	private boolean showBrowserPanel = false;
	private boolean showCellFormatToolBar = false;
	
	
	
	/**
	 * Construct spreadsheet view as a split panel. 
	 * Left panel holds file tree browser, right panel holds spreadsheet. 
	 */
	public SpreadsheetView(Application app, int columns, int rows) {
		/*
		JList table = new JList();
		setViewportView(table);
		table.setFocusable(true);
		table.addKeyListener(new KeyListener0());
		/**/
		
		this.app = app;
		kernel = app.getKernel();
		view = this;
		
		// table
		tableModel = new DefaultTableModel(rows, columns);
		table = new MyTable(this, tableModel);
		
		table.columnHeader.setPreferredSize(new Dimension((int)(table.preferredColumnWidth)
				, (int)(MyTable.TABLE_CELL_HEIGHT)));

		
		// row header list
		listModel = new MyListModel(tableModel); 
		rowHeader = new JList(listModel);
		rowHeader.setFocusable(true);
		rowHeader.setAutoscrolls(false);
		rowHeader.addMouseListener(new MouseListener1());
		rowHeader.addMouseMotionListener(new MouseMotionListener1());
		rowHeader.addKeyListener(new KeyListener1());
		//rowHeader.setFixedCellWidth(MyTable.TABLE_CELL_WIDTH);
		rowHeader.setFixedCellWidth(ROW_HEADER_WIDTH);
		
		//G.STURR 2010-1-9: row heights are no longer fixed 
		//rowHeader.setFixedCellHeight(table.getRowHeight()); // + table.getRowMargin();
		
		rowHeaderRenderer = new RowHeaderRenderer(table, rowHeader);
		rowHeader.setCellRenderer(rowHeaderRenderer);
			
		
		// put the table and the row header list into a scroll plane
		// G.STURR 2010-2-12: scrollPane now named as spreadsheet
		spreadsheet = new JScrollPane();
		spreadsheet.setRowHeaderView(rowHeader);
		spreadsheet.setViewportView(table);
		
		
		// Florian Sonner 2008-10-20
		setBorder(BorderFactory.createEmptyBorder());
		
		// create and set corners, Markus December 08
		Corner upperLeftCorner = new Corner(); //use FlowLayout
		upperLeftCorner.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, MyTable.TABLE_GRID_COLOR));		
		upperLeftCorner.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				//table.setSelectionType(table.CELL_SELECT);
				table.selectAll();
				//table.selectionChanged(); //G.Sturr 2010-1-29
			}
		});
		
		
		//Set the corners.
		spreadsheet.setCorner(JScrollPane.UPPER_LEFT_CORNER, upperLeftCorner);
		spreadsheet.setCorner(JScrollPane.LOWER_LEFT_CORNER, new Corner());
		spreadsheet.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new Corner());
		
	
		//G.STURR 2010-2-12			
		// Add spreadsheet and browser panes to SpreadsheetView
			
		spreadsheetPanel = new JPanel(new BorderLayout());
		spreadsheetPanel.add(getCellFormatToolBar(),BorderLayout.NORTH);
		setShowToolBar(showCellFormatToolBar);
		spreadsheetPanel.add(spreadsheet,BorderLayout.CENTER);
		
		setRightComponent(spreadsheetPanel);	
		setShowBrowserPanel(showBrowserPanel);  //adds browser Panel or null panel to left component
		
		
		updateFonts();
		attachView(); //G.Sturr 2010-1-18
	
		//G.Sturr 2010-4-10: Add listener for row/column size change.
		// Needed for auto-enlarging spreadsheet.
		table.addComponentListener(this);
		
		
	}
	
	
	
	private class Corner extends JComponent {
		private static final long serialVersionUID = -4426785169061557674L;

		protected void paintComponent(Graphics g) {
	        g.setColor(MyTable.BACKGROUND_COLOR_HEADER);
	        g.fillRect(0, 0, getWidth(), getHeight());
	    }
	}

		/**/
	
	public void attachView() {
		clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);		
	}

	public void detachView() {
		kernel.detach(this);
		clearView();
		//kernel.notifyRemoveAll(this);		
	}
	
	public Application getApplication() {
		return app;
	}
	
	public MyTable getTable() {
		return table;
	}
	

	//===============================================
	//         Tracing
	//===============================================
	
	public SpreadsheetTraceManager getTraceManager() {
		if (traceManager == null)
			traceManager = new SpreadsheetTraceManager(this);
		return traceManager;
	}
	
	public void addRemoveSpreadsheetTrace(GeoElement geo, boolean addGeo){
		if(addGeo)
			traceManager.addSpreadsheetTraceGeo(geo);
		else
			traceManager.removeSpreadsheetTraceGeo(geo);	
	}
	
	public void showTraceDialog(GeoElement geo, CellRange traceCell){
		if (traceDialog == null){
			traceDialog = new TraceDialog(app, geo, traceCell);
		}else{
			traceDialog.setTraceDialogSelection(geo, traceCell);
		}
		traceDialog.setVisible(true);
		
	}
	

	public int getHighestUsedColumn() {
		//traceHandler.resetTraceRow(highestUsedColumn+1);
		//traceHandler.resetTraceRow(highestUsedColumn+2);
		return highestUsedColumn;
	}
			
	/*
	public int getTraceRow(int column) {
		return traceHandler.getTraceRow(column);
	}
	*/
	
	
	
	/*
	private void resetTraceRow(int col) {
		if (col < MAX_COLUMNS) traceRow[col] = 1;
	}
	
	
	
	public int getTraceRow(int column) {
		if (column < 0 || column >= MAX_COLUMNS) return -1;
		if (traceRow[column] == 0) traceRow[column] = 1; //first call
		return (int)traceRow[column]++;
	}
	
	
	
	boolean collectingTraces = false;
	HashMap traces = null;
	
	
	
	public void startCollectingSpreadsheetTraces() {
		collectingTraces = true;
		if (traces == null)
			traces = new HashMap();
		traces.clear();
	}
	
	
	
	
	public void stopCollectingSpreadsheetTraces() {
		collectingTraces = false;
		
		Iterator it = traces.values().iterator();
		
		while (it.hasNext()) {
			traceToSpreadsheet((GeoElement)it.next());
		}
		
		traces.clear();

	}
	
	
	
	
	private double[] coords = new double[2];

	public void traceToSpreadsheet(GeoElement geo) {
		
		if (collectingTraces) {
			traces.put(geo.getTraceColumn1(), geo);
			return;
		}
		
		Construction cons = app.getKernel().getConstruction();
		
		
		switch (geo.getGeoClassType()) {
			
		case GeoElement.GEO_CLASS_POINT:
			
			GeoPoint P = (GeoPoint)geo;
			
    		boolean polar = P.getMode() == Kernel.COORD_POLAR;
    		
	    	if (polar)
	    		P.getPolarCoords(coords);
	    	else
	    		P.getInhomCoords(coords);

			
	    	String col = P.getTraceColumn1(); // call before getTraceRow()
	    	int row = P.getTraceRow();
	    	if (row > 0) {
    	    	//Application.debug(col+row);   		
		    	app.getGuiManager().setScrollToShow(true);
		    	
		    	GeoNumeric traceCell = new GeoNumeric(cons, col + row,coords[0]);
		    	traceCell.setAuxiliaryObject(true);
		    	
		    	col = P.getTraceColumn2(); // call before getTraceRow()
    	    	//Application.debug(col+row);   		
		    	
		    	GeoNumeric traceCell2;
		    	
		    	if (polar) traceCell2 = new GeoAngle(cons,col+row,coords[1]);
		    	else traceCell2 = new GeoNumeric(cons,col+row,coords[1]);
		    	
		    	traceCell2.setAuxiliaryObject(true);
		    	
		    	cons.getApplication().getGuiManager().setScrollToShow(false);	
		    	
		    	P.setLastTrace1(coords[0]);
		    	P.setLastTrace2(coords[1]);
	    	}
			break;
		case GeoElement.GEO_CLASS_VECTOR:
			
			GeoVector vector = (GeoVector)geo;
			
			vector.getInhomCoords(coords);
			
	    	col = vector.getTraceColumn1();
	    	row = vector.getTraceRow();
	    	if (row > 0) {
		    	cons.getApplication().getGuiManager().setScrollToShow(true);
	    		
	    		GeoNumeric traceCell = new GeoNumeric(cons,col+row,coords[0]);
		    	traceCell.setAuxiliaryObject(true);
		    	GeoNumeric traceCell2 = new GeoNumeric(cons,vector.getTraceColumn2()+row,coords[1]);
		    	traceCell2.setAuxiliaryObject(true);
		    	
		    	cons.getApplication().getGuiManager().setScrollToShow(false);
		    	
		    	vector.setLastTrace1(coords[0]);
		    	vector.setLastTrace2(coords[1]);
	    	}
			break;
		case GeoElement.GEO_CLASS_NUMERIC:
			
			GeoNumeric num = (GeoNumeric)geo;
			
	    	col = num.getTraceColumn1(); // must be called before getTraceRow()
	    	row = num.getTraceRow();
	    	
	    	cons.getApplication().getGuiManager().setScrollToShow(true);
	    	GeoNumeric traceCell = new GeoNumeric(cons, col+row, num.getValue());
	    	cons.getApplication().getGuiManager().setScrollToShow(false);
	    	
	    	traceCell.setAuxiliaryObject(true);
	    	
	    	num.setLastTrace1(num.getValue());
			break;
		
		}
	}
	
	
	*/
	
	
	
//	public void incrementTraceRow(int column) {
//		if (column < 0 || column >= MAX_COLUMNS) return;
//		traceRow[column]++;
//	}
	
//	public void resetTraceRow(int column) {
//		if (column < 0 || column >= MAX_COLUMNS) return;
//		traceRow[column] = 0;
//	}
	
	/* used to "reserve" a column
	 * 
	 */
//	public void incrementHighestUsedColumn() {
//		highestUsedColumn++;
//	}
	
	
	public void add(GeoElement geo) {	
		//Application.debug(new Date() + " ADD: " + geo);				

		update(geo);
		
		Point location = geo.getSpreadsheetCoords();
		
		// autoscroll to new cell's location
		if (scrollToShow && location != null )
			table.scrollRectToVisible(table.getCellRect(location.y, location.x, true));
		
		//G.Sturr 2010-4-2: mark this geo to adjust row width for tall LaTeX images 
		//MyCellEditor.cellResizeHeightSet.add(new Point(location.x,location.y));
		
		
		//Application.debug("highestUsedColumn="+highestUsedColumn);
	}
	
	private boolean scrollToShow = false;
	
	public void setScrollToShow(boolean scrollToShow) {
		this.scrollToShow = scrollToShow;
	}
	
	public void remove(GeoElement geo) {
		//Application.debug(new Date() + " REMOVE: " + geo);
				
		Point location = geo.getSpreadsheetCoords();
		if (location != null) {
			doRemove(geo, location.y, location.x);
		}
	}
	
	private void doRemove(GeoElement geo, int row, int col) {
		tableModel.setValueAt(null, row, col);
		if (col <= highestUsedColumn) checkColumnEmpty(highestUsedColumn);
		//Application.debug("highestUsedColumn="+highestUsedColumn);
	}
	
	/**
	 * Updates highestUsedColumn when this is sent as a parameter
	 */
	private void checkColumnEmpty(int col) {
		
		if (col == -1) return; // end recursion
		
		// check if this was the last cell used in this column
		boolean columnNotEmpty = false;
		for (int r = 0 ; r < tableModel.getRowCount() ; r++) {
			if (tableModel.getValueAt(r, col) != null) {
				// column not empty
				columnNotEmpty = true;
				break;
			}
		}
		if (!columnNotEmpty) {
			highestUsedColumn--;
			checkColumnEmpty(highestUsedColumn);
		}
		
	}
	
	public void rename(GeoElement geo) {
		//Application.debug(new Date() + " RENAME");
		Point location = geo.getOldSpreadsheetCoords();
		if (location != null) {
			doRemove(geo, location.y, location.x);
		}
		
		add(geo);
		
	}
	
	public void updateAuxiliaryObject(GeoElement geo) {		
	}
	
// G.STURR -- selectedElems is no longer  used	
//	public static HashSet selectedElems = new HashSet();
	
	public void repaintView() {
		/*
		 * Markus Hohenwarter 2008-09-18
		 *   The following code is extremely slow and a very bad performance bottleneck.
		 *   If this needs to be done, then definitely NOT in repaintView()
		 * 
		ArrayList elems = app.getSelectedGeos();
		selectedElems.clear();
		for (int i = 0; i < elems.size(); ++ i) {
			GeoElement geo = (GeoElement)elems.get(i);
			selectedElems.add(geo.getLabel());
		}
		if (System.currentTimeMillis() - table.selectionTime > 100) {
			table.selectNone();
		}
		*/
		
		repaint();		
	}
	
	public void clearView() {
		
		//Application.debug(new Date() + " CLEAR VIEW");
		
		int rows = tableModel.getRowCount();
		int columns = tableModel.getColumnCount();
		for (int c = 0; c < columns; ++c) {
			for (int r = 0; r < rows; ++r) {
				tableModel.setValueAt(null, r, c);
			}
		}	
	}
	

		
	public static class MyListModel extends AbstractListModel {
		
		private static final long serialVersionUID = 1L;
		
		protected DefaultTableModel model;

		public MyListModel(DefaultTableModel model0) {
			model = model0;
		}
		
		public int getSize() {
			return model.getRowCount();
		}
		
		public Object getElementAt(int index) {
			return "" + (index + 1);
		}
		
		//G.STURR 2010-1-9: forces update of rowHeader, called after row resizing
		public Void changed() {
			this.fireContentsChanged(this, 0, model.getRowCount());
			return null;
			
		}
		
    }

// G.Sturr, moved these declarations to the top
//	protected int minSelectionRow = -1;
//	protected int maxSelectionRow = -1;

	public class RowHeaderRenderer extends JLabel implements ListCellRenderer, ListSelectionListener {
	
    	private static final long serialVersionUID = 1L;
    	
    	protected JTableHeader header;
    	protected JList rowHeader;
    	protected ListSelectionModel selectionModel;
    	private Color defaultBackground;
	
		public RowHeaderRenderer(JTable table, JList rowHeader) {
	 		super("", JLabel.CENTER);
    		setOpaque(true);
    		defaultBackground = MyTable.BACKGROUND_COLOR_HEADER;
			
			this.rowHeader = rowHeader;
			header = table.getTableHeader() ;
//			setOpaque(true);
			//setBorder(UIManager.getBorder("TableHeader.cellBorder" ));
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, MyTable.TABLE_GRID_COLOR));
//			setHorizontalAlignment(CENTER) ;
//			setForeground(header.getForeground()) ;
//			setBackground(header.getBackground());
			if (getFont().getSize() == 0) {
				Font font1 = app.getPlainFont();
				if (font1 == null || font1.getSize() == 0) {
					font1 = new Font("dialog", 0, 12);
				}
				setFont(font1);
			}
			table.getSelectionModel().addListSelectionListener(this);
		}
	
		public Component getListCellRendererComponent(JList list, Object value,	int index, boolean  isSelected, boolean cellHasFocus) {
			
			// G.STURR 2010-1-9: adjust row height to match spreadsheet table row height 
			Dimension size = getPreferredSize();
		    size.height = table.getRowHeight(index);
		    setPreferredSize(size);
		    //END GSTURR
			
			setText((value == null) ? "" : value.toString());
			
			if (table.getSelectionType() == table.COLUMN_SELECT ) {
				setBackground(defaultBackground);
			} else {
				if (table.selectedRowSet.contains(index)
						|| (index >= minSelectionRow && index <= maxSelectionRow)) {
					setBackground(MyTable.SELECTED_BACKGROUND_COLOR_HEADER);
				} else {
					setBackground(defaultBackground);
				}
			}
			
			/* --------- old code
			if (minSelectionRow != -1 && maxSelectionRow != -1) {
				if (index >= minSelectionRow && index <= maxSelectionRow &&
						selectionModel.isSelectedIndex(index)) 
				{
					setBackground(MyTable.SELECTED_BACKGROUND_COLOR_HEADER);
				}
				else {
					setBackground(defaultBackground);					
				}
			}
			else {
				setBackground(defaultBackground);
			}		
			*/
			
			return this;
		}
	
		/**
		 * Update the rowHeader list when row selection changes in the table
		 */
		public void valueChanged(ListSelectionEvent e) {
			selectionModel = (ListSelectionModel)e.getSource();
			minSelectionRow = selectionModel.getMinSelectionIndex();
			maxSelectionRow = selectionModel.getMaxSelectionIndex();
			rowHeader.repaint();
		}

    }

	
	
	    //G.STURR 2010-1-9
	    //
	    // Returns index of row to be resized if mouse point P is 
		// near a row boundary (within 3 pixels) 
	 	private int getResizingRow(Point p){ 
	 		int resizeRow = -1;
	 		Point point = table.getIndexFromPixel(p.x, p.y);
			if (point != null) {
				// test if mouse is 3 pixels from row boundary
				int cellRow = (int) point.getY();
				if(cellRow >= 0) {
					Rectangle r = table.getCellRect(cellRow, 0, true);
					// near row bottom
					if (p.y < r.y+3) resizeRow = cellRow-1;
					// near row top
					if (p.y > r.y + r.height - 3)resizeRow = cellRow;
				}
			}
	        return resizeRow; 
	    } 
	    
	    // Cursor change for when mouse is over a row boundary  
	    private void swapCursor(){ 
	        Cursor tmp = rowHeader.getCursor(); 
	        rowHeader.setCursor(otherCursor); 
	        otherCursor = tmp; 
	    } 
	    
	    //
	    //END GSTURR
	
	
	
	
	
	protected int row0 = -1;

	protected class MouseListener1 implements MouseListener
	{
		
		public void mouseClicked(MouseEvent e) {
			
			// G.Sturr 2010-3-29
			// Double clicking on a row boundary auto-adjusts the 
			// height of the row above the boundary (the resizingRow)
			
			if (resizingRow >= 0 && !Application.isRightClick(e) && e.getClickCount() == 2) {
				
				table.fitRow(resizingRow);
				e.consume();
			}
			
			//END G.Sturr
			
		}
		
		public void mouseEntered(MouseEvent e) {
		}
		
		public void mouseExited(MouseEvent e) {
		}
		
		public void mousePressed(MouseEvent e) {						
			boolean shiftPressed = e.isShiftDown();	
			boolean metaDown = Application.isControlDown(e);							
			boolean rightClick = Application.isRightClick(e);
					
			int x = e.getX();
			int y = e.getY();
			
			
			//G.STURR 2010-1-9: 
			// Update resizingRow. If nonnegative, then mouse is over a boundary
			// and it gives the row to be resized (resizing is done in mouseDragged).
			Point p = e.getPoint(); 
	        resizingRow = getResizingRow(p); 
	        mouseYOffset = p.y - table.getRowHeight(resizingRow); 
	        //
			
			
			// left click
			if (!rightClick) {		
				
				if(resizingRow >=0) return; //GSTURR 2010-1-9
				
				Point point = table.getIndexFromPixel(x, y);
				if (point != null) {
					//G.STURR 2010-1-29
					if(table.getSelectionType() != table.ROW_SELECT){
						table.setSelectionType(table.ROW_SELECT);
						rowHeader.requestFocusInWindow();
					}
					/*
					if (table.getSelectionModel().getSelectionMode() != ListSelectionModel.MULTIPLE_INTERVAL_SELECTION ||
							table.getColumnSelectionAllowed() == true) {
						table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
						table.setColumnSelectionAllowed(false);
						table.setRowSelectionAllowed(true);
					}
					*/
					if (shiftPressed) {
						if (row0 != -1) {
							int row = (int)point.getY();
							table.setRowSelectionInterval(row0, row);
						}
					}	
					/*
				    * G.Sturr 2009-11-15 (ctrl-select now handled in table)
					} else if (metaDown) {
						row0 = (int) point.getY();
						table.setRowSelectionInterval(row0, row0);
						// table.addRowSelectionInterval(row0, row0);
					}
                     */
						
					else {
						row0 = (int)point.getY();
						table.setRowSelectionInterval(row0, row0);
					}
					table.repaint();
				}
			}
			/* G.Sturr 2009-9-30: moved this to mouseReleased
			// RIGHT CLICK
			else {	
				if (!app.letShowPopupMenu()) return;    	
    		       
				if (minSelectionRow != -1 && maxSelectionRow != -1) {
					ContextMenuRow popupMenu = new ContextMenuRow(table, 0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow, new boolean[0]);
			        popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}	
						
			}
			*/
			
		}
		
		public void mouseReleased(MouseEvent e)	{
			//G.Sturr 2009-9-30: moved show contextMenu from mousePressed
			// and added right click selection
			
			boolean rightClick = Application.isRightClick(e);
			
			if (rightClick) { 			
				if (!app.letShowPopupMenu()) return; 
				
				
				Point p = table.getIndexFromPixel(e.getX(), e.getY());
				if (p == null) return;
				
				// if click is outside current selection then change selection
				if(p.getY() < minSelectionRow ||  p.getY() > maxSelectionRow 
						|| p.getX() < table.minSelectionColumn || p.getX() > table.maxSelectionColumn){
					// switch to row selection mode and select row
					//G.STURR 2010-1-29
					if(table.getSelectionType() != table.ROW_SELECT){
						table.setSelectionType(table.ROW_SELECT);
					}
					/*------- old code
					if (table.getSelectionModel().getSelectionMode() != ListSelectionModel.MULTIPLE_INTERVAL_SELECTION ||
							table.getColumnSelectionAllowed() == true) {
						table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
						table.setColumnSelectionAllowed(false);
						table.setRowSelectionAllowed(true);
					}
					*/
					//table.selectNone();
					table.setRowSelectionInterval((int)p.getY(), (int)p.getY());
				}	
			
				//show contextMenu
				
				//G.STURR 2009-12-20 We now use single ContextMenu for all right clicks 
				//
				//ContextMenuRow popupMenu = new ContextMenuRow(table, 0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow, new boolean[0]);
			    //   popupMenu.show(e.getComponent(), e.getX(), e.getY());
				
				ContextMenu popupMenu = new ContextMenu(table, 0, minSelectionRow, table.getModel().getColumnCount() - 1, 
						maxSelectionRow, new boolean[0],1);
		        popupMenu.show(e.getComponent(), e.getX(), e.getY());
		        // END GSTURR
			} 
			
			
			//G.STURR 2010-1-9
			// If row resize has happened, resize all other selected rows
			if (doRowResize) {
				if (minSelectionRow != -1 && maxSelectionRow != -1
						&& (maxSelectionRow - minSelectionRow > 1)) {
					if (table.isSelectAll())
						table.setRowHeight(table.getRowHeight(resizingRow));
					else
						for (int row = minSelectionRow; row <= maxSelectionRow; row++) {
							table.setRowHeight(row, table.getRowHeight(resizingRow));
						}
				}
				doRowResize = false;
			}

		}

	}
	
	protected class MouseMotionListener1 implements MouseMotionListener
	{
		
		public void mouseDragged(MouseEvent e) {
			if(Application.isRightClick(e))return; //G.Sturr 2009-9-30 
			
			// G.STURR 2010-1-9
			// On mouse drag either resize or select a row
			int x = e.getX();
			int y = e.getY();
			if (resizingRow >= 0) {
				// resize row
				int newHeight = y - mouseYOffset;
				if (newHeight > 0) {
					table.setRowHeight(resizingRow, newHeight);
					// set this flag to resize all selected rows on mouse release
					doRowResize = true; 
				}

			} else { // select row
				Point point = table.getIndexFromPixel(x, y);
				if (point != null) {
					int row = (int) point.getY();
					table.setRowSelectionInterval(row0, row);
					
					//G.Sturr 2010-4-4
					// keep the row header updated when drag selecting multiple rows 
					view.updateRowHeader();
					table.scrollRectToVisible(table.getCellRect(point.y,point.x,true));
					table.repaint();
				}
			}
			
			
		    /* -------- old code	
	        int x = e.getX();
			int y = e.getY();
			Point point = table.getIndexFromPixel(x, y);
			if (point != null) {
				int row = (int)point.getY();
				table.setRowSelectionInterval(row0, row);
				table.repaint();
			}
			*/
				
		}
		
		public void mouseMoved(MouseEvent e) {
			
			//G.STURR 2010-1-9
			// Show resize cursor when mouse is over a row boundary
			if ( ( getResizingRow(e.getPoint()) >= 0 ) != (rowHeader.getCursor() == resizeCursor ) ){
				swapCursor();
				}
			//END GSTURR
		}
		
	}
	
	

	
	protected class KeyListener1 implements KeyListener 
	{
		
		public void keyTyped(KeyEvent e) {
		}
		
		public void keyPressed(KeyEvent e) {
			
			int keyCode = e.getKeyCode();
			
			boolean metaDown = Application.isControlDown(e);				
			boolean altDown = e.isAltDown();				
			
			//G.Sturr 2009-11-15: metaDown flag needed to handle ctrl-select in MyTable
			table.metaDown = metaDown;
			
			//Application.debug(keyCode);
			switch (keyCode) {				
			case KeyEvent.VK_C : // control + c
				if (metaDown && minSelectionRow != -1 && maxSelectionRow != -1) {
					table.copyPasteCut.copy(0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow, altDown);
					e.consume();
				}
				break;
			case KeyEvent.VK_V : // control + v
				if (metaDown && minSelectionRow != -1 && maxSelectionRow != -1) {
					boolean storeUndo = table.copyPasteCut.paste(0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow);
					if (storeUndo)
		 				app.storeUndoInfo();
					e.consume();
				}
				break;				
			case KeyEvent.VK_X : // control + x
				if (metaDown && minSelectionRow != -1 && maxSelectionRow != -1) {
					table.copyPasteCut.copy(0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow, altDown);
					e.consume();
				}
				boolean storeUndo = table.copyPasteCut.delete(0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow);
				if (storeUndo)
	 				app.storeUndoInfo();
				break;
				
			case KeyEvent.VK_DELETE : // delete
			case KeyEvent.VK_BACK_SPACE : // delete on MAC
				storeUndo = table.copyPasteCut.delete(0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow);
				if (storeUndo)
	 				app.storeUndoInfo();
				break;			
			}
		}
		
		public void keyReleased(KeyEvent e) {
			//G.Sturr 2009-11-15: metaDown flag needed to handle ctrl-select in MyTable
			table.metaDown = false;
			
		}
		
	}
		

	/**/
		
	public void restart() {
		highestUsedColumn = -1;
		updateColumnWidths();
	}	
	
	public void reset() {
	}	
	
	public void update(GeoElement geo) {
		Point location = geo.getSpreadsheetCoords();
		if (location != null && location.x < MAX_COLUMNS && location.y < MAX_ROWS) {
			
			if (location.x > highestUsedColumn) highestUsedColumn = location.x;
			
			if (location.y >= tableModel.getRowCount()) {
				tableModel.setRowCount(location.y + 1);		
				spreadsheet.getRowHeader().revalidate();
			}
			if (location.x >= tableModel.getColumnCount()) {
				table.setMyColumnCount(location.x + 1);		
				JViewport cH = spreadsheet.getColumnHeader();
				
				// bugfix: double-click to load ggb file gives cH = null
				if (cH != null) cH.revalidate();
			}
			tableModel.setValueAt(geo, location.y, location.x);
			
			//G.Sturr 2010-4-2
			//Mark this cell to be resized by height
			table.cellResizeHeightSet.add(new Point(location.x, location.y));
		}			
	}	

	
	
	
	/**
	 * returns settings in XML format
	 */
	public void getXML(StringBuilder sb) {
		sb.append("<spreadsheetView>\n");
		
		int width = getWidth();//getPreferredSize().width;
		int height = getHeight();//getPreferredSize().height;
		
		//if (width > MIN_WIDTH && height > MIN_HEIGHT) 
		{
			sb.append("\t<size ");
			sb.append(" width=\"");
			sb.append(width);
			sb.append("\"");
			sb.append(" height=\"");
			sb.append(height);
			sb.append("\"");
			sb.append("/>\n");
		}
		
		// column widths 
		for (int col = 0 ; col < table.getColumnCount() ; col++) {
			TableColumn column = table.getColumnModel().getColumn(col); 
			int colWidth = column.getWidth();
			if (colWidth != DEFAULT_COLUMN_WIDTH)
				sb.append("\t<spreadsheetColumn id=\""+col+"\" width=\""+colWidth+"\"/>\n");
		}

		sb.append("</spreadsheetView>\n");
	}
	

	
	public void updateFonts() {
			
		Font font = app.getPlainFont();
		
		int size = font.getSize();
		if (size < 12) size = 12; // minimum size
		double multiplier = (double)(size)/12.0;
		
		table.setRowHeight((int)(MyTable.TABLE_CELL_HEIGHT * multiplier));
		rowHeader.setFixedCellWidth((int)(ROW_HEADER_WIDTH * multiplier));	
		
		//rowHeader.setFixedCellHeight(table.getRowHeight()); //G.STURR 2010-1-9 
	
		table.setFont(app.getPlainFont());
		rowHeader.setFont(font);
		table.columnHeader.setFont(font);
		rowHeaderRenderer.setFont(font);
		table.preferredColumnWidth = (int) (MyTable.TABLE_CELL_WIDTH * multiplier);
		table.columnHeader.setPreferredSize(new Dimension(table.preferredColumnWidth
						, (int)(MyTable.TABLE_CELL_HEIGHT * multiplier)));
		
		// G.Sturr 2010-4-2
		// Adjust row heights for tall LaTeX images
		table.fitAll(true, false); 
		
		/*
		JLabel testLabel = new JLabel();
		testLabel.setFont(app.getFontCanDisplay("M", Font.BOLD));
		testLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		
		testLabel.setText("M");
		int newHeight = (int) testLabel.getPreferredSize().getHeight();
		
		table.setRowHeight(newHeight);
		table.columnHeader.setPreferredSize(new Dimension((int)(MyTable.TABLE_CELL_WIDTH * multiplier)
				, newHeight));
		*/
		
	}
	
	
	public void setColumnWidth(int col, int width) {
		//Application.debug("col = "+col+" width = "+width);
		TableColumn column = table.getColumnModel().getColumn(col); 
		column.setPreferredWidth(width);
		//column.
	}
	
	
	public void updateColumnWidths() {
		Font font = app.getPlainFont();
		
		int size = font.getSize();
		if (size < 12) size = 12; // minimum size
		double multiplier = (double)(size)/12.0;
		table.preferredColumnWidth = (int) (MyTable.TABLE_CELL_WIDTH * multiplier);
		for (int i = 0; i < table.getColumnCount(); ++ i) {
			table.getColumnModel().getColumn(i).setPreferredWidth(table.preferredColumnWidth);
		}
		
	}

	
	
	//G.STURR 2010-1-9
	public void updateRowHeader() {
		listModel.changed();
	}
	//END GSTURR
		
	
	//G.STURR 2010-2-12: Added methods to support file browser
	//
	
	public JViewport getRowHeader(){
		return spreadsheet.getRowHeader();
	}
	
	public JViewport getColumnHeader(){
		return spreadsheet.getColumnHeader();
	}
	
	
	public boolean loadSpreadsheetFromURL(File f) {
		
		boolean succ = false;
		
		URL url = null;
		try {
			url = f.toURI().toURL();
			succ = loadSpreadsheetFromURL(url); 
		} 
		
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return succ;
	}
	
	public boolean loadSpreadsheetFromURL(URL url) {
		
		boolean succ = table.copyPasteCut.pasteFromURL(url);
		if (succ) {
			app.storeUndoInfo();
		}
		return succ;
	}
	
	public FileBrowserPanel getBrowserPanel() {		
		if (browserPanel == null) {
			browserPanel = new FileBrowserPanel(this);
			browserPanel.setMinimumSize(new Dimension(50, 0));
		}	
		return browserPanel;
	}
	
	
	
	public void setShowBrowserPanel(boolean showBrowser) {
		
		if (showBrowser) {
			setLeftComponent(getBrowserPanel());
			setDividerLocation(defaultDividerLocation);
			setDividerSize(4);
		} else {
			setLeftComponent(null);
			setLastDividerLocation(getDividerLocation());
			setDividerLocation(0);
			setDividerSize(0);
		}
		showBrowserPanel = showBrowser;
	}
	
	public boolean getShowBrowserPanel(){
		return showBrowserPanel;
		
	}
	
	public void minimizeBrowserPanel(){
		setDividerLocation(10);
		setDividerSize(0);
		setLeftComponent(getRestorePanel());
	}
	
	
	public void restoreBrowserPanel(){
		setDividerLocation(getLastDividerLocation());
		setDividerSize(4);
		setLeftComponent(getBrowserPanel());
		
	}
	

	/**
	 * Returns restorePanel, if none exists a new one is built.
	 * RestorePanel is a slim vertical bar that holds the place 
	 * of the minimized fileBrowser. When clicked it restores the
	 * file browser to full size. 
	 * 
	 */
	public JPanel getRestorePanel() {
		if (restorePanel == null) {
			restorePanel = new JPanel();
			restorePanel.setMinimumSize(new Dimension(10,0));
			restorePanel.setBorder(BorderFactory.createEtchedBorder(1));
			restorePanel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					restoreBrowserPanel();
				}
				public void mouseEntered(MouseEvent e) {
					restorePanel.setBackground(Color.LIGHT_GRAY);
				}
				public void mouseExited(MouseEvent e) {
					restorePanel.setBackground(null);
				}
			});
				}
		restorePanel.setBackground(null);
		return restorePanel;
	}
	
	//END GSTURR (file browser support)
	

	
	
	
	//-------------------------------------------
	//	Spreadsheet Properties (get/set)
	//-------------------------------------------
	
	
	public void setShowRowHeader(boolean showRowHeader) {
		if (showRowHeader) {
			spreadsheet.setRowHeaderView(rowHeader);
		} else {
			spreadsheet.setRowHeaderView(null);
		}
		this.showRowHeader = showRowHeader;
	}
	
	public boolean getShowRowHeader(){
		return showRowHeader;
	}
	
	
	public void setShowColumnHeader(boolean showColumnHeader) {
		if (showColumnHeader) {
			spreadsheet.setColumnHeaderView(table.getTableHeader());
		} else {
			spreadsheet.setColumnHeader(null);
		}
		this.showColumnHeader = showColumnHeader;
	}
	
	public boolean getShowColumnHeader(){
		return showColumnHeader;
	}
	

	public void setShowVScrollbar(boolean showVScrollBar) {
		if (showVScrollBar) {
			spreadsheet
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		} else {
			spreadsheet
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		}
		this.showVScrollBar = showVScrollBar;
	}

	public boolean getShowVScrollbar() {
		return showVScrollBar;
	}

	public void setShowHScrollbar(boolean showHScrollBar) {
		if (showHScrollBar) {
			spreadsheet
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		} else {
			spreadsheet
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}
		this.showHScrollBar = showHScrollBar;
	}

	public boolean getShowHScrollbar() {
		return showHScrollBar;
	}

	public void setShowGrid(boolean showGrid) {
		table.setShowGrid(showGrid);
		this.showGrid = showGrid;
	}

	public boolean getShowGrid() {
		return showGrid;
	}
	
	public void setShowToolBar(boolean showToolBar){
		toolBar.setVisible(showToolBar);
		spreadsheetPanel.validate();
		this.showCellFormatToolBar = showToolBar;
	}
	
	public boolean getShowToolBar(){
		return showCellFormatToolBar;
	}
	
	/**
	 * 
	 * get spreadsheet format toolbar 
	 */
	private CellFormatToolBar getCellFormatToolBar(){
		if(toolBar==null){
			toolBar = new CellFormatToolBar(this);
		}
		
		return toolBar;
	}
	
	
	


	// G.Sturr 2010-4-10
	// ==========================================================
	// Handle spreadsheet resize.
	//
	// Adds extra rows and columns to fill the enclosing scrollpane. 
	// This is sometimes needed when rows or columns are resized
	// or the application window is enlarged.
	
	
	/**
	 * Tests if the spreadsheet fits the enclosing scrollpane viewport.
	 * Adds rows or columns if needed to fill the viewport.
	 */
	public void expandSpreadsheetToViewport() {

		if (table.getWidth() < spreadsheet.getWidth()) {

			int newColumns = (spreadsheet.getWidth() - table.getWidth())
					/ table.preferredColumnWidth;
			table.removeComponentListener(this);
			table.setMyColumnCount(table.getColumnCount() + newColumns);
			table.addComponentListener(this);

		}
		if (table.getHeight() < spreadsheet.getHeight()) {
			int newRows = (spreadsheet.getHeight() - table.getHeight())
					/ table.getRowHeight();
			table.removeComponentListener(this);
			tableModel.setRowCount(table.getRowCount() + newRows);
			table.addComponentListener(this);

		}

		// if table has grown after resizing all rows or columns, then select
		// all again
		// TODO --- why doesn't this work:
		/*
		 * if(table.isSelectAll()){ table.selectAll(); }
		 */

	}
	
	// Listener for a resized column or row 

	public void componentResized(ComponentEvent e) {		
		expandSpreadsheetToViewport();
	}
	public void componentHidden(ComponentEvent e) {		
	}

	public void componentMoved(ComponentEvent e) {		
	}

	public void componentShown(ComponentEvent e) {
		
	}
	
	//=============================================
	// END G.Sturr

	public void requestFocus() {
		if (table != null)
			table.requestFocus();
	}

	public boolean hasFocus() {
		if (table == null)
			return false;
		return table.hasFocus()
				|| rowHeader.hasFocus()
				|| table.getTableHeader().hasFocus()
				|| spreadsheet.getCorner(JScrollPane.UPPER_LEFT_CORNER).hasFocus();
	}

	public void focusGained(FocusEvent arg0) {

	}

	public void focusLost(FocusEvent arg0) {
		getTable().repaint();

	}

}


