package geogebra.gui.view.spreadsheet;

import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class SpreadsheetColumnController implements KeyListener, MouseListener, MouseMotionListener {

	private Application app;
	private SpreadsheetView view;
	private Kernel kernel;
	private MyTable table;
	private DefaultTableModel model;	
	private MyCellEditor editor;


	protected int column0 = -1;
	protected boolean isResizing = false;


	public SpreadsheetColumnController(Application app, MyTable table){

		this.app = app;
		this.kernel = app.getKernel();
		this.table = table;
		this.view = table.getView();
		this.model = (DefaultTableModel) table.getModel();  
		this.editor = table.editor;

	}



	//=========================================================
	//       Mouse Listener Methods
	//=========================================================


	public void mouseClicked(MouseEvent e) {

		// Double clicking on a column boundary auto-adjusts the 
		// width of the column on the left

		if (isResizing && !Application.isRightClick(e) && e.getClickCount() == 2) {

			// get column to adjust
			int x = e.getX();
			int y = e.getY();
			Point point = table.getIndexFromPixel(x, y);
			Point testPoint = table.getIndexFromPixel(x-4, y);
			int col = (int) point.getX();
			if(point.getX()!= testPoint.getX()){
				col = col-1;
			}				

			// enlarge or shrink to fit the contents 
			table.fitColumn(col);

			e.consume();
		}	
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

		if(!view.hasViewFocus())
			app.getGuiManager().getLayout().getDockManager().setFocusedPanel(Application.VIEW_SPREADSHEET);

		
		if (!rightClick) {
			Point point = table.getIndexFromPixel(x, y);
			if (point != null) {
				Point point2 = table.getPixel((int)point.getX(), (int)point.getY(), true);
				Point point3 = table.getPixel((int)point.getX(), (int)point.getY(), false);
				int x2 = (int)point2.getX();
				int x3 = (int)point3.getX();
				isResizing = ! (x > x2 + 2 && x < x3 - 3);
				if (! isResizing) {

					if(table.getSelectionType() != MyTable.COLUMN_SELECT){
						table.setSelectionType(MyTable.COLUMN_SELECT);
						table.getTableHeader().requestFocusInWindow();
					}


					if (shiftDown) {
						if (column0 != -1) {
							int column = (int)point.getX();
							table.setColumnSelectionInterval(column0, column);
						}
					}
					else if (metaDown) {					
						column0 = (int)point.getX();
						//Note: ctrl-select now handled in table.changeSelection
						table.setColumnSelectionInterval(column0, column0);
					}
					else {
						column0 = (int)point.getX();
						table.setColumnSelectionInterval(column0, column0);
					}
					//repaint();
				}
			}

		}
	}

	public void mouseReleased(MouseEvent e)	{
		boolean rightClick = Application.isRightClick(e); 	 

		if (!kernel.getApplication().letShowPopupMenu()) return;    

		if (rightClick) { 	 

			if (!app.letShowPopupMenu()) return; 

			Point p = table.getIndexFromPixel(e.getX(), e.getY());	
			if (p == null) return;

			// if click is outside current selection then change selection
			if(p.getY() < table.minSelectionRow ||  p.getY() > table.maxSelectionRow 
					|| p.getX() < table.minSelectionColumn || p.getX() > table.maxSelectionColumn)
			{
				// switch to column selection mode and select column
				if(table.getSelectionType() != MyTable.COLUMN_SELECT)
					table.setSelectionType(MyTable.COLUMN_SELECT);

				//selectNone();
				table.setColumnSelectionInterval((int)p.getX(), (int)p.getX());
			}	

			//show contextMenu
			SpreadsheetContextMenu popupMenu = new SpreadsheetContextMenu(table, e.isShiftDown());
			popupMenu.show(e.getComponent(), e.getX(), e.getY());


		}
		else if (isResizing) {

			if (e.getClickCount() == 2 )return;

			int x = e.getX();
			int y = e.getY();
			Point point = table.getIndexFromPixel(x, y);
			if (point == null) return;
			Point point2 = table.getPixel((int)point.getX(), (int)point.getY(), false);
			int column = (int)point.getX();
			if (x < (int)point2.getX() - 3) {
				-- column;
			}

			if(x<=0) x=0; //G.Sturr 2010-4-10 prevent x=-1 with very small row size

			int width = table.getColumnModel().getColumn(column).getWidth();
			int[] selected = table.getSelectedColumns();
			if (selected == null) return;
			boolean in = false;
			for (int i = 0; i < selected.length; ++ i) {
				if (column == selected[i]) in = true;
			}
			if (! in) return;				
			for (int i = 0; i < selected.length; ++ i) {
				table.getColumnModel().getColumn(selected[i]).setPreferredWidth(width);					
			}
		}
	}




	//=========================================================
	//       MouseMotion Listener Methods
	//=========================================================



	public void mouseDragged(MouseEvent e) {

		if(Application.isRightClick(e))return; //G.Sturr 2009-9-30 

		if (isResizing) return;
		int x = e.getX();
		int y = e.getY();
		Point point = table.getIndexFromPixel(x, y);
		if (point != null) {
			int column = (int)point.getX();
			table.setColumnSelectionInterval(column0, column);
			//	repaint();
		}
	}

	public void mouseMoved(MouseEvent e) {
	}





	//=========================================================
	//       Key Listener Methods
	//=========================================================


	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {

		boolean metaDown = Application.isControlDown(e);
		boolean altDown = e.isAltDown();
		int keyCode = e.getKeyCode();

		switch (keyCode) {

		case KeyEvent.VK_C : // control + c
			//Application.debug(minSelectionColumn);
			//Application.debug(maxSelectionColumn);
			if (metaDown  && table.minSelectionColumn != -1 && table.maxSelectionColumn != -1) {
				table.copyPasteCut.copy(table.minSelectionColumn, 0, table.maxSelectionColumn, model.getRowCount() - 1, altDown);
				e.consume();
			}
			break;

		case KeyEvent.VK_V : // control + v
			if (metaDown && table.minSelectionColumn != -1 && table.maxSelectionColumn != -1) {
				boolean storeUndo = table.copyPasteCut.paste(table.minSelectionColumn, 0, table.maxSelectionColumn, model.getRowCount() - 1);					
				if (storeUndo)
					app.storeUndoInfo();
				view.getRowHeader().revalidate();
				e.consume();
			}
			break;		

		case KeyEvent.VK_X : // control + x
			if (metaDown && table.minSelectionColumn != -1 && table.maxSelectionColumn != -1) {
				boolean storeUndo = table.copyPasteCut.cut(table.minSelectionColumn, 0, table.maxSelectionColumn, model.getRowCount() - 1);
				if (storeUndo)
					app.storeUndoInfo();
				e.consume();
			}
			break;

		case KeyEvent.VK_BACK_SPACE : // delete
		case KeyEvent.VK_DELETE : // delete
			boolean storeUndo = table.copyPasteCut.delete(table.minSelectionColumn, 0, table.maxSelectionColumn, model.getRowCount() - 1);
			if (storeUndo)
				app.storeUndoInfo();
			break;
		}
	}

	public void keyReleased(KeyEvent e) {

	}




	//=========================================================
	//       Renderer Class
	//=========================================================
	
	
	protected class ColumnHeaderRenderer extends JLabel implements TableCellRenderer  
	{
		private static final long serialVersionUID = 1L;

		private Color defaultBackground;

		private ImageIcon traceIcon = new ImageIcon();
		private ImageIcon emptyIcon = new ImageIcon();

		public ColumnHeaderRenderer() {    		
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

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus,
				int rowIndex, int colIndex) {

			setText(value.toString());
			setIcon(emptyIcon);

			if (((MyTable)table).getSelectionType() == MyTable.ROW_SELECT) {
				setBackground(defaultBackground);
			} else {
				if (((MyTable)table).selectedColumnSet.contains(colIndex)
						|| (colIndex >= ((MyTable)table).minSelectionColumn && colIndex <= ((MyTable)table).maxSelectionColumn) ) {
					setBackground(MyTable.SELECTED_BACKGROUND_COLOR_HEADER);
				} else {
					setBackground(defaultBackground);
				}
			}
			if(view.getTraceManager().isTraceColumn(colIndex)){
				setIcon(traceIcon);
			}
			return this;
		}
	}


}
