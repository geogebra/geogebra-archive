/**
 * 
 */
package geogebra.cas.view;

import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;

import javax.swing.CellEditor;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;

/**
 * @author Quan
 * 
 */
public class CASTable extends JTable {
	
	public final static int COL_CAS_CELLS = 0;

	private CASTableModel tableModel;
	protected Kernel kernel;
	protected Application app;
	private CASView view;
	private CASTable table; 
	
	private CASTableCellEditor editor;
	private CASTableCellRenderer renderer;

	public static final Color SELECTED_BACKGROUND_COLOR_HEADER = new Color(185,
			185, 210);

	public CASTable(CASView view) {
		this.view = view;
		app = view.getApp();
		kernel = app.getKernel();
		this.table = this;

		setShowGrid(true);
		setGridColor(MyTable.TABLE_GRID_COLOR);
		setBackground(Color.white);

		tableModel = new CASTableModel(this, app);
		this.setModel(tableModel);
		this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		// init editor and renderer
		editor = new CASTableCellEditor(view);
		renderer = new CASTableCellRenderer(view);
		getColumnModel().getColumn(COL_CAS_CELLS).setCellEditor(editor);
		getColumnModel().getColumn(COL_CAS_CELLS).setCellRenderer(renderer);				
		setTableHeader(null); 
		
		// remove all default mouse listeners
		MouseListener [] ml = getMouseListeners();
		if (ml != null) {
			for (int i=0; i < ml.length; i++) {
				removeMouseListener(ml[i]);
			}
		}			
		
		addComponentListener(new ComponentListener() {

			public void componentHidden(ComponentEvent e) {
				
			}

			public void componentMoved(ComponentEvent e) {
				
			}

			public void componentResized(ComponentEvent e) {
				if (isEditing()) return;
				
				// keep editor value after resizing
				int row = editor.getEditingRow();
				if (row >=0 && row < getRowCount()) {
					editor.stopCellEditing();
					updateRow(row);
				}
			}

			public void componentShown(ComponentEvent e) {
				
			}
			
		});

		// tableModel listener to resize the column width after row updates
		// note: this only adjusts column 0
		tableModel.addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) {
				if(e.getType()==TableModelEvent.UPDATE || e.getType()==TableModelEvent.DELETE){
					TableCellRenderer renderer; 
					int prefWidth = 0; 
					// iterate through all rows and get max preferred width
					for (int r=0; r < getRowCount(); r++) {
						renderer =  getCellRenderer(r, 0);
						int w = prepareRenderer(renderer, r, 0).getPreferredSize().width;
						prefWidth = Math.max(prefWidth, w);
					}

					// adjust the width
					if(prefWidth != table.getColumnModel().getColumn(0).getPreferredWidth()) {   	  
						table.getColumnModel().getColumn(0).setPreferredWidth(prefWidth);
						table.getColumnModel().getColumn(0).setMinWidth(prefWidth);
					}
				}
			}
		});
		
		
		
		// Set the width of the index column;
		// this.getColumn(this.getColumnName(CASPara.indexCol)).setMinWidth(30);
		// this.getColumn(this.getColumnName(CASPara.indexCol)).setMaxWidth(30);

		// this.sizeColumnsToFit(0);
		//this.setSurrendersFocusOnKeystroke(true);
	}
	
	public CASView getCASView() {
		return view;
	}
	
	public void stopEditing() {
		// stop editing 
		CellEditor editor = (CellEditor) getEditorComponent();
		if (editor != null) editor.stopCellEditing();
	}		
		
	public CASTableCellEditor getEditor() {
		return editor;		
	}		

		
	/**
	 * Inserts a row at the end and starts editing
	 * the new row.
	 */
	public void insertRow(CASTableCellValue newValue, boolean startEditing) {
		int lastRow = tableModel.getRowCount()-1;
		if (isRowEmpty(lastRow)) {
			if (newValue == null)
				newValue = new CASTableCellValue(view);
			tableModel.setValueAt(newValue, lastRow, COL_CAS_CELLS);
			startEditingRow(lastRow);
		} else {
			insertRowAfter(lastRow, newValue, startEditing);	
		}	
	}
	
	/**
	 * Inserts a row after selectedRow and starts editing
	 * the new row.
	 */
	public void insertRowAfter(int selectedRow, CASTableCellValue newValue, boolean startEditing) {							
		if (newValue == null)
			newValue = new CASTableCellValue(view);
		tableModel.insertRow(selectedRow + 1, new Object[] { newValue });
		// make sure the row is shown when at the bottom of the viewport
		table.scrollRectToVisible(table.getCellRect(selectedRow+1, 0, false));
		
		int rowCount = tableModel.getRowCount();
		if (selectedRow + 2 < rowCount)
		{
			CASInputHandler h = view.getInputHandler();
			for (int i=selectedRow+2; i < rowCount; i++) {
				h.updateReferencesAfterRowInsertOrDelete(selectedRow, i, CASInputHandler.ROW_REFERENCE_DYNAMIC, true);
				h.updateReferencesAfterRowInsertOrDelete(selectedRow, i, CASInputHandler.ROW_REFERENCE_STATIC, true);
			}
		}
		// update height of new row
		if (startEditing)
			startEditingRow(selectedRow  + 1);
	}	
	
	/**
	 * Returns the preferred height of a row.
	 * The result is equal to the tallest cell in the row.
	 * 
	 * @see http://www.exampledepot.com/egs/javax.swing.table/RowHeight.html
	 */    
    public int getPreferredRowHeight(int rowIndex) {
        // Get the current default height for all rows
        int height = getRowHeight();
    
        // Determine highest cell in the row
        for (int c=0; c < getColumnCount(); c++) {
            TableCellRenderer renderer =  getCellRenderer(rowIndex, c);
            Component comp = prepareRenderer(renderer, rowIndex, c);
            int h = comp.getPreferredSize().height; // + 2*margin;
            height = Math.max(height, h);
        }
        return height;
    }

    /** 
     * The height of each row is set to the preferred height of the
     * tallest cell in that row.
     */
    public void packRows() {
        packRows(0, getRowCount());
    }
    
    /**
     *  For each row >= start and < end, the height of a
     *  row is set to the preferred height of the tallest cell
     *  in that row.
     */
    public void packRows(int start, int end) {
        for (int r=start; r < end; r++) {
            // Get the preferred height
            int h = getPreferredRowHeight(r);
    
            // Now set the row height using the preferred height
            if (getRowHeight(r) != h) {
                setRowHeight(r, h);
            }
        }
    }

    /**
     * Creates a new row in the cas view.
     */
    public CASTableCellValue createRow() {
    	stopEditing();
    	
    	// make sure we have at least one row
    	int rows = getRowCount();
    	if (rows == 0) { 
    		insertRow(new CASTableCellValue(view), false);
    		rows = 1;
    	} 
    	
    	CASTableCellValue retRow;
    	
    	// check if last row is empty
		if (isRowEmpty(rows-1)) {
			// last row empty
			retRow = getCASTableCellValue(rows-1);
		}
		else {
			// last row is not empty
			retRow = new CASTableCellValue(view);
			insertRow(retRow, false);
		}
    	
    	return retRow;
    }
	
	public void updateRow(int row) {
		tableModel.fireTableRowsUpdated(row, row);	
	}
	
	public void updateAllRows() {
		int rowCount = tableModel.getRowCount();
		if (rowCount > 0)
			tableModel.fireTableRowsUpdated(0, rowCount - 1);
	}
	
	public CASTableCellValue getCASTableCellValue(int row) {
		return (CASTableCellValue) tableModel.getValueAt(row, COL_CAS_CELLS);
	}
	
	public boolean isRowEmpty(int row) {	
		if (row < 0) return false;
		
		CASTableCellValue value = (CASTableCellValue) tableModel.getValueAt(row, 0);
		return value.isEmpty();
	}

	
	/*
	 * Function: Delete a rolw, and set the focus at the right position
	 */
	public void deleteAllRows() {
		stopEditing();
		int row = tableModel.getRowCount();
		for (int i = row - 1; i >= 0; i--)
			tableModel.removeRow(i);
		this.repaint();
		// if (tableModel.getRowCount() == 0)
		// insertRow(-1, CASPara.contCol);
	}

	/*
	 * Function: Delete a rolw, and set the focus at the right position
	 */
	public void deleteRow(int row) {
		stopEditing();
		tableModel.removeRow(row);

		int rowCount = tableModel.getRowCount();
		if (row < rowCount)
		{
			CASInputHandler h = view.getInputHandler();
			for (int i= row; i < rowCount; i++) {
				h.updateReferencesAfterRowInsertOrDelete(row, i, CASInputHandler.ROW_REFERENCE_DYNAMIC, false);
				h.updateReferencesAfterRowInsertOrDelete(row, i, CASInputHandler.ROW_REFERENCE_STATIC, false);
			}
		}

		this.repaint();
		
		if (rowCount == 0)
			insertRow(null, true);
		else 
			startEditingRow(Math.min(row, rowCount-1));
	}
	
//	/**
//	 * Changes all dynamic references in rows according to the row number change.
//	 * @param fromRow
//	 * @param toRow
//	 */
//	private void changeRowNumberChanged(int fromRow, int toRow) {
//		int rowCount = tableModel.getRowCount();
//		if (rowCount == 0) return;
//		
//		String oldRef = "\\" + CASInputHandler.ROW_REFERENCE_DYNAMIC + Integer.toString(fromRow-1);
//		String newRef =  "\\" + CASInputHandler.ROW_REFERENCE_DYNAMIC + Integer.toString(toRow-1);
//		for (int i=0; i < rowCount; i++) {
//			CASTableCellValue cellValue = getCASTableCellValue(i);
//			cellValue.replaceRowReferences(oldRef, newRef);
//		}
//	}

	/*
	 * Function: Set the focus on the specified row
	 */
	public void startEditingRow(int editRow) {									
		if (editRow >= tableModel.getRowCount()) {
			// insert new row, this starts editing
			insertRow(null, true);
		}
		else {		
			// start editing
			setRowSelectionInterval(editRow, editRow);	
	        scrollRectToVisible(getCellRect( editRow, COL_CAS_CELLS, true ) );	
			editCellAt(editRow, COL_CAS_CELLS);			
		}
	}
	
	public boolean editCellAt(int editRow, int editCol) {
		boolean success = super.editCellAt(editRow, editCol);
		if (success && editCol == COL_CAS_CELLS) {
			editor.setInputAreaFocused();
		}
		return success;
	}

	public void setFont(Font ft) {
		super.setFont(ft);
		if (editor != null)
			editor.setFont(getFont());
		if (renderer != null)
			renderer.setFont(getFont());
	}


	
	//===============================================================
	// Workaround for java horizontal scrolling bug, see:
	// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4127936
	
	/**
	 * When the viewport shrinks below the preferred size, stop tracking the
	 * viewport width
	 */
	public boolean getScrollableTracksViewportWidth() {
	    if (autoResizeMode != AUTO_RESIZE_OFF) {
	        if (getParent() instanceof JViewport) {
	            return (((JViewport)getParent()).getWidth() > getPreferredSize().width);
	        }
	    }
	    return false;
	}
	
	/**
	 * When the viewport shrinks below the preferred size, return the minimum
	 * size so that scrollbars will be shown
	 */
	public Dimension getPreferredSize() {
	    if (getParent() instanceof JViewport) {
	        if (((JViewport)getParent()).getWidth() < super.getPreferredSize().width) {
	            return getMinimumSize();
	        }
	    }

	    return super.getPreferredSize();
	}

	// End horizontal scrolling fix
	//================================================================
	
	
	
	
	/** When the table is smaller than the viewport fill this extra  
	 * space with the same background color as the table.*/
	@Override
	protected void configureEnclosingScrollPane() {
		super.configureEnclosingScrollPane();
		Container p = getParent();
		if (p instanceof JViewport) {
			((JViewport) p).setBackground(getBackground());
		}
	}
	
	public void setLabels(){
		editor.setLabels();
	}
	


}
