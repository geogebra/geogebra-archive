package geogebra.spreadsheet;

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

public class MyTable extends JTable
{

	public static final int TABLE_CELL_WIDTH = 100;
	public static final int TABLE_CELL_HEIGHT = 20;
	public static final int DOT_SIZE = 5;
	public static final int LINE_THICKNESS = 3;
	
	private static final long serialVersionUID = 1L;
	
	protected Kernel kernel;
	protected RelativeCopy relativeCopy;
	protected MyCellEditor editor;

	public MyTable(MyTableModel tableModel, Kernel kernel0) {
		super(tableModel);
		kernel = kernel0;
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setCellSelectionEnabled(true);
		// set cell size
		setRowHeight(TABLE_CELL_HEIGHT);
		for (int i = 0; i < getColumnCount(); ++ i) {
			getColumnModel().getColumn(i).setPreferredWidth(TABLE_CELL_WIDTH);
		}
		// add renderer & editor
		setDefaultRenderer(Object.class, new MyCellRenderer());
		editor = new MyCellEditor(kernel);
		setDefaultEditor(Object.class, editor);
		// set selection colors
		setSelectionBackground(new Color(200, 220, 240));
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
		// setup selection listener
		getSelectionModel().addListSelectionListener(new ListSelectionListener1());
		getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener2());
		// relative copy
		relativeCopy = new RelativeCopy(this, kernel);
	}

	protected int Column = -1;
	protected int minSelectionRow = -1;
	protected int maxSelectionRow = -1;
	protected int minSelectionColumn = -1;
	protected int maxSelectionColumn = -1;
	protected boolean isDragingDot = false;
	protected int dragingToRow = -1;
	protected int dragingToColumn = -1;
	
	protected void selectionChanged() {
		repaint();
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
		return getPixel(minSelectionColumn - 1, minSelectionRow - 1, true);
	}
	
	protected Point getMaxSelectionPixel() {
		return getPixel(maxSelectionColumn, maxSelectionRow, false);
	}
	
	protected Point getIndexFromPixel(int x, int y) {
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
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS);
				graphics.fillRect(x1, y1, LINE_THICKNESS, y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS, x2 - x1, LINE_THICKNESS);
			}
			else if (dragingToRow > maxSelectionRow) { // 4
				Point point1 = getPixel(minSelectionColumn, maxSelectionRow + 1, true);
				Point point2 = getPixel(maxSelectionColumn, dragingToRow, false);
				int x1 = (int)point1.getX();
				int y1 = (int)point1.getY();
				int x2 = (int)point2.getX();
				int y2 = (int)point2.getY();
				graphics.fillRect(x1, y1, LINE_THICKNESS, y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS, x2 - x1, LINE_THICKNESS);
				graphics.fillRect(x2 - LINE_THICKNESS, y1, LINE_THICKNESS, y2 - y1);
			}
			else if (dragingToRow < minSelectionRow) { // 1
				Point point1 = getPixel(minSelectionColumn, dragingToRow, true);
				Point point2 = getPixel(maxSelectionColumn, minSelectionRow - 1, false);
				int x1 = (int)point1.getX();
				int y1 = (int)point1.getY();
				int x2 = (int)point2.getX();
				int y2 = (int)point2.getY();
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS);
				graphics.fillRect(x1, y1, LINE_THICKNESS, y2 - y1);
				graphics.fillRect(x2 - LINE_THICKNESS, y1, LINE_THICKNESS, y2 - y1);
			}
			else if (dragingToColumn > maxSelectionColumn) { // 3
				Point point1 = getPixel(maxSelectionColumn + 1, minSelectionRow, true);
				Point point2 = getPixel(dragingToColumn, maxSelectionRow, false);
				int x1 = (int)point1.getX();
				int y1 = (int)point1.getY();
				int x2 = (int)point2.getX();
				int y2 = (int)point2.getY();
				graphics.fillRect(x2 - LINE_THICKNESS, y1, LINE_THICKNESS, y2 - y1);
				graphics.fillRect(x1, y2 - LINE_THICKNESS, x2 - x1, LINE_THICKNESS);
				graphics.fillRect(x1, y1, x2 - x1, LINE_THICKNESS);
			}
		}
		Point pixel1 = getMaxSelectionPixel();
		if (pixel1 != null) {
			graphics.setColor(Color.BLUE);
			int x = (int)pixel1.getX() - (DOT_SIZE + 1) / 2;
			int y = (int)pixel1.getY() - (DOT_SIZE + 1) / 2;
			graphics.fillRect(x, y, DOT_SIZE, DOT_SIZE);
		}
	}
	
	protected class MouseListener1 implements MouseListener
	{
		
		private static final long serialVersionUID = 1L;

		public void mouseClicked(MouseEvent e) {
		}
		
		public void mouseEntered(MouseEvent e) {
		}
		
		public void mouseExited(MouseEvent e) {
		}
		
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Point point1 = getMaxSelectionPixel();
				if (point1 == null) return;
				int x1 = e.getX();
				int y1 = e.getY();
				int x2 = (int)point1.getX();
				int y2 = (int)point1.getY();
				int range = DOT_SIZE / 2;
				if (x1 >= x2 - range && y1 <= y2 + range && y1 >= y2 - range && y1 <= y2 + range) {
					isDragingDot = true;
					e.consume();
				}
				else if (editor.isEditing()) {
					String text = editor.getEditingValue();
					if (text.startsWith("=")) {
						e.consume();					
						Point point = getIndexFromPixel(e.getX(), e.getY());
						int column = (int)point.getX();
						int row = (int)point.getY();
						editor.addLabel(column, row);
					}	
				}
			}
		}
		
		public void mouseReleased(MouseEvent e)	 {
			if (isDragingDot) {
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
				relativeCopy.doCopy(minSelectionColumn, minSelectionRow, maxSelectionColumn, maxSelectionRow, x1, y1, x2, y2);
				isDragingDot = false;
				dragingToRow = -1;
				dragingToColumn = -1;
				repaint();
			}
		}
		
	}
	
	protected class MouseMotionListener1 implements MouseMotionListener
	{
		
		private static final long serialVersionUID = 1L;

		public void mouseDragged(MouseEvent e) {
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
	
	protected class ListSelectionListener1 implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel selectionModel = (ListSelectionModel)e.getSource();
			minSelectionRow = selectionModel.getMinSelectionIndex(); 
			maxSelectionRow = selectionModel.getMaxSelectionIndex();
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
		
		public void setValue(Object value) {
			if (value == null) {
				setText("");
			}
			else {
				setText(((GeoElement)value).toValueString());
			}
		}
		
	}

}
