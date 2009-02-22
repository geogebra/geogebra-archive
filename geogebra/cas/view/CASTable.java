/**
 * 
 */
package geogebra.cas.view;

import geogebra.cas.view.CASView.ConsoleTableKeyListener;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;

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
	 * Inserts a row after the "selectedRow row", and set the focus at
	 * the new row
	 */
	public void insertRowAfter(int selectedRow, CASTableCellValue newValue) {		
		if (newValue == null)
			newValue = new CASTableCellValue();
		tableModel.insertRow(selectedRow + 1, new Object[] { newValue });
		startEditingRow(selectedRow  + 1);
	}		

	public void insertRow(CASTableCellValue newValue) {
		if (newValue == null)
			newValue = new CASTableCellValue();
		int rowNum = tableModel.getRowCount();
		tableModel.insertRow(rowNum, new Object[] { newValue});
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
		tableModel.removeRow(row);
		if (tableModel.getRowCount() == 0)
			insertRowAfter(-1, null);
		else 
			startEditingRow(Math.min(row, getRowCount()-1));
	}

	/*
	 * Function: Set the focus on the specified row
	 */
	public void startEditingRow(int editRow) {								
		// TODO: reove
		System.out.println("startEditingRow: " +editRow);
		
		// insert new row
		if (editRow >= tableModel.getRowCount()) {
			insertRow(null);
			editRow = tableModel.getRowCount()-1;
		}
							
		changeSelection(editRow, CONTENT_COLUMN, false, false);
        scrollRectToVisible(getCellRect( editRow, CONTENT_COLUMN, false ) );	
		editCellAt(editRow, CONTENT_COLUMN);	
		editor.setInputAreaFocused();
	}		

	public Point getIndexFromPixel(int x, int y) {
		if (x < 0 || y < 0)
			return null;
		int indexX = -1;
		int indexY = -1;
		for (int i = 0; i < getColumnCount(); ++i) {
			Point point = getPixel(i, 0, false);
			if (x < point.getX()) {
				indexX = i;
				break;
			}
		}
		if (indexX == -1) {
			return null;
		}
		for (int i = 0; i < getRowCount(); ++i) {
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

	protected Point getPixel(int column, int row, boolean min) {
		if (column < 0 || row < 0) {
			return null;
		}
		if (min && column == 0 && row == 0) {
			return new Point(0, 0);
		}
		int x = 0;
		int y = 0;
		if (!min) {
			++column;
			++row;
		}
		for (int i = 0; i < column; ++i) {
			x += getColumnModel().getColumn(i).getWidth();
		}
		int rowHeight;
		for (int i = 0; i < row; ++i) {
			rowHeight = getRowHeight(i);
			y += rowHeight;
		}
		return new Point(x, y);
	}
}
