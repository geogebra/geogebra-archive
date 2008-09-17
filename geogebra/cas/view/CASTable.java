/**
 * 
 */
package geogebra.cas.view;

import geogebra.Application;
import geogebra.kernel.Kernel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.*;

/**
 * @author Quan
 * 
 */
public class CASTable extends JTable {

	private CASTableModel tableModel;
	protected Kernel kernel;
	protected Application app;
	
	public static final Color SELECTED_BACKGROUND_COLOR_HEADER = new Color(185,185,210);

	public CASTable(Application app) {
		super();
		
		this.app = app;
		this.kernel = this.app.getKernel();
		
	}

	/*
	 * Default Cinfiguration for Table
	 */
	public void initializeTable(int rows, CASSession session, Application app) {

		this.setShowGrid(false);
		// Dynamically change the height of the table
		this.setRowHeight(CASPara.originalHeight);
		this.setBackground(Color.white);

		tableModel = new CASTableModel(this, rows, session, app);
		this.setModel(tableModel);
		this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		// Set the width of the index column;
		//this.getColumn(this.getColumnName(CASPara.indexCol)).setMinWidth(30);
		//this.getColumn(this.getColumnName(CASPara.indexCol)).setMaxWidth(30);

		// this.sizeColumnsToFit(0);
		this.setSurrendersFocusOnKeystroke(true);
	}

	/*
	 * Function: Insert a row after the "selectedRow row", and set the focus at
	 * the new row
	 */
	public void insertRow(int selectedRow, int selectedCol) {

		CASTableCellValue newValue = new CASTableCellValue();
		tableModel.insertRow(selectedRow + 1, new Object[] { newValue, "New" });
		changeSelection(selectedRow + 1, selectedCol, false, false);
		editCellAt(selectedRow + 1, selectedCol);
		((Component) ((CASTableCellEditor) getCellEditor(selectedRow + 1,
				selectedCol)).getTableCellEditorComponent(this, newValue, true,
				selectedRow + 1, selectedCol)).requestFocus();
	}

	public void insertRow(CASTableCellValue newValue) {

		// CASTableCellValue newValue = new CASTableCellValue();
		int rowNum = tableModel.getRowCount();
		// insert the row before
		tableModel.insertRow(rowNum - 1, new Object[] { newValue, "load" });
		// changeSelection(selectedRow + 1, selectedCol, false, false);
		// editCellAt(selectedRow + 1, selectedCol);
		// ((Component) ((CASTableCellEditor) getCellEditor(selectedRow + 1,
		// selectedCol)).getTableCellEditorComponent(this, newValue, true,
		// selectedRow + 1, selectedCol)).requestFocus();
		// Enlarge the cell hight
		if(newValue.isOutputVisible())
			this.setRowHeight(rowNum - 1, CASPara.inputOutputHeight);
	}

	public void insertRow(int selectedRow, int selectedCol, char c) {
		char[] in = new char[1];
		in[0] = c;
		CASTableCellValue newValue = new CASTableCellValue(new String(in));
		tableModel.insertRow(selectedRow + 1, new Object[] { newValue, "New" });
		changeSelection(selectedRow + 1, selectedCol, false, false);
		editCellAt(selectedRow + 1, selectedCol);
		((Component) ((CASTableCellEditor) getCellEditor(selectedRow + 1,
				selectedCol)).getTableCellEditorComponent(this, newValue, true,
				selectedRow + 1, selectedCol)).requestFocus();

	}

	/*
	 * Function: Delete a rolw, and set the focus at the right position
	 */
	public void deleteAllRow() {
		int row = tableModel.getRowCount();

		for (int i = row - 1; i >= 0; i--)
			tableModel.removeRow(i);
		this.repaint();
		
		if (tableModel.getRowCount() == 0)
			insertRow(-1, CASPara.contCol);
	}

	/*
	 * Function: Delete a rolw, and set the focus at the right position
	 */
	public void deleteRow(int row) {
		tableModel.removeRow(row);
		this.repaint(); // Update the table
		if (tableModel.getRowCount() == 0)
			insertRow(-1, CASPara.contCol);
		else
			setFocusAtRow(row, CASPara.contCol);
	}

	/*
	 * Function: Set the focus on the specified row
	 */
	public void setFocusAtRow(int editRow, int editCol) {

		CASTableCellValue value = (CASTableCellValue) tableModel.getValueAt(
				editRow, editCol);

		changeSelection(editRow, editCol, false, false);
		editCellAt(editRow, editCol);
		((Component) ((CASTableCellEditor) getCellEditor(editRow, editCol))
				.getTableCellEditorComponent(this, value, true, editRow,
						editCol)).requestFocus();
	}

	/*
	 * Function: Set the focus on the linepanle of a specified row
	 */
	public void setFocusAtRowLinePanel(int editRow, int editCol) {
		CASTableCellValue value = (CASTableCellValue) tableModel.getValueAt(
				editRow, editCol);

		changeSelection(editRow, editCol, false, false);
		editCellAt(editRow, editCol);
		CASTableCell temp = ((CASTableCell) ((CASTableCellEditor) getCellEditor(
				editRow, editCol)).getTableCellEditorComponent(this, value,
				true, editRow, editCol));
		setRowHeight(editRow, temp.addLinePanel());
		temp.setLineBorderFocus();
	}
}
