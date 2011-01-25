package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianConstants;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

public class MyTable extends JTable implements FocusListener 
{
	

	public static final int MAX_CELL_EDIT_STRING_LENGTH = 10;

	public static final int TABLE_CELL_WIDTH = 70;
	public static final int TABLE_CELL_HEIGHT = 21;  //G.Sturr (old height 20) + 1 to stop cell editor clipping
	public static final int DOT_SIZE = 7;
	public static final int LINE_THICKNESS1 = 3;
	public static final int LINE_THICKNESS2 = 2;
	public static final Color SELECTED_BACKGROUND_COLOR = new Color(214, 224, 245);
	public static final Color SELECTED_BACKGROUND_COLOR_HEADER = Color.lightGray;
	public static final Color BACKGROUND_COLOR_HEADER = new Color(232, 238, 247);
	public static final Color TABLE_GRID_COLOR = Color.gray;
	public static final Color SELECTED_RECTANGLE_COLOR = Color.BLUE;

	private static final long serialVersionUID = 1L;
	
	protected Kernel kernel;
	protected Application app;
	protected MyCellEditor editor;
	protected MyCellEditorBoolean editorBoolean;
	protected MyCellEditorButton editorButton;
	protected MyCellEditorList editorList;
	
	
	protected RelativeCopy relativeCopy;
	protected CopyPasteCut copyPasteCut;
	protected MyColumnHeaderRenderer columnHeader;
	protected SpreadsheetView view;
	protected DefaultTableModel tableModel;
	private CellRangeProcessor crProcessor;
	private MyTable table;
	private MyTableColumnModelListener columnModelListener;
	//G.Sturr 2010-4-4
	private CellFormat formatHandler;
	
	
	
	//G.STURR: 2010-1-29
	/**
	 * All currently selected cell ranges are held in this list.
	 * Cell ranges are added when selecting with ctrl-down. 
	 * The first element is the most recently selected cell range. 	 
	 */
	public ArrayList<CellRange> selectedCellRanges;
	
	
	// These keep track of internal selection using actual ranges and do not 
	// use -1 flags for row and column.
	// Note: selectedCellRanges.get(0) gives the same selection but uses -1 flags
	protected int minSelectionRow = -1;
	protected int maxSelectionRow = -1;
	protected int minSelectionColumn = -1;
	protected int maxSelectionColumn = -1;
	public boolean[] selectedColumns;
	
	
	// Used for rendering headers with ctrl-select
	protected HashSet selectedColumnSet = new HashSet();
	protected HashSet selectedRowSet = new HashSet();
	
	
	// Selection type
	public static final int CELL_SELECT = 0;
	public static final int ROW_SELECT = 1;
	public static final int COLUMN_SELECT = 2;
	private int selectionType = CELL_SELECT;
	
	private boolean doShowDragHandle = true;
	private Color selectionRectangleColor = SELECTED_RECTANGLE_COLOR ;
	
	
	// Dragging vars
	protected boolean isDragingDot = false;
	protected int dragingToRow = -1;
	protected int dragingToColumn = -1;
	protected boolean isOverDot = false;
	
	// Keep track of ctrl-down. This is needed in some
	// selection methods that do not receive key events.
	protected boolean metaDown = false;
	
	
	// G.Sturr 2010-3-29
	// Cells to be resized on next repaint are put in these HashSets.
	// A cell is added to a set when editing is done. The cells are removed
	// after a repaint in MyTable.
	
	public static HashSet<Point> cellResizeHeightSet = new HashSet<Point>();
	public static HashSet<Point> cellResizeWidthSet = new HashSet<Point>();
	
	// END G.Sturr
	
	
	private ArrayList<Point> adjustedRowHeights = new ArrayList<Point>();
	private boolean doRecordRowHeights = true;

	public int preferredColumnWidth = TABLE_CELL_WIDTH; //G.Sturr 2010-4-10 
	
	// G.Sturr 2010-6-4
	// Collection of cells that contain geos that can be edited with one click,
	// e.g. booleans, buttons, lists
	protected HashMap<Point,GeoElement> oneClickEditMap = new HashMap<Point,GeoElement>();

	

	
	
	//============================================================
	// Construct table
	//
	
	public MyTable(SpreadsheetView view, DefaultTableModel tableModel) {
		super(tableModel);

		this.tableModel = tableModel;
		this.view = view;
		app = view.getApplication();
		kernel = app.getKernel();
		table = this;
		
		//G.Sturr 2009-11-15
		selectedCellRanges = new ArrayList<CellRange>();
		selectedCellRanges.add(new CellRange(this));

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//setAutoscrolls(true);

		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setCellSelectionEnabled(true);
		// set cell size and column header
		setRowHeight(TABLE_CELL_HEIGHT);
		columnHeader = new MyColumnHeaderRenderer();
		columnHeader.setPreferredSize(new Dimension(preferredColumnWidth, TABLE_CELL_HEIGHT));
		
		for (int i = 0; i < getColumnCount(); ++ i) {
			getColumnModel().getColumn(i).setHeaderRenderer(columnHeader);
			getColumnModel().getColumn(i).setPreferredWidth(preferredColumnWidth);
		}
		// add renderer & editor
		
		//G.Sturr 2010-4-4: add format handling to cell renderer
		setDefaultRenderer(Object.class, new MyCellRenderer(app, view, this.getCellFormatHandler()));
		
		//setDefaultRenderer(Object.class, new MyCellRenderer(app));
		editorButton = new MyCellEditorButton();
		editorBoolean = new MyCellEditorBoolean(kernel);
		editorList = new MyCellEditorList();
		editor = new MyCellEditor(kernel);
		setDefaultEditor(Object.class, editor);

		// set selection colors
		setSelectionBackground( SELECTED_BACKGROUND_COLOR);
		setSelectionForeground(Color.BLACK);
		
		// setup mouse listeners
		MouseListener[] mouseListeners = getMouseListeners();
		addMouseListener(new MouseListener1());
		for (int i = 0; i < mouseListeners.length; ++ i) {
			removeMouseListener(mouseListeners[i]);
			addMouseListener(mouseListeners[i]);
		}

		// setup mouse motion listeners
		MouseMotionListener[] mouseMotionListeners = getMouseMotionListeners();
		addMouseMotionListener(new MouseMotionListener1());
		for (int i = 0; i < mouseMotionListeners.length; ++ i) {
			removeMouseMotionListener(mouseMotionListeners[i]);
			addMouseMotionListener(mouseMotionListeners[i]);
		}
		// key listener
		KeyListener[] defaultKeyListeners = getKeyListeners();
		for (int i = 0; i < defaultKeyListeners.length; ++ i) {
			removeKeyListener(defaultKeyListeners[i]);
		}
		addKeyListener(new KeyListener1());

		// setup selection listener
		//TODO 
		//These listeners are no longer needed.
		//getSelectionModel().addListSelectionListener(new RowSelectionListener());
		//getColumnModel().getSelectionModel().addListSelectionListener(new ColumnSelectionListener());
		//getColumnModel().getSelectionModel().addListSelectionListener(columnHeader);
		
		// table model listener
		tableModel.addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) {
				// force rowHeader redraw when a new row is added (after drag down or arrow down)
				if(e.getType()==TableModelEvent.INSERT){
					getView().updateRowHeader();
				}
			}

		});
		
		
		// relative copy
		relativeCopy = new RelativeCopy(this, kernel);
		copyPasteCut = new CopyPasteCut(this, kernel);
		
		// column header
		this.getTableHeader().setFocusable(true);
		this.getTableHeader().addMouseListener(new MouseListener2());
		this.getTableHeader().addMouseMotionListener(new MouseMotionListener2());
		this.getTableHeader().addKeyListener(new KeyListener2());
		//
		this.getTableHeader().setReorderingAllowed(false);
		setAutoCreateColumnsFromModel(false);

		// visual appearance 	 
		setShowGrid(true); 	 
		setGridColor(TABLE_GRID_COLOR); 	 

		// - see ticket #135
		 addFocusListener(this);
		
		// editing 	 
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		
		//G.Sturr 2010-4-10
		columnModelListener = new MyTableColumnModelListener();
		getColumnModel().addColumnModelListener( columnModelListener); 
		
		// set first cell active 
		// needed in case spreadsheet selected with ctrl-tab rather than mouse click
		//changeSelection(0, 0, false, false);
		
		
		
	}
	
	//==============================================================
	
	
	/**
	 * Returns parent SpreadsheetView for this table
	 */
	public SpreadsheetView getView() {
		return view;		
	}

	
	/**
	 * Returns CellRangeProcessor for this table.
	 * If none exists, a new one is created.
	 */
	public CellRangeProcessor getCellRangeProcessor() {
    	if (crProcessor == null)
    		crProcessor = new CellRangeProcessor(this);
    	return crProcessor;
    }
	
	
	/**
	 * Returns CellFormat helper class for this table.
	 * If none exist, a new one is created.
	 */
	public CellFormat getCellFormatHandler(){
		if(formatHandler == null)
			formatHandler = new CellFormat(this);
		return formatHandler;
	}
	
	
	
	
	/**
	 * Appends columns to the table if newColumnCount is larger than 
	 * current number of columns. 
	 */
	public void setMyColumnCount(int newColumnCount) {	
		int oldColumnCount = tableModel.getColumnCount();		
		if (newColumnCount <= oldColumnCount)
			return;

		// add new columns to table			
		for (int i = oldColumnCount; i < newColumnCount; ++i) {
			TableColumn col = new TableColumn(i);
			col.setHeaderRenderer(columnHeader);
			col.setPreferredWidth(preferredColumnWidth);
			addColumn(col);
		}	
		tableModel.setColumnCount(newColumnCount);
		
		//addColumn destroys custom row heights, so we must reset them
		resetRowHeights();
		
	}


	@Override
	public TableCellEditor getCellEditor(int row, int column){
		
		Point p = new Point(column, row);
		if (view.allowSpecialEditor() && oneClickEditMap.containsKey(p) 
				&& kernel.getAlgebraStyle()==Kernel.ALGEBRA_STYLE_VALUE){
			
			switch (oneClickEditMap.get(p).getGeoClassType()){
			case GeoElement.GEO_CLASS_BOOLEAN:
				return editorBoolean;
			case GeoElement.GEO_CLASS_BUTTON:
				return editorButton;
			case GeoElement.GEO_CLASS_LIST:
				return editorList;					
			}
		}
		return editor;
	}
	
	
	/**
	 * sets requirement that commands entered into cells must start with "="
	 */
	public void setEqualsRequired(boolean isEqualsRequired){
		editor.setEqualsRequired(isEqualsRequired);
	}
	
	/**
	 * gets flag for requirement that commands entered into cells must start with "="
	 */
	public boolean isEqualsRequired(){
		return view.isEqualsRequired();
	}
	
	
	
	
	
	//===============================================================
	//                   Selection
	//===============================================================
	
	
	
	//G.STURR 2009-11-15
	
	/**
	 * JTable does not support non-contiguous cell selection. It treats ctrl-down 
	 * cell selection as if it was shift-extend. To prevent this behavior the JTable 
	 * changeSelection method is overridden here. 
	 */
	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
		//if(Application.getControlDown()) 
			//super.changeSelection(rowIndex, columnIndex, false, false);	
		//else
		
		//G.Sturr 2010-7-10: force column selection
		if(view.isColumnSelect()){
			setColumnSelectionInterval(columnIndex, columnIndex);
		}
		// END G.Sturr 

		
			super.changeSelection(rowIndex, columnIndex, toggle, extend);
		// let selectionChanged know about a change in single cell selection
		selectionChanged();
	}
	
	
	@Override
	public void selectAll(){		
		setSelectionType(CELL_SELECT);
		this.setAutoscrolls(false);
		changeSelection(0,0,false,false);
		changeSelection(getRowCount()-1,getColumnCount()-1, false, true);
		setSelectAll(true);
	//	this.scrollRectToVisible(getCellRect(0,0,true));
		this.setAutoscrolls(true);
		//setRowSelectionInterval(0, getRowCount()-1);
		//getColumnModel().getSelectionModel().setSelectionInterval(0, getColumnCount()-1);
		//selectionChanged();
		//this.getSelectAll();
		
		
	}
	
   
   //G.STURR 2010-1-29
	/**
	 * This handles all selection changes for the table.
	 */
	public void selectionChanged() {
		
	
		
		// create a cell range object to store
		// the current table selection 
		
		CellRange newSelection = new CellRange(this);
	
		if(view.isTraceDialogVisible()){
			
			newSelection = view.getTraceSelectionRange(
				getColumnModel().getSelectionModel().getAnchorSelectionIndex(), 
				getSelectionModel().getAnchorSelectionIndex());
			
			scrollRectToVisible(getCellRect(newSelection.getMinRow(), newSelection.getMaxColumn(),true));
		
		}else{

			switch (selectionType) {
			
				case CELL_SELECT:
				newSelection.setCellRange(
					getColumnModel().getSelectionModel().getAnchorSelectionIndex(), 
					getSelectionModel().getAnchorSelectionIndex(), 
					getColumnModel().getSelectionModel().getLeadSelectionIndex(),
					getSelectionModel().getLeadSelectionIndex());
				break;
				
				case ROW_SELECT:
				newSelection.setCellRange(
					-1, 
					getSelectionModel().getAnchorSelectionIndex(), 
					-1, 
					getSelectionModel().getLeadSelectionIndex());
				break;
				
				case COLUMN_SELECT:
				newSelection.setCellRange(
					getColumnModel().getSelectionModel().getAnchorSelectionIndex(), 
					-1, 
					getColumnModel().getSelectionModel().getLeadSelectionIndex(), 
					-1);
				break;
			}
				
		}
  
		// newSelection.debug();
		/*
		// return if it is not really a new cell 
		if(selectedCellRanges.size()>0 && newSelection.equals(selectedCellRanges.get(0))) 
				return;
		*/
		
		
		// update the selection list
		
		if (!Application.getControlDown()) {
			selectedCellRanges.clear();
			selectedColumnSet.clear();
			selectedRowSet.clear();
			selectedCellRanges.add(0, newSelection);
			
		} else { //ctrl-select
			
			/*
			// return if we have already ctrl-selected this range
			for (CellRange cr : selectedCellRanges) {
				if (cr.equals(newSelection)){
					System.out.println("reutrned");
					return;
				}
			}
			*/
			
			
			// handle dragging  
			if (selectedCellRanges.get(0).hasSameAnchor(newSelection)) {
				selectedCellRanges.remove(0);
			}
			
			// add the selection to the list
			selectedCellRanges.add(0, newSelection);
		}
		
	
		// update sets of selected rows/columns (used for rendering in the headers)
		if(selectionType == COLUMN_SELECT)
			for (int i = newSelection.getMinColumn(); i<= newSelection.getMaxColumn(); i++)
				selectedColumnSet.add(i);
		
		if(selectionType == ROW_SELECT)
			for (int i = newSelection.getMinRow(); i<= newSelection.getMaxRow(); i++)
				selectedRowSet.add(i);
		
		
		// update internal selection variables
		newSelection.setActualRange();  
		minSelectionColumn = newSelection.getMinColumn();
		maxSelectionColumn = newSelection.getMaxColumn();
		minSelectionRow = newSelection.getMinRow();
		maxSelectionRow = newSelection.getMaxRow();
		
		//newSelection.debug();
		//printSelectionParameters();
		
		// update the geo selection list
		ArrayList list = new ArrayList();
		for (int i = 0; i < selectedCellRanges.size(); i++) {
			list.addAll(0,(selectedCellRanges.get(i)).toGeoList());
		}
		app.setSelectedGeos(list);
		
		//System.out.println("------------------");
		//for (CellRange cr: selectedCellRanges)cr.debug();
		 
		view.notifySpreadsheetSelectionChange();
		
		repaint();
		
		
		/*  ------------  old code

		protected void selectionChanged() {
		if (selectionChangedNonResponsive) return;
		selectionTime = System.currentTimeMillis();
		int[] cols = this.getSelectedColumns();
		int[] rows = this.getSelectedRows();
		ArrayList list = new ArrayList();
		if (cols != null && rows != null) {
			if (cols.length != 0 && rows.length != 0) {
				for (int i = 0; i < cols.length; ++ i) {
					for (int j = 0; j < rows.length; ++ j) {
						GeoElement geo = RelativeCopy.getValue(this, cols[i], rows[j]);
						if (geo != null) {
							list.add(geo);							
						}
					}
				}
			}
			else if (rows.length == 0) {
				for (int i = 0; i < cols.length; ++ i) {
					for (int j = 0; j < tableModel.getRowCount(); ++ j) {
						GeoElement geo = RelativeCopy.getValue(this, cols[i], j);
						if (geo != null) {
							list.add(geo);							
						}
					}
				}
			}
			else if (cols.length == 0) {				
				for (int i = 0; i < tableModel.getColumnCount(); ++ i) {
					for (int j = 0; j < rows.length; ++ j) {
						GeoElement geo = RelativeCopy.getValue(this, i, rows[j]);
						if (geo != null) {
							list.add(geo);							
						}
					}
				}
			}
		}
		app.setSelectedGeos(list);		
		*/
		
	}

	
	
	
	private void printSelectionParameters(){
		System.out.println("----------------------------------");
		System.out.println("minSelectionColumn = " + minSelectionColumn );
		System.out.println("maxSelectionColumn = " + maxSelectionColumn );
		System.out.println("minSelectionRow = " + minSelectionRow );
		System.out.println("maxSelectionRow = " + maxSelectionRow );
		System.out.println("----------------------------------");
	}
	
	/**
	 * Sets the initial selection parameters to a single cell. Does this without
	 * calling changeSelection, so it should only be used at startup.
	 */
	public void setInitialCellSelection(int row, int column) {
		
		setSelectionType(CELL_SELECT);
		
		if (column == -1) column = 0;
		if (row == -1) row = 0;
		minSelectionColumn = column;
		maxSelectionColumn = column;
		minSelectionRow = row;
		maxSelectionRow = row;
		
		getColumnModel().getSelectionModel().setSelectionInterval(column, column);
		getSelectionModel().setSelectionInterval(row, row);
	}


	
	
	
	/*
	public void setSelectionRectangle(CellRange cr){

		if (cr == null){
			this.minSelectionColumn = -1;
			this.minSelectionRow = -1;
			this.maxSelectionColumn = -1;
			this.maxSelectionRow = -1;
			return;
		}
		
		this.minSelectionColumn = cr.getMinColumn();
		this.minSelectionRow = cr.getMinRow();
		this.maxSelectionColumn = cr.getMaxColumn();
		this.maxSelectionRow = cr.getMaxRow();
		this.repaint();
		
	}
	
	*/
	
	/*
	public void setTraceSelectionRectangle() {
		
		if (view.getSelectedTrace() == null) {
			cellFrame = null;
		} else {
		
		int c1 = view.getSelectedTrace().traceColumn1;
		int r1 = view.getSelectedTrace().traceRow1;
		int c2 = view.getSelectedTrace().traceColumn2;
		int r2 = view.getSelectedTrace().doRowLimit ? view.getSelectedTrace().traceRow2 : getRowCount();
	
		Point point1 = getPixel(c1,r1, true);
		Point point2 = getPixel(c2,r2, false);
			
		cellFrame.setFrameFromDiagonal(point1, point2);
			
		// scroll to upper left corner of rectangle
		scrollRectToVisible(table.getCellRect(r1,c1, true));
			
		}
		repaint();

	}
	
	*/
	
	
	public void setSelection(int c1, int r1, int c2, int r2){
		
		CellRange cr = new CellRange(this,c1,r1,c2,r2);
		ArrayList<CellRange> list = new ArrayList<CellRange>();
		list.add(cr);
		setSelection(cr);
		//setSelection(list, color, doShowDragHandle);
	}
	
	public void setSelection(CellRange cr) {

		if (cr == null || cr.isEmptyRange()) {
			getSelectionModel().clearSelection();

		} else {

			this.setAutoscrolls(false);

			if (cr.isRow()) {
				setRowSelectionInterval(cr.getMinRow(), cr.getMaxRow());
				
			} else if (cr.isColumn()) {
				setColumnSelectionInterval(cr.getMinColumn(), cr.getMaxColumn());
				
			} else {
				changeSelection(cr.getMinRow(), cr.getMinColumn(), false, false);
				changeSelection(cr.getMaxRow(), cr.getMaxColumn(), false, true);
			}

			// scroll to upper left corner of rectangle
			this.setAutoscrolls(true);
			scrollRectToVisible(getCellRect(cr.getMinRow(), cr.getMinColumn(),true));
		}

	}
	
	
	
	/*
	public void setSelection(ArrayList<CellRange> selection){

		selectionRectangleColor = (color == null) ? SELECTED_RECTANGLE_COLOR : color;
		
		 // rectangle not drawn correctly without handle ... needs fix 
		this.doShowDragHandle = true;  // doShowDragHandle;
		
		if (selection == null) {
			
			setSelectionType(COLUMN_SELECT);
			
			// clear the selection visuals and the deselect geos from here
			//TODO: this should be handled by the changeSelection() method
			selectedColumnSet.clear();
			selectedRowSet.clear();
			this.minSelectionColumn = -1;
			this.minSelectionRow = -1;
			this.maxSelectionColumn = -1;
			this.maxSelectionRow = -1;
			app.setSelectedGeos(null);
			//setSelectionType(COLUMN_SELECT);
			view.repaint();
			setSelectionType(CELL_SELECT);
			
		} else {

			for (CellRange cr : selection) {
				
				this.setAutoscrolls(false);
				
				if (cr.isRow()) {
					setRowSelectionInterval(cr.getMinRow(), cr.getMaxRow());
				} else if (cr.isColumn()) {
					setColumnSelectionInterval(cr.getMinColumn(), cr
							.getMaxColumn());
				} else {
					changeSelection(cr.getMinRow(), cr.getMinColumn(), false,
							false);
					changeSelection(cr.getMaxRow(), cr.getMaxColumn(), false,
							true);
				}
				
				// scroll to upper left corner of rectangle
				
				this.setAutoscrolls(true);
				scrollRectToVisible(getCellRect(cr.getMinRow(), cr.getMinColumn(), true));
			}
			
				
		}

	}
	
	*/
	
	
	public void setSelectionType(int selType) {
		
		if(view.isColumnSelect()){
			selType = COLUMN_SELECT;
		}
		
		switch (selType) {

		case CELL_SELECT:
			setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			setColumnSelectionAllowed(true);
			setRowSelectionAllowed(true);
			break;

		case ROW_SELECT:
			setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			setColumnSelectionAllowed(false);
			setRowSelectionAllowed(true);
			break;

		case COLUMN_SELECT:
			setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			setColumnSelectionAllowed(true);
			setRowSelectionAllowed(false); 
			break;
			
		}
	
		this.selectionType = selType;
		

	}
	
	public int getSelectionType(){
		return selectionType;
	}
	

	//G.STURR 2010-1-29
	// By adding a call to selectionChanged in JTable's setRowSelectionInterval 
	// and setColumnSelectionInterval methods, selectionChanged becomes 
	// the sole handler for selection events.
	@Override
	public void setRowSelectionInterval(int row0, int row1) {
		super.setRowSelectionInterval(row0, row1);
		selectionChanged(); 
		setSelectionType(ROW_SELECT);
		
	}
	@Override
	public void setColumnSelectionInterval(int col0, int col1) {
		super.setColumnSelectionInterval(col0, col1);
		setSelectionType(COLUMN_SELECT);
		selectionChanged(); 
		
	}
	//END GSTURR
	
	 
	//G.STURR 2010-1-9
	private boolean isSelectAll = false;
	public boolean getSelectAll() {	
		return isSelectAll;
		/*
		if (minSelectionColumn == 0 && maxSelectionColumn == getColumnCount()-1 && minSelectionRow == 0 
				&& maxSelectionRow == getRowCount()-1)
			return true;
		
		return false;
		*/
	}
	public void setSelectAll(boolean isSelectAll) {	
		this.isSelectAll = isSelectAll;
	}
	
	
	public ArrayList<Integer> getSelectedColumnsList(){
		
		ArrayList<Integer> columns = new ArrayList<Integer>();

		for(CellRange cr:this.selectedCellRanges){
			for(int c = cr.getMinColumn(); c <= cr.getMaxColumn(); ++c ){
				if(!columns.contains(c)) columns.add(c);
			}
		}	
		return columns;
	}
	
	
	@Override
	public int[] getSelectedColumns(){
		
		ArrayList<Integer> columns = getSelectedColumnsList();
		int[] ret = new int[columns.size()];
		for (int c = 0; c < columns.size(); c++)
			ret[c] = columns.get(c);
		
		return ret;
	}
	
	
	//===============================================================
	//                   Paint 
	//===============================================================
	
	
	
	public Color getSelectionRectangleColor(){	
		return selectionRectangleColor;	
	}
	
	public void setSelectionRectangleColor(Color color){	
		selectionRectangleColor = color;	
	}
	
	
	
	protected Point getPixel(int column, int row, boolean min) {
		if (column < 0 || row < 0) {
			return null;
		}
		if (min && column == 0 && row == 0) {
			return new Point(0, 0);
		}
		
		//G.Sturr 2009-9-23
		//Replace old code with JTable method to get pixel location
		Rectangle cellRect = getCellRect(row, column, false);
		if (min)return new Point(cellRect.x, cellRect.y);
		else return new Point(cellRect.x + cellRect.width , cellRect.y + cellRect.height);
		
		
		//Old code -- adds extra border pixel when min false
		/*
		int x = 0;
		int y = 0;
		if (! min) {
			++ column;
			++ row;
		}
		for (int i = 0; i < column; ++ i) {
			x += getColumnModel().getColumn(i).getWidth();
		}
		int rowHeight = getRowHeight();
		for (int i = 0; i < row; ++ i) {
			y += rowHeight;
		}
		return new Point(x, y);
		*/ 
	}

	protected Point getMinSelectionPixel() {
		return getPixel(minSelectionColumn, minSelectionRow, true);
	}

	protected Point getMaxSelectionPixel() {
		return getPixel(maxSelectionColumn, maxSelectionRow, false);
	}

	public Point getIndexFromPixel(int x, int y) {
		if (x < 0 || y < 0) return null;
		int indexX = -1;
		int indexY = -1;
		for (int i = 0; i < getColumnCount(); ++ i) {
			Point point = getPixel(i, 0, false);
			if (x < point.getX()) {
				indexX = i;
				break;
			}
		}
		if (indexX == -1) {
			return null;
		}
		for (int i = 0; i < getRowCount(); ++ i) {
			Point point = getPixel(0, i, false);
			if (y < point.getY()) {
				indexY = i;
				break;
			}
		}
		if (indexY == -1) {
			return null;
		}
		return new Point(indexX, indexY);
	}

	
	private Rectangle cellFrame;
	
	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);

		/* G.Sturr 2009-9-30: removed so we can draw row/column selection frames
		if (MyTable.this.getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_INTERVAL_SELECTION) {
			return;
		}
		*/

		
		Graphics2D g2 = (Graphics2D)graphics;

		if(cellFrame != null){
			g2.setColor(Color.GRAY);
			g2.setStroke(new BasicStroke(3));
			g2.draw(cellFrame);
		}
		
		/*
		for(GeoElement geo: view.getTraceManager().getTraceGeoList()){
			tSet = view.getTraceManager().getTraceSettings(geo);
			int row = tSet.tracingRow != -1 ? tSet.tracingRow-1 : tSet.traceRow2;
			Point point1 = getPixel(tSet.traceColumn1, row, true);
			Point point2 = getPixel(tSet.traceColumn2, row, false);
			cellFrame.setFrameFromDiagonal(point1, point2);
			g2.draw(cellFrame);
		}
		*/	
		/*
		//Don't draw anything if the view does not have the focus	
		if(!view.hasFocus()) {
			return;
		}
		*/
		
		//draw special dragging frame for cell editor
		if (isDragging2) {
			Point point1 = getPixel(minColumn2, minRow2, true);
			Point point2 = getPixel(maxColumn2, maxRow2, false);
			int x1 = (int)point1.getX();
			int y1 = (int)point1.getY();
			int x2 = (int)point2.getX();
			int y2 = (int)point2.getY();
			graphics.setColor(Color.GRAY);
			//Application.debug(x1 + "," + y1 + "," + x2 + "," + y2);
			graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS1);
			graphics.fillRect(x1, y1, LINE_THICKNESS1, y2 - y1);
			graphics.fillRect(x1, y2 - LINE_THICKNESS1, x2 - x1, LINE_THICKNESS1);
			graphics.fillRect(x2 - LINE_THICKNESS1, y1, LINE_THICKNESS1, y2 - y1);
		}
		
		// draw dragging frame
		if (dragingToRow != -1 && dragingToColumn != -1) {
			/*
			Application.debug("minSelectionRow = " + minSelectionRow);
			Application.debug("minSelectionColumn = " + minSelectionColumn);
			Application.debug("maxSelectionRow = " + maxSelectionRow);
			Application.debug("maxSelectionColumn = " + maxSelectionColumn);
			Application.debug("dragingToRow = " + dragingToRow);
			Application.debug("dragingToColumn = " + dragingToColumn);
			/**/
			// -|1|-
			// 2|-|3
			// -|4|-
			graphics.setColor(Color.gray);
			if (dragingToColumn < minSelectionColumn) { // 2
				Point point1 = getPixel(dragingToColumn, minSelectionRow, true);
				Point point2 = getPixel(minSelectionColumn - 1, maxSelectionRow, false);
				int x1 = (int)point1.getX();
				int y1 = (int)point1.getY();
				int x2 = (int)point2.getX();
				int y2 = (int)point2.getY();
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS1);
				graphics.fillRect(x1, y1, LINE_THICKNESS1, y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS1, x2 - x1, LINE_THICKNESS1);
			}
			else if (dragingToRow > maxSelectionRow) { // 4
				Point point1 = getPixel(minSelectionColumn, maxSelectionRow + 1, true);
				Point point2 = getPixel(maxSelectionColumn, dragingToRow, false);
				int x1 = (int)point1.getX();
				int y1 = (int)point1.getY();
				int x2 = (int)point2.getX();
				int y2 = (int)point2.getY();
				graphics.fillRect(x1, y1, LINE_THICKNESS1, y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS1, x2 - x1, LINE_THICKNESS1);
				graphics.fillRect(x2 - LINE_THICKNESS1, y1, LINE_THICKNESS1, y2 - y1);
			}
			else if (dragingToRow < minSelectionRow) { // 1
				Point point1 = getPixel(minSelectionColumn, dragingToRow, true);
				Point point2 = getPixel(maxSelectionColumn, minSelectionRow - 1, false);
				int x1 = (int)point1.getX();
				int y1 = (int)point1.getY();
				int x2 = (int)point2.getX();
				int y2 = (int)point2.getY();
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS1);
				graphics.fillRect(x1, y1, LINE_THICKNESS1, y2 - y1);
				graphics.fillRect(x2 - LINE_THICKNESS1, y1, LINE_THICKNESS1, y2 - y1);
			}
			else if (dragingToColumn > maxSelectionColumn) { // 3
				Point point1 = getPixel(maxSelectionColumn + 1, minSelectionRow, true);
				Point point2 = getPixel(dragingToColumn, maxSelectionRow, false);
				int x1 = (int)point1.getX();
				int y1 = (int)point1.getY();
				int x2 = (int)point2.getX();
				int y2 = (int)point2.getY();
				graphics.fillRect(x2 - LINE_THICKNESS1, y1, LINE_THICKNESS1, y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS1, x2 - x1, LINE_THICKNESS1);
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS1);
			}
		}
		
		// draw dragging dot
		Point pixel1 = getMaxSelectionPixel();
		if (doShowDragHandle && pixel1 != null && ! editor.isEditing()) {
			
			//(G.Sturr 20099-12) Highlight the dragging dot if mouseover 
			if (isOverDot) 
				{graphics.setColor(Color.gray);}
			else
				//{graphics.setColor(Color.BLUE);}
				{graphics.setColor(selectionRectangleColor);}
			//(G.Sturr)
			
			int x = (int)pixel1.getX() - (DOT_SIZE + 1) / 2;
			int y = (int)pixel1.getY() - (DOT_SIZE + 1) / 2;
			graphics.fillRect(x, y, DOT_SIZE, DOT_SIZE);
		}
		

		if (minSelectionRow != -1 && maxSelectionRow != -1 && minSelectionColumn != -1 && maxSelectionColumn != -1) {
			Point min = this.getMinSelectionPixel();
			Point max = this.getMaxSelectionPixel();
			int x1 = (int)min.getX();
			int y1 = (int)min.getY();
			int x2 = (int)max.getX();
			int y2 = (int)max.getY();
			
			//graphics.setColor(Color.BLUE);
			graphics.setColor(selectionRectangleColor);
			
			// draw frame around current selection
			// G.Sturr 2009-9-23 adjusted parameters to work with getPixel fix
			if (! editor.isEditing()) {
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS2);
				graphics.fillRect(x1, y1, LINE_THICKNESS2, y2 - y1);
				graphics.fillRect(x2 - LINE_THICKNESS2, y1, LINE_THICKNESS2, y2 - y1 - DOT_SIZE / 2 - 1);		
				graphics.fillRect(x1, y2 - LINE_THICKNESS2, x2 - x1 - DOT_SIZE / 2 - 1, LINE_THICKNESS2);
			}
			// draw small frame around current editing cell 
			else {
				x1 -= LINE_THICKNESS2-1;
				x2 += LINE_THICKNESS2-1;
				y1 -= LINE_THICKNESS2-1;
				y2 += LINE_THICKNESS2-1;
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS2);
				graphics.fillRect(x1, y1, LINE_THICKNESS2, y2 - y1);
				graphics.fillRect(x2 - LINE_THICKNESS2, y1, LINE_THICKNESS2, y2 - y1);		
				graphics.fillRect(x1, y2 - LINE_THICKNESS2, x2 - x1, LINE_THICKNESS2);
			}
		}
		
		
		//G.Sturr 2010-4-2
		// After rendering the LaTeX image for a geo, update the row height 
		// with the preferred size set by the renderer.
		
		resizeMarkedCells();
		
	}
	
	
	
	
	/**
	 * Starts in-cell editing for cells with short editing strings. For strings longer
	 * than MAX_CELL_EDIT_STRING_LENGTH, the redefine dialog is shown. 
	 * Also prevents fixed cells from being edited. 
	 */
	@Override
	public boolean editCellAt(int row, int col) {
		Object ob = getValueAt(row, col);
		
		if (ob instanceof GeoElement) {
			GeoElement geo = (GeoElement) ob;
			
			if (!geo.isFixed()) {
				if (!geo.isGeoText() && 
						editor.getEditorInitString(geo).length() > MAX_CELL_EDIT_STRING_LENGTH) {
					app.getGuiManager().showRedefineDialog(geo, false);
					return true;
				}
	
				if (geo.isGeoText() && ((GeoText)geo).isLaTeX() ) {
					app.getGuiManager().showRedefineDialog(geo, true);
					return true;
				}
			}

			
			
		}
		
	
		// STANDARD case: in cell editing
		return super.editCellAt(row, col);
	}
	



	protected String name0;
	protected String prefix0, postfix0;
	protected boolean isDragging2 = false;
	protected int minColumn2 = -1;
	protected int maxColumn2 = -1;
	protected int minRow2 = -1;
	protected int maxRow2 = -1;


	protected class MouseListener1 implements MouseListener
	{

		public void mouseClicked(MouseEvent e) {	

			boolean doubleClick = (e.getClickCount() != 1);

			Point point = getIndexFromPixel(e.getX(), e.getY());
			if (point != null) {

				if (doubleClick) {
					
					//G.Sturr added 2009-9-15
					// auto-fill down if dragging dot is double-clicked
					if(isOverDot) {
						handleAutoFillDown();
						return;
					}  
					// 
					//otherwise, doubleClick edits cell
					
					if(!oneClickEditMap.containsKey(point)){
						allowEditing = true;
						editCellAt(getSelectedRow(), getSelectedColumn()); 

						// workaround, see
						// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4192625
						final JTextComponent f = (JTextComponent)getEditorComponent();
						if (f != null) {
							f.requestFocus();
							f.getCaret().setVisible(true);
						}

						allowEditing = false;
					}
				}
			}

			if (editor.isEditing()) {
				String text = editor.getEditingValue();
				if (text.startsWith("=")) {
					point = getIndexFromPixel(e.getX(), e.getY());
					if (point != null) {
						int column = (int)point.getX();
						int row = (int)point.getY();
						GeoElement geo = RelativeCopy.getValue(MyTable.this, column, row);
						if (geo != null) {
							e.consume();
						}
					}
				}	
				name0 = null;
				prefix0 = null;
				isDragging2 = false;
				repaint();
			}
			else if (app.getMode() != EuclidianConstants.MODE_SELECTION_LISTENER) {
				int row = rowAtPoint(e.getPoint());
				int col = columnAtPoint(e.getPoint());
				GeoElement geo = (GeoElement) getModel().getValueAt(row, col);	
				// let euclidianView know about the click
				app.getEuclidianView().clickedGeo(geo, e);
			}
		
//			else
//			{ // !editor.isEditing()
//				int row = rowAtPoint(e.getPoint());
//				int col = columnAtPoint(e.getPoint());
//				GeoElement geo = (GeoElement) getModel().getValueAt(row, col);			
//				
//				// copy description into input bar when a cell is clicked on
//				copyDefinitionToInputBar(geo);
//				selectionChanged();	
//			}
		}				

		//G. Sturr added 2009-9-18
		// auto fill down on dragging dot double-click
		public void handleAutoFillDown() {
			int col = getSelectedColumn();
			int row = maxSelectionRow;
			if(tableModel.getValueAt(row,col) != null) {									
				// count nonempty cells below selection 
				// if no cells below, count left ... if none on the left, count right
				while (row < getRowCount() - 1 && tableModel.getValueAt(row+1, col) != null) row++;
				if ( row - maxSelectionRow == 0 && col > 0) 
					while (row < getRowCount() - 1 && tableModel.getValueAt(row+1, col-1) != null) row++;
				if (row - maxSelectionRow == 0 && maxSelectionColumn <= getColumnCount()-1 )
					while ( row < getRowCount() - 1 && tableModel.getValueAt(row+1, maxSelectionColumn + 1) != null) row++;
				int rowCount = row - maxSelectionRow;
				
				// now fill down
				if (rowCount != 0){
					boolean succ = relativeCopy.doCopy(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow,
							minSelectionColumn, maxSelectionRow + 1, maxSelectionColumn, maxSelectionRow + rowCount);
					if (succ) app.storeUndoInfo();		
				}
				isDragingDot = false;
			}
		}
		
		
		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			boolean rightClick = Application.isRightClick(e); 
			
			// tell selection listener about click on GeoElement
			if (!rightClick && app.getMode() == EuclidianConstants.MODE_SELECTION_LISTENER) {
				int row = rowAtPoint(e.getPoint());
				int col = columnAtPoint(e.getPoint());
				GeoElement geo = (GeoElement) getModel().getValueAt(row, col);
				
				// double click or empty geo
				if (e.getClickCount() == 2 || geo == null) {
					requestFocusInWindow();
				}
				else {					
					// tell selection listener about click
					app.geoElementSelected(geo, false);
					e.consume();
					return;
				}
			}					

			if (!rightClick) {
				
				if(getSelectionType() != CELL_SELECT){
					setSelectionType(CELL_SELECT);
				}
				
				//G.Sturr 2010-7-10: force column selection
				if(view.isColumnSelect()){
					Point point = getIndexFromPixel(e.getX(), e.getY());
					if (point != null) {
						int column = (int)point.getX();
						setColumnSelectionInterval(column, column);
					}
				}
				// END G.Sturr 
				
				
				/*
				if (MyTable.this.getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_INTERVAL_SELECTION) {
					setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
					setColumnSelectionAllowed(true);
					setRowSelectionAllowed(true);
				}
				*/
				Point point1 = getMaxSelectionPixel();
				if (point1 == null) return;
				int x1 = e.getX();
				int y1 = e.getY();
				int x2 = (int)point1.getX();
				int y2 = (int)point1.getY();
				int range = DOT_SIZE / 2;
				
				// Handle click in another cell while editing a cell:
				// if the edit string begins with "=" then the clicked cell name
				// is inserted into the edit text
				if (editor.isEditing()) {
					String text = editor.getEditingValue();
					if (text.startsWith("=")) {
						Point point = getIndexFromPixel(e.getX(), e.getY());
						if (point != null) {
							int column = (int)point.getX();
							int row = (int)point.getY();
							GeoElement geo = RelativeCopy.getValue(MyTable.this, column, row);
							if (geo != null) {
								String name = GeoElement.getSpreadsheetCellName(column, row);
								if (geo.isGeoFunction()) name += "(x)";
								name0 = name;
								int caretPos = editor.getCaretPosition();
								prefix0 = text.substring(0, caretPos);
								postfix0 = text.substring(caretPos, text.length());
								isDragging2 = true;
								minColumn2 = column;
								maxColumn2 = column;
								minRow2 = row;
								maxRow2 = row;
								editor.addLabel(name);
								e.consume();
								repaint();
							}
						}
					}	
				}
				else if (x1 >= x2 - range && x1 <= x2 + range && y1 >= y2 - range && y1 <= y2 + range) {
					isDragingDot = true;
					e.consume();
				}
			}
			
			/*
			//G.Sturr 2009-9-23: moved show context menu to mouseReleased 
			// to allow right click selection
			
			// RIGHT CLICK: show context menu
			else {
				if (!kernel.getApplication().letShowPopupMenu()) return;    	

				if ((minSelectionColumn != -1 && maxSelectionColumn != -1) || (minSelectionRow != -1 && maxSelectionRow != -1)) {
					ContextMenu popupMenu = new ContextMenu(MyTable.this, minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow, selectedColumns);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
			*/
			
		}

		public void mouseReleased(MouseEvent e)	 {
			boolean rightClick = Application.isRightClick(e); 	        

			if (!rightClick) {
				if (editor.isEditing()) {
					String text = editor.getEditingValue();
					if (text.startsWith("=")) {
						Point point = getIndexFromPixel(e.getX(), e.getY());
						if (point != null) {
							int column = (int)point.getX();
							int row = (int)point.getY();
							if (column != editor.column || row != editor.row) {
								e.consume();
							}
						}
					}
					name0 = null;
					prefix0 = null;
					postfix0 = null;
					isDragging2 = false;
					repaint();
				}
				if (isDragingDot) {
					if (dragingToColumn == -1 || dragingToRow == -1) return;
					int x1 = -1;
					int y1 = -1;
					int x2 = -1;
					int y2 = -1;
					// -|1|-
					// 2|-|3
					// -|4|-
					if (dragingToColumn < minSelectionColumn) { // 2
						x1 = dragingToColumn;
						y1 = minSelectionRow;
						x2 = minSelectionColumn - 1;
						y2 = maxSelectionRow;
					}
					else if (dragingToRow > maxSelectionRow) { // 4
						x1 = minSelectionColumn;
						y1 = maxSelectionRow + 1;
						x2 = maxSelectionColumn;
						y2 = dragingToRow;
					}
					else if (dragingToRow < minSelectionRow) { // 1
						x1 = minSelectionColumn;
						y1 = dragingToRow;
						x2 = maxSelectionColumn;
						y2 = minSelectionRow - 1;
					}
					else if (dragingToColumn > maxSelectionColumn) { // 3
						x1 = maxSelectionColumn + 1;
						y1 = minSelectionRow;
						x2 = dragingToColumn;
						y2 = maxSelectionRow;
					}
					boolean succ = relativeCopy.doCopy(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow, x1, y1, x2, y2);
					if (succ) {
						app.storeUndoInfo();
						//	minSelectionColumn = -1;
						//	minSelectionRow = -1;
						//	maxSelectionColumn = -1;
						//	maxSelectionRow = -1;						
					}
					
					//(G.Sturr 2009-9-12) extend the selection to include the drag copy selection
					// and un-highlight dragging dot 
					changeSelection(dragingToRow, dragingToColumn, true, true);
					isOverDot = false;
					//(G.Sturr)
					
					isDragingDot = false;
					dragingToRow = -1;
					dragingToColumn = -1;
					repaint();
				}
			}
			
			// Alt click: copy definition to input field
			if (!isEditing() && e.isAltDown() && app.showAlgebraInput()) {
				int row = rowAtPoint(e.getPoint());
				int col = columnAtPoint(e.getPoint());
				GeoElement geo = (GeoElement) getModel().getValueAt(row, col);
			
				if (geo != null) {
					// F3 key: copy definition to input bar
					app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(3, geo);					
					return;
				}					
			}
			
			//G.Sturr 2009-9-23 
			//Show context menu .... moved from mousePressed 
			//to allow right click selection (as done in drawing pad)
			if (rightClick){
				if (!kernel.getApplication().letShowPopupMenu()) return;
				
				Point p = getIndexFromPixel(e.getX(), e.getY());
				
				// change selection if right click is outside current selection
				if(p.getY() < minSelectionRow ||  p.getY() > maxSelectionRow 
						|| p.getX() < minSelectionColumn || p.getX() > maxSelectionColumn)
				{
					//switch to cell selection mode 

					if(getSelectionType() != CELL_SELECT){
						setSelectionType(CELL_SELECT);
					}
					/*
					if (MyTable.this.getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_INTERVAL_SELECTION) {
						setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
						setColumnSelectionAllowed(true);
						setRowSelectionAllowed(true);
					}
					*/
					
					//now change the selection
					changeSelection((int) p.getY(), (int) p.getX(),false, false );
					//selectionChanged();		
				}
				//G.STURR 2009-12-20 A single ContextMenu is now used for all right clicks 
				//				
				//ContextMenu popupMenu = new ContextMenu(MyTable.this, minSelectionColumn, minSelectionRow, 
				//		maxSelectionColumn, maxSelectionRow, selectedColumns);
				
			//	ContextMenu popupMenu = new SpreadsheetContextMenu(MyTable.this, minSelectionColumn, minSelectionRow, 
			//			maxSelectionColumn, maxSelectionRow, selectedColumns,1);
				
				SpreadsheetContextMenu popupMenu = new SpreadsheetContextMenu(MyTable.this, e.isShiftDown());
				
				//END GSTURR
				
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
			
			
		}		
	}

	protected class MouseMotionListener1 implements MouseMotionListener
	{

		public void mouseDragged(MouseEvent e) {
			if (editor.isEditing()) {
				Point point = getIndexFromPixel(e.getX(), e.getY());
				if (point != null && name0 != null) {
					int column2 = (int)point.getX();
					int row2 = (int)point.getY();
					
					Matcher matcher = GeoElement.spreadsheetPattern.matcher(name0);
 					int column1 = GeoElement.getSpreadsheetColumn(matcher);
 					int row1 = GeoElement.getSpreadsheetRow(matcher);
					
 					if (column1 > column2) {
						int temp = column1;
						column1 = column2;
						column2 = temp;
					}
					if (row1 > row2) {
						int temp = row1;
						row1 = row2;
						row2 = temp;
					}
					String name1 = GeoElement.getSpreadsheetCellName(column1, row1);
					String name2 = GeoElement.getSpreadsheetCellName(column2, row2);
					if (! name1.equals(name2)) {
						name1 += ":" + name2;
					}

					name1 = prefix0 + name1 + postfix0;
					editor.setLabel(name1);
					minColumn2 = column1;
					maxColumn2 = column2;
					minRow2 = row1;
					maxRow2 = row2;
					repaint();
				}
				e.consume();
				return;
			}
			if (isDragingDot) {
				e.consume();
				int x = e.getX();
				int y = e.getY();
				Point point = getIndexFromPixel(x, y);
				//(G.Sturr 2009-9-12) save the selected cell position so it can be re-selected if needed
				int row = getSelectedRow();
				int column = getSelectedColumn();
				//(G.Sturr)
				
				if (point == null) {
					dragingToRow = -1;
					dragingToColumn = -1;
				}
				else {
					dragingToRow = (int)point.getY();
					dragingToColumn = (int)point.getX();							
					
					// increase size if we're at the bottom of the spreadsheet
					
					if (dragingToRow + 1 == getRowCount() && dragingToRow < SpreadsheetView.MAX_ROWS) {
						tableModel.setRowCount(getRowCount() +1);		
							
						//getView().getRowHeader().revalidate();  //G.STURR 2010-1-9
						
					}
					
					//(G.Sturr 2009-9-12) increase size when you go off the right edge
					// also moved scrolling call to the end so column addition works correctly   
					
					if (dragingToColumn + 1 == getColumnCount() && dragingToColumn < SpreadsheetView.MAX_COLUMNS) {
						
						setMyColumnCount(getColumnCount() +1);		
						getView().getColumnHeader().revalidate();
						
						// Java's addColumn will clear selection, so re-select our cell 
						changeSelection(row, column, false, false);
						
					}
					
					// scroll to show "highest" selected cell
					scrollRectToVisible(getCellRect(point.y, point.x, true));
					//(G.Sturr)
					
					
					// 1|2|3
					// 4|5|6
					// 7|8|9
					if (dragingToRow < minSelectionRow) {
						if (dragingToColumn < minSelectionColumn) { // 1
							int dy = minSelectionRow - dragingToRow;
							int dx = minSelectionColumn - dragingToColumn;
							if (dx > dy) {
								dragingToRow = minSelectionRow;
							}
							else {
								dragingToColumn = minSelectionColumn;
							}
						}
						else if (dragingToColumn > maxSelectionColumn) { // 3
							int dy = minSelectionRow - dragingToRow;
							int dx = dragingToColumn - maxSelectionColumn;
							if (dx > dy) {
								dragingToRow = minSelectionRow;
							}
							else {
								dragingToColumn = maxSelectionColumn;
							}
						}
						else { // 2
							dragingToColumn = minSelectionColumn;
						}
					}
					else if (dragingToRow > maxSelectionRow) {
						if (dragingToColumn < minSelectionColumn) { // 7
							int dy = dragingToRow - minSelectionRow;
							int dx = minSelectionColumn - dragingToColumn;
							if (dx > dy) {
								dragingToRow = minSelectionRow;
							}
							else {
								dragingToColumn = maxSelectionColumn;
							}
						}
						else if (dragingToColumn > maxSelectionColumn) { // 9
							int dy = dragingToRow - maxSelectionRow;
							int dx = dragingToColumn - maxSelectionColumn;
							if (dx > dy) {
								dragingToRow = maxSelectionRow;
							}
							else {
								dragingToColumn = maxSelectionColumn;
							}
						}
						else { // 8
							dragingToColumn = maxSelectionColumn;
						}
					}
					else {
						if (dragingToColumn < minSelectionColumn) { // 6
							dragingToRow = maxSelectionRow;
						}
						else if (dragingToColumn > maxSelectionColumn) { // 4
							dragingToRow = minSelectionRow;							
						}
						else { // 5
							dragingToRow = -1;
							dragingToColumn = -1;
						}
					}
				}
				repaint();
			}

			//G.STURR 2010-1-29: handle ctrl-select dragging of cell blocks
			else{
				if(e.isControlDown()){
					handleControlDragSelect(e);}
			}
		}

		/**
		 *  Shows tool tip description of geo on mouse over
		 */
		public void mouseMoved(MouseEvent e) {
			if (isEditing())
				return;

			// get GeoElement at mouse location
			int row = rowAtPoint(e.getPoint());
			int col = columnAtPoint(e.getPoint());
			GeoElement geo = (GeoElement) getModel().getValueAt(row, col);

			// set tooltip with geo's description
			if (geo != null & view.getAllowToolTips()) {
				setToolTipText(geo.getLongDescriptionHTML(true, true));				
			} else
				setToolTipText(null);	
			
			//(G. Sturr 2009-9-12) highlight dragging dot on mouseover
			Point point1 = getMaxSelectionPixel();
			if (point1 == null) return;
			int x1 = e.getX();
			int y1 = e.getY();
			int x2 = (int)point1.getX();
			int y2 = (int)point1.getY();
			int range = DOT_SIZE / 2;
			boolean nowOverDot = (x1 >= x2 - range && x1 <= x2 + range && y1 >= y2 - range && y1 <= y2 + range); 
			if (isOverDot != nowOverDot) {	
				isOverDot = nowOverDot;
				repaint();
			}
			//(G.Sturr)
			
		}

	}

	
	//G.STURR 2010-1-29: This handles ctrl-select dragging of cell blocks
	// because JTable does not do this correctly. 
	// TODO: JTable is still making selections that are not overridden,
	// so sometimes you can still get unwanted extended selection.
	//
	private void handleControlDragSelect(MouseEvent e) {

		Point p = e.getPoint();
		int row = this.rowAtPoint(p);
		int column = this.columnAtPoint(p);
		ListSelectionModel cm = getColumnModel().getSelectionModel();
		ListSelectionModel rm = getSelectionModel();
		
		/*
		//handle startup case of empty selection
		if ((column == -1) && (row == -1)){		
			cm.setSelectionInterval(0, 0);
			rm.setSelectionInterval(0, 0);			
		}
		*/
		
		if ((column == -1) || (row == -1)) {
			return;
		}

		// adjust the selection if mouse has left the old selected cell
		if (row != this.getSelectedRow() || column != this.getSelectedColumn()) {
			//boolean selected = true;
			int colAnchor = cm.getAnchorSelectionIndex();
			int rowAnchor = rm.getAnchorSelectionIndex();
			
			if (rowAnchor == -1 || rowAnchor >= getRowCount()) {
				rowAnchor = 0;
				//selected = false;
			}

			if (colAnchor == -1 || colAnchor >= getColumnCount()) {
				colAnchor = 0;
				//selected = false;
			}

			//selected = selected && isCellSelected(rowAnchor, colAnchor);

			cm.setSelectionInterval(colAnchor, column);
			rm.setSelectionInterval(rowAnchor, row);
			
			selectionChanged();

		}
		
	}
	
	
	
	
	protected class KeyListener1 implements KeyListener 
	{

		public void keyTyped(KeyEvent e) {

		}

		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			//Application.debug(keyCode+"");
			//boolean shiftDown = e.isShiftDown(); 	 
			boolean altDown = e.isAltDown(); 	 
			boolean ctrlDown = Application.isControlDown(e) // Windows ctrl/Mac Meta
			|| e.isControlDown(); // Fudge (Mac ctrl key)	
						
			//G.Sturr 2009-11-15: metaDown flag is needed for changeSelection method
			metaDown = Application.isControlDown(e);
					
			int row = getSelectedRow();
			int column = getSelectedColumn();
			
			TableModel model = getModel();


			switch (keyCode) {
			
			case KeyEvent.VK_UP:

				if (Application.isControlDown(e)) {

					if (model.getValueAt(row, column) != null) {
						// move to top of current "block"
						// if shift pressed, select cells too
						while ( row > 0 && model.getValueAt(row - 1, column) != null) row--;
						changeSelection(row, column, false, e.isShiftDown());
					} else {
						// move up to next defined cell
						while ( row > 0 && model.getValueAt(row - 1, column) == null) row--;
						changeSelection(Math.max(0, row - 1), column, false, false);
						
					}
					e.consume();
				}
				// copy description into input bar when a cell is entered
//				GeoElement geo = (GeoElement) getModel().getValueAt(getSelectedRow() - 1, getSelectedColumn());
//				if (geo != null) {
//					AlgebraInput ai = (AlgebraInput)(app.getGuiManager().getAlgebraInput());
//					ai.setString(geo);
//				}
				
				break;
				
			case KeyEvent.VK_LEFT:
				if (Application.isControlDown(e)) {

					if (model.getValueAt(row, column) != null) {
						// move to left of current "block"
						// if shift pressed, select cells too
						while ( column > 0 && model.getValueAt(row, column - 1) != null) column--;
						changeSelection(row, column, false, e.isShiftDown());
					} else {
						// move left to next defined cell
						while ( column > 0 && model.getValueAt(row, column - 1) == null) column--;
						changeSelection(row, Math.max(0, column - 1), false, false);						
					}
					
					e.consume();
				}
//				// copy description into input bar when a cell is entered
//				geo = (GeoElement) getModel().getValueAt(getSelectedRow(), getSelectedColumn() - 1);
//				if (geo != null) {
//					AlgebraInput ai = (AlgebraInput)(app.getGuiManager().getAlgebraInput());
//					ai.setString(geo);
//				}
				break;

			
			case KeyEvent.VK_DOWN:
				// auto increase spreadsheet size when you go off the bottom	
				if (getSelectedRow() + 1 == getRowCount() && getSelectedRow() < SpreadsheetView.MAX_ROWS) {
					tableModel.setRowCount(getRowCount() +1);
					
					//getView().getRowHeader().revalidate();   //G.STURR 2010-1-9
				}
				
				else if (Application.isControlDown(e)) {

					if (model.getValueAt(row, column) != null) {
					
						// move to bottom of current "block"
						// if shift pressed, select cells too
						while ( row < getRowCount()-1 && model.getValueAt(row + 1, column) != null) row++;
						changeSelection(row, column, false, e.isShiftDown());
					} else {
						// move down to next selected cell
						while ( row < getRowCount()-1 && model.getValueAt(row + 1, column) == null) row++;
						changeSelection(Math.min(getRowCount() - 1, row + 1), column, false, false);
						
					}
					
					e.consume();
				}


//				// copy description into input bar when a cell is entered
//				geo = (GeoElement) getModel().getValueAt(getSelectedRow()+1, getSelectedColumn());
//				if (geo != null) {
//					AlgebraInput ai = (AlgebraInput)(app.getGuiManager().getAlgebraInput());
//					ai.setString(geo);
//				}

				
				break;
				
			case KeyEvent.VK_HOME:

				// if shift pressed, select cells too
				if (Application.isControlDown(e)) {
					// move to top left of spreadsheet
					changeSelection(0, 0, false, e.isShiftDown());
				}
				else {
					// move to left of current row
					changeSelection(row, 0, false, e.isShiftDown());
				}
				
				e.consume();
				break;
				
			case KeyEvent.VK_END:

				// move to bottom right of spreadsheet
				// if shift pressed, select cells too
				
				// find rectangle that will contain all cells 
				for (int c = 0 ; c < model.getColumnCount() ; c++)
				for (int r = 0 ; r < model.getRowCount() ; r++)
					if ((r > row || c > column) && getModel().getValueAt(r, c) != null) {
						if (r > row) row = r;
						if (c > column) column = c;
					}
				changeSelection(row, column, false, e.isShiftDown());
				
				e.consume();

			case KeyEvent.VK_RIGHT:
				// auto increase spreadsheet size when you go off the right
				
				if (getSelectedColumn() + 1 == getColumnCount() && getSelectedColumn() < SpreadsheetView.MAX_COLUMNS) {
					setMyColumnCount(getColumnCount() +1);		
					getView().getColumnHeader().revalidate();
					
					// these two lines are a workaround for Java 6
					// (Java bug?)
					changeSelection(row, column + 1, false, false);
					e.consume();
				}
				else if (Application.isControlDown(e)) {

					if (model.getValueAt(row, column) != null) {
						// move to bottom of current "block"
						// if shift pressed, select cells too
						while ( column < getColumnCount() - 1 && model.getValueAt(row, column + 1) != null) column++;
						changeSelection(row, column, false, e.isShiftDown());
					} else {
						// move right to next defined cell
						while ( column < getColumnCount() - 1 && model.getValueAt(row, column + 1) == null) column++;
						changeSelection(row, Math.min(getColumnCount() - 1, column + 1), false, false);
						
					}
					e.consume();
				}

//				// copy description into input bar when a cell is entered
//				geo = (GeoElement) getModel().getValueAt(getSelectedRow(), getSelectedColumn() + 1);
//				if (geo != null) {
//					AlgebraInput ai = (AlgebraInput)(app.getGuiManager().getAlgebraInput());
//					ai.setString(geo);
//				}
				break;
				
			case KeyEvent.VK_SHIFT:
			case KeyEvent.VK_CONTROL:
			case KeyEvent.VK_ALT:
			case KeyEvent.VK_META: //MAC_OS Meta
				e.consume(); // stops editing start
				break;

			case KeyEvent.VK_F9:
				kernel.updateConstruction();
				e.consume(); // stops editing start
				break;

			case KeyEvent.VK_R:
				if (Application.isControlDown(e)) {
					kernel.updateConstruction();
					e.consume();
				}
				else letterOrDigitTyped();
				break;

				// needs to be here to stop keypress starting a cell edit after the undo
			case KeyEvent.VK_Z: //undo
				if (ctrlDown) {
					//Application.debug("undo");
					app.getGuiManager().undo();
					e.consume();
				}
				else letterOrDigitTyped();
				break;

				// needs to be here to stop keypress starting a cell edit after the redo
			case KeyEvent.VK_Y: //redo
				if (ctrlDown) {
					//Application.debug("redo");
					app.getGuiManager().redo();
					e.consume();
				}
				else letterOrDigitTyped();
				break;


			case KeyEvent.VK_C: 	                         
			case KeyEvent.VK_V: 	                        
			case KeyEvent.VK_X: 	                         
			case KeyEvent.VK_DELETE: 	                         
			case KeyEvent.VK_BACK_SPACE:
				if (! editor.isEditing()) {
					if (Character.isLetterOrDigit(e.getKeyChar()) &&
							!editor.isEditing() && !(ctrlDown || e.isAltDown())) {
						letterOrDigitTyped();
					} else	if (ctrlDown) {
						e.consume();

						if (keyCode == KeyEvent.VK_C) {
							copyPasteCut.copy(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow, altDown);
						}
						else if (keyCode == KeyEvent.VK_V) {
							boolean storeUndo = copyPasteCut.paste(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow);
							getView().getRowHeader().revalidate();
							if (storeUndo)
								app.storeUndoInfo();
						}
						else if (keyCode == KeyEvent.VK_X) {
							boolean storeUndo = copyPasteCut.cut(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow);
							if (storeUndo)
								app.storeUndoInfo();
						}
					}
					if (keyCode == KeyEvent.VK_DELETE || 	                                         
							keyCode == KeyEvent.VK_BACK_SPACE) {
						e.consume();
						//Application.debug("deleting...");
						boolean storeUndo = copyPasteCut.delete(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow);
						if (storeUndo)
							app.storeUndoInfo();
					}
					return;
				}
				break;		
				
			//case KeyEvent.VK_ENTER:	
			case KeyEvent.VK_F2:	
				if (!editor.isEditing()) {
					allowEditing = true;
					editCellAt(getSelectedRow(), getSelectedColumn());
					 final JTextComponent f = (JTextComponent)getEditorComponent();
			            f.requestFocus();
			            f.getCaret().setVisible(true);
					allowEditing = false;
				}
				e.consume();
				break;	
				
			case KeyEvent.VK_ENTER:	
				
				if (MyCellEditor.tabReturnCol > -1) {
					changeSelection(row , MyCellEditor.tabReturnCol, false, false);
					MyCellEditor.tabReturnCol = -1;
				}
				
				// fall through
			case KeyEvent.VK_PAGE_DOWN:	
			case KeyEvent.VK_PAGE_UP:	
				// stop cell being erased before moving
				break;
				
				// stop TAB erasing cell before moving
			case KeyEvent.VK_TAB:
				// disable shift-tab in column A
				if (getSelectedColumn() == 0 && e.isShiftDown()) 
					e.consume();
				break;

			case KeyEvent.VK_A:
				if (Application.isControlDown(e)) {
					// select all cells
					
					row = 0;
					column = 0;
					// find rectangle that will contain all defined cells 
					for (int c = 0 ; c < model.getColumnCount() ; c++)
					for (int r = 0 ; r < model.getRowCount() ; r++)
						if ((r > row || c > column) && getModel().getValueAt(r, c) != null) {
							if (r > row) row = r;
							if (c > column) column = c;
						}
					changeSelection(0, 0, false, false);
					changeSelection(row, column, false, true);

					
					e.consume();
					
				}
				// no break, fall through
			default:
				if (!Character.isIdentifierIgnorable(e.getKeyChar()) &&
						!editor.isEditing() && !(ctrlDown || e.isAltDown())) {
					letterOrDigitTyped();
				} else
					e.consume();
			break;
				
			}
				
			/*
			if (keyCode >= 37 && keyCode <= 40) {
				if (editor.isEditing())	return;			
			}

			for (int i = 0; i < defaultKeyListeners.length; ++ i) {
				if (e.isConsumed()) break;
				defaultKeyListeners[i].keyPressed(e);			
			}
			 */
		}
		
		public void letterOrDigitTyped() {
			allowEditing = true;
			repaint();  //G.Sturr 2009-10-10: cleanup when keypress edit begins
			
			// check if cell fixed
			Object o = tableModel.getValueAt(getSelectedRow(), getSelectedColumn());			
			if ( o != null && o instanceof GeoElement) {
				GeoElement geo = (GeoElement)o;
				if (geo.isFixed()) return;
			}
		
			tableModel.setValueAt(null, getSelectedRow(), getSelectedColumn());
			editCellAt(getSelectedRow(), getSelectedColumn()); 
			// workaround, see
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4192625				
            final JTextComponent f = (JTextComponent)getEditorComponent();
            f.requestFocus();
            f.getCaret().setVisible(true);
            
            // workaround for Mac OS X 10.5 problem (first character typed deleted)
            if (Application.MAC_OS)
	            SwingUtilities.invokeLater( new Runnable(){ public void
	            	run() { f.setSelectionStart(1);
		            f.setSelectionEnd(1);} });

			allowEditing = false;
			
		}

		public void keyReleased(KeyEvent e) {
			metaDown = false;    //G Sturr 
		}

	}


	
	//G.STURR 2010-1-29
    // Row and Column selection listeners no longer needed.
	
	protected class RowSelectionListener implements ListSelectionListener
	{
		
		public void valueChanged(ListSelectionEvent e) {
			//selectionChanged();
         /* -------------------------------------------
 			ListSelectionModel selectionModel = (ListSelectionModel) e
					.getSource();
			minSelectionRow = selectionModel.getMinSelectionIndex();
			maxSelectionRow = selectionModel.getMaxSelectionIndex();
			if (getSelectionModel().getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
				minSelectionColumn = 0;
				maxSelectionColumn = MyTable.this.getColumnCount() - 1;

				selectionChanged();
			}
			--------------------------------------------
			/*
			 * removed Michael Borcherds 2008-08-08 causes a bug when multiple
			 * rows are selected selected = new boolean[getColumnCount()]; for
			 * (int i = 0; i < selected.length; ++ i) { if
			 * (selectionModel.isSelectedIndex(i)) { selected[i] = true; } }
			 */

		}
		
		

	}

	protected class ColumnSelectionListener implements ListSelectionListener
	{

		public void valueChanged(ListSelectionEvent e) {
		//	selectionChanged();
			/* ------------------------------------
			ListSelectionModel selectionModel = (ListSelectionModel)e.getSource();
			minSelectionColumn = selectionModel.getMinSelectionIndex(); 
			maxSelectionColumn = selectionModel.getMaxSelectionIndex();
			
			//G.Sturr 2009-9-30  added to allow drawing column selection rectangle
			if (getSelectionModel().getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
				minSelectionRow = 0;
				maxSelectionRow = MyTable.this.getRowCount() - 1;
				selectionChanged();	
			}
			
			----------------------------------------------
			
			//  old code --- selectedColumns is no longer used
			/*
			selectedColumns = new boolean[getColumnCount()];
			for (int i = 0; i < selectedColumns.length; ++ i) {
				if (selectionModel.isSelectedIndex(i)) {
					selectedColumns[i] = true;
				}
			}
			selectionChanged();	
			*/
		}

	}

	

	/*
	protected class KeyListener4 implements KeyListener 
	{

		public void keyTyped(KeyEvent e) {
			int keyCode = e.getKeyChar();
			int keyCode2 = e.getKeyCode();
			Application.debug("getKeyChar=" + keyCode);
			Application.debug("getKeyCode=" + keyCode2);
			if (keyCode == 27) {
				if (editor.isEditing()) {
					editor.undoEdit();
					editor.editing = false;
				}
			}
		}

		public void keyPressed(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
		}

	}
	/**/

	protected class MyColumnHeaderRenderer extends JLabel implements TableCellRenderer, ListSelectionListener  //, FocusListener
	{
		private static final long serialVersionUID = 1L;

		private Color defaultBackground;

		private ImageIcon traceIcon = new ImageIcon();
		private ImageIcon emptyIcon = new ImageIcon();
		
		public MyColumnHeaderRenderer() {    		
			super("", SwingConstants.CENTER);
			setOpaque(true);
			defaultBackground = MyTable.BACKGROUND_COLOR_HEADER;
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, MyTable.TABLE_GRID_COLOR));
			Font font1 = getFont(); 
			if (font1 == null || font1.getSize() == 0) {
				kernel.getApplication().getPlainFont();
				if (font1 == null || font1.getSize() == 0) {
					font1 = new Font("dialog", 0, 12);
				}
			}
			setFont(font1);
			
			traceIcon = app.getImageIcon("spreadsheettrace.gif");
			emptyIcon = new ImageIcon();
			
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int colIndex) {
			
			setText(value.toString());
			setIcon(emptyIcon);
			
			if (getSelectionType() == ROW_SELECT) {
				setBackground(defaultBackground);
			} else {
				if (selectedColumnSet.contains(colIndex)
						|| (colIndex >= minSelectionColumn && colIndex <= maxSelectionColumn) ) {
					setBackground(MyTable.SELECTED_BACKGROUND_COLOR_HEADER);
				} else {
					setBackground(defaultBackground);
				}
			}
			if(view.getTraceManager().isTraceColumn(colIndex)){
				setIcon(traceIcon);
			}
			return this;
			
		/* ------------- old code ----------	
		if (minSelectionColumn != -1 && maxSelectionColumn != -1) {
				if (colIndex >= minSelectionColumn && colIndex <= maxSelectionColumn && selectedColumns != null && selectedColumns.length > colIndex && selectedColumns[colIndex]) {
					setBackground(MyTable.SELECTED_BACKGROUND_COLOR_HEADER);					
				}
				else {
					setBackground(defaultBackground);				
				}
			}
			else {
				setBackground(defaultBackground);				
			}
        
			return this;
          ------------------  */
          			
		}

		// G.STURR 2010-1-29 (no longer needed)
		
		public void valueChanged(ListSelectionEvent e) {
	/*
			ListSelectionModel selectionModel = (ListSelectionModel)e.getSource();
			//minSelectionColumn = selectionModel.getMinSelectionIndex();
			//maxSelectionColumn = selectionModel.getMaxSelectionIndex();
			selectedColumns = new boolean[getColumnCount()];
			for (int i = 0; i < selectedColumns.length; ++ i) {
				if (selectionModel.isSelectedIndex(i)) {
					selectedColumns[i] = true;
				}
			}
			//repaint();
		*/
		}

		/*   (focus listener not needed yet)
		public void focusGained(FocusEvent e) {
			if (Application.isVirtualKeyboardActive())
				app.getGuiManager().toggleKeyboard(true);
			
		}


		public void focusLost(FocusEvent e) {
			// avoid infinite loop!
			if (e.getOppositeComponent() instanceof VirtualKeyboard)
				return;
			if (Application.isVirtualKeyboardActive()
			app.getGuiManager().toggleKeyboard(false);
			
		}
		*/
		
	
	}
	
	
	//  MouseListener2 is the column header listener
	//
	protected int column0 = -1;
	protected boolean isResizing = false;

	protected class MouseListener2 implements MouseListener
	{

		public void mouseClicked(MouseEvent e) {
			
			// G.Sturr 2010-3-29
			// Double clicking on a column boundary auto-adjusts the 
			// width of the column on the left
						
			if (isResizing && !Application.isRightClick(e) && e.getClickCount() == 2) {
							
				// get column to adjust
				int x = e.getX();
				int y = e.getY();
				Point point = getIndexFromPixel(x, y);
				Point testPoint = getIndexFromPixel(x-4, y);
				int col = (int) point.getX();
				if(point.getX()!= testPoint.getX()){
					col = col-1;
				}				
				
				// enlarge or shrink to fit the contents 
				fitColumn(col);
				
				e.consume();
			}	
			//END G.Sturr
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			boolean metaDown = Application.isControlDown(e); 	 
			boolean shiftDown = e.isShiftDown(); 	 
			boolean rightClick = Application.isRightClick(e); 	 

			if (!rightClick) {
				Point point = getIndexFromPixel(x, y);
				if (point != null) {
					Point point2 = getPixel((int)point.getX(), (int)point.getY(), true);
					Point point3 = getPixel((int)point.getX(), (int)point.getY(), false);
					int x2 = (int)point2.getX();
					int x3 = (int)point3.getX();
					isResizing = ! (x > x2 + 2 && x < x3 - 3);
					if (! isResizing) {

						if(getSelectionType() != COLUMN_SELECT){
							setSelectionType(COLUMN_SELECT);
							getTableHeader().requestFocusInWindow();
						}
						
						/*
						if (getSelectionModel().getSelectionMode() != ListSelectionModel.MULTIPLE_INTERVAL_SELECTION || 
								getColumnSelectionAllowed() == false) {
							setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
							setColumnSelectionAllowed(true);
							setRowSelectionAllowed(false);
							getTableHeader().requestFocusInWindow();
						}
						*/
						
						if (shiftDown) {
							if (column0 != -1) {
								int column = (int)point.getX();
								setColumnSelectionInterval(column0, column);
							}
						}
						else if (metaDown) {					
							column0 = (int)point.getX();
							//G.Sturr 2009-11-15: ctrl-select now handled in changeSelection
							setColumnSelectionInterval(column0, column0);
							//addColumnSelectionInterval(column0, column0);
						}
						else {
							column0 = (int)point.getX();
							setColumnSelectionInterval(column0, column0);
						}
						//repaint();
					}
				}

			}
		}

		public void mouseReleased(MouseEvent e)	{
			boolean rightClick = Application.isRightClick(e); 	 
			
			if (!kernel.getApplication().letShowPopupMenu()) return;    
			
			//G.Sturr 2009-9-30: added right click selection
			
			if (rightClick) { 	 
			
				if (!app.letShowPopupMenu()) return; 
				
				Point p = getIndexFromPixel(e.getX(), e.getY());	
				if (p == null) return;
				
				// if click is outside current selection then change selection
				if(p.getY() < minSelectionRow ||  p.getY() > maxSelectionRow 
						|| p.getX() < minSelectionColumn || p.getX() > maxSelectionColumn)
				{
					// switch to column selection mode and select column
					if(table.getSelectionType() != MyTable.COLUMN_SELECT)
						setSelectionType(MyTable.COLUMN_SELECT);
					/*
					if (getSelectionModel().getSelectionMode() != ListSelectionModel.MULTIPLE_INTERVAL_SELECTION ||
						getColumnSelectionAllowed() == true) {
						setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
						setColumnSelectionAllowed(true);
						setRowSelectionAllowed(false);
					}
					*/
					
					//selectNone();
					setColumnSelectionInterval((int)p.getX(), (int)p.getX());
					
				}	
				
				//show contextMenu
				
				//GSTURR 2009-11-15 added a parameter to indicate selection type
				//
				//ContextMenuCol popupMenu = new ContextMenuCol(MyTable.this, minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow, 
				//		selectedColumns);
			//	ContextMenu popupMenu = new SpreadsheetContextMenu(MyTable.this, minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow, 
			//			selectedColumns,1);
				
				SpreadsheetContextMenu popupMenu = new SpreadsheetContextMenu(MyTable.this, e.isShiftDown());
				
				//END GSTURR
		        popupMenu.show(e.getComponent(), e.getX(), e.getY());
			
		        
				/*    (old code)
				if (minSelectionColumn != -1 && maxSelectionColumn != -1) {
					ContextMenuCol popupMenu = new ContextMenuCol(MyTable.this, minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow, selectedColumns);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}	
				*/
					
			}
			else if (isResizing) {
				
				if (e.getClickCount() == 2 )return;
				
				int x = e.getX();
				int y = e.getY();
				Point point = getIndexFromPixel(x, y);
				if (point == null) return;
				Point point2 = getPixel((int)point.getX(), (int)point.getY(), false);
				int column = (int)point.getX();
				if (x < (int)point2.getX() - 3) {
					-- column;
				}
				
				if(x<=0) x=0; //G.Sturr 2010-4-10 prevent x=-1 with very small row size
				
				int width = getColumnModel().getColumn(column).getWidth();
				int[] selected = getSelectedColumns();
				if (selected == null) return;
				boolean in = false;
				for (int i = 0; i < selected.length; ++ i) {
					if (column == selected[i]) in = true;
				}
				if (! in) return;				
				for (int i = 0; i < selected.length; ++ i) {
					getColumnModel().getColumn(selected[i]).setPreferredWidth(width);					
				}
			}
		}

	}

	//
	//  MouseMotionListener2 is the column header motion listener
	
	protected class MouseMotionListener2 implements MouseMotionListener
	{

		public void mouseDragged(MouseEvent e) {
			
			if(Application.isRightClick(e))return; //G.Sturr 2009-9-30 
				
			if (isResizing) return;
			int x = e.getX();
			int y = e.getY();
			Point point = getIndexFromPixel(x, y);
			if (point != null) {
				int column = (int)point.getX();
				setColumnSelectionInterval(column0, column);
			//	repaint();
			}
		}

		public void mouseMoved(MouseEvent e) {
		}

	}



	protected class KeyListener2 implements KeyListener 
	{

		public void keyTyped(KeyEvent e) {
		}

		public void keyPressed(KeyEvent e) {
			//Application.debug("keypressed");
			
			//G.Sturr 2009-11-15: metaDown now declared above so it can be used in changeSelection
			// to handle ctrl-select
			//boolean metaDown = Application.isControlDown(e);
			metaDown = Application.isControlDown(e);
			
			//boolean metaDown = Application.isControlDown(e);
			boolean altDown = e.isAltDown();
			int keyCode = e.getKeyCode();
			switch (keyCode) {

			case KeyEvent.VK_C : // control + c
				//Application.debug(minSelectionColumn);
				//Application.debug(maxSelectionColumn);
				if (metaDown  && minSelectionColumn != -1 && maxSelectionColumn != -1) {
					copyPasteCut.copy(minSelectionColumn, 0, maxSelectionColumn, tableModel.getRowCount() - 1, altDown);
					e.consume();
				}
				break;

			case KeyEvent.VK_V : // control + v
				if (metaDown && minSelectionColumn != -1 && maxSelectionColumn != -1) {
					boolean storeUndo = copyPasteCut.paste(minSelectionColumn, 0, maxSelectionColumn, tableModel.getRowCount() - 1);					
					if (storeUndo)
						app.storeUndoInfo();
					getView().getRowHeader().revalidate();
					e.consume();
				}
				break;		

			case KeyEvent.VK_X : // control + x
				if (metaDown && minSelectionColumn != -1 && maxSelectionColumn != -1) {
					boolean storeUndo = copyPasteCut.cut(minSelectionColumn, 0, maxSelectionColumn, tableModel.getRowCount() - 1);
					if (storeUndo)
						app.storeUndoInfo();
					e.consume();
				}
				break;

			case KeyEvent.VK_BACK_SPACE : // delete
			case KeyEvent.VK_DELETE : // delete
				boolean storeUndo = copyPasteCut.delete(minSelectionColumn, 0, maxSelectionColumn, tableModel.getRowCount() - 1);
				if (storeUndo)
					app.storeUndoInfo();
				break;
			}
		}

		public void keyReleased(KeyEvent e) {
			//G.Sturr 2009-11-15: metaDown flag needed to do ctrl-select in changeSelection 
			metaDown = false;  
		}

	}

	@Override
	public int convertColumnIndexToModel(int viewColumnIndex) {
		return viewColumnIndex;    	
	}

	private boolean allowEditing = false;

	/*
	 * we need to return false for this normally, otherwise we can't detect double-clicks
	 * 
	 */
	@Override
	public boolean isCellEditable(int row, int column)
	{
		if(view.isColumnSelect()) return false;
		
		// to avoid getValueAt() unless necessary
		
		if (!allowEditing && !oneClickEditMap.containsKey(new Point(column,row))) return false; 
		
		GeoElement geo = (GeoElement) getModel().getValueAt(row, column);
		
		if (geo != null && geo.isFixed()) return false;
		
		return true;
	}


	public void focusGained(FocusEvent e) {
		if (Application.isVirtualKeyboardActive())
			app.getGuiManager().toggleKeyboard(true);
		
	}


	public void focusLost(FocusEvent e) {
		// avoid infinite loop!
		if (e.getOppositeComponent() instanceof VirtualKeyboard)
			return;
		if (Application.isVirtualKeyboardActive())
			app.getGuiManager().toggleKeyboard(false);
		
	}
	
	
	
	
	
	//G.STURR 2010-1-15
	// Keep row heights of table and rowHeader in sync
	@Override
	public void setRowHeight(int row, int rowHeight) {
		super.setRowHeight(row, rowHeight);
		try {
			view.updateRowHeader();
			if(doRecordRowHeights)
				adjustedRowHeights.add(new Point(row, rowHeight));
		} catch (Exception e) {
		}
	}
	
	@Override
	public void setRowHeight(int rowHeight) {
		super.setRowHeight(rowHeight);
		try {
			view.updateRowHeader();
		} catch (Exception e) {
		}
	}
	
	// Reset the row heights --- used after addColumn destoys the row heights
	public void resetRowHeights(){
		doRecordRowHeights = false;
		for(Point p: adjustedRowHeights){
			setRowHeight(p.x,p.y);		
		}
		doRecordRowHeights = true;
	}
	
	
	
	
	
	
	
	
	//==================================================
	// Table row and column size adjustment methods
	//==================================================
	
	
	/**
	 * Enlarge the row and/or column of all marked cells. 
	 * A cell is marked by placing it in one of two hashSets:
	 * cellResizeHeightSet or cellResizeWidthSet.
	 * Currently, this is only done after a geo is added to a cell
	 * and the row needs to be widened to fit the LaTeX image. 
	 *  
	 */
	public void resizeMarkedCells() {

		if (!cellResizeHeightSet.isEmpty()) {
			for (Point cellPoint : cellResizeHeightSet) {
				setPreferredCellSize((int) cellPoint.getY(), (int) cellPoint.getX(), false, true);	
			}
			cellResizeHeightSet.clear();
		}

		if (!cellResizeWidthSet.isEmpty()) {
			for (Point cellPoint : cellResizeWidthSet) {
				setPreferredCellSize((int) cellPoint.getY(), (int) cellPoint.getX(), true, false);
			}
			cellResizeWidthSet.clear();
		}
	}
	
		
	/**
	 * Enlarge the row and/or column of a cell to fit the cell's preffered size. 
	 */
	public void setPreferredCellSize(int row, int col, boolean adjustWidth, boolean adjustHeight) {

		Dimension prefSize = table.getCellRenderer(row, col)
				.getTableCellRendererComponent(table,
						table.getValueAt(row, col), false, false, row, col)
				.getPreferredSize();
		
		if (adjustWidth) {
			
			TableColumn tableColumn = table.getColumnModel().getColumn(col);

			int resultWidth = Math.max(tableColumn.getWidth(), (int) prefSize
					.getWidth());
			tableColumn.setWidth(resultWidth
					+ table.getIntercellSpacing().width);
		}
		
		if (adjustHeight) {

			int resultHeight = Math.max(getRowHeight(row), (int) prefSize
					.getHeight());
			setRowHeight(row, resultHeight);
		}

	}
	
	/**
	 * Adjust the width of a column to fit the maximum prefferred width of 
	 * its cell contents.
	 */
	public void fitColumn(int column){
		
		TableColumn tableColumn = table.getColumnModel().getColumn(column); 
		
		// iterate through the rows and find the preferred width
		int currentWidth = tableColumn.getWidth();
		int prefWidth = 0;
		int tempWidth = -1;
		for (int row = 0; row < getRowCount(); row++) {
			if(table.getValueAt(row, column)!=null){
			tempWidth = (int) table.getCellRenderer(row, column)
					.getTableCellRendererComponent(table,
							table.getValueAt(row, column), false, false,
							row, column).getPreferredSize().getWidth();
			prefWidth = Math.max(prefWidth, tempWidth);
			}
		}
		
		
		// set the new column width
		if (tempWidth == -1) {
			// column is empty
			prefWidth = preferredColumnWidth
					- getIntercellSpacing().width;
		} else {
			prefWidth = Math.max(prefWidth, tableColumn.getMinWidth());
		}
		getTableHeader().setResizingColumn(tableColumn);
		tableColumn.setWidth(prefWidth
				+ getIntercellSpacing().width);

	}

	/**
	 * Adjust the height of a row to fit the maximum prefferred height of 
	 * the its cell contents. 
	 */
	public void fitRow(int row){
		
		// iterate through the columns and find the preferred height
		int currentHeight = table.getRowHeight(row);
		int prefHeight = table.getRowHeight();
		int tempHeight = 0;
		for (int column = 0; column < table.getColumnCount(); column++) {

			tempHeight = (int) table.getCellRenderer(row, column)
					.getTableCellRendererComponent(table,
							table.getValueAt(row, column), false, false,
							row, column).getPreferredSize().getHeight();
			
			prefHeight = Math.max(prefHeight, tempHeight);

		}
		
		// set the new row height
		table.setRowHeight(row, prefHeight) ;
	}
	
	
	
	/**
	 * Adjust all rows/columns to fit the maximum preffered height/width
	 * of their cell contents.
	 * 
	 */
	public void fitAll(boolean doRows, boolean doColumns){
		if (doRows) {
			for (int row = 0; row < table.getRowCount(); row++) {
				fitRow(row);
			}
		}
		if (doColumns) {
			for (int column = 0; column < table.getColumnCount(); column++) {
				fitRow(column);
			}
		}
	}

	

	
	/**
	 * Column model listener --- used to reset the preferred column width 
	 * when all columns have been selected.
	 */
	public class MyTableColumnModelListener implements TableColumnModelListener { 
		
		public void columnMarginChanged(ChangeEvent e) {
			if(getSelectAll() && minSelectionColumn >= 0){
				preferredColumnWidth = table.getColumnModel().getColumn(minSelectionColumn).getPreferredWidth();
			}
		}
		
		public void columnAdded(TableColumnModelEvent arg0) {			
		}

		public void columnMoved(TableColumnModelEvent arg0) {	
		}

		public void columnRemoved(TableColumnModelEvent arg0) {	
		}

		public void columnSelectionChanged(ListSelectionEvent arg0) {	
		}
	}

	
	// When the spreadsheet is smaller than the viewport fill the extra space with 
	// the same background color as the spreadsheet.
	// This gives a smoother look when the spreadsheet auto-adjusts to fill the space.	

	@Override
	protected void configureEnclosingScrollPane() {
		super.configureEnclosingScrollPane();
		Container p = getParent();
		if (p instanceof JViewport) {
			((JViewport) p).setBackground(getBackground());
		}
	}
	
	
	
}
