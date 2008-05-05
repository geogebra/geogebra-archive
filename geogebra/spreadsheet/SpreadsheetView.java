
package geogebra.spreadsheet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import javax.swing.AbstractListModel;
import javax.swing.ListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.UIManager;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent ;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import geogebra.Application;
import geogebra.View;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.spreadsheet.MyTable.ListSelectionListener1;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.ListSelectionModel;

public class SpreadsheetView extends JScrollPane implements View
{

	public static final int ROW_HEADER_WIDTH = 27;
	
	private static final long serialVersionUID = 1L;

	protected MyTable table;
	protected MyTableModel tableModel;
	
	public SpreadsheetView(Application app, int columns, int rows) {
		/*
		JList table = new JList();
		setViewportView(table);
		table.setFocusable(true);
		table.addKeyListener(new KeyListener0());
		/**/
		Kernel kernel = app.getKernel();
		// table
		tableModel = new MyTableModel(rows, columns);
		table = new MyTable(tableModel, kernel);
		// row header list
		MyListModel listModel = new MyListModel(tableModel.getRowCount());
		JList rowHeader = new JList(listModel);
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
		//
		kernel.notifyAddAll(this);
		kernel.attach(this);
		/**/
	}
	
	public void add(GeoElement geo) {
		Point location = geo.getSpreadsheetCoords();
		if (location != null) {
			tableModel.setValueAt(geo, location.y, location.x);
		}
	}
	
	public void remove(GeoElement geo) {
		Point location = geo.getSpreadsheetCoords();
		if (location != null) {
			tableModel.setValueAt(null, location.y, location.x);
		}
	}
	
	public void rename(GeoElement geo) {
		Point location = geo.getOldSpreadsheetCoords();
		if (location != null) {
			tableModel.setValueAt(null, location.y, location.x);
		}
		add(geo);
	}
	
	public void update(GeoElement geo) {
		Point location = geo.getSpreadsheetCoords();
		if (location != null) {
		}
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		update(geo);
	}
	
	public void repaintView() {
	}
	
	public void reset() {
	}
	
	public void clearView() {
	}
		
	public static class MyListModel extends AbstractListModel {
		
		private static final long serialVersionUID = 1L;

		protected String[] headers;
		
		public MyListModel(int rows) {
			headers = new String[rows];
			for (int i = 0; i < headers.length; ++ i) {
				headers[i] = "" + (i + 1);
			}
		}
		
		public int getSize() {
			return headers.length;
		}
		
		public Object getElementAt(int index) {
			return headers[index];
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
			setFont(header.getFont());
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
			int x = e.getX();
			int y = e.getY();
			if (e.getButton() == MouseEvent.BUTTON1) {			
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
					//else if (ctrlPressed) {					
					//	row0 = (int)point.getY();
					//	table.addRowSelectionInterval(row0, row0);
					//}
					else {
						row0 = (int)point.getY();
						table.setRowSelectionInterval(row0, row0);
					}
					table.repaint();
				}
			}
			else if (e.getButton() == MouseEvent.BUTTON3) {
				if (minSelectionRow != -1 && maxSelectionRow != -1) {
					ContextMenuRow.showPopupMenu2(table, e.getComponent(), 0, minSelectionRow, 25, maxSelectionRow, x, y);
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
	
	public boolean ctrlPressed = false;
	public boolean shiftPressed = false;

	protected class KeyListener1 implements KeyListener 
	{
		
		public void keyTyped(KeyEvent e) {
		}
		
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			System.out.println(keyCode);
			switch (keyCode) {
			case 16 : shiftPressed = true; break;
			case 17 : ctrlPressed = true; break;
			case 67 : // control + c
				if (ctrlPressed && minSelectionRow != -1 && maxSelectionRow != -1) {
					table.copyPasteCut.copy(0, minSelectionRow, 25, maxSelectionRow);
				}
				e.consume();
				break;
			case 86 : // control + v
				if (ctrlPressed && minSelectionRow != -1 && maxSelectionRow != -1) {
					table.copyPasteCut.paste(0, minSelectionRow, 25, maxSelectionRow);
				}
				e.consume();
				break;				
			case 88 : // control + x
				if (ctrlPressed && minSelectionRow != -1 && maxSelectionRow != -1) {
					table.copyPasteCut.copy(0, minSelectionRow, 25, maxSelectionRow);
				}
				e.consume();
				table.copyPasteCut.delete(0, minSelectionRow, 25, maxSelectionRow);
				break;
			case 127 : // delete
				table.copyPasteCut.delete(0, minSelectionRow, 25, maxSelectionRow);
				break;
			}
		}
		
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case 16 : shiftPressed = false; break;
			case 17 : ctrlPressed = false; break;
			}
		}
		
	}
		
	protected static class KeyListener0 implements KeyListener 
	{
		
		public void keyTyped(KeyEvent e) {
			int keyCode = e.getKeyChar();
			System.out.println("*** keyTyped:" + keyCode);
		}
		
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			System.out.println("*** keyPressed:" + keyCode);
		}
		
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			System.out.println("*** keyReleased:" + keyCode);
		}
		
	}
		
}
