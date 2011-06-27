package geogebra.gui.view.spreadsheet;


import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class SpreadsheetRowHeader extends JList 
implements MouseListener, MouseMotionListener, KeyListener, ListSelectionListener

{

	private Application app;
	private SpreadsheetView view;
	private MyTable table;
	private MyListModel listModel;


	
	// note: MyTable uses its own minSelectionRow and maxSelectionRow.
	// The selection listener keeps them in sync.
	private int minSelectionRow = -1 ; 
	private int maxSelectionRow = -1 ; 
	private ListSelectionModel selectionModel;


	// fields for resizing rows
	private static Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
	private Cursor otherCursor = resizeCursor; 
	private int mouseYOffset, resizingRow; 
	private boolean doRowResize = false;



	protected int row0 = -1;




	/***************************************************
	 * Constructor
	 */
	public SpreadsheetRowHeader(Application app, MyTable table){

		this.app = app;
		this.table = table;
		this.view = table.getView();


		listModel = new MyListModel((DefaultTableModel) table.getModel()); 
		this.setModel(listModel);

		setFocusable(true);
		setAutoscrolls(false);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		setFixedCellWidth(view.ROW_HEADER_WIDTH);

		setCellRenderer(new RowHeaderRenderer(table, this));

		table.getSelectionModel().addListSelectionListener(this);

	}



	public static class MyListModel extends AbstractListModel {

		private static final long serialVersionUID = 1L;
		protected DefaultTableModel model;

		public MyListModel(DefaultTableModel model) {
			this.model = model;
		}

		public int getSize() {
			return model.getRowCount();
		}

		public Object getElementAt(int index) {
			return "" + (index + 1);
		}

		//forces update of rowHeader, called after row resizing
		public Void changed() {
			this.fireContentsChanged(this, 0, model.getRowCount());
			return null;
		}

	}



	public void updateRowHeader() {
		listModel.changed();
	}



	public class RowHeaderRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 1L;

		protected JTableHeader header;
		protected JList rowHeader;
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

		}

		public Component getListCellRendererComponent(JList list, Object value,	int index, boolean  isSelected, boolean cellHasFocus) {

			// adjust row height to match spreadsheet table row height 
			Dimension size = getPreferredSize();
			size.height = table.getRowHeight(index);
			setPreferredSize(size);

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
			return this;
		}
	}



	/**
	 * Update the rowHeader list when row selection changes in the table
	 */
	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel selectionModel = (ListSelectionModel)e.getSource();
		minSelectionRow = selectionModel.getMinSelectionIndex();
		maxSelectionRow = selectionModel.getMaxSelectionIndex();
		repaint();
	}



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
		Cursor tmp = getCursor(); 
		setCursor(otherCursor); 
		otherCursor = tmp; 
	} 





	//===============================================
	//   Mouse Listener Methods
	//===============================================


	public void mouseClicked(MouseEvent e) {

		// Double clicking on a row boundary auto-adjusts the 
		// height of the row above the boundary (the resizingRow)

		if (resizingRow >= 0 && !Application.isRightClick(e) && e.getClickCount() == 2) {

			table.fitRow(resizingRow);
			e.consume();
		}
	}

	public void mouseEntered(MouseEvent e) { }

	public void mouseExited(MouseEvent e) { }

	public void mousePressed(MouseEvent e) {						
		boolean shiftPressed = e.isShiftDown();	
		boolean metaDown = Application.isControlDown(e);							
		boolean rightClick = Application.isRightClick(e);
				
		int x = e.getX();
		int y = e.getY();
		
		if(!view.hasViewFocus())
			app.getGuiManager().getLayout().getDockManager().setFocusedPanel(Application.VIEW_SPREADSHEET);

		
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
					requestFocusInWindow();
				}
				
				if (shiftPressed) {
					if (row0 != -1) {
						int row = (int)point.getY();
						table.setRowSelectionInterval(row0, row);
					}
				}	
				
			    // ctrl-select is handled in table
				
				else {
					row0 = (int)point.getY();
					table.setRowSelectionInterval(row0, row0);
				}
				table.repaint();
			}
		}
		
	}



	public void mouseReleased(MouseEvent e)	{

		boolean rightClick = Application.isRightClick(e);

		if (rightClick) { 			
			if (!app.letShowPopupMenu()) return; 


			Point p = table.getIndexFromPixel(e.getX(), e.getY());
			if (p == null) return;

			// if click is outside current selection then change selection
			if(p.getY() < minSelectionRow ||  p.getY() > maxSelectionRow 
					|| p.getX() < table.minSelectionColumn || p.getX() > table.maxSelectionColumn){

				// switch to row selection mode and select row
				if(table.getSelectionType() != table.ROW_SELECT){
					table.setSelectionType(table.ROW_SELECT);
				}

				table.setRowSelectionInterval((int)p.getY(), (int)p.getY());
			}	

			//show contextMenu		
			SpreadsheetContextMenu popupMenu = new SpreadsheetContextMenu(table, e.isShiftDown());
			popupMenu.show(e.getComponent(), e.getX(), e.getY());

		} 


		// If row resize has happened, resize all other selected rows
		if (doRowResize) {
			if (minSelectionRow != -1 && maxSelectionRow != -1
					&& (maxSelectionRow - minSelectionRow > 1)) {
				if (table.getSelectAll())
					table.setRowHeight(table.getRowHeight(resizingRow));
				else
					for (int row = minSelectionRow; row <= maxSelectionRow; row++) {
						table.setRowHeight(row, table.getRowHeight(resizingRow));
					}
			}
			doRowResize = false;
		}
	}


	//===============================================
	//  MouseMotion Listener Methods
	//===============================================



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

	}

	public void mouseMoved(MouseEvent e) {
		// Show resize cursor when mouse is over a row boundary
		if ( ( getResizingRow(e.getPoint()) >= 0 ) != (getCursor() == resizeCursor ) ){
			swapCursor();
		}
	}



	//===============================================
	//  Key Listener Methods
	//===============================================



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

	public void keyReleased(KeyEvent e) { }

}
