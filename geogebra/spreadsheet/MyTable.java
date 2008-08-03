package geogebra.spreadsheet;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent ;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.JLabel;

import geogebra.Application;
import geogebra.GeoGebra;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

public class MyTable extends JTable
{

	public static final int TABLE_CELL_WIDTH = 70;
	public static final int TABLE_CELL_HEIGHT = 20;
	public static final int DOT_SIZE = 7;
	public static final int LINE_THICKNESS1 = 3;
	public static final int LINE_THICKNESS2 = 2;
	public static final Color SELECTED_BACKGROUND_COLOR = Application.COLOR_SELECTION;
	public static final Color SELECTED_BACKGROUND_COLOR_HEADER = Color.white;
	
	private static final long serialVersionUID = 1L;
	
	protected Kernel kernel;
	protected MyCellEditor editor;
	protected RelativeCopy relativeCopy;
	protected CopyPasteCut copyPasteCut;
	protected KeyListener[] defaultKeyListeners;
	protected TableCellRenderer1 columnHeader;
	protected SpreadsheetView view;
	protected MyTableModel tableModel;

	public MyTable(MyTableModel tableModel, Kernel kernel0) {
		super(tableModel);
		this.tableModel = tableModel;
		kernel = kernel0;
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setCellSelectionEnabled(true);
		// set cell size and column header
		setRowHeight(TABLE_CELL_HEIGHT);
		columnHeader = new TableCellRenderer1();
		columnHeader.setPreferredSize(new Dimension(TABLE_CELL_WIDTH, TABLE_CELL_HEIGHT));
		for (int i = 0; i < getColumnCount(); ++ i) {
			getColumnModel().getColumn(i).setHeaderRenderer(columnHeader);
			getColumnModel().getColumn(i).setPreferredWidth(TABLE_CELL_WIDTH);
		}
		// add renderer & editor
		setDefaultRenderer(Object.class, new MyCellRenderer());
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
		defaultKeyListeners = getKeyListeners();
		for (int i = 0; i < defaultKeyListeners.length; ++ i) {
			removeKeyListener(defaultKeyListeners[i]);
		}
		addKeyListener(new KeyListener1());
		// setup selection listener
		getSelectionModel().addListSelectionListener(new ListSelectionListener1());
		getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener2());
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
		setGridColor(Color.gray);
		
		// editing
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE); 
	}
	
	public void setView(SpreadsheetView view0) {
		view = view0;		
	}

	public SpreadsheetView getView() {
		return view;		
	}

	protected int Column = -1;
	protected int minSelectionRow = -1;
	protected int maxSelectionRow = -1;
	protected int minSelectionColumn = -1;
	protected int maxSelectionColumn = -1;
	protected boolean isDragingDot = false;
	protected int dragingToRow = -1;
	protected int dragingToColumn = -1;
	
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
				MyTableModel model = (MyTableModel)getModel();
				for (int i = 0; i < cols.length; ++ i) {
					for (int j = 0; j < model.rowCount; ++ j) {
						GeoElement geo = RelativeCopy.getValue(this, cols[i], j);
						if (geo != null) {
							list.add(geo);							
						}
					}
				}
			}
			else if (cols.length == 0) {
				MyTableModel model = (MyTableModel)getModel();
				for (int i = 0; i < model.columnCount; ++ i) {
					for (int j = 0; j < rows.length; ++ j) {
						GeoElement geo = RelativeCopy.getValue(this, i, rows[j]);
						if (geo != null) {
							list.add(geo);							
						}
					}
				}
			}
		}
		kernel.getApplication().setSelectedGeos(list);
		//kernel.notifyRepaint();
	}
	
	protected Point getPixel(int column, int row, boolean min) {
		if (column < 0 || row < 0) {
			return null;
		}
		if (min && column == 0 && row == 0) {
			return new Point(0, 0);
		}
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
		if (isDragging2) {
			Point point1 = getPixel(minColumn2, minRow2, true);
			Point point2 = getPixel(maxColumn2, maxRow2, false);
			int x1 = (int)point1.getX();
			int y1 = (int)point1.getY();
			int x2 = (int)point2.getX();
			int y2 = (int)point2.getY();
			graphics.setColor(Color.GRAY);
			//System.out.println(x1 + "," + y1 + "," + x2 + "," + y2);
			graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS1);
			graphics.fillRect(x1, y1, LINE_THICKNESS1, y2 - y1);
			graphics.fillRect(x1, y2 - LINE_THICKNESS1, x2 - x1, LINE_THICKNESS1);
			graphics.fillRect(x2 - LINE_THICKNESS1, y1, LINE_THICKNESS1, y2 - y1);
		}
		if (dragingToRow != -1 && dragingToColumn != -1) {
			/*
			System.out.println("minSelectionRow = " + minSelectionRow);
			System.out.println("minSelectionColumn = " + minSelectionColumn);
			System.out.println("maxSelectionRow = " + maxSelectionRow);
			System.out.println("maxSelectionColumn = " + maxSelectionColumn);
			System.out.println("dragingToRow = " + dragingToRow);
			System.out.println("dragingToColumn = " + dragingToColumn);
			/**/
			// -|1|-
			// 2|-|3
			// -|4|-
			graphics.setColor(Color.GRAY);
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
		Point pixel1 = getMaxSelectionPixel();
		if (pixel1 != null && ! editor.isEditing()) {
			graphics.setColor(Color.BLUE);
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
			if (! editor.isEditing()) {
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS2);
				graphics.fillRect(x1, y1, LINE_THICKNESS2, y2 - y1);
				graphics.fillRect(x2 - LINE_THICKNESS2, y1, LINE_THICKNESS2, y2 - y1 - DOT_SIZE / 2 - 2);		
				graphics.fillRect(x1, y2 - LINE_THICKNESS2, x2 - x1 - DOT_SIZE / 2 - 2, LINE_THICKNESS2);
			}
			else {
				x1 -= LINE_THICKNESS2;
				x2 += LINE_THICKNESS2 - 1;
				y1 -= LINE_THICKNESS2;
				y2 += LINE_THICKNESS2 - 1;
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS2);
				graphics.fillRect(x1, y1, LINE_THICKNESS2, y2 - y1);
				graphics.fillRect(x2 - LINE_THICKNESS2, y1, LINE_THICKNESS2, y2 - y1);		
				graphics.fillRect(x1, y2 - LINE_THICKNESS2, x2 - x1, LINE_THICKNESS2);
			}
		}
	}
	
	protected String name0;
	protected String prefix0;
	protected boolean isDragging2 = false;
	protected int minColumn2 = -1;
	protected int maxColumn2 = -1;
	protected int minRow2 = -1;
	protected int maxRow2 = -1;
	
	protected class MouseListener1 implements MouseListener
	{
		
		public void mouseClicked(MouseEvent e) {
			if (editor.isEditing()) {
				String text = editor.getEditingValue();
				if (text.startsWith("=")) {
					Point point = getIndexFromPixel(e.getX(), e.getY());
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
		}
		
		public void mouseEntered(MouseEvent e) {
		}
		
		public void mouseExited(MouseEvent e) {
		}
		
		public void mousePressed(MouseEvent e) {			
			boolean rightClick = Application.isRightClick(e);
			
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
								prefix0 = text;
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
				else if (x1 >= x2 - range && y1 <= y2 + range && y1 >= y2 - range && y1 <= y2 + range) {
					isDragingDot = true;
					e.consume();
				}
			}
			
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
					//	minSelectionColumn = -1;
					//	minSelectionRow = -1;
					//	maxSelectionColumn = -1;
					//	maxSelectionRow = -1;
					}
					isDragingDot = false;
					dragingToRow = -1;
					dragingToColumn = -1;
					repaint();
				}
			}
			// RIGHT CLICK
			else if (rightClick) {
				int x1 = e.getX();
				int y1 = e.getY();
				if ((minSelectionColumn != -1 && maxSelectionColumn != -1) || (minSelectionRow != -1 && maxSelectionRow != -1)) {
					ContextMenu.showPopupMenu(MyTable.this, e.getComponent(), minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow, x1, y1);
				}
			}
		}		
		
	}
	
	protected class MouseMotionListener1 implements MouseMotionListener
	{
		
		public void mouseDragged(MouseEvent e) {
			if (editor.isEditing()) {
				Point point = getIndexFromPixel(e.getX(), e.getY());
				if (point != null) {
					int column2 = (int)point.getX();
					int row2 = (int)point.getY();
					int column1 = GeoElement.getSpreadsheetColumn(name0);
					int row1 = GeoElement.getSpreadsheetRow(name0);
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
					name1 = prefix0 + name1;
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
				if (point == null) {
					dragingToRow = -1;
					dragingToColumn = -1;
				}
				else {
					dragingToRow = (int)point.getY();
					dragingToColumn = (int)point.getX();
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
		
		public void mouseMoved(MouseEvent e) {
		}
		
	}
	
	public boolean ctrlPressed = false;
	public boolean shiftPressed = false;

	protected class KeyListener1 implements KeyListener 
	{
		
		public void keyTyped(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if (editor.isEditing()) {							
					editor.undoEdit();
					//editor.cancelCellEditing();
					//editor.stopCellEditing();
										
					editor.editing = false;
					//e.consume();
					//requestFocusInWindow(false);				   
				}
			}			
		}
		
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			
			boolean shiftDown = e.isShiftDown();
			boolean ctrlDown = Application.isControlDown(e);
			
			//System.out.println(keyCode);
			switch (keyCode) {
			case KeyEvent.VK_C:
			case KeyEvent.VK_V:
			case KeyEvent.VK_X:
			case KeyEvent.VK_DELETE:
			case KeyEvent.VK_BACK_SPACE:
				if (! editor.isEditing()) {
					if (ctrlPressed) {
						e.consume();
						if (keyCode == KeyEvent.VK_C) {
							copyPasteCut.copy(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow);
						}
						else if (keyCode == KeyEvent.VK_V) {
							copyPasteCut.paste(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow);
							getView().getRowHeader().revalidate();
						}
						else if (keyCode == KeyEvent.VK_X) {
							copyPasteCut.cut(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow);
						}
					}
					if (keyCode == KeyEvent.VK_DELETE ||
						keyCode == KeyEvent.VK_BACK_SPACE) {
						e.consume();
						//System.out.println("deleting...");
						copyPasteCut.delete(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow);
					}
					return;
				}
				break;
			
				
				
						
			}
			
				
			// arrow keys
			if (keyCode >= 37 && keyCode <= 40) {
				if (editor.isEditing())	{					
					return;			
				}
			}
			
			/*
			for (int i = 0; i < defaultKeyListeners.length; ++ i) {
				if (e.isConsumed()) break;
				defaultKeyListeners[i].keyPressed(e);			
			}
			*/
		}
		
		public void keyReleased(KeyEvent e) {
			
		}
		
	}

	protected class ListSelectionListener1 implements ListSelectionListener
	{
		
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel selectionModel = (ListSelectionModel)e.getSource();
			minSelectionRow = selectionModel.getMinSelectionIndex(); 
			maxSelectionRow = selectionModel.getMaxSelectionIndex();
			if (getSelectionModel().getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
				minSelectionColumn = 0;
				maxSelectionColumn = MyTable.this.getColumnCount() - 1;
			}
			selectionChanged();
		}
		
	}

	protected class ListSelectionListener2 implements ListSelectionListener
	{
		
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel selectionModel = (ListSelectionModel)e.getSource();
			minSelectionColumn = selectionModel.getMinSelectionIndex(); 
			maxSelectionColumn = selectionModel.getMaxSelectionIndex();
			selectionChanged();
		}
		
	}
	
	protected class MyCellRenderer extends DefaultTableCellRenderer
	{

		private static final long serialVersionUID = 1L;
		private Color defaultBackground;
		
		public MyCellRenderer() {
			this.setHorizontalAlignment(JLabel.TRAILING);
			defaultBackground = getBackground();
			if (getFont().getSize() == 0) {
				Font font1 = kernel.getApplication().getPlainFont();
				if (font1 == null || font1.getSize() == 0) {
					font1 = new Font("dialog", 0, 12);
				}
				setFont(font1);				
			}
		}
		
		public void setValue(Object value) {
			if (value == null) {
				setText("");
				this.setBackground(null);
			}
			else {
				GeoElement geo = (GeoElement)value;
				setFont(kernel.getApplication().boldFont);
				setForeground(geo.getLabelColor());
				
				String text = geo.toValueString();
				if (geo.hasIndexLabel()) {
					text = GeoElement.indicesToHTML(text, false);				
				}
				setText(text);
				this.setForeground(geo.getObjectColor());
				String label = ((GeoElement)value).getLabel();
				if (SpreadsheetView.selectedElems.contains(label) || geo.doHighlighting()) {
					//System.out.println(label);
					this.setBackground(MyTable.SELECTED_BACKGROUND_COLOR);
				}
				else {
					this.setBackground(defaultBackground);
				}
			}
		}
		
	}

	/*
	protected class KeyListener4 implements KeyListener 
	{
		
		public void keyTyped(KeyEvent e) {
			int keyCode = e.getKeyChar();
			int keyCode2 = e.getKeyCode();
			System.out.println("getKeyChar=" + keyCode);
			System.out.println("getKeyCode=" + keyCode2);
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
	
	protected class TableCellRenderer1 extends JLabel implements TableCellRenderer, ListSelectionListener
	{
		private static final long serialVersionUID = 1L;

    	protected boolean[] selected;
    	private Color defaultBackground;
    	
    	public TableCellRenderer1() {    		
    		super("", JLabel.CENTER);
    		setOpaque(true);
    		defaultBackground = getBackground();
    		setBorder(UIManager.getBorder("TableHeader.cellBorder" ));
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
				if (colIndex >= minSelectionColumn && colIndex <= maxSelectionColumn && selected != null && selected.length > colIndex && selected[colIndex]) {
					setBackground(MyTable.SELECTED_BACKGROUND_COLOR_HEADER);					
				}
				else {
					setBackground(defaultBackground);				
				}
			}
			return this;			
		}
		
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel selectionModel = (ListSelectionModel)e.getSource();
			minSelectionColumn = selectionModel.getMinSelectionIndex();
			maxSelectionColumn = selectionModel.getMaxSelectionIndex();
			selected = new boolean[getColumnCount()];
			for (int i = 0; i < selected.length; ++ i) {
				if (selectionModel.isSelectedIndex(i)) {
					selected[i] = true;
				}
			}
			getTableHeader().repaint();
		}
	}
	
	// for column header

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
			
			if (rightClick) {
				int x = e.getX();
				int y = e.getY();
			
				if (minSelectionColumn != -1 && maxSelectionColumn != -1) {
					ContextMenuCol.showPopupMenu2(MyTable.this, e.getComponent(), minSelectionColumn, 0, maxSelectionColumn, tableModel.rowCount - 1, x, y);
				}
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
		
	protected class MouseMotionListener2 implements MouseMotionListener
	{
		
		public void mouseDragged(MouseEvent e) {
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
		
			boolean metaDown = Application.isControlDown(e);
			
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_C : // control + c
				//System.out.println(minSelectionColumn);
				//System.out.println(maxSelectionColumn);
				if (metaDown && minSelectionColumn != -1 && maxSelectionColumn != -1) {
					copyPasteCut.copy(minSelectionColumn, 0, maxSelectionColumn, tableModel.rowCount - 1);
					e.consume();
				}
				break;
			case KeyEvent.VK_V : // control + v
				if (metaDown && minSelectionColumn != -1 && maxSelectionColumn != -1) {
					copyPasteCut.paste(minSelectionColumn, 0, maxSelectionColumn, tableModel.rowCount - 1);
					getView().getRowHeader().revalidate();
					e.consume();
				}
				break;		
			case KeyEvent.VK_X : // control + x
				if (metaDown && minSelectionColumn != -1 && maxSelectionColumn != -1) {
					copyPasteCut.cut(minSelectionColumn, 0, maxSelectionColumn, tableModel.rowCount - 1);
					e.consume();
				}
				break;
			case KeyEvent.VK_BACK_SPACE : // delete
			case KeyEvent.VK_DELETE : // delete
				copyPasteCut.delete(minSelectionColumn, 0, maxSelectionColumn, tableModel.rowCount - 1);
				break;
			}
		}
		
		public void keyReleased(KeyEvent e) {
		
		}
		
	}
		
    public int convertColumnIndexToModel(int viewColumnIndex) {
    	return viewColumnIndex;    	
    }
   	
}
