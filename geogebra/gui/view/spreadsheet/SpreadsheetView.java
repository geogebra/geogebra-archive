
package geogebra.gui.view.spreadsheet;

import geogebra.main.Application;
import geogebra.main.View;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class SpreadsheetView extends JScrollPane implements View
{

	public static final int ROW_HEADER_WIDTH = 30;
	
	private static final long serialVersionUID = 1L;

	protected MyTable table;
	protected DefaultTableModel tableModel;
	public JList rowHeader;
	protected Application app;
	private Kernel kernel;
	
	// if these are increased above 32000, you need to change traceRow to an int[]
	private static int MAX_COLUMNS = 9999; // TODO make sure this is actually used
	private static int MAX_ROWS = 9999; // TODO make sure this is actually used
	
	private int highestUsedColumn = -1; // for trace
	short[] traceRow = new short[MAX_COLUMNS + 1]; // for trace
	
	private static int DEFAULT_COLUMN_WIDTH = 70;
	
	public SpreadsheetView(Application app0, int columns, int rows) {
		/*
		JList table = new JList();
		setViewportView(table);
		table.setFocusable(true);
		table.addKeyListener(new KeyListener0());
		/**/
		app = app0;
		kernel = app.getKernel();
		// table
		tableModel = new DefaultTableModel(rows, columns);
		table = new MyTable(this, tableModel);
		
		// row header list
		MyListModel listModel = new MyListModel(tableModel);
		rowHeader = new JList(listModel);
		rowHeader.setFocusable(true);
		rowHeader.setAutoscrolls(false);
		rowHeader.addMouseListener(new MouseListener1());
		rowHeader.addMouseMotionListener(new MouseMotionListener1());
		rowHeader.addKeyListener(new KeyListener1());
		//rowHeader.setFixedCellWidth(MyTable.TABLE_CELL_WIDTH);
		rowHeader.setFixedCellWidth(ROW_HEADER_WIDTH);
		rowHeader.setFixedCellHeight(table.getRowHeight()); // + table.getRowMargin();
		rowHeader.setCellRenderer(new RowHeaderRenderer(table, rowHeader));
		// put the table and the row header list into a scroll plane
		setRowHeaderView(rowHeader);
		setViewportView(table);
		
		// Florian Sonner 2008-10-20
		setBorder(BorderFactory.createEmptyBorder());
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
	
	public int getHighestUsedColumn() {
		resetTraceRow(highestUsedColumn+1);
		resetTraceRow(highestUsedColumn+2);
		return highestUsedColumn;
	}
	
	private void resetTraceRow(int col) {
		if (col < MAX_COLUMNS) traceRow[col] = 1;
	}
	
	public int getTraceRow(int column) {
		if (column < 0 || column >= MAX_COLUMNS) return -1;
		if (traceRow[column] == 0) traceRow[column] = 1; //first call
		return (int)traceRow[column]++;
	}
	
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
		Point location = geo.getSpreadsheetCoords();
		if (location != null && location.x < MAX_COLUMNS && location.y < MAX_ROWS) {
			
			if (location.x > highestUsedColumn) highestUsedColumn = location.x;
			
			if (location.y >= tableModel.getRowCount()) {
				tableModel.setRowCount(location.y + 1);				
			}
			if (location.x >= tableModel.getColumnCount()) {
				table.setMyColumnCount(location.x + 1);				
			}
			tableModel.setValueAt(geo, location.y, location.x);
		}
		//Application.debug("highestUsedColumn="+highestUsedColumn);
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
	
	public static HashSet selectedElems = new HashSet();
	
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
		
    }

	protected int minSelectionRow = -1;
	protected int maxSelectionRow = -1;

	public class RowHeaderRenderer extends JLabel implements ListCellRenderer, ListSelectionListener {
	
    	private static final long serialVersionUID = 1L;
    	
    	protected JTableHeader header;
    	protected JList rowHeader;
    	protected ListSelectionModel selectionModel;
    	private Color defaultBackground;
	
		public RowHeaderRenderer(JTable table, JList rowHeader) {
	 		super("", JLabel.CENTER);
    		setOpaque(true);
    		defaultBackground = getBackground();
			
			this.rowHeader = rowHeader;
			header = table.getTableHeader() ;
//			setOpaque(true);
			setBorder(UIManager.getBorder("TableHeader.cellBorder" ));
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
			setText ((value == null) ? ""  : value.toString());
			if (minSelectionRow != -1 && maxSelectionRow != -1) {
				if (index >= minSelectionRow && index <= maxSelectionRow &&
						selectionModel.isSelectedIndex(index)) {
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
			selectionModel = (ListSelectionModel)e.getSource();
			minSelectionRow = selectionModel.getMinSelectionIndex();
			maxSelectionRow = selectionModel.getMaxSelectionIndex();
			rowHeader.repaint();
		}

    }

	protected int row0 = -1;

	protected class MouseListener1 implements MouseListener
	{
		
		public void mouseClicked(MouseEvent e) {
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
			
			// left click
			if (!rightClick) {								
				Point point = table.getIndexFromPixel(x, y);
				if (point != null) {
					if (table.getSelectionModel().getSelectionMode() != ListSelectionModel.MULTIPLE_INTERVAL_SELECTION ||
							table.getColumnSelectionAllowed() == true) {
						table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
						table.setColumnSelectionAllowed(false);
						table.setRowSelectionAllowed(true);
					}
					if (shiftPressed) {
						if (row0 != -1) {
							int row = (int)point.getY();
							table.setRowSelectionInterval(row0, row);
						}
					}
					else if (metaDown) {					
						row0 = (int)point.getY();
						table.addRowSelectionInterval(row0, row0);
					}
					else {
						row0 = (int)point.getY();
						table.setRowSelectionInterval(row0, row0);
					}
					table.repaint();
				}
			}	
			// RIGHT CLICK
			else {	
				if (!app.letShowPopupMenu()) return;    	
    		       
				if (minSelectionRow != -1 && maxSelectionRow != -1) {
					ContextMenuRow popupMenu = new ContextMenuRow(table, 0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow, new boolean[0]);
			        popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}	
						
			}
		}
		
		public void mouseReleased(MouseEvent e)	{
			
			
		}

	}
	
	protected class MouseMotionListener1 implements MouseMotionListener
	{
		
		public void mouseDragged(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			Point point = table.getIndexFromPixel(x, y);
			if (point != null) {
				int row = (int)point.getY();
				table.setRowSelectionInterval(row0, row);
				table.repaint();
			}
		}
		
		public void mouseMoved(MouseEvent e) {
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
			
			//Application.debug(keyCode);
			switch (keyCode) {				
			case KeyEvent.VK_C : // control + c
				if (metaDown && minSelectionRow != -1 && maxSelectionRow != -1) {
					table.copyPasteCut.copy(0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow, altDown);
				}
				e.consume();
				break;
			case KeyEvent.VK_V : // control + v
				if (metaDown && minSelectionRow != -1 && maxSelectionRow != -1) {
					boolean storeUndo = table.copyPasteCut.paste(0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow);
					if (storeUndo)
		 				app.storeUndoInfo();
				}
				e.consume();
				break;				
			case KeyEvent.VK_X : // control + x
				if (metaDown && minSelectionRow != -1 && maxSelectionRow != -1) {
					table.copyPasteCut.copy(0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow, altDown);
				}
				e.consume();
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
			
		}
		
	}
		

	/**/
		
	public void reset() {
	}	
	
	public void update(GeoElement geo) {
	}	
	
	/*
	public void add(GeoElement geo) {
	}
	public void remove(GeoElement geo) {
	}	
	public void rename(GeoElement geo) {
	}	
	public void updateAuxiliaryObject(GeoElement geo) {
	}	
	public void repaintView() {
	}
	public void clearView() {
	}
	
	/**/
	/**
	 * returns settings in XML format
	 */
	public String getXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<spreadsheetView>\n");
		
		int width = getWidth(); //getPreferredSize().width;
		int height = getHeight(); //getPreferredSize().height;
		
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
		return sb.toString();
	}
	
	public void setColumnWidth(int col, int width) {
		//Application.debug("col = "+col+" width = "+width);
		TableColumn column = table.getColumnModel().getColumn(col); 
		column.setPreferredWidth(width);
		//column.
	}
		
	
}
		

