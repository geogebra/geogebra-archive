/**
 * 
 */
package geogebra.cas.view;

import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;

import javax.swing.CellEditor;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;

/**
 * @author Quan
 * 
 */
public class CASTable extends JTable {
	
	public final static int CONTENT_COLUMN = 0;

	private CASTableModel tableModel;
	protected Kernel kernel;
	protected Application app;
	private CASView view;
	private boolean showCellSeparator = false;
	
	
	private CASTableCellEditor editor;
	private CASTableCellRenderer renderer;

	public static final Color SELECTED_BACKGROUND_COLOR_HEADER = new Color(185,
			185, 210);

	public CASTable(CASView view, int rows) {
		this.view = view;
		app = view.getApp();
		kernel = app.getKernel();					

		this.setShowGrid(false);
		// Dynamically change the height of the table
		this.setRowHeight(CASPara.inputLineHeight);
		this.setBackground(Color.white);

		tableModel = new CASTableModel(this, rows, app);
		this.setModel(tableModel);
		this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		// init editor and renderer
		editor = new CASTableCellEditor(view);
		renderer = new CASTableCellRenderer(view);
		getColumnModel().getColumn(CONTENT_COLUMN).setCellEditor(editor);
		getColumnModel().getColumn(CONTENT_COLUMN).setCellRenderer(renderer);				
		setTableHeader(null); 

		// Set the width of the index column;
		// this.getColumn(this.getColumnName(CASPara.indexCol)).setMinWidth(30);
		// this.getColumn(this.getColumnName(CASPara.indexCol)).setMaxWidth(30);

		// this.sizeColumnsToFit(0);
		//this.setSurrendersFocusOnKeystroke(true);
		
//		addFocusListener(new FocusListener() {
//			public void focusGained(FocusEvent arg0) {
//				// TODO: remove
//				System.out.println("table GAINED focus");
////				startEditingRow(getSelectedRow());
//			}
//
//			public void focusLost(FocusEvent arg0) {
//				// TODO: remove
//				System.out.println("table LOST focus");
//			}			
//		});
	
	}
	
	public CASView getCASView() {
		return view;
	}
	
	public final boolean isShowCellSeparator() {
		return showCellSeparator;
	}

	public final void setShowCellSeparator(boolean showCellSeparator) {
		this.showCellSeparator = showCellSeparator;
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
	public void insertRow(CASTableCellValue newValue) {
		insertRowAfter(tableModel.getRowCount()-1, newValue);		
	}
	
	/**
	 * Inserts a row after selectedRow and starts editing
	 * the new row.
	 */
	public void insertRowAfter(int selectedRow, CASTableCellValue newValue) {		
		// TODO: remove
		System.out.println("insertRowAfter: " + selectedRow);
							
		if (newValue == null)
			newValue = new CASTableCellValue(view);
		tableModel.insertRow(selectedRow + 1, new Object[] { newValue });
		
		// update height of new row
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

    
	
	public void updateRow(int row) {
		//stopEditing();
		
		// TODO: remove
		CASTableCellValue value = getCASTableCellValue(row);
		System.out.println("update row: " + row + ", input: " + value.getInput() + ", output: " + value.getOutput());
		
		tableModel.fireTableRowsUpdated(row, row);	
	}
	
	public CASTableCellValue getCASTableCellValue(int row) {
		return (CASTableCellValue) tableModel.getValueAt(row, CONTENT_COLUMN);
	}
	
	public boolean isRowEmpty(int row) {		
		CASTableCellValue value = (CASTableCellValue) tableModel.getValueAt(row, 0);
		String input = value.getInput();
		String output = value.getOutput(); 
		return (input == null || input.length() == 0) && (output == null || output.length() == 0);
	}

	
	/*
	 * Function: Delete a rolw, and set the focus at the right position
	 */
	public void deleteAllRow() {
		int row = tableModel.getRowCount();

		for (int i = row - 1; i >= 0; i--)
			tableModel.removeRow(i);
		this.repaint();

		this.getRowCount();
		// if (tableModel.getRowCount() == 0)
		// insertRow(-1, CASPara.contCol);
	}

	/*
	 * Function: Delete a rolw, and set the focus at the right position
	 */
	public void deleteRow(int row) {
		// TODO:remove
		System.out.println("tableModel.removeRow " + row);

		tableModel.removeRow(row);

		int rowCount = tableModel.getRowCount();
		if (rowCount == 0)
			insertRowAfter(-1, null);
		else 
			startEditingRow(Math.min(row, rowCount-1));
	}

	/*
	 * Function: Set the focus on the specified row
	 */
	public void startEditingRow(int editRow) {								
		// TODO: remove
		System.out.println("startEditingRow: " +editRow);
				
		if (editRow >= tableModel.getRowCount()) {
			// insert new row, this starts editing
			insertRow(null);
		}
		else {		
			// start editing
			setRowSelectionInterval(editRow, editRow);	
	        scrollRectToVisible(getCellRect( editRow, CONTENT_COLUMN, false ) );	
			editCellAt(editRow, CONTENT_COLUMN);	
			editor.setInputAreaFocused();			
		}
	}		



}
