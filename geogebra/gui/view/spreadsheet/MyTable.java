package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianConstants;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

public class MyTable extends JTable implements FocusListener 
{
	
	public static final int TABLE_MODE_STANDARD = 0;
	public static final int TABLE_MODE_AUTOFUNCTION = 1;
	
	private int tableMode = TABLE_MODE_STANDARD;
	public int getTableMode() { return tableMode; }
	public void setTableMode(int tableMode) { 
		this.tableMode = tableMode; 
		if(tableMode == TABLE_MODE_AUTOFUNCTION){
			targetCellLoc = new Point(minSelectionColumn,minSelectionRow);
			targetcellFrame = this.getCellBlockRect(minSelectionColumn, minSelectionRow, 
					minSelectionColumn, minSelectionRow, true);
			setSelectionRectangleColor(Color.GRAY);
			minSelectionColumn = -1;
			maxSelectionColumn = -1;
			minSelectionRow = -1;
			maxSelectionRow = -1;
			app.clearSelectedGeos();
			
		}else{
			targetCellLoc = null;
			targetcellFrame = null;
			this.setSelectionRectangleColor(Color.BLUE);
		}
			
	}
	
	private Point targetCellLoc;
	
	
	
	
	
	public static final int MAX_CELL_EDIT_STRING_LENGTH = 10;

	public static final int TABLE_CELL_WIDTH = 70;
	public static final int TABLE_CELL_HEIGHT = 21;  //G.Sturr (old height 20) + 1 to stop cell editor clipping
	public static final int DOT_SIZE = 7;
	public static final int LINE_THICKNESS1 = 3;
	public static final int LINE_THICKNESS2 = 2;
	public static final Color SELECTED_BACKGROUND_COLOR = GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR; 
	public static final Color SELECTED_BACKGROUND_COLOR_HEADER = GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR_HEADER;
	public static final Color BACKGROUND_COLOR_HEADER = GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER;
	public static final Color TABLE_GRID_COLOR = GeoGebraColorConstants.TABLE_GRID_COLOR;
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
	protected SpreadsheetColumnController.ColumnHeaderRenderer headerRenderer;
	protected SpreadsheetView view;
	protected DefaultTableModel tableModel;
	private CellRangeProcessor crProcessor;
	private MyTable table;
	private MyTableColumnModelListener columnModelListener;
	
	private CellFormat formatHandler;
	
	
	
	
	
	/**
	 * All currently selected cell ranges are held in this list.
	 * Cell ranges are added when selecting with ctrl-down. 
	 * The first element is the most recently selected cell range. 	 
	 */
	public ArrayList<CellRange> selectedCellRanges;
	
	
	public ArrayList<CellRange> getSelectedCellRanges() {
		return selectedCellRanges;
	}


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
	protected boolean isDragging2 = false;
	
	protected int minColumn2 = -1;
	protected int maxColumn2 = -1;
	protected int minRow2 = -1;
	protected int maxRow2 = -1;
	
	protected boolean isOverDnDRegion = false;
	
	// Keep track of ctrl-down. This is needed in some
	// selection methods that do not receive key events.
	protected boolean metaDown = false;
	
	
	
	
	// Cells to be resized on next repaint are put in these HashSets.
	// A cell is added to a set when editing is done. The cells are removed
	// after a repaint in MyTable.
	public static HashSet<Point> cellResizeHeightSet = new HashSet<Point>();
	public static HashSet<Point> cellResizeWidthSet = new HashSet<Point>();
	
	
	private ArrayList<Point> adjustedRowHeights = new ArrayList<Point>();
	private boolean doRecordRowHeights = true;

	public int preferredColumnWidth = TABLE_CELL_WIDTH; //G.Sturr 2010-4-10 
	
	
	// Collection of cells that contain geos that can be edited with one click,
	// e.g. booleans, buttons, lists
	protected HashMap<Point,GeoElement> oneClickEditMap = new HashMap<Point,GeoElement>();

	

	
	public HashMap<Point, GeoElement> getOneClickEditMap() {
		return oneClickEditMap;
	}

	public void setOneClickEditMap(HashMap<Point, GeoElement> oneClickEditMap) {
		this.oneClickEditMap = oneClickEditMap;
	}

	
	// cursors
	protected static Cursor defaultCursor = Cursor.getDefaultCursor(); 
	protected static Cursor crossHairCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	protected static Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
	protected static Cursor grabbingCursor, grabCursor, largeCrossCursor; 
	
	
	
	
	/*******************************************************************
	 * Constructor
	 */
	public MyTable(SpreadsheetView view, DefaultTableModel tableModel) {
		super(tableModel);

		app = view.getApplication();
		kernel = app.getKernel();
		this.tableModel = tableModel;
		this.view = view;
		table = this;
		grabCursor = createCursor(app.getImageIcon("cursor_grab.gif").getImage(), true);
		grabbingCursor = createCursor(app.getImageIcon("cursor_grabbing.gif").getImage(), true);
		largeCrossCursor = createCursor(app.getImageIcon("cursor_large_cross.gif").getImage(), true);
		
		// prepare column headers
		SpreadsheetColumnController columnController = new SpreadsheetColumnController(app,this);
		headerRenderer = columnController.new ColumnHeaderRenderer();
		getTableHeader().setFocusable(true);
		getTableHeader().addMouseListener(columnController);
		getTableHeader().addMouseMotionListener(columnController);
		getTableHeader().addKeyListener(columnController);
		getTableHeader().setReorderingAllowed(false);
		setAutoCreateColumnsFromModel(false);

		
		// set cell size 
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setRowHeight(TABLE_CELL_HEIGHT);
		headerRenderer.setPreferredSize(new Dimension(preferredColumnWidth, TABLE_CELL_HEIGHT));	
		for (int i = 0; i < getColumnCount(); ++ i) {
			getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
			getColumnModel().getColumn(i).setPreferredWidth(preferredColumnWidth);
		}
		
		
		// set visual appearance 	 
		setShowGrid(true); 	 
		setGridColor(TABLE_GRID_COLOR); 	
		setSelectionBackground( SELECTED_BACKGROUND_COLOR);
		setSelectionForeground(Color.BLACK);
		
		
		// add cell renderer & editors
		setDefaultRenderer(Object.class, new MyCellRenderer(app, view, this.getCellFormatHandler()));
		editorButton = new MyCellEditorButton();
		editorBoolean = new MyCellEditorBoolean(kernel);
		editorList = new MyCellEditorList();
		editor = new MyCellEditor(kernel);
		setDefaultEditor(Object.class, editor);

		// initialize selection fields
		selectedCellRanges = new ArrayList<CellRange>();
		selectedCellRanges.add(new CellRange(this));
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setCellSelectionEnabled(true);
		
		
		// add mouse and key listeners 
		SpreadsheetMouseListener ml = new SpreadsheetMouseListener(app,this);
		
		MouseListener[] mouseListeners = getMouseListeners();
		addMouseListener(ml); 
		for (int i = 0; i < mouseListeners.length; ++ i) { 
			removeMouseListener(mouseListeners[i]); 
			addMouseListener(mouseListeners[i]); 
		} 

		MouseMotionListener[] mouseMotionListeners = getMouseMotionListeners(); 
		addMouseMotionListener(ml); 
		for (int i = 0; i < mouseMotionListeners.length; ++ i) { 
			removeMouseMotionListener(mouseMotionListeners[i]); 
			addMouseMotionListener(mouseMotionListeners[i]); 
		} 

		// key listener 
		KeyListener[] defaultKeyListeners = getKeyListeners(); 
		for (int i = 0; i < defaultKeyListeners.length; ++ i) { 
			removeKeyListener(defaultKeyListeners[i]); 
		} 
		addKeyListener(new SpreadsheetKeyListener(app, this));

	
		// setup selection listener
		//TODO 
		//These listeners are no longer needed.
		//getSelectionModel().addListSelectionListener(new RowSelectionListener());
		//getColumnModel().getSelectionModel().addListSelectionListener(new ColumnSelectionListener());
		//getColumnModel().getSelectionModel().addListSelectionListener(columnHeader);
		
		// add table model listener
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
		
	
		// - see ticket #135
		 addFocusListener(this);
		
		// editing 	 
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		
		
		columnModelListener = new MyTableColumnModelListener();
		getColumnModel().addColumnModelListener(columnModelListener); 
		
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
	 * If none exists, a new one is created.
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
			col.setHeaderRenderer(headerRenderer);
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
	
	public void setLabels() {
		editor.setLabels();
	}
	
	
	
	//===============================================================
	//                   Selection
	//===============================================================
	
	
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
		
		//force column selection
		if(view.isColumnSelect()){
			setColumnSelectionInterval(columnIndex, columnIndex);
		}

		
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
		
		// if the selection has changed, update selected geos 
		boolean changed = !list.equals(app.getSelectedGeos());
		if(changed){
			app.setSelectedGeos(list);
			view.notifySpreadsheetSelectionChange();
		}
		
		// if the selection has changed or an empty cell has been clicked, repaint 
		if(changed || list.isEmpty()){			
			repaint();
			getTableHeader().repaint();
		}
		
		
		//System.out.println("------------------");
		//for (CellRange cr: selectedCellRanges)cr.debug();
		
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
	

	// By adding a call to selectionChanged in JTable's setRowSelectionInterval 
	// and setColumnSelectionInterval methods, selectionChanged becomes 
	// the sole handler for selection events.
	@Override
	public void setRowSelectionInterval(int row0, int row1) {
		setSelectionType(ROW_SELECT);
		super.setRowSelectionInterval(row0, row1);
		selectionChanged(); 
	}
	@Override
	public void setColumnSelectionInterval(int col0, int col1) {
		setSelectionType(COLUMN_SELECT);
		super.setColumnSelectionInterval(col0, col1);
		selectionChanged(); 
	}
	
	
	 
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
		
		Rectangle cellRect = getCellRect(row, column, false);
		if (min)
			return new Point(cellRect.x, cellRect.y);
		else 
			return new Point(cellRect.x + cellRect.width , cellRect.y + cellRect.height);
	}

	
	protected Point getMinSelectionPixel() {
		return getPixel(minSelectionColumn, minSelectionRow, true);
	}

	
	protected Point getMaxSelectionPixel() {
		return getPixel(maxSelectionColumn, maxSelectionRow, false);
	}

	/**
	 * Returns Point(columnIndex, rowIndex), the cell indices at the given pixel location as 
	 */
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

	public Rectangle getCellBlockRect(int column1, int row1, int column2, int row2, boolean includeSpacing){
		Rectangle r1 = getCellRect(row1, column1, includeSpacing);
		Rectangle r2 = getCellRect(row2, column2, includeSpacing);
		r1.setBounds(r1.x, r1.y, (r2.x - r1.x) + r2.width, (r2.y - r1.y) + r2.height);
		return r1;
	}
	
	public Rectangle getSelectionRect(boolean includeSpacing){
		return getCellBlockRect(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow,includeSpacing);
	}
	
	
	
	
	private Rectangle targetcellFrame;
	final static float dash1[] = { 2.0f };
	final static BasicStroke dashed = new BasicStroke(3.0f,
		      BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
	
	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);

		if(!view.hasViewFocus())
			return;
		
		Graphics2D g2 = (Graphics2D)graphics;

		if(targetcellFrame != null){
			g2.setColor(GeoGebraColorConstants.DARKBLUE);
			g2.setStroke(dashed);
			
			g2.draw(targetcellFrame);
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
			
			//Highlight the dragging dot if mouseover 
			if (isOverDot) 
				{graphics.setColor(Color.gray);}
			else
				//{graphics.setColor(Color.BLUE);}
				{graphics.setColor(selectionRectangleColor);}
			
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
	




	
	//This handles ctrl-select dragging of cell blocks
	// because JTable does not do this correctly. 
	// TODO: JTable is still making selections that are not overridden,
	// so sometimes you can still get unwanted extended selection.
	//
	protected void handleControlDragSelect(MouseEvent e) {

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

	

	
	@Override
	public int convertColumnIndexToModel(int viewColumnIndex) {
		return viewColumnIndex;    	
	}

	private boolean allowEditing = false;


	public boolean isAllowEditing() {
		return allowEditing;
	}

	public void setAllowEditing(boolean allowEditing) {
		this.allowEditing = allowEditing;
	}

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
	
	
	
	protected void handleAutoFunction(){

		if (targetCellLoc == null || selectedCellRanges.get(0).isEmpty()){
			app.setMoveMode();
			return;
		}
		
		String targetCellName = GeoElement.getSpreadsheetCellName(targetCellLoc.x, targetCellLoc.y);
		String cellRangeString = getCellRangeProcessor().getCellRangeString(selectedCellRanges.get(0));
	
		String cmd = null;
		if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_SUM) cmd = "Sum";
		else if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_COUNT) cmd = "Length";
		else if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_AVERAGE) cmd = "Mean";
		else if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_MAX) cmd = "Max";
		else if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_MIN) cmd = "Min";
			
		String expr = targetCellName + " = " + cmd + "[" + cellRangeString + "]";
		
		table.kernel.getAlgebraProcessor()
		.processAlgebraCommandNoExceptions(expr,false);
		
		changeSelection(targetCellLoc.y, targetCellLoc.x, false, false);
		app.setMoveMode();
		
	
	}
	
	
	
	
	
	//===========================================
	// copy/paste/cut/delete methods
	//
	// this is temporary code while cleaning up
	//===========================================
	public void copy(boolean altDown){
		copyPasteCut.copy(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow, altDown);
	}
	
	public boolean paste(){
		 return copyPasteCut.paste(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow);
	}
	public boolean cut(){
		 return copyPasteCut.cut(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow);
	}
	public boolean delete(){
		 return copyPasteCut.cut(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow);
	}
	
	
	
	private Cursor createCursor(Image cursorImage, boolean center){
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Point cursorHotSpot;
		if(center){
			cursorHotSpot = new Point(cursorImage.getWidth(null)/2,cursorImage.getHeight(null)/2);
		}else{
			cursorHotSpot= new Point(0,0);
		}
		Cursor cursor = toolkit.createCustomCursor(cursorImage, cursorHotSpot, null);
		return cursor;
	}

	
	
}
