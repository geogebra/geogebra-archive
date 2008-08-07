
package geogebra.spreadsheet;

import geogebra.Application;
import geogebra.View;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;

public class SpreadsheetView extends JScrollPane implements View
{

	public static final int ROW_HEADER_WIDTH = 30;
	
	private static final long serialVersionUID = 1L;

	protected MyTable table;
	protected MyTableModel tableModel;
	public JList rowHeader;
	protected Application app;
	
	public SpreadsheetView(Application app0, int columns, int rows) {
		/*
		JList table = new JList();
		setViewportView(table);
		table.setFocusable(true);
		table.addKeyListener(new KeyListener0());
		/**/
		app = app0;
		Kernel kernel = app.getKernel();
		// table
		tableModel = new MyTableModel(rows, columns);
		table = new MyTable(tableModel, kernel);
		tableModel.setTable(table);
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
		table.setView(this);
		// put the table and the row header list into a scroll plane
		setRowHeaderView(rowHeader);
		setViewportView(table);
		//
		kernel.notifyAddAll(this);
		kernel.attach(this);
		///
		Cross cross = new Cross(app);
		this.add(cross);
		cross.setBounds(5, 5, 5 + Cross.LENGTH, 5 + Cross.LENGTH);
		/**/
	}
	
	public void add(GeoElement geo) {
		// TODO: remove
		//Application.debug(new Date() + " ADD: " + geo);
		
		
		Point location = geo.getSpreadsheetCoords();
		if (location != null) {
			if (location.y >= tableModel.getRowCount()) {
				tableModel.setRowCount(location.y + 1);				
			}
			if (location.x >= tableModel.getColumnCount()) {
				tableModel.setColumnCount(location.x + 1);				
			}
			tableModel.setValueAt(geo, location.y, location.x);
		}
	}
	
	public void remove(GeoElement geo) {
		// TODO: remove	
		//Application.debug(new Date() + " REMOVE: " + geo);
				
		Point location = geo.getSpreadsheetCoords();
		if (location != null) {
			tableModel.setValueAt(null, location.y, location.x);
		}
	}
	
	public void rename(GeoElement geo) {
		//Application.debug(new Date() + " RENAME");
		Point location = geo.getOldSpreadsheetCoords();
		if (location != null) {
			if (location.y >= tableModel.getRowCount()) {
				tableModel.setRowCount(location.y + 1);				
			}
			if (location.x >= tableModel.getColumnCount()) {
				tableModel.setColumnCount(location.x + 1);				
			}
			tableModel.setValueAt(null, location.y, location.x);
		}
		add(geo);
	}
	
	public void updateAuxiliaryObject(GeoElement geo) {
		update(geo);
	}
	
	public static HashSet selectedElems = new HashSet();
	
	public void repaintView() {
		ArrayList elems = app.getSelectedGeos();
		selectedElems.clear();
		for (int i = 0; i < elems.size(); ++ i) {
			GeoElement geo = (GeoElement)elems.get(i);
			selectedElems.add(geo.getLabel());
		}
		if (System.currentTimeMillis() - table.selectionTime > 100) {
			table.selectNone();
		}
		repaint();
	}
	
	public void clearView() {
		//Application.debug(new Date() + " CLEAR VIEW");
		int rows = tableModel.getRowCount();
		int columns = tableModel.getColumnCount();
		for (int i = 0; i < columns; ++ i) {
			for (int j = 0; j < rows; ++ j) {
				tableModel.setValueAt(null, i, j);
			}
		}
	}
		
	public static class MyListModel extends AbstractListModel {
		
		private static final long serialVersionUID = 1L;
		
		protected MyTableModel model;

		public MyListModel(MyTableModel model0) {
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
				if (minSelectionRow != -1 && maxSelectionRow != -1) {
					ContextMenuRow.showPopupMenu2(table, e.getComponent(), 0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow, x, y);
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
			
			//Application.debug(keyCode);
			switch (keyCode) {				
			case KeyEvent.VK_C : // control + c
				if (metaDown && minSelectionRow != -1 && maxSelectionRow != -1) {
					table.copyPasteCut.copy(0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow);
				}
				e.consume();
				break;
			case KeyEvent.VK_V : // control + v
				if (metaDown && minSelectionRow != -1 && maxSelectionRow != -1) {
					table.copyPasteCut.paste(0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow);
				}
				e.consume();
				break;				
			case KeyEvent.VK_X : // control + x
				if (metaDown && minSelectionRow != -1 && maxSelectionRow != -1) {
					table.copyPasteCut.copy(0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow);
				}
				e.consume();
				table.copyPasteCut.delete(0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow);
				break;
				
			case KeyEvent.VK_DELETE : // delete
			case KeyEvent.VK_BACK_SPACE : // delete on MAC
				table.copyPasteCut.delete(0, minSelectionRow, table.getModel().getColumnCount() - 1, maxSelectionRow);
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
		
	
}
		

