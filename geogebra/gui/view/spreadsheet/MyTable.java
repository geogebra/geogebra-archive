package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianConstants;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
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
import java.util.Set;

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
	private static final long serialVersionUID = 1L;

	public static final int TABLE_MODE_STANDARD = 0;
	public static final int TABLE_MODE_AUTOFUNCTION = 1;	
	public static final int TABLE_MODE_DROP = 2;	
	private int tableMode = TABLE_MODE_STANDARD;

	public static final int MAX_CELL_EDIT_STRING_LENGTH = 10;

	public static final int TABLE_CELL_WIDTH = 70;
	public static final int TABLE_CELL_HEIGHT = 21;  //G.Sturr (old height 20) + 1 to stop cell editor clipping
	public static final int DOT_SIZE = 7;
	public static final int LINE_THICKNESS1 = 3;
	public static final int LINE_THICKNESS2 = 2;
	public static final Color SELECTED_BACKGROUND_COLOR = GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR; 
	public static final Color SELECTED_BACKGROUND_COLOR_HEADER = GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR_HEADER;
	public static final Color BACKGROUND_COLOR_HEADER = GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER;
	public static final Color TABLE_GRID_COLOR = GeoGebraColorConstants.GRAY2;
	public static final Color HEADER_GRID_COLOR = GeoGebraColorConstants.GRAY4;
	public static final Color SELECTED_RECTANGLE_COLOR = Color.BLUE;

	protected Kernel kernel;
	protected Application app;
	protected MyCellEditor editor;
	private MyCellEditorBoolean editorBoolean;
	private MyCellEditorButton editorButton;
	private MyCellEditorList editorList;

	protected RelativeCopy relativeCopy;
	protected CopyPasteCut copyPasteCut;
	protected SpreadsheetColumnController.ColumnHeaderRenderer headerRenderer;
	protected SpreadsheetView view;
	protected DefaultTableModel tableModel;
	private CellRangeProcessor crProcessor;
	private MyTable table;
	private MyTableColumnModelListener columnModelListener;

	private CellFormat formatHandler;

	private GeoElement targetCell;



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

	public boolean isOverDnDRegion() {
		return isOverDnDRegion;
	}


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

	public int preferredColumnWidth = TABLE_CELL_WIDTH;



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
	protected  Cursor defaultCursor = Cursor.getDefaultCursor(); 
	protected  Cursor crossHairCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	protected  Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
	protected  Cursor grabbingCursor, grabCursor; 




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

		// set row height 	
		setRowHeight(TABLE_CELL_HEIGHT);


		// prepare column headers
		SpreadsheetColumnController columnController = new SpreadsheetColumnController(app,this);
		headerRenderer = columnController.new ColumnHeaderRenderer();
		getTableHeader().setFocusable(true);
		getTableHeader().addMouseListener(columnController);
		getTableHeader().addMouseMotionListener(columnController);
		getTableHeader().addKeyListener(columnController);
		getTableHeader().setReorderingAllowed(false);
		setAutoCreateColumnsFromModel(false);

		// set columns and column headers
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

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
	 * Returns boolean editor (checkbox) for this table.
	 * If none exists, a new one is created.
	 */
	public MyCellEditorBoolean getEditorBoolean() {
		if(editorBoolean == null)
			editorBoolean = new MyCellEditorBoolean(kernel);
		return editorBoolean;
	}

	/**
	 * Returns button editor for this table.
	 * If none exists, a new one is created.
	 */
	public MyCellEditorButton getEditorButton() {
		if(editorButton == null)
			editorButton = new MyCellEditorButton();
		return editorButton;
	}

	/**
	 * Returns list editor (comboBox) for this table.
	 * If none exists, a new one is created.
	 */
	public MyCellEditorList getEditorList() {
		if(editorList == null)
			editorList = new MyCellEditorList();
		return editorList;
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
				return getEditorBoolean();
			case GeoElement.GEO_CLASS_BUTTON:
				return getEditorButton();
			case GeoElement.GEO_CLASS_LIST:
				return getEditorList();					
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

	public int preferredColumnWidth() {
		return preferredColumnWidth;
	}

	public void setPreferredColumnWidth(int preferredColumnWidth) {
		this.preferredColumnWidth = preferredColumnWidth;
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
		// select the upper left corner cell
		changeSelection(0,0,false,false);
		// extend the selection to the current lower right corner cell 
		changeSelection(getRowCount()-1,getColumnCount()-1, false, true);
		setSelectAll(true);
		this.setAutoscrolls(true);

		//	this.scrollRectToVisible(getCellRect(0,0,true));
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


		// check for change in anchor cell (for now this is minrow and mincol ...)
		boolean changedAnchor =  minSelectionColumn - newSelection.getMinColumn() != 0 ||
		minSelectionRow - newSelection.getMinRow() != 0 ;



		// update internal selection variables
		newSelection.setActualRange();  
		minSelectionColumn = newSelection.getMinColumn();
		maxSelectionColumn = newSelection.getMaxColumn();
		minSelectionRow = newSelection.getMinRow();
		maxSelectionRow = newSelection.getMaxRow();

		//newSelection.debug();
		//printSelectionParameters();

		if(isSelectNone && (minSelectionColumn != -1 || minSelectionRow != -1)) 
			setSelectNone(false);

		if(changedAnchor && !isEditing())
			view.updateFormulaBar();


		// update the geo selection list
		ArrayList list = new ArrayList();
		for (int i = 0; i < selectedCellRanges.size(); i++) {
			list.addAll(0,(selectedCellRanges.get(i)).toGeoList());
		}

		// if the geo selection has changed, update selected geos 
		boolean changed = !list.equals(app.getSelectedGeos());
		if(changed){

			if(getTableMode() == TABLE_MODE_AUTOFUNCTION){
				table.updateAutoFunction();
			}

			if(view.isVisibleStyleBar())
				view.getSpreadsheetStyleBar().updateStyleBar();


			app.setSelectedGeos(list);
			view.notifySpreadsheetSelectionChange();
		}

		// if the selection has changed or an empty cell has been clicked, repaint 
		if(changed || list.isEmpty()){			
			repaint();
			if(table.getTableHeader() != null)
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


	public  boolean setSelection(String cellName){

		if(cellName == null) 
			return setSelection(-1,-1,-1,-1);

		Point newCell = GeoElement.spreadsheetIndices(cellName);
		if(newCell.x != -1 && newCell.y != -1)
			return setSelection(newCell.x, newCell.y);
		else 
			return false;
	}



	public  boolean setSelection(int c, int r){
		CellRange cr = new CellRange(this,c,r,c,r);
		return setSelection(cr);
	}

	public boolean setSelection(int c1, int r1, int c2, int r2){

		CellRange cr = new CellRange(this,c1,r1,c2,r2);
		if(!cr.isValid()) return false;

		//ArrayList<CellRange> list = new ArrayList<CellRange>();
		//list.add(cr);

		return setSelection(cr);

	}

	public boolean setSelection(CellRange cr) {

		if(!cr.isValid()) return false;

		try {
			if (cr == null || cr.isEmptyRange()) {
				getSelectionModel().clearSelection();

			} else {

				this.setAutoscrolls(false);

				// row selection
				if (cr.isRow()) {
					setRowSelectionInterval(cr.getMinRow(), cr.getMaxRow());

					// column selection	
				} else if (cr.isColumn()) {
					setColumnSelectionInterval(cr.getMinColumn(), cr.getMaxColumn());

					// cell block selection	
				} else {
					setSelectionType(CELL_SELECT);
					changeSelection(cr.getMinRow(), cr.getMinColumn(), false, false);
					changeSelection(cr.getMaxRow(), cr.getMaxColumn(), false, true);
				}

				selectionChanged();

				// scroll to upper left corner of rectangle
				this.setAutoscrolls(true);
				scrollRectToVisible(getCellRect(cr.getMinRow(), cr.getMinColumn(),true));
				repaint();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}


	//TODO Handle selection for a list of cell ranges



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
	private boolean isSelectNone = false;


	public boolean isSelectNone() {
		return isSelectNone;
	}

	public void setSelectNone(boolean isSelectNone) {

		this.isSelectNone = isSelectNone;

		if(isSelectNone == true){
			setSelection(-1,-1,-1,-1);
			view.updateFormulaBar();
		}

	}


	public boolean isSelectAll() {	
		return isSelectAll;
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
	 * Returns Point(columnIndex, rowIndex), cell indices for the given pixel location  
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


	// draws a grid line beneath the give row
	private void drawGridRow(Graphics2D g2, int row){

		Rectangle rect1 = getCellRect(row, 0, true);
		int r1 = rect1.x-1;
		int c1 = rect1.y-1;
		Rectangle rect2 = getCellRect(row, getColumnCount(), true);
		int r2 = rect2.x-1;
		int c2 = rect2.y-1;

		g2.drawLine(r1, c1, r2, c2);

	}

	// draws a grid line to the right the give column
	private void drawGridColumn(Graphics2D g2, int column){

		Rectangle rect1 = getCellRect(0, column, true);
		int r1 = rect1.x-1;
		int c1 = rect1.y-1;
		Rectangle rect2 = getCellRect(getRowCount(), column, true);
		int r2 = rect2.x-1;
		int c2 = rect2.y-1;

		g2.drawLine(r1, c1, r2, c2);

	}


	private void drawGridLine(Graphics2D g2, int col1, int row1, int col2, int row2){

		Rectangle rect1 = this.getCellRect(row1, col1, true);
		int r1 = rect1.x-1;
		int c1 = rect1.y-1;
		Rectangle rect2 = this.getCellRect(row2, col2, true);
		int r2 = rect2.x-1;
		int c2 = rect2.y-1;

		g2.drawLine(r1, c1, r2, c2);

	}

	private void drawBoxPartial(Graphics2D g2, int col1, int row1, int col2, int row2, byte v){

		Rectangle rect1 = this.getCellRect(row1, col1, true);
		int r1 = rect1.x-1;
		int c1 = rect1.y-1;
		Rectangle rect2 = this.getCellRect(row2, col2, true);
		int r2 = rect2.x-1;
		int c2 = rect2.y-1;

		// left bar
		if(!isZeroBit(v,0))
			g2.drawLine(r1, c1, r1, c2);
		// top bar
		if(!isZeroBit(v,1))
			g2.drawLine(r1, c1, r2, c1);
		// right bar
		if(!isZeroBit(v,2))
			g2.drawLine(r2, c1, r2, c2);
		// bottom bar
		if(!isZeroBit(v,3))
			g2.drawLine(r1, c2, r2, c2);

	}

	static public boolean isZeroBit(int value, int position){
		return (value &= (1 << position)) == 0;
	} 


	private void handleRowColumnGridFormat(Graphics2D g2, int col, int row, byte v){

		// row
		if(col == -1){
			// top bar
			if(!isZeroBit(v,1))
				drawGridRow(g2, row);
			// bottom bar
			if(!isZeroBit(v,3))
				drawGridRow(g2, row+1);
		}

		// column
		if(row == -1){
			// left bar
			if(!isZeroBit(v,0))
				drawGridColumn(g2, col);
			// right bar
			if(!isZeroBit(v,2))
				drawGridColumn(g2, col+1);
		}
	}


	private void drawFormatBorders(Graphics2D g2){

		g2.setColor(GeoGebraColorConstants.BLACK);
		g2.setStroke(new BasicStroke(1));

		HashMap<Point,Object> map = getCellFormatHandler().getFormatMap(CellFormat.FORMAT_BORDER);
		Set<Point> formatCell = map.keySet();

		int c = 0,r = 0;
		for(Point cell:formatCell){

			Byte b = (Byte) getCellFormatHandler().getCellFormat(cell, CellFormat.FORMAT_BORDER);
			if(b != null){
				c = cell.x;
				r  = cell.y;
				//System.out.println(cell.toString());
				if(c == -1 || r == -1)
					handleRowColumnGridFormat(g2, c, r, b);
				else
					drawBoxPartial(g2,c,r,c+1,r+1,b);
			}
		}

	}


	private Rectangle targetcellFrame;


	public Rectangle getTargetcellFrame() {
		return targetcellFrame;
	}

	public void setTargetcellFrame(Rectangle targetcellFrame) {
		this.targetcellFrame = targetcellFrame;
	}


	final static float dash1[] = { 2.0f };
	final static BasicStroke dashed = new BasicStroke(3.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);

		Graphics2D g2 = (Graphics2D)graphics;

		drawFormatBorders(g2);


		if(targetcellFrame != null){
			g2.setColor(GeoGebraColorConstants.DARKBLUE);
			g2.setStroke(dashed);

			g2.draw(targetcellFrame);
		}


		if(!view.hasViewFocus()){
			if(!isSelectNone)
				setSelectNone(true);
			return;
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
			if(geo.isGeoButton()
					|| geo.isGeoImage()){
				app.getGuiManager().showPropertiesDialog();
				return true;
			}
			if(!view.getShowFormulaBar()){
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
		if(view.isColumnSelect()) 
			return false;

		// allow use of special editors for e.g. buttons, lists
		if (view.allowSpecialEditor() && oneClickEditMap.containsKey(new Point(column,row))) 
			return true; 
		
		// normal case: return false so we can handle double click in our mouseReleased  
		if (!allowEditing) 
			return false; 
		
		// prevent editing fixed geos when allowEditing == true 
		GeoElement geo = (GeoElement) getModel().getValueAt(row, column);
		if (geo != null && geo.isFixed()) 
			return false;

		// return true when editing is allowed (mostly for blank cells). This lets 
		// the JTable mousePressed listener catch double clicks and invoke the editor 
		return true;
	}


	public void updateEditor(String text){
		if(this.isEditing()){
			editor.setText(text);
		}
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





	// Keep row heights of table and rowHeader in sync
	@Override
	public void setRowHeight(int row, int rowHeight) {
		super.setRowHeight(row, rowHeight);	
		try {
			if(view != null){
				view.updateRowHeader();
				if(doRecordRowHeights)
					adjustedRowHeights.add(new Point(row, rowHeight));
				view.updateRowHeightSetting(row, rowHeight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setRowHeight(int rowHeight) {
		super.setRowHeight(rowHeight);
		try {
			if(view != null){
				view.updateRowHeader();
				view.updatePreferredRowHeight(rowHeight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Reset the row heights --- used after addColumn destroys the row heights
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
	 * Enlarge the row and/or column of a cell to fit the cell's preferred size. 
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
	 * Adjust the width of a column to fit the maximum preferred width of 
	 * its cell contents.
	 */
	public void fitColumn(int column){

		TableColumn tableColumn = table.getColumnModel().getColumn(column); 

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
		// note: the table might have its header set to null, 
		// so we get the actual header from view
		view.getTableHeader().setResizingColumn(tableColumn);
		tableColumn.setWidth(prefWidth
				+ getIntercellSpacing().width);

	}

	/**
	 * Adjust the height of a row to fit the maximum preferred height of 
	 * the its cell contents. 
	 */
	public void fitRow(int row){

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
			if(isSelectAll() && minSelectionColumn >= 0){
				preferredColumnWidth = table.getColumnModel().getColumn(minSelectionColumn).getPreferredWidth();
				//view.updatePreferredColumnWidth(preferredColumnWidth);
			}
			// TODO: find more efficient way to record column widths
			view.updateAllColumnWidthSettings();
		}

		public void columnAdded(TableColumnModelEvent arg0) {}
		public void columnMoved(TableColumnModelEvent arg0) {}
		public void columnRemoved(TableColumnModelEvent arg0) {}
		public void columnSelectionChanged(ListSelectionEvent arg0) {}
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





	//==================================================
	// Table mode change
	//==================================================



	public int getTableMode() { 
		return tableMode; 
	}

	/**
	 * Sets the table mode 
	 * @param tableMode
	 */
	public void setTableMode(int tableMode) { 

		if(tableMode == TABLE_MODE_AUTOFUNCTION){

			if(!initAutoFunction())  return;
		}

		else if(tableMode == TABLE_MODE_DROP){
			// nothing to do (yet)
		}

		else
		{
			// Clear the targetcellFrame and ensure the selection rectangle color is standard 
			targetcellFrame = null;
			this.setSelectionRectangleColor(Color.BLUE);
		}

		this.tableMode = tableMode; 
		repaint();
	}



	//==================================================
	// Autofunction handlers
	//==================================================

	/**
	 * Initializes the autoFunction feature. The targetCell is prepared and the
	 * GUI is adjusted to handle selection drag with an autoFunction
	 */
	protected boolean initAutoFunction(){


		// Selection is a single cell.
		// The selected cell is the target cell. Allow the user to drag a new selection for the
		// autoFunction. The autoFunction values are previewed in the targetCell while dragging.
		if(selectedCellRanges.size() == 1 && selectedCellRanges.get(0).isSingleCell()){

			// Clear the target cell, exit if this is not possible
			if(RelativeCopy.getValue(this, minSelectionColumn, minSelectionRow) != null){
				boolean isOK = copyPasteCut.delete(
						minSelectionColumn, minSelectionRow, minSelectionColumn, minSelectionRow);
				if(!isOK) 
					return false;
			}

			// Set targetCell as a GeoNumeric that can be used to preview the autofunction result
			// (later it will be set as a GeoList)
			targetCell = new GeoNumeric(kernel.getConstruction(),0);
			targetCell.setLabel(GeoElement.getSpreadsheetCellName(minSelectionColumn, minSelectionRow));
			targetCell.setUndefined();

			// Set the targetcellFrame so the Paint method can use it to draw a dashed frame 
			targetcellFrame = this.getCellBlockRect(minSelectionColumn, minSelectionRow, 
					minSelectionColumn, minSelectionRow, true);

			// Change the selection frame color to gray
			// and clear the current selection
			setSelectionRectangleColor(Color.GRAY);
			minSelectionColumn = -1;
			maxSelectionColumn = -1;
			minSelectionRow = -1;
			maxSelectionRow = -1;
			app.clearSelectedGeos();

		}

		// try to create autoFunction cell(s) adjacent to the selection
		else if(selectedCellRanges.size() == 1){

			try {
				performAutoFunctionCreation(selectedCellRanges.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Don't stay in this mode, we're done
			return false;
		}


		// Exit if any other type of selection exists
		else 
			return false;

		return true;
	}



	/**
	 * Creates autofunction cells based on the given cell range and the current autofunction mode.
	 */
	protected void performAutoFunctionCreation(CellRange cr){

		if(cr.isColumn() || cr.isRow()) return;

		boolean success = true;
		boolean isOK = true;
		GeoElement targetCell = null;
		CellRange targetRange;

		// Case 1: Partial row, targetCell created beneath the column 
		if(cr.isPartialRow() || (!cr.isPartialColumn() && Application.getShiftDown())){
			targetRange = new CellRange(this, cr.getMaxColumn() + 1, cr.getMinRow(), cr.getMaxColumn() + 1, cr.getMaxRow());
			for(int row = cr.getMinRow(); row <= cr.getMaxRow(); row ++){

				// try to clear the target cell, exit if this is not possible
				if(RelativeCopy.getValue(this, cr.getMaxColumn() + 1, row) != null){
					isOK = copyPasteCut.delete(cr.getMaxColumn() + 1, row, cr.getMaxColumn() + 1, row);	
				}
				// create new targetCell
				if(isOK){ 
					targetCell = new GeoNumeric(kernel.getConstruction(),0);
					targetCell.setLabel(GeoElement.getSpreadsheetCellName(cr.getMaxColumn() + 1, row));
					createAutoFunctionCell(targetCell, new CellRange(this, cr.getMinColumn(), row, cr.getMaxColumn(), row) );
				}
			}

			app.setMoveMode();
			setSelection(targetRange);
			repaint();
		}
		else {

			targetRange = new CellRange(this, cr.getMinColumn(), cr.getMaxRow() + 1, cr.getMaxColumn(), cr.getMaxRow() + 1);
			for(int col = cr.getMinColumn(); col <= cr.getMaxColumn(); col ++){

				// try to clear the target cell, exit if this is not possible
				if(RelativeCopy.getValue(this, col, cr.getMaxRow() + 1) != null){
					isOK = copyPasteCut.delete(col, cr.getMaxRow() + 1, col, cr.getMaxRow() + 1);	
				}
				// create new targetCell
				if(isOK){ 
					targetCell = new GeoNumeric(kernel.getConstruction(),0);
					targetCell.setLabel(GeoElement.getSpreadsheetCellName(col, cr.getMaxRow() + 1));
					createAutoFunctionCell(targetCell, new CellRange(this, col, cr.getMinRow(), col, cr.getMaxRow()) );
				}
			}

			app.setMoveMode();
			setSelection(targetRange);
			repaint();

		}

	}



	/**
	 * Stops the autofunction from updating and creates a new geo for the target
	 * cell based on the current autofunction mode.
	 */
	protected void stopAutoFunction(){

		setTableMode(TABLE_MODE_STANDARD);

		if(createAutoFunctionCell(targetCell, selectedCellRanges.get(0))){
			// select the new geo
			app.setMoveMode();
			Point coords = targetCell.getSpreadsheetCoords();
			changeSelection(coords.y, coords.x, false, false);
			repaint();
		}

	}



	/**
	 * Creates an autofunction in the given target cell based on the current
	 * autofunction mode and the given cell range.
	 */
	protected boolean createAutoFunctionCell(GeoElement targetCell, CellRange cr){

		boolean success = true;

		// Get the targetCell label and the selected cell range
		String targetCellLabel = targetCell.getLabel();
		String cellRangeString = getCellRangeProcessor().getCellRangeString(cr);

		// Create a String expression for the new autofunction command geo
		String cmd = null;
		if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_SUM) cmd = "Sum";
		else if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_COUNT) cmd = "Length";
		else if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_AVERAGE) cmd = "Mean";
		else if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_MAX) cmd = "Max";
		else if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_MIN) cmd = "Min";

		String expr = targetCellLabel + " = " + cmd + "[" + cellRangeString + "]";

		// Create the new geo
		if(!selectedCellRanges.get(0).contains(targetCell)){
			table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptions(expr,false);
		}else{
			targetCell.setUndefined();
			success = false;
		}

		return success;
	}






	/**
	 * Updates the autofunction by recalculating the autofunction value as the
	 * user drags the mouse to create a selection. The current autofunction
	 * value is displayed in the targetCell.
	 */
	public void updateAutoFunction(){

		if (targetCell == null || selectedCellRanges.get(0).isEmpty() || tableMode != TABLE_MODE_AUTOFUNCTION){
			app.setMoveMode();
			return;
		}

		// Get a string representation of the seleced range (e.g. A1:B3)
		String cellRangeString = getCellRangeProcessor().getCellRangeString(selectedCellRanges.get(0));

		// Build a String expression for the autofunction
		String cmd = null;
		if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_SUM) cmd = "Sum";
		else if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_COUNT) cmd = "Length";
		else if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_AVERAGE) cmd = "Mean";
		else if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_MAX) cmd = "Max";
		else if(view.getMode() == EuclidianConstants.MODE_SPREADSHEET_MIN) cmd = "Min";

		String expr = cmd + "[" + cellRangeString + "]";

		// Evaluate the autofunction and put the result in targetCell
		if(!selectedCellRanges.get(0).contains(targetCell))
			((GeoNumeric)targetCell).setValue(table.kernel.getAlgebraProcessor().evaluateToDouble(expr));
		else
			((GeoNumeric)targetCell).setUndefined();

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
