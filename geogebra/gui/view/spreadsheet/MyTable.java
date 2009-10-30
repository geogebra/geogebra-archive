package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.regex.Matcher;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

public class MyTable extends JTable
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

	private static final long serialVersionUID = 1L;

	protected Kernel kernel;
	protected Application app;
	protected MyCellEditor editor;
	protected RelativeCopy relativeCopy;
	protected CopyPasteCut copyPasteCut;
	protected MyColumnHeaderRenderer columnHeader;
	protected SpreadsheetView view;
	protected DefaultTableModel tableModel;
	
	//(G.Sturr 2009-9-12) test for dragging dot highlight
	protected boolean isOverDot = false;
	//(G.Sturr)
	

	public MyTable(SpreadsheetView view, DefaultTableModel tableModel) {
		super(tableModel);

		this.tableModel = tableModel;
		this.view = view;
		app = view.getApplication();
		kernel = app.getKernel();

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//setAutoscrolls(true);

		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setCellSelectionEnabled(true);
		// set cell size and column header
		setRowHeight(TABLE_CELL_HEIGHT);
		columnHeader = new MyColumnHeaderRenderer();
		columnHeader.setPreferredSize(new Dimension(TABLE_CELL_WIDTH, TABLE_CELL_HEIGHT));
		
		for (int i = 0; i < getColumnCount(); ++ i) {
			getColumnModel().getColumn(i).setHeaderRenderer(columnHeader);
			getColumnModel().getColumn(i).setPreferredWidth(TABLE_CELL_WIDTH);
		}
		// add renderer & editor
		setDefaultRenderer(Object.class, new MyCellRenderer(app));
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
		getSelectionModel().addListSelectionListener(new RowSelectionListener());
		getColumnModel().getSelectionModel().addListSelectionListener(new ColumnSelectionListener());
		getColumnModel().getSelectionModel().addListSelectionListener(columnHeader);
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

		// editing 	 
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);	}


	public SpreadsheetView getView() {
		return view;		
	}

	/**
	 * 
	 * @param newColumnCount
	 */
	public void setMyColumnCount(int newColumnCount) {	
		int oldColumnCount = tableModel.getColumnCount();		
		if (newColumnCount <= oldColumnCount)
			return;

		// add new columns to table			
		for (int i = oldColumnCount; i < newColumnCount; ++i) {
			TableColumn col = new TableColumn(i);
			col.setHeaderRenderer(columnHeader);
			col.setPreferredWidth(MyTable.TABLE_CELL_WIDTH);
			addColumn(col);
		}	
		tableModel.setColumnCount(newColumnCount);	
	}

	protected int Column = -1;
	protected int minSelectionRow = -1;
	protected int maxSelectionRow = -1;
	protected int minSelectionColumn = -1;
	protected int maxSelectionColumn = -1;
	protected boolean isDragingDot = false;
	protected int dragingToRow = -1;
	protected int dragingToColumn = -1;
	public boolean[] selectedColumns;

	public void selectNone() {
		selectionChangedNonResponsive = true;
		this.clearSelection();
		selectionChangedNonResponsive = false;
	}	
	
	protected boolean selectionChangedNonResponsive = false;
	public long selectionTime = 0;

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

	public void paint(Graphics graphics) {
		super.paint(graphics);

		if (MyTable.this.getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_INTERVAL_SELECTION) {
			return;
		}
		
		//draw sprecial dragging frame for cell editor
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
		if (pixel1 != null && ! editor.isEditing()) {
			
			//(G.Sturr 20099-12) Highlight the dragging dot if mouseover 
			if (isOverDot) 
				{graphics.setColor(Color.gray);}
			else
				{graphics.setColor(Color.BLUE);}
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
			graphics.setColor(Color.BLUE);
			
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
	}
	
	/**
	 * Starts in-cell editing for cells with short editing strings. For strings longer
	 * than MAX_CELL_EDIT_STRING_LENGTH, the redefine dialog is shown.
	 */
	public boolean editCellAt(int row, int col) {
		Object ob = getValueAt(row, col);
		
		if (ob instanceof GeoElement) {
			GeoElement geo = (GeoElement) ob;
			if (!geo.isGeoText() && 
					editor.getEditorInitString(geo).length() > MAX_CELL_EDIT_STRING_LENGTH) {
				app.getGuiManager().showRedefineDialog(geo, false);
				return true;
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
			else if (app.getMode() != EuclidianView.MODE_SELECTION_LISTENER) {
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
			if (!rightClick && app.getMode() == EuclidianView.MODE_SELECTION_LISTENER) {
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
				if (MyTable.this.getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_INTERVAL_SELECTION) {
					setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
					setColumnSelectionAllowed(true);
					setRowSelectionAllowed(true);
				}
				Point point1 = getMaxSelectionPixel();
				if (point1 == null) return;
				int x1 = e.getX();
				int y1 = e.getY();
				int x2 = (int)point1.getX();
				int y2 = (int)point1.getY();
				int range = DOT_SIZE / 2;
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
					if (MyTable.this.getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_INTERVAL_SELECTION) {
						setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
						setColumnSelectionAllowed(true);
						setRowSelectionAllowed(true);
					}
					changeSelection((int) p.getY(), (int) p.getX(),false, false );
					selectionChanged();		
				}
				 					
				ContextMenu popupMenu = new ContextMenu(MyTable.this, minSelectionColumn, minSelectionRow, 
						maxSelectionColumn, maxSelectionRow, selectedColumns);
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
						getView().getRowHeader().revalidate();	
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
			if (geo != null) {
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
					getView().getRowHeader().revalidate();
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

				// move to top left of spreadsheet
				// if shift pressed, select cells too
				changeSelection(0, 0, false, e.isShiftDown());
				
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
				
			case KeyEvent.VK_PAGE_DOWN:	
			case KeyEvent.VK_PAGE_UP:	
			case KeyEvent.VK_ENTER:	
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
		}

	}

	protected class RowSelectionListener implements ListSelectionListener
	{

		public void valueChanged(ListSelectionEvent e) {			
			ListSelectionModel selectionModel = (ListSelectionModel)e.getSource();
			minSelectionRow = selectionModel.getMinSelectionIndex(); 
			maxSelectionRow = selectionModel.getMaxSelectionIndex();
			if (getSelectionModel().getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
				minSelectionColumn = 0;
				maxSelectionColumn = MyTable.this.getColumnCount() - 1;
			}
			/* removed Michael Borcherds 2008-08-08
			 * causes a bug when multiple rows are selected
			selected = new boolean[getColumnCount()];
			for (int i = 0; i < selected.length; ++ i) {
				if (selectionModel.isSelectedIndex(i)) {
					selected[i] = true;
				}
			}
			 */
			selectionChanged();
		}

	}

	protected class ColumnSelectionListener implements ListSelectionListener
	{

		public void valueChanged(ListSelectionEvent e) {			
			ListSelectionModel selectionModel = (ListSelectionModel)e.getSource();
			minSelectionColumn = selectionModel.getMinSelectionIndex(); 
			maxSelectionColumn = selectionModel.getMaxSelectionIndex();
			
			//G.Sturr 2009-9-30  added to allow drawing column selection rectangle
			if (getSelectionModel().getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
				minSelectionRow = 0;
				maxSelectionRow = MyTable.this.getRowCount() - 1;
			}
			// end G.Sturr
			
			selectedColumns = new boolean[getColumnCount()];
			for (int i = 0; i < selectedColumns.length; ++ i) {
				if (selectionModel.isSelectedIndex(i)) {
					selectedColumns[i] = true;
				}
			}
			selectionChanged();	
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

	protected class MyColumnHeaderRenderer extends JLabel implements TableCellRenderer, ListSelectionListener
	{
		private static final long serialVersionUID = 1L;

		private Color defaultBackground;

		public MyColumnHeaderRenderer() {    		
			super("", JLabel.CENTER);
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
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int colIndex) {
			setText(value.toString());
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
		}

		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel selectionModel = (ListSelectionModel)e.getSource();
			minSelectionColumn = selectionModel.getMinSelectionIndex();
			maxSelectionColumn = selectionModel.getMaxSelectionIndex();
			selectedColumns = new boolean[getColumnCount()];
			for (int i = 0; i < selectedColumns.length; ++ i) {
				if (selectionModel.isSelectedIndex(i)) {
					selectedColumns[i] = true;
				}
			}
			repaint();
		}
	}
	
	
	//  MouseListener2 is the column header listener
	//
	protected int column0 = -1;
	protected boolean isResizing = false;

	protected class MouseListener2 implements MouseListener
	{

		public void mouseClicked(MouseEvent e) {
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
						if (getSelectionModel().getSelectionMode() != ListSelectionModel.MULTIPLE_INTERVAL_SELECTION || 
								getColumnSelectionAllowed() == false) {
							setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
							setColumnSelectionAllowed(true);
							setRowSelectionAllowed(false);
							getTableHeader().requestFocusInWindow();
						}
						if (shiftDown) {
							if (column0 != -1) {
								int column = (int)point.getX();
								setColumnSelectionInterval(column0, column);
							}
						}
						else if (metaDown) {					
							column0 = (int)point.getX();
							addColumnSelectionInterval(column0, column0);
						}
						else {
							column0 = (int)point.getX();
							setColumnSelectionInterval(column0, column0);
						}
						repaint();
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
					if (getSelectionModel().getSelectionMode() != ListSelectionModel.MULTIPLE_INTERVAL_SELECTION ||
						getColumnSelectionAllowed() == true) {
						setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
						setColumnSelectionAllowed(true);
						setRowSelectionAllowed(false);
					}
					selectNone();
					setColumnSelectionInterval((int)p.getX(), (int)p.getX());
					
				}	
				
				//show contextMenu
				ContextMenuCol popupMenu = new ContextMenuCol(MyTable.this, minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow, 
						selectedColumns);
		        popupMenu.show(e.getComponent(), e.getX(), e.getY());
			
		        
				/*    (old code)
				if (minSelectionColumn != -1 && maxSelectionColumn != -1) {
					ContextMenuCol popupMenu = new ContextMenuCol(MyTable.this, minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow, selectedColumns);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}	
				*/
					
			}
			else if (isResizing) {				
				int x = e.getX();
				int y = e.getY();
				Point point = getIndexFromPixel(x, y);
				if (point == null) return;
				Point point2 = getPixel((int)point.getX(), (int)point.getY(), false);
				int column = (int)point.getX();
				if (x < (int)point2.getX() - 3) {
					-- column;
				}
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
				repaint();
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
			boolean metaDown = Application.isControlDown(e);
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
		}

	}

	public int convertColumnIndexToModel(int viewColumnIndex) {
		return viewColumnIndex;    	
	}

	private boolean allowEditing = false;

	/*
	 * we need to return false for this normally, otherwise we can't detect double-clicks
	 * 
	 */
	public boolean isCellEditable(int row, int column)
	{
		if (!allowEditing) return false; // to avoid getValueAt() unless necessary
		
		GeoElement geo = (GeoElement) getModel().getValueAt(row, column);
		
		if (geo != null && geo.isFixed()) return false;
		
		return true;
	}

}
