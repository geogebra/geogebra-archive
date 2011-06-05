package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.regex.Matcher;

import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;



public class SpreadsheetMouseListener implements MouseListener, MouseMotionListener
{
	
	protected String selectedCellName;
	protected String prefix0, postfix0;
	
	private Application app;
	private SpreadsheetView view;
	private Kernel kernel;
	private MyTable table;
	private DefaultTableModel model;	
	private MyCellEditor editor;
	
	private RelativeCopy relativeCopy;
	
	
	public SpreadsheetMouseListener(Application app, MyTable table){
		
		this.app = app;
		this.kernel = app.getKernel();
		this.table = table;
		this.view = table.getView();
		this.model = (DefaultTableModel) table.getModel();  
		this.editor = table.editor;
			
		this.relativeCopy = new RelativeCopy(table, kernel);
	}
	
	
	
	public void mouseClicked(MouseEvent e) {	

		boolean doubleClick = (e.getClickCount() != 1);

		Point point = table.getIndexFromPixel(e.getX(), e.getY());
		if (point != null) {

			if (doubleClick) {
				
				// auto-fill down if dragging dot is double-clicked
				if(table.isOverDot) {
					handleAutoFillDown();
					return;
				}  
				
				//otherwise, doubleClick edits cell
				
				if(!table.getOneClickEditMap().containsKey(point)){
					table.setAllowEditing(true);
					table.editCellAt(table.getSelectedRow(), table.getSelectedColumn()); 

					// workaround, see
					// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4192625
					final JTextComponent f = (JTextComponent)table.getEditorComponent();
					if (f != null) {
						f.requestFocus();
						f.getCaret().setVisible(true);
					}

					table.setAllowEditing(false);
				}
			}
		}

		if (editor.isEditing()) {
			String text = editor.getEditingValue();
			if (text.startsWith("=")) {
				point = table.getIndexFromPixel(e.getX(), e.getY());
				if (point != null) {
					int column = (int)point.getX();
					int row = (int)point.getY();
					GeoElement geo = RelativeCopy.getValue(table, column, row);
					if (geo != null) {
						e.consume();
					}
				}
			}	
			selectedCellName = null;
			prefix0 = null;
			table.isDragging2 = false;
			table.repaint();
		}
		else if (app.getMode() != EuclidianConstants.MODE_SELECTION_LISTENER) {
			int row = table.rowAtPoint(e.getPoint());
			int col = table.columnAtPoint(e.getPoint());
			GeoElement geo = (GeoElement) model.getValueAt(row, col);	
			// let euclidianView know about the click
			app.getEuclidianView().clickedGeo(geo, e);
		}
	
//		else
//		{ // !editor.isEditing()
//			int row = rowAtPoint(e.getPoint());
//			int col = columnAtPoint(e.getPoint());
//			GeoElement geo = (GeoElement) getModel().getValueAt(row, col);			
//			
//			// copy description into input bar when a cell is clicked on
//			copyDefinitionToInputBar(geo);
//			selectionChanged();	
//		}
	}				

	
	// automatic fill down from the dragging dot 
	public void handleAutoFillDown() {
		int col = table.getSelectedColumn();
		int row = table.maxSelectionRow;
		if(model.getValueAt(row,col) != null) {									
			// count nonempty cells below selection 
			// if no cells below, count left ... if none on the left, count right
			while (row < table.getRowCount() - 1 && model.getValueAt(row+1, col) != null) row++;
			if ( row - table.maxSelectionRow == 0 && col > 0) 
				while (row < table.getRowCount() - 1 && model.getValueAt(row+1, col-1) != null) row++;
			if (row - table.maxSelectionRow == 0 && table.maxSelectionColumn <= table.getColumnCount()-1 )
				while ( row < table.getRowCount() - 1 && model.getValueAt(row+1, table.maxSelectionColumn + 1) != null) row++;
			int rowCount = row - table.maxSelectionRow;
			
			// now fill down
			if (rowCount != 0){
				boolean succ = relativeCopy.doCopy(table.minSelectionColumn, table.minSelectionRow, table.maxSelectionColumn, table.maxSelectionRow,
						table.minSelectionColumn, table.maxSelectionRow + 1, table.maxSelectionColumn, table.maxSelectionRow + rowCount);
				if (succ) app.storeUndoInfo();		
			}
			table.isDragingDot = false;
		}
	}
	
	
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		boolean rightClick = Application.isRightClick(e); 
		
		// tell selection listener about click on GeoElement
		if (!rightClick && app.getMode() == EuclidianConstants.MODE_SELECTION_LISTENER) {
			int row = table.rowAtPoint(e.getPoint());
			int col = table.columnAtPoint(e.getPoint());
			GeoElement geo = (GeoElement) model.getValueAt(row, col);
			
			// double click or empty geo
			if (e.getClickCount() == 2 || geo == null) {
				table.requestFocusInWindow();
			}
			else {					
				// tell selection listener about click
				app.geoElementSelected(geo, false);
				e.consume();
				return;
			}
		}					

		if (!rightClick) {
			
			if(table.getSelectionType() != MyTable.CELL_SELECT){
				table.setSelectionType(MyTable.CELL_SELECT);
			}
			
			//force column selection
			if(view.isColumnSelect()){
				Point point = table.getIndexFromPixel(e.getX(), e.getY());
				if (point != null) {
					int column = (int)point.getX();
					table.setColumnSelectionInterval(column, column);
				}
			}
			
			
			/*
			if (MyTable.this.getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_INTERVAL_SELECTION) {
				setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
				setColumnSelectionAllowed(true);
				setRowSelectionAllowed(true);
			}
			*/
			
			Point point1 = table.getMaxSelectionPixel();
			if (point1 == null) return;
			int x1 = e.getX();
			int y1 = e.getY();
			int x2 = (int)point1.getX();
			int y2 = (int)point1.getY();
			int range = MyTable.DOT_SIZE / 2;
			
			// Handle click in another cell while editing a cell:
			// if the edit string begins with "=" then the clicked cell name
			// is inserted into the edit text
			if (editor.isEditing()) {
				String text = editor.getEditingValue();
				if (text.startsWith("=")) {
					Point point = table.getIndexFromPixel(e.getX(), e.getY());
					if (point != null) {
						int column = (int)point.getX();
						int row = (int)point.getY();
						GeoElement geo = RelativeCopy.getValue(table, column, row);
						if (geo != null) {
							String name = GeoElement.getSpreadsheetCellName(column, row);
							if (geo.isGeoFunction()) name += "(x)";
							selectedCellName = name;
							int caretPos = editor.getCaretPosition();
							prefix0 = text.substring(0, caretPos);
							postfix0 = text.substring(caretPos, text.length());
							table.isDragging2 = true;
							table.minColumn2 = column;
							table.maxColumn2 = column;
							table.minRow2 = row;
							table.maxRow2 = row;
							editor.addLabel(name);
							e.consume();
							table.repaint();
						}
					}
				}	
			}
			else if (x1 >= x2 - range && x1 <= x2 + range && y1 >= y2 - range && y1 <= y2 + range) {
				table.isDragingDot = true;
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
					Point point = table.getIndexFromPixel(e.getX(), e.getY());
					if (point != null) {
						int column = (int)point.getX();
						int row = (int)point.getY();
						if (column != editor.column || row != editor.row) {
							e.consume();
						}
					}
				}
				selectedCellName = null;
				prefix0 = null;
				postfix0 = null;
				table.isDragging2 = false;
				table.repaint();
			}
			if (table.isDragingDot) {
				if (table.dragingToColumn == -1 || table.dragingToRow == -1) return;
				int x1 = -1;
				int y1 = -1;
				int x2 = -1;
				int y2 = -1;
				// -|1|-
				// 2|-|3
				// -|4|-
				if (table.dragingToColumn < table.minSelectionColumn) { // 2
					x1 = table.dragingToColumn;
					y1 = table.minSelectionRow;
					x2 = table.minSelectionColumn - 1;
					y2 = table.maxSelectionRow;
				}
				else if (table.dragingToRow > table.maxSelectionRow) { // 4
					x1 = table.minSelectionColumn;
					y1 = table.maxSelectionRow + 1;
					x2 = table.maxSelectionColumn;
					y2 = table.dragingToRow;
				}
				else if (table.dragingToRow < table.minSelectionRow) { // 1
					x1 = table.minSelectionColumn;
					y1 = table.dragingToRow;
					x2 = table.maxSelectionColumn;
					y2 = table.minSelectionRow - 1;
				}
				else if (table.dragingToColumn > table.maxSelectionColumn) { // 3
					x1 = table.maxSelectionColumn + 1;
					y1 = table.minSelectionRow;
					x2 = table.dragingToColumn;
					y2 = table.maxSelectionRow;
				}
				boolean succ = relativeCopy.doCopy(table.minSelectionColumn, table.minSelectionRow, table.maxSelectionColumn, table.maxSelectionRow, x1, y1, x2, y2);
				if (succ) {
					app.storeUndoInfo();
					//	table.minSelectionColumn = -1;
					//	table.minSelectionRow = -1;
					//	table.maxSelectionColumn = -1;
					//	table.maxSelectionRow = -1;						
				}
				
				//(G.Sturr 2009-9-12) extend the selection to include the drag copy selection
				// and un-highlight dragging dot 
				table.changeSelection(table.dragingToRow, table.dragingToColumn, true, true);
				table.isOverDot = false;
				//(G.Sturr)
				
				table.isDragingDot = false;
				table.dragingToRow = -1;
				table.dragingToColumn = -1;
				table.repaint();
			}
		}
		
		// Alt click: copy definition to input field
		if (!table.isEditing() && e.isAltDown() && app.showAlgebraInput()) {
			int row = table.rowAtPoint(e.getPoint());
			int col = table.columnAtPoint(e.getPoint());
			GeoElement geo = (GeoElement) model.getValueAt(row, col);
		
			if (geo != null) {
				// F3 key: copy definition to input bar
				app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(3, geo);					
				return;
			}					
		}
		
		
		//handle right click
		if (rightClick){
			if (!kernel.getApplication().letShowPopupMenu()) return;
			
			Point p = table.getIndexFromPixel(e.getX(), e.getY());
			
			// change selection if right click is outside current selection
			if(p.getY() < table.minSelectionRow ||  p.getY() > table.maxSelectionRow 
					|| p.getX() < table.minSelectionColumn || p.getX() > table.maxSelectionColumn)
			{
				//switch to cell selection mode 

				if(table.getSelectionType() != MyTable.CELL_SELECT){
					table.setSelectionType(MyTable.CELL_SELECT);
				}
				
				//now change the selection
				table.changeSelection((int) p.getY(), (int) p.getX(),false, false );		
			}
			
			//create and show context menu 
			SpreadsheetContextMenu popupMenu = new SpreadsheetContextMenu(table, e.isShiftDown());
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
		
		
	}		



	public void mouseDragged(MouseEvent e) {
		if (editor.isEditing()) {
			Point point = table.getIndexFromPixel(e.getX(), e.getY());
			if (point != null && selectedCellName != null) {
				int column2 = (int)point.getX();
				int row2 = (int)point.getY();
				
				Matcher matcher = GeoElement.spreadsheetPattern.matcher(selectedCellName);
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
				table.minColumn2 = column1;
				table.maxColumn2 = column2;
				table.minRow2 = row1;
				table.maxRow2 = row2;
				table.repaint();
			}
			e.consume();
			return;
		}
		if (table.isDragingDot) {
			e.consume();
			int x = e.getX();
			int y = e.getY();
			Point point = table.getIndexFromPixel(x, y);
			
			//save the selected cell position so it can be re-selected if needed
			int row = table.getSelectedRow();
			int column = table.getSelectedColumn();
			
			if (point == null) {
				table.dragingToRow = -1;
				table.dragingToColumn = -1;
			}
			else {
				table.dragingToRow = (int)point.getY();
				table.dragingToColumn = (int)point.getX();							
				
				// increase size if we're at the bottom of the spreadsheet				
				if (table.dragingToRow + 1 == table.getRowCount() && table.dragingToRow < SpreadsheetView.MAX_ROWS) {
					model.setRowCount(table.getRowCount() +1);							
				}
				
				// increase size when you go off the right edge
				if (table.dragingToColumn + 1 == table.getColumnCount() && table.dragingToColumn < SpreadsheetView.MAX_COLUMNS) {
					
					table.setMyColumnCount(table.getColumnCount() +1);		
					view.getColumnHeader().revalidate();
					
					// Java's addColumn will clear selection, so re-select our cell 
					table.changeSelection(row, column, false, false);
					
				}
				
				// scroll to show "highest" selected cell
				table.scrollRectToVisible(table.getCellRect(point.y, point.x, true));
				
				
				// 1|2|3
				// 4|5|6
				// 7|8|9
				if (table.dragingToRow < table.minSelectionRow) {
					if (table.dragingToColumn < table.minSelectionColumn) { // 1
						int dy = table.minSelectionRow - table.dragingToRow;
						int dx = table.minSelectionColumn - table.dragingToColumn;
						if (dx > dy) {
							table.dragingToRow = table.minSelectionRow;
						}
						else {
							table.dragingToColumn = table.minSelectionColumn;
						}
					}
					else if (table.dragingToColumn > table.maxSelectionColumn) { // 3
						int dy = table.minSelectionRow - table.dragingToRow;
						int dx = table.dragingToColumn - table.maxSelectionColumn;
						if (dx > dy) {
							table.dragingToRow = table.minSelectionRow;
						}
						else {
							table.dragingToColumn = table.maxSelectionColumn;
						}
					}
					else { // 2
						table.dragingToColumn = table.minSelectionColumn;
					}
				}
				else if (table.dragingToRow > table.maxSelectionRow) {
					if (table.dragingToColumn < table.minSelectionColumn) { // 7
						int dy = table.dragingToRow - table.minSelectionRow;
						int dx = table.minSelectionColumn - table.dragingToColumn;
						if (dx > dy) {
							table.dragingToRow = table.minSelectionRow;
						}
						else {
							table.dragingToColumn = table.maxSelectionColumn;
						}
					}
					else if (table.dragingToColumn > table.maxSelectionColumn) { // 9
						int dy = table.dragingToRow - table.maxSelectionRow;
						int dx = table.dragingToColumn - table.maxSelectionColumn;
						if (dx > dy) {
							table.dragingToRow = table.maxSelectionRow;
						}
						else {
							table.dragingToColumn = table.maxSelectionColumn;
						}
					}
					else { // 8
						table.dragingToColumn = table.maxSelectionColumn;
					}
				}
				else {
					if (table.dragingToColumn < table.minSelectionColumn) { // 6
						table.dragingToRow = table.maxSelectionRow;
					}
					else if (table.dragingToColumn > table.maxSelectionColumn) { // 4
						table.dragingToRow = table.minSelectionRow;							
					}
					else { // 5
						table.dragingToRow = -1;
						table.dragingToColumn = -1;
					}
				}
			}
			table.repaint();
		}

		//G.STURR 2010-1-29: handle ctrl-select dragging of cell blocks
		else{
			if(e.isControlDown()){
				table.handleControlDragSelect(e);}
		}
	}

	/**
	 *  Shows tool tip description of geo on mouse over
	 */
	public void mouseMoved(MouseEvent e) {
		if (table.isEditing())
			return;

		// get GeoElement at mouse location
		int row = table.rowAtPoint(e.getPoint());
		int col = table.columnAtPoint(e.getPoint());
		GeoElement geo = (GeoElement) model.getValueAt(row, col);

		// set tooltip with geo's description
		if (geo != null & view.getAllowToolTips()) {
			app.setTooltipFlag();
			table.setToolTipText(geo.getLongDescriptionHTML(true, true));	
			app.clearTooltipFlag();
		} else
			table.setToolTipText(null);	
		
		//highlight dragging dot on mouseover
		Point point1 = table.getMaxSelectionPixel();
		if (point1 == null) return;
		int x1 = e.getX();
		int y1 = e.getY();
		int x2 = (int)point1.getX();
		int y2 = (int)point1.getY();
		int range = MyTable.DOT_SIZE / 2;
		boolean nowOverDot = (x1 >= x2 - range && x1 <= x2 + range && y1 >= y2 - range && y1 <= y2 + range); 
		if (table.isOverDot != nowOverDot) {	
			table.isOverDot = nowOverDot;
			table.repaint();
		}
		
	}

}

